package com.android.support;

/* loaded from: classes.dex */
final class TabRailMetrics {
    static final float ICON_LABEL_GAP_DP = 2.0f;
    static final float ICON_ONLY_MAX_DP = 22.0f;
    static final float ICON_ONLY_MIN_DP = 14.0f;
    private static final float[][] LABELLED_STEPS = {new float[]{18.0f, 11.0f}, new float[]{17.0f, 10.0f}, new float[]{16.0f, 10.0f}, new float[]{15.0f, 9.0f}};
    static final float MAX_GAP_DP = 8.0f;
    static final float MAX_ITEM_HEIGHT_DP = 46.0f;
    static final float MIN_GAP_DP = 1.0f;
    static final float VERTICAL_PADDING_DP = 6.0f;

    private TabRailMetrics() {
    }

    static final class Spec {
        final int gapPx;
        final int iconSizePx;
        final int itemHeightPx;
        final int labelTextSizePx;
        final boolean labelVisible;
        final int verticalPaddingPx;

        Spec(int i, int i2, int i3, int i4, boolean z, int i5) {
            this.itemHeightPx = i;
            this.gapPx = i2;
            this.iconSizePx = i3;
            this.verticalPaddingPx = i4;
            this.labelVisible = z;
            this.labelTextSizePx = i5;
        }

        int totalHeightPx(int i) {
            int i2 = this.verticalPaddingPx;
            return i <= 0 ? i2 * 2 : (i2 * 2) + (this.itemHeightPx * i) + (this.gapPx * (i - 1));
        }
    }

    static Spec resolve(int i, int i2, float f, float f2) {
        char c = 1;
        int iMax = Math.max(1, i2);
        float f3 = (f <= 0.0f || !Float.isFinite(f)) ? 1.0f : f;
        float f4 = (f2 <= 0.0f || !Float.isFinite(f2)) ? 1.0f : f2;
        int iPx = px(VERTICAL_PADDING_DP, f3);
        int iMax2 = Math.max(1, i - (iPx * 2));
        int iPx2 = px(2.0f, f3);
        int iPx3 = px(1.0f, f3);
        int iPx4 = px(MAX_ITEM_HEIGHT_DP, f3);
        float[][] fArr = LABELLED_STEPS;
        int length = fArr.length;
        char c2 = 0;
        int i3 = 0;
        while (i3 < length) {
            float[] fArr2 = fArr[i3];
            int iPx5 = px(fArr2[c2], f3);
            int iLabelTextSizePx = labelTextSizePx(fArr2[c], f3, f4);
            int iLabelLineHeightPx = iPx5 + iPx2 + labelLineHeightPx(iLabelTextSizePx);
            int iFitItemHeight = fitItemHeight(iMax2, iMax, iPx3);
            if (iFitItemHeight >= iLabelLineHeightPx) {
                int iMin = Math.min(iFitItemHeight, Math.max(iLabelLineHeightPx, iPx4));
                return new Spec(iMin, distributeGap(iMax2, iMax, iMin, iPx3, f3), iPx5, iPx, true, iLabelTextSizePx);
            }
            i3++;
            c = 1;
            c2 = 0;
        }
        int iMin2 = Math.min(fitItemHeight(iMax2, iMax, iPx3), iPx4);
        return new Spec(iMin2, distributeGap(iMax2, iMax, iMin2, iPx3, f3), clamp(iMin2 - px(VERTICAL_PADDING_DP, f3), px(ICON_ONLY_MIN_DP, f3), px(ICON_ONLY_MAX_DP, f3)), iPx, false, 0);
    }

    private static int fitItemHeight(int i, int i2, int i3) {
        return Math.max(1, (i - (i3 * (i2 - 1))) / i2);
    }

    private static int distributeGap(int i, int i2, int i3, int i4, float f) {
        return i2 <= 1 ? i4 : clamp((i - (i3 * i2)) / (i2 - 1), i4, px(MAX_GAP_DP, f));
    }

    private static int labelTextSizePx(float f, float f2, float f3) {
        return Math.max(1, Math.round(f * f2 * f3));
    }

    private static int labelLineHeightPx(int i) {
        return Math.round(i * 1.18f) + 1;
    }

    private static int px(float f, float f2) {
        return Math.max(1, Math.round(f * f2));
    }

    private static int clamp(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i, Math.max(i2, i3)));
    }
}
