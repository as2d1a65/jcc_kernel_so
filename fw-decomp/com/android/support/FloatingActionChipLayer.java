package com.android.support;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import com.android.support.OverlayDragPump;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
final class FloatingActionChipLayer {
    private static final long EXIT_FADE_MS = 160;
    private final Host host;
    private final int touchSlop;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<Integer, Chip> chips = new LinkedHashMap();

    interface Host {
        Context context();

        boolean inGameHudReady();

        boolean isChipActive(int i);

        boolean isChipPending(int i);

        void onChipTapped(int i);

        int overlayHeight();

        int overlayWidth();

        SharedPreferences preferences();

        WindowManager windowManager();
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class Chip {
        boolean added;
        Runnable dimRunnable;
        float downRawX;
        float downRawY;
        int downX;
        int downY;
        boolean dragging;
        WindowManager.LayoutParams params;
        OverlayDragPump pump;
        int screenHeight;
        int screenWidth;
        ValueAnimator settleAnimator;
        FloatingActionChipView view;

        private Chip() {
        }
    }

    FloatingActionChipLayer(Host host) {
        this.host = host;
        this.touchSlop = ViewConfiguration.get(host.context()).getScaledTouchSlop();
    }

    void appendSecureTargets(List<View> list) {
        if (list == null) {
            return;
        }
        for (Chip chip : this.chips.values()) {
            if (chip != null && chip.view != null) {
                list.add(chip.view);
            }
        }
    }

    boolean isEnabled(int i) {
        SharedPreferences sharedPreferencesPreferences;
        if (FloatingActionChipPolicy.isKind(i) && (sharedPreferencesPreferences = this.host.preferences()) != null) {
            return sharedPreferencesPreferences.getBoolean(FloatingActionChipPolicy.enabledKey(i), false);
        }
        return false;
    }

    void setEnabled(int i, boolean z) {
        if (FloatingActionChipPolicy.isKind(i)) {
            SharedPreferences sharedPreferencesPreferences = this.host.preferences();
            if (sharedPreferencesPreferences != null) {
                sharedPreferencesPreferences.edit().putBoolean(FloatingActionChipPolicy.enabledKey(i), z).apply();
            }
            if (z) {
                show(i);
            } else {
                hide(i);
            }
        }
    }

    void restoreAll() {
        for (int i : FloatingActionChipPolicy.ALL_KINDS) {
            if (isEnabled(i)) {
                show(i);
            } else {
                hide(i);
            }
        }
    }

    void detachAll() {
        for (int i : FloatingActionChipPolicy.ALL_KINDS) {
            hide(i);
        }
    }

    int scalePercent() {
        SharedPreferences sharedPreferencesPreferences = this.host.preferences();
        return FloatingActionChipPolicy.normalizeScalePercent(sharedPreferencesPreferences != null ? sharedPreferencesPreferences.getInt("fab_chip_scale_percent", 100) : 100);
    }

