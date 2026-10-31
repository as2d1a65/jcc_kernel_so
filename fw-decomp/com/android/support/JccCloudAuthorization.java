package com.android.support;

/* loaded from: classes.dex */
public final class JccCloudAuthorization {
    private JccCloudAuthorization() {
    }

    public static String currentToken() {
        try {
            String strBearerTokenNow = JccSessionState.shared().bearerTokenNow();
            return strBearerTokenNow == null ? "" : strBearerTokenNow;
        } catch (Exception | LinkageError unused) {
            return "";
        }
    }

    public static String sign(byte[] bArr) {
        try {
            return JccDeviceIdentity.shared().signBase64Url(bArr);
        } catch (Exception | LinkageError unused) {
            return "";
        }
    }
}
