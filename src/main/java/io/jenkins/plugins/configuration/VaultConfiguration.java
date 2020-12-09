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

public class VaultConfiguration extends AbstractDescribableImpl<VaultConfiguration> implements Serializable {

    private String keyhubURI;
    private String vaultId;
    private String vaultSecret;

    @DataBoundConstructor
    public VaultConfiguration() {
        this.keyhubURI = GlobalPluginConfiguration.getInstance().getKeyhubURI();
        // no args constructor
    }

    public String getKeyhubURI() {
        return this.keyhubURI;
    }

    public String getVaultId() {
        return this.vaultId;
    }

    @DataBoundSetter
    public void setVaultId(String vaultId) {
        this.vaultId = vaultId;
    }

    public String getVaultSecret() {
        return this.vaultSecret;
    }

    @DataBoundSetter
    public void setVaultSecret(String vaultSecret) {
        this.vaultSecret = vaultSecret;
    }

    public ClientCredentials getClientCredentials() {
        return new ClientCredentials(getVaultId(), Secret.fromString(getVaultSecret()));
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<VaultConfiguration> {

        @Override
        @NonNull
        public String getDisplayName() {
            return "Vault Configuration";
        }
    }
}
