package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
final class SwapBoardActionChrome {
    static final String STOP_TEXT = "■  停一下";
    static final String STOP_TOAST = "停了停了，不换了";
    static final String TIP_TEXT = "空格子会跳过，有棋子才换 · 点上面这条立马停";
    static final int[][] AXIS_COLORS = {new int[]{-13058568, -16337196}, new int[]{-22700, -38037}, new int[]{-5745161, -1292135}};
    static final String[] AXIS_ICONS = {"⇄", "⇅", "✦"};
    static final String[] AXIS_MAIN = {"左右调", "上下换", "乱换"};
    static final String[] AXIS_SUB = {"左右翻一下", "上下翻一下", "瞎鸡巴乱换"};
    static final int[] AXIS_MAP = {0, 1, 2};
    static final String[] AXIS_TOASTS = {"开搞 · 左右调", "开搞 · 上下换", "开搞 · 瞎鸡巴乱换"};

    interface Host {
        void animateClick(View view);

        Context context();

        void onAxisChosen(int i, String str);

        void onStopChosen();
    }

    static int buttonFill(int i) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | 1140850688;
    }

    static int buttonStroke(int i) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | DsTokens.RIPPLE_DARK;
    }

    static int iconShadow(int i) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | (-872415232);
    }

    private SwapBoardActionChrome() {
    }

    static LinearLayout buildAxisRow(final Host host, int i, int i2, int i3, int i4) {
        Context context = host.context();
        LinearLayout linearLayout = new LinearLayout(context);
        char c = 0;
        linearLayout.setOrientation(0);
        final int i5 = 0;
        while (true) {
            String[] strArr = AXIS_MAIN;
            if (i5 >= strArr.length) {
                return linearLayout;
            }
            final int i6 = AXIS_MAP[i5];
            int[] iArr = AXIS_COLORS[i5];
            int i7 = iArr[c];
            int i8 = iArr[1];
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(1);
            linearLayout2.setGravity(17);
            linearLayout2.setPadding(i2, i3, i2, i3);
            linearLayout2.setClickable(true);
            linearLayout2.setFocusable(true);
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{buttonFill(i7), buttonFill(i8)});
            gradientDrawable.setCornerRadius(i4);
            gradientDrawable.setStroke(i, buttonStroke(i7));
            linearLayout2.setBackground(gradientDrawable);
            TextView textView = new TextView(context);
            textView.setText(AXIS_ICONS[i5]);
            textView.setTextColor(i7);
            textView.setTextSize(18.0f);
            textView.setTypeface(null, 1);
            textView.setShadowLayer(8.0f, 0.0f, 0.0f, iconShadow(i7));
            textView.setGravity(17);
            linearLayout2.addView(textView, new LinearLayout.LayoutParams(-1, -2));
            TextView textView2 = new TextView(context);
            textView2.setText(strArr[i5]);
            textView2.setTextColor(-1);
            textView2.setTextSize(11.0f);
            textView2.setTypeface(null, 1);
            textView2.setGravity(17);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
            layoutParams.topMargin = i2;
            linearLayout2.addView(textView2, layoutParams);
            TextView textView3 = new TextView(context);
            textView3.setText(AXIS_SUB[i5]);
            textView3.setTextColor(-1426063361);
            textView3.setTextSize(8.0f);
            textView3.setGravity(17);
            linearLayout2.addView(textView3, new LinearLayout.LayoutParams(-1, -2));
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, -2, 1.0f);
            if (i5 > 0) {
                layoutParams2.setMarginStart(i3);
            }
            linearLayout.addView(linearLayout2, layoutParams2);
            linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SwapBoardActionChrome$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SwapBoardActionChrome.lambda$buildAxisRow$0(host, i6, i5, view);
                }
            });
            i5++;
            c = 0;
        }
    }

    static /* synthetic */ void lambda$buildAxisRow$0(Host host, int i, int i2, View view) {
        host.animateClick(view);
        host.onAxisChosen(i, AXIS_TOASTS[i2]);
    }

    static TextView buildStopButton(final Host host, int i, int i2, int i3) {
        TextView textView = new TextView(host.context());
        textView.setText(STOP_TEXT);
        textView.setTextColor(-38037);
        textView.setTextSize(12.0f);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        textView.setPadding(0, i2, 0, i2);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(i3);
        gradientDrawable.setColor(587156843);
        gradientDrawable.setStroke(i, -1996534421);
        textView.setBackground(gradientDrawable);
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SwapBoardActionChrome$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SwapBoardActionChrome.lambda$buildStopButton$1(host, view);
            }
        });
        return textView;
    }

    static /* synthetic */ void lambda$buildStopButton$1(Host host, View view) {
        host.animateClick(view);
        host.onStopChosen();
    }

    static TextView buildTip(Context context) {
        TextView textView = new TextView(context);
        textView.setText(TIP_TEXT);
        textView.setTextColor(-1996488705);
        textView.setTextSize(9.0f);
        textView.setGravity(17);
        return textView;
    }
}
