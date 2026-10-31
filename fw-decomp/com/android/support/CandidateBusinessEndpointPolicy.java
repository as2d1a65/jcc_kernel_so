package com.android.support;

/* loaded from: classes.dex */
public final class CandidateBusinessEndpointPolicy {
    public static boolean allowsProductionBusinessEndpoints(boolean z) {
        return !z;
    }

    private CandidateBusinessEndpointPolicy() {
    }

    public static boolean rejectCandidateBuild(boolean z, Runnable runnable) {
        if (!z) {
            return false;
        }
        if (runnable == null) {
            throw new IllegalArgumentException("onRejected must not be null");
        }
        runnable.run();
        return true;
    }
}
