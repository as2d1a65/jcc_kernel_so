package com.android.support;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;

/* loaded from: classes.dex */
final class NativeStartupFailureView {
    private NativeStartupFailureView() {
    }

    static void show(final Activity activity) {
        int iRound = Math.round(activity.getResources().getDisplayMetrics().density * 24.0f);
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(16);
        linearLayout.setPadding(iRound, iRound, iRound, iRound);
        TextView textView = new TextView(activity);
        textView.setText("启动组件加载失败");
        textView.setTextSize(22.0f);
        linearLayout.addView(textView);
        TextView textView2 = new TextView(activity);
        textView2.setText("请重新安装完整安装包后再打开。如果仍然出现此提示，请复制错误信息发给客服。");
        textView2.setTextSize(16.0f);
        textView2.setPadding(0, iRound, 0, iRound);
        linearLayout.addView(textView2);
        Button button = new Button(activity);
        button.setText("复制错误信息");
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.NativeStartupFailureView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NativeStartupFailureView.lambda$show$0(activity, view);
            }
        });
        linearLayout.addView(button);
        activity.setContentView(linearLayout);
    }

    static /* synthetic */ void lambda$show$0(Activity activity, View view) {
        Throwable thLoadFailure = NativeLibraryLoader.loadFailure();
        String str = "8.0 / 12\n" + Build.MANUFACTURER + " " + Build.MODEL + "\nAndroid " + Build.VERSION.RELEASE + " / SDK " + Build.VERSION.SDK_INT + "\nABI " + Arrays.toString(Build.SUPPORTED_ABIS) + "\n" + (thLoadFailure == null ? "runtime unavailable" : thLoadFailure.toString());
        ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService("clipboard");
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("启动错误", str));
            Toast.makeText(activity, "错误信息已复制", 0).show();
        }
    }
}
