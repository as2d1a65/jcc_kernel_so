package com.android.support;

/* loaded from: classes.dex */
final class HexProbePolicy {
    static final long INTERVAL_MS = 5000;

    static boolean hiddenIdle(boolean z, int i, boolean z2) {
        if (i == 0) {
            return z2 || z;
        }
        return false;
    }

    static boolean shouldReadLevels(boolean z, boolean z2, boolean z3) {
        return z && z2 && !z3;
    }

    static boolean shouldRequest(boolean z, boolean z2, boolean z3, boolean z4, long j, long j2) {
        return !z4 && z && z2 && z3 && j - j2 > INTERVAL_MS;
    }

    private HexProbePolicy() {
    }

    static boolean shouldRequest(boolean z, boolean z2, boolean z3, long j, long j2) {
        return shouldRequest(z, z2, z3, false, j, j2);
    }

    static boolean hiddenIdle(boolean z, int i) {
        return hiddenIdle(z, i, false);
    }
}
