package nl.topicus.keyhub.jenkins.vault;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.util.Collection;
import java.util.Collections;

import hudson.ExtensionList;
import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;

@PrepareForTest(KeyHubCommunicationService.class)
@RunWith(PowerMockRunner.class)
public class KeyHubCommunicationServiceTest {

    // @Rule
    // public JenkinsRule jenkins = new JenkinsRule();

    //private IKeyHubCommunicationService spyCommunicationService = new KeyHubCommunicationService();

    @Before
    public void setUp() {
        // ExtensionList<IKeyHubCommunicationService> extensionList = ExtensionList
        // .lookup(IKeyHubCommunicationService.class);
        // KeyHubCommunicationService realService =
        // extensionList.get(KeyHubCommunicationService.class);
        // extensionList.remove(realService);

        // IKeyHubCommunicationService keyHubCommunicationServiceStub = ExtensionList
        // .lookup(IKeyHubCommunicationService.class).get(IKeyHubCommunicationService.class);

        //spyCommunicationService = spy(new KeyHubCommunicationService());
    }

    @Test
    public void fetchCredentialsTest() throws Exception {
        // Arrange
        IKeyHubCommunicationService spyCommunicationService = PowerMockito.mock(new KeyHubCommunicationService());
        ClientCredentials clientCredentials = new ClientCredentials("testId", Secret.fromString("testSecret"));
        Collection<KeyHubUsernamePasswordCredentials> result;

        // Act
        // PowerMockito.doReturn(null).when(spyCommunicationService, "getKeyHubURI");
        PowerMockito.when(spyCommunicationService, "getKeyHubURI").thenReturn(null);
        result = spyCommunicationService.fetchCredentials(clientCredentials);

        // Assert
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void fetchRecordSecretTest() {

    }

}
