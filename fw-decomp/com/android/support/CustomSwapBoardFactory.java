package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
final class CustomSwapBoardFactory {
    static final int BATTLE_COLS = 7;
    static final int BATTLE_ROWS = 4;
    static final int BENCH_SLOTS = 9;

    interface Host {
        void bindBattlefieldCell(int i, int i2, FrameLayout frameLayout);

        void bindBenchCell(int i, FrameLayout frameLayout);

        Context context();

        void onBattlefieldCellTap(int i, int i2);
    }

    private CustomSwapBoardFactory() {
    }

    static int battlefieldCellFill(int i, int i2) {
        return (i + i2) % 2 == 0 ? 570482943 : 352365277;
    }

    static LinearLayout buildBoard(final Host host, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        Context context = host.context();
        LinearLayout linearLayout = new LinearLayout(context);
        boolean z = true;
        linearLayout.setOrientation(1);
        linearLayout.setPadding(i4, i4, i4, i4);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{855644200, 855640084});
        gradientDrawable.setCornerRadius(i5);
        gradientDrawable.setStroke(i, 1140906495);
        linearLayout.setBackground(gradientDrawable);
        TextView textView = new TextView(context);
        textView.setText("战场 4×7 · 自身");
        textView.setTextColor(GameHudPalette.CYAN);
        textView.setTextSize(9.0f);
        textView.setTypeface(null, 1);
        int i9 = -1;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = i2;
        linearLayout.addView(textView, layoutParams);
        final int i10 = 0;
        while (true) {
            float f = 1.0f;
            int i11 = 8;
            if (i10 >= 4) {
                break;
            }
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(0);
            final int i12 = 0;
            while (i12 < 7) {
                FrameLayout frameLayout = new FrameLayout(context);
                frameLayout.setClickable(z);
                frameLayout.setFocusable(z);
                GradientDrawable gradientDrawable2 = new GradientDrawable();
                gradientDrawable2.setCornerRadius(i3);
                gradientDrawable2.setColor(battlefieldCellFill(i10, i12));
                gradientDrawable2.setStroke(i, 855693823);
                frameLayout.setBackground(gradientDrawable2);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, i8, f);
                layoutParams2.setMargins(i, i, i, i);
                frameLayout.setLayoutParams(layoutParams2);
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(i9, i9);
                layoutParams3.setMargins(i2, i2, i2, i2);
                imageView.setLayoutParams(layoutParams3);
                imageView.setVisibility(i11);
                frameLayout.addView(imageView);
                TextView textView2 = new TextView(context);
                textView2.setTextSize(8.0f);
                textView2.setTextColor(i9);
                textView2.setTypeface(null, 1);
                textView2.setGravity(17);
                textView2.setVisibility(i11);
                FrameLayout.LayoutParams layoutParams4 = new FrameLayout.LayoutParams(i6, i6);
                layoutParams4.gravity = 8388661;
                layoutParams4.setMargins(0, i, i, 0);
                textView2.setLayoutParams(layoutParams4);
                frameLayout.addView(textView2);
                frameLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.CustomSwapBoardFactory$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        host.onBattlefieldCellTap(i12, i10);
                    }
                });
                host.bindBattlefieldCell(i10, i12, frameLayout);
                linearLayout2.addView(frameLayout);
                i12++;
                z = true;
                i9 = -1;
                f = 1.0f;
                i11 = 8;
            }
            linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-1, -2));
            i10++;
            z = true;
            i9 = -1;
        }
        TextView textView3 = new TextView(context);
        textView3.setText("🎒 备战 1×9");
        textView3.setTextColor(-19641);
        textView3.setTextSize(9.0f);
        textView3.setTypeface(null, 1);
        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams5.topMargin = i3;
        layoutParams5.bottomMargin = i2;
        linearLayout.addView(textView3, layoutParams5);
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(0);
        for (int i13 = 0; i13 < 9; i13++) {
            FrameLayout frameLayout2 = new FrameLayout(context);
            GradientDrawable gradientDrawable3 = new GradientDrawable();
            gradientDrawable3.setCornerRadius(i3);
            gradientDrawable3.setColor(587180544);
            gradientDrawable3.setStroke(i, 1157608263);
            frameLayout2.setBackground(gradientDrawable3);
            LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(0, i7, 1.0f);
            layoutParams6.setMargins(i, i, i, i);
            frameLayout2.setLayoutParams(layoutParams6);
            ImageView imageView2 = new ImageView(context);
            imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams layoutParams7 = new FrameLayout.LayoutParams(-1, -1);
            layoutParams7.setMargins(i, i, i, i);
            imageView2.setLayoutParams(layoutParams7);
            imageView2.setVisibility(8);
            frameLayout2.addView(imageView2);
            host.bindBenchCell(i13, frameLayout2);
            linearLayout3.addView(frameLayout2);
        }
        linearLayout.addView(linearLayout3, new LinearLayout.LayoutParams(-1, -2));
        return linearLayout;
    }
}
