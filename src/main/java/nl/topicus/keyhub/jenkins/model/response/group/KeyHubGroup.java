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

package nl.topicus.keyhub.jenkins.model.response.group;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.topicus.keyhub.jenkins.model.response.Link;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyHubGroup {
    private List<Link> links;
    private AdditionalObjectsOfGroup additionalObjects;
    private String name;

    @JsonProperty("links")
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Link> value) {
        this.links = value;
    }

    @JsonProperty("additionalObjects")
    public AdditionalObjectsOfGroup getAdditionalObjects() {
        return additionalObjects;
    }

    @JsonProperty("additionalObjects")
    public void setAdditionalObjects(AdditionalObjectsOfGroup value) {
        this.additionalObjects = value;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String value) {
        this.name = value;
    }

    public String getHref() {
        return links.get(0).getHref();
    }

    public int getId() {
        return links.get(0).getID();
    }

}
