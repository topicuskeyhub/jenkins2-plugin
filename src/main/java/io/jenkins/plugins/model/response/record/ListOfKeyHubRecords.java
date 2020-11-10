package io.jenkins.plugins.model.response.record;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListOfKeyHubRecords {
    private List<KeyHubRecord> records;

    @JsonProperty("items")
    public List<KeyHubRecord> getItems() {
        return records;
    }

    @JsonProperty("items")
    public void setItems(List<KeyHubRecord> value) {
        this.records = value;
    }
}
