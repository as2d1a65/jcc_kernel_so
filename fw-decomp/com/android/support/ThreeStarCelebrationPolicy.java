package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public final class ThreeStarCelebrationPolicy {
    public static final int MIN_CELEBRATED_STAR = 3;
    private boolean baselineTaken;
    private final Set<Integer> celebrated = new HashSet();

    public static int starOf(int i) {
        if (i >= 40000 && i < 50000) {
            return 4;
        }
        if (i >= 30000 && i < 40000) {
            return 3;
        }
        if (i < 20000 || i >= 30000) {
            return (i < 10000 || i >= 20000) ? 0 : 1;
        }
        return 2;
    }

    public static final class Hit {
        public final int baseHeroId;
        public final int heroId;
        public final int star;

        Hit(int i, int i2, int i3) {
            this.heroId = i;
            this.baseHeroId = i2;
            this.star = i3;
        }
    }

    public void resetForNewMatch() {
        this.celebrated.clear();
        this.baselineTaken = false;
    }

    public List<Hit> inspect(int[] iArr, int[] iArr2) {
        List<Hit> listCollect = collect(iArr2, collect(iArr, null));
        if (this.baselineTaken) {
            return listCollect == null ? Collections.emptyList() : listCollect;
        }
        this.baselineTaken = true;
        return Collections.emptyList();
    }

    private List<Hit> collect(int[] iArr, List<Hit> list) {
        if (iArr == null) {
            return list;
        }
        for (int i : iArr) {
            int iStarOf = starOf(i);
            if (iStarOf >= 3) {
                int iBaseIdOf = baseIdOf(i);
                if (this.celebrated.add(Integer.valueOf((iBaseIdOf * 8) + iStarOf))) {
                    if (list == null) {
                        list = new ArrayList<>(2);
                    }
                    list.add(new Hit(i, iBaseIdOf, iStarOf));
                }
            }
        }
        return list;
    }

    public static int baseIdOf(int i) {
        int iStarOf = starOf(i);
        return iStarOf <= 1 ? i : i - ((iStarOf - 1) * 10000);
    }
}
