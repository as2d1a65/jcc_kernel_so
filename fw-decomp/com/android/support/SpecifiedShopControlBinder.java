package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.support.SharedMemManager;
import com.android.support.SpecifiedPredictionPreviewView;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class SpecifiedShopControlBinder {
    private static final int[] COST_CHIP_IDS = {R.id.specifiedShopCost1, R.id.specifiedShopCost2, R.id.specifiedShopCost3, R.id.specifiedShopCost4, R.id.specifiedShopCost5};
    private static final int[] COST_COLORS = {-6381922, GameHudPalette.SUCCESS, -14575885, -5552196, -26624};
    private static final int COST_MASK_ALL = 31;
    private static final int DOT_IDLE = -10851467;
    private static final int DOT_LIVE = -11740828;
    static final int GOLD_MAX = 100;
    private static final int GOLD_STEP = 10;
    private static final String STATUS_OFF = "未开启";
    private static final String STATUS_ON = "已开启 · 正在盯着商店";
    private static final String TAG = "FloatingWindowService";

    static int clampGold(int i) {
        if (i < 0) {
            return 0;
        }
        if (i > 100) {
            return 100;
        }
        return i;
    }

    static /* synthetic */ void lambda$bind$4(Boolean bool) {
    }

    private SpecifiedShopControlBinder() {
    }

    static SpecifiedPredictionPreviewView bindPreview(View view, Context context, SharedMemManager sharedMemManager, SpecifiedPredictionPreviewView.Presenter presenter) {
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.specifiedShopPreviewHost);
        if (frameLayout == null) {
            return null;
        }
        SpecifiedPredictionPreviewView specifiedPredictionPreviewView = new SpecifiedPredictionPreviewView(context, SpecifiedPredictionPreview.KIND_SHOP, presenter);
        frameLayout.addView(specifiedPredictionPreviewView, new FrameLayout.LayoutParams(-1, -1));
        specifiedPredictionPreviewView.bind(sharedMemManager);
        return specifiedPredictionPreviewView;
    }

    static boolean isEnabled(SharedPreferences sharedPreferences) {
        return sharedPreferences != null && sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_ENABLED, false);
    }

    static int reserveGold(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            return 0;
        }
        return clampGold(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_RESERVE_GOLD, 0));
    }

    static int costMask(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            return 31;
        }
        return sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_COST_MASK, 31) & 31;
    }

    static boolean affordOnly(SharedPreferences sharedPreferences) {
        return sharedPreferences == null || sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_AFFORD_ONLY, true);
    }

    static boolean useLineup(SharedPreferences sharedPreferences) {
        return sharedPreferences != null && sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_USE_LINEUP, false);
    }

    static boolean strict(SharedPreferences sharedPreferences) {
        return sharedPreferences != null && sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_STRICT, false);
    }

    static void bind(final View view, final Context context, final SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, final BiConsumer<String, Boolean> biConsumer, BiConsumer<String, Integer> biConsumer2) {
        View viewFindViewById = view.findViewById(R.id.cardSpecifiedShopMain);
        final Switch r8 = (Switch) view.findViewById(R.id.switchSpecifiedShop);
        if (viewFindViewById == null || r8 == null) {
            Log.w(TAG, "指定商店页未找到，跳过绑定");
            return;
        }
        final TextView textView = (TextView) view.findViewById(R.id.tvSpecifiedShopStatus);
        TextView textView2 = (TextView) view.findViewById(R.id.tvSpecifiedShopGold);
        View viewFindViewById2 = view.findViewById(R.id.btnSpecifiedShopGoldMinus);
        View viewFindViewById3 = view.findViewById(R.id.btnSpecifiedShopGoldPlus);
        boolean zIsEnabled = isEnabled(sharedPreferences);
        r8.setChecked(zIsEnabled);
        applyStatus(textView, zIsEnabled);
        pushEnabled(supplier, zIsEnabled);
        r8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda18
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SpecifiedShopControlBinder.lambda$bind$0(biConsumer, textView, supplier, view, sharedPreferences, context, compoundButton, z);
            }
        });
        viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda19
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                r8.toggle();
            }
        });
        OverlayUiMotion.installPressScale(viewFindViewById);
        refreshRuntime(view, sharedPreferences, supplier);
        SharedMemManager sharedMemManager = supplier == null ? null : supplier.get();
        if (sharedMemManager != null) {
            final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            sharedMemManager.setSpecifiedShopStatusListener(new SharedMemManager.SpecifiedShopStatusListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda20
                @Override // com.android.support.SharedMemManager.SpecifiedShopStatusListener
                public final void onSpecifiedShopStatus(SharedMemManager.SpecifiedShopStatus specifiedShopStatus) {
                    SpecifiedShopControlBinder.lambda$bind$3(atomicBoolean, view, sharedPreferences, supplier, specifiedShopStatus);
                }
            });
        }
        bindGold(context, sharedPreferences, supplier, biConsumer2, textView2, viewFindViewById2, viewFindViewById3);
        bindCostFilter(view, context, sharedPreferences, supplier, biConsumer2);
        bindLineupToggle(view, context, sharedPreferences, supplier, biConsumer);
        bindAffordToggle(view, context, sharedPreferences, supplier, biConsumer);
        bindStrictToggle(view, sharedPreferences, supplier, biConsumer);
        bindMoveToggle(view, R.id.switchSpecifiedShopAutoUncheck, R.id.rowSpecifiedShopAutoUncheck, FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_AUTO_UNCHECK, false, sharedPreferences, biConsumer, new Consumer() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda21
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SpecifiedShopControlBinder.lambda$bind$4((Boolean) obj);
            }
        });
        bindMoveToggle(view, R.id.switchSpecifiedShopRefreshOnly, R.id.rowSpecifiedShopRefreshOnly, FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_REFRESH_ONLY, false, sharedPreferences, biConsumer, new Consumer() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SpecifiedShopControlBinder.lambda$bind$5(supplier, (Boolean) obj);
            }
        });
        bindMoveToggle(view, R.id.switchSpecifiedMovePlanning, R.id.rowSpecifiedMovePlanning, FloatingWindowPrefsKeys.KEY_SPECIFIED_MOVE_PLANNING, true, sharedPreferences, biConsumer, new Consumer() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SpecifiedShopControlBinder.lambda$bind$6(supplier, (Boolean) obj);
            }
        });
        bindMoveToggle(view, R.id.switchSpecifiedMoveCombat, R.id.rowSpecifiedMoveCombat, FloatingWindowPrefsKeys.KEY_SPECIFIED_MOVE_COMBAT, true, sharedPreferences, biConsumer, new Consumer() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SpecifiedShopControlBinder.lambda$bind$7(supplier, (Boolean) obj);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(BiConsumer biConsumer, TextView textView, Supplier supplier, View view, SharedPreferences sharedPreferences, Context context, CompoundButton compoundButton, boolean z) {
        String str;
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_ENABLED, Boolean.valueOf(z));
        applyStatus(textView, z);
        pushEnabled(supplier, z);
        refreshRuntime(view, sharedPreferences, supplier);
        if (z) {
            str = "指定商店已开启，去右边勾选想要的棋子";
        } else {
            str = "指定商店已关闭";
        }
        FloatingWindowService.safeToast(context, str, 0);
    }

    static /* synthetic */ void lambda$bind$3(final AtomicBoolean atomicBoolean, final View view, final SharedPreferences sharedPreferences, final Supplier supplier, SharedMemManager.SpecifiedShopStatus specifiedShopStatus) {
        if (atomicBoolean.compareAndSet(false, true)) {
            view.postDelayed(new Runnable() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SpecifiedShopControlBinder.lambda$bind$2(atomicBoolean, view, sharedPreferences, supplier);
                }
            }, 300L);
        }
    }

    static /* synthetic */ void lambda$bind$2(AtomicBoolean atomicBoolean, View view, SharedPreferences sharedPreferences, Supplier supplier) {
        atomicBoolean.set(false);
        refreshRuntime(view, sharedPreferences, supplier);
    }

    static /* synthetic */ void lambda$bind$5(Supplier supplier, Boolean bool) {
        SharedMemManager sharedMemManager = supplier == null ? null : (SharedMemManager) supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setSpecifiedShopRefreshOnly(bool.booleanValue());
        }
    }

    static /* synthetic */ void lambda$bind$6(Supplier supplier, Boolean bool) {
        SharedMemManager sharedMemManager = supplier == null ? null : (SharedMemManager) supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setSpecifiedMovePlanning(bool.booleanValue());
        }
    }

    static /* synthetic */ void lambda$bind$7(Supplier supplier, Boolean bool) {
        SharedMemManager sharedMemManager = supplier == null ? null : (SharedMemManager) supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setSpecifiedMoveCombat(bool.booleanValue());
        }
    }

    private static void bindMoveToggle(View view, int i, int i2, final String str, boolean z, SharedPreferences sharedPreferences, final BiConsumer<String, Boolean> biConsumer, final Consumer<Boolean> consumer) {
        final Switch r1 = (Switch) view.findViewById(i);
        if (r1 == null) {
            return;
        }
        if (sharedPreferences != null) {
            z = sharedPreferences.getBoolean(str, z);
        }
        r1.setChecked(z);
        consumer.accept(Boolean.valueOf(z));
        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda4
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                SpecifiedShopControlBinder.lambda$bindMoveToggle$8(biConsumer, str, consumer, compoundButton, z2);
            }
        });
        View viewFindViewById = view.findViewById(i2);
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    r1.toggle();
                }
            });
            OverlayUiMotion.installPressScale(viewFindViewById);
        }
    }

    static /* synthetic */ void lambda$bindMoveToggle$8(BiConsumer biConsumer, String str, Consumer consumer, CompoundButton compoundButton, boolean z) {
        biConsumer.accept(str, Boolean.valueOf(z));
        consumer.accept(Boolean.valueOf(z));
    }

    private static void bindStrictToggle(View view, SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, final BiConsumer<String, Boolean> biConsumer) {
        final Switch r0 = (Switch) view.findViewById(R.id.switchSpecifiedShopStrict);
        if (r0 == null) {
            return;
        }
        boolean zStrict = strict(sharedPreferences);
        r0.setChecked(zStrict);
        final Consumer consumer = new Consumer() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda11
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SpecifiedShopControlBinder.lambda$bindStrictToggle$10(supplier, (Boolean) obj);
            }
        };
        consumer.accept(Boolean.valueOf(zStrict));
        r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda14
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SpecifiedShopControlBinder.lambda$bindStrictToggle$11(biConsumer, consumer, compoundButton, z);
            }
        });
        View viewFindViewById = view.findViewById(R.id.rowSpecifiedShopStrict);
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda15
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    r0.toggle();
                }
            });
            OverlayUiMotion.installPressScale(viewFindViewById);
        }
    }

    static /* synthetic */ void lambda$bindStrictToggle$10(Supplier supplier, Boolean bool) {
        SharedMemManager sharedMemManager = supplier == null ? null : (SharedMemManager) supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setSpecifiedShopStrict(bool.booleanValue());
        }
    }

    static /* synthetic */ void lambda$bindStrictToggle$11(BiConsumer biConsumer, Consumer consumer, CompoundButton compoundButton, boolean z) {
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_STRICT, Boolean.valueOf(z));
        consumer.accept(Boolean.valueOf(z));
    }

    private static void bindAffordToggle(View view, final Context context, SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, final BiConsumer<String, Boolean> biConsumer) {
        View viewFindViewById = view.findViewById(R.id.rowSpecifiedShopAfford);
        final Switch r1 = (Switch) view.findViewById(R.id.switchSpecifiedShopAfford);
        if (r1 == null) {
            return;
        }
        final TextView textView = (TextView) view.findViewById(R.id.tvSpecifiedShopAffordHint);
        boolean zAffordOnly = affordOnly(sharedPreferences);
        r1.setChecked(zAffordOnly);
        applyAffordHint(textView, zAffordOnly);
        pushAffordOnly(supplier, zAffordOnly);
        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda16
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SpecifiedShopControlBinder.lambda$bindAffordToggle$13(biConsumer, textView, supplier, context, compoundButton, z);
            }
        });
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda17
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    r1.toggle();
                }
            });
            OverlayUiMotion.installPressScale(viewFindViewById);
        }
    }

    static /* synthetic */ void lambda$bindAffordToggle$13(BiConsumer biConsumer, TextView textView, Supplier supplier, Context context, CompoundButton compoundButton, boolean z) {
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_AFFORD_ONLY, Boolean.valueOf(z));
        applyAffordHint(textView, z);
        pushAffordOnly(supplier, z);
        FloatingWindowService.safeToast(context, z ? "钱不够时会先攒着" : "不看金币，预测到就刷", 0);
    }

    private static void applyAffordHint(TextView textView, boolean z) {
        String str;
        if (textView == null) {
            return;
        }
        if (z) {
            str = "买不起就不刷，把钱留着（刷新还要 2 块）";
        } else {
            str = "不看金币 · 只要预测到就刷";
        }
        textView.setText(str);
    }

    private static void pushAffordOnly(Supplier<SharedMemManager> supplier, boolean z) {
        SharedMemManager sharedMemManager;
        if (supplier == null || (sharedMemManager = supplier.get()) == null) {
            return;
        }
        sharedMemManager.setSpecifiedShopAffordOnly(z);
    }

    private static void bindCostFilter(View view, final Context context, SharedPreferences sharedPreferences, Supplier<SharedMemManager> supplier, BiConsumer<String, Integer> biConsumer) {
        final Context context2;
        final Supplier<SharedMemManager> supplier2;
        final BiConsumer<String, Integer> biConsumer2;
        int length = COST_CHIP_IDS.length;
        final TextView[] textViewArr = new TextView[length];
        int i = 0;
        int i2 = 0;
        while (true) {
            int[] iArr = COST_CHIP_IDS;
            if (i2 >= iArr.length) {
                break;
            }
            textViewArr[i2] = (TextView) view.findViewById(iArr[i2]);
            i2++;
        }
        int iCostMask = costMask(sharedPreferences);
        final int[] iArr2 = {iCostMask};
        if (iCostMask == 0) {
            iArr2[0] = 31;
            biConsumer.accept(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_COST_MASK, 31);
        }
        final Runnable runnable = new Runnable() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                SpecifiedShopControlBinder.lambda$bindCostFilter$15(textViewArr, context, iArr2);
            }
        };
        runnable.run();
        pushCostMask(supplier, iArr2[0]);
        while (i < length) {
            TextView textView = textViewArr[i];
            if (textView == null) {
                context2 = context;
                supplier2 = supplier;
                biConsumer2 = biConsumer;
            } else {
                final int i3 = 1 << i;
                context2 = context;
                supplier2 = supplier;
                biConsumer2 = biConsumer;
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda13
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        SpecifiedShopControlBinder.lambda$bindCostFilter$16(iArr2, i3, context2, runnable, biConsumer2, supplier2, view2);
                    }
                });
            }
            i++;
            context = context2;
            biConsumer = biConsumer2;
            supplier = supplier2;
        }
    }

    static /* synthetic */ void lambda$bindCostFilter$15(TextView[] textViewArr, Context context, int[] iArr) {
        for (int i = 0; i < textViewArr.length; i++) {
            TextView textView = textViewArr[i];
            int i2 = COST_COLORS[i];
            boolean z = true;
            if ((iArr[0] & (1 << i)) == 0) {
                z = false;
            }
            styleChip(context, textView, i2, z);
        }
    }

    static /* synthetic */ void lambda$bindCostFilter$16(int[] iArr, int i, Context context, Runnable runnable, BiConsumer biConsumer, Supplier supplier, View view) {
        int i2 = iArr[0];
        boolean z = (i2 & i) != 0;
        if (z && ((~i) & i2 & 31) == 0) {
            FloatingWindowService.safeToast(context, "至少要留一个费用，否则没有棋子可预测", 0);
            return;
        }
        iArr[0] = (z ? (~i) & i2 : i | i2) & 31;
        runnable.run();
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_COST_MASK, Integer.valueOf(iArr[0]));
        pushCostMask(supplier, iArr[0]);
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

    private static void bindLineupToggle(View view, final Context context, SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, final BiConsumer<String, Boolean> biConsumer) {
        View viewFindViewById = view.findViewById(R.id.rowSpecifiedShopLineup);
        final Switch r1 = (Switch) view.findViewById(R.id.switchSpecifiedShopLineup);
        if (r1 == null) {
            return;
        }
        final TextView textView = (TextView) view.findViewById(R.id.tvSpecifiedShopLineupHint);
        boolean zUseLineup = useLineup(sharedPreferences);
        r1.setChecked(zUseLineup);
        applyLineupHint(textView, zUseLineup);
        pushUseLineup(supplier, zUseLineup);
        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda6
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SpecifiedShopControlBinder.lambda$bindLineupToggle$17(biConsumer, textView, supplier, context, compoundButton, z);
            }
        });
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    r1.toggle();
                }
            });
            OverlayUiMotion.installPressScale(viewFindViewById);
        }
    }

    static /* synthetic */ void lambda$bindLineupToggle$17(BiConsumer biConsumer, TextView textView, Supplier supplier, Context context, CompoundButton compoundButton, boolean z) {
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_USE_LINEUP, Boolean.valueOf(z));
        applyLineupHint(textView, z);
        pushUseLineup(supplier, z);
        FloatingWindowService.safeToast(context, z ? "阵容里的棋子也一起预测" : "只预测你手动勾选的棋子", 0);
    }

    static void refreshRuntime(View view, SharedPreferences sharedPreferences, Supplier<SharedMemManager> supplier) {
        if (view == null || view.findViewById(R.id.tvSpecifiedShopRuntime) == null) {
            return;
        }
        SharedMemManager sharedMemManager = supplier == null ? null : supplier.get();
        applyRuntime(view, isEnabled(sharedPreferences), sharedMemManager != null ? sharedMemManager.getSpecifiedShopStatus() : null);
    }

    private static void applyLineupHint(TextView textView, boolean z) {
        String str;
        if (textView == null) {
            return;
        }
        if (z) {
            str = "阵容棋盘里选的棋子也一起预测";
        } else {
            str = "只预测「勾选拿牌」里手动选的棋子";
        }
        textView.setText(str);
    }

    private static void bindGold(final Context context, SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, final BiConsumer<String, Integer> biConsumer, final TextView textView, View view, View view2) {
        if (textView == null) {
            return;
        }
        final int[] iArr = {reserveGold(sharedPreferences)};
        textView.setText(String.valueOf(iArr[0]));
        pushGold(supplier, iArr[0]);
        final Runnable runnable = new Runnable() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                SpecifiedShopControlBinder.lambda$bindGold$19(textView, iArr, biConsumer, supplier);
            }
        };
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda9
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    SpecifiedShopControlBinder.lambda$bindGold$20(iArr, context, runnable, view3);
                }
            });
            OverlayUiMotion.installPressScale(view);
        }
        if (view2 != null) {
            view2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedShopControlBinder$$ExternalSyntheticLambda10
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    SpecifiedShopControlBinder.lambda$bindGold$21(iArr, context, runnable, view3);
                }
            });
            OverlayUiMotion.installPressScale(view2);
        }
    }

    static /* synthetic */ void lambda$bindGold$19(TextView textView, int[] iArr, BiConsumer biConsumer, Supplier supplier) {
        textView.setText(String.valueOf(iArr[0]));
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_SPECIFIED_SHOP_RESERVE_GOLD, Integer.valueOf(iArr[0]));
        pushGold(supplier, iArr[0]);
    }

    static /* synthetic */ void lambda$bindGold$20(int[] iArr, Context context, Runnable runnable, View view) {
        int i = iArr[0];
        if (i <= 0) {
            FloatingWindowService.safeToast(context, "已经是不限制了", 0);
        } else {
            iArr[0] = clampGold(i - 10);
            runnable.run();
        }
    }

    static /* synthetic */ void lambda$bindGold$21(int[] iArr, Context context, Runnable runnable, View view) {
        int i = iArr[0];
        if (i >= 100) {
            FloatingWindowService.safeToast(context, "最多保留 100 金币", 0);
        } else {
            iArr[0] = clampGold(i + 10);
            runnable.run();
        }
    }

    private static void pushEnabled(Supplier<SharedMemManager> supplier, boolean z) {
        SharedMemManager sharedMemManager;
        if (supplier == null || (sharedMemManager = supplier.get()) == null) {
            return;
        }
        sharedMemManager.setSpecifiedShop(z);
    }

    private static void pushGold(Supplier<SharedMemManager> supplier, int i) {
        SharedMemManager sharedMemManager;
        if (supplier == null || (sharedMemManager = supplier.get()) == null) {
            return;
        }
        sharedMemManager.setSpecifiedShopReserveGold(i);
    }

    private static void pushCostMask(Supplier<SharedMemManager> supplier, int i) {
        SharedMemManager sharedMemManager;
        if (supplier == null || (sharedMemManager = supplier.get()) == null) {
            return;
        }
        sharedMemManager.setSpecifiedShopCostMask(i);
    }

    private static void pushUseLineup(Supplier<SharedMemManager> supplier, boolean z) {
        SharedMemManager sharedMemManager;
        if (supplier == null || (sharedMemManager = supplier.get()) == null) {
            return;
        }
        sharedMemManager.setSpecifiedShopUseLineup(z);
    }

    private static void applyStatus(TextView textView, boolean z) {
        if (textView == null) {
            return;
        }
        textView.setText(z ? STATUS_ON : STATUS_OFF);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0030  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void applyRuntime(android.view.View r9, boolean r10, com.android.support.SharedMemManager.SpecifiedShopStatus r11) {
        /*
            Method dump skipped, instructions count: 277
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.SpecifiedShopControlBinder.applyRuntime(android.view.View, boolean, com.android.support.SharedMemManager$SpecifiedShopStatus):void");
    }
}
