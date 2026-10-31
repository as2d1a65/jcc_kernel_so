package com.android.support;

/* loaded from: classes.dex */
final class RankWarningRenderText {
    private RankWarningRenderText() {
    }

    static PlayerText forPlayer(PlayerInfo playerInfo) {
        return new PlayerText(playerInfo.rank, playerInfo.level, playerInfo.gold);
    }

    static final class PlayerText {
        final int gold;
        final String goldPlain;
        final String goldWithSuffix;
        final int level;
        final String levelWithDotPrefix;
        final String levelWithPrefix;
        final int rank;
        final String rankPlain;
        final String rankWithHash;

        private PlayerText(int i, int i2, int i3) {
            this.rank = i;
            this.level = i2;
            this.gold = i3;
            this.rankPlain = String.valueOf(i);
            this.rankWithHash = "#" + i;
            this.levelWithPrefix = "Lv" + i2;
            this.levelWithDotPrefix = "Lv." + i2;
            this.goldPlain = String.valueOf(i3);
            this.goldWithSuffix = i3 + "g";
        }

        boolean matches(PlayerInfo playerInfo) {
            return playerInfo != null && this.rank == playerInfo.rank && this.level == playerInfo.level && this.gold == playerInfo.gold;
        }
    }
}
