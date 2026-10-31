package com.android.support;

import org.json.JSONObject;

/* loaded from: classes.dex */
final class InputTimingStatus {
    InputTimingStatus() {
    }

    static String issue(String str) {
        if ("timing_session".equals(str)) {
            return "等待进入对局";
        }
        if ("timing_failed".equals(str)) {
            return "暂未准备好，请重新开始";
        }
        if ("timing_timeout".equals(str) || "timing_spread".equals(str) || "timing_runtime".equals(str)) {
            return "正在准备，自动重试中";
        }
        if ("timing_motion".equals(str) || "timing_unstable".equals(str)) {
            return "正在寻找目标";
        }
        if ("timing_player".equals(str)) {
            return "正在准备，请稍候（找不到自己的座位）";
        }
        if ("timing_position".equals(str)) {
            return "正在准备，请稍候（读不到小小英雄位置）";
        }
        if ("timing_frame".equals(str)) {
            return "正在准备，请稍候（读不到逻辑帧）";
        }
        if ("timing_payload".equals(str)) {
            return "正在准备，请稍候（移动指令没组好）";
        }
        if ("timing_identity".equals(str)) {
            return "正在准备，请稍候（上一次移动还没结束）";
        }
        if ("timing_probe_0".equals(str) || "timing_probe_1".equals(str) || "timing_probe_2".equals(str)) {
            return "正在准备，请稍候";
        }
        return null;
    }

    static String range(JSONObject jSONObject) {
        return "";
    }
}
