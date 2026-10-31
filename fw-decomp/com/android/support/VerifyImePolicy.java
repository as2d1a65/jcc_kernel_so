package com.android.support;

/* loaded from: classes.dex */
final class VerifyImePolicy {

    static final class Decision {
        final boolean applyChrome;
        final boolean collapseChrome;
        final int gravity;
        final int height;
        final boolean shifted;
        final boolean skipLayout;
        final int x;
        final int y;

        Decision(boolean z, boolean z2, boolean z3, boolean z4, int i, int i2, int i3, int i4) {
            this.skipLayout = z;
            this.applyChrome = z2;
            this.collapseChrome = z3;
            this.shifted = z4;
            this.gravity = i;
            this.x = i2;
            this.y = i3;
            this.height = i4;
        }
    }

    private VerifyImePolicy() {
    }

    static Decision decide(int i, int i2, int i3, boolean z, int i4, int i5, int i6, int i7) {
        if (i <= 0) {
            boolean z2 = i2 < i7;
            if (!z) {
                return new Decision(true, true, z2, false, 0, 0, 0, 0);
            }
            return new Decision(false, true, z2, false, 17, 0, 0, i2);
        }
        int iMin = Math.min(i2, Math.max(1, (i4 - i) - (i5 * 2)));
        if (z && Math.abs(i3 - iMin) <= i6) {
            return new Decision(true, false, false, true, 0, 0, 0, 0);
        }
        return new Decision(false, true, iMin < i7, true, 49, 0, i5, iMin);
    }
}
