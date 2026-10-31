package com.android.support;

/* loaded from: classes.dex */
public final class VoiceAlertCostFilter {
    public static final int ALL_COSTS_MASK = 31;

    public static int sanitize(int i) {
        return i & 31;
    }

    private VoiceAlertCostFilter() {
    }

    public static boolean includes(int i, int i2) {
        return i2 >= 1 && i2 <= 5 && (sanitize(i) & (1 << (i2 - 1))) != 0;
    }

    public static int withCost(int i, int i2, boolean z) {
        int iSanitize = sanitize(i);
        if (i2 < 1 || i2 > 5) {
            return iSanitize;
        }
        int i3 = 1 << (i2 - 1);
        return z ? iSanitize | i3 : iSanitize & (~i3);
    }
}
