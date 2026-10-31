package com.android.support;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.android.support.OverlayDragPump;

/* loaded from: classes.dex */
final class OverlayHeaderDragBinder {
    static final int SHORT_PRESS_MS = 220;

    interface Host {
        long nowMs();

        boolean onShortPress();

        View overlay();

        WindowManager.LayoutParams params();

        void persistDrag(int i, int i2);

        int slopPx();

        WindowManager windowManager();
    }

    static boolean shouldPersist(boolean z, WindowManager.LayoutParams layoutParams) {
        return z && layoutParams != null;
    }

    static boolean shouldShortPress(boolean z, int i, long j) {
        return !z && i == 1 && j < 220;
    }

    private OverlayHeaderDragBinder() {
    }

    static boolean shouldStartDrag(boolean z, float f, float f2, int i) {
        if (z) {
            return false;
        }
        float fAbs = Math.abs(f);
        float f3 = i;
        return fAbs > f3 || Math.abs(f2) > f3;
    }

    static View.OnTouchListener bind(final Host host) {
        return new View.OnTouchListener() { // from class: com.android.support.OverlayHeaderDragBinder.1
            private long downTimeMs;
            private boolean dragging;
            private int origX;
            private int origY;
            private OverlayDragPump pump;
            private float touchStartX;
            private float touchStartY;

            private OverlayDragPump pump() {
                if (this.pump == null) {
                    this.pump = new OverlayDragPump(new OverlayDragPump.Target() { // from class: com.android.support.OverlayHeaderDragBinder.1.1
                        @Override // com.android.support.OverlayDragPump.Target
                        public void onPositionApplied() {
                        }

                        @Override // com.android.support.OverlayDragPump.Target
                        public View view() {
                            return host.overlay();
                        }

                        @Override // com.android.support.OverlayDragPump.Target
                        public WindowManager.LayoutParams params() {
                            return host.params();
                        }

                        @Override // com.android.support.OverlayDragPump.Target
                        public WindowManager windowManager() {
                            return host.windowManager();
                        }

                        @Override // com.android.support.OverlayDragPump.Target
                        public void applyPosition(float f, float f2) {
                            WindowManager.LayoutParams layoutParamsParams = host.params();
                            if (layoutParamsParams == null) {
                                return;
                            }
                            layoutParamsParams.x = AnonymousClass1.this.origX + ((int) (f - AnonymousClass1.this.touchStartX));
                            layoutParamsParams.y = AnonymousClass1.this.origY + ((int) (f2 - AnonymousClass1.this.touchStartY));
                        }
                    });
                }
                return this.pump;
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.touchStartX = motionEvent.getRawX();
                    this.touchStartY = motionEvent.getRawY();
                    WindowManager.LayoutParams layoutParamsParams = host.params();
                    if (layoutParamsParams != null) {
                        this.origX = layoutParamsParams.x;
                        this.origY = layoutParamsParams.y;
                    }
                    this.dragging = false;
                    this.downTimeMs = host.nowMs();
                    pump().start(motionEvent.getRawX(), motionEvent.getRawY());
                    return true;
                }
                if (action != 1) {
                    if (action == 2) {
                        if (OverlayHeaderDragBinder.shouldStartDrag(this.dragging, motionEvent.getRawX() - this.touchStartX, motionEvent.getRawY() - this.touchStartY, host.slopPx())) {
                            this.dragging = true;
                        }
                        if (this.dragging) {
                            pump().request(motionEvent.getRawX(), motionEvent.getRawY());
                        }
                        return true;
                    }
                    if (action != 3) {
                        return false;
                    }
                }
                pump().finish();
                WindowManager.LayoutParams layoutParamsParams2 = host.params();
                if (OverlayHeaderDragBinder.shouldPersist(this.dragging, layoutParamsParams2)) {
                    host.persistDrag(layoutParamsParams2.x, layoutParamsParams2.y);
                } else if (OverlayHeaderDragBinder.shouldShortPress(this.dragging, motionEvent.getAction(), host.nowMs() - this.downTimeMs)) {
                    host.onShortPress();
                }
                this.dragging = false;
                return true;
            }
        };
    }
}
