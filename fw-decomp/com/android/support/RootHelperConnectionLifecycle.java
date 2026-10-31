package com.android.support;

import java.io.Closeable;

/* loaded from: classes.dex */
final class RootHelperConnectionLifecycle {
    private Closeable activeConnection;
    private boolean closed;

    RootHelperConnectionLifecycle() {
    }

    synchronized boolean register(Closeable closeable) {
        if (this.closed) {
            closeQuietly(closeable);
            return false;
        }
        this.activeConnection = closeable;
        return true;
    }

    synchronized boolean clear(Closeable closeable) {
        if (this.activeConnection != closeable) {
            return false;
        }
        this.activeConnection = null;
        return true;
    }

    synchronized boolean isClosed() {
        return this.closed;
    }

    void close() {
        synchronized (this) {
            if (this.closed) {
                return;
            }
            this.closed = true;
            Closeable closeable = this.activeConnection;
            this.activeConnection = null;
            closeQuietly(closeable);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable unused) {
        }
    }
}
