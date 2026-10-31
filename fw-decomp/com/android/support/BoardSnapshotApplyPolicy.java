package com.android.support;

/* loaded from: classes.dex */
final class BoardSnapshotApplyPolicy {
    static boolean shouldCachePositions(boolean z, boolean z2, boolean z3) {
        return (z || z2) && z3;
    }

    static boolean shouldPaintBoard(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        if (z || (z2 && z3)) {
            return z4 || !z5;
        }
        return false;
    }

    static boolean shouldResetBoardUiTimestamps(boolean z, boolean z2) {
        return (z || z2) ? false : true;
    }

    static boolean shouldUpdateSelector(int i, int i2) {
        return i != i2;
    }

    private BoardSnapshotApplyPolicy() {
    }
}
