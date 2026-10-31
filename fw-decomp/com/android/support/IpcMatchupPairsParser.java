package com.android.support;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcMatchupPairsParser {
    private IpcMatchupPairsParser() {
    }

    static List<MatchupPairData> parse(JSONObject jSONObject) throws Exception {
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("pairs");
        ArrayList arrayList = new ArrayList();
        if (jSONArrayOptJSONArray != null) {
            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i);
                arrayList.add(new MatchupPairData(jSONObject2.optInt("p1", -1), jSONObject2.optInt("p2", -1), jSONObject2.optInt("ghost", -1)));
            }
        }
        return arrayList;
    }
}
