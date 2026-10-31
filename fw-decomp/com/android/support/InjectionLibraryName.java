package com.android.support;

import java.util.UUID;

/* loaded from: classes.dex */
final class InjectionLibraryName {
    private InjectionLibraryName() {
    }

    static String create() {
        return "lib" + UUID.randomUUID().toString().replace("-", "") + ".so";
    }

    static boolean isGeneratedName(String str) {
        return str != null && str.matches("lib[0-9a-f]{32}\\.so");
    }
}
