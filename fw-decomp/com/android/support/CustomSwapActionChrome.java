package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.exifinterface.media.ExifInterface;

/* loaded from: classes.dex */
final class CustomSwapActionChrome {
    static final int[] COST_COLORS = {-5196098, -5708033, -10032997, -12605185, -3767297, -14273};
    static final String[] COST_LABELS = {"全", "1", ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4", "5"};
    static final String[] MIRROR_ICONS = {"⇄", "⇅", "✦"};
    static final String[] MIRROR_LABELS = {"左右", "上下", "乱换"};
    static final int[][] MIRROR_COLORS = {new int[]{-13058568, 1144569336, -2009547272}, new int[]{-22700, 1157605204, -1996511404}, new int[]{-5745161, 1151882743, -2002233865}};
    static final String[] MIRROR_TOASTS = {"开搞 · 左右调", "开搞 · 上下换", "开搞 · 瞎鸡巴乱换"};

    interface Host {
        void animateClick(View view);

        Context context();

        void onCostFilterChosen(int i);

        void onMirrorChosen(int i, String str);
    }

    static int selectedFill(int i) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | 1140850688;
    }

    static int unselectedFill(int i) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | 268435456;
    }

    static int unselectedStroke(int i) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | 1426063360;
    }

    static int unselectedText(int i) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | (-872415232);
    }

    private CustomSwapActionChrome() {
    }

    static String costFilterToast(int i) {
        if (i == 0) {
            return "费用筛选已关 · 全部棋子参与";
        }
        return i + " 费 · 左右/上下/乱换仅作用于此费";
    }

    static void applyCostFilterStyle(Context context, TextView textView, int i, boolean z) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        int iDp2px = ScreenUtils.dp2px(context, 1.0f);
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 10.0f));
        if (z) {
            gradientDrawable.setColor(selectedFill(i));
            gradientDrawable.setStroke(iDp2px * 2, i);
            textView.setTextColor(i);
            textView.setShadowLayer(6.0f, 0.0f, 0.0f, i);
        } else {
            gradientDrawable.setColor(unselectedFill(i));
            gradientDrawable.setStroke(iDp2px, unselectedStroke(i));
            textView.setTextColor(unselectedText(i));
            textView.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        }
        textView.setBackground(gradientDrawable);
    }

    static void refreshCostFilterButtons(Context context, TextView[] textViewArr, int i) {
        if (textViewArr == null) {
            return;
        }
        int i2 = 0;
        while (i2 < textViewArr.length) {
            TextView textView = textViewArr[i2];
            if (textView != null) {
                int[] iArr = COST_COLORS;
                applyCostFilterStyle(context, textView, iArr[i2 % iArr.length], i2 == i);
            }
            i2++;
        }
    }

    static LinearLayout buildCostFilterRow(final Host host, TextView[] textViewArr, int i, int i2, int i3, int i4) {
        Context context = host.context();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        final int i5 = 0;
        while (true) {
            String[] strArr = COST_LABELS;
            if (i5 >= strArr.length) {
                return linearLayout;
            }
            TextView textView = new TextView(context);
            textView.setText(strArr[i5]);
            textView.setTextSize(10.0f);
            textView.setTypeface(null, 1);
            textView.setGravity(17);
            textView.setPadding(i4, i3, i4, i3);
            textView.setClickable(true);
            textView.setFocusable(true);
            if (textViewArr != null && i5 < textViewArr.length) {
                textViewArr[i5] = textView;
            }
            applyCostFilterStyle(context, textView, COST_COLORS[i5], i5 == i);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.CustomSwapActionChrome$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CustomSwapActionChrome.lambda$buildCostFilterRow$0(host, i5, view);
                }
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
            if (i5 > 0) {
                layoutParams.setMarginStart(i2);
            }
            linearLayout.addView(textView, layoutParams);
            i5++;
        }
    }

    static /* synthetic */ void lambda$buildCostFilterRow$0(Host host, int i, View view) {
        host.animateClick(view);
        host.onCostFilterChosen(i);
    }

    static LinearLayout buildMirrorRow(final Host host, int i, int i2, int i3) {
        Context context = host.context();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        final int i4 = 0;
        while (true) {
            String[] strArr = MIRROR_LABELS;
            if (i4 >= strArr.length) {
                return linearLayout;
            }
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(0);
            linearLayout2.setGravity(17);
            linearLayout2.setPadding(i2, i2, i2, i2);
            linearLayout2.setClickable(true);
            linearLayout2.setFocusable(true);
            GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TL_BR;
            int[][] iArr = MIRROR_COLORS;
            int[] iArr2 = iArr[i4];
            GradientDrawable gradientDrawable = new GradientDrawable(orientation, new int[]{iArr2[1], iArr2[2]});
            gradientDrawable.setCornerRadius(i3);
            gradientDrawable.setStroke(i, iArr[i4][2]);
            linearLayout2.setBackground(gradientDrawable);
            TextView textView = new TextView(context);
            textView.setText(MIRROR_ICONS[i4]);
            textView.setTextColor(iArr[i4][0]);
            textView.setTextSize(13.0f);
            textView.setTypeface(null, 1);
            textView.setGravity(17);
            textView.setShadowLayer(4.0f, 0.0f, 0.0f, iArr[i4][2]);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.setMarginEnd(i2);
            linearLayout2.addView(textView, layoutParams);
            TextView textView2 = new TextView(context);
            textView2.setText(strArr[i4]);
            textView2.setTextColor(-1);
            textView2.setTextSize(10.0f);
            textView2.setTypeface(null, 1);
            textView2.setGravity(17);
            linearLayout2.addView(textView2, new LinearLayout.LayoutParams(-2, -2));
            linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.CustomSwapActionChrome$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CustomSwapActionChrome.lambda$buildMirrorRow$1(host, i4, view);
                }
            });
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, -2, 1.0f);
            if (i4 > 0) {
                layoutParams2.setMarginStart(i2);
            }
            linearLayout.addView(linearLayout2, layoutParams2);
            i4++;
        }
    }

    static /* synthetic */ void lambda$buildMirrorRow$1(Host host, int i, View view) {
        host.animateClick(view);
        host.onMirrorChosen(i, MIRROR_TOASTS[i]);
    }
}
