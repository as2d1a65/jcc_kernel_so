package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
final class AllInPageControlBinder {

    interface Host {
        boolean allInRequestPending();

        boolean allInRequested();

        void animateButtonClick(View view);

        Context context();

        boolean isMatchActive();

        void onChaseSlotsChanged();

        SharedPreferences preferences();
    }

    static /* synthetic */ void lambda$bindSwitch$15() {
    }

    private AllInPageControlBinder() {
    }

    static final class Views {
        private final Runnable reload;
        final TextView status;

        Views(TextView textView, Runnable runnable) {
            this.status = textView;
            this.reload = runnable;
        }
    }

    static boolean isConfigKey(String str) {
        if (str == null) {
            return false;
        }
        return FloatingWindowPrefsKeys.KEY_ALL_IN_MAX_TARGETS.equals(str) || FloatingWindowPrefsKeys.KEY_ALL_IN_RESERVE_GOLD.equals(str) || FloatingWindowPrefsKeys.KEY_ALL_IN_ACK_TIMEOUT_MS.equals(str) || FloatingWindowPrefsKeys.KEY_ALL_IN_BLOCKED_ACTION.equals(str) || FloatingWindowPrefsKeys.KEY_ALL_IN_COST_MASK.equals(str) || FloatingWindowPrefsKeys.KEY_ALL_IN_ANTI_MISTOUCH.equals(str) || FloatingWindowPrefsKeys.KEY_ALL_IN_KEEP_LINEUP.equals(str);
    }

    static void reload(Views views) {
        if (views == null || views.reload == null) {
            return;
        }
        views.reload.run();
    }

