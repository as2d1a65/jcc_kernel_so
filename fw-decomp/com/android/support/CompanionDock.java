package com.android.support;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import com.android.support.diagnostics.TraceCatalog;

/* loaded from: classes.dex */
public final class CompanionDock {

    public enum Side {
        LEFT,
        RIGHT
    }

    public static final class Placement {
        public final int height;
        public final int left;
        public final Side side;
        public final int top;
        public final int width;

        public Placement(int i, int i2, int i3, int i4, Side side) {
            this.left = i;
            this.top = i2;
            this.width = i3;
            this.height = i4;
            this.side = side;
        }

        public Rect toRect() {
            int i = this.left;
            int i2 = this.top;
            return new Rect(i, i2, this.width + i, this.height + i2);
        }

        public String toString() {
            return "Placement{" + this.side + " " + this.left + "," + this.top + " " + this.width + "x" + this.height + "}";
        }
    }

    private CompanionDock() {
    }

    public static Placement calculate(Context context, Rect rect, Side side, int i, int i2) {
        DisplayMetrics displayMetrics;
        Rect rect2;
        Side side2;
        Side side3;
        Side side4;
        if (context == null) {
            displayMetrics = new DisplayMetrics();
        } else {
            displayMetrics = context.getResources().getDisplayMetrics();
        }
        int i3 = displayMetrics.widthPixels > 0 ? displayMetrics.widthPixels : 1080;
        int i4 = displayMetrics.heightPixels > 0 ? displayMetrics.heightPixels : 1920;
        int iMax = Math.max(1, Math.round(displayMetrics.density));
        int i5 = iMax * 8;
        int iMax2 = Math.max(4, i5);
        int iMax3 = Math.max(iMax2, i5);
        int i6 = iMax3 * 2;
        int iMax4 = Math.max(1, i3 - i6);
        int iClamp = clamp(i > 0 ? i : Math.round(i3 * 0.4f), i > 0 ? 1 : Math.min(iMax * TraceCatalog.Feature.OPPONENT_PREDICTION, iMax4), Math.min(iMax * 520, iMax4));
        int iMax5 = Math.max(1, i4 - i6);
        int iClamp2 = clamp(i2 > 0 ? i2 : Math.round(i4 * 0.68f), i2 > 0 ? 1 : Math.min(iMax * TraceCatalog.Feature.SELL_ALL_IN, iMax5), iMax5);
        if (rect == null) {
            rect2 = new Rect(Math.max(iMax3, (i3 - dp(context, TraceCatalog.Feature.SHOP_HUD)) / 2), Math.max(iMax3, (i4 - dp(context, 480)) / 2), Math.max(iMax3, (i3 - dp(context, TraceCatalog.Feature.SHOP_HUD)) / 2) + dp(context, TraceCatalog.Feature.SHOP_HUD), Math.max(iMax3, (i4 - dp(context, 480)) / 2) + dp(context, 480));
        } else {
            rect2 = new Rect(rect);
        }
        int i7 = i3 - iMax3;
        int i8 = i4 - iMax3;
        int iMin = rect2.right + iMax2;
        int iMax6 = (rect2.left - iMax2) - iClamp;
        boolean z = iMin + iClamp <= i7;
        boolean z2 = iMax6 >= iMax3;
        Side side5 = side == null ? Side.RIGHT : side;
        if (side5 == Side.RIGHT && z) {
            side2 = Side.RIGHT;
        } else {
            if (side5 == Side.LEFT && z2) {
                side3 = Side.LEFT;
            } else if (side5 == Side.RIGHT && z2) {
                side3 = Side.LEFT;
            } else if (side5 == Side.LEFT && z) {
                side2 = Side.RIGHT;
            } else {
                int iMax7 = Math.max(1, i7 - iMin);
                int iMax8 = Math.max(1, (iMax6 + iClamp) - iMax3);
                if (side5 == Side.RIGHT && iMax7 >= iMax8) {
                    side2 = Side.RIGHT;
                    iClamp = Math.min(iClamp, iMax7);
                    iMin = Math.max(iMax3, i7 - iClamp);
                } else {
                    if (side5 == Side.LEFT && iMax8 >= iMax7) {
                        Side side6 = Side.LEFT;
                        iClamp = Math.min(iClamp, iMax8);
                        side4 = side6;
                        iMin = iMax3;
                        int i9 = iClamp;
                        return new Placement(clamp(iMin, iMax3, Math.max(iMax3, i7 - i9)), clamp(rect2.top, iMax3, Math.max(iMax3, i8 - iClamp2)), i9, iClamp2, side4);
                    }
                    if (side5 == Side.RIGHT) {
                        side3 = Side.LEFT;
                        iClamp = Math.min(iClamp, iMax8);
                        iMax6 = Math.max(iMax3, (rect2.left - iMax2) - iClamp);
                    } else {
                        side2 = Side.RIGHT;
                        iClamp = Math.min(iClamp, iMax7);
                        iMin = Math.min(i7 - iClamp, rect2.right + iMax2);
                    }
                }
            }
            side4 = side3;
            iMin = iMax6;
            int i92 = iClamp;
            return new Placement(clamp(iMin, iMax3, Math.max(iMax3, i7 - i92)), clamp(rect2.top, iMax3, Math.max(iMax3, i8 - iClamp2)), i92, iClamp2, side4);
        }
        side4 = side2;
        int i922 = iClamp;
        return new Placement(clamp(iMin, iMax3, Math.max(iMax3, i7 - i922)), clamp(rect2.top, iMax3, Math.max(iMax3, i8 - iClamp2)), i922, iClamp2, side4);
    }

    private static int clamp(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i, Math.max(i2, i3)));
    }

    private static int dp(Context context, int i) {
        return context == null ? i : Math.round(i * context.getResources().getDisplayMetrics().density);
    }
}
