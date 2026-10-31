package com.android.support;

import org.json.JSONObject;

/* loaded from: classes.dex */
final class GameLaunchGuard {
    static final long MAX_STATUS_AGE_MS = 1500;
    static final String STATUS_METHOD = "runtime.launchStatus";

    static final class Status {
        final boolean ready;
        final String reason;
        final long receivedAtMs;
        final long verifiedAtBootMs;

        Status(boolean z, long j, long j2, String str) {
            this.ready = z;
            this.receivedAtMs = j;
            this.verifiedAtBootMs = j2;
            this.reason = str;
        }

        boolean fresh(long j) {
            if (!this.ready) {
                return false;
            }
            long j2 = this.receivedAtMs;
            if (j2 <= 0 || j < j2 || j - j2 > GameLaunchGuard.MAX_STATUS_AGE_MS) {
                return false;
            }
            long j3 = this.verifiedAtBootMs;
            return j3 > 0 && j3 <= j2 && j - j3 <= GameLaunchGuard.MAX_STATUS_AGE_MS;
        }

        boolean failed() {
            return (this.ready || "waiting".equals(this.reason) || "authorization_pending".equals(this.reason) || "module_loading".equals(this.reason)) ? false : true;
        }
    }

    static Status parse(JSONObject jSONObject, long j) {
        if (jSONObject == null || !(jSONObject.opt("ready") instanceof Boolean)) {
            return null;
        }
        boolean zOptBoolean = jSONObject.optBoolean("ready");
        String strOptString = jSONObject.optString("reason", "waiting");
        if (zOptBoolean && !"ready".equals(strOptString)) {
            return null;
        }
        Object objOpt = jSONObject.opt("verifiedAtBootMs");
        long jLongValue = ((objOpt instanceof Long) || (objOpt instanceof Integer)) ? ((Number) objOpt).longValue() : 0L;
        if (!zOptBoolean || (jLongValue > 0 && jLongValue <= j)) {
            return new Status(zOptBoolean, j, jLongValue, strOptString);
        }
        return null;
    }

    static boolean allows(String str, String str2, boolean z, Status status, long j) {
        return str != null && !str.isEmpty() && str.equals(str2) && z && status != null && status.fresh(j);
    }

    static boolean isPreparationProvider(String str, String str2, String str3, String str4, boolean z) {
        return z && str != null && str.equals(str3) && "com.gcore.abase.GCFileProvider".equals(str2) && new StringBuilder().append(str).append(".ABaseFileProvider").toString().equals(str4);
    }

    static boolean preparationCompleted(boolean z, int i, String str) {
        if (z || (!(i == 0 || i == 139) || str == null)) {
            return false;
        }
        boolean z2 = false;
        for (String str2 : str.split("\\r?\\n")) {
            String strTrim = str2.trim();
            if (!strTrim.isEmpty()) {
                if (!z2 && "Result: null".equals(strTrim)) {
                    z2 = true;
                } else if (!z2 || i != 139 || !"Segmentation fault".equals(strTrim)) {
                    return false;
                }
            }
        }
        return z2;
    }

    private GameLaunchGuard() {
    }
}
