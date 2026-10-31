package com.android.support;

import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.View;

/* loaded from: classes.dex */
final class OverlayAttachVerifier {
    private OverlayAttachVerifier() {
    }

    static void verify(final String str, final View view, final String str2) {
        if (view == null) {
            Log.e(str2, "[overlay-verify] " + str + ": view=null");
        } else {
            view.post(new Runnable() { // from class: com.android.support.OverlayAttachVerifier$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    OverlayAttachVerifier.lambda$verify$0(view, str2, str);
                }
            });
        }
    }

    static /* synthetic */ void lambda$verify$0(View view, String str, String str2) {
        try {
            boolean zIsAttachedToWindow = view.isAttachedToWindow();
            IBinder windowToken = view.getWindowToken();
            Display display = view.getDisplay();
            int displayId = display != null ? display.getDisplayId() : -1;
            String name = display != null ? display.getName() : "null";
            int visibility = view.getVisibility();
            float alpha = view.getAlpha();
            int width = view.getWidth();
            int height = view.getHeight();
            if (!zIsAttachedToWindow || windowToken == null) {
                Log.e(str, "[overlay-verify] ✗ " + str2 + " NOT attached! attached=" + zIsAttachedToWindow + " token=" + windowToken + " display=" + displayId + " (静默 addView 失败！可能 ROM/模拟器禁止 OVERLAY)");
            } else {
                Dbg.d(str, "[overlay-verify] ✓ " + str2 + " attached token=" + windowToken + " display=" + displayId + "(" + name + ") vis=" + visibility + " alpha=" + alpha + " size=" + width + "x" + height);
            }
        } catch (Throwable th) {
            Log.e(str, "[overlay-verify] " + str2 + " check failed", th);
        }
    }
}
