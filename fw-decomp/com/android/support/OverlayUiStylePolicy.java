package com.android.support;

/* loaded from: classes.dex */
public final class OverlayUiStylePolicy {
    public static final int AVATAR_RADIUS_DP = 12;
    public static final int CARD_RADIUS_DP = 16;
    public static final int CONTROL_RADIUS_DP = 14;
    public static final long ENTER_DURATION_MS = 450;
    public static final int ENTER_OFFSET_DP = 14;
    public static final float ENTER_START_SCALE = 0.98f;
    public static final long EXIT_DURATION_MS = 150;
    public static final float EXIT_END_SCALE = 0.96f;
    public static final int EXIT_OFFSET_DP = 10;
    public static final long LIST_ENTER_MS = 260;
    public static final int LIST_ENTER_OFFSET_DP = 5;
    public static final long LIST_STAGGER_MS = 45;
    public static final int MAX_STAGGER_INDEX = 6;
    public static final int OUTER_RADIUS_DP = 20;
    public static final long PRESS_IN_MS = 90;
    public static final long PRESS_OUT_MS = 200;
    public static final float PRESS_SCALE = 0.965f;
    public static final long VALUE_PULSE_MS = 220;
    public static final float VALUE_PULSE_SCALE = 0.97f;

    public static long listEnterDelayMs(int i) {
        return Math.max(0, Math.min(6, i)) * 45;
    }

    public static boolean shouldAnimateIdentitySet(String str, String str2) {
        return (str2 == null || str2.equals(str)) ? false : true;
    }

    private OverlayUiStylePolicy() {
    }
}
