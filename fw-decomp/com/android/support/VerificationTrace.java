package com.android.support;

import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import kotlin.UByte;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class VerificationTrace {
    private static final String TAG = "JccVerify";
    private final String attemptId = UUID.randomUUID().toString().replace("-", "");
    private final List<JSONObject> events = new ArrayList();
    private final String id;
    private final Sink sink;
    private static final char[] ID_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern CODE_PATTERN = Pattern.compile("[A-Z][A-Z0-9_]{1,31}");
    private static final Pattern NAMED_SECRET = Pattern.compile("(?i)(card|card_key|license|token|device|device_id|imei|signature|hmac|key)([\"']?\\s*[:=]\\s*[\"']?)[^\\s&,}\"']+");
    private static final Pattern QUERY = Pattern.compile("([?&][^=\\s]+)=([^&#\\s]+)");
    private static final Pattern LONG_IDENTIFIER = Pattern.compile("(?<![A-Za-z0-9])[A-Za-z0-9_+/=-]{12,}(?![A-Za-z0-9])");
    private static final Set<String> CODES = Collections.unmodifiableSet(new HashSet(Arrays.asList("START", "RETRY", "OK", "CANCELLED", "INPUT_INVALID", "NET_DNS", "NET_CONNECT", "NET_TLS", "WIG_HTTP", "WIG_REJECT", "WIG_PARSE", "WIG_AUTHORIZATION_EXPIRED", "WIG_AUTHORIZATION_DENIED", "WIG_UPSTREAM_UNAVAILABLE", "GATEWAY_SESSION", "CLOUD_FUNCTION", "PRESENCE", "INTEGRITY", "ENVIRONMENT", "ADB_DEBUG", "DEVICE_BINDING", "BUILD_REGISTRATION", "TRUST_REJECTED", "LICENSE_BANNED", "TRANSIENT", "RESPONSE_TAMPERED", "INJECT_PENDING", "LOCAL_CHECK_TIMEOUT", "LOCAL_CHECK_BUSY", "LOCAL_CHECK_UNKNOWN", "SESSION_REGION_CHANGED", "UNKNOWN", "NATIVE_WRONG_LENGTH", "NATIVE_BAD_SIGNATURE", "NATIVE_MALFORMED_LEASE", "NATIVE_CLOCK_OVERFLOW", "NATIVE_VM_REJECTED", "NATIVE_VM_PROGRAM_INVALID", "NATIVE_MANIFEST_INVALID", "NATIVE_REVOKED", "NATIVE_LEASE_EXPIRED", "NATIVE_TRANSPORT", "NATIVE_NATIVE_UNAVAILABLE", "NATIVE_CANCELED", "NATIVE_BUILD_NOT_REGISTERED", "NATIVE_UNKNOWN", "WIG_HTTP", "WIG_TIMEOUT", "TRANSIENT", "WIG_UPSTREAM_UNAVAILABLE", "WIG_UNAVAILABLE", "UPSTREAM_TIMEOUT", "GATEWAY_UNAVAILABLE", "WIG_GRANT_REQUIRED", "WIG_BANNED", "WIG_LICENSE_BANNED", "USER_DISABLED", "DEVICE_DISABLED", "BUILD_UNKNOWN", "BUILD_NOT_REGISTERED", "BUILD_DISABLED", "BUILD_NOT_ALLOWED", "CLOUD_RELEASE_REJECTED", "UPDATE_REQUIRED", "GLOBAL_DISABLED", "SEAT_OCCUPIED", "SEAT_INACTIVE", "SEAT_CHILD_MISMATCH", "SEAT_REJECTED", "DEVICE_PROOF_FAILED", "WIG_AUTHORIZATION_DENIED", "WIG_AUTHORIZATION_EXPIRED", "WIG_REJECT", "LICENSE_INVALID", "LICENSE_REJECTED", "GATEWAY_BUILD_MANIFEST_INVALID", "INTEGRITY", "VERSION_KEY_UNCONFIGURED", "VERSION_KEY_INVALID", "EMPTY_RESPONSE", "INVALID_RESPONSE", "RESPONSE_TOO_LARGE", "REDIRECT_REJECTED", "VERSION_ALGORITHM_INVALID", "VERSION_KEY_ID_INVALID", "VERSION_SIGNATURE_INVALID", "VERSION_POLICY_INVALID", "LICENSE_BANNED", "BUILD_REGISTRATION", "TRUST_REJECTED", "GATEWAY_SESSION", "DEVICE_BINDING", "DEVICE_CONFIRMATION", "SESSION_OCCUPIED", "MAINTENANCE", "LOCAL_UNAVAILABLE", "LOGIN_TIMEOUT", "RESPONSE_TAMPERED", "PRESENCE", "CANCELED", "ADB_DEBUG", "ENVIRONMENT", "CLOUD_FUNCTION", "INJECT_PENDING", "LOCAL_CHECK_TIMEOUT", "LOCAL_CHECK_BUSY", "LOCAL_CHECK_UNKNOWN", "NET_DNS", "NET_CONNECT", "NET_TLS", "WIG_PARSE", "INPUT_INVALID", "DEVICE_BINDING_MISMATCH", "SESSION_REVOKED", "SESSION_RESPONSE_INVALID", "SESSION_EXPIRED", "SESSION_INVALID", "TOKEN_EXPIRED", "TOKEN_INVALID", "AUTH_REQUIRED", "CLOCK_INVALID", "REPLAY_REJECTED", "REFRESH_CHALLENGE_INVALID")));

    interface Sink {
        void write(String str);
    }

    public enum Stage {
        INPUT,
        NETWORK,
        WIG,
        LICENSE,
        GATEWAY,
        CLOUD_FUNCTION,
        PRESENCE,
        INTEGRITY,
        COMPLETE
    }

    private VerificationTrace(String str, Sink sink) {
        this.id = str;
        this.sink = sink;
    }

    public static VerificationTrace start() {
        return start(new Sink() { // from class: com.android.support.VerificationTrace$$ExternalSyntheticLambda0
            @Override // com.android.support.VerificationTrace.Sink
            public final void write(String str) {
                VerificationTrace.lambda$start$0(str);
            }
        });
    }

    static /* synthetic */ void lambda$start$0(String str) {
        if (str.contains(" raw=")) {
            Log.w(TAG, str);
        } else {
            Dbg.i(TAG, str);
        }
    }

    static VerificationTrace start(Sink sink) {
        if (sink == null) {
            throw new IllegalArgumentException("sink must not be null");
        }
        char[] cArr = new char[8];
        for (int i = 0; i < 8; i++) {
            char[] cArr2 = ID_ALPHABET;
            cArr[i] = cArr2[RANDOM.nextInt(cArr2.length)];
        }
        return new VerificationTrace(new String(cArr), sink);
    }

    public String id() {
        return this.id;
    }

    public void event(Stage stage, String str, long j) {
        if (stage == null || !isAllowedCode(str)) {
            throw new IllegalArgumentException("diagnostic stage/code not allow-listed");
        }
        long jMax = Math.max(0L, Math.min(j, 86400000L));
        remember(stage, str, jMax);
        this.sink.write("verify trace=" + this.id + " stage=" + stage.name() + " code=" + str + " duration_ms=" + jMax);
    }

    public void failure(Stage stage, String str, String str2, long j) {
        failure(stage, str, str2, j, 0);
    }

    public void failure(Stage stage, String str, String str2, long j, int i) {
        if (!isAllowedCode(str)) {
            str = "UNKNOWN";
        }
        String str3 = isAllowedCode(str2) ? str2 : "UNKNOWN";
        remember(stage, str, j, i);
        this.sink.write("verify trace=" + this.id + " stage=" + stage.name() + " raw=" + str + " code=" + str3 + " duration_ms=" + Math.max(0L, Math.min(j, 86400000L)));
        RuntimeDiagnostics runtimeDiagnostics = RuntimeDiagnostics.get();
        if (runtimeDiagnostics != null) {
            runtimeDiagnostics.record("AUTH", str, "FAIL", "trace=" + this.id + " phase=" + stage.name());
        }
    }

    static String legacyReportCode(String str) {
        str.hashCode();
        switch (str) {
            case "UPDATE_REQUIRED":
                return "BUILD_REGISTRATION";
            case "MAINTENANCE":
                return "CLOUD_FUNCTION";
            case "LOGIN_TIMEOUT":
            case "CANCELED":
                return "TRANSIENT";
            case "SESSION_REVOKED":
            case "SESSION_OCCUPIED":
                return "GATEWAY_SESSION";
            case "DEVICE_CONFIRMATION":
                return "DEVICE_BINDING";
            case "DEVICE_DISABLED":
            case "USER_DISABLED":
                return "LICENSE_BANNED";
            case "LOCAL_UNAVAILABLE":
                return "LOCAL_CHECK_UNKNOWN";
            default:
                return str;
        }
    }

    private void remember(Stage stage, String str, long j) {
        remember(stage, str, j, 0);
    }

    private synchronized void remember(Stage stage, String str, long j, int i) {
        try {
            if (this.events.size() >= 32) {
                this.events.remove(0);
            }
            this.events.add(new JSONObject().put("stage", stage.name()).put("code", str).put("http_status", Math.max(0, Math.min(i, 599))).put("duration_ms", Math.max(0L, Math.min(j, 86400000L))));
        } catch (JSONException unused) {
        }
    }

    synchronized JSONObject reportPayload(String str) throws JSONException {
        JSONObject jSONObjectPut;
        jSONObjectPut = new JSONObject().put("schema", 1).put("attempt_id", this.attemptId).put("trace_id", this.id);
        if (!isAllowedCode(str)) {
            str = "UNKNOWN";
        }
        return jSONObjectPut.put("code", str).put("events", new JSONArray((Collection) this.events));
    }

    static boolean isAllowedCode(String str) {
        return str != null && CODE_PATTERN.matcher(str).matches() && CODES.contains(str);
    }

    static boolean isReportableFailure(String str) {
        return (!isAllowedCode(str) || "START".equals(str) || "RETRY".equals(str) || "OK".equals(str) || "CANCELLED".equals(str) || "CANCELED".equals(str)) ? false : true;
    }

    public static String shortFingerprint(String str) throws NoSuchAlgorithmException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            if (str == null) {
                str = "";
            }
            byte[] bArrDigest = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(12);
            for (int i = 0; i < 6; i++) {
                sb.append(String.format(Locale.US, "%02x", Integer.valueOf(bArrDigest[i] & UByte.MAX_VALUE)));
            }
            Arrays.fill(bArrDigest, (byte) 0);
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    static String redact(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return LONG_IDENTIFIER.matcher(QUERY.matcher(NAMED_SECRET.matcher(str).replaceAll("$1$2[REDACTED]")).replaceAll("$1=[REDACTED]")).replaceAll("[REDACTED]");
    }
}
