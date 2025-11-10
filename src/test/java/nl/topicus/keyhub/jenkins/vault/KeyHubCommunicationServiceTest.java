package nl.topicus.keyhub.jenkins.vault;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;

import com.topicus.keyhub.sdk.models.group.Group;
import com.topicus.keyhub.sdk.models.vault.VaultRecord;
import com.topicus.keyhub.sdk.models.vault.VaultRecordAdditionalObjects;
import com.topicus.keyhub.sdk.models.vault.VaultRecordSecrets;
import com.topicus.keyhub.sdk.models.vault.VaultSecretType;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;

public class KeyHubCommunicationServiceTest {
	private static final String TEST_UUID = UUID.randomUUID().toString();
	
    @Test
    public void fetchCredentialsTest() throws Exception {
        // Arrange
        Group testGroup = new Group();
        testGroup.setName("testKeyHubGroup");

        VaultRecord testRecord = new VaultRecord();
        testRecord.setUuid(TEST_UUID);
        testRecord.setName("testKeyHubRecord");
        testRecord.setUsername("testRecordUsername");
		testRecord.setTypes(List.of(VaultSecretType.PASSWORD));

        List<VaultRecord> testKeyHubRecordList = new ArrayList<>(Arrays.asList(testRecord));

        Secret testSecret = Secret.fromString("testSecret");
        ClientCredentials testClientCredentials = new ClientCredentials("testId", testSecret);
        IVaultAccessor mockedVaultAccessor = mock(IVaultAccessor.class);

        IKeyHubCommunicationService communicationService = new KeyHubCommunicationService() {
            @Override
            protected Optional<String> getKeyHubURI() {
                return Optional.of("https://keyhub.topicusonderwijs.nl");
            }

            @Override
            protected IVaultAccessor createVaultAccessor(ClientCredentials clientCredentials) {
                return mockedVaultAccessor;
            }
        };

        when(mockedVaultAccessor.fetchRecordsFromVault()).thenReturn(testKeyHubRecordList);

        // Act
        List<KeyHubUsernamePasswordCredentials> result = communicationService
                .fetchCredentials(KeyHubUsernamePasswordCredentials.class, testClientCredentials);

        // Assert
        assertEquals(TEST_UUID, result.iterator().next().getId());
        assertEquals("testKeyHubRecord", result.iterator().next().getDescription());
        assertEquals("testRecordUsername", result.iterator().next().getUsername());

    }

    @Test
    public void fetchRecordSecretTest() {
        //Arrange
        IVaultAccessor mockedVaultAccessor = mock(IVaultAccessor.class);
        IKeyHubCommunicationService communicationService = new KeyHubCommunicationService() {
            @Override
            protected Optional<String> getKeyHubURI() {
                return Optional.of("https://keyhub.topicusonderwijs.nl");
            }

            @Override
            protected IVaultAccessor createVaultAccessor(ClientCredentials clientCredentials) {
                return mockedVaultAccessor;
            }
        };
        VaultRecord testRecord = new VaultRecord();
        VaultRecordAdditionalObjects additionalObjects = new VaultRecordAdditionalObjects();
        VaultRecordSecrets testRecordSecret = new VaultRecordSecrets();
        testRecord.setUuid(TEST_UUID);
        testRecord.setAdditionalObjects(additionalObjects);
        testRecordSecret.setPassword("testSecret");
        additionalObjects.setSecret(testRecordSecret);

        ClientCredentials testClientCredentials = new ClientCredentials("testId", Secret.fromString("testSecret"));

        when(mockedVaultAccessor.fetchRecordSecret(anyString())).thenReturn(testRecord);

        //Act
        VaultRecord record = communicationService.fetchRecordSecret(testClientCredentials, TEST_UUID);

        //Assert
        assertEquals("testSecret", record.getAdditionalObjects().getSecret().getPassword());
    }

}
