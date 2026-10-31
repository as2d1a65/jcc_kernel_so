package com.android.support;

import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcRankAnchorsParser {
    private IpcRankAnchorsParser() {
    }

    static List<RankWarningAnchor> parse(JSONObject jSONObject) {
        return RankWarningAnchor.parse(jSONObject.optJSONArray("anchors"));
    }
}
