package com.android.support;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
final class ConfigManagementControlBinder {

    interface Host {
        void animateButtonClick(View view);

        String exportConfigToDownload();

        void refreshConfigList();

        void resetAllConfig();

        void restartFloatingWindow();

        boolean saveConfigToFile(String str);

        void setupModifyEditText(EditText editText);

        void showImportConfigDialog();
    }

    private ConfigManagementControlBinder() {
    }

    static void bind(View view, final Context context, final Host host) {
        if (view == null) {
            return;
        }
        final EditText editText = (EditText) view.findViewById(R.id.etConfigName);
        Button button = (Button) view.findViewById(R.id.btnSaveConfig);
        Button button2 = (Button) view.findViewById(R.id.btnLoadConfig);
        Button button3 = (Button) view.findViewById(R.id.btnResetConfig);
        if (editText != null) {
            host.setupModifyEditText(editText);
        }
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ConfigManagementControlBinder$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ConfigManagementControlBinder.lambda$bind$0(editText, host, context, view2);
                }
            });
        }
        if (button2 != null) {
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ConfigManagementControlBinder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ConfigManagementControlBinder.lambda$bind$1(host, context, view2);
                }
            });
        }
        if (button3 != null) {
            button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ConfigManagementControlBinder$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ConfigManagementControlBinder.lambda$bind$2(host, context, view2);
                }
            });
        }
        Button button4 = (Button) view.findViewById(R.id.btnExportConfig);
        Button button5 = (Button) view.findViewById(R.id.btnImportConfig);
        if (button4 != null) {
            button4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ConfigManagementControlBinder$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ConfigManagementControlBinder.lambda$bind$3(host, context, view2);
                }
            });
        }
        if (button5 != null) {
            button5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.ConfigManagementControlBinder$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ConfigManagementControlBinder.lambda$bind$4(host, view2);
                }
            });
        }
        host.refreshConfigList();
    }

    static /* synthetic */ void lambda$bind$0(EditText editText, Host host, Context context, View view) {
        String strTrim;
        if (editText == null) {
            strTrim = "";
        } else {
            strTrim = editText.getText().toString().trim();
        }
        if (strTrim.isEmpty()) {
            strTrim = "配置_" + new SimpleDateFormat("MMdd_HHmm", Locale.getDefault()).format(new Date());
        }
        if (host.saveConfigToFile(strTrim)) {
            FloatingWindowService.safeToast(context, "✅ 配置 \"" + strTrim + "\" 已保存", 0);
            if (editText != null) {
                editText.setText("");
            }
            host.refreshConfigList();
            return;
        }
        FloatingWindowService.safeToast(context, "❌ 保存配置失败", 0);
    }

    static /* synthetic */ void lambda$bind$1(Host host, Context context, View view) {
        host.refreshConfigList();
        FloatingWindowService.safeToast(context, "已刷新配置列表，点击\"加载\"按钮应用", 0);
    }

    static /* synthetic */ void lambda$bind$2(Host host, Context context, View view) {
        Long l = (Long) view.getTag();
        long jCurrentTimeMillis = System.currentTimeMillis();
        long jLongValue = l == null ? -1L : jCurrentTimeMillis - l.longValue();
        if (jLongValue >= 600 && jLongValue <= 5000) {
            view.setTag(null);
            host.resetAllConfig();
            FloatingWindowService.safeToast(context, "✅ 所有配置已重置为默认值，重启悬浮窗生效", 1);
            host.restartFloatingWindow();
            return;
        }
        view.setTag(Long.valueOf(jCurrentTimeMillis));
        FloatingWindowService.safeToast(context, "⚠️ 这会清空全部配置。请稍等 1 秒后再点一次确认（5 秒内有效）", 1);
    }

    static /* synthetic */ void lambda$bind$3(Host host, Context context, View view) {
        host.animateButtonClick(view);
        String strExportConfigToDownload = host.exportConfigToDownload();
        if (strExportConfigToDownload != null) {
            FloatingWindowService.safeToast(context, "✅ 已导出:" + strExportConfigToDownload, 1);
        } else {
            FloatingWindowService.safeToast(context, "❌ 导出失败,查看日志", 0);
        }
    }

    static /* synthetic */ void lambda$bind$4(Host host, View view) {
        host.animateButtonClick(view);
        host.showImportConfigDialog();
    }
}
