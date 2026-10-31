package com.android.support;

import com.android.support.diagnostics.TraceCatalog;

/* loaded from: classes.dex */
public final class QuickActionOverlayPolicy {
    static final int ALL_IN_ALERT_MIN_VOLUME = 60;
    public static final int ALL_IN_COST_MASK_BOTH = 24;
    public static final int ALL_IN_COST_MASK_FIVE = 16;
    public static final int ALL_IN_COST_MASK_FOUR = 8;
    public static final int DEFAULT_ALL_IN_ACK_TIMEOUT_MS = 3000;
    public static final int DEFAULT_ALL_IN_COST_MASK = 16;
    public static final int DOCK_TIER_LARGE = 2;
    public static final int DOCK_TIER_PHONE = 1;
    public static final int DOCK_TIER_XS = 0;
    static final long POPUP_OPENING_TOUCH_GUARD_MS = 240;

    public static boolean canStepDown(int i, int i2) {
        return i > i2;
    }

    public static boolean canStepUp(int i, int i2) {
        return i < i2;
    }

    public static int normalizeAllInBlockedAction(int i) {
        return i == 1 ? 1 : 0;
    }

    public static int normalizeAllInCostMask(int i) {
        int i2 = i & 24;
        if (i2 == 0) {
            return 16;
        }
        return i2;
    }

    public static boolean shouldConsumePopupOpeningTouch(long j, long j2, long j3, boolean z, boolean z2) {
        if (!z || j <= 0 || j3 < j) {
            return false;
        }
        return ((j2 > 0L ? 1 : (j2 == 0L ? 0 : -1)) > 0 && (j2 > j ? 1 : (j2 == j ? 0 : -1)) <= 0) || (z2 && ((j3 - j) > POPUP_OPENING_TOUCH_GUARD_MS ? 1 : ((j3 - j) == POPUP_OPENING_TOUCH_GUARD_MS ? 0 : -1)) <= 0);
    }

    private QuickActionOverlayPolicy() {
    }

    public static final class Rect {
        public final int bottom;
        public final int left;
        public final int right;
        public final int top;

        public Rect(int i, int i2, int i3, int i4) {
            this.left = Math.min(i, i3);
            this.top = Math.min(i2, i4);
            this.right = Math.max(i, i3);
            this.bottom = Math.max(i2, i4);
        }
    }

    public static final class Point {
        public final int x;
        public final int y;

        private Point(int i, int i2) {
            this.x = i;
            this.y = i2;
        }
    }

    public static final class Normalized {
        public final double x;
        public final double y;

        public Normalized(double d, double d2) {
            this.x = d;
            this.y = d2;
        }
    }

    public static final class PopupPlacement {
        public final boolean below;
        public final int x;
        public final int y;

        private PopupPlacement(int i, int i2, boolean z) {
            this.x = i;
            this.y = i2;
            this.below = z;
        }
    }

    public static Point defaultPosition(Rect rect, int i, int i2, int i3) {
        return clamp(rect, rect.left + Math.max(0, i3), rect.top + Math.max(0, i3), i, i2);
    }

    public static Point clamp(Rect rect, int i, int i2, int i3, int i4) {
        return new Point(clampInt(i, rect.left, Math.max(rect.left, rect.right - Math.max(0, i3))), clampInt(i2, rect.top, Math.max(rect.top, rect.bottom - Math.max(0, i4))));
    }

    public static Point snapHorizontal(Rect rect, int i, int i2, int i3, int i4) {
        Point pointClamp = clamp(rect, i, i2, i3, i4);
        int iMax = Math.max(rect.left, rect.right - Math.max(0, i3));
        if (Math.abs(pointClamp.x - rect.left) <= Math.abs(iMax - pointClamp.x)) {
            iMax = rect.left;
        }
        return new Point(iMax, pointClamp.y);
    }

    public static Point finishDrag(Rect rect, int i, int i2, int i3, int i4) {
        return clamp(rect, i, i2, i3, i4);
    }

    public static Normalized normalize(Rect rect, int i, int i2, int i3, int i4) {
        Point pointClamp = clamp(rect, i, i2, i3, i4);
        int iMax = Math.max(0, (rect.right - rect.left) - Math.max(0, i3));
        int iMax2 = Math.max(0, (rect.bottom - rect.top) - Math.max(0, i4));
        return new Normalized(iMax == 0 ? 0.0d : (pointClamp.x - rect.left) / iMax, iMax2 != 0 ? (pointClamp.y - rect.top) / iMax2 : 0.0d);
    }

    public static Point restore(Rect rect, Normalized normalized, int i, int i2) {
        return clamp(rect, rect.left + ((int) Math.round((normalized == null ? 0.0d : clampDouble(normalized.x, 0.0d, 1.0d)) * Math.max(0, (rect.right - rect.left) - Math.max(0, i)))), rect.top + ((int) Math.round((normalized != null ? clampDouble(normalized.y, 0.0d, 1.0d) : 0.0d) * Math.max(0, (rect.bottom - rect.top) - Math.max(0, i2)))), i, i2);
    }

