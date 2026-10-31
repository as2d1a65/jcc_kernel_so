package com.android.support;

import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcBoardHeroAnchorsParser {

    static final class Payload {
        final List<BoardHeroAnchor> anchors;
        final boolean planning;
        final long sequence;

        Payload(long j, boolean z, List<BoardHeroAnchor> list) {
            this.sequence = j;
            this.planning = z;
            this.anchors = list;
        }
    }

    private IpcBoardHeroAnchorsParser() {
    }

    static Payload parse(JSONObject jSONObject) {
        return new Payload(jSONObject.optLong("sequence", -1L), jSONObject.optBoolean("planning", false), BoardHeroAnchor.parse(jSONObject.optJSONArray("anchors")));
    }
}
