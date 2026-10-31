package com.android.support;

/* loaded from: classes.dex */
final class RankWarningDataApplyPolicy {

    enum Action {
        SKIP,
        UPDATE,
        RESET
    }

    private RankWarningDataApplyPolicy() {
    }

    static Action decide(boolean z, boolean z2) {
        if (z) {
            return z2 ? Action.UPDATE : Action.RESET;
        }
        return Action.SKIP;
    }
}
