package com.android.support;

import android.content.SharedPreferences;

/* loaded from: classes.dex */
final class QuickActionDockPrefs {
    static final int BUTTON_ALL_IN = 32;
    static final int BUTTON_MASK_ALL = 127;
    static final int BUTTON_MASK_LEGACY_ALL = 63;
    static final int BUTTON_MIRROR = 64;
    static final int BUTTON_REFRESH = 16;
    static final int BUTTON_SELL_ALL = 4;
    static final int BUTTON_SELL_BATTLE = 2;
    static final int BUTTON_SELL_BENCH = 1;
    static final int BUTTON_SWAP = 8;

    private QuickActionDockPrefs() {
    }

    static int buttonMask(SharedPreferences sharedPreferences) {
        return QuickActionOverlayPolicy.migrateVisibleMask(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_BUTTON_MASK, 127), 63, 127);
    }

    static int layout(SharedPreferences sharedPreferences) {
        int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_LAYOUT, 0);
        if (i < 0 || i > 2) {
            return 0;
        }
        return i;
    }

    static boolean comfortable(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_DENSITY, 0) == 1;
    }

    static int controlHeightDp(SharedPreferences sharedPreferences) {
        return QuickActionOverlayPolicy.normalizeControlHeightDp(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_HEIGHT_DP, comfortable(sharedPreferences) ? 52 : 44));
    }

    static int scalePercent(SharedPreferences sharedPreferences) {
        return QuickActionOverlayPolicy.normalizeDockScalePercent(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_SCALE_PERCENT, 70));
    }

    static int gapDp(SharedPreferences sharedPreferences) {
        return QuickActionOverlayPolicy.normalizeActionGapDp(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_GAP_DP, comfortable(sharedPreferences) ? 10 : 4));
    }

    static int opacityPercent(SharedPreferences sharedPreferences) {
        return QuickActionOverlayPolicy.normalizeOpacityPercent(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_OPACITY_PERCENT, 72));
    }

    static boolean positionLocked(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POSITION_LOCKED, false);
    }

    static void ensureCompactAdaptiveV2(SharedPreferences sharedPreferences) {
        if (sharedPreferences != null) {
            if (sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_COMPACT_ADAPTIVE_V2, false)) {
                return;
            }
            boolean z = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_DENSITY, 0) == 1;
            int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_HEIGHT_DP, z ? 52 : 46);
            int i2 = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_GAP_DP, z ? 10 : 8);
            int i3 = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_OPACITY_PERCENT, 96);
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            if (i == 46) {
                editorEdit.putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_HEIGHT_DP, 44);
            }
            if (i2 == 8) {
                editorEdit.putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_GAP_DP, 4);
            }
            if (i3 == 96) {
                editorEdit.putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_OPACITY_PERCENT, 72);
            }
            editorEdit.putBoolean(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_COMPACT_ADAPTIVE_V2, true).apply();
        }
    }

    static void ensureAdaptiveDefaultV3(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null || sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_ADAPTIVE_DEFAULT_V3, false)) {
            return;
        }
        int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_LAYOUT, 0);
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        if (i == 1) {
            editorEdit.putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_LAYOUT, 0);
        }
        editorEdit.putBoolean(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_ADAPTIVE_DEFAULT_V3, true).apply();
    }
}
