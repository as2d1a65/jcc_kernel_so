package com.android.support;

import android.graphics.drawable.GradientDrawable;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.support.CustomSwapPairingPolicy;
import java.util.List;

/* loaded from: classes.dex */
final class CustomSwapCellChrome {
    private CustomSwapCellChrome() {
    }

    static void applyAll(FrameLayout[][] frameLayoutArr, List<int[]> list, int i, int i2, int[] iArr, int i3, int i4, int i5) {
        if (frameLayoutArr == null) {
            return;
        }
        for (int i6 = 0; i6 < 4; i6++) {
            for (int i7 = 0; i7 < 7; i7++) {
                applyCell(frameLayoutArr[i6][i7], list, i7, i6, i, i2, iArr, i3, i4, i5);
            }
        }
    }

    static void applyCell(FrameLayout frameLayout, List<int[]> list, int i, int i2, int i3, int i4, int[] iArr, int i5, int i6, int i7) {
        if (frameLayout == null) {
            return;
        }
        CustomSwapPairingPolicy.Badge badgeBadgeAt = CustomSwapPairingPolicy.badgeAt(list, i, i2);
        int i8 = badgeBadgeAt == null ? -1 : badgeBadgeAt.pairIndex;
        int iPairColor = CustomSwapCellVisualPolicy.pairColor(iArr, i8);
        boolean z = i3 == i && i4 == i2;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(i7);
        gradientDrawable.setColor(CustomSwapCellVisualPolicy.fillColor(i2, i, z, iPairColor));
        if (CustomSwapCellVisualPolicy.thickStroke(z, i8 >= 0)) {
            i5 = i6;
        }
        gradientDrawable.setStroke(i5, CustomSwapCellVisualPolicy.strokeColor(z, iPairColor));
        frameLayout.setBackground(gradientDrawable);
        TextView textViewBadge = CustomSwapCellViews.badge(frameLayout);
        if (textViewBadge == null) {
            return;
        }
        if (badgeBadgeAt != null) {
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            gradientDrawable2.setShape(1);
            gradientDrawable2.setColor(iPairColor);
            textViewBadge.setBackground(gradientDrawable2);
            textViewBadge.setText(CustomSwapPairingPolicy.badgeText(badgeBadgeAt));
            textViewBadge.setVisibility(0);
            return;
        }
        textViewBadge.setVisibility(8);
    }
}
