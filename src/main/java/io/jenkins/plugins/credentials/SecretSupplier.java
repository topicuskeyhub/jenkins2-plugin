package io.jenkins.plugins.credentials;

import java.io.UnsupportedEncodingException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.util.Secret;
import io.jenkins.plugins.vault.IVaultAccessor;

public class SecretSupplier implements Supplier<Secret> {

    private static final Logger LOG = Logger.getLogger(SecretSupplier.class.getName());

    private transient IVaultAccessor va;
    private String href;

    public SecretSupplier(IVaultAccessor va, String href) {
        this.va = va;
        this.href = href;
    }

    @Override
    public Secret get() {
        try {
            return va.fetchRecordSecret(this.href);
        } catch (UnsupportedEncodingException e) {
            LOG.log(Level.WARNING, "The supplier could not get the password: message=[{0}]", e.getMessage());
        }
        return Secret.fromString("");
    }

}
