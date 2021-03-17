package nl.topicus.keyhub.jenkins.credentials.username_password;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KeyHubCredentialsBuilderTest {

    @Test
    public void builtKeyHubUsernameCredentialsCorrectly() {
        // Arrange
        KeyHubUsernamePasswordCredentials builtCredentials;

        // Act
        builtCredentials = KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance().id("123-testId")
                .recordName("testRecordName").href("testHref").username("testUsername").password(null).build();

        // Assert
        builtCredentials.getHref();
        assertEquals("123-testId", builtCredentials.getId());
        assertEquals("testRecordName", builtCredentials.getDescription());
        assertEquals("testHref", builtCredentials.getHref());
    }

}
