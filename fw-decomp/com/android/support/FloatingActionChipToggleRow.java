package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import java.util.function.IntUnaryOperator;

/* loaded from: classes.dex */
final class FloatingActionChipToggleRow {

    interface Host {
        int chipScalePercent();

        Context context();

        boolean isChipEnabled(int i);

        void setChipEnabled(int i, boolean z);

        void setChipScalePercent(int i);
    }

    private FloatingActionChipToggleRow() {
    }

    static Runnable install(View view, int i, int i2, Host host) {
        if (view == null || host == null) {
            return null;
        }
        ViewGroup viewGroupContentColumn = contentColumn(view.findViewById(i));
        if (viewGroupContentColumn == null) {
            Log.w("FloatingWindowService", "悬浮键开关未安装：找不到竖排内容列 kind=" + i2);
            return null;
        }
        View viewFindViewWithTag = viewGroupContentColumn.findViewWithTag(tagFor(i2));
        if (viewFindViewWithTag != null) {
            viewGroupContentColumn.removeView(viewFindViewWithTag);
        }
        Runnable[] runnableArr = new Runnable[1];
        viewGroupContentColumn.addView(build(host, i2, runnableArr), 0);
        return runnableArr[0];
    }

    private static ViewGroup contentColumn(View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        if (viewGroup instanceof ScrollView) {
            if (viewGroup.getChildCount() == 0) {
                return null;
            }
            View childAt = viewGroup.getChildAt(0);
            if (isVerticalColumn(childAt)) {
                return (ViewGroup) childAt;
            }
            return null;
        }
        if (isVerticalColumn(viewGroup)) {
            return viewGroup;
        }
        return null;
    }

    private static boolean isVerticalColumn(View view) {
        return (view instanceof LinearLayout) && ((LinearLayout) view).getOrientation() == 1;
    }

    private static String tagFor(int i) {
        return "fab_chip_toggle_" + i;
    }