    void setScalePercent(int i) {
        int iNormalizeScalePercent = FloatingActionChipPolicy.normalizeScalePercent(i);
        SharedPreferences sharedPreferencesPreferences = this.host.preferences();
        if (sharedPreferencesPreferences != null) {
            sharedPreferencesPreferences.edit().putInt("fab_chip_scale_percent", iNormalizeScalePercent).apply();
        }
        for (Map.Entry<Integer, Chip> entry : this.chips.entrySet()) {
            final Chip value = entry.getValue();
            if (value.view != null) {
                value.view.setScalePercent(iNormalizeScalePercent);
                if (value.added) {
                    final int iIntValue = entry.getKey().intValue();
                    value.view.post(new Runnable() { // from class: com.android.support.FloatingActionChipLayer$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m148x1c702849(iIntValue, value);
                        }
                    });
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: reclampAfterResize, reason: merged with bridge method [inline-methods] */
    public void m148x1c702849(int i, Chip chip) {
        if (chip.view == null || chip.params == null || !chip.added) {
            return;
        }
        int iMax = Math.max(1, this.host.overlayWidth());
        int iMax2 = Math.max(1, this.host.overlayHeight());
        int iMax3 = Math.max(1, chip.view.getWidth());
        int iMax4 = Math.max(1, chip.view.getHeight());
        int iClampX = FloatingActionChipPolicy.clampX(chip.params.x, iMax3, iMax);
        int iClampY = FloatingActionChipPolicy.clampY(chip.params.y, iMax4, iMax2);
        if (iClampX == chip.params.x && iClampY == chip.params.y) {
            return;
        }
        chip.params.x = iClampX;
        chip.params.y = iClampY;
        commit(chip);
        persistPosition(i, chip);
    }

    void flash(int i, String str, int i2) {
        Chip chip = this.chips.get(Integer.valueOf(i));
        if (chip == null || chip.view == null || !chip.added) {
            return;
        }
        chip.view.flash(str, i2);
        wakeUp(chip);
        scheduleIdleDim(chip);
    }

    void syncState() {
        for (Chip chip : this.chips.values()) {
            if (chip.view != null) {
                int iKind = chip.view.kind();
                chip.view.setState(this.host.isChipActive(iKind), this.host.isChipPending(iKind));
            }
        }
    }

    private void show(final int i) {
        WindowManager windowManager = this.host.windowManager();
        Context context = this.host.context();
        if (windowManager == null || context == null) {
            return;
        }
        if (!this.host.inGameHudReady()) {
            hide(i);
            return;
        }
        final Chip chip = this.chips.get(Integer.valueOf(i));
        if (chip != null && chip.added) {
            chip.view.setState(this.host.isChipActive(i), this.host.isChipPending(i));
            return;
        }
        if (chip == null) {
            chip = new Chip();
        }
        this.chips.put(Integer.valueOf(i), chip);
        if (chip.view == null) {
            chip.view = new FloatingActionChipView(context, i);
            chip.view.setScalePercent(scalePercent());
            chip.view.setState(this.host.isChipActive(i), this.host.isChipPending(i));
            chip.view.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.FloatingActionChipLayer$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m150lambda$show$1$comandroidsupportFloatingActionChipLayer(chip, i, view);
                }
            });
            attachTouch(i, chip);
        }
        if (chip.params == null) {
            chip.params = buildParams();
            restorePosition(i, chip);
        }
        if (chip.view.isAttachedToWindow() || chip.view.getParent() != null) {
            chip.view.animate().cancel();
            chip.added = true;
            chip.view.setAlpha(1.0f);
            chip.view.setScaleX(1.0f);
            chip.view.setScaleY(1.0f);
            scheduleIdleDim(chip);
            return;
        }
        try {
            windowManager.addView(chip.view, chip.params);
            chip.added = true;
            chip.view.setAlpha(0.0f);
            chip.view.setScaleX(0.86f);
            chip.view.setScaleY(0.86f);
            chip.view.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(220L).withLayer().start();
            scheduleIdleDim(chip);
        } catch (RuntimeException unused) {
        }
    }

    /* renamed from: lambda$show$1$com-android-support-FloatingActionChipLayer, reason: not valid java name */
    /* synthetic */ void m150lambda$show$1$comandroidsupportFloatingActionChipLayer(Chip chip, int i, View view) {
        if (chip.view.isPending()) {
            return;
        }
        scheduleIdleDim(chip);
        this.host.onChipTapped(i);
    }

    private void hide(int i) {
        Chip chip = this.chips.get(Integer.valueOf(i));
        if (chip == null) {
            return;
        }
        cancelIdleDim(chip);
        if (chip.settleAnimator != null) {
            chip.settleAnimator.cancel();
            chip.settleAnimator = null;
        }
        if (chip.pump != null) {
            chip.pump.cancel();
        }
        if (!chip.added || chip.view == null) {
            chip.added = false;
            return;
        }
        final FloatingActionChipView floatingActionChipView = chip.view;
        chip.added = false;
        floatingActionChipView.animate().cancel();
        floatingActionChipView.animate().alpha(0.0f).scaleX(0.86f).scaleY(0.86f).setDuration(EXIT_FADE_MS).withEndAction(new Runnable() { // from class: com.android.support.FloatingActionChipLayer$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m146lambda$hide$2$comandroidsupportFloatingActionChipLayer(floatingActionChipView);
            }
        }).start();
        this.handler.postDelayed(new Runnable() { // from class: com.android.support.FloatingActionChipLayer$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m147lambda$hide$3$comandroidsupportFloatingActionChipLayer(floatingActionChipView);
            }
        }, 240L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: detach, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public void m147lambda$hide$3$comandroidsupportFloatingActionChipLayer(View view) {
        WindowManager windowManager;
        if (view == null) {
            return;
        }
        if ((view.isAttachedToWindow() || view.getParent() != null) && (windowManager = this.host.windowManager()) != null) {
            try {
                windowManager.removeViewImmediate(view);
            } catch (RuntimeException unused) {
            }
        }
    }

