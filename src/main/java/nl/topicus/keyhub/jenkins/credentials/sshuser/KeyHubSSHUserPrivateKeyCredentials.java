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

package nl.topicus.keyhub.jenkins.credentials.sshuser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.cloudbees.jenkins.plugins.sshcredentials.SSHUserPrivateKey;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.Messages;
import nl.topicus.keyhub.jenkins.credentials.AbstractKeyHubCredentials;

public class KeyHubSSHUserPrivateKeyCredentials extends AbstractKeyHubCredentials implements SSHUserPrivateKey {

	private static final long serialVersionUID = 1L;
	private final String username;
	private Supplier<Secret> password;
	private Supplier<Secret> file;

	public KeyHubSSHUserPrivateKeyCredentials(Builder builder) {
		super(builder.id, builder.recordName, builder.href);
		this.username = builder.username;
		this.password = builder.password;
		this.file = builder.file;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPrivateKey() {
		return getPrivateKeys().get(0);
	}

	@Override
	public List<String> getPrivateKeys() {
		return Collections.singletonList(
				new String(Base64.getDecoder().decode(file.get().getPlainText()), StandardCharsets.UTF_8));
	}

	@Override
	public Secret getPassphrase() {
		return password.get();
	}

	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(Base64.getDecoder().decode(file.get().getPlainText()));
	}

	@NonNull
	public Secret getFile() {
		return file.get();
	}

	@Extension
	public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
		@Override
		@NonNull
		public String getDisplayName() {
			return Messages.keyhubSSHUserPrivateKey();
		}

		@Override
		public String getIconClassName() {
			return "icon-fingerprint";
		}
	}

	public static class Builder {

		private String id;
		private String recordName;
		private String href;
		private String username;
		private Supplier<Secret> password;
		private Supplier<Secret> file;

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

		public Builder file(Supplier<Secret> file) {
			this.file = file;
			return this;
		}

		public KeyHubSSHUserPrivateKeyCredentials build() {
			return new KeyHubSSHUserPrivateKeyCredentials(this);
		}

	}
}
