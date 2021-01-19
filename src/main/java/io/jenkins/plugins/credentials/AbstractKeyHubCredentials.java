package io.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;

import hudson.util.Secret;

public abstract class AbstractKeyHubCredentials extends BaseStandardCredentials {

    private static final long serialVersionUID = 1L;
    protected String recordName;
    protected String href;
    protected String username;

    protected AbstractKeyHubCredentials(String id, String description) {
        super(id, description);
    }

    public String getRecordName() {
        return this.recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public abstract Secret getPassword();
}
