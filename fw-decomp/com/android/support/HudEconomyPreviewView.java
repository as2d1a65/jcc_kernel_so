package com.android.support;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public final class HudEconomyPreviewView extends View {
    private HudAppearance appearance;
    private final Paint paint;

    public HudEconomyPreviewView(Context context) {
        super(context);
        this.paint = new Paint(1);
        this.appearance = HudAppearance.defaults();
        init();
    }

    public HudEconomyPreviewView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.paint = new Paint(1);
        this.appearance = HudAppearance.defaults();
        init();
    }

    private void init() {
        setWillNotDraw(false);
        this.paint.setTypeface(Typeface.DEFAULT_BOLD);
        this.paint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setAppearance(HudAppearance hudAppearance) {
        if (hudAppearance == null) {
            hudAppearance = HudAppearance.defaults();
        }
        this.appearance = hudAppearance;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = getResources().getDisplayMetrics().density;
        float width = getWidth() - (6.0f * f);
        this.paint.setTextSize(Math.min(getHeight() * 0.7f, 20.0f * f));
        this.paint.setShadowLayer(2.5f * f, 0.0f, 1.0f, GameHudPalette.SHADOW_COLOR);
        Paint.FontMetrics fontMetrics = this.paint.getFontMetrics();
        float height = (getHeight() - (fontMetrics.ascent + fontMetrics.descent)) / 2.0f;
        this.paint.setColor(this.appearance.getGoldColor());
        canvas.drawText("40金", width, height, this.paint);
        float fMeasureText = this.paint.measureText("40金");
        this.paint.setColor(this.appearance.getLevelColor());
        canvas.drawText("4级", (width - fMeasureText) - (f * 8.0f), height, this.paint);
        this.paint.clearShadowLayer();
    }
}
