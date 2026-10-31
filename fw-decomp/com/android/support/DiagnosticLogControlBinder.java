package com.android.support;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.function.Consumer;

/* loaded from: classes.dex */
final class DiagnosticLogControlBinder {
    private DiagnosticLogControlBinder() {
    }

    static void bind(View view, final Context context, Consumer<TextView> consumer, final Runnable runnable, final Runnable runnable2, final Runnable runnable3) {
        Button button = (Button) view.findViewById(R.id.btnExportLog);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.DiagnosticLogControlBinder$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    DiagnosticLogControlBinder.lambda$bind$0(context, runnable, view2);
                }
            });
        }
        consumer.accept((TextView) view.findViewById(R.id.tvContinuousLogStatus));
        Switch r2 = (Switch) view.findViewById(R.id.switchContinuousLog);
        if (r2 != null) {
            r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.DiagnosticLogControlBinder$$ExternalSyntheticLambda1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    DiagnosticLogControlBinder.lambda$bind$1(runnable2, context, runnable3, compoundButton, z);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$0(Context context, Runnable runnable, View view) {
        try {
            FloatingWindowService.safeToast(context, "正在导出日志...", 0);
        } catch (Exception unused) {
        }
        runnable.run();
    }

    static /* synthetic */ void lambda$bind$1(Runnable runnable, Context context, Runnable runnable2, CompoundButton compoundButton, boolean z) {
        try {
            if (z) {
                runnable.run();
                FloatingWindowService.safeToast(context, "持续日志已开启，文件保存到 Download/", 0);
            } else {
                runnable2.run();
                FloatingWindowService.safeToast(context, "持续日志已关闭", 0);
            }
        } catch (Exception unused) {
        }
    }
}
