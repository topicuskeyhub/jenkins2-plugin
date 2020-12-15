package io.jenkins.plugins.credentials.supplier;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class ParallelSupplier<T> implements Supplier<Collection<T>> {

    private final Collection<Supplier<T>> suppliers;

    ParallelSupplier(Collection<Supplier<T>> suppliers) {
        this.suppliers = suppliers;
    }

    /**
     * Run the suppliers.
     *
     * @throws CancellationException if one of the suppliers' computations was cancelled
     * @throws CompletionException if one of the suppliers' futures completed exceptionally or a completion computation
     * threw an exception
     */
    @Override
    public Collection<T> get() {
        final Collection<CompletableFuture<T>> supplierFutures = suppliers.stream()
                .map(CompletableFuture::supplyAsync)
                .collect(Collectors.toList());

        final CompletableFuture<Collection<T>> future = flip(supplierFutures);

        return future.join();
    }

    /**
     * Flip a Seq of Futures to a Future of Seq.
     */
    private static <T> CompletableFuture<Collection<T>> flip(Collection<CompletableFuture<T>> futures) {
        final CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0]));
        return allDoneFuture.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}