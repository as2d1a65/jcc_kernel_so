package com.android.support;

import android.content.SharedPreferences;

/* loaded from: classes.dex */
final class AllConfigPersistence {
    private AllConfigPersistence() {
    }

    static final class Snapshot {
        int attackIconOffsetX;
        int attackIconOffsetY;
        int attackIconSizePct;
        boolean boardBenchHidden;
        boolean boardOverlayEnabled;
        int boardScaleProgress;
        int heroDisplayColumns;
        int heroDisplayLayout;
        int heroDisplayRows;
        int infoOffsetX;
        boolean nativeAttackIconEnabled;
        int nativeAttackIconOffsetX;
        int nativeAttackIconOffsetY;
        int nativeAttackIconSizePct;
        boolean opponentAttackIcon;
        int opponentDisplayMode;
        boolean opponentHighlightEnabled;
        boolean opponentShowBox;
        boolean opponentShowText;
        int opponentTextPosXProgress;
        int opponentTextPosYProgress;
        int opponentTextSizeProgress;
        float overlayAlpha;
        int rankCostFilter;
        int rankHeroOffsetX;
        int rankHeroSizePercent;
        boolean rankHideBoxes;
        int rankInfoSizePercent;
        String rankLayoutProfile = "";
        boolean rankWarningEnabled;
        int rankWarningOpacityPercent;
        int rankWarningThreshold;
        float selectedHeroesScale;
        int shopSlotOffsetX;
        int shopSlotOffsetY;
        int shopSlotSpacing;
        boolean showAllMatchups;
        boolean voiceBannerEnabled;
        int voiceBannerXPercent;
        int voiceBannerYPercent;
        boolean voiceEnabled;
        int voicePitch;
        boolean voiceRankAlertEnabled;
        int voiceRankCostMask;
        int voiceRankThreshold;
        int voiceSpeed;
        int voiceVolume;

        Snapshot() {
        }
    }

