package com.android.support;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/* loaded from: classes.dex */
final class MemoryModControlBinder {

    interface Host {
        void animateButtonClick(View view);

        int levelToExp(int i);

        void refreshPopulationDisplay(TextView textView);

        void setupModifyEditText(EditText editText);

        SharedMemManager sharedMemManager();

        void showModifyFeedback(View view, boolean z, String str);

        void startPopulationRefreshTimer(TextView textView);

        void writeModifyTargets();
    }

    private MemoryModControlBinder() {
    }

    static void bind(View view, final SharedPreferences sharedPreferences, final Host host) {
        final EditText editText = (EditText) view.findViewById(R.id.etModifyGold);
        View viewFindViewById = view.findViewById(R.id.btnModifyGold);
        host.setupModifyEditText(editText);
        if (editText != null) {
            String string = sharedPreferences.getString(FloatingWindowPrefsKeys.KEY_MODIFY_GOLD_VALUE, "");
            if (!string.isEmpty()) {
                editText.setText(string);
            }
        }
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) throws NumberFormatException {
                    MemoryModControlBinder.lambda$bind$1(host, editText, sharedPreferences, view2);
                }
            });
        }
        final EditText editText2 = (EditText) view.findViewById(R.id.etModifyHp);
        View viewFindViewById2 = view.findViewById(R.id.btnModifyHp);
        host.setupModifyEditText(editText2);
        if (editText2 != null) {
            String string2 = sharedPreferences.getString(FloatingWindowPrefsKeys.KEY_MODIFY_HP_VALUE, "");
            if (!string2.isEmpty()) {
                editText2.setText(string2);
            }
        }
        if (viewFindViewById2 != null) {
            viewFindViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) throws NumberFormatException {
                    MemoryModControlBinder.lambda$bind$3(host, editText2, sharedPreferences, view2);
                }
            });
        }
        final EditText editText3 = (EditText) view.findViewById(R.id.etModifyLevel);
        View viewFindViewById3 = view.findViewById(R.id.btnModifyLevel);
        host.setupModifyEditText(editText3);
        if (editText3 != null) {
            String string3 = sharedPreferences.getString(FloatingWindowPrefsKeys.KEY_MODIFY_LEVEL_VALUE, "");
            if (!string3.isEmpty()) {
                editText3.setText(string3);
            }
        }
        if (viewFindViewById3 != null) {
            viewFindViewById3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) throws NumberFormatException {
                    MemoryModControlBinder.lambda$bind$5(host, editText3, sharedPreferences, view2);
                }
            });
        }
        final TextView textView = (TextView) view.findViewById(R.id.tvPopValue);
        View viewFindViewById4 = view.findViewById(R.id.btnPopMinus);
        View viewFindViewById5 = view.findViewById(R.id.btnPopPlus);
        if (viewFindViewById4 != null) {
            viewFindViewById4.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    MemoryModControlBinder.lambda$bind$8(host, textView, view2);
                }
            });
        }
        if (viewFindViewById5 != null) {
            viewFindViewById5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    MemoryModControlBinder.lambda$bind$11(host, textView, view2);
                }
            });
        }
        if (textView != null) {
            host.startPopulationRefreshTimer(textView);
        }
    }

    static /* synthetic */ void lambda$bind$1(final Host host, EditText editText, SharedPreferences sharedPreferences, final View view) throws NumberFormatException {
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (editText == null || sharedMemManager == null) {
            return;
        }
        String strTrim = editText.getText().toString().trim();
        if (strTrim.isEmpty()) {
            host.showModifyFeedback(view, false, "请输入金币数量");
            return;
        }
        try {
            final int i = Integer.parseInt(strTrim);
            sharedPreferences.edit().putString(FloatingWindowPrefsKeys.KEY_MODIFY_GOLD_VALUE, strTrim).apply();
            host.animateButtonClick(view);
            host.writeModifyTargets();
            sharedMemManager.modifyGold(i, new Runnable() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    host.showModifyFeedback(view, true, "金币已修改为 " + i);
                }
            });
        } catch (NumberFormatException unused) {
            host.showModifyFeedback(view, false, "请输入有效数字");
        }
    }

    static /* synthetic */ void lambda$bind$3(final Host host, EditText editText, SharedPreferences sharedPreferences, final View view) throws NumberFormatException {
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (editText == null || sharedMemManager == null) {
            return;
        }
        String strTrim = editText.getText().toString().trim();
        if (strTrim.isEmpty()) {
            host.showModifyFeedback(view, false, "请输入血量");
            return;
        }
        try {
            final int i = Integer.parseInt(strTrim);
            sharedPreferences.edit().putString(FloatingWindowPrefsKeys.KEY_MODIFY_HP_VALUE, strTrim).apply();
            host.animateButtonClick(view);
            host.writeModifyTargets();
            sharedMemManager.modifyHP(i, new Runnable() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    host.showModifyFeedback(view, true, "血量已修改为 " + i);
                }
            });
        } catch (NumberFormatException unused) {
            host.showModifyFeedback(view, false, "请输入有效数字");
        }
    }

    static /* synthetic */ void lambda$bind$5(final Host host, EditText editText, SharedPreferences sharedPreferences, final View view) throws NumberFormatException {
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (editText == null || sharedMemManager == null) {
            return;
        }
        String strTrim = editText.getText().toString().trim();
        if (strTrim.isEmpty()) {
            host.showModifyFeedback(view, false, "请输入等级(2-10)");
            return;
        }
        try {
            final int i = Integer.parseInt(strTrim);
            if (i >= 2 && i <= 10) {
                sharedPreferences.edit().putString(FloatingWindowPrefsKeys.KEY_MODIFY_LEVEL_VALUE, strTrim).apply();
                final int iLevelToExp = host.levelToExp(i);
                host.animateButtonClick(view);
                host.writeModifyTargets();
                sharedMemManager.modifyLevel(iLevelToExp, new Runnable() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda11
                    @Override // java.lang.Runnable
                    public final void run() {
                        host.showModifyFeedback(view, true, "等级已修改为 " + i + " (经验:" + iLevelToExp + ")");
                    }
                });
                return;
            }
            host.showModifyFeedback(view, false, "等级范围: 2-10");
        } catch (NumberFormatException unused) {
            host.showModifyFeedback(view, false, "请输入有效数字");
        }
    }

    static /* synthetic */ void lambda$bind$8(final Host host, final TextView textView, View view) {
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (sharedMemManager == null) {
            return;
        }
        host.animateButtonClick(view);
        host.writeModifyTargets();
        applyOptimisticPopulationDelta(textView, -1);
        sharedMemManager.modifyPopulation(-1, new Runnable() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MemoryModControlBinder.lambda$bind$7(textView, host);
            }
        });
    }

    static /* synthetic */ void lambda$bind$7(final TextView textView, final Host host) {
        if (textView != null) {
            textView.postDelayed(new Runnable() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    host.refreshPopulationDisplay(textView);
                }
            }, 200L);
        }
    }

    static /* synthetic */ void lambda$bind$11(final Host host, final TextView textView, View view) {
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (sharedMemManager == null) {
            return;
        }
        host.animateButtonClick(view);
        host.writeModifyTargets();
        applyOptimisticPopulationDelta(textView, 1);
        sharedMemManager.modifyPopulation(1, new Runnable() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                MemoryModControlBinder.lambda$bind$10(textView, host);
            }
        });
    }

    static /* synthetic */ void lambda$bind$10(final TextView textView, final Host host) {
        if (textView != null) {
            textView.postDelayed(new Runnable() { // from class: com.android.support.MemoryModControlBinder$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    host.refreshPopulationDisplay(textView);
                }
            }, 200L);
        }
    }

    static String nextPopulationLabel(String str, int i) {
        if (str == null) {
            return null;
        }
        try {
            int i2 = Integer.parseInt(str.trim()) + i;
            if (i2 < 1) {
                i2 = 1;
            }
            if (i2 > 15) {
                i2 = 15;
            }
            return String.valueOf(i2);
        } catch (NumberFormatException unused) {
            return str;
        }
    }

    static void applyOptimisticPopulationDelta(TextView textView, int i) {
        if (textView == null) {
            return;
        }
        textView.setText(nextPopulationLabel(textView.getText().toString(), i));
    }
}
