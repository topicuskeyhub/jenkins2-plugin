package io.jenkins.plugins.vault;

import java.io.IOException;
import java.util.List;

import io.jenkins.plugins.model.KeyHubRecord;
import io.jenkins.plugins.model.response.KeyHubGroup;

public interface IVaultAccessor {
    public void fetchAuthenticationTokenAndGetVaultAccess() throws IOException, InterruptedException;

    public List<KeyHubGroup> fetchGroupData() throws IOException ;

    public void fetchRecordsFromVault(KeyHubGroup group);

    public KeyHubRecord fetchRecordSecret(KeyHubRecord record);

}
