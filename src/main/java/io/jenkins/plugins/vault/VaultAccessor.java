package io.jenkins.plugins.vault;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.client.Entity;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import com.jayway.jsonpath.JsonPath;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import hudson.util.Secret;
import io.jenkins.plugins.credentials.KeyHubClientCredentials;
import io.jenkins.plugins.model.response.group.KeyHubGroup;
import io.jenkins.plugins.model.response.group.ListOfKeyHubGroups;
import io.jenkins.plugins.model.response.record.KeyHubRecord;
import io.jenkins.plugins.model.response.record.ListOfKeyHubRecords;
import io.jenkins.plugins.model.response.KeyHubTokenResponse;

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

    public ListOfKeyHubGroups fetchGroupData() throws IOException {
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group";
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        ListOfKeyHubGroups keyhubGroups;
        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44").get()) {
            String json = response.readEntity(String.class);
            keyhubGroups = restClientBuilder.getMapper().readValue(json, ListOfKeyHubGroups.class);
            System.out.println(keyhubGroups.getName());
        }
        return keyhubGroups;
    }

    public ListOfKeyHubRecords fetchRecordsFromVault(KeyHubGroup group) throws IOException {
        final String ENDPOINT = group.getHref() + "/vault/record";
        ListOfKeyHubRecords keyhubRecords;
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44").get()) {
            String json = response.readEntity(String.class);
            keyhubRecords = restClientBuilder.getMapper().readValue(json, ListOfKeyHubRecords.class);
        }
        return keyhubRecords;
    }

    public void fetchRecordSecret(KeyHubRecord record) throws UnsupportedEncodingException {
        String param = "?additional=secret";
        final String ENDPOINT = record.getHref() + param;
        System.out.println(ENDPOINT);
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer "
                + "eyJ4NXQjUzI1NiI6IlBBTXpSdDdTRzJNTlAtMy1GMHdrWFBFYWNGbVRBN0w3RTBuZ3VLTm1qV3MiLCJraWQiOiJrZXlodWJpZHAtNDczNTU4Mzg4OTYwMTk4ODg5MyIsImFsZyI6IlJTMjU2In0.eyJhdWQiOiJlOWExN2E4NS1iZmRkLTQ5NjgtODEwZS0wZTQ4YzM5ZjZlYTkiLCJzdWIiOiJlOWExN2E4NS1iZmRkLTQ5NjgtODEwZS0wZTQ4YzM5ZjZlYTkiLCJuYmYiOjE2MDQ5Njg0OTcsImFhdCI6MTYwNDk2ODQ5NywiYW1yIjoicHdkIiwic2NvcGUiOlsiY2xpZW50IiwiZ3JvdXAiLCJhY2Nlc3NfdmF1bHQiXSwiaXNzIjoiaHR0cHM6XC9cL2tleWh1Yi50b3BpY3Vzb25kZXJ3aWpzLm5sIiwidHlwZSI6ImFjY2VzcyIsImV4cCI6MTYwNDk3MjA5NywiaWF0IjoxNjA0OTY4NDk3LCJqdGkiOiI5ZDQ5MDQyYS1jZjVhLTQyOTEtOTAxOS1iNTYwYjU1ZThjODUifQ.K-6sqoIUjvupfdVHhbdYT5GlYGXMX9otiEdBqy9PwJ6bJvwBvwFbSULvwnlDGcsIisO6p4kbo_Xk1f9dTqLXQUhQRvLM8tC7C3W2zTCe6R77p6PHNRMWUMapPFloDb7yEspfIucZeH7b-BleBlzUH6BqxImYflVi3WfewX-7MzV1ES-sl9pdhaDKAAycFupJMe4rN3AV1oc-JSjfWqYsm1nxa8_CnUn-FPj2yx5y4sxdDaB4-eGyOZehtaIEQCqjexzL-_KbYbHq2eHyGI9tS8z-4pHMhyAKxVrZqZlSTn8WPnSjiG6sv46-PpcxdBg72rW8TMDoFlr3rI6vPFFaOg")
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44")
                .header("topicus-Vault-session",
                        "1cfc83ef-7695-4478-b503-12c204272516:AES:SYM1:RZwG+3rQKMlMJR4fxRvVlJXXTDKwWHnt75R2wsrEufw=")
                .get()) {
            String json = response.readEntity(String.class);
            String recordSecret = JsonPath.parse(json).read("$.additionalObjects..secret..password").toString()
                    .replace("[", "").replace("\"", "").replace("]", "");
            record.setRecordSecret(recordSecret);
        }
    }
}
