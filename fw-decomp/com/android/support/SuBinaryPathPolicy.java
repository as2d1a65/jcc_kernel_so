package com.android.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
final class SuBinaryPathPolicy {
    static final String AUTO = "";
    private static final int MAX_LENGTH = 255;
    static final String PREF_KEY = "su_binary_path";
    private static final Pattern ABSOLUTE_PATH = Pattern.compile("^(/[A-Za-z0-9._][A-Za-z0-9._+-]*)+$");
    private static final Pattern BARE_NAME = Pattern.compile("^[A-Za-z0-9._][A-Za-z0-9._+-]{0,63}$");
    static final String DEFAULT_BINARY = "su";
    private static final List<String> AUTO_VERSION_PROBES = Collections.unmodifiableList(Arrays.asList("/system/xbin/su", "/system/bin/su", DEFAULT_BINARY));

    private SuBinaryPathPolicy() {
    }

    static String normalize(String str) {
        if (str == null) {
            return "";
        }
        String strTrim = str.trim();
        while (strTrim.length() >= 2 && ((strTrim.startsWith("\"") && strTrim.endsWith("\"")) || (strTrim.startsWith("'") && strTrim.endsWith("'")))) {
            strTrim = strTrim.substring(1, strTrim.length() - 1).trim();
        }
        return strTrim;
    }

    static boolean isAuto(String str) {
        return str == null || str.trim().isEmpty();
    }

    static String validate(String str) {
        if (isAuto(str)) {
            return null;
        }
        String strNormalize = normalize(str);
        if (strNormalize.length() > 255) {
            return "路径太长，请检查后重填";
        }
        if (strNormalize.contains("..")) {
            return "路径不能包含 ..";
        }
        if (strNormalize.endsWith("/")) {
            return "请填到 su 程序本身，不要以 / 结尾";
        }
        if (strNormalize.startsWith("/")) {
            if (ABSOLUTE_PATH.matcher(strNormalize).matches()) {
                return null;
            }
            return "路径只能是字母、数字和 . _ - +，不能有空格或特殊符号";
        }
        if (BARE_NAME.matcher(strNormalize).matches()) {
            return null;
        }
        return "请填写完整路径，例如 /system/bin/su";
    }

    static String sanitize(String str) {
        String strNormalize = normalize(str);
        return validate(strNormalize) == null ? strNormalize : "";
    }

    static String binary(String str) {
        return isAuto(str) ? DEFAULT_BINARY : normalize(str);
    }

    static boolean isKernelSuDebugBinary(String str) {
        String strBinary = binary(str);
        int iLastIndexOf = strBinary.lastIndexOf(47);
        if (iLastIndexOf >= 0) {
            strBinary = strBinary.substring(iLastIndexOf + 1);
        }
        return "ksud".equals(strBinary) || "libksud.so".equals(strBinary);
    }

    static String describe(String str) {
        return isAuto(str) ? "自动检测（su）" : normalize(str);
    }

    static List<String[]> mountMasterPrefixes(String str) {
        String strBinary = binary(str);
        ArrayList arrayList = new ArrayList(3);
        arrayList.add(new String[]{strBinary, "-mm", "-c"});
        arrayList.add(new String[]{strBinary, "-M", "-c"});
        arrayList.add(new String[]{strBinary, "--mount-master", "-c"});
        return arrayList;
    }

    static String[] plainPrefix(String str) {
        return new String[]{binary(str), "-c"};
    }

    static String[] pipeCommand(String str) {
        String strBinary = binary(str);
        if (isKernelSuDebugBinary(strBinary)) {
            return new String[]{strBinary, "debug", DEFAULT_BINARY};
        }
        return new String[]{strBinary};
    }

    static List<String> versionProbeCandidates(String str) {
        return isAuto(str) ? AUTO_VERSION_PROBES : Collections.singletonList(binary(str));
    }
}
