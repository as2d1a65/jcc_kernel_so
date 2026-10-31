package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class RankWarningSelfFilter {
    private RankWarningSelfFilter() {
    }

    static boolean isSelf(int i, List<PlayerInfo> list, List<BoardPositionInfo> list2) {
        if (list != null) {
            for (PlayerInfo playerInfo : list) {
                if (playerInfo != null && playerInfo.playerId == i && playerInfo.isMe) {
                    return true;
                }
            }
        }
        if (list2 == null) {
            return false;
        }
        for (BoardPositionInfo boardPositionInfo : list2) {
            if (boardPositionInfo != null && boardPositionInfo.playerId == i && boardPositionInfo.isMe) {
                return true;
            }
        }
        return false;
    }
}
