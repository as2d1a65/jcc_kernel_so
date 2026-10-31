package com.android.support;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

/* loaded from: classes.dex */
final class NoticeCardStyle {
    private static final int EDGE_INNER = 536870911;
    private static final int EDGE_OUTER = 1560281087;
    static final int INK = -591106;
    private static final int SHEEN_BOTTOM = 16777215;
    private static final int SHEEN_MID = 184549375;
    private static final int SHEEN_TOP = 822083583;
    static final int SUB = -4734001;
    private static final int SURFACE_BOTTOM = -1005907684;
    private static final int SURFACE_TOP = -1205982144;

    private NoticeCardStyle() {
    }

    static int[] kindColors(int i) {
        if (i == 1) {
            return new int[]{-9115994, -13648254, -11348085};
        }
        if (i == 2) {
            return new int[]{-13960, -30116, -19372};
        }
        return new int[]{-7936010, -12605710, -11282450};
    }

    static int accentSolid(int i) {
        return kindColors(i)[2];
    }

    static Drawable glassBackground(Context context, int i) {
        int iMax = Math.max(1, ScreenUtils.dp2px(context, 1.0f));
        int iMax2 = Math.max(0, i - iMax);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable.setColors(new int[]{SURFACE_TOP, SURFACE_BOTTOM});
        gradientDrawable.setCornerRadius(i);
        gradientDrawable.setStroke(iMax, EDGE_OUTER);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(0);
        gradientDrawable2.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable2.setColors(new int[]{SHEEN_TOP, SHEEN_MID, 16777215});
        float f = iMax2;
        gradientDrawable2.setCornerRadius(f);
        GradientDrawable gradientDrawable3 = new GradientDrawable();
        gradientDrawable3.setShape(0);
        gradientDrawable3.setColor(0);
        gradientDrawable3.setCornerRadius(f);
        gradientDrawable3.setStroke(iMax, EDGE_INNER);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable, gradientDrawable2, gradientDrawable3});
        layerDrawable.setLayerInset(1, iMax, iMax, iMax, iMax);
        layerDrawable.setLayerInset(2, iMax, iMax, iMax, iMax);
        return layerDrawable;
    }

    static GradientDrawable progressFill(Context context, int i) {
        int[] iArrKindColors = kindColors(i);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientDrawable.setColors(new int[]{iArrKindColors[1], iArrKindColors[2], iArrKindColors[0]});
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 2.0f));
        return gradientDrawable;
    }

    static GradientDrawable accentRail(Context context, int i) {
        int[] iArrKindColors = kindColors(i);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable.setColors(new int[]{iArrKindColors[0], iArrKindColors[1]});
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 2.0f));
        return gradientDrawable;
    }
}
