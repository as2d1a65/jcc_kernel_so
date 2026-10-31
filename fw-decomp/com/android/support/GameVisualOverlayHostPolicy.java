package com.android.support;

/* loaded from: classes.dex */
final class GameVisualOverlayHostPolicy {
    static final int LAYER_BOARD = 1;
    static final int LAYER_BOARD_HERO = 2;
    static final int LAYER_CARD_POOL = 8;
    private static final int LAYER_MASK = 31;
    static final int LAYER_RANK_WARNING = 4;
    static final int LAYER_TRANSIENT = 16;

    static boolean isDetachConfirmed(boolean z, boolean z2) {
        return (z || z2) ? false : true;
    }

    static boolean shouldAttach(int i) {
        return (i & 31) != 0;
    }

    static int transition(int i, int i2, boolean z) {
        int i3 = i & 31;
        return (i2 == 1 || i2 == 2 || i2 == 4 || i2 == 8 || i2 == 16) ? z ? i3 | i2 : i3 & (~i2) : i3;
    }

    private GameVisualOverlayHostPolicy() {
    }

    static double combinedObscuringOpacity(double... dArr) {
        if (dArr == null || dArr.length == 0) {
            return 0.0d;
        }
        double d = 1.0d;
        for (double d2 : dArr) {
            if (!Double.isNaN(d2) && d2 > 0.0d) {
                if (d2 >= 1.0d) {
                    return 1.0d;
                }
                d *= 1.0d - d2;
            }
        }
        return 1.0d - d;
    }

    static double normalizedChildAlpha(double d, double d2) {
        if (Double.isNaN(d) || Double.isNaN(d2) || d <= 0.0d || d2 <= 0.0d) {
            return 0.0d;
        }
        double d3 = d / d2;
        if (d3 >= 1.0d) {
            return 1.0d;
        }
        return d3;
    }
}
