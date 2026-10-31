package com.android.support;

/* loaded from: classes.dex */
final class OpponentPredictSourcePolicy {
    static final int SOURCE_IMAGE = 1;
    static final int SOURCE_NATIVE = 2;
    static final int SOURCE_OFF = 0;

    static boolean imageOverlayEnabled(int i, boolean z, boolean z2) {
        if (i == 0) {
            return false;
        }
        return i == 1 ? z2 || z : z;
    }

    private OpponentPredictSourcePolicy() {
    }

    static boolean imageOverlayEnabled(int i, boolean z) {
        return imageOverlayEnabled(i, z, true);
    }
}
