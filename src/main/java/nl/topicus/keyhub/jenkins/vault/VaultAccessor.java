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

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.google.common.base.Strings;

import org.apache.http.HttpHeaders;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.model.response.KeyHubTokenResponse;
import nl.topicus.keyhub.jenkins.model.response.group.KeyHubGroup;
import nl.topicus.keyhub.jenkins.model.response.group.ListOfKeyHubGroups;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;
import nl.topicus.keyhub.jenkins.model.response.record.ListOfKeyHubVaultRecords;

public class VaultAccessor implements IVaultAccessor {
	private static final Logger LOG = Logger.getLogger(VaultAccessor.class.getName());

	private Instant createdAt;
	private ClientCredentials clientCredentials;
	private String keyhubUri;
	private RestClientBuilder restClientBuilder;
	private KeyHubTokenResponse keyhubToken;
	private static final MediaType RESPONSE_ACCEPT = MediaType
			.valueOf("application/vnd.topicus.keyhub+json;version=77");

	private List<KeyHubVaultRecord> cachedRecords = null;

	public VaultAccessor(ClientCredentials clientCredentials, String keyhubUri, RestClientBuilder restClientBuilder,
			KeyHubTokenResponse keyhubToken) {
		if (Strings.isNullOrEmpty(keyhubUri)) {
			throw new IllegalArgumentException("KeyHub URI cannot be null or empty.");
		}
		this.createdAt = Instant.now();
		this.clientCredentials = clientCredentials;
		this.keyhubUri = keyhubUri;
		this.restClientBuilder = restClientBuilder;
		this.keyhubToken = keyhubToken;
	}

	public boolean isExpired() {
		return createdAt.isBefore(Instant.now().minus(5, ChronoUnit.MINUTES)) || keyhubToken.isExpired();
	}

	public KeyHubTokenResponse getKeyhubToken() {
		return this.keyhubToken;
	}

	public ClientCredentials getClientCredentials() {
		return this.clientCredentials;
	}

	@Override
	public List<KeyHubVaultRecord> fetchRecordsFromVault() throws IOException {
		if (cachedRecords == null)
			cachedRecords = fetchRecordsFromVault(fetchGroupData());
		return cachedRecords;
	}

	private List<KeyHubGroup> fetchGroupData() throws IOException {
		LOG.info("Fetching groups for client " + clientCredentials.getClientId());
		UriBuilder groupDataUri = UriBuilder.fromUri(keyhubUri).path("/keyhub/rest/v1/group");
		ResteasyWebTarget target = restClientBuilder.getClient().target(groupDataUri);
		String authHeader = "Bearer " + keyhubToken.getToken();
		try (Response response = target.request().header(HttpHeaders.AUTHORIZATION, authHeader).accept(RESPONSE_ACCEPT)
				.get()) {
			return response.readEntity(ListOfKeyHubGroups.class).getGroups();
		}
	}

	private List<KeyHubVaultRecord> fetchRecordsFromVault(List<KeyHubGroup> groups) throws IOException {
		List<KeyHubVaultRecord> ret = new ArrayList<>();
		for (KeyHubGroup group : groups) {
			LOG.info("Fetching records in vault '" + group.getName() + "' for client "
					+ clientCredentials.getClientId());
			UriBuilder recordsUri = UriBuilder.fromUri(group.getHref()).path("vault/record").queryParam("sort",
					"asc-name");
			ResteasyWebTarget target = restClientBuilder.getClient().target(recordsUri);
			String authHeader = "Bearer " + keyhubToken.getToken();
			try (Response response = target.request().header(HttpHeaders.AUTHORIZATION, authHeader)
					.accept(RESPONSE_ACCEPT).get()) {
				ret.addAll(response.readEntity(ListOfKeyHubVaultRecords.class).getRecords());
			}
		}
		return ret;
	}

	public KeyHubVaultRecord fetchRecordSecret(String href) {
		UriBuilder recordSecretUri = UriBuilder.fromUri(href).queryParam("additional", "secret");
		ResteasyWebTarget target = restClientBuilder.getClient().target(recordSecretUri);
		String authHeader = "Bearer " + keyhubToken.getToken();
		try (Response response = target.request().header(HttpHeaders.AUTHORIZATION, authHeader).accept(RESPONSE_ACCEPT)
				.header("topicus-Vault-session", keyhubToken.getVaultSession()).get()) {
			return response.readEntity(KeyHubVaultRecord.class);
		}
	}
}
