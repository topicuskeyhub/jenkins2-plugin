package io.jenkins.plugins.credentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.common.IdCredentials;

import org.acegisecurity.Authentication;

import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import hudson.security.ACL;
import io.jenkins.plugins.configuration.FolderKeyHubClientConfiguration;
import io.jenkins.plugins.configuration.GlobalPluginConfiguration;
import io.jenkins.plugins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import io.jenkins.plugins.model.ClientCredentials;
import io.jenkins.plugins.model.response.group.KeyHubGroup;
import io.jenkins.plugins.model.response.record.KeyHubRecord;
import io.jenkins.plugins.vault.VaultAccessor;

@Extension
public class KeyHubCredentialsProvider extends CredentialsProvider {

    private static final Logger LOG = Logger.getLogger(KeyHubCredentialsProvider.class.getName());

    @Override
    public <C extends Credentials> List<C> getCredentials(Class<C> type, ItemGroup itemGroup,
            @Nullable Authentication authentication) {

        if (ACL.SYSTEM.equals(authentication)) {
            List<C> result = new ArrayList<>();
            Set<String> ids = new HashSet<>();
            if (itemGroup instanceof Folder) {
                final AbstractFolder<?> folder = AbstractFolder.class.cast(itemGroup);
                FolderKeyHubClientConfiguration property = Optional
                        .ofNullable(folder.getProperties().get(FolderKeyHubClientConfiguration.class))
                        .orElse(new FolderKeyHubClientConfiguration());
                if (property.getConfiguration() == null) {
                    return Collections.emptyList();
                }
                ClientCredentials folderClientCredentials = property.getConfiguration().getClientCredentials();
                if (folderClientCredentials.getClientId().isEmpty()) {
                    return Collections.emptyList();
                }
                Collection<KeyHubUsernamePasswordCredentials> khUsernamePasswordCredentials = fetchCredentials(
                        folderClientCredentials);
                for (Credentials credentials : khUsernamePasswordCredentials) {
                    if (!(credentials instanceof IdCredentials) || ids.add(((IdCredentials) credentials).getId())) {
                        result.add(type.cast(credentials));
                    }
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    private Collection<KeyHubUsernamePasswordCredentials> fetchCredentials(ClientCredentials clientCredentials) {
        GlobalPluginConfiguration keyhubGlobalConfig = GlobalPluginConfiguration.getInstance();
        if (keyhubGlobalConfig.getKeyhubURI().isEmpty()) {
            return Collections.emptyList();
        }

        VaultAccessor va = new VaultAccessor(clientCredentials, keyhubGlobalConfig.getKeyhubURI());
        List<KeyHubGroup> khGroups = new ArrayList<>();
        List<KeyHubRecord> khRecords = new ArrayList<>();

        List<KeyHubUsernamePasswordCredentials> jRecords = new ArrayList<>();
        try {
            va.connect();
            khGroups = va.fetchGroupData();
            khRecords = va.fetchRecordsFromVault(khGroups);
            for (int j = 0; j < khGroups.size(); j++) {
                for (int i = 0; i < khRecords.size(); i++) {
                    jRecords.add(KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance()
                            .id(khRecords.get(i).getUUID()).recordName(khRecords.get(i).getName())
                            .href(khRecords.get(i).getHref()).username(khRecords.get(i).getUsername())
                            .password(new SecretSupplier(va, khRecords.get(i).getHref())).build());
                }
            }
            return jRecords;

        } catch (IOException e) {
            LOG.log(Level.WARNING, "IO could not fetch credentials out of the KeyHub vault: message=[{0}]",
                    e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public String getDisplayName() {
        return "KeyHub Credentials Provider"; // TODO Use Properties file
    }

    @Override
    public CredentialsStore getStore(ModelObject object) {
        if (!(object instanceof ItemGroup)) {
            return null;
        }
        ItemGroup owner = (ItemGroup) object;

        return new KeyHubCredentialsStore(this, owner);

    }

    @Override
    public String getIconClassName() {
        return "icon-keyhub-credentials-vault";
    }
}