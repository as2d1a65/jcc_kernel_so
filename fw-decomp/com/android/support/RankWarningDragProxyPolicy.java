package com.android.support;

/* loaded from: classes.dex */
final class RankWarningDragProxyPolicy {
    static final float WINDOW_ALPHA = 0.01f;

    static boolean isDetachConfirmed(boolean z, boolean z2) {
        return (z || z2) ? false : true;
    }

    static boolean shouldAttach(boolean z, boolean z2, int i, int i2) {
        return z && z2 && i > 0 && i2 > 0;
    }

    private RankWarningDragProxyPolicy() {
    }

    static Bounds compactBounds(int i, int i2, int i3, int i4) {
        return new Bounds(i, i2, Math.max(1, i3), Math.max(1, i4));
    }

    static final class Bounds {
        final int height;
        final int width;
        final int x;
        final int y;

        Bounds(int i, int i2, int i3, int i4) {
            this.x = i;
            this.y = i2;
            this.width = i3;
            this.height = i4;
        }
    }
}
