package com.android.support;

/* loaded from: classes.dex */
final class VerifyImeInsets {
    private VerifyImeInsets() {
    }

    static int fromImeBottom(int i) {
        return Math.max(0, i);
    }

    static int fromWindowIme(boolean z, int i, int i2, int i3) {
        int iMax = 0;
        if (!z) {
            return 0;
        }
        if (i2 > 0 && i3 > 0) {
            iMax = Math.max(0, i2 - i3);
        }
        return Math.max(fromImeBottom(i), iMax);
    }

    static int fromVisibleFrame(int i, int i2) {
        int i3;
        if (i > 0 && (i3 = i - i2) > i / 5) {
            return i3;
        }
        return 0;
    }
}
