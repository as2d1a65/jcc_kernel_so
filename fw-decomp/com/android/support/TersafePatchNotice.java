package com.android.support;

/* loaded from: classes.dex */
public final class TersafePatchNotice {
    private static final String RETIRED_MESSAGE = "检测初始化成功";

    private TersafePatchNotice() {
    }

    public static boolean isRetiredMessage(CharSequence charSequence) {
        return charSequence != null && RETIRED_MESSAGE.equals(charSequence.toString().trim());
    }
}
