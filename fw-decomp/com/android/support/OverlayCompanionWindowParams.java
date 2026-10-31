package com.android.support;

import android.view.WindowManager;

/* loaded from: classes.dex */
final class OverlayCompanionWindowParams {
    static final int IN_SCREEN_FLAGS = 296;
    static final int NO_LIMITS_FLAGS = 520;

    private OverlayCompanionWindowParams() {
    }

    static WindowManager.LayoutParams inScreen(int i, int i2, int i3, int i4) {
        return create(i, i2, IN_SCREEN_FLAGS, i3, i4);
    }

    static WindowManager.LayoutParams noLimits(int i, int i2, int i3, int i4) {
        return create(i, i2, NO_LIMITS_FLAGS, i3, i4);
    }

    static WindowManager.LayoutParams create(int i, int i2, int i3, int i4, int i5) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(i, i2, 2038, i3, -3);
        layoutParams.gravity = 8388659;
        layoutParams.x = i4;
        layoutParams.y = i5;
        return layoutParams;
    }
}
