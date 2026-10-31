package com.android.support;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

/* loaded from: classes.dex */
final class BlockDialogsControlBinder {
    private static final String TAG = "FloatingWindowService";

    private BlockDialogsControlBinder() {
    }

    static void bind(View view, final SharedMemManager sharedMemManager) {
        Switch r2 = (Switch) view.findViewById(R.id.switchBlockDialogs);
        if (r2 == null) {
            return;
        }
        r2.setChecked(false);
        if (sharedMemManager != null) {
            sharedMemManager.setBlockDialogs(false);
        }
        Dbg.d(TAG, "拦截弹窗为运行时开关，不保存配置，默认关闭");
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.BlockDialogsControlBinder$$ExternalSyntheticLambda0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                BlockDialogsControlBinder.lambda$bind$0(sharedMemManager, compoundButton, z);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(SharedMemManager sharedMemManager, CompoundButton compoundButton, boolean z) {
        Dbg.d(TAG, "Switch BlockDialogs changed: " + z);
        if (sharedMemManager != null) {
            sharedMemManager.setBlockDialogs(z);
        }
        Dbg.d(TAG, "拦截弹窗仅本次运行生效，不保存配置");
    }
}
