package com.android.support;

import android.content.Context;

/* loaded from: classes.dex */
final class XfOverlayScale {
    static final float COMPACT_BADGE_SIZE_DP = 20.0f;
    static final float COMPACT_CARD_END_OFFSET_DP = 10.0f;
    static final float COMPACT_CARD_RADIUS_DP = 16.0f;
    static final float COMPACT_CARD_TOP_OFFSET_DP = 10.0f;
    static final float COMPACT_CARD_WIDTH_DP = 184.0f;
    static final float COMPACT_NOTICE_HEIGHT_DP = 48.0f;
    static final float COMPACT_NOTICE_WITH_TITLE_HEIGHT_DP = 56.0f;

    private XfOverlayScale() {
    }

    static float of(Context context) {
        if (context == null) {
            return 1.0f;
        }
        if (ScreenUtils.isTablet(context)) {
            return 1.18f;
        }
        if (ScreenUtils.isLandscape(context)) {
            return 1.02f;
        }
        int iRound = Math.round(ScreenUtils.getRealScreenWidth(context) / context.getResources().getDisplayMetrics().density);
        if (iRound <= 360) {
            return 0.84f;
        }
        if (iRound <= 430) {
            return 0.92f;
        }
        return iRound <= 520 ? 1.0f : 1.08f;
    }

    static int dp(Context context, float f) {
        return ScreenUtils.dp2px(context, f * of(context));
    }

    static float sp(Context context, float f) {
        return f * of(context);
    }

    static int cardWidth(Context context) {
        int iDp = dp(context, COMPACT_CARD_WIDTH_DP);
        int realScreenWidth = ScreenUtils.getRealScreenWidth(context);
        return realScreenWidth <= 0 ? iDp : Math.max(1, Math.min(iDp, realScreenWidth - (dp(context, 10.0f) * 2)));
    }

    static int cardMinWidth(Context context) {
        return cardWidth(context);
    }

    static int radius(Context context) {
        return dp(context, 16.0f);
    }

    static int topOffset(Context context) {
        return dp(context, 10.0f);
    }

    static int endOffset(Context context) {
        return dp(context, 10.0f);
    }

    static int badgeSize(Context context) {
        return dp(context, 20.0f);
    }

    static int noticeHeight(Context context, boolean z) {
        return dp(context, z ? COMPACT_NOTICE_WITH_TITLE_HEIGHT_DP : COMPACT_NOTICE_HEIGHT_DP);
    }
}
