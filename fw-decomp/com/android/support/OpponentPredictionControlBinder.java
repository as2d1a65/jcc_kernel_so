package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import com.android.support.OpponentDisplayPolicy;
import com.android.support.diagnostics.TraceCatalog;

/* loaded from: classes.dex */
final class OpponentPredictionControlBinder {

    interface Host {
        void applyOpponentDisplayState();

        void applyOpponentTextPreset(int i);

        void applyOpponentTextSize();

        void applyRankBoxPreset(int i);

        void applyRankInfoOffset(float f);

        int attackIconOffsetX();

        int attackIconOffsetY();

        int attackIconSizePct();

        boolean boardOverlayEnabled();

        ChessBoardView chessBoardView();

        void enterOpponentTextDragMode();

        void enterRankBoxDragMode();

        void exitOpponentTextDragMode();

        void exitRankBoxDragMode();

        void hideBoardOverlay();

        void hideOpponentTextOverlay();

        int infoOffsetX();

        boolean isRankWarningVisible();

        boolean nativeAttackIconEnabled();

        int nativeAttackIconOffsetX();

        int nativeAttackIconOffsetY();

        int nativeAttackIconSizePct();

        boolean opponentAttackIcon();

        boolean opponentHighlightEnabled();

        boolean opponentShowBox();

        boolean opponentShowText();

        boolean opponentTextDragging();

        int opponentTextPosXProgress();

        int opponentTextPosYProgress();

        int opponentTextSizeProgress();

        boolean rankBoxDragging();

        float rankDisplayDensity();

        int rankHeroOffsetX();

        RankWarningView rankWarningView();

        void saveRankProfileInt(String str, int i);

        int selectedBoardPlayerId();

        void setAttackIconOffsetX(int i);

        void setAttackIconOffsetY(int i);

        void setAttackIconSizePct(int i);

        void setBoardOverlayEnabled(boolean z);

        void setBoardPlayerManuallySelected(boolean z);

        void setDebugBoxMode(boolean z);

        void setInfoOffsetX(int i);

        void setLastAutoSelectedOpponentId(int i);

        void setLayoutAttackIconAdjustField(LinearLayout linearLayout);

        void setLayoutOpponentBoxPosField(LinearLayout linearLayout);

        void setLayoutOpponentTextPosField(LinearLayout linearLayout);

        void setLayoutShowAllMatchupsField(LinearLayout linearLayout);

        void setNativeAttackIconEnabled(boolean z);

        void setNativeAttackIconOffsetX(int i);

        void setNativeAttackIconOffsetY(int i);

        void setNativeAttackIconSizePct(int i);

        void setOpponentAttackIcon(boolean z);

        void setOpponentHighlightEnabled(boolean z);

        void setOpponentShowBox(boolean z);

        void setOpponentShowText(boolean z);

        void setOpponentTextPosXProgress(int i);

        void setOpponentTextPosYProgress(int i);

        void setOpponentTextSizeProgress(int i);

        void setRankHeroOffsetX(int i);

        void setSelectedBoardPlayerId(int i);

        void setShowAllMatchups(boolean z);

        void setSuppressBoardStationToast(boolean z);

        SharedMemManager shmManager();

        void showOpponentTextOverlay();

        void showRankWarningOverlay();

        void startAutoRefresh();

        void updateOpponentTextPosition();

        void updateRankWarningLayout();
    }

    private OpponentPredictionControlBinder() {
    }

