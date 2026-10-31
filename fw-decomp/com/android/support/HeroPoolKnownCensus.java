package com.android.support;

import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
final class HeroPoolKnownCensus {
    final int knownUnique;
    final int lockedCount;

    HeroPoolKnownCensus(int i, int i2) {
        this.knownUnique = i;
        this.lockedCount = i2;
    }

    static HeroPoolKnownCensus count(List<HeroData> list, List<HeroData> list2) {
        HashMap map = new HashMap();
        if (list != null) {
            for (HeroData heroData : list) {
                if (HeroDisplayName.isKnown(heroData) && !map.containsKey(Integer.valueOf(heroData.heroId))) {
                    map.put(Integer.valueOf(heroData.heroId), heroData);
                }
            }
        }
        if (list2 != null) {
            for (HeroData heroData2 : list2) {
                if (HeroDisplayName.isKnown(heroData2)) {
                    map.put(Integer.valueOf(heroData2.heroId), heroData2);
                }
            }
        }
        return new HeroPoolKnownCensus(map.size(), list2 != null ? list2.size() : 0);
    }
}
