package io.jenkins.plugins.vault;

import java.io.IOException;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.credentialsbinding.BindingDescriptor;
import org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordBinding;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.ItemGroup;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.configuration.FolderKeyHubVaultConfiguration;
import io.jenkins.plugins.credentials.KeyHubCredentialsStore;
import io.jenkins.plugins.model.ClientCredentials;

public class KeyhubUsernamePasswordBinding extends UsernamePasswordBinding {

    @DataBoundConstructor
    public KeyhubUsernamePasswordBinding(String variable, String credentialsId) {
        super(variable, credentialsId);
        System.out.println("Custom Binding constructor triggered.");
    }

    @Override
    protected Class<StandardUsernamePasswordCredentials> type() {
        return StandardUsernamePasswordCredentials.class;
    }

    @Override
    public SingleEnvironment bindSingle(@NonNull Run<?, ?> build, @Nullable FilePath workspace,
            @Nullable Launcher launcher, @NonNull TaskListener listener) throws IOException, InterruptedException {
        StandardUsernamePasswordCredentials credentials = getCredentials(build);
        ClientCredentials clientCredentials = new ClientCredentials();
        for (ItemGroup p = build.getParent().getParent(); p instanceof AbstractFolder; p = ((AbstractFolder) p)
                .getParent()) {
            FolderKeyHubVaultConfiguration folderProperty = ((AbstractFolder<?>) p).getProperties()
                    .get(FolderKeyHubVaultConfiguration.class);
            clientCredentials.setClientId(folderProperty.getConfiguration().getVaultId());
            clientCredentials.setClientSecret(Secret.fromString(folderProperty.getConfiguration().getVaultSecret()));
        }
        KeyHubCredentialsStore store = new KeyHubCredentialsStore();
        store.getCredentials(clientCredentials);
        return new SingleEnvironment(credentials.getUsername() + ':' + credentials.getPassword().getPlainText());
    }

    @Symbol("usernameColonPassword")
    @Extension
    public static class DescriptorImpl extends BindingDescriptor<StandardUsernamePasswordCredentials> {

        @Override
        protected Class<StandardUsernamePasswordCredentials> type() {
            return StandardUsernamePasswordCredentials.class;
        }

        @Override
        public String getDisplayName() {
            return "Username and password binding";
        }

        @Override
        public boolean requiresWorkspace() {
            return false;
        }
    }
}
