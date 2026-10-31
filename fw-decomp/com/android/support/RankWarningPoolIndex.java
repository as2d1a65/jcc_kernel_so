package com.android.support;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
final class RankWarningPoolIndex {
    static int baseHeroId(int i) {
        return (i < 40000 || i >= 50000) ? (i < 30000 || i >= 40000) ? (i < 20000 || i >= 30000) ? i : i - 10000 : i - AccessibilityNodeInfoCompat.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_MAX_LENGTH : i - 30000;
    }

    static final class Entry {
        final int remaining;
        final int total;

        Entry(int i, int i2) {
            this.remaining = i;
            this.total = i2;
        }
    }

    private RankWarningPoolIndex() {
    }

    static Map<Integer, Entry> build(List<HeroData> list, List<HeroData> list2) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        add(linkedHashMap, list);
        add(linkedHashMap, list2);
        return linkedHashMap;
    }

    private static void add(Map<Integer, Entry> map, List<HeroData> list) {
        int iMin;
        if (list == null) {
            return;
        }
        for (HeroData heroData : list) {
            if (heroData != null && heroData.heroId > 0 && heroData.currentCount >= 0) {
                int iBaseHeroId = baseHeroId(heroData.heroId);
                int iMax = Math.max(0, heroData.totalCount);
                if (iMax > 0) {
                    iMin = Math.min(heroData.currentCount, iMax);
                } else {
                    iMin = heroData.currentCount;
                }
                map.put(Integer.valueOf(iBaseHeroId), new Entry(Math.max(0, iMin), iMax));
            }
        }
    }
}
