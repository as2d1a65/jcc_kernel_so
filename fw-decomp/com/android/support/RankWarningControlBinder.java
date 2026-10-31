package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
final class RankWarningControlBinder {

    interface Host {
        void applyRankBoxPreset(int i);

        void applyVoiceBannerEnabled(boolean z);

        boolean boardOverlayEnabled();

        boolean debugBoxMode();

        void enterRankBoxDragMode();

        void exitRankBoxDragMode();

        GameVisualOverlayHost gameVisualOverlayHost();

        void hideRankWarningOverlay();

        boolean isHeroPoolPageVisible();

        boolean isOpponentTextWanted();

        boolean isPlayersPageVisible();

        boolean opponentHighlightEnabled();

        boolean opponentShowBox();

        boolean rankBoxDragging();

        int rankBoxHeightDp();

        int rankBoxPosXProgress();

        int rankBoxPosYProgress();

        int rankBoxSpacingDp();

        int rankBoxWidthDp();

        int rankCostFilter();

        String rankCostFilterToText(int i);

        int rankHeroSizePercent();

        boolean rankHideBoxes();

        int rankInfoSizePercent();

        boolean rankWarningEnabled();

        int rankWarningOpacityPercent();

        int rankWarningThreshold();

        RankWarningView rankWarningView();

        void saveRankProfileInt(String str, int i);

        boolean selectedHeroIdsEmpty();

        void setRankBoxHeightDp(int i);

        void setRankBoxPosXProgress(int i);

        void setRankBoxPosYProgress(int i);

        void setRankBoxSpacingDp(int i);

        void setRankBoxWidthDp(int i);

        void setRankCostFilter(int i);

        void setRankHeroSizePercent(int i);

        void setRankHideBoxes(boolean z);

        void setRankInfoSizePercent(int i);

        void setRankTextBannerSwitchRef(Switch r1);

        void setRankWarningEnabled(boolean z);

        void setRankWarningOpacityPercent(int i);

        void setRankWarningThreshold(int i);

        void setRankWarningX(int i);

        void setRankWarningY(int i);

        void setVoiceBannerEnabled(boolean z);

        void setVoiceRankCostMask(int i);

        void setVoiceRankThreshold(int i);

        void showRankWarningOverlay();

        void startAutoRefresh();

        void stopAutoRefresh();

        boolean syncingVoiceBannerSwitches();

        void updateRankWarningLayout();

        WindowManager windowManager();
    }

    private RankWarningControlBinder() {
    }

