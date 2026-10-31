package com.android.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/* loaded from: classes.dex */
final class AccessibilityServiceSettings {
    private AccessibilityServiceSettings() {
    }

    static boolean contains(String str, String str2) {
        String strCanonical = canonical(str2);
        if (strCanonical.isEmpty()) {
            return false;
        }
        Iterator<String> it = split(str).iterator();
        while (it.hasNext()) {
            if (canonical(it.next()).equals(strCanonical)) {
                return true;
            }
        }
        return false;
    }

    static String merge(String str, String str2) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (String str3 : split(str)) {
            linkedHashMap.putIfAbsent(canonical(str3), str3);
        }
        String strCanonical = canonical(str2);
        if (!strCanonical.isEmpty()) {
            linkedHashMap.putIfAbsent(strCanonical, str2.trim());
        }
        return String.join(":", linkedHashMap.values());
    }

    static String remove(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        String strCanonical = canonical(str2);
        for (String str3 : split(str)) {
            if (!canonical(str3).equals(strCanonical)) {
                arrayList.add(str3);
            }
        }
        return String.join(":", arrayList);
    }

    static boolean isSafeComponentList(String str) {
        if (str != null && (str.indexOf(13) >= 0 || str.indexOf(10) >= 0)) {
            return false;
        }
        Iterator<String> it = split(str).iterator();
        while (it.hasNext()) {
            if (!it.next().matches("[A-Za-z0-9_.$]+/[A-Za-z0-9_.$]+")) {
                return false;
            }
        }
        return true;
    }

    private static List<String> split(String str) {
        ArrayList arrayList = new ArrayList();
        if (str != null && !str.trim().isEmpty() && !"null".equalsIgnoreCase(str.trim())) {
            for (String str2 : str.split(":")) {
                String strTrim = str2.trim();
                if (!strTrim.isEmpty() && !"null".equalsIgnoreCase(strTrim)) {
                    arrayList.add(strTrim);
                }
            }
        }
        return arrayList;
    }

    private static String canonical(String str) {
        if (str == null) {
            return "";
        }
        String strTrim = str.trim();
        int iIndexOf = strTrim.indexOf(47);
        if (iIndexOf <= 0 || iIndexOf == strTrim.length() - 1) {
            return strTrim;
        }
        String strSubstring = strTrim.substring(0, iIndexOf);
        String strSubstring2 = strTrim.substring(iIndexOf + 1);
        if (strSubstring2.startsWith(".")) {
            strSubstring2 = strSubstring + strSubstring2;
        }
        return strSubstring + "/" + strSubstring2;
    }
}
