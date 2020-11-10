package io.jenkins.plugins.model.response.group;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListOfKeyHubGroups {
    private List<KeyHubGroup> groups;

    @JsonProperty("items")
    public List<KeyHubGroup> getGroups() {
        return groups;
    }

    @JsonProperty("items")
    public void setItems(List<KeyHubGroup> value) {
        this.groups = value;
    }

    public String getName() {
        return groups.get(0).getName();
    }
}
