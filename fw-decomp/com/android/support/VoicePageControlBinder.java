package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/* loaded from: classes.dex */
final class VoicePageControlBinder {

    interface Host {
        void applyCelebrationEnabled(boolean z);

        void applyVoiceBannerEnabled(boolean z);

        void bindVoiceBannerSeekBar(SeekBar seekBar, TextView textView, boolean z);

        Button btnVoiceBannerPreviewRef();

        boolean celebrationEnabled();

        int celebrationVolume();

        void enhanceSinglePlayerControl(View view);

        void previewCelebration();

        RankWarningView rankWarningView();

        void requestVoiceTest(Button button);

        void setBtnVoiceBannerPreviewRef(Button button);

        void setCelebrationVolume(int i);

        void setRankWarningThreshold(int i);

        void setVoiceBannerPreviewVisible(boolean z);

        void setVoiceBannerSwitchRef(Switch r1);

        void setVoiceBannerXPercent(int i);

        void setVoiceBannerYPercent(int i);

        void setVoiceEnabled(boolean z);

        void setVoicePitch(int i);

        void setVoiceRankAlertEnabled(boolean z);

        void setVoiceRankCostMask(int i);

        void setVoiceRankThreshold(int i);

        void setVoiceSpeed(int i);

        void setVoiceVolume(int i);

        void stopVoiceBannerPreview();

        void syncAutoRefreshState();

        boolean syncingVoiceBannerSwitches();

        void updateVoiceBannerPosition(TextView textView, TextView textView2);

        VoiceAlertQueue voiceAlertQueue();

        boolean voiceBannerEnabled();

        VoiceAlertBannerLifecycle voiceBannerLifecycle();

        boolean voiceBannerPreviewVisible();

        int voiceBannerXPercent();

        int voiceBannerYPercent();

        int voicePitch();

        int voiceRankThreshold();

        int voiceSpeed();

        int voiceVolume();
    }

    private VoicePageControlBinder() {
    }

