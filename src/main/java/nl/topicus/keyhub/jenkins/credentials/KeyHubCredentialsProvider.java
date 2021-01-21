package nl.topicus.keyhub.jenkins.credentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;

import org.acegisecurity.Authentication;

import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import hudson.security.ACL;
import nl.topicus.keyhub.jenkins.configuration.FolderKeyHubClientConfiguration;
import nl.topicus.keyhub.jenkins.configuration.GlobalPluginConfiguration;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.model.response.group.KeyHubGroup;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;
import nl.topicus.keyhub.jenkins.vault.RestClientBuilder;
import nl.topicus.keyhub.jenkins.vault.VaultAccessor;

@Extension
public class KeyHubCredentialsProvider extends CredentialsProvider {

    private static final Logger LOG = Logger.getLogger(KeyHubCredentialsProvider.class.getName());
    private RestClientBuilder restClientBuilder = new RestClientBuilder();
    private VaultAccessor vaultAccessor;

    @SuppressWarnings("rawtypes")
    @Override
    public <C extends Credentials> List<C> getCredentials(Class<C> type, ItemGroup itemGroup,
            @Nullable Authentication authentication) {
        if (ACL.SYSTEM.equals(authentication)) {
            List<C> result = new ArrayList<>();
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
                for (KeyHubUsernamePasswordCredentials credentials : khUsernamePasswordCredentials) {
                    result.add(type.cast(credentials));
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    private Collection<KeyHubUsernamePasswordCredentials> fetchCredentials(ClientCredentials clientCredentials) {
        GlobalPluginConfiguration keyhubGlobalConfig = GlobalPluginConfiguration.getInstance();
        if (keyhubGlobalConfig.getKeyhubURI().isEmpty() || keyhubGlobalConfig.getKeyhubURI() == null) {
            return Collections.emptyList();
        }
        System.out.println("Provider VaultAccessor: " + this.vaultAccessor);
        if (this.vaultAccessor == null) {
            this.vaultAccessor = new VaultAccessor(clientCredentials, keyhubGlobalConfig.getKeyhubURI(),
                    restClientBuilder);
        }
        List<KeyHubGroup> khGroups = new ArrayList<>();
        List<KeyHubVaultRecord> khRecords = new ArrayList<>();

        List<KeyHubUsernamePasswordCredentials> jRecords = new ArrayList<>();
        try {
            khGroups = vaultAccessor.fetchGroupData();
            khRecords = vaultAccessor.fetchRecordsFromVault(khGroups);
            for (int j = 0; j < khGroups.size(); j++) {
                for (int i = 0; i < khRecords.size(); i++) {
                    jRecords.add(KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance()
                            .id(khRecords.get(i).getUUID()).recordName(khRecords.get(i).getName())
                            .href(khRecords.get(i).getHref()).username(khRecords.get(i).getUsername())
                            .password(new SecretSupplier(vaultAccessor, khRecords.get(i).getHref())).build());
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
        ItemGroup<?> owner = (ItemGroup<?>) object;

        return new KeyHubCredentialsStore(this, owner);

    }

    @Override
    public String getIconClassName() {
        return "icon-keyhub-credentials-vault";
    }
}