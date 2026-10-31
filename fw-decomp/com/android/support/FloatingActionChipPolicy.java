package com.android.support;

import androidx.core.os.EnvironmentCompat;
import java.util.function.IntPredicate;

/* loaded from: classes.dex */
final class FloatingActionChipPolicy {
    static final float ACTIVE_ALPHA = 1.0f;
    static final int[] ALL_KINDS = {0, 1, 2};
    static final int DEFAULT_MARGIN_DP = 16;
    static final long FLASH_MS = 1100;
    static final int GLOW_DP = 6;
    static final int HEIGHT_DP = 44;
    static final int HORIZONTAL_PADDING_DP = 18;
    static final float IDLE_ALPHA = 0.72f;
    static final long IDLE_DIM_DELAY_MS = 2600;
    static final long IDLE_DIM_MS = 420;
    static final int KIND_ALL_IN = 2;
    static final int KIND_REFRESH = 0;
    static final int KIND_SELL = 1;
    static final int MIN_WIDTH_DP = 116;
    static final int POSITION_UNSET = Integer.MIN_VALUE;
    private static final String PREFIX = "fab_chip_";
    static final long PULSE_MS = 1600;
    static final int SCALE_DEFAULT_PERCENT = 100;
    static final int SCALE_MAX_PERCENT = 150;
    static final int SCALE_MIN_PERCENT = 10;
    static final String SCALE_PERCENT_KEY = "fab_chip_scale_percent";
    static final int SCALE_STEP_PERCENT = 10;
    static final long SETTLE_MS = 260;
    static final int STACK_GAP_DP = 12;
    static final int STROKE_DP = 2;
    static final int TEXT_SIZE_SP = 16;
    static final int TONE_FAIL = 1;
    static final int TONE_INFO = 2;
    static final int TONE_SUCCESS = 0;
    static final int TOUCH_MIN_DP = 28;

    static int dockButtonFor(int i) {
        if (i == 0) {
            return 16;
        }
        if (i != 1) {
            return i != 2 ? 0 : 32;
        }
        return 4;
    }

    static int gradientBottom(int i, boolean z) {
        if (z) {
            return -2540969;
        }
        if (i == 0) {
            return -14702741;
        }
        if (i != 1) {
            return i != 2 ? -11905429 : -2059989;
        }
        return -2540969;
    }

    static int gradientTop(int i, boolean z) {
        if (z) {
            return -38012;
        }
        if (i == 0) {
            return -11082849;
        }
        if (i != 1) {
            return i != 2 ? -8746336 : -13960;
        }
        return -33645;
    }

    static boolean isKind(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    static int strokeColor(int i, boolean z) {
        if (z) {
            return -15666;
        }
        if (i == 0) {
            return -5701676;
        }
        if (i != 1) {
            return i != 2 ? -4801335 : -7248;
        }
        return -15666;
    }

    static int textColor(boolean z) {
        return z ? -1643278 : -1;
    }

    static int toneBottom(int i) {
        if (i != 0) {
            return i != 1 ? -12947495 : -2537414;
        }
        return -14702741;
    }

    static int toneStroke(int i) {
        if (i != 0) {
            return i != 1 ? -4006913 : -13896;
        }
        return -5701676;
    }

    static int toneTop(int i) {
        if (i != 0) {
            return i != 1 ? -8406785 : -30101;
        }
        return -11082849;
    }

    private FloatingActionChipPolicy() {
    }

    static String enabledKey(int i) {
        return PREFIX + suffix(i) + "_enabled";
    }

    static String posXKey(int i) {
        return PREFIX + suffix(i) + "_x";
    }

    static String posYKey(int i) {
        return PREFIX + suffix(i) + "_y";
    }

    private static String suffix(int i) {
        if (i == 0) {
            return "refresh";
        }
        if (i == 1) {
            return "sell";
        }
        if (i == 2) {
            return "allin";
        }
        return EnvironmentCompat.MEDIA_UNKNOWN;
    }

    static String label(int i, boolean z, boolean z2) {
        if (z2) {
            return "请稍候";
        }
        if (i == 0) {
            return z ? "停止刷新" : "开始刷新";
        }
        if (i == 1) {
            return "一键卖牌";
        }
        if (i != 2) {
            return "";
        }
        return z ? "停止梭哈" : "一键梭哈";
    }

    static String contentDescription(int i, boolean z, boolean z2) {
        return label(i, z, z2) + "，长按可拖动位置";
    }

    static String toggleTitle(int i) {
        if (i == 0) {
            return "刷新悬浮键";
        }
        if (i == 1) {
            return "卖牌悬浮键";
        }
        if (i == 2) {
            return "梭哈悬浮键";
        }
        return "悬浮键";
    }

    static String toggleHint(int i, boolean z) {
        if (!z) {
            return "关闭中 · 打开后在对局画面上显示独立按钮";
        }
        if (i == 0) {
            return "已开启 · 进对局后显示，点按开关自动刷新";
        }
        if (i == 1) {
            return "已开启 · 进对局后显示，点按按当前设置卖牌";
        }
        if (i == 2) {
            return "已开启 · 进对局后显示，点按开关一键梭哈";
        }
        return "已开启 · 进对局后显示，可单独拖动";
    }

    static int glowColor(int i, boolean z) {
        if (z) {
            return -38012;
        }
        return strokeColor(i, false);
    }

    static int normalizeScalePercent(int i) {
        if (i < 10) {
            return 10;
        }
        return i > SCALE_MAX_PERCENT ? SCALE_MAX_PERCENT : (Math.round((i - 10) / 10.0f) * 10) + 10;
    }

    static float scaled(float f, int i) {
        return (f * normalizeScalePercent(i)) / 100.0f;
    }

    static String scaleSummary(int i) {
        return "按钮大小 " + normalizeScalePercent(i) + "%";
    }

    static boolean dockButtonSupersededByChip(int i, IntPredicate intPredicate) {
        for (int i2 : ALL_KINDS) {
            if (dockButtonFor(i2) == i) {
                return intPredicate.test(i2);
            }
        }
        return false;
    }

    static int minVisiblePx(int i, int i2) {
        return Math.max(1, Math.min(i, i2) / 2);
    }

    static int clampX(int i, int i2, int i3) {
        return OverlayDragMath.clampAxis(i, i2, i3, minVisiblePx(i2, i2));
    }

    static int clampY(int i, int i2, int i3) {
        return OverlayDragMath.clampAxis(i, i2, i3, minVisiblePx(i2, i2));
    }

    static int settleX(int i, int i2, int i3) {
        return clampX(i, i2, i3);
    }

    static int settleY(int i, int i2, int i3) {
        return clampY(i, i2, i3);
    }

    static int defaultX(int i, int i2, int i3) {
        return Math.max(0, (i2 - i) - i3);
    }

    static int defaultY(int i, int i2, int i3, int i4, int i5) {
        int iStackIndex = stackIndex(i);
        int length = ALL_KINDS.length;
        return Math.max(i5, (i4 - ((length * i2) + ((length - 1) * i3))) / 2) + (iStackIndex * (i2 + i3));
    }

    static int stackIndex(int i) {
        int i2 = 0;
        while (true) {
            int[] iArr = ALL_KINDS;
            if (i2 >= iArr.length) {
                return 0;
            }
            if (iArr[i2] == i) {
                return i2;
            }
            i2++;
        }
    }
}