    static void bind(View view, final Context context, final SharedPreferences sharedPreferences, final Host host) {
        final Context context2;
        final SharedPreferences sharedPreferences2;
        final Host host2;
        Switch r0 = (Switch) view.findViewById(R.id.switchRankWarning);
        if (r0 != null) {
            r0.setChecked(sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_RANK_WARNING_ENABLED, true));
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda0
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    RankWarningControlBinder.lambda$bind$0(host, sharedPreferences, context, compoundButton, z);
                }
            });
        }
        Switch r02 = (Switch) view.findViewById(R.id.switchRankTextBanner);
        host.setRankTextBannerSwitchRef(r02);
        if (r02 != null) {
            boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_VOICE_BANNER_ENABLED, true);
            host.setVoiceBannerEnabled(z);
            r02.setChecked(z);
            r02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    RankWarningControlBinder.lambda$bind$1(host, compoundButton, z2);
                }
            });
        }
        Switch r03 = (Switch) view.findViewById(R.id.switchRankHideBoxes);
        if (r03 != null) {
            r03.setChecked(host.rankHideBoxes());
            r03.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    RankWarningControlBinder.lambda$bind$2(host, sharedPreferences, compoundButton, z2);
                }
            });
        }
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarRankOpacity);
        final TextView textView = (TextView) view.findViewById(R.id.tvRankOpacity);
        if (seekBar != null) {
            host.setRankWarningOpacityPercent(RankWarningPositionMath.clampOpacityPercent(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_RANK_WARNING_OPACITY, 90)));
            seekBar.setProgress(host.rankWarningOpacityPercent() - 20);
            if (textView != null) {
                textView.setText(host.rankWarningOpacityPercent() + "%");
            }
            RankWarningView rankWarningView = host.rankWarningView();
            if (rankWarningView != null) {
                rankWarningView.setAlpha(host.rankWarningOpacityPercent() / 100.0f);
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.RankWarningControlBinder.1
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int i, boolean z2) {
                    if (z2) {
                        host.setRankWarningOpacityPercent(RankWarningPositionMath.clampOpacityPercent(i + 20));
                        TextView textView2 = textView;
                        if (textView2 != null) {
                            textView2.setText(host.rankWarningOpacityPercent() + "%");
                        }
                        RankWarningView rankWarningView2 = host.rankWarningView();
                        if (rankWarningView2 != null) {
                            rankWarningView2.setAlpha(host.rankWarningOpacityPercent() / 100.0f);
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_RANK_WARNING_OPACITY, host.rankWarningOpacityPercent()).apply();
                }
            });
        }
        SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBarRankInfoSize);
        final TextView textView2 = (TextView) view.findViewById(R.id.tvRankInfoSize);
        if (seekBar2 != null) {
            host.setRankInfoSizePercent(RankWarningPositionMath.clampSizePercent(host.rankInfoSizePercent()));
            seekBar2.setProgress(host.rankInfoSizePercent() - 20);
            if (textView2 != null) {
                textView2.setText(host.rankInfoSizePercent() + "%");
            }
            seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.RankWarningControlBinder.2
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar3) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar3, int i, boolean z2) {
                    if (z2) {
                        host.setRankInfoSizePercent(RankWarningPositionMath.clampSizePercent(i + 20));
                        TextView textView3 = textView2;
                        if (textView3 != null) {
                            textView3.setText(host.rankInfoSizePercent() + "%");
                        }
                        RankWarningView rankWarningView2 = host.rankWarningView();
                        if (rankWarningView2 != null) {
                            rankWarningView2.animateInfoSizeScale(host.rankInfoSizePercent() / 100.0f);
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar3) {
                    Host host3 = host;
                    host3.saveRankProfileInt(FloatingWindowPrefsKeys.KEY_RANK_INFO_SIZE, host3.rankInfoSizePercent());
                }
            });
        }
        SeekBar seekBar3 = (SeekBar) view.findViewById(R.id.seekBarRankHeroSize);
        final TextView textView3 = (TextView) view.findViewById(R.id.tvRankHeroSize);
        if (seekBar3 != null) {
            host.setRankHeroSizePercent(RankWarningPositionMath.clampSizePercent(host.rankHeroSizePercent()));
            seekBar3.setProgress(host.rankHeroSizePercent() - 20);
            if (textView3 != null) {
                textView3.setText(host.rankHeroSizePercent() + "%");
            }
            seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.RankWarningControlBinder.3
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar4, int i, boolean z2) {
                    if (z2) {
                        host.setRankHeroSizePercent(RankWarningPositionMath.clampSizePercent(i + 20));
                        TextView textView4 = textView3;
                        if (textView4 != null) {
                            textView4.setText(host.rankHeroSizePercent() + "%");
                        }
                        RankWarningView rankWarningView2 = host.rankWarningView();
                        if (rankWarningView2 != null) {
                            rankWarningView2.setHeroSizeScale(host.rankHeroSizePercent() / 100.0f);
                            host.updateRankWarningLayout();
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar4) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(true);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar4) {
                    Host host3 = host;
                    host3.saveRankProfileInt(FloatingWindowPrefsKeys.KEY_RANK_HERO_SIZE, host3.rankHeroSizePercent());
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(false);
                    }
                }
            });
        }
        View viewFindViewById = view.findViewById(R.id.btnRankBoxPresetTL);
        View viewFindViewById2 = view.findViewById(R.id.btnRankBoxPresetLM);
        View viewFindViewById3 = view.findViewById(R.id.btnRankBoxPresetTR);
        View viewFindViewById4 = view.findViewById(R.id.btnRankBoxPresetRM);
        View viewFindViewById5 = view.findViewById(R.id.btnRankBoxDragMode);
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyRankBoxPreset(0);
                }
            });
        }
        if (viewFindViewById2 != null) {
            viewFindViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyRankBoxPreset(1);
                }
            });
        }
        if (viewFindViewById3 != null) {
            viewFindViewById3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyRankBoxPreset(2);
                }
            });
        }
        if (viewFindViewById4 != null) {
            viewFindViewById4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyRankBoxPreset(3);
                }
            });
        }
        if (viewFindViewById5 != null) {
            viewFindViewById5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    RankWarningControlBinder.lambda$bind$7(host, view2);
                }
            });
        }
        SeekBar seekBar4 = (SeekBar) view.findViewById(R.id.seekBarRankBoxWidth);
        SeekBar seekBar5 = (SeekBar) view.findViewById(R.id.seekBarRankBoxHeight);
        SeekBar seekBar6 = (SeekBar) view.findViewById(R.id.seekBarRankBoxSpacing);
        SeekBar seekBar7 = (SeekBar) view.findViewById(R.id.seekBarRankBoxPosX);
        SeekBar seekBar8 = (SeekBar) view.findViewById(R.id.seekBarRankBoxPosY);
        final TextView textView4 = (TextView) view.findViewById(R.id.tvRankBoxWidth);
        final TextView textView5 = (TextView) view.findViewById(R.id.tvRankBoxHeight);
        final TextView textView6 = (TextView) view.findViewById(R.id.tvRankBoxSpacing);
        final TextView textView7 = (TextView) view.findViewById(R.id.tvRankBoxPosX);
        final TextView textView8 = (TextView) view.findViewById(R.id.tvRankBoxPosY);
        if (seekBar4 != null && seekBar5 != null && seekBar6 != null && seekBar7 != null && seekBar8 != null) {
            final Point point = new Point();
            host.windowManager().getDefaultDisplay().getRealSize(point);
            seekBar4.setProgress(host.rankBoxWidthDp());
            seekBar5.setProgress(host.rankBoxHeightDp());
            seekBar6.setProgress(host.rankBoxSpacingDp());
            seekBar7.setProgress(host.rankBoxPosXProgress());
            seekBar8.setProgress(host.rankBoxPosYProgress());
            if (textView4 != null) {
                textView4.setText(String.valueOf(host.rankBoxWidthDp()));
            }
            if (textView5 != null) {
                textView5.setText(String.valueOf(host.rankBoxHeightDp()));
            }
            if (textView6 != null) {
                textView6.setText(String.valueOf(host.rankBoxSpacingDp()));
            }
            if (textView7 != null) {
                textView7.setText(String.valueOf(host.rankBoxPosXProgress()));
            }
            if (textView8 != null) {
                textView8.setText(String.valueOf(host.rankBoxPosYProgress()));
            }
            seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.RankWarningControlBinder.4
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar9, int i, boolean z2) {
                    if (z2) {
                        host.setRankBoxWidthDp(Math.max(10, i));
                        TextView textView9 = textView4;
                        if (textView9 != null) {
                            textView9.setText(String.valueOf(host.rankBoxWidthDp()));
                        }
                        RankWarningView rankWarningView2 = host.rankWarningView();
                        if (rankWarningView2 != null) {
                            rankWarningView2.setBoxSize(ScreenUtils.dp2px(context, host.rankBoxWidthDp()), ScreenUtils.dp2px(context, host.rankBoxHeightDp()), ScreenUtils.dp2px(context, host.rankBoxSpacingDp()));
                            host.updateRankWarningLayout();
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar9) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(true);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar9) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_RANK_BOX_WIDTH, host.rankBoxWidthDp()).apply();
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(false);
                    }
                }
            });
            seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.RankWarningControlBinder.5
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar9, int i, boolean z2) {
                    if (z2) {
                        host.setRankBoxHeightDp(Math.max(10, i));
                        TextView textView9 = textView5;
                        if (textView9 != null) {
                            textView9.setText(String.valueOf(host.rankBoxHeightDp()));
                        }
                        RankWarningView rankWarningView2 = host.rankWarningView();
                        if (rankWarningView2 != null) {
                            rankWarningView2.setBoxSize(ScreenUtils.dp2px(context, host.rankBoxWidthDp()), ScreenUtils.dp2px(context, host.rankBoxHeightDp()), ScreenUtils.dp2px(context, host.rankBoxSpacingDp()));
                            host.updateRankWarningLayout();
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar9) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(true);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar9) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_RANK_BOX_HEIGHT, host.rankBoxHeightDp()).apply();
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(false);
                    }
                }
            });
            seekBar6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.RankWarningControlBinder.6
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar9, int i, boolean z2) {
                    if (z2) {
                        host.setRankBoxSpacingDp(i);
                        TextView textView9 = textView6;
                        if (textView9 != null) {
                            textView9.setText(String.valueOf(host.rankBoxSpacingDp()));
                        }
                        RankWarningView rankWarningView2 = host.rankWarningView();
                        if (rankWarningView2 != null) {
                            rankWarningView2.setBoxSize(ScreenUtils.dp2px(context, host.rankBoxWidthDp()), ScreenUtils.dp2px(context, host.rankBoxHeightDp()), ScreenUtils.dp2px(context, host.rankBoxSpacingDp()));
                            host.updateRankWarningLayout();
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar9) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(true);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar9) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_RANK_BOX_SPACING, host.rankBoxSpacingDp()).apply();
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(false);
                    }
                }
            });
            seekBar7.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.RankWarningControlBinder.7
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar9, int i, boolean z2) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (!z2 || host.gameVisualOverlayHost() == null) {
                        return;
                    }
                    if (rankWarningView2 == null || !rankWarningView2.isAutoAnchorMode()) {
                        host.setRankBoxPosXProgress(i);
                        host.setRankWarningX((int) ((point.x * i) / 100.0f));
                        TextView textView9 = textView7;
                        if (textView9 != null) {
                            textView9.setText(String.valueOf(i));
                        }
                        host.updateRankWarningLayout();
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar9) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(true);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar9) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 == null || !rankWarningView2.isAutoAnchorMode()) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_RANK_BOX_POS_X, seekBar9.getProgress()).apply();
                    }
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(false);
                    }
                }
            });
            seekBar8.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.RankWarningControlBinder.8
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar9, int i, boolean z2) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (!z2 || host.gameVisualOverlayHost() == null) {
                        return;
                    }
                    if (rankWarningView2 == null || !rankWarningView2.isAutoAnchorMode()) {
                        host.setRankBoxPosYProgress(i);
                        host.setRankWarningY((int) ((point.y * i) / 100.0f));
                        TextView textView9 = textView8;
                        if (textView9 != null) {
                            textView9.setText(String.valueOf(i));
                        }
                        host.updateRankWarningLayout();
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar9) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(true);
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar9) {
                    RankWarningView rankWarningView2 = host.rankWarningView();
                    if (rankWarningView2 == null || !rankWarningView2.isAutoAnchorMode()) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_RANK_BOX_POS_Y, seekBar9.getProgress()).apply();
                    }
                    if (rankWarningView2 != null) {
                        rankWarningView2.setAdjustMode(false);
                    }
                }
            });
        }
        SeekBar seekBar9 = (SeekBar) view.findViewById(R.id.seekBarRankWarningThreshold);
        final TextView textView9 = (TextView) view.findViewById(R.id.tvRankWarningThreshold);
        if (seekBar9 != null) {
            int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_RANK_WARNING_THRESHOLD, 7);
            host.setRankWarningThreshold(i);
            host.setVoiceRankThreshold(i);
            seekBar9.setProgress(i - 1);
            if (textView9 != null) {
                textView9.setText(String.valueOf(i));
            }
            seekBar9.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.RankWarningControlBinder.9
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar10) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar10, int i2, boolean z2) {
                    if (z2) {
                        host.setRankWarningThreshold(i2 + 1);
                        Host host3 = host;
                        host3.setVoiceRankThreshold(host3.rankWarningThreshold());
                        TextView textView10 = textView9;
                        if (textView10 != null) {
                            textView10.setText(String.valueOf(host.rankWarningThreshold()));
                        }
                        RankWarningView rankWarningView2 = host.rankWarningView();
                        if (rankWarningView2 != null) {
                            rankWarningView2.setThreshold(host.rankWarningThreshold());
                            host.updateRankWarningLayout();
                        }
                    }
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar10) {
                    sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_RANK_WARNING_THRESHOLD, host.rankWarningThreshold()).putInt(FloatingWindowPrefsKeys.KEY_VOICE_RANK_THRESHOLD, host.rankWarningThreshold()).apply();
                }
            });
        }
        host.setRankCostFilter(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_RANK_COST_FILTER, 31));
        host.setVoiceRankCostMask(VoiceAlertCostFilter.sanitize(host.rankCostFilter()));
        int[] iArr = {R.id.btnRankCost1, R.id.btnRankCost2, R.id.btnRankCost3, R.id.btnRankCost4, R.id.btnRankCost5};
        final int[] iArr2 = {-6381922, GameHudPalette.SUCCESS, -14575885, -5552196, -26624};
        final TextView[] textViewArr = new TextView[5];
        for (int i2 = 0; i2 < 5; i2++) {
            textViewArr[i2] = (TextView) view.findViewById(iArr[i2]);
        }
        final Runnable runnable = new Runnable() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                RankWarningControlBinder.lambda$bind$8(textViewArr, host, context, iArr2);
            }
        };
        runnable.run();
        final int i3 = 0;
        while (i3 < 5) {
            TextView textView10 = textViewArr[i3];
            if (textView10 == null) {
                context2 = context;
                sharedPreferences2 = sharedPreferences;
                host2 = host;
            } else {
                context2 = context;
                sharedPreferences2 = sharedPreferences;
                host2 = host;
                textView10.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.RankWarningControlBinder$$ExternalSyntheticLambda9
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        RankWarningControlBinder.lambda$bind$9(host2, i3, context2, sharedPreferences2, runnable, view2);
                    }
                });
            }
            i3++;
            host = host2;
            context = context2;
            sharedPreferences = sharedPreferences2;
        }
    }

    static /* synthetic */ void lambda$bind$0(Host host, SharedPreferences sharedPreferences, Context context, CompoundButton compoundButton, boolean z) {
        host.setRankWarningEnabled(z);
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_RANK_WARNING_ENABLED, z).apply();
        if (z) {
            host.showRankWarningOverlay();
            host.startAutoRefresh();
            FloatingWindowService.safeToast(context, "三星预警已开启", 0);
            return;
        }
        if ((!host.opponentHighlightEnabled() || !host.opponentShowBox()) && !host.debugBoxMode()) {
            host.hideRankWarningOverlay();
        }
        if (!host.isHeroPoolPageVisible() && !host.isPlayersPageVisible() && !host.boardOverlayEnabled() && !host.rankWarningEnabled() && !host.isOpponentTextWanted() && !host.debugBoxMode() && ((!host.opponentHighlightEnabled() || !host.opponentShowBox()) && host.selectedHeroIdsEmpty())) {
            host.stopAutoRefresh();
        }
        FloatingWindowService.safeToast(context, "三星预警已关闭", 0);
    }

    static /* synthetic */ void lambda$bind$1(Host host, CompoundButton compoundButton, boolean z) {
        if (host.syncingVoiceBannerSwitches()) {
            return;
        }
        host.applyVoiceBannerEnabled(z);
    }

    static /* synthetic */ void lambda$bind$2(Host host, SharedPreferences sharedPreferences, CompoundButton compoundButton, boolean z) {
        host.setRankHideBoxes(z);
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_RANK_HIDE_BOXES, z).apply();
        RankWarningView rankWarningView = host.rankWarningView();
        if (rankWarningView != null) {
            rankWarningView.setHideBoxes(z);
            host.updateRankWarningLayout();
        }
    }

    static /* synthetic */ void lambda$bind$7(Host host, View view) {
        if (host.rankBoxDragging()) {
            host.exitRankBoxDragMode();
        } else {
            host.enterRankBoxDragMode();
        }
    }

    static /* synthetic */ void lambda$bind$8(TextView[] textViewArr, Host host, Context context, int[] iArr) {
        for (int i = 0; i < 5; i++) {
            if (textViewArr[i] != null) {
                boolean z = (host.rankCostFilter() & (1 << i)) != 0;
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(0);
                gradientDrawable.setCornerRadius(ScreenUtils.dp2px(context, 8.0f));
                if (z) {
                    gradientDrawable.setColor((iArr[i] & ViewCompat.MEASURED_SIZE_MASK) | (-872415232));
                    gradientDrawable.setStroke(ScreenUtils.dp2px(context, 2.0f), iArr[i]);
                    textViewArr[i].setTextColor(-1);
                    textViewArr[i].setShadowLayer(4.0f, 0.0f, 0.0f, iArr[i]);
                } else {
                    gradientDrawable.setColor(858993459);
                    gradientDrawable.setStroke(ScreenUtils.dp2px(context, 1.0f), 1149798536);
                    textViewArr[i].setTextColor(-2004318072);
                    textViewArr[i].setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                }
                textViewArr[i].setBackground(gradientDrawable);
            }
        }
    }

    static /* synthetic */ void lambda$bind$9(Host host, int i, Context context, SharedPreferences sharedPreferences, Runnable runnable, View view) {
        int i2 = 1 << i;
        host.setRankCostFilter(host.rankCostFilter() ^ i2);
        if (host.rankCostFilter() == 0) {
            host.setRankCostFilter(i2);
            FloatingWindowService.safeToast(context, "至少选择一个费用", 0);
            return;
        }
        host.setVoiceRankCostMask(host.rankCostFilter());
        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_RANK_COST_FILTER, host.rankCostFilter()).putInt(FloatingWindowPrefsKeys.KEY_VOICE_RANK_COST_FILTER, host.rankCostFilter()).apply();
        FloatingWindowService.safeToast(context, "预警费用: " + host.rankCostFilterToText(host.rankCostFilter()), 0);
        runnable.run();
        RankWarningView rankWarningView = host.rankWarningView();
        if (rankWarningView != null) {
            rankWarningView.setCostFilter(host.rankCostFilter());
            host.updateRankWarningLayout();
        }
    }
}
