package com.android.support;

/* loaded from: classes.dex */
public final class CustomSwapCellVisualPolicy {
    static final int DEFAULT_STROKE = 855693823;
    static final int SELECTED_FILL = 855703474;
    static final int SELECTED_STROKE = -16711758;

    public static int strokeColor(boolean z, int i) {
        return i != 0 ? i : z ? SELECTED_STROKE : DEFAULT_STROKE;
    }

    public static boolean thickStroke(boolean z, boolean z2) {
        return z || z2;
    }

    private CustomSwapCellVisualPolicy() {
    }

    public static int fillColor(int i, int i2, boolean z, int i3) {
        return i3 != 0 ? (16777215 & i3) | 855638016 : z ? SELECTED_FILL : CustomSwapBoardFactory.battlefieldCellFill(i, i2);
    }

    public static int pairColor(int[] iArr, int i) {
        if (iArr == null || iArr.length == 0 || i < 0) {
            return 0;
        }
        return iArr[i % iArr.length];
    }
}
