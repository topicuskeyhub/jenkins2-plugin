package io.jenkins.plugins.credentials;

import java.io.Serializable;
import java.time.Duration;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;

final class CustomSuppliers {

    private CustomSuppliers() {

    }

    public static <T> Supplier<T> memoizeWithExpiration(Supplier<T> delegate, Supplier<Duration> duration) {
        return new ExpiringMemoizingSupplier<>(delegate, duration);
    }

    static class ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable {
        final Supplier<T> delegate;
        final Supplier<Duration> duration;
        transient volatile T value;
        // The special value 0 means "not yet initialized".
        transient volatile long expirationNanos;

        ExpiringMemoizingSupplier(Supplier<T> delegate, Supplier<Duration> duration) {
            this.delegate = Preconditions.checkNotNull(delegate);
            this.duration = duration;
        }

        private long getDurationNanos() {
            Duration d = duration.get();
            Preconditions.checkArgument(!d.isNegative() && !d.isZero());
            return d.toNanos();
        }

        @Override
        public T get() {
            // Another variant of Double Checked Locking.
            //
            // We use two volatile reads. We could reduce this to one by
            // putting our fields into a holder class, but (at least on x86)
            // the extra memory consumption and indirection are more
            // expensive than the extra volatile reads.
            long nanos = expirationNanos;
            long now = System.nanoTime();
            if (nanos == 0 || now - nanos >= 0) {
                synchronized (this) {
                    if (nanos == expirationNanos) { // recheck for lost race
                        T t = delegate.get();
                        value = t;
                        nanos = now + getDurationNanos();
                        // In the very unlikely event that nanos is 0, set it to 1;
                        // no one will notice 1 ns of tardiness.
                        expirationNanos = (nanos == 0) ? 1 : nanos;
                        return t;
                    }
                }
            }
            return value;
        }

        private static final long serialVersionUID = 0;
    }
}