    static void bind(final View view, final Context context, final SharedPreferences sharedPreferences, final Switch r28, final Host host) {
        SeekBar seekBar;
        TextView textView;
        final Switch r8 = (Switch) view.findViewById(R.id.switchOpponentHighlight);
        final CompoundButton.OnCheckedChangeListener[] onCheckedChangeListenerArr = new CompoundButton.OnCheckedChangeListener[1];
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layoutOpponentDisplayMode);
        LinearLayout linearLayout2 = (LinearLayout) view.findViewById(R.id.layoutOpponentTextPos);
        LinearLayout linearLayout3 = (LinearLayout) view.findViewById(R.id.layoutOpponentBoxPos);
        host.setLayoutOpponentTextPosField(linearLayout2);
        host.setLayoutOpponentBoxPosField(linearLayout3);
        View viewFindViewById = view.findViewById(R.id.btnOppBoxPresetTL);
        View viewFindViewById2 = view.findViewById(R.id.btnOppBoxPresetLM);
        View viewFindViewById3 = view.findViewById(R.id.btnOppBoxPresetTR);
        View viewFindViewById4 = view.findViewById(R.id.btnOppBoxPresetRM);
        View viewFindViewById5 = view.findViewById(R.id.btnOppBoxDragMode);
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyRankBoxPreset(0);
                }
            });
        }
        if (viewFindViewById2 != null) {
            viewFindViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda14
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyRankBoxPreset(1);
                }
            });
        }
        if (viewFindViewById3 != null) {
            viewFindViewById3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda15
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyRankBoxPreset(2);
                }
            });
        }
        if (viewFindViewById4 != null) {
            viewFindViewById4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda16
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyRankBoxPreset(3);
                }
            });
        }
        if (viewFindViewById5 != null) {
            viewFindViewById5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    OpponentPredictionControlBinder.lambda$bind$4(host, view2);
                }
            });
        }
        View viewFindViewById6 = view.findViewById(R.id.btnOppTextPresetTL);
        View viewFindViewById7 = view.findViewById(R.id.btnOppTextPresetTC);
        View viewFindViewById8 = view.findViewById(R.id.btnOppTextPresetTR);
        View viewFindViewById9 = view.findViewById(R.id.btnOppTextPresetBC);
        View viewFindViewById10 = view.findViewById(R.id.btnOppTextDragMode);
        if (viewFindViewById6 != null) {
            viewFindViewById6.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyOpponentTextPreset(0);
                }
            });
        }
        if (viewFindViewById7 != null) {
            viewFindViewById7.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyOpponentTextPreset(1);
                }
            });
        }
        if (viewFindViewById8 != null) {
            viewFindViewById8.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyOpponentTextPreset(2);
                }
            });
        }
        if (viewFindViewById9 != null) {
            viewFindViewById9.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.applyOpponentTextPreset(3);
                }
            });
        }
        if (viewFindViewById10 != null) {
            viewFindViewById10.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    OpponentPredictionControlBinder.lambda$bind$9(host, view2);
                }
            });
        }
        SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBarOpponentTextX);
        SeekBar seekBar3 = (SeekBar) view.findViewById(R.id.seekBarOpponentTextY);
        SeekBar seekBar4 = (SeekBar) view.findViewById(R.id.seekBarOpponentTextSize);
        final TextView textView2 = (TextView) view.findViewById(R.id.tvOpponentTextX);
        TextView textView3 = (TextView) view.findViewById(R.id.tvOpponentTextY);
        final TextView textView4 = (TextView) view.findViewById(R.id.tvOpponentTextSize);
        final LinearLayout linearLayout4 = (LinearLayout) view.findViewById(R.id.layoutShowAllMatchups);
        host.setLayoutShowAllMatchupsField(linearLayout4);
        Switch r0 = (Switch) view.findViewById(R.id.switchShowAllMatchups);
        if (r8 != null) {
            boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_HIGHLIGHT, true);
            r8.setChecked(z);
            if (linearLayout != null) {
                linearLayout.setVisibility(0);
            }
            if (linearLayout4 != null) {
                linearLayout4.setVisibility((z && sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_OPPONENT_DISPLAY_MODE, 0) == 1) ? 0 : 8);
            }
            if (r0 != null) {
                r0.setChecked(sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_SHOW_ALL_MATCHUPS, false));
                r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda8
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                        OpponentPredictionControlBinder.lambda$bind$10(host, sharedPreferences, compoundButton, z2);
                    }
                });
            }
            boolean zOpponentShowBox = host.opponentShowBox();
            boolean zOpponentShowText = host.opponentShowText();
            if (linearLayout2 != null) {
                linearLayout2.setVisibility(zOpponentShowText ? 0 : 8);
            }
            if (linearLayout3 != null) {
                linearLayout3.setVisibility(zOpponentShowBox ? 0 : 8);
            }
            if (seekBar2 != null) {
                seekBar2.setProgress(host.opponentTextPosXProgress());
                if (textView2 != null) {
                    textView2.setText(String.valueOf(host.opponentTextPosXProgress()));
                }
            }
            if (seekBar3 != null) {
                seekBar3.setProgress(host.opponentTextPosYProgress());
                if (textView3 != null) {
                    textView3.setText(String.valueOf(host.opponentTextPosYProgress()));
                }
            }
            host.setOpponentTextSizeProgress(Math.max(50, Math.min(TraceCatalog.Feature.AUTO_PICK, sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_OPPONENT_TEXT_SIZE, 100))));
            if (seekBar4 != null) {
                seekBar4.setProgress(host.opponentTextSizeProgress() - 50);
                if (textView4 != null) {
                    textView4.setText(host.opponentTextSizeProgress() + "%");
                }
            }
            onCheckedChangeListenerArr[0] = new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda9
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    OpponentPredictionControlBinder.lambda$bind$11(host, sharedPreferences, linearLayout, linearLayout4, r28, view, context, compoundButton, z2);
                }
            };
            r8.setOnCheckedChangeListener(onCheckedChangeListenerArr[0]);
            Switch r02 = (Switch) view.findViewById(R.id.switchOpponentBox);
            Switch r3 = (Switch) view.findViewById(R.id.switchOpponentText);
            Switch r4 = (Switch) view.findViewById(R.id.switchOpponentAttackIcon);
            if (r02 != null) {
                r02.setChecked(host.opponentShowBox());
                r02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda10
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                        OpponentPredictionControlBinder.lambda$bind$12(host, sharedPreferences, compoundButton, z2);
                    }
                });
            }
            if (r3 != null) {
                r3.setChecked(host.opponentShowText());
                r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda11
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                        OpponentPredictionControlBinder.lambda$bind$13(host, sharedPreferences, compoundButton, z2);
                    }
                });
            }
            if (r4 != null) {
                r4.setChecked(host.opponentAttackIcon());
                r4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda12
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                        OpponentPredictionControlBinder.lambda$bind$15(host, sharedPreferences, r8, onCheckedChangeListenerArr, compoundButton, z2);
                    }
                });
            }
            LinearLayout linearLayout5 = (LinearLayout) view.findViewById(R.id.layoutAttackIconAdjust);
            SeekBar seekBar5 = (SeekBar) view.findViewById(R.id.seekBarAttackIconOffsetX);
            SeekBar seekBar6 = (SeekBar) view.findViewById(R.id.seekBarAttackIconOffsetY);
            SeekBar seekBar7 = (SeekBar) view.findViewById(R.id.seekBarAttackIconSize);
            final TextView textView5 = (TextView) view.findViewById(R.id.tvAttackIconOffsetX);
            final TextView textView6 = (TextView) view.findViewById(R.id.tvAttackIconOffsetY);
            final TextView textView7 = (TextView) view.findViewById(R.id.tvAttackIconSize);
            host.setLayoutAttackIconAdjustField(linearLayout5);
            if (linearLayout5 != null) {
                linearLayout5.setVisibility(0);
            }
            if (seekBar5 != null) {
                textView = textView3;
                seekBar = seekBar2;
                seekBar5.setProgress(Math.max(0, Math.min(TraceCatalog.Feature.AUTO_PICK, host.attackIconOffsetX() + 100)));
                if (textView5 != null) {
                    textView5.setText(host.attackIconOffsetX() + "dp");
                }
                seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.1
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar8, int i, boolean z2) {
                        if (z2) {
                            host.setAttackIconOffsetX(i - 100);
                            TextView textView8 = textView5;
                            if (textView8 != null) {
                                textView8.setText(host.attackIconOffsetX() + "dp");
                            }
                            RankWarningView rankWarningView = host.rankWarningView();
                            if (rankWarningView != null) {
                                rankWarningView.setAttackIconOffsetPx((int) (host.attackIconOffsetX() * context.getResources().getDisplayMetrics().density));
                            }
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar8) {
                        ViewParent parent = seekBar8.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar8) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_ATTACK_ICON_OFFSET_X, host.attackIconOffsetX()).apply();
                        ViewParent parent = seekBar8.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                });
            } else {
                seekBar = seekBar2;
                textView = textView3;
            }
            if (seekBar6 != null) {
                seekBar6.setProgress(Math.max(0, Math.min(TraceCatalog.Feature.AUTO_PICK, host.attackIconOffsetY() + 100)));
                if (textView6 != null) {
                    textView6.setText(host.attackIconOffsetY() + "dp");
                }
                seekBar6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.2
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar8, int i, boolean z2) {
                        if (z2) {
                            host.setAttackIconOffsetY(i - 100);
                            TextView textView8 = textView6;
                            if (textView8 != null) {
                                textView8.setText(host.attackIconOffsetY() + "dp");
                            }
                            RankWarningView rankWarningView = host.rankWarningView();
                            if (rankWarningView != null) {
                                rankWarningView.setAttackIconOffsetYPx((int) (host.attackIconOffsetY() * context.getResources().getDisplayMetrics().density));
                            }
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar8) {
                        ViewParent parent = seekBar8.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar8) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_ATTACK_ICON_OFFSET_Y, host.attackIconOffsetY()).apply();
                        ViewParent parent = seekBar8.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                });
            }
            if (seekBar7 != null) {
                host.setAttackIconSizePct(RankWarningPositionMath.clampOpponentIconSizePercent(host.attackIconSizePct()));
                seekBar7.setProgress(host.attackIconSizePct() - 50);
                if (textView7 != null) {
                    textView7.setText(host.attackIconSizePct() + "%");
                }
                seekBar7.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.3
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar8, int i, boolean z2) {
                        if (z2) {
                            host.setAttackIconSizePct(RankWarningPositionMath.clampOpponentIconSizePercent(i + 50));
                            TextView textView8 = textView7;
                            if (textView8 != null) {
                                textView8.setText(host.attackIconSizePct() + "%");
                            }
                            RankWarningView rankWarningView = host.rankWarningView();
                            if (rankWarningView != null) {
                                rankWarningView.setAttackIconScale(host.attackIconSizePct() / 100.0f);
                            }
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar8) {
                        ViewParent parent = seekBar8.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar8) {
                        Host host2 = host;
                        host2.saveRankProfileInt(FloatingWindowPrefsKeys.KEY_ATTACK_ICON_SIZE, host2.attackIconSizePct());
                        ViewParent parent = seekBar8.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                });
            }
            Switch r32 = (Switch) view.findViewById(R.id.switchNativeAttackIcon);
            SeekBar seekBar8 = (SeekBar) view.findViewById(R.id.seekBarNativeAttackIconOffsetX);
            SeekBar seekBar9 = (SeekBar) view.findViewById(R.id.seekBarNativeAttackIconOffsetY);
            SeekBar seekBar10 = (SeekBar) view.findViewById(R.id.seekBarNativeAttackIconSize);
            final TextView textView8 = (TextView) view.findViewById(R.id.tvNativeAttackIconOffsetX);
            final TextView textView9 = (TextView) view.findViewById(R.id.tvNativeAttackIconOffsetY);
            final TextView textView10 = (TextView) view.findViewById(R.id.tvNativeAttackIconSize);
            if (r32 != null) {
                r32.setChecked(host.nativeAttackIconEnabled());
                r32.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda13
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                        OpponentPredictionControlBinder.lambda$bind$16(host, sharedPreferences, compoundButton, z2);
                    }
                });
            }
            if (seekBar8 != null) {
                seekBar8.setProgress(host.nativeAttackIconOffsetX() + 100);
                if (textView8 != null) {
                    textView8.setText(host.nativeAttackIconOffsetX() + "dp");
                }
                seekBar8.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.4
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar11) {
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar11, int i, boolean z2) {
                        if (z2) {
                            host.setNativeAttackIconOffsetX(i - 100);
                            TextView textView11 = textView8;
                            if (textView11 != null) {
                                textView11.setText(host.nativeAttackIconOffsetX() + "dp");
                            }
                            SharedMemManager sharedMemManagerShmManager = host.shmManager();
                            if (sharedMemManagerShmManager != null) {
                                sharedMemManagerShmManager.setNativeAttackIconAppearance(host.nativeAttackIconOffsetX(), host.nativeAttackIconOffsetY(), host.nativeAttackIconSizePct());
                            }
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar11) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_NATIVE_ATTACK_ICON_OFFSET_X, host.nativeAttackIconOffsetX()).apply();
                    }
                });
            }
            if (seekBar9 != null) {
                seekBar9.setProgress(host.nativeAttackIconOffsetY() + 100);
                if (textView9 != null) {
                    textView9.setText(host.nativeAttackIconOffsetY() + "dp");
                }
                seekBar9.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.5
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar11) {
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar11, int i, boolean z2) {
                        if (z2) {
                            host.setNativeAttackIconOffsetY(i - 100);
                            TextView textView11 = textView9;
                            if (textView11 != null) {
                                textView11.setText(host.nativeAttackIconOffsetY() + "dp");
                            }
                            SharedMemManager sharedMemManagerShmManager = host.shmManager();
                            if (sharedMemManagerShmManager != null) {
                                sharedMemManagerShmManager.setNativeAttackIconAppearance(host.nativeAttackIconOffsetX(), host.nativeAttackIconOffsetY(), host.nativeAttackIconSizePct());
                            }
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar11) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_NATIVE_ATTACK_ICON_OFFSET_Y, host.nativeAttackIconOffsetY()).apply();
                    }
                });
            }
            if (seekBar10 != null) {
                seekBar10.setProgress(host.nativeAttackIconSizePct() - 50);
                if (textView10 != null) {
                    textView10.setText(host.nativeAttackIconSizePct() + "%");
                }
                seekBar10.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.6
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar11) {
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar11, int i, boolean z2) {
                        if (z2) {
                            host.setNativeAttackIconSizePct(Math.max(50, Math.min(TraceCatalog.Feature.AUTO_PICK, i + 50)));
                            TextView textView11 = textView10;
                            if (textView11 != null) {
                                textView11.setText(host.nativeAttackIconSizePct() + "%");
                            }
                            SharedMemManager sharedMemManagerShmManager = host.shmManager();
                            if (sharedMemManagerShmManager != null) {
                                sharedMemManagerShmManager.setNativeAttackIconAppearance(host.nativeAttackIconOffsetX(), host.nativeAttackIconOffsetY(), host.nativeAttackIconSizePct());
                            }
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar11) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_NATIVE_ATTACK_ICON_SIZE, host.nativeAttackIconSizePct()).apply();
                    }
                });
            }
            SeekBar seekBar11 = (SeekBar) view.findViewById(R.id.seekBarInfoOffsetX);
            final TextView textView11 = (TextView) view.findViewById(R.id.tvInfoOffsetX);
            if (seekBar11 != null) {
                seekBar11.setProgress(host.infoOffsetX() + SwapBoardMinimizePolicy.MIN_DOCK_WIDTH_DP);
                if (textView11 != null) {
                    textView11.setText(host.infoOffsetX() + "dp");
                }
                seekBar11.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.7
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar12, int i, boolean z2) {
                        if (z2) {
                            host.setInfoOffsetX(RankWarningPositionMath.clampOffsetDp(i - 120));
                            TextView textView12 = textView11;
                            if (textView12 != null) {
                                textView12.setText(host.infoOffsetX() + "dp");
                            }
                            if (host.rankWarningView() != null) {
                                Host host2 = host;
                                host2.applyRankInfoOffset(host2.rankDisplayDensity());
                                host.updateRankWarningLayout();
                            }
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar12) {
                        ViewParent parent = seekBar12.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar12) {
                        Host host2 = host;
                        host2.saveRankProfileInt(FloatingWindowPrefsKeys.KEY_INFO_OFFSET_X, host2.infoOffsetX());
                        ViewParent parent = seekBar12.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                });
            }
            SeekBar seekBar12 = (SeekBar) view.findViewById(R.id.seekBarRankHeroOffsetX);
            final TextView textView12 = (TextView) view.findViewById(R.id.tvRankHeroOffsetX);
            if (seekBar12 != null) {
                seekBar12.setProgress(host.rankHeroOffsetX() + SwapBoardMinimizePolicy.MIN_DOCK_WIDTH_DP);
                if (textView12 != null) {
                    textView12.setText(host.rankHeroOffsetX() + "dp");
                }
                seekBar12.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.8
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar13, int i, boolean z2) {
                        if (z2) {
                            host.setRankHeroOffsetX(RankWarningPositionMath.clampOffsetDp(i - 120));
                            TextView textView13 = textView12;
                            if (textView13 != null) {
                                textView13.setText(host.rankHeroOffsetX() + "dp");
                            }
                            RankWarningView rankWarningView = host.rankWarningView();
                            if (rankWarningView != null) {
                                rankWarningView.setWarningHeroOffsetPx(Math.round(host.rankHeroOffsetX() * host.rankDisplayDensity()));
                                host.updateRankWarningLayout();
                            }
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar13) {
                        ViewParent parent = seekBar13.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar13) {
                        Host host2 = host;
                        host2.saveRankProfileInt(FloatingWindowPrefsKeys.KEY_RANK_HERO_OFFSET_X, host2.rankHeroOffsetX());
                        ViewParent parent = seekBar13.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                });
            }
            if (seekBar != null) {
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.9
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar13, int i, boolean z2) {
                        if (z2) {
                            host.setOpponentTextPosXProgress(i);
                            TextView textView13 = textView2;
                            if (textView13 != null) {
                                textView13.setText(String.valueOf(i));
                            }
                            host.updateOpponentTextPosition();
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar13) {
                        ViewParent parent = seekBar13.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar13) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_OPPONENT_TEXT_POS_X, host.opponentTextPosXProgress()).apply();
                        ViewParent parent = seekBar13.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                });
            }
            if (seekBar3 != null) {
                final TextView textView13 = textView;
                seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.10
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar13, int i, boolean z2) {
                        if (z2) {
                            host.setOpponentTextPosYProgress(i);
                            TextView textView14 = textView13;
                            if (textView14 != null) {
                                textView14.setText(String.valueOf(i));
                            }
                            host.updateOpponentTextPosition();
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar13) {
                        ViewParent parent = seekBar13.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar13) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_OPPONENT_TEXT_POS_Y, host.opponentTextPosYProgress()).apply();
                        ViewParent parent = seekBar13.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                });
            }
            if (seekBar4 != null) {
                seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.support.OpponentPredictionControlBinder.11
                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onProgressChanged(SeekBar seekBar13, int i, boolean z2) {
                        if (z2) {
                            host.setOpponentTextSizeProgress(i + 50);
                            TextView textView14 = textView4;
                            if (textView14 != null) {
                                textView14.setText(host.opponentTextSizeProgress() + "%");
                            }
                            host.applyOpponentTextSize();
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStartTrackingTouch(SeekBar seekBar13) {
                        ViewParent parent = seekBar13.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }

                    @Override // android.widget.SeekBar.OnSeekBarChangeListener
                    public void onStopTrackingTouch(SeekBar seekBar13) {
                        sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_OPPONENT_TEXT_SIZE, host.opponentTextSizeProgress()).apply();
                        ViewParent parent = seekBar13.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$bind$4(Host host, View view) {
        if (host.rankBoxDragging()) {
            host.exitRankBoxDragMode();
        } else {
            host.enterRankBoxDragMode();
        }
    }

    static /* synthetic */ void lambda$bind$9(Host host, View view) {
        if (host.opponentTextDragging()) {
            host.exitOpponentTextDragMode();
        } else {
            host.enterOpponentTextDragMode();
        }
    }

    static /* synthetic */ void lambda$bind$10(Host host, SharedPreferences sharedPreferences, CompoundButton compoundButton, boolean z) {
        host.setShowAllMatchups(z);
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_SHOW_ALL_MATCHUPS, z).apply();
    }

    static /* synthetic */ void lambda$bind$11(Host host, SharedPreferences sharedPreferences, LinearLayout linearLayout, LinearLayout linearLayout2, Switch r5, View view, Context context, CompoundButton compoundButton, boolean z) {
        host.setOpponentHighlightEnabled(z);
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_HIGHLIGHT, z).apply();
        if (linearLayout != null) {
            linearLayout.setVisibility(0);
        }
        if (linearLayout2 != null) {
            linearLayout2.setVisibility((z && host.opponentShowText()) ? 0 : 8);
        }
        RankWarningView rankWarningView = host.rankWarningView();
        if (rankWarningView != null) {
            rankWarningView.setAttackIconMode(host.opponentAttackIcon() && z);
            if (!z) {
                int opponentId = rankWarningView.getOpponentId();
                rankWarningView.setShowOpponent(false);
                rankWarningView.setOpponentId(-1);
                host.hideOpponentTextOverlay();
                if (host.selectedBoardPlayerId() == opponentId && opponentId >= 0) {
                    host.setSelectedBoardPlayerId(-1);
                    ChessBoardView chessBoardView = host.chessBoardView();
                    if (chessBoardView != null) {
                        chessBoardView.clearData();
                    }
                    host.hideBoardOverlay();
                }
                host.setBoardPlayerManuallySelected(false);
                host.setLastAutoSelectedOpponentId(-1);
                host.setDebugBoxMode(false);
                rankWarningView.setDebugMode(false);
                rankWarningView.setFloatingEconomyVisible(false);
            } else {
                if (host.opponentShowBox() || host.opponentAttackIcon()) {
                    rankWarningView.setShowOpponent(OpponentDisplayPolicy.shouldShowOpponentAnchor(true, host.opponentShowBox(), host.opponentAttackIcon()));
                    if (!host.isRankWarningVisible()) {
                        host.showRankWarningOverlay();
                    }
                    if (host.opponentShowBox() && !host.boardOverlayEnabled() && r5 != null) {
                        host.setBoardOverlayEnabled(true);
                        host.setSuppressBoardStationToast(true);
                        try {
                            r5.setChecked(true);
                            sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_BOARD_OVERLAY_ENABLED, true).apply();
                            RadioGroup radioGroup = view == null ? null : (RadioGroup) view.findViewById(R.id.groupBoardPlaceholder);
                            if (radioGroup != null && radioGroup.getCheckedRadioButtonId() != R.id.radioBoardPlaceholderImage) {
                                radioGroup.check(R.id.radioBoardPlaceholderImage);
                            }
                            host.setSuppressBoardStationToast(false);
                            FloatingWindowService.safeToast(context, "已自动开启棋盘站位显示", 0);
                        } catch (Throwable th) {
                            host.setSuppressBoardStationToast(false);
                            throw th;
                        }
                    }
                    host.setDebugBoxMode(true);
                    rankWarningView.setDebugMode(true);
                    rankWarningView.setFloatingEconomyVisible(host.opponentShowBox());
                    host.updateRankWarningLayout();
                } else {
                    rankWarningView.setShowOpponent(false);
                    host.showOpponentTextOverlay();
                    host.setDebugBoxMode(false);
                    rankWarningView.setDebugMode(false);
                    rankWarningView.setFloatingEconomyVisible(false);
                }
                host.startAutoRefresh();
                host.setBoardPlayerManuallySelected(false);
            }
        }
        host.applyOpponentDisplayState();
    }

    static /* synthetic */ void lambda$bind$12(Host host, SharedPreferences sharedPreferences, CompoundButton compoundButton, boolean z) {
        host.setOpponentShowBox(z);
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_SHOW_BOX, z).apply();
        host.applyOpponentDisplayState();
    }

    static /* synthetic */ void lambda$bind$13(Host host, SharedPreferences sharedPreferences, CompoundButton compoundButton, boolean z) {
        host.setOpponentShowText(z);
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_SHOW_TEXT, z).apply();
        host.applyOpponentDisplayState();
    }

    static /* synthetic */ void lambda$bind$15(Host host, final SharedPreferences sharedPreferences, Switch r4, CompoundButton.OnCheckedChangeListener[] onCheckedChangeListenerArr, CompoundButton compoundButton, boolean z) {
        OpponentDisplayPolicy.State stateApplyAttackToggle = OpponentDisplayPolicy.applyAttackToggle(host.opponentHighlightEnabled(), host.opponentAttackIcon(), z, new OpponentDisplayPolicy.Writer() { // from class: com.android.support.OpponentPredictionControlBinder$$ExternalSyntheticLambda7
            @Override // com.android.support.OpponentDisplayPolicy.Writer
            public final void write(boolean z2, boolean z3) {
                sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_HIGHLIGHT, z2).putBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_ATTACK_ICON, z3).apply();
            }
        });
        host.setOpponentHighlightEnabled(stateApplyAttackToggle.highlightEnabled);
        host.setOpponentAttackIcon(stateApplyAttackToggle.attackIconEnabled);
        if (r4 != null && r4.isChecked() != stateApplyAttackToggle.highlightEnabled) {
            r4.setOnCheckedChangeListener(null);
            r4.setChecked(stateApplyAttackToggle.highlightEnabled);
            r4.setOnCheckedChangeListener(onCheckedChangeListenerArr[0]);
        }
        host.applyOpponentDisplayState();
    }

    static /* synthetic */ void lambda$bind$16(Host host, SharedPreferences sharedPreferences, CompoundButton compoundButton, boolean z) {
        host.setNativeAttackIconEnabled(z);
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_NATIVE_ATTACK_ICON_ENABLED, z).apply();
        SharedMemManager sharedMemManagerShmManager = host.shmManager();
        if (sharedMemManagerShmManager != null) {
            sharedMemManagerShmManager.setNativeAttackIconEnabled(z);
        }
    }
}
