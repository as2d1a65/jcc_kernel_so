package com.android.support;

/* loaded from: classes.dex */
final class CompanionPanelLayoutMath {
    static final float EDGE_MARGIN_DP = 12.0f;
    private static final float HERO_POOL_HEIGHT_DP = 500.0f;
    private static final float HERO_POOL_WIDTH_DP = 300.0f;
    static final int KIND_HERO_POOL = 1;
    static final int KIND_OPPONENT_CARDING = 2;
    static final float MIN_SUPPORTING_HEIGHT_DP = 160.0f;
    private static final float OPPONENT_CARDING_HEIGHT_DP = 500.0f;
    static final float PANEL_GAP_DP = 2.0f;
    static final float PHONE_MAX_HEIGHT_DP = 480.0f;
    static final float PHONE_MAX_WIDTH_DP = 300.0f;
    static final int SIDE_BOTTOM = 2;
    static final int SIDE_LEFT = -1;
    static final int SIDE_RIGHT = 1;
    static final int SIDE_TOP = -2;
    static final float SMART_LARGE_MAX_WIDTH_DP = 272.0f;
    static final float SMART_LARGE_MIN_WIDTH_DP = 264.0f;
    static final float SMART_LARGE_PREFERRED_WIDTH_DP = 268.0f;
    static final float SMART_PHONE_MAX_WIDTH_DP = 248.0f;
    static final float SMART_PHONE_MIN_WIDTH_DP = 232.0f;
    static final float SMART_PHONE_PREFERRED_WIDTH_DP = 236.0f;

    static boolean canUseViewInsets(boolean z, boolean z2, int i, int i2) {
        return (z || z2) && i >= 0 && i == i2;
    }

    private static int clampLong(long j, int i, int i2) {
        return j < ((long) i) ? i : j > ((long) i2) ? i2 : (int) j;
    }

    private static int positiveDimension(int i) {
        if (i > 0) {
            return i;
        }
        return 1;
    }

    private CompanionPanelLayoutMath() {
    }

    static PanelSize resolveSize(String str, int i, int i2, int i3, float f) {
        float fMin;
        int iMax;
        int iPositiveDimension = positiveDimension(i2);
        int iPositiveDimension2 = positiveDimension(i3);
        float fValidDensity = validDensity(f);
        int iBoundedMargin = boundedMargin(iPositiveDimension, px(EDGE_MARGIN_DP, fValidDensity));
        int iBoundedMargin2 = boundedMargin(iPositiveDimension2, px(EDGE_MARGIN_DP, fValidDensity));
        int iMax2 = Math.max(1, iPositiveDimension - (iBoundedMargin * 2));
        int iMax3 = Math.max(1, iPositiveDimension2 - (iBoundedMargin2 * 2));
        boolean z = "phone_portrait".equals(str) || "phone_landscape".equals(str);
        float f2 = SMART_PHONE_MIN_WIDTH_DP;
        if (i != 2) {
            fMin = 300.0f;
        } else if (z) {
            fMin = Math.max(SMART_PHONE_MIN_WIDTH_DP, Math.min(SMART_PHONE_PREFERRED_WIDTH_DP, SMART_PHONE_MAX_WIDTH_DP));
        } else {
            fMin = Math.max(SMART_LARGE_MIN_WIDTH_DP, Math.min(SMART_LARGE_PREFERRED_WIDTH_DP, SMART_LARGE_MAX_WIDTH_DP));
        }
        float fMin2 = 500.0f;
        if (z) {
            fMin = Math.min(fMin, 300.0f);
            fMin2 = Math.min(500.0f, PHONE_MAX_HEIGHT_DP);
        }
        int iPx = px(fMin, fValidDensity);
        if (i == 2) {
            if (!z) {
                f2 = 264.0f;
            }
            iMax = Math.max(px(f2, fValidDensity), Math.min(iPx, iMax2));
        } else {
            iMax = Math.max(1, Math.min(iPx, iMax2));
        }
        return new PanelSize(iMax, Math.max(1, Math.min(px(fMin2, fValidDensity), iMax3)), z);
    }

