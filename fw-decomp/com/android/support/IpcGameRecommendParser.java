package com.android.support;

import androidx.core.app.NotificationCompat;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcGameRecommendParser {

    static final class ActiveTargets {
        final boolean active;
        final int[] heroIds;

        ActiveTargets(boolean z, int[] iArr) {
            this.active = z;
            this.heroIds = iArr;
        }
    }

    private IpcGameRecommendParser() {
    }

    static String formatUiLine(JSONObject jSONObject) {
        return "[game-recommend-ui-diag] callback=" + Math.max(0, Math.min(3, jSONObject.optInt("callback", 0))) + " active=" + (jSONObject.optInt(OpponentCardingStatus.STATE_ACTIVE, 0) == 0 ? 0 : 1) + " observed=" + (jSONObject.optInt("observed", 0) != 0 ? 1 : 0);
    }

    static ActiveTargets parseActiveTargets(JSONObject jSONObject) {
        boolean z = jSONObject.optInt(OpponentCardingStatus.STATE_ACTIVE, 0) != 0;
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("heroIds");
        int iMin = jSONArrayOptJSONArray == null ? 0 : Math.min(jSONArrayOptJSONArray.length(), 32);
        int[] iArrCopyOf = new int[iMin];
        int i = 0;
        for (int i2 = 0; i2 < iMin; i2++) {
            int iOptInt = jSONArrayOptJSONArray.optInt(i2, 0);
            if (iOptInt > 0) {
                iArrCopyOf[i] = iOptInt;
                i++;
            }
        }
        if (i != iMin) {
            iArrCopyOf = Arrays.copyOf(iArrCopyOf, i);
        }
        return new ActiveTargets(z, iArrCopyOf);
    }

    static String formatHookLine(JSONObject jSONObject) {
        return "[game-recommend-hook-diag] event=" + Math.max(1, Math.min(3, jSONObject.optInt(NotificationCompat.CATEGORY_EVENT, 1))) + " argClass=" + Math.max(0, Math.min(2, jSONObject.optInt("argClass", 0))) + " readable=" + (jSONObject.optInt("readable", 0) == 0 ? 0 : 1) + " present=" + (jSONObject.optInt("present", 0) == 0 ? 0 : 1) + " active=" + (jSONObject.optInt(OpponentCardingStatus.STATE_ACTIVE, 0) == 0 ? 0 : 1) + " observed=" + (jSONObject.optInt("observed", 0) == 0 ? 0 : 1) + " star1Count=" + Math.max(0, Math.min(64, jSONObject.optInt("star1Count", 0))) + " star2Count=" + Math.max(0, Math.min(64, jSONObject.optInt("star2Count", 0))) + " star3Count=" + Math.max(0, Math.min(64, jSONObject.optInt("star3Count", 0))) + " otherStarCount=" + Math.max(0, Math.min(64, jSONObject.optInt("otherStarCount", 0)));
    }
}
