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
package nl.topicus.keyhub.jenkins.configuration;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

public class FolderKeyHubClientConfiguration extends AbstractFolderProperty<AbstractFolder<?>> {

    private final ClientConfiguration configuration;

    @DataBoundConstructor
    public FolderKeyHubClientConfiguration(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    public FolderKeyHubClientConfiguration() {
        this.configuration = null;
    }

    public ClientConfiguration getConfiguration() {
        return configuration;
    }

    @Extension
    public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {

    }

}
