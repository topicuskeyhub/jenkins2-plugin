package io.jenkins.plugins.vault;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import com.jayway.jsonpath.JsonPath;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import hudson.util.Secret;
import io.jenkins.plugins.model.ClientCredentials;
import io.jenkins.plugins.model.response.KeyHubTokenResponse;
import io.jenkins.plugins.model.response.group.KeyHubGroup;
import io.jenkins.plugins.model.response.group.ListOfKeyHubGroups;
import io.jenkins.plugins.model.response.record.KeyHubRecord;
import io.jenkins.plugins.model.response.record.ListOfKeyHubRecords;

public class VaultAccessor implements IVaultAccessor {

    /**
     *
     */
    private ClientCredentials clientCredentials;
    private String keyhubGlobalConfiguration;
    private RestClientBuilder restClientBuilder = new RestClientBuilder();
    private KeyHubTokenResponse keyhubToken;
    private static final String RESPONSE_ACCEPT = "application/vnd.topicus.keyhub+json;version=44";

    public VaultAccessor() {
    }

    public IVaultAccessor getVaultAccessor() {
        return this;
    }

    public VaultAccessor(ClientCredentials credentials, String keyhubGlobalConfiguration) {
        this.clientCredentials = credentials;
        this.keyhubGlobalConfiguration = keyhubGlobalConfiguration;
    }

    public void setCredentials(ClientCredentials credentials) {
        this.clientCredentials = credentials;
    }

    public void setKeyhubGlobalConfiguration(String keyhubGlobalConfiguration) {
        this.keyhubGlobalConfiguration = keyhubGlobalConfiguration;
    }

    public KeyHubTokenResponse getKeyhubToken() {
        return this.keyhubToken;
    }

    public ClientCredentials getCredentials() {
        return this.clientCredentials;
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

        final String AUTH_ENDPOINT = keyhubGlobalConfiguration + "/login/oauth2/token?authVault=access";
        Form connectionRequest = new Form().param("grant_type", "client_credentials");
        ResteasyWebTarget target = restClientBuilder.getClient().target(AUTH_ENDPOINT);
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
                .header("Content-Type", "application/json").header("Accept", RESPONSE_ACCEPT).get()) {
            String json = response.readEntity(String.class);
            keyhubGroups = restClientBuilder.getMapper().readValue(json, ListOfKeyHubGroups.class);
        }
        return keyhubGroups.getGroups();
    }

    public List<KeyHubRecord> fetchRecordsFromVault(List<KeyHubGroup> groups) throws IOException {
        String endpoint;
        ListOfKeyHubRecords keyhubRecords = new ListOfKeyHubRecords();
        for (KeyHubGroup group : groups) {
            endpoint = group.getHref() + "/vault/record";
            ResteasyWebTarget target = restClientBuilder.getClient().target(endpoint);
            try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                    .header("Content-Type", "application/json").header("Accept", RESPONSE_ACCEPT).get()) {
                String json = response.readEntity(String.class);
                keyhubRecords = restClientBuilder.getMapper().readValue(json, ListOfKeyHubRecords.class);
            }
        }
        return keyhubRecords.getRecords();
    }

    public Secret fetchRecordSecret(String href) throws UnsupportedEncodingException {
        String param = "?additional=secret";
        final String ENDPOINT = href + param;
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .header("Content-Type", "application/json").header("Accept", RESPONSE_ACCEPT)
                .header("topicus-Vault-session", keyhubToken.getVaultSession()).get()) {
            String json = response.readEntity(String.class);
            return Secret.fromString(JsonPath.parse(json).read("$.additionalObjects..secret..password").toString()
                    .replace("[", "").replace("\"", "").replace("]", ""));
        }
    }
}
