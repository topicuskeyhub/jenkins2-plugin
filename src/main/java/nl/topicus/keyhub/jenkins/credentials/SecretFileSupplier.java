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

import java.util.function.Supplier;

import com.topicus.keyhub.sdk.models.vault.VaultRecord;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.vault.IKeyHubCommunicationService;

public class SecretFileSupplier implements Supplier<Secret> {

	private transient IKeyHubCommunicationService keyhubCommunicationService;
	private ClientCredentials clientCredentials;
	private String uuid;

	public SecretFileSupplier(IKeyHubCommunicationService keyhubCommunicationsService,
			ClientCredentials clientCredentials, String uuid) {
		this.keyhubCommunicationService = keyhubCommunicationsService;
		this.clientCredentials = clientCredentials;
		this.uuid = uuid;
	}

	@Override
	public Secret get() {
		VaultRecord secret = keyhubCommunicationService.fetchRecordSecret(this.clientCredentials, this.uuid);
		return Secret.fromString(secret.getAdditionalObjects().getSecret().getFile());
	}
}
