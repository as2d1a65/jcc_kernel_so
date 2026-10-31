package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
final class CustomSwapGuideChrome {
    static final String STATUS_HINT_TAG = "statusHint";
    static final String STAT_LABEL = "已配对：";
    static final String TIP_ICON = "①";
    static final String TIP_TEXT = "点起点 ② 点目标 ③ 配多对 → ▶ 开搞";
    static final String WAITING_HINT = " · 等待棋盘数据";

    interface Host {
        void bindCountView(TextView textView);

        void bindPairList(LinearLayout linearLayout);

        void bindStatusHint(TextView textView);

        Context context();
    }

    private CustomSwapGuideChrome() {
    }

    static LinearLayout buildTipCard(Context context, int i, int i2, int i3) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(i3, i2, i3, i2);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(i3);
        gradientDrawable.setColor(576646143);
        gradientDrawable.setStroke(i, 1140906495);
        linearLayout.setBackground(gradientDrawable);
        TextView textView = new TextView(context);
        textView.setText(TIP_ICON);
        textView.setTextColor(GameHudPalette.CYAN);
        textView.setTextSize(11.0f);
        textView.setTypeface(null, 1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMarginEnd(i2);
        linearLayout.addView(textView, layoutParams);
        TextView textView2 = new TextView(context);
        textView2.setText(TIP_TEXT);
        textView2.setTextColor(-286331154);
        textView2.setTextSize(9.0f);
        linearLayout.addView(textView2, new LinearLayout.LayoutParams(0, -2, 1.0f));
        return linearLayout;
    }

    static LinearLayout buildStatRow(Host host, int i) {
        Context context = host.context();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        TextView textView = new TextView(context);
        textView.setText(STAT_LABEL);
        textView.setTextColor(-1427441921);
        textView.setTextSize(10.0f);
        linearLayout.addView(textView);
        TextView textView2 = new TextView(context);
        textView2.setText(CustomSwapPairChipFactory.countText(0));
        textView2.setTextColor(-10133);
        textView2.setTextSize(11.0f);
        textView2.setTypeface(null, 1);
        textView2.setPadding(i, 0, 0, 0);
        linearLayout.addView(textView2);
        host.bindCountView(textView2);
        TextView textView3 = new TextView(context);
        textView3.setText(WAITING_HINT);
        textView3.setTextColor(-1996488705);
        textView3.setTextSize(9.0f);
        textView3.setSingleLine(true);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        linearLayout.addView(textView3, new LinearLayout.LayoutParams(0, -2, 1.0f));
        textView3.setTag(STATUS_HINT_TAG);
        host.bindStatusHint(textView3);
        return linearLayout;
    }

    static HorizontalScrollView buildPairScroll(Host host, int i) {
        Context context = host.context();
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setPadding(0, i, 0, 0);
        horizontalScrollView.addView(linearLayout, new ViewGroup.LayoutParams(-2, -2));
        host.bindPairList(linearLayout);
        ScrollEdgeCue.attach(horizontalScrollView);
        return horizontalScrollView;
    }
}
