package com.android.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
final class HeroCostGroups {
    private HeroCostGroups() {
    }

    static Map<Integer, List<HeroData>> group(List<HeroData> list) {
        HashMap map = new HashMap();
        if (list != null) {
            for (HeroData heroData : list) {
                if (HeroDisplayName.isKnown(heroData)) {
                    List arrayList = (List) map.get(Integer.valueOf(heroData.cost));
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        map.put(Integer.valueOf(heroData.cost), arrayList);
                    }
                    arrayList.add(heroData);
                }
            }
        }
        return map;
    }
}
