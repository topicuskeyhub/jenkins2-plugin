package io.jenkins.plugins.credentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.common.IdCredentials;

import org.acegisecurity.Authentication;

import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.Item;
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

    @Override
    public <C extends Credentials> List<C> getCredentials(Class<C> type, ItemGroup itemGroup,
            @Nullable Authentication authentication) {
        List<C> result = new ArrayList<C>();
        Set<String> ids = new HashSet<String>();

        if (ACL.SYSTEM.equals(authentication)) {
            System.out.println("ItemGroup: " + itemGroup.getAllItems().toString());
            while (itemGroup != null) {
                if (itemGroup instanceof Folder) {
                    final AbstractFolder<?> folder = AbstractFolder.class.cast(itemGroup);
                    FolderKeyHubClientConfiguration property = folder.getProperties()
                            .get(FolderKeyHubClientConfiguration.class);
                    ClientCredentials folderClientCredentials = property.getConfiguration().getClientCredentials();
                    if (folderClientCredentials.getClientId().isEmpty()) {
                        return Collections.emptyList();
                    }
                    Collection<KeyHubUsernamePasswordCredentials> khUsernamePasswordCredentials = fetchCredentials(
                            folderClientCredentials);
                    if (khUsernamePasswordCredentials.isEmpty()) {
                        return Collections.emptyList();
                    }
                    for (Credentials credentials : khUsernamePasswordCredentials) {
                        if (!(credentials instanceof IdCredentials) || ids.add(((IdCredentials) credentials).getId())) {
                            result.add(type.cast(credentials));
                        }
                    }
                }
                if (itemGroup instanceof Item) {
                    itemGroup = Item.class.cast(itemGroup).getParent();
                } else {
                    break;
                }
            }
        }
        return result;
    }

    private Collection<KeyHubUsernamePasswordCredentials> fetchCredentials(ClientCredentials clientCredentials) {
        GlobalPluginConfiguration keyhubGlobalConfig = GlobalPluginConfiguration.all()
                .get(GlobalPluginConfiguration.class);
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
            for (KeyHubGroup group : khGroups) {
                for (int i = 0; i < khRecords.size(); i++) {
                    jRecords.add(KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance()
                            .id(khRecords.get(i).getUUID()).recordName(khRecords.get(i).getName())
                            .href(khRecords.get(i).getHref()).username(khRecords.get(i).getUsername())
                            .password(new SecretSupplier(va, khRecords.get(i).getHref())).build());
                }
            }
            return jRecords;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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