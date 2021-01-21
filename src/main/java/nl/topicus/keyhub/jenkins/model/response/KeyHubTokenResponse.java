package nl.topicus.keyhub.jenkins.model.response;

import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;

/**
 * Custom field vaultSession added to save the vault session.
 */
public class KeyHubTokenResponse extends AccessTokenResponse {

    @JsonProperty("vaultSession")
    private String vaultSession;
    private Instant tokenReceivedAt;

    public KeyHubTokenResponse() {
        super();
    }

    public String getVaultSession() {
        return this.vaultSession;
    }

    public void setVaultSession(String vaultSession) {
        this.vaultSession = vaultSession;
    }

    public Instant getTokenReceivedAt() {
        return this.tokenReceivedAt;
    }

    public void setTokenReceivedAt(Instant tokenReceivedAt) {
        this.tokenReceivedAt = tokenReceivedAt;
    }

    
}
