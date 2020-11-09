package io.jenkins.plugins.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyHubGroup {
    private Item[] items;

    @JsonProperty("items")
    public Item[] getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setItems(Item[] value) {
        this.items = value;
    }

    public String getName() {
        return items[0].getName();
    }

    public String getHref() {
        return items[0].getLinks()[0].getHref();
    }
}
