package com.android.support;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import java.util.function.Consumer;

/* loaded from: classes.dex */
final class BenchHideControlBinder {
    private BenchHideControlBinder() {
    }

    static void bind(View view, final SharedPreferences sharedPreferences, final ChessBoardView chessBoardView, final Consumer<Boolean> consumer, final Runnable runnable) {
        Switch r2 = (Switch) view.findViewById(R.id.switchBoardBenchHidden);
        if (r2 == null) {
            return;
        }
        boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_BOARD_BENCH_HIDDEN, false);
        r2.setChecked(z);
        consumer.accept(Boolean.valueOf(z));
        if (chessBoardView != null) {
            chessBoardView.setBenchHidden(z);
        }
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.BenchHideControlBinder$$ExternalSyntheticLambda0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                BenchHideControlBinder.lambda$bind$0(consumer, sharedPreferences, chessBoardView, runnable, compoundButton, z2);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(Consumer consumer, SharedPreferences sharedPreferences, ChessBoardView chessBoardView, Runnable runnable, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_BOARD_BENCH_HIDDEN, z).apply();
        if (chessBoardView != null) {
            chessBoardView.setBenchHidden(z);
            runnable.run();
        }
    }
}
