package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class CustomSwapMyPlayerIdPolicy {
    private CustomSwapMyPlayerIdPolicy() {
    }

    static int resolve(int i, List<PlayerInfo> list, List<PlayerInfo> list2) {
        if (i >= 0) {
            return i;
        }
        int iFirstMe = firstMe(list);
        return iFirstMe >= 0 ? iFirstMe : firstMe(list2);
    }

    static int firstMe(List<PlayerInfo> list) {
        if (list == null) {
            return -1;
        }
        for (PlayerInfo playerInfo : list) {
            if (playerInfo != null && playerInfo.isMe) {
                return playerInfo.playerId;
            }
        }
        return -1;
    }
}
