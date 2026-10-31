package com.android.support;

/* loaded from: classes.dex */
final class BoardOverlayPaintPolicy {

    enum Mode {
        HIDDEN,
        PLACEHOLDER,
        LIVE
    }

    private BoardOverlayPaintPolicy() {
    }

    static Mode resolve(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        if (!z || !z2) {
            return Mode.HIDDEN;
        }
        if (z3 && !z5) {
            return Mode.LIVE;
        }
        if (z6 || (z4 && !z5)) {
            return Mode.PLACEHOLDER;
        }
        return Mode.HIDDEN;
    }
}
