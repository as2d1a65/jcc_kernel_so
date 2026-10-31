package com.android.support;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class OpponentCardingConfig {
    public static final int ALL_COSTS_MASK = 31;
    public static final int STRATEGY_CONTINUOUS = 1;
    public static final int STRATEGY_SMART = 0;
    private final int costMask;
    private final boolean enabled;
    private final int maxOwnedCopies;
    private final int reserveGold;
    private final int strategy = 1;
    private final int threshold;

    public static int costBit(int i) {
        if (i < 1 || i > 5) {
            return 0;
        }
        return 1 << (i - 1);
    }

    private OpponentCardingConfig(boolean z, int i, int i2, int i3, int i4, int i5) {
        this.enabled = z;
        this.threshold = clamp(i, 3, 9);
        this.costMask = i2 & 31;
        this.reserveGold = clamp(i3, 0, 80);
        this.maxOwnedCopies = clamp(i5, 1, 9);
    }

    public static OpponentCardingConfig defaults() {
        return create(false, 6, 31, 20, 1, 3);
    }

    public static OpponentCardingConfig create(boolean z, int i, int i2, int i3, int i4, int i5) {
        return new OpponentCardingConfig(z, i, i2, i3, i4, i5);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public int getCostMask() {
        return this.costMask;
    }

    public int getReserveGold() {
        return this.reserveGold;
    }

    public int getStrategy() {
        return this.strategy;
    }

    public int getMaxOwnedCopies() {
        return this.maxOwnedCopies;
    }

    public OpponentCardingConfig withEnabled(boolean z) {
        return create(z, this.threshold, this.costMask, this.reserveGold, this.strategy, this.maxOwnedCopies);
    }

    public OpponentCardingConfig withThreshold(int i) {
        return create(this.enabled, i, this.costMask, this.reserveGold, this.strategy, this.maxOwnedCopies);
    }

    public OpponentCardingConfig withCostMask(int i) {
        return create(this.enabled, this.threshold, i, this.reserveGold, this.strategy, this.maxOwnedCopies);
    }

    public OpponentCardingConfig withReserveGold(int i) {
        return create(this.enabled, this.threshold, this.costMask, i, this.strategy, this.maxOwnedCopies);
    }

    public OpponentCardingConfig withStrategy(int i) {
        return create(this.enabled, this.threshold, this.costMask, this.reserveGold, i, this.maxOwnedCopies);
    }

    public OpponentCardingConfig withMaxOwnedCopies(int i) {
        return create(this.enabled, this.threshold, this.costMask, this.reserveGold, this.strategy, i);
    }

    public String toSummary() {
        return "阈值" + this.threshold + " · " + describeCostMask(this.costMask) + " · 留" + this.reserveGold + "金";
    }

    public static String describeCostMask(int i) {
        int i2 = i & 31;
        if (i2 == 31) {
            return "全费用";
        }
        if (i2 == 0) {
            return "未选费用";
        }
        ArrayList arrayList = new ArrayList();
        for (int i3 = 1; i3 <= 5; i3++) {
            if ((costBit(i3) & i2) != 0) {
                arrayList.add(String.valueOf(i3));
            }
        }
        return join(arrayList, "/") + "费";
    }

    private static String join(List<String> list, String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(str);
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    private static int clamp(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i, i3));
    }
}