    private WindowManager.LayoutParams buildParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2038, 16777768, -3);
        layoutParams.gravity = 8388659;
        return layoutParams;
    }

    private void restorePosition(int i, Chip chip) {
        SharedPreferences sharedPreferencesPreferences = this.host.preferences();
        int iMax = Math.max(1, this.host.overlayWidth());
        int iMax2 = Math.max(1, this.host.overlayHeight());
        float f = this.host.context().getResources().getDisplayMetrics().density;
        int iScalePercent = scalePercent();
        int iRound = Math.round(FloatingActionChipPolicy.scaled(116.0f, iScalePercent) * f);
        int iRound2 = Math.round(FloatingActionChipPolicy.scaled(44.0f, iScalePercent) * f);
        int iRound3 = Math.round(16.0f * f);
        int iRound4 = Math.round(f * 12.0f);
        int iDefaultX = FloatingActionChipPolicy.defaultX(iRound, iMax, iRound3);
        int iDefaultY = FloatingActionChipPolicy.defaultY(i, iRound2, iRound4, iMax2, iRound3);
        int i2 = sharedPreferencesPreferences == null ? Integer.MIN_VALUE : sharedPreferencesPreferences.getInt(FloatingActionChipPolicy.posXKey(i), Integer.MIN_VALUE);
        int i3 = sharedPreferencesPreferences == null ? Integer.MIN_VALUE : sharedPreferencesPreferences.getInt(FloatingActionChipPolicy.posYKey(i), Integer.MIN_VALUE);
        if (i2 != Integer.MIN_VALUE) {
            iDefaultX = i2;
        }
        if (i3 != Integer.MIN_VALUE) {
            iDefaultY = i3;
        }
        chip.params.x = FloatingActionChipPolicy.clampX(iDefaultX, iRound, iMax);
        chip.params.y = FloatingActionChipPolicy.clampY(iDefaultY, iRound2, iMax2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void persistPosition(int i, Chip chip) {
        SharedPreferences sharedPreferencesPreferences = this.host.preferences();
        if (sharedPreferencesPreferences == null || chip.params == null) {
            return;
        }
        sharedPreferencesPreferences.edit().putInt(FloatingActionChipPolicy.posXKey(i), chip.params.x).putInt(FloatingActionChipPolicy.posYKey(i), chip.params.y).apply();
    }

    private void attachTouch(final int i, final Chip chip) {
        chip.pump = new OverlayDragPump(new OverlayDragPump.Target() { // from class: com.android.support.FloatingActionChipLayer.1
            @Override // com.android.support.OverlayDragPump.Target
            public void onPositionApplied() {
            }

            @Override // com.android.support.OverlayDragPump.Target
            public View view() {
                return chip.view;
            }

            @Override // com.android.support.OverlayDragPump.Target
            public WindowManager.LayoutParams params() {
                return chip.params;
            }

            @Override // com.android.support.OverlayDragPump.Target
            public WindowManager windowManager() {
                return FloatingActionChipLayer.this.host.windowManager();
            }

            @Override // com.android.support.OverlayDragPump.Target
            public void applyPosition(float f, float f2) {
                int width = chip.view.getWidth();
                int height = chip.view.getHeight();
                int iRound = chip.downX + Math.round(f - chip.downRawX);
                int iRound2 = chip.downY + Math.round(f2 - chip.downRawY);
                chip.params.x = FloatingActionChipPolicy.clampX(iRound, width, chip.screenWidth);
                chip.params.y = FloatingActionChipPolicy.clampY(iRound2, height, chip.screenHeight);
            }
        });
        chip.view.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.support.FloatingActionChipLayer$$ExternalSyntheticLambda4
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.m145lambda$attachTouch$4$comandroidsupportFloatingActionChipLayer(chip, i, view, motionEvent);
            }
        });
    }

    /* renamed from: lambda$attachTouch$4$com-android-support-FloatingActionChipLayer, reason: not valid java name */
    /* synthetic */ boolean m145lambda$attachTouch$4$comandroidsupportFloatingActionChipLayer(Chip chip, int i, View view, MotionEvent motionEvent) {
        if (chip.params == null) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            cancelIdleDim(chip);
            wakeUp(chip);
            if (chip.settleAnimator != null) {
                chip.settleAnimator.cancel();
                chip.settleAnimator = null;
            }
            chip.downRawX = motionEvent.getRawX();
            chip.downRawY = motionEvent.getRawY();
            chip.downX = chip.params.x;
            chip.downY = chip.params.y;
            chip.screenWidth = Math.max(1, this.host.overlayWidth());
            chip.screenHeight = Math.max(1, this.host.overlayHeight());
            chip.dragging = false;
            chip.pump.start(motionEvent.getRawX(), motionEvent.getRawY());
            view.setPressed(true);
            return true;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                if (!chip.dragging && OverlayDragMath.exceededSlop(motionEvent.getRawX() - chip.downRawX, motionEvent.getRawY() - chip.downRawY, this.touchSlop)) {
                    chip.dragging = true;
                    view.setPressed(false);
                }
                if (chip.dragging) {
                    chip.pump.request(motionEvent.getRawX(), motionEvent.getRawY());
                }
                return true;
            }
            if (actionMasked != 3) {
                return false;
            }
        }
        view.setPressed(false);
        chip.pump.finish();
        if (chip.dragging) {
            settle(i, chip);
        } else if (motionEvent.getActionMasked() == 1) {
            view.performClick();
        }
        chip.dragging = false;
        scheduleIdleDim(chip);
        return true;
    }

    private void settle(final int i, final Chip chip) {
        if (chip.view == null || chip.params == null) {
            return;
        }
        final int i2 = chip.params.x;
        final int i3 = chip.params.y;
        final int i4 = FloatingActionChipPolicy.settleX(i2, chip.view.getWidth(), chip.screenWidth);
        final int i5 = FloatingActionChipPolicy.settleY(i3, chip.view.getHeight(), chip.screenHeight);
        if (i2 == i4 && i3 == i5) {
            persistPosition(i, chip);
            return;
        }
        if (!OverlayUiMotion.animationsEnabled(this.host.context())) {
            chip.params.x = i4;
            chip.params.y = i5;
            commit(chip);
            persistPosition(i, chip);
            return;
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(260L);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.support.FloatingActionChipLayer$$ExternalSyntheticLambda6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.m149lambda$settle$5$comandroidsupportFloatingActionChipLayer(chip, i2, i4, i3, i5, valueAnimator);
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.support.FloatingActionChipLayer.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                chip.settleAnimator = null;
                FloatingActionChipLayer.this.persistPosition(i, chip);
            }
        });
        chip.settleAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.start();
    }

    /* renamed from: lambda$settle$5$com-android-support-FloatingActionChipLayer, reason: not valid java name */
    /* synthetic */ void m149lambda$settle$5$comandroidsupportFloatingActionChipLayer(Chip chip, int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        chip.params.x = Math.round(i + ((i2 - i) * fFloatValue));
        chip.params.y = Math.round(i3 + ((i4 - i3) * fFloatValue));
        commit(chip);
    }

    private void commit(Chip chip) {
        WindowManager windowManager = this.host.windowManager();
        if (windowManager == null || chip.view == null || !chip.added) {
            return;
        }
        try {
            windowManager.updateViewLayout(chip.view, chip.params);
        } catch (RuntimeException unused) {
        }
    }

    private void scheduleIdleDim(final Chip chip) {
        cancelIdleDim(chip);
        if (chip.view == null) {
            return;
        }
        chip.dimRunnable = new Runnable() { // from class: com.android.support.FloatingActionChipLayer$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FloatingActionChipLayer.lambda$scheduleIdleDim$6(chip);
            }
        };
        this.handler.postDelayed(chip.dimRunnable, 2600L);
    }

    static /* synthetic */ void lambda$scheduleIdleDim$6(Chip chip) {
        if (chip.view == null || !chip.added) {
            return;
        }
        chip.view.animate().alpha(0.72f).setDuration(420L).start();
    }

    private void cancelIdleDim(Chip chip) {
        if (chip.dimRunnable == null) {
            return;
        }
        this.handler.removeCallbacks(chip.dimRunnable);
        chip.dimRunnable = null;
    }

    private void wakeUp(Chip chip) {
        if (chip.view == null) {
            return;
        }
        chip.view.animate().alpha(1.0f).setDuration(120L).start();
    }
}
