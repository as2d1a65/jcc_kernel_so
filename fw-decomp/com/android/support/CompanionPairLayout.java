package com.android.support;

/* loaded from: classes.dex */
final class CompanionPairLayout {
    static final float EDGE_MARGIN_DP = 12.0f;
    static final float MAX_CELL_SCALE = 1.14f;
    static final int MAX_GRID_COLUMNS = 6;
    static final float MAX_PANE_TO_MAIN_RATIO = 1.2f;
    static final float MIN_CELL_SCALE = 0.86f;
    static final int MODE_SINGLE_PANE = 1;
    static final int MODE_TWO_PANE = 0;
    static final float PANE_GAP_DP = 8.0f;
    static final float PANE_MAX_WIDTH_DP = 400.0f;
    static final float PANE_MIN_WIDTH_DP = 228.0f;
    static final float TWO_PANE_MAX_COVERAGE = 0.8f;

    private CompanionPairLayout() {
    }

    static final class Pair {
        final int mainHeightPx;
        final int mainWidthPx;
        final int mode;
        final int paneHeightPx;
        final int paneWidthPx;

        Pair(int i, int i2, int i3, int i4, int i5) {
            this.mode = i;
            this.mainWidthPx = i2;
            this.mainHeightPx = i3;
            this.paneWidthPx = i4;
            this.paneHeightPx = i5;
        }

        boolean twoPane() {
            return this.mode == 0;
        }
    }

    static final class Grid {
        final int cellHeightPx;
        final int cellWidthPx;
        final int columns;
        final int rows;
        final int spacingPx;

        Grid(int i, int i2, int i3, int i4, int i5) {
            this.columns = i;
            this.rows = i2;
            this.cellWidthPx = i3;
            this.cellHeightPx = i4;
            this.spacingPx = i5;
        }

        int contentWidthPx() {
            int i = this.columns;
            return (this.cellWidthPx * i) + ((i - 1) * this.spacingPx);
        }

        int contentHeightPx() {
            int i = this.rows;
            return (this.cellHeightPx * i) + ((i - 1) * this.spacingPx);
        }
    }

    static Pair resolve(String str, int i, int i2, float f, int i3, int i4) {
        float fValidDensity = validDensity(f);
        int iMax = Math.max(1, i);
        int iMax2 = Math.max(1, i2);
        int iPx = px(EDGE_MARGIN_DP, fValidDensity);
        int iPx2 = px(PANE_GAP_DP, fValidDensity);
        int iPx3 = px(PANE_MIN_WIDTH_DP, fValidDensity);
        int i5 = iPx * 2;
        int iMin = Math.min(Math.max(1, i4), Math.max(1, iMax2 - i5));
        int iMax3 = Math.max(1, i3);
        int i6 = iMax - i5;
        int iMin2 = Math.min(iMax3, Math.max(1, i6));
        int iMin3 = Math.min((i6 - iPx2) - iMin2, (Math.round(iMax * TWO_PANE_MAX_COVERAGE) - iPx2) - iMin2);
        if ((!"tablet".equals(str) && !"emulator_desktop".equals(str)) || iMin3 < iPx3) {
            return new Pair(1, iMin2, iMin, 0, 0);
        }
        return new Pair(0, iMin2, iMin, Math.min(iMin3, Math.max(iPx3, Math.min(px(PANE_MAX_WIDTH_DP, fValidDensity), Math.round(iMin2 * MAX_PANE_TO_MAIN_RATIO)))), iMin);
    }

    static Grid resolveGrid(int i, int i2, int i3, int i4, int i5) {
        int i6;
        int i7;
        int iMax = Math.max(1, i);
        int iMax2 = Math.max(1, i2);
        int iMax3 = Math.max(1, i3);
        int iMax4 = Math.max(1, i4);
        int iMax5 = Math.max(0, i5);
        int iMax6 = Math.max(1, Math.min(6, (iMax + iMax5) / (iMax3 + iMax5)));
        int iFill = fill(iMax, iMax6, iMax5);
        int iMax7 = Math.max(1, (iMax2 + iMax5) / (iMax4 + iMax5));
        int iFill2 = fill(iMax2, iMax7, iMax5);
        float f = iMax4;
        int iMax8 = Math.max(1, Math.round(MAX_CELL_SCALE * f));
        int iMax9 = Math.max(1, Math.round(f * MIN_CELL_SCALE));
        if (iFill2 > iMax8) {
            int i8 = iMax7 + 1;
            int iFill3 = fill(iMax2, i8, iMax5);
            if (iFill3 >= iMax9) {
                i6 = i8;
                i7 = iFill3;
            } else {
                i6 = iMax7;
                i7 = iMax8;
            }
        } else {
            i6 = iMax7;
            i7 = iFill2;
        }
        while (i6 > 1 && (i6 * i7) + ((i6 - 1) * iMax5) > iMax2) {
            i6--;
        }
        return new Grid(iMax6, i6, iFill, i7, iMax5);
    }

    private static int fill(int i, int i2, int i3) {
        int iMax = Math.max(1, i2);
        return Math.max(1, (i - ((iMax - 1) * i3)) / iMax);
    }

    private static float validDensity(float f) {
        if (f <= 0.0f || Float.isNaN(f) || Float.isInfinite(f)) {
            return 1.0f;
        }
        return f;
    }

    private static int px(float f, float f2) {
        return Math.max(1, Math.round(f * f2));
    }
}
