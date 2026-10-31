package com.android.support;

/* loaded from: classes.dex */
final class HeroDisplayName {
    private HeroDisplayName() {
    }

    static boolean isKnown(HeroData heroData) {
        return (heroData == null || heroData.chineseName == null || heroData.chineseName.equals("未知") || heroData.chineseName.equals("未知英雄")) ? false : true;
    }

    static String labelOrId(HeroData heroData) {
        if (isKnown(heroData)) {
            return heroData.chineseName;
        }
        return heroData == null ? "" : "ID:" + heroData.heroId;
    }
}
