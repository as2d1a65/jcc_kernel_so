package com.android.support;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.support.VoiceAlertBannerLifecycle;
import com.android.support.VoiceAlertBannerPosition;
import com.android.support.VoiceAlertBannerText;
import java.util.Objects;

/* loaded from: classes.dex */
final class VoiceAlertBannerOverlay implements VoiceAlertBannerLifecycle.Surface, AutoCloseable {
    private static final long ENTER_DURATION_MS = 350;
    private static final int SAFE_MARGIN_DP = 12;
    private long animationToken;
    private boolean attached;
    private volatile boolean closeRequested;
    private final Context context;
    private final TextView copiesView;
    private final float density;
    private boolean desiredVisible;
    private boolean failed;
    private final Handler handler;
    private final TextView heroView;
    private final TextView playerView;
    private final MaxWidthLinearLayout root;
    private final TextView starsView;
    private final TextView titleView;
    private WindowManager windowManager;
    private final WindowManager.LayoutParams windowParams;
    private int xPercent = 50;
    private int yPercent = 35;

    View rootView() {
        return this.root;
    }

    VoiceAlertBannerOverlay(Context context, WindowManager windowManager, Handler handler) {
        Context applicationContext = ((Context) Objects.requireNonNull(context, "context")).getApplicationContext();
        this.context = applicationContext;
        this.windowManager = (WindowManager) Objects.requireNonNull(windowManager, "windowManager");
        Handler handler2 = (Handler) Objects.requireNonNull(handler, "mainHandler");
        this.handler = handler2;
        if (handler2.getLooper() != Looper.getMainLooper()) {
            throw new IllegalArgumentException("mainHandler must use the main Looper");
        }
        this.density = applicationContext.getResources().getDisplayMetrics().density;
        this.root = new MaxWidthLinearLayout(applicationContext);
        this.starsView = new TextView(applicationContext);
        this.titleView = new TextView(applicationContext);
        this.playerView = new TextView(applicationContext);
        this.heroView = new TextView(applicationContext);
        this.copiesView = new TextView(applicationContext);
        configureView();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2038, 56, -3);
        this.windowParams = layoutParams;
        layoutParams.gravity = 8388659;
        layoutParams.alpha = 0.78f;
        runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.attachOnce();
            }
        });
    }

    VoiceAlertBannerLifecycle.Scheduler scheduler() {
        return new VoiceAlertBannerLifecycle.Scheduler() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda5
            @Override // com.android.support.VoiceAlertBannerLifecycle.Scheduler
            public final VoiceAlertBannerLifecycle.Cancelable schedule(long j, Runnable runnable) {
                return this.f$0.m1597lambda$scheduler$1$comandroidsupportVoiceAlertBannerOverlay(j, runnable);
            }
        };
    }

    /* renamed from: lambda$scheduler$1$com-android-support-VoiceAlertBannerOverlay, reason: not valid java name */
    /* synthetic */ VoiceAlertBannerLifecycle.Cancelable m1597lambda$scheduler$1$comandroidsupportVoiceAlertBannerOverlay(long j, final Runnable runnable) {
        this.handler.postDelayed(runnable, Math.max(0L, j));
        return new VoiceAlertBannerLifecycle.Cancelable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda9
            @Override // com.android.support.VoiceAlertBannerLifecycle.Cancelable
            public final void cancel() {
                this.f$0.m1596lambda$scheduler$0$comandroidsupportVoiceAlertBannerOverlay(runnable);
            }
        };
    }

    /* renamed from: lambda$scheduler$0$com-android-support-VoiceAlertBannerOverlay, reason: not valid java name */
    /* synthetic */ void m1596lambda$scheduler$0$comandroidsupportVoiceAlertBannerOverlay(Runnable runnable) {
        this.handler.removeCallbacks(runnable);
    }

    void setPositionPercent(int i, int i2) {
        final int iClampPercent = VoiceAlertBannerPosition.clampPercent(i);
        final int iClampPercent2 = VoiceAlertBannerPosition.clampPercent(i2);
        runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1598x28d64b53(iClampPercent, iClampPercent2);
            }
        });
    }

    /* renamed from: lambda$setPositionPercent$2$com-android-support-VoiceAlertBannerOverlay, reason: not valid java name */
    /* synthetic */ void m1598x28d64b53(int i, int i2) {
        if (canOperate()) {
            this.xPercent = i;
            this.yPercent = i2;
            requestPositionUpdate(this.animationToken);
        }
    }

    void onDisplayGeometryChanged() {
        runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1594x30c18eb9();
            }
        });
    }

    /* renamed from: lambda$onDisplayGeometryChanged$3$com-android-support-VoiceAlertBannerOverlay, reason: not valid java name */
    /* synthetic */ void m1594x30c18eb9() {
        if (canOperate()) {
            requestPositionUpdate(this.animationToken);
        }
    }

    void migrate(final WindowManager windowManager) {
        Objects.requireNonNull(windowManager, "newWindowManager");
        runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1593lambda$migrate$4$comandroidsupportVoiceAlertBannerOverlay(windowManager);
            }
        });
    }

    @Override // com.android.support.VoiceAlertBannerLifecycle.Surface
    public void showPreview() {
        showParts(VoiceAlertBannerText.create("预览玩家", "预览英雄", 8));
    }

    @Override // com.android.support.VoiceAlertBannerLifecycle.Surface
    public void showAlert(VoiceAlertRequest voiceAlertRequest) {
        if (voiceAlertRequest == null) {
            return;
        }
        showParts(VoiceAlertBannerText.create(voiceAlertRequest.getPlayerName(), voiceAlertRequest.getHeroName(), voiceAlertRequest.getCopies()));
    }

    @Override // com.android.support.VoiceAlertBannerLifecycle.Surface
    public void hide(final int i) {
        runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1592lambda$hide$5$comandroidsupportVoiceAlertBannerOverlay(i);
            }
        });
    }

    /* renamed from: lambda$hide$5$com-android-support-VoiceAlertBannerOverlay, reason: not valid java name */
    /* synthetic */ void m1592lambda$hide$5$comandroidsupportVoiceAlertBannerOverlay(int i) {
        hideOnMain(Math.max(0, i));
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.closeRequested) {
            return;
        }
        this.closeRequested = true;
        runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.closeOnMain();
            }
        });
    }

    private void configureView() {
        this.root.setOrientation(0);
        this.root.setGravity(16);
        this.root.setVisibility(8);
        this.root.setPadding(dp(18), dp(10), dp(18), dp(10));
        this.root.setElevation(dpFloat(12));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(-451669744);
        gradientDrawable.setCornerRadius(dpFloat(16));
        gradientDrawable.setStroke(Math.max(1, Math.round(dpFloat(1) * 1.5f)), -2579413);
        this.root.setBackground(gradientDrawable);
        configureSegment(this.starsView, -15770, true);
        configureSegment(this.titleView, -15770, true);
        configureSegment(this.playerView, -1, false);
        configureSegment(this.heroView, -7776, false);
        configureSegment(this.copiesView, -22467, true);
        this.playerView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.heroView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.root.addView(this.starsView, wrapContentParams());
        this.root.addView(this.titleView, wrapContentParams());
        this.root.addView(this.playerView, wrapContentParams());
        this.root.addView(this.heroView, wrapContentParams());
        this.root.addView(this.copiesView, wrapContentParams());
    }

    private void configureSegment(TextView textView, int i, boolean z) {
        textView.setSingleLine(true);
        textView.setTextColor(i);
        textView.setTextSize(2, 15.0f);
        textView.setTypeface(Typeface.create("sans-serif", z ? 1 : 0));
        textView.setIncludeFontPadding(false);
        GameHudPalette.applyTextShadow(textView);
    }

    private LinearLayout.LayoutParams wrapContentParams() {
        return new LinearLayout.LayoutParams(-2, -2);
    }

    private void showParts(final VoiceAlertBannerText.Parts parts) {
        runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1599lambda$showParts$6$comandroidsupportVoiceAlertBannerOverlay(parts);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showPartsOnMain, reason: merged with bridge method [inline-methods] */
    public void m1599lambda$showParts$6$comandroidsupportVoiceAlertBannerOverlay(VoiceAlertBannerText.Parts parts) {
        if (canOperate()) {
            this.desiredVisible = true;
            final long jNextAnimationToken = nextAnimationToken();
            bindParts(parts);
            this.root.setVisibility(0);
            this.root.setAlpha(0.0f);
            this.root.setTranslationY(-dpFloat(8));
            this.root.setScaleX(0.96f);
            this.root.setScaleY(0.96f);
            requestPositionUpdate(jNextAnimationToken);
            this.root.post(new Runnable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1600x10e4ca9f(jNextAnimationToken);
                }
            });
        }
    }

    /* renamed from: lambda$showPartsOnMain$7$com-android-support-VoiceAlertBannerOverlay, reason: not valid java name */
    /* synthetic */ void m1600x10e4ca9f(long j) {
        if (isCurrent(j) && this.root.getVisibility() == 0) {
            this.root.animate().alpha(1.0f).translationY(0.0f).scaleX(1.0f).scaleY(1.0f).setDuration(ENTER_DURATION_MS).setListener(null).start();
        }
    }

    private void bindParts(VoiceAlertBannerText.Parts parts) {
        this.starsView.setText(parts.stars + " ");
        this.titleView.setText(parts.title + "｜");
        this.playerView.setText(parts.playerName + " · ");
        this.heroView.setText(parts.heroName + " · ");
        this.copiesView.setText(parts.copies);
    }

    private void hideOnMain(int i) {
        if (!this.attached || this.failed) {
            return;
        }
        this.desiredVisible = false;
        final long jNextAnimationToken = nextAnimationToken();
        if (i == 0 || this.root.getVisibility() != 0) {
            makeGone(jNextAnimationToken);
        } else {
            this.root.animate().alpha(0.0f).translationY(-dpFloat(4)).setDuration(i).setListener(new AnimatorListenerAdapter() { // from class: com.android.support.VoiceAlertBannerOverlay.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (VoiceAlertBannerOverlay.this.isCurrent(jNextAnimationToken)) {
                        VoiceAlertBannerOverlay.this.makeGone(jNextAnimationToken);
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
        this.desiredVisible = false;
        this.root.animate().setListener(null);
        this.root.setVisibility(8);
        this.root.setAlpha(1.0f);
        this.root.setTranslationY(0.0f);
        this.root.setScaleX(1.0f);
        this.root.setScaleY(1.0f);
    }

    private void requestPositionUpdate(final long j) {
        applyWidthLimit(readSafeBounds());
        this.root.requestLayout();
        this.root.post(new Runnable() { // from class: com.android.support.VoiceAlertBannerOverlay$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1595xc9706426(j);
            }
        });
    }

    /* renamed from: lambda$requestPositionUpdate$8$com-android-support-VoiceAlertBannerOverlay, reason: not valid java name */
    /* synthetic */ void m1595xc9706426(long j) {
        if (canOperate() && j == this.animationToken) {
            updatePositionNow(readSafeBounds());
        }
    }

    private void applyWidthLimit(SafeBounds safeBounds) {
        int iMax = Math.max(1, Math.max(1, safeBounds.right - safeBounds.left) / 2);
        this.root.setMaxWidth(iMax);
        int iMax2 = Math.max(0, iMax - ((((this.root.getPaddingLeft() + this.root.getPaddingRight()) + desiredWidth(this.starsView)) + desiredWidth(this.titleView)) + desiredWidth(this.copiesView)));
        int iDesiredWidth = desiredWidth(this.playerView);
        int iDesiredWidth2 = desiredWidth(this.heroView);
        int iMax3 = Math.max(0, (iDesiredWidth + iDesiredWidth2) - iMax2);
        int iMin = Math.min(iDesiredWidth, iMax3);
        int iMax4 = Math.max(0, iDesiredWidth2 - (iMax3 - iMin));
        this.playerView.setMaxWidth(iDesiredWidth - iMin);
        this.heroView.setMaxWidth(iMax4);
    }

    private int desiredWidth(TextView textView) {
        return textView.getCompoundPaddingLeft() + ((int) Math.ceil(textView.getPaint().measureText(textView.getText() == null ? "" : r3.toString()))) + textView.getCompoundPaddingRight();
    }

    private void updatePositionNow(SafeBounds safeBounds) {
        int measuredWidth = this.root.getMeasuredWidth();
        int measuredHeight = this.root.getMeasuredHeight();
        if (measuredWidth <= 0 || measuredHeight <= 0) {
            return;
        }
        VoiceAlertBannerPosition.PixelPosition pixelPositionCalculate = VoiceAlertBannerPosition.calculate(safeBounds.left, safeBounds.top, safeBounds.right, safeBounds.bottom, measuredWidth, measuredHeight, dp(12), this.xPercent, this.yPercent);
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
    public void m1593lambda$migrate$4$comandroidsupportVoiceAlertBannerOverlay(WindowManager windowManager) {
        if (this.closeRequested || this.failed || this.windowManager == windowManager) {
            return;
        }
        long jNextAnimationToken = nextAnimationToken();
        if (this.desiredVisible) {
            this.root.setVisibility(0);
            this.root.setAlpha(1.0f);
            this.root.setTranslationY(0.0f);
            this.root.setScaleX(1.0f);
            this.root.setScaleY(1.0f);
        } else {
            makeGone(jNextAnimationToken);
        }
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
        if (canOperate()) {
            requestPositionUpdate(jNextAnimationToken);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeOnMain() {
        this.animationToken++;
        this.desiredVisible = false;
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
        this.desiredVisible = false;
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

    private static final class MaxWidthLinearLayout extends LinearLayout {
        private int maxWidth;

        MaxWidthLinearLayout(Context context) {
            super(context);
            this.maxWidth = Integer.MAX_VALUE;
        }

        void setMaxWidth(int i) {
            int iMax = Math.max(1, i);
            if (this.maxWidth != iMax) {
                this.maxWidth = iMax;
                requestLayout();
            }
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int mode = View.MeasureSpec.getMode(i);
            int size = View.MeasureSpec.getSize(i);
            if (mode == 0 || size > this.maxWidth) {
                i = View.MeasureSpec.makeMeasureSpec(this.maxWidth, Integer.MIN_VALUE);
            }
            super.onMeasure(i, i2);
        }
    }
}
