package com.android.support;

import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class SharedPreferencesJsonCodec {
    private SharedPreferencesJsonCodec() {
    }

    static JSONObject dump(SharedPreferences sharedPreferences) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Set) {
                JSONArray jSONArray = new JSONArray();
                Iterator it = ((Set) value).iterator();
                while (it.hasNext()) {
                    jSONArray.put(String.valueOf(it.next()));
                }
                jSONObject.put(entry.getKey(), jSONArray);
            } else if (value != null) {
                jSONObject.put(entry.getKey(), value);
            }
        }
        return jSONObject;
    }

    static boolean restoreExact(SharedPreferences sharedPreferences, JSONObject jSONObject) {
        if (jSONObject == null) {
            return true;
        }
        SharedPreferences.Editor editorClear = sharedPreferences.edit().clear();
        Iterator<String> itKeys = jSONObject.keys();
        while (itKeys.hasNext()) {
            String next = itKeys.next();
            Object objOpt = jSONObject.opt(next);
            if (objOpt instanceof Boolean) {
                editorClear.putBoolean(next, ((Boolean) objOpt).booleanValue());
            } else if (objOpt instanceof Integer) {
                editorClear.putInt(next, ((Integer) objOpt).intValue());
            } else if (objOpt instanceof Long) {
                editorClear.putLong(next, ((Long) objOpt).longValue());
            } else if (objOpt instanceof Number) {
                editorClear.putFloat(next, ((Number) objOpt).floatValue());
            } else if (objOpt instanceof JSONArray) {
                HashSet hashSet = new HashSet();
                JSONArray jSONArray = (JSONArray) objOpt;
                for (int i = 0; i < jSONArray.length(); i++) {
                    hashSet.add(jSONArray.optString(i, ""));
                }
                editorClear.putStringSet(next, hashSet);
            } else if (objOpt != null) {
                editorClear.putString(next, String.valueOf(objOpt));
            }
        }
        return editorClear.commit();
    }
}
