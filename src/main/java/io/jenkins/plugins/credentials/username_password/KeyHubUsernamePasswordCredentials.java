package io.jenkins.plugins.credentials.username_password;

import java.util.function.Supplier;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import io.jenkins.plugins.credentials.AbstractKeyHubCredentials;

public class KeyHubUsernamePasswordCredentials extends AbstractKeyHubCredentials
        implements StandardUsernamePasswordCredentials {

    private static final long serialVersionUID = 1L;
    private Supplier<Secret> password;

    public KeyHubUsernamePasswordCredentials(KeyHubCredentialsBuilder builder) {
        super(builder.id, builder.recordName);
        this.href = builder.href;
        this.username = builder.username;
        this.password = builder.password;
    }

    @NonNull
    @Override
    public Secret getPassword() {
        return password.get();
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
        private String href;
        private String username;
        private Supplier<Secret> password;

        public static KeyHubCredentialsBuilder newInstance() {
            return new KeyHubCredentialsBuilder();
        }

        private KeyHubCredentialsBuilder() {
        }

        public KeyHubCredentialsBuilder id(String id) {
            this.id = id;
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

        public KeyHubCredentialsBuilder password(Supplier<Secret> password) {
            this.password = password;
            return this;
        }

        public KeyHubUsernamePasswordCredentials build() {
            return new KeyHubUsernamePasswordCredentials(this);
        }

    }
}
