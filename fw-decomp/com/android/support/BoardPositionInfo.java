package com.android.support;

/* loaded from: classes.dex */
public class BoardPositionInfo {
    public int benchBitmask;
    public int benchCount;
    public int boardBitmask;
    public int boardCount;
    public boolean isMe;
    public int playerId;
    public int[] boardHeroIds = new int[28];
    public int[] benchHeroIds = new int[9];

    public boolean isBoardOccupied(int i, int i2) {
        int i3 = (i * 7) + i2;
        return i3 >= 0 && i3 < 28 && ((this.boardBitmask >> i3) & 1) != 0;
    }

    public boolean isBenchOccupied(int i) {
        return i >= 0 && i < 9 && ((this.benchBitmask >> i) & 1) != 0;
    }

    public int getBoardHeroId(int i, int i2) {
        int i3 = (i * 7) + i2;
        if (i3 < 0 || i3 >= 28) {
            return 0;
        }
        return this.boardHeroIds[i3];
    }

    public int getBenchHeroId(int i) {
        if (i < 0 || i >= 9) {
            return 0;
        }
        return this.benchHeroIds[i];
    }
}
