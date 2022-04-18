package nl.topicus.keyhub.jenkins;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.credentials.SecretPasswordSupplier;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.model.response.record.AdditionalObjectsOfVaultRecord;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;
import nl.topicus.keyhub.jenkins.model.response.record.RecordSecret;
import nl.topicus.keyhub.jenkins.vault.IKeyHubCommunicationService;
import nl.topicus.keyhub.jenkins.vault.KeyHubCommunicationService;

public class SecretSupplierTest {

    @Test
    public void secretSupplierGet() {
        // Arrange
        IKeyHubCommunicationService mockedService = mock(KeyHubCommunicationService.class);
        KeyHubVaultRecord testRecord = new KeyHubVaultRecord();
        AdditionalObjectsOfVaultRecord additionalObjects = new AdditionalObjectsOfVaultRecord();
        RecordSecret testRecordSecret = new RecordSecret();
        testRecord.setAdditionalObjects(additionalObjects);
        testRecordSecret.setPassword(Secret.fromString("testSecret"));
        additionalObjects.setSecret(testRecordSecret);

        testRecord.setAdditionalObjects(additionalObjects);

        ClientCredentials testClientCredentials = new ClientCredentials("testId", Secret.fromString("testSecret"));


        SecretPasswordSupplier secretSupplier = new SecretPasswordSupplier(mockedService, testClientCredentials, "testHref");

        when(mockedService.fetchRecordSecret(any(), anyString())).thenReturn(testRecord);

        // Act
        Secret secret = secretSupplier.get();   

        // Assert
        assertEquals(secret, testRecord.getAdditionalObjects().getSecret().getPassword());
    }

}
