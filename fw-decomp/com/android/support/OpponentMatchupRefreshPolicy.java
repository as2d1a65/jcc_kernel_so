package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class OpponentMatchupRefreshPolicy {
    static boolean shouldRead(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        if (z) {
            return z2 || z3 || z4 || z5;
        }
        return false;
    }

    static final class Resolved {
        final boolean explicitEmptyCleared;
        final int matchupMyId;
        final int opponentId;
        final List<MatchupPairData> pairs;
        final int retainedPlanningOpponentId;

        Resolved(int i, int i2, List<MatchupPairData> list, int i3, boolean z) {
            this.opponentId = i;
            this.matchupMyId = i2;
            this.pairs = list;
            this.retainedPlanningOpponentId = i3;
            this.explicitEmptyCleared = z;
        }
    }

    private OpponentMatchupRefreshPolicy() {
    }

    static Resolved resolve(MatchupSnapshot matchupSnapshot, long j, long j2, List<PlayerInfo> list, int i, boolean z) {
        int i2;
        int i3;
        boolean zIsFresh = matchupSnapshot.isFresh(j, j2);
        if (zIsFresh) {
            i2 = matchupSnapshot.opponentId;
            list = matchupSnapshot.pairs.isEmpty() ? null : matchupSnapshot.pairs;
            i3 = matchupSnapshot.myId;
        } else {
            i2 = -1;
            i3 = -1;
        }
        List<MatchupPairData> list2 = list;
        boolean z2 = zIsFresh && matchupSnapshot.isExplicitEmpty();
        int iResolve = OpponentSelectionPolicy.resolve(i2, i3, list2, list);
        if (z2) {
            return new Resolved(-1, i3, list2, -1, true);
        }
        int iRetainDuringActiveMatch = OpponentSelectionPolicy.retainDuringActiveMatch(iResolve, i, z, list);
        return new Resolved(iRetainDuringActiveMatch, i3, list2, iRetainDuringActiveMatch, false);
    }
}
