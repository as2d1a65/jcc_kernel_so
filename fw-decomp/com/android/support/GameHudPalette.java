package com.android.support;

import android.graphics.Paint;
import android.widget.TextView;

/* loaded from: classes.dex */
public final class GameHudPalette {
    public static final int ALERT_RED = -45737;
    public static final int BORDER = -1721832449;
    public static final int CYAN = -10556417;
    public static final int DANGER = -36205;
    public static final int GOLD = -15770;
    public static final int PEER_ORANGE = -23988;
    public static final int PLATE = -871689952;
    public static final int PLATE_STRONG = -435746028;
    public static final int PURPLE = -3561729;
    public static final int SHADOW_COLOR = -436207616;
    public static final float SHADOW_DY_DP = 1.0f;
    public static final float SHADOW_RADIUS_DP = 2.0f;
    public static final int SUCCESS = -9899865;
    public static final int TEXT = -1378052;
    public static final int TEXT_DIM = -7034446;

    public static int costColor(int i) {
        switch (i) {
            case 1:
                return -4801335;
            case 2:
                return -9650054;
            case 3:
                return -11556640;
            case 4:
                return -4889104;
            case 5:
                return -15770;
            case 6:
                return -33112;
            default:
                return -7432282;
        }
    }

    public static void applyTextShadow(TextView textView) {
        if (textView == null) {
            return;
        }
        float f = textView.getResources().getDisplayMetrics().density;
        textView.setShadowLayer(2.0f * f, 0.0f, f * 1.0f, SHADOW_COLOR);
    }

    public static void applyTextShadow(Paint paint, float f) {
        if (paint == null) {
            return;
        }
        paint.setShadowLayer(2.0f * f, 0.0f, f * 1.0f, SHADOW_COLOR);
    }

    private GameHudPalette() {
    }
}
