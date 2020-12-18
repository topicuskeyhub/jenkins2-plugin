package io.jenkins.plugins.configuration;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.Secret;
import io.jenkins.plugins.model.ClientCredentials;

public class ClientConfiguration extends AbstractDescribableImpl<ClientConfiguration> implements Serializable {

    private String keyhubURI;
    private String clientId;
    private String clientSecret;

    @DataBoundConstructor
    public ClientConfiguration() {
        this.keyhubURI = GlobalPluginConfiguration.getInstance().getKeyhubURI();
    }

    public String getKeyhubURI() {
        return this.keyhubURI;
    }

    public String getClientId() {
        return this.clientId;
    }

    @DataBoundSetter
    public void setClientId(String vaultId) {
        this.clientId = vaultId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    @DataBoundSetter
    public void setClientSecret(String vaultSecret) {
        this.clientSecret = vaultSecret;
    }

    public ClientCredentials getClientCredentials() {
        return new ClientCredentials(getClientId(), Secret.fromString(getClientSecret()));
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ClientConfiguration> {

        @Override
        @NonNull
        public String getDisplayName() {
            return "Vault Configuration";
        }
    }
}
