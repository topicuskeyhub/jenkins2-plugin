package nl.topicus.keyhub.jenkins.credentials;

import java.io.UnsupportedEncodingException;
import java.util.function.Supplier;
import java.util.logging.Logger;

import hudson.util.Secret;
import nl.topicus.keyhub.jenkins.model.response.record.RecordSecret;
import nl.topicus.keyhub.jenkins.vault.IVaultAccessor;

public class SecretSupplier implements Supplier<Secret> {

    private transient IVaultAccessor va;
    private String href;

    public SecretSupplier(IVaultAccessor va, String href) {
        this.va = va;
        this.href = href;
    }

    @Override
    public Secret get() {
        RecordSecret secret = va.fetchRecordSecret(this.href);
        return secret.getPassword();
    }

}
