package com.android.support;

/* loaded from: classes.dex */
public final class LeftoverReleaseRefreshPolicy {
    static final int FORCE_TIMESTAMP = Integer.MIN_VALUE;

    public static boolean nextLeftoverHold(boolean z, boolean z2, boolean z3) {
        if (z) {
            return true;
        }
        return z2 && !z3;
    }

    public static boolean released(boolean z, boolean z2) {
        return z && !z2;
    }

    public static boolean shouldInvalidateApplyClocks(boolean z, boolean z2) {
        return z && !z2;
    }

    public static boolean shouldParseHeroPool(boolean z, boolean z2, boolean z3, int i, int i2) {
        if (!z || z2) {
            return false;
        }
        return z3 || i != i2;
    }

    private LeftoverReleaseRefreshPolicy() {
    }
}
