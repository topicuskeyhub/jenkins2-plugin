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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.CredentialsStoreAction;
import com.cloudbees.plugins.credentials.domains.Domain;

import org.acegisecurity.Authentication;
import org.jenkins.ui.icon.Icon;
import org.jenkins.ui.icon.IconSet;
import org.jenkins.ui.icon.IconType;
import org.kohsuke.stapler.export.ExportedBean;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import hudson.security.ACL;
import hudson.security.Permission;
import jenkins.model.Jenkins;
import nl.topicus.keyhub.jenkins.Messages;

public class KeyHubCredentialsStore extends CredentialsStore {

    private final KeyHubCredentialsProvider provider;
    private final KeyHubCredentialsStoreAction action = new KeyHubCredentialsStoreAction(this);
    private ItemGroup<?> itemGroup;

    public KeyHubCredentialsStore(KeyHubCredentialsProvider provider, ItemGroup<?> itemGroup) {
        super(KeyHubCredentialsProvider.class);
        this.provider = provider;
        this.itemGroup = itemGroup;
    }

    @NonNull
    @Override
    public ModelObject getContext() {
        return itemGroup;
    }

    @Override
    public boolean hasPermission(Authentication a, Permission permission) {
        return CredentialsProvider.VIEW.equals(permission) && Jenkins.get().getACL().hasPermission(a, permission);
    }

    @Override
    public List<Credentials> getCredentials(Domain domain) {
        if (Domain.global().equals(domain) && Jenkins.get().hasPermission(CredentialsProvider.VIEW)) {
            return provider.getCredentialsForStore(Credentials.class, itemGroup);
        }
        return Collections.emptyList();

    }

    @Override
    public boolean addCredentials(Domain domain, Credentials credentials) throws IOException {
        return false;
    }

    @Override
    public boolean removeCredentials(Domain domain, Credentials credentials) throws IOException {
        return false;
    }

    @Override
    public boolean updateCredentials(Domain domain, Credentials current, Credentials replacement) throws IOException {
        return false;
    }

    @Nullable
    @Override
    public CredentialsStoreAction getStoreAction() {
        return action;
    }

    @ExportedBean
    public static final class KeyHubCredentialsStoreAction extends CredentialsStoreAction {

        private static final String ICON_CLASS = "icon-keyhub-credentials-vault";

        private final KeyHubCredentialsStore store;

        private KeyHubCredentialsStoreAction(KeyHubCredentialsStore store) {
            this.store = store;
            addIcons();
        }

        private void addIcons() {
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-sm", "keyhub-vault-plugin/images/16x16/icon.jpg",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-md", "keyhub-vault-plugin/images/24x24/icon.jpg",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-lg", "keyhub-vault-plugin/images/32x32/icon.jpg",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon(ICON_CLASS + " icon-xlg", "keyhub-vault-plugin/images/48x48/icon.jpg",
                    Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
        }

        @Override
        @NonNull
        public CredentialsStore getStore() {
            return store;
        }

        @Override
        public String getIconFileName() {
            return isVisible() ? "/plugin/keyhub-vault-plugin/images/32x32/alauda.png" : null;
        }

        @Override
        public String getIconClassName() {
            return isVisible() ? ICON_CLASS : null;
        }

        @Override
        public String getDisplayName() {
            return Messages.keyhubCredentialsStore();
        }
    }
}
