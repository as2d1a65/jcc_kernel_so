package com.android.support;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class BoardOverlayTransformControlBinder {

    interface PosMapper {
        int toPixel(int i, int i2);
    }

    interface ScaleMapper {
        float toScale(int i);
    }

    private BoardOverlayTransformControlBinder() {
    }

    static void bind(View view, final SharedPreferences sharedPreferences, final WindowManager windowManager, IntSupplier intSupplier, final IntConsumer intConsumer, final Supplier<Float> supplier, final Consumer<Float> consumer, final ScaleMapper scaleMapper, final Supplier<ChessBoardView> supplier2, final Runnable runnable, final IntConsumer intConsumer2, final IntConsumer intConsumer3, final PosMapper posMapper) {
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarBoardScale);
        final TextView textView = (TextView) view.findViewById(R.id.tvBoardScalePercent);
        if (seekBar != null && textView != null) {
            seekBar.setProgress(intSupplier.getAsInt());
            textView.setText(((int) (supplier.get().floatValue() * 100.0f)) + "%");
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.BoardOverlayTransformControlBinder.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                    if (z) {
                        intConsumer.accept(i);
                        consumer.accept(Float.valueOf(scaleMapper.toScale(i)));
                        textView.setText(((int) (((Float) supplier.get()).floatValue() * 100.0f)) + "%");
                        ChessBoardView chessBoardView = (ChessBoardView) supplier2.get();
                        if (chessBoardView != null) {
                            chessBoardView.setViewScale(((Float) supplier.get()).floatValue());
                            runnable.run();
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_BOARD_OVERLAY_SCALE, seekBar2.getProgress()).apply();
                }
            });
        }
        SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBarBoardPosX);
        SeekBar seekBar3 = (SeekBar) view.findViewById(R.id.seekBarBoardPosY);
        final TextView textView2 = (TextView) view.findViewById(R.id.tvBoardPosX);
        final TextView textView3 = (TextView) view.findViewById(R.id.tvBoardPosY);
        if (seekBar2 == null || seekBar3 == null || textView2 == null || textView3 == null) {
            return;
        }
        int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_BOARD_OVERLAY_POS_X, 50);
        int i2 = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_BOARD_OVERLAY_POS_Y, 50);
        seekBar2.setProgress(i);
        seekBar3.setProgress(i2);
        textView2.setText(String.valueOf(posMapper.toPixel(i, screenWidth(windowManager))));
        textView3.setText(String.valueOf(posMapper.toPixel(i2, screenHeight(windowManager))));
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.BoardOverlayTransformControlBinder.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar4) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar4, int i3, boolean z) {
                if (!z || supplier2.get() == null) {
                    return;
                }
                intConsumer2.accept(i3);
                runnable.run();
                textView2.setText(String.valueOf(posMapper.toPixel(i3, BoardOverlayTransformControlBinder.screenWidth(windowManager))));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar4) {
                sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_BOARD_OVERLAY_POS_X, seekBar4.getProgress()).apply();
            }
        });
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.BoardOverlayTransformControlBinder.3
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar4) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar4, int i3, boolean z) {
                if (!z || supplier2.get() == null) {
                    return;
                }
                intConsumer3.accept(i3);
                runnable.run();
                textView3.setText(String.valueOf(posMapper.toPixel(i3, BoardOverlayTransformControlBinder.screenHeight(windowManager))));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar4) {
                sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_BOARD_OVERLAY_POS_Y, seekBar4.getProgress()).apply();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int screenWidth(WindowManager windowManager) {
        return realSize(windowManager).x;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int screenHeight(WindowManager windowManager) {
        return realSize(windowManager).y;
    }

    private static Point realSize(WindowManager windowManager) {
        Point point = new Point();
        if (windowManager != null) {
            try {
                windowManager.getDefaultDisplay().getRealSize(point);
            } catch (RuntimeException unused) {
            }
        }
        return point;
    }
}
