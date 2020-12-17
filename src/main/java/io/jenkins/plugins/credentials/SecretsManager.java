package io.jenkins.plugins.credentials;

import java.io.UnsupportedEncodingException;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.jenkins.plugins.vault.IVaultAccessor;

public class SecretsManager {

    private IVaultAccessor client;
    private String href;

    public SecretsManager(IVaultAccessor client, String href) {
        this.client = client;
        this.href = href;
    }

    @NonNull
    SecretValue getSecretValue() {
        try {
            return SecretValue.string((client.fetchRecordSecret(this.href)).getEncryptedValue());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}