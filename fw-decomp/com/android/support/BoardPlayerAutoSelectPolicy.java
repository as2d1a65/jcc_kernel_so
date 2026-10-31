package com.android.support;

import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
final class BoardPlayerAutoSelectPolicy {

    static final class State {
        final String debugReason;
        final boolean isBoardPlayerManuallySelected;
        final int lastAutoSelectedOpponentId;
        final int selectedBoardPlayerId;

        State(int i, int i2, boolean z, String str) {
            this.selectedBoardPlayerId = i;
            this.lastAutoSelectedOpponentId = i2;
            this.isBoardPlayerManuallySelected = z;
            this.debugReason = str;
        }
    }

    private BoardPlayerAutoSelectPolicy() {
    }

    static State apply(boolean z, List<BoardPositionInfo> list, int i, int i2, int i3, boolean z2, boolean z3) {
        String str;
        if (!z || list == null || list.isEmpty()) {
            return new State(i2, i3, z2, null);
        }
        int i4 = -1;
        if (i >= 0) {
            if (i3 != i) {
                z2 = false;
            }
            if (!z2 && i2 != i) {
                Iterator<BoardPositionInfo> it = list.iterator();
                while (it.hasNext()) {
                    if (it.next().playerId == i) {
                        str = "opponent";
                        i2 = i;
                        break;
                    }
                }
            }
            str = null;
        } else {
            if (i3 < 0) {
                i = i3;
            } else {
                if (!z2) {
                    str = "opponent-cleared";
                    i = -1;
                    i2 = i;
                    break;
                }
                i = -1;
            }
            str = null;
        }
        if (!z2 && z3) {
            Iterator<BoardPositionInfo> it2 = list.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                BoardPositionInfo next = it2.next();
                if (next != null && next.isMe) {
                    String str2 = i2 == next.playerId ? str : null;
                    i2 = next.playerId;
                    str = str2;
                }
            }
        }
        if (!z2 && i2 < 0) {
            int i5 = -1;
            for (BoardPositionInfo boardPositionInfo : list) {
                int i6 = boardPositionInfo.boardCount + boardPositionInfo.benchCount;
                if (i6 > i5) {
                    i4 = boardPositionInfo.playerId;
                    i5 = i6;
                }
            }
            if (i4 >= 0) {
                str = "max-pieces";
                i2 = i4;
            }
        }
        return new State(i2, i, z2, str);
    }
}
