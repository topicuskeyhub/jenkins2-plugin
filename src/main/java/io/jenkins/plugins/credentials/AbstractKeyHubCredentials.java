package io.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;

import hudson.util.Secret;
import io.jenkins.plugins.vault.IVaultAccessor;

public abstract class AbstractKeyHubCredentials extends BaseStandardCredentials {

    protected String recordName;
    protected String href;
    protected String username;

    public AbstractKeyHubCredentials(String id, String description) {
        super(id, description);
        // TODO Auto-generated constructor stub
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