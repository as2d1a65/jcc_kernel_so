package com.android.support;

/* loaded from: classes.dex */
public final class HeroPoolCountTier {
    public static final int PLENTIFUL = 2;
    public static final int SCARCE = 1;
    public static final int SOLD_OUT = 0;

    public static boolean soldOut(int i) {
        return i <= 0;
    }

    private HeroPoolCountTier() {
    }

    public static int of(int i, int i2) {
        if (soldOut(i)) {
            return 0;
        }
        return ((double) i) <= ((double) i2) * 0.3d ? 1 : 2;
    }
}
