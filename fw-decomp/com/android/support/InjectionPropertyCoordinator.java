package com.android.support;

import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
final class InjectionPropertyCoordinator {
    private final AtomicLong generation = new AtomicLong();
    private final Object actionLock = new Object();

    InjectionPropertyCoordinator() {
    }

    long beginLease() {
        return this.generation.incrementAndGet();
    }

    boolean runIfCurrent(long j, Runnable runnable) {
        if (j <= 0 || this.generation.get() != j) {
            return false;
        }
        synchronized (this.actionLock) {
            if (this.generation.get() != j) {
                return false;
            }
            runnable.run();
            return true;
        }
    }
}
