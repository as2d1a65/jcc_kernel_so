package com.android.support;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

/* loaded from: classes.dex */
final class OverlayPanelMotion {
    static final int ENTER_MS = 180;
    static final int EXIT_MS = 140;
    static final float SCALE = 0.92f;

    private OverlayPanelMotion() {
    }

    static void playEnter(View view) {
        if (view == null) {
            return;
        }
        view.setAlpha(0.0f);
        view.setScaleX(SCALE);
        view.setScaleY(SCALE);
        view.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(180L).setInterpolator(new DecelerateInterpolator()).start();
    }

    static void playExit(View view, Runnable runnable) {
        if (view != null) {
            view.animate().alpha(0.0f).scaleX(SCALE).scaleY(SCALE).setDuration(140L).withEndAction(runnable).start();
        } else if (runnable != null) {
            runnable.run();
        }
    }
}
