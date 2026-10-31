package com.android.support;

import java.util.regex.Pattern;

/* loaded from: classes.dex */
final class GameDiagnosticState {
    private static final Pattern MAP_LINE = Pattern.compile("(?m)^[0-9a-fA-F]+-[0-9a-fA-F]+\\s+[-r][-w][-x][ps]\\s+[0-9a-fA-F]+\\s+[0-9a-fA-F]+:[0-9a-fA-F]+\\s+\\d+(?:[ \\t]+[^\\r\\n]*)?$");
    private static final Pattern EXECUTABLE_IL2CPP = Pattern.compile("(?m)^[0-9a-fA-F]+-[0-9a-fA-F]+\\s+[-r][-w]x[ps]\\s+\\S+\\s+\\S+\\s+\\d+\\s+\\S*/libil2cpp\\.so(?: \\(deleted\\))?\\s*$");

    private GameDiagnosticState() {
    }

    static int singlePid(String str) {
        if (str != null && str.trim().matches("[1-9][0-9]{0,9}")) {
            try {
                return Integer.parseInt(str.trim());
            } catch (NumberFormatException unused) {
            }
        }
        return -1;
    }

    static String describe(int i, String str, boolean z) {
        String str2;
        String strValueOf = i > 0 ? String.valueOf(i) : "未取得唯一PID";
        if (i <= 0 || str == null || !MAP_LINE.matcher(str).find()) {
            str2 = "未知(未取得映射)";
        } else {
            str2 = EXECUTABLE_IL2CPP.matcher(str).find() ? "已映射" : "未发现可执行映射";
        }
        return "进程=" + strValueOf + " libil2cpp=" + str2 + " IPC当前会话=" + (z ? "已连接" : "未连接");
    }
}
