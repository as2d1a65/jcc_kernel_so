package com.android.support;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import java.util.Locale;

/* loaded from: classes.dex */
final class OfficialWatermarkOverlay {
    private static final long ATTACH_RETRY_INTERVAL_MS = 5000;
    private static final float COMPACT_PHONE_ALPHA = 0.52f;
    private static final int COMPACT_PHONE_BACKDROP_COLOR = 1073741824;
    private static final int COMPACT_PHONE_HORIZONTAL_PADDING_DP = 5;
    private static final int COMPACT_PHONE_MAX_WIDTH_DP = 240;
    private static final int COMPACT_PHONE_MIN_BOTTOM_OFFSET_DP = 52;
    private static final int COMPACT_PHONE_SAFE_BOTTOM_OFFSET_DP = 44;
    private static final int COMPACT_PHONE_START_MARGIN_DP = 8;
    private static final int COMPACT_PHONE_TEXT_SIZE_SP = 9;
    private static final int COMPACT_PHONE_VERTICAL_PADDING_DP = 2;
    private static final float DEFAULT_ALPHA = 0.75f;
    private static final int DEFAULT_BACKDROP_COLOR = 1711276032;
    private static final int DEFAULT_HORIZONTAL_PADDING_DP = 8;
    private static final int DEFAULT_MAX_WIDTH_DP = 360;
    private static final int DEFAULT_MIN_BOTTOM_OFFSET_DP = 24;
    private static final int DEFAULT_SAFE_BOTTOM_OFFSET_DP = 12;
    private static final int DEFAULT_START_MARGIN_DP = 12;
    private static final int DEFAULT_TEXT_SIZE_SP = 14;
    private static final int DEFAULT_VERTICAL_PADDING_DP = 4;
    private static final String TAG = "OfficialWatermark";
    private final Context appContext;
    private final float density;
    private long lastAttachAttemptMs;
    private WindowManager owner;
    private WindowManager.LayoutParams params;
    private TextView view;

    OfficialWatermarkOverlay(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.appContext = applicationContext;
        this.density = applicationContext.getResources().getDisplayMetrics().density;
    }

    synchronized void show(WindowManager windowManager, String str) {
        requireMainThread();
        WindowManager windowManagerTrustedOwnerFor = trustedOwnerFor(windowManager);
        if (windowManagerTrustedOwnerFor == null) {
            return;
        }
        ensureView();
        updateText(str);
        if (isViewAttached()) {
            WindowManager windowManager2 = this.owner;
            if (windowManager2 == null) {
                this.owner = windowManagerTrustedOwnerFor;
                this.lastAttachAttemptMs = 0L;
                return;
            } else if (windowManager2 == windowManagerTrustedOwnerFor) {
                refreshLayout(windowManagerTrustedOwnerFor);
                this.lastAttachAttemptMs = 0L;
                return;
            } else {
                migrate(windowManager2, windowManagerTrustedOwnerFor);
                return;
            }
        }
        this.owner = null;
        attach(windowManagerTrustedOwnerFor);
    }

    synchronized void updateText(String str) {
        requireMainThread();
        TextView textView = this.view;
        if (textView != null) {
            if (str == null) {
                str = "";
            }
            textView.setText(str);
        }
    }

    synchronized void migrate(WindowManager windowManager, WindowManager windowManager2) {
        requireMainThread();
        WindowManager windowManagerTrustedOwnerFor = trustedOwnerFor(windowManager2);
        if (windowManagerTrustedOwnerFor == null) {
            return;
        }
        ensureView();
        if (isViewAttached() && this.owner == windowManagerTrustedOwnerFor) {
            this.lastAttachAttemptMs = 0L;
        } else if (detach(windowManager)) {
            attach(windowManagerTrustedOwnerFor);
        }
    }

