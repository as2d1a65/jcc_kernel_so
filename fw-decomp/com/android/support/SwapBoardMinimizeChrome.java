package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
public final class SwapBoardMinimizeChrome {
    static final float EXPAND_ALPHA_FROM = 0.6f;
    static final int EXPAND_ANIM_MS = 200;
    static final int EXPAND_FILL = 1426128818;
    static final float EXPAND_SCALE_FROM = 0.94f;
    static final int EXPAND_STROKE = -1727987790;
    static final int FULL_FILL = 1140906495;
    static final int FULL_STROKE = -2013210113;
    static final int INITIAL_MIN_BTN_COLOR = -1;
    static final String INITIAL_MIN_BTN_TEXT = "—";
    static final float INITIAL_MIN_BTN_TEXT_SP = 12.0f;
    static final int MINIMIZE_ANIM_MS = 180;
    static final float MINIMIZE_SCALE_FROM = 1.06f;
    static final float MIN_BTN_TEXT_SP_FULL = 14.0f;
    static final float MIN_BTN_TEXT_SP_MINIMIZED = 13.0f;

    interface Surface {
        View body();

        View card();

        GradientDrawable cardBg();

        int cardMinWidthFull();

        View closeBtn();

        int[] cornerHolder();

        View dot();

        View dragHint();

        View header();

        GradientDrawable headerBg();

        TextView minBtn();

        TextView title();
    }

    public static float cardCornerPx(boolean z, int i, int i2) {
        return z ? i2 : i;
    }

    public static int cardMinWidthPx(boolean z, int i) {
        if (z) {
            return 0;
        }
        return i;
    }

    public static int dotMarginEndPx(boolean z, int i, int i2) {
        return z ? i : i2;
    }

    public static int dotSizePx(boolean z, int i, int i2) {
        return z ? i : i2;
    }

    public static int headerPadBottom(boolean z, int i, int i2) {
        return z ? i : i2;
    }

    public static int headerPadEnd(boolean z, int i, int i2) {
        return z ? i : i2;
    }

    public static int headerPadTop(boolean z, int i, int i2) {
        return z ? i : i2;
    }

    public static int minBtnFill(boolean z) {
        return z ? EXPAND_FILL : FULL_FILL;
    }

    public static int minBtnMarginEndPx(boolean z, int i) {
        if (z) {
            return 0;
        }
        return i;
    }

    public static int minBtnSizePx(boolean z, int i, int i2) {
        return z ? i : i2;
    }

    public static int minBtnStroke(boolean z) {
        return z ? EXPAND_STROKE : FULL_STROKE;
    }

    public static float minBtnTextSizeSp(boolean z) {
        if (z) {
            return 13.0f;
        }
        return MIN_BTN_TEXT_SP_FULL;
    }

    private SwapBoardMinimizeChrome() {
    }

    public static float[] pillRadii(float f) {
        return new float[]{f, f, f, f, f, f, f, f};
    }

    public static float[] fullHeaderRadii(float f) {
        return new float[]{f, f, f, f, 0.0f, 0.0f, 0.0f, 0.0f};
    }

    static TextView buildMinButton(Context context, int i, int i2, int i3) {
        TextView textView = new TextView(context);
        textView.setText(INITIAL_MIN_BTN_TEXT);
        textView.setTextColor(-1);
        textView.setTextSize(INITIAL_MIN_BTN_TEXT_SP);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(FULL_FILL);
        gradientDrawable.setStroke(i, FULL_STROKE);
        textView.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i3, i3);
        layoutParams.setMarginEnd(i2);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    static void applyChrome(Surface surface, boolean z, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        int i10 = z ? 8 : 0;
        surface.body().setVisibility(i10);
        if (surface.dragHint() != null) {
            surface.dragHint().setVisibility(i10);
        }
        if (surface.closeBtn() != null) {
            surface.closeBtn().setVisibility(i10);
        }
        surface.header().setPadding(i5, headerPadTop(z, i2, i4), headerPadEnd(z, i2, i3), headerPadBottom(z, i2, i4));
        if (surface.title() != null) {
            surface.title().setTextSize(SwapBoardMinimizePolicy.titleSizeSp(z));
        }
        if (surface.dot() != null) {
            ViewGroup.LayoutParams layoutParams = surface.dot().getLayoutParams();
            if (layoutParams instanceof LinearLayout.LayoutParams) {
                int iDotSizePx = dotSizePx(z, i2, i3);
                layoutParams.width = iDotSizePx;
                layoutParams.height = iDotSizePx;
                ((LinearLayout.LayoutParams) layoutParams).setMarginEnd(dotMarginEndPx(z, i3, i4));
                surface.dot().setLayoutParams(layoutParams);
            }
        }
        float fCardCornerPx = cardCornerPx(z, i6, i7);
        if (z) {
            surface.headerBg().setCornerRadii(pillRadii(fCardCornerPx));
        } else {
            surface.headerBg().setCornerRadii(fullHeaderRadii(i6));
        }
        if (surface.cardBg() != null) {
            surface.cardBg().setCornerRadius(fCardCornerPx);
        }
        surface.cornerHolder()[0] = (int) fCardCornerPx;
        if (surface.card() != null) {
            surface.card().setMinimumWidth(cardMinWidthPx(z, surface.cardMinWidthFull()));
        }
        if (surface.minBtn() != null) {
            surface.minBtn().setText(SwapBoardMinimizePolicy.minButtonText(z));
            surface.minBtn().setTextSize(minBtnTextSizeSp(z));
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(1);
            gradientDrawable.setColor(minBtnFill(z));
            gradientDrawable.setStroke(i, minBtnStroke(z));
            surface.minBtn().setBackground(gradientDrawable);
            ViewGroup.LayoutParams layoutParams2 = surface.minBtn().getLayoutParams();
            if (layoutParams2 instanceof LinearLayout.LayoutParams) {
                int iMinBtnSizePx = minBtnSizePx(z, i8, i9);
                layoutParams2.width = iMinBtnSizePx;
                layoutParams2.height = iMinBtnSizePx;
                ((LinearLayout.LayoutParams) layoutParams2).setMarginEnd(minBtnMarginEndPx(z, i2));
                surface.minBtn().setLayoutParams(layoutParams2);
            }
        }
    }

    static void playToggleAnim(View view, boolean z, boolean z2) {
        if (view == null || !z2) {
            return;
        }
        if (z) {
            view.setScaleX(MINIMIZE_SCALE_FROM);
            view.setScaleY(MINIMIZE_SCALE_FROM);
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(180L).setInterpolator(new DecelerateInterpolator()).start();
        } else {
            view.setAlpha(EXPAND_ALPHA_FROM);
            view.setScaleX(EXPAND_SCALE_FROM);
            view.setScaleY(EXPAND_SCALE_FROM);
            view.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200L).setInterpolator(new DecelerateInterpolator()).start();
        }
    }
}
