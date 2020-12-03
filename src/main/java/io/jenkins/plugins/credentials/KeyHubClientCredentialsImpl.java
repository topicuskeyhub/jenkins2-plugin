package io.jenkins.plugins.credentials;

import java.io.IOException;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;

import org.apache.http.client.ClientProtocolException;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import io.jenkins.plugins.configuration.GlobalPluginConfiguration;


public class KeyHubClientCredentialsImpl extends BaseStandardCredentials implements KeyHubClientCredentials {
    /**
     *
     */
    private String globalUri;
    private final String clientId;
    private final Secret clientSecret;
    private final String keyhubGroup;
    private final String keyhubRecord;

    @DataBoundConstructor
    public KeyHubClientCredentialsImpl(@CheckForNull CredentialsScope scope, @CheckForNull String id,
            @NonNull String clientId, @NonNull String clientSecret, @NonNull String keyhubGroup,
            @NonNull String keyhubRecord, @CheckForNull String description) {
        super(scope, id, description);
        this.globalUri = GlobalPluginConfiguration.getInstance().getKeyhubURI();
        this.clientId = clientId;
        this.clientSecret = Secret.fromString(clientSecret);
        this.keyhubGroup = keyhubGroup;
        this.keyhubRecord = keyhubRecord;
    }

    @NonNull
    public String getClientId() {
        return clientId;
    }

    @NonNull
    public Secret getClientSecret() {
        return clientSecret;
    }

    @NonNull
    public String getKeyhubGroup() {
        return keyhubGroup;
    }

    @NonNull
    public String getKeyhubRecord() {
        return keyhubRecord;
    }

    public String getGlobalUri() {
        return this.globalUri;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {

        private static final String ICON_CLASS = "icon-keyhub-credentials";

        public DescriptorImpl() throws ClientProtocolException, IOException {
            load();
        }

        @Override
        public String getIconClassName() {
            return ICON_CLASS;
        }

        @Override
        public String getDisplayName() {
            return "Keyhub Vault Credentials";
        }
    }

}
