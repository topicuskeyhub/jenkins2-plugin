package nl.topicus.keyhub.jenkins.credentials;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.Credentials;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.model.ItemGroup;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials.Builder;

public class KeyHubCredentialsStoreTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void credentialsStoreGetCredentialsTest() {
        // Arrange
        KeyHubCredentialsProvider mockedProvider = mock(KeyHubCredentialsProvider.class);
        ItemGroup mockedItemGroup = mock(ItemGroup.class);
        KeyHubCredentialsStore store = new KeyHubCredentialsStore(mockedProvider, mockedItemGroup);
        List<Credentials> result = new ArrayList<>();
        KeyHubUsernamePasswordCredentials testCredentials = Builder.newInstance().id("testId")
                .recordName("testRecord").href("testHref").username("testUsername").build();
        Credentials testCredential = (Credentials) testCredentials;
        result.add(testCredential);

        when(mockedProvider.getCredentialsForItemGroup(any(), any(ItemGroup.class))).thenReturn(result);

        // Act
        List<Credentials> credentials = store.getCredentials(Domain.global());

        // Assert
        assertEquals(testCredential, credentials.get(0));
    }

    @Test
    public void credentialsStoreGetCredentialsWrongDomainTest() {
        // Arrange
        KeyHubCredentialsProvider mockedProvider = mock(KeyHubCredentialsProvider.class);
        ItemGroup mockedItemGroup = mock(ItemGroup.class);
        KeyHubCredentialsStore store = new KeyHubCredentialsStore(mockedProvider, mockedItemGroup);
        List<Credentials> result = new ArrayList<>();
        KeyHubUsernamePasswordCredentials testCredentials = Builder.newInstance().id("testId")
                .recordName("testRecord").href("testHref").username("testUsername").build();
        Credentials testCredential = (Credentials) testCredentials;
        result.add(testCredential);
        Domain wrongDomain = new Domain("testDomain", "testDescription", null);

        when(mockedProvider.getCredentialsForItemGroup(any(), any(ItemGroup.class))).thenReturn(result);

        // Act
        List<Credentials> credentials = store.getCredentials(wrongDomain);

        // Assert
        assertTrue(credentials.isEmpty());
    }

}
