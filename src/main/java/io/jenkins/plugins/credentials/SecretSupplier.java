package io.jenkins.plugins.credentials;

import java.io.UnsupportedEncodingException;
import java.util.function.Supplier;

import hudson.util.Secret;
import io.jenkins.plugins.vault.IVaultAccessor;

public class SecretSupplier extends SecretsManager implements Supplier<Secret> {

    private IVaultAccessor va;
    private String href;

    public SecretSupplier(IVaultAccessor va, String href) {
        super(va, href);
    }

    @Override
    public Secret get() {
        try {
            return va.fetchRecordSecret(this.href);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Secret.fromString("");
    }

}
