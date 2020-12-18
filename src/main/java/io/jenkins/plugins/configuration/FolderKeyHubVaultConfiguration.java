package io.jenkins.plugins.configuration;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.ItemGroup;

public class FolderKeyHubVaultConfiguration extends AbstractFolderProperty<AbstractFolder<?>> {

    private final VaultConfiguration configuration;

    @DataBoundConstructor
    public FolderKeyHubVaultConfiguration(VaultConfiguration configuration) {
        this.configuration = configuration;
    }

    public FolderKeyHubVaultConfiguration() {
        this.configuration = null;
    }

    public VaultConfiguration getConfiguration() {
        return configuration;
    }

    @Extension
    public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {

    }

}
