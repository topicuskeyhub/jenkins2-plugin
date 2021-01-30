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

package nl.topicus.keyhub.jenkins.configuration;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.Messages;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;

public class ClientConfiguration extends AbstractDescribableImpl<ClientConfiguration> implements Serializable {

    private static final long serialVersionUID = 1L;
    private String keyhubURI;
    private String clientId;
    private String clientSecret;

    @DataBoundConstructor
    public ClientConfiguration() {
        this.keyhubURI = GlobalPluginConfiguration.getInstance().getKeyhubURI();
    }

    public String getKeyhubURI() {
        return this.keyhubURI;
    }

    public String getClientId() {
        return this.clientId;
    }

    @DataBoundSetter
    public void setClientId(String vaultId) {
        this.clientId = vaultId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    @DataBoundSetter
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public ClientCredentials getClientCredentials() {
        return new ClientCredentials(getClientId(), Secret.fromString(getClientSecret()));
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ClientConfiguration> {

        @Override
        @NonNull
        public String getDisplayName() {
            return Messages.vaultConfiguration();
        }
    }
}
