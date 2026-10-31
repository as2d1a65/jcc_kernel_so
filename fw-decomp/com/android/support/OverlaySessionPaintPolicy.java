package com.android.support;

/* loaded from: classes.dex */
final class OverlaySessionPaintPolicy {
    static boolean shouldPaint(boolean z, boolean z2) {
        return z && !z2;
    }

    private OverlaySessionPaintPolicy() {
    }

    static boolean shouldShowSelectedHeroes(boolean z, boolean z2, boolean z3) {
        return z && shouldPaint(z2, z3);
    }

    static boolean shouldShowSelectedHeroes(boolean z, boolean z2, boolean z3, boolean z4) {
        return z4 && shouldShowSelectedHeroes(z, z2, z3);
    }
}
