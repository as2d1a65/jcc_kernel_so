package com.android.support;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.app.NotificationManagerCompat;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/* loaded from: classes.dex */
final class ShopDisplaySettingsControlBinder {
    private ShopDisplaySettingsControlBinder() {
    }

    static void bind(View view, final SharedPreferences sharedPreferences, final IntConsumer intConsumer, final IntConsumer intConsumer2, final IntConsumer intConsumer3, final IntSupplier intSupplier, final IntSupplier intSupplier2, final IntSupplier intSupplier3, final Runnable runnable) {
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarShopSlotX);
        final TextView textView = (TextView) view.findViewById(R.id.tvShopSlotX);
        if (seekBar != null) {
            int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_OFFSET_X, 0);
            intConsumer.accept(i);
            seekBar.setProgress(i + 1000);
            if (textView != null) {
                textView.setText(String.valueOf(i));
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.ShopDisplaySettingsControlBinder.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int i2, boolean z) {
                    intConsumer.accept(i2 + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
                    TextView textView2 = textView;
                    if (textView2 != null) {
                        textView2.setText(String.valueOf(intSupplier.getAsInt()));
                    }
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_OFFSET_X, intSupplier.getAsInt()).apply();
                    runnable.run();
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_OFFSET_X, intSupplier.getAsInt()).apply();
                }
            });
        }
        SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBarShopSlotY);
        final TextView textView2 = (TextView) view.findViewById(R.id.tvShopSlotY);
        if (seekBar2 != null) {
            int i2 = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_OFFSET_Y, 0);
            intConsumer2.accept(i2);
            seekBar2.setProgress(i2 + 1000);
            if (textView2 != null) {
                textView2.setText(String.valueOf(i2));
            }
            seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.ShopDisplaySettingsControlBinder.2
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar3) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar3, int i3, boolean z) {
                    intConsumer2.accept(i3 + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
                    TextView textView3 = textView2;
                    if (textView3 != null) {
                        textView3.setText(String.valueOf(intSupplier2.getAsInt()));
                    }
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_OFFSET_Y, intSupplier2.getAsInt()).apply();
                    runnable.run();
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar3) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_OFFSET_Y, intSupplier2.getAsInt()).apply();
                }
            });
        }
        SeekBar seekBar3 = (SeekBar) view.findViewById(R.id.seekBarShopSlotSpacing);
        final TextView textView3 = (TextView) view.findViewById(R.id.tvShopSlotSpacing);
        if (seekBar3 != null) {
            int i3 = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_SPACING, 0);
            seekBar3.setProgress(i3);
            if (textView3 != null) {
                textView3.setText(i3 == 0 ? "自动" : String.valueOf(i3));
            }
            seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.ShopDisplaySettingsControlBinder.3
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar4) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar4, int i4, boolean z) {
                    intConsumer3.accept(i4);
                    TextView textView4 = textView3;
                    if (textView4 != null) {
                        textView4.setText(i4 == 0 ? "自动" : String.valueOf(i4));
                    }
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_SPACING, i4).apply();
                    runnable.run();
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar4) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SHOP_SLOT_SPACING, intSupplier3.getAsInt()).apply();
                }
            });
        }
    }
}
