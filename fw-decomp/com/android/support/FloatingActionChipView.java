package com.android.support;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
final class FloatingActionChipView extends View {
    private boolean active;
    private final RectF body;
    private final Runnable clearFlash;
    private final float density;
    private final Paint fillPaint;
    private String flashText;
    private int flashTone;
    private final RectF gloss;
    private final Paint glossPaint;
    private final RectF glow;
    private final Paint glowPaint;
    private final int kind;
    private String label;
    private boolean pending;
    private float pillHeight;
    private float pillWidth;
    private float pulse;
    private ValueAnimator pulseAnimator;
    private int scalePercent;
    private final Paint strokePaint;
    private final Paint textPaint;

    FloatingActionChipView(Context context, int i) {
        super(context);
        this.scalePercent = 100;
        this.fillPaint = new Paint(1);
        Paint paint = new Paint(1);
        this.strokePaint = paint;
        Paint paint2 = new Paint(1);
        this.glowPaint = paint2;
        Paint paint3 = new Paint(1);
        this.glossPaint = paint3;
        Paint paint4 = new Paint(1);
        this.textPaint = paint4;
        this.body = new RectF();
        this.glow = new RectF();
        this.gloss = new RectF();
        this.label = "";
        this.clearFlash = new Runnable() { // from class: com.android.support.FloatingActionChipView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.clearFlash();
            }
        };
        this.kind = i;
        this.density = context.getResources().getDisplayMetrics().density;
        paint.setStyle(Paint.Style.STROKE);
        paint2.setStyle(Paint.Style.STROKE);
        paint3.setStyle(Paint.Style.FILL);
        paint4.setTextAlign(Paint.Align.LEFT);
        paint4.setFakeBoldText(true);
        applyScaleToPaints();
        paint4.setShadowLayer(dp(2.0f), 0.0f, dp(1.0f), -1291845632);
        setClickable(true);
        setFocusable(true);
        OverlayUiMotion.installPressScale(this);
    }

    int kind() {
        return this.kind;
    }

    void setScalePercent(int i) {
        int iNormalizeScalePercent = FloatingActionChipPolicy.normalizeScalePercent(i);
        if (iNormalizeScalePercent == this.scalePercent) {
            return;
        }
        this.scalePercent = iNormalizeScalePercent;
        applyScaleToPaints();
        requestLayout();
        invalidate();
    }

    private void applyScaleToPaints() {
        this.strokePaint.setStrokeWidth(scaledDp(2.0f));
        this.glowPaint.setStrokeWidth(scaledDp(2.0f) * 2.0f);
        this.textPaint.setTextSize(FloatingActionChipPolicy.scaled(16.0f, this.scalePercent) * getResources().getDisplayMetrics().scaledDensity);
        this.textPaint.setShadowLayer(scaledDp(2.0f), 0.0f, scaledDp(1.0f), -1291845632);
    }

    private float scaledDp(float f) {
        return FloatingActionChipPolicy.scaled(f, this.scalePercent) * this.density;
    }

    void setState(boolean z, boolean z2) {
        boolean z3 = this.active == z && this.pending == z2;
        this.active = z;
        this.pending = z2;
        setContentDescription(FloatingActionChipPolicy.contentDescription(this.kind, z, z2));
        setAlpha(z2 ? 0.7f : getAlpha());
        if (this.flashText != null) {
            return;
        }
        String strLabel = FloatingActionChipPolicy.label(this.kind, z, z2);
        if (z3 && strLabel.equals(this.label)) {
            return;
        }
        this.label = strLabel;
        rebuildFillShader();
        syncPulse();
        requestLayout();
        invalidate();
    }

    void flash(String str, int i) {
        if (str == null || str.isEmpty()) {
            return;
        }
        removeCallbacks(this.clearFlash);
        this.flashText = str;
        this.flashTone = i;
        this.label = str;
        rebuildFillShader();
        syncPulse();
        requestLayout();
        invalidate();
        postDelayed(this.clearFlash, 1100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearFlash() {
        if (this.flashText == null) {
            return;
        }
        this.flashText = null;
        this.label = FloatingActionChipPolicy.label(this.kind, this.active, this.pending);
        rebuildFillShader();
        syncPulse();
        requestLayout();
        invalidate();
    }

    boolean isPending() {
        return this.pending;
    }

    private void syncPulse() {
        if (this.active && !this.pending && this.flashText == null && OverlayUiMotion.animationsEnabled(getContext())) {
            ValueAnimator valueAnimator = this.pulseAnimator;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.pulseAnimator = valueAnimatorOfFloat;
                valueAnimatorOfFloat.setDuration(1600L);
                this.pulseAnimator.setRepeatCount(-1);
                this.pulseAnimator.setRepeatMode(2);
                this.pulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.support.FloatingActionChipView$$ExternalSyntheticLambda1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        this.f$0.m151lambda$syncPulse$0$comandroidsupportFloatingActionChipView(valueAnimator2);
                    }
                });
                this.pulseAnimator.start();
                return;
            }
            return;
        }
        ValueAnimator valueAnimator2 = this.pulseAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
            this.pulseAnimator = null;
        }
        this.pulse = 0.0f;
    }

    /* renamed from: lambda$syncPulse$0$com-android-support-FloatingActionChipView, reason: not valid java name */
    /* synthetic */ void m151lambda$syncPulse$0$comandroidsupportFloatingActionChipView(ValueAnimator valueAnimator) {
        this.pulse = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        ValueAnimator valueAnimator = this.pulseAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.pulseAnimator = null;
        }
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        this.pillHeight = dp(44.0f) + (dp(6.0f) * 2.0f);
        float fMax = Math.max(dp(116.0f) + (dp(6.0f) * 2.0f), this.textPaint.measureText(this.label) + (dp(18.0f) * 2.0f) + (dp(6.0f) * 2.0f));
        this.pillWidth = fMax;
        float f = this.density * 28.0f;
        setMeasuredDimension(Math.round(Math.max(fMax, f)), Math.round(Math.max(this.pillHeight, f)));
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        float fDp = dp(6.0f);
        float fDp2 = dp(2.0f);
        float f = this.pillWidth;
        float fMin = f > 0.0f ? Math.min(f, i) : i;
        float f2 = this.pillHeight;
        float fMin2 = f2 > 0.0f ? Math.min(f2, i2) : i2;
        float f3 = (i - fMin) / 2.0f;
        float f4 = (i2 - fMin2) / 2.0f;
        float f5 = fDp2 / 2.0f;
        this.body.set(f3 + fDp + f5, f4 + fDp + f5, ((f3 + fMin) - fDp) - f5, ((f4 + fMin2) - fDp) - f5);
        this.fillPaint.setShader(new LinearGradient(0.0f, this.body.top, 0.0f, this.body.bottom, FloatingActionChipPolicy.gradientTop(this.kind, this.active), FloatingActionChipPolicy.gradientBottom(this.kind, this.active), Shader.TileMode.CLAMP));
        this.gloss.set(this.body.left + fDp2, this.body.top + fDp2, this.body.right - fDp2, this.body.top + (this.body.height() * 0.52f));
        this.glossPaint.setShader(new LinearGradient(0.0f, this.gloss.top, 0.0f, this.gloss.bottom, 1090519039, ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.CLAMP));
        rebuildFillShader();
    }

    private void rebuildFillShader() {
        int iGradientTop;
        int iGradientBottom;
        int iStrokeColor;
        if (this.body.isEmpty()) {
            return;
        }
        boolean z = this.flashText != null;
        Paint paint = this.fillPaint;
        float f = this.body.top;
        float f2 = this.body.bottom;
        if (z) {
            iGradientTop = FloatingActionChipPolicy.toneTop(this.flashTone);
        } else {
            iGradientTop = FloatingActionChipPolicy.gradientTop(this.kind, this.active);
        }
        int i = iGradientTop;
        if (z) {
            iGradientBottom = FloatingActionChipPolicy.toneBottom(this.flashTone);
        } else {
            iGradientBottom = FloatingActionChipPolicy.gradientBottom(this.kind, this.active);
        }
        paint.setShader(new LinearGradient(0.0f, f, 0.0f, f2, i, iGradientBottom, Shader.TileMode.CLAMP));
        Paint paint2 = this.strokePaint;
        if (z) {
            iStrokeColor = FloatingActionChipPolicy.toneStroke(this.flashTone);
        } else {
            iStrokeColor = FloatingActionChipPolicy.strokeColor(this.kind, this.active);
        }
        paint2.setColor(iStrokeColor);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.body.isEmpty()) {
            return;
        }
        float fHeight = this.body.height() / 2.0f;
        boolean z = this.flashText != null;
        if (this.pulse > 0.0f) {
            float fDp = dp(6.0f) * this.pulse;
            this.glow.set(this.body.left - fDp, this.body.top - fDp, this.body.right + fDp, this.body.bottom + fDp);
            int iRound = Math.round((1.0f - (this.pulse * 0.55f)) * 150.0f);
            this.glowPaint.setColor(FloatingActionChipPolicy.glowColor(this.kind, this.active));
            this.glowPaint.setAlpha(iRound);
            float f = fDp + fHeight;
            canvas.drawRoundRect(this.glow, f, f, this.glowPaint);
        }
        canvas.drawRoundRect(this.body, fHeight, fHeight, this.fillPaint);
        canvas.drawRoundRect(this.gloss, fHeight, fHeight, this.glossPaint);
        canvas.drawRoundRect(this.body, fHeight, fHeight, this.strokePaint);
        this.textPaint.setColor(z ? -1 : FloatingActionChipPolicy.textColor(this.pending));
        Paint.FontMetrics fontMetrics = this.textPaint.getFontMetrics();
        canvas.drawText(this.label, this.body.centerX() - (this.textPaint.measureText(this.label) / 2.0f), this.body.centerY() - ((fontMetrics.ascent + fontMetrics.descent) / 2.0f), this.textPaint);
    }

    private float dp(float f) {
        return scaledDp(f);
    }
}
