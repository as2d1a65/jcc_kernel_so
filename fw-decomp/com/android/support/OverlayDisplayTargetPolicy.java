package com.android.support;

/* loaded from: classes.dex */
final class OverlayDisplayTargetPolicy {
    static final int NO_TARGET = -1;

    static int afterDisplayRemoved(int i, int i2, int i3) {
        if (i == i2 && i3 >= 0 && i3 != i) {
            return i3;
        }
        return -1;
    }

    static boolean shouldRepairSameDisplay(int i, int i2, boolean z, boolean z2) {
        return (i == i2 && z && !z2) ? false : true;
    }

    private OverlayDisplayTargetPolicy() {
    }
}
