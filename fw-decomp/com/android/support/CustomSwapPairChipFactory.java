package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import java.util.List;

/* loaded from: classes.dex */
final class CustomSwapPairChipFactory {

    interface Host {
        Context context();

        void onChipRemove(int i);
    }

    static int fill(int i) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | 570425344;
    }

    static int stroke(int i) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | (-2013265920);
    }

    private CustomSwapPairChipFactory() {
    }

    static String countText(int i) {
        return i + " 对";
    }

    static void addChips(LinearLayout linearLayout, final Host host, List<int[]> list, int[] iArr, int i, int i2, int i3, int i4) {
        if (linearLayout == null || host == null || list == null || iArr == null || iArr.length == 0) {
            return;
        }
        Context context = host.context();
        for (final int i5 = 0; i5 < list.size(); i5++) {
            int[] iArr2 = list.get(i5);
            if (CustomSwapPairingPolicy.isValidPair(iArr2)) {
                int i6 = iArr[i5 % iArr.length];
                TextView textView = new TextView(context);
                textView.setText(CustomSwapPairingPolicy.chipLabel(i5, iArr2));
                textView.setTextColor(i6);
                textView.setTextSize(10.0f);
                textView.setTypeface(null, 1);
                int i7 = i2 / 2;
                textView.setPadding(i3, i7, i3, i7);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setCornerRadius(i4);
                gradientDrawable.setColor(fill(i6));
                gradientDrawable.setStroke(i, stroke(i6));
                textView.setBackground(gradientDrawable);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                layoutParams.setMarginEnd(i2);
                textView.setLayoutParams(layoutParams);
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.CustomSwapPairChipFactory$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        host.onChipRemove(i5);
                    }
                });
                linearLayout.addView(textView);
            }
        }
    }
}