    synchronized void ensureAttached(WindowManager windowManager, String str) {
        requireMainThread();
        WindowManager windowManagerTrustedOwnerFor = trustedOwnerFor(windowManager);
        updateText(str);
        if (isViewAttached()) {
            if (this.owner == null) {
                this.owner = windowManagerTrustedOwnerFor;
            }
            WindowManager windowManager2 = this.owner;
            if (windowManager2 != windowManagerTrustedOwnerFor) {
                migrate(windowManager2, windowManagerTrustedOwnerFor);
                return;
            } else {
                refreshLayout(windowManagerTrustedOwnerFor);
                this.lastAttachAttemptMs = 0L;
                return;
            }
        }
        if (windowManagerTrustedOwnerFor == null) {
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j = this.lastAttachAttemptMs;
        if (j == 0 || jElapsedRealtime - j >= ATTACH_RETRY_INTERVAL_MS) {
            show(windowManagerTrustedOwnerFor, str);
        }
    }

    synchronized void remove() {
        requireMainThread();
        if (detach(this.owner)) {
            this.view = null;
            this.owner = null;
            this.params = null;
            this.lastAttachAttemptMs = 0L;
        }
    }

    synchronized boolean isAttached() {
        requireMainThread();
        return isViewAttached();
    }

    private void ensureView() {
        if (this.view != null) {
            return;
        }
        TextView textView = new TextView(this.appContext);
        this.view = textView;
        textView.setSingleLine(true);
        this.view.setTypeface(Typeface.create("sans-serif", 1));
        this.view.setTextColor(-15770);
        this.view.setShadowLayer(dp(2), 0.0f, dp(1), GameHudPalette.SHADOW_COLOR);
        this.view.setEllipsize(TextUtils.TruncateAt.END);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2038, 280, -3);
        this.params = layoutParams;
        layoutParams.gravity = 8388691;
        this.params.x = dp(12);
        this.params.y = dp(24);
        this.params.alpha = 0.79f;
        applyStyleForCurrentDisplay();
    }

    private int dp(int i) {
        return Math.round(i * this.density);
    }

    private WindowManager trustedOwnerFor(WindowManager windowManager) {
        if (windowManager == null) {
            return null;
        }
        try {
            return TrustedVisualOverlayWindowManager.forApplicationDisplay(this.appContext, windowManager.getDefaultDisplay());
        } catch (RuntimeException e) {
            Log.w(TAG, "Unable to resolve trusted watermark host: " + e.getClass().getSimpleName());
            return null;
        }
    }

    private void attach(WindowManager windowManager) {
        updateSafeOffsets(windowManager);
        try {
            windowManager.addView(this.view, this.params);
            e = null;
        } catch (RuntimeException e) {
            e = e;
        }
        if (isViewAttached()) {
            this.owner = windowManager;
            this.lastAttachAttemptMs = 0L;
            return;
        }
        this.owner = null;
        recordAttachFailure();
        if (e != null) {
            Log.w(TAG, "Unable to attach watermark: " + e.getClass().getSimpleName());
        }
    }

