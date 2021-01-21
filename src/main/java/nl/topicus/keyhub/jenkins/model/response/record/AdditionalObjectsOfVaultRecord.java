package nl.topicus.keyhub.jenkins.model.response.record;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdditionalObjectsOfVaultRecord {
    private RecordSecret secret;

    @JsonProperty("secret")
    public RecordSecret getSecret() {
        return secret;
    }

    @JsonProperty("secret")
    public void setSecret(RecordSecret value) {
        this.secret = value;
    }
}