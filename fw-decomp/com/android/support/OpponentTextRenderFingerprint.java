package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class OpponentTextRenderFingerprint {
    private OpponentTextRenderFingerprint() {
    }

    static long build(int i, List<PlayerInfo> list, List<MatchupPairData> list2, int i2, boolean z, boolean z2, int i3, int i4, int i5, int i6) {
        long jAddCollectionSizes = OpponentTextRefreshPolicy.addCollectionSizes(OpponentTextRefreshPolicy.begin(i, i2, z, z2, i3), list == null ? 0 : list.size(), list2 != null ? list2.size() : 0);
        if (list != null) {
            long jAddPlayer = jAddCollectionSizes;
            for (PlayerInfo playerInfo : list) {
                if (playerInfo != null) {
                    jAddPlayer = OpponentTextRefreshPolicy.addPlayer(jAddPlayer, playerInfo.playerId, playerInfo.hp, playerInfo.level, playerInfo.gold, playerInfo.rank, playerInfo.isMe, playerInfo.name, playerInfo.isPartner, playerInfo.teamId, playerInfo.enemyId);
                }
            }
            jAddCollectionSizes = jAddPlayer;
        }
        if (list2 != null) {
            for (MatchupPairData matchupPairData : list2) {
                if (matchupPairData != null) {
                    jAddCollectionSizes = OpponentTextRefreshPolicy.addPair(jAddCollectionSizes, matchupPairData.player1Id, matchupPairData.player2Id, matchupPairData.ghostSourcePlayer);
                }
            }
        }
        return OpponentTextRefreshPolicy.addSelectorSummary(jAddCollectionSizes, i4, i5, i6);
    }
}
