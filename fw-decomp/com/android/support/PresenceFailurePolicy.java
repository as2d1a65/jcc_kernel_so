package com.android.support;

/* loaded from: classes.dex */
public final class PresenceFailurePolicy {
    private PresenceFailurePolicy() {
    }

    public static boolean isSessionLoss(String str) {
        return "AUTH_REQUIRED".equals(str) || "TOKEN_INVALID".equals(str) || "TOKEN_EXPIRED".equals(str) || "SESSION_EXPIRED".equals(str) || "SESSION_REVOKED".equals(str);
    }
}
