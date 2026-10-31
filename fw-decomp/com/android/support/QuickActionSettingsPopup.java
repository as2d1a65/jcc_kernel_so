package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import com.android.support.QuickActionOverlayPolicy;
import java.util.Objects;

/* loaded from: classes.dex */
final class QuickActionSettingsPopup {

    interface Host {
        void animateQuickSettingsPopupIn(View view);

        void configureQuickSettingsPopup(PopupWindow popupWindow, View view, int i, boolean z);

        int configuredSellCostMask();

        int configuredSellScope();

        Context context();

        TextView createQuickActionText(String str, int i, int i2, int i3, int i4, int i5);

        View createQuickActionTuningRow(String str, int i, int i2, int[] iArr, String str2);

        String describeSellCostMask(int i);

        String describeSellScope(int i);

        void dismissAllInSettingsPopup();

        void dismissAutoRefreshSettingsPopup();

        void dismissQuickActionSettingsPopup();

        QuickActionOverlayPolicy.Rect getQuickActionSafeRect();

        boolean handleVisibleQuickSettingsPopup(PopupWindow popupWindow);

        Handler mainHandler();

        void makeQuickSettingsControlTouchSafe(TextView textView);

        SharedPreferences preferences();

        int quickActionButtonMask();

        int quickActionControlHeightDp();

        int quickActionGapDp();

        int quickActionLayout();

        int quickActionOpacityPercent();

        View quickActionOverlay();

        boolean quickActionPositionLocked();

        int quickActionScalePercent();

        PopupWindow quickActionSettingsPopup();

        void rebuildQuickActionOverlay();

        void safeToast(CharSequence charSequence);

        void saveAllConfig();

        void setQuickActionSettingsPopup(PopupWindow popupWindow);

        void styleQuickActionConfigChip(TextView textView, boolean z, int i);

        boolean systemAnimationsEnabled();
    }

    private static int clampPercent(int i) {
        if (i < 0) {
            return 0;
        }
        if (i > 100) {
            return 100;
        }
        return i;
    }

    private QuickActionSettingsPopup() {
    }

