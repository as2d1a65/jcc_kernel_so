package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/* loaded from: classes.dex */
final class DisplayModeToggleControlBinder {
    static final int BOARD_PLACEHOLDER_ENTITY = 2;
    static final int BOARD_PLACEHOLDER_IMAGE = 1;
    static final int BOARD_PLACEHOLDER_OFF = 0;
    static final int OPP_SRC_IMAGE = 1;
    static final int OPP_SRC_NATIVE = 2;
    static final int OPP_SRC_OFF = 0;
    static final int SHOP_REMAIN_IMAGE = 1;
    static final int SHOP_REMAIN_NATIVE = 2;
    static final int SHOP_REMAIN_OFF = 0;

    interface Host {
        void applyAutoSelectOwnedHeroes();

        void applyBoardHeroHeadOverlayMode(boolean z, boolean z2);

        void applyBoardPlaceholderMode(int i, boolean z);

        void applyNativePlayerEconNameMode(boolean z, boolean z2);

        void applyOpponentPredictSource(int i, boolean z);

        void applyShopRemainMode(int i, boolean z);

        void bindHudAppearancePanel();

        void clearAutoSelectedHeroIds();

        int resolveBoardPlaceholderMode();

        int resolveOpponentPredictSource();

        int resolveShopRemainMode();

        void setOpponentPredictSourceMode(int i);

        void setSuppressPredictSourceCallback(boolean z);

        boolean suppressPredictSourceCallback();
    }

    private DisplayModeToggleControlBinder() {
    }

