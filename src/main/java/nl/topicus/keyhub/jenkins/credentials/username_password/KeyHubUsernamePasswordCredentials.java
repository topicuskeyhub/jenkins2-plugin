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

package nl.topicus.keyhub.jenkins.credentials.username_password;

import java.util.function.Supplier;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.Messages;
import nl.topicus.keyhub.jenkins.credentials.AbstractKeyHubCredentials;

public class KeyHubUsernamePasswordCredentials extends AbstractKeyHubCredentials
        implements StandardUsernamePasswordCredentials {

    private static final long serialVersionUID = 1L;
    private final String username;
    private Supplier<Secret> password;

    public KeyHubUsernamePasswordCredentials(Builder builder) {
        super(builder.id, builder.recordName, builder.href);
        this.username = builder.username;
        this.password = builder.password;
    }

    public String getUsername() {
        return this.username;
    }

    @NonNull
    @Override
    public Secret getPassword() {
        return password.get();
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
        @Override
        @NonNull
        public String getDisplayName() {
            return Messages.keyhubUsernamePassword();
        }

        @Override
        public String getIconClassName() {
            return "icon-credentials-userpass";
        }
    }

    public static class Builder {

        private String id;
        private String recordName;
        private String href;
        private String username;
        private Supplier<Secret> password;

        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder href(String href) {
            this.href = href;
            return this;
        }

        public Builder recordName(String recordName) {
            this.recordName = recordName;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(Supplier<Secret> password) {
            this.password = password;
            return this;
        }

        public KeyHubUsernamePasswordCredentials build() {
            return new KeyHubUsernamePasswordCredentials(this);
        }

    }
}
