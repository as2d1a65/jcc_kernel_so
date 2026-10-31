package com.android.support;

import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.android.support.WindowDraggingControlBinder;

/* loaded from: classes.dex */
final class VerifyOverlayAttach {
    private static final String TAG = "FloatingWindowService";

    interface Host {
        View floatingView();

        void flog(String str);

        WindowManager.LayoutParams paramsVerifyOverlay();

        void setParamsVerifyOverlay(WindowManager.LayoutParams layoutParams);

        void setVerifyChromeCollapsed(boolean z);

        void setVerifyChromeDivider(View view);

        void setVerifyChromeStatus(View view);

        void setVerifyChromeTitleBar(View view);

        void setVerifyImeListener(ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener);

        void setVerifyImeShifted(boolean z);

        void setVerifyOverlay(View view);

        void setVerifyOverlaySpec(VerifyWindowSpec verifyWindowSpec);

        ViewTreeObserver.OnGlobalLayoutListener verifyImeAvoidance();

        void verifyOverlayAttached(String str, View view);

        WindowDraggingControlBinder.Host windowDragHost();

        WindowManager windowManager();
    }

    private VerifyOverlayAttach() {
    }

    static WindowManager.LayoutParams createParams(VerifyWindowSpec verifyWindowSpec) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(verifyWindowSpec.widthPx, verifyWindowSpec.heightPx, 2038, 32, -3);
        layoutParams.gravity = 17;
        return layoutParams;
    }

    static void show(Host host, View view, View view2, View view3, View view4, VerifyWindowSpec verifyWindowSpec) {
        host.setVerifyOverlay(view);
        WindowManager.LayoutParams layoutParamsCreateParams = createParams(verifyWindowSpec);
        host.setParamsVerifyOverlay(layoutParamsCreateParams);
        WindowDraggingControlBinder.bind(layoutParamsCreateParams, view2, host.windowDragHost());
        try {
            host.windowManager().addView(view, layoutParamsCreateParams);
            host.flog("[verify] verifyOverlay addView 成功");
            host.verifyOverlayAttached("verifyOverlay", view);
            host.setVerifyOverlaySpec(verifyWindowSpec);
            host.setVerifyImeShifted(false);
            host.setVerifyChromeCollapsed(false);
            host.setVerifyChromeTitleBar(view2);
            host.setVerifyChromeDivider(view3);
            host.setVerifyChromeStatus(view4);
            ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListenerVerifyImeAvoidance = host.verifyImeAvoidance();
            host.setVerifyImeListener(onGlobalLayoutListenerVerifyImeAvoidance);
            view.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListenerVerifyImeAvoidance);
            try {
                View viewFloatingView = host.floatingView();
                if (viewFloatingView != null && viewFloatingView.getParent() != null) {
                    host.windowManager().removeView(viewFloatingView);
                    Dbg.d(TAG, "Main view removed from WM during verification");
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to remove main view during verification: " + e.getMessage());
            }
            Dbg.d(TAG, "Verify overlay created and shown");
        } catch (Exception e2) {
            Log.e(TAG, "Failed to show verify overlay", e2);
        }
    }
}
