package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
final class DeckCostChrome {
    static final String[] ICONS = {"◇", "◆", "◆", "◆", "◆", "★"};
    static final String[] LABELS = {"Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ"};

    private DeckCostChrome() {
    }

    static String icon(int i) {
        return (i < 1 || i > 6) ? "◇" : ICONS[i - 1];
    }

    static String label(int i) {
        return LABELS[(i < 1 || i > 6) ? 0 : i - 1];
    }

    static LinearLayout cardShell(Context context, int i) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(ScreenUtils.dp2px(context, 10.0f), ScreenUtils.dp2px(context, 6.0f), ScreenUtils.dp2px(context, 10.0f), ScreenUtils.dp2px(context, 6.0f));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = ScreenUtils.dp2px(context, i == 1 ? 0.0f : 6.0f);
        layoutParams.bottomMargin = ScreenUtils.dp2px(context, 4.0f);
        linearLayout.setLayoutParams(layoutParams);
        int iCostColor = GameHudPalette.costColor(i);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 10.0f));
        gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        int i2 = iCostColor & ViewCompat.MEASURED_SIZE_MASK;
        gradientDrawable.setColors(new int[]{805306368 | i2, 285212671});
        gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), i2 | 1073741824);
        linearLayout.setBackground(gradientDrawable);
        return linearLayout;
    }
}
