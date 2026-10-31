package com.android.support;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/* loaded from: classes.dex */
final class CancellableRetryDelay {
    private CancellableRetryDelay() {
    }

    static boolean await(Object obj, long j, BooleanSupplier booleanSupplier) {
        if (obj == null || booleanSupplier == null) {
            throw new IllegalArgumentException("monitor and shouldContinue are required");
        }
        synchronized (obj) {
            if (!booleanSupplier.getAsBoolean()) {
                return false;
            }
            if (j <= 0) {
                return true;
            }
            long nanos = TimeUnit.MILLISECONDS.toNanos(j);
            long jNanoTime = System.nanoTime() + nanos;
            while (booleanSupplier.getAsBoolean() && nanos > 0) {
                long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
                try {
                    obj.wait(millis, (int) (nanos - TimeUnit.MILLISECONDS.toNanos(millis)));
                    nanos = jNanoTime - System.nanoTime();
                } catch (InterruptedException unused) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return booleanSupplier.getAsBoolean();
        }
    }
}
