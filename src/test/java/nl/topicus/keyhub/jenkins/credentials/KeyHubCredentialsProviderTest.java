package nl.topicus.keyhub.jenkins.credentials;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import com.cloudbees.plugins.credentials.Credentials;

import hudson.model.ItemGroup;

@WithJenkins
class KeyHubCredentialsProviderTest {

    private JenkinsRule jenkinsRule;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        jenkinsRule = rule;
    }

    @Test
    void getCredentialsNoPermissionsTest() {
        // Arrange
        KeyHubCredentialsProvider provider = new KeyHubCredentialsProvider();
        ItemGroup<?> mockedItemGroup = mock(ItemGroup.class);

        // Act
        List<Credentials> credentials = provider.getCredentials(Credentials.class, mockedItemGroup, null);

        // Assert
        assertTrue(credentials.isEmpty());
    }

    @Test
    void getCredentialsWithPermissionsButEmptyItemGroup() {
        // Arrange
        KeyHubCredentialsProvider provider = new KeyHubCredentialsProvider();
        ItemGroup<?> mockedItemGroup = mock(ItemGroup.class);

        // Act
        List<Credentials> credentials = provider.getCredentials(Credentials.class, mockedItemGroup, jenkinsRule.jenkins.getAuthentication());

        // Assert
        assertTrue(credentials.isEmpty());
    }

}
