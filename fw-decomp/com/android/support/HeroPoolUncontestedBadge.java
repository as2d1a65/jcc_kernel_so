package com.android.support;

/* loaded from: classes.dex */
public final class HeroPoolUncontestedBadge {
    public static final int MAX_COST = 5;
    public static final int MIN_COST = 4;

    public static boolean shouldShow(int i, int i2, int i3) {
        return i >= 4 && i <= 5 && i3 > 0 && i2 >= i3;
    }

    private HeroPoolUncontestedBadge() {
    }
}
