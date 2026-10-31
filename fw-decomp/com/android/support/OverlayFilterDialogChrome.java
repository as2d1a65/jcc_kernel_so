package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
final class OverlayFilterDialogChrome {
    private OverlayFilterDialogChrome() {
    }

    static LinearLayout container(Context context, int i, int i2) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 14.0f));
        gradientDrawable.setColor(-266855388);
        gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), i);
        linearLayout.setBackground(gradientDrawable);
        float f = i2;
        linearLayout.setPadding(ScreenUtils.dp2px(context, f), ScreenUtils.dp2px(context, f), ScreenUtils.dp2px(context, f), ScreenUtils.dp2px(context, f));
        return linearLayout;
    }

    static RelativeLayout titleBar(Context context, String str, float f, int i, final Runnable runnable) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextSize(f);
        textView.setTextColor(i);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams.addRule(13);
        textView.setLayoutParams(layoutParams);
        relativeLayout.addView(textView);
        TextView textView2 = new TextView(context);
        textView2.setText("✕");
        textView2.setTextSize(16.0f);
        textView2.setTextColor(-48060);
        textView2.setTypeface(null, 1);
        textView2.setGravity(17);
        textView2.setPadding(ScreenUtils.dp2px(context, 8.0f), ScreenUtils.dp2px(context, 2.0f), ScreenUtils.dp2px(context, 4.0f), ScreenUtils.dp2px(context, 2.0f));
        textView2.setClickable(true);
        textView2.setFocusable(true);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.addRule(21);
        layoutParams2.addRule(15);
        textView2.setLayoutParams(layoutParams2);
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OverlayFilterDialogChrome$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                runnable.run();
            }
        });
        relativeLayout.addView(textView2);
        return relativeLayout;
    }

    static TextView hint(Context context, String str, float f, int i, int i2) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextSize(f);
        textView.setTextColor(-7829368);
        textView.setGravity(17);
        textView.setPadding(0, ScreenUtils.dp2px(context, i), 0, ScreenUtils.dp2px(context, i2));
        return textView;
    }

    static final class ActionBar {
        final LinearLayout bar;
        final TextView deselectAll;
        final TextView selectAll;

        ActionBar(LinearLayout linearLayout, TextView textView, TextView textView2) {
            this.bar = linearLayout;
            this.selectAll = textView;
            this.deselectAll = textView2;
        }
    }

    static ActionBar actionBar(Context context, float f, int i, int i2, int i3) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(17);
        linearLayout.setPadding(0, 0, 0, ScreenUtils.dp2px(context, i3));
        TextView textView = new TextView(context);
        textView.setText("全选");
        textView.setTextSize(f);
        textView.setTextColor(-16733628);
        float f2 = i;
        float f3 = i2;
        textView.setPadding(ScreenUtils.dp2px(context, f2), ScreenUtils.dp2px(context, f3), ScreenUtils.dp2px(context, f2), ScreenUtils.dp2px(context, f3));
        textView.setClickable(true);
        TextView textView2 = new TextView(context);
        textView2.setText("清空");
        textView2.setTextSize(f);
        textView2.setTextColor(-5618620);
        textView2.setPadding(ScreenUtils.dp2px(context, f2), ScreenUtils.dp2px(context, f3), ScreenUtils.dp2px(context, f2), ScreenUtils.dp2px(context, f3));
        textView2.setClickable(true);
        linearLayout.addView(textView);
        linearLayout.addView(textView2);
        return new ActionBar(linearLayout, textView, textView2);
    }

    static ScrollView scrollPane(Context context) {
        ScrollView scrollView = new ScrollView(context);
        scrollView.setVerticalScrollBarEnabled(true);
        scrollView.setScrollbarFadingEnabled(false);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0f));
        ScrollEdgeCue.attach(scrollView);
        return scrollView;
    }

    static final class BottomBar {
        final LinearLayout bar;
        final TextView cancel;
        final TextView confirm;

        BottomBar(LinearLayout linearLayout, TextView textView, TextView textView2) {
            this.bar = linearLayout;
            this.confirm = textView;
            this.cancel = textView2;
        }
    }

    static BottomBar bottomBar(Context context, int i, float f, int i2, int i3, int i4, int i5) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(17);
        linearLayout.setPadding(0, ScreenUtils.dp2px(context, i), 0, 0);
        TextView textView = new TextView(context);
        textView.setText("确  定");
        textView.setTextSize(f);
        textView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        textView.setBackgroundColor(i2);
        textView.setGravity(17);
        float f2 = i3;
        float f3 = i4;
        textView.setPadding(ScreenUtils.dp2px(context, f2), ScreenUtils.dp2px(context, f3), ScreenUtils.dp2px(context, f2), ScreenUtils.dp2px(context, f3));
        textView.setClickable(true);
        TextView textView2 = new TextView(context);
        textView2.setText("取  消");
        textView2.setTextSize(f);
        textView2.setTextColor(-3355444);
        textView2.setBackgroundColor(-13421773);
        textView2.setGravity(17);
        textView2.setPadding(ScreenUtils.dp2px(context, f2), ScreenUtils.dp2px(context, f3), ScreenUtils.dp2px(context, f2), ScreenUtils.dp2px(context, f3));
        textView2.setClickable(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        float f4 = i5;
        layoutParams.setMargins(ScreenUtils.dp2px(context, f4), 0, ScreenUtils.dp2px(context, f4), 0);
        textView.setLayoutParams(layoutParams);
        textView2.setLayoutParams(layoutParams);
        linearLayout.addView(textView2);
        linearLayout.addView(textView);
        return new BottomBar(linearLayout, textView, textView2);
    }

    static WindowManager.LayoutParams overlayParams(Context context, int i, float f, float f2) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(Math.min(ScreenUtils.dp2px(context, i), (int) (ScreenUtils.getRealScreenWidth(context) * f)), (int) (ScreenUtils.getRealScreenHeight(context) * f2), 2038, 264, -3);
        layoutParams.gravity = 17;
        return layoutParams;
    }
}
