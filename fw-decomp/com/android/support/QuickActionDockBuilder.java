package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.support.QuickActionOverlayPolicy;
import java.util.ArrayList;
import java.util.Objects;

/* loaded from: classes.dex */
final class QuickActionDockBuilder {

    interface Host {
        void animateQuickActionConfirm(View view);

        void applyQuickActionPosition(QuickActionOverlayPolicy.Point point, boolean z);

        boolean autoRefreshEnabled();

        boolean canShowVerifiedFeatures();

        void configureQuickActionControl(TextView textView, int i, float f);

        Context context();

        LinearLayout createQuickActionSplitUnit(TextView textView, TextView textView2, int i, int i2);

        TextView createQuickActionText(String str, int i, int i2, int i3, int i4, int i5);

        void ensureQuickActionAdaptiveDefaultV3();

        void ensureQuickActionCompactAdaptiveV2();

        QuickActionOverlayPolicy.Rect getQuickActionDragRect();

        QuickActionOverlayPolicy.Rect getQuickActionSafeRect();

        boolean isQuickActionAdded();

        boolean isQuickActionButtonEnabled(int i);

        boolean isQuickActionVisibleByPreference();

        void logDockAdded(boolean z);

        int measuredQuickActionHeight();

        int measuredQuickActionWidth();

        WindowManager.LayoutParams paramsQuickAction();

        int quickActionButtonMask();

        int quickActionControlHeightDp();

        int quickActionGapDp();

        int quickActionLayout();

        int quickActionOpacityPercent();

        View quickActionOverlay();

        boolean quickActionPositionLocked();

        int quickActionScalePercent();

        void restoreAndClampQuickActionPosition();

        void safeToast(CharSequence charSequence);

        void saveAllConfig();

        void setBtnAllInFloatRef(TextView textView);

        void setBtnAutoRefreshFloatRef(TextView textView);

        void setBtnSellAllFloatRef(TextView textView);

        void setParamsQuickAction(WindowManager.LayoutParams layoutParams);

        void setQuickActionDragHandle(TextView textView);

        void setQuickActionOverlay(View view);

        void setQuickActionVisible(boolean z);

        boolean shmReady();

        void showAllInSettingsPopup(View view);

        void showAutoRefreshSettingsPopup(View view);

        void showQuickActionSettingsPopup(View view);

        void syncQuickActionOverlayVisibility();

        void toggleAllInFromQuickAction(View view);

        void toggleAutoRefreshFromQuickAction(View view);

        void toggleSwapBoardWindow();

        void triggerConfiguredSell(View view, int i, String str);

        void triggerMirrorFromQuickAction();
    }

    private QuickActionDockBuilder() {
    }

