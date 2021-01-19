package io.jenkins.plugins.vault;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.jayway.jsonpath.JsonPath;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.groovy.runtime.powerassert.SourceText;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import hudson.util.Secret;
import io.jenkins.plugins.model.ClientCredentials;
import io.jenkins.plugins.model.response.KeyHubTokenResponse;
import io.jenkins.plugins.model.response.group.KeyHubGroup;
import io.jenkins.plugins.model.response.group.ListOfKeyHubGroups;
import io.jenkins.plugins.model.response.record.KeyHubVaultRecord;
import io.jenkins.plugins.model.response.record.ListOfKeyHubVaultRecords;

public class VaultAccessor implements IVaultAccessor {

    private ClientCredentials clientCredentials;
    private String keyhubGlobalConfiguration;
    private RestClientBuilder restClientBuilder;
    private KeyHubTokenResponse keyhubToken;
    private static final MediaType RESPONSE_ACCEPT = MediaType
            .valueOf("application/vnd.topicus.keyhub+json;version=44");

    public VaultAccessor() {
    }

    public VaultAccessor(ClientCredentials credentials, String keyhubGlobalConfiguration,
            RestClientBuilder restClientBuilder) {
        this.clientCredentials = credentials;
        this.keyhubGlobalConfiguration = keyhubGlobalConfiguration;
        this.restClientBuilder = restClientBuilder;
    }

    public KeyHubTokenResponse getKeyhubToken() {
        return this.keyhubToken;
    }

    public VaultAccessor connect() {
        if (keyhubToken == null) {
            fetchAuthenticationTokenAndGetVaultAccess();
        }
        if (keyhubToken.getExpiresIn() < 2) {
            fetchAuthenticationTokenAndGetVaultAccess();
        }
        return this;
    }

    /**
     * 
     * Fetches the Auth2.0 token including the vault access session from Keyhub.
     * 
     * @return
     * @throws InterruptedException
     * @throws ClientProtocolException
     * @throws IOException
     */
    private void fetchAuthenticationTokenAndGetVaultAccess() {
        if (clientCredentials.getClientSecret() == null) {
            throw new IllegalStateException("Cannot refresh access token, no secret stored/given.");
        }

        UriBuilder authenticateUri = UriBuilder.fromUri(keyhubGlobalConfiguration).path("login").path("oauth2")
                .path("token").queryParam("authVault", "access");
        Form connectionRequest = new Form().param("grant_type", "client_credentials");
        ResteasyWebTarget target = restClientBuilder.getClient().target(authenticateUri);
        target.register(new BasicAuthentication(clientCredentials.getClientId(),
                Secret.toString(clientCredentials.getClientSecret())));
        target.request().header("Accept", RESPONSE_ACCEPT);
        this.keyhubToken = target.request().post(Entity.form(connectionRequest), KeyHubTokenResponse.class);
    }

    public List<KeyHubGroup> fetchGroupData() throws IOException {
        final String ENDPOINT = keyhubGlobalConfiguration + "/keyhub/rest/v1/group";
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        ListOfKeyHubGroups keyhubGroups;

        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .accept(RESPONSE_ACCEPT).get()) {
            keyhubGroups = response.readEntity(ListOfKeyHubGroups.class);
        }
        return keyhubGroups.getGroups();
    }

    public List<KeyHubVaultRecord> fetchRecordsFromVault(List<KeyHubGroup> groups) throws IOException {
        ListOfKeyHubVaultRecords keyhubRecords = new ListOfKeyHubVaultRecords();
        for (KeyHubGroup group : groups) {
            UriBuilder recordsUri = UriBuilder.fromUri(group.getHref()).path("vault").path("record");
            ResteasyWebTarget target = restClientBuilder.getClient().target(recordsUri);
            try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                    .accept(RESPONSE_ACCEPT).get()) {
                keyhubRecords = response.readEntity(ListOfKeyHubVaultRecords.class);
            }
        }
        return keyhubRecords.getRecords();
    }

    public Secret fetchRecordSecret(String href) throws UnsupportedEncodingException {
        String param = "?additional=secret";
        final String ENDPOINT = href + param;
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .accept(RESPONSE_ACCEPT).header("topicus-Vault-session", keyhubToken.getVaultSession()).get()) {
            String json = response.readEntity(String.class);
            return Secret.fromString(JsonPath.parse(json).read("$.additionalObjects..secret..password").toString()
                    .replace("[", "").replace("\"", "").replace("]", ""));
        }
    }
}
