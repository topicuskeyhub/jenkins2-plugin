package io.jenkins.plugins.model;

import hudson.util.Secret;

/**
 * KeyHubRecord
 */
public class KeyHubRecord {

    private int id;
    private String name;
    private Secret recordSecret;
    private String selfLink;

    public KeyHubRecord(int id, String name) {
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

    public Secret getRecordSecret() {
        return this.recordSecret;
    }

    public void setRecordSecret(Secret recordSecret) {
        this.recordSecret = recordSecret;
    }

}