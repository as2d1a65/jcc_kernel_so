package com.android.support;

import android.content.SharedPreferences;
import com.android.support.QuickActionOverlayPolicy;

/* loaded from: classes.dex */
final class QuickActionDockGeometry {
    private QuickActionDockGeometry() {
    }

    static QuickActionOverlayPolicy.Rect displayRect(int i, int i2) {
        return new QuickActionOverlayPolicy.Rect(0, 0, Math.max(1, i), Math.max(1, i2));
    }

    static QuickActionOverlayPolicy.Point restorePoint(SharedPreferences sharedPreferences, QuickActionOverlayPolicy.Rect rect, int i, int i2, int i3) {
        if (sharedPreferences.contains(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NX) && sharedPreferences.contains(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NY)) {
            return QuickActionOverlayPolicy.restore(rect, new QuickActionOverlayPolicy.Normalized(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NX, 0) / 1000000.0d, sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NY, 0) / 1000000.0d), i, i2);
        }
        if (sharedPreferences.contains(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_X) && sharedPreferences.contains(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_Y)) {
            return QuickActionOverlayPolicy.clamp(rect, sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_X, rect.left), sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_Y, rect.top), i, i2);
        }
        return QuickActionOverlayPolicy.defaultPosition(rect, i, i2, i3);
    }

    static void persistPosition(SharedPreferences sharedPreferences, QuickActionOverlayPolicy.Rect rect, QuickActionOverlayPolicy.Point point, int i, int i2) {
        QuickActionOverlayPolicy.Normalized normalizedNormalize = QuickActionOverlayPolicy.normalize(rect, point.x, point.y, i, i2);
        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_X, point.x).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_Y, point.y).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NX, (int) Math.round(normalizedNormalize.x * 1000000.0d)).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NY, (int) Math.round(normalizedNormalize.y * 1000000.0d)).apply();
    }
}
