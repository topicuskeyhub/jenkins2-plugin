package io.jenkins.plugins.credentials.username_password;

import com.cloudbees.plugins.credentials.CredentialsSnapshotTaker;

import hudson.Extension;
import io.jenkins.plugins.credentials.Snapshot;

@Extension
public class KeyHubUsernamePasswordCredentialsSnapshotTaker
        extends CredentialsSnapshotTaker<KeyHubUsernamePasswordCredentials> {

    @Override
    public Class<KeyHubUsernamePasswordCredentials> type() {
        return KeyHubUsernamePasswordCredentials.class;
    }

    @Override
    public KeyHubUsernamePasswordCredentials snapshot(KeyHubUsernamePasswordCredentials credential) {
        return KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance().id(credential.getId())
                .recordName(credential.getRecordName()).href(credential.getHref()).username(credential.getUsername())
                .password(new Snapshot<>(credential.getPassword())).build();
    }
}
