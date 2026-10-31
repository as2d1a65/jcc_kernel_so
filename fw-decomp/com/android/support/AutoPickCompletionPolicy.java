package com.android.support;

import com.android.support.lineup.HeroStarId;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/* loaded from: classes.dex */
final class AutoPickCompletionPolicy {
    private AutoPickCompletionPolicy() {
    }

    static int initialTargetStar(int i, boolean z, List<BoardPositionInfo> list) {
        if (z) {
            return Math.max(2, Math.min(3, actualStars(list).getOrDefault(Integer.valueOf(HeroStarId.baseOf(i)), 0).intValue() + 1));
        }
        return 2;
    }

    static Set<Integer> completed(Set<Integer> set, Map<Integer, Integer> map, List<BoardPositionInfo> list) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (set != null && map != null && list != null) {
            Map<Integer, Integer> mapActualStars = actualStars(list);
            for (Integer num : set) {
                if (num != null && num.intValue() > 0) {
                    int iTargetFor = HeroStarId.targetFor(map, num.intValue());
                    if (iTargetFor <= 0) {
                        iTargetFor = 2;
                    }
                    if (mapActualStars.getOrDefault(Integer.valueOf(HeroStarId.baseOf(num.intValue())), 0).intValue() >= iTargetFor) {
                        linkedHashSet.add(num);
                    }
                }
            }
        }
        return linkedHashSet;
    }

    private static Map<Integer, Integer> actualStars(List<BoardPositionInfo> list) {
        HashMap map = new HashMap();
        if (list != null) {
            for (BoardPositionInfo boardPositionInfo : list) {
                if (boardPositionInfo != null && boardPositionInfo.isMe) {
                    collect(map, boardPositionInfo.boardHeroIds, boardPositionInfo.boardBitmask);
                    collect(map, boardPositionInfo.benchHeroIds, boardPositionInfo.benchBitmask);
                }
            }
        }
        return map;
    }

    private static void collect(Map<Integer, Integer> map, int[] iArr, int i) {
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (((1 << i2) & i) != 0) {
                int iBaseOf = HeroStarId.baseOf(iArr[i2]);
                int iStarOf = HeroStarId.starOf(iArr[i2]);
                if (iBaseOf > 0 && iStarOf > 0) {
                    map.merge(Integer.valueOf(iBaseOf), Integer.valueOf(iStarOf), new BiFunction() { // from class: com.android.support.AutoPickCompletionPolicy$$ExternalSyntheticLambda0
                        @Override // java.util.function.BiFunction
                        public final Object apply(Object obj, Object obj2) {
                            return Integer.valueOf(Math.max(((Integer) obj).intValue(), ((Integer) obj2).intValue()));
                        }
                    });
                }
            }
        }
    }
}
