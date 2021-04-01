package nl.topicus.keyhub.jenkins.credentials;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.plugins.credentials.Credentials;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.model.ItemGroup;

public class KeyHubCredentialsProviderTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void getCredentialsNoPermissionsTest() {
        // Arrange
        KeyHubCredentialsProvider provider = new KeyHubCredentialsProvider();
        ItemGroup mockedItemGroup = mock(ItemGroup.class);

        // Act
        List<Credentials> credentials = provider.getCredentials(Credentials.class, mockedItemGroup, null);

        // Assert
        assertTrue(credentials.isEmpty());
    }

    @Test
    public void getCredentialsWithPermissionsButEmptyItemGroup() {
        // Arrange
        KeyHubCredentialsProvider provider = new KeyHubCredentialsProvider();
        ItemGroup<AbstractFolder<?>> mockedItemGroup = mock(ItemGroup.class);

        // Act
        List<Credentials> credentials = provider.getCredentials(Credentials.class, mockedItemGroup, jenkinsRule.jenkins.getAuthentication());

        // Assert
        assertTrue(credentials.isEmpty());
    }

}
