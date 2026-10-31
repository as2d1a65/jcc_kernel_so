package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
final class CustomSwapFooterChrome {
    static final String CLEAR_TEXT = "✕ 清空配对";
    static final String CLEAR_TOAST = "已清空配对";
    static final String EMPTY_GO_TOAST = "还没配对呢，先点棋子选一对";
    static final String FOOT_TIP = "再点已配对格可取消 · 选费后镜像仅换该费 · 速度=2 帧默认";
    static final String GO_TEXT = "▶ 开搞";
    static final String REFRESH_TEXT = "🔄 刷新棋盘";
    static final String REFRESH_TOAST = "棋盘已刷新";
    static final String STOP_TEXT = "■ 停一下";
    static final String STOP_TOAST = "已停止当前换位/卖牌任务";

    interface Host {
        void animateClick(View view);

        Context context();

        void onClear();

        void onGo();

        void onRefresh();

        void onStop();
    }

    private CustomSwapFooterChrome() {
    }

    static String goSuccessToast(int i) {
        return "开搞 · 自定义换位 " + i + " 对";
    }

    static String submitFailToast(String str) {
        return "提交失败：" + str;
    }

    static TextView capsule(Context context, String str, int i, int i2, int i3, int i4, int i5, int i6) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(i);
        textView.setTextSize(12.0f);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        textView.setPadding(0, i6, 0, i6);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(i5);
        gradientDrawable.setColor(i2);
        gradientDrawable.setStroke(i4, i3);
        textView.setBackground(gradientDrawable);
        textView.setClickable(true);
        textView.setFocusable(true);
        return textView;
    }

    static TextView buildStop(final Host host, int i, int i2, int i3) {
        TextView textViewCapsule = capsule(host.context(), STOP_TEXT, -24673, 587156843, 1728007531, i, i2, i3);
        textViewCapsule.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.CustomSwapFooterChrome$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CustomSwapFooterChrome.lambda$buildStop$0(host, view);
            }
        });
        return textViewCapsule;
    }

    static /* synthetic */ void lambda$buildStop$0(Host host, View view) {
        host.animateClick(view);
        host.onStop();
    }

    static LinearLayout buildActionRow(final Host host, int i, int i2, int i3, int i4) {
        Context context = host.context();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        TextView textViewCapsule = capsule(context, REFRESH_TEXT, GameHudPalette.CYAN, 576646143, 1711331839, i, i4, i3);
        TextView textViewCapsule2 = capsule(context, CLEAR_TEXT, -24673, 587156843, -1996534421, i, i4, i3);
        TextView textViewCapsule3 = capsule(context, GO_TEXT, -10133, 872393216, -855660032, i, i4, i3);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams2.setMarginStart(i2);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(0, -2, 1.3f);
        layoutParams3.setMarginStart(i2);
        linearLayout.addView(textViewCapsule, layoutParams);
        linearLayout.addView(textViewCapsule2, layoutParams2);
        linearLayout.addView(textViewCapsule3, layoutParams3);
        textViewCapsule.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.CustomSwapFooterChrome$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CustomSwapFooterChrome.lambda$buildActionRow$1(host, view);
            }
        });
        textViewCapsule2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.CustomSwapFooterChrome$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CustomSwapFooterChrome.lambda$buildActionRow$2(host, view);
            }
        });
        textViewCapsule3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.CustomSwapFooterChrome$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CustomSwapFooterChrome.lambda$buildActionRow$3(host, view);
            }
        });
        return linearLayout;
    }

    static /* synthetic */ void lambda$buildActionRow$1(Host host, View view) {
        host.animateClick(view);
        host.onRefresh();
    }

    static /* synthetic */ void lambda$buildActionRow$2(Host host, View view) {
        host.animateClick(view);
        host.onClear();
    }

    static /* synthetic */ void lambda$buildActionRow$3(Host host, View view) {
        host.animateClick(view);
        host.onGo();
    }

    static TextView buildFootTip(Context context) {
        TextView textView = new TextView(context);
        textView.setText(FOOT_TIP);
        textView.setTextColor(-1996488705);
        textView.setTextSize(8.0f);
        textView.setGravity(17);
        return textView;
    }
}
