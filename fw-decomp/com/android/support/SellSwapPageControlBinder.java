package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/* loaded from: classes.dex */
final class SellSwapPageControlBinder {
    private static final int[] SELL_COST_IDS = {R.id.btnSellCost1, R.id.btnSellCost2, R.id.btnSellCost3, R.id.btnSellCost4, R.id.btnSellCost5};
    private static final int[] SELL_COST_COLORS = {-6381922, GameHudPalette.SUCCESS, -14575885, -5552196, -26624};
    private static final int[] SELL_SCOPE_IDS = {R.id.btnSellScopeBench, R.id.btnSellScopeBattle, R.id.btnSellScopeAll};
    private static final int[] SWAP_COST_IDS = {R.id.btnSwapCostAll, R.id.btnSwapCost1, R.id.btnSwapCost2, R.id.btnSwapCost3, R.id.btnSwapCost4, R.id.btnSwapCost5};

    interface Host {
        void animateButtonClick(View view);

        Context context();

        SharedPreferences preferences();

        void rebuildQuickActionOverlayIfPresent();

        void showCustomSwapWindow();

        void toggleSwapBoardWindow();
    }

    private SellSwapPageControlBinder() {
    }

    static void bind(View view, Host host) {
        bindSwapEntries(view, host);
        bindSellRules(view, host);
        bindSellSafetySwitches(view, host);
        bindSwapCostFilter(view, host);
    }

    private static void bindSellSafetySwitches(View view, Host host) {
        SharedPreferences sharedPreferencesPreferences = host.preferences();
        bindSwitch(view, R.id.switchSellAntiMistouch, FloatingWindowPrefsKeys.KEY_SELL_ANTI_MISTOUCH, sharedPreferencesPreferences, false);
        bindSwitch(view, R.id.switchSellKeepAutoPick, FloatingWindowPrefsKeys.KEY_SELL_KEEP_AUTO_PICK, sharedPreferencesPreferences, true);
        bindKeepAutoPickHint(view, sharedPreferencesPreferences);
    }

