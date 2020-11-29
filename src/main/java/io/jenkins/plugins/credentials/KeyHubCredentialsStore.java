package io.jenkins.plugins.credentials;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.CredentialsStoreAction;
import com.cloudbees.plugins.credentials.common.IdCredentials;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.google.common.base.Suppliers;

import org.acegisecurity.Authentication;
import org.jenkins.ui.icon.Icon;
import org.jenkins.ui.icon.IconSet;
import org.jenkins.ui.icon.IconType;
import org.kohsuke.stapler.export.ExportedBean;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.model.ModelObject;
import hudson.security.ACL;
import hudson.security.Permission;
import jenkins.model.Jenkins;

public class KeyHubCredentialsStore extends CredentialsStore {

    private final KeyHubCredentialsProvider provider;
    private final KeyHubCredentialsStoreAction action = new KeyHubCredentialsStoreAction(this);

    public KeyHubCredentialsStore(KeyHubCredentialsProvider provider) {
        super(KeyHubCredentialsProvider.class);
        this.provider = provider;
    }

    @Override
    public ModelObject getContext() {
        return Jenkins.get();
    }

    @Override
    public boolean hasPermission(Authentication a, Permission permission) {
        return CredentialsProvider.VIEW.equals(permission) && Jenkins.get().getACL().hasPermission(a, permission);
    }

    @Override
    public List<Credentials> getCredentials(Domain domain) {
        if (Domain.global().equals(domain) && Jenkins.get().hasPermission(CredentialsProvider.VIEW)) {
            return provider.getCredentials(Credentials.class, Jenkins.get(), ACL.SYSTEM);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean addCredentials(Domain domain, Credentials credentials) throws IOException {
        throw new UnsupportedOperationException("Jenkins may not add credentials to the KeyHub Vault");
    }

    @Override
    public boolean removeCredentials(Domain domain, Credentials credentials) throws IOException {
        throw new UnsupportedOperationException("Jenkins may not remove credentials from the KeyHub Vault");
    }

    @Override
    public boolean updateCredentials(Domain domain, Credentials current, Credentials replacement) throws IOException {
        throw new UnsupportedOperationException("Jenkins may not update credentials of the KeyHub Vault");
    }

    @Nullable
    @Override
    public CredentialsStoreAction getStoreAction() {
        return action;
    }

    @ExportedBean
    public static final class KeyHubCredentialsStoreAction extends CredentialsStoreAction {

        private static final String ICON_CLASS = "icon-keyhub-credentials-vault";

        private final KeyHubCredentialsStore store;

        private KeyHubCredentialsStoreAction(KeyHubCredentialsStore store) {
            this.store = store;
            addIcons();
        }

        private void addIcons() {
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-sm", "keyhub-vault-plugin/images/16x16/icon.jpg",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-md", "keyhub-vault-plugin/images/24x24/icon.jpg",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-lg", "keyhub-vault-plugin/images/32x32/icon.jpg",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-xlg", "keyhub-vault-plugin/images/48x48/icon.jpg",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
        }

        @Override
        @NonNull
        public CredentialsStore getStore() {
            return store;
        }

        // @Override
        // public String getIconFileName() {
        // return isVisible() ?
        // "/plugin/gcp-secrets-manager-credentials-provider/images/32x32/icon.png" :
        // null;
        // }

        @Override
        public String getIconClassName() {
            return isVisible() ? ICON_CLASS : null;
        }

        @Override
        public String getDisplayName() {
            return "KeyHub Vault";
        }
    }
}
