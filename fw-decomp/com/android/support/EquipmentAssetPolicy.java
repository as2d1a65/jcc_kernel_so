package com.android.support;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/* loaded from: classes.dex */
public final class EquipmentAssetPolicy {
    private EquipmentAssetPolicy() {
    }

    public static EquipmentAssetStatus choose(String str, Set<String> set, boolean z, String... strArr) {
        String strNormalize = normalize(str);
        if (set == null) {
            set = Collections.emptySet();
        }
        if (!strNormalize.isEmpty() && containsPng(set, strNormalize)) {
            return EquipmentAssetStatus.LOCAL;
        }
        if (strArr != null) {
            for (String str2 : strArr) {
                if (containsPng(set, normalize(str2))) {
                    return EquipmentAssetStatus.ALIAS;
                }
            }
        }
        if (strNormalize.isEmpty()) {
            return EquipmentAssetStatus.INVALID;
        }
        if (z) {
            return EquipmentAssetStatus.REMOTE;
        }
        return EquipmentAssetStatus.PLACEHOLDER;
    }

    public static boolean isSelectable(String str, String str2, boolean z) {
        return z && !isBlank(str) && !isBlank(str2) && str.matches("[0-9]{1,6}");
    }

    public static String normalize(String str) {
        if (str == null) {
            return "";
        }
        String strTrim = str.trim();
        if (strTrim.isEmpty() || strTrim.contains("/") || strTrim.contains("\\") || strTrim.contains("..")) {
            return "";
        }
        return strTrim.endsWith(".png") ? strTrim.substring(0, strTrim.length() - 4) : strTrim;
    }

    public static String assetKey(String str) {
        return normalize(str).toLowerCase(Locale.ROOT);
    }

    private static boolean containsPng(Set<String> set, String str) {
        if (str.isEmpty() || set.isEmpty()) {
            return false;
        }
        String lowerCase = str.toLowerCase(Locale.ROOT);
        if (set.contains(lowerCase) || set.contains(lowerCase + ".png")) {
            return true;
        }
        for (String str2 : set) {
            if (str2 != null && (str2.equalsIgnoreCase(lowerCase) || str2.equalsIgnoreCase(lowerCase + ".png"))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
