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

import com.cloudbees.plugins.credentials.common.StandardCredentials;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.configuration.GlobalPluginConfiguration;
import nl.topicus.keyhub.jenkins.credentials.file.KeyHubFileCredentials;
import nl.topicus.keyhub.jenkins.credentials.sshuser.KeyHubSSHUserPrivateKeyCredentials;
import nl.topicus.keyhub.jenkins.credentials.string.KeyHubStringCredentials;
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
		List<StandardCredentials> credentials = communicationService
				.fetchCredentials(StandardCredentials.class, testClientCredentials);

		// Assert
		assertEquals("Demo Record 1_IT", credentials.get(1).getDescription());
		assertEquals(KeyHubSSHUserPrivateKeyCredentials.class, credentials.get(1).getClass());
		assertEquals("Demo Record 2_IT", credentials.get(2).getDescription());
		assertEquals(KeyHubUsernamePasswordCredentials.class, credentials.get(2).getClass());
		assertEquals("Demo Record 3_IT", credentials.get(3).getDescription());
		assertEquals(KeyHubUsernamePasswordCredentials.class, credentials.get(3).getClass());
		assertEquals("Demo Record 4_IT", credentials.get(4).getDescription());
		assertEquals(KeyHubFileCredentials.class, credentials.get(4).getClass());
		assertEquals("Demo Record 5_IT", credentials.get(5).getDescription());
		assertEquals(KeyHubStringCredentials.class, credentials.get(5).getClass());
		KeyHubUsernamePasswordCredentials cred0 = (KeyHubUsernamePasswordCredentials) credentials.get(0);
		MatcherAssert.assertThat(communicationService.fetchRecordSecret(testClientCredentials, cred0.getId())
				.getAdditionalObjects().getSecret().getPassword(), is(not(emptyOrNullString())));
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
		assertEquals("Demo Record 5_IT", credentials.get(0).getDescription());
		assertEquals("2fXciTPxIRfeQUbZWdxEb2VmFV3N", credentials.get(0).getSecret().getPlainText());
		assertEquals(1, credentials.size());
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
		assertEquals("Demo Record 4_IT", credentials.get(0).getDescription());
		assertEquals("KeyHubTestSecretFile.txt", credentials.get(0).getFileName());
		assertEquals("Content of the KeyHub secret file",
				new String(IO.readBytes(credentials.get(0).getContent()), StandardCharsets.UTF_8));
	}
}
