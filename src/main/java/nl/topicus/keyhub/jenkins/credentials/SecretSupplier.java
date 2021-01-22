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

package nl.topicus.keyhub.jenkins.credentials;

import java.io.UnsupportedEncodingException;
import java.util.function.Supplier;
import java.util.logging.Logger;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.model.response.record.RecordSecret;
import nl.topicus.keyhub.jenkins.vault.IVaultAccessor;

public class SecretSupplier implements Supplier<Secret> {

    private transient IVaultAccessor va;
    private String href;

    public SecretSupplier(IVaultAccessor va, String href) {
        this.va = va;
        this.href = href;
    }

    @Override
    public Secret get() {
        RecordSecret secret = va.fetchRecordSecret(this.href);
        return secret.getPassword();
    }

}
