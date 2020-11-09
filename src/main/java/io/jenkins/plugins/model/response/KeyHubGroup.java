package io.jenkins.plugins.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyHubGroup {
    private Item[] items;
    private Object[] segments;

    @JsonProperty("items")
    public Item[] getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setItems(Item[] value) {
        this.items = value;
    }

    @JsonProperty("segments")
    public Object[] getSegments() {
        return segments;
    }

    @JsonProperty("segments")
    public void setSegments(Object[] value) {
        this.segments = value;
    }

    public String getName() {
        return items[0].getName();
    }
}
