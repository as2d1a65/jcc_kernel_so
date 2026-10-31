package com.android.support;

import android.content.Context;
import android.content.Intent;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class GameRegionState {
    private static final String STORE = "game_region_selection";
    private static volatile GameRegion selected = GameRegion.CN;
    private static final Set<String> SCOPED = new HashSet(Arrays.asList(FloatingWindowPrefsKeys.PREF_NAME, EmulatorShortcutService.PREF_NAME, "lineup_auto_apply", "lineup_hud_state", "lineup_favorites", "lineup_cdn", "jcc_specified_hex", "jcc_specified_recast", "app_settings", UiSurfaceControl.PREFERENCES_NAME, "ipc_v2_config_revision", "injection_runtime_state"));

    public static void initialize(Context context) {
        try {
            selected = GameRegion.fromId(context.getSharedPreferences(STORE, 0).getString("selected", "cn"));
        } catch (IllegalArgumentException unused) {
            selected = GameRegion.CN;
        }
    }

    public static GameRegion current() {
        return selected;
    }

    public static synchronized boolean select(Context context, GameRegion gameRegion) {
        if (gameRegion == null) {
            return false;
        }
        if (selected == gameRegion) {
            return true;
        }
        SharedMemManager sharedMemManagerPeekInstance = SharedMemManager.peekInstance();
        if (sharedMemManagerPeekInstance != null && sharedMemManagerPeekInstance.isCppConnected()) {
            return false;
        }
        if (FloatingWindowService.isServiceReady()) {
            try {
                context.stopService(new Intent(context, (Class<?>) FloatingWindowService.class));
            } catch (RuntimeException unused) {
            }
        }
        if (!context.getSharedPreferences(STORE, 0).edit().putString("selected", gameRegion.id).commit()) {
            return false;
        }
        SharedMemManager.resetForGameRegion();
        synchronized (JccSessionState.shared()) {
            selected = gameRegion;
            JccSessionState.shared().clearAll();
        }
        GameRegionControl.onRegionChanged();
        return true;
    }

    public static String preferenceName(String str) {
        return preferenceName(str, selected);
    }

    static String preferenceName(String str, GameRegion gameRegion) {
        return SCOPED.contains(str) ? gameRegion.namespace(str) : str;
    }

    public static File dataRoot(Context context) {
        File filesDir = context.getFilesDir();
        GameRegion gameRegionRegionOf = GameRegionContext.regionOf(context);
        return gameRegionRegionOf == GameRegion.CN ? filesDir : new File(filesDir, "regions/" + gameRegionRegionOf.id);
    }

    private GameRegionState() {
    }
}
