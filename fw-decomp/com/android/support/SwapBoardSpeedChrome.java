package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.view.GravityCompat;

/* loaded from: classes.dex */
final class SwapBoardSpeedChrome {
    static final int CARD_FILL = 855640084;
    static final int CARD_STROKE = 1140906495;
    static final String LABEL = "快慢调节";
    static final int SEEK_MAX = 12;
    static final String TICK_FAST = "飞快";
    static final int TICK_FAST_COLOR = -2013200504;
    static final String TICK_STEADY = "稳";
    static final int TICK_STEADY_COLOR = -1996510720;
    static final int VALUE_COLOR = -10133;

    interface Host {
        void applyFramesToNative(int i);

        void bindSpeedBar(SeekBar seekBar);

        Context context();

        void persistFrames(int i);
    }

    private SwapBoardSpeedChrome() {
    }

    static LinearLayout buildCard(final Host host, int i, int i2, int i3, int i4, int i5) {
        Context context = host.context();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(i5, i4, i5, i4);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(i5);
        gradientDrawable.setColor(CARD_FILL);
        gradientDrawable.setStroke(i2, CARD_STROKE);
        linearLayout.setBackground(gradientDrawable);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        TextView textView = new TextView(context);
        textView.setText(LABEL);
        textView.setTextColor(GameHudPalette.CYAN);
        textView.setTextSize(11.0f);
        textView.setTypeface(null, 1);
        linearLayout2.addView(textView, new LinearLayout.LayoutParams(0, -2, 1.0f));
        final TextView textView2 = new TextView(context);
        textView2.setTextColor(VALUE_COLOR);
        textView2.setTextSize(10.0f);
        textView2.setTypeface(null, 1);
        linearLayout2.addView(textView2);
        linearLayout.addView(linearLayout2);
        final SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(12);
        seekBar.setProgress(ChessSwapSpeedPolicy.progressFromFrames(i));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = i3;
        linearLayout.addView(seekBar, layoutParams);
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(0);
        TextView textView3 = new TextView(context);
        textView3.setText(TICK_FAST);
        textView3.setTextColor(TICK_FAST_COLOR);
        textView3.setTextSize(9.0f);
        TextView textView4 = new TextView(context);
        textView4.setText("");
        TextView textView5 = new TextView(context);
        textView5.setText(TICK_STEADY);
        textView5.setTextColor(TICK_STEADY_COLOR);
        textView5.setTextSize(9.0f);
        textView5.setGravity(GravityCompat.END);
        linearLayout3.addView(textView3, new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout3.addView(textView4, new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout3.addView(textView5, new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout.addView(linearLayout3);
        final Runnable runnable = new Runnable() { // from class: com.android.support.SwapBoardSpeedChrome$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                textView2.setText(ChessSwapSpeedPolicy.valueLabel(ChessSwapSpeedPolicy.framesFromProgress(seekBar.getProgress())));
            }
        };
        runnable.run();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.SwapBoardSpeedChrome.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i6, boolean z) {
                runnable.run();
                if (z) {
                    host.persistFrames(ChessSwapSpeedPolicy.framesFromProgress(i6));
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
                host.applyFramesToNative(ChessSwapSpeedPolicy.framesFromProgress(seekBar2.getProgress()));
            }
        });
        host.bindSpeedBar(seekBar);
        return linearLayout;
    }
}
