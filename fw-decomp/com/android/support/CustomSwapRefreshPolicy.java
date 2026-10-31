package com.android.support;

/* loaded from: classes.dex */
final class CustomSwapRefreshPolicy {
    static boolean shouldRefresh(boolean z, boolean z2, int i, int i2) {
        return z && z2 && i != i2;
    }

    private CustomSwapRefreshPolicy() {
    }
}
