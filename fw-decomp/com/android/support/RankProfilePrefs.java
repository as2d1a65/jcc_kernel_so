package com.android.support;

/* loaded from: classes.dex */
final class RankProfilePrefs {
    private RankProfilePrefs() {
    }

    static String[] rankLayoutProfiles() {
        return new String[]{"phone_portrait", "phone_landscape", "tablet", "emulator_desktop"};
    }

    static String[] rankProfileBaseKeys() {
        return new String[]{FloatingWindowPrefsKeys.KEY_RANK_INFO_SIZE, FloatingWindowPrefsKeys.KEY_RANK_HERO_SIZE, FloatingWindowPrefsKeys.KEY_INFO_OFFSET_X, FloatingWindowPrefsKeys.KEY_RANK_HERO_OFFSET_X, FloatingWindowPrefsKeys.KEY_ATTACK_ICON_SIZE};
    }

    static int[] rankProfileDefaults() {
        return new int[]{85, 85, 0, 0, 100};
    }
}
