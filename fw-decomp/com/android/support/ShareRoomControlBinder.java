package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.android.support.ShareRoomController;
import com.android.support.ShareRoomState;

/* loaded from: classes.dex */
final class ShareRoomControlBinder {
    private static final String TAG = "FloatingWindowService";

    interface Host {
        boolean canShowVerifiedFeatures();

        void copyShareEntry();

        void copyShareRoom();

        void ensureShareRoomController();

        void openSharePage();

        void renderShareRoomState(ShareRoomController.ViewState viewState);

        void setShareViews(Switch r1, TextView textView, TextView textView2, Button button, Button button2, Button button3);

        void setUpdatingShareSwitch(boolean z);

        ShareRoomController shareRoomController();

        SharedMemManager sharedMemManager();

        boolean updatingShareSwitch();
    }

    private ShareRoomControlBinder() {
    }

    static void bind(View view, final Context context, final SharedPreferences sharedPreferences, final Host host) {
        final Switch r2 = (Switch) view.findViewById(R.id.switchWebShare);
        TextView textView = (TextView) view.findViewById(R.id.tvShareRoomCode);
        TextView textView2 = (TextView) view.findViewById(R.id.tvShareStatus);
        Button button = (Button) view.findViewById(R.id.btnCopyShareRoom);
        Button button2 = (Button) view.findViewById(R.id.btnCopyShareEntry);
        Button button3 = (Button) view.findViewById(R.id.btnOpenSharePage);
        host.setShareViews(r2, textView, textView2, button, button2, button3);
        if (r2 == null || textView == null || textView2 == null || button == null || button2 == null || button3 == null) {
            Log.w(TAG, "网页共享控件初始化不完整");
            return;
        }
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ShareRoomControlBinder$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                host.copyShareEntry();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ShareRoomControlBinder$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                host.openSharePage();
            }
        });
        if (host.sharedMemManager() == null) {
            Log.w(TAG, "网页共享数据源尚未初始化");
            return;
        }
        host.ensureShareRoomController();
        boolean z = sharedPreferences != null && sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_WEB_SHARE_ENABLED, false) && host.canShowVerifiedFeatures();
        host.setUpdatingShareSwitch(true);
        try {
            r2.setChecked(z);
            host.setUpdatingShareSwitch(false);
            r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.ShareRoomControlBinder$$ExternalSyntheticLambda2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    ShareRoomControlBinder.lambda$bind$2(host, sharedPreferences, r2, context, compoundButton, z2);
                }
            });
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ShareRoomControlBinder$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.copyShareRoom();
                }
            });
            if (z) {
                textView.setText("XF---");
                textView2.setText("正在创建共享房间");
                textView2.setTextColor(-734614);
                button.setEnabled(false);
                host.shareRoomController().start();
                return;
            }
            host.renderShareRoomState(new ShareRoomController.ViewState(false, ShareRoomState.Phase.DISABLED, "", "共享已关闭"));
        } catch (Throwable th) {
            host.setUpdatingShareSwitch(false);
            throw th;
        }
    }

    static /* synthetic */ void lambda$bind$2(Host host, SharedPreferences sharedPreferences, Switch r3, Context context, CompoundButton compoundButton, boolean z) {
        if (host.updatingShareSwitch() || host.shareRoomController() == null) {
            return;
        }
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_WEB_SHARE_ENABLED, z).apply();
        }
        if (!z || host.canShowVerifiedFeatures()) {
            if (z) {
                host.shareRoomController().start();
                return;
            } else {
                host.shareRoomController().stop();
                return;
            }
        }
        host.setUpdatingShareSwitch(true);
        try {
            r3.setChecked(false);
            host.setUpdatingShareSwitch(false);
            host.renderShareRoomState(new ShareRoomController.ViewState(false, ShareRoomState.Phase.DISABLED, "", "请先完成授权验证"));
            FloatingWindowService.safeToast(context, "请先完成授权验证", 0);
        } catch (Throwable th) {
            host.setUpdatingShareSwitch(false);
            throw th;
        }
    }
}
