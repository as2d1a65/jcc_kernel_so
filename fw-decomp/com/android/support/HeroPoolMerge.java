package com.android.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
final class HeroPoolMerge {
    private HeroPoolMerge() {
    }

    static List<HeroData> merge(List<HeroData> list, List<HeroData> list2) {
        HashMap map = new HashMap();
        if (list != null) {
            for (HeroData heroData : list) {
                if (heroData != null && !map.containsKey(Integer.valueOf(heroData.heroId))) {
                    map.put(Integer.valueOf(heroData.heroId), heroData);
                }
            }
        }
        if (list2 != null) {
            for (HeroData heroData2 : list2) {
                if (heroData2 != null) {
                    map.put(Integer.valueOf(heroData2.heroId), heroData2);
                }
            }
        }
        return new ArrayList(map.values());
    }
}
