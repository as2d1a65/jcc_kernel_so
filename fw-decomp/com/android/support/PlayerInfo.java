package com.android.support;

/* loaded from: classes.dex */
public class PlayerInfo {
    public int avatarId;
    public String avatarUrl;
    public boolean botKnown;
    public int enemyId;
    public int gold;
    public int hp;
    public boolean isBot;
    public boolean isMe;
    public boolean isPartner;
    public int level;
    public String name;
    public int playerId;
    public int rank;
    public int teamId;

    public PlayerInfo() {
        this.enemyId = -1;
        this.avatarUrl = "";
        this.avatarId = -1;
        this.playerId = -1;
        this.hp = 0;
        this.level = 0;
        this.gold = 0;
        this.rank = 0;
        this.isMe = false;
        this.name = "";
        this.isPartner = false;
        this.teamId = -1;
    }

    public PlayerInfo(int i, int i2, int i3, int i4, int i5, boolean z, String str) {
        this.enemyId = -1;
        this.avatarUrl = "";
        this.avatarId = -1;
        this.playerId = i;
        this.hp = i2;
        this.level = i3;
        this.gold = i4;
        this.rank = i5;
        this.isMe = z;
        this.name = str;
    }

    public String toString() {
        return String.format("[%d] %s | Rank:%d HP:%d Lv:%d Gold:%d %s", Integer.valueOf(this.playerId), this.name, Integer.valueOf(this.rank), Integer.valueOf(this.hp), Integer.valueOf(this.level), Integer.valueOf(this.gold), this.isMe ? "(我)" : "");
    }
}
