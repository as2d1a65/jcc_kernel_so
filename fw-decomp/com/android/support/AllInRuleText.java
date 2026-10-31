package com.android.support;

/* loaded from: classes.dex */
final class AllInRuleText {
    static final int ACK_TIMEOUT_MAX = 6000;
    static final int ACK_TIMEOUT_MIN = 2000;
    static final int ACK_TIMEOUT_STEP = 500;
    static final int BLOCKED_SELL_NEXT = 0;
    static final int BLOCKED_STOP = 1;
    static final boolean KEEP_LINEUP_DEFAULT = true;
    static final int MAX_TARGETS_DEFAULT = 3;
    static final int MAX_TARGETS_MAX = 9;
    static final int MAX_TARGETS_MIN = 1;
    static final int MAX_TARGETS_STEP = 1;
    static final int RESERVE_GOLD_DEFAULT = 0;
    static final int RESERVE_GOLD_MAX = 50;
    static final int RESERVE_GOLD_MIN = 0;
    static final int RESERVE_GOLD_STEP = 10;

    private AllInRuleText() {
    }

    static String describeReserveGold(int i) {
        int iNormalizeAllInReserveGold = QuickActionOverlayPolicy.normalizeAllInReserveGold(i);
        return iNormalizeAllInReserveGold == 0 ? "不留金币" : "留" + iNormalizeAllInReserveGold + "金";
    }

    static String describeBlockedAction(int i) {
        return QuickActionOverlayPolicy.normalizeAllInBlockedAction(i) == 1 ? "卡死则停止" : "卡死则卖掉换下一个";
    }

    static String describeCostMask(int i) {
        int iNormalizeAllInCostMask = QuickActionOverlayPolicy.normalizeAllInCostMask(i);
        boolean z = (iNormalizeAllInCostMask & 8) != 0;
        boolean z2 = (iNormalizeAllInCostMask & 16) != 0;
        if (z && z2) {
            return "追4/5费";
        }
        if (z) {
            return "只追4费";
        }
        return "只追5费";
    }

    static String summary(int i, int i2, int i3, int i4) {
        return describeCostMask(i4) + " · 凑" + QuickActionOverlayPolicy.normalizeAllInMaxTargets(i) + "个三星 · " + describeReserveGold(i2) + " · " + describeBlockedAction(i3);
    }

    static String statusLine(boolean z, boolean z2, boolean z3) {
        if (z2) {
            return "正在与游戏核对状态…";
        }
        if (z) {
            return "运行中 · 追满设定的个数后自动停止";
        }
        if (z3) {
            return "未运行 · 用悬浮键或快捷栏的梭哈键开始";
        }
        return "未运行 · 需先进入对局";
    }
}
