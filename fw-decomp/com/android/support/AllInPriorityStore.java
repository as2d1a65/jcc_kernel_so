package com.android.support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class AllInPriorityStore {
    static final int MAX_ENTRIES = 64;

    private AllInPriorityStore() {
    }

    static int[] decode(String str, String str2) {
        if (str == null || str.isEmpty() || str2 == null || str2.isEmpty()) {
            return new int[0];
        }
        try {
            JSONArray jSONArrayOptJSONArray = new JSONObject(str).optJSONArray(str2);
            if (jSONArrayOptJSONArray == null) {
                return new int[0];
            }
            int iMin = Math.min(jSONArrayOptJSONArray.length(), 64);
            int[] iArr = new int[iMin];
            int i = 0;
            for (int i2 = 0; i2 < jSONArrayOptJSONArray.length() && i < iMin; i2++) {
                int iOptInt = jSONArrayOptJSONArray.optInt(i2, 0);
                if (iOptInt > 0) {
                    int i3 = 0;
                    while (true) {
                        if (i3 < i) {
                            if (iArr[i3] == iOptInt) {
                                break;
                            }
                            i3++;
                        } else {
                            iArr[i] = iOptInt;
                            i++;
                            break;
                        }
                    }
                }
            }
            if (i == iMin) {
                return iArr;
            }
            int[] iArr2 = new int[i];
            System.arraycopy(iArr, 0, iArr2, 0, i);
            return iArr2;
        } catch (Exception unused) {
            return new int[0];
        }
    }

    static String encode(String str, String str2, int[] iArr) throws JSONException {
        JSONObject jSONObject;
        if (str2 == null || str2.isEmpty()) {
            return str == null ? "" : str;
        }
        if (str != null) {
            try {
                jSONObject = str.isEmpty() ? new JSONObject() : new JSONObject(str);
            } catch (Exception unused) {
                jSONObject = new JSONObject();
            }
        }
        try {
            JSONArray jSONArray = new JSONArray();
            if (iArr != null) {
                for (int i : iArr) {
                    if (i > 0) {
                        if (jSONArray.length() >= 64) {
                            break;
                        }
                        jSONArray.put(i);
                    }
                }
            }
            if (jSONArray.length() == 0) {
                jSONObject.remove(str2);
            } else {
                jSONObject.put(str2, jSONArray);
            }
            return jSONObject.toString();
        } catch (Exception unused2) {
            return str == null ? "" : str;
        }
    }
}
