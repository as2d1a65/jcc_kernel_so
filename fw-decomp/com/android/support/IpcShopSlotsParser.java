package com.android.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcShopSlotsParser {
    static final long FINGERPRINT_SEED = -3750763034362895579L;

    static long mixFingerprint(long j, int i) {
        for (int i2 = 0; i2 < 32; i2 += 8) {
            j = (j ^ ((i >>> i2) & 255)) * 1099511628211L;
        }
        return j;
    }

    static final class Payload {
        final long fingerprint;
        final List<ShopSlotData> slots;

        private Payload(long j, List<ShopSlotData> list) {
            this.fingerprint = j;
            this.slots = list;
        }
    }

    private IpcShopSlotsParser() {
    }

    static Payload parse(JSONObject jSONObject) throws Exception {
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("slots");
        return new Payload(fingerprint(jSONArrayOptJSONArray), parseList(jSONArrayOptJSONArray));
    }

    static long fingerprint(JSONArray jSONArray) {
        int length = jSONArray == null ? 0 : jSONArray.length();
        long jMixFingerprint = mixFingerprint(FINGERPRINT_SEED, length);
        for (int i = 0; i < length; i++) {
            JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
            int iOptInt = i + 1;
            if (jSONObjectOptJSONObject != null) {
                iOptInt = jSONObjectOptJSONObject.optInt("index", iOptInt);
            }
            jMixFingerprint = mixFingerprint(mixFingerprint(jMixFingerprint, iOptInt), jSONObjectOptJSONObject == null ? 0 : jSONObjectOptJSONObject.optInt("heroId", 0));
        }
        return jMixFingerprint;
    }

    static List<ShopSlotData> parseList(JSONArray jSONArray) throws Exception {
        ArrayList arrayList = new ArrayList();
        if (jSONArray != null) {
            int i = 0;
            while (i < jSONArray.length()) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                ShopSlotData shopSlotData = new ShopSlotData();
                i++;
                shopSlotData.index = jSONObject.optInt("index", i);
                shopSlotData.heroId = jSONObject.optInt("heroId", 0);
                arrayList.add(shopSlotData);
            }
        }
        return arrayList;
    }

    static String diagnosticHeroIds(List<ShopSlotData> list) {
        StringBuilder sb = new StringBuilder();
        Iterator<ShopSlotData> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next().heroId).append(",");
        }
        return sb.toString();
    }
}
