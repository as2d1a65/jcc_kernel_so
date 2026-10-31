package com.android.support;

import androidx.core.text.HtmlCompat;
import androidx.core.view.MotionEventCompat;
import com.android.support.diagnostics.TraceCatalog;
import kotlin.text.Typography;
import kotlinx.coroutines.internal.LockFreeTaskQueueCore;

/* loaded from: classes.dex */
public final class GatewayFailurePolicy {
    public static final long RETRY_BASE_DELAY_MS = 400;
    public static final double RETRY_JITTER_RATIO = 0.5d;

    private static boolean isRetryableTransportStatus(int i) {
        return i == 0 || i == 502 || i == 503 || i == 504 || i == 522;
    }

    private GatewayFailurePolicy() {
    }

    public static long retryDelayMs(int i, double d) {
        return Math.max(1L, Math.round((Math.max(0, i) + 1) * 400 * (((d >= 0.0d ? Math.min(d, 1.0d) : 0.0d) * 1.0d) + 0.5d)));
    }

    public static boolean shouldRetry(String str, int i, int i2) {
        if (i2 != 0 || str == null) {
            return false;
        }
        if ("WIG_HTTP".equals(str) || "TRANSIENT".equals(str) || "NET_DNS".equals(str) || "NET_CONNECT".equals(str) || "CLOUD_FUNCTION".equals(str)) {
            return isRetryableTransportStatus(i);
        }
        if ("WIG_GRANT_REQUIRED".equals(str)) {
            return i == 409;
        }
        if ("WIG_UPSTREAM_UNAVAILABLE".equals(str) || "WIG_UNAVAILABLE".equals(str) || "UPSTREAM_TIMEOUT".equals(str) || "GATEWAY_UNAVAILABLE".equals(str)) {
            return i == 0 || i == 502 || i == 503 || i == 504 || i == 522;
        }
        return false;
    }

    public static boolean allowsSilentRetry(String str) {
        if (str == null) {
            return false;
        }
        str.hashCode();
        switch (str) {
        }
        return false;
    }

