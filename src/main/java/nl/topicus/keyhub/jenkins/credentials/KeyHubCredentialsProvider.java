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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.acegisecurity.Authentication;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;

import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import hudson.security.ACL;
import nl.topicus.keyhub.jenkins.Messages;
import nl.topicus.keyhub.jenkins.configuration.FolderKeyHubClientConfiguration;
import nl.topicus.keyhub.jenkins.credentials.file.KeyHubFileCredentials;
import nl.topicus.keyhub.jenkins.credentials.sshuser.KeyHubSSHUserPrivateKeyCredentials;
import nl.topicus.keyhub.jenkins.credentials.string.KeyHubStringCredentials;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.vault.IKeyHubCommunicationService;

@Extension
public class KeyHubCredentialsProvider extends CredentialsProvider {
	private static final Set<Class<? extends Credentials>> SUPPORTED_CREDENTIALS;
	static {
		Set<Class<? extends Credentials>> tmp = new HashSet<>();
		tmp.add(KeyHubFileCredentials.class);
		tmp.add(KeyHubStringCredentials.class);
		tmp.add(KeyHubUsernamePasswordCredentials.class);
		tmp.add(KeyHubSSHUserPrivateKeyCredentials.class);
		SUPPORTED_CREDENTIALS = Collections.unmodifiableSet(tmp);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <C extends Credentials> List<C> getCredentials(Class<C> type, ItemGroup itemGroup,
			@Nullable Authentication authentication) {
		if (ACL.SYSTEM.equals(authentication)) {
			List<C> result = new ArrayList<>();
			while (itemGroup != null) {
				if (itemGroup instanceof AbstractFolder) {
					result.addAll(getCredentialsForItemGroup(type, itemGroup));
				}
				if (itemGroup instanceof Item) {
					itemGroup = Item.class.cast(itemGroup).getParent();
				} else {
					break;
				}
			}
			return result;
		}
		return Collections.emptyList();
	}

	public <C extends Credentials> List<C> getCredentialsForItemGroup(Class<C> type, ItemGroup<?> itemGroup) {
		if (itemGroup instanceof AbstractFolder && isCredentialsSupported(type)) {
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
			return getKeyHubCommunicationService().fetchCredentials(type, folderClientCredentials);
		}
		return Collections.emptyList();
	}

	private boolean isCredentialsSupported(Class<? extends Credentials> type) {
		return SUPPORTED_CREDENTIALS.stream().anyMatch(type::isAssignableFrom);
	}

	public IKeyHubCommunicationService getKeyHubCommunicationService() {
		return ExtensionList.lookupSingleton(IKeyHubCommunicationService.class);
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