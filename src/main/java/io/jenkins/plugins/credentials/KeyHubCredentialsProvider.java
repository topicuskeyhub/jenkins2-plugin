package io.jenkins.plugins.credentials;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.google.common.base.Suppliers;

import org.acegisecurity.Authentication;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import hudson.util.Secret;
import io.jenkins.plugins.configuration.FolderKeyHubVaultConfiguration;
import io.jenkins.plugins.configuration.GlobalPluginConfiguration;
import io.jenkins.plugins.model.ClientCredentials;
import io.jenkins.plugins.model.response.group.ListOfKeyHubGroups;
import io.jenkins.plugins.model.response.record.ListOfKeyHubRecords;
import io.jenkins.plugins.vault.VaultAccessor;
import jenkins.model.Jenkins;

@Extension
public class KeyHubCredentialsProvider extends CredentialsProvider {

    private final KeyHubCredentialsStore store = new KeyHubCredentialsStore(this);
    private ClientCredentials clientCredentials;

    private Supplier<Collection<StandardUsernamePasswordCredentials>> credentialsSupplier = memoizeWithExpiration(
            this::fetchCredentials, Duration.ofMinutes(5));

    public void refreshCredentials() {
        credentialsSupplier = memoizeWithExpiration(this::fetchCredentials, Duration.ofMinutes(5));
    }

    private <T> Supplier<T> memoizeWithExpiration(Supplier<T> base, Duration duration) {
        return Suppliers.memoizeWithExpiration(base::get, duration.toMillis(), TimeUnit.MILLISECONDS)::get;
    }

    private Collection<StandardUsernamePasswordCredentials> fetchCredentials() {
        ClientCredentials clientCredentials = new ClientCredentials();
        GlobalPluginConfiguration keyhubGlobalConfig = GlobalPluginConfiguration.all()
                .get(GlobalPluginConfiguration.class);
        if (keyhubGlobalConfig == null) {
            throw new NullPointerException("No global config was entered."); // Make a custom runtime exception
        }
        VaultAccessor va = new VaultAccessor(this.clientCredentials);
        ListOfKeyHubRecords khRecords = new ListOfKeyHubRecords();
        ListOfKeyHubGroups khGroups = new ListOfKeyHubGroups();
        List<StandardUsernamePasswordCredentials> jRecords = new ArrayList<>();
        try {
            va.connect();
            khGroups = va.fetchGroupData();
            khRecords = va.fetchRecordsFromVault(khGroups.getGroups().get(0));
            for (int i = 0; i < khRecords.getItems().size(); i++) {
                jRecords.add(new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
                        khRecords.getItems().get(i).getUUID(), khRecords.getItems().get(i).getName(),
                        khRecords.getItems().get(i).getUsername(), ""));
            }
            return jRecords;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public <C extends Credentials> List<C> getCredentials(Class<C> type, ItemGroup itemGroup,
            @Nullable Authentication authentication) {
        List<FolderKeyHubVaultConfiguration> listOfClientCredentials;
        List<AbstractFolder> foldersList = itemGroup.getAllItems(AbstractFolder.class);

        final List<C> list = new ArrayList<>();
        if (this.clientCredentials == null) {
        }

        try {
            for (StandardUsernamePasswordCredentials credential : credentialsSupplier.get()) {
                if (type.isAssignableFrom(credential.getClass())) {
                    list.add(type.cast(credential));
                }
                // log if it doesn't match?
            }
            return list;
        } catch (RuntimeException e) {
        }
        return Collections.emptyList();
    }

    public ClientCredentials getClientCredentials() {
        return clientCredentials;
    }

    public void setClientCredentials(ClientCredentials credentials) {
        this.clientCredentials = credentials;
    }

    @Override
    public String getDisplayName() {
        return "KeyHub Credentials Provider"; // TODO Use Properties file
    }

    @Override
    public CredentialsStore getStore(ModelObject object) {
        return object == Jenkins.get() ? store : null;
    }

    @Override
    public String getIconClassName() {
        return "icon-keyhub-credentials-vault";
    }
}