    public static String customerCode(String str, int i) {
        String str2;
        if (str == null) {
            return i >= 500 ? "CLOUD_FUNCTION" : "UNKNOWN";
        }
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2109323578:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_LICENSE_BANNED")) {
                    c = 0;
                    break;
                }
                break;
            case -2095858980:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("LICENSE_REJECTED")) {
                    c = 1;
                    break;
                }
                break;
            case -2093315735:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("VERSION_SIGNATURE_INVALID")) {
                    c = 2;
                    break;
                }
                break;
            case -2038013325:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("ADB_DEBUG")) {
                    c = 3;
                    break;
                }
                break;
            case -1987808683:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("UPDATE_REQUIRED")) {
                    c = 4;
                    break;
                }
                break;
            case -1900056923:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("TRUST_REJECTED")) {
                    c = 5;
                    break;
                }
                break;
            case -1848351359:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("REDIRECT_REJECTED")) {
                    c = 6;
                    break;
                }
                break;
            case -1828016795:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SEAT_INACTIVE")) {
                    c = 7;
                    break;
                }
                break;
            case -1824356621:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("MAINTENANCE")) {
                    c = '\b';
                    break;
                }
                break;
            case -1820247863:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("INJECT_PENDING")) {
                    c = '\t';
                    break;
                }
                break;
            case -1733271737:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("NET_DNS")) {
                    c = '\n';
                    break;
                }
                break;
            case -1733256423:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("NET_TLS")) {
                    c = 11;
                    break;
                }
                break;
            case -1345867105:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("TOKEN_EXPIRED")) {
                    c = '\f';
                    break;
                }
                break;
            case -1328990463:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("DEVICE_PROOF_FAILED")) {
                    c = '\r';
                    break;
                }
                break;
            case -1314247541:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_AUTHORIZATION_DENIED")) {
                    c = 14;
                    break;
                }
                break;
            case -1191502261:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("LOGIN_TIMEOUT")) {
                    c = 15;
                    break;
                }
                break;
            case -1118458818:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("VERSION_KEY_UNCONFIGURED")) {
                    c = 16;
                    break;
                }
                break;
            case -1059933288:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SEAT_OCCUPIED")) {
                    c = 17;
                    break;
                }
                break;
            case -1015541971:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("INTEGRITY")) {
                    c = 18;
                    break;
                }
                break;
            case -913353642:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("AUTH_REQUIRED")) {
                    c = 19;
                    break;
                }
                break;
            case -894481737:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_TIMEOUT")) {
                    c = 20;
                    break;
                }
                break;
            case -872543224:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("NET_CONNECT")) {
                    c = 21;
                    break;
                }
                break;
            case -866047748:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("LICENSE_BANNED")) {
                    c = 22;
                    break;
                }
                break;
            case -829571392:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("CLOUD_RELEASE_REJECTED")) {
                    c = 23;
                    break;
                }
                break;
            case -772326846:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("INPUT_INVALID")) {
                    c = 24;
                    break;
                }
                break;
            case -744018507:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SESSION_REVOKED")) {
                    c = 25;
                    break;
                }
                break;
            case -653650923:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_AUTHORIZATION_EXPIRED")) {
                    c = 26;
                    break;
                }
                break;
            case -541500857:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SESSION_OCCUPIED")) {
                    c = 27;
                    break;
                }
                break;
            case -508431662:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_HTTP")) {
                    c = 28;
                    break;
                }
                break;
            case -494039813:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("PRESENCE")) {
                    c = 29;
                    break;
                }
                break;
            case -462333527:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("INVALID_RESPONSE")) {
                    c = 30;
                    break;
                }
                break;
            case -455626134:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("TRANSIENT")) {
                    c = 31;
                    break;
                }
                break;
            case -407934876:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("LOCAL_CHECK_BUSY")) {
                    c = ' ';
                    break;
                }
                break;
            case -407646421:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SEAT_CHILD_MISMATCH")) {
                    c = '!';
                    break;
                }
                break;
            case -374260356:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("DEVICE_BINDING")) {
                    c = Typography.quote;
                    break;
                }
                break;
            case -323639786:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_UPSTREAM_UNAVAILABLE")) {
                    c = '#';
                    break;
                }
                break;
            case -301389037:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("EMPTY_RESPONSE")) {
                    c = Typography.dollar;
                    break;
                }
                break;
            case -186703013:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("GATEWAY_SESSION")) {
                    c = '%';
                    break;
                }
                break;
            case -145897602:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("DEVICE_CONFIRMATION")) {
                    c = Typography.amp;
                    break;
                }
                break;
            case -101726899:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("BUILD_DISABLED")) {
                    c = '\'';
                    break;
                }
                break;
            case 115629326:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SESSION_INVALID")) {
                    c = '(';
                    break;
                }
                break;
            case 257326905:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("BUILD_UNKNOWN")) {
                    c = ')';
                    break;
                }
                break;
            case 263038594:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SESSION_RESPONSE_INVALID")) {
                    c = '*';
                    break;
                }
                break;
            case 268347953:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("VERSION_POLICY_INVALID")) {
                    c = '+';
                    break;
                }
                break;
            case 333400784:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("VERSION_KEY_INVALID")) {
                    c = ',';
                    break;
                }
                break;
            case 357811994:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("VERSION_KEY_ID_INVALID")) {
                    c = '-';
                    break;
                }
                break;
            case 414655197:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("UPSTREAM_TIMEOUT")) {
                    c = '.';
                    break;
                }
                break;
            case 415145718:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("LOCAL_CHECK_TIMEOUT")) {
                    c = '/';
                    break;
                }
                break;
            case 613648965:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("DEVICE_DISABLED")) {
                    c = '0';
                    break;
                }
                break;
            case 622759327:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("BUILD_NOT_REGISTERED")) {
                    c = '1';
                    break;
                }
                break;
            case 630175426:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("CLOUD_FUNCTION")) {
                    c = '2';
                    break;
                }
                break;
            case 659453081:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("CANCELED")) {
                    c = '3';
                    break;
                }
                break;
            case 670976920:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("GLOBAL_DISABLED")) {
                    c = '4';
                    break;
                }
                break;
            case 750444849:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("DEVICE_BINDING_MISMATCH")) {
                    c = '5';
                    break;
                }
                break;
            case 820681482:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("BUILD_REGISTRATION")) {
                    c = '6';
                    break;
                }
                break;
            case 833944296:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_BANNED")) {
                    c = '7';
                    break;
                }
                break;
            case 856373894:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("CLOCK_INVALID")) {
                    c = '8';
                    break;
                }
                break;
            case 962430613:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("GATEWAY_UNAVAILABLE")) {
                    c = '9';
                    break;
                }
                break;
            case 1084364242:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("RESPONSE_TOO_LARGE")) {
                    c = ':';
                    break;
                }
                break;
            case 1141576252:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SESSION_EXPIRED")) {
                    c = ';';
                    break;
                }
                break;
            case 1176399318:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("REPLAY_REJECTED")) {
                    c = Typography.less;
                    break;
                }
                break;
            case 1219068857:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("LICENSE_INVALID")) {
                    c = '=';
                    break;
                }
                break;
            case 1270624934:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_UNAVAILABLE")) {
                    c = Typography.greater;
                    break;
                }
                break;
            case 1295576937:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_REJECT")) {
                    c = '?';
                    break;
                }
                break;
            case 1425308041:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_PARSE")) {
                    c = '@';
                    break;
                }
                break;
            case 1433767024:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("USER_DISABLED")) {
                    c = 'A';
                    break;
                }
                break;
            case 1444216287:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("LOCAL_CHECK_UNKNOWN")) {
                    c = 'B';
                    break;
                }
                break;
            case 1457454412:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("WIG_GRANT_REQUIRED")) {
                    c = 'C';
                    break;
                }
                break;
            case 1520615955:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("GATEWAY_BUILD_MANIFEST_INVALID")) {
                    c = 'D';
                    break;
                }
                break;
            case 1649278496:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("VERSION_ALGORITHM_INVALID")) {
                    c = 'E';
                    break;
                }
                break;
            case 1833788792:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SEAT_REJECTED")) {
                    c = 'F';
                    break;
                }
                break;
            case 1856641340:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("LOCAL_UNAVAILABLE")) {
                    c = 'G';
                    break;
                }
                break;
            case 1923153265:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("TOKEN_INVALID")) {
                    c = 'H';
                    break;
                }
                break;
            case 2067056115:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("ENVIRONMENT")) {
                    c = 'I';
                    break;
                }
                break;
            case 2072358231:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("REFRESH_CHALLENGE_INVALID")) {
                    c = 'J';
                    break;
                }
                break;
            case 2079351090:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("SESSION_REGION_CHANGED")) {
                    c = 'K';
                    break;
                }
                break;
            case 2083727243:
                str2 = "RESPONSE_TAMPERED";
                if (str.equals("BUILD_NOT_ALLOWED")) {
                    c = 'L';
                    break;
                }
                break;
            case 2086094106:
                if (str.equals("RESPONSE_TAMPERED")) {
                    c = 'M';
                }
            default:
                str2 = "RESPONSE_TAMPERED";
                break;
        }
        switch (c) {
            case 0:
            case '7':
                return "LICENSE_BANNED";
            case 1:
            case LockFreeTaskQueueCore.CLOSED_SHIFT /* 61 */:
            case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                return "WIG_REJECT";
            case 2:
            case 6:
            case 30:
            case '$':
            case MotionEventCompat.AXIS_GENERIC_12 /* 43 */:
            case MotionEventCompat.AXIS_GENERIC_14 /* 45 */:
            case ':':
            case 'E':
                return str2;
            case 3:
            case 5:
            case '\b':
            case '\t':
            case '\n':
            case 11:
            case 14:
            case 15:
            case 21:
            case 22:
            case 24:
            case 26:
            case 27:
            case 29:
            case ' ':
            case '\"':
            case MotionEventCompat.AXIS_GENERIC_6 /* 37 */:
            case MotionEventCompat.AXIS_GENERIC_7 /* 38 */:
            case MotionEventCompat.AXIS_GENERIC_16 /* 47 */:
            case '0':
            case '2':
            case '3':
            case '6':
            case '@':
            case TraceCatalog.Step.CHECK_SHOP_LOCK /* 65 */:
            case TraceCatalog.Step.RESOLVE_ENTITY /* 66 */:
            case 'G':
            case 'I':
            case 'M':
                return str;
            case 4:
                return "UPDATE_REQUIRED";
            case 7:
            case '\f':
            case 19:
            case MotionEventCompat.AXIS_GENERIC_2 /* 33 */:
            case '(':
            case MotionEventCompat.AXIS_GENERIC_11 /* 42 */:
            case '8':
            case ';':
            case '<':
            case TraceCatalog.Step.DISPATCH_ACTION /* 70 */:
            case 'H':
            case 'J':
                return "GATEWAY_SESSION";
            case '\r':
                return "DEVICE_CONFIRMATION";
            case 16:
            case 18:
            case MotionEventCompat.AXIS_GENERIC_13 /* 44 */:
            case 'D':
                return "INTEGRITY";
            case 17:
                return "SESSION_OCCUPIED";
            case 20:
            case MotionEventCompat.AXIS_RELATIVE_Y /* 28 */:
            case 31:
            case MotionEventCompat.AXIS_GENERIC_4 /* 35 */:
            case MotionEventCompat.AXIS_GENERIC_15 /* 46 */:
            case '9':
            case '>':
            case 'C':
                return "TRANSIENT";
            case 23:
            case MotionEventCompat.AXIS_GENERIC_8 /* 39 */:
            case MotionEventCompat.AXIS_GENERIC_10 /* 41 */:
            case '1':
            case 'L':
                return "BUILD_REGISTRATION";
            case 25:
                return "SESSION_REVOKED";
            case DsTokens.CTA_HEIGHT /* 52 */:
                return "MAINTENANCE";
            case '5':
                return "DEVICE_BINDING";
            case 'K':
                return "GATEWAY_SESSION";
            default:
                return i >= 500 ? "CLOUD_FUNCTION" : "UNKNOWN";
        }
    }
}
