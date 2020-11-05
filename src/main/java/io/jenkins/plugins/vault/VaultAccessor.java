package io.jenkins.plugins.vault;

import java.io.IOException;
import java.io.Serializable;

import java.util.List;
import javax.ws.rs.client.Entity;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import com.jayway.jsonpath.JsonPath;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import hudson.util.Secret;
import io.jenkins.plugins.credentials.KeyHubClientCredentials;

public class VaultAccessor implements Serializable {

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
     * @throws ClientProtocolException
     * @throws IOException
     */
    public void fetchAuthenticationTokenAndGetVaultAccess() throws ClientProtocolException, IOException {
        final String AUTH_ENDPOINT = "https://keyhub.topicusonderwijs.nl/login/oauth2/token?authVault=access";
        Form connectionRequest = new Form().param("grant_type", "client_credentials");
        ResteasyWebTarget target = restClientBuilder.getClient().target(AUTH_ENDPOINT);
        target.register(new BasicAuthentication(credentials.getClientId(), Secret.toString(credentials.getClientId())));
        target.request().header("Accept", "application/vnd.topicus.keyhub+json;version=44");
        this.keyhubToken = target.request().post(Entity.form(connectionRequest), KeyHubTokenResponse.class);
    }

    public List<String> fetchGroupData() throws IOException {
        final String ENDPOINT = "https://keyhub.topicusonderwijs.nl/keyhub/rest/v1/group";
        ResteasyWebTarget target = restClientBuilder.getClient().target(ENDPOINT);
        List<String> groupNamesList;
        try (Response response = target.request().header("Authorization", "Bearer " + keyhubToken.getToken())
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.topicus.keyhub+json;version=44").get()) {
            String json = response.readEntity(String.class);
            groupNamesList = JsonPath.parse(json).read("$..items..name");
        }
        return groupNamesList;
    }

}
