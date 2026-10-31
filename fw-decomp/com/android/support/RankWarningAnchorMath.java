package com.android.support;

/* loaded from: classes.dex */
final class RankWarningAnchorMath {
    static boolean isRecentRankActivity(long j, long j2, long j3) {
        return j > 0 && j3 >= 0 && j2 >= j && j2 - j <= j3;
    }

    static boolean shouldRenderPlayerRow(boolean z, boolean z2) {
        return !z || z2;
    }

    static boolean shouldReplaceAnchors(int i, int i2) {
        return i <= 0 || i2 > 0;
    }

    static boolean shouldSuppressInGameOverlays(int i) {
        return i >= 3;
    }

    static boolean shouldUseAutoAnchors(boolean z, boolean z2, int i, long j) {
        return !z && z2 && i > 0 && j > 0;
    }

    private RankWarningAnchorMath() {
    }

    static PixelRect mapNormalizedToPixels(float f, float f2, float f3, float f4, int i, int i2) {
        if (!isNormalizedRect(f, f2, f3, f4) || i <= 0 || i2 <= 0) {
            return null;
        }
        float f5 = i;
        int iRound = Math.round(f * f5);
        float f6 = i2;
        return new PixelRect(iRound, Math.round(f2 * f6), Math.round(f3 * f5), Math.round(f4 * f6), false);
    }

    static boolean mapNormalizedToPixels(float f, float f2, float f3, float f4, int i, int i2, int[] iArr) {
        if (iArr == null || iArr.length < 4) {
            throw new IllegalArgumentException("outLeftTopWidthHeight must have length >= 4");
        }
        if (!isNormalizedRect(f, f2, f3, f4) || i <= 0 || i2 <= 0) {
            return false;
        }
        float f5 = i;
        iArr[0] = Math.round(f * f5);
        float f6 = i2;
        iArr[1] = Math.round(f2 * f6);
        iArr[2] = Math.round(f3 * f5);
        iArr[3] = Math.round(f4 * f6);
        return true;
    }

    static boolean samePixelRect(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i, int i2) {
        if (i > 0 && i2 > 0) {
            float f9 = i;
            if (Math.round(f * f9) == Math.round(f5 * f9)) {
                float f10 = i2;
                if (Math.round(f2 * f10) == Math.round(f6 * f10) && Math.round(f3 * f9) == Math.round(f7 * f9) && Math.round(f4 * f10) == Math.round(f8 * f10)) {
                    return true;
                }
            }
        }
        return false;
    }

    static float safePlayerNameLeft(float f, boolean z, float f2, float f3) {
        if (!z || !isFinite(f)) {
            f = f2;
        }
        return isFinite(f3) ? Math.min(f, f3) : f;
    }

    static float minimumPlayerNameX(float f, float f2) {
        return !isFinite(f2) ? f : isFinite(f) ? Math.min(f, f2) : f2;
    }

    static float preferredPlayerNameX(float f, float f2, boolean z) {
        return z ? minimumPlayerNameX(f, f2) : f;
    }

    static float preferredPlayerLaneX(float f, float f2, boolean z, float f3) {
        if (!isFinite(f3)) {
            return f;
        }
        if (z && isFinite(f2)) {
            f3 = Math.min(f3, f2);
        }
        return minimumPlayerNameX(f, f3);
    }

    static boolean shouldSuppressInGameOverlays(int i, boolean z) {
        return shouldSuppressInGameOverlays(i);
    }

    static boolean shouldSuppressInGameOverlays(int i, boolean z, boolean z2) {
        return shouldSuppressInGameOverlays(i);
    }

    static boolean shouldSuppressInGameOverlays(int i, boolean z, boolean z2, boolean z3) {
        if (shouldSuppressInGameOverlays(i)) {
            return true;
        }
        return (!z3 || z || z2) ? false : true;
    }

    static PixelRect forPlayer(String str, PixelRect pixelRect) {
        return (str == null || str.isEmpty() || pixelRect == null) ? PixelRect.legacy() : pixelRect;
    }

    static FloatRect alignBoxToRowAvatar(PixelRect pixelRect) {
        if (pixelRect == null || pixelRect.useLegacyLayout) {
            return null;
        }
        return alignBoxToRowAvatar(pixelRect.left, pixelRect.top, pixelRect.width, pixelRect.height);
    }

    static FloatRect alignBoxToRowAvatar(float f, float f2, float f3, float f4) {
        if (isFinite(f) && isFinite(f2) && isFinite(f3) && isFinite(f4) && f4 > 0.0f && f3 >= f4) {
            return new FloatRect((f + f3) - f4, f2, f4, f4);
        }
        return null;
    }

    static boolean alignBoxToRowAvatar(float f, float f2, float f3, float f4, float[] fArr) {
        if (fArr == null || fArr.length < 4) {
            throw new IllegalArgumentException("outLeftTopWidthHeight must have length >= 4");
        }
        if (!isFinite(f) || !isFinite(f2) || !isFinite(f3) || !isFinite(f4) || f4 <= 0.0f || f3 < f4) {
            return false;
        }
        fArr[0] = (f + f3) - f4;
        fArr[1] = f2;
        fArr[2] = f4;
        fArr[3] = f4;
        return true;
    }

    static float smoothingAlpha(long j, float f, long j2) {
        if (j <= 0) {
            return 0.0f;
        }
        if (f <= 0.0f) {
            return 1.0f;
        }
        if (j2 > 0) {
            j = Math.min(j, j2);
        }
        return 1.0f - ((float) Math.exp((-j) / f));
    }

    static float lerp(float f, float f2, float f3) {
        return f + ((f2 - f) * Math.max(0.0f, Math.min(1.0f, f3)));
    }

    private static boolean isNormalizedRect(float f, float f2, float f3, float f4) {
        return isFinite(f) && isFinite(f2) && isFinite(f3) && isFinite(f4) && f >= 0.0f && f2 >= 0.0f && f3 > 0.0f && f4 > 0.0f && f + f3 <= 1.0f && f2 + f4 <= 1.0f;
    }

    private static boolean isFinite(float f) {
        return (Float.isNaN(f) || Float.isInfinite(f)) ? false : true;
    }

    static final class PixelRect {
        final int height;
        final int left;
        final int top;
        final boolean useLegacyLayout;
        final int width;

        private PixelRect(int i, int i2, int i3, int i4, boolean z) {
            this.left = i;
            this.top = i2;
            this.width = i3;
            this.height = i4;
            this.useLegacyLayout = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static PixelRect legacy() {
            return new PixelRect(0, 0, 0, 0, true);
        }
    }

    static final class FloatRect {
        final float height;
        final float left;
        final float top;
        final float width;

        private FloatRect(float f, float f2, float f3, float f4) {
            this.left = f;
            this.top = f2;
            this.width = f3;
            this.height = f4;
        }

        float right() {
            return this.left + this.width;
        }

        float bottom() {
            return this.top + this.height;
        }
    }
}
