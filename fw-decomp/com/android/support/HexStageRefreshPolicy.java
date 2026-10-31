package com.android.support;

/* loaded from: classes.dex */
final class HexStageRefreshPolicy {
    static boolean shouldQuery(boolean z, long j, long j2) {
        if (z) {
            return false;
        }
        return j2 < 0 || j - j2 >= 5000;
    }

    HexStageRefreshPolicy() {
    }
}
