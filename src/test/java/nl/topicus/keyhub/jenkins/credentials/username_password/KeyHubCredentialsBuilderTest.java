package nl.topicus.keyhub.jenkins.credentials.username_password;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KeyHubCredentialsBuilderTest {

    @Test
    public void builtKeyHubUsernameCredentialsCorrectly() {
        // Arrange
        KeyHubUsernamePasswordCredentials builtCredentials;

        // Act
        builtCredentials = KeyHubUsernamePasswordCredentials.Builder.newInstance().id("123-testId")
                .recordName("testRecordName").username("testUsername").password(null).build();

        // Assert
        assertEquals("123-testId", builtCredentials.getId());
        assertEquals("testRecordName", builtCredentials.getDescription());
        assertEquals("testUsername", builtCredentials.getUsername());
    }

}
