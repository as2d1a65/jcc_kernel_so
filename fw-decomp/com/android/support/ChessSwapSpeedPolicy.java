package com.android.support;

/* loaded from: classes.dex */
public final class ChessSwapSpeedPolicy {
    public static final int MAX_PROGRESS = 12;
    public static final int MIN_FRAMES = 2;

    private ChessSwapSpeedPolicy() {
    }

    public static int progressFromFrames(int i) {
        return Math.max(0, Math.min(12, i - 2));
    }

    public static int framesFromProgress(int i) {
        return Math.max(0, Math.min(12, i)) + 2;
    }

    public static String tagForFrames(int i) {
        if (i <= 3) {
            return "飞快";
        }
        if (i <= 6) {
            return "推荐";
        }
        if (i <= 10) {
            return "稳一点";
        }
        return "慢悠悠";
    }

    public static String valueLabel(int i) {
        return String.format("%s · %d帧 (~%.0fms)", tagForFrames(i), Integer.valueOf(i), Float.valueOf((i * 1000.0f) / 60.0f));
    }
}
