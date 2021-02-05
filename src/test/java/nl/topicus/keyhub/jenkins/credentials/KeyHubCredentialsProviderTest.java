package nl.topicus.keyhub.jenkins.credentials;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import hudson.ExtensionList;
import hudson.model.ItemGroup;
import hudson.security.ACL;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ExtensionList.class })
public class KeyHubCredentialsProviderTest {

    private ItemGroup<?> itemGroup;

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ExtensionList.class);

    }

    @Test
    public void getCredentialsTestItemGroupNull() {
        KeyHubCredentialsProvider provider = new KeyHubCredentialsProvider();

        List<UsernamePasswordCredentials> credentials = provider.getCredentials(UsernamePasswordCredentials.class,
                (ItemGroup<?>) null, ACL.SYSTEM);
        assertEquals(Collections.emptyList(), credentials);
    }

    @Test
    public void getCredentialsTestNotAuthenticatedReturnsEmptyList() {
        KeyHubCredentialsProvider provider = new KeyHubCredentialsProvider();

        List<UsernamePasswordCredentials> credentials = provider.getCredentials(UsernamePasswordCredentials.class,
                (ItemGroup<?>) null, null);
        assertEquals(Collections.emptyList(), credentials);
    }

}