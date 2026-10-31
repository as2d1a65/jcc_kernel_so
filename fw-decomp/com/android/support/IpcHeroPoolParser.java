package com.android.support;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcHeroPoolParser {

    static final class Payload {
        final List<HeroData> locked;
        final List<HeroData> pool;

        Payload(List<HeroData> list, List<HeroData> list2) {
            this.pool = list;
            this.locked = list2;
        }
    }

    private IpcHeroPoolParser() {
    }

    static Payload parse(JSONObject jSONObject) throws Exception {
        ArrayList arrayList;
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("pool");
        ArrayList arrayList2 = null;
        if (jSONArrayOptJSONArray != null) {
            arrayList = new ArrayList();
            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                JSONArray jSONArrayOptJSONArray2 = jSONArrayOptJSONArray.getJSONObject(i).optJSONArray("heroes");
                if (jSONArrayOptJSONArray2 != null) {
                    for (int i2 = 0; i2 < jSONArrayOptJSONArray2.length(); i2++) {
                        JSONObject jSONObject2 = jSONArrayOptJSONArray2.getJSONObject(i2);
                        int iOptInt = jSONObject2.optInt("heroID", 0);
                        int iOptInt2 = jSONObject2.optInt("cost", 0);
                        int iOptInt3 = jSONObject2.optInt("currentCount", 0);
                        int iOptInt4 = jSONObject2.optInt("totalCount", 0);
                        if (iOptInt > 0) {
                            arrayList.add(new HeroData(iOptInt, iOptInt2, iOptInt3, iOptInt4));
                        }
                    }
                }
            }
        } else {
            arrayList = null;
        }
        JSONArray jSONArrayOptJSONArray3 = jSONObject.optJSONArray("locked");
        if (jSONArrayOptJSONArray3 != null) {
            arrayList2 = new ArrayList();
            for (int i3 = 0; i3 < jSONArrayOptJSONArray3.length(); i3++) {
                JSONObject jSONObject3 = jSONArrayOptJSONArray3.getJSONObject(i3);
                int iOptInt5 = jSONObject3.optInt("heroId", 0);
                int iOptInt6 = jSONObject3.optInt("cost", 0);
                int iOptInt7 = jSONObject3.optInt("leftCount", 0);
                int iOptInt8 = jSONObject3.optInt("totalCount", 0);
                if (iOptInt5 > 0) {
                    HeroData heroData = new HeroData(iOptInt5, iOptInt6, iOptInt7, iOptInt8);
                    heroData.isLocked = true;
                    arrayList2.add(heroData);
                }
            }
        }
        return new Payload(arrayList, arrayList2);
    }
}
