package io.jenkins.plugins.vault;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import antlr.Token;
import groovy.ui.SystemOutputInterceptor;
import hudson.util.Secret;
import io.jenkins.plugins.credentials.KeyHubClientCredentials;
import io.jenkins.plugins.model.KeyHubRecord;
import io.jenkins.plugins.model.response.Item;
import io.jenkins.plugins.model.response.KeyHubGroup;

// TODO Turn ENDPOINTS into UriBuilders when the link between VaultAccessor and GlobalUri is established
public class VaultAccessor implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private KeyHubClientCredentials credentials;
    private RestClientBuilder restClientBuilder = new RestClientBuilder();
    private KeyHubTokenResponse keyhubToken;
    private List<KeyHubGroup> groupIdsAndNames;
    private Map<Integer, String> recordIdsAndNames;

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
        // Vragen of groepen dezelfde naam kunnen hebben
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group";
        ObjectMapper mapper = new ObjectMapper();
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer "
                + "eyJ4NXQjUzI1NiI6IlBBTXpSdDdTRzJNTlAtMy1GMHdrWFBFYWNGbVRBN0w3RTBuZ3VLTm1qV3MiLCJraWQiOiJrZXlodWJpZHAtNDczNTU4Mzg4OTYwMTk4ODg5MyIsImFsZyI6IlJTMjU2In0.eyJhdWQiOiJlOWExN2E4NS1iZmRkLTQ5NjgtODEwZS0wZTQ4YzM5ZjZlYTkiLCJzdWIiOiJlOWExN2E4NS1iZmRkLTQ5NjgtODEwZS0wZTQ4YzM5ZjZlYTkiLCJuYmYiOjE2MDQ4ODU1NDcsImFhdCI6MTYwNDg4NTU0NywiYW1yIjoicHdkIiwic2NvcGUiOlsiY2xpZW50IiwiZ3JvdXAiLCJhY2Nlc3NfdmF1bHQiXSwiaXNzIjoiaHR0cHM6XC9cL2tleWh1Yi50b3BpY3Vzb25kZXJ3aWpzLm5sIiwidHlwZSI6ImFjY2VzcyIsImV4cCI6MTYwNDg4OTE0NywiaWF0IjoxNjA0ODg1NTQ3LCJqdGkiOiIzZDFmYTE2Mi02ZDJhLTRlMDEtODc4NC04MTk4YTYxNWY4ODIifQ.gDga-3NRdh-pah_tut2Pvnjw6kZQtRsB746u-3GZoXWJDRM7PmCLIGYJA4jY4cxDXLeIyZdnXRWxkT067qvrhCtux6rrkYLTUmfaoYCfBq1eTt5kDvecsyzOxxu2BCR7NJWbUGsOBk_g1vRp5ef_OFb4PzIwnF1z8Ri3dR888OtLv_dYL0qjG25pHTPSXb8BvIatr-V6zVb6NLP8Ss4M8HVQ5MoMff20FMimfhs1qYc99zPu36T60Z9tOkHQzhw4W_L7AEnjbTQIHZWOKYvigPRgLEpQdLPhH2LEQeaNxqHClzb7hk6LGPyuDtVfPSDW04PmeOYQxSTRotnvqs94IQ")
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44").get()) {
            String json = response.readEntity(String.class);
            System.out.println("Json Response: " + json);
            List<Integer> groupIds = JsonPath.parse(json).read("$..links..id");
            List<String> groupNames = JsonPath.parse(json).read("$..items..name");

            JsonNode node = mapper.readTree(json);
            JsonNode itemNode = node.get("items").get(0);
            String link = itemNode.get("links").get(0).get("href").asText();
            // System.out.println(link);
            KeyHubGroup groupTest = mapper.readValue(json, KeyHubGroup.class);
            System.out.println("Item Name: " + groupTest.getName());
            // System.out.println("KeyHub Group Name: " + groupTest.getName());
            // this.groupIdsAndNames = listsToMap(groupIds, groupNames);
        }
        // return this.groupIdsAndNames;
        return null;
    }

    public void fetchRecordsFromVault(int groupId) {
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group/" + groupId + "/vault/record";
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer "
                + "eyJ4NXQjUzI1NiI6IlBBTXpSdDdTRzJNTlAtMy1GMHdrWFBFYWNGbVRBN0w3RTBuZ3VLTm1qV3MiLCJraWQiOiJrZXlodWJpZHAtNDczNTU4Mzg4OTYwMTk4ODg5MyIsImFsZyI6IlJTMjU2In0.eyJhdWQiOiJlOWExN2E4NS1iZmRkLTQ5NjgtODEwZS0wZTQ4YzM5ZjZlYTkiLCJzdWIiOiJlOWExN2E4NS1iZmRkLTQ5NjgtODEwZS0wZTQ4YzM5ZjZlYTkiLCJuYmYiOjE2MDQ2MjY0MTksImFhdCI6MTYwNDYyNjQxOSwiYW1yIjoicHdkIiwic2NvcGUiOlsiY2xpZW50IiwiZ3JvdXAiLCJhY2Nlc3NfdmF1bHQiXSwiaXNzIjoiaHR0cHM6XC9cL2tleWh1Yi50b3BpY3Vzb25kZXJ3aWpzLm5sIiwidHlwZSI6ImFjY2VzcyIsImV4cCI6MTYwNDYzMDAxOSwiaWF0IjoxNjA0NjI2NDE5LCJqdGkiOiI5ODMwZTI2ZC1mNGM5LTQzY2YtOTVlMy1lYmViYWEyZjgwN2UifQ.hBz0TmKSTC_JUN8wphM4bE0XTFHOWk2o9j6l-ru0Yvxd9Jj4Rcy-SGMTLsI0agVYf0v9D3uAMI8KAq1NYI3Sjde76r96e6exxVtfPY5Fv_caeK13H0wwVoJDDGq9S3gEzq56rBSwOIkGgG5cYAP1lb0Zx7QSF305cP7YZtcvYbXfg1oeTA9S5Xwo7vas-Mh1OLDy2VFelBOEZrZaTVbly9at6dEFzwJx2yXwnV9X6-BQ9cNyzWd4luDGIcqadnV4-K7djI9PV5-WgCotwfNEbw-y9lvJsxA5f1F7f0jtNWNgO8PkMIqagDxPOV5JEJK3rH0XQ9wAaMaux5WgmTVwZQ")
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44").get()) {
            String json = response.readEntity(String.class);
            List<Integer> recordIds = JsonPath.parse(json).read("$..links..id");
            List<String> recordNames = JsonPath.parse(json).read("$..items..name");
            this.recordIdsAndNames = listsToMap(recordIds, recordNames);
            System.out.println(recordIdsAndNames.toString());
        }
    }

    public KeyHubRecord fetchRecordSecret(int groupId, int recordId) {
        System.out.println("CALLING FETCHRECORDSECRET");
        KeyHubRecord recordWithSecret;
        // https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group/32735401/vault/record/32839120?additional=secret
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group/32735401/vault/record/32839120?additional=secret";
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44")
                .header("topicus-Vault-session",
                        "c03fc46b-ac48-4d4a-8971-1ffa6a477100:AES:SYM1:zOCWcc4l9b9ZE6ztUkoOIeNkf1esXjCM+NXKZcTVd2A=")
                .get()) {
            String json = response.readEntity(String.class);
            String recordSecret = JsonPath.parse(json).read("$..secret..password").toString();

        }
        return null;
    }

    public KeyHubRecord fetchRecordSecret(KeyHubRecord record) {
        System.out.println("CALLING FETCHRECORDSECRET");
        KeyHubRecord recordWithSecret;
        // https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group/32735401/vault/record/32839120?additional=secret
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group/32735401/vault/record/32839120?additional=secret";
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try (Response response = target.request().header("Authorization", "Bearer "
                + "eyJ4NXQjUzI1NiI6IlBBTXpSdDdTRzJNTlAtMy1GMHdrWFBFYWNGbVRBN0w3RTBuZ3VLTm1qV3MiLCJraWQiOiJrZXlodWJpZHAtNDczNTU4Mzg4OTYwMTk4ODg5MyIsImFsZyI6IlJTMjU2In0.eyJhdWQiOiJlOWExN2E4NS1iZmRkLTQ5NjgtODEwZS0wZTQ4YzM5ZjZlYTkiLCJzdWIiOiJlOWExN2E4NS1iZmRkLTQ5NjgtODEwZS0wZTQ4YzM5ZjZlYTkiLCJuYmYiOjE2MDQ2NzMwMDQsImFhdCI6MTYwNDY3MzAwNCwiYW1yIjoicHdkIiwic2NvcGUiOlsiY2xpZW50IiwiZ3JvdXAiLCJhY2Nlc3NfdmF1bHQiXSwiaXNzIjoiaHR0cHM6XC9cL2tleWh1Yi50b3BpY3Vzb25kZXJ3aWpzLm5sIiwidHlwZSI6ImFjY2VzcyIsImV4cCI6MTYwNDY3NjYwNCwiaWF0IjoxNjA0NjczMDA0LCJqdGkiOiJlNzE0MzhlNC02OTUyLTQyMDctOTc4NS1jZGViOWU0ZTExYzMifQ.E5_it3W6Y_NDMU05bLFgZyfLveRcR4D8GePhCWxeg0LfsJUb4yCos8CSoLXwX6ENQ27bZfH2V7v9KJclFhCrRoalo0ypsbgWBYyqQ1iWMCOYj4sUx18Aqz_TpfylUbXJV1cQO2HFCcObTuS6XIztR2HjBducCPLlWpBDMXdPc9MP-3ubF_VveE1eqcyCSPF_fSyBWOHQ2ZQ7TuPHFmmM3XaVX1OZFEsUkRQ6SfwXJ2Hc3HiUXcXsX-o4oPxbkJPfOjYP_cgzKt3SEatGHvfE0-ogzh3BHWuXPYFqXVVQAAkdrYC1tojHot3Rjkfta41RR7HJrjEFagK61TLut93B8w")
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44")
                .header("topicus-Vault-session",
                        "c03fc46b-ac48-4d4a-8971-1ffa6a477100:AES:SYM1:zOCWcc4l9b9ZE6ztUkoOIeNkf1esXjCM+NXKZcTVd2A=")
                .get()) {
            String json = response.readEntity(String.class);
            String recordSecret = JsonPath.parse(json).read("$..secret..password[0]");
            System.out.println("Record Secret: " + recordSecret);
            // record.setRecordSecret(recordSecret);
        }
        return null;
    }

    private Map<Integer, String> listsToMap(List<Integer> key, List<String> value) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        for (int i = 0; i < key.size(); i++) {
            map.put(key.get(i), value.get(i));
        }
        return map;
    }

}
