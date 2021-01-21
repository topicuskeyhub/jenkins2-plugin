package nl.topicus.keyhub.jenkins.vault;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.model.response.group.KeyHubGroup;
import nl.topicus.keyhub.jenkins.model.response.record.KeyHubVaultRecord;
import nl.topicus.keyhub.jenkins.model.response.record.RecordSecret;

public interface IVaultAccessor {

    public List<KeyHubGroup> fetchGroupData() throws IOException;

    public List<KeyHubVaultRecord> fetchRecordsFromVault(List<KeyHubGroup> groups) throws IOException;

    public RecordSecret fetchRecordSecret(String href);
}