    private void refreshLayout(WindowManager windowManager) {
        applyStyleForCurrentDisplay();
        updateSafeOffsets(windowManager);
        try {
            windowManager.updateViewLayout(this.view, this.params);
        } catch (RuntimeException e) {
            Log.w(TAG, "Unable to refresh watermark layout: " + e.getClass().getSimpleName());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x004d  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0050  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0056  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x005a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateSafeOffsets(android.view.WindowManager r7) {
        /*
            r6 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 30
            r2 = 0
            if (r0 < r1) goto L44
            android.view.WindowMetrics r7 = r7.getCurrentWindowMetrics()     // Catch: java.lang.RuntimeException -> L23
            android.view.WindowInsets r7 = r7.getWindowInsets()     // Catch: java.lang.RuntimeException -> L23
            int r0 = android.view.WindowInsets.Type.systemBars()     // Catch: java.lang.RuntimeException -> L23
            int r1 = android.view.WindowInsets.Type.displayCutout()     // Catch: java.lang.RuntimeException -> L23
            r0 = r0 | r1
            android.graphics.Insets r7 = r7.getInsetsIgnoringVisibility(r0)     // Catch: java.lang.RuntimeException -> L23
            int r0 = r7.left     // Catch: java.lang.RuntimeException -> L23
            int r2 = r7.bottom     // Catch: java.lang.RuntimeException -> L21
            goto L41
        L21:
            r7 = move-exception
            goto L25
        L23:
            r7 = move-exception
            r0 = r2
        L25:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r3 = "Unable to read watermark safe area: "
            r1.<init>(r3)
            java.lang.Class r7 = r7.getClass()
            java.lang.String r7 = r7.getSimpleName()
            java.lang.StringBuilder r7 = r1.append(r7)
            java.lang.String r7 = r7.toString()
            java.lang.String r1 = "OfficialWatermark"
            android.util.Log.w(r1, r7)
        L41:
            r7 = r2
            r2 = r0
            goto L45
        L44:
            r7 = r2
        L45:
            boolean r0 = r6.isCompactPhoneLandscape()
            r1 = 12
            if (r0 == 0) goto L50
            r3 = 8
            goto L51
        L50:
            r3 = r1
        L51:
            if (r0 == 0) goto L56
            r4 = 52
            goto L58
        L56:
            r4 = 24
        L58:
            if (r0 == 0) goto L5c
            r1 = 44
        L5c:
            android.view.WindowManager$LayoutParams r0 = r6.params
            int r5 = r6.dp(r3)
            int r3 = r6.dp(r3)
            int r2 = r2 + r3
            int r2 = java.lang.Math.max(r5, r2)
            r0.x = r2
            android.view.WindowManager$LayoutParams r0 = r6.params
            int r2 = r6.dp(r4)
            int r6 = r6.dp(r1)
            int r7 = r7 + r6
            int r6 = java.lang.Math.max(r2, r7)
            r0.y = r6
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.OfficialWatermarkOverlay.updateSafeOffsets(android.view.WindowManager):void");
    }

    private void applyStyleForCurrentDisplay() {
        if (this.view == null || this.params == null) {
            return;
        }
        boolean zIsCompactPhoneLandscape = isCompactPhoneLandscape();
        int i = zIsCompactPhoneLandscape ? 9 : 14;
        int i2 = zIsCompactPhoneLandscape ? COMPACT_PHONE_BACKDROP_COLOR : 1711276032;
        int i3 = zIsCompactPhoneLandscape ? 5 : 8;
        int i4 = zIsCompactPhoneLandscape ? 2 : 4;
        int i5 = zIsCompactPhoneLandscape ? 240 : 360;
        float f = zIsCompactPhoneLandscape ? COMPACT_PHONE_ALPHA : DEFAULT_ALPHA;
        this.view.setTextSize(2, i);
        this.view.setPadding(dp(i3), dp(i4), dp(i3), dp(i4));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(i2);
        gradientDrawable.setCornerRadius(dp(6));
        this.view.setBackground(gradientDrawable);
        this.view.setMaxWidth(dp(i5));
        this.params.alpha = f;
    }

    private boolean isCompactPhoneLandscape() {
        DisplayMetrics displayMetrics = this.appContext.getResources().getDisplayMetrics();
        if (displayMetrics.widthPixels > displayMetrics.heightPixels && !isDesktopLikeAbi()) {
            int iRound = this.appContext.getResources().getConfiguration().smallestScreenWidthDp;
            if (iRound == 0) {
                iRound = Math.round(Math.min(r1, r2) / displayMetrics.density);
            }
            if (iRound < 600) {
                return true;
            }
        }
        return false;
    }

    private boolean isDesktopLikeAbi() {
        for (String str : Build.SUPPORTED_ABIS) {
            if (str != null && str.toLowerCase(Locale.US).contains("x86")) {
                return true;
            }
        }
        return false;
    }

    private boolean detach(WindowManager windowManager) {
        if (!isViewAttached()) {
            this.owner = null;
            return true;
        }
        WindowManager windowManager2 = this.owner;
        if (windowManager2 != null) {
            windowManager = windowManager2;
        }
        if (windowManager == null) {
            Log.w(TAG, "Unable to detach watermark: owner unavailable");
            return false;
        }
        try {
            windowManager.removeView(this.view);
        } catch (RuntimeException e) {
            Log.w(TAG, "Unable to detach watermark: " + e.getClass().getSimpleName());
        }
        if (isViewAttached()) {
            this.owner = windowManager;
            return false;
        }
        this.owner = null;
        return true;
    }

    private boolean isViewAttached() {
        TextView textView = this.view;
        if (textView != null) {
            return textView.isAttachedToWindow() || this.view.getParent() != null;
        }
        return false;
    }

    private void recordAttachFailure() {
        this.lastAttachAttemptMs = SystemClock.elapsedRealtime();
    }

    private static void requireMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Official watermark overlay requires the main thread");
        }
    }
}
