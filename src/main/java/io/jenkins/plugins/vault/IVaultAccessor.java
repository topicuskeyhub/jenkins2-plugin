package io.jenkins.plugins.vault;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import hudson.util.Secret;
import io.jenkins.plugins.model.response.group.KeyHubGroup;
import io.jenkins.plugins.model.response.record.KeyHubRecord;

public interface IVaultAccessor {

    public List<KeyHubGroup> fetchGroupData() throws IOException;

    public List<KeyHubRecord> fetchRecordsFromVault(List<KeyHubGroup> groups) throws IOException;

    public String fetchRecordSecret(String href) throws UnsupportedEncodingException;
}
