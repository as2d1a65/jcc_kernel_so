package com.android.support;

/* loaded from: classes.dex */
final class VerifyWindowSpec {
    final boolean compact;
    final int heightPx;
    final int widthPx;

    VerifyWindowSpec(int i, int i2, boolean z) {
        this.widthPx = i;
        this.heightPx = i2;
        this.compact = z;
    }

    static boolean useColumns(int i, float f, float f2) {
        float fMax = 1.0f;
        if (f <= 0.0f || !Float.isFinite(f)) {
            f = 1.0f;
        }
        if (f2 > 0.0f && Float.isFinite(f2)) {
            fMax = Math.max(1.0f, f2);
        }
        return (((float) i) / f) / fMax >= 600.0f;
    }

    static VerifyWindowSpec adaptive(int i, int i2, float f, float f2) {
        if (f <= 0.0f || !Float.isFinite(f)) {
            f = 1.0f;
        }
        return measure(Math.max(1, i), Math.max(1, i2), f2, Math.round(280.0f * f), Math.round((((float) i) / f >= 680.0f ? 880 : 480) * f), Math.round((i > i2 ? 600 : 720) * f), Math.round(180.0f * f), Math.round(f * 430.0f));
    }

    static VerifyWindowSpec measure(int i, int i2, float f, int i3, int i4, int i5, int i6, int i7) {
        int iMax = Math.max(i3, Math.min(i4, Math.round(i * 0.92f)));
        if (iMax <= i) {
            i = iMax;
        }
        int iMin = Math.min(i5, Math.round(i2 * 0.94f));
        int iMin2 = Math.min(i2, i6);
        if (iMin < iMin2) {
            iMin = iMin2;
        }
        if (f <= 0.0f || !Float.isFinite(f)) {
            f = 1.0f;
        }
        return new VerifyWindowSpec(i, iMin, Math.round(((float) iMin) / Math.max(1.0f, f)) < i7);
    }
}
