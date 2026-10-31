package com.android.support;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/* loaded from: classes.dex */
final class CustomSwapCellViews {
    private CustomSwapCellViews() {
    }

    static ImageView avatar(FrameLayout frameLayout) {
        if (frameLayout != null && frameLayout.getChildCount() >= 1) {
            View childAt = frameLayout.getChildAt(0);
            if (childAt instanceof ImageView) {
                return (ImageView) childAt;
            }
        }
        return null;
    }

    static TextView badge(FrameLayout frameLayout) {
        if (frameLayout != null && frameLayout.getChildCount() >= 2) {
            View childAt = frameLayout.getChildAt(1);
            if (childAt instanceof TextView) {
                return (TextView) childAt;
            }
        }
        return null;
    }
}
