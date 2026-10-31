package com.android.support;

import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcHexLevelsParser {
    private static int sanitizeStageLevel(int i) {
        if (i < 1 || i > 3) {
            return 0;
        }
        return i;
    }

    private IpcHexLevelsParser() {
    }

    static final class Payload {
        final boolean available;
        final int drawsPerStage;
        final int[] levels;
        final int[] livePool;
        final int livePoolTotalWeight;
        final int[] livePoolWeights;
        final String matchGeneration;
        final String reason;

        private Payload(String str, int[] iArr, boolean z, String str2, int[] iArr2, int[] iArr3, int i, int i2) {
            this.matchGeneration = str;
            this.levels = iArr;
            this.available = z;
            this.reason = str2 == null ? "" : str2;
            this.livePool = iArr2 == null ? new int[0] : iArr2;
            this.livePoolWeights = iArr3 == null ? new int[0] : iArr3;
            this.livePoolTotalWeight = Math.max(0, i);
            this.drawsPerStage = Math.max(0, i2);
        }

        boolean isForMatch(String str) {
            return this.matchGeneration.equals(str);
        }
    }

    static Payload parse(JSONObject jSONObject) {
        String strOptString = jSONObject.optString("matchGeneration", "");
        if (strOptString.length() != 22) {
            return null;
        }
        if (!jSONObject.optBoolean("available", true)) {
            return new Payload(strOptString, null, false, jSONObject.optString("reason", "unavailable"), null, null, 0, 0);
        }
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("levels");
        if (jSONArrayOptJSONArray == null) {
            return new Payload(strOptString, null, false, "invalid_levels", null, null, 0, 0);
        }
        int[] iArr = new int[4];
        boolean z = false;
        for (int i = 0; i < 3; i++) {
            int iSanitizeStageLevel = sanitizeStageLevel(jSONArrayOptJSONArray.optInt(i, 0));
            iArr[i] = iSanitizeStageLevel;
            if (iSanitizeStageLevel != 0) {
                z = true;
            }
        }
        iArr[3] = jSONArrayOptJSONArray.length() >= 4 ? sanitizeStageLevel(jSONArrayOptJSONArray.optInt(3, 0)) : 0;
        if (!z) {
            return new Payload(strOptString, null, false, "levels_pending", null, null, 0, 0);
        }
        Pool livePool = parseLivePool(jSONObject);
        return new Payload(strOptString, iArr, true, "", livePool.ids, livePool.weights, jSONObject.optInt("livePoolTotalWeight", 0), jSONObject.optInt("drawsPerStage", 0));
    }

    private static final class Pool {
        final int[] ids;
        final int[] weights;

        Pool(int[] iArr, int[] iArr2) {
            this.ids = iArr;
            this.weights = iArr2;
        }
    }

    private static Pool parseLivePool(JSONObject jSONObject) {
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("livePool");
        if (jSONArrayOptJSONArray == null || jSONArrayOptJSONArray.length() == 0) {
            return new Pool(new int[0], new int[0]);
        }
        JSONArray jSONArrayOptJSONArray2 = jSONObject.optJSONArray("livePoolWeights");
        int length = jSONArrayOptJSONArray.length();
        int[] iArr = new int[length];
        int[] iArr2 = new int[jSONArrayOptJSONArray.length()];
        int i = 0;
        int i2 = 0;
        while (i < jSONArrayOptJSONArray.length()) {
            int iOptInt = jSONArrayOptJSONArray.optInt(i, 0);
            if (iOptInt > 0) {
                int i3 = 0;
                while (true) {
                    if (i3 < i2) {
                        if (iArr[i3] == iOptInt) {
                            break;
                        }
                        i3++;
                    } else {
                        iArr2[i2] = (jSONArrayOptJSONArray2 == null || i >= jSONArrayOptJSONArray2.length()) ? 0 : Math.max(0, jSONArrayOptJSONArray2.optInt(i, 0));
                        iArr[i2] = iOptInt;
                        i2++;
                    }
                }
            }
            i++;
        }
        if (i2 == length) {
            return new Pool(iArr, iArr2);
        }
        int[] iArr3 = new int[i2];
        int[] iArr4 = new int[i2];
        System.arraycopy(iArr, 0, iArr3, 0, i2);
        System.arraycopy(iArr2, 0, iArr4, 0, i2);
        return new Pool(iArr3, iArr4);
    }

    static String diagnosticLine(int[] iArr) {
        if (iArr == null || iArr.length < 4) {
            return "海克斯数据: 不可用";
        }
        return String.format("海克斯数据: [%d, %d, %d, %d]", Integer.valueOf(iArr[0]), Integer.valueOf(iArr[1]), Integer.valueOf(iArr[2]), Integer.valueOf(iArr[3]));
    }

    static String diagnosticLine(Payload payload) {
        if (payload.available) {
            return diagnosticLine(payload.levels);
        }
        return "海克斯数据已失效: " + payload.reason;
    }
}
