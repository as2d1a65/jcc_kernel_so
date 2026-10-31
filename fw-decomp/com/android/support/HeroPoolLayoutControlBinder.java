package com.android.support;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/* loaded from: classes.dex */
final class HeroPoolLayoutControlBinder {
    private static final String TAG = "FloatingWindowService";

    private HeroPoolLayoutControlBinder() {
    }

    static void bind(View view, final SharedPreferences sharedPreferences, final IntSupplier intSupplier, final IntConsumer intConsumer, final IntSupplier intSupplier2, final IntConsumer intConsumer2, final IntSupplier intSupplier3, final IntConsumer intConsumer3, final Runnable runnable, final Runnable runnable2) {
        final Button button = (Button) view.findViewById(R.id.btnLayoutHorizontal);
        final Button button2 = (Button) view.findViewById(R.id.btnLayoutVertical);
        Button button3 = (Button) view.findViewById(R.id.btnColumnsDown);
        Button button4 = (Button) view.findViewById(R.id.btnColumnsUp);
        final TextView textView = (TextView) view.findViewById(R.id.tvColumnsValue);
        final TextView textView2 = (TextView) view.findViewById(R.id.tvColumnsLabel);
        Button button5 = (Button) view.findViewById(R.id.btnRowsDown);
        Button button6 = (Button) view.findViewById(R.id.btnRowsUp);
        final TextView textView3 = (TextView) view.findViewById(R.id.tvRowsValue);
        final TextView textView4 = (TextView) view.findViewById(R.id.tvRowsLabel);
        if (button == null || button2 == null || button3 == null || button4 == null || textView == null || button5 == null || button6 == null || textView3 == null) {
            return;
        }
        final Runnable runnable3 = new Runnable() { // from class: com.android.support.HeroPoolLayoutControlBinder$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HeroPoolLayoutControlBinder.lambda$bind$0(intSupplier, button, button2, textView2, textView4, textView, intSupplier2, intSupplier3, textView3);
            }
        };
        runnable3.run();
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroPoolLayoutControlBinder$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                HeroPoolLayoutControlBinder.lambda$bind$1(intSupplier, intConsumer, sharedPreferences, runnable, runnable3, runnable2, view2);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroPoolLayoutControlBinder$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                HeroPoolLayoutControlBinder.lambda$bind$2(intSupplier, intConsumer, sharedPreferences, runnable, runnable3, runnable2, view2);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroPoolLayoutControlBinder$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                HeroPoolLayoutControlBinder.lambda$bind$3(intSupplier2, intConsumer2, sharedPreferences, textView, runnable, runnable2, view2);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroPoolLayoutControlBinder$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                HeroPoolLayoutControlBinder.lambda$bind$4(intSupplier2, intConsumer2, sharedPreferences, textView, runnable, runnable2, view2);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroPoolLayoutControlBinder$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                HeroPoolLayoutControlBinder.lambda$bind$5(intSupplier3, intConsumer3, sharedPreferences, textView3, runnable, runnable2, view2);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroPoolLayoutControlBinder$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                HeroPoolLayoutControlBinder.lambda$bind$6(intSupplier3, intConsumer3, sharedPreferences, textView3, runnable, runnable2, view2);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(IntSupplier intSupplier, Button button, Button button2, TextView textView, TextView textView2, TextView textView3, IntSupplier intSupplier2, IntSupplier intSupplier3, TextView textView4) {
        if (intSupplier.getAsInt() == 0) {
            button.setTextColor(GameHudPalette.SUCCESS);
            button2.setTextColor(-7829368);
            if (textView != null) {
                textView.setText("每行个数");
            }
            if (textView2 != null) {
                textView2.setText("行数限制");
            }
        } else {
            button.setTextColor(-7829368);
            button2.setTextColor(GameHudPalette.SUCCESS);
            if (textView != null) {
                textView.setText("每列个数");
            }
            if (textView2 != null) {
                textView2.setText("列数限制");
            }
        }
        textView3.setText(String.valueOf(intSupplier2.getAsInt()));
        int asInt = intSupplier3.getAsInt();
        textView4.setText(asInt == 0 ? "自动" : String.valueOf(asInt));
    }

    static /* synthetic */ void lambda$bind$1(IntSupplier intSupplier, IntConsumer intConsumer, SharedPreferences sharedPreferences, Runnable runnable, Runnable runnable2, Runnable runnable3, View view) {
        if (intSupplier.getAsInt() != 0) {
            intConsumer.accept(0);
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HERO_DISPLAY_LAYOUT, 0).apply();
            runnable.run();
            runnable2.run();
            runnable3.run();
            Dbg.d(TAG, "牌库排列方式: 横排");
        }
    }

    static /* synthetic */ void lambda$bind$2(IntSupplier intSupplier, IntConsumer intConsumer, SharedPreferences sharedPreferences, Runnable runnable, Runnable runnable2, Runnable runnable3, View view) {
        if (intSupplier.getAsInt() != 1) {
            intConsumer.accept(1);
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HERO_DISPLAY_LAYOUT, 1).apply();
            runnable.run();
            runnable2.run();
            runnable3.run();
            Dbg.d(TAG, "牌库排列方式: 竖排");
        }
    }

    static /* synthetic */ void lambda$bind$3(IntSupplier intSupplier, IntConsumer intConsumer, SharedPreferences sharedPreferences, TextView textView, Runnable runnable, Runnable runnable2, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt > 1) {
            int i = asInt - 1;
            intConsumer.accept(i);
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HERO_DISPLAY_COLUMNS, i).apply();
            textView.setText(String.valueOf(i));
            runnable.run();
            runnable2.run();
            Dbg.d(TAG, "牌库每行/列个数: " + i);
        }
    }

    static /* synthetic */ void lambda$bind$4(IntSupplier intSupplier, IntConsumer intConsumer, SharedPreferences sharedPreferences, TextView textView, Runnable runnable, Runnable runnable2, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt < 20) {
            int i = asInt + 1;
            intConsumer.accept(i);
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HERO_DISPLAY_COLUMNS, i).apply();
            textView.setText(String.valueOf(i));
            runnable.run();
            runnable2.run();
            Dbg.d(TAG, "牌库每行/列个数: " + i);
        }
    }

    static /* synthetic */ void lambda$bind$5(IntSupplier intSupplier, IntConsumer intConsumer, SharedPreferences sharedPreferences, TextView textView, Runnable runnable, Runnable runnable2, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt > 0) {
            int i = asInt - 1;
            intConsumer.accept(i);
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HERO_DISPLAY_ROWS, i).apply();
            textView.setText(i == 0 ? "自动" : String.valueOf(i));
            runnable.run();
            runnable2.run();
            Dbg.d(TAG, "牌库行数限制: " + (i != 0 ? Integer.valueOf(i) : "自动"));
        }
    }

    static /* synthetic */ void lambda$bind$6(IntSupplier intSupplier, IntConsumer intConsumer, SharedPreferences sharedPreferences, TextView textView, Runnable runnable, Runnable runnable2, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt < 20) {
            int i = asInt + 1;
            intConsumer.accept(i);
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HERO_DISPLAY_ROWS, i).apply();
            textView.setText(i == 0 ? "自动" : String.valueOf(i));
            runnable.run();
            runnable2.run();
            Dbg.d(TAG, "牌库行数限制: " + (i != 0 ? Integer.valueOf(i) : "自动"));
        }
    }
}
