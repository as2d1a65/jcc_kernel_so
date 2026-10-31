package com.android.support;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
public final class HudColorPickerView extends View {
    private static final int DRAG_HUE = 2;
    private static final int DRAG_NONE = 0;
    private static final int DRAG_SV = 1;
    private final Path clipPath;
    private float density;
    private int dragMode;
    private final RectF expandedHit;
    private final float[] hsv;
    private final RectF hueRect;
    private OnColorChangedListener listener;
    private final Paint paint;
    private final Paint stroke;
    private final RectF svRect;

    public interface OnColorChangedListener {
        void onColorChanged(int i);
    }

    public HudColorPickerView(Context context) {
        super(context);
        this.paint = new Paint(1);
        this.stroke = new Paint(1);
        this.svRect = new RectF();
        this.hueRect = new RectF();
        this.expandedHit = new RectF();
        this.clipPath = new Path();
        this.hsv = new float[]{180.0f, 0.5f, 1.0f};
        this.dragMode = 0;
        init();
    }

    public HudColorPickerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.paint = new Paint(1);
        this.stroke = new Paint(1);
        this.svRect = new RectF();
        this.hueRect = new RectF();
        this.expandedHit = new RectF();
        this.clipPath = new Path();
        this.hsv = new float[]{180.0f, 0.5f, 1.0f};
        this.dragMode = 0;
        init();
    }

    private void init() {
        this.density = Math.max(1.0f, getResources().getDisplayMetrics().density);
        this.stroke.setStyle(Paint.Style.STROKE);
        this.stroke.setStrokeWidth(dp(2.0f));
        setFocusable(true);
        setClickable(true);
        setWillNotDraw(false);
        updateAccessibilityLabel();
    }

    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        this.listener = onColorChangedListener;
    }

    public void setColor(int i) {
        Color.colorToHSV((i & ViewCompat.MEASURED_SIZE_MASK) | ViewCompat.MEASURED_STATE_MASK, this.hsv);
        if (!Float.isFinite(this.hsv[0])) {
            this.hsv[0] = 0.0f;
        }
        invalidate();
        updateAccessibilityLabel();
    }

    public int getColor() {
        return Color.HSVToColor(this.hsv);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(resolveSize(Math.round(dp(280.0f)), i), resolveSize(Math.round(dp(132.0f)), i2));
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        float fDp = dp(3.0f);
        float fDp2 = dp(22.0f);
        float fDp3 = dp(10.0f);
        float f = i - fDp;
        float f2 = i2 - fDp;
        this.svRect.set(fDp, fDp, Math.max(dp(72.0f) + fDp, (f - fDp2) - fDp3), f2);
        this.hueRect.set(this.svRect.right + fDp3, fDp, f, f2);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSaturationValue(canvas);
        drawHue(canvas);
        drawPointers(canvas);
    }

    private void drawSaturationValue(Canvas canvas) {
        this.paint.setShader(new ComposeShader(new LinearGradient(this.svRect.left, 0.0f, this.svRect.right, 0.0f, -1, Color.HSVToColor(new float[]{this.hsv[0], 1.0f, 1.0f}), Shader.TileMode.CLAMP), new LinearGradient(0.0f, this.svRect.top, 0.0f, this.svRect.bottom, -1, ViewCompat.MEASURED_STATE_MASK, Shader.TileMode.CLAMP), PorterDuff.Mode.MULTIPLY));
        this.clipPath.reset();
        this.clipPath.addRoundRect(this.svRect, dp(6.0f), dp(6.0f), Path.Direction.CW);
        int iSave = canvas.save();
        canvas.clipPath(this.clipPath);
        canvas.drawRect(this.svRect, this.paint);
        canvas.restoreToCount(iSave);
        this.paint.setShader(null);
        this.stroke.setColor(-2141262849);
        this.stroke.setStrokeWidth(dp(1.0f));
        canvas.drawRoundRect(this.svRect, dp(6.0f), dp(6.0f), this.stroke);
    }

    private void drawHue(Canvas canvas) {
        this.paint.setShader(new LinearGradient(0.0f, this.hueRect.top, 0.0f, this.hueRect.bottom, new int[]{SupportMenu.CATEGORY_MASK, InputDeviceCompat.SOURCE_ANY, -16711936, -16711681, -16776961, -65281, SupportMenu.CATEGORY_MASK}, (float[]) null, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(this.hueRect, dp(6.0f), dp(6.0f), this.paint);
        this.paint.setShader(null);
        this.stroke.setColor(-2137017146);
        this.stroke.setStrokeWidth(dp(1.0f));
        canvas.drawRoundRect(this.hueRect, dp(6.0f), dp(6.0f), this.stroke);
    }

    private void drawPointers(Canvas canvas) {
        float fWidth = this.svRect.left + (this.hsv[1] * this.svRect.width());
        float fHeight = this.svRect.top + ((1.0f - this.hsv[2]) * this.svRect.height());
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(getColor());
        canvas.drawCircle(fWidth, fHeight, dp(7.0f), this.paint);
        this.stroke.setStrokeWidth(dp(2.0f));
        this.stroke.setColor(-1);
        canvas.drawCircle(fWidth, fHeight, dp(8.0f), this.stroke);
        this.stroke.setStrokeWidth(dp(1.0f));
        this.stroke.setColor(-872415232);
        canvas.drawCircle(fWidth, fHeight, dp(9.5f), this.stroke);
        float fHeight2 = this.hueRect.top + ((this.hsv[0] / 360.0f) * this.hueRect.height());
        RectF rectF = new RectF(this.hueRect.left - dp(3.0f), fHeight2 - dp(4.0f), this.hueRect.right + dp(3.0f), fHeight2 + dp(4.0f));
        this.paint.setColor(Color.HSVToColor(new float[]{this.hsv[0], 1.0f, 1.0f}));
        canvas.drawRoundRect(rectF, dp(3.0f), dp(3.0f), this.paint);
        this.stroke.setStrokeWidth(dp(2.0f));
        this.stroke.setColor(-1);
        canvas.drawRoundRect(rectF, dp(3.0f), dp(3.0f), this.stroke);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.expandedHit.set(this.hueRect);
            this.expandedHit.inset(-dp(14.0f), 0.0f);
            if (this.expandedHit.contains(motionEvent.getX(), motionEvent.getY())) {
                this.dragMode = 2;
            } else {
                this.expandedHit.set(this.svRect);
                this.expandedHit.inset(-dp(4.0f), -dp(4.0f));
                if (!this.expandedHit.contains(motionEvent.getX(), motionEvent.getY())) {
                    return false;
                }
                this.dragMode = 1;
            }
            getParent().requestDisallowInterceptTouchEvent(true);
            updateFromTouch(motionEvent.getX(), motionEvent.getY());
            return true;
        }
        if (actionMasked == 1) {
            if (this.dragMode == 0) {
                return false;
            }
            updateFromTouch(motionEvent.getX(), motionEvent.getY());
            this.dragMode = 0;
            getParent().requestDisallowInterceptTouchEvent(false);
            performClick();
            return true;
        }
        if (actionMasked == 2) {
            if (this.dragMode == 0) {
                return false;
            }
            updateFromTouch(motionEvent.getX(), motionEvent.getY());
            return true;
        }
        if (actionMasked == 3) {
            this.dragMode = 0;
            getParent().requestDisallowInterceptTouchEvent(false);
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    private void updateFromTouch(float f, float f2) {
        int i = this.dragMode;
        if (i == 2) {
            this.hsv[0] = clamp01((f2 - this.hueRect.top) / Math.max(1.0f, this.hueRect.height())) * 360.0f;
            float[] fArr = this.hsv;
            if (fArr[0] >= 360.0f) {
                fArr[0] = 359.999f;
            }
        } else if (i == 1) {
            this.hsv[1] = clamp01((f - this.svRect.left) / Math.max(1.0f, this.svRect.width()));
            this.hsv[2] = 1.0f - clamp01((f2 - this.svRect.top) / Math.max(1.0f, this.svRect.height()));
        }
        invalidate();
        updateAccessibilityLabel();
        OnColorChangedListener onColorChangedListener = this.listener;
        if (onColorChangedListener != null) {
            onColorChangedListener.onColorChanged(getColor());
        }
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        float f = (keyEvent == null || !keyEvent.isShiftPressed()) ? 2.0f : 10.0f;
        if (i == 21) {
            float[] fArr = this.hsv;
            fArr[0] = ((fArr[0] - f) + 360.0f) % 360.0f;
        } else if (i == 22) {
            float[] fArr2 = this.hsv;
            fArr2[0] = (fArr2[0] + f) % 360.0f;
        } else if (i == 19) {
            float[] fArr3 = this.hsv;
            fArr3[2] = clamp01(fArr3[2] + (f / 100.0f));
        } else if (i == 20) {
            float[] fArr4 = this.hsv;
            fArr4[2] = clamp01(fArr4[2] - (f / 100.0f));
        } else {
            return super.onKeyDown(i, keyEvent);
        }
        invalidate();
        updateAccessibilityLabel();
        OnColorChangedListener onColorChangedListener = this.listener;
        if (onColorChangedListener == null) {
            return true;
        }
        onColorChangedListener.onColorChanged(getColor());
        return true;
    }

    @Override // android.view.View
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void updateAccessibilityLabel() {
        setContentDescription("连续颜色选择器，当前 #" + HudAppearance.hex24(getColor()) + "；也可使用下方十六进制输入框精确设置");
    }

    private float dp(float f) {
        return f * this.density;
    }

    private static float clamp01(float f) {
        return Math.max(0.0f, Math.min(1.0f, f));
    }
}
