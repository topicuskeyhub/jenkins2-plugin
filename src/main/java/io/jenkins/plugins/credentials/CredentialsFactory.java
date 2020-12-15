package io.jenkins.plugins.credentials;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.cloudbees.plugins.credentials.SecretBytes;
import com.cloudbees.plugins.credentials.common.StandardCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.util.Secret;
import io.jenkins.plugins.credentials.username_password.KeyHubUsernamePasswordCredentials;
import io.jenkins.plugins.credentials.username_password.KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder;
import io.jenkins.plugins.model.response.record.KeyHubRecord;
import io.jenkins.plugins.vault.IVaultAccessor;

public abstract class CredentialsFactory {

    private CredentialsFactory() {

    }

    /**
     * Construct a Jenkins credential from a Secrets Manager secret.
     *
     * @param name        the secret's name (must be unique within the AWS account)
     * @param description the secret's description
     * @param tags        the secret's AWS tags
     * @param client      the Secrets Manager client that will retrieve the secret's
     *                    value on demand
     * @return a credential (if one could be constructed from the secret's
     *         properties)
     */
    public static Optional<StandardCredentials> create(String id, String name, String href, String description,
            Map<String, String> tags, IVaultAccessor client) {
        final String type = "";
        final String username = "";
        final String filename = "";

        switch (type) {
            case Type.usernamePassword:
                return Optional.of(
                        KeyHubUsernamePasswordCredentials.KeyHubCredentialsBuilder.newInstance().id(id).recordName(name)
                                .href(href).username(description).password(new SecretSupplier(client, name)).build());
            default:
                return Optional.empty();
        }
    }

    private static class SecretBytesSupplier extends RealSecretsManager implements Supplier<SecretBytes> {

        private SecretBytesSupplier(IVaultAccessor client, String name) {
            super(client, name);
        }

        @Override
        public SecretBytes get() {
            return getSecretValue().match(new SecretValue.Matcher<SecretBytes>() {
                @Override
                public SecretBytes string(String str) {
                    return null;
                }

                @Override
                public SecretBytes binary(byte[] bytes) {
                    return SecretBytes.fromBytes(bytes);
                }
            });
        }
    }

    private static class SecretSupplier extends RealSecretsManager implements Supplier<Secret> {

        private SecretSupplier(IVaultAccessor client, String name) {
            super(client, name);
        }

        @Override
        public Secret get() {
            return getSecretValue().match(new SecretValue.Matcher<Secret>() {
                @Override
                public Secret string(String str) {
                    return Secret.fromString(str);
                }

                @Override
                public Secret binary(byte[] bytes) {
                    return null;
                }
            });
        }
    }

    private static class StringSupplier extends RealSecretsManager implements Supplier<String> {

        private StringSupplier(IVaultAccessor client, String name) {
            super(client, name);
        }

        @Override
        public String get() {
            return getSecretValue().match(new SecretValue.Matcher<String>() {
                @Override
                public String string(String str) {
                    return str;
                }

                @Override
                public String binary(byte[] bytes) {
                    return null;
                }
            });
        }
    }

    private static class RealSecretsManager {

        private final String id;
        private final transient IVaultAccessor client;

        RealSecretsManager(IVaultAccessor client, String id) {
            this.client = client;
            this.id = id;
        }

        @NonNull
        SecretValue getSecretValue() {
            String result;
            try {
                result = client.fetchRecordSecret("href");
                return SecretValue.string(result);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
