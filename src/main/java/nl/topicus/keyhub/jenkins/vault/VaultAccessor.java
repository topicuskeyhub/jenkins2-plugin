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

import static com.topicus.keyhub.sdk.vaultrecord.GetAdditionalQueryParameterType.Secret;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.microsoft.kiota.http.OkHttpRequestAdapter;

import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.sdk.KeyHubAuthenticationProvider;
import com.topicus.keyhub.sdk.KeyHubClient;
import com.topicus.keyhub.sdk.models.group.Group;
import com.topicus.keyhub.sdk.models.vault.VaultRecord;
import com.topicus.keyhub.sdk.models.vault.VaultRecordLinkableWrapper;
import com.topicus.keyhub.sdk.vaultrecord.GetAdditionalQueryParameterType;
import okhttp3.OkHttpClient;

public class VaultAccessor implements IVaultAccessor {
	private static final Logger LOG = Logger.getLogger(VaultAccessor.class.getName());

	private final Instant createdAt;
	private final KeyHubClient client;
	private final String clientId;

	private List<VaultRecord> cachedRecords = null;

	public static IVaultAccessor create(OkHttpClient httpClient, Optional<String> keyHubURI,
			ClientCredentials clientCredentials) {
		if (httpClient == null || keyHubURI.isEmpty() || clientCredentials == null
				|| clientCredentials.getClientId() == null || clientCredentials.getClientSecret() == null) {
			return new EmptyVaultAccessor();
		}
		return new VaultAccessor(httpClient, keyHubURI.get(), clientCredentials);
	}

	private VaultAccessor(OkHttpClient httpClient, String keyHubURI, ClientCredentials clientCredentials) {
		this.createdAt = Instant.now();
		this.clientId = clientCredentials.getClientId();
		OkHttpRequestAdapter requestAdapter = new OkHttpRequestAdapter(new KeyHubAuthenticationProvider(keyHubURI,
				clientCredentials.getClientId(), clientCredentials.getClientSecret().getPlainText()), null, null,
				httpClient);
		requestAdapter.setBaseUrl(keyHubURI + "/keyhub/rest/v1");
		this.client = new KeyHubClient(requestAdapter);
	}

	public boolean isExpired() {
		return createdAt.isBefore(Instant.now().minus(5, ChronoUnit.MINUTES));
	}

	@Override
	public List<VaultRecord> fetchRecordsFromVault() {
		if (cachedRecords == null)
			cachedRecords = fetchRecordsFromVault(fetchGroupData());
		return cachedRecords;
	}

	private List<Group> fetchGroupData() {
		LOG.info("Fetching groups for client " + clientId);
		return client.group().get().getItems();
	}

	private List<VaultRecord> fetchRecordsFromVault(List<Group> groups) {
		List<VaultRecord> ret = new ArrayList<>();
		for (Group group : groups) {
			LOG.info("Fetching records in vault '" + group.getName() + "' for client " + clientId);
			VaultRecordLinkableWrapper curRecords = client.group().byGroupid(group.getLinks().get(0).getId()).vault()
					.record().get(c -> {
						c.queryParameters.sort = new String[] { "asc-name" };
					});
			ret.addAll(curRecords.getItems());
		}
		return ret;
	}

	public VaultRecord fetchRecordSecret(String uuid) {
		return client.vaultrecord().get(c -> {
			c.queryParameters.additional = new GetAdditionalQueryParameterType[] { Secret };
			c.queryParameters.uuid = new String[] { uuid };
		}).getItems().stream().findFirst().orElse(null);
	}
}
