package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class RankWarningAnchor {
    public final boolean hasMeasuredName;
    public final float height;
    public final float nameX;
    public final int playerId;
    public final float width;
    public final float x;
    public final float y;

    private RankWarningAnchor(int i, float f, float f2, float f3, float f4, float f5, boolean z) {
        this.playerId = i;
        this.x = f;
        this.y = f2;
        this.width = f3;
        this.height = f4;
        this.nameX = f5;
        this.hasMeasuredName = z;
    }

    public static RankWarningAnchor fromNormalized(int i, float f, float f2, float f3, float f4) {
        return fromNormalized(i, f, f2, f3, f4, f);
    }

    public static RankWarningAnchor fromNormalized(int i, float f, float f2, float f3, float f4, float f5) {
        if (i < 0 || i > 7 || !isFinite(f) || !isFinite(f2) || !isFinite(f3) || !isFinite(f4) || !isFinite(f5) || f < 0.0f || f2 < 0.0f || f3 <= 0.0f || f4 <= 0.0f || f + f3 > 1.0f || f2 + f4 > 1.0f || f5 < 0.0f || f5 > 1.0f) {
            return null;
        }
        return new RankWarningAnchor(i, f, f2, f3, f4, f5, f5 != f);
    }

    public static List<RankWarningAnchor> parse(JSONArray jSONArray) {
        if (jSONArray == null || jSONArray.length() == 0) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
            if (jSONObjectOptJSONObject != null) {
                float fOptDouble = (float) jSONObjectOptJSONObject.optDouble("x", Double.NaN);
                RankWarningAnchor rankWarningAnchorFromNormalized = fromNormalized(jSONObjectOptJSONObject.optInt("id", -1), fOptDouble, (float) jSONObjectOptJSONObject.optDouble("y", Double.NaN), (float) jSONObjectOptJSONObject.optDouble("width", Double.NaN), (float) jSONObjectOptJSONObject.optDouble("height", Double.NaN), (float) jSONObjectOptJSONObject.optDouble("nameX", fOptDouble));
                if (rankWarningAnchorFromNormalized != null) {
                    arrayList.add(rankWarningAnchorFromNormalized);
                }
            }
        }
        return arrayList;
    }

    private static boolean isFinite(float f) {
        return (Float.isNaN(f) || Float.isInfinite(f)) ? false : true;
    }
}
