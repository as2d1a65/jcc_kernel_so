package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/* loaded from: classes.dex */
final class ScreenSecureControlBinder {
    private static long requestGeneration;

    private ScreenSecureControlBinder() {
    }

    static void bind(final View view, final Context context, final SharedPreferences sharedPreferences, final Consumer<Boolean> consumer, final Runnable runnable, final Runnable runnable2, final BooleanSupplier booleanSupplier, final Consumer<String> consumer2) {
        Switch r0 = (Switch) view.findViewById(R.id.switchScreenSecure);
        if (r0 == null) {
            return;
        }
        boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SCREEN_SECURE, false);
        consumer.accept(Boolean.valueOf(z));
        r0.setChecked(z);
        if (z) {
            runnable.run();
            runnable2.run();
        }
        requestGeneration++;
        final Handler handler = new Handler(Looper.getMainLooper());
        r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.ScreenSecureControlBinder$$ExternalSyntheticLambda0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                ScreenSecureControlBinder.lambda$bind$2(consumer, sharedPreferences, runnable, runnable2, consumer2, handler, view, context, booleanSupplier, compoundButton, z2);
            }
        });
    }

    static /* synthetic */ void lambda$bind$2(Consumer consumer, final SharedPreferences sharedPreferences, final Runnable runnable, final Runnable runnable2, final Consumer consumer2, final Handler handler, final View view, final Context context, final BooleanSupplier booleanSupplier, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_SCREEN_SECURE, z).apply();
        final long j = requestGeneration + 1;
        requestGeneration = j;
        runnable.run();
        runnable2.run();
        if (z) {
            new Thread(new Runnable() { // from class: com.android.support.ScreenSecureControlBinder$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ScreenSecureControlBinder.lambda$bind$1(consumer2, handler, j, sharedPreferences, runnable, runnable2, view, context, booleanSupplier);
                }
            }, "ScreenSecureInit").start();
        } else {
            FloatingWindowService.safeToast(context, "防录屏/防截屏 已关闭", 0);
        }
    }

    static /* synthetic */ void lambda$bind$1(Consumer consumer, Handler handler, final long j, final SharedPreferences sharedPreferences, final Runnable runnable, final Runnable runnable2, final View view, final Context context, final BooleanSupplier booleanSupplier) {
        try {
            consumer.accept("settings put global hidden_api_policy 1");
        } catch (Throwable unused) {
        }
        handler.post(new Runnable() { // from class: com.android.support.ScreenSecureControlBinder$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ScreenSecureControlBinder.lambda$bind$0(j, sharedPreferences, runnable, runnable2, view, context, booleanSupplier);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(long j, SharedPreferences sharedPreferences, Runnable runnable, Runnable runnable2, View view, Context context, BooleanSupplier booleanSupplier) {
        String str;
        if (j == requestGeneration && sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SCREEN_SECURE, false)) {
            runnable.run();
            runnable2.run();
            if (view.isAttachedToWindow()) {
                if (booleanSupplier.getAsBoolean()) {
                    str = "防录屏/防截屏 已开启";
                } else {
                    str = "防录屏开启失败(系统不支持setSkipScreenshot)";
                }
                FloatingWindowService.safeToast(context, str, 0);
            }
        }
    }
}
