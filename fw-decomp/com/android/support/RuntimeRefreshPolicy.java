package com.android.support;

/* loaded from: classes.dex */
final class RuntimeRefreshPolicy {
    static final int ACTIVE_DELAY_MS = 200;
    static final int IDLE_DELAY_MS = 1000;

    static boolean boardSnapshotChanged(int i, int i2, int i3, int i4, boolean z) {
        return (!z && i == i3 && i2 == i4) ? false : true;
    }

    static boolean needsLoop(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11, boolean z12) {
        return z || z2 || z3 || z4 || z5 || z6 || z7 || z8 || z9 || z10 || z11 || z12;
    }

    static int nextDelayMs(boolean z, boolean z2) {
        return (z || z2) ? 200 : 1000;
    }

    static boolean rankSnapshotChanged(int i, int i2, int i3, int i4, boolean z) {
        return (!z && i == i3 && i2 == i4) ? false : true;
    }

    private RuntimeRefreshPolicy() {
    }

    static final class Needs {
        final boolean boardOverlay;
        final boolean heroPool;
        final boolean players;
        final boolean rankWarning;
        final boolean selfBoardWatch;

        Needs(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
            this.heroPool = z;
            this.players = z2;
            this.boardOverlay = z3;
            this.rankWarning = z4;
            this.selfBoardWatch = z5;
        }
    }

    static Needs evaluate(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11, boolean z12, boolean z13) {
        return new Needs(z && (z2 || z3 || z4 || z8 || z5), z && z6, z && z7, z && (z8 || z9 || ((z10 && (z11 || z12)) || z5)), z && z13);
    }

    static boolean needsLoop(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11) {
        return needsLoop(z, z2, z3, z4, z5, z6, z7, z8, z9, z10, z11, false);
    }
}
