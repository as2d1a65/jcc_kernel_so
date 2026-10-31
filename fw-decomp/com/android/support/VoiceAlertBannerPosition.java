package com.android.support;

/* loaded from: classes.dex */
final class VoiceAlertBannerPosition {
    static final int DEFAULT_X_PERCENT = 50;
    static final int DEFAULT_Y_PERCENT = 35;

    private VoiceAlertBannerPosition() {
    }

    static int clampPercent(int i) {
        return Math.max(0, Math.min(100, i));
    }

    static PixelPosition calculate(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        int iMax = Math.max(0, i7) + i;
        int iMax2 = Math.max(0, i7) + i2;
        return new PixelPosition(Math.max(iMax, Math.min(Math.max(iMax, (i3 - i7) - Math.max(0, i5)), (i + Math.round(((i3 - i) * clampPercent(i8)) / 100.0f)) - (i5 / 2))), Math.max(iMax2, Math.min(Math.max(iMax2, (i4 - i7) - Math.max(0, i6)), (i2 + Math.round(((i4 - i2) * clampPercent(i9)) / 100.0f)) - (i6 / 2))));
    }

    static final class PixelPosition {
        final int x;
        final int y;

        PixelPosition(int i, int i2) {
            this.x = i;
            this.y = i2;
        }
    }
}
