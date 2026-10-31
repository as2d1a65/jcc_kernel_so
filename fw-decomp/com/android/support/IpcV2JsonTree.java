package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class IpcV2JsonTree {
    private IpcV2JsonTree() {
    }

    public static List<Object> toList(JSONArray jSONArray) throws JSONException {
        if (jSONArray == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(jSONArray.length());
        for (int i = 0; i < jSONArray.length(); i++) {
            arrayList.add(toJavaValue(jSONArray.get(i)));
        }
        return Collections.unmodifiableList(arrayList);
    }

    private static Map<String, Object> toMap(JSONObject jSONObject) throws JSONException {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Iterator<String> itKeys = jSONObject.keys();
        while (itKeys.hasNext()) {
            String next = itKeys.next();
            linkedHashMap.put(next, toJavaValue(jSONObject.get(next)));
        }
        return Collections.unmodifiableMap(linkedHashMap);
    }

    private static Object toJavaValue(Object obj) throws JSONException {
        if (obj == null || obj == JSONObject.NULL) {
            return null;
        }
        if (obj instanceof JSONArray) {
            return toList((JSONArray) obj);
        }
        if (obj instanceof JSONObject) {
            return toMap((JSONObject) obj);
        }
        if ((obj instanceof String) || (obj instanceof Boolean) || (obj instanceof Number)) {
            return obj;
        }
        throw new IllegalArgumentException("unsupported JSON value: " + obj.getClass().getName());
    }
}
