package com.android.support;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class CustomSwapPairJson {
    static final String FROM_COL = "fromCol";
    static final String FROM_ROW = "fromRow";
    static final String TO_COL = "toCol";
    static final String TO_ROW = "toRow";

    private CustomSwapPairJson() {
    }

    static JSONObject objectOf(int[] iArr) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(FROM_COL, iArr[0]);
        jSONObject.put(FROM_ROW, iArr[1]);
        jSONObject.put(TO_COL, iArr[2]);
        jSONObject.put(TO_ROW, iArr[3]);
        return jSONObject;
    }

    static JSONArray arrayOf(List<int[]> list) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        if (list != null) {
            for (int[] iArr : list) {
                if (CustomSwapPairingPolicy.isValidPair(iArr)) {
                    jSONArray.put(objectOf(iArr));
                }
            }
        }
        return jSONArray;
    }
}
