package com.android.support;

import com.android.support.SpecifiedPredictionPreview;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcSpecifiedPredictionPreviewParser {
    private IpcSpecifiedPredictionPreviewParser() {
    }

    static SpecifiedPredictionPreview parse(String str, JSONObject jSONObject) throws JSONException {
        String strString = string(jSONObject, "kind");
        if (!str.equals(strString)) {
            throw new JSONException("Preview kind mismatch");
        }
        String strString2 = string(jSONObject, "matchGeneration");
        if (strString2.length() != 22) {
            throw new JSONException("Invalid preview match");
        }
        long jInteger = integer(jSONObject, "sessionGeneration", 1L, Long.MAX_VALUE);
        long jInteger2 = integer(jSONObject, "revision", 1L, Long.MAX_VALUE);
        long jInteger3 = integer(jSONObject, "frame", 0L, Long.MAX_VALUE);
        boolean zBool = bool(jSONObject, "ready");
        String strString3 = string(jSONObject, "state");
        String strString4 = string(jSONObject, "message");
        JSONArray jSONArray = jSONObject.getJSONArray("items");
        if (jSONArray.length() > 32 || ((zBool && jSONArray.length() == 0) || ((zBool && SpecifiedPredictionPreview.KIND_SHOP.equals(strString) && jSONArray.length() != 5) || (!zBool && jSONArray.length() != 0)))) {
            throw new JSONException("Invalid preview item count");
        }
        ArrayList arrayList = new ArrayList(jSONArray.length());
        HashSet hashSet = new HashSet();
        int i = 0;
        while (i < jSONArray.length()) {
            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
            JSONArray jSONArray2 = jSONArray;
            int iInteger = (int) integer(jSONObject2, "slot", 0L, 2147483647L);
            if (!hashSet.add(Integer.valueOf(iInteger))) {
                throw new JSONException("Duplicate preview slot");
            }
            if ((SpecifiedPredictionPreview.KIND_SHOP.equals(strString) && iInteger >= 5) || (SpecifiedPredictionPreview.KIND_HEX.equals(strString) && iInteger >= 3)) {
                throw new JSONException("Preview slot out of range");
            }
            int iInteger2 = (int) integer(jSONObject2, "inputId", 0L, 2147483647L);
            if (SpecifiedPredictionPreview.KIND_RECAST.equals(strString) && iInteger2 == 0) {
                throw new JSONException("Missing recast input");
            }
            arrayList.add(new SpecifiedPredictionPreview.Item(iInteger, iInteger2, (int) integer(jSONObject2, "resultId", 1L, 2147483647L), bool(jSONObject2, "selected"), (int) integer(jSONObject2, "tier", 0L, 2147483647L)));
            i++;
            jSONArray = jSONArray2;
            strString3 = strString3;
        }
        return new SpecifiedPredictionPreview(strString, strString2, jInteger, zBool, strString3, strString4, jInteger2, jInteger3, arrayList);
    }

    private static String string(JSONObject jSONObject, String str) throws JSONException {
        Object obj = jSONObject.get(str);
        if (!(obj instanceof String)) {
            throw new JSONException("Expected string: " + str);
        }
        return (String) obj;
    }

    private static boolean bool(JSONObject jSONObject, String str) throws JSONException {
        Object obj = jSONObject.get(str);
        if (!(obj instanceof Boolean)) {
            throw new JSONException("Expected boolean: " + str);
        }
        return ((Boolean) obj).booleanValue();
    }

    private static long integer(JSONObject jSONObject, String str, long j, long j2) throws JSONException {
        Object obj = jSONObject.get(str);
        if (!(obj instanceof Integer) && !(obj instanceof Long)) {
            throw new JSONException("Expected integer: " + str);
        }
        long jLongValue = ((Number) obj).longValue();
        if (jLongValue < j || jLongValue > j2) {
            throw new JSONException("Integer out of range: " + str);
        }
        return jLongValue;
    }
}
