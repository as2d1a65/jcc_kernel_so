package com.android.support;

import android.view.Choreographer;
import android.view.View;
import android.view.WindowManager;

/* loaded from: classes.dex */
final class OverlayDragPump {
    private boolean active;
    private boolean framePending;
    private float latestRawX;
    private float latestRawY;
    private final Target target;
    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() { // from class: com.android.support.OverlayDragPump$$ExternalSyntheticLambda0
        @Override // android.view.Choreographer.FrameCallback
        public final void doFrame(long j) {
            this.f$0.m1392lambda$new$0$comandroidsupportOverlayDragPump(j);
        }
    };
    private final Choreographer choreographer = Choreographer.getInstance();

    interface Target {
        void applyPosition(float f, float f2);

        void onPositionApplied();

        WindowManager.LayoutParams params();

        View view();

        WindowManager windowManager();
    }

    /* renamed from: lambda$new$0$com-android-support-OverlayDragPump, reason: not valid java name */
    /* synthetic */ void m1392lambda$new$0$comandroidsupportOverlayDragPump(long j) {
        this.framePending = false;
        if (this.active) {
            flush();
        }
    }

    OverlayDragPump(Target target) {
        this.target = target;
    }

    void start(float f, float f2) {
        this.latestRawX = f;
        this.latestRawY = f2;
        this.active = true;
    }

    void request(float f, float f2) {
        this.latestRawX = f;
        this.latestRawY = f2;
        if (!this.active || this.framePending) {
            return;
        }
        this.framePending = true;
        this.choreographer.postFrameCallback(this.frameCallback);
    }

    void finish() {
        boolean z = this.framePending;
        clearPendingFrame();
        if (this.active && z) {
            flush();
        }
        this.active = false;
    }

    void cancel() {
        clearPendingFrame();
        this.active = false;
    }

    private void clearPendingFrame() {
        if (this.framePending) {
            this.choreographer.removeFrameCallback(this.frameCallback);
            this.framePending = false;
        }
    }

    private void flush() {
        View view = this.target.view();
        WindowManager.LayoutParams layoutParamsParams = this.target.params();
        WindowManager windowManager = this.target.windowManager();
        if (view == null || layoutParamsParams == null || windowManager == null) {
            return;
        }
        this.target.applyPosition(this.latestRawX, this.latestRawY);
        try {
            windowManager.updateViewLayout(view, layoutParamsParams);
            this.target.onPositionApplied();
        } catch (RuntimeException unused) {
        }
    }
}
