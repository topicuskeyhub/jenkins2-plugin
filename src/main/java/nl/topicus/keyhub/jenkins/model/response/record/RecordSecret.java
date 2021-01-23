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

package nl.topicus.keyhub.jenkins.model.response.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import hudson.util.Secret;

public class RecordSecret {
    private String type;
    private Secret password;
    private String writeTotp;
    private String file;
    private String comment;

    @JsonProperty("$type")
    public String getType() {
        return type;
    }

    @JsonProperty("$type")
    public void setType(String value) {
        this.type = value;
    }

    @JsonProperty("password")
    public Secret getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(Secret value) {
        this.password = value;
    }

    @JsonProperty("writeTotp")
    public String getWriteTotp() {
        return writeTotp;
    }

    @JsonProperty("writeTotp")
    public void setWriteTotp(String value) {
        this.writeTotp = value;
    }

    @JsonProperty("file")
    public String getFile() {
        return file;
    }

    @JsonProperty("file")
    public void setFile(String value) {
        this.file = value;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(String value) {
        this.comment = value;
    }
}
