package com.android.support;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/* loaded from: classes.dex */
final class OverlayScrollDetailList {
    private OverlayScrollDetailList() {
    }

    static LinearLayout content(View view) {
        return (LinearLayout) ((ScrollView) view).getChildAt(0);
    }

    static LinearLayout detail(LinearLayout linearLayout) {
        if (linearLayout != null && linearLayout.getChildCount() > 1) {
            View childAt = linearLayout.getChildAt(1);
            if (childAt instanceof LinearLayout) {
                return (LinearLayout) childAt;
            }
        }
        return null;
    }

    static LinearLayout detailFromWindow(View view) {
        if (!(view instanceof ScrollView)) {
            return null;
        }
        View childAt = ((ScrollView) view).getChildAt(0);
        if (childAt instanceof LinearLayout) {
            return detail((LinearLayout) childAt);
        }
        return null;
    }
}
