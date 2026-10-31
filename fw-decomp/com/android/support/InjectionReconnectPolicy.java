package com.android.support;

/* loaded from: classes.dex */
final class InjectionReconnectPolicy {
    static boolean shouldAutoRecoverRuntime(int i, boolean z, boolean z2) {
        return i > 0 && z && z2;
    }

    static boolean shouldAwaitAuthenticatedRuntime(int i, boolean z, boolean z2, boolean z3, boolean z4) {
        return i > 0 && z && !z2 && !z3 && z4;
    }

    static boolean shouldBlockDuplicateInjection(int i, boolean z, boolean z2, boolean z3) {
        return i > 0 && z && !z2 && z3;
    }

    static boolean shouldReuseRuntime(int i, boolean z, boolean z2, boolean z3, boolean z4) {
        if (i <= 0) {
            return false;
        }
        if (z2) {
            return true;
        }
        return z && z3 && z4;
    }

    private InjectionReconnectPolicy() {
    }

    static boolean shouldReuseRuntime(int i, boolean z, boolean z2, boolean z3) {
        return shouldReuseRuntime(i, z, z2, z3, true);
    }
}
