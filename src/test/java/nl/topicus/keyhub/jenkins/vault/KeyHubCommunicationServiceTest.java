package nl.topicus.keyhub.jenkins.vault;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.model.response.KeyHubTokenResponse;
import nl.topicus.keyhub.jenkins.model.response.Link;
import nl.topicus.keyhub.jenkins.model.response.group.KeyHubGroup;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;

public class KeyHubCommunicationServiceTest {

    @Test
    public void fetchCredentialsTest() throws Exception {
        // Arrange
        KeyHubGroup testGroup = new KeyHubGroup();
        testGroup.setName("testKeyHubGroup");
        List<KeyHubGroup> testKeyHubGroupList = new ArrayList<>(Arrays.asList(testGroup));

        Link testLink = new Link();
        testLink.setHref("www.testHref.com");
        List<Link> testLinkList = new ArrayList<>(Arrays.asList(testLink));

        KeyHubVaultRecord testRecord = new KeyHubVaultRecord();
        testRecord.setLinks(testLinkList);
        testRecord.setUUID("testRecordUUID");
        testRecord.setName("testKeyHubRecord");
        testRecord.setUsername("testRecordUsername");

        List<KeyHubVaultRecord> testKeyHubRecordList = new ArrayList<>(Arrays.asList(testRecord));

        Secret testSecret = Secret.fromString("testSecret");
        ClientCredentials testClientCredentials = new ClientCredentials("testId", testSecret);
        IVaultAccessor mockedVaultAccessor = mock(VaultAccessor.class);

        IKeyHubCommunicationService communicationService = new KeyHubCommunicationService() {
            @Override
            protected Optional<String> getKeyHubURI() {
                return Optional.of("https://keyhub.topicusonderwijs.nl");
            }

            @Override
            protected KeyHubTokenResponse getTokenForClient(ClientCredentials credentials) {
                return new KeyHubTokenResponse();
            }

            @Override
            protected IVaultAccessor createVaultAccessor(ClientCredentials clientCredentials) {
                return mockedVaultAccessor;
            }
        };

        when(mockedVaultAccessor.fetchGroupData()).thenReturn(testKeyHubGroupList);
        when(mockedVaultAccessor.fetchRecordsFromVault(anyList())).thenReturn(testKeyHubRecordList);

        // Act
        Collection<KeyHubUsernamePasswordCredentials> result = communicationService
                .fetchCredentials(testClientCredentials);

        // Assert
        assertEquals("testRecordUUID", result.iterator().next().getId());
        assertEquals("testKeyHubRecord", result.iterator().next().getDescription());
        assertEquals("www.testHref.com", result.iterator().next().getHref());
        assertEquals("testRecordUsername", result.iterator().next().getUsername());

    }

    @Test
    public void fetchRecordSecretTest() {
        //Arrange
        IVaultAccessor mockedVaultAccessor = mock(VaultAccessor.class);
        
        //Act

        //Assert
    }

}
