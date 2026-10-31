package com.android.support;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import com.android.support.OpponentCardingStatus;
import java.util.Iterator;

/* loaded from: classes.dex */
final class OpponentCardingSettingsPanel extends FrameLayout {
    private static final int CLOSE_TARGET_DP = 48;
    private static final int COLOR_ACCENT = -10556417;
    private static final int COLOR_BORDER = -14991022;
    private static final int COLOR_CONTROL = -15655890;
    private static final int COLOR_DANGER = -36205;
    private static final int COLOR_GROUP = -15722455;
    private static final int COLOR_PANEL = -16117988;
    private static final int COLOR_SUCCESS = -9899865;
    private static final int COLOR_TEXT = -1378052;
    private static final int COLOR_TEXT_SECONDARY = -7034446;
    private static final int COLOR_WARNING = -15770;
    private static final int COST_TOUCH_TARGET_DP = 48;
    private static final int FIVE_COST_ROW_MIN_DP = 248;
    private static final int HEADER_HEIGHT_DP = 48;
    private static final int MIN_TOUCH_TARGET_DP = 48;
    private final LinearLayout bodyContent;
    private final ScrollView bodyScroll;
    private final ImageView close;
    private Boolean compactMode;
    private final Button[] costButtons;
    private final LinearLayout costRows;
    private final TextView headerState;
    private final HeroIconLoader heroIconLoader;
    private final HeroNameResolver heroNameResolver;
    private final Listener listener;
    private final TextView maxOwnedValue;
    private int renderedCostColumns;
    private String renderedTargetIdentitySignature;
    private final TextView reserveGoldValue;
    private OpponentCardingStatus status;
    private final float targetDisplayDensity;
    private final int targetDisplayId;
    private final float targetDisplayScaledDensity;
    private final LinearLayout targetIconRow;
    private final TextView thresholdValue;
    private final TextView title;
    private OpponentCardingUiState uiState;

    interface HeroIconLoader {
        void load(ImageView imageView, int i);
    }

    interface HeroNameResolver {
        String resolveHeroName(int i);
    }

    interface Listener {
        void onCloseRequested();

        void onConfigChanged(OpponentCardingConfig opponentCardingConfig);
    }

