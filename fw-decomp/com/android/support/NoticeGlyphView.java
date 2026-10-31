package com.android.support;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.PathInterpolator;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
final class NoticeGlyphView extends View {
    private static final PathInterpolator EASE_OUT = new PathInterpolator(0.16f, 1.0f, 0.3f, 1.0f);
    private int accentDeep;
    private int accentLight;
    private int accentSolid;
    private float dotCx;
    private float dotCy;
    private final Paint dotPaint;
    private float dotR;
    private final Path drawPath;
    private final Paint fillPaint;
    private RadialGradient fillShader;
    private ValueAnimator glyphAnim;
    private final Paint glyphPaint;
    private final Path glyphPath;
    private float glyphProgress;
    private boolean hasDot;
    private int kind;
    private final PathMeasure pathMeasure;
    private ValueAnimator ringAnim;
    private final Paint ringPaint;
    private float ringProgress;

    NoticeGlyphView(Context context) {
        super(context);
        this.fillPaint = new Paint(1);
        Paint paint = new Paint(1);
        this.ringPaint = paint;
        Paint paint2 = new Paint(1);
        this.glyphPaint = paint2;
        Paint paint3 = new Paint(1);
        this.dotPaint = paint3;
        this.glyphPath = new Path();
        this.drawPath = new Path();
        this.pathMeasure = new PathMeasure();
        this.kind = 0;
        this.accentLight = -7936010;
        this.accentDeep = -12605710;
        this.accentSolid = -11282450;
        this.glyphProgress = 1.0f;
        this.ringProgress = 1.0f;
        this.hasDot = false;
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStrokeJoin(Paint.Join.ROUND);
        paint2.setColor(-1);
        paint3.setStyle(Paint.Style.FILL);
        paint3.setColor(-1);
        paint.setStyle(Paint.Style.STROKE);
        setKind(this.kind);
    }

    void setKind(int i) {
        this.kind = i;
        int[] iArrKindColors = NoticeCardStyle.kindColors(i);
        this.accentLight = iArrKindColors[0];
        this.accentDeep = iArrKindColors[1];
        this.accentSolid = iArrKindColors[2];
        this.glyphProgress = 1.0f;
        this.ringProgress = 1.0f;
        rebuildPath();
        invalidate();
    }

    int accent() {
        return this.accentSolid;
    }

    void playIntro() {
        if (!OverlayUiMotion.animationsEnabled(getContext())) {
            this.glyphProgress = 1.0f;
            this.ringProgress = 1.0f;
            invalidate();
            return;
        }
        cancelAnims();
        this.glyphProgress = 0.0f;
        this.ringProgress = 0.0f;
        invalidate();
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.glyphAnim = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setStartDelay(120L);
        this.glyphAnim.setDuration(460L);
        ValueAnimator valueAnimator = this.glyphAnim;
        PathInterpolator pathInterpolator = EASE_OUT;
        valueAnimator.setInterpolator(pathInterpolator);
        this.glyphAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.support.NoticeGlyphView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.m1375lambda$playIntro$0$comandroidsupportNoticeGlyphView(valueAnimator2);
            }
        });
        this.glyphAnim.start();
        ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.ringAnim = valueAnimatorOfFloat2;
        valueAnimatorOfFloat2.setStartDelay(80L);
        this.ringAnim.setDuration(720L);
        this.ringAnim.setInterpolator(pathInterpolator);
        this.ringAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.support.NoticeGlyphView$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.m1376lambda$playIntro$1$comandroidsupportNoticeGlyphView(valueAnimator2);
            }
        });
        this.ringAnim.start();
    }

    /* renamed from: lambda$playIntro$0$com-android-support-NoticeGlyphView, reason: not valid java name */
    /* synthetic */ void m1375lambda$playIntro$0$comandroidsupportNoticeGlyphView(ValueAnimator valueAnimator) {
        this.glyphProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$playIntro$1$com-android-support-NoticeGlyphView, reason: not valid java name */
    /* synthetic */ void m1376lambda$playIntro$1$comandroidsupportNoticeGlyphView(ValueAnimator valueAnimator) {
        this.ringProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private void cancelAnims() {
        ValueAnimator valueAnimator = this.glyphAnim;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.glyphAnim = null;
        }
        ValueAnimator valueAnimator2 = this.ringAnim;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
            this.ringAnim = null;
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        cancelAnims();
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        rebuildPath();
    }

    private void rebuildPath() {
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) {
            return;
        }
        float f = width / 2.0f;
        float f2 = height / 2.0f;
        float fMin = Math.min(width, height) / 2.0f;
        float f3 = 0.82f * fMin;
        this.glyphPaint.setStrokeWidth(Math.max(2.0f, 0.22f * f3));
        this.ringPaint.setStrokeWidth(Math.max(1.5f, fMin * 0.07f));
        float f4 = f2 - (0.3f * f3);
        RadialGradient radialGradient = new RadialGradient(f, f4, f3 * 1.18f, new int[]{this.accentLight, this.accentDeep}, (float[]) null, Shader.TileMode.CLAMP);
        this.fillShader = radialGradient;
        this.fillPaint.setShader(radialGradient);
        this.glyphPath.reset();
        this.hasDot = false;
        int i = this.kind;
        if (i == 1) {
            this.glyphPath.moveTo(f - (0.34f * f3), (0.02f * f3) + f2);
            this.glyphPath.lineTo(f - (0.08f * f3), (0.28f * f3) + f2);
            this.glyphPath.lineTo(f + (0.36f * f3), f2 - (f3 * 0.24f));
        } else {
            Path path = this.glyphPath;
            if (i == 2) {
                float f5 = 0.34f * f3;
                path.moveTo(f, f2 - f5);
                this.glyphPath.lineTo(f, (0.12f * f3) + f2);
                this.hasDot = true;
                this.dotCx = f;
                this.dotCy = f2 + f5;
                this.dotR = f3 * 0.11f;
            } else {
                path.moveTo(f, f2 - (0.04f * f3));
                this.glyphPath.lineTo(f, f2 + (0.34f * f3));
                this.hasDot = true;
                this.dotCx = f;
                this.dotCy = f4;
                this.dotR = f3 * 0.11f;
            }
        }
        this.pathMeasure.setPath(this.glyphPath, false);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) {
            return;
        }
        float f = width / 2.0f;
        float f2 = height / 2.0f;
        float fMin = Math.min(width, height) / 2.0f;
        float f3 = 0.82f * fMin;
        float f4 = this.ringProgress;
        if (f4 > 0.0f && f4 < 1.0f) {
            this.ringPaint.setColor((((int) ((1.0f - f4) * 190.0f)) << 24) | (this.accentSolid & ViewCompat.MEASURED_SIZE_MASK));
            canvas.drawCircle(f, f2, Math.min(((fMin - f3) * f4) + f3, fMin - (this.ringPaint.getStrokeWidth() / 2.0f)), this.ringPaint);
        }
        if (this.fillShader == null) {
            rebuildPath();
        }
        canvas.drawCircle(f, f2, f3, this.fillPaint);
        float length = this.pathMeasure.getLength();
        if (length > 0.0f) {
            this.drawPath.reset();
            this.pathMeasure.getSegment(0.0f, length * this.glyphProgress, this.drawPath, true);
            canvas.drawPath(this.drawPath, this.glyphPaint);
        }
        if (this.hasDot) {
            this.dotPaint.setColor(Color.argb((int) (Math.min(1.0f, this.glyphProgress * 1.5f) * 255.0f), 255, 255, 255));
            canvas.drawCircle(this.dotCx, this.dotCy, this.dotR, this.dotPaint);
        }
    }
}
