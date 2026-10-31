package com.android.support;

import android.content.Context;
import android.util.Log;
import android.view.View;

/* loaded from: classes.dex */
final class SpecifiedRecastPageBinder {
    private static final String TAG = "FloatingWindowService";

    private SpecifiedRecastPageBinder() {
    }

    static SpecifiedRecastPanel tryCreate(Context context, View view, View view2, SharedMemManager sharedMemManager) {
        if (RecastFeatureGate.isEnabled() && view != null && view2 != null) {
            try {
                return new SpecifiedRecastPanel(context, view, view2, sharedMemManager);
            } catch (RuntimeException e) {
                Log.e(TAG, "指定重铸页面初始化失败", e);
            }
        }
        return null;
    }
}
