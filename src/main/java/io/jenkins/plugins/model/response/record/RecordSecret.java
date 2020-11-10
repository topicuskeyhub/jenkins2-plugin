package io.jenkins.plugins.model.response.record;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordSecret {
    private String type;
    private String password;

    @JsonProperty("$type")
    public String getType() {
        return type;
    }

    @JsonProperty("$type")
    public void setType(String value) {
        this.type = value;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String value) {
        this.password = value;
    }
}
