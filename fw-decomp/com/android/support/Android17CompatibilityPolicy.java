package com.android.support;

/* loaded from: classes.dex */
public final class Android17CompatibilityPolicy {
    private static final int ANDROID_17_API = 37;
    private static final String STAGED_NATIVE_ARTIFACT_MODE = "500";

    public static boolean mayUseBackgroundAudio(int i, int i2, boolean z, boolean z2) {
        if (!z) {
            return false;
        }
        if (i < 37 || i2 < 37) {
            return true;
        }
        return z2;
    }

    private Android17CompatibilityPolicy() {
    }

    public static String stagedNativeArtifactMode() {
        return STAGED_NATIVE_ARTIFACT_MODE;
    }

    public static boolean isLoopbackOnlyHost(String str) {
        return "127.0.0.1".equals(str) || "::1".equals(str);
    }
}
