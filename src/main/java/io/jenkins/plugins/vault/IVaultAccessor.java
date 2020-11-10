package io.jenkins.plugins.vault;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import io.jenkins.plugins.model.response.group.KeyHubGroup;
import io.jenkins.plugins.model.response.group.ListOfKeyHubGroups;
import io.jenkins.plugins.model.response.record.KeyHubRecord;
import io.jenkins.plugins.model.response.record.ListOfKeyHubRecords;

public interface IVaultAccessor {
    public void fetchAuthenticationTokenAndGetVaultAccess() throws IOException, InterruptedException;

    public ListOfKeyHubGroups fetchGroupData() throws IOException;

    public ListOfKeyHubRecords fetchRecordsFromVault(KeyHubGroup group) throws IOException;

    public void fetchRecordSecret(KeyHubRecord record) throws IOException, UnsupportedEncodingException;
}
