package com.android.support;

import org.json.JSONObject;

/* loaded from: classes.dex */
public final class RecastRunStatus {
    public static final String PHASE_BLOCKED = "blocked";
    public static final String PHASE_FIRED = "fired";
    public static final String PHASE_HIT = "hit";
    public static final String PHASE_MISS = "miss";
    public static final String PHASE_STOPPED = "stopped";
    public static final String PHASE_WAITING = "waiting";
    public static final String REASON_FIRE_TIMEOUT = "fire_timeout";
    public static final String REASON_NEED_REPLACE = "need_replace";
    public static final String REASON_NO_CANDIDATE = "no_candidate";
    public static final String REASON_NO_RECASTER = "no_recaster";
    public static final String REASON_SOURCE_IS_TARGET = "source_is_target";
    public static final String REASON_TARGET_UNAVAILABLE = "target_unavailable";
    public static final String REASON_WRONG_TIER = "wrong_tier";
    public final int candidates;
    public final int delayMax;
    public final int delayMin;
    public final long evaluations;
    public final String matchGeneration;
    public final int observedId;
    public final int observedInternalSamples;
    public final int observedRngCount;
    public final int observedRngDelta;
    public final int observedTier;
    public final String phase;
    public final int poolSize;
    public final String reason;
    public final int resultId;
    public final int submitRngCount;
    public final int targetId;
    public final String timingRange;
    public final int windowMax;
    public final int windowMin;
    public static final String PHASE_IDLE = "idle";
    public static final RecastRunStatus IDLE = new RecastRunStatus(PHASE_IDLE, 0, 0, 0, 0, 0, "", "", 0, 0, -1, -1, -1, 0, "", -1, -1, -1, -1);

    static String runtimeIssue(String str) {
        String strIssue = InputTimingStatus.issue(str);
        if (strIssue != null) {
            return strIssue;
        }
        if ("runtime_not_ready".equals(str)) {
            return "正在等待本局重铸数据";
        }
        if ("compute_pending".equals(str)) {
            return "正在更新重铸预测";
        }
        if ("unpredictable".equals(str)) {
            return "暂未获得完整装备预测，正在重试";
        }
        if ("rng_changed".equals(str)) {
            return "结果已变化，正在重新预测";
        }
        if ("submit_failed".equals(str)) {
            return "重铸提交未完成，请检查游戏中的结果";
        }
        return null;
    }

    private RecastRunStatus(String str, int i, int i2, int i3, long j, int i4, String str2, String str3, int i5, int i6, int i7, int i8, int i9, int i10, String str4, int i11, int i12, int i13, int i14) {
        this.timingRange = str4;
        this.delayMin = i11;
        this.delayMax = i12;
        this.windowMin = i13;
        this.windowMax = i14;
        this.phase = str;
        this.targetId = i;
        this.candidates = i2;
        this.poolSize = i3;
        this.evaluations = j;
        this.resultId = i4;
        this.reason = str2;
        this.matchGeneration = str3;
        this.observedId = i5;
        this.observedTier = i6;
        this.submitRngCount = i7;
        this.observedRngCount = i8;
        this.observedRngDelta = i9;
        this.observedInternalSamples = i10;
    }

    public static RecastRunStatus fromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            return IDLE;
        }
        return new RecastRunStatus(jSONObject.optString("phase", PHASE_IDLE), jSONObject.optInt("targetId", 0), jSONObject.optInt("candidates", 0), jSONObject.optInt("poolSize", 0), jSONObject.optLong("evaluations", 0L), jSONObject.optInt("resultId", 0), jSONObject.optString("reason", ""), jSONObject.optString("matchGeneration", ""), jSONObject.optInt("observedId", 0), jSONObject.optInt("observedTier", 0), jSONObject.optInt("submitRngCount", -1), jSONObject.optInt("observedRngCount", -1), jSONObject.optInt("observedRngDelta", -1), jSONObject.optInt("observedInternalSamples", 0), InputTimingStatus.range(jSONObject), jSONObject.optInt("delayMin", -1), jSONObject.optInt("delayMax", -1), jSONObject.optInt("windowMin", -1), jSONObject.optInt("windowMax", -1));
    }

    public boolean isRunning() {
        return "waiting".equals(this.phase) || PHASE_FIRED.equals(this.phase) || PHASE_BLOCKED.equals(this.phase);
    }

    public boolean isSettled() {
        return PHASE_HIT.equals(this.phase) || PHASE_MISS.equals(this.phase);
    }

    public boolean isStopped() {
        return PHASE_STOPPED.equals(this.phase);
    }

    public boolean isHit() {
        return PHASE_HIT.equals(this.phase);
    }
}
