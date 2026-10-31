package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class DualPlayTeamPolicy {
    private DualPlayTeamPolicy() {
    }

    static void apply(List<PlayerInfo> list, boolean z) {
        if (!z || list == null) {
            if (list != null) {
                for (PlayerInfo playerInfo : list) {
                    if (playerInfo != null) {
                        playerInfo.isPartner = false;
                        playerInfo.teamId = -1;
                    }
                }
                return;
            }
            return;
        }
        int i = -1;
        for (PlayerInfo playerInfo2 : list) {
            if (playerInfo2 != null) {
                if (playerInfo2.playerId < 0 || playerInfo2.playerId >= 8) {
                    playerInfo2.teamId = -1;
                } else {
                    playerInfo2.teamId = playerInfo2.playerId / 2;
                    if (playerInfo2.isMe) {
                        i = playerInfo2.teamId;
                    }
                }
            }
        }
        if (i >= 0) {
            for (PlayerInfo playerInfo3 : list) {
                if (playerInfo3 != null) {
                    playerInfo3.isPartner = playerInfo3.teamId == i && !playerInfo3.isMe;
                }
            }
        }
    }
}
