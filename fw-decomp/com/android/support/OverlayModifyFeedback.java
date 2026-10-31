package com.android.support;

import android.view.View;
import android.widget.TextView;

/* loaded from: classes.dex */
final class OverlayModifyFeedback {
    static final int FAILURE_COLOR = -48060;
    static final String FAILURE_TEXT = "✗ 失败";
    static final int FLASH_MS = 800;
    static final String SUCCESS_TEXT = "✓ 成功";

    interface Toaster {
        void toast(String str);
    }

    private OverlayModifyFeedback() {
    }

    static void apply(Toaster toaster, View view, boolean z, String str) {
        toaster.toast(str);
        if (view == null || !(view instanceof TextView)) {
            return;
        }
        final TextView textView = (TextView) view;
        final int currentTextColor = textView.getCurrentTextColor();
        final CharSequence text = textView.getText();
        textView.setTextColor(z ? GameHudPalette.SUCCESS : FAILURE_COLOR);
        textView.setText(z ? SUCCESS_TEXT : FAILURE_TEXT);
        view.postDelayed(new Runnable() { // from class: com.android.support.OverlayModifyFeedback$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                OverlayModifyFeedback.lambda$apply$0(textView, currentTextColor, text);
            }
        }, 800L);
    }

    static /* synthetic */ void lambda$apply$0(TextView textView, int i, CharSequence charSequence) {
        textView.setTextColor(i);
        textView.setText(charSequence);
    }
}
