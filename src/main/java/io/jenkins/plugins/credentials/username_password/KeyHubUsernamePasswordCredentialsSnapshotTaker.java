package io.jenkins.plugins.credentials.username_password;

import com.cloudbees.plugins.credentials.CredentialsSnapshotTaker;

import hudson.Extension;
import hudson.util.Secret;
import io.jenkins.plugins.credentials.Snapshot;

@Extension
public class KeyHubUsernamePasswordCredentialsSnapshotTaker
        extends CredentialsSnapshotTaker<KeyHubUsernamePasswordCredentials> {

    @Override
    public Class<KeyHubUsernamePasswordCredentials> type() {
        System.out.println("UsernamePasswordCredentials Type Snapshot taken.");
        return KeyHubUsernamePasswordCredentials.class;
    }

    @Override
    public KeyHubUsernamePasswordCredentials snapshot(KeyHubUsernamePasswordCredentials credential) {
        System.out.println("UsernamePasswordCredentials Snapshot was taken.");
        return KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance().id(credential.getId())
                .recordName(credential.getRecordName()).href(credential.getHref()).username(credential.getUsername())
                .password(new SecretSnapshot(credential.getPassword())).build();
    }

    private static class SecretSnapshot extends Snapshot<Secret> {
        SecretSnapshot(Secret value) {
            super(value);
            System.out.println("Constructor of SecretSnapshot is being called.");
        }
    }

}
