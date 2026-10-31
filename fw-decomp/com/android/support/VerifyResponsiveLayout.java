package com.android.support;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.view.GravityCompat;

/* loaded from: classes.dex */
final class VerifyResponsiveLayout extends LinearLayout {
    private LinearLayout actions;
    private boolean arranged;
    private LinearLayout columns;
    private LinearLayout form;
    private LinearLayout information;
    private boolean wide;

    VerifyResponsiveLayout(Context context) {
        super(context);
    }

    void bind(LinearLayout linearLayout, LinearLayout linearLayout2, LinearLayout linearLayout3, LinearLayout linearLayout4) {
        this.columns = linearLayout;
        this.information = linearLayout2;
        this.form = linearLayout3;
        this.actions = linearLayout4;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.columns != null) {
            int iMax = Math.max(0, (View.MeasureSpec.getSize(i) - getPaddingLeft()) - getPaddingRight());
            float f = getResources().getDisplayMetrics().density;
            boolean zUseColumns = VerifyWindowSpec.useColumns(iMax, f, getResources().getConfiguration().fontScale);
            if (!this.arranged || this.wide != zUseColumns) {
                View viewFindFocus = findFocus();
                this.wide = zUseColumns;
                this.arranged = true;
                this.columns.removeAllViews();
                this.columns.setOrientation(1 ^ (this.wide ? 1 : 0));
                this.columns.setBaselineAligned(false);
                int iRound = Math.round(f * 20.0f);
                if (this.wide) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
                    layoutParams.setMarginEnd(iRound);
                    this.columns.addView(this.information, layoutParams);
                    this.columns.addView(this.form, new LinearLayout.LayoutParams(0, -2, 1.15f));
                } else {
                    this.columns.addView(this.form, new LinearLayout.LayoutParams(-1, -2));
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
                    layoutParams2.topMargin = iRound;
                    this.columns.addView(this.information, layoutParams2);
                }
                if (viewFindFocus != null) {
                    viewFindFocus.requestFocus();
                }
            }
            View viewFindViewWithTag = this.form.findViewWithTag("verify_optional_intro");
            if (viewFindViewWithTag != null) {
                viewFindViewWithTag.setVisibility((((float) View.MeasureSpec.getSize(i2)) / f) / Math.max(1.0f, getResources().getConfiguration().fontScale) < 430.0f ? 8 : 0);
            }
            LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.actions.getLayoutParams();
            layoutParams3.width = this.wide ? Math.round(((iMax - (f * 20.0f)) * 1.15f) / 2.15f) : -1;
            layoutParams3.gravity = GravityCompat.END;
        }
        super.onMeasure(i, i2);
    }
}
