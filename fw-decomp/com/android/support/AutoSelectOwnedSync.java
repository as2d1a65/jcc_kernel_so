package com.android.support;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
final class AutoSelectOwnedSync {
    static boolean canRestoreLegacy(boolean z, boolean z2) {
        return z || !z2;
    }

    private AutoSelectOwnedSync() {
    }

    static Set<Integer> apply(Set<Integer> set, Set<Integer> set2, Set<Integer> set3, List<HeroData> list) {
        HashSet hashSet = new HashSet();
        if (set3 != null && list != null) {
            for (HeroData heroData : list) {
                if (heroData != null && set3.contains(Integer.valueOf(heroData.heroId))) {
                    hashSet.add(Integer.valueOf(heroData.heroId));
                }
            }
        }
        if (set == null || set2 == null) {
            return hashSet;
        }
        synchronized (set) {
            hashSet.removeAll(manualSelection(set, set2));
            set.removeAll(set2);
            set.addAll(hashSet);
        }
        return hashSet;
    }

    static Set<Integer> manualSelection(Set<Integer> set, Set<Integer> set2) {
        HashSet hashSet;
        synchronized (set) {
            hashSet = new HashSet(set);
            hashSet.removeAll(set2);
        }
        return hashSet;
    }

    static void clearAutomatic(Set<Integer> set, Set<Integer> set2) {
        synchronized (set) {
            set.removeAll(set2);
            set2.clear();
        }
    }
}
