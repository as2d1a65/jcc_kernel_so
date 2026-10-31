package com.android.support;

import android.content.SharedPreferences;

/* loaded from: classes.dex */
final class SellRuleText {
    static final int COST_MASK_ALL = 31;
    static final boolean KEEP_AUTO_PICK_DEFAULT = true;
    static final int SCOPE_ALL = 3;
    static final int SCOPE_BATTLE = 2;
    static final int SCOPE_BENCH = 1;

    static int normalizeCostMask(int i) {
        return i & 31;
    }

    static int normalizeScope(int i) {
        if (i < 1 || i > 3) {
            return 3;
        }
        return i;
    }

    static int normalizeSwapCostFilter(int i) {
        if (i < 0 || i > 5) {
            return 0;
        }
        return i;
    }

    private SellRuleText() {
    }

    static void ensureKeepAutoPickDefaultV2(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null || sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SELL_KEEP_AUTO_PICK_DEFAULT_V2, false)) {
            return;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        if (!sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SELL_KEEP_AUTO_PICK, false)) {
            editorEdit.putBoolean(FloatingWindowPrefsKeys.KEY_SELL_KEEP_AUTO_PICK, true);
        }
        editorEdit.putBoolean(FloatingWindowPrefsKeys.KEY_SELL_KEEP_AUTO_PICK_DEFAULT_V2, true).apply();
    }

    static boolean costSelected(int i, int i2) {
        return i2 >= 1 && i2 <= 5 && (normalizeCostMask(i) & (1 << (i2 - 1))) != 0;
    }

    static int toggleCost(int i, int i2) {
        if (i2 < 1 || i2 > 5) {
            return normalizeCostMask(i);
        }
        return normalizeCostMask(i ^ (1 << (i2 - 1)));
    }

    static String describeScope(int i) {
        int iNormalizeScope = normalizeScope(i);
        if (iNormalizeScope == 1) {
            return "备战席";
        }
        return iNormalizeScope == 2 ? "场上" : "全部";
    }

    static String describeCostMask(int i) {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 1; i2 <= 5; i2++) {
            if (costSelected(i, i2)) {
                if (sb.length() > 0) {
                    sb.append('/');
                }
                sb.append(i2);
            }
        }
        return sb.length() == 0 ? "全费用" : ((Object) sb) + "费";
    }

    static String summary(int i, int i2) {
        return "当前规则：" + describeScope(i) + " · " + describeCostMask(i2);
    }

    static String describeSwapCostFilter(int i) {
        int iNormalizeSwapCostFilter = normalizeSwapCostFilter(i);
        return iNormalizeSwapCostFilter == 0 ? "全部棋子" : iNormalizeSwapCostFilter + " 费棋子";
    }
}
