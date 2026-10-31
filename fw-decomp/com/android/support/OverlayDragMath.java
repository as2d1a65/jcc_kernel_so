package com.android.support;

/* loaded from: classes.dex */
final class OverlayDragMath {
    static int resolveX(int i, int i2, boolean z) {
        return z ? i - i2 : i + i2;
    }

    private OverlayDragMath() {
    }

    static boolean exceededSlop(float f, float f2, int i) {
        float f3 = i;
        return Math.abs(f) >= f3 || Math.abs(f2) >= f3;
    }

    static int clampAxis(int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        int i6 = i3 - i4;
        if (i6 < i5) {
            i6 = i5;
        }
        return Math.max(i5, Math.min(i, i6));
    }

    static int clampPrimaryX(int i, int i2, int i3, int i4, int i5, boolean z, int i6) {
        int iMax = Math.max(1, i3);
        int iMax2 = Math.max(1, i2);
        int iMax3 = Math.max(0, Math.min(iMax - 1, i4));
        int iMax4 = Math.max(iMax3 + 1, iMax - Math.max(0, i5));
        int i7 = iMax4 - iMax3;
        int iMax5 = Math.max(1, Math.min(i7, i6));
        long j = iMax3;
        if (iMax2 > i7) {
            j = (j - iMax2) + iMax5;
        }
        long jMax = Math.max(j, Math.min(z ? (iMax - i) - iMax2 : i, iMax2 <= i7 ? iMax4 - iMax2 : iMax4 - iMax5));
        if (z) {
            jMax = (iMax - jMax) - iMax2;
        }
        return (int) jMax;
    }

    static int clampPrimaryY(int i, int i2, int i3, int i4, int i5, int i6) {
        int iMax = Math.max(1, i3);
        int iMax2 = Math.max(0, Math.min(iMax - 1, i4));
        int iMax3 = Math.max(iMax2 + 1, iMax - Math.max(0, i5));
        int i7 = iMax3 - iMax2;
        int iMin = Math.min(Math.max(1, i2), i7);
        if (i2 > i7) {
            iMin = Math.max(1, Math.min(i7, i6));
        }
        return Math.max(iMax2, Math.min(i, iMax3 - iMin));
    }
}
