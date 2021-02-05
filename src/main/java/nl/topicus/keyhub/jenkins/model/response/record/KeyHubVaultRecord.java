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

package nl.topicus.keyhub.jenkins.model.response.record;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.model.response.Link;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyHubVaultRecord {
    private String type;
    private List<Link> links;
    private AdditionalObjectsOfVaultRecord additionalObjects;
    private String uuid;
    private String name;
    private String username;

    @JsonProperty("$type")
    public String getType() {
        return type;
    }

    @JsonProperty("$type")
    public void setType(String value) {
        this.type = value;
    }

    @JsonProperty("links")
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Link> value) {
        this.links = value;
    }

    @JsonProperty("additionalObjects")
    public AdditionalObjectsOfVaultRecord getAdditionalObjects() {
        return additionalObjects;
    }

    @JsonProperty("additionalObjects")
    public void setAdditionalObjects(AdditionalObjectsOfVaultRecord value) {
        this.additionalObjects = value;
    }

    @JsonProperty("uuid")
    public String getUUID() {
        return this.uuid;
    }

    @JsonProperty("uuid")
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String value) {
        this.name = value;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String value) {
        this.username = value;
    }

    public String getHref() {
        return links.get(0).getHref();
    }

    public void setRecordSecret(Secret secret) {
        additionalObjects.getSecret().setPassword(secret);
    }
}
