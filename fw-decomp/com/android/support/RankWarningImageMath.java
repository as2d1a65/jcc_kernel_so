package com.android.support;

/* loaded from: classes.dex */
final class RankWarningImageMath {
    private RankWarningImageMath() {
    }

    static void centerCropBounds(int i, int i2, float f, float f2, int[] iArr) {
        int i3;
        int i4;
        int i5;
        int i6;
        if (iArr == null || iArr.length < 4) {
            throw new IllegalArgumentException("outBounds must contain four entries");
        }
        if (i <= 0 || i2 <= 0 || !hasValidDestination(f, f2)) {
            throw new IllegalArgumentException("source and destination must be positive");
        }
        double d = i;
        double d2 = i2;
        double d3 = d / d2;
        double d4 = f / f2;
        if (d3 > d4) {
            int iMax = Math.max(1, Math.min(i, (int) Math.round(d2 * d4)));
            i5 = (i - iMax) / 2;
            i3 = iMax + i5;
            i4 = i2;
            i6 = 0;
        } else if (d3 < d4) {
            int iMax2 = Math.max(1, Math.min(i2, (int) Math.round(d / d4)));
            i6 = (i2 - iMax2) / 2;
            i4 = iMax2 + i6;
            i3 = i;
            i5 = 0;
        } else {
            i3 = i;
            i4 = i2;
            i5 = 0;
            i6 = 0;
        }
        iArr[0] = i5;
        iArr[1] = i6;
        iArr[2] = i3;
        iArr[3] = i4;
    }

    static boolean hasValidDestination(float f, float f2) {
        return Float.isFinite(f) && Float.isFinite(f2) && f > 0.0f && f2 > 0.0f;
    }
}
