package com.android.support;

import java.util.Locale;

/* loaded from: classes.dex */
final class NativeLibrarySelector {
    private static final String PRIMARY_FILE = "lib10edb57537.so";
    private static final String PRIMARY_RUNTIME = "demo";
    private static final String TRANSLATED_FILE = "lib30ae545382.so";
    private static final String TRANSLATED_RUNTIME = "demo_compat";

    private NativeLibrarySelector() {
    }

    static boolean shouldUseCompat(String[] strArr) {
        String str;
        if (strArr == null || strArr.length == 0 || (str = strArr[0]) == null) {
            return false;
        }
        return str.toLowerCase(Locale.ROOT).contains("x86");
    }

    static String selectRuntimeBaseName(String[] strArr, boolean z) {
        if (z || shouldUseCompat(strArr)) {
            return TRANSLATED_RUNTIME;
        }
        return PRIMARY_RUNTIME;
    }

    static String selectRuntimeFileName(String[] strArr, boolean z) {
        if (z || shouldUseCompat(strArr)) {
            return TRANSLATED_FILE;
        }
        return PRIMARY_FILE;
    }
}
