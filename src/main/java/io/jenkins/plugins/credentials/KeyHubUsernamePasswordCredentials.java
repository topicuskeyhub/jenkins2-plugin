package io.jenkins.plugins.credentials;

import java.io.IOException;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import io.jenkins.plugins.vault.IVaultAccessor;

public class KeyHubUsernamePasswordCredentials extends AbstractKeyHubCredentials
        implements StandardUsernamePasswordCredentials {

    public KeyHubUsernamePasswordCredentials(KeyHubCredentialsBuilder builder) {
        super(builder.id, builder.recordName, builder.va);
        //this.va = builder.va;
        this.href = builder.href;
        this.username = builder.username;
    }

    @Override
    public Secret getPassword() {
        try {
            return va.fetchRecordSecret(this.href);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Extension
    @SuppressWarnings("unused")
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
        @Override
        @NonNull
        public String getDisplayName() {
            return "KeyHubUsernamePasswordCredentials";
        }

        @Override
        public String getIconClassName() {
            return "icon-keyhub-credentials-vault";
        }
    }

    public static class KeyHubCredentialsBuilder {

        private String id;
        private String recordName;
        private IVaultAccessor va;
        private String href;
        private String username;

        public static KeyHubCredentialsBuilder newInstance() {
            return new KeyHubCredentialsBuilder();
        }

        private KeyHubCredentialsBuilder() {
        }

        public KeyHubCredentialsBuilder id(String id) {
            this.id = id;
            return this;
        }

        public KeyHubCredentialsBuilder va(IVaultAccessor va) {
            this.va = va;
            return this;
        }

        public KeyHubCredentialsBuilder href(String href) {
            this.href = href;
            return this;
        }

        public KeyHubCredentialsBuilder recordName(String recordName) {
            this.recordName = recordName;
            return this;
        }

        public KeyHubCredentialsBuilder username(String username) {
            this.username = username;
            return this;
        }

        public KeyHubUsernamePasswordCredentials build() {
            return new KeyHubUsernamePasswordCredentials(this);
        }

    }
}
