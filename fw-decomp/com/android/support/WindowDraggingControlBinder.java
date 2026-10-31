package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import androidx.core.view.GravityCompat;
import com.android.support.OverlayDragPump;

/* loaded from: classes.dex */
final class WindowDraggingControlBinder {
    private static final String TAG = "FloatingWindowService";

    interface Host {
        Object companionDisplayGeometry();

        void constrainOpponentCardingRestoreAnchor(Object obj);

        void constrainWindowBounds(WindowManager.LayoutParams layoutParams, View view);

        Context context();

        View floatingBall();

        View floatingView();

        float initialTouchX();

        float initialTouchY();

        int initialX();

        int initialY();

        boolean minimized();

        boolean opponentCardingMainAutoShifted();

        boolean opponentCardingWindowVisible();

        WindowManager.LayoutParams paramsBall();

        WindowManager.LayoutParams paramsMain();

        WindowManager.LayoutParams paramsVerifyOverlay();

        int persistentMainWindowX();

        int persistentMainWindowY();

        SharedPreferences preferences();

        void setDragActive(boolean z);

        void setInitialTouchX(float f);

        void setInitialTouchY(float f);

        void setInitialX(int i);

        void setInitialY(int i);

        void setLastKnownMainSize(int i, int i2);

        void setOpponentCardingMainAutoShifted(boolean z);

        void updateVisibleChildWindowPositions();

        View verifyOverlay();

        WindowManager windowManager();
    }

    private WindowDraggingControlBinder() {
    }

