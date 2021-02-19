package nl.topicus.keyhub.jenkins.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import java.util.Optional;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.google.common.base.Strings;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;

import hudson.ExtensionList;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.Shell;
import nl.topicus.keyhub.jenkins.vault.IKeyHubCommunicationService;
import nl.topicus.keyhub.jenkins.vault.KeyHubCommunicationService;

public class GlobalPluginConfigurationTest {

    @Rule
    public final JenkinsRule jenkins = new JenkinsRule();

    //private CredentialsStore store;

    @Test
    public void blankTest() {
        assertEquals("","");
    }


}
