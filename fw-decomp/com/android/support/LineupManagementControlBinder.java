package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

/* loaded from: classes.dex */
final class LineupManagementControlBinder {

    interface Host {
        boolean hasSelectedHeroes();

        void hideKeyboard(View view);

        void refreshLineupList();

        void saveLineup(String str);

        void setLineupViews(EditText editText, Button button, TextView textView, LinearLayout linearLayout);

        void setMainFocusable(boolean z);

        SharedMemManager sharedMemManager();

        void showKeyboard(View view);

        void updateCurrentLineupInfo();
    }

    private LineupManagementControlBinder() {
    }

    static void bind(View view, final Context context, SharedPreferences sharedPreferences, final Host host) {
        final EditText editText = (EditText) view.findViewById(R.id.etLineupName);
        Button button = (Button) view.findViewById(R.id.btnSaveLineup);
        host.setLineupViews(editText, button, (TextView) view.findViewById(R.id.tvCurrentLineupInfo), (LinearLayout) view.findViewById(R.id.llLineupList));
        host.updateCurrentLineupInfo();
        editText.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LineupManagementControlBinder$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                LineupManagementControlBinder.lambda$bind$0(host, editText, view2);
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.android.support.LineupManagementControlBinder$$ExternalSyntheticLambda1
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view2, boolean z) {
                LineupManagementControlBinder.lambda$bind$1(host, editText, view2, z);
            }
        });
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LineupManagementControlBinder$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                LineupManagementControlBinder.lambda$bind$2(editText, context, host, view2);
            }
        });
        host.refreshLineupList();
        Switch r4 = (Switch) view.findViewById(R.id.switchDebugWindow);
        if (r4 != null) {
            sharedPreferences.edit().remove(FloatingWindowPrefsKeys.KEY_SHOW_DEBUG_WINDOW).apply();
            r4.setChecked(false);
            r4.setEnabled(false);
            SharedMemManager sharedMemManager = host.sharedMemManager();
            if (sharedMemManager != null) {
                sharedMemManager.setShowDebugWindow(false);
            }
        }
    }

    static /* synthetic */ void lambda$bind$0(Host host, EditText editText, View view) {
        host.setMainFocusable(true);
        editText.requestFocus();
        host.showKeyboard(editText);
    }

    static /* synthetic */ void lambda$bind$1(Host host, EditText editText, View view, boolean z) {
        if (z) {
            host.setMainFocusable(true);
            host.showKeyboard(editText);
        } else {
            host.setMainFocusable(false);
        }
    }

    static /* synthetic */ void lambda$bind$2(EditText editText, Context context, Host host, View view) {
        String strTrim = editText.getText().toString().trim();
        if (strTrim.isEmpty()) {
            FloatingWindowService.safeToast(context, "请输入阵容名称", 0);
            return;
        }
        if (!host.hasSelectedHeroes()) {
            FloatingWindowService.safeToast(context, "请先选择英雄", 0);
            return;
        }
        host.saveLineup(strTrim);
        editText.setText("");
        editText.clearFocus();
        host.hideKeyboard(editText);
        host.setMainFocusable(false);
        FloatingWindowService.safeToast(context, "阵容已保存: " + strTrim, 0);
    }
}
