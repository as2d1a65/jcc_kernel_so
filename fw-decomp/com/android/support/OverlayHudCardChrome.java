package com.android.support;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
final class OverlayHudCardChrome {
    private OverlayHudCardChrome() {
    }

    static LinearLayout gradientCard(Context context, int i, int i2, int i3) {
        ScrollCuePanel scrollCuePanel = new ScrollCuePanel(context);
        scrollCuePanel.setOrientation(1);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{-233958364, -233169098, -234024158});
        gradientDrawable.setCornerRadius(i2);
        gradientDrawable.setStroke(i, 1711331839);
        scrollCuePanel.setBackground(gradientDrawable);
        scrollCuePanel.setElevation(i3);
        return scrollCuePanel;
    }

    static void clipRoundRect(View view, float f) {
        clipRoundRect(view, new float[]{f});
    }

    static void clipRoundRect(View view, final float[] fArr) {
        if (fArr == null) {
            return;
        }
        view.setClipToOutline(true);
        view.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.support.OverlayHudCardChrome.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view2, Outline outline) {
                outline.setRoundRect(0, 0, view2.getWidth(), view2.getHeight(), fArr[0]);
            }
        });
    }

    static void clipRoundRect(View view, final int[] iArr) {
        if (iArr == null) {
            return;
        }
        view.setClipToOutline(true);
        view.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.support.OverlayHudCardChrome.2
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view2, Outline outline) {
                outline.setRoundRect(0, 0, view2.getWidth(), view2.getHeight(), iArr[0]);
            }
        });
    }

    static LinearLayout headerBar(Context context, int i, int i2, int i3, int i4, int i5) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(i, i2, i3, i4);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{1140906495, 866670071, 587180544});
        float f = i5;
        gradientDrawable.setCornerRadii(new float[]{f, f, f, f, 0.0f, 0.0f, 0.0f, 0.0f});
        linearLayout.setBackground(gradientDrawable);
        return linearLayout;
    }

    static View statusDot(Context context, int i, int i2, int i3) {
        View view = new View(context);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(i2);
        view.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i, i);
        layoutParams.setMarginEnd(i3);
        view.setLayoutParams(layoutParams);
        return view;
    }

    static TextView title(Context context, String str, float f) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(-1);
        textView.setTextSize(f);
        textView.setTypeface(null, 1);
        textView.setShadowLayer(6.0f, 0.0f, 0.0f, -872359425);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        return textView;
    }

    static TextView dragHint(Context context, String str, float f, int i, int i2, int i3, int i4) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(-1426063361);
        textView.setTextSize(f);
        textView.setPadding(i, i2, i, i2);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(i3);
        gradientDrawable.setColor(872415231);
        textView.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMarginEnd(i4);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    static TextView closeButton(Context context, int i, float f, int i2) {
        TextView textView = new TextView(context);
        textView.setText("×");
        textView.setTextColor(-1);
        textView.setTextSize(f);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(1157582187);
        gradientDrawable.setStroke(i2, -1996534421);
        textView.setBackground(gradientDrawable);
        textView.setLayoutParams(new LinearLayout.LayoutParams(i, i));
        return textView;
    }
}
