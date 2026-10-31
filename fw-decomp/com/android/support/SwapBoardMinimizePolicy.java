package com.android.support;

/* loaded from: classes.dex */
public final class SwapBoardMinimizePolicy {
    public static final int FALLBACK_FULL_X_DP = 60;
    public static final int FALLBACK_FULL_Y_DP = 140;
    public static final int FULL_HEIGHT_DP = 460;
    public static final int FULL_WIDTH_DP = 360;
    public static final int MIN_DOCK_HEIGHT_DP = 34;
    public static final int MIN_DOCK_WIDTH_DP = 120;
    public static final int MIN_WIDTH_DP = 160;

    public static int initialOrigin(boolean z, boolean z2, int i, int i2, int i3) {
        return (z && z2) ? i : z ? i2 : i3;
    }

    public static int minimizedHeightPx(int i, int i2) {
        return i + i2;
    }

    public static boolean shouldApplySavedPos(boolean z, int i, int i2) {
        return (!z || i == Integer.MIN_VALUE || i2 == Integer.MIN_VALUE) ? false : true;
    }

    public static boolean shouldDockToCompanion(boolean z) {
        return !z;
    }

    public static float titleSizeSp(boolean z) {
        return z ? 13.0f : 15.0f;
    }

    private SwapBoardMinimizePolicy() {
    }

    public static String minButtonText(boolean z) {
        return z ? "⤢" : "—";
    }
}
