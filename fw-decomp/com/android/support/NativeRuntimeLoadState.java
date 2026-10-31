package com.android.support;

/* loaded from: classes.dex */
final class NativeRuntimeLoadState {
    private volatile Throwable failure;
    private volatile boolean loaded;

    NativeRuntimeLoadState() {
    }

    synchronized void load(Runnable runnable) {
        if (this.loaded) {
            return;
        }
        try {
            runnable.run();
            this.loaded = true;
            this.failure = null;
        } catch (LinkageError | SecurityException e) {
            this.failure = e;
            throw e;
        }
    }

    boolean isLoaded() {
        return this.loaded;
    }

    Throwable failure() {
        return this.failure;
    }
}