    static void bind(View view, final Context context, final SharedPreferences sharedPreferences, final BooleanSupplier booleanSupplier, final Consumer<Boolean> consumer, final Host host) {
        int i;
        int i2;
        int i3;
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.groupBoardPlaceholder);
        if (radioGroup != null) {
            int iResolveBoardPlaceholderMode = host.resolveBoardPlaceholderMode();
            if (iResolveBoardPlaceholderMode == 2) {
                i3 = R.id.radioBoardPlaceholderEntity;
            } else if (iResolveBoardPlaceholderMode == 0) {
                i3 = R.id.radioBoardPlaceholderOff;
            } else {
                i3 = R.id.radioBoardPlaceholderImage;
            }
            radioGroup.check(i3);
            host.applyBoardPlaceholderMode(iResolveBoardPlaceholderMode, false);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.android.support.DisplayModeToggleControlBinder$$ExternalSyntheticLambda0
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup2, int i4) {
                    DisplayModeToggleControlBinder.lambda$bind$0(host, booleanSupplier, context, radioGroup2, i4);
                }
            });
        }
        RadioGroup radioGroup2 = (RadioGroup) view.findViewById(R.id.groupShopRemain);
        if (radioGroup2 != null) {
            int iResolveShopRemainMode = host.resolveShopRemainMode();
            if (iResolveShopRemainMode == 2) {
                i2 = R.id.radioShopRemainNative;
            } else if (iResolveShopRemainMode == 0) {
                i2 = R.id.radioShopRemainOff;
            } else {
                i2 = R.id.radioShopRemainImage;
            }
            radioGroup2.check(i2);
            host.applyShopRemainMode(iResolveShopRemainMode, false);
            radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.android.support.DisplayModeToggleControlBinder$$ExternalSyntheticLambda1
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup3, int i4) {
                    DisplayModeToggleControlBinder.lambda$bind$1(host, context, radioGroup3, i4);
                }
            });
        }
        Switch r8 = (Switch) view.findViewById(R.id.switchNativePlayerEconName);
        if (r8 != null) {
            boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_NATIVE_PLAYER_ECON_NAME, true);
            r8.setChecked(z);
            host.applyNativePlayerEconNameMode(z, false);
            r8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.DisplayModeToggleControlBinder$$ExternalSyntheticLambda2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    DisplayModeToggleControlBinder.lambda$bind$2(host, context, compoundButton, z2);
                }
            });
        }
        RadioGroup radioGroup3 = (RadioGroup) view.findViewById(R.id.groupOpponentPredictSource);
        if (radioGroup3 != null) {
            int iResolveOpponentPredictSource = host.resolveOpponentPredictSource();
            host.setOpponentPredictSourceMode(iResolveOpponentPredictSource);
            host.setSuppressPredictSourceCallback(true);
            if (iResolveOpponentPredictSource == 2) {
                i = R.id.radioPredictSourceNative;
            } else if (iResolveOpponentPredictSource == 0) {
                i = R.id.radioPredictSourceOff;
            } else {
                i = R.id.radioPredictSourceImage;
            }
            radioGroup3.check(i);
            host.setSuppressPredictSourceCallback(false);
            host.applyOpponentPredictSource(iResolveOpponentPredictSource, false);
            radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.android.support.DisplayModeToggleControlBinder$$ExternalSyntheticLambda3
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup4, int i4) {
                    DisplayModeToggleControlBinder.lambda$bind$3(host, context, radioGroup4, i4);
                }
            });
        }
        host.bindHudAppearancePanel();
        Switch r82 = (Switch) view.findViewById(R.id.switchAutoSelectOwned);
        if (r82 != null) {
            boolean z2 = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_AUTO_SELECT_OWNED, false);
            consumer.accept(Boolean.valueOf(z2));
            r82.setChecked(z2);
            r82.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.DisplayModeToggleControlBinder$$ExternalSyntheticLambda4
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z3) {
                    DisplayModeToggleControlBinder.lambda$bind$4(consumer, sharedPreferences, host, context, compoundButton, z3);
                }
            });
        }
        Switch r5 = (Switch) view.findViewById(R.id.switchBoardHeroHeadOverlay);
        if (r5 != null) {
            boolean z3 = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_BOARD_HERO_HEAD_OVERLAY, false);
            r5.setChecked(z3);
            host.applyBoardHeroHeadOverlayMode(z3, false);
            r5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.DisplayModeToggleControlBinder$$ExternalSyntheticLambda5
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z4) {
                    DisplayModeToggleControlBinder.lambda$bind$5(host, context, compoundButton, z4);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$0(Host host, BooleanSupplier booleanSupplier, Context context, RadioGroup radioGroup, int i) {
        int i2;
        String str;
        if (i == R.id.radioBoardPlaceholderEntity) {
            i2 = 2;
        } else {
            i2 = i == R.id.radioBoardPlaceholderOff ? 0 : 1;
        }
        host.applyBoardPlaceholderMode(i2, true);
        if (booleanSupplier.getAsBoolean()) {
            return;
        }
        if (i2 == 2) {
            str = "棋盘占位：实体占位";
        } else if (i2 == 0) {
            str = "棋盘占位：已关闭";
        } else {
            str = "棋盘占位：图片版（棋盘站位悬浮显示）";
        }
        FloatingWindowService.safeToast(context, str, 0);
    }

    static /* synthetic */ void lambda$bind$1(Host host, Context context, RadioGroup radioGroup, int i) {
        int i2;
        String str;
        if (i == R.id.radioShopRemainNative) {
            i2 = 2;
        } else {
            i2 = i == R.id.radioShopRemainOff ? 0 : 1;
        }
        host.applyShopRemainMode(i2, true);
        if (i2 == 2) {
            str = "商店剩余：内置版（画在商店金币旁）";
        } else if (i2 == 0) {
            str = "商店剩余：已关闭";
        } else {
            str = "商店剩余：图片版（悬浮层，可调位置）";
        }
        FloatingWindowService.safeToast(context, str, 0);
    }

    static /* synthetic */ void lambda$bind$2(Host host, Context context, CompoundButton compoundButton, boolean z) {
        String str;
        host.applyNativePlayerEconNameMode(z, true);
        if (z) {
            str = "游戏内显示经济 已开启：冰块等级 + 琥珀金币";
        } else {
            str = "游戏内显示经济 已关闭（名字已还原）";
        }
        FloatingWindowService.safeToast(context, str, 0);
    }

    static /* synthetic */ void lambda$bind$3(Host host, Context context, RadioGroup radioGroup, int i) {
        int i2;
        String str;
        if (host.suppressPredictSourceCallback()) {
            return;
        }
        if (i == R.id.radioPredictSourceNative) {
            i2 = 2;
        } else {
            i2 = i == R.id.radioPredictSourceOff ? 0 : 1;
        }
        host.applyOpponentPredictSource(i2, true);
        if (i2 == 2) {
            str = "对手预测：内置版（我与对手同色 + 红色座位标记）";
        } else if (i2 == 0) {
            str = "对手预测：已关闭";
        } else {
            str = "对手预测：图片版（悬浮，下方可细调方框/文字）";
        }
        FloatingWindowService.safeToast(context, str, 0);
    }

    static /* synthetic */ void lambda$bind$4(Consumer consumer, SharedPreferences sharedPreferences, Host host, Context context, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_AUTO_SELECT_OWNED, z).apply();
        if (z) {
            host.applyAutoSelectOwnedHeroes();
            FloatingWindowService.safeToast(context, "自动选中已有英雄 已开启", 0);
        } else {
            host.clearAutoSelectedHeroIds();
            FloatingWindowService.safeToast(context, "自动选中已有英雄 已关闭", 0);
        }
    }

    static /* synthetic */ void lambda$bind$5(Host host, Context context, CompoundButton compoundButton, boolean z) {
        host.applyBoardHeroHeadOverlayMode(z, true);
        FloatingWindowService.safeToast(context, z ? "棋子脚下牌库 已开启" : "棋子脚下牌库 已关闭", 0);
    }
}
