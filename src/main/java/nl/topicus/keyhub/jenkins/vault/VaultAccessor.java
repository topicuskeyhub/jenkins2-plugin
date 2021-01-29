/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
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
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

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

    private ClientCredentials clientCredentials;
    private String keyhubUri;
    private RestClientBuilder restClientBuilder;
    private KeyHubTokenResponse keyhubToken;
    private static final MediaType RESPONSE_ACCEPT = MediaType
            .valueOf("application/vnd.topicus.keyhub+json;version=44");

    public VaultAccessor(ClientCredentials clientCredentials, String keyhubUri, RestClientBuilder restClientBuilder,
            KeyHubTokenResponse keyhubToken) {
        if (Strings.isNullOrEmpty(keyhubUri)) {
            throw new IllegalArgumentException("KeyHub URI cannot be null or empty.");
        }
        this.clientCredentials = clientCredentials;
        this.keyhubUri = keyhubUri;
        this.restClientBuilder = restClientBuilder;
        this.keyhubToken = keyhubToken;
    }

    public KeyHubTokenResponse getKeyhubToken() {
        return this.keyhubToken;
    }

    public ClientCredentials getClientCredentials() {
        return this.clientCredentials;
    }

    public List<KeyHubGroup> fetchGroupData() throws IOException {
        UriBuilder groupDataUri = UriBuilder.fromUri(keyhubUri).path("/keyhub/rest/v1/group");
        ResteasyWebTarget target = restClientBuilder.getClient().target(groupDataUri);
        ListOfKeyHubGroups keyhubGroups;
        String authHeader = "Bearer " + keyhubToken.getToken();
        try (Response response = target.request().header(HttpHeaders.AUTHORIZATION, authHeader).accept(RESPONSE_ACCEPT)
                .get()) {
            keyhubGroups = response.readEntity(ListOfKeyHubGroups.class);
        }
        return keyhubGroups.getGroups();
    }

    public List<KeyHubVaultRecord> fetchRecordsFromVault(List<KeyHubGroup> groups) throws IOException {
        ListOfKeyHubVaultRecords keyhubRecords = new ListOfKeyHubVaultRecords();
        for (KeyHubGroup group : groups) {
            UriBuilder recordsUri = UriBuilder.fromUri(group.getHref()).path("vault/record");
            ResteasyWebTarget target = restClientBuilder.getClient().target(recordsUri);
            String authHeader = "Bearer " + keyhubToken.getToken();
            try (Response response = target.request().header(HttpHeaders.AUTHORIZATION, authHeader)
                    .accept(RESPONSE_ACCEPT).get()) {
                keyhubRecords = response.readEntity(ListOfKeyHubVaultRecords.class);
            }
        }
        return keyhubRecords.getRecords();
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
