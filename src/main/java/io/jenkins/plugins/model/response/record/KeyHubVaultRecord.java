package io.jenkins.plugins.model.response.record;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.jenkins.plugins.model.response.Link;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyHubVaultRecord {
    private String type;
    private List<Link> links;
    private AdditionalObjectsOfVaultRecord additionalObjects;
    private String uuid;
    private String name;
    private String username;

    @JsonProperty("$type")
    public String getType() {
        return type;
    }

    @JsonProperty("$type")
    public void setType(String value) {
        this.type = value;
    }

    @JsonProperty("links")
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Link> value) {
        this.links = value;
    }

    @JsonProperty("additionalObjects")
    public AdditionalObjectsOfVaultRecord getAdditionalObjects() {
        return additionalObjects;
    }

    @JsonProperty("additionalObjects")
    public void setAdditionalObjects(AdditionalObjectsOfVaultRecord value) {
        this.additionalObjects = value;
    }

    @JsonProperty("uuid")
    public String getUUID() {
        return this.uuid;
    }

    @JsonProperty("uuid")
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String value) {
        this.name = value;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String value) {
        this.username = value;
    }

    public String getHref() {
        return links.get(0).getHref();
    }

    public void setRecordSecret(String secret) {
        additionalObjects.getSecret().setPassword(secret);
    }
}
