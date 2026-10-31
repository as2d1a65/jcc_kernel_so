package com.android.support;

import org.json.JSONObject;

/* loaded from: classes.dex */
public final class RootHelperProtocol {
    public static final String SOCKET_PREFIX = "jcc_helper_";
    public static final String TYPE_GAME_PROBE = "game_probe";
    public static final String TYPE_HELPER_READY = "helper_ready";

    private RootHelperProtocol() {
    }

    public static String socketNameFor(String str) {
        return SOCKET_PREFIX + str;
    }

    public static RootHelperGameState parseGameProbe(String str) {
        if (str != null && str.contains(TYPE_GAME_PROBE)) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                return new RootHelperGameState(jSONObject.optInt("pid", 0), jSONObject.optLong("cpu_ticks", 0L), jSONObject.optLong("rss_kb", 0L), jSONObject.optString("libil2cpp", "0"), jSONObject.optBoolean("runtime_loaded", false));
            } catch (Throwable unused) {
            }
        }
        return null;
    }
}
