package io.jenkins.plugins.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Permission {
    private String full;
    private String type;
    private String[] operations;
    private String[] instances;

    @JsonProperty("full")
    public String getFull() {
        return full;
    }

    @JsonProperty("full")
    public void setFull(String value) {
        this.full = value;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String value) {
        this.type = value;
    }

    @JsonProperty("operations")
    public String[] getOperations() {
        return operations;
    }

    @JsonProperty("operations")
    public void setOperations(String[] value) {
        this.operations = value;
    }

    @JsonProperty("instances")
    public String[] getInstances() {
        return instances;
    }

    @JsonProperty("instances")
    public void setInstances(String[] value) {
        this.instances = value;
    }
}