    static DockPlacement dockBesideAnchor(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12) {
        int iMin;
        int iPositiveDimension = positiveDimension(i);
        int iPositiveDimension2 = positiveDimension(i2);
        int iMax = Math.max(0, i11);
        int iBoundedMargin = boundedMargin(iPositiveDimension, Math.max(0, i12));
        int iBoundedMargin2 = boundedMargin(iPositiveDimension2, Math.max(0, i12));
        int iMax2 = Math.max(1, i5);
        int iClampLong = clampLong(i3, -iPositiveDimension, iPositiveDimension);
        int iMin2 = Math.min(iPositiveDimension, iMax2 + iClampLong);
        int iMax3 = Math.max(1, i7);
        int iMax4 = Math.max(1, Math.min(i8, iMax3));
        int iMax5 = Math.max(0, (iClampLong - iMax) - iBoundedMargin);
        int iMax6 = Math.max(0, ((iPositiveDimension - iMin2) - iMax) - iBoundedMargin);
        boolean z = iMax5 >= iMax3 || (iMax6 < iMax3 && iMax5 >= iMax6);
        if (!z) {
            iMax5 = iMax6;
        }
        boolean z2 = iMax5 < iMax4;
        if (z2) {
            iMin = Math.min(iMax3, Math.max(1, iPositiveDimension - (iBoundedMargin * 2)));
        } else {
            iMin = Math.min(iMax3, iMax5);
        }
        int iClampLong2 = clampLong(z ? r6 - iMin : iMin2 + iMax, iBoundedMargin, Math.max(iBoundedMargin, (iPositiveDimension - iBoundedMargin) - iMin));
        int iMax7 = Math.max(1, Math.min(i10, Math.max(1, iPositiveDimension2 - (iBoundedMargin2 * 2))));
        int i13 = iPositiveDimension2 - iBoundedMargin2;
        int iClampLong3 = clampLong(i4, iBoundedMargin2, Math.max(iBoundedMargin2, i13 - iMax7));
        return new DockPlacement(iClampLong2, iClampLong3, iMin, Math.max(iMax7, Math.min(Math.max(1, i9), Math.max(iMax7, i13 - iClampLong3))), z, z2);
    }

    static Placement place(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float f) {
        int i9;
        int iPositiveDimension = positiveDimension(i);
        int iPositiveDimension2 = positiveDimension(i2);
        float fValidDensity = validDensity(f);
        int iBoundedMargin = boundedMargin(iPositiveDimension, px(EDGE_MARGIN_DP, fValidDensity));
        int iBoundedMargin2 = boundedMargin(iPositiveDimension2, px(EDGE_MARGIN_DP, fValidDensity));
        int iPx = px(2.0f, fValidDensity);
        int iMax = Math.max(1, Math.min(positiveDimension(i7), Math.max(1, iPositiveDimension - (iBoundedMargin * 2))));
        int iMax2 = Math.max(1, Math.min(positiveDimension(i8), Math.max(1, iPositiveDimension2 - (iBoundedMargin2 * 2))));
        long j = i5;
        long j2 = iPx;
        long j3 = j + j2;
        long j4 = i3;
        long j5 = (j4 - j2) - iMax;
        int iMax3 = Math.max(iBoundedMargin, (iPositiveDimension - iBoundedMargin) - iMax);
        long j6 = iBoundedMargin;
        boolean z = false;
        boolean z2 = j3 >= j6 && j3 <= ((long) iMax3);
        if (j5 >= j6 && j5 <= iMax3) {
            z = true;
        }
        if (z2) {
            i9 = 1;
        } else if (z) {
            j3 = j5;
            i9 = -1;
        } else {
            int i10 = (((long) iPositiveDimension) - j6) - j >= j4 - j6 ? 1 : -1;
            if (i10 != 1) {
                j3 = j5;
            }
            i9 = i10;
        }
        return new Placement(clampLong(j3, iBoundedMargin, iMax3), clampLong(i4, iBoundedMargin2, Math.max(iBoundedMargin2, (iPositiveDimension2 - iBoundedMargin2) - iMax2)), iMax, iMax2, i9);
    }

    static SupportingPlacement placeSupportingPane(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float f) {
        return placeSupportingPane(i, i2, 0, 0, 0, 0, i3, i4, i5, i6, i7, i8, f);
    }

