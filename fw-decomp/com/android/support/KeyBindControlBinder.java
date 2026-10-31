package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

/* loaded from: classes.dex */
final class KeyBindControlBinder {
    private KeyBindControlBinder() {
    }

    static void bind(View view, SharedPreferences sharedPreferences, Context context) {
        bindButton(view, sharedPreferences, context, R.id.btnShortcutKeyWindow, EmulatorShortcutService.KEY_TOGGLE_WINDOW, EmulatorShortcutService.DEFAULT_KEY_WINDOW);
        bindButton(view, sharedPreferences, context, R.id.btnShortcutKeyAutoPick, EmulatorShortcutService.KEY_TOGGLE_AUTOPICK, EmulatorShortcutService.DEFAULT_KEY_AUTOPICK);
        bindButton(view, sharedPreferences, context, R.id.btnShortcutKeyCardPool, EmulatorShortcutService.KEY_TOGGLE_CARDPOOL, EmulatorShortcutService.DEFAULT_KEY_CARDPOOL);
    }

    static void bindButton(View view, final SharedPreferences sharedPreferences, final Context context, int i, final String str, final int i2) {
        final Button button = (Button) view.findViewById(i);
        if (button == null) {
            return;
        }
        button.setText(EmulatorShortcutService.getKeyName(sharedPreferences.getInt(str, i2)));
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.KeyBindControlBinder$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                KeyBindControlBinder.lambda$bindButton$0(sharedPreferences, str, i2, button, context, view2);
            }
        });
    }

    static /* synthetic */ void lambda$bindButton$0(SharedPreferences sharedPreferences, String str, int i, Button button, Context context, View view) {
        int iIndexOfKey = EmulatorShortcutService.indexOfKey(sharedPreferences.getInt(str, i));
        int keyCount = EmulatorShortcutService.getKeyCount();
        int i2 = (iIndexOfKey + 1) % keyCount;
        int i3 = EmulatorShortcutService.AVAILABLE_KEYS[i2];
        int i4 = sharedPreferences.getInt(EmulatorShortcutService.KEY_TOGGLE_WINDOW, EmulatorShortcutService.DEFAULT_KEY_WINDOW);
        int i5 = sharedPreferences.getInt(EmulatorShortcutService.KEY_TOGGLE_AUTOPICK, EmulatorShortcutService.DEFAULT_KEY_AUTOPICK);
        int i6 = sharedPreferences.getInt(EmulatorShortcutService.KEY_TOGGLE_CARDPOOL, EmulatorShortcutService.DEFAULT_KEY_CARDPOOL);
        for (int i7 = 0; i7 < keyCount; i7++) {
            boolean z = !str.equals(EmulatorShortcutService.KEY_TOGGLE_WINDOW) && i3 == i4;
            if (!str.equals(EmulatorShortcutService.KEY_TOGGLE_AUTOPICK) && i3 == i5) {
                z = true;
            }
            if (!str.equals(EmulatorShortcutService.KEY_TOGGLE_CARDPOOL) && i3 == i6) {
                z = true;
            }
            if (!z) {
                break;
            }
            i2 = (i2 + 1) % keyCount;
            i3 = EmulatorShortcutService.AVAILABLE_KEYS[i2];
        }
        sharedPreferences.edit().putInt(str, i3).apply();
        button.setText(EmulatorShortcutService.getKeyName(i3));
        EmulatorShortcutService.notifyConfigChanged();
        FloatingWindowService.safeToast(context, "已绑定: " + EmulatorShortcutService.getKeyName(i3), 0);
    }
}
