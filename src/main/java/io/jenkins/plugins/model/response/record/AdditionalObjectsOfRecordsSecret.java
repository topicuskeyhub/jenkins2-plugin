package io.jenkins.plugins.model.response.record;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdditionalObjectsOfRecordsSecret {
    private String secret;

    @JsonProperty("secret")
    public String getSecret() {
        return secret;
    }

    @JsonProperty("secret")
    public void setSecret(String value) {
        this.secret = value;
    }
}
