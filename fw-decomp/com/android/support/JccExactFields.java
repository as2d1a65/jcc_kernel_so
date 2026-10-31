package com.android.support;

import java.util.HashSet;

/* loaded from: classes.dex */
public final class JccExactFields {

    public interface Lookup {
        boolean has(String str);
    }

    private JccExactFields() {
    }

    public static boolean matches(int i, Lookup lookup, String... strArr) {
        if (i < 0 || lookup == null || strArr == null || i != strArr.length) {
            return false;
        }
        HashSet hashSet = new HashSet();
        for (String str : strArr) {
            if (str == null || str.isEmpty() || !hashSet.add(str) || !lookup.has(str)) {
                return false;
            }
        }
        return true;
    }
}
