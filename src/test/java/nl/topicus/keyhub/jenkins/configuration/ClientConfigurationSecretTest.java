package nl.topicus.keyhub.jenkins.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.cloudbees.hudson.plugins.folder.Folder;

import hudson.util.Secret;
import jenkins.model.Jenkins;

public class ClientConfigurationSecretTest {

    private static final String PLAIN_SECRET = "plain-client-secret-do-not-store";

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void clientSecretIsNotStoredInPlainText() throws Exception {
        Folder folder = jenkinsRule.jenkins.createProject(Folder.class, "team-a");
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setClientId("client-id");
        configuration.setClientSecret(Secret.fromString(PLAIN_SECRET));
        folder.addProperty(new FolderKeyHubClientConfiguration(configuration));
        folder.save();

        String configXml = Files.readString(folder.getConfigFile().getFile().toPath(), StandardCharsets.UTF_8);
        assertFalse("config.xml contains the client secret in plain text", configXml.contains(PLAIN_SECRET));

        jenkinsRule.jenkins.reload();
        Folder reloaded = jenkinsRule.jenkins.getItemByFullName("team-a", Folder.class);
        ClientConfiguration reloadedConfiguration = reloaded.getProperties().get(FolderKeyHubClientConfiguration.class)
                .getConfiguration();
        assertEquals(PLAIN_SECRET, reloadedConfiguration.getClientCredentials().getClientSecret().getPlainText());
    }

    @Test
    public void clientSecretIsNotRenderedInConfigurePage() throws Exception {
        Folder folder = jenkinsRule.jenkins.createProject(Folder.class, "team-a");
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setClientId("client-id");
        configuration.setClientSecret(Secret.fromString(PLAIN_SECRET));
        folder.addProperty(new FolderKeyHubClientConfiguration(configuration));

        try (JenkinsRule.WebClient webClient = jenkinsRule.createWebClient()) {
            String page = webClient.getPage(folder, "configure").getWebResponse().getContentAsString();
            assertFalse("configure page contains the client secret in plain text", page.contains(PLAIN_SECRET));
        }
    }

    @Test
    public void legacyPlainTextSecretIsStillReadable() {
        String legacyXml = "<nl.topicus.keyhub.jenkins.configuration.ClientConfiguration>"
                + "<clientId>client-id</clientId>"
                + "<clientSecret>" + PLAIN_SECRET + "</clientSecret>"
                + "</nl.topicus.keyhub.jenkins.configuration.ClientConfiguration>";

        ClientConfiguration configuration = (ClientConfiguration) Jenkins.XSTREAM2.fromXML(legacyXml);

        assertEquals("client-id", configuration.getClientId());
        assertEquals(PLAIN_SECRET, configuration.getClientCredentials().getClientSecret().getPlainText());
    }
}