    static void write(SharedPreferences.Editor editor, Snapshot snapshot) {
        editor.putFloat(FloatingWindowPrefsKeys.KEY_SELECTED_HEROES_SCALE, snapshot.selectedHeroesScale);
        editor.putInt(FloatingWindowPrefsKeys.KEY_HERO_DISPLAY_LAYOUT, snapshot.heroDisplayLayout);
        editor.putInt(FloatingWindowPrefsKeys.KEY_HERO_DISPLAY_COLUMNS, snapshot.heroDisplayColumns);
        editor.putInt(FloatingWindowPrefsKeys.KEY_HERO_DISPLAY_ROWS, snapshot.heroDisplayRows);
        editor.putInt(FloatingWindowPrefsKeys.KEY_OVERLAY_ALPHA, (int) (snapshot.overlayAlpha * 100.0f));
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_BOARD_OVERLAY_ENABLED, snapshot.boardOverlayEnabled);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_BOARD_BENCH_HIDDEN, snapshot.boardBenchHidden);
        editor.putInt(FloatingWindowPrefsKeys.KEY_BOARD_OVERLAY_SCALE, snapshot.boardScaleProgress);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_RANK_WARNING_ENABLED, snapshot.rankWarningEnabled);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_RANK_HIDE_BOXES, snapshot.rankHideBoxes);
        editor.remove(FloatingWindowPrefsKeys.KEY_RANK_BOX_WIDTH);
        editor.remove(FloatingWindowPrefsKeys.KEY_RANK_BOX_HEIGHT);
        editor.remove(FloatingWindowPrefsKeys.KEY_RANK_BOX_SPACING);
        editor.remove(FloatingWindowPrefsKeys.KEY_RANK_BOX_POS_X);
        editor.remove(FloatingWindowPrefsKeys.KEY_RANK_BOX_POS_Y);
        editor.putInt(FloatingWindowPrefsKeys.KEY_RANK_WARNING_THRESHOLD, snapshot.rankWarningThreshold);
        editor.putInt(FloatingWindowPrefsKeys.KEY_RANK_WARNING_OPACITY, snapshot.rankWarningOpacityPercent);
        if (!snapshot.rankLayoutProfile.isEmpty()) {
            editor.putInt(FloatingWindowPrefsKeys.profilePreferenceKey(FloatingWindowPrefsKeys.KEY_RANK_HERO_SIZE, snapshot.rankLayoutProfile), snapshot.rankHeroSizePercent);
            editor.putInt(FloatingWindowPrefsKeys.profilePreferenceKey(FloatingWindowPrefsKeys.KEY_RANK_INFO_SIZE, snapshot.rankLayoutProfile), snapshot.rankInfoSizePercent);
            editor.putInt(FloatingWindowPrefsKeys.profilePreferenceKey(FloatingWindowPrefsKeys.KEY_INFO_OFFSET_X, snapshot.rankLayoutProfile), snapshot.infoOffsetX);
            editor.putInt(FloatingWindowPrefsKeys.profilePreferenceKey(FloatingWindowPrefsKeys.KEY_RANK_HERO_OFFSET_X, snapshot.rankLayoutProfile), snapshot.rankHeroOffsetX);
        }
        editor.putInt(FloatingWindowPrefsKeys.KEY_RANK_COST_FILTER, snapshot.rankCostFilter);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_HIGHLIGHT, snapshot.opponentHighlightEnabled);
        editor.putInt(FloatingWindowPrefsKeys.KEY_OPPONENT_DISPLAY_MODE, snapshot.opponentDisplayMode);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_SHOW_BOX, snapshot.opponentShowBox);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_SHOW_TEXT, snapshot.opponentShowText);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_ATTACK_ICON, snapshot.opponentAttackIcon);
        editor.putInt(FloatingWindowPrefsKeys.KEY_ATTACK_ICON_OFFSET_X, snapshot.attackIconOffsetX);
        editor.putInt(FloatingWindowPrefsKeys.KEY_ATTACK_ICON_OFFSET_Y, snapshot.attackIconOffsetY);
        if (!snapshot.rankLayoutProfile.isEmpty()) {
            editor.putInt(FloatingWindowPrefsKeys.profilePreferenceKey(FloatingWindowPrefsKeys.KEY_ATTACK_ICON_SIZE, snapshot.rankLayoutProfile), snapshot.attackIconSizePct);
        }
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_NATIVE_ATTACK_ICON_ENABLED, snapshot.nativeAttackIconEnabled);
        editor.putInt(FloatingWindowPrefsKeys.KEY_NATIVE_ATTACK_ICON_OFFSET_X, snapshot.nativeAttackIconOffsetX);
        editor.putInt(FloatingWindowPrefsKeys.KEY_NATIVE_ATTACK_ICON_OFFSET_Y, snapshot.nativeAttackIconOffsetY);
        editor.putInt(FloatingWindowPrefsKeys.KEY_NATIVE_ATTACK_ICON_SIZE, snapshot.nativeAttackIconSizePct);
        editor.putInt(FloatingWindowPrefsKeys.KEY_OPPONENT_TEXT_POS_X, snapshot.opponentTextPosXProgress);
        editor.putInt(FloatingWindowPrefsKeys.KEY_OPPONENT_TEXT_POS_Y, snapshot.opponentTextPosYProgress);
        editor.putInt(FloatingWindowPrefsKeys.KEY_OPPONENT_TEXT_SIZE, snapshot.opponentTextSizeProgress);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_SHOW_ALL_MATCHUPS, snapshot.showAllMatchups);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_VOICE_ENABLED, snapshot.voiceEnabled);
        editor.putInt(FloatingWindowPrefsKeys.KEY_VOICE_SPEED, snapshot.voiceSpeed);
        editor.putInt(FloatingWindowPrefsKeys.KEY_VOICE_VOLUME, snapshot.voiceVolume);
        editor.putInt(FloatingWindowPrefsKeys.KEY_VOICE_PITCH, snapshot.voicePitch);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_VOICE_RANK_ENABLED, snapshot.voiceRankAlertEnabled);
        editor.putInt(FloatingWindowPrefsKeys.KEY_VOICE_RANK_THRESHOLD, snapshot.voiceRankThreshold);
        editor.putInt(FloatingWindowPrefsKeys.KEY_VOICE_RANK_COST_FILTER, snapshot.voiceRankCostMask);
        editor.putBoolean(FloatingWindowPrefsKeys.KEY_VOICE_BANNER_ENABLED, snapshot.voiceBannerEnabled);
        editor.putInt(FloatingWindowPrefsKeys.KEY_VOICE_BANNER_POS_X_PERCENT, snapshot.voiceBannerXPercent);
        editor.putInt(FloatingWindowPrefsKeys.KEY_VOICE_BANNER_POS_Y_PERCENT, snapshot.voiceBannerYPercent);
        editor.putInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_OFFSET_X, snapshot.shopSlotOffsetX);
        editor.putInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_OFFSET_Y, snapshot.shopSlotOffsetY);
        editor.putInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_SPACING, snapshot.shopSlotSpacing);
    }
}
