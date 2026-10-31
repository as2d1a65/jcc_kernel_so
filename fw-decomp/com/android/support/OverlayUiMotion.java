package com.android.support;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;

/* loaded from: classes.dex */
public final class OverlayUiMotion {
    private static final long ANIMATION_FLAG_TTL_MS = 3000;
    private static final int CONTAINER_OFFSET_DP = 4;
    private static final float CONTAINER_SCALE = 0.985f;
    private static volatile long cachedAnimationsAtMs;
    private static final TimeInterpolator EASE_OUT = new PathInterpolator(0.12f, 0.92f, 0.28f, 1.0f);
    private static final TimeInterpolator EASE_IN_OUT = new PathInterpolator(0.42f, 0.0f, 0.18f, 1.0f);
    private static final TimeInterpolator SETTLE_OUT = new OvershootInterpolator(0.32f);
    private static final TimeInterpolator SPRING_OUT = new PathInterpolator(0.18f, 0.89f, 0.32f, 1.27f);
    private static volatile boolean cachedAnimationsEnabled = true;

    private OverlayUiMotion() {
    }

    public static void enableRoundedClip(View view) {
        if (view != null) {
            view.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            view.setClipToOutline(true);
        }
    }

    public static boolean animationsEnabled(Context context) {
        boolean z = true;
        if (context == null) {
            return true;
        }
        long jUptimeMillis = SystemClock.uptimeMillis();
        long j = cachedAnimationsAtMs;
        if (j != 0 && jUptimeMillis - j < ANIMATION_FLAG_TTL_MS) {
            return cachedAnimationsEnabled;
        }
        try {
            if (Settings.Global.getFloat(context.getContentResolver(), "animator_duration_scale", 1.0f) <= 0.0f) {
                z = false;
            }
        } catch (RuntimeException unused) {
        }
        cachedAnimationsEnabled = z;
        cachedAnimationsAtMs = jUptimeMillis;
        return z;
    }

    public static void prepareCompanionEnter(View view, int i) {
        if (view == null) {
            return;
        }
        clearAnimator(view);
        view.setVisibility(0);
        if (!animationsEnabled(view.getContext())) {
            finishVisibleState(view);
            return;
        }
        int i2 = i < 0 ? -1 : 1;
        view.setAlpha(0.0f);
        view.setTranslationX(i2 * dp(view, 14));
        view.setScaleX(0.98f);
        view.setScaleY(0.98f);
    }

    public static void playCompanionEnter(View view, int i) {
        if (view == null) {
            return;
        }
        if (!animationsEnabled(view.getContext())) {
            finishVisibleState(view);
            return;
        }
        clearAnimator(view);
        view.setVisibility(0);
        view.animate().alpha(1.0f).translationX(0.0f).scaleX(1.0f).scaleY(1.0f).setDuration(450L).setInterpolator(SPRING_OUT).setListener(null).withLayer().start();
    }

