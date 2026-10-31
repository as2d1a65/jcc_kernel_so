package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class GameHudSessionPolicy {
    static boolean hasBoardEvidence(int i, boolean z, int i2) {
        if (i < 0 || i >= 8) {
            return false;
        }
        return z || i2 >= 4;
    }

    private static boolean isForCurrentMatch(long j, long j2, long j3) {
        return j >= j2 && j3 >= j;
    }

    private static boolean isFreshForMatch(long j, long j2, long j3, long j4) {
        return j >= j2 && j3 >= j && j3 - j <= j4;
    }

    private GameHudSessionPolicy() {
    }

    static final class SnapshotEvidence {
        final boolean hasLocalBoard;
        final int localPlayerId;

        SnapshotEvidence(int i, boolean z) {
            this.localPlayerId = i;
            this.hasLocalBoard = z;
        }

        boolean hasLocalPlayer() {
            return this.localPlayerId >= 0;
        }
    }

    static SnapshotEvidence collectSnapshotEvidence(List<PlayerInfo> list, List<BoardPositionInfo> list2) {
        int i;
        int i2;
        if (list != null) {
            for (PlayerInfo playerInfo : list) {
                if (playerInfo != null && playerInfo.isMe && playerInfo.playerId >= 0 && playerInfo.playerId < 8) {
                    i = playerInfo.playerId;
                    break;
                }
            }
            i = -1;
        } else {
            i = -1;
        }
        boolean[] zArr = new boolean[8];
        boolean z = false;
        if (list2 != null) {
            i2 = 0;
            for (BoardPositionInfo boardPositionInfo : list2) {
                if (boardPositionInfo != null && boardPositionInfo.playerId >= 0 && boardPositionInfo.playerId < 8) {
                    if (!zArr[boardPositionInfo.playerId]) {
                        zArr[boardPositionInfo.playerId] = true;
                        i2++;
                    }
                    if (boardPositionInfo.playerId == i) {
                        z = true;
                    }
                }
            }
        } else {
            i2 = 0;
        }
        return new SnapshotEvidence(i, hasBoardEvidence(i, z, i2));
    }

    static boolean isReady(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, long j, long j2, long j3, long j4, long j5) {
        if (!hasCurrentMatchEvidence(z, z3, z4, z5, j, j2, j3, j4) || j5 < 0) {
            return false;
        }
        return isFreshForMatch(j3, j, j4, j5);
    }

    static boolean hasCurrentMatchEvidence(boolean z, boolean z2, boolean z3, boolean z4, long j, long j2, long j3, long j4) {
        return z && z2 && z3 && z4 && j > 0 && isForCurrentMatch(j2, j, j4) && isForCurrentMatch(j3, j, j4);
    }
}
