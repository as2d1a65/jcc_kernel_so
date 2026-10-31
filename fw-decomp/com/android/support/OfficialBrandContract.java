package com.android.support;

import java.nio.charset.StandardCharsets;
import kotlin.UByte;

/* loaded from: classes.dex */
final class OfficialBrandContract {
    private OfficialBrandContract() {
    }

    static final class TextParts {
        final String handle;
        final String prefix;

        TextParts(String str, String str2) {
            this.prefix = str;
            this.handle = str2;
        }
    }

    static int fnv1aUtf8(String str) {
        if (str == null) {
            str = "";
        }
        int i = -2128831035;
        for (byte b : str.getBytes(StandardCharsets.UTF_8)) {
            i = (i ^ (b & UByte.MAX_VALUE)) * 16777619;
        }
        return i;
    }

    static String validatedValue(String str, int i, String str2, int i2) {
        return (str != null && i == i2 && fnv1aUtf8(str) == i2) ? str : str2;
    }

    static TextParts validatedTextParts(String str, String str2, int i, String str3, String str4, int i2) {
        if (str != null && str2 != null && i == i2 && fnv1aUtf8(str + str2) == i2) {
            return new TextParts(str, str2);
        }
        return new TextParts(str3, str4);
    }

    static boolean approvedBundleValid(String str, String str2, String str3, String str4, String str5, int i, int i2, int i3, int i4) {
        return (str == null || str2 == null || str3 == null || str4 == null || str5 == null || fnv1aUtf8(new StringBuilder().append(str).append(str2).toString()) != i || fnv1aUtf8(str3) != i2 || fnv1aUtf8(str4) != i3 || fnv1aUtf8(str5) != i4) ? false : true;
    }
}
