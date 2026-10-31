package com.android.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import com.android.support.BoardHeroAvatarCache;
import com.android.support.diagnostics.TraceCatalog;

/* loaded from: classes.dex */
final class BoardHeroOverlayView extends View {
    private static final int COLOR_AVATAR_STROKE = -1902593;
    private static final int COLOR_CAPSULE = -1038870246;
    private static final int COLOR_ENEMY_CAPSULE = -767946977;
    private static final int COLOR_LEFT = -10233345;
    private static final int COLOR_OWN = -10093614;
    private Bitmap avatarBitmap;
    private final BoardHeroAvatarCache avatarCache;
    private final Paint avatarFallbackPaint;
    private final Matrix avatarMatrix;
    private boolean avatarOnly;
    private final Paint avatarPaint;
    private final RectF avatarRect;
    private BitmapShader avatarShader;
    private final float avatarSize;
    private final Paint capsulePaint;
    private boolean enemy;
    private int heroId;
    private int left;
    private String leftText;
    private final float markerHeight;
    private int own;
    private String ownText;
    private boolean planning;
    private int season;
    private final Paint strokePaint;
    private final Paint textPaint;
    private final float textSize;

    BoardHeroOverlayView(Context context, BoardHeroAvatarCache boardHeroAvatarCache) {
        super(context);
        this.avatarSize = dp(22.0f);
        this.markerHeight = dp(26.0f);
        this.textSize = sp(9.5f);
        this.capsulePaint = new Paint(1);
        Paint paint = new Paint(1);
        this.textPaint = paint;
        this.avatarPaint = new Paint(3);
        this.avatarFallbackPaint = new Paint(1);
        Paint paint2 = new Paint(1);
        this.strokePaint = paint2;
        this.avatarMatrix = new Matrix();
        this.avatarRect = new RectF();
        this.own = -1;
        this.left = -1;
        this.planning = true;
        this.ownText = "我 —";
        this.leftText = "余 —";
        this.avatarCache = boardHeroAvatarCache;
        setWillNotDraw(false);
        setBackgroundColor(0);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        GameHudPalette.applyTextShadow(paint, context.getResources().getDisplayMetrics().density);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(dp(1.0f));
    }

