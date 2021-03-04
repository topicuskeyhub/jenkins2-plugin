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

package nl.topicus.keyhub.jenkins.credentials;

import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;

import hudson.util.Secret;

public abstract class AbstractKeyHubCredentials extends BaseStandardCredentials {

    private static final long serialVersionUID = 1L;
    private final String href;
    private final String username;

    protected AbstractKeyHubCredentials(String id, String description, String href, String username) {
        super(id, description);
        this.href = href;
        this.username = username;
    }

    public String getHref() {
        return this.href;
    }

    public String getUsername() {
        return this.username;
    }

    public abstract Secret getPassword();
}
