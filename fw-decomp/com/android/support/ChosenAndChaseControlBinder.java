package com.android.support;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class ChosenAndChaseControlBinder {
    private static final String TAG = "FloatingWindowService";

    interface BooleanSupplier {
        boolean getAsBoolean();
    }

    interface ConsumerTextView {
        void accept(TextView textView);
    }

    private ChosenAndChaseControlBinder() {
    }

    static void bind(View view, final SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, final BiConsumer<String, Boolean> biConsumer, ConsumerTextView consumerTextView, final ConsumerTextView consumerTextView2, final IntSupplier intSupplier, final IntConsumer intConsumer, final IntSupplier intSupplier2, final IntConsumer intConsumer2, BooleanSupplier booleanSupplier, Runnable runnable, ConsumerTextView consumerTextView3, final ConsumerTextView consumerTextView4) {
        Switch r1 = (Switch) view.findViewById(R.id.switchAutoPick5CostChosen);
        if (r1 == null) {
            Log.w(TAG, "switchAutoPick5CostChosen not found in layout");
        } else {
            boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_AUTO_PICK_5COST_CHOSEN, false);
            r1.setChecked(z);
            SharedMemManager sharedMemManager = supplier.get();
            if (sharedMemManager != null) {
                sharedMemManager.setAutoPick5CostChosen(z);
            }
            Dbg.d(TAG, "加载保存的配置: 五费天选=" + z);
            r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda0
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    ChosenAndChaseControlBinder.lambda$bind$0(supplier, biConsumer, compoundButton, z2);
                }
            });
        }
        final TextView textView = (TextView) view.findViewById(R.id.btn5CostChosenFilter);
        if (textView != null) {
            consumerTextView.accept(textView);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    consumerTextView2.accept(textView);
                }
            });
        }
        final TextView textView2 = (TextView) view.findViewById(R.id.tvChase3ThresholdValue);
        final TextView textView3 = (TextView) view.findViewById(R.id.tvChase3ThresholdHint);
        TextView textView4 = (TextView) view.findViewById(R.id.btnChase3ThresholdMinus);
        TextView textView5 = (TextView) view.findViewById(R.id.btnChase3ThresholdPlus);
        SharedMemManager sharedMemManager2 = supplier.get();
        if (sharedMemManager2 != null) {
            sharedMemManager2.setChase3StarThreshold(intSupplier.getAsInt());
        }
        final Runnable runnable2 = new Runnable() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ChosenAndChaseControlBinder.lambda$bind$2(intSupplier, textView2, textView3);
            }
        };
        runnable2.run();
        if (textView4 != null) {
            textView4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChosenAndChaseControlBinder.lambda$bind$3(intSupplier, intConsumer, supplier, sharedPreferences, runnable2, view2);
                }
            });
        }
        if (textView5 != null) {
            textView5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChosenAndChaseControlBinder.lambda$bind$4(intSupplier, intConsumer, supplier, sharedPreferences, runnable2, view2);
                }
            });
        }
        final TextView textView6 = (TextView) view.findViewById(R.id.tvChosenThresholdValue);
        final TextView textView7 = (TextView) view.findViewById(R.id.tvChosenThresholdHint);
        TextView textView8 = (TextView) view.findViewById(R.id.btnChosenThresholdMinus);
        TextView textView9 = (TextView) view.findViewById(R.id.btnChosenThresholdPlus);
        final Runnable runnable3 = new Runnable() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ChosenAndChaseControlBinder.lambda$bind$5(intSupplier2, textView6, textView7);
            }
        };
        runnable3.run();
        if (textView8 != null) {
            textView8.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChosenAndChaseControlBinder.lambda$bind$6(intSupplier2, intConsumer2, supplier, sharedPreferences, runnable3, view2);
                }
            });
        }
        if (textView9 != null) {
            textView9.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChosenAndChaseControlBinder.lambda$bind$7(intSupplier2, intConsumer2, supplier, sharedPreferences, runnable3, view2);
                }
            });
        }
        Switch r12 = (Switch) view.findViewById(R.id.switchAutoPickAllChosen);
        if (r12 != null) {
            boolean z2 = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_AUTO_PICK_ALL_CHOSEN, false);
            r12.setChecked(z2);
            SharedMemManager sharedMemManager3 = supplier.get();
            if (sharedMemManager3 != null) {
                sharedMemManager3.setAutoPickAllChosen(z2);
            }
            r12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda8
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z3) {
                    ChosenAndChaseControlBinder.lambda$bind$8(supplier, biConsumer, compoundButton, z3);
                }
            });
        }
        final TextView textView10 = (TextView) view.findViewById(R.id.btnAllChosenFilter);
        if (textView10 != null) {
            if (booleanSupplier.getAsBoolean()) {
                runnable.run();
            }
            consumerTextView3.accept(textView10);
            textView10.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ChosenAndChaseControlBinder$$ExternalSyntheticLambda9
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    consumerTextView4.accept(textView10);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$0(Supplier supplier, BiConsumer biConsumer, CompoundButton compoundButton, boolean z) {
        Dbg.d(TAG, "Switch AutoPick5CostChosen changed: " + z);
        SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setAutoPick5CostChosen(z);
            biConsumer.accept(FloatingWindowPrefsKeys.KEY_AUTO_PICK_5COST_CHOSEN, Boolean.valueOf(z));
            Dbg.d(TAG, "配置已保存: 五费天选=" + z);
        }
    }

    static /* synthetic */ void lambda$bind$2(IntSupplier intSupplier, TextView textView, TextView textView2) {
        int asInt = intSupplier.getAsInt();
        if (textView != null) {
            textView.setText(String.valueOf(asInt));
            if (textView.isAttachedToWindow()) {
                OverlayUiMotion.pulseValue(textView);
            }
        }
        if (textView2 != null) {
            if (asInt <= 0) {
                textView2.setText("关闭");
                textView2.setTextColor(-7561045);
            } else {
                textView2.setText("≥" + asInt + "张就拿");
                textView2.setTextColor(GameHudPalette.SUCCESS);
            }
        }
    }

    static /* synthetic */ void lambda$bind$3(IntSupplier intSupplier, IntConsumer intConsumer, Supplier supplier, SharedPreferences sharedPreferences, Runnable runnable, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt > 0) {
            int i = asInt - 1;
            intConsumer.accept(i);
            SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
            if (sharedMemManager != null) {
                sharedMemManager.setChase3StarThreshold(i);
            }
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_CHASE_3STAR_MODE, i).apply();
            runnable.run();
        }
    }

    static /* synthetic */ void lambda$bind$4(IntSupplier intSupplier, IntConsumer intConsumer, Supplier supplier, SharedPreferences sharedPreferences, Runnable runnable, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt < 9) {
            int i = asInt + 1;
            intConsumer.accept(i);
            SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
            if (sharedMemManager != null) {
                sharedMemManager.setChase3StarThreshold(i);
            }
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_CHASE_3STAR_MODE, i).apply();
            runnable.run();
        }
    }

    static /* synthetic */ void lambda$bind$5(IntSupplier intSupplier, TextView textView, TextView textView2) {
        int asInt = intSupplier.getAsInt();
        if (textView != null) {
            textView.setText(String.valueOf(asInt));
            if (textView.isAttachedToWindow()) {
                OverlayUiMotion.pulseValue(textView);
            }
        }
        if (textView2 != null) {
            if (asInt <= 0) {
                textView2.setText("不拿天选");
                textView2.setTextColor(-7561045);
            } else {
                textView2.setText("≥" + asInt + "张拿天选");
                textView2.setTextColor(-15770);
            }
        }
    }

    static /* synthetic */ void lambda$bind$6(IntSupplier intSupplier, IntConsumer intConsumer, Supplier supplier, SharedPreferences sharedPreferences, Runnable runnable, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt > 0) {
            int i = asInt - 1;
            intConsumer.accept(i);
            SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
            if (sharedMemManager != null) {
                sharedMemManager.setChase3StarChosenThreshold(i);
            }
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_CHASE_3STAR_CHOSEN_THRESHOLD, i).apply();
            runnable.run();
        }
    }

    static /* synthetic */ void lambda$bind$7(IntSupplier intSupplier, IntConsumer intConsumer, Supplier supplier, SharedPreferences sharedPreferences, Runnable runnable, View view) {
        int asInt = intSupplier.getAsInt();
        if (asInt < 9) {
            int i = asInt + 1;
            intConsumer.accept(i);
            SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
            if (sharedMemManager != null) {
                sharedMemManager.setChase3StarChosenThreshold(i);
            }
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_CHASE_3STAR_CHOSEN_THRESHOLD, i).apply();
            runnable.run();
        }
    }

    static /* synthetic */ void lambda$bind$8(Supplier supplier, BiConsumer biConsumer, CompoundButton compoundButton, boolean z) {
        SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setAutoPickAllChosen(z);
        }
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_AUTO_PICK_ALL_CHOSEN, Boolean.valueOf(z));
    }
}
