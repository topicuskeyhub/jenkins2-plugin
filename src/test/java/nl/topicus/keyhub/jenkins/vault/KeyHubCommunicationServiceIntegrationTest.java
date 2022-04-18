package nl.topicus.keyhub.jenkins.vault;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.jenkinsci.plugins.plaincredentials.FileCredentials;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.configuration.GlobalPluginConfiguration;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;

public class KeyHubCommunicationServiceIntegrationTest {

	public String globalKeyHubURI = "https://keyhub.topicusonderwijs.nl";

	@Rule
	public JenkinsRule jenkins = new JenkinsRule();

	@Test
	public void getCredentialsFromKeyHubVault() {
		// Arrange
		IKeyHubCommunicationService communicationService = new KeyHubCommunicationService();
		ClientCredentials testClientCredentials = new ClientCredentials(System.getenv("CLIENT_ID"),
				Secret.fromString(System.getenv("CLIENT_SECRET")));
		jenkins.getInstance().getExtensionList(GlobalPluginConfiguration.class).get(0).setKeyhubURI(globalKeyHubURI);

		// Act
		List<StandardUsernamePasswordCredentials> credentials = communicationService
				.fetchCredentials(StandardUsernamePasswordCredentials.class, testClientCredentials);

		// Assert
		assertEquals("Demo Record 3_IT", credentials.get(1).getDescription());
		assertEquals("Demo Record 1_IT", credentials.get(2).getDescription());
		assertEquals("Demo Record 2_IT", credentials.get(3).getDescription());
		KeyHubUsernamePasswordCredentials cred0 = (KeyHubUsernamePasswordCredentials) credentials.get(0);
		MatcherAssert.assertThat(communicationService.fetchRecordSecret(testClientCredentials, cred0.getHref())
				.getAdditionalObjects().getSecret().getPassword().getPlainText(), is(not(emptyOrNullString())));
	}

	@Test
	public void getStringCredentialsFromKeyHubVault() {
		// Arrange
		IKeyHubCommunicationService communicationService = new KeyHubCommunicationService();
		ClientCredentials testClientCredentials = new ClientCredentials(System.getenv("CLIENT_ID"),
				Secret.fromString(System.getenv("CLIENT_SECRET")));
		jenkins.getInstance().getExtensionList(GlobalPluginConfiguration.class).get(0).setKeyhubURI(globalKeyHubURI);

		// Act
		List<StringCredentials> credentials = communicationService.fetchCredentials(StringCredentials.class,
				testClientCredentials);

		// Assert
		assertEquals("Demo Record 3_IT", credentials.get(1).getDescription());
		assertEquals("Demo Record 3_IT", credentials.get(1).getSecret().getPlainText());
		assertEquals("Demo Record 1_IT", credentials.get(2).getDescription());
		assertEquals("Demo Record 3_IT", credentials.get(2).getSecret().getPlainText());
		assertEquals("Demo Record 2_IT", credentials.get(3).getDescription());
		assertEquals("Demo Record 3_IT", credentials.get(3).getSecret().getPlainText());
	}

	@Test
	public void getFileCredentialsFromKeyHubVault() throws IOException {
		// Arrange
		IKeyHubCommunicationService communicationService = new KeyHubCommunicationService();
		ClientCredentials testClientCredentials = new ClientCredentials(System.getenv("CLIENT_ID"),
				Secret.fromString(System.getenv("CLIENT_SECRET")));
		jenkins.getInstance().getExtensionList(GlobalPluginConfiguration.class).get(0).setKeyhubURI(globalKeyHubURI);

		// Act
		List<FileCredentials> credentials = communicationService.fetchCredentials(FileCredentials.class,
				testClientCredentials);

		// Assert
		assertEquals("Demo Record 3_IT", credentials.get(1).getDescription());
		assertEquals("Demo Record 3_IT", credentials.get(1).getFileName());
		assertEquals("Demo Record 3_IT",
				new String(IO.readBytes(credentials.get(1).getContent()), StandardCharsets.UTF_8));
	}
}
