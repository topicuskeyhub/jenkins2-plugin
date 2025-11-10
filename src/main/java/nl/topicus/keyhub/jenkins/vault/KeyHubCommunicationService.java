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

package nl.topicus.keyhub.jenkins.vault;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudbees.plugins.credentials.Credentials;
import com.google.common.base.Strings;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.http.KiotaClientFactory;

import hudson.Extension;
import hudson.ExtensionList;
import nl.topicus.keyhub.jenkins.configuration.GlobalPluginConfiguration;
import nl.topicus.keyhub.jenkins.credentials.SecretFileSupplier;
import nl.topicus.keyhub.jenkins.credentials.SecretPasswordSupplier;
import nl.topicus.keyhub.jenkins.credentials.file.KeyHubFileCredentials;
import nl.topicus.keyhub.jenkins.credentials.sshuser.KeyHubSSHUserPrivateKeyCredentials;
import nl.topicus.keyhub.jenkins.credentials.string.KeyHubStringCredentials;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import com.topicus.keyhub.sdk.models.vault.VaultRecord;
import com.topicus.keyhub.sdk.models.vault.VaultSecretType;
import okhttp3.OkHttpClient;

@Extension
public class KeyHubCommunicationService implements IKeyHubCommunicationService {

	private static final Logger LOG = Logger.getLogger(KeyHubCommunicationService.class.getName());
	private OkHttpClient httpClient = KiotaClientFactory.create().connectTimeout(Duration.ofSeconds(30))
			.readTimeout(Duration.ofMinutes(1)).build();
	private Map<String, IVaultAccessor> cachedVaultAccessors = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public <C extends Credentials> List<C> fetchCredentials(Class<C> type, ClientCredentials clientCredentials) {
		Optional<String> keyhubURI = getKeyHubURI();
		if (!keyhubURI.isPresent()) {
			LOG.log(Level.WARNING, "The KeyHub URI is not set, cannot fetch vault records.");
			return Collections.emptyList();
		}
		IVaultAccessor vaultAccessor = createVaultAccessor(clientCredentials);
		List<C> jRecords = new ArrayList<>();
		try {
			List<VaultRecord> khRecords = vaultAccessor.fetchRecordsFromVault();
			for (VaultRecord curRecord : khRecords) {
				Credentials curCredentials = keyHubVaultRecordToCredentials(curRecord, clientCredentials);
				if (type.isInstance(curCredentials)) {
					jRecords.add((C) curCredentials);
				}
			}
			return jRecords;

		} catch (ApiException e) {
			LOG.log(Level.WARNING, "IO error while fetching vault records: message=[{0}]", e.getMessage());
		}
		return Collections.emptyList();
	}

	private Credentials keyHubVaultRecordToCredentials(VaultRecord record, ClientCredentials clientCredentials) {
		if (record.getUsername() == null) {
			if (record.getTypes().contains(VaultSecretType.PASSWORD)) {
				return KeyHubStringCredentials.Builder.newInstance().id(record.getUuid()).recordName(record.getName())
						.secret(new SecretPasswordSupplier(this, clientCredentials, record.getUuid())).build();
			} else if (record.getTypes().contains(VaultSecretType.FILE)) {
				return KeyHubFileCredentials.Builder.newInstance().id(record.getUuid()).recordName(record.getName())
						.filename(record.getFilename())
						.file(new SecretFileSupplier(this, clientCredentials, record.getUuid())).build();
			} else {
				return null;
			}
		} else {
			if (record.getTypes().contains(VaultSecretType.PASSWORD)) {
				if (record.getTypes().contains(VaultSecretType.FILE)) {
					return KeyHubSSHUserPrivateKeyCredentials.Builder.newInstance().id(record.getUuid())
							.recordName(record.getName()).username(record.getUsername())
							.password(new SecretPasswordSupplier(this, clientCredentials, record.getUuid()))
							.file(new SecretFileSupplier(this, clientCredentials, record.getUuid())).build();
				} else {
					return KeyHubUsernamePasswordCredentials.Builder.newInstance().id(record.getUuid())
							.recordName(record.getName()).username(record.getUsername())
							.password(new SecretPasswordSupplier(this, clientCredentials, record.getUuid())).build();
				}
			} else {
				if (record.getTypes().contains(VaultSecretType.FILE)) {
					return KeyHubSSHUserPrivateKeyCredentials.Builder.newInstance().id(record.getUuid())
							.recordName(record.getName()).username(record.getUsername())
							.file(new SecretFileSupplier(this, clientCredentials, record.getUuid())).build();
				} else {
					return null;
				}
			}
		}
	}

	protected IVaultAccessor createVaultAccessor(ClientCredentials clientCredentials) {
		return cachedVaultAccessors.compute(clientCredentials.getClientId(),
				(clientId, cached) -> cached != null && !cached.isExpired() ? cached
						: VaultAccessor.create(httpClient, this.getKeyHubURI(), clientCredentials));
	}

	public VaultRecord fetchRecordSecret(ClientCredentials clientCredentials, String uuid) {
		IVaultAccessor vaultAccessor = createVaultAccessor(clientCredentials);
		return vaultAccessor.fetchRecordSecret(uuid);
	}

	protected Optional<String> getKeyHubURI() {
		return Optional.ofNullable(
				Strings.emptyToNull(ExtensionList.lookupSingleton(GlobalPluginConfiguration.class).getKeyhubURI()));
	}
}
