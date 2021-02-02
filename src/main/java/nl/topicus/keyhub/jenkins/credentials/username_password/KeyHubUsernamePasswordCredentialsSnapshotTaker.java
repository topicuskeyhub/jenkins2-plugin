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

import com.cloudbees.plugins.credentials.CredentialsSnapshotTaker;

import hudson.Extension;
import nl.topicus.keyhub.jenkins.credentials.Snapshot;

@Extension
public class KeyHubUsernamePasswordCredentialsSnapshotTaker
        extends CredentialsSnapshotTaker<KeyHubUsernamePasswordCredentials> {

    @Override
    public Class<KeyHubUsernamePasswordCredentials> type() {
        return KeyHubUsernamePasswordCredentials.class;
    }

    @Override
    public KeyHubUsernamePasswordCredentials snapshot(KeyHubUsernamePasswordCredentials credential) {
        return KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance().id(credential.getId())
                .recordName(credential.getRecordName()).href(credential.getHref()).username(credential.getUsername())
                .password(new Snapshot<>(credential.getPassword())).build();
    }
}
