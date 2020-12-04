package io.jenkins.plugins.vault;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.matchers.IdMatcher;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.ItemGroup;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.Secret;
import io.jenkins.plugins.configuration.FolderKeyHubVaultConfiguration;
import io.jenkins.plugins.configuration.VaultConfiguration;
import io.jenkins.plugins.credentials.KeyHubClientCredentials;
import io.jenkins.plugins.model.ClientCredentials;
import jenkins.tasks.SimpleBuildWrapper;

public class VaultBuildWrapper extends SimpleBuildWrapper {

    ClientCredentials clientCredentials;
    VaultConfiguration configuration;
    private transient VaultAccessor vaultAccessor = new VaultAccessor();
    private String vaultUrl;

    @DataBoundConstructor
    public VaultBuildWrapper(VaultConfiguration configuration) {
        this.configuration = configuration;
    }

    public void provideValuesFromVault(Context context, Run build, EnvVars envVars)
            throws IOException, InterruptedException {
        ClientCredentials buildCredentials = new ClientCredentials();

        for (ItemGroup p = build.getParent().getParent(); p instanceof AbstractFolder; p = ((AbstractFolder) p)
                .getParent()) {
            FolderKeyHubVaultConfiguration folderProperty = ((AbstractFolder<?>) p).getProperties()
                    .get(FolderKeyHubVaultConfiguration.class);
            buildCredentials.setClientId(folderProperty.getConfiguration().getVaultId());
            buildCredentials.setClientSecret(Secret.fromString(folderProperty.getConfiguration().getVaultSecret()));

        }
        vaultAccessor.setCredentials(buildCredentials);
        vaultAccessor.connect();
    }

    public KeyHubClientCredentials retrieveVaultCredentials(Run build) {
        String id = configuration.getVaultId();

        List<KeyHubClientCredentials> credentials = CredentialsProvider.lookupCredentials(KeyHubClientCredentials.class,
                build.getParent(), ACL.SYSTEM, Collections.emptyList());
        KeyHubClientCredentials credential = CredentialsMatchers.firstOrNull(credentials, new IdMatcher(id));

        return credential;
    }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener,
            EnvVars initialEnvironment) throws IOException, InterruptedException {
        provideValuesFromVault(context, build, initialEnvironment);
        if (configuration != null) {
            provideValuesFromVault(context, build, initialEnvironment);
        }

    }

    public ClientCredentials getClientCredentials() {
        return this.clientCredentials;
    }

    public void setClientCredentials(ClientCredentials clientCredentials) {
        this.clientCredentials = clientCredentials;
    }

    public VaultConfiguration getConfiguration() {
        return this.configuration;
    }

    @DataBoundSetter
    public void setConfiguration(VaultConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getVaultUrl() {
        return this.vaultUrl;
    }

    @DataBoundSetter
    public void setVaultUrl(String vaultUrl) {
        this.vaultUrl = "testing";
    }

    @Extension
    @Symbol("withKeyHub")
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(VaultBuildWrapper.class);
            load();
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "KeyHub Vault Plugin Test";
        }
    }

}
