package com.android.support;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.support.CustomSwapPairChipFactory;
import java.util.List;

/* loaded from: classes.dex */
final class CustomSwapStatRowRefresh {
    static boolean canRemoveChip(int i, int i2) {
        return i >= 0 && i < i2;
    }

    private CustomSwapStatRowRefresh() {
    }

    static void apply(Context context, TextView textView, LinearLayout linearLayout, CustomSwapPairChipFactory.Host host, List<int[]> list, int[] iArr) {
        if (textView != null) {
            textView.setText(CustomSwapPairChipFactory.countText(list.size()));
        }
        if (linearLayout == null) {
            return;
        }
        int iDp2px = ScreenUtils.dp2px(context, 1.0f);
        int iDp2px2 = ScreenUtils.dp2px(context, 6.0f);
        int iDp2px3 = ScreenUtils.dp2px(context, 8.0f);
        int iDp2px4 = ScreenUtils.dp2px(context, 10.0f);
        linearLayout.removeAllViews();
        CustomSwapPairChipFactory.addChips(linearLayout, host, list, iArr, iDp2px, iDp2px2, iDp2px3, iDp2px4);
    }
}
