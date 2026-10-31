package com.android.support;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

/* loaded from: classes.dex */
final class OverlayActionButtonStyle {
    private static final int[] GO_GRADIENT = {-9439489, -6583041, -29989};
    private static final int GO_STROKE = -1376769;
    private static final int OFF_FILL_GO = -11903104;
    private static final int OFF_FILL_STOP = -12957085;
    private static final int OFF_STROKE_GO = -7690552;
    private static final int OFF_STROKE_STOP = -9666148;
    private static final int STOP_FILL = -7718316;
    private static final int STOP_STROKE = -20282;
    private static final int TEXT_OFF = -1379332;
    private static final int TEXT_ON = -1;

    enum Kind {
        GO,
        STOP
    }

    private OverlayActionButtonStyle() {
    }

    static int fingerprint(Kind kind, boolean z, int i, int i2) {
        return ((((((kind == Kind.GO ? 1 : 2) * 31) + (z ? 1 : 0)) * 31) + i) * 31) + i2;
    }

    static void apply(View view, Kind kind, boolean z, int i, int i2) {
        if (view == null) {
            return;
        }
        int iFingerprint = fingerprint(kind, z, i, i2);
        Object tag = view.getTag(R.id.overlay_action_button_style_key);
        if ((tag instanceof Integer) && ((Integer) tag).intValue() == iFingerprint && view.getBackground() != null) {
            return;
        }
        view.setTag(R.id.overlay_action_button_style_key, Integer.valueOf(iFingerprint));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(i);
        if (!z) {
            boolean z2 = kind == Kind.GO;
            gradientDrawable.setColor(z2 ? OFF_FILL_GO : OFF_FILL_STOP);
            gradientDrawable.setStroke(i2, z2 ? OFF_STROKE_GO : OFF_STROKE_STOP);
        } else if (kind == Kind.GO) {
            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            gradientDrawable.setColors(GO_GRADIENT);
            gradientDrawable.setStroke(i2, GO_STROKE);
        } else {
            gradientDrawable.setColor(STOP_FILL);
            gradientDrawable.setStroke(i2, STOP_STROKE);
        }
        view.setBackground(gradientDrawable);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(z ? -1 : TEXT_OFF);
        }
    }
}
