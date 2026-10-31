package com.android.support;

/* loaded from: classes.dex */
final class ControlPanelMetrics {
    static final float CHROME_HEIGHT_DP = 35.0f;
    static final float MIN_CONTENT_HEIGHT_DP = 248.0f;
    static final float MIN_CONTENT_WIDTH_DP = 244.0f;
    static final float SCREEN_MARGIN_DP = 12.0f;
    static final float SIDEBAR_WIDTH_DP = 44.0f;

    private ControlPanelMetrics() {
    }

    static final class Size {
        final int contentHeightPx;
        final int contentWidthPx;
        final int sidebarWidthPx;

        Size(int i, int i2, int i3) {
            this.contentWidthPx = i;
            this.contentHeightPx = i2;
            this.sidebarWidthPx = i3;
        }
    }

    static Size resolve(String str, int i, int i2, float f, float f2) {
        float f3;
        float f4;
        float fMin;
        float f5;
        if (f <= 0.0f || Float.isNaN(f)) {
            f = 1.0f;
        }
        float fMax = Math.max(1, i) / f;
        float fMax2 = Math.max(1, i2) / f;
        if ("tablet".equals(str)) {
            f4 = 336.0f;
            fMin = 400.0f;
            f3 = 360.0f;
            f5 = 440.0f;
        } else {
            f3 = 344.0f;
            if ("emulator_desktop".equals(str)) {
                fMin = 372.0f;
                f5 = 412.0f;
                f4 = 320.0f;
            } else if ("phone_landscape".equals(str)) {
                f4 = (0.4f * fMax) - SIDEBAR_WIDTH_DP;
                f5 = 300.0f;
                fMin = (0.66f * fMax2) - CHROME_HEIGHT_DP;
                f3 = 320.0f;
            } else {
                f4 = (0.68f * fMax) - SIDEBAR_WIDTH_DP;
                f3 = 300.0f;
                fMin = (0.46f * fMax2) - CHROME_HEIGHT_DP;
                f5 = 344.0f;
            }
        }
        if (f2 > 1.0f && !Float.isNaN(f2)) {
            fMin *= (Math.min(f2 - 1.0f, 0.5f) * 0.5f) + 1.0f;
        }
        float fClamp = clamp(f4, MIN_CONTENT_WIDTH_DP, f3);
        float fClamp2 = clamp(fMin, MIN_CONTENT_HEIGHT_DP, f5);
        return new Size(Math.max(1, Math.round(Math.min(fClamp, Math.max(1.0f, (fMax - 24.0f) - SIDEBAR_WIDTH_DP)) * f)), Math.max(1, Math.round(Math.min(fClamp2, Math.max(1.0f, (fMax2 - 24.0f) - CHROME_HEIGHT_DP)) * f)), Math.max(1, Math.round(f * SIDEBAR_WIDTH_DP)));
    }

    private static float clamp(float f, float f2, float f3) {
        return Float.isNaN(f) ? f2 : Math.max(f2, Math.min(f, Math.max(f2, f3)));
    }
}
