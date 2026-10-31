package com.android.support;

/* loaded from: classes.dex */
final class JccSessionResponsePolicy {
    private static final long MAX_SERVER_CLOCK_LEAD_SECONDS = 120;
    private static final long SESSION_TTL_SECONDS = 900;

    private JccSessionResponsePolicy() {
    }

    static boolean isAcceptable(String str, long j, long j2) {
        return "Bearer".equals(str) && j > j2 && j - j2 <= 1020;
    }
}
