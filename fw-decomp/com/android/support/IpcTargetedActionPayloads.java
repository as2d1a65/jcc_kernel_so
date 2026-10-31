package com.android.support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcTargetedActionPayloads {
    private IpcTargetedActionPayloads() {
    }

    static JSONObject scalar(String str, int i, int i2, int i3) throws JSONException {
        return withTarget(new JSONObject().put(str, i), i2, i3);
    }

    static JSONObject heroDrop(int[] iArr, int i, int i2, int i3) throws JSONException {
        return withTarget(new JSONObject().put("heroIds", intArray(iArr)).put("starLevel", i), i2, i3);
    }

    static JSONObject equipmentDrop(int[] iArr, int i, int i2) throws JSONException {
        return withTarget(new JSONObject().put("equipIds", intArray(iArr)), i, i2);
    }

    private static JSONObject withTarget(JSONObject jSONObject, int i, int i2) throws JSONException {
        return jSONObject.put("targetMask", i).put("targetSelf", i2);
    }

    private static JSONArray intArray(int[] iArr) {
        JSONArray jSONArray = new JSONArray();
        for (int i : iArr) {
            jSONArray.put(i);
        }
        return jSONArray;
    }
}
