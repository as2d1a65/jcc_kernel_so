package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class AutoPickCostFilterControlBinder {
    private AutoPickCostFilterControlBinder() {
    }

    static void bind(View view, final Context context, final SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, IntSupplier intSupplier, IntConsumer intConsumer, final IntFunction<String> intFunction) {
        int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_AUTO_PICK_COST_MASK, 31) & 31;
        int i2 = i != 0 ? i : 31;
        final IntConsumer intConsumer2 = intConsumer;
        intConsumer2.accept(i2);
        SharedMemManager sharedMemManager = supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setAutoPickCostMask(i2);
        }
        int[] iArr = {R.id.btnAutoPickCost1, R.id.btnAutoPickCost2, R.id.btnAutoPickCost3, R.id.btnAutoPickCost4, R.id.btnAutoPickCost5};
        final int[] iArr2 = {-6381922, GameHudPalette.SUCCESS, -14575885, -5552196, -26624};
        final TextView[] textViewArr = new TextView[5];
        for (int i3 = 0; i3 < 5; i3++) {
            textViewArr[i3] = (TextView) view.findViewById(iArr[i3]);
        }
        final IntSupplier intSupplier2 = intSupplier;
        final Runnable runnable = new Runnable() { // from class: com.android.support.AutoPickCostFilterControlBinder$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AutoPickCostFilterControlBinder.lambda$bind$0(intSupplier2, textViewArr, context, iArr2);
            }
        };
        runnable.run();
        final int i4 = 0;
        while (i4 < 5) {
            TextView textView = textViewArr[i4];
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AutoPickCostFilterControlBinder$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        AutoPickCostFilterControlBinder.lambda$bind$1(intSupplier2, i4, intConsumer2, context, sharedPreferences, supplier, intFunction, runnable, view2);
                    }
                });
            }
            i4++;
            intSupplier2 = intSupplier;
            intConsumer2 = intConsumer;
        }
    }

    static /* synthetic */ void lambda$bind$0(IntSupplier intSupplier, TextView[] textViewArr, Context context, int[] iArr) {
        int asInt = intSupplier.getAsInt();
        for (int i = 0; i < 5; i++) {
            if (textViewArr[i] != null) {
                boolean z = ((1 << i) & asInt) != 0;
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(0);
                gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 8.0f));
                if (z) {
                    gradientDrawable.setColor((iArr[i] & ViewCompat.MEASURED_SIZE_MASK) | (-872415232));
                    gradientDrawable.setStroke(ScreenUtils.dp2px(context, 2.0f), iArr[i]);
                    textViewArr[i].setTextColor(-1);
                    textViewArr[i].setShadowLayer(4.0f, 0.0f, 0.0f, iArr[i]);
                } else {
                    gradientDrawable.setColor(858993459);
                    gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), 1149798536);
                    textViewArr[i].setTextColor(-2004318072);
                    textViewArr[i].setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                }
                textViewArr[i].setBackground(gradientDrawable);
            }
        }
    }

    static /* synthetic */ void lambda$bind$1(IntSupplier intSupplier, int i, IntConsumer intConsumer, Context context, SharedPreferences sharedPreferences, Supplier supplier, IntFunction intFunction, Runnable runnable, View view) {
        int i2 = 1 << i;
        int asInt = intSupplier.getAsInt() ^ i2;
        if (asInt == 0) {
            intConsumer.accept(i2);
            FloatingWindowService.safeToast(context, "至少选择一个费用", 0);
            return;
        }
        intConsumer.accept(asInt);
        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_AUTO_PICK_COST_MASK, asInt).apply();
        SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setAutoPickCostMask(asInt);
        }
        FloatingWindowService.safeToast(context, "拿牌费用: " + ((String) intFunction.apply(asInt)), 0);
        runnable.run();
    }
}
