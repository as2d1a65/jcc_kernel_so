package com.android.support;

/* loaded from: classes.dex */
final class BoardOverlayMath {
    static int positionForProgress(int i, int i2) {
        float f;
        float f2;
        if (i <= 0) {
            f2 = -0.5f;
        } else {
            if (i < 100) {
                if (i <= 50) {
                    f = i2;
                    f2 = (i * 0.02f) - 0.5f;
                } else {
                    f = i2;
                    f2 = ((i - 50) * 0.04f) + 0.5f;
                }
                return (int) (f * f2);
            }
            f2 = 2.5f;
        }
        f = i2;
        return (int) (f * f2);
    }

    private BoardOverlayMath() {
    }

    static float scaleForProgress(int i) {
        return Math.max(0.1f, i / 50.0f);
    }

    static int clampVisiblePosition(int i, int i2, int i3, int i4) {
        if (i3 <= 0) {
            return i;
        }
        int iMax = Math.max(1, i2);
        int iMax2 = Math.max(1, Math.min(i4, iMax));
        int i5 = iMax2 - iMax;
        int i6 = i3 - iMax2;
        return i6 < i5 ? i5 : Math.max(i5, Math.min(i6, i));
    }
}
