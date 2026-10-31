package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class PlayersListRefreshPolicy {

    enum ApplyAction {
        SKIP,
        CACHE_ONLY,
        PAINT_LIST
    }

    static boolean shouldApply(int i, int i2) {
        return i != i2;
    }

    private PlayersListRefreshPolicy() {
    }

    static ApplyAction decide(int i, int i2, boolean z) {
        if (shouldApply(i, i2)) {
            return z ? ApplyAction.PAINT_LIST : ApplyAction.CACHE_ONLY;
        }
        return ApplyAction.SKIP;
    }

    static boolean shouldClear(List<?> list) {
        return list == null || list.isEmpty();
    }
}
