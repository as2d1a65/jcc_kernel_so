package com.android.support;

import android.content.SharedPreferences;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
final class RequiredFeatureDefaultsMigration {
    private static final Map<String, Boolean> BOOLEAN_VALUES;
    static final int CURRENT_VERSION = 1;
    private static final Map<String, Integer> INT_VALUES;
    static final int MODE_NATIVE = 2;
    static final int OPPONENT_DISPLAY_TEXT = 1;
    static final int RANK_COST_4_ONLY = 8;
    static final int RANK_THRESHOLD = 6;

    static boolean shouldApply(int i) {
        return i < 1;
    }

    static {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_SHOW_HERO_AVATAR, true);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_SCREEN_SECURE, true);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_HEX_ALWAYS_SHOW, true);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_VISIBLE, true);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_SHOP_NATIVE_LABEL, true);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_ENABLED, false);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_BOARD_OVERLAY_ENABLED, false);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_OPPONENT_HIGHLIGHT, true);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_OPPONENT_SHOW_BOX, false);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_OPPONENT_SHOW_TEXT, true);
        linkedHashMap.put(FloatingWindowPrefsKeys.KEY_RANK_WARNING_ENABLED, true);
        BOOLEAN_VALUES = Collections.unmodifiableMap(linkedHashMap);
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        linkedHashMap2.put(FloatingWindowPrefsKeys.KEY_SHOP_REMAIN_MODE, 2);
        linkedHashMap2.put(FloatingWindowPrefsKeys.KEY_BOARD_PLACEHOLDER_MODE, 2);
        linkedHashMap2.put(FloatingWindowPrefsKeys.KEY_OPP_PREDICT_SOURCE, 2);
        linkedHashMap2.put(FloatingWindowPrefsKeys.KEY_OPPONENT_PREDICT_STYLE, 2);
        linkedHashMap2.put(FloatingWindowPrefsKeys.KEY_OPPONENT_DISPLAY_MODE, 1);
        linkedHashMap2.put(FloatingWindowPrefsKeys.KEY_RANK_WARNING_THRESHOLD, 6);
        linkedHashMap2.put(FloatingWindowPrefsKeys.KEY_VOICE_RANK_THRESHOLD, 6);
        linkedHashMap2.put(FloatingWindowPrefsKeys.KEY_RANK_COST_FILTER, 8);
        linkedHashMap2.put(FloatingWindowPrefsKeys.KEY_VOICE_RANK_COST_FILTER, 8);
        INT_VALUES = Collections.unmodifiableMap(linkedHashMap2);
    }

    private RequiredFeatureDefaultsMigration() {
    }

    static boolean apply(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null || !shouldApply(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_REQUIRED_FEATURE_DEFAULTS_MIGRATION_VERSION, 0))) {
            return false;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        for (Map.Entry<String, Boolean> entry : BOOLEAN_VALUES.entrySet()) {
            editorEdit.putBoolean(entry.getKey(), entry.getValue().booleanValue());
        }
        for (Map.Entry<String, Integer> entry2 : INT_VALUES.entrySet()) {
            editorEdit.putInt(entry2.getKey(), entry2.getValue().intValue());
        }
        editorEdit.putInt(FloatingWindowPrefsKeys.KEY_REQUIRED_FEATURE_DEFAULTS_MIGRATION_VERSION, 1);
        return editorEdit.commit();
    }

    static Map<String, Boolean> booleanValuesForTest() {
        return BOOLEAN_VALUES;
    }

    static Map<String, Integer> intValuesForTest() {
        return INT_VALUES;
    }
}