    public static void playCompanionExit(View view, int i, final Runnable runnable) {
        if (view == null || !animationsEnabled(view.getContext())) {
            runOnce(runnable);
        } else {
            clearAnimator(view);
            view.animate().alpha(0.0f).translationX((i < 0 ? -1 : 1) * dp(view, 10)).scaleX(0.96f).scaleY(0.96f).setDuration(150L).setInterpolator(EASE_IN_OUT).setListener(new AnimatorListenerAdapter() { // from class: com.android.support.OverlayUiMotion.1
                private boolean settled;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    finish();
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    finish();
                }

                private void finish() {
                    if (this.settled) {
                        return;
                    }
                    this.settled = true;
                    OverlayUiMotion.runOnce(runnable);
                }
            }).withLayer().start();
        }
    }

    public static void finishVisibleState(View view) {
        if (view == null) {
            return;
        }
        clearAnimator(view);
        view.setVisibility(0);
        view.setAlpha(1.0f);
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
    }

    public static void installPressScale(View view) {
        if (view != null) {
            if (!animationsEnabled(view.getContext())) {
                view.setStateListAnimator(null);
                return;
            }
            AnimatorSet animatorSetScaleAnimator = scaleAnimator(view, 0.965f, 90L);
            AnimatorSet animatorSetScaleAnimator2 = scaleAnimator(view, 1.0f, 200L);
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{android.R.attr.state_pressed}, animatorSetScaleAnimator);
            stateListAnimator.addState(new int[0], animatorSetScaleAnimator2);
            view.setStateListAnimator(stateListAnimator);
        }
    }

    public static void pulseValue(View view) {
        if (view == null) {
            return;
        }
        if (!animationsEnabled(view.getContext())) {
            finishVisibleState(view);
            return;
        }
        clearAnimator(view);
        view.setAlpha(1.0f);
        view.setScaleX(0.97f);
        view.setScaleY(0.97f);
        view.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(220L).setInterpolator(SPRING_OUT).setListener(null).start();
    }

    public static void animateListItem(View view, int i) {
        if (view == null) {
            return;
        }
        if (!animationsEnabled(view.getContext())) {
            finishVisibleState(view);
            return;
        }
        clearAnimator(view);
        view.setAlpha(0.0f);
        view.setTranslationY(dp(view, 5));
        view.setScaleX(0.98f);
        view.setScaleY(0.98f);
        view.animate().alpha(1.0f).translationY(0.0f).scaleX(1.0f).scaleY(1.0f).setStartDelay(OverlayUiStylePolicy.listEnterDelayMs(i)).setDuration(260L).setInterpolator(EASE_OUT).setListener(null).start();
    }

    public static void playContainerTransition(final View view, final View view2, int i, final Runnable runnable) {
        if (view == null || view2 == null) {
            if (view != null) {
                finishHiddenState(view);
            }
            if (view2 != null) {
                finishVisibleState(view2);
            }
            runOnce(runnable);
            return;
        }
        clearAnimator(view);
        clearAnimator(view2);
        if (!animationsEnabled(view.getContext()) || view.getVisibility() != 0) {
            finishHiddenState(view);
            finishVisibleState(view2);
            runOnce(runnable);
        } else {
            final int i2 = i < 0 ? -1 : 1;
            view.animate().alpha(0.0f).translationX((-i2) * dp(view, 4)).scaleX(CONTAINER_SCALE).scaleY(CONTAINER_SCALE).setDuration(140L).setInterpolator(EASE_IN_OUT).withLayer().setListener(new AnimatorListenerAdapter() { // from class: com.android.support.OverlayUiMotion.2
                private boolean completed;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (this.completed) {
                        return;
                    }
                    this.completed = true;
                    OverlayUiMotion.finishHiddenState(view);
                    view2.setVisibility(0);
                    view2.setAlpha(0.0f);
                    View view3 = view2;
                    view3.setTranslationX(i2 * OverlayUiMotion.dp(view3, 4));
                    view2.setScaleX(OverlayUiMotion.CONTAINER_SCALE);
                    view2.setScaleY(OverlayUiMotion.CONTAINER_SCALE);
                    OverlayUiMotion.runOnce(runnable);
                    view2.animate().alpha(1.0f).translationX(0.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200L).setInterpolator(OverlayUiMotion.EASE_OUT).setListener(null).withLayer().start();
                }
            }).start();
        }
    }

    public static void crossfadeVisibility(View view, View view2, Runnable runnable) {
        playContainerTransition(view, view2, 1, runnable);
    }

    private static AnimatorSet scaleAnimator(View view, float f, long j) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_X, f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, f));
        animatorSet.setDuration(j);
        animatorSet.setInterpolator(EASE_IN_OUT);
        return animatorSet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void finishHiddenState(View view) {
        clearAnimator(view);
        view.setVisibility(8);
        view.setAlpha(1.0f);
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
    }

    private static void clearAnimator(View view) {
        view.animate().setListener(null);
        view.animate().cancel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void runOnce(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float dp(View view, int i) {
        return i * view.getResources().getDisplayMetrics().density;
    }
}
