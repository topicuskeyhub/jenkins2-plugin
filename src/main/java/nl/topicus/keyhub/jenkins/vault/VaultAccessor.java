package nl.topicus.keyhub.jenkins.vault;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.model.response.KeyHubTokenResponse;
import nl.topicus.keyhub.jenkins.model.response.group.KeyHubGroup;
import nl.topicus.keyhub.jenkins.model.response.group.ListOfKeyHubGroups;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;
import nl.topicus.keyhub.jenkins.model.response.record.ListOfKeyHubVaultRecords;
import nl.topicus.keyhub.jenkins.model.response.record.RecordSecret;

public class VaultAccessor implements IVaultAccessor {

    private ClientCredentials clientCredentials;
    private String keyhubUri;
    private RestClientBuilder restClientBuilder;
    private KeyHubTokenResponse keyhubToken;
    private static final MediaType RESPONSE_ACCEPT = MediaType
            .valueOf("application/vnd.topicus.keyhub+json;version=44");

    public VaultAccessor() {
    }

    public VaultAccessor(ClientCredentials credentials, String keyhubUri,
            RestClientBuilder restClientBuilder) {
        this.clientCredentials = credentials;
        this.keyhubUri = keyhubUri;
        this.restClientBuilder = restClientBuilder;
    }

    public KeyHubTokenResponse getKeyhubToken() {
        return this.keyhubToken;
    }

    public VaultAccessor connect() {
        if (keyhubToken == null) {
            fetchAuthenticationTokenAndGetVaultAccess();
        }
        System.out.println("Time Difference in hours: "
                + ChronoUnit.HOURS.between(this.keyhubToken.getTokenReceivedAt(), Instant.now()));
        System.out.println("Time Difference in seconds: "
                + ChronoUnit.SECONDS.between(this.keyhubToken.getTokenReceivedAt(), Instant.now()));
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

        UriBuilder authenticateUri = UriBuilder.fromUri(keyhubUri).path("/login/oauth2/token")
                .queryParam("authVault", "access");
        Form connectionRequest = new Form().param("grant_type", "client_credentials");
        ResteasyWebTarget target = restClientBuilder.getClient().target(authenticateUri);
        target.register(new BasicAuthentication(clientCredentials.getClientId(),
                Secret.toString(clientCredentials.getClientSecret())));
        target.request().accept(RESPONSE_ACCEPT);
        this.keyhubToken = target.request().post(Entity.form(connectionRequest), KeyHubTokenResponse.class);
        this.keyhubToken.setTokenReceivedAt(Instant.now());
    }

    public List<KeyHubGroup> fetchGroupData() throws IOException {
        connect();
        UriBuilder groupDataUri = UriBuilder.fromUri(keyhubUri).path("/keyhub/rest/v1/group");
        ResteasyWebTarget target = restClientBuilder.getClient().target(groupDataUri);
        ListOfKeyHubGroups keyhubGroups;
        String authHeader = "Bearer " + keyhubToken.getToken();
        try (Response response = target.request().header(HttpHeaders.AUTHORIZATION, authHeader)
                .accept(RESPONSE_ACCEPT).get()) {
            keyhubGroups = response.readEntity(ListOfKeyHubGroups.class);
        }
        return keyhubGroups.getGroups();
    }

    public List<KeyHubVaultRecord> fetchRecordsFromVault(List<KeyHubGroup> groups) throws IOException {
        connect();
        ListOfKeyHubVaultRecords keyhubRecords = new ListOfKeyHubVaultRecords();
        for (KeyHubGroup group : groups) {
            UriBuilder recordsUri = UriBuilder.fromUri(group.getHref()).path("vault/record");
            ResteasyWebTarget target = restClientBuilder.getClient().target(recordsUri);
            String authHeader = "Bearer " + keyhubToken.getToken();
            try (Response response = target.request().header(HttpHeaders.AUTHORIZATION, authHeader)
                    .accept(RESPONSE_ACCEPT).get()) {
                keyhubRecords = response.readEntity(ListOfKeyHubVaultRecords.class);
            }
        }
        return keyhubRecords.getRecords();
    }

    public RecordSecret fetchRecordSecret(String href) {
        connect();
        UriBuilder recordSecretUri = UriBuilder.fromUri(href).queryParam("additional", "secret");
        ResteasyWebTarget target = restClientBuilder.getClient().target(recordSecretUri);
        String authHeader = "Bearer " + keyhubToken.getToken();
        try (Response response = target.request().header(HttpHeaders.AUTHORIZATION, authHeader)
                .accept(RESPONSE_ACCEPT).header("topicus-Vault-session", keyhubToken.getVaultSession()).get()) {
            return response.readEntity(RecordSecret.class);
        }
    }
}
