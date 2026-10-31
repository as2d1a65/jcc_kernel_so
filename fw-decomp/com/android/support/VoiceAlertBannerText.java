package com.android.support;

/* loaded from: classes.dex */
final class VoiceAlertBannerText {
    private static final int MAX_NAME_CODE_POINTS = 8;
    private static final String STARS = "★★★";
    private static final String TITLE = "三星预警";
    private static final String UNKNOWN_HERO = "未知英雄";
    private static final String UNKNOWN_PLAYER = "未知玩家";

    private VoiceAlertBannerText() {
    }

    static Parts create(String str, String str2, int i) {
        return new Parts(STARS, TITLE, shorten(str, UNKNOWN_PLAYER), shorten(str2, UNKNOWN_HERO), Math.max(1, Math.min(99, i)) + "张");
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
        final String copies;
        final String heroName;
        final String playerName;
        final String stars;
        final String title;

        Parts(String str, String str2, String str3, String str4, String str5) {
            this.stars = str;
            this.title = str2;
            this.playerName = str3;
            this.heroName = str4;
            this.copies = str5;
        }
    }
}
