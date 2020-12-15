package io.jenkins.plugins.credentials;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cloudbees.plugins.credentials.common.StandardCredentials;

import io.jenkins.plugins.credentials.CredentialsFactory;
import io.jenkins.plugins.model.response.record.KeyHubRecord;
import io.jenkins.plugins.vault.IVaultAccessor;
import io.jenkins.plugins.vault.VaultAccessor;

public class CredentialsSupplier implements Supplier<Collection<StandardCredentials>> {

    private CredentialsSupplier() {

    }

    public static Supplier<Collection<StandardCredentials>> standard() {
        return new CredentialsSupplier();
    }

    @Override
    public Collection<StandardCredentials> get() {

        final Optional<List<IVaultAccessor>> clients = Optional.ofNullable(config.getBeta())
                .flatMap(beta -> Optional.ofNullable(beta.getClients())).map(Clients::build);

        // clients = va
        IVaultAccessor va = new VaultAccessor();

        final Stream<StandardCredentials> creds;
        if (va != null) {
            final Supplier<Collection<StandardCredentials>> supplier = new SingleAccountCredentialsSupplier(va);

            final Collection<Supplier<Collection<StandardCredentials>>> multipleSuppliers = clients.get().stream()
                    .map(client -> new SingleAccountCredentialsSupplier(client))
                    .collect(Collectors.toList());
            try {
                creds = supplier.get().stream().flatMap(Collection::stream);
            } catch (CompletionException | IllegalStateException e) {
                e.printStackTrace();
            }
        }

        return creds.collect(Collectors.toMap(StandardCredentials::getId, Function.identity())).values();
    }

    private static class SingleAccountCredentialsSupplier implements Supplier<Collection<StandardCredentials>> {

        private final IVaultAccessor client;

        SingleAccountCredentialsSupplier(IVaultAccessor client) {
            this.client = client;
        }

        @Override
        public Collection<StandardCredentials> get() {
            final Collection<KeyHubRecord> secretList = new ListSecretsOperation(client).get();

            return secretList.stream().flatMap(secretListEntry -> {
                final Optional<StandardCredentials> cred = CredentialsFactory.create(name, description, tags, client);
                return Optionals.stream(cred);
            }).collect(Collectors.toList());
        }
    }
}
