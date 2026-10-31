package com.android.support;

/* loaded from: classes.dex */
final class ShopSlotOverlayApplyPolicy {
    private ShopSlotOverlayApplyPolicy() {
    }

    static boolean shouldPaint(boolean z, boolean z2, boolean z3, boolean z4) {
        return OverlaySessionPaintPolicy.shouldPaint(z, z2) && z3 && z4;
    }
}
