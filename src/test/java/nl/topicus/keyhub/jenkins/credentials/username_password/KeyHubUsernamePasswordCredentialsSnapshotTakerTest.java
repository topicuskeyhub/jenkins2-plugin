package nl.topicus.keyhub.jenkins.credentials.username_password;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.credentials.SecretSupplier;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.vault.IKeyHubCommunicationService;
import nl.topicus.keyhub.jenkins.vault.KeyHubCommunicationService;

public class KeyHubUsernamePasswordCredentialsSnapshotTakerTest {

    @Test
    public void KeyHubUsernameSnapshotTest() {
        // Arrange
        KeyHubUsernamePasswordCredentialsSnapshotTaker snapshotTaker = new KeyHubUsernamePasswordCredentialsSnapshotTaker();
        IKeyHubCommunicationService mockService = mock(KeyHubCommunicationService.class);
        ClientCredentials testClientCredentials = new ClientCredentials("testId", Secret.fromString("testSecret"));
        KeyHubUsernamePasswordCredentials testCredential = KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder
                .newInstance().id("testId").recordName("testName").href("testHref").username("testUsername")
                .password(new SecretSupplier(mockService, testClientCredentials, "testHref")).build();

        // Act
        // KeyHubUsernamePasswordCredentials usernamePasswordCredential =
        // snapshotTaker.snapshot(testCredential);

        // Assert
        // assertEquals(testCredential, usernamePasswordCredential);

    }

}
