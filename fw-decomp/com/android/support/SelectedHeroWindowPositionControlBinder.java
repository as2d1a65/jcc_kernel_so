package com.android.support;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

/* loaded from: classes.dex */
final class SelectedHeroWindowPositionControlBinder {
    private static final String TAG = "FloatingWindowService";

    interface Host {
        void applySelectedHeroesWindowPosition();

        WindowManager.LayoutParams selectedHeroesParams();

        View selectedHeroesWindow();

        WindowManager windowManager();
    }

    private SelectedHeroWindowPositionControlBinder() {
    }

    static void bind(View view, final SharedPreferences sharedPreferences, final Host host) {
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarPositionX);
        SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBarPositionY);
        final TextView textView = (TextView) view.findViewById(R.id.tvPositionX);
        final TextView textView2 = (TextView) view.findViewById(R.id.tvPositionY);
        if (seekBar != null && seekBar2 != null && textView != null && textView2 != null) {
            Display defaultDisplay = host.windowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getRealSize(point);
            final int i = point.x;
            final int i2 = point.y;
            int i3 = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_SELECTED_HEROES_POS_X_PERCENT, 5);
            int i4 = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_SELECTED_HEROES_POS_Y_PERCENT, 10);
            seekBar.setProgress(i3);
            seekBar2.setProgress(i4);
            int i5 = (int) ((i * i3) / 100.0f);
            int i6 = (int) ((i2 * i4) / 100.0f);
            textView.setText(String.valueOf(i5));
            textView2.setText(String.valueOf(i6));
            WindowManager.LayoutParams layoutParamsSelectedHeroesParams = host.selectedHeroesParams();
            if (layoutParamsSelectedHeroesParams == null) {
                Log.w(TAG, "位置控制初始化失败: paramsSelectedHeroes为null");
            } else {
                layoutParamsSelectedHeroesParams.x = i5;
                layoutParamsSelectedHeroesParams.y = i6;
                Dbg.d(TAG, "位置控制初始化: paramsSelectedHeroes.x=" + layoutParamsSelectedHeroesParams.x + ", y=" + layoutParamsSelectedHeroesParams.y);
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.SelectedHeroWindowPositionControlBinder.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar3, int i7, boolean z) {
                    if (z) {
                        WindowManager.LayoutParams layoutParamsSelectedHeroesParams2 = host.selectedHeroesParams();
                        WindowManager windowManager = host.windowManager();
                        View viewSelectedHeroesWindow = host.selectedHeroesWindow();
                        Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "X滑动条拖动: progress=" + i7 + ", fromUser=" + z);
                        Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "  - paramsSelectedHeroes=".concat(layoutParamsSelectedHeroesParams2 != null ? "非null" : "null"));
                        Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "  - windowManager=".concat(windowManager != null ? "非null" : "null"));
                        Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "  - selectedHeroesWindow=".concat(viewSelectedHeroesWindow == null ? "null" : "非null"));
                        if (layoutParamsSelectedHeroesParams2 == null || windowManager == null) {
                            return;
                        }
                        int i8 = (int) ((i * i7) / 100.0f);
                        layoutParamsSelectedHeroesParams2.x = i8;
                        textView.setText(String.valueOf(i8));
                        Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "  - 计算的xPos=" + i8 + ", paramsSelectedHeroes.x=" + layoutParamsSelectedHeroesParams2.x);
                        if (viewSelectedHeroesWindow != null) {
                            try {
                                host.applySelectedHeroesWindowPosition();
                            } catch (Exception e) {
                                Log.e(SelectedHeroWindowPositionControlBinder.TAG, "  ❌ 更新X位置失败", e);
                            }
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar3) {
                    Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "X滑动条开始拖动");
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar3) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SELECTED_HEROES_POS_X_PERCENT, seekBar3.getProgress()).apply();
                    Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "X滑动条停止拖动，保存X位置: " + seekBar3.getProgress() + "%");
                }
            });
            seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.SelectedHeroWindowPositionControlBinder.2
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar3, int i7, boolean z) {
                    if (z) {
                        Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "Y滑动条拖动: progress=" + i7 + ", fromUser=" + z);
                        WindowManager.LayoutParams layoutParamsSelectedHeroesParams2 = host.selectedHeroesParams();
                        WindowManager windowManager = host.windowManager();
                        View viewSelectedHeroesWindow = host.selectedHeroesWindow();
                        if (layoutParamsSelectedHeroesParams2 == null || windowManager == null) {
                            return;
                        }
                        int i8 = (int) ((i2 * i7) / 100.0f);
                        layoutParamsSelectedHeroesParams2.y = i8;
                        textView2.setText(String.valueOf(i8));
                        Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "  - 计算的yPos=" + i8 + ", paramsSelectedHeroes.y=" + layoutParamsSelectedHeroesParams2.y);
                        if (viewSelectedHeroesWindow != null) {
                            try {
                                host.applySelectedHeroesWindowPosition();
                            } catch (Exception e) {
                                Log.e(SelectedHeroWindowPositionControlBinder.TAG, "  ❌ 更新Y位置失败", e);
                            }
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar3) {
                    Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "Y滑动条开始拖动");
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar3) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_SELECTED_HEROES_POS_Y_PERCENT, seekBar3.getProgress()).apply();
                    Dbg.d(SelectedHeroWindowPositionControlBinder.TAG, "Y滑动条停止拖动，保存Y位置: " + seekBar3.getProgress() + "%");
                }
            });
            View viewSelectedHeroesWindow = host.selectedHeroesWindow();
            Dbg.d(TAG, "位置控制初始化完成: X=" + i5 + "px (" + i3 + "%), Y=" + i6 + "px (" + i4 + "%)");
            Dbg.d(TAG, "选中英雄窗口状态: " + (viewSelectedHeroesWindow != null ? "已创建" : "未创建") + ", visible=" + (viewSelectedHeroesWindow != null && viewSelectedHeroesWindow.isShown()));
            return;
        }
        Log.w(TAG, "位置控制初始化跳过: seekBarPositionX=" + (seekBar != null) + ", seekBarPositionY=" + (seekBar2 != null) + ", tvPositionX=" + (textView != null) + ", tvPositionY=" + (textView2 != null) + ", paramsSelectedHeroes=" + (host.selectedHeroesParams() != null));
    }
}
