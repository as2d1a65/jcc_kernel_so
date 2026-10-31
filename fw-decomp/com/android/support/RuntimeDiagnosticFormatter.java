package com.android.support;

import java.util.Locale;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class RuntimeDiagnosticFormatter {
    private static final int MAX_DETAIL_LENGTH = 512;
    private static final Pattern SAFE_FIELD = Pattern.compile("[A-Z][A-Z0-9_.-]{1,31}");
    private static final Pattern SECRET_FIELD = Pattern.compile("(?i)(card|card_key|license|token|password|secret|device|device_id|imei|signature|hmac|rc4(?:_key)?|new_code)(\\s*[:=]\\s*)[^\\s&,}\\\"']+");
    private static final Pattern QUERY_VALUE = Pattern.compile("([?&][^=\\s]+)=([^&#\\s]+)");
    private static final Pattern LONG_IDENTIFIER = Pattern.compile("(?<![A-Za-z0-9])[A-Za-z0-9_+/=-]{16,}(?![A-Za-z0-9])");

    private RuntimeDiagnosticFormatter() {
    }

    public static String format(String str, String str2, String str3, String str4, long j, int i, String str5) {
        return String.format(Locale.US, "runtime_diag session=%s stage=%s action=%s result=%s elapsed_ms=%d pid=%d detail=%s", safeField(str), safeField(str2), safeField(str3), safeField(str4), Long.valueOf(Math.max(0L, Math.min(j, 86400000L))), Integer.valueOf(Math.max(0, i)), safeDetail(str5));
    }

    private static String safeField(String str) {
        return (str == null || !SAFE_FIELD.matcher(str).matches()) ? "UNKNOWN" : str;
    }

    private static String safeDetail(String str) {
        if (str == null || str.isEmpty()) {
            return "-";
        }
        String strReplaceAll = LONG_IDENTIFIER.matcher(QUERY_VALUE.matcher(SECRET_FIELD.matcher(str).replaceAll("$1$2[REDACTED]")).replaceAll("$1=[REDACTED]")).replaceAll("[REDACTED]");
        StringBuilder sb = new StringBuilder(Math.min(strReplaceAll.length(), 512));
        for (int i = 0; i < strReplaceAll.length() && sb.length() < 512; i++) {
            char cCharAt = strReplaceAll.charAt(i);
            if (Character.isISOControl(cCharAt)) {
                cCharAt = ' ';
            }
            sb.append(cCharAt);
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }
}
