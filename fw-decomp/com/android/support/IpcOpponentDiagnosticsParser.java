package com.android.support;

import androidx.core.app.NotificationCompat;
import androidx.core.os.EnvironmentCompat;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcOpponentDiagnosticsParser {
    private IpcOpponentDiagnosticsParser() {
    }

    static String formatLine(JSONObject jSONObject) {
        String strOptString = jSONObject.optString(NotificationCompat.CATEGORY_STATUS, EnvironmentCompat.MEDIA_UNKNOWN);
        int iMax = Math.max(0, Math.min(jSONObject.optInt("count", 0), 32));
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("values");
        StringBuilder sb = new StringBuilder();
        if (jSONArrayOptJSONArray != null) {
            int iMin = Math.min(iMax, jSONArrayOptJSONArray.length());
            for (int i = 0; i < iMin; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(jSONArrayOptJSONArray.optInt(i, -1));
            }
        }
        return "[opponent-diag] status=" + strOptString + " count=" + iMax + " values=[" + ((Object) sb) + "]";
    }
}
