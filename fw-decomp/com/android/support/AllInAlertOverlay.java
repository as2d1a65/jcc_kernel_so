package com.android.support;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import com.android.support.AllInAlertText;
import com.android.support.VoiceAlertBannerPosition;
import java.util.Objects;

/* loaded from: classes.dex */
final class AllInAlertOverlay implements AutoCloseable {
    private static final long ENTER_DURATION_MS = 260;
    private static final long EXIT_DURATION_MS = 360;
    private static final long HOLD_MS = 3000;
    private static final int SAFE_MARGIN_DP = 12;
    private static final int X_PERCENT = 100;
    private static final int Y_PERCENT = 0;
    private long animationToken;
    private boolean attached;
    private final Runnable autoHide = new Runnable() { // from class: com.android.support.AllInAlertOverlay$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.startExit();
        }
    };
    private volatile boolean closeRequested;
    private final Context context;
    private final float density;
    private final TextView detailView;
    private boolean failed;
    private final Handler handler;
    private final LinearLayout root;
    private final TextView titleView;
    private WindowManager windowManager;
    private final WindowManager.LayoutParams windowParams;

    View rootView() {
        return this.root;
    }

    AllInAlertOverlay(Context context, WindowManager windowManager, Handler handler) {
        Context applicationContext = ((Context) Objects.requireNonNull(context, "context")).getApplicationContext();
        this.context = applicationContext;
        this.windowManager = (WindowManager) Objects.requireNonNull(windowManager, "windowManager");
        Handler handler2 = (Handler) Objects.requireNonNull(handler, "mainHandler");
        this.handler = handler2;
        if (handler2.getLooper() != Looper.getMainLooper()) {
            throw new IllegalArgumentException("mainHandler must use the main Looper");
        }
        this.density = applicationContext.getResources().getDisplayMetrics().density;
        this.root = new LinearLayout(applicationContext);
        this.titleView = new TextView(applicationContext);
        this.detailView = new TextView(applicationContext);
        configureView();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2038, 56, -3);
        this.windowParams = layoutParams;
        layoutParams.gravity = 8388659;
        layoutParams.alpha = 0.82f;
        runOnMain(new Runnable() { // from class: com.android.support.AllInAlertOverlay$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.attachOnce();
            }
        });
    }

    void alert(final AllInAlertText.Parts parts) {
        if (parts == null) {
            return;
        }
        runOnMain(new Runnable() { // from class: com.android.support.AllInAlertOverlay$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m55lambda$alert$0$comandroidsupportAllInAlertOverlay(parts);
            }
        });
    }

    void onDisplayGeometryChanged() {
        runOnMain(new Runnable() { // from class: com.android.support.AllInAlertOverlay$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m58xfc95ac37();
            }
        });
    }

    /* renamed from: lambda$onDisplayGeometryChanged$1$com-android-support-AllInAlertOverlay, reason: not valid java name */
    /* synthetic */ void m58xfc95ac37() {
        if (canOperate()) {
            requestPositionUpdate(this.animationToken);
        }
    }

    void migrate(final WindowManager windowManager) {
        Objects.requireNonNull(windowManager, "newWindowManager");
        runOnMain(new Runnable() { // from class: com.android.support.AllInAlertOverlay$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m57lambda$migrate$2$comandroidsupportAllInAlertOverlay(windowManager);
            }
        });
    }

    void hideNow() {
        runOnMain(new Runnable() { // from class: com.android.support.AllInAlertOverlay$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m56lambda$hideNow$3$comandroidsupportAllInAlertOverlay();
            }
        });
    }

    /* renamed from: lambda$hideNow$3$com-android-support-AllInAlertOverlay, reason: not valid java name */
    /* synthetic */ void m56lambda$hideNow$3$comandroidsupportAllInAlertOverlay() {
        if (!this.attached || this.failed) {
            return;
        }
        this.handler.removeCallbacks(this.autoHide);
        makeGone(nextAnimationToken());
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.closeRequested) {
            return;
        }
        this.closeRequested = true;
        runOnMain(new Runnable() { // from class: com.android.support.AllInAlertOverlay$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.closeOnMain();
            }
        });
    }

    private void configureView() {
        this.root.setOrientation(1);
        this.root.setGravity(GravityCompat.END);
        this.root.setVisibility(8);
        this.root.setPadding(dp(16), dp(9), dp(16), dp(9));
        this.root.setElevation(dpFloat(12));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(-450620911);
        gradientDrawable.setCornerRadius(dpFloat(16));
        gradientDrawable.setStroke(Math.max(1, Math.round(dpFloat(1) * 1.5f)), -2069425);
        this.root.setBackground(gradientDrawable);
        configureSegment(this.titleView, -19296, 17.0f, true);
        configureSegment(this.detailView, -9780, 12.0f, false);
        this.root.addView(this.titleView, wrapContentParams());
        this.root.addView(this.detailView, wrapContentParams());
    }

    private void configureSegment(TextView textView, int i, float f, boolean z) {
        textView.setSingleLine(true);
        textView.setTextColor(i);
        textView.setTextSize(2, f);
        textView.setTypeface(Typeface.create("sans-serif", z ? 1 : 0));
        textView.setIncludeFontPadding(false);
        GameHudPalette.applyTextShadow(textView);
    }

    private LinearLayout.LayoutParams wrapContentParams() {
        return new LinearLayout.LayoutParams(-2, -2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showOnMain, reason: merged with bridge method [inline-methods] */
    public void m55lambda$alert$0$comandroidsupportAllInAlertOverlay(AllInAlertText.Parts parts) {
        if (canOperate()) {
            final long jNextAnimationToken = nextAnimationToken();
            this.handler.removeCallbacks(this.autoHide);
            this.titleView.setText(parts.title);
            this.detailView.setText(parts.detail);
            this.root.setVisibility(0);
            this.root.setAlpha(0.0f);
            this.root.setTranslationY(-dpFloat(10));
            requestPositionUpdate(jNextAnimationToken);
            this.root.post(new Runnable() { // from class: com.android.support.AllInAlertOverlay$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m60lambda$showOnMain$4$comandroidsupportAllInAlertOverlay(jNextAnimationToken);
                }
            });
        }
    }

    /* renamed from: lambda$showOnMain$4$com-android-support-AllInAlertOverlay, reason: not valid java name */
    /* synthetic */ void m60lambda$showOnMain$4$comandroidsupportAllInAlertOverlay(long j) {
        if (isCurrent(j) && this.root.getVisibility() == 0) {
            this.root.animate().alpha(1.0f).translationY(0.0f).setDuration(260L).setListener(null).start();
            this.handler.postDelayed(this.autoHide, HOLD_MS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startExit() {
        if (canOperate() && this.root.getVisibility() == 0) {
            final long jNextAnimationToken = nextAnimationToken();
            this.root.animate().alpha(0.0f).translationY(-dpFloat(6)).setDuration(EXIT_DURATION_MS).setListener(new AnimatorListenerAdapter() { // from class: com.android.support.AllInAlertOverlay.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (AllInAlertOverlay.this.isCurrent(jNextAnimationToken)) {
                        AllInAlertOverlay.this.makeGone(jNextAnimationToken);
                    }
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeGone(long j) {
        if (j != this.animationToken) {
            return;
        }
        this.root.animate().setListener(null);
        this.root.setVisibility(8);
        this.root.setAlpha(1.0f);
        this.root.setTranslationY(0.0f);
    }

    private void requestPositionUpdate(final long j) {
        this.root.requestLayout();
        this.root.post(new Runnable() { // from class: com.android.support.AllInAlertOverlay$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m59xd7b76023(j);
            }
        });
    }

    /* renamed from: lambda$requestPositionUpdate$5$com-android-support-AllInAlertOverlay, reason: not valid java name */
    /* synthetic */ void m59xd7b76023(long j) {
        if (canOperate() && j == this.animationToken) {
            updatePositionNow(readSafeBounds());
        }
    }

    private void updatePositionNow(SafeBounds safeBounds) {
        int measuredWidth = this.root.getMeasuredWidth();
        int measuredHeight = this.root.getMeasuredHeight();
        if (measuredWidth <= 0 || measuredHeight <= 0) {
            return;
        }
        VoiceAlertBannerPosition.PixelPosition pixelPositionCalculate = VoiceAlertBannerPosition.calculate(safeBounds.left, safeBounds.top, safeBounds.right, safeBounds.bottom, measuredWidth, measuredHeight, dp(12), 100, 0);
        this.windowParams.x = pixelPositionCalculate.x;
        this.windowParams.y = pixelPositionCalculate.y;
        try {
            this.windowManager.updateViewLayout(this.root, this.windowParams);
        } catch (RuntimeException unused) {
            disableAfterWindowFailure();
        }
    }

    private SafeBounds readSafeBounds() {
        int i;
        WindowInsets rootWindowInsets;
        Point point = new Point();
        try {
            Display defaultDisplay = this.windowManager.getDefaultDisplay();
            if (defaultDisplay != null) {
                defaultDisplay.getRealSize(point);
            }
        } catch (RuntimeException unused) {
        }
        if (point.x <= 0 || point.y <= 0) {
            point.x = Math.max(1, this.context.getResources().getDisplayMetrics().widthPixels);
            point.y = Math.max(1, this.context.getResources().getDisplayMetrics().heightPixels);
        }
        int iMax = point.x;
        int iMax2 = point.y;
        int i2 = 0;
        if (!this.root.isAttachedToWindow() || (rootWindowInsets = this.root.getRootWindowInsets()) == null) {
            i = 0;
        } else {
            int iMax3 = Math.max(0, rootWindowInsets.getSystemWindowInsetLeft());
            int iMax4 = Math.max(0, rootWindowInsets.getSystemWindowInsetTop());
            iMax = Math.max(iMax3 + 1, iMax - rootWindowInsets.getSystemWindowInsetRight());
            iMax2 = Math.max(iMax4 + 1, iMax2 - rootWindowInsets.getSystemWindowInsetBottom());
            i = iMax4;
            i2 = iMax3;
        }
        return new SafeBounds(i2, i, iMax, iMax2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attachOnce() {
        if (this.closeRequested || this.attached || this.failed) {
            return;
        }
        try {
            this.windowManager.addView(this.root, this.windowParams);
            this.attached = true;
        } catch (RuntimeException unused) {
            this.attached = this.root.isAttachedToWindow() || this.root.getParent() != null;
            this.failed = true;
            this.root.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: migrateOnMain, reason: merged with bridge method [inline-methods] */
    public void m57lambda$migrate$2$comandroidsupportAllInAlertOverlay(WindowManager windowManager) {
        if (this.closeRequested || this.failed || this.windowManager == windowManager) {
            return;
        }
        this.handler.removeCallbacks(this.autoHide);
        makeGone(nextAnimationToken());
        if (this.attached) {
            try {
                this.windowManager.removeViewImmediate(this.root);
                this.attached = false;
            } catch (RuntimeException unused) {
                disableAfterWindowFailure();
                return;
            }
        }
        this.windowManager = windowManager;
        attachOnce();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeOnMain() {
        this.animationToken++;
        this.handler.removeCallbacks(this.autoHide);
        cancelCurrentAnimation();
        this.root.setVisibility(8);
        if (this.attached) {
            try {
                this.windowManager.removeView(this.root);
            } catch (RuntimeException unused) {
            }
        }
        this.attached = false;
        this.failed = true;
    }

    private void disableAfterWindowFailure() {
        this.animationToken++;
        this.handler.removeCallbacks(this.autoHide);
        cancelCurrentAnimation();
        this.root.setVisibility(8);
        this.failed = true;
    }

    private long nextAnimationToken() {
        long j = this.animationToken + 1;
        this.animationToken = j;
        cancelCurrentAnimation();
        return j;
    }

    private void cancelCurrentAnimation() {
        this.root.animate().setListener(null);
        this.root.animate().cancel();
    }

    private boolean canOperate() {
        return (this.closeRequested || this.failed || !this.attached) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCurrent(long j) {
        return canOperate() && this.animationToken == j;
    }

    private void runOnMain(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            this.handler.post(runnable);
        }
    }

    private int dp(int i) {
        return Math.round(i * this.density);
    }

    private float dpFloat(int i) {
        return i * this.density;
    }

    private static final class SafeBounds {
        final int bottom;
        final int left;
        final int right;
        final int top;

        SafeBounds(int i, int i2, int i3, int i4) {
            this.left = i;
            this.top = i2;
            this.right = i3;
            this.bottom = i4;
        }
    }
}
