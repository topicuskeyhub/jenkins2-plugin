package io.jenkins.plugins.credentials;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;

import org.apache.http.client.ClientProtocolException;
import org.jenkins.ui.icon.Icon;
import org.jenkins.ui.icon.IconSet;
import org.jenkins.ui.icon.IconType;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import io.jenkins.plugins.configuration.GlobalPluginConfiguration;
import net.sf.json.JSONObject;

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

            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-sm", "webapp/images/16x16/keyHub_key.png",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-md", "webapp/images/24x24/keyHub_key.png",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-lg", "webapp/images/32x32/keyHub_key.png",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-xlg", "webapp/images/48x48/keyHub_key.png",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
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
