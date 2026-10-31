package com.android.support;

/* loaded from: classes.dex */
final class AllInAlertText {
    static final int REASON_NO_REACHABLE_TARGET = 10;
    private static final String TITLE = "梭不动了";

    static boolean shouldAnnounce(int i) {
        return i == 10;
    }

    private AllInAlertText() {
    }

    static Parts forStopReason(int i, String str) {
        if (!shouldAnnounce(i)) {
            return null;
        }
        String strTrim = str == null ? "" : str.trim();
        if (strTrim.isEmpty()) {
            strTrim = "目标牌库已抽空，追不到三星了";
        }
        return new Parts(TITLE, strTrim);
    }

    static final class Parts {
        final String detail;
        final String title;

        Parts(String str, String str2) {
            this.title = str;
            this.detail = str2;
        }
    }
}
