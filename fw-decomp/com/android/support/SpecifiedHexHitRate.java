package com.android.support;

/* loaded from: classes.dex */
final class SpecifiedHexHitRate {
    static final int FALLBACK_DRAWS_PER_STAGE = 65;

    enum Outlook {
        UNKNOWN,
        NOT_IN_POOL,
        IN_POOL_UNRATED,
        RATED
    }

    static int resolveDraws(int i) {
        if (i > 0) {
            return i;
        }
        return 65;
    }

    private SpecifiedHexHitRate() {
    }

    static int weightOf(int i, int[] iArr, int[] iArr2) {
        if (i > 0 && iArr != null && iArr2 != null) {
            int iMin = Math.min(iArr.length, iArr2.length);
            for (int i2 = 0; i2 < iMin; i2++) {
                if (iArr[i2] == i) {
                    return Math.max(0, iArr2[i2]);
                }
            }
        }
        return 0;
    }

    static int totalWeight(int[] iArr, int i) {
        if (i > 0) {
            return i;
        }
        if (iArr == null) {
            return 0;
        }
        int i2 = 0;
        for (int i3 : iArr) {
            if (i3 > 0) {
                i2 += i3;
            }
        }
        return i2;
    }

    static double perRefresh(int i, int[] iArr, int[] iArr2, int i2) {
        int iWeightOf;
        int i3 = totalWeight(iArr2, i2);
        if (i3 > 0 && (iWeightOf = weightOf(i, iArr, iArr2)) > 0) {
            return Math.min(1.0d, iWeightOf / i3);
        }
        return 0.0d;
    }

    static double perRound(double d, int i) {
        if (d <= 0.0d || i <= 0) {
            return 0.0d;
        }
        if (d >= 1.0d) {
            return 1.0d;
        }
        return 1.0d - Math.pow(1.0d - d, i);
    }

    static double roundChance(int i, int[] iArr, int[] iArr2, int i2, int i3) {
        return perRound(perRefresh(i, iArr, iArr2, i2), resolveDraws(i3));
    }

    static double roundChanceForAny(int[] iArr, int[] iArr2, int[] iArr3, int i, int i2) {
        int i3 = totalWeight(iArr3, i);
        if (iArr == null || i3 <= 0) {
            return 0.0d;
        }
        int iWeightOf = 0;
        for (int i4 = 0; i4 < iArr.length; i4++) {
            int i5 = 0;
            while (true) {
                if (i5 < i4) {
                    if (iArr[i5] == iArr[i4]) {
                        break;
                    }
                    i5++;
                } else {
                    iWeightOf += weightOf(iArr[i4], iArr2, iArr3);
                    break;
                }
            }
        }
        if (iWeightOf <= 0) {
            return 0.0d;
        }
        return perRound(Math.min(1.0d, iWeightOf / i3), resolveDraws(i2));
    }

    static int rollableCount(int[] iArr, int[] iArr2) {
        if (iArr == null || !SpecifiedHexOfferPolicy.hasLivePool(iArr2)) {
            return 0;
        }
        int i = 0;
        for (int i2 : iArr) {
            if (SpecifiedHexOfferPolicy.isRollableNow(i2, iArr2)) {
                i++;
            }
        }
        return i;
    }

    static boolean weightsKnown(int[] iArr, int i) {
        return totalWeight(iArr, i) > 0;
    }

    static Outlook outlookFor(int i, int[] iArr, int[] iArr2, int i2, boolean z) {
        if (!z || !SpecifiedHexOfferPolicy.hasLivePool(iArr)) {
            return Outlook.UNKNOWN;
        }
        if (!SpecifiedHexOfferPolicy.isRollableNow(i, iArr)) {
            return Outlook.NOT_IN_POOL;
        }
        if (weightsKnown(iArr2, i2)) {
            return weightOf(i, iArr, iArr2) > 0 ? Outlook.RATED : Outlook.IN_POOL_UNRATED;
        }
        return Outlook.IN_POOL_UNRATED;
    }

    static String chanceLabel(Outlook outlook, double d) {
        if (outlook == null || outlook == Outlook.UNKNOWN) {
            return "";
        }
        if (outlook == Outlook.NOT_IN_POOL) {
            return "本阶段不会出";
        }
        if (outlook == Outlook.IN_POOL_UNRATED || d <= 0.0d) {
            return "本阶段可出";
        }
        int iRound = (int) Math.round(d * 100.0d);
        if (iRound >= 99) {
            return "本阶段稳出";
        }
        if (iRound < 1) {
            return "本阶段几乎不出";
        }
        return "本阶段 " + iRound + "%";
    }

    static String summaryLabel(int i, int i2, double d, boolean z, boolean z2) {
        if (i <= 0) {
            return "";
        }
        if (!z) {
            return "已选 " + i + " 个 · 等开店确认";
        }
        if (i2 <= 0) {
            return "已选 " + i + " 个 · 本阶段都不会出";
        }
        if (!z2 || d <= 0.0d) {
            return "可出 " + i2 + "/" + i;
        }
        int iRound = (int) Math.round(d * 100.0d);
        if (iRound >= 99) {
            iRound = 99;
        }
        if (iRound < 1) {
            iRound = 1;
        }
        return "可出 " + i2 + "/" + i + " · 本阶段 " + iRound + "%";
    }
}
