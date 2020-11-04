package io.jenkins.plugins.vault;

import java.io.IOException;
import java.io.Serializable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;
import io.jenkins.plugins.credentials.KeyHubClientCredentials;

public class VaultAccessor implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private KeyHubClientCredentials credentials;
    private RestClientBuilder restClientBuilder = new RestClientBuilder();

    public VaultAccessor() {
    }

    public VaultAccessor(KeyHubClientCredentials credentials) {
        this.credentials = credentials;
    }

    /**
     * 
     * Fetches the Auth2.0 and vault access token from Keyhub.
     * 
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String fetchAuthenticationTokenAndGetVaultAccess() throws ClientProtocolException, IOException {
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/login/oauth2/token?authVault=access";

        System.out.println("Triggering fetchAuthenticationTokenAndGetVaultAccess method.");
        fetchGroupData();
        Form connectionRequest = new Form().param("grant_type", "client_credentials");
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        target.register(new BasicAuthentication("clientId",
                "clientSecret")); //TODO Use Secret.toString() if request is done.
        target.request().header("Accept", "application/vnd.topicus.keyhub+json;version=44");
        try (Response response = target.request().post(Entity.entity(connectionRequest, MediaType.APPLICATION_JSON),
                Response.class)) {
            System.out.println("Status code: " + response.getStatus());
        }

        // https://keyhub.topicusonderwijs.nl/login/oauth2/token

        return "Method called";
    }

    public void fetchGroupData() throws JsonMappingException, JsonProcessingException {
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group";

        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        try(Response response = target.request().header("Authorization", 
        "Bearer token")
        .header("Content-Type", "application/json")
        .header("Accept", "application/vnd.topicus.keyhub+json;version=44")
        .get()) {
            System.out.println("Group data status code: "  + response.getStatus());
            System.out.println(response.readEntity(String.class));
        }

        
        //https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group
    }








}