    static void show(final Host host, View view) {
        int i;
        char c;
        int[] iArr;
        LinearLayout linearLayout;
        String[] strArr;
        int i2;
        int i3;
        int i4;
        int i5;
        boolean[] zArr;
        int i6;
        LinearLayout linearLayout2;
        int[] iArr2;
        if (host.handleVisibleQuickSettingsPopup(host.quickActionSettingsPopup())) {
            return;
        }
        host.dismissAutoRefreshSettingsPopup();
        host.dismissAllInSettingsPopup();
        Context context = host.context();
        int iDp2px = ScreenUtils.dp2px(context, 10.0f);
        int iDp2px2 = ScreenUtils.dp2px(context, 4.0f);
        int iDp2px3 = ScreenUtils.dp2px(context, 6.0f);
        int iDp2px4 = ScreenUtils.dp2px(context, 8.0f);
        int iDp2px5 = ScreenUtils.dp2px(context, 10.0f);
        int iDp2px6 = ScreenUtils.dp2px(context, 12.0f);
        QuickActionOverlayPolicy.Rect quickActionSafeRect = host.getQuickActionSafeRect();
        int iAdaptiveSettingsWidthDp = QuickActionOverlayPolicy.adaptiveSettingsWidthDp(Math.max(1, Math.round((quickActionSafeRect.right - quickActionSafeRect.left) / Math.max(0.1f, context.getResources().getDisplayMetrics().density))));
        int iMax = Math.max(1, ScreenUtils.dp2px(context, iAdaptiveSettingsWidthDp));
        int iAdaptiveSettingsMaxHeight = QuickActionOverlayPolicy.adaptiveSettingsMaxHeight(quickActionSafeRect.bottom - quickActionSafeRect.top, iDp2px6);
        final ScrollCuePanel scrollCuePanel = new ScrollCuePanel(context);
        scrollCuePanel.setOrientation(1);
        scrollCuePanel.setPadding(iDp2px, iDp2px, iDp2px, iDp2px);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{-233958364, -234221536});
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 12.0f));
        gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), 1433374381);
        scrollCuePanel.setBackground(gradientDrawable);
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(1);
        LinearLayout linearLayout4 = new LinearLayout(context);
        linearLayout4.setOrientation(1);
        linearLayout4.setPadding(0, iDp2px2, 0, iDp2px2);
        linearLayout4.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(false);
        scrollView.setClipToPadding(false);
        scrollView.setVerticalScrollBarEnabled(true);
        scrollView.setOverScrollMode(1);
        scrollView.addView(linearLayout4);
        ScrollEdgeCue.attach(scrollView);
        LinearLayout linearLayout5 = new LinearLayout(context);
        linearLayout5.setOrientation(1);
        linearLayout5.setPadding(0, iDp2px2, 0, 0);
        scrollCuePanel.addView(linearLayout3, new LinearLayout.LayoutParams(-1, -2));
        scrollCuePanel.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        scrollCuePanel.addView(linearLayout5, new LinearLayout.LayoutParams(-1, -2));
        TextView textViewCreateQuickActionText = host.createQuickActionText("快捷栏设置", -1, 0, 0, iDp2px2, iDp2px5);
        textViewCreateQuickActionText.setTextSize(14.0f);
        textViewCreateQuickActionText.setGravity(GravityCompat.START);
        linearLayout3.addView(textViewCreateQuickActionText);
        int i7 = iDp2px2;
        TextView textViewCreateQuickActionText2 = host.createQuickActionText("显示项会自动补位 · 保存后立即应用", -7694155, 0, 0, 0, iDp2px5);
        textViewCreateQuickActionText2.setTextSize(9.0f);
        textViewCreateQuickActionText2.setGravity(GravityCompat.START);
        linearLayout3.addView(textViewCreateQuickActionText2);
        TextView textViewCreateQuickActionText3 = host.createQuickActionText("快捷栏排版", -10133, 0, 0, iDp2px3, iDp2px5);
        textViewCreateQuickActionText3.setTextSize(10.0f);
        textViewCreateQuickActionText3.setGravity(GravityCompat.START);
        linearLayout4.addView(textViewCreateQuickActionText3);
        final int[] iArr3 = {host.quickActionLayout()};
        final int[] iArr4 = {host.quickActionControlHeightDp()};
        final int[] iArr5 = {host.quickActionScalePercent()};
        final int[] iArr6 = {host.quickActionGapDp()};
        final int[] iArr7 = {host.quickActionOpacityPercent()};
        boolean[] zArr2 = {host.quickActionPositionLocked()};
        LinearLayout linearLayout6 = new LinearLayout(context);
        linearLayout6.setOrientation(0);
        int i8 = 3;
        char c2 = 2;
        String[] strArr2 = {"自动补位", "优先横排", "双列矩阵"};
        TextView[] textViewArr = new TextView[3];
        boolean[] zArr3 = zArr2;
        final int i9 = 0;
        while (i9 < i8) {
            final TextView[] textViewArr2 = textViewArr;
            int i10 = iMax;
            int i11 = iAdaptiveSettingsMaxHeight;
            int i12 = iDp2px4;
            boolean[] zArr4 = zArr3;
            int i13 = iAdaptiveSettingsWidthDp;
            LinearLayout linearLayout7 = linearLayout6;
            TextView textViewCreateQuickActionText4 = host.createQuickActionText(strArr2[i9], -1, 0, iDp2px3, iDp2px3, iDp2px5);
            textViewCreateQuickActionText4.setTextSize(9.0f);
            host.makeQuickSettingsControlTouchSafe(textViewCreateQuickActionText4);
            textViewArr2[i9] = textViewCreateQuickActionText4;
            host.styleQuickActionConfigChip(textViewCreateQuickActionText4, iArr3[0] == i9, GameHudPalette.CYAN);
            textViewCreateQuickActionText4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    QuickActionSettingsPopup.lambda$show$0(iArr3, i9, textViewArr2, host, view2);
                }
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
            int i14 = i7;
            if (i9 > 0) {
                layoutParams.setMarginStart(i14);
            }
            linearLayout7.addView(textViewCreateQuickActionText4, layoutParams);
            i9++;
            i7 = i14;
            linearLayout6 = linearLayout7;
            textViewArr = textViewArr2;
            iAdaptiveSettingsWidthDp = i13;
            iMax = i10;
            iAdaptiveSettingsMaxHeight = i11;
            i8 = 3;
            c2 = 2;
            zArr3 = zArr4;
            iDp2px4 = i12;
        }
        int i15 = iDp2px4;
        int i16 = iMax;
        int i17 = iAdaptiveSettingsMaxHeight;
        final boolean[] zArr5 = zArr3;
        linearLayout4.addView(linearLayout6);
        int i18 = i7;
        TextView textViewCreateQuickActionText5 = host.createQuickActionText("手感与外观", -10133, 0, 0, iDp2px3, iDp2px5);
        textViewCreateQuickActionText5.setTextSize(10.0f);
        textViewCreateQuickActionText5.setGravity(GravityCompat.START);
        linearLayout4.addView(textViewCreateQuickActionText5);
        linearLayout4.addView(host.createQuickActionTuningRow("快捷栏大小", 50, 100, iArr5, "%"));
        linearLayout4.addView(host.createQuickActionTuningRow("按钮高度", 44, 54, iArr4, "dp"));
        linearLayout4.addView(host.createQuickActionTuningRow("操作间距", 4, 12, iArr6, "dp"));
        linearLayout4.addView(host.createQuickActionTuningRow("背景浓度", 72, 100, iArr7, "%"));
        SharedPreferences sharedPreferencesPreferences = host.preferences();
        final int[] iArr8 = {clampPercent(sharedPreferencesPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NX, 0) / 10000)};
        int[] iArr9 = {clampPercent(sharedPreferencesPreferences.getInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NY, 0) / 10000)};
        linearLayout4.addView(host.createQuickActionTuningRow("X坐标", 0, 100, iArr8, "%"));
        int[] iArr10 = iArr9;
        linearLayout4.addView(host.createQuickActionTuningRow("Y坐标", 0, 100, iArr9, "%"));
        int i19 = iDp2px3;
        int i20 = iDp2px5;
        final TextView textViewCreateQuickActionText6 = host.createQuickActionText("防误触锁定位置", -1, 0, i15, i19, i20);
        textViewCreateQuickActionText6.setTextSize(10.0f);
        textViewCreateQuickActionText6.setContentDescription("防误触锁定位置开关");
        host.makeQuickSettingsControlTouchSafe(textViewCreateQuickActionText6);
        host.styleQuickActionConfigChip(textViewCreateQuickActionText6, zArr5[0], GameHudPalette.CYAN);
        textViewCreateQuickActionText6.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                QuickActionSettingsPopup.lambda$show$1(zArr5, host, textViewCreateQuickActionText6, view2);
            }
        });
        linearLayout4.addView(textViewCreateQuickActionText6);
        int[] iArr11 = {1, 2, 4, 8, 64, 16, 32};
        char c3 = 2;
        int i21 = 3;
        String[] strArr3 = {"卖备战", "卖场上", "一键卖牌", "一键换位", "镜像换位", "自动刷新", "一键梭哈"};
        final int[] iArr12 = {-38037, -38037, -19641, -10133, -7607811, GameHudPalette.CYAN, -10133};
        final int[] iArr13 = {host.quickActionButtonMask()};
        LinearLayout linearLayout8 = new LinearLayout(context);
        linearLayout8.setOrientation(1);
        int i22 = QuickActionOverlayPolicy.settingsGridColumns(iAdaptiveSettingsWidthDp);
        int i23 = (i22 + 6) / i22;
        int i24 = 0;
        while (i24 < i23) {
            int i25 = i23;
            LinearLayout linearLayout9 = new LinearLayout(context);
            linearLayout9.setOrientation(0);
            int i26 = 0;
            while (true) {
                if (i26 >= i22) {
                    i = i22;
                    c = c3;
                    iArr = iArr10;
                    linearLayout = linearLayout9;
                    strArr = strArr3;
                    i2 = i20;
                    i3 = i24;
                    i4 = i21;
                    i5 = i19;
                    zArr = zArr5;
                    i6 = -2;
                    linearLayout2 = linearLayout8;
                    iArr2 = iArr11;
                    break;
                }
                LinearLayout linearLayout10 = linearLayout9;
                final int i27 = (i24 * i22) + i26;
                i = i22;
                if (i27 >= 7) {
                    c = c3;
                    iArr = iArr10;
                    linearLayout = linearLayout10;
                    strArr = strArr3;
                    i2 = i20;
                    iArr2 = iArr11;
                    i5 = i19;
                    i6 = -2;
                    i3 = i24;
                    i4 = i21;
                    zArr = zArr5;
                    linearLayout2 = linearLayout8;
                    break;
                }
                String[] strArr4 = strArr3;
                LinearLayout linearLayout11 = linearLayout8;
                int i28 = i19;
                int i29 = i26;
                char c4 = c3;
                int[] iArr14 = iArr10;
                int i30 = i24;
                int i31 = i21;
                boolean[] zArr6 = zArr5;
                TextView textViewCreateQuickActionText7 = host.createQuickActionText(strArr4[i27], -1, 0, i28, i19, i20);
                int i32 = i20;
                textViewCreateQuickActionText7.setTextSize(10.0f);
                host.makeQuickSettingsControlTouchSafe(textViewCreateQuickActionText7);
                textViewCreateQuickActionText7.setContentDescription(strArr4[i27] + "显示开关");
                host.styleQuickActionConfigChip(textViewCreateQuickActionText7, (iArr13[0] & iArr11[i27]) != 0, iArr12[i27]);
                final int[] iArr15 = iArr11;
                textViewCreateQuickActionText7.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        QuickActionSettingsPopup.lambda$show$2(iArr13, iArr15, i27, host, iArr12, view2);
                    }
                });
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, -2, 1.0f);
                if (i29 > 0) {
                    layoutParams2.setMarginStart(i18);
                }
                linearLayout10.addView(textViewCreateQuickActionText7, layoutParams2);
                i26 = i29 + 1;
                iArr11 = iArr15;
                i19 = i28;
                linearLayout8 = linearLayout11;
                linearLayout9 = linearLayout10;
                strArr3 = strArr4;
                zArr5 = zArr6;
                iArr10 = iArr14;
                i22 = i;
                c3 = c4;
                i20 = i32;
                i21 = i31;
                i24 = i30;
            }
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, i6);
            if (i3 > 0) {
                layoutParams3.topMargin = i18;
            }
            linearLayout2.addView(linearLayout, layoutParams3);
            iArr11 = iArr2;
            i19 = i5;
            linearLayout8 = linearLayout2;
            strArr3 = strArr;
            zArr5 = zArr;
            iArr10 = iArr;
            i23 = i25;
            i22 = i;
            c3 = c;
            i20 = i2;
            i21 = i4;
            i24 = i3 + 1;
        }
        int i33 = i20;
        int i34 = i21;
        char c5 = c3;
        final boolean[] zArr7 = zArr5;
        final int[] iArr16 = iArr10;
        linearLayout4.addView(linearLayout8);
        TextView textViewCreateQuickActionText8 = host.createQuickActionText("一键卖牌规则", -10133, 0, 0, i19, i33);
        int i35 = i19;
        textViewCreateQuickActionText8.setTextSize(11.0f);
        textViewCreateQuickActionText8.setGravity(GravityCompat.START);
        linearLayout4.addView(textViewCreateQuickActionText8);
        TextView textViewCreateQuickActionText9 = host.createQuickActionText("一键卖牌范围", -7694155, 0, 0, i18, i33);
        textViewCreateQuickActionText9.setTextSize(9.0f);
        textViewCreateQuickActionText9.setGravity(GravityCompat.START);
        linearLayout4.addView(textViewCreateQuickActionText9);
        final int[] iArr17 = {host.configuredSellScope()};
        LinearLayout linearLayout12 = new LinearLayout(context);
        linearLayout12.setOrientation(0);
        TextView[] textViewArr3 = new TextView[i34];
        String[] strArr5 = new String[i34];
        strArr5[0] = "备战席";
        strArr5[1] = "场上";
        strArr5[c5] = "全部";
        int i36 = 0;
        while (i36 < i34) {
            final int i37 = i36 + 1;
            final TextView[] textViewArr4 = textViewArr3;
            String[] strArr6 = strArr5;
            int i38 = i36;
            int i39 = i35;
            int i40 = i35;
            LinearLayout linearLayout13 = linearLayout12;
            TextView textViewCreateQuickActionText10 = host.createQuickActionText(strArr5[i36], -1, 0, i40, i39, i33);
            textViewCreateQuickActionText10.setTextSize(10.0f);
            host.makeQuickSettingsControlTouchSafe(textViewCreateQuickActionText10);
            textViewArr4[i38] = textViewCreateQuickActionText10;
            host.styleQuickActionConfigChip(textViewCreateQuickActionText10, iArr17[0] == i37, -19641);
            textViewCreateQuickActionText10.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    QuickActionSettingsPopup.lambda$show$3(iArr17, i37, textViewArr4, host, view2);
                }
            });
            LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(0, -2, 1.0f);
            if (i38 > 0) {
                layoutParams4.setMarginStart(i18);
            }
            linearLayout13.addView(textViewCreateQuickActionText10, layoutParams4);
            linearLayout12 = linearLayout13;
            i36 = i37;
            textViewArr3 = textViewArr4;
            strArr5 = strArr6;
            i34 = 3;
            i35 = i40;
        }
        int i41 = i35;
        linearLayout4.addView(linearLayout12);
        TextView textViewCreateQuickActionText11 = host.createQuickActionText("允许卖出的费用（可多选）", -7694155, 0, 0, i41, i33);
        textViewCreateQuickActionText11.setTextSize(9.0f);
        textViewCreateQuickActionText11.setGravity(GravityCompat.START);
        linearLayout4.addView(textViewCreateQuickActionText11);
        final int[] iArr18 = {host.configuredSellCostMask()};
        LinearLayout linearLayout14 = new LinearLayout(context);
        linearLayout14.setOrientation(0);
        int[] iArr19 = {-6511697, -11870592, -10443270, -4160260, -11930};
        int i42 = 0;
        for (int i43 = 5; i42 < i43; i43 = 5) {
            final int i44 = i42 + 1;
            int i45 = i42;
            int i46 = i18;
            final int[] iArr20 = iArr19;
            TextView textViewCreateQuickActionText12 = host.createQuickActionText(i44 + "费", -1, 0, i46, i41, i33);
            textViewCreateQuickActionText12.setTextSize(10.0f);
            host.makeQuickSettingsControlTouchSafe(textViewCreateQuickActionText12);
            host.styleQuickActionConfigChip(textViewCreateQuickActionText12, (iArr18[0] & (1 << i45)) != 0, iArr20[i45]);
            textViewCreateQuickActionText12.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    QuickActionSettingsPopup.lambda$show$4(iArr18, i44, host, iArr20, view2);
                }
            });
            LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(0, -2, 1.0f);
            if (i45 > 0) {
                layoutParams5.setMarginStart(i46);
            }
            linearLayout14.addView(textViewCreateQuickActionText12, layoutParams5);
            i18 = i46;
            i42 = i44;
            iArr19 = iArr20;
        }
        int i47 = i18;
        linearLayout4.addView(linearLayout14);
        SharedPreferences sharedPreferencesPreferences2 = host.preferences();
        final boolean[] zArr8 = {sharedPreferencesPreferences2.getBoolean(FloatingWindowPrefsKeys.KEY_SELL_ANTI_MISTOUCH, false)};
        final boolean[] zArr9 = {sharedPreferencesPreferences2.getBoolean(FloatingWindowPrefsKeys.KEY_SELL_KEEP_AUTO_PICK, true)};
        LinearLayout linearLayout15 = new LinearLayout(context);
        linearLayout15.setOrientation(0);
        LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams6.topMargin = i41;
        linearLayout15.setLayoutParams(layoutParams6);
        TextView textViewCreateQuickActionText13 = host.createQuickActionText("防误触", -1, 0, i47, i41, i33);
        textViewCreateQuickActionText13.setTextSize(10.0f);
        host.makeQuickSettingsControlTouchSafe(textViewCreateQuickActionText13);
        host.styleQuickActionConfigChip(textViewCreateQuickActionText13, zArr8[0], -11930);
        textViewCreateQuickActionText13.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                QuickActionSettingsPopup.lambda$show$5(zArr8, host, view2);
            }
        });
        TextView textViewCreateQuickActionText14 = host.createQuickActionText("保留阵容棋子", -1, 0, i47, i41, i33);
        textViewCreateQuickActionText14.setTextSize(10.0f);
        host.makeQuickSettingsControlTouchSafe(textViewCreateQuickActionText14);
        host.styleQuickActionConfigChip(textViewCreateQuickActionText14, zArr9[0], -11870592);
        textViewCreateQuickActionText14.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                QuickActionSettingsPopup.lambda$show$6(zArr9, host, view2);
            }
        });
        LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(0, -2, 1.0f);
        LinearLayout.LayoutParams layoutParams8 = new LinearLayout.LayoutParams(0, -2, 1.6f);
        layoutParams8.setMarginStart(i47);
        linearLayout15.addView(textViewCreateQuickActionText13, layoutParams7);
        linearLayout15.addView(textViewCreateQuickActionText14, layoutParams8);
        linearLayout4.addView(linearLayout15);
        TextView textViewCreateQuickActionText15 = host.createQuickActionText("开启后勾选拿牌与阵容棋盘里的棋子一律不卖，只清阵容外的牌", -9534826, 0, 0, i47, 0);
        textViewCreateQuickActionText15.setTextSize(9.0f);
        textViewCreateQuickActionText15.setGravity(GravityCompat.START);
        linearLayout4.addView(textViewCreateQuickActionText15);
        TextView textViewCreateQuickActionText16 = host.createQuickActionText("当前：" + host.describeSellScope(iArr17[0]) + " · " + host.describeSellCostMask(iArr18[0]), GameHudPalette.CYAN, 0, 0, i41, i33);
        textViewCreateQuickActionText16.setTextSize(9.0f);
        textViewCreateQuickActionText16.setGravity(GravityCompat.START);
        linearLayout4.addView(textViewCreateQuickActionText16);
        TextView textViewCreateQuickActionText17 = host.createQuickActionText("保存并立即应用", -16314081, GameHudPalette.CYAN, i15, i15, i33);
        textViewCreateQuickActionText17.setTextSize(11.0f);
        host.makeQuickSettingsControlTouchSafe(textViewCreateQuickActionText17);
        textViewCreateQuickActionText17.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                QuickActionSettingsPopup.lambda$show$7(iArr18, host, iArr13, iArr4, iArr5, iArr6, iArr7, iArr3, zArr7, iArr8, iArr16, iArr17, zArr8, zArr9, view2);
            }
        });
        LinearLayout.LayoutParams layoutParams9 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams9.topMargin = i15;
        linearLayout5.addView(textViewCreateQuickActionText17, layoutParams9);
        int iMax2 = Math.max(1, i17);
        PopupWindow popupWindow = new PopupWindow((View) scrollCuePanel, i16, iMax2, true);
        host.setQuickActionSettingsPopup(popupWindow);
        int[] iArr21 = new int[2];
        view.getLocationOnScreen(iArr21);
        int i48 = iArr21[0];
        QuickActionOverlayPolicy.PopupPlacement popupPlacementPlacePopup = QuickActionOverlayPolicy.placePopup(quickActionSafeRect, new QuickActionOverlayPolicy.Rect(i48, iArr21[1], view.getWidth() + i48, iArr21[1] + view.getHeight()), i16, iMax2, i47);
        host.configureQuickSettingsPopup(popupWindow, scrollCuePanel, 10, popupPlacementPlacePopup.below);
        popupWindow.showAtLocation(host.quickActionOverlay(), 8388659, popupPlacementPlacePopup.x, popupPlacementPlacePopup.y);
        scrollCuePanel.post(new Runnable() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                host.animateQuickSettingsPopupIn(scrollCuePanel);
            }
        });
    }

    static /* synthetic */ void lambda$show$0(int[] iArr, int i, TextView[] textViewArr, Host host, View view) {
        iArr[0] = i;
        int i2 = 0;
        while (i2 < textViewArr.length) {
            host.styleQuickActionConfigChip(textViewArr[i2], iArr[0] == i2, GameHudPalette.CYAN);
            i2++;
        }
    }

    static /* synthetic */ void lambda$show$1(boolean[] zArr, Host host, TextView textView, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        host.styleQuickActionConfigChip(textView, z, GameHudPalette.CYAN);
    }

    static /* synthetic */ void lambda$show$2(int[] iArr, int[] iArr2, int i, Host host, int[] iArr3, View view) {
        int i2 = iArr[0] ^ iArr2[i];
        if ((i2 & 127) == 0) {
            host.safeToast("至少保留一个快捷操作");
        } else {
            iArr[0] = i2;
            host.styleQuickActionConfigChip((TextView) view, (iArr2[i] & i2) != 0, iArr3[i]);
        }
    }

    static /* synthetic */ void lambda$show$3(int[] iArr, int i, TextView[] textViewArr, Host host, View view) {
        iArr[0] = i;
        int i2 = 0;
        while (i2 < textViewArr.length) {
            TextView textView = textViewArr[i2];
            i2++;
            host.styleQuickActionConfigChip(textView, iArr[0] == i2, -19641);
        }
    }

    static /* synthetic */ void lambda$show$4(int[] iArr, int i, Host host, int[] iArr2, View view) {
        int i2 = i - 1;
        int i3 = 1 << i2;
        int i4 = iArr[0] ^ i3;
        iArr[0] = i4;
        host.styleQuickActionConfigChip((TextView) view, (i4 & i3) != 0, iArr2[i2]);
    }

    static /* synthetic */ void lambda$show$5(boolean[] zArr, Host host, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        host.styleQuickActionConfigChip((TextView) view, z, -11930);
    }

    static /* synthetic */ void lambda$show$6(boolean[] zArr, Host host, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        host.styleQuickActionConfigChip((TextView) view, z, -11870592);
    }

    static /* synthetic */ void lambda$show$7(int[] iArr, final Host host, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5, int[] iArr6, int[] iArr7, boolean[] zArr, int[] iArr8, int[] iArr9, int[] iArr10, boolean[] zArr2, boolean[] zArr3, View view) {
        if (iArr[0] == 0) {
            host.safeToast("至少选择一个费用");
            return;
        }
        int iNormalizeVisibleMask = QuickActionOverlayPolicy.normalizeVisibleMask(iArr2[0], 127);
        int iNormalizeControlHeightDp = QuickActionOverlayPolicy.normalizeControlHeightDp(iArr3[0]);
        int iNormalizeDockScalePercent = QuickActionOverlayPolicy.normalizeDockScalePercent(iArr4[0]);
        int iNormalizeActionGapDp = QuickActionOverlayPolicy.normalizeActionGapDp(iArr5[0]);
        host.preferences().edit().putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_BUTTON_MASK, iNormalizeVisibleMask).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_LAYOUT, iArr7[0]).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_DENSITY, (iNormalizeControlHeightDp >= 50 || iNormalizeActionGapDp >= 10) ? 1 : 0).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_HEIGHT_DP, iNormalizeControlHeightDp).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_SCALE_PERCENT, iNormalizeDockScalePercent).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_GAP_DP, iNormalizeActionGapDp).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_OPACITY_PERCENT, QuickActionOverlayPolicy.normalizeOpacityPercent(iArr6[0])).putBoolean(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POSITION_LOCKED, zArr[0]).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NX, clampPercent(iArr8[0]) * 10000).putInt(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_POS_NY, clampPercent(iArr9[0]) * 10000).putInt(FloatingWindowPrefsKeys.KEY_SELL_SCOPE, iArr10[0]).putInt(FloatingWindowPrefsKeys.KEY_SELL_COST_MASK, iArr[0] & 31).putBoolean(FloatingWindowPrefsKeys.KEY_SELL_ANTI_MISTOUCH, zArr2[0]).putBoolean(FloatingWindowPrefsKeys.KEY_SELL_KEEP_AUTO_PICK, zArr3[0]).apply();
        host.saveAllConfig();
        host.dismissQuickActionSettingsPopup();
        Handler handlerMainHandler = host.mainHandler();
        Objects.requireNonNull(host);
        handlerMainHandler.postDelayed(new Runnable() { // from class: com.android.support.QuickActionSettingsPopup$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                host.rebuildQuickActionOverlay();
            }
        }, host.systemAnimationsEnabled() ? 130L : 0L);
        host.safeToast("快捷栏设置已应用");
    }
}
