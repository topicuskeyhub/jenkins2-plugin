package nl.topicus.keyhub.jenkins;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.topicus.keyhub.sdk.models.vault.VaultRecord;
import com.topicus.keyhub.sdk.models.vault.VaultRecordAdditionalObjects;
import com.topicus.keyhub.sdk.models.vault.VaultRecordSecrets;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.credentials.SecretPasswordSupplier;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.vault.IKeyHubCommunicationService;
import nl.topicus.keyhub.jenkins.vault.KeyHubCommunicationService;

public class SecretSupplierTest {

    @Test
    public void secretSupplierGet() {
        // Arrange
        IKeyHubCommunicationService mockedService = mock(KeyHubCommunicationService.class);
        VaultRecord testRecord = new VaultRecord();
        VaultRecordAdditionalObjects additionalObjects = new VaultRecordAdditionalObjects();
        VaultRecordSecrets testRecordSecret = new VaultRecordSecrets();
        testRecord.setAdditionalObjects(additionalObjects);
        testRecordSecret.setPassword("testSecret");
        additionalObjects.setSecret(testRecordSecret);

        testRecord.setAdditionalObjects(additionalObjects);

        ClientCredentials testClientCredentials = new ClientCredentials("testId", Secret.fromString("testSecret"));


        SecretPasswordSupplier secretSupplier = new SecretPasswordSupplier(mockedService, testClientCredentials, "testHref");

        when(mockedService.fetchRecordSecret(any(), anyString())).thenReturn(testRecord);

        // Act
        Secret secret = secretSupplier.get();   

        // Assert 
        assertEquals(secret.getPlainText(), testRecord.getAdditionalObjects().getSecret().getPassword());
    }

}
