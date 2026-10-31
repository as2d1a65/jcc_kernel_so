package com.android.support;

/* loaded from: classes.dex */
final class RankAnchorUiApplyPolicy {

    enum Action {
        WRITE,
        CLEAR,
        CHECK_REFRESH
    }

    private RankAnchorUiApplyPolicy() {
    }

    static Action decide(boolean z, RankAnchorSnapshot rankAnchorSnapshot, int i, boolean z2) {
        if (z) {
            if (rankAnchorSnapshot != null && rankAnchorSnapshot.timestamp != i) {
                return Action.WRITE;
            }
            return Action.CHECK_REFRESH;
        }
        if (i != -1 || z2) {
            return Action.CLEAR;
        }
        return Action.CHECK_REFRESH;
    }
}