    static SupportingPlacement placeSupportingPane(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, float f) {
        int iPositiveDimension = positiveDimension(i);
        int iPositiveDimension2 = positiveDimension(i2);
        float fValidDensity = validDensity(f);
        int iClampLong = clampLong(Math.max(0, i3), 0, Math.max(0, iPositiveDimension - 1));
        int i13 = iPositiveDimension - iClampLong;
        int iClampLong2 = clampLong(Math.max(0, i5), 0, Math.max(0, i13 - 1));
        int iClampLong3 = clampLong(Math.max(0, i4), 0, Math.max(0, iPositiveDimension2 - 1));
        int i14 = iPositiveDimension2 - iClampLong3;
        int iClampLong4 = clampLong(Math.max(0, i6), 0, Math.max(0, i14 - 1));
        int iMax = Math.max(1, i13 - iClampLong2);
        int iMax2 = Math.max(1, i14 - iClampLong4);
        int iBoundedMargin = boundedMargin(iMax, px(EDGE_MARGIN_DP, fValidDensity));
        int iBoundedMargin2 = boundedMargin(iMax2, px(EDGE_MARGIN_DP, fValidDensity));
        int iPx = px(2.0f, fValidDensity);
        int i15 = iClampLong + iBoundedMargin;
        int i16 = iClampLong3 + iBoundedMargin2;
        int iMax3 = Math.max(i15 + 1, (iPositiveDimension - iClampLong2) - iBoundedMargin);
        int iMax4 = Math.max(i16 + 1, (iPositiveDimension2 - iClampLong4) - iBoundedMargin2);
        int iMax5 = Math.max(1, iMax3 - i15);
        int iMax6 = Math.max(1, iMax4 - i16);
        int iMax7 = Math.max(1, Math.min(Math.max(1, i9 - i7), iMax5));
        int iMax8 = Math.max(1, Math.min(Math.max(1, i10 - i8), iMax6));
        int iPositiveDimension3 = positiveDimension(i11);
        int iMinimumSupportingWidthPx = minimumSupportingWidthPx(iPositiveDimension3, fValidDensity);
        int iMax9 = Math.max(iPositiveDimension3, iMinimumSupportingWidthPx);
        int iMax10 = Math.max(1, Math.min(positiveDimension(i12), iMax6));
        long j = i7;
        int iClampLong5 = clampLong(j, i15, Math.max(i15, iMax3 - iMax7));
        long j2 = i8;
        int iClampLong6 = clampLong(j2, i16, Math.max(i16, iMax4 - iMax8));
        if (iMax5 < iMinimumSupportingWidthPx) {
            return new SupportingPlacement(iClampLong5, iClampLong6, iMax7, iMax8, i15, i16, iMax9, iMax10, 1, false, false);
        }
        int iMin = Math.min(iMax9, iMax5);
        int iMax11 = Math.max(0, (iMax5 - iMax7) - iPx);
        if (iMax11 >= iMinimumSupportingWidthPx) {
            int iMin2 = Math.min(iMin, iMax11);
            int iMin3 = Math.min(iMax10, iMax6);
            int iClampLong7 = clampLong(j, i15, Math.max(i15, iMax3 - ((iMax7 + iPx) + iMin2)));
            return supporting(iClampLong7, iClampLong6, iMax7, iMax8, iClampLong7 + iMax7 + iPx, clampLong(j2, i16, Math.max(i16, iMax4 - iMin3)), iMin2, iMin3, 1, (iClampLong7 == i7 && iClampLong6 == i8) ? false : true);
        }
        int i17 = iClampLong6 + iMax8;
        int iMax12 = Math.max(0, (iMax4 - i17) - iPx);
        int iMax13 = Math.max(0, (iClampLong6 - i16) - iPx);
        int iMin4 = Math.min(iMax10, px(MIN_SUPPORTING_HEIGHT_DP, fValidDensity));
        int iMin5 = Math.min(iMin, iMax5);
        int iClampLong8 = clampLong(j, i15, Math.max(i15, iMax3 - iMin5));
        boolean z = iMax12 >= iMin4;
        boolean z2 = iMax13 >= iMin4;
        if (z && (!z2 || iMax12 >= iMax13)) {
            boolean z3 = true;
            int iMin6 = Math.min(iMax10, Math.max(1, iMax12));
            int i18 = i17 + iPx;
            if (iClampLong5 == i7 && iClampLong6 == i8) {
                z3 = false;
            }
            return supporting(iClampLong5, iClampLong6, iMax7, iMax8, iClampLong8, i18, iMin5, iMin6, 2, z3);
        }
        boolean z4 = true;
        if (z2) {
            int iMin7 = Math.min(iMax10, iMax13);
            int i19 = (iClampLong6 - iPx) - iMin7;
            if (iClampLong5 == i7 && iClampLong6 == i8) {
                z4 = false;
            }
            return supporting(iClampLong5, iClampLong6, iMax7, iMax8, iClampLong8, i19, iMin5, iMin7, -2, z4);
        }
        return new SupportingPlacement(iClampLong5, iClampLong6, iMax7, iMax8, i15, i16, iMin5, iMax10, 1, false, false);
    }

