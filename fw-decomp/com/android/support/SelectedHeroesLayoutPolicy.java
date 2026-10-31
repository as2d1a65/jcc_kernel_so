package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
public final class SelectedHeroesLayoutPolicy {
    public static boolean rowCapReached(int i, int i2, int i3) {
        return i > 0 && i3 >= i * i2;
    }

    private SelectedHeroesLayoutPolicy() {
    }

    public static String snapshot(int i, int i2, int i3, List<HeroData> list) {
        StringBuilder sb = new StringBuilder("L");
        sb.append(i).append("C").append(i2).append("R").append(i3).append("|");
        if (list != null) {
            for (HeroData heroData : list) {
                if (heroData != null) {
                    sb.append(heroData.heroId).append(":").append(heroData.currentCount).append(";");
                }
            }
        }
        return sb.toString();
    }

    public static String structureSnapshot(int i, int i2, int i3, List<HeroData> list) {
        StringBuilder sb = new StringBuilder("L");
        sb.append(i).append("C").append(i2).append("R").append(i3).append("|");
        if (list != null) {
            for (HeroData heroData : list) {
                if (heroData != null) {
                    sb.append(heroData.heroId).append(';');
                }
            }
        }
        return sb.toString();
    }

    public static int cappedGroupSize(int i, int i2, int i3, int i4) {
        return i2 > 0 ? Math.min(i, (i2 * i3) - i4) : i;
    }

    public static int groupRowCount(int i, int i2) {
        return (int) Math.ceil(i / Math.max(1, i2));
    }

    public static int groupColCount(int i, int i2) {
        return (int) Math.ceil(i / Math.max(1, i2));
    }

    public static int remainingVerticalCols(int i, int i2, int i3) {
        if (i <= 0) {
            return Integer.MAX_VALUE;
        }
        return i - ((int) Math.ceil(i3 / Math.max(1, i2)));
    }

    public static int cappedVerticalGroupSize(int i, int i2, int i3) {
        return Math.min(i, i2 * Math.max(1, i3));
    }
}
