package com.android.support;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.View;
import android.widget.TextView;

/* loaded from: classes.dex */
public final class DsTokens {
    public static final int ACCENT = -13541720;
    public static final int BRAND = -16220325;
    public static final int BRAND_A16 = 688422747;
    public static final int BRAND_A24 = 1023967067;
    public static final int BRAND_A40 = 1711832923;
    public static final int BRAND_DEEP = -16357815;
    public static final int BRAND_SOFT = -2231318;
    public static final int CONTROL_HEIGHT = 48;
    public static final int CTA_HEIGHT = 52;
    public static final int DANGER = -4377272;
    public static final int DANGER_A16 = 700265800;
    public static final int DANGER_A40 = 1723675976;
    public static final int INK = -1;
    public static final int LED = 8;
    public static final int LINE = -2038298;
    public static final int LINE_STRONG = -3550765;
    public static final int OK = -15305670;
    public static final int OK_A16 = 689337402;
    public static final int OK_A40 = 1712747578;
    public static final int RADIUS_LG = 8;
    public static final int RADIUS_MD = 8;
    public static final int RADIUS_PILL = 999;
    public static final int RADIUS_SM = 8;
    public static final int RADIUS_XL = 8;
    public static final int RIPPLE_DARK = 1711276032;
    public static final int SP1 = 4;
    public static final int SP2 = 8;
    public static final int SP3 = 12;
    public static final int SP4 = 16;
    public static final int SP5 = 20;
    public static final int SURFACE_1 = -1;
    public static final int SURFACE_2 = -1117710;
    public static final int SURFACE_3 = -1840919;
    public static final int TAP_MIN = 48;
    public static final int TEXT_1 = -14670037;
    public static final int TEXT_2 = -11312794;
    public static final int TEXT_3 = -10062726;
    public static final float TEXT_BODY = 15.0f;
    public static final float TEXT_CAPTION = 11.0f;
    public static final float TEXT_HEADLINE = 16.0f;
    public static final float TEXT_LABEL = 13.0f;
    public static final float TEXT_MICRO = 10.0f;
    public static final float TEXT_TITLE = 20.0f;
    public static final int WARN = -6725632;
    public static final int WARN_A16 = 697917440;
    public static final int WARN_A40 = 1721327616;
    public static final int WINDOW_BOTTOM = -657673;
    public static final int WINDOW_TOP = -657673;

    public static GradientDrawable shape(Context context, int i, int i2, int i3) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, i3));
        gradientDrawable.setColor(i);
        if (i2 != 0) {
            gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), i2);
        }
        return gradientDrawable;
    }

    public static GradientDrawable verticalGradient(Context context, int i, int i2, int i3, int i4) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable.setColors(new int[]{i, i2});
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, i4));
        if (i3 != 0) {
            gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), i3);
        }
        return gradientDrawable;
    }

    public static GradientDrawable primaryFill(Context context, int i) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientDrawable.setColor(BRAND);
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, i));
        return gradientDrawable;
    }

    public static Drawable withRipple(Context context, Drawable drawable, int i, int i2) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, i2));
        gradientDrawable.setColor(-1);
        return new RippleDrawable(ColorStateList.valueOf(i), drawable, gradientDrawable);
    }

    public static Drawable outlineButton(Context context, int i, int i2, int i3) {
        return withRipple(context, shape(context, 0, i, i3), i2, i3);
    }

    public static Drawable secondaryButton(Context context, int i) {
        return withRipple(context, shape(context, SURFACE_2, LINE_STRONG, i), BRAND_A24, i);
    }

    public static Drawable primaryButton(Context context, int i) {
        return withRipple(context, primaryFill(context, i), RIPPLE_DARK, i);
    }

    public static GradientDrawable led(Context context, int i) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(i);
        return gradientDrawable;
    }

    public static GradientDrawable accentBar(Context context, int i) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setColor(i);
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 2.0f));
        return gradientDrawable;
    }

    public static void pressable(View view) {
        if (view == null) {
            return;
        }
        view.setClickable(true);
        view.setFocusable(true);
        OverlayUiMotion.installPressScale(view);
    }

    public static void plateShadow(TextView textView) {
        if (textView == null) {
            return;
        }
        float f = textView.getResources().getDisplayMetrics().density;
        textView.setShadowLayer(2.0f * f, 0.0f, f * 1.0f, GameHudPalette.SHADOW_COLOR);
    }

    private DsTokens() {
    }
}
