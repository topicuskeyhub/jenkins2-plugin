package io.jenkins.plugins.vault;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import hudson.util.Secret;
import io.jenkins.plugins.credentials.KeyHubClientCredentials;
import io.jenkins.plugins.model.KeyHubRecord;
import io.jenkins.plugins.model.response.KeyHubGroup;

// TODO Turn ENDPOINTS into UriBuilders when the link between VaultAccessor and GlobalUri is established
public class VaultAccessor implements IVaultAccessor {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private KeyHubClientCredentials credentials;
    private RestClientBuilder restClientBuilder = new RestClientBuilder();
    private KeyHubTokenResponse keyhubToken;

    public VaultAccessor() {
    }

    public VaultAccessor(KeyHubClientCredentials credentials) {
        this.credentials = credentials;
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
    public void fetchAuthenticationTokenAndGetVaultAccess() throws IOException, InterruptedException {
        if (credentials.getClientSecret() == null) {
            throw new IllegalStateException("Cannot refresh access token, no secret stored/given.");
        }

        final String AUTH_ENDPOINT = "https://keyhub.topicusonderwijs.nl/login/oauth2/token?authVault=access";
        Form connectionRequest = new Form().param("grant_type", "client_credentials");
        ResteasyWebTarget target = restClientBuilder.getClient().target(AUTH_ENDPOINT);
        target.register(
                new BasicAuthentication(credentials.getClientId(), Secret.toString(credentials.getClientSecret())));
        target.request().header("Accept", "application/vnd.topicus.keyhub+json;version=44");
        this.keyhubToken = target.request().post(Entity.form(connectionRequest), KeyHubTokenResponse.class);
    }

    public List<KeyHubGroup> fetchGroupData() throws IOException {
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group";
        ObjectMapper mapper = new ObjectMapper();
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44").get()) {
            String json = response.readEntity(String.class);

            // readTree way to parse the Json Response
            // JsonNode node = mapper.readTree(json);
            // JsonNode itemNode = node.get("items").get(0);
            // String link = itemNode.get("links").get(0).get("href").asText();
            KeyHubGroup groupTest = mapper.readValue(json, KeyHubGroup.class);
            System.out.println("Item Name: " + groupTest.getName());
        }
        // return this.groupIdsAndNames;
        return null;
    }

    public void fetchRecordsFromVault(KeyHubGroup group) {
        // final String ENDPOINT =
        // "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group/" + groupId +
        // "/vault/record";
        final String ENDPOINT = group.getHref() + "/vault/record";
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44").get()) {
            String json = response.readEntity(String.class);
            List<Integer> recordIds = JsonPath.parse(json).read("$..links..id");
            List<String> recordNames = JsonPath.parse(json).read("$..items..name");
        }
    }

    public KeyHubRecord fetchRecordSecret(KeyHubRecord record) {
        System.out.println("CALLING FETCHRECORDSECRET");
        KeyHubRecord recordWithSecret;
        // https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group/32735401/vault/record/32839120?additional=secret
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group/32735401/vault/record/32839120?additional=secret";
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44")
                .header("topicus-Vault-session", keyhubToken.getVaultSession()).get()) {
            String json = response.readEntity(String.class);
            String recordSecret = JsonPath.parse(json).read("$..secret..password[0]");
            System.out.println("Record Secret: " + recordSecret);
            // record.setRecordSecret(recordSecret);
        }
        return null;
    }

}
