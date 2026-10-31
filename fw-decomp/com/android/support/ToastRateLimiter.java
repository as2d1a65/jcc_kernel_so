package com.android.support;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/* loaded from: classes.dex */
final class ToastRateLimiter {
    private static final long GLOBAL_MIN_GAP_MS = 200;
    private static final long SAME_TEXT_GAP_MS = 1200;
    private static volatile long lastToastSameTextTimeMs = 0;
    private static volatile String lastToastText = "";
    private static volatile long lastToastTimeMs;

    private ToastRateLimiter() {
    }

    static void show(final Context context, CharSequence charSequence, final int i) {
        if (context == null || charSequence == null) {
            return;
        }
        final String string = charSequence.toString();
        long jCurrentTimeMillis = System.currentTimeMillis();
        if ((!string.equals(lastToastText) || jCurrentTimeMillis - lastToastSameTextTimeMs >= SAME_TEXT_GAP_MS) && jCurrentTimeMillis - lastToastTimeMs >= 200) {
            lastToastTimeMs = jCurrentTimeMillis;
            lastToastText = string;
            lastToastSameTextTimeMs = jCurrentTimeMillis;
            Runnable runnable = new Runnable() { // from class: com.android.support.ToastRateLimiter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ToastRateLimiter.lambda$show$0(context, i, string);
                }
            };
            if (Looper.myLooper() == Looper.getMainLooper()) {
                runnable.run();
            } else {
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        }
    }

    static /* synthetic */ void lambda$show$0(Context context, int i, String str) {
        String str2;
        Context applicationContext = context.getApplicationContext();
        int iEndOffset = XfOverlayScale.endOffset(context);
        int iDp = XfOverlayScale.dp(context, 88.0f);
        try {
            Toast toast = new Toast(applicationContext);
            toast.setDuration(i);
            str2 = str;
            try {
                toast.setView(new XfNoticeCardView(applicationContext, "", str2, GameHudPalette.CYAN, null));
                toast.setGravity(8388661, iEndOffset, iDp);
                toast.show();
            } catch (Throwable unused) {
                try {
                    Toast toastMakeText = Toast.makeText(applicationContext, str2, i);
                    toastMakeText.setGravity(8388661, iEndOffset, iDp);
                    toastMakeText.show();
                } catch (Throwable unused2) {
                }
            }
        } catch (Throwable unused3) {
            str2 = str;
        }
    }
}
