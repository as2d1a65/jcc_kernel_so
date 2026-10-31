package com.android.support;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class SelectedHeroScaleControlBinder {
    private static final String TAG = "FloatingWindowService";

    private SelectedHeroScaleControlBinder() {
    }

    static void bind(View view, final SharedPreferences sharedPreferences, final IntSupplier intSupplier, final IntConsumer intConsumer, final Consumer<Float> consumer, final float[] fArr, final Supplier<String> supplier, final Runnable runnable, final Runnable runnable2) {
        Button button = (Button) view.findViewById(R.id.btnScaleDown);
        Button button2 = (Button) view.findViewById(R.id.btnScaleUp);
        final TextView textView = (TextView) view.findViewById(R.id.tvScalePercent);
        if (button == null || button2 == null || textView == null) {
            return;
        }
        textView.setText(supplier.get());
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SelectedHeroScaleControlBinder$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                SelectedHeroScaleControlBinder.lambda$bind$0(intSupplier, intConsumer, fArr, consumer, textView, supplier, sharedPreferences, runnable, runnable2, view2);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SelectedHeroScaleControlBinder$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                SelectedHeroScaleControlBinder.lambda$bind$1(intSupplier, fArr, intConsumer, consumer, textView, supplier, sharedPreferences, runnable, runnable2, view2);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(IntSupplier intSupplier, IntConsumer intConsumer, float[] fArr, Consumer consumer, TextView textView, Supplier supplier, SharedPreferences sharedPreferences, Runnable runnable, Runnable runnable2, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt > 0) {
            int i = asInt - 1;
            intConsumer.accept(i);
            float f = fArr[i];
            consumer.accept(Float.valueOf(f));
            textView.setText((CharSequence) supplier.get());
            sharedPreferences.edit().putFloat(FloatingWindowPrefsKeys.KEY_SELECTED_HEROES_SCALE, f).apply();
            runnable.run();
            runnable2.run();
            Dbg.d(TAG, "选中英雄缩放: " + ((String) supplier.get()));
        }
    }

    static /* synthetic */ void lambda$bind$1(IntSupplier intSupplier, float[] fArr, IntConsumer intConsumer, Consumer consumer, TextView textView, Supplier supplier, SharedPreferences sharedPreferences, Runnable runnable, Runnable runnable2, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt < fArr.length - 1) {
            int i = asInt + 1;
            intConsumer.accept(i);
            float f = fArr[i];
            consumer.accept(Float.valueOf(f));
            textView.setText((CharSequence) supplier.get());
            sharedPreferences.edit().putFloat(FloatingWindowPrefsKeys.KEY_SELECTED_HEROES_SCALE, f).apply();
            runnable.run();
            runnable2.run();
            Dbg.d(TAG, "选中英雄缩放: " + ((String) supplier.get()));
        }
    }
}
