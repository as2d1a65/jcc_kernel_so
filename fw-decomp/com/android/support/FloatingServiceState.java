package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;

/* loaded from: classes.dex */
final class FloatingServiceState {
    private static final String KEY_SERVICE_WAS_RUNNING = "service_was_running";
    private static final String PREF_NAME = "FloatingWindowConfig";

    private FloatingServiceState() {
    }

    static void markRunning(Context context) {
        setRunning(context, true);
    }

    static void clearRunning(Context context) {
        setRunning(context, false);
    }

    static boolean wasRunning(Context context) {
        if (context == null) {
            return false;
        }
        return prefs(context).getBoolean(KEY_SERVICE_WAS_RUNNING, false);
    }

    private static void setRunning(Context context, boolean z) {
        if (context == null) {
            return;
        }
        prefs(context).edit().putBoolean(KEY_SERVICE_WAS_RUNNING, z).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences("FloatingWindowConfig", 0);
    }
}
