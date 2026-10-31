package com.android.support;

/* loaded from: classes.dex */
class HeroData {
    public String chineseName;
    public int cost;
    public int currentCount;
    public String englishName;
    public int heroId;
    public boolean isLocked = false;
    public int totalCount;

    public HeroData(int i, int i2, int i3, int i4) {
        this.heroId = i;
        this.cost = i2;
        this.currentCount = i3;
        this.totalCount = i4;
        this.chineseName = HeroIdMapper.getChineseName(i, 5);
        this.englishName = HeroIdMapper.getEnglishNameById(i, 5);
        if ("未知英雄".equals(this.chineseName)) {
            this.chineseName = HeroIdMapper.getChineseName(i, 4);
            this.englishName = HeroIdMapper.getEnglishNameById(i, 4);
        }
        if ("未知英雄".equals(this.chineseName)) {
            this.chineseName = HeroIdMapper.getChineseName(i, 3);
            this.englishName = HeroIdMapper.getEnglishNameById(i, 3);
        }
        if ("未知英雄".equals(this.chineseName)) {
            this.chineseName = HeroIdMapper.getChineseName(i, 2);
            this.englishName = HeroIdMapper.getEnglishNameById(i, 2);
        }
        if ("未知英雄".equals(this.chineseName)) {
            this.chineseName = HeroIdMapper.getChineseName(i, 0);
            this.englishName = HeroIdMapper.getEnglishNameById(i, 0);
        }
        if ("未知英雄".equals(this.chineseName)) {
            this.chineseName = HeroIdMapper.getChineseName(i, 1);
            this.englishName = HeroIdMapper.getEnglishNameById(i, 1);
        }
        if ("未知英雄".equals(this.chineseName)) {
            this.chineseName = HeroIdMapper.getChineseName(i, 6);
            this.englishName = HeroIdMapper.getEnglishNameById(i, 6);
        }
    }

    public String toString() {
        return String.format("Hero{id=%d, cost=%d, current=%d, total=%d, name=%s(%s)}", Integer.valueOf(this.heroId), Integer.valueOf(this.cost), Integer.valueOf(this.currentCount), Integer.valueOf(this.totalCount), this.chineseName, this.englishName);
    }
}
