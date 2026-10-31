package com.android.support;

import org.json.JSONObject;

/* loaded from: classes.dex */
public final class SpecifiedHexStatus {
    public static final SpecifiedHexStatus IDLE = new SpecifiedHexStatus(RecastRunStatus.PHASE_IDLE, 0, "", 0, 0, "", 0, "");
    public static final int MAX_STAGE = 3;
    public final String code;
    public final String matchGeneration;
    public final String phase;
    public final long sessionGeneration;
    public final int stage;
    public final int targetHexId;
    public final long targetRevision;
    public final String timingRange;

    private SpecifiedHexStatus(String str, int i, String str2, int i2, long j, String str3, long j2, String str4) {
        this.timingRange = str4;
        this.phase = str;
        this.targetHexId = i;
        this.code = str2;
        this.stage = i2;
        this.sessionGeneration = j;
        this.matchGeneration = str3;
        this.targetRevision = j2;
    }

    public static SpecifiedHexStatus fromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            throw new IllegalArgumentException("status == null");
        }
        String strOptString = jSONObject.optString("phase", "");
        if (!isKnownPhase(strOptString)) {
            throw new IllegalArgumentException("unknown specified Hex phase");
        }
        int iOptInt = jSONObject.optInt("targetHexId", -1);
        String strOptString2 = jSONObject.optString("code", "");
        int iOptInt2 = jSONObject.optInt("stage", 0);
        long jOptLong = jSONObject.optLong("sessionGeneration", 0L);
        String strOptString3 = jSONObject.optString("matchGeneration", "");
        long jOptLong2 = jSONObject.optLong("targetRevision", 0L);
        if (iOptInt < 0 || iOptInt > 2000000 || iOptInt2 < 0 || iOptInt2 > 3 || strOptString2.length() > 64 || !strOptString2.matches("[a-z0-9_]*") || jOptLong <= 0 || strOptString3.length() != 22 || jOptLong2 <= 0) {
            throw new IllegalArgumentException("invalid specified Hex status");
        }
        return new SpecifiedHexStatus(strOptString, iOptInt, strOptString2, iOptInt2, jOptLong, strOptString3, jOptLong2, InputTimingStatus.range(jSONObject));
    }

    public boolean isRunning() {
        return "armed".equals(this.phase);
    }

    public boolean isTerminal() {
        return "completed".equals(this.phase) || "failed".equals(this.phase);
    }

    private static boolean isKnownPhase(String str) {
        return RecastRunStatus.PHASE_IDLE.equals(str) || "armed".equals(str) || "completed".equals(str) || "failed".equals(str);
    }
}