    public static PopupPlacement placePopup(Rect rect, Rect rect2, int i, int i2, int i3) {
        int iMin = Math.min(Math.max(0, i), rect.right - rect.left);
        int iMin2 = Math.min(Math.max(0, i2), rect.bottom - rect.top);
        int iClampInt = clampInt(rect2.left, rect.left, Math.max(rect.left, rect.right - iMin));
        int iMax = rect2.bottom + Math.max(0, i3);
        int iMax2 = (rect2.top - Math.max(0, i3)) - iMin2;
        boolean z = iMax + iMin2 <= rect.bottom || iMax2 < rect.top;
        if (!z) {
            iMax = iMax2;
        }
        Point pointClamp = clamp(rect, iClampInt, iMax, iMin, iMin2);
        return new PopupPlacement(pointClamp.x, pointClamp.y, z);
    }

    public static boolean useCompactLabels(int i, int i2) {
        return i < Math.max(0, i2);
    }

    public static int popupWidthForSafeArea(int i, int i2, int i3) {
        return Math.min(Math.max(0, i2), Math.max(0, Math.max(0, i) - (Math.max(0, i3) * 2)));
    }

    public static final class DockMetrics {
        public final int actionPaddingDp;
        public final int arrowWidthDp;
        public final int cornerRadiusDp;
        public final int dividerHeightDp;
        public final int handleTextSizeSp;
        public final int handleWidthDp;
        public final int minActionWidthDp;
        public final int shellPaddingDp;
        public final int textSizeSp;

        DockMetrics(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
            this.textSizeSp = i;
            this.handleTextSizeSp = i2;
            this.actionPaddingDp = i3;
            this.dividerHeightDp = i4;
            this.handleWidthDp = i5;
            this.arrowWidthDp = i6;
            this.cornerRadiusDp = i7;
            this.shellPaddingDp = i8;
            this.minActionWidthDp = i9;
        }
    }

    public static int dockSizeTier(int i) {
        int iMax = Math.max(0, i);
        if (iMax >= 600) {
            return 2;
        }
        return iMax >= 360 ? 1 : 0;
    }

    public static DockMetrics compactDockMetrics(int i) {
        int iDockSizeTier = dockSizeTier(i);
        if (iDockSizeTier == 0) {
            return new DockMetrics(10, 13, 6, 16, 32, 24, 10, 5, 48);
        }
        if (iDockSizeTier == 2) {
            return new DockMetrics(12, 15, 12, 20, 40, 30, 14, 6, 60);
        }
        return new DockMetrics(12, 14, 10, 18, 36, 28, 12, 6, 54);
    }

    public static int slimRowColumns(int i, int i2, int i3, int i4, int i5, int i6) {
        if (i == 2) {
            return resolveColumns(2, i2, i3, i4, i5, i6);
        }
        return resolveColumns(1, i2, i3, i4, i5, i6);
    }

    public static int adaptiveActionUnitWidthDp(int i) {
        int iMax = Math.max(0, i);
        if (iMax >= 800) {
            return 92;
        }
        return iMax >= 600 ? 88 : 84;
    }

    public static int adaptiveRailWidthDp(int i) {
        return Math.max(0, i) >= 600 ? 48 : 44;
    }

    public static int adaptiveDockMaxColumns(int i) {
        return Math.max(0, i) >= 600 ? 3 : 2;
    }

    public static int capDockColumns(int i, int i2) {
        return Math.max(1, Math.min(Math.max(1, i), adaptiveDockMaxColumns(i2)));
    }

    public static int adaptiveSettingsWidthDp(int i) {
        int iMax = Math.max(0, i);
        return Math.min(TraceCatalog.Feature.SHOP_HUD, Math.min(Math.max(0, iMax - 24), (int) Math.round(iMax * 0.78d)));
    }

    public static int adaptiveSettingsMaxHeight(int i, int i2) {
        int iMax = Math.max(1, i);
        return Math.min(Math.max(1, iMax - (Math.max(0, i2) * 2)), Math.max(1, (int) Math.floor(iMax * 0.7d)));
    }

    public static int settingsGridColumns(int i) {
        return Math.max(0, i) >= 300 ? 3 : 2;
    }

    public static int normalizeVisibleMask(int i, int i2) {
        int iMax = Math.max(0, i2);
        int i3 = i & iMax;
        return i3 == 0 ? iMax : i3;
    }

    public static int migrateVisibleMask(int i, int i2, int i3) {
        int iMax = Math.max(0, i3);
        int iMax2 = Math.max(0, i2);
        return (iMax2 == 0 || i != iMax2 || (iMax2 & iMax) != iMax2 || iMax2 == iMax) ? normalizeVisibleMask(i, iMax) : iMax;
    }

