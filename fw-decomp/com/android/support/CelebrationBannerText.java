package com.android.support;

/* loaded from: classes.dex */
final class CelebrationBannerText {
    private static final int MAX_NAME_CODE_POINTS = 8;
    static final String TITLE = "咱俩真牛逼！";
    private static final String UNKNOWN_EQUIP = "指定装备";
    private static final String UNKNOWN_HERO = "未知英雄";

    private CelebrationBannerText() {
    }

    static Parts forThreeStar(String str, int i) {
        int iMax = Math.max(3, Math.min(9, i));
        StringBuilder sb = new StringBuilder(iMax);
        for (int i2 = 0; i2 < iMax; i2++) {
            sb.append((char) 9733);
        }
        return new Parts(sb.toString(), TITLE, shorten(str, UNKNOWN_HERO) + " " + iMax + "星达成");
    }

    static Parts forRecast(String str) {
        return new Parts("", TITLE, "重铸成功 · 已锤出「" + shorten(str, UNKNOWN_EQUIP) + "」");
    }

    private static String shorten(String str, String str2) {
        String strTrim = str == null ? "" : str.trim();
        if (!strTrim.isEmpty()) {
            str2 = strTrim;
        }
        if (str2.codePointCount(0, str2.length()) <= 8) {
            return str2;
        }
        return str2.substring(0, str2.offsetByCodePoints(0, 7)) + "…";
    }

    static final class Parts {
        final String detail;
        final String stars;
        final String title;

        Parts(String str, String str2, String str3) {
            this.stars = str;
            this.title = str2;
            this.detail = str3;
        }
    }
}
