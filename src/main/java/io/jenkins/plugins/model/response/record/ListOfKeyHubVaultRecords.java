package io.jenkins.plugins.model.response.record;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListOfKeyHubVaultRecords {
    private List<KeyHubVaultRecord> records;

    @JsonProperty("items")
    public List<KeyHubVaultRecord> getRecords() {
        return records;
    }

    @JsonProperty("items")
    public void setItems(List<KeyHubVaultRecord> value) {
        this.records = value;
    }
}
