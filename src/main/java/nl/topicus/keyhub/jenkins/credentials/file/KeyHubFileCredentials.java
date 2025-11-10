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

package nl.topicus.keyhub.jenkins.credentials.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.function.Supplier;

import org.jenkinsci.plugins.plaincredentials.FileCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.Messages;
import nl.topicus.keyhub.jenkins.credentials.AbstractKeyHubCredentials;

public class KeyHubFileCredentials extends AbstractKeyHubCredentials implements FileCredentials {

	private static final long serialVersionUID = 1L;
	private final String filename;
	private Supplier<Secret> file;

	public KeyHubFileCredentials(Builder builder) {
		super(builder.id, builder.recordName);
		this.filename = builder.filename;
		this.file = builder.file;
	}

	@Override
	public String getFileName() {
		return filename;
	}

	@NonNull
	@Override
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
			return Messages.keyhubFile();
		}
	}

	public static class Builder {

		private String id;
		private String recordName;
		private String filename;
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

		public Builder recordName(String recordName) {
			this.recordName = recordName;
			return this;
		}

		public Builder filename(String filename) {
			this.filename = filename;
			return this;
		}

		public Builder file(Supplier<Secret> file) {
			this.file = file;
			return this;
		}

		public KeyHubFileCredentials build() {
			return new KeyHubFileCredentials(this);
		}

	}
}
