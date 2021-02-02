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

package nl.topicus.keyhub.jenkins.model.response;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;

/**
 * Custom field vaultSession added to save the vault session.
 */
public class KeyHubTokenResponse extends AccessTokenResponse {

    @JsonProperty("vaultSession")
    private String vaultSession;
    private Instant tokenReceivedAt;

    public KeyHubTokenResponse() {
        super();
    }

    public String getVaultSession() {
        return this.vaultSession;
    }

    public void setVaultSession(String vaultSession) {
        this.vaultSession = vaultSession;
    }

    public Instant getTokenReceivedAt() {
        return this.tokenReceivedAt;
    }

    public void setTokenReceivedAt(Instant tokenReceivedAt) {
        this.tokenReceivedAt = tokenReceivedAt;
    }

    public boolean isExpired() {
        return tokenReceivedAt.plusSeconds(getExpiresIn()).minus(2, ChronoUnit.MINUTES).isBefore(Instant.now());
    }
}
