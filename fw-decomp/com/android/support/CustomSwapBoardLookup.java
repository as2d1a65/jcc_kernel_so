package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
public final class CustomSwapBoardLookup {
    public static int statusHintColor(int i, BoardPositionInfo boardPositionInfo) {
        return (i < 0 || boardPositionInfo == null) ? -855657657 : -1715937025;
    }

    private CustomSwapBoardLookup() {
    }

    public static BoardPositionInfo findMine(List<BoardPositionInfo> list, int i) {
        if (list != null && i >= 0) {
            for (BoardPositionInfo boardPositionInfo : list) {
                if (boardPositionInfo != null && boardPositionInfo.playerId == i) {
                    return boardPositionInfo;
                }
            }
        }
        return null;
    }

    public static String statusHint(int i, BoardPositionInfo boardPositionInfo) {
        if (i < 0) {
            return " · 等待自身玩家数据…";
        }
        if (boardPositionInfo == null) {
            return " · 等待棋盘数据…(玩家ID=" + i + ")";
        }
        return " · 自身棋盘 战" + boardPositionInfo.boardCount + "·备" + boardPositionInfo.benchCount + (boardPositionInfo.boardCount + boardPositionInfo.benchCount == 0 ? "" : " · 点棋子开始");
    }
}
