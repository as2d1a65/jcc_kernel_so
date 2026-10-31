package com.android.support;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/* loaded from: classes.dex */
final class OverlayCompanionAttach {
    private static final String TAG = "FloatingWindowService";

    interface AttachedProbe {
        boolean isAttached(View view);

        void mark(View view, boolean z);

        void scheduleReconcile();
    }

    interface OwnerSink {
        void set(WindowManager windowManager);
    }

    static boolean shouldClearOwner(boolean z) {
        return !z;
    }

    static boolean shouldReconcileAfterAdd(boolean z) {
        return !z;
    }

    private OverlayCompanionAttach() {
    }

    static void add(WindowManager windowManager, View view, WindowManager.LayoutParams layoutParams, OwnerSink ownerSink, boolean z, AttachedProbe attachedProbe, Runnable runnable, String str) {
        try {
            windowManager.addView(view, layoutParams);
            ownerSink.set(windowManager);
            attachedProbe.mark(view, attachedProbe.isAttached(view));
            if (shouldReconcileAfterAdd(z)) {
                attachedProbe.scheduleReconcile();
            }
            runnable.run();
        } catch (Exception e) {
            Log.e(TAG, str, e);
            boolean zIsAttached = attachedProbe.isAttached(view);
            attachedProbe.mark(view, zIsAttached);
            if (shouldClearOwner(zIsAttached)) {
                ownerSink.set(null);
            }
            attachedProbe.scheduleReconcile();
        }
    }
}