    private static SupportingPlacement supporting(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, boolean z) {
        boolean z2 = false;
        if (i9 != 1 ? !(i9 != -1 ? i9 != 2 ? i6 + i8 > i2 : i6 < i2 + i4 : i5 + i7 > i) : i5 >= i + i3) {
            z2 = true;
        }
        return new SupportingPlacement(i, i2, i3, i4, i5, i6, i7, i8, i9, z, z2);
    }

    private static int minimumSupportingWidthPx(int i, float f) {
        int iPx = px(SMART_LARGE_MIN_WIDTH_DP, f);
        return i >= iPx ? iPx : px(SMART_PHONE_MIN_WIDTH_DP, f);
    }

    private static float validDensity(float f) {
        if (f <= 0.0f || !Float.isFinite(f)) {
            return 1.0f;
        }
        return f;
    }

    private static int boundedMargin(int i, int i2) {
        return Math.max(0, Math.min(i2, Math.max(0, (i - 1) / 2)));
    }

    private static int px(float f, float f2) {
        if (Double.isFinite(f * f2)) {
            return (int) Math.max(1.0d, Math.min(2147483647L, Math.round(r0)));
        }
        return 1;
    }

    static final class DockPlacement {
        final boolean dockedLeft;
        final int heightPx;
        final boolean overlapsAnchor;
        final int widthPx;
        final int xPx;
        final int yPx;

        DockPlacement(int i, int i2, int i3, int i4, boolean z, boolean z2) {
            this.xPx = i;
            this.yPx = i2;
            this.widthPx = i3;
            this.heightPx = i4;
            this.dockedLeft = z;
            this.overlapsAnchor = z2;
        }
    }

    static final class PanelSize {
        final boolean compact;
        final int heightPx;
        final int widthPx;

        PanelSize(int i, int i2, boolean z) {
            this.widthPx = i;
            this.heightPx = i2;
            this.compact = z;
        }
    }

    static final class Placement {
        final int heightPx;
        final int side;
        final int widthPx;
        final int xPx;
        final int yPx;

        Placement(int i, int i2, int i3, int i4, int i5) {
            this.xPx = i;
            this.yPx = i2;
            this.widthPx = i3;
            this.heightPx = i4;
            this.side = i5;
        }
    }

    static final class SupportingPlacement {
        final int anchorHeightPx;
        final boolean anchorShifted;
        final int anchorWidthPx;
        final int anchorXPx;
        final int anchorYPx;
        final boolean nonOverlapping;
        final int paneHeightPx;
        final int paneWidthPx;
        final int paneXPx;
        final int paneYPx;
        final int side;

        SupportingPlacement(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, boolean z, boolean z2) {
            this.anchorXPx = i;
            this.anchorYPx = i2;
            this.anchorWidthPx = i3;
            this.anchorHeightPx = i4;
            this.paneXPx = i5;
            this.paneYPx = i6;
            this.paneWidthPx = i7;
            this.paneHeightPx = i8;
            this.side = i9;
            this.anchorShifted = z;
            this.nonOverlapping = z2;
        }
    }
}
