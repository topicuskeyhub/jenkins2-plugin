package nl.topicus.keyhub.jenkins.vault;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.model.response.KeyHubTokenResponse;
import nl.topicus.keyhub.jenkins.model.response.group.KeyHubGroup;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;

// @RunWith(PowerMockRunner.class)
@PrepareForTest(KeyHubCommunicationService.class)
// @PowerMockIgnore({ "javax.net.ssl.*", "javax.management.*",
// "javax.crypto.JceSecurity.*", "javax.crypto.*" })
public class KeyHubCommunicationServiceTest {

    @Rule
    public final JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void fetchCredentialsTest() throws Exception {
        // Arrange
        KeyHubGroup testGroup = new KeyHubGroup();
        testGroup.setName("testKeyHubGroup");
        List<KeyHubGroup> testKeyHubGroupList = new ArrayList<>(Arrays.asList(testGroup));

        KeyHubVaultRecord testRecord = new KeyHubVaultRecord();
        testRecord.setName("testKeyHubRecord");
        List<KeyHubVaultRecord> testKeyHubRecordList = new ArrayList<>(Arrays.asList(testRecord));

        Secret testSecret = Secret.fromString("testSecret");
        ClientCredentials testClientCredentials = new ClientCredentials("testId", testSecret);
        VaultAccessor mockedVaultAccessor = mock(VaultAccessor.class);

        PowerMockito.whenNew(VaultAccessor.class).withArguments(eq(testClientCredentials), anyString(),
                any(RestClientBuilder.class), any(KeyHubTokenResponse.class)).thenReturn(mockedVaultAccessor);
        IKeyHubCommunicationService communicationService = new KeyHubCommunicationService();

        when(communicationService, "getKeyHubURI").thenReturn(null);

        when(mockedVaultAccessor.fetchGroupData()).thenReturn(testKeyHubGroupList);
        when(mockedVaultAccessor.fetchRecordsFromVault(anyList())).thenReturn(testKeyHubRecordList);

        // Act
        Collection<KeyHubUsernamePasswordCredentials> result = communicationService
                .fetchCredentials(testClientCredentials);

        // Assert
        System.out.println(result.stream());

    }

    @Test
    public void fetchRecordSecretTest() {
        assertEquals("", "");
    }

}
