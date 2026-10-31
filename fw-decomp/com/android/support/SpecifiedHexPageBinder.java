package com.android.support;

import android.content.Context;
import android.util.Log;
import android.view.View;

/* loaded from: classes.dex */
final class SpecifiedHexPageBinder {
    private static final String TAG = "FloatingWindowService";

    private SpecifiedHexPageBinder() {
    }

    static SpecifiedHexPanel tryCreate(Context context, View view, View view2, SharedMemManager sharedMemManager) {
        GameRegion gameRegionCurrent = GameRegionState.current();
        if (gameRegionCurrent != null && RecastFeatureGate.isEnabledForPackage(gameRegionCurrent.packageName) && view != null && view2 != null && sharedMemManager != null) {
            try {
                return new SpecifiedHexPanel(context, view, view2, sharedMemManager);
            } catch (RuntimeException e) {
                Log.e(TAG, "指定海克斯页面初始化失败", e);
            }
        }
        return null;
    }
}
