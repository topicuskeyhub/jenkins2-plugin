package io.jenkins.plugins.model.response.group;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.jenkins.plugins.model.response.Link;

public class KeyHubGroup {
    private List<Link> links;
    private AdditionalObjectsOfGroup additionalObjects;
    private String name;

    @JsonProperty("links")
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Link> value) {
        this.links = value;
    }

    @JsonProperty("additionalObjects")
    public AdditionalObjectsOfGroup getAdditionalObjects() {
        return additionalObjects;
    }

    @JsonProperty("additionalObjects")
    public void setAdditionalObjects(AdditionalObjectsOfGroup value) {
        this.additionalObjects = value;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String value) {
        this.name = value;
    }

    public String getHref() {
        return links.get(0).getHref();
    }

    public int getId() {
        return links.get(0).getID();
    }

}
