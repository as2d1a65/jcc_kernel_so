package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.android.support.InitializeSessionControlBinder;
import com.android.support.SharedMemManager;
import java.util.Objects;

/* loaded from: classes.dex */
final class InitializeSessionControlBinder {
    private static final String TAG = "FloatingWindowService";

    interface Host {
        void activateSessionFromInitialize();

        void animateButtonClick(View view);

        void applyOpponentCardingConfig(OpponentCardingConfig opponentCardingConfig, boolean z);

        void deactivateSessionFromInitialize();

        void flog(String str);

        void hideOpponentCardingWindow(boolean z);

        boolean isOpponentCardingWindowVisible();

        boolean isUpdatingAutoPickShortcut();

        void onAllInStatusReported(boolean z, int i);

        OpponentCardingUiState opponentCardingUiState();

        void refreshOpponentCardingUi();

        void saveBooleanPrefBatched(String str, boolean z);

        void setAutoPickStateCache(boolean z);

        void setInitializeButtonRef(Button button);

        void setOpponentCardingStatus(OpponentCardingStatus opponentCardingStatus);

        void setOpponentCardingUiState(OpponentCardingUiState opponentCardingUiState);

        void setOpponentCardingViews(Switch r1, TextView textView, TextView textView2, View view);

        void setSessionActiveLocal(boolean z);

        void setSessionAutoActivationSuppressed(boolean z);

        SharedMemManager sharedMemManager();

        void showOpponentCardingWindow();

        void updateInitButtonState(Button button, boolean z);
    }

    private InitializeSessionControlBinder() {
    }

