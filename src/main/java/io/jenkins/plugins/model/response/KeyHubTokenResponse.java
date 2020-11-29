package io.jenkins.plugins.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;

/**
 * Custom field vaultSession added to save the vault session.
 */
public class KeyHubTokenResponse extends AccessTokenResponse {

    @JsonProperty("vaultSession")
    protected String vaultSession;

    public KeyHubTokenResponse() {
        super();
    }

    public String getVaultSession() {
        return this.vaultSession;
    }

    public void setVaultSession(String vaultSession) {
        this.vaultSession = vaultSession;
    }
}
