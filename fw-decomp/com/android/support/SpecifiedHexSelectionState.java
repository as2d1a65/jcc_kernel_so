package com.android.support;

/* loaded from: classes.dex */
final class SpecifiedHexSelectionState {
    private static final int STAGE_COUNT = 3;

    static boolean canAddCurrentQualityTarget(int i, int i2) {
        return i >= 0 && i2 > 0 && i < i2;
    }

    static boolean isCurrentQuality(int i, int i2) {
        return i >= 1 && i <= 3 && i2 >= 1 && i2 <= 3 && i == i2;
    }

    static int runtimeSubmitStage(int i) {
        if (i < 0 || i >= 3) {
            return -1;
        }
        return i;
    }

    private SpecifiedHexSelectionState() {
    }

    static int activeStageIndex(int i, int i2) {
        return (i < 0 || i >= 3) ? clampStage(i2) : i;
    }

    static String armContextKey(String str, int i, int i2) {
        if (!isActiveGeneration(str) || i < 0 || i >= 3 || i2 < 1 || i2 > 3) {
            return "";
        }
        return str + "|" + i + "|" + i2;
    }

    static boolean shouldAutoArmContext(String str, String str2, String str3, String str4) {
        return (!isActiveGeneration(str) || str2 == null || str2.isEmpty() || str2.equals(str3) || str.equals(str4)) ? false : true;
    }

    static boolean isCurrentResponse(String str, String str2) {
        return isActiveGeneration(str) && str.equals(str2);
    }

    static String retainManualCancellation(String str, String str2) {
        return ((!isActiveGeneration(str2) || str2.equals(str)) && str != null) ? str : "";
    }

    static boolean shouldRearmAfterRestore(String str, String str2, String str3) {
        return isRestoreCode(str) && isActiveGeneration(str2) && !str2.equals(str3);
    }

    static boolean isNewRestore(String str, long j, long j2, long j3, long j4) {
        if (!isRestoreCode(str) || j <= 0 || j2 <= 0) {
            return false;
        }
        return (j == j3 && j2 == j4) ? false : true;
    }

    private static boolean isRestoreCode(String str) {
        return "session_restored".equals(str) || "session_changed".equals(str);
    }

    static boolean shouldUnlockAfterSubmitFailure(String str) {
        return "not_ready".equals(str) || "not_connected".equals(str) || "busy".equals(str) || "request_timeout_before_write".equals(str) || "response_timeout".equals(str) || "session_replaced".equals(str) || "peer_disconnected_after_write".equals(str);
    }

    private static boolean isActiveGeneration(String str) {
        return str != null && str.length() == 22;
    }

    private static int clampStage(int i) {
        if (i < 0) {
            return 0;
        }
        return Math.min(i, 2);
    }
}
