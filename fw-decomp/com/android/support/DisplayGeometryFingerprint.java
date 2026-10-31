package com.android.support;

import android.graphics.Point;
import android.view.Display;

/* loaded from: classes.dex */
final class DisplayGeometryFingerprint {
    static final long UNAVAILABLE = Long.MIN_VALUE;

    private DisplayGeometryFingerprint() {
    }

    static long compute(Display display, String str) {
        if (display == null) {
            return Long.MIN_VALUE;
        }
        try {
            Point point = new Point();
            display.getRealSize(point);
            if (point.x > 0 && point.y > 0) {
                return ((((((527 + display.getDisplayId()) * 31) + display.getRotation()) * 31) + point.x) * 31) + point.y;
            }
            return Long.MIN_VALUE;
        } catch (RuntimeException e) {
            Dbg.d(str, "displayGeometryFingerprint: unable to read display geometry", e);
            return Long.MIN_VALUE;
        }
    }
}
