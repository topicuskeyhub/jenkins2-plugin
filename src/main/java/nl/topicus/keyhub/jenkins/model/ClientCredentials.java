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

package nl.topicus.keyhub.jenkins.model;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.util.Secret;

public class ClientCredentials {
    private String clientId;
    private Secret clientSecret;

    @DataBoundConstructor
    public ClientCredentials(String clientId, Secret clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public ClientCredentials() {

    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Secret getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(Secret clientSecret) {
        this.clientSecret = clientSecret;
    }

}
