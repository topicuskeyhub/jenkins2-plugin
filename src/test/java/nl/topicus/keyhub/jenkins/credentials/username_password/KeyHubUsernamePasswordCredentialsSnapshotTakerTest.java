package nl.topicus.keyhub.jenkins.credentials.username_password;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import org.junit.Test;

import hudson.util.Secret;

public class KeyHubUsernamePasswordCredentialsSnapshotTakerTest {

    @Test
    public void KeyHubUsernameSnapshotTest() {
        // Arrange
        KeyHubUsernamePasswordCredentialsSnapshotTaker snapshotTaker = new KeyHubUsernamePasswordCredentialsSnapshotTaker();
        Secret testSecret = Secret.fromString("testSecret");
        Supplier<Secret> mockedSecretSupplier1 = mock(Supplier.class);

        KeyHubUsernamePasswordCredentials testCredential = KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder
                .newInstance().id("testId").recordName("testName").href("testHref").username("testUsername")
                .password(mockedSecretSupplier1).build();

        when(mockedSecretSupplier1.get()).thenReturn(testSecret);
        
        // Act
        KeyHubUsernamePasswordCredentials usernamePasswordCredentialSnapshot = snapshotTaker.snapshot(testCredential);
        usernamePasswordCredentialSnapshot.getPassword(); // Additional calls to prove that the snapshot is actually a snapshot.
        usernamePasswordCredentialSnapshot.getPassword();

        // Assert
        assertNotSame(testCredential, usernamePasswordCredentialSnapshot);
        assertEquals(testSecret, usernamePasswordCredentialSnapshot.getPassword());
        verify(mockedSecretSupplier1, times(1)).get();
    }

}
