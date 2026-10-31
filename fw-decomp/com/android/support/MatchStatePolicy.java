package com.android.support;

/* loaded from: classes.dex */
final class MatchStatePolicy {
    static boolean scoped(String str) {
        if (str != null) {
            return str.startsWith("update") || str.equals(IpcV2PushMethods.HEX_SPECIFIED_STATUS) || str.startsWith("recast.");
        }
        return false;
    }

    static boolean accepts(String str, String str2) {
        return str != null && str.length() == 22 && str.equals(str2);
    }

    private MatchStatePolicy() {
    }
}
