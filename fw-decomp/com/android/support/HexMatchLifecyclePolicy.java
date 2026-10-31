package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class HexMatchLifecyclePolicy {
    static final long ACTIVITY_STALE_MAX_AGE_MS = 15000;
    static final long UNSET_FINGERPRINT = Long.MIN_VALUE;

    static boolean hasLiveActivity(long j, long j2) {
        return j > 0 || j2 > 0;
    }

    static boolean shouldAcceptLivePlayerClock(boolean z, int i, int i2) {
        return (z && i == i2) ? false : true;
    }

    static boolean shouldAcceptLiveRankClock(boolean z, long j, long j2) {
        return !z || j > j2;
    }

    static boolean shouldHoldLeftoverVoiceOnHide(boolean z) {
        return z;
    }

    static boolean shouldKeepLeftoverSkip(boolean z, boolean z2) {
        return z2 && !z;
    }

    static boolean shouldReadRankSnapshot(boolean z) {
        return !z;
    }

    static boolean shouldSkipLeftoverVoice(boolean z, int i, int i2, int i3, int i4, long j, long j2, long j3, long j4) {
        if (!z) {
            return false;
        }
        if (j2 != Long.MIN_VALUE) {
            return j == 0 || j == j2;
        }
        return (i2 >= 0 && i == i2) || (i4 >= 0 && i3 == i4) || ((j4 > Long.MIN_VALUE ? 1 : (j4 == Long.MIN_VALUE ? 0 : -1)) != 0 && (j3 > j4 ? 1 : (j3 == j4 ? 0 : -1)) == 0);
    }

    static boolean shouldTreatHideAsStaleLobby(boolean z, boolean z2) {
        return z || z2;
    }

    static final class Transition {
        final boolean hideOverlays;
        final boolean restoreOverlays;

        Transition(boolean z, boolean z2) {
            this.hideOverlays = z;
            this.restoreOverlays = z2;
        }
    }

    private HexMatchLifecyclePolicy() {
    }

    static boolean hidBecauseStaleOnly(int i, boolean z) {
        return z && !RankWarningAnchorMath.shouldSuppressInGameOverlays(i);
    }

    static boolean leftoverIdentityUnchanged(boolean z, long j, List<PlayerInfo> list) {
        if (z) {
            return leftoverRosterStillMatches(j, list);
        }
        return false;
    }

    static boolean leftoverRosterStillMatches(long j, List<PlayerInfo> list) {
        if (j == Long.MIN_VALUE) {
            return false;
        }
        return list == null || list.isEmpty() || rosterFingerprint(list) == j;
    }

    static boolean shouldSkipLeftoverVoice(boolean z, int i, int i2, int i3, int i4) {
        return shouldSkipLeftoverVoice(z, i, i2, i3, i4, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE);
    }

    static boolean shouldSkipLeftoverVoice(boolean z, int i, int i2, int i3, int i4, List<PlayerInfo> list, long j, List<BoardPositionInfo> list2, long j2) {
        if (!z) {
            return false;
        }
        if (j != Long.MIN_VALUE) {
            return leftoverRosterStillMatches(j, list);
        }
        return shouldSkipLeftoverVoice(true, i, i2, i3, i4, rosterFingerprint(list), j, boardFingerprint(list2), j2);
    }

    static long rosterFingerprint(List<PlayerInfo> list) {
        if (list == null || list.isEmpty()) {
            return 0L;
        }
        long jHashCode = 1;
        for (PlayerInfo playerInfo : list) {
            if (playerInfo != null) {
                jHashCode = (((jHashCode * 31) + playerInfo.playerId) * 31) + (playerInfo.name == null ? 0 : r2.hashCode());
            }
        }
        return jHashCode;
    }

    static long boardFingerprint(List<BoardPositionInfo> list) {
        if (list == null || list.isEmpty()) {
            return 0L;
        }
        long j = 1;
        for (BoardPositionInfo boardPositionInfo : list) {
            if (boardPositionInfo != null) {
                j = (((((j * 31) + boardPositionInfo.playerId) * 31) + boardPositionInfo.boardBitmask) * 31) + boardPositionInfo.benchBitmask;
                if (boardPositionInfo.boardHeroIds != null) {
                    for (int i = 0; i < boardPositionInfo.boardHeroIds.length; i++) {
                        j = (j * 31) + r2[i];
                    }
                }
            }
        }
        return j;
    }

    static boolean isActivityStale(long j, long j2, long j3, long j4) {
        return (!hasLiveActivity(j, j2) || RankWarningAnchorMath.isRecentRankActivity(j, j3, j4) || RankWarningAnchorMath.isRecentRankActivity(j2, j3, j4)) ? false : true;
    }

    static Transition evaluate(boolean z, int i, boolean z2) {
        return evaluate(z, i, z2, false, true);
    }

    static Transition evaluate(boolean z, int i, boolean z2, boolean z3) {
        return evaluate(z, i, z2, z3, true);
    }

    static Transition evaluate(boolean z, int i, boolean z2, boolean z3, boolean z4) {
        return evaluate(z, i, z2, z3, z4, false);
    }

    static Transition evaluate(boolean z, int i, boolean z2, boolean z3, boolean z4, boolean z5) {
        return evaluate(z, i, z2, z3, z4, z5, i == 0 && !z3);
    }

    static Transition evaluate(boolean z, int i, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        boolean z7 = false;
        if (!z) {
            return new Transition(false, false);
        }
        boolean z8 = (!z6 && (RankWarningAnchorMath.shouldSuppressInGameOverlays(i) || z3)) && !z2;
        if (z2 && z6 && z4 && !z5) {
            z7 = true;
        }
        return new Transition(z8, z7);
    }
}
