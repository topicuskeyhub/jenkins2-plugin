package io.jenkins.plugins.credentials;

import java.io.IOException;

import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.util.Secret;

@NameWith( 
  value = KeyHubClientCredentials.NameProvider.class,
  priority = 32
)
public interface KeyHubClientCredentials extends StandardCredentials {

  String getGlobalUri();
  String getClientId();
  Secret getClientSecret() throws IOException, InterruptedException;
  
  class NameProvider extends CredentialsNameProvider<KeyHubClientCredentials> {

    @NonNull
    @Override
    public String getName(@NonNull KeyHubClientCredentials c) {
      return c.getDescription();
    }
  }
}
