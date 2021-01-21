package nl.topicus.keyhub.jenkins.configuration;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

public class FolderKeyHubClientConfiguration extends AbstractFolderProperty<AbstractFolder<?>> {

    private final ClientConfiguration configuration;

    @DataBoundConstructor
    public FolderKeyHubClientConfiguration(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    public FolderKeyHubClientConfiguration() {
        this.configuration = null;
    }

    public ClientConfiguration getConfiguration() {
        return configuration;
    }

    @Extension
    public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {

    }

}