    static void bind(final WindowManager.LayoutParams layoutParams, View view, final Host host) {
        final boolean z = layoutParams == host.paramsMain();
        final boolean z2 = layoutParams == host.paramsBall();
        final boolean z3 = layoutParams == host.paramsVerifyOverlay();
        final int scaledTouchSlop = ViewConfiguration.get(host.context()).getScaledTouchSlop();
        final boolean z4 = z3;
        final OverlayDragPump overlayDragPump = new OverlayDragPump(new OverlayDragPump.Target() { // from class: com.android.support.WindowDraggingControlBinder.1
            @Override // com.android.support.OverlayDragPump.Target
            public View view() {
                return WindowDraggingControlBinder.dragTargetView(host, z3, z2, z);
            }

            @Override // com.android.support.OverlayDragPump.Target
            public WindowManager.LayoutParams params() {
                return layoutParams;
            }

            @Override // com.android.support.OverlayDragPump.Target
            public WindowManager windowManager() {
                return host.windowManager();
            }

            @Override // com.android.support.OverlayDragPump.Target
            public void applyPosition(float f, float f2) {
                View view2 = view();
                if (view2 == null) {
                    return;
                }
                int iInitialTouchX = (int) (f - host.initialTouchX());
                int iInitialTouchY = (int) (f2 - host.initialTouchY());
                layoutParams.x = OverlayDragMath.resolveX(host.initialX(), iInitialTouchX, (layoutParams.gravity & GravityCompat.END) == 8388613 || (layoutParams.gravity & 5) == 5);
                layoutParams.y = host.initialY() + iInitialTouchY;
                layoutParams.flags |= 512;
                host.constrainWindowBounds(layoutParams, view2);
            }

            @Override // com.android.support.OverlayDragPump.Target
            public void onPositionApplied() {
                if (z) {
                    View viewFloatingView = host.floatingView();
                    if (viewFloatingView != null && viewFloatingView.getWidth() > 0) {
                        host.setLastKnownMainSize(viewFloatingView.getWidth(), viewFloatingView.getHeight());
                    }
                    host.updateVisibleChildWindowPositions();
                }
            }
        });
        final boolean z5 = z;
        view.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.support.WindowDraggingControlBinder.2
            private boolean dragging = false;

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0) {
                    host.setInitialX(layoutParams.x);
                    host.setInitialY(layoutParams.y);
                    host.setInitialTouchX(motionEvent.getRawX());
                    host.setInitialTouchY(motionEvent.getRawY());
                    this.dragging = false;
                    overlayDragPump.start(motionEvent.getRawX(), motionEvent.getRawY());
                    return true;
                }
                if (actionMasked == 1) {
                    overlayDragPump.finish();
                    if (z4 || !this.dragging) {
                        return true;
                    }
                    this.dragging = false;
                    if (z5) {
                        WindowDraggingControlBinder.settleDrag(host);
                    }
                    WindowDraggingControlBinder.persistDroppedPosition(layoutParams, host, z5);
                    return true;
                }
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        return false;
                    }
                    overlayDragPump.finish();
                    if (this.dragging && z5) {
                        WindowDraggingControlBinder.settleDrag(host);
                    }
                    this.dragging = false;
                    return true;
                }
                if (!this.dragging) {
                    if (!OverlayDragMath.exceededSlop(motionEvent.getRawX() - host.initialTouchX(), motionEvent.getRawY() - host.initialTouchY(), scaledTouchSlop)) {
                        return true;
                    }
                    this.dragging = true;
                    if (z5) {
                        host.setDragActive(true);
                        if (host.opponentCardingWindowVisible() && host.opponentCardingMainAutoShifted()) {
                            host.setOpponentCardingMainAutoShifted(false);
                        }
                    }
                }
                overlayDragPump.request(motionEvent.getRawX(), motionEvent.getRawY());
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void settleDrag(Host host) {
        host.setDragActive(false);
        host.updateVisibleChildWindowPositions();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static View dragTargetView(Host host, boolean z, boolean z2, boolean z3) {
        if (z) {
            return host.verifyOverlay();
        }
        if (z2) {
            return host.floatingBall();
        }
        if (z3) {
            return host.floatingView();
        }
        return host.minimized() ? host.floatingBall() : host.floatingView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void persistDroppedPosition(WindowManager.LayoutParams layoutParams, Host host, boolean z) {
        View viewFloatingBall;
        int iPersistentMainWindowX = layoutParams.x;
        int iPersistentMainWindowY = layoutParams.y;
        if (z && host.opponentCardingMainAutoShifted()) {
            host.constrainOpponentCardingRestoreAnchor(host.companionDisplayGeometry());
            iPersistentMainWindowX = host.persistentMainWindowX();
            iPersistentMainWindowY = host.persistentMainWindowY();
        } else if ((layoutParams.gravity & GravityCompat.START) == 8388611 || (layoutParams.gravity & 3) == 3) {
            int realScreenWidth = ScreenUtils.getRealScreenWidth(host.context());
            if (!host.minimized() && host.floatingView() != null) {
                viewFloatingBall = host.floatingView();
            } else {
                viewFloatingBall = host.minimized() ? host.floatingBall() : null;
            }
            int iDp2px = 0;
            if (viewFloatingBall != null) {
                int width = viewFloatingBall.getWidth();
                if (width <= 0) {
                    viewFloatingBall.measure(0, 0);
                    iDp2px = viewFloatingBall.getMeasuredWidth();
                } else {
                    iDp2px = width;
                }
            }
            if (iDp2px <= 0) {
                iDp2px = layoutParams.width > 0 ? layoutParams.width : ScreenUtils.dp2px(host.context(), 300.0f);
            }
            int i = (realScreenWidth - layoutParams.x) - iDp2px;
            layoutParams.gravity = 8388661;
            layoutParams.x = i;
            if (viewFloatingBall != null) {
                try {
                    if (host.windowManager() != null) {
                        host.windowManager().updateViewLayout(viewFloatingBall, layoutParams);
                    }
                } catch (RuntimeException unused) {
                }
            }
            iPersistentMainWindowX = i;
        }
        host.preferences().edit().putInt(FloatingWindowPrefsKeys.KEY_WINDOW_X, iPersistentMainWindowX).putInt(FloatingWindowPrefsKeys.KEY_WINDOW_Y, iPersistentMainWindowY).apply();
        Dbg.d(TAG, "窗口位置已保存(END格式): x=" + iPersistentMainWindowX + ", y=" + iPersistentMainWindowY);
    }
}
