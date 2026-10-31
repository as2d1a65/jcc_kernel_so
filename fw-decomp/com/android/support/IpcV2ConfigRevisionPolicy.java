package com.android.support;

/* loaded from: classes.dex */
final class IpcV2ConfigRevisionPolicy {
    private IpcV2ConfigRevisionPolicy() {
    }

    static long reserveInitial(long j, long j2) {
        if (j < 0 || j2 <= 0) {
            throw new IllegalArgumentException("invalid config revision source");
        }
        if (j == Long.MAX_VALUE) {
            throw new IllegalStateException("config revision exhausted");
        }
        return Math.max(j + 1, j2);
    }
}
