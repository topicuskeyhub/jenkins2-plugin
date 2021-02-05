/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.topicus.keyhub.jenkins.credentials;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import nl.topicus.keyhub.jenkins.Messages;
import nl.topicus.keyhub.jenkins.configuration.FolderKeyHubClientConfiguration;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.vault.KeyHubCommunicationService;

@Extension
public class KeyHubCredentialsProvider extends CredentialsProvider {

    private KeyHubCommunicationService communicationService = new KeyHubCommunicationService();

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
                Collection<KeyHubUsernamePasswordCredentials> khUsernamePasswordCredentials = communicationService
                        .fetchCredentials(folderClientCredentials);
                for (KeyHubUsernamePasswordCredentials credentials : khUsernamePasswordCredentials) {
                    result.add(type.cast(credentials));
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public String getDisplayName() {
        return Messages.keyhubCredentialsProvider();
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