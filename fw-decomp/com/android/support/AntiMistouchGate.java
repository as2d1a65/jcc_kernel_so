package com.android.support;

/* loaded from: classes.dex */
final class AntiMistouchGate {
    static final int ARM = 1;
    static final long CONFIRM_WINDOW_MS = 3000;
    static final int EXECUTE = 0;

    static int decide(boolean z, long j, long j2) {
        if (z) {
            return (j > 0 && j2 >= j && j2 - j <= CONFIRM_WINDOW_MS) ? 0 : 1;
        }
        return 0;
    }

    private AntiMistouchGate() {
    }
}
