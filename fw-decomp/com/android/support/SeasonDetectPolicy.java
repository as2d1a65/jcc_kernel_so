package com.android.support;

/* loaded from: classes.dex */
final class SeasonDetectPolicy {
    static final int BIG_CHANGE = 8;
    static final String UNKNOWN_HERO_NAME = "未知英雄";

    interface NameLookup {
        String name(int i, int i2);
    }

    static int pick(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13) {
        return ((((i + i2) + i3) + i4) + i5) + i6 <= 0 ? i7 : (i6 < i || i6 < i2 || i6 < i3 || i6 < i4 || i6 < i5 || i6 <= 0) ? (i5 < i || i5 < i2 || i5 < i3 || i5 < i4 || i5 <= 0) ? (i4 < i || i4 < i2 || i4 < i3 || i4 <= 0) ? (i3 < i || i3 < i2) ? i >= i2 ? i8 : i9 : i10 : i11 : i12 : i13;
    }

    static final class Hits {
        final int fux;
        final int s10;
        final int s11;
        final int s16;
        final int s17;
        final int s18;
        final int s8;

        Hits(int i, int i2, int i3, int i4, int i5, int i6) {
            this(i, i2, i3, i4, i5, i6, 0);
        }

        Hits(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            this.fux = i;
            this.s10 = i2;
            this.s16 = i3;
            this.s17 = i4;
            this.s8 = i5;
            this.s18 = i6;
            this.s11 = i7;
        }

        boolean recognized() {
            return this.fux > 0 || this.s10 > 0 || this.s16 > 0 || this.s17 > 0 || this.s8 > 0 || this.s18 > 0 || this.s11 > 0;
        }
    }

    private SeasonDetectPolicy() {
    }

    static boolean isKnownName(String str) {
        return (str == null || UNKNOWN_HERO_NAME.equals(str)) ? false : true;
    }

    static String recognizedGeneration(Hits hits, String str) {
        return (hits == null || !hits.recognized() || str == null || str.length() != 22) ? "" : str;
    }

    static Hits countHits(Iterable<HeroData> iterable, NameLookup nameLookup, int i, int i2, int i3, int i4, int i5, int i6) {
        if (iterable == null || nameLookup == null) {
            return new Hits(0, 0, 0, 0, 0, 0);
        }
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        int i10 = 0;
        int i11 = 0;
        int i12 = 0;
        for (HeroData heroData : iterable) {
            if (heroData != null) {
                if (isKnownName(nameLookup.name(heroData.heroId, i))) {
                    i7++;
                }
                if (isKnownName(nameLookup.name(heroData.heroId, i2))) {
                    i8++;
                }
                if (isKnownName(nameLookup.name(heroData.heroId, i3))) {
                    i9++;
                }
                if (isKnownName(nameLookup.name(heroData.heroId, i4))) {
                    i10++;
                }
                if (isKnownName(nameLookup.name(heroData.heroId, i5))) {
                    i11++;
                }
                if (isKnownName(nameLookup.name(heroData.heroId, i6))) {
                    i12++;
                }
            }
        }
        return new Hits(i7, i8, i9, i10, i11, i12);
    }

    static Hits countHits(Iterable<HeroData> iterable, NameLookup nameLookup, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        Hits hitsCountHits = countHits(iterable, nameLookup, i, i2, i3, i4, i5, i6);
        int i8 = 0;
        if (iterable != null && nameLookup != null) {
            for (HeroData heroData : iterable) {
                if (heroData != null && isKnownName(nameLookup.name(heroData.heroId, i7))) {
                    i8++;
                }
            }
        }
        return new Hits(hitsCountHits.fux, hitsCountHits.s10, hitsCountHits.s16, hitsCountHits.s17, hitsCountHits.s8, hitsCountHits.s18, i8);
    }

    static int pick(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15) {
        return (i7 <= 0 || i7 < Math.max(Math.max(Math.max(i, i2), Math.max(i3, i4)), Math.max(i5, i6))) ? pick(i, i2, i3, i4, i5, i6, i8, i9, i10, i11, i12, i13, i14) : i15;
    }

    static boolean shouldDetect(int i, int i2) {
        if (i <= 0) {
            return false;
        }
        return i2 < 0 || Math.abs(i - i2) >= 8;
    }
}
