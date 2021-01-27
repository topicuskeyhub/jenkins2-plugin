package nl.topicus.keyhub.jenkins.vault;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.groovy.runtime.powerassert.SourceText;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.configuration.GlobalPluginConfiguration;
import nl.topicus.keyhub.jenkins.credentials.SecretSupplier;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.model.response.KeyHubTokenResponse;
import nl.topicus.keyhub.jenkins.model.response.group.KeyHubGroup;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;

@Extension
public class KeyHubCommunicationService {

    private static final Logger LOG = Logger.getLogger(KeyHubCommunicationService.class.getName());
    private RestClientBuilder restClientBuilder = new RestClientBuilder();
    private Map<String, KeyHubTokenResponse> currentClientIdWithTokens = new ConcurrentHashMap<>();
    GlobalPluginConfiguration keyhubGlobalConfig = ExtensionList.lookup(GlobalPluginConfiguration.class)
            .get(GlobalPluginConfiguration.class);
    
    private static final MediaType RESPONSE_ACCEPT = MediaType
            .valueOf("application/vnd.topicus.keyhub+json;version=44");
    // Als klassenatribuut of fetchCredentials de fetchAuthenticationTokenAndGetVaultAccess laten aanroepen met als extra parameter clientCredentials
    private ClientCredentials clientCredentials;

    private void fetchAuthenticationTokenAndGetVaultAccess() {
        String keyhubUri = keyhubGlobalConfig.getKeyhubURI();
        KeyHubTokenResponse keyhubToken;
        if (clientCredentials.getClientSecret() == null) {
            throw new IllegalStateException("Cannot refresh access token, no secret stored/given.");
        }

        UriBuilder authenticateUri = UriBuilder.fromUri(keyhubUri).path("/login/oauth2/token").queryParam("authVault",
                "access");
        Form connectionRequest = new Form().param("grant_type", "client_credentials");
        ResteasyWebTarget target = restClientBuilder.getClient().target(authenticateUri);
        target.register(new BasicAuthentication(clientCredentials.getClientId(),
                Secret.toString(clientCredentials.getClientSecret())));
        target.request().accept(RESPONSE_ACCEPT);
        keyhubToken = target.request().post(Entity.form(connectionRequest), KeyHubTokenResponse.class);
        keyhubToken.setTokenReceivedAt(Instant.now());
    }

    public Collection<KeyHubUsernamePasswordCredentials> fetchCredentials(ClientCredentials clientCredentials) {
        this.clientCredentials = clientCredentials;
        if (keyhubGlobalConfig.getKeyhubURI().isEmpty() || keyhubGlobalConfig.getKeyhubURI() == null) {
            return Collections.emptyList();
        }
        VaultAccessor vaultAccessor = new VaultAccessor(clientCredentials, keyhubGlobalConfig.getKeyhubURI(),
                restClientBuilder);
        List<KeyHubGroup> khGroups = new ArrayList<>();
        List<KeyHubVaultRecord> khRecords = new ArrayList<>();

        List<KeyHubUsernamePasswordCredentials> jRecords = new ArrayList<>();
        try {
            khGroups = vaultAccessor.fetchGroupData();
            khRecords = vaultAccessor.fetchRecordsFromVault(khGroups);
            for (int j = 0; j < khGroups.size(); j++) {
                for (int i = 0; i < khRecords.size(); i++) {
                    jRecords.add(KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance()
                            .id(khRecords.get(i).getUUID()).recordName(khRecords.get(i).getName())
                            .href(khRecords.get(i).getHref()).username(khRecords.get(i).getUsername())
                            .password(new SecretSupplier(vaultAccessor, khRecords.get(i).getHref())).build());
                }
            }
            currentClientIdWithTokens.put(vaultAccessor.getClientCredentials().getClientId(),
                    vaultAccessor.getKeyhubToken());
            return jRecords;

        } catch (IOException e) {
            LOG.log(Level.WARNING, "IO could not fetch credentials out of the KeyHub vault: message=[{0}]",
                    e.getMessage());
        }
        return Collections.emptyList();
    }

    public Map<String, KeyHubTokenResponse> getCurrentClientIdWithTokens() {
        return this.currentClientIdWithTokens;
    }
}
