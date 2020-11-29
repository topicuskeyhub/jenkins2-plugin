package io.jenkins.plugins.model.response.record;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdditionalObjectsOfRecordsSecret {
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
