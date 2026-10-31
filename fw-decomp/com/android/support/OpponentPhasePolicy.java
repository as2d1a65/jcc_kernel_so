package com.android.support;

/* loaded from: classes.dex */
final class OpponentPhasePolicy {
    static boolean isCombat(int i) {
        return i >= 5 && i <= 11;
    }

    static boolean isPlanning(int i) {
        return i >= 1 && i <= 4;
    }

    private OpponentPhasePolicy() {
    }

    static boolean shouldShowTextOverlay(int i, boolean z) {
        return (isCombat(i) && z) ? false : true;
    }
}
