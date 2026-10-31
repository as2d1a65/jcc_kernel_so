package com.android.support;

import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.android.support.OverlayDragPump;

/* loaded from: classes.dex */
final class BallDraggingControlBinder {
    private static final String TAG = "FloatingWindowService";

    interface Host {
        void constrainWindowBounds(WindowManager.LayoutParams layoutParams, View view);

        View floatingBall();

        float initialTouchX();

        float initialTouchY();

        int initialX();

        int initialY();

        WindowManager.LayoutParams paramsBall();

        WindowManager.LayoutParams paramsMain();

        SharedPreferences preferences();

        void setInitialTouchX(float f);

        void setInitialTouchY(float f);

        void setInitialX(int i);

        void setInitialY(int i);

        void showMainView();

        WindowManager windowManager();
    }

    private BallDraggingControlBinder() {
    }

    static void bind(final Host host) {
        final OverlayDragPump overlayDragPump = new OverlayDragPump(new OverlayDragPump.Target() { // from class: com.android.support.BallDraggingControlBinder.1
            @Override // com.android.support.OverlayDragPump.Target
            public void onPositionApplied() {
            }

            @Override // com.android.support.OverlayDragPump.Target
            public View view() {
                return host.floatingBall();
            }

            @Override // com.android.support.OverlayDragPump.Target
            public WindowManager.LayoutParams params() {
                return host.paramsBall();
            }

            @Override // com.android.support.OverlayDragPump.Target
            public WindowManager windowManager() {
                return host.windowManager();
            }

            @Override // com.android.support.OverlayDragPump.Target
            public void applyPosition(float f, float f2) {
                WindowManager.LayoutParams layoutParamsParamsBall = host.paramsBall();
                View viewFloatingBall = host.floatingBall();
                if (layoutParamsParamsBall == null || viewFloatingBall == null) {
                    return;
                }
                layoutParamsParamsBall.x = host.initialX() + ((int) (host.initialTouchX() - f));
                layoutParamsParamsBall.y = host.initialY() + ((int) (f2 - host.initialTouchY()));
                host.constrainWindowBounds(layoutParamsParamsBall, viewFloatingBall);
            }
        });
        host.floatingBall().setOnTouchListener(new View.OnTouchListener() { // from class: com.android.support.BallDraggingControlBinder.2
            private long touchStartTime = 0;
            private float touchStartX = 0.0f;
            private float touchStartY = 0.0f;

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0) {
                    Host host2 = host;
                    host2.setInitialX(host2.paramsBall().x);
                    Host host3 = host;
                    host3.setInitialY(host3.paramsBall().y);
                    host.setInitialTouchX(motionEvent.getRawX());
                    host.setInitialTouchY(motionEvent.getRawY());
                    this.touchStartTime = System.currentTimeMillis();
                    this.touchStartX = motionEvent.getRawX();
                    this.touchStartY = motionEvent.getRawY();
                    overlayDragPump.start(motionEvent.getRawX(), motionEvent.getRawY());
                    return true;
                }
                if (actionMasked != 1) {
                    if (actionMasked == 2) {
                        overlayDragPump.request(motionEvent.getRawX(), motionEvent.getRawY());
                        return true;
                    }
                    if (actionMasked != 3) {
                        return false;
                    }
                    overlayDragPump.finish();
                    Dbg.d(BallDraggingControlBinder.TAG, "Ball touch CANCEL");
                    return true;
                }
                overlayDragPump.finish();
                long jCurrentTimeMillis = System.currentTimeMillis() - this.touchStartTime;
                float fAbs = Math.abs(motionEvent.getRawX() - this.touchStartX);
                float fAbs2 = Math.abs(motionEvent.getRawY() - this.touchStartY);
                if (jCurrentTimeMillis < 300 && fAbs < 15.0f && fAbs2 < 15.0f) {
                    Dbg.d(BallDraggingControlBinder.TAG, "Ball clicked - showing main view");
                    host.showMainView();
                } else {
                    Dbg.d(BallDraggingControlBinder.TAG, "Ball dragged - staying minimized");
                    WindowManager.LayoutParams layoutParamsParamsBall = host.paramsBall();
                    if (layoutParamsParamsBall != null) {
                        WindowManager.LayoutParams layoutParamsParamsMain = host.paramsMain();
                        if (layoutParamsParamsMain != null) {
                            layoutParamsParamsMain.gravity = layoutParamsParamsBall.gravity;
                            layoutParamsParamsMain.x = layoutParamsParamsBall.x;
                            layoutParamsParamsMain.y = layoutParamsParamsBall.y;
                        }
                        SharedPreferences sharedPreferencesPreferences = host.preferences();
                        if (sharedPreferencesPreferences != null) {
                            sharedPreferencesPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_WINDOW_X, layoutParamsParamsBall.x).putInt(FloatingWindowPrefsKeys.KEY_WINDOW_Y, layoutParamsParamsBall.y).apply();
                        }
                    }
                }
                return true;
            }
        });
        Dbg.d(TAG, "Ball drag listener setup complete");
    }
}
