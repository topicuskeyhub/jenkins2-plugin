package nl.topicus.keyhub.jenkins.credentials.username_password;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;

public class KeyHubCredentialsBuilderTest {

    @Test
    public void builtKeyHubUsernameCredentialsCorrectly() {

        KeyHubUsernamePasswordCredentials builtCredentials = KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance().
        id("123-testId").recordName("testRecordName").href("testHref").username("testUsername").password(null).build();

        builtCredentials.getHref();
        assertEquals("", "");
    }

    /*
     * KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance()
     * .id(khRecords.get(i).getUUID()).recordName(khRecords.get(i).getName())
     * .href(khRecords.get(i).getHref()).username(khRecords.get(i).getUsername())
     * .password(new SecretSupplier(vaultAccessor,
     * khRecords.get(i).getHref())).build()
     */

}
