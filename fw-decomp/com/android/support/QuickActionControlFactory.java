package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/* loaded from: classes.dex */
final class QuickActionControlFactory {
    private QuickActionControlFactory() {
    }

    static TextView text(Context context, String str, int i, int i2, int i3, int i4, int i5) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(i);
        textView.setTextSize(10.0f);
        textView.setTypeface(null, 1);
        GameHudPalette.applyTextShadow(textView);
        textView.setGravity(17);
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setPadding(i3, i4, i3, i4);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(i5);
        gradientDrawable.setColor(i2);
        textView.setBackground(gradientDrawable);
        return textView;
    }

    static void configure(TextView textView, int i, float f) {
        if (textView == null) {
            return;
        }
        textView.setMinHeight(i);
        textView.setTextSize(f);
        textView.setSingleLine(true);
        textView.setMaxLines(1);
        textView.setIncludeFontPadding(false);
        OverlayUiMotion.installPressScale(textView);
    }

    static LinearLayout splitUnit(Context context, TextView textView, TextView textView2, int i, int i2) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.addView(textView, new LinearLayout.LayoutParams(-2, i));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i2, i);
        layoutParams.setMarginStart(ScreenUtils.dp2px(context, 1.0f));
        linearLayout.addView(textView2, layoutParams);
        return linearLayout;
    }

    static LinearLayout tuningRow(Context context, String str, final int i, int i2, final int[] iArr, final String str2) {
        int iDp2px = ScreenUtils.dp2px(context, 4.0f);
        int iDp2px2 = ScreenUtils.dp2px(context, 8.0f);
        int iDp2px3 = ScreenUtils.dp2px(context, 44.0f);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        TextView textViewText = text(context, str, -6311226, 0, 0, 0, 0);
        textViewText.setTextSize(10.0f);
        textViewText.setGravity(8388627);
        linearLayout.addView(textViewText, new LinearLayout.LayoutParams(ScreenUtils.dp2px(context, 74.0f), iDp2px3));
        SeekBar seekBar = new SeekBar(context);
        int i3 = i2 - i;
        seekBar.setMax(Math.max(0, i3));
        seekBar.setProgress(Math.max(0, Math.min(i3, iArr[0] - i)));
        seekBar.setMinHeight(iDp2px3);
        seekBar.setContentDescription(str + "调节");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, iDp2px3, 1.0f);
        layoutParams.setMarginStart(iDp2px);
        layoutParams.setMarginEnd(iDp2px2);
        linearLayout.addView(seekBar, layoutParams);
        final TextView textViewText2 = text(context, iArr[0] + str2, -854017, 0, 0, 0, 0);
        textViewText2.setTextSize(10.0f);
        textViewText2.setGravity(8388629);
        linearLayout.addView(textViewText2, new LinearLayout.LayoutParams(ScreenUtils.dp2px(context, 46.0f), iDp2px3));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.QuickActionControlFactory.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i4, boolean z) {
                iArr[0] = i + i4;
                textViewText2.setText(iArr[0] + str2);
                if (z) {
                    OverlayUiMotion.pulseValue(textViewText2);
                }
            }
        });
        return linearLayout;
    }
}
