package com.android.support;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

/* loaded from: classes.dex */
final class OverlaySoftKeyboard {
    static int showFlag(boolean z) {
        return z ? 2 : 1;
    }

    private OverlaySoftKeyboard() {
    }

    static void show(View view, InputMethodManager inputMethodManager, boolean z) {
        view.requestFocus();
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, showFlag(z));
        }
    }

    static void hide(View view, InputMethodManager inputMethodManager) {
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