    static void bind(View view, View view2, Context context, final SharedPreferences sharedPreferences, final Host host) {
        if (view == null) {
            return;
        }
        Switch r0 = (Switch) view.findViewById(R.id.switchVoiceEnabled);
        if (r0 != null) {
            boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_VOICE_ENABLED, false);
            host.setVoiceEnabled(z);
            r0.setChecked(z);
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.VoicePageControlBinder$$ExternalSyntheticLambda0
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    VoicePageControlBinder.lambda$bind$0(host, sharedPreferences, compoundButton, z2);
                }
            });
        }
        final Button button = (Button) view.findViewById(R.id.btnVoiceTest);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VoicePageControlBinder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    host.requestVoiceTest(button);
                }
            });
        }
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarVoiceSpeed);
        final TextView textView = (TextView) view.findViewById(R.id.tvVoiceSpeedVal);
        if (seekBar != null) {
            host.setVoiceSpeed(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_VOICE_SPEED, 50));
            seekBar.setProgress(host.voiceSpeed());
            if (textView != null) {
                textView.setText(String.valueOf(host.voiceSpeed()));
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.VoicePageControlBinder.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int i, boolean z2) {
                    host.setVoiceSpeed(i);
                    TextView textView2 = textView;
                    if (textView2 != null) {
                        textView2.setText(String.valueOf(i));
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_VOICE_SPEED, host.voiceSpeed()).apply();
                }
            });
        }
        SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBarVoiceVolume);
        final TextView textView2 = (TextView) view.findViewById(R.id.tvVoiceVolumeVal);
        if (seekBar2 != null) {
            host.setVoiceVolume(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_VOICE_VOLUME, 80));
            seekBar2.setProgress(host.voiceVolume());
            if (textView2 != null) {
                textView2.setText(String.valueOf(host.voiceVolume()));
            }
            seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.VoicePageControlBinder.2
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar3) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar3, int i, boolean z2) {
                    host.setVoiceVolume(i);
                    TextView textView3 = textView2;
                    if (textView3 != null) {
                        textView3.setText(String.valueOf(i));
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar3) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_VOICE_VOLUME, host.voiceVolume()).apply();
                }
            });
        }
        SeekBar seekBar3 = (SeekBar) view.findViewById(R.id.seekBarVoicePitch);
        final TextView textView3 = (TextView) view.findViewById(R.id.tvVoicePitchVal);
        if (seekBar3 != null) {
            host.setVoicePitch(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_VOICE_PITCH, 50));
            seekBar3.setProgress(host.voicePitch());
            if (textView3 != null) {
                textView3.setText(String.valueOf(host.voicePitch()));
            }
            seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.VoicePageControlBinder.3
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar4) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar4, int i, boolean z2) {
                    host.setVoicePitch(i);
                    TextView textView4 = textView3;
                    if (textView4 != null) {
                        textView4.setText(String.valueOf(i));
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar4) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_VOICE_PITCH, host.voicePitch()).apply();
                }
            });
        }
        Switch r02 = (Switch) view.findViewById(R.id.switchVoiceRankAlert);
        if (r02 != null) {
            boolean z2 = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_VOICE_RANK_ENABLED, false);
            host.setVoiceRankAlertEnabled(z2);
            r02.setChecked(z2);
            r02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.VoicePageControlBinder$$ExternalSyntheticLambda2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z3) {
                    VoicePageControlBinder.lambda$bind$2(host, sharedPreferences, compoundButton, z3);
                }
            });
        }
        host.setVoiceRankCostMask(VoiceAlertCostFilter.sanitize(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_RANK_COST_FILTER, 31)));
        SeekBar seekBar4 = (SeekBar) view.findViewById(R.id.seekBarVoiceRankThresh);
        final TextView textView4 = (TextView) view.findViewById(R.id.tvVoiceRankThreshVal);
        if (seekBar4 != null) {
            host.setVoiceRankThreshold(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_RANK_WARNING_THRESHOLD, 7));
            seekBar4.setProgress(host.voiceRankThreshold() - 1);
            if (textView4 != null) {
                textView4.setText(String.valueOf(host.voiceRankThreshold()));
            }
            seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.VoicePageControlBinder.4
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar5) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar5, int i, boolean z3) {
                    host.setVoiceRankThreshold(i + 1);
                    TextView textView5 = textView4;
                    if (textView5 != null) {
                        textView5.setText(String.valueOf(host.voiceRankThreshold()));
                    }
                    Host host2 = host;
                    host2.setRankWarningThreshold(host2.voiceRankThreshold());
                    RankWarningView rankWarningView = host.rankWarningView();
                    if (rankWarningView != null) {
                        rankWarningView.setThreshold(host.voiceRankThreshold());
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar5) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_RANK_WARNING_THRESHOLD, host.voiceRankThreshold()).putInt(FloatingWindowPrefsKeys.KEY_VOICE_RANK_THRESHOLD, host.voiceRankThreshold()).apply();
                }
            });
        }
        bindBannerControls(view, view2, context, sharedPreferences, host);
        bindCelebrationControls(view, context, sharedPreferences, host);
        bindHexCueControls(view, sharedPreferences);
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$0(Host host, SharedPreferences sharedPreferences, CompoundButton compoundButton, boolean z) {
        host.setVoiceEnabled(z);
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_VOICE_ENABLED, z).apply();
        if (!z) {
            host.stopVoiceBannerPreview();
            VoiceAlertQueue voiceAlertQueue = host.voiceAlertQueue();
            if (voiceAlertQueue != null) {
                voiceAlertQueue.resetForNewMatch();
            }
        }
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$2(Host host, SharedPreferences sharedPreferences, CompoundButton compoundButton, boolean z) {
        host.setVoiceRankAlertEnabled(z);
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_VOICE_RANK_ENABLED, z).apply();
        host.syncAutoRefreshState();
    }

    private static void bindHexCueControls(View view, final SharedPreferences sharedPreferences) {
        Switch r0 = (Switch) view.findViewById(R.id.switchHexCue);
        if (r0 != null) {
            r0.setChecked(sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_HEX_CUE_ENABLED, true));
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.VoicePageControlBinder$$ExternalSyntheticLambda3
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_HEX_CUE_ENABLED, z).apply();
                }
            });
        }
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarHexCueVolume);
        final TextView textView = (TextView) view.findViewById(R.id.tvHexCueVolumeVal);
        if (seekBar != null) {
            int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_HEX_CUE_VOLUME, 80);
            seekBar.setProgress(i);
            if (textView != null) {
                textView.setText(String.valueOf(i));
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.VoicePageControlBinder.5
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int i2, boolean z) {
                    TextView textView2 = textView;
                    if (textView2 != null) {
                        textView2.setText(String.valueOf(i2));
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_HEX_CUE_VOLUME, seekBar2.getProgress()).apply();
                }
            });
        }
    }

    private static void bindCelebrationControls(View view, final Context context, final SharedPreferences sharedPreferences, final Host host) {
        Switch r0 = (Switch) view.findViewById(R.id.switchCelebration);
        if (r0 != null) {
            r0.setChecked(host.celebrationEnabled());
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.VoicePageControlBinder$$ExternalSyntheticLambda7
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    host.applyCelebrationEnabled(z);
                }
            });
        }
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarCelebrationVolume);
        final TextView textView = (TextView) view.findViewById(R.id.tvCelebrationVolumeVal);
        if (seekBar != null) {
            seekBar.setProgress(host.celebrationVolume());
            if (textView != null) {
                textView.setText(String.valueOf(host.celebrationVolume()));
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.VoicePageControlBinder.6
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                    host.setCelebrationVolume(i);
                    TextView textView2 = textView;
                    if (textView2 != null) {
                        textView2.setText(String.valueOf(i));
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_CELEBRATION_VOLUME, host.celebrationVolume()).apply();
                }
            });
        }
        Button button = (Button) view.findViewById(R.id.btnCelebrationTest);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VoicePageControlBinder$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    VoicePageControlBinder.lambda$bindCelebrationControls$5(host, context, view2);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bindCelebrationControls$5(Host host, Context context, View view) {
        if (!host.celebrationEnabled()) {
            FloatingWindowService.safeToast(context, "请先开启高光庆祝", 0);
        } else {
            host.previewCelebration();
        }
    }

    private static void bindBannerControls(View view, View view2, final Context context, final SharedPreferences sharedPreferences, final Host host) {
        final Host host2;
        Switch r0 = (Switch) view.findViewById(R.id.switchVoiceBannerEnabled);
        host.setVoiceBannerSwitchRef(r0);
        Button button = (Button) view.findViewById(R.id.btnVoiceBannerPreview);
        host.setBtnVoiceBannerPreviewRef(button);
        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarVoiceBannerX);
        final SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBarVoiceBannerY);
        final TextView textView = (TextView) view.findViewById(R.id.tvVoiceBannerX);
        final TextView textView2 = (TextView) view.findViewById(R.id.tvVoiceBannerY);
        Button button2 = (Button) view.findViewById(R.id.btnVoiceBannerReset);
        if (r0 != null) {
            r0.setChecked(host.voiceBannerEnabled());
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.VoicePageControlBinder$$ExternalSyntheticLambda4
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    VoicePageControlBinder.lambda$bindBannerControls$6(host, compoundButton, z);
                }
            });
        }
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VoicePageControlBinder$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    VoicePageControlBinder.lambda$bindBannerControls$7(host, context, view3);
                }
            });
        }
        host.bindVoiceBannerSeekBar(seekBar, textView, true);
        host.bindVoiceBannerSeekBar(seekBar2, textView2, false);
        if (button2 != null) {
            host2 = host;
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VoicePageControlBinder$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    VoicePageControlBinder.lambda$bindBannerControls$8(host2, seekBar, seekBar2, textView, textView2, sharedPreferences, view3);
                }
            });
        } else {
            host2 = host;
        }
        host2.enhanceSinglePlayerControl(view2.findViewById(R.id.btnGenerateDrop));
    }

    static /* synthetic */ void lambda$bindBannerControls$6(Host host, CompoundButton compoundButton, boolean z) {
        if (host.syncingVoiceBannerSwitches()) {
            return;
        }
        host.applyVoiceBannerEnabled(z);
    }

    static /* synthetic */ void lambda$bindBannerControls$7(Host host, Context context, View view) {
        VoiceAlertBannerLifecycle voiceAlertBannerLifecycleVoiceBannerLifecycle = host.voiceBannerLifecycle();
        if (voiceAlertBannerLifecycleVoiceBannerLifecycle == null || !host.voiceBannerEnabled()) {
            FloatingWindowService.safeToast(context, "请先开启同步文字横幅", 0);
            return;
        }
        if (host.voiceBannerPreviewVisible()) {
            host.stopVoiceBannerPreview();
            return;
        }
        if (voiceAlertBannerLifecycleVoiceBannerLifecycle.showPreview()) {
            host.setVoiceBannerPreviewVisible(true);
            Button buttonBtnVoiceBannerPreviewRef = host.btnVoiceBannerPreviewRef();
            if (buttonBtnVoiceBannerPreviewRef != null) {
                buttonBtnVoiceBannerPreviewRef.setText("结束预览");
            }
        }
    }

    static /* synthetic */ void lambda$bindBannerControls$8(Host host, SeekBar seekBar, SeekBar seekBar2, TextView textView, TextView textView2, SharedPreferences sharedPreferences, View view) {
        host.setVoiceBannerXPercent(50);
        host.setVoiceBannerYPercent(35);
        if (seekBar != null) {
            seekBar.setProgress(host.voiceBannerXPercent());
        }
        if (seekBar2 != null) {
            seekBar2.setProgress(host.voiceBannerYPercent());
        }
        host.updateVoiceBannerPosition(textView, textView2);
        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_VOICE_BANNER_POS_X_PERCENT, host.voiceBannerXPercent()).putInt(FloatingWindowPrefsKeys.KEY_VOICE_BANNER_POS_Y_PERCENT, host.voiceBannerYPercent()).apply();
        VoiceAlertBannerLifecycle voiceAlertBannerLifecycleVoiceBannerLifecycle = host.voiceBannerLifecycle();
        if (host.voiceBannerEnabled() && voiceAlertBannerLifecycleVoiceBannerLifecycle != null && voiceAlertBannerLifecycleVoiceBannerLifecycle.showPreview()) {
            host.setVoiceBannerPreviewVisible(true);
            Button buttonBtnVoiceBannerPreviewRef = host.btnVoiceBannerPreviewRef();
            if (buttonBtnVoiceBannerPreviewRef != null) {
                buttonBtnVoiceBannerPreviewRef.setText("结束预览");
            }
        }
    }
}
