package com.android.support;

/* loaded from: classes.dex */
final class HeroWarningInfo {
    static final int SCARCE_POOL_THRESHOLD = 9;
    static final int UNKNOWN_POOL_COUNT = -1;
    int baseHeroId;
    int heroCost;
    int totalCopies;
    int remainingCopies = -1;
    int totalPoolCopies = -1;
    String ownedStatText = "持0";
    String remainingStatText = "余--";
    String riskFlagText = "";
    String heroCostText = "?";

    HeroWarningInfo() {
    }

    void setHeroCost(int i) {
        this.heroCost = i;
        this.heroCostText = i > 0 ? String.valueOf(i) : "?";
    }

    void setTotalCopies(int i) {
        String str;
        this.totalCopies = Math.max(0, i);
        this.ownedStatText = "持" + this.totalCopies;
        int i2 = this.totalCopies;
        if (i2 >= 9) {
            str = "★";
        } else if (i2 == 8) {
            str = "差1";
        } else {
            str = i2 == 7 ? "7/9" : "";
        }
        this.riskFlagText = str;
    }

    void setPoolCounts(int i, int i2) {
        if (i < 0) {
            clearPoolCounts();
            return;
        }
        if (i2 <= 0) {
            i2 = -1;
        }
        this.totalPoolCopies = i2;
        if (i2 > 0) {
            i = Math.min(i, i2);
        }
        this.remainingCopies = i;
        this.remainingStatText = "余" + this.remainingCopies;
    }

    void clearPoolCounts() {
        this.remainingCopies = -1;
        this.totalPoolCopies = -1;
        this.remainingStatText = "余--";
    }

    boolean hasPoolCount() {
        return this.remainingCopies >= 0;
    }

    boolean isPoolScarce() {
        return hasPoolCount() && this.remainingCopies <= 9;
    }

    float remainingRatio() {
        int i;
        if (!hasPoolCount() || (i = this.totalPoolCopies) <= 0) {
            return 0.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, this.remainingCopies / i));
    }

    static int compareRiskPriority(HeroWarningInfo heroWarningInfo, HeroWarningInfo heroWarningInfo2) {
        int iCompare;
        int iCompare2 = Integer.compare(heroWarningInfo2.totalCopies, heroWarningInfo.totalCopies);
        return iCompare2 != 0 ? iCompare2 : heroWarningInfo.hasPoolCount() != heroWarningInfo2.hasPoolCount() ? heroWarningInfo.hasPoolCount() ? -1 : 1 : (!heroWarningInfo.hasPoolCount() || (iCompare = Integer.compare(heroWarningInfo.remainingCopies, heroWarningInfo2.remainingCopies)) == 0) ? Integer.compare(heroWarningInfo.baseHeroId, heroWarningInfo2.baseHeroId) : iCompare;
    }
}
