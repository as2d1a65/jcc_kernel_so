package com.android.support;

/* loaded from: classes.dex */
final class RankOpponentAnchorApplyPolicy {
    private RankOpponentAnchorApplyPolicy() {
    }

    static boolean shouldPublish(boolean z, boolean z2, boolean z3, boolean z4) {
        return z || OpponentDisplayPolicy.shouldShowOpponentAnchor(z2, z3, z4);
    }
}
