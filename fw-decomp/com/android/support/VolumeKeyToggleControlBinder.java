package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class VolumeKeyToggleControlBinder {
    private VolumeKeyToggleControlBinder() {
    }

    static void bind(View view, final Context context, final SharedPreferences sharedPreferences, final Consumer<Boolean> consumer, final Consumer<Boolean> consumer2, final Supplier<View> supplier, final BooleanSupplier booleanSupplier, final BiConsumer<View, Boolean> biConsumer, final Runnable runnable) {
        Switch r11 = (Switch) view.findViewById(R.id.switchVolumeToggle);
        if (r11 == null) {
            return;
        }
        boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_VOLUME_TOGGLE_ENABLED, false);
        consumer.accept(Boolean.valueOf(z));
        r11.setChecked(z);
        r11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.VolumeKeyToggleControlBinder$$ExternalSyntheticLambda0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                VolumeKeyToggleControlBinder.lambda$bind$0(consumer, sharedPreferences, consumer2, supplier, booleanSupplier, biConsumer, runnable, context, compoundButton, z2);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(Consumer consumer, SharedPreferences sharedPreferences, Consumer consumer2, Supplier supplier, BooleanSupplier booleanSupplier, BiConsumer biConsumer, Runnable runnable, Context context, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_VOLUME_TOGGLE_ENABLED, z).apply();
        if (z) {
            consumer2.accept(true);
            sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_SELECTED_HEROES_VISIBLE, true).apply();
            View view = (View) supplier.get();
            if (view != null && booleanSupplier.getAsBoolean()) {
                biConsumer.accept(view, true);
                runnable.run();
            }
            FloatingWindowService.safeToast(context, "音量键切换牌库显隐 已开启", 0);
            return;
        }
        FloatingWindowService.safeToast(context, "音量键切换牌库显隐 已关闭", 0);
    }
}
