package com.android.support;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import java.util.function.Consumer;

/* loaded from: classes.dex */
final class OverlayFocusEditTextBinder {
    private static final String TAG = "FloatingWindowService";

    private OverlayFocusEditTextBinder() {
    }

    static void bind(final EditText editText, final WindowManager windowManager, final View view, final WindowManager.LayoutParams layoutParams, final Consumer<View> consumer) {
        if (editText == null) {
            return;
        }
        editText.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OverlayFocusEditTextBinder$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                OverlayFocusEditTextBinder.lambda$bind$0(layoutParams, windowManager, view, editText, consumer, view2);
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.android.support.OverlayFocusEditTextBinder$$ExternalSyntheticLambda1
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view2, boolean z) {
                OverlayFocusEditTextBinder.lambda$bind$1(layoutParams, windowManager, view, consumer, editText, view2, z);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(WindowManager.LayoutParams layoutParams, WindowManager windowManager, View view, EditText editText, Consumer consumer, View view2) {
        if (layoutParams != null && windowManager != null && view != null) {
            layoutParams.flags &= -9;
            try {
                windowManager.updateViewLayout(view, layoutParams);
            } catch (Exception e) {
                Log.e(TAG, "更新窗口布局失败", e);
            }
        }
        editText.requestFocus();
        consumer.accept(editText);
    }

    static /* synthetic */ void lambda$bind$1(WindowManager.LayoutParams layoutParams, WindowManager windowManager, View view, Consumer consumer, EditText editText, View view2, boolean z) {
        if (z) {
            if (layoutParams != null && windowManager != null && view != null) {
                layoutParams.flags &= -9;
                try {
                    windowManager.updateViewLayout(view, layoutParams);
                } catch (Exception e) {
                    Log.e(TAG, "更新窗口布局失败", e);
                }
            }
            consumer.accept(editText);
            return;
        }
        if (layoutParams == null || windowManager == null || view == null) {
            return;
        }
        layoutParams.flags |= 8;
        try {
            windowManager.updateViewLayout(view, layoutParams);
        } catch (Exception e2) {
            Log.e(TAG, "更新窗口布局失败", e2);
        }
    }
}
