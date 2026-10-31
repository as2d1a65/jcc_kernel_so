package com.android.support;

/* loaded from: classes.dex */
final class AutoRefreshFallbackPolicy {
    static final int DEFAULT_MS = 300;
    static final int MAX_MS = 2000;
    static final int MAX_PROGRESS = 38;
    static final int MIN_MS = 100;
    static final int STEP_MS = 50;

    private AutoRefreshFallbackPolicy() {
    }

    static int normalizeMs(int i) {
        return (Math.round((Math.max(100, Math.min(MAX_MS, i)) - 100) / 50.0f) * 50) + 100;
    }

    static int msFromProgress(int i) {
        return (Math.max(0, Math.min(38, i)) * 50) + 100;
    }

    static int progressFromMs(int i) {
        return (normalizeMs(i) - 100) / 50;
    }

    static String describeMs(int i) {
        return normalizeMs(i) + "ms";
    }
}
