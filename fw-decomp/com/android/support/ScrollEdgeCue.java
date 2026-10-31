package com.android.support;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import com.android.support.diagnostics.TraceCatalog;

/* loaded from: classes.dex */
final class ScrollEdgeCue {
    private static Press press;

    private ScrollEdgeCue() {
    }

    static void attach(View view) {
        if (view == null) {
            return;
        }
        view.setVerticalScrollBarEnabled(false);
        view.setHorizontalScrollBarEnabled(false);
        if (view.getTag(R.id.tag_scroll_edge_cue) != null) {
            return;
        }
        Controller controller = new Controller(view);
        view.setTag(R.id.tag_scroll_edge_cue, controller);
        controller.install();
    }

    static int attachTree(View view) {
        int iAttachTree;
        if (view == null) {
            return 0;
        }
        if (isScrollContainer(view) && view.getId() != R.id.svSpecifiedRecastTargets && view.getTag(R.id.tag_scroll_edge_cue) == null) {
            attach(view);
            iAttachTree = 1;
        } else {
            iAttachTree = 0;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                iAttachTree += attachTree(viewGroup.getChildAt(i));
            }
        }
        return iAttachTree;
    }

    static boolean isScrollContainer(View view) {
        return (view instanceof ScrollView) || (view instanceof HorizontalScrollView) || (view instanceof AbsListView);
    }

    static boolean routeTouch(View view, MotionEvent motionEvent) {
        if (view == null || motionEvent == null) {
            return false;
        }
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            clearPress();
            Controller.cancelRunningScroll();
            Press pressFindPress = findPress(view, rawX, rawY);
            if (pressFindPress == null) {
                return false;
            }
            press = pressFindPress;
            pressFindPress.owner.setPressed(pressFindPress.direction, true);
            return true;
        }
        if (actionMasked == 1) {
            Press press2 = press;
            if (press2 == null) {
                return false;
            }
            boolean zCueContains = press2.owner.cueContains(press2.direction, rawX, rawY);
            clearPress();
            if (zCueContains) {
                press2.owner.stepScroll(press2.direction);
            }
            return true;
        }
        if (actionMasked == 2) {
            Press press3 = press;
            if (press3 == null) {
                return false;
            }
            press3.owner.setPressed(press.direction, press.owner.cueContains(press.direction, rawX, rawY));
            return true;
        }
        if (press == null) {
            return false;
        }
        clearPress();
        return true;
    }

    private static void clearPress() {
        Press press2 = press;
        if (press2 == null) {
            return;
        }
        press2.owner.setPressed(press.direction, false);
        press = null;
    }

    private static Press findPress(View view, float f, float f2) {
        Controller controller;
        int iCueAt;
        if (view.getVisibility() != 0 || !(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
            Press pressFindPress = findPress(viewGroup.getChildAt(childCount), f, f2);
            if (pressFindPress != null) {
                return pressFindPress;
            }
        }
        Object tag = view.getTag(R.id.tag_scroll_edge_cue);
        if ((tag instanceof Controller) && (iCueAt = (controller = (Controller) tag).cueAt(f, f2)) >= 0) {
            return new Press(controller, iCueAt);
        }
        return null;
    }

    private static final class Press {
        final int direction;
        final Controller owner;

        Press(Controller controller, int i) {
            this.owner = controller;
            this.direction = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class Controller {
        private static final long STEP_DURATION_MS = 300;
        private static final TimeInterpolator STEP_EASE = new PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f);
        private static final float STEP_RATIO = 0.82f;
        private static ValueAnimator runningScroll;
        private final int maxInsetPx;
        private final int maxSizePx;
        private final int minStepPx;
        private int syncedScrollX;
        private int syncedScrollY;
        private final int touchPadPx;
        private final View view;
        private final CueDrawable[] cues = new CueDrawable[4];
        private final Rect box = new Rect();
        private final Rect hitBox = new Rect();
        private final Rect visible = new Rect();
        private final int[] onScreen = new int[2];
        private long syncedAtMs = -1;

        Controller(View view) {
            this.view = view;
            Context context = view.getContext();
            this.maxSizePx = Math.round(ScrollEdgeCue.dp(context, 30.0f));
            this.maxInsetPx = Math.round(ScrollEdgeCue.dp(context, 6.0f));
            this.touchPadPx = Math.round(ScrollEdgeCue.dp(context, 5.0f));
            this.minStepPx = Math.round(ScrollEdgeCue.dp(context, 48.0f));
            int i = 0;
            while (true) {
                CueDrawable[] cueDrawableArr = this.cues;
                if (i >= cueDrawableArr.length) {
                    return;
                }
                cueDrawableArr[i] = new CueDrawable(this, context, i);
                i++;
            }
        }

        void install() {
            for (CueDrawable cueDrawable : this.cues) {
                this.view.getOverlay().add(cueDrawable);
            }
            this.view.invalidate();
        }

        private void cueBox(int i, int i2, int i3, Rect rect) {
            int iMin = Math.min(this.maxSizePx, Math.round(Math.min(i2, i3) * 0.72f));
            int iMin2 = Math.min(this.maxInsetPx, Math.max(0, (Math.min(i2, i3) - iMin) / 2));
            int i4 = (i2 - iMin) / 2;
            int i5 = (i3 - iMin) / 2;
            if (i == 0) {
                rect.set(i4, iMin2, i4 + iMin, iMin + iMin2);
                return;
            }
            if (i == 1) {
                int i6 = i3 - iMin2;
                rect.set(i4, i6 - iMin, iMin + i4, i6);
            } else if (i == 2) {
                rect.set(iMin2, i5, iMin2 + iMin, iMin + i5);
            } else {
                int i7 = i2 - iMin2;
                rect.set(i7 - iMin, i5, i7, iMin + i5);
            }
        }

        void syncBeforeDraw(long j) {
            if (j == this.syncedAtMs) {
                return;
            }
            this.syncedAtMs = j;
            int width = this.view.getWidth();
            int height = this.view.getHeight();
            if (width <= 0 || height <= 0) {
                return;
            }
            int scrollX = this.view.getScrollX();
            int scrollY = this.view.getScrollY();
            boolean z = (scrollX == this.syncedScrollX && scrollY == this.syncedScrollY) ? false : true;
            this.syncedScrollX = scrollX;
            this.syncedScrollY = scrollY;
            for (int i = 0; i < this.cues.length; i++) {
                cueBox(i, width, height, this.box);
                this.box.offset(scrollX, scrollY);
                this.cues[i].place(this.box);
                if (z) {
                    this.cues[i].wake(j);
                }
            }
            boolean zAnimationsEnabled = ScrollEdgeCue.animationsEnabled(this.view.getContext());
            this.cues[0].setActive(this.view.canScrollVertically(-1), zAnimationsEnabled);
            this.cues[1].setActive(this.view.canScrollVertically(1), zAnimationsEnabled);
            this.cues[2].setActive(this.view.canScrollHorizontally(-1), zAnimationsEnabled);
            this.cues[3].setActive(this.view.canScrollHorizontally(1), zAnimationsEnabled);
        }

        int cueAt(float f, float f2) {
            for (int i = 0; i < this.cues.length; i++) {
                if (cueContains(i, f, f2)) {
                    return i;
                }
            }
            return -1;
        }

        boolean cueContains(int i, float f, float f2) {
            if (!this.cues[i].isTappable()) {
                return false;
            }
            int width = this.view.getWidth();
            int height = this.view.getHeight();
            if (width <= 0 || height <= 0 || !this.view.isShown()) {
                return false;
            }
            this.view.getLocationOnScreen(this.onScreen);
            int iRound = Math.round(f) - this.onScreen[0];
            int iRound2 = Math.round(f2) - this.onScreen[1];
            cueBox(i, width, height, this.hitBox);
            Rect rect = this.hitBox;
            int i2 = this.touchPadPx;
            rect.inset(-i2, -i2);
            if (this.hitBox.contains(iRound, iRound2) && this.view.getLocalVisibleRect(this.visible)) {
                return this.visible.contains(iRound + this.view.getScrollX(), iRound2 + this.view.getScrollY());
            }
            return false;
        }

        void setPressed(int i, boolean z) {
            this.cues[i].setPressed(z);
        }

        void stepScroll(int i) {
            int width = this.view.getWidth();
            int height = this.view.getHeight();
            if (width <= 0 || height <= 0) {
                return;
            }
            final boolean z = i == 0 || i == 1;
            int i2 = (i == 0 || i == 2) ? -1 : 1;
            int i3 = this.minStepPx;
            if (z) {
                width = height;
            }
            final int iMax = Math.max(i3, Math.round(width * STEP_RATIO)) * i2;
            cancelRunningScroll();
            View view = this.view;
            if (view instanceof AbsListView) {
                ((AbsListView) view).smoothScrollBy(iMax, TraceCatalog.Feature.BOARD_HUD);
                return;
            }
            if (!ScrollEdgeCue.animationsEnabled(view.getContext())) {
                this.view.scrollBy(z ? 0 : iMax, z ? iMax : 0);
                return;
            }
            final int[] iArr = {0};
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimatorOfFloat.setDuration(STEP_DURATION_MS);
            valueAnimatorOfFloat.setInterpolator(STEP_EASE);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.support.ScrollEdgeCue$Controller$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.m1422lambda$stepScroll$0$comandroidsupportScrollEdgeCue$Controller(iMax, iArr, z, valueAnimator);
                }
            });
            runningScroll = valueAnimatorOfFloat;
            valueAnimatorOfFloat.start();
        }

        /* renamed from: lambda$stepScroll$0$com-android-support-ScrollEdgeCue$Controller, reason: not valid java name */
        /* synthetic */ void m1422lambda$stepScroll$0$comandroidsupportScrollEdgeCue$Controller(int i, int[] iArr, boolean z, ValueAnimator valueAnimator) {
            int iRound = Math.round(i * ((Float) valueAnimator.getAnimatedValue()).floatValue());
            int i2 = iRound - iArr[0];
            iArr[0] = iRound;
            if (i2 != 0) {
                this.view.scrollBy(z ? 0 : i2, z ? i2 : 0);
            }
        }

        static void cancelRunningScroll() {
            ValueAnimator valueAnimator = runningScroll;
            runningScroll = null;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean animationsEnabled(Context context) {
        return OverlayUiMotion.animationsEnabled(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float dp(Context context, float f) {
        return f * context.getResources().getDisplayMetrics().density;
    }

    private static final class CueDrawable extends Drawable {
        private static final int ACCENT = -10556417;
        private static final float BASE_RADIUS_DP = 15.0f;
        private static final long BOUNCE_PERIOD_MS = 1100;
        static final long BOUNCE_WINDOW_MS = 2600;
        private static final float CHEVRON_DROP_RATIO = 0.21333334f;
        private static final float CHEVRON_HALF_RATIO = 0.43333334f;
        static final int DIR_DOWN = 1;
        static final int DIR_LEFT = 2;
        static final int DIR_RIGHT = 3;
        static final int DIR_UP = 0;
        private static final TimeInterpolator FADE = new PathInterpolator(0.22f, 0.61f, 0.36f, 1.0f);
        private static final float PRESS_RATE_MS = 90.0f;
        private static final float PRESS_SCALE = 0.14f;
        private static final long STALE_GAP_MS = 600;
        private boolean active;
        private final float baseRadiusPx;
        private final float bouncePx;
        private final float boundsPadPx;
        private final Paint chevronPaint;
        private final int direction;
        private final Paint fillPaint;
        private final float glowPx;
        private final Paint haloPaint;
        private long lastFrameMs;
        private final Controller owner;
        private float pressFraction;
        private boolean pressed;
        private float shownFraction;
        private final float strokeMaxPx;
        private final float strokeMinPx;
        private final Paint strokePaint;
        private long wokeAtMs;
        private final Path chevron = new Path();
        private final RectF pill = new RectF();
        private final Rect slot = new Rect();
        private boolean animateEnabled = true;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        CueDrawable(Controller controller, Context context, int i) {
            this.owner = controller;
            this.direction = i;
            this.baseRadiusPx = dp(context, 15.0f);
            this.bouncePx = dp(context, 3.0f);
            this.strokeMaxPx = dp(context, 2.2f);
            this.strokeMinPx = dp(context, 1.4f);
            this.glowPx = dp(context, 3.2f);
            this.boundsPadPx = dp(context, 6.0f);
            Paint paint = new Paint(1);
            this.fillPaint = paint;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(-435481568);
            Paint paint2 = new Paint(1);
            this.strokePaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setStrokeWidth(dp(context, 1.0f));
            paint2.setColor(-10556417);
            Paint paint3 = new Paint(1);
            this.haloPaint = paint3;
            paint3.setStyle(Paint.Style.STROKE);
            paint3.setStrokeCap(Paint.Cap.ROUND);
            paint3.setStrokeJoin(Paint.Join.ROUND);
            paint3.setColor(-10556417);
            Paint paint4 = new Paint(1);
            this.chevronPaint = paint4;
            paint4.setStyle(Paint.Style.STROKE);
            paint4.setStrokeCap(Paint.Cap.ROUND);
            paint4.setStrokeJoin(Paint.Join.ROUND);
            paint4.setColor(-10556417);
        }

        void place(Rect rect) {
            this.slot.set(rect);
            int iRound = Math.round(this.boundsPadPx);
            setBounds(rect.left - iRound, rect.top - iRound, rect.right + iRound, rect.bottom + iRound);
        }

        void wake(long j) {
            this.wokeAtMs = j;
        }

        boolean isTappable() {
            return this.active && this.shownFraction > 0.55f;
        }

        void setPressed(boolean z) {
            if (this.pressed == z) {
                return;
            }
            this.pressed = z;
            wake(SystemClock.uptimeMillis());
            invalidateSelf();
        }

        void setActive(boolean z, boolean z2) {
            this.animateEnabled = z2;
            if (this.active == z) {
                return;
            }
            this.active = z;
            if (!z2) {
                this.shownFraction = z ? 1.0f : 0.0f;
            }
            wake(SystemClock.uptimeMillis());
            invalidateSelf();
        }

        /* JADX WARN: Removed duplicated region for block: B:69:0x012c  */
        @Override // android.graphics.drawable.Drawable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void draw(android.graphics.Canvas r19) {
            /*
                Method dump skipped, instructions count: 445
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.support.ScrollEdgeCue.CueDrawable.draw(android.graphics.Canvas):void");
        }

        private float bounceGain(long j) {
            long j2 = j - this.wokeAtMs;
            if (j2 <= 0) {
                return 1.0f;
            }
            if (j2 >= BOUNCE_WINDOW_MS) {
                return 0.0f;
            }
            float f = 1.0f - (j2 / 2600.0f);
            return f * f;
        }

        private static int blend(int i, int i2, float f) {
            int i3 = (i >>> 24) & 255;
            int i4 = (i >> 16) & 255;
            int i5 = (i >> 8) & 255;
            return Math.round((i & 255) + (((i2 & 255) - r5) * f)) | (Math.round(i3 + ((((i2 >>> 24) & 255) - i3) * f)) << 24) | (Math.round(i4 + ((((i2 >> 16) & 255) - i4) * f)) << 16) | (Math.round(i5 + ((((i2 >> 8) & 255) - i5) * f)) << 8);
        }

        private void applyRadius(float f, float f2) {
            float fMin = Math.min(1.0f, f / this.baseRadiusPx);
            float fMax = Math.max(this.strokeMinPx, this.strokeMaxPx * fMin);
            this.chevronPaint.setStrokeWidth(fMax);
            this.haloPaint.setStrokeWidth(fMax + (this.glowPx * fMin * (f2 + 1.0f)));
        }

        private void buildChevron(float f, float f2, float f3) {
            this.chevron.reset();
            float f4 = CHEVRON_HALF_RATIO * f3;
            float f5 = f3 * CHEVRON_DROP_RATIO;
            int i = this.direction;
            if (i == 0) {
                float f6 = (0.6f * f5) + f2;
                this.chevron.moveTo(f - f4, f6);
                this.chevron.lineTo(f, f2 - (f5 * 0.9f));
                this.chevron.lineTo(f + f4, f6);
                return;
            }
            if (i == 1) {
                float f7 = f2 - (0.6f * f5);
                this.chevron.moveTo(f - f4, f7);
                this.chevron.lineTo(f, f2 + (f5 * 0.9f));
                this.chevron.lineTo(f + f4, f7);
                return;
            }
            Path path = this.chevron;
            if (i == 2) {
                float f8 = (0.6f * f5) + f;
                path.moveTo(f8, f2 - f4);
                this.chevron.lineTo(f - (f5 * 0.9f), f2);
                this.chevron.lineTo(f8, f2 + f4);
                return;
            }
            float f9 = f - (0.6f * f5);
            path.moveTo(f9, f2 - f4);
            this.chevron.lineTo(f + (f5 * 0.9f), f2);
            this.chevron.lineTo(f9, f2 + f4);
        }

        private static float dp(Context context, float f) {
            return f * context.getResources().getDisplayMetrics().density;
        }
    }
}