    OpponentCardingSettingsPanel(Context context, int i, float f, final Listener listener, HeroNameResolver heroNameResolver, HeroIconLoader heroIconLoader) {
        Display defaultDisplay;
        super(context);
        this.uiState = new OpponentCardingUiState(OpponentCardingConfig.defaults());
        this.status = OpponentCardingStatus.disabled();
        this.costButtons = new Button[5];
        float f2 = context.getResources().getDisplayMetrics().density;
        this.targetDisplayScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        if (Build.VERSION.SDK_INT >= 30) {
            defaultDisplay = context.getDisplay();
        } else {
            WindowManager windowManager = (WindowManager) context.getSystemService("window");
            defaultDisplay = windowManager == null ? null : windowManager.getDefaultDisplay();
        }
        int displayId = defaultDisplay == null ? -1 : defaultDisplay.getDisplayId();
        this.targetDisplayId = displayId != i ? -1 : displayId;
        if (f > 0.0f && Float.isFinite(f)) {
            f2 = f;
        } else if (f2 <= 0.0f) {
            f2 = 1.0f;
        }
        this.targetDisplayDensity = f2;
        this.listener = listener;
        this.heroNameResolver = heroNameResolver;
        this.heroIconLoader = heroIconLoader;
        setClipChildren(true);
        setClipToPadding(true);
        applyBackground(this, panelBackground(), R.drawable.bg_tech_window);
        OverlayUiMotion.enableRoundedClip(this);
        setElevation(dp(12.0f));
        ScrollCuePanel scrollCuePanel = new ScrollCuePanel(getContext());
        scrollCuePanel.setOrientation(1);
        addView(scrollCuePanel, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout linearLayoutHorizontalRow = horizontalRow();
        linearLayoutHorizontalRow.setPadding(dp(10.0f), 0, 0, 0);
        applyBackground(linearLayoutHorizontalRow, roundedBackground(COLOR_GROUP, 16, COLOR_GROUP, 0), R.drawable.bg_tech_titlebar);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.ic_tech_function);
        imageView.setColorFilter(-10556417);
        imageView.setContentDescription(null);
        linearLayoutHorizontalRow.addView(imageView, new LinearLayout.LayoutParams(dp(18.0f), dp(18.0f)));
        TextView textViewText = text("对手卡牌", 15, -10556417, true);
        this.title = textViewText;
        textViewText.setSingleLine(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.leftMargin = dp(6.0f);
        linearLayoutHorizontalRow.addView(textViewText, layoutParams);
        TextView textViewText2 = text("已关闭", 10, -7034446, true);
        this.headerState = textViewText2;
        textViewText2.setGravity(17);
        textViewText2.setSingleLine(true);
        textViewText2.setPadding(dp(6.0f), dp(3.0f), dp(6.0f), dp(3.0f));
        textViewText2.setBackground(roundedBackground(COLOR_CONTROL, 14, COLOR_BORDER, 1));
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams2.rightMargin = dp(4.0f);
        linearLayoutHorizontalRow.addView(textViewText2, layoutParams2);
        ImageView imageView2 = new ImageView(context);
        this.close = imageView2;
        imageView2.setImageResource(R.drawable.ic_panel_close);
        imageView2.setColorFilter(-36205);
        imageView2.setBackground(interactiveBackground(COLOR_BORDER, -36205));
        imageView2.setPadding(dp(15.0f), dp(15.0f), dp(15.0f), dp(15.0f));
        imageView2.setMinimumWidth(dp(48.0f));
        imageView2.setMinimumHeight(dp(48.0f));
        imageView2.setClickable(true);
        imageView2.setFocusable(true);
        imageView2.setContentDescription("关闭对手卡牌设置");
        OverlayUiMotion.installPressScale(imageView2);
        imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentCardingSettingsPanel$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                OpponentCardingSettingsPanel.lambda$new$0(listener, view);
            }
        });
        linearLayoutHorizontalRow.addView(imageView2, new LinearLayout.LayoutParams(dp(48.0f), dp(48.0f)));
        scrollCuePanel.addView(linearLayoutHorizontalRow, new LinearLayout.LayoutParams(-1, dp(48.0f)));
        addSectionDivider(scrollCuePanel);
        ScrollView scrollView = new ScrollView(context);
        this.bodyScroll = scrollView;
        scrollView.setFillViewport(true);
        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setClipToPadding(false);
        scrollView.setOverScrollMode(1);
        ScrollEdgeCue.attach(scrollView);
        scrollCuePanel.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        LinearLayout linearLayoutVerticalGroup = verticalGroup();
        this.bodyContent = linearLayoutVerticalGroup;
        linearLayoutVerticalGroup.setPadding(dp(4.0f), dp(6.0f), dp(4.0f), dp(10.0f));
        scrollView.addView(linearLayoutVerticalGroup, new FrameLayout.LayoutParams(-1, -2));
        LinearLayout linearLayoutVerticalGroup2 = verticalGroup();
        linearLayoutVerticalGroup2.setPadding(0, dp(2.0f), 0, dp(2.0f));
        applyBackground(linearLayoutVerticalGroup2, roundedBackground(COLOR_GROUP, 16, COLOR_BORDER, 1), R.drawable.bg_tech_card);
        linearLayoutVerticalGroup.addView(linearLayoutVerticalGroup2, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout linearLayoutLabeledRow = labeledRow("触发张数");
        Button buttonActionButton = actionButton("−");
        buttonActionButton.setContentDescription("降低触发张数");
        buttonActionButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentCardingSettingsPanel$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1383lambda$new$1$comandroidsupportOpponentCardingSettingsPanel(view);
            }
        });
        TextView textViewValueText = valueText();
        this.thresholdValue = textViewValueText;
        Button buttonActionButton2 = actionButton("+");
        buttonActionButton2.setContentDescription("提高触发张数");
        buttonActionButton2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentCardingSettingsPanel$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1384lambda$new$2$comandroidsupportOpponentCardingSettingsPanel(view);
            }
        });
        linearLayoutLabeledRow.addView(buttonActionButton, touchTargetParams());
        linearLayoutLabeledRow.addView(textViewValueText, compactValueParams());
        linearLayoutLabeledRow.addView(buttonActionButton2, touchTargetParams());
        linearLayoutVerticalGroup2.addView(linearLayoutLabeledRow);
        addSectionDivider(linearLayoutVerticalGroup2);
        LinearLayout linearLayoutVerticalGroup3 = verticalGroup();
        linearLayoutVerticalGroup3.setPadding(dp(4.0f), dp(5.0f), dp(4.0f), dp(6.0f));
        linearLayoutVerticalGroup3.addView(sectionLabel("费用"));
        LinearLayout linearLayoutVerticalGroup4 = verticalGroup();
        this.costRows = linearLayoutVerticalGroup4;
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams3.topMargin = dp(4.0f);
        linearLayoutVerticalGroup3.addView(linearLayoutVerticalGroup4, layoutParams3);
        for (final int i2 = 1; i2 <= 5; i2++) {
            Button buttonActionButton3 = actionButton(i2 + "费");
            buttonActionButton3.setContentDescription("切换" + i2 + "费英雄");
            buttonActionButton3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentCardingSettingsPanel$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1385lambda$new$3$comandroidsupportOpponentCardingSettingsPanel(i2, view);
                }
            });
            this.costButtons[i2 - 1] = buttonActionButton3;
        }
        linearLayoutVerticalGroup2.addView(linearLayoutVerticalGroup3);
        addSectionDivider(linearLayoutVerticalGroup2);
        LinearLayout linearLayoutLabeledRow2 = labeledRow("保留金币");
        Button buttonActionButton4 = actionButton("−10");
        buttonActionButton4.setContentDescription("降低保留金币");
        buttonActionButton4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentCardingSettingsPanel$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1386lambda$new$4$comandroidsupportOpponentCardingSettingsPanel(view);
            }
        });
        TextView textViewValueText2 = valueText();
        this.reserveGoldValue = textViewValueText2;
        Button buttonActionButton5 = actionButton("+10");
        buttonActionButton5.setContentDescription("提高保留金币");
        buttonActionButton5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentCardingSettingsPanel$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1387lambda$new$5$comandroidsupportOpponentCardingSettingsPanel(view);
            }
        });
        linearLayoutLabeledRow2.addView(buttonActionButton4, touchTargetParams());
        linearLayoutLabeledRow2.addView(textViewValueText2, compactValueParams());
        linearLayoutLabeledRow2.addView(buttonActionButton5, touchTargetParams());
        linearLayoutVerticalGroup2.addView(linearLayoutLabeledRow2);
        addSectionDivider(linearLayoutVerticalGroup2);
        LinearLayout linearLayoutLabeledRow3 = labeledRow("持有上限");
        Button buttonActionButton6 = actionButton("−");
        buttonActionButton6.setContentDescription("降低持有上限");
        buttonActionButton6.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentCardingSettingsPanel$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1388lambda$new$6$comandroidsupportOpponentCardingSettingsPanel(view);
            }
        });
        TextView textViewValueText3 = valueText();
        this.maxOwnedValue = textViewValueText3;
        Button buttonActionButton7 = actionButton("+");
        buttonActionButton7.setContentDescription("提高持有上限");
        buttonActionButton7.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentCardingSettingsPanel$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1389lambda$new$7$comandroidsupportOpponentCardingSettingsPanel(view);
            }
        });
        linearLayoutLabeledRow3.addView(buttonActionButton6, touchTargetParams());
        linearLayoutLabeledRow3.addView(textViewValueText3, compactValueParams());
        linearLayoutLabeledRow3.addView(buttonActionButton7, touchTargetParams());
        linearLayoutVerticalGroup2.addView(linearLayoutLabeledRow3);
        TextView textViewText3 = text("达到设置张数后持续购买；低于张数会自动停止。每次购买都会等待商店更新，避免重复操作。", 12, -7034446, false);
        textViewText3.setLineSpacing(0.0f, 1.2f);
        textViewText3.setPadding(dp(10.0f), dp(8.0f), dp(10.0f), dp(8.0f));
        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams4.topMargin = dp(8.0f);
        this.bodyContent.addView(textViewText3, layoutParams4);
        TextView textViewSectionLabel = sectionLabel("监控目标");
        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams5.topMargin = dp(8.0f);
        layoutParams5.leftMargin = dp(4.0f);
        layoutParams5.bottomMargin = dp(4.0f);
        this.bodyContent.addView(textViewSectionLabel, layoutParams5);
        LinearLayout linearLayoutVerticalGroup5 = verticalGroup();
        this.targetIconRow = linearLayoutVerticalGroup5;
        linearLayoutVerticalGroup5.setOrientation(1);
        linearLayoutVerticalGroup5.setPadding(0, dp(2.0f), 0, dp(4.0f));
        this.bodyContent.addView(linearLayoutVerticalGroup5, new LinearLayout.LayoutParams(-1, -2));
        View view = new View(context);
        view.setBackground(roundedBackground(-10556417, 1, -10556417, 0));
        FrameLayout.LayoutParams layoutParams6 = new FrameLayout.LayoutParams(dp(1.0f), -1, GravityCompat.START);
        layoutParams6.topMargin = dp(14.0f);
        layoutParams6.bottomMargin = dp(14.0f);
        addView(view, layoutParams6);
        reflowCostButtons(dp(236.0f));
        bind(this.uiState.getConfig(), this.status);
    }

    static /* synthetic */ void lambda$new$0(Listener listener, View view) {
        if (listener != null) {
            listener.onCloseRequested();
        }
    }

    /* renamed from: lambda$new$1$com-android-support-OpponentCardingSettingsPanel, reason: not valid java name */
    /* synthetic */ void m1383lambda$new$1$comandroidsupportOpponentCardingSettingsPanel(View view) {
        updateState(this.uiState.stepThreshold(-1));
    }

    /* renamed from: lambda$new$2$com-android-support-OpponentCardingSettingsPanel, reason: not valid java name */
    /* synthetic */ void m1384lambda$new$2$comandroidsupportOpponentCardingSettingsPanel(View view) {
        updateState(this.uiState.stepThreshold(1));
    }

    /* renamed from: lambda$new$3$com-android-support-OpponentCardingSettingsPanel, reason: not valid java name */
    /* synthetic */ void m1385lambda$new$3$comandroidsupportOpponentCardingSettingsPanel(int i, View view) {
        updateState(this.uiState.toggleCost(i));
    }

    /* renamed from: lambda$new$4$com-android-support-OpponentCardingSettingsPanel, reason: not valid java name */
    /* synthetic */ void m1386lambda$new$4$comandroidsupportOpponentCardingSettingsPanel(View view) {
        updateState(this.uiState.stepReserveGold(-1));
    }

    /* renamed from: lambda$new$5$com-android-support-OpponentCardingSettingsPanel, reason: not valid java name */
    /* synthetic */ void m1387lambda$new$5$comandroidsupportOpponentCardingSettingsPanel(View view) {
        updateState(this.uiState.stepReserveGold(1));
    }

    /* renamed from: lambda$new$6$com-android-support-OpponentCardingSettingsPanel, reason: not valid java name */
    /* synthetic */ void m1388lambda$new$6$comandroidsupportOpponentCardingSettingsPanel(View view) {
        updateState(this.uiState.stepMaxOwnedCopies(-1));
    }

    /* renamed from: lambda$new$7$com-android-support-OpponentCardingSettingsPanel, reason: not valid java name */
    /* synthetic */ void m1389lambda$new$7$comandroidsupportOpponentCardingSettingsPanel(View view) {
        updateState(this.uiState.stepMaxOwnedCopies(1));
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        reflowCostButtons(i);
    }

    void setCompactMode(boolean z) {
        int iRound;
        Boolean bool = this.compactMode;
        if (bool == null || bool.booleanValue() != z) {
            this.compactMode = Boolean.valueOf(z);
            this.title.setTextSize(z ? 14.0f : 15.0f);
            this.bodyContent.setPadding(dp(4.0f), dp(6.0f), dp(4.0f), dp(z ? 8.0f : 10.0f));
            if (z) {
                iRound = Math.round(236.0f);
            } else {
                iRound = Math.round(268.0f);
            }
            reflowCostButtons(getWidth() > 0 ? getWidth() : dp(iRound));
            requestLayout();
        }
    }

    boolean usesTargetDisplayDensity(float f) {
        return f > 0.0f && Float.isFinite(f) && Math.abs(this.targetDisplayDensity - f) < 0.001f;
    }

    boolean usesTargetDisplay(int i, float f) {
        if (this.targetDisplayId != i || !usesTargetDisplayDensity(f)) {
            return false;
        }
        float f2 = this.targetDisplayScaledDensity;
        return f2 > 0.0f && Float.isFinite(f2);
    }

    void bind(OpponentCardingConfig opponentCardingConfig, OpponentCardingStatus opponentCardingStatus) {
        int i;
        if (opponentCardingConfig == null) {
            opponentCardingConfig = OpponentCardingConfig.defaults();
        }
        this.uiState = new OpponentCardingUiState(opponentCardingConfig);
        if (opponentCardingStatus == null) {
            opponentCardingStatus = OpponentCardingStatus.disabled();
        }
        this.status = opponentCardingStatus;
        OpponentCardingConfig config = this.uiState.getConfig();
        this.thresholdValue.setText(config.getThreshold() + "张");
        this.reserveGoldValue.setText(String.valueOf(config.getReserveGold()));
        this.maxOwnedValue.setText(config.getMaxOwnedCopies() + "张");
        int i2 = 1;
        while (true) {
            i = 0;
            boolean z = false;
            if (i2 > 5) {
                break;
            }
            if ((config.getCostMask() & OpponentCardingConfig.costBit(i2)) != 0) {
                z = true;
            }
            styleSelectable(this.costButtons[i2 - 1], z);
            i2++;
        }
        int size = this.status.getTargets().size();
        String strBuildTargetIdentitySignature = buildTargetIdentitySignature(this.status);
        boolean zShouldAnimateIdentitySet = OverlayUiStylePolicy.shouldAnimateIdentitySet(this.renderedTargetIdentitySignature, strBuildTargetIdentitySignature);
        this.headerState.setText(compactHeaderState(config, this.status, size));
        this.headerState.setTextColor(config.isEnabled() ? statusColor(this.status.getState()) : -7034446);
        this.targetIconRow.removeAllViews();
        if (size == 0) {
            TextView textViewText = text("暂无达到设置张数的目标", 12, -7034446, false);
            textViewText.setGravity(16);
            textViewText.setMinHeight(dp(48.0f));
            textViewText.setPadding(dp(10.0f), 0, dp(10.0f), 0);
            textViewText.setBackground(roundedBackground(COLOR_GROUP, 16, COLOR_BORDER, 1));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, dp(48.0f));
            layoutParams.leftMargin = dp(2.0f);
            layoutParams.rightMargin = dp(2.0f);
            this.targetIconRow.addView(textViewText, layoutParams);
            this.renderedTargetIdentitySignature = strBuildTargetIdentitySignature;
            return;
        }
        Iterator<OpponentCardingStatus.Target> it = this.status.getTargets().iterator();
        while (it.hasNext()) {
            View viewCreateTargetCard = createTargetCard(it.next());
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
            layoutParams2.leftMargin = dp(2.0f);
            layoutParams2.rightMargin = dp(2.0f);
            if (i > 0) {
                layoutParams2.topMargin = dp(6.0f);
            }
            this.targetIconRow.addView(viewCreateTargetCard, layoutParams2);
            if (zShouldAnimateIdentitySet) {
                OverlayUiMotion.animateListItem(viewCreateTargetCard, i);
            }
            i++;
        }
        this.renderedTargetIdentitySignature = strBuildTargetIdentitySignature;
    }

    private View createTargetCard(OpponentCardingStatus.Target target) {
        String string;
        String playerName = target.getPlayerName();
        if (playerName == null || playerName.isEmpty()) {
            playerName = "对手 " + target.getPlayerId();
        }
        HeroNameResolver heroNameResolver = this.heroNameResolver;
        String strResolveHeroName = heroNameResolver == null ? "" : heroNameResolver.resolveHeroName(target.getHeroId());
        if (strResolveHeroName == null || strResolveHeroName.isEmpty() || "未知英雄".equals(strResolveHeroName)) {
            strResolveHeroName = "英雄 " + target.getHeroId();
        }
        String reason = target.getReason();
        if (target.isPurchaseEligible()) {
            string = "可自动购买";
        } else {
            StringBuilder sb = new StringBuilder("仅监控 · ");
            if (reason == null || reason.isEmpty()) {
                reason = "当前费用未开启";
            }
            string = sb.append(reason).toString();
        }
        LinearLayout linearLayoutHorizontalRow = horizontalRow();
        linearLayoutHorizontalRow.setGravity(16);
        linearLayoutHorizontalRow.setMinimumHeight(dp(60.0f));
        linearLayoutHorizontalRow.setPadding(dp(10.0f), dp(6.0f), dp(10.0f), dp(6.0f));
        linearLayoutHorizontalRow.setBackground(roundedBackground(COLOR_CONTROL, 16, target.isPurchaseEligible() ? -9899865 : COLOR_BORDER, 1));
        linearLayoutHorizontalRow.setContentDescription(playerName + "，" + strResolveHeroName + "，" + target.getCopies() + "/" + target.getThreshold() + "张，本方已卡" + target.getOwnedCopies() + "/" + target.getMaxOwnedCopies() + "张，还需" + target.getNeededCopies() + "张，" + string);
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setContentDescription(null);
        imageView.setBackground(roundedBackground(COLOR_GROUP, 12, COLOR_BORDER, 1));
        linearLayoutHorizontalRow.addView(imageView, new LinearLayout.LayoutParams(dp(36.0f), dp(36.0f)));
        HeroIconLoader heroIconLoader = this.heroIconLoader;
        if (heroIconLoader != null) {
            heroIconLoader.load(imageView, target.getHeroId());
        }
        LinearLayout linearLayoutVerticalGroup = verticalGroup();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.leftMargin = dp(8.0f);
        linearLayoutHorizontalRow.addView(linearLayoutVerticalGroup, layoutParams);
        TextView textViewText = text(playerName + " · " + strResolveHeroName, 12, -1378052, true);
        textViewText.setSingleLine(true);
        textViewText.setEllipsize(TextUtils.TruncateAt.END);
        linearLayoutVerticalGroup.addView(textViewText, new LinearLayout.LayoutParams(-1, -2));
        TextView textViewText2 = text(target.getCopies() + "/" + target.getThreshold() + "张 · 本方已卡 " + target.getOwnedCopies() + "/" + target.getMaxOwnedCopies() + " · 还需 " + target.getNeededCopies() + "张 · " + string, 11, target.isPurchaseEligible() ? -9899865 : -15770, false);
        textViewText2.setMaxLines(2);
        textViewText2.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams2.topMargin = dp(2.0f);
        linearLayoutVerticalGroup.addView(textViewText2, layoutParams2);
        return linearLayoutHorizontalRow;
    }

    private void reflowCostButtons(int i) {
        int i2 = Math.max(1, Math.max(1, i) - dp(16.0f)) >= dp(248.0f) ? 5 : 3;
        if (this.renderedCostColumns != i2 || this.costRows.getChildCount() <= 0) {
            this.renderedCostColumns = i2;
            this.costRows.removeAllViews();
            int i3 = 0;
            while (i3 < this.costButtons.length) {
                LinearLayout linearLayoutHorizontalRow = horizontalRow();
                linearLayoutHorizontalRow.setGravity(17);
                int iMin = Math.min(i2, this.costButtons.length - i3);
                int i4 = 0;
                while (i4 < iMin) {
                    int i5 = i3 + 1;
                    Button button = this.costButtons[i3];
                    ViewParentCompat.detach(button);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp(48.0f), dp(48.0f));
                    if (i4 > 0) {
                        layoutParams.leftMargin = dp(2.0f);
                    }
                    linearLayoutHorizontalRow.addView(button, layoutParams);
                    i4++;
                    i3 = i5;
                }
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
                if (this.costRows.getChildCount() > 0) {
                    layoutParams2.topMargin = dp(4.0f);
                }
                this.costRows.addView(linearLayoutHorizontalRow, layoutParams2);
            }
        }
    }

    private String compactHeaderState(OpponentCardingConfig opponentCardingConfig, OpponentCardingStatus opponentCardingStatus, int i) {
        if (!opponentCardingConfig.isEnabled()) {
            return "已关闭";
        }
        if (i > 0) {
            return i + "个目标";
        }
        String state = opponentCardingStatus.getState();
        return OpponentCardingStatus.STATE_BLOCKED_GOLD.equals(state) ? "等待金币" : OpponentCardingStatus.STATE_BLOCKED_LIMIT.equals(state) ? "已达上限" : OpponentCardingStatus.STATE_TOO_LATE.equals(state) ? "等待下轮" : OpponentCardingStatus.STATE_ACTIVE.equals(state) ? "运行中" : "已启用";
    }

    private void updateState(OpponentCardingUiState opponentCardingUiState) {
        OpponentCardingConfig config = this.uiState.getConfig();
        this.uiState = opponentCardingUiState;
        bind(opponentCardingUiState.getConfig(), this.status);
        OpponentCardingConfig config2 = opponentCardingUiState.getConfig();
        if (config.getThreshold() != config2.getThreshold()) {
            OverlayUiMotion.pulseValue(this.thresholdValue);
        }
        if (config.getReserveGold() != config2.getReserveGold()) {
            OverlayUiMotion.pulseValue(this.reserveGoldValue);
        }
        if (config.getMaxOwnedCopies() != config2.getMaxOwnedCopies()) {
            OverlayUiMotion.pulseValue(this.maxOwnedValue);
        }
        Listener listener = this.listener;
        if (listener != null) {
            listener.onConfigChanged(opponentCardingUiState.getConfig());
        }
    }

    private String buildTargetIdentitySignature(OpponentCardingStatus opponentCardingStatus) {
        StringBuilder sb = new StringBuilder();
        for (OpponentCardingStatus.Target target : opponentCardingStatus.getTargets()) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append(target.getPlayerId()).append(':').append(target.getHeroId()).append(':').append(target.isPurchaseEligible());
        }
        return sb.toString();
    }

    private LinearLayout labeledRow(String str) {
        LinearLayout linearLayoutHorizontalRow = horizontalRow();
        linearLayoutHorizontalRow.setPadding(dp(8.0f), dp(4.0f), dp(8.0f), dp(4.0f));
        linearLayoutHorizontalRow.setMinimumHeight(dp(56.0f));
        linearLayoutHorizontalRow.addView(settingLabel(str), labelParams());
        return linearLayoutHorizontalRow;
    }

    private LinearLayout horizontalRow() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        return linearLayout;
    }

    private LinearLayout verticalGroup() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        return linearLayout;
    }

    private void addSectionDivider(LinearLayout linearLayout) {
        View view = new View(getContext());
        view.setBackgroundColor(COLOR_BORDER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, dp(1.0f));
        layoutParams.leftMargin = dp(8.0f);
        layoutParams.rightMargin = dp(8.0f);
        linearLayout.addView(view, layoutParams);
    }

    private TextView settingLabel(String str) {
        TextView textViewText = text(str, 12, -1378052, true);
        textViewText.setGravity(16);
        return textViewText;
    }

    private TextView sectionLabel(String str) {
        return text(str, 12, -10556417, true);
    }

    private TextView valueText() {
        TextView textViewText = text("", 12, -1378052, true);
        textViewText.setGravity(17);
        textViewText.setMinHeight(dp(48.0f));
        return textViewText;
    }

    private Button actionButton(String str) {
        Button button = new Button(getContext());
        button.setAllCaps(false);
        button.setText(str);
        button.setTextSize(12.0f);
        button.setTextColor(-1378052);
        button.setGravity(17);
        button.setMinWidth(dp(48.0f));
        button.setMinHeight(dp(48.0f));
        button.setPadding(dp(4.0f), 0, dp(4.0f), 0);
        button.setStateListAnimator(null);
        button.setBackground(interactiveBackground(COLOR_BORDER, -10556417));
        OverlayUiMotion.installPressScale(button);
        return button;
    }

    private TextView text(String str, int i, int i2, boolean z) {
        TextView textView = new TextView(getContext());
        textView.setText(str);
        textView.setTextSize(Math.max(10, i));
        textView.setTextColor(i2);
        if (z) {
            textView.setTypeface(Typeface.DEFAULT, 1);
        }
        return textView;
    }

    private void styleSelectable(Button button, boolean z) {
        button.setTextColor(z ? -10556417 : -7034446);
        button.setBackground(interactiveBackground(z ? -10556417 : COLOR_BORDER, -10556417));
        button.setSelected(z);
    }

    private LinearLayout.LayoutParams labelParams() {
        return new LinearLayout.LayoutParams(0, -2, 1.0f);
    }

    private LinearLayout.LayoutParams touchTargetParams() {
        return new LinearLayout.LayoutParams(dp(48.0f), dp(48.0f));
    }

    private LinearLayout.LayoutParams compactValueParams() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp(48.0f), dp(48.0f));
        layoutParams.leftMargin = dp(2.0f);
        layoutParams.rightMargin = dp(2.0f);
        return layoutParams;
    }

    private int statusColor(String str) {
        if (OpponentCardingStatus.STATE_ACTIVE.equals(str)) {
            return -9899865;
        }
        return (OpponentCardingStatus.STATE_BLOCKED_GOLD.equals(str) || OpponentCardingStatus.STATE_BLOCKED_LIMIT.equals(str) || OpponentCardingStatus.STATE_TOO_LATE.equals(str)) ? -15770 : -7034446;
    }

    private GradientDrawable roundedBackground(int i, int i2, int i3, int i4) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setColor(i);
        gradientDrawable.setCornerRadius(dp(i2));
        if (i4 > 0) {
            gradientDrawable.setStroke(dp(i4), i3);
        }
        return gradientDrawable;
    }

    private Drawable interactiveBackground(int i, int i2) {
        return new RippleDrawable(ColorStateList.valueOf((i2 & ViewCompat.MEASURED_SIZE_MASK) | 855638016), roundedBackground(COLOR_CONTROL, 14, i, 1), roundedBackground(-1, 14, -1, 0));
    }

    private GradientDrawable panelBackground() {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-234023900, COLOR_PANEL, -234419435});
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(dp(20.0f));
        gradientDrawable.setStroke(dp(1.0f), COLOR_BORDER);
        return gradientDrawable;
    }

    private void applyBackground(View view, Drawable drawable, int i) {
        view.setBackground(drawable);
        if (view.getBackground() == null) {
            view.setBackgroundResource(i);
        }
    }

    private int dp(float f) {
        return Math.round(f * this.targetDisplayDensity);
    }

    private static final class ViewParentCompat {
        private ViewParentCompat() {
        }

        static void detach(View view) {
            if (view.getParent() instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }
    }
}