    static void create(final Host host) {
        int i;
        int i2;
        int i3;
        int i4;
        host.ensureQuickActionCompactAdaptiveV2();
        host.ensureQuickActionAdaptiveDefaultV3();
        Context context = host.context();
        int iDp2px = ScreenUtils.dp2px(context, 1.0f);
        int iDp2px2 = ScreenUtils.dp2px(context, 4.0f);
        int iDp2px3 = ScreenUtils.dp2px(context, 6.0f);
        QuickActionOverlayPolicy.Rect quickActionSafeRect = host.getQuickActionSafeRect();
        int i5 = quickActionSafeRect.right - quickActionSafeRect.left;
        QuickActionOverlayPolicy.DockMetrics dockMetricsScaleDockMetrics = QuickActionOverlayPolicy.scaleDockMetrics(QuickActionOverlayPolicy.compactDockMetrics(Math.max(1, Math.round(i5 / Math.max(0.1f, context.getResources().getDisplayMetrics().density)))), host.quickActionScalePercent());
        int iDp2px4 = ScreenUtils.dp2px(context, dockMetricsScaleDockMetrics.handleWidthDp);
        int iDp2px5 = ScreenUtils.dp2px(context, dockMetricsScaleDockMetrics.arrowWidthDp);
        int iDp2px6 = ScreenUtils.dp2px(context, dockMetricsScaleDockMetrics.actionPaddingDp);
        int iDp2px7 = ScreenUtils.dp2px(context, dockMetricsScaleDockMetrics.dividerHeightDp);
        int iDp2px8 = ScreenUtils.dp2px(context, dockMetricsScaleDockMetrics.shellPaddingDp);
        int iDp2px9 = ScreenUtils.dp2px(context, dockMetricsScaleDockMetrics.cornerRadiusDp);
        int iDp2px10 = ScreenUtils.dp2px(context, dockMetricsScaleDockMetrics.minActionWidthDp);
        float f = dockMetricsScaleDockMetrics.textSizeSp;
        final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        int iDp2px11 = ScreenUtils.dp2px(context, QuickActionOverlayPolicy.scaleControlHeightDp(host.quickActionControlHeightDp(), host.quickActionScalePercent()));
        int iDp2px12 = ScreenUtils.dp2px(context, host.quickActionGapDp());
        int i6 = (iDp2px4 * 2) + iDp2px;
        int i7 = iDp2px8 * 2;
        int iSlimRowColumns = QuickActionOverlayPolicy.slimRowColumns(host.quickActionLayout(), Integer.bitCount(host.quickActionButtonMask()), i5, i6 + iDp2px12 + iDp2px + iDp2px2 + i7, iDp2px10, iDp2px12);
        int iMax = (iDp2px10 * iSlimRowColumns) + (Math.max(0, iSlimRowColumns - 1) * (iDp2px + (iDp2px12 * 2)));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, dockMetricsScaleDockMetrics.cornerRadiusDp + 2));
        gradientDrawable.setColor((Math.round((host.quickActionOpacityPercent() * 255.0f) / 100.0f) << 24) | 659230);
        gradientDrawable.setStroke(iDp2px, 1030721197);
        linearLayout.setBackground(gradientDrawable);
        linearLayout.setPadding(iDp2px8, iDp2px8, iDp2px8, iDp2px8);
        linearLayout.setElevation(iDp2px3);
        OverlayUiMotion.enableRoundedClip(linearLayout);
        OverlayUiMotion.prepareCompanionEnter(linearLayout, 1);
        int i8 = iDp2px12;
        TextView textViewCreateQuickActionText = host.createQuickActionText("⠿", GameHudPalette.CYAN, 576646143, 0, 0, iDp2px9);
        host.setQuickActionDragHandle(textViewCreateQuickActionText);
        textViewCreateQuickActionText.setTextSize(dockMetricsScaleDockMetrics.handleTextSizeSp);
        textViewCreateQuickActionText.setMinWidth(iDp2px4);
        textViewCreateQuickActionText.setMinHeight(iDp2px11);
        textViewCreateQuickActionText.setContentDescription("拖动快捷栏");
        textViewCreateQuickActionText.setFocusable(true);
        OverlayUiMotion.installPressScale(textViewCreateQuickActionText);
        textViewCreateQuickActionText.setOnHoverListener(new View.OnHoverListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda0
            @Override // android.view.View.OnHoverListener
            public final boolean onHover(View view, MotionEvent motionEvent) {
                return QuickActionDockBuilder.lambda$create$0(view, motionEvent);
            }
        });
        textViewCreateQuickActionText.setAlpha(0.82f);
        textViewCreateQuickActionText.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.support.QuickActionDockBuilder.1
            private boolean dragging;
            private float startRawX;
            private float startRawY;
            private int startX;
            private int startY;

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                WindowManager.LayoutParams layoutParamsParamsQuickAction = host.paramsQuickAction();
                if (layoutParamsParamsQuickAction == null || host.quickActionOverlay() == null) {
                    return false;
                }
                if (host.quickActionPositionLocked()) {
                    if (motionEvent.getActionMasked() == 0) {
                        host.safeToast("位置已锁定，请在快捷栏设置中关闭锁定");
                    }
                    return true;
                }
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0) {
                    this.startRawX = motionEvent.getRawX();
                    this.startRawY = motionEvent.getRawY();
                    this.startX = layoutParamsParamsQuickAction.x;
                    this.startY = layoutParamsParamsQuickAction.y;
                    this.dragging = false;
                    view.setPressed(true);
                    return true;
                }
                if (actionMasked != 1) {
                    if (actionMasked == 2) {
                        float rawX = motionEvent.getRawX() - this.startRawX;
                        float rawY = motionEvent.getRawY() - this.startRawY;
                        if (!this.dragging && Math.hypot(rawX, rawY) >= scaledTouchSlop) {
                            this.dragging = true;
                        }
                        if (!this.dragging) {
                            return true;
                        }
                        host.applyQuickActionPosition(QuickActionOverlayPolicy.clamp(host.getQuickActionDragRect(), this.startX + Math.round(rawX), this.startY + Math.round(rawY), host.measuredQuickActionWidth(), host.measuredQuickActionHeight()), false);
                        return true;
                    }
                    if (actionMasked != 3) {
                        return false;
                    }
                }
                view.setPressed(false);
                if (this.dragging) {
                    WindowManager.LayoutParams layoutParamsParamsQuickAction2 = host.paramsQuickAction();
                    host.applyQuickActionPosition(QuickActionOverlayPolicy.finishDrag(host.getQuickActionDragRect(), layoutParamsParamsQuickAction2.x, layoutParamsParamsQuickAction2.y, host.measuredQuickActionWidth(), host.measuredQuickActionHeight()), true);
                    host.saveAllConfig();
                } else if (motionEvent.getActionMasked() == 1) {
                    view.performClick();
                }
                this.dragging = false;
                return true;
            }
        });
        TextView textViewCreateQuickActionText2 = host.createQuickActionText("⚙", GameHudPalette.CYAN, 576646143, 0, 0, iDp2px9);
        textViewCreateQuickActionText2.setTextSize(dockMetricsScaleDockMetrics.handleTextSizeSp);
        textViewCreateQuickActionText2.setMinWidth(iDp2px4);
        textViewCreateQuickActionText2.setMinHeight(iDp2px11);
        textViewCreateQuickActionText2.setContentDescription("快捷栏设置");
        Objects.requireNonNull(host);
        textViewCreateQuickActionText2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.showQuickActionSettingsPopup(view);
            }
        });
        OverlayUiMotion.installPressScale(textViewCreateQuickActionText2);
        TextView textViewCreateQuickActionText3 = host.createQuickActionText("卖备战", -25940, 268435456, iDp2px6, 0, iDp2px9);
        int i9 = iDp2px7;
        TextView textViewCreateQuickActionText4 = host.createQuickActionText("卖场上", -25940, 268435456, iDp2px6, 0, iDp2px9);
        TextView textViewCreateQuickActionText5 = host.createQuickActionText("卖牌", -15770, 268435456, iDp2px6, 0, iDp2px9);
        textViewCreateQuickActionText5.setContentDescription("一键卖牌");
        host.setBtnSellAllFloatRef(textViewCreateQuickActionText5);
        TextView textViewCreateQuickActionText6 = host.createQuickActionText("换位", GameHudPalette.PURPLE, 268435456, iDp2px6, 0, iDp2px9);
        textViewCreateQuickActionText6.setContentDescription("一键换位");
        TextView textViewCreateQuickActionText7 = host.createQuickActionText("镜像", -7607811, 268435456, iDp2px6, 0, iDp2px9);
        textViewCreateQuickActionText7.setContentDescription("镜像换位");
        TextView textViewCreateQuickActionText8 = host.createQuickActionText("刷新", host.autoRefreshEnabled() ? -1048582 : -1511433, host.autoRefreshEnabled() ? 543748263 : 268435456, iDp2px6, 0, iDp2px9);
        host.setBtnAutoRefreshFloatRef(textViewCreateQuickActionText8);
        TextView textViewCreateQuickActionText9 = host.createQuickActionText("▾", -6375989, 201326592, 0, 0, iDp2px9);
        textViewCreateQuickActionText9.setMinWidth(iDp2px5);
        textViewCreateQuickActionText9.setContentDescription("刷新设置");
        Objects.requireNonNull(host);
        textViewCreateQuickActionText9.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.showAutoRefreshSettingsPopup(view);
            }
        });
        TextView textViewCreateQuickActionText10 = host.createQuickActionText("梭哈", -10133, 268435456, iDp2px6, 0, iDp2px9);
        host.setBtnAllInFloatRef(textViewCreateQuickActionText10);
        TextView textViewCreateQuickActionText11 = host.createQuickActionText("▾", -6375989, 201326592, 0, 0, iDp2px9);
        textViewCreateQuickActionText11.setMinWidth(iDp2px5);
        textViewCreateQuickActionText11.setContentDescription("梭哈设置");
        TextView[] textViewArr = {textViewCreateQuickActionText3, textViewCreateQuickActionText4, textViewCreateQuickActionText5, textViewCreateQuickActionText6, textViewCreateQuickActionText7, textViewCreateQuickActionText8, textViewCreateQuickActionText9, textViewCreateQuickActionText10, textViewCreateQuickActionText11};
        int i10 = 0;
        for (int i11 = 9; i10 < i11; i11 = 9) {
            host.configureQuickActionControl(textViewArr[i10], iDp2px11, f);
            i10++;
        }
        textViewCreateQuickActionText8.setContentDescription(host.autoRefreshEnabled() ? "自动刷新已开启" : "自动刷新已关闭");
        textViewCreateQuickActionText10.setContentDescription("开始一键梭哈");
        textViewCreateQuickActionText3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QuickActionDockBuilder.lambda$create$1(host, view);
            }
        });
        textViewCreateQuickActionText4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QuickActionDockBuilder.lambda$create$2(host, view);
            }
        });
        textViewCreateQuickActionText5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.triggerConfiguredSell(view, 0, "开始一键卖牌");
            }
        });
        textViewCreateQuickActionText6.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QuickActionDockBuilder.lambda$create$4(host, view);
            }
        });
        textViewCreateQuickActionText7.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QuickActionDockBuilder.lambda$create$5(host, view);
            }
        });
        Objects.requireNonNull(host);
        textViewCreateQuickActionText8.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.toggleAutoRefreshFromQuickAction(view);
            }
        });
        textViewCreateQuickActionText8.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda3
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return QuickActionDockBuilder.lambda$create$6(host, view);
            }
        });
        Objects.requireNonNull(host);
        textViewCreateQuickActionText10.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.toggleAllInFromQuickAction(view);
            }
        });
        Objects.requireNonNull(host);
        textViewCreateQuickActionText11.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.showAllInSettingsPopup(view);
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(17);
        linearLayout2.addView(textViewCreateQuickActionText, new LinearLayout.LayoutParams(iDp2px4, iDp2px11));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(iDp2px4, iDp2px11);
        layoutParams.setMarginStart(iDp2px);
        linearLayout2.addView(textViewCreateQuickActionText2, layoutParams);
        final ArrayList arrayList = new ArrayList();
        if (host.isQuickActionButtonEnabled(1)) {
            arrayList.add(textViewCreateQuickActionText3);
        }
        if (host.isQuickActionButtonEnabled(2)) {
            arrayList.add(textViewCreateQuickActionText4);
        }
        if (host.isQuickActionButtonEnabled(4)) {
            arrayList.add(textViewCreateQuickActionText5);
        }
        if (host.isQuickActionButtonEnabled(8)) {
            arrayList.add(textViewCreateQuickActionText6);
        }
        if (host.isQuickActionButtonEnabled(64)) {
            arrayList.add(textViewCreateQuickActionText7);
        }
        if (host.isQuickActionButtonEnabled(16)) {
            arrayList.add(host.createQuickActionSplitUnit(textViewCreateQuickActionText8, textViewCreateQuickActionText9, iDp2px11, iDp2px5));
        }
        if (host.isQuickActionButtonEnabled(32)) {
            arrayList.add(host.createQuickActionSplitUnit(textViewCreateQuickActionText10, textViewCreateQuickActionText11, iDp2px11, iDp2px5));
        }
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(1);
        int i12 = 16;
        linearLayout3.setGravity(16);
        int iRowsFor = QuickActionOverlayPolicy.rowsFor(arrayList.size(), iSlimRowColumns);
        int i13 = 0;
        int i14 = 0;
        while (true) {
            int i15 = 860711818;
            if (i13 >= iRowsFor) {
                break;
            }
            LinearLayout linearLayout4 = new LinearLayout(context);
            linearLayout4.setOrientation(0);
            linearLayout4.setGravity(i12);
            int iItemsInRow = QuickActionOverlayPolicy.itemsInRow(arrayList.size(), iSlimRowColumns, i13);
            int i16 = i14;
            int i17 = 0;
            while (i17 < iItemsInRow) {
                if (i17 > 0) {
                    View view = new View(context);
                    view.setBackgroundColor(i15);
                    i = iRowsFor;
                    i2 = i9;
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(iDp2px, i2);
                    i3 = i13;
                    layoutParams2.gravity = 16;
                    i4 = i8;
                    layoutParams2.setMarginStart(i4);
                    layoutParams2.setMarginEnd(i4);
                    linearLayout4.addView(view, layoutParams2);
                } else {
                    i = iRowsFor;
                    i2 = i9;
                    i3 = i13;
                    i4 = i8;
                }
                linearLayout4.addView((View) arrayList.get(i16), new LinearLayout.LayoutParams(-2, iDp2px11));
                i17++;
                i16++;
                i15 = 860711818;
                i8 = i4;
                i13 = i3;
                i9 = i2;
                iRowsFor = i;
            }
            int i18 = iRowsFor;
            int i19 = i9;
            int i20 = i13;
            int i21 = i8;
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-2, iDp2px11);
            if (i20 > 0) {
                layoutParams3.topMargin = i21;
            }
            linearLayout3.addView(linearLayout4, layoutParams3);
            int i22 = i20 + 1;
            i9 = i19;
            i8 = i21;
            i14 = i16;
            iRowsFor = i18;
            i13 = i22;
            i12 = 16;
        }
        int i23 = i8;
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-2, -2));
        View view2 = new View(context);
        view2.setBackgroundColor(860711818);
        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(iDp2px, i9);
        layoutParams4.gravity = 16;
        layoutParams4.setMarginStart(iDp2px2);
        layoutParams4.setMarginEnd(i23);
        linearLayout.addView(view2, layoutParams4);
        linearLayout.addView(linearLayout3, new LinearLayout.LayoutParams(-2, -2));
        host.setQuickActionOverlay(linearLayout);
        WindowManager.LayoutParams layoutParams5 = new WindowManager.LayoutParams(-2, -2, 2038, 40, -3);
        layoutParams5.gravity = 8388659;
        int iRowsFor2 = QuickActionOverlayPolicy.rowsFor(arrayList.size(), iSlimRowColumns);
        QuickActionOverlayPolicy.Point pointDefaultPosition = QuickActionOverlayPolicy.defaultPosition(quickActionSafeRect, i7 + i6 + iDp2px + iDp2px2 + i23 + iMax, i7 + Math.max(iDp2px11, (iRowsFor2 * iDp2px11) + (Math.max(0, iRowsFor2 - 1) * i23)), ScreenUtils.dp2px(context, 12.0f));
        layoutParams5.x = pointDefaultPosition.x;
        layoutParams5.y = pointDefaultPosition.y;
        host.setParamsQuickAction(layoutParams5);
        host.setQuickActionVisible(host.isQuickActionVisibleByPreference());
        host.syncQuickActionOverlayVisibility();
        linearLayout.post(new Runnable() { // from class: com.android.support.QuickActionDockBuilder$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                QuickActionDockBuilder.lambda$create$7(host, arrayList);
            }
        });
        host.logDockAdded(host.isQuickActionVisibleByPreference() && host.canShowVerifiedFeatures());
    }

    static /* synthetic */ boolean lambda$create$0(View view, MotionEvent motionEvent) {
        view.setAlpha(motionEvent.getAction() == 9 ? 1.0f : 0.82f);
        return false;
    }

    static /* synthetic */ void lambda$create$1(Host host, View view) {
        if (!host.shmReady()) {
            host.safeToast("共享内存未初始化");
        } else {
            host.triggerConfiguredSell(view, 1, "开始卖备战席英雄");
        }
    }

    static /* synthetic */ void lambda$create$2(Host host, View view) {
        if (!host.shmReady()) {
            host.safeToast("共享内存未初始化");
        } else {
            host.triggerConfiguredSell(view, 2, "开始卖场上英雄");
        }
    }

    static /* synthetic */ void lambda$create$4(Host host, View view) {
        host.animateQuickActionConfirm(view);
        host.toggleSwapBoardWindow();
    }

    static /* synthetic */ void lambda$create$5(Host host, View view) {
        host.animateQuickActionConfirm(view);
        host.triggerMirrorFromQuickAction();
    }

    static /* synthetic */ boolean lambda$create$6(Host host, View view) {
        host.showAutoRefreshSettingsPopup(view);
        return true;
    }

    static /* synthetic */ void lambda$create$7(Host host, ArrayList arrayList) {
        host.restoreAndClampQuickActionPosition();
        if (!host.isQuickActionAdded() || host.quickActionOverlay() == null) {
            return;
        }
        OverlayUiMotion.playCompanionEnter(host.quickActionOverlay(), 1);
        for (int i = 0; i < arrayList.size(); i++) {
            OverlayUiMotion.animateListItem((View) arrayList.get(i), i);
        }
    }
}
