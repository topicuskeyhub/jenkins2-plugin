package nl.topicus.keyhub.jenkins.vault;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.configuration.GlobalPluginConfiguration;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;

public class KeyHubCommunicationServiceIntegrationTest {

        public String globalKeyHubURI = "https://keyhub.topicusonderwijs.nl";
        private static final Logger LOG = Logger.getLogger(KeyHubCommunicationServiceIntegrationTest.class.getName());

        @Rule
        public JenkinsRule jenkins = new JenkinsRule();

        @Test
        public void getCredentialsFromKeyHubVault() {
                // Arrange
                IKeyHubCommunicationService communicationService = new KeyHubCommunicationService();
                ClientCredentials testClientCredentials = new ClientCredentials(System.getenv("CLIENT_ID"),
                                Secret.fromString(System.getenv("CLIENT_SECRET")));
                List<KeyHubUsernamePasswordCredentials> retrievedCredentials = new ArrayList<>();
                jenkins.getInstance().getExtensionList(GlobalPluginConfiguration.class).get(0)
                                .setKeyhubURI(globalKeyHubURI);

                // Act
                LOG.log(Level.WARNING, "Client ID : " + testClientCredentials.getClientId());
                System.out.println("Client ID : " + testClientCredentials.getClientId());
                Collection<KeyHubUsernamePasswordCredentials> credentials = communicationService
                                .fetchCredentials(testClientCredentials);
                for (KeyHubUsernamePasswordCredentials credential : credentials) {
                        retrievedCredentials.add(credential);
                }

                // Assert
                assertEquals("Demo Record 3_IT", retrievedCredentials.get(1).getDescription());
                assertEquals("Demo Record 1_IT", retrievedCredentials.get(2).getDescription());
                assertEquals("Demo Record 2_IT", retrievedCredentials.get(3).getDescription());
                assertThat(communicationService
                                .fetchRecordSecret(testClientCredentials, retrievedCredentials.get(0).getHref())
                                .getAdditionalObjects().getSecret().getPassword().getPlainText(),
                                is(not(emptyOrNullString())));
        }

}
