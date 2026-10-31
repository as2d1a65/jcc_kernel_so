package com.android.support;

/* loaded from: classes.dex */
final class LeftoverIdleWorkPolicy {
    static boolean shouldClearRetainedPlanningOpponent(boolean z) {
        return z;
    }

    static boolean shouldPublishRankAnchors(boolean z) {
        return !z;
    }

    static boolean shouldReadBoard(boolean z) {
        return !z;
    }

    private LeftoverIdleWorkPolicy() {
    }

    static boolean shouldPostApply(boolean z, boolean z2, Boolean bool) {
        return (z && bool != null && bool.booleanValue() == z2) ? false : true;
    }

    static Boolean nextPostedCppConnected(boolean z, boolean z2, boolean z3, Boolean bool) {
        if (!z3) {
            return bool;
        }
        if (z) {
            return Boolean.valueOf(z2);
        }
        return null;
    }
}
