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

package nl.topicus.keyhub.jenkins.credentials.string;

import java.util.function.Supplier;

import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.Messages;
import nl.topicus.keyhub.jenkins.credentials.AbstractKeyHubCredentials;

public class KeyHubStringCredentials extends AbstractKeyHubCredentials implements StringCredentials {

	private static final long serialVersionUID = 1L;
	private Supplier<Secret> secret;

	public KeyHubStringCredentials(Builder builder) {
		super(builder.id, builder.recordName, builder.href);
		this.secret = builder.secret;
	}

	@NonNull
	@Override
	public Secret getSecret() {
		return secret.get();
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
		private Supplier<Secret> secret;

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

		public Builder secret(Supplier<Secret> secret) {
			this.secret = secret;
			return this;
		}

		public KeyHubStringCredentials build() {
			return new KeyHubStringCredentials(this);
		}

	}
}
