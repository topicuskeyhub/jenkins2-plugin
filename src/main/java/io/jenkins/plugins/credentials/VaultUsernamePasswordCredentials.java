package io.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;

import hudson.util.Secret;

public class VaultUsernamePasswordCredentials extends BaseStandardCredentials implements UsernamePasswordCredentials {

    private String uuid;

    public VaultUsernamePasswordCredentials(String id, String uuid) {
        super(id, uuid);

    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Secret getPassword() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
