package com.android.support;

/* loaded from: classes.dex */
final class HexOverlayRefreshPolicy {
    static boolean shouldAccept(int[] iArr, int i, int i2) {
        return (iArr == null || i == 0 || i == i2) ? false : true;
    }

    static boolean shouldClear(int[] iArr, int i, int i2) {
        return (iArr != null || i == 0 || i == i2) ? false : true;
    }

    static boolean shouldPopup(boolean z, boolean z2) {
        return z || !z2;
    }

    private HexOverlayRefreshPolicy() {
    }

    static boolean hasValidLevels(int[] iArr) {
        if (iArr == null) {
            return false;
        }
        int iMin = Math.min(3, iArr.length);
        for (int i = 0; i < iMin; i++) {
            int i2 = iArr[i];
            if (i2 >= 1 && i2 <= 3) {
                return true;
            }
        }
        return false;
    }
}
