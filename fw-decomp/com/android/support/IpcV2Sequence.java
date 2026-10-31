package com.android.support;

/* loaded from: classes.dex */
public final class IpcV2Sequence {
    private long lastAccepted;

    public IpcV2Sequence() {
        this(0L);
    }

    IpcV2Sequence(long j) {
        if (j < 0) {
            throw new IllegalArgumentException("sequence cannot be negative");
        }
        this.lastAccepted = j;
    }

    public synchronized boolean accept(long j) {
        long j2 = this.lastAccepted;
        if (j2 != Long.MAX_VALUE && j == j2 + 1) {
            this.lastAccepted = j;
            return true;
        }
        return false;
    }

    public synchronized long lastAccepted() {
        return this.lastAccepted;
    }
}
