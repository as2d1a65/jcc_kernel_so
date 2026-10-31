package com.android.support;

/* loaded from: classes.dex */
final class SpecifiedPageFitPolicy {
    static final int MAX_EXPLAINER_LINES = 6;
    static final int MIN_TARGET_CARD_DP = 52;

    static boolean canShowRecastSteps(int i) {
        return i >= 3;
    }

    static boolean canShowRecastWarning(int i) {
        return i >= 4;
    }

    private SpecifiedPageFitPolicy() {
    }

    static int explainerLines(int i, int i2, int i3, int i4, float f) {
        if (i4 <= 0) {
            return 0;
        }
        if (f <= 0.0f || Float.isNaN(f)) {
            f = 1.0f;
        }
        int iRound = ((i - i2) - i3) - Math.round(f * 52.0f);
        if (iRound < i4) {
            return 0;
        }
        return Math.min(6, iRound / i4);
    }
}
