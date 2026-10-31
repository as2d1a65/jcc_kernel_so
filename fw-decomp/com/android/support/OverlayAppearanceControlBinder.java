package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/* loaded from: classes.dex */
final class OverlayAppearanceControlBinder {
    private static final String KEY_SHOW_HERO_AVATAR = "show_hero_avatar";
    private static final String TAG = "FloatingWindowService";

    private OverlayAppearanceControlBinder() {
    }

    static void bind(View view, final Context context, final SharedPreferences sharedPreferences, final Consumer<Boolean> consumer, final BiConsumer<String, Boolean> biConsumer, final Consumer<Boolean> consumer2, final Consumer<Float> consumer3, final Consumer<Boolean> consumer4, final Runnable runnable) {
        Switch r0 = (Switch) view.findViewById(R.id.switchHardwareAcceleration);
        if (r0 != null) {
            boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_HARDWARE_ACCELERATION, true);
            r0.setChecked(z);
            consumer.accept(Boolean.valueOf(z));
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.OverlayAppearanceControlBinder$$ExternalSyntheticLambda0
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    OverlayAppearanceControlBinder.lambda$bind$0(consumer, biConsumer, consumer2, context, compoundButton, z2);
                }
            });
        }
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarOverlayAlpha);
        final TextView textView = (TextView) view.findViewById(R.id.tvOverlayAlpha);
        if (seekBar != null) {
            int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_OVERLAY_ALPHA, 60);
            seekBar.setProgress(i);
            if (textView != null) {
                textView.setText(i + "%");
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OverlayAppearanceControlBinder.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int i2, boolean z2) {
                    if (z2) {
                        int iMax = Math.max(20, i2);
                        TextView textView2 = textView;
                        if (textView2 != null) {
                            textView2.setText(iMax + "%");
                        }
                        consumer3.accept(Float.valueOf(iMax / 100.0f));
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                    int iMax = Math.max(20, seekBar2.getProgress());
                    if (seekBar2.getProgress() != iMax) {
                        seekBar2.setProgress(iMax);
                    }
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_OVERLAY_ALPHA, iMax).apply();
                }
            });
        }
        Switch r3 = (Switch) view.findViewById(R.id.switchShowHeroAvatar);
        if (r3 != null) {
            boolean z2 = sharedPreferences.getBoolean("show_hero_avatar", true);
            r3.setChecked(z2);
            consumer4.accept(Boolean.valueOf(z2));
            Dbg.d(TAG, "加载保存的配置: 英雄头像=" + z2);
            r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.OverlayAppearanceControlBinder$$ExternalSyntheticLambda1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z3) {
                    OverlayAppearanceControlBinder.lambda$bind$1(consumer4, sharedPreferences, runnable, context, compoundButton, z3);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$0(Consumer consumer, BiConsumer biConsumer, Consumer consumer2, Context context, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_HARDWARE_ACCELERATION, Boolean.valueOf(z));
        consumer2.accept(Boolean.valueOf(z));
        FloatingWindowService.safeToast(context, z ? "硬件加速已开启" : "硬件加速已关闭(需重新打开子窗口生效)", 0);
    }

    static /* synthetic */ void lambda$bind$1(Consumer consumer, SharedPreferences sharedPreferences, Runnable runnable, Context context, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        sharedPreferences.edit().putBoolean("show_hero_avatar", z).apply();
        Dbg.d(TAG, "配置已保存: 英雄头像=" + z);
        runnable.run();
        FloatingWindowService.safeToast(context, z ? "✅ 已切换为头像显示" : "✅ 已切换为名字显示", 0);
    }
}
