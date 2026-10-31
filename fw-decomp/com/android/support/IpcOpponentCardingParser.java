package com.android.support;

import com.android.support.OpponentCardingStatus;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcOpponentCardingParser {
    private IpcOpponentCardingParser() {
    }

    static OpponentCardingStatus parse(JSONObject jSONObject) {
        String strOptString = jSONObject.optString("state", "waiting");
        String strOptString2 = jSONObject.optString("summary", "");
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("targets");
        ArrayList arrayList = new ArrayList();
        if (jSONArrayOptJSONArray != null) {
            int iMin = Math.min(jSONArrayOptJSONArray.length(), 32);
            for (int i = 0; i < iMin; i++) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i);
                if (jSONObjectOptJSONObject != null) {
                    arrayList.add(OpponentCardingStatus.Target.create(jSONObjectOptJSONObject.optInt("playerId", -1), jSONObjectOptJSONObject.optString("name", ""), jSONObjectOptJSONObject.optInt("heroId", 0), jSONObjectOptJSONObject.optInt("copies", 0), jSONObjectOptJSONObject.optInt("threshold", 6), jSONObjectOptJSONObject.optBoolean("purchaseEligible", true), jSONObjectOptJSONObject.optString("reason", ""), jSONObjectOptJSONObject.optInt("ownedCopies", 0), jSONObjectOptJSONObject.optInt("maxOwnedCopies", 3), jSONObjectOptJSONObject.optInt("neededCopies", 3)));
                }
            }
        }
        return OpponentCardingStatus.create(strOptString, strOptString2, arrayList);
    }
}