    static void bind(View view, final Context context, SharedPreferences sharedPreferences, final Host host) {
        final Button button = (Button) view.findViewById(R.id.btnInitialize);
        host.setInitializeButtonRef(button);
        if (button != null) {
            SharedMemManager sharedMemManager = host.sharedMemManager();
            boolean zIsSessionActive = sharedMemManager != null ? sharedMemManager.isSessionActive() : false;
            host.setSessionActiveLocal(zIsSessionActive);
            host.updateInitButtonState(button, zIsSessionActive);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.InitializeSessionControlBinder$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    InitializeSessionControlBinder.lambda$bind$0(host, context, button, view2);
                }
            });
        }
        Switch r0 = (Switch) view.findViewById(R.id.switchAutoPickCard);
        Dbg.d(TAG, "switchAutoPickCard found: " + (r0 != null));
        if (r0 == null) {
            Log.e(TAG, "ERROR: switchAutoPickCard is null!");
        } else {
            boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_AUTO_PICK_CARD, false);
            host.setAutoPickStateCache(z);
            r0.setChecked(z);
            SharedMemManager sharedMemManager2 = host.sharedMemManager();
            if (sharedMemManager2 != null) {
                sharedMemManager2.setAutoPickCard(z);
            }
            Dbg.d(TAG, "加载保存的配置: 自动拿牌=" + z);
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.InitializeSessionControlBinder$$ExternalSyntheticLambda1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    InitializeSessionControlBinder.lambda$bind$1(host, context, compoundButton, z2);
                }
            });
        }
        int i = sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_OPPONENT_CARDING_STRATEGY, 1);
        OpponentCardingConfig opponentCardingConfigCreate = OpponentCardingConfig.create(sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_OPPONENT_CARDING_ENABLED, false), sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_OPPONENT_CARDING_THRESHOLD, 6), sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_OPPONENT_CARDING_COST_MASK, 31), sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_OPPONENT_CARDING_RESERVE_GOLD, 20), 1, sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_OPPONENT_CARDING_MAX_OWNED, 3));
        if (i != 1) {
            sharedPreferences.edit().putInt(FloatingWindowPrefsKeys.KEY_OPPONENT_CARDING_STRATEGY, 1).apply();
        }
        host.setOpponentCardingUiState(new OpponentCardingUiState(opponentCardingConfigCreate));
        Switch r11 = (Switch) view.findViewById(R.id.switchOpponentCarding);
        TextView textView = (TextView) view.findViewById(R.id.tvOpponentCardingSummary);
        TextView textView2 = (TextView) view.findViewById(R.id.tvOpponentCardingStatus);
        View viewFindViewById = view.findViewById(R.id.cardOpponentCarding);
        host.setOpponentCardingViews(r11, textView, textView2, viewFindViewById);
        if (r11 != null) {
            r11.setChecked(opponentCardingConfigCreate.isEnabled());
            r11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.InitializeSessionControlBinder$$ExternalSyntheticLambda2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    InitializeSessionControlBinder.Host host2 = host;
                    host2.applyOpponentCardingConfig(host2.opponentCardingUiState().withEnabled(z2).getConfig(), true);
                }
            });
        }
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.InitializeSessionControlBinder$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    InitializeSessionControlBinder.lambda$bind$3(host, view2);
                }
            });
        }
        host.refreshOpponentCardingUi();
        SharedMemManager sharedMemManager3 = host.sharedMemManager();
        if (sharedMemManager3 != null) {
            sharedMemManager3.setOpponentCardingConfig(opponentCardingConfigCreate);
            host.setOpponentCardingStatus(sharedMemManager3.getOpponentCardingStatus());
            sharedMemManager3.setOpponentCardingStatusListener(new SharedMemManager.OpponentCardingStatusListener() { // from class: com.android.support.InitializeSessionControlBinder$$ExternalSyntheticLambda4
                @Override // com.android.support.SharedMemManager.OpponentCardingStatusListener
                public final void onOpponentCardingStatus(OpponentCardingStatus opponentCardingStatus) {
                    InitializeSessionControlBinder.lambda$bind$4(host, opponentCardingStatus);
                }
            });
            Objects.requireNonNull(host);
            sharedMemManager3.setAllInStatusListener(new SharedMemManager.AllInStatusListener() { // from class: com.android.support.InitializeSessionControlBinder$$ExternalSyntheticLambda5
                @Override // com.android.support.SharedMemManager.AllInStatusListener
                public final void onAllInStatus(boolean z2, int i2) {
                    host.onAllInStatusReported(z2, i2);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$0(Host host, Context context, Button button, View view) {
        host.animateButtonClick(view);
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (sharedMemManager == null) {
            FloatingWindowService.safeToast(context, "共享内存未连接", 0);
            return;
        }
        boolean zIsSessionActive = sharedMemManager.isSessionActive();
        if (!zIsSessionActive && !sharedMemManager.isCppConnected()) {
            host.setSessionActiveLocal(false);
            host.updateInitButtonState(button, false);
            FloatingWindowService.safeToast(context, "C++ 尚未连接，请稍后再初始化", 0);
            return;
        }
        if (zIsSessionActive) {
            sharedMemManager.setSessionActive(false);
            host.setSessionActiveLocal(false);
            host.setSessionAutoActivationSuppressed(true);
            host.deactivateSessionFromInitialize();
            host.updateInitButtonState(button, false);
            FloatingWindowService.safeToast(context, "功能已关闭", 0);
            host.flog("★ 用户手动关闭会话主开关");
            return;
        }
        host.setSessionAutoActivationSuppressed(false);
        sharedMemManager.triggerReinitIL2CPP();
        sharedMemManager.setSessionActive(true);
        host.setSessionActiveLocal(true);
        host.activateSessionFromInitialize();
        host.updateInitButtonState(button, true);
        FloatingWindowService.safeToast(context, "初始化完成，所有功能已激活", 0);
        host.flog("★ 用户点击初始化，会话主开关 ON");
    }

    static /* synthetic */ void lambda$bind$1(Host host, Context context, CompoundButton compoundButton, boolean z) {
        if (host.isUpdatingAutoPickShortcut()) {
            return;
        }
        Dbg.d(TAG, "Switch AutoPickCard changed: " + z);
        host.setAutoPickStateCache(z);
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (sharedMemManager != null) {
            sharedMemManager.setAutoPickCard(z);
            host.saveBooleanPrefBatched(FloatingWindowPrefsKeys.KEY_AUTO_PICK_CARD, z);
            Dbg.d(TAG, "配置已保存: 自动拿牌=" + z);
        }
        FloatingWindowService.safeToast(context, z ? "自动拿牌已开启" : "自动拿牌已关闭", 0);
    }

    static /* synthetic */ void lambda$bind$3(Host host, View view) {
        if (host.isOpponentCardingWindowVisible()) {
            host.hideOpponentCardingWindow(true);
        } else {
            host.showOpponentCardingWindow();
        }
    }

    static /* synthetic */ void lambda$bind$4(Host host, OpponentCardingStatus opponentCardingStatus) {
        host.setOpponentCardingStatus(opponentCardingStatus);
        host.refreshOpponentCardingUi();
    }
}
