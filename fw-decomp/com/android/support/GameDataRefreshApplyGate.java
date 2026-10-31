package com.android.support;

/* loaded from: classes.dex */
final class GameDataRefreshApplyGate {
    static boolean shouldApplyOverlays(boolean z, boolean z2) {
        return z && !z2;
    }

    static boolean shouldCheckThreeStarCelebration(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        return z && !z5 && !z6 && z2 && z3 && z4;
    }

    static boolean shouldCheckVoiceAlerts(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7) {
        return !z6 && !z7 && z && z2 && z3 && z4 && z5;
    }

    private GameDataRefreshApplyGate() {
    }

    static boolean shouldCheckVoiceAlerts(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        return shouldCheckVoiceAlerts(z, z2, z3, z4, z5, false);
    }

    static boolean shouldCheckVoiceAlerts(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        return shouldCheckVoiceAlerts(z, z2, z3, z4, z5, z6, false);
    }
}
