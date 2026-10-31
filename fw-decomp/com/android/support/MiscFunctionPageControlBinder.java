package com.android.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class MiscFunctionPageControlBinder {
    private static final String TAG = "FloatingWindowService";

    private MiscFunctionPageControlBinder() {
    }

    static void bind(View view, final Context context, SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, BooleanSupplier booleanSupplier, final Consumer<Boolean> consumer, final BiConsumer<String, Boolean> biConsumer, final Runnable runnable, final Consumer<View> consumer2) {
        Switch r0 = (Switch) view.findViewById(R.id.switchQuickActionVisible);
        if (r0 == null) {
            Log.w(TAG, "switchQuickActionVisible not found in layout");
        } else {
            boolean asBoolean = booleanSupplier.getAsBoolean();
            consumer.accept(Boolean.valueOf(asBoolean));
            r0.setChecked(asBoolean);
            Dbg.d(TAG, "加载保存的配置: 快捷按钮显示=" + asBoolean);
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.MiscFunctionPageControlBinder$$ExternalSyntheticLambda1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    MiscFunctionPageControlBinder.lambda$bind$0(consumer, biConsumer, runnable, context, compoundButton, z);
                }
            });
        }
        Switch r8 = (Switch) view.findViewById(R.id.switchIOSPlatformFake);
        if (r8 == null) {
            Log.w(TAG, "switchIOSPlatformFake not found in layout");
        } else {
            r8.setEnabled(true);
            boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_IOS_PLATFORM_FAKE, false);
            r8.setChecked(z);
            SharedMemManager sharedMemManager = supplier.get();
            if (sharedMemManager != null) {
                sharedMemManager.setIOSPlatformFake(z);
            }
            Dbg.d(TAG, "加载保存的配置: iOS转区=" + z);
            r8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.MiscFunctionPageControlBinder$$ExternalSyntheticLambda2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    MiscFunctionPageControlBinder.lambda$bind$1(biConsumer, supplier, context, compoundButton, z2);
                }
            });
        }
        Button button = (Button) view.findViewById(R.id.btnExitGame);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MiscFunctionPageControlBinder$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    MiscFunctionPageControlBinder.lambda$bind$3(consumer2, supplier, context, view2);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$0(Consumer consumer, BiConsumer biConsumer, Runnable runnable, Context context, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_QUICK_ACTION_VISIBLE, Boolean.valueOf(z));
        runnable.run();
        Dbg.d(TAG, "配置已保存: 快捷按钮显示=" + z);
        FloatingWindowService.safeToast(context, z ? "快捷按钮已显示" : "快捷按钮已隐藏", 0);
    }

    static /* synthetic */ void lambda$bind$1(BiConsumer biConsumer, Supplier supplier, Context context, CompoundButton compoundButton, boolean z) {
        String str;
        Dbg.d(TAG, "Switch iOS转区 changed: " + z);
        biConsumer.accept(FloatingWindowPrefsKeys.KEY_IOS_PLATFORM_FAKE, Boolean.valueOf(z));
        SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setIOSPlatformFake(z);
            Dbg.d(TAG, "配置已保存: iOS转区=" + z);
        }
        if (sharedMemManager == null) {
            str = "设置已保存，连接游戏后应用";
        } else {
            str = z ? "iOS区服标识已开启，请返回登录页选区" : "iOS区服标识已关闭";
        }
        FloatingWindowService.safeToast(context, str, 0);
    }

    static /* synthetic */ void lambda$bind$3(Consumer consumer, final Supplier supplier, final Context context, View view) {
        consumer.accept(view);
        if (((SharedMemManager) supplier.get()) == null) {
            FloatingWindowService.safeToast(context, "共享内存未连接", 0);
            return;
        }
        AlertDialog alertDialogCreate = new AlertDialog.Builder(context, android.R.style.Theme.DeviceDefault.Dialog.Alert).setTitle("退出对局").setMessage("确定要退出当前对局吗？\n退出后将直接淘汰。").setPositiveButton("确认退出", new DialogInterface.OnClickListener() { // from class: com.android.support.MiscFunctionPageControlBinder$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MiscFunctionPageControlBinder.lambda$bind$2(supplier, context, dialogInterface, i);
            }
        }).setNegativeButton("取消", (DialogInterface.OnClickListener) null).setCancelable(true).create();
        if (alertDialogCreate.getWindow() != null) {
            alertDialogCreate.getWindow().setType(2038);
        }
        alertDialogCreate.show();
    }

    static /* synthetic */ void lambda$bind$2(Supplier supplier, Context context, DialogInterface dialogInterface, int i) {
        ((SharedMemManager) supplier.get()).triggerExitGame();
        FloatingWindowService.safeToast(context, "已发送退出对局请求", 0);
    }
}
