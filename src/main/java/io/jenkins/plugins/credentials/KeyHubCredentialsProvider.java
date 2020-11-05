package io.jenkins.plugins.credentials;

import java.util.List;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;

import org.acegisecurity.Authentication;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.ItemGroup;

@Extension
public class KeyHubCredentialsProvider extends CredentialsProvider {

    // TODO after authentication and integration
    @NonNull
    @Override
    public <C extends Credentials> List<C> getCredentials(Class<C> type, ItemGroup itemGroup,
            Authentication authentication) {
        return getCredentials(type, itemGroup, authentication, null);
    }

    @Override
    public String getDisplayName() {
        return "KeyHub Credentials Provider"; // TODO Use Properties file
    }
}