    private static void bindKeepAutoPickHint(View view, final SharedPreferences sharedPreferences) {
        final TextView textView = (TextView) view.findViewById(R.id.tvSellKeepAutoPickHint);
        Switch r2 = (Switch) view.findViewById(R.id.switchSellKeepAutoPick);
        if (textView == null) {
            return;
        }
        final Runnable runnable = new Runnable() { // from class: com.android.support.SellSwapPageControlBinder$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                textView.setText(sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SELL_KEEP_AUTO_PICK, true) ? "只卖阵容外的牌" : "⚠ 阵容棋子也会被卖掉");
            }
        };
        runnable.run();
        if (r2 == null) {
            return;
        }
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.SellSwapPageControlBinder$$ExternalSyntheticLambda5
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SellSwapPageControlBinder.lambda$bindKeepAutoPickHint$1(sharedPreferences, runnable, compoundButton, z);
            }
        });
    }

    static /* synthetic */ void lambda$bindKeepAutoPickHint$1(SharedPreferences sharedPreferences, Runnable runnable, CompoundButton compoundButton, boolean z) {
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_SELL_KEEP_AUTO_PICK, z).apply();
        runnable.run();
    }

    private static void bindSwitch(View view, int i, final String str, final SharedPreferences sharedPreferences, boolean z) {
        Switch r0 = (Switch) view.findViewById(i);
        if (r0 == null) {
            return;
        }
        r0.setChecked(sharedPreferences.getBoolean(str, z));
        r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.SellSwapPageControlBinder$$ExternalSyntheticLambda3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                sharedPreferences.edit().putBoolean(str, z2).apply();
            }
        });
    }

    private static void bindSwapEntries(View view, final Host host) {
        Button button = (Button) view.findViewById(R.id.btnCustomSwap);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SellSwapPageControlBinder$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    SellSwapPageControlBinder.lambda$bindSwapEntries$3(host, view2);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bindSwapEntries$3(Host host, View view) {
        host.animateButtonClick(view);
        host.showCustomSwapWindow();
    }

    private static void bindSellRules(View view, Host host) {
        final Host host2;
        int[] iArr;
        final SharedPreferences sharedPreferences;
        final Context context = host.context();
        SharedPreferences sharedPreferencesPreferences = host.preferences();
        final TextView textView = (TextView) view.findViewById(R.id.tvSellRuleSummary);
        int length = SELL_COST_IDS.length;
        final TextView[] textViewArr = new TextView[length];
        int length2 = SELL_SCOPE_IDS.length;
        final TextView[] textViewArr2 = new TextView[length2];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            textViewArr[i2] = (TextView) view.findViewById(SELL_COST_IDS[i2]);
        }
        for (int i3 = 0; i3 < length2; i3++) {
            textViewArr2[i3] = (TextView) view.findViewById(SELL_SCOPE_IDS[i3]);
        }
        final int[] iArr2 = {SellRuleText.normalizeCostMask(sharedPreferencesPreferences.getInt(FloatingWindowPrefsKeys.KEY_SELL_COST_MASK, 31))};
        final int[] iArr3 = {SellRuleText.normalizeScope(sharedPreferencesPreferences.getInt(FloatingWindowPrefsKeys.KEY_SELL_SCOPE, 3))};
        if (iArr2[0] == 0) {
            iArr2[0] = 31;
            sharedPreferencesPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SELL_COST_MASK, iArr2[0]).apply();
        }
        Runnable runnable = new Runnable() { // from class: com.android.support.SellSwapPageControlBinder$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                SellSwapPageControlBinder.lambda$bindSellRules$4(textViewArr, context, iArr2, textViewArr2, iArr3, textView);
            }
        };
        int[] iArr4 = iArr3;
        runnable.run();
        int i4 = 0;
        while (i4 < length) {
            TextView textView2 = textViewArr[i4];
            if (textView2 == null) {
                sharedPreferences = sharedPreferencesPreferences;
            } else {
                final Context context2 = context;
                final int i5 = i4 + 1;
                final Runnable runnable2 = runnable;
                sharedPreferences = sharedPreferencesPreferences;
                View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.android.support.SellSwapPageControlBinder$$ExternalSyntheticLambda7
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        SellSwapPageControlBinder.lambda$bindSellRules$5(iArr2, i5, context2, sharedPreferences, runnable2, view2);
                    }
                };
                context = context2;
                runnable = runnable2;
                textView2.setOnClickListener(onClickListener);
            }
            i4++;
            sharedPreferencesPreferences = sharedPreferences;
        }
        SharedPreferences sharedPreferences2 = sharedPreferencesPreferences;
        while (i < length2) {
            TextView textView3 = textViewArr2[i];
            if (textView3 == null) {
                host2 = host;
                iArr = iArr4;
            } else {
                final int i6 = i + 1;
                final int[] iArr5 = iArr4;
                final SharedPreferences sharedPreferences3 = sharedPreferences2;
                host2 = host;
                final Runnable runnable3 = runnable;
                View.OnClickListener onClickListener2 = new View.OnClickListener() { // from class: com.android.support.SellSwapPageControlBinder$$ExternalSyntheticLambda8
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        SellSwapPageControlBinder.lambda$bindSellRules$6(iArr5, i6, sharedPreferences3, runnable3, host2, view2);
                    }
                };
                iArr = iArr5;
                sharedPreferences2 = sharedPreferences3;
                textView3.setOnClickListener(onClickListener2);
            }
            i++;
            iArr4 = iArr;
            host = host2;
        }
    }

    static /* synthetic */ void lambda$bindSellRules$4(TextView[] textViewArr, Context context, int[] iArr, TextView[] textViewArr2, int[] iArr2, TextView textView) {
        int i = 0;
        while (i < textViewArr.length) {
            TextView textView2 = textViewArr[i];
            int i2 = SELL_COST_COLORS[i];
            i++;
            styleChip(context, textView2, i2, SellRuleText.costSelected(iArr[0], i));
        }
        int i3 = 0;
        while (i3 < textViewArr2.length) {
            TextView textView3 = textViewArr2[i3];
            i3++;
            styleChip(context, textView3, -15770, iArr2[0] == i3);
        }
        if (textView != null) {
            textView.setText(SellRuleText.summary(iArr2[0], iArr[0]));
        }
    }

    static /* synthetic */ void lambda$bindSellRules$5(int[] iArr, int i, Context context, SharedPreferences sharedPreferences, Runnable runnable, View view) {
        int i2 = SellRuleText.toggleCost(iArr[0], i);
        if (i2 == 0) {
            FloatingWindowService.safeToast(context, "至少保留一个可卖费用", 0);
            return;
        }
        iArr[0] = i2;
        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SELL_COST_MASK, i2).apply();
        runnable.run();
    }

    static /* synthetic */ void lambda$bindSellRules$6(int[] iArr, int i, SharedPreferences sharedPreferences, Runnable runnable, Host host, View view) {
        if (iArr[0] == i) {
            return;
        }
        iArr[0] = i;
        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SELL_SCOPE, i).apply();
        runnable.run();
        host.rebuildQuickActionOverlayIfPresent();
    }

    private static void bindSwapCostFilter(View view, Host host) {
        final Context context = host.context();
        final SharedPreferences sharedPreferencesPreferences = host.preferences();
        final TextView textView = (TextView) view.findViewById(R.id.tvSwapCostSummary);
        int length = SWAP_COST_IDS.length;
        final TextView[] textViewArr = new TextView[length];
        for (int i = 0; i < length; i++) {
            textViewArr[i] = (TextView) view.findViewById(SWAP_COST_IDS[i]);
        }
        final int[] iArr = {SellRuleText.normalizeSwapCostFilter(sharedPreferencesPreferences.getInt(FloatingWindowPrefsKeys.KEY_CUSTOM_SWAP_COST_FILTER, 0))};
        final Runnable runnable = new Runnable() { // from class: com.android.support.SellSwapPageControlBinder$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SellSwapPageControlBinder.lambda$bindSwapCostFilter$7(textViewArr, context, iArr, textView);
            }
        };
        runnable.run();
        for (final int i2 = 0; i2 < length; i2++) {
            TextView textView2 = textViewArr[i2];
            if (textView2 != null) {
                textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SellSwapPageControlBinder$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        SellSwapPageControlBinder.lambda$bindSwapCostFilter$8(iArr, i2, sharedPreferencesPreferences, runnable, context, view2);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$bindSwapCostFilter$7(TextView[] textViewArr, Context context, int[] iArr, TextView textView) {
        int i = 0;
        while (i < textViewArr.length) {
            styleChip(context, textViewArr[i], CustomSwapActionChrome.COST_COLORS[i % CustomSwapActionChrome.COST_COLORS.length], iArr[0] == i);
            i++;
        }
        if (textView != null) {
            textView.setText("当前：" + SellRuleText.describeSwapCostFilter(iArr[0]) + "参与换位");
        }
    }

    static /* synthetic */ void lambda$bindSwapCostFilter$8(int[] iArr, int i, SharedPreferences sharedPreferences, Runnable runnable, Context context, View view) {
        if (iArr[0] == i) {
            return;
        }
        iArr[0] = i;
        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_CUSTOM_SWAP_COST_FILTER, i).apply();
        runnable.run();
        FloatingWindowService.safeToast(context, CustomSwapActionChrome.costFilterToast(i), 0);
    }

    private static void styleChip(Context context, TextView textView, int i, boolean z) {
        if (textView == null) {
            return;
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 8.0f));
        if (z) {
            gradientDrawable.setColor((16777215 & i) | (-872415232));
            gradientDrawable.setStroke(ScreenUtils.dp2px(context, 2.0f), i);
            textView.setTextColor(-1);
            textView.setShadowLayer(4.0f, 0.0f, 0.0f, i);
        } else {
            gradientDrawable.setColor(858993459);
            gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), 1149798536);
            textView.setTextColor(-2004318072);
            textView.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        }
        textView.setBackground(gradientDrawable);
    }
}