    void submitMarker(int i, int i2, int i3, int i4, boolean z, boolean z2, boolean z3) {
        boolean z4 = (this.heroId == i && this.season == i4) ? false : true;
        if (!z4 && this.own == i2 && this.left == i3 && this.planning == z && this.avatarOnly == z2 && this.enemy == z3) {
            return;
        }
        this.heroId = i;
        this.own = i2;
        this.left = i3;
        this.season = i4;
        this.planning = z;
        this.avatarOnly = z2;
        this.enemy = z3;
        this.ownText = i2 < 0 ? "我 —" : "我 " + this.own;
        this.leftText = this.left < 0 ? "余 —" : "余 " + this.left;
        if (z4) {
            requestAvatar(i, i4);
        }
        postInvalidateOnAnimation();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Canvas canvas2;
        super.onDraw(canvas);
        if (this.heroId <= 0 || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        int iMin = this.planning ? 255 : 188;
        if (this.avatarOnly) {
            iMin = Math.min(TraceCatalog.Feature.SPECIFIED_HEX, iMin);
        }
        if (this.avatarOnly) {
            canvas2 = canvas;
        } else {
            this.capsulePaint.setStyle(Paint.Style.FILL);
            this.capsulePaint.setColor(this.enemy ? COLOR_ENEMY_CAPSULE : COLOR_CAPSULE);
            this.capsulePaint.setAlpha((Color.alpha(COLOR_CAPSULE) * iMin) / 255);
            float f = this.avatarSize * 0.55f;
            float width = getWidth();
            float f2 = this.markerHeight;
            canvas2 = canvas;
            canvas2.drawRoundRect(f, 0.0f, width, f2, f2 / 2.0f, f2 / 2.0f, this.capsulePaint);
        }
        float width2 = this.avatarOnly ? (getWidth() - this.avatarSize) / 2.0f : 0.0f;
        float f3 = this.markerHeight;
        float f4 = this.avatarSize;
        float f5 = (f3 - f4) / 2.0f;
        this.avatarRect.set(width2, f5, width2 + f4, f4 + f5);
        drawCircularAvatar(canvas2, this.avatarBitmap, this.avatarRect, iMin);
        this.strokePaint.setColor(this.enemy ? GameHudPalette.ALERT_RED : COLOR_AVATAR_STROKE);
        this.strokePaint.setAlpha(iMin);
        canvas2.drawOval(this.avatarRect, this.strokePaint);
        if (this.avatarOnly) {
            return;
        }
        drawCounts(canvas2, iMin);
    }

    private void drawCounts(Canvas canvas, int i) {
        this.textPaint.setTextSize(this.textSize);
        boolean z = this.enemy;
        float f = this.markerHeight;
        if (z) {
            float fAscent = (f / 2.0f) - ((this.textPaint.ascent() + this.textPaint.descent()) / 2.0f);
            this.textPaint.setColor(GameHudPalette.ALERT_RED);
            this.textPaint.setAlpha(i);
            canvas.drawText("敌", this.avatarSize + dp(4.0f), fAscent, this.textPaint);
            return;
        }
        float fAscent2 = (f / 2.0f) - ((this.textPaint.ascent() + this.textPaint.descent()) / 2.0f);
        float fDp = this.avatarSize + dp(4.0f);
        this.textPaint.setColor(COLOR_OWN);
        this.textPaint.setAlpha(i);
        canvas.drawText(this.ownText, fDp, fAscent2, this.textPaint);
        float fMeasureText = fDp + this.textPaint.measureText(this.ownText) + dp(5.0f);
        this.textPaint.setColor(COLOR_LEFT);
        this.textPaint.setAlpha(i);
        canvas.drawText(this.leftText, fMeasureText, fAscent2, this.textPaint);
    }

    private void drawCircularAvatar(Canvas canvas, Bitmap bitmap, RectF rectF, int i) {
        if (bitmap == null || bitmap.isRecycled() || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            this.avatarFallbackPaint.setStyle(Paint.Style.FILL);
            this.avatarFallbackPaint.setColor(-14404547);
            this.avatarFallbackPaint.setAlpha(i);
            canvas.drawOval(rectF, this.avatarFallbackPaint);
            this.avatarFallbackPaint.setColor(-4666669);
            this.avatarFallbackPaint.setAlpha(i);
            canvas.drawCircle(rectF.centerX(), rectF.top + (rectF.height() * 0.36f), rectF.width() * 0.17f, this.avatarFallbackPaint);
            canvas.drawOval(rectF.left + (rectF.width() * 0.25f), rectF.top + (rectF.height() * 0.54f), rectF.right - (rectF.width() * 0.25f), rectF.bottom - (rectF.height() * 0.12f), this.avatarFallbackPaint);
            return;
        }
        if (this.avatarShader == null) {
            this.avatarShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        float fMax = Math.max(rectF.width() / bitmap.getWidth(), rectF.height() / bitmap.getHeight());
        this.avatarMatrix.reset();
        this.avatarMatrix.setScale(fMax, fMax);
        this.avatarMatrix.postTranslate(rectF.centerX() - ((bitmap.getWidth() * fMax) / 2.0f), rectF.centerY() - ((bitmap.getHeight() * fMax) / 2.0f));
        this.avatarShader.setLocalMatrix(this.avatarMatrix);
        this.avatarPaint.setShader(this.avatarShader);
        this.avatarPaint.setAlpha(i);
        canvas.drawOval(rectF, this.avatarPaint);
        this.avatarPaint.setShader(null);
    }

    private void requestAvatar(final int i, final int i2) {
        this.avatarBitmap = this.avatarCache.get(i, i2, new BoardHeroAvatarCache.Callback() { // from class: com.android.support.BoardHeroOverlayView$$ExternalSyntheticLambda0
            @Override // com.android.support.BoardHeroAvatarCache.Callback
            public final void onReady() {
                this.f$0.m98lambda$requestAvatar$0$comandroidsupportBoardHeroOverlayView(i, i2);
            }
        });
        this.avatarShader = null;
    }

    /* renamed from: lambda$requestAvatar$0$com-android-support-BoardHeroOverlayView, reason: not valid java name */
    /* synthetic */ void m98lambda$requestAvatar$0$comandroidsupportBoardHeroOverlayView(int i, int i2) {
        Bitmap bitmap;
        if (this.heroId != i || this.season != i2 || (bitmap = this.avatarCache.get(i, i2, null)) == null || bitmap == this.avatarBitmap) {
            return;
        }
        this.avatarBitmap = bitmap;
        this.avatarShader = null;
        postInvalidateOnAnimation();
    }

    void close() {
        animate().cancel();
    }

    private float dp(float f) {
        return TypedValue.applyDimension(1, f, getResources().getDisplayMetrics());
    }

    private float sp(float f) {
        return TypedValue.applyDimension(2, f, getResources().getDisplayMetrics());
    }
}
