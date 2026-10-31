package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.support.ConfigPreferenceNamespaces;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class DynamicConfigSnapshotCodec {
    private static final String LINEUPS_KEY = "_lineups";
    private static final String TAG = "FloatingWindowService";

    private DynamicConfigSnapshotCodec() {
    }

    static void write(JSONObject jSONObject, SharedPreferences sharedPreferences, Context context, String str, List<ConfigPreferenceNamespaces.Namespace> list) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            if (entry.getKey().startsWith(str) && (entry.getValue() instanceof String)) {
                jSONObject2.put(entry.getKey(), (String) entry.getValue());
            }
        }
        jSONObject.put(LINEUPS_KEY, jSONObject2);
        for (ConfigPreferenceNamespaces.Namespace namespace : list) {
            jSONObject.put(namespace.jsonKey, SharedPreferencesJsonCodec.dump(context.getSharedPreferences(namespace.preferencesName, 0)));
        }
    }

    static boolean restore(JSONObject jSONObject, SharedPreferences sharedPreferences, Context context, String str, List<ConfigPreferenceNamespaces.Namespace> list) {
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(LINEUPS_KEY);
            if (jSONObjectOptJSONObject != null) {
                SharedPreferences.Editor editorEdit = sharedPreferences.edit();
                for (String str2 : sharedPreferences.getAll().keySet()) {
                    if (str2.startsWith(str) && !jSONObjectOptJSONObject.has(str2)) {
                        editorEdit.remove(str2);
                    }
                }
                Iterator<String> itKeys = jSONObjectOptJSONObject.keys();
                while (itKeys.hasNext()) {
                    String next = itKeys.next();
                    editorEdit.putString(next, jSONObjectOptJSONObject.optString(next, ""));
                }
                if (!editorEdit.commit()) {
                    Log.e(TAG, "恢复动态阵容写入失败");
                    return false;
                }
            }
            boolean z = true;
            for (ConfigPreferenceNamespaces.Namespace namespace : list) {
                if (!SharedPreferencesJsonCodec.restoreExact(context.getSharedPreferences(namespace.preferencesName, 0), jSONObject.optJSONObject(namespace.jsonKey))) {
                    Log.e(TAG, "恢复独立配置写入失败: " + namespace.preferencesName);
                    z = false;
                }
            }
            return z;
        } catch (Exception e) {
            Log.e(TAG, "恢复动态阵容/收藏失败", e);
            return false;
        }
    }
}
