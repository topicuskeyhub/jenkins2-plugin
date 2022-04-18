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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import com.cloudbees.plugins.credentials.Credentials;
import com.google.common.base.Strings;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.configuration.GlobalPluginConfiguration;
import nl.topicus.keyhub.jenkins.credentials.SecretFileSupplier;
import nl.topicus.keyhub.jenkins.credentials.SecretPasswordSupplier;
import nl.topicus.keyhub.jenkins.credentials.file.KeyHubFileCredentials;
import nl.topicus.keyhub.jenkins.credentials.string.KeyHubStringCredentials;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.model.response.KeyHubTokenResponse;
import nl.topicus.keyhub.jenkins.model.response.group.KeyHubGroup;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;

@Extension
public class KeyHubCommunicationService implements IKeyHubCommunicationService {

	private static final Logger LOG = Logger.getLogger(KeyHubCommunicationService.class.getName());
	private RestClientBuilder restClientBuilder = new RestClientBuilder();
	private Map<String, KeyHubTokenResponse> currentClientIdWithTokens = new ConcurrentHashMap<>();

	private KeyHubTokenResponse fetchAuthenticationTokenIfNeeded(ClientCredentials clientCredentials,
			KeyHubTokenResponse currentToken) {
		if (currentToken != null && !currentToken.isExpired()) {
			return currentToken;
		}
		String keyhubURI = getKeyHubURI().get();
		KeyHubTokenResponse keyhubToken;
		if (clientCredentials.getClientSecret() == null) {
			throw new IllegalStateException("Cannot refresh access token, no secret stored/given.");
		}

		UriBuilder authenticateUri = UriBuilder.fromUri(keyhubURI).path("/login/oauth2/token").queryParam("authVault",
				"access");
		Form connectionRequest = new Form().param("grant_type", "client_credentials");
		ResteasyWebTarget target = restClientBuilder.getClient().target(authenticateUri);
		target.register(new BasicAuthentication(clientCredentials.getClientId(),
				Secret.toString(clientCredentials.getClientSecret())));
		target.request().accept(MediaType.APPLICATION_JSON_TYPE);
		keyhubToken = target.request().post(Entity.form(connectionRequest), KeyHubTokenResponse.class);
		keyhubToken.setTokenReceivedAt(Instant.now());

		return keyhubToken;
	}

	protected KeyHubTokenResponse getTokenForClient(ClientCredentials credentials) {
		return currentClientIdWithTokens.compute(credentials.getClientId(),
				(clientId, token) -> fetchAuthenticationTokenIfNeeded(credentials, token));
	}

	@SuppressWarnings("unchecked")
	public <C extends Credentials> List<C> fetchCredentials(Class<C> type, ClientCredentials clientCredentials) {
		Optional<String> keyhubURI = getKeyHubURI();
		if (!keyhubURI.isPresent()) {
			LOG.log(Level.WARNING, "The KeyHub URI is not present which will present you with no output.");
			return Collections.emptyList();
		}
		IVaultAccessor vaultAccessor = createVaultAccessor(clientCredentials);
		List<C> jRecords = new ArrayList<>();
		try {
			List<KeyHubGroup> khGroups = vaultAccessor.fetchGroupData();
			List<KeyHubVaultRecord> khRecords = vaultAccessor.fetchRecordsFromVault(khGroups);
			for (KeyHubVaultRecord curRecord : khRecords) {
				if (type.isAssignableFrom(KeyHubUsernamePasswordCredentials.class)) {
					jRecords.add((C) KeyHubUsernamePasswordCredentials.Builder.newInstance().id(curRecord.getUUID())
							.recordName(curRecord.getName()).href(curRecord.getHref()).username(curRecord.getUsername())
							.password(new SecretPasswordSupplier(this, clientCredentials, curRecord.getHref()))
							.build());
				} else if (type.isAssignableFrom(KeyHubStringCredentials.class)) {
					jRecords.add((C) KeyHubStringCredentials.Builder.newInstance().id(curRecord.getUUID())
							.recordName(curRecord.getName()).href(curRecord.getHref())
							.secret(new SecretPasswordSupplier(this, clientCredentials, curRecord.getHref())).build());
				} else if (type.isAssignableFrom(KeyHubFileCredentials.class)) {
					jRecords.add((C) KeyHubFileCredentials.Builder.newInstance().id(curRecord.getUUID())
							.recordName(curRecord.getName()).href(curRecord.getHref()).filename(curRecord.getFilename())
							.file(new SecretFileSupplier(this, clientCredentials, curRecord.getHref())).build());
				}
			}
			return jRecords;

		} catch (IOException e) {
			LOG.log(Level.WARNING, "IO could not fetch credentials out of the KeyHub vault: message=[{0}]",
					e.getMessage());
		}
		return Collections.emptyList();
	}

	protected IVaultAccessor createVaultAccessor(ClientCredentials clientCredentials) {
		return new VaultAccessor(clientCredentials, this.getKeyHubURI().orElse(""), restClientBuilder,
				getTokenForClient(clientCredentials));
	}

	public KeyHubVaultRecord fetchRecordSecret(ClientCredentials clientCredentials, String href) {
		IVaultAccessor vaultAccessor = createVaultAccessor(clientCredentials);
		return vaultAccessor.fetchRecordSecret(href);
	}

	protected Optional<String> getKeyHubURI() {
		return Optional.ofNullable(
				Strings.emptyToNull(ExtensionList.lookupSingleton(GlobalPluginConfiguration.class).getKeyhubURI()));
	}
}
