package com.android.support;

import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcMatchLifecycleParser {
    private IpcMatchLifecycleParser() {
    }

    static final class Started {
        final String matchGeneration;
        final long sessionGeneration;

        private Started(String str, long j) {
            this.matchGeneration = str;
            this.sessionGeneration = j;
        }
    }

    static Started parseStarted(JSONObject jSONObject) throws Exception {
        String string = jSONObject.getString("matchGeneration");
        long jOptLong = jSONObject.optLong("sessionGeneration", 0L);
        if (string.length() != 22) {
            throw new IllegalArgumentException("invalid match generation");
        }
        if (jOptLong <= 0) {
            throw new IllegalArgumentException("invalid native session generation");
        }
        return new Started(string, jOptLong);
    }

    static String parseEnded(JSONObject jSONObject) throws Exception {
        return jSONObject.getString("matchGeneration");
    }
}
