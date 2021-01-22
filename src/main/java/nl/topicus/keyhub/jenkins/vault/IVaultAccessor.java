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
import java.io.UnsupportedEncodingException;
import java.util.List;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.model.response.group.KeyHubGroup;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;
import nl.topicus.keyhub.jenkins.model.response.record.RecordSecret;

public interface IVaultAccessor {

    public List<KeyHubGroup> fetchGroupData() throws IOException;

    public List<KeyHubVaultRecord> fetchRecordsFromVault(List<KeyHubGroup> groups) throws IOException;

    public RecordSecret fetchRecordSecret(String href);
}