    public static int resolveColumns(int i, int i2, int i3, int i4, int i5, int i6) {
        int iMax = Math.max(1, i2);
        int iMax2 = Math.max(1, i3 - Math.max(0, i4));
        int iMax3 = Math.max(1, i5);
        int iMax4 = Math.max(0, i6);
        int iMax5 = Math.max(1, Math.min(iMax, (iMax2 + iMax4) / (iMax3 + iMax4)));
        if (i == 1) {
            return iMax5;
        }
        if (i == 2) {
            return Math.max(1, Math.min(iMax, Math.min(2, iMax5)));
        }
        int iMin = Math.min(3, iMax5);
        if (iMax != 4 || iMin < 2) {
            return Math.max(1, Math.min(iMax, iMin));
        }
        return 2;
    }

    public static int rowsFor(int i, int i2) {
        int iMax = Math.max(0, i);
        if (iMax == 0) {
            return 0;
        }
        int iMax2 = Math.max(1, i2);
        return ((iMax + iMax2) - 1) / iMax2;
    }

    public static int itemsInRow(int i, int i2, int i3) {
        int iMax = Math.max(0, i);
        int iMax2 = Math.max(1, i2);
        return Math.max(0, Math.min(iMax2, iMax - (Math.max(0, i3) * iMax2)));
    }

    public static int normalizeControlHeightDp(int i) {
        return clampInt(i, 44, 54);
    }

    public static int normalizeActionGapDp(int i) {
        return clampInt(i, 4, 12);
    }

    public static int normalizeOpacityPercent(int i) {
        return clampInt(i, 72, 100);
    }

    public static int normalizeDockScalePercent(int i) {
        return clampInt(i, 50, 100);
    }

    public static int scaleControlHeightDp(int i, int i2) {
        return Math.max(22, (int) Math.round((normalizeControlHeightDp(i) * normalizeDockScalePercent(i2)) / 100.0d));
    }

    public static DockMetrics scaleDockMetrics(DockMetrics dockMetrics, int i) {
        int iNormalizeDockScalePercent = normalizeDockScalePercent(i);
        if (dockMetrics == null) {
            dockMetrics = compactDockMetrics(SwapBoardMinimizePolicy.FULL_WIDTH_DP);
        }
        return new DockMetrics(Math.max(8, scaleInt(dockMetrics.textSizeSp, iNormalizeDockScalePercent)), Math.max(10, scaleInt(dockMetrics.handleTextSizeSp, iNormalizeDockScalePercent)), Math.max(4, scaleInt(dockMetrics.actionPaddingDp, iNormalizeDockScalePercent)), Math.max(12, scaleInt(dockMetrics.dividerHeightDp, iNormalizeDockScalePercent)), Math.max(24, scaleInt(dockMetrics.handleWidthDp, iNormalizeDockScalePercent)), Math.max(20, scaleInt(dockMetrics.arrowWidthDp, iNormalizeDockScalePercent)), Math.max(8, scaleInt(dockMetrics.cornerRadiusDp, iNormalizeDockScalePercent)), Math.max(3, scaleInt(dockMetrics.shellPaddingDp, iNormalizeDockScalePercent)), Math.max(36, scaleInt(dockMetrics.minActionWidthDp, iNormalizeDockScalePercent)));
    }

    public static int normalizeAllInMaxTargets(int i) {
        return clampInt(i, 1, 9);
    }

    public static int normalizeAllInReserveGold(int i) {
        return clampInt(i, 0, 50);
    }

    public static int normalizeAllInAckTimeoutMs(int i) {
        return clampInt(i, 2000, 6000);
    }

    public static String allInStopMessage(int i) {
        if (i == 1) {
            return "梭哈已完成：三星已凑够设定数";
        }
        if (i == 3) {
            return "梭哈设置无效，请重新保存";
        }
        if (i == 5) {
            return "棋盘数据不完整，梭哈停止";
        }
        if (i == 6) {
            return "对局已变化，梭哈停止";
        }
        switch (i) {
            case 8:
                return "目标追不到或备战席已被目标占满，梭哈停止";
            case 9:
                return "等待游戏数据超时，梭哈停止";
            case 10:
                return "目标牌库已抽空，追不到三星了";
            default:
                return null;
        }
    }

    static float allInAlertVolume(int i) {
        if (i < 0) {
            i = 0;
        } else if (i > 100) {
            i = 100;
        }
        return Math.max(60, i) / 100.0f;
    }

    private static int scaleInt(int i, int i2) {
        return Math.max(1, (int) Math.round((i * i2) / 100.0d));
    }

    private static int clampInt(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i3, i));
    }

    private static double clampDouble(double d, double d2, double d3) {
        return !Double.isFinite(d) ? d2 : Math.max(d2, Math.min(d3, d));
    }
}
