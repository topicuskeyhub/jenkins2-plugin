package io.jenkins.plugins.credentials.username_password;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;

public class KeyHubUsernamePasswordCredentialsTest {

    @Test
    public void getCredentialsTestItemGroupNull() {
        // KeyHubUsernamePasswordCredentials khUsernamePasswordCreds = new
        // KeyHubUsernamePasswordCredentials();

        assertEquals("", "");
    }

}
