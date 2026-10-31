package com.android.support;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/* loaded from: classes.dex */
final class VerifyOverlayShell {

    interface Host {
        int dpVerify(int i);

        void hideVerifyOverlay();

        LinearLayout verifyStep(String str, String str2, int i);
    }

    static final class Built {
        final LinearLayout actionArea;
        final LinearLayout announcementArea;
        final View connLed;
        final TextView connText;
        final LinearLayout contentLayout;
        final ScrollView contentScroll;
        final View divider1;
        final LinearLayout footerArea;
        final LinearLayout reasonArea;
        final LinearLayout rootLayout;
        final LinearLayout titleBar;

        Built(LinearLayout linearLayout, LinearLayout linearLayout2, View view, TextView textView, View view2, ScrollView scrollView, LinearLayout linearLayout3, LinearLayout linearLayout4, LinearLayout linearLayout5, LinearLayout linearLayout6, LinearLayout linearLayout7) {
            this.rootLayout = linearLayout;
            this.titleBar = linearLayout2;
            this.connLed = view;
            this.connText = textView;
            this.divider1 = view2;
            this.contentScroll = scrollView;
            this.contentLayout = linearLayout3;
            this.footerArea = linearLayout4;
            this.actionArea = linearLayout5;
            this.announcementArea = linearLayout6;
            this.reasonArea = linearLayout7;
        }
    }

    private VerifyOverlayShell() {
    }

    static Built build(Context context, final Host host, VerifyWindowSpec verifyWindowSpec, String str) {
        VerifyResponsiveLayout verifyResponsiveLayout = new VerifyResponsiveLayout(context);
        verifyResponsiveLayout.setOrientation(1);
        verifyResponsiveLayout.setBackground(DsTokens.shape(context, -657673, DsTokens.LINE, 20));
        int iDpVerify = host.dpVerify(verifyWindowSpec.compact ? 16 : 24);
        verifyResponsiveLayout.setPadding(iDpVerify, host.dpVerify(12), iDpVerify, host.dpVerify(16));
        verifyResponsiveLayout.setElevation(host.dpVerify(16));
        OverlayUiMotion.enableRoundedClip(verifyResponsiveLayout);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(16);
        linearLayout.setPadding(0, 0, 0, host.dpVerify(10));
        verifyResponsiveLayout.addView(linearLayout, new LinearLayout.LayoutParams(-1, -2));
        TextView textViewText = text(context, "XF", 17.0f, DsTokens.BRAND);
        textViewText.setTypeface(null, 1);
        textViewText.setGravity(17);
        textViewText.setBackground(DsTokens.shape(context, DsTokens.BRAND_SOFT, 0, 12));
        linearLayout.addView(textViewText, new LinearLayout.LayoutParams(host.dpVerify(44), host.dpVerify(44)));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.setMarginStart(host.dpVerify(12));
        linearLayout.addView(linearLayout2, layoutParams);
        TextView textViewText2 = text(context, "网络验证", 21.0f, DsTokens.TEXT_1);
        textViewText2.setTypeface(null, 1);
        linearLayout2.addView(textViewText2);
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setGravity(16);
        linearLayout2.addView(linearLayout3);
        View view = new View(context);
        view.setBackground(DsTokens.led(context, DsTokens.WARN));
        linearLayout3.addView(view, new LinearLayout.LayoutParams(host.dpVerify(6), host.dpVerify(6)));
        TextView textViewText3 = text(context, "正在连接授权服务…", 12.0f, DsTokens.TEXT_2);
        textViewText3.setPadding(host.dpVerify(6), host.dpVerify(3), 0, 0);
        textViewText3.setMaxLines(2);
        textViewText3.setAccessibilityLiveRegion(1);
        linearLayout3.addView(textViewText3, new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout.addView(text(context, WigVerify.versionLabel(str), 11.0f, DsTokens.TEXT_3));
        TextView textViewText4 = text(context, "×", 26.0f, DsTokens.TEXT_2);
        textViewText4.setGravity(17);
        textViewText4.setContentDescription("关闭使用验证");
        textViewText4.setBackground(DsTokens.secondaryButton(context, 12));
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(host.dpVerify(48), host.dpVerify(48));
        layoutParams2.setMarginStart(host.dpVerify(10));
        linearLayout.addView(textViewText4, layoutParams2);
        textViewText4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyOverlayShell$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                host.hideVerifyOverlay();
            }
        });
        DsTokens.pressable(textViewText4);
        View view2 = new View(context);
        view2.setBackgroundColor(DsTokens.LINE);
        verifyResponsiveLayout.addView(view2, new LinearLayout.LayoutParams(-1, host.dpVerify(1)));
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.setVerticalScrollBarEnabled(true);
        scrollView.setClipToPadding(false);
        scrollView.setPadding(0, host.dpVerify(16), 0, host.dpVerify(8));
        ScrollEdgeCue.attach(scrollView);
        verifyResponsiveLayout.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        LinearLayout linearLayout4 = new LinearLayout(context);
        scrollView.addView(linearLayout4, new FrameLayout.LayoutParams(-1, -2));
        LinearLayout linearLayoutColumn = column(context);
        LinearLayout linearLayoutColumn2 = column(context);
        LinearLayout linearLayoutColumn3 = column(context);
        linearLayoutColumn3.setPadding(0, host.dpVerify(12), 0, 0);
        verifyResponsiveLayout.addView(linearLayoutColumn3, new LinearLayout.LayoutParams(-1, -2));
        verifyResponsiveLayout.bind(linearLayout4, linearLayoutColumn, linearLayoutColumn2, linearLayoutColumn3);
        LinearLayout linearLayoutColumn4 = column(context);
        linearLayoutColumn4.setVisibility(8);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams3.bottomMargin = host.dpVerify(12);
        linearLayoutColumn.addView(linearLayoutColumn4, layoutParams3);
        LinearLayout linearLayoutColumn5 = column(context);
        linearLayoutColumn.addView(linearLayoutColumn5, new LinearLayout.LayoutParams(-1, -2));
        return new Built(verifyResponsiveLayout, linearLayout, view, textViewText3, view2, scrollView, linearLayoutColumn, linearLayoutColumn2, linearLayoutColumn3, linearLayoutColumn5, linearLayoutColumn4);
    }

    private static LinearLayout column(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        return linearLayout;
    }

    private static TextView text(Context context, String str, float f, int i) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextSize(f);
        textView.setTextColor(i);
        return textView;
    }
}
