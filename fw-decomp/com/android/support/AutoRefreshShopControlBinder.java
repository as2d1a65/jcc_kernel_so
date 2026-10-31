package com.android.support;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.support.diagnostics.TraceCatalog;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class AutoRefreshShopControlBinder {
    private static final String KEY_AUTO_REFRESH_FALLBACK_MS = "auto_refresh_fallback_ms";
    private static final String TAG = "FloatingWindowService";

    private AutoRefreshShopControlBinder() {
    }

    static void bind(View view, final SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, Consumer<Boolean> consumer, Consumer<Boolean> consumer2) {
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarRefreshFallback);
        final TextView textView = (TextView) view.findViewById(R.id.tvRefreshFallback);
        boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_AUTO_REFRESH_SHOP, false);
        int iNormalizeMs = AutoRefreshFallbackPolicy.normalizeMs(sharedPreferences.getInt("auto_refresh_fallback_ms", TraceCatalog.Feature.BOARD_HUD));
        consumer.accept(Boolean.valueOf(z));
        consumer2.accept(Boolean.valueOf(z));
        SharedMemManager sharedMemManager = supplier.get();
        if (sharedMemManager != null) {
            if (z) {
                sharedMemManager.setAutoRefreshFallbackMs(iNormalizeMs);
            }
            sharedMemManager.setAutoRefreshShop(z);
        }
        if (seekBar != null) {
            seekBar.setMax(38);
            seekBar.setProgress(AutoRefreshFallbackPolicy.progressFromMs(iNormalizeMs));
        }
        if (textView != null) {
            textView.setText(AutoRefreshFallbackPolicy.describeMs(iNormalizeMs));
        }
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.AutoRefreshShopControlBinder.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int i, boolean z2) {
                    TextView textView2 = textView;
                    if (textView2 != null) {
                        textView2.setText(AutoRefreshFallbackPolicy.describeMs(AutoRefreshFallbackPolicy.msFromProgress(i)));
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                    int iMsFromProgress = AutoRefreshFallbackPolicy.msFromProgress(seekBar2.getProgress());
                    SharedMemManager sharedMemManager2 = (SharedMemManager) supplier.get();
                    if (sharedMemManager2 != null) {
                        sharedMemManager2.setAutoRefreshFallbackMs(iMsFromProgress);
                    }
                    sharedPreferences.edit().putInt("auto_refresh_fallback_ms", iMsFromProgress).apply();
                    Dbg.d(AutoRefreshShopControlBinder.TAG, "配置已保存: 刷新保底=" + iMsFromProgress + "ms");
                }
            });
        }
    }
}
