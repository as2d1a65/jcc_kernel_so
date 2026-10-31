package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import com.android.support.SharedMemManager;
import com.android.support.diagnostics.TraceCatalog;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class HexPredictionControlBinder {
    private HexPredictionControlBinder() {
    }

    static String formatPosPercentLabel(int i) {
        int iMax = Math.max(0, Math.min(TraceCatalog.Feature.AUTO_PICK, i));
        int i2 = iMax / 2;
        if ((iMax & 1) == 0) {
            return i2 + "%";
        }
        return i2 + ".5%";
    }

    static void bind(View view, final Context context, final SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, final Consumer<View> consumer, final Consumer<int[]> consumer2, BooleanSupplier booleanSupplier, final Consumer<Boolean> consumer3, final Runnable runnable, BooleanSupplier booleanSupplier2, final Consumer<Boolean> consumer4, final Supplier<int[]> supplier2, final BooleanSupplier booleanSupplier3, final Consumer<int[]> consumer5, final IntSupplier intSupplier, final IntConsumer intConsumer, final IntSupplier intSupplier2, final IntConsumer intConsumer2, final IntSupplier intSupplier3, final IntConsumer intConsumer3, final Runnable runnable2) {
        Button button = (Button) view.findViewById(R.id.btnHexPredict);
        if (button != null) {
            if (!HexFeatureGate.isEnabled()) {
                button.setAlpha(0.48f);
                button.setText("海克斯预测 · 已禁用");
            }
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HexPredictionControlBinder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    HexPredictionControlBinder.lambda$bind$1(consumer, context, supplier, consumer2, view2);
                }
            });
        }
        Switch r1 = (Switch) view.findViewById(R.id.switchHexAlwaysShow);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarHexX);
        SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBarHexY);
        SeekBar seekBar3 = (SeekBar) view.findViewById(R.id.seekBarHexSize);
        final TextView textView = (TextView) view.findViewById(R.id.tvHexX);
        final TextView textView2 = (TextView) view.findViewById(R.id.tvHexY);
        final TextView textView3 = (TextView) view.findViewById(R.id.tvHexSize);
        if (!HexFeatureGate.isEnabled()) {
            if (r1 != null) {
                r1.setEnabled(false);
            }
            if (seekBar != null) {
                seekBar.setEnabled(false);
            }
            if (seekBar2 != null) {
                seekBar2.setEnabled(false);
            }
            if (seekBar3 != null) {
                seekBar3.setEnabled(false);
            }
        }
        if (r1 != null) {
            r1.setChecked(booleanSupplier.getAsBoolean());
            r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.HexPredictionControlBinder$$ExternalSyntheticLambda2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    HexPredictionControlBinder.lambda$bind$2(consumer3, sharedPreferences, runnable, compoundButton, z);
                }
            });
        }
        Switch r0 = (Switch) view.findViewById(R.id.switchHexHorizontal);
        if (!HexFeatureGate.isEnabled() && r0 != null) {
            r0.setEnabled(false);
        }
        if (r0 != null) {
            r0.setChecked(booleanSupplier2.getAsBoolean());
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.HexPredictionControlBinder$$ExternalSyntheticLambda3
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    HexPredictionControlBinder.lambda$bind$3(consumer4, sharedPreferences, supplier2, booleanSupplier3, consumer5, compoundButton, z);
                }
            });
        }
        if (seekBar != null) {
            seekBar.setProgress(intSupplier.getAsInt());
            if (textView != null) {
                textView.setText(formatPosPercentLabel(intSupplier.getAsInt()));
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.HexPredictionControlBinder.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar4, int i, boolean z) {
                    if (z) {
                        intConsumer.accept(i);
                        TextView textView4 = textView;
                        if (textView4 != null) {
                            textView4.setText(HexPredictionControlBinder.formatPosPercentLabel(i));
                        }
                        runnable2.run();
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar4) {
                    ViewParent parent = seekBar4.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar4) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HEX_POS_X, intSupplier.getAsInt()).apply();
                    ViewParent parent = seekBar4.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                }
            });
        }
        if (seekBar2 != null) {
            seekBar2.setProgress(intSupplier2.getAsInt());
            if (textView2 != null) {
                textView2.setText(formatPosPercentLabel(intSupplier2.getAsInt()));
            }
            seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.HexPredictionControlBinder.2
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar4, int i, boolean z) {
                    if (z) {
                        intConsumer2.accept(i);
                        TextView textView4 = textView2;
                        if (textView4 != null) {
                            textView4.setText(HexPredictionControlBinder.formatPosPercentLabel(i));
                        }
                        runnable2.run();
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar4) {
                    ViewParent parent = seekBar4.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar4) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HEX_POS_Y, intSupplier2.getAsInt()).apply();
                    ViewParent parent = seekBar4.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                }
            });
        }
        if (seekBar3 != null) {
            seekBar3.setProgress(Math.max(0, intSupplier3.getAsInt() - 30));
            if (textView3 != null) {
                textView3.setText(intSupplier3.getAsInt() + "%");
            }
            seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.HexPredictionControlBinder.3
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar4, int i, boolean z) {
                    if (z) {
                        intConsumer3.accept(i + 30);
                        TextView textView4 = textView3;
                        if (textView4 != null) {
                            textView4.setText(intSupplier3.getAsInt() + "%");
                        }
                        int[] iArr = (int[]) supplier2.get();
                        if (!booleanSupplier3.getAsBoolean() || iArr == null) {
                            return;
                        }
                        consumer5.accept(iArr);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar4) {
                    ViewParent parent = seekBar4.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar4) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HEX_SIZE, intSupplier3.getAsInt()).apply();
                    ViewParent parent = seekBar4.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$1(Consumer consumer, final Context context, Supplier supplier, final Consumer consumer2, View view) {
        consumer.accept(view);
        if (!HexFeatureGate.isEnabled()) {
            FloatingWindowService.safeToast(context, "海克斯功能暂时已禁用", 0);
            return;
        }
        SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
        if (sharedMemManager == null) {
            FloatingWindowService.safeToast(context, "共享内存未初始化", 0);
        } else {
            FloatingWindowService.safeToast(context, "正在刷新海克斯数据...", 0);
            sharedMemManager.requestHexLevels(new SharedMemManager.HexLevelsCallback() { // from class: com.android.support.HexPredictionControlBinder$$ExternalSyntheticLambda0
                @Override // com.android.support.SharedMemManager.HexLevelsCallback
                public final void onResult(int[] iArr) {
                    HexPredictionControlBinder.lambda$bind$0(consumer2, context, iArr);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$0(Consumer consumer, Context context, int[] iArr) {
        if (iArr != null) {
            consumer.accept(iArr);
        } else {
            FloatingWindowService.safeToast(context, "海克斯查询超时，请确认已进入对局", 0);
        }
    }

    static /* synthetic */ void lambda$bind$2(Consumer consumer, SharedPreferences sharedPreferences, Runnable runnable, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_HEX_ALWAYS_SHOW, z).apply();
        runnable.run();
    }

    static /* synthetic */ void lambda$bind$3(Consumer consumer, SharedPreferences sharedPreferences, Supplier supplier, BooleanSupplier booleanSupplier, Consumer consumer2, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_HEX_HORIZONTAL, z).apply();
        int[] iArr = (int[]) supplier.get();
        if (iArr == null || !booleanSupplier.getAsBoolean()) {
            return;
        }
        consumer2.accept(iArr);
    }
}
