package io.jenkins.plugins.credentials;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.validation.metadata.Scope;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.common.IdCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.google.common.base.Suppliers;

import org.acegisecurity.Authentication;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import io.jenkins.plugins.configuration.GlobalPluginConfiguration;
import io.jenkins.plugins.model.response.KeyHubTokenResponse;
import jenkins.model.Jenkins;

@Extension
public class KeyHubCredentialsProvider extends CredentialsProvider {

    private final KeyHubCredentialsStore store = new KeyHubCredentialsStore(this);

    private Supplier<Collection<StandardUsernamePasswordCredentials>> credentialsSupplier = memoizeWithExpiration(
            KeyHubCredentialsProvider::fetchCredentials, Duration.ofMinutes(5));

    public void refreshCredentials() {
        credentialsSupplier = memoizeWithExpiration(KeyHubCredentialsProvider::fetchCredentials, Duration.ofMinutes(5));
    }

    private static <T> Supplier<T> memoizeWithExpiration(Supplier<T> base, Duration duration) {
        return Suppliers.memoizeWithExpiration(base::get, duration.toMillis(), TimeUnit.MILLISECONDS)::get;
    }

    private static Collection<StandardUsernamePasswordCredentials> fetchCredentials() {
        GlobalPluginConfiguration keyhubGlobalConfig = GlobalPluginConfiguration.all()
                .get(GlobalPluginConfiguration.class);
        if (keyhubGlobalConfig == null) {
            throw new NullPointerException("No global config was entered."); // Make a custom runtime exception
        }

        String clientId = "";
        KeyHubTokenResponse keyhubClientCredentials;
        List<StandardUsernamePasswordCredentials> credentials = new ArrayList();

        StandardUsernamePasswordCredentials cred1 = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
                "cred1", "Description of cred1", "Username of cred1", "password of cred1");

        credentials.add(cred1);
        return credentials;
        // return Collections.emptyList();
    }

    @NonNull
    @Override
    public <C extends Credentials> List<C> getCredentials(Class<C> type, ItemGroup itemGroup,
            @Nullable Authentication authentication) {
        final List<C> list = new ArrayList<>();

        try {
            for(StandardUsernamePasswordCredentials credential : credentialsSupplier.get()) {
                if(type.isAssignableFrom(credential.getClass())) {
                    list.add(type.cast(credential));
                }
                //log if it doesn't match
            }
            return list;
        } catch (RuntimeException e) {
        }
        // return getCredentials(type, itemGroup, authentication, null);
        return Collections.emptyList();
    }

    @Override
    public String getDisplayName() {
        return "KeyHub Credentials Provider"; // TODO Use Properties file
    }

    @Override
    public CredentialsStore getStore(ModelObject object) {
        return object == Jenkins.get() ? store : null;
    }

    @Override
    public String getIconClassName() {
        return "icon-keyhub-credentials-vault";
    }
}