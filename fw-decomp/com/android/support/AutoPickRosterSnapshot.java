package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
final class AutoPickRosterSnapshot {
    private AutoPickRosterSnapshot() {
    }

    static String of(List<HeroData> list, List<HeroData> list2) {
        ArrayList arrayList = new ArrayList();
        for (HeroData heroData : HeroPoolMerge.merge(list, list2)) {
            if (HeroDisplayName.isKnown(heroData) && heroData.cost >= 1 && heroData.cost <= 6) {
                arrayList.add(heroData.heroId + ":" + heroData.cost + ":" + heroData.chineseName + ":" + heroData.englishName);
            }
        }
        Collections.sort(arrayList);
        return arrayList.toString();
    }
}
