package io.jenkins.plugins.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyHubGroup {

    private int id;
    private String name;
    @JsonProperty("href")
    private String hrefLink;

    public KeyHubGroup() {
        super();
    }

    public KeyHubGroup(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
