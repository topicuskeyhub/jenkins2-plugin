import java.util.Collection;

import hudson.Extension;
import hudson.ExtensionPoint;
import nl.topicus.keyhub.jenkins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import nl.topicus.keyhub.jenkins.model.ClientCredentials;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;
import nl.topicus.keyhub.jenkins.vault.IKeyHubCommunicationService;

@Extension
public class KeyHubCommunicationServiceStub implements IKeyHubCommunicationService, ExtensionPoint {

    @Override
    public Collection<KeyHubUsernamePasswordCredentials> fetchCredentials(ClientCredentials clientCredentials) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public KeyHubVaultRecord fetchRecordSecret(ClientCredentials clientCredentials, String href) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
