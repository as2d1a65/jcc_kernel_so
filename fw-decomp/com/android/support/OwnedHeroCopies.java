package com.android.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class OwnedHeroCopies {
    static final OwnedHeroCopies EMPTY = new OwnedHeroCopies(Collections.emptyMap());
    private final Map<Integer, Integer> values;

    private OwnedHeroCopies(Map<Integer, Integer> map) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap(map));
    }

    static OwnedHeroCopies parse(JSONArray jSONArray) {
        if (jSONArray == null || jSONArray.length() == 0) {
            return EMPTY;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
            if (jSONObjectOptJSONObject != null) {
                int iOptInt = jSONObjectOptJSONObject.optInt("heroId", 0);
                int iOptInt2 = jSONObjectOptJSONObject.optInt("copies", -1);
                if (iOptInt > 0 && iOptInt2 >= 0) {
                    linkedHashMap.put(Integer.valueOf(iOptInt), Integer.valueOf(iOptInt2));
                }
            }
        }
        return linkedHashMap.isEmpty() ? EMPTY : new OwnedHeroCopies(linkedHashMap);
    }

    int get(int i) {
        Integer num = this.values.get(Integer.valueOf(i));
        if (num == null) {
            return -1;
        }
        return num.intValue();
    }

    Set<Integer> heroIds() {
        return this.values.keySet();
    }

    Map<Integer, Integer> asMap() {
        return this.values;
    }
}