    static Views bind(View view, Host host) {
        final TextView textView = (TextView) view.findViewById(R.id.tvAllInSummary);
        final SharedPreferences sharedPreferencesPreferences = host.preferences();
        Runnable runnable = new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                AllInPageControlBinder.lambda$bind$0(textView, sharedPreferencesPreferences);
            }
        };
        final ArrayList arrayList = new ArrayList();
        arrayList.add(bindCostFilter(view, host, runnable));
        arrayList.addAll(bindSteppers(view, host, runnable));
        arrayList.add(bindBlockedAction(view, host, runnable));
        arrayList.addAll(bindSafetySwitches(view, host));
        arrayList.add(runnable);
        Runnable runnable2 = new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                AllInPageControlBinder.lambda$bind$1(arrayList);
            }
        };
        runnable2.run();
        Views views = new Views((TextView) view.findViewById(R.id.tvAllInStatus), runnable2);
        render(host, views);
        return views;
    }

    static /* synthetic */ void lambda$bind$0(TextView textView, SharedPreferences sharedPreferences) {
        if (textView == null) {
            return;
        }
        textView.setText(AllInRuleText.summary(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_ALL_IN_MAX_TARGETS, 3), sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_ALL_IN_RESERVE_GOLD, 0), sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_ALL_IN_BLOCKED_ACTION, 0), sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_ALL_IN_COST_MASK, 16)));
    }

    static /* synthetic */ void lambda$bind$1(List list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
    }

    static void render(Host host, Views views) {
        if (views == null || views.status == null) {
            return;
        }
        views.status.setText(AllInRuleText.statusLine(host.allInRequested(), host.allInRequestPending(), host.isMatchActive()));
    }

    private static List<Runnable> bindSteppers(View view, final Host host, final Runnable runnable) {
        SharedPreferences sharedPreferencesPreferences = host.preferences();
        ArrayList arrayList = new ArrayList();
        arrayList.add(bindStepper(view, R.id.btnAllInTargetsMinus, R.id.btnAllInTargetsPlus, R.id.tvAllInMaxTargets, FloatingWindowPrefsKeys.KEY_ALL_IN_MAX_TARGETS, 3, 1, 9, 1, sharedPreferencesPreferences, host, new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                AllInPageControlBinder.lambda$bindSteppers$2(runnable, host);
            }
        }));
        arrayList.add(bindStepper(view, R.id.btnAllInReserveMinus, R.id.btnAllInReservePlus, R.id.tvAllInReserveGold, FloatingWindowPrefsKeys.KEY_ALL_IN_RESERVE_GOLD, 0, 0, 50, 10, sharedPreferencesPreferences, host, runnable));
        arrayList.add(bindStepper(view, R.id.btnAllInAckMinus, R.id.btnAllInAckPlus, R.id.tvAllInAckTimeout, FloatingWindowPrefsKeys.KEY_ALL_IN_ACK_TIMEOUT_MS, 3000, 2000, 6000, 500, sharedPreferencesPreferences, host, runnable));
        return arrayList;
    }

    static /* synthetic */ void lambda$bindSteppers$2(Runnable runnable, Host host) {
        runnable.run();
        host.onChaseSlotsChanged();
    }

    private static Runnable bindStepper(View view, int i, int i2, int i3, final String str, final int i4, final int i5, final int i6, final int i7, final SharedPreferences sharedPreferences, final Host host, final Runnable runnable) {
        final TextView textView = (TextView) view.findViewById(i3);
        View viewFindViewById = view.findViewById(i);
        View viewFindViewById2 = view.findViewById(i2);
        final Runnable runnable2 = new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                AllInPageControlBinder.lambda$bindStepper$3(textView, sharedPreferences, str, i4, i5, i6);
            }
        };
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda13
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    AllInPageControlBinder.lambda$bindStepper$4(sharedPreferences, str, i4, i5, i6, host, i7, runnable2, runnable, view2);
                }
            });
        }
        if (viewFindViewById2 != null) {
            viewFindViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda14
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    AllInPageControlBinder.lambda$bindStepper$5(sharedPreferences, str, i4, i5, i6, host, i7, runnable2, runnable, view2);
                }
            });
        }
        return runnable2;
    }

    static /* synthetic */ void lambda$bindStepper$3(TextView textView, SharedPreferences sharedPreferences, String str, int i, int i2, int i3) {
        if (textView == null) {
            return;
        }
        textView.setText(String.valueOf(stored(sharedPreferences, str, i, i2, i3)));
    }

    static /* synthetic */ void lambda$bindStepper$4(SharedPreferences sharedPreferences, String str, int i, int i2, int i3, Host host, int i4, Runnable runnable, Runnable runnable2, View view) {
        int iStored = stored(sharedPreferences, str, i, i2, i3);
        if (iStored <= i2) {
            return;
        }
        host.animateButtonClick(view);
        sharedPreferences.edit().putInt(str, clamp(iStored - i4, i2, i3)).apply();
        runnable.run();
        runnable2.run();
    }

    static /* synthetic */ void lambda$bindStepper$5(SharedPreferences sharedPreferences, String str, int i, int i2, int i3, Host host, int i4, Runnable runnable, Runnable runnable2, View view) {
        int iStored = stored(sharedPreferences, str, i, i2, i3);
        if (iStored >= i3) {
            return;
        }
        host.animateButtonClick(view);
        sharedPreferences.edit().putInt(str, clamp(iStored + i4, i2, i3)).apply();
        runnable.run();
        runnable2.run();
    }

    private static int stored(SharedPreferences sharedPreferences, String str, int i, int i2, int i3) {
        return clamp(sharedPreferences.getInt(str, i), i2, i3);
    }

    private static Runnable bindCostFilter(View view, final Host host, final Runnable runnable) {
        final Context context = host.context();
        final SharedPreferences sharedPreferencesPreferences = host.preferences();
        final TextView textView = (TextView) view.findViewById(R.id.btnAllInCostFive);
        final TextView textView2 = (TextView) view.findViewById(R.id.btnAllInCostFour);
        final TextView textView3 = (TextView) view.findViewById(R.id.tvAllInCostHint);
        final Runnable runnable2 = new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AllInPageControlBinder.lambda$bindCostFilter$6(sharedPreferencesPreferences, context, textView, textView2, textView3);
            }
        };
        if (textView != null) {
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    AllInPageControlBinder.lambda$bindCostFilter$7(host, sharedPreferencesPreferences, runnable2, runnable, view2);
                }
            });
        }
        if (textView2 != null) {
            textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    AllInPageControlBinder.lambda$bindCostFilter$8(host, sharedPreferencesPreferences, runnable2, runnable, view2);
                }
            });
        }
        return runnable2;
    }

    static /* synthetic */ void lambda$bindCostFilter$6(SharedPreferences sharedPreferences, Context context, TextView textView, TextView textView2, TextView textView3) {
        String str;
        int iStoredCostMask = storedCostMask(sharedPreferences);
        boolean z = (iStoredCostMask & 16) != 0;
        boolean z2 = (iStoredCostMask & 8) != 0;
        styleChip(context, textView, -15770, z);
        styleChip(context, textView2, -15770, z2);
        if (textView3 != null) {
            if (z2 && z) {
                str = "四费和五费都追，按优先级/张数排序";
            } else if (z2) {
                str = "只追四费（池子大，更容易凑三星）";
            } else {
                str = "只追五费（默认）";
            }
            textView3.setText(str);
        }
    }

    static /* synthetic */ void lambda$bindCostFilter$7(Host host, SharedPreferences sharedPreferences, Runnable runnable, Runnable runnable2, View view) {
        host.animateButtonClick(view);
        toggleCostBit(host, sharedPreferences, 16, runnable, runnable2);
    }

    static /* synthetic */ void lambda$bindCostFilter$8(Host host, SharedPreferences sharedPreferences, Runnable runnable, Runnable runnable2, View view) {
        host.animateButtonClick(view);
        toggleCostBit(host, sharedPreferences, 8, runnable, runnable2);
    }

    private static void toggleCostBit(Host host, SharedPreferences sharedPreferences, int i, Runnable runnable, Runnable runnable2) {
        int iStoredCostMask = i ^ storedCostMask(sharedPreferences);
        if ((iStoredCostMask & 24) == 0) {
            Toast.makeText(host.context(), "至少选一个费用", 0).show();
            return;
        }
        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_ALL_IN_COST_MASK, QuickActionOverlayPolicy.normalizeAllInCostMask(iStoredCostMask)).apply();
        runnable.run();
        runnable2.run();
    }

    private static int storedCostMask(SharedPreferences sharedPreferences) {
        return QuickActionOverlayPolicy.normalizeAllInCostMask(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_ALL_IN_COST_MASK, 16));
    }

    private static Runnable bindBlockedAction(View view, Host host, final Runnable runnable) {
        final Context context = host.context();
        final SharedPreferences sharedPreferencesPreferences = host.preferences();
        final TextView textView = (TextView) view.findViewById(R.id.btnAllInBlockedSell);
        final TextView textView2 = (TextView) view.findViewById(R.id.btnAllInBlockedStop);
        final Runnable runnable2 = new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                AllInPageControlBinder.lambda$bindBlockedAction$9(sharedPreferencesPreferences, context, textView, textView2);
            }
        };
        if (textView != null) {
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    AllInPageControlBinder.selectBlockedAction(sharedPreferencesPreferences, 0, runnable2, runnable);
                }
            });
        }
        if (textView2 != null) {
            textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    AllInPageControlBinder.selectBlockedAction(sharedPreferencesPreferences, 1, runnable2, runnable);
                }
            });
        }
        return runnable2;
    }

    static /* synthetic */ void lambda$bindBlockedAction$9(SharedPreferences sharedPreferences, Context context, TextView textView, TextView textView2) {
        int iStoredBlockedAction = storedBlockedAction(sharedPreferences);
        styleChip(context, textView, -15770, iStoredBlockedAction == 0);
        styleChip(context, textView2, GameHudPalette.DANGER, iStoredBlockedAction == 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void selectBlockedAction(SharedPreferences sharedPreferences, int i, Runnable runnable, Runnable runnable2) {
        if (storedBlockedAction(sharedPreferences) == i) {
            return;
        }
        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_ALL_IN_BLOCKED_ACTION, i).apply();
        runnable.run();
        runnable2.run();
    }

    private static int storedBlockedAction(SharedPreferences sharedPreferences) {
        return QuickActionOverlayPolicy.normalizeAllInBlockedAction(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_ALL_IN_BLOCKED_ACTION, 0));
    }

    private static List<Runnable> bindSafetySwitches(View view, Host host) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(bindSwitch(view, R.id.switchAllInAntiMistouch, FloatingWindowPrefsKeys.KEY_ALL_IN_ANTI_MISTOUCH, host.preferences(), false));
        arrayList.add(bindKeepLineup(view, host.preferences()));
        return arrayList;
    }

    private static Runnable bindKeepLineup(View view, final SharedPreferences sharedPreferences) {
        final Switch r0 = (Switch) view.findViewById(R.id.switchAllInKeepLineup);
        final TextView textView = (TextView) view.findViewById(R.id.tvAllInKeepLineupHint);
        final Runnable runnable = new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AllInPageControlBinder.lambda$bindKeepLineup$12(textView, sharedPreferences);
            }
        };
        if (r0 != null) {
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda9
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    AllInPageControlBinder.lambda$bindKeepLineup$13(sharedPreferences, runnable, compoundButton, z);
                }
            });
        }
        return new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                AllInPageControlBinder.lambda$bindKeepLineup$14(r0, sharedPreferences, runnable);
            }
        };
    }

    static /* synthetic */ void lambda$bindKeepLineup$12(TextView textView, SharedPreferences sharedPreferences) {
        if (textView == null) {
            return;
        }
        textView.setText(storedKeepLineup(sharedPreferences) ? "清场时只卖阵容外的牌" : "⚠ 阵容棋子也会被卖掉换钱");
    }

    static /* synthetic */ void lambda$bindKeepLineup$13(SharedPreferences sharedPreferences, Runnable runnable, CompoundButton compoundButton, boolean z) {
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_ALL_IN_KEEP_LINEUP, z).apply();
        runnable.run();
    }

    static /* synthetic */ void lambda$bindKeepLineup$14(Switch r0, SharedPreferences sharedPreferences, Runnable runnable) {
        if (r0 != null) {
            r0.setChecked(storedKeepLineup(sharedPreferences));
        }
        runnable.run();
    }

    private static boolean storedKeepLineup(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_ALL_IN_KEEP_LINEUP, true);
    }

    private static Runnable bindSwitch(View view, int i, final String str, final SharedPreferences sharedPreferences, final boolean z) {
        final Switch r0 = (Switch) view.findViewById(i);
        if (r0 == null) {
            return new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    AllInPageControlBinder.lambda$bindSwitch$15();
                }
            };
        }
        r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda7
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                sharedPreferences.edit().putBoolean(str, z2).apply();
            }
        });
        return new Runnable() { // from class: com.android.support.AllInPageControlBinder$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                r0.setChecked(sharedPreferences.getBoolean(str, z));
            }
        };
    }

    private static int clamp(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i3, i));
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