    private static View build(Host host, int i, Runnable[] runnableArr) {
        Context context = host.context();
        int iDp2px = ScreenUtils.dp2px(context, 10.0f);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setTag(tagFor(i));
        linearLayout.setOrientation(1);
        linearLayout.setPadding(iDp2px, iDp2px, iDp2px, iDp2px);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 10.0f));
        gradientDrawable.setColor(352321535);
        gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), 861858815);
        linearLayout.setBackground(gradientDrawable);
        OverlayUiMotion.enableRoundedClip(linearLayout);
        View viewBuildSizeRow = buildSizeRow(host, context, runnableArr);
        linearLayout.addView(buildToggleRow(host, i, context, viewBuildSizeRow));
        linearLayout.addView(viewBuildSizeRow);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = ScreenUtils.dp2px(context, 8.0f);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    private static View buildToggleRow(final Host host, final int i, Context context, final View view) {
        int iDp2px = ScreenUtils.dp2px(context, 2.0f);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setText(FloatingActionChipPolicy.toggleTitle(i));
        textView.setTextColor(GameHudPalette.CYAN);
        textView.setTextSize(13.0f);
        textView.setTypeface(textView.getTypeface(), 1);
        final TextView textView2 = new TextView(context);
        textView2.setTextColor(GameHudPalette.TEXT_DIM);
        textView2.setTextSize(10.0f);
        textView2.setPadding(0, iDp2px, 0, 0);
        textView2.setText(FloatingActionChipPolicy.toggleHint(i, host.isChipEnabled(i)));
        linearLayout2.addView(textView);
        linearLayout2.addView(textView2);
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(0, -2, 1.0f));
        Switch r0 = new Switch(context);
        r0.setChecked(host.isChipEnabled(i));
        view.setVisibility(r0.isChecked() ? 0 : 8);
        r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.FloatingActionChipToggleRow$$ExternalSyntheticLambda5
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                FloatingActionChipToggleRow.lambda$buildToggleRow$0(host, i, textView2, view, compoundButton, z);
            }
        });
        linearLayout.addView(r0, new LinearLayout.LayoutParams(-2, -2));
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        return linearLayout;
    }

    static /* synthetic */ void lambda$buildToggleRow$0(Host host, int i, TextView textView, View view, CompoundButton compoundButton, boolean z) {
        host.setChipEnabled(i, z);
        textView.setText(FloatingActionChipPolicy.toggleHint(i, z));
        view.setVisibility(z ? 0 : 8);
    }

    private static View buildSizeRow(final Host host, Context context, Runnable[] runnableArr) {
        int iDp2px = ScreenUtils.dp2px(context, 8.0f);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = iDp2px;
        linearLayout.setLayoutParams(layoutParams);
        TextView textView = new TextView(context);
        textView.setText("按钮大小");
        textView.setTextColor(GameHudPalette.TEXT_DIM);
        textView.setTextSize(11.0f);
        linearLayout.addView(textView, new LinearLayout.LayoutParams(0, -2, 1.0f));
        final TextView textView2 = new TextView(context);
        textView2.setTextColor(GameHudPalette.CYAN);
        textView2.setTextSize(12.0f);
        textView2.setGravity(17);
        textView2.setTypeface(textView2.getTypeface(), 1);
        textView2.setMinWidth(ScreenUtils.dp2px(context, 48.0f));
        Runnable runnable = new Runnable() { // from class: com.android.support.FloatingActionChipToggleRow$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                textView2.setText(FloatingActionChipPolicy.normalizeScalePercent(host.chipScalePercent()) + "%");
            }
        };
        runnable.run();
        if (runnableArr != null && runnableArr.length > 0) {
            runnableArr[0] = runnable;
        }
        final IntUnaryOperator intUnaryOperator = new IntUnaryOperator() { // from class: com.android.support.FloatingActionChipToggleRow$$ExternalSyntheticLambda1
            @Override // java.util.function.IntUnaryOperator
            public final int applyAsInt(int i) {
                return FloatingActionChipToggleRow.lambda$buildSizeRow$2(host, i);
            }
        };
        View viewBuildStepper = buildStepper(context, "−", new Runnable() { // from class: com.android.support.FloatingActionChipToggleRow$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                intUnaryOperator.applyAsInt(-10);
            }
        });
        View viewBuildStepper2 = buildStepper(context, "+", new Runnable() { // from class: com.android.support.FloatingActionChipToggleRow$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                intUnaryOperator.applyAsInt(10);
            }
        });
        linearLayout.addView(viewBuildStepper);
        linearLayout.addView(textView2, new LinearLayout.LayoutParams(-2, -2));
        linearLayout.addView(viewBuildStepper2);
        return linearLayout;
    }

    static /* synthetic */ int lambda$buildSizeRow$2(Host host, int i) {
        int iNormalizeScalePercent = FloatingActionChipPolicy.normalizeScalePercent(host.chipScalePercent());
        int i2 = i + iNormalizeScalePercent;
        if (i2 < 10 || i2 > 150) {
            return iNormalizeScalePercent;
        }
        host.setChipScalePercent(FloatingActionChipPolicy.normalizeScalePercent(i2));
        return i2;
    }

    private static View buildStepper(Context context, String str, final Runnable runnable) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(GameHudPalette.TEXT);
        textView.setTextSize(16.0f);
        textView.setTypeface(textView.getTypeface(), 1);
        textView.setGravity(17);
        textView.setClickable(true);
        textView.setFocusable(true);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 8.0f));
        gradientDrawable.setColor(452984831);
        gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), 872415231);
        textView.setBackground(gradientDrawable);
        OverlayUiMotion.installPressScale(textView);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.FloatingActionChipToggleRow$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                runnable.run();
            }
        });
        int iDp2px = ScreenUtils.dp2px(context, 34.0f);
        textView.setLayoutParams(new LinearLayout.LayoutParams(iDp2px, iDp2px));
        return textView;
    }
}
