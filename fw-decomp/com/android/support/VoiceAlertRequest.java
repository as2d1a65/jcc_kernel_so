package com.android.support;

/* loaded from: classes.dex */
public final class VoiceAlertRequest {
    private final int canonicalHeroId;
    private final int copies;
    private final int cost;
    private final String heroName;
    private final int playerId;
    private final String playerName;

    public VoiceAlertRequest(int i, int i2, String str, String str2, int i3) {
        this(i, i2, str, str2, 0, i3);
    }

    public VoiceAlertRequest(int i, int i2, String str, String str2, int i3, int i4) {
        this.playerId = i;
        this.canonicalHeroId = i2;
        this.playerName = str == null ? "" : str;
        this.heroName = str2 == null ? "" : str2;
        this.cost = i3;
        this.copies = i4;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getCanonicalHeroId() {
        return this.canonicalHeroId;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getHeroName() {
        return this.heroName;
    }

    public int getCost() {
        return this.cost;
    }

    public int getCopies() {
        return this.copies;
    }

    public String key() {
        return this.playerId + ":" + this.canonicalHeroId;
    }
}
