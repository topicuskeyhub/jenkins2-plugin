package nl.topicus.keyhub.jenkins.model;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.util.Secret;

public class ClientCredentials {
    private String clientId;
    private Secret clientSecret;

    @DataBoundConstructor
    public ClientCredentials(String clientId, Secret clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public ClientCredentials() {

    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Secret getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(Secret clientSecret) {
        this.clientSecret = clientSecret;
    }

}
