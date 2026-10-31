package com.android.support;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import com.android.support.AuthStatusCardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
final class AuthStatusCardController {
    static final long SUCCESS_SHOW_MS = 3000;
    private static final String TAG = "FloatingWindowService";
    private AuthStatusCardView cardView;
    private Runnable hideRunnable;
    private final Host host;
    private WindowManager.LayoutParams layoutParams;
    private Boolean shownVerified;

    interface Host {
        Context context();

        long expireTime();

        void onCardAction(boolean z);

        void postDelayed(Runnable runnable, long j);

        void recordOverlayOwner(View view, WindowManager windowManager);

        void removeCallbacks(Runnable runnable);

        boolean removeTrustedVisualOverlay(View view, String str);

        boolean serviceDestroyed();

        boolean verified();

        WindowManager windowManager();
    }

    AuthStatusCardController(Host host) {
        this.host = host;
    }

    void sync() {
        AuthStatusCardView authStatusCardView;
        if (this.host.serviceDestroyed() || this.host.windowManager() == null) {
            return;
        }
        boolean zVerified = this.host.verified();
        Boolean bool = this.shownVerified;
        if (bool == null || bool.booleanValue() != zVerified || (authStatusCardView = this.cardView) == null || authStatusCardView.getParent() == null) {
            ensureCard();
            if (this.cardView == null) {
                return;
            }
            this.shownVerified = Boolean.valueOf(zVerified);
            this.cardView.setState(zVerified, formatExpiry(this.host.expireTime()));
            attachCard();
            if (zVerified) {
                this.cardView.startProgress(SUCCESS_SHOW_MS);
                scheduleAutoHide();
            } else {
                cancelAutoHide();
            }
        }
    }

    void remove() {
        cancelAutoHide();
        this.shownVerified = null;
        AuthStatusCardView authStatusCardView = this.cardView;
        if (authStatusCardView == null) {
            return;
        }
        try {
            if (!this.host.removeTrustedVisualOverlay(authStatusCardView, "auth-status-card")) {
                WindowManager windowManager = this.host.windowManager();
                if (this.cardView.getParent() != null && windowManager != null) {
                    windowManager.removeViewImmediate(this.cardView);
                }
            }
        } catch (Exception unused) {
        }
        this.cardView = null;
        this.layoutParams = null;
    }

    static String formatExpiry(long j) {
        if (j <= 0) {
            return "";
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date(j));
        } catch (Exception unused) {
            return "";
        }
    }

    private void ensureCard() {
        if (this.cardView != null || this.host.windowManager() == null) {
            return;
        }
        Context context = this.host.context();
        try {
            this.cardView = new AuthStatusCardView(context, new AuthStatusCardView.Listener() { // from class: com.android.support.AuthStatusCardController.1
                @Override // com.android.support.AuthStatusCardView.Listener
                public void onCardClick() {
                    AuthStatusCardController.this.host.onCardAction(AuthStatusCardController.this.host.verified());
                }

                @Override // com.android.support.AuthStatusCardView.Listener
                public void onSettingsClick() {
                    AuthStatusCardController.this.host.onCardAction(AuthStatusCardController.this.host.verified());
                }
            });
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(XfOverlayScale.cardWidth(context), XfOverlayScale.noticeHeight(context, true), 2038, 296, -3);
            this.layoutParams = layoutParams;
            layoutParams.gravity = 8388661;
            this.layoutParams.x = XfOverlayScale.endOffset(context);
            this.layoutParams.y = XfOverlayScale.topOffset(context);
            this.layoutParams.layoutInDisplayCutoutMode = 3;
            attachCard();
        } catch (Exception e) {
            Log.w(TAG, "[auth-status] ensure failed", e);
            this.cardView = null;
            this.layoutParams = null;
        }
    }

    private void attachCard() {
        WindowManager windowManager = this.host.windowManager();
        AuthStatusCardView authStatusCardView = this.cardView;
        if (authStatusCardView == null || this.layoutParams == null || windowManager == null) {
            return;
        }
        try {
            if (authStatusCardView.getParent() != null && !this.host.removeTrustedVisualOverlay(this.cardView, "auth-status-card-stale")) {
                try {
                    windowManager.removeViewImmediate(this.cardView);
                } catch (Exception unused) {
                }
            }
            windowManager.addView(this.cardView, this.layoutParams);
            this.host.recordOverlayOwner(this.cardView, windowManager);
            this.cardView.setAlpha(0.0f);
            this.cardView.setTranslationX(ScreenUtils.dp2px(this.host.context(), 24.0f));
            this.cardView.animate().alpha(1.0f).translationX(0.0f).setDuration(260L).setInterpolator(new DecelerateInterpolator(1.8f)).start();
        } catch (Exception e) {
            Log.w(TAG, "[auth-status] attach failed", e);
        }
    }

    private void scheduleAutoHide() {
        cancelAutoHide();
        Runnable runnable = new Runnable() { // from class: com.android.support.AuthStatusCardController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m78x4155fe1();
            }
        };
        this.hideRunnable = runnable;
        this.host.postDelayed(runnable, SUCCESS_SHOW_MS);
    }

    /* renamed from: lambda$scheduleAutoHide$0$com-android-support-AuthStatusCardController, reason: not valid java name */
    /* synthetic */ void m78x4155fe1() {
        this.hideRunnable = null;
        remove();
    }

    private void cancelAutoHide() {
        Runnable runnable = this.hideRunnable;
        if (runnable == null) {
            return;
        }
        this.host.removeCallbacks(runnable);
        this.hideRunnable = null;
    }
}
