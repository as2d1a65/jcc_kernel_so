package com.android.support;

import com.android.support.diagnostics.TraceCatalog;

/* loaded from: classes.dex */
final class RankWarningPositionMath {
    private static final float ATTACK_RETICLE_VISUAL_RADIUS_SCALE = 0.65f;
    static final float ECONOMY_LEADER_SCALE = 1.22f;
    static final int MAX_OFFSET_DP = 120;
    static final int MAX_SIZE_PERCENT = 120;
    static final int MIN_OFFSET_DP = -120;
    static final int MIN_SIZE_PERCENT = 20;
    static final int PLAYER_SLOT_COUNT = 8;
    static final String PROFILE_EMULATOR_DESKTOP = "emulator_desktop";
    static final String PROFILE_PHONE_LANDSCAPE = "phone_landscape";
    static final String PROFILE_PHONE_PORTRAIT = "phone_portrait";
    static final String PROFILE_TABLET = "tablet";
    static final float WARNING_CARD_WIDTH_RATIO = 0.84f;
    static final float WARNING_VISUAL_EDGE_INSET_PX = 2.0f;

    static float attackReticleScale(int i, boolean z) {
        if (z) {
            return WARNING_CARD_WIDTH_RATIO;
        }
        if (i <= 0) {
            return 0.78f;
        }
        if (i < 600) {
            return 0.72f;
        }
        if (i < 840) {
            return 0.78f;
        }
        return WARNING_CARD_WIDTH_RATIO;
    }

    static float economyEmphasisScale(boolean z) {
        if (z) {
            return ECONOMY_LEADER_SCALE;
        }
        return 1.0f;
    }

    static float infoColumnRight(float f, float f2) {
        return (f - 4.0f) + f2;
    }

    static boolean isEconomyEmphasisCandidate(boolean z, boolean z2, int i) {
        return (z || z2 || i <= 0) ? false : true;
    }

    static float opponentArrowLeft(float f, float f2) {
        return f + f2;
    }

    static boolean shouldEmphasizeEconomy(boolean z, boolean z2, int i, int i2, int i3, int i4) {
        if (!z && !z2) {
            if (i3 >= 0 && i == i3) {
                return true;
            }
            if (i4 >= 0 && i2 == i4) {
                return true;
            }
        }
        return false;
    }

    static float warningLaneRight(float f, float f2, float f3) {
        return (f - f2) - f3;
    }

    private RankWarningPositionMath() {
    }

    static float topForRank(int i, int i2, int i3, int i4) {
        return ((i < 1 || i > 8) ? Math.max(0, Math.min(7, i2)) : i - 1) * (i3 + i4);
    }

    static float reticleCenterY(Float f, float f2, int i) {
        if (f != null) {
            f2 = f.floatValue();
        }
        return f2 + (i / 2.0f);
    }

    static float reticleCenterX(Float f, Float f2, float f3, int i, float f4, int i2) {
        return (f == null || f2 == null) ? f3 + i + (f4 * 0.55f) + i2 : (f.floatValue() - (f2.floatValue() / 2.0f)) + i2;
    }

    static float warningBadgeTextSize(float f) {
        return Math.max(14.0f, f * 0.34f);
    }

    static float warningGoldTextSize(float f) {
        return Math.max(15.0f, f * 0.68f);
    }

    static float warningGoldTextSize(float f, int i) {
        return (warningGoldTextSize(f) * clampSizePercent(i)) / 100.0f;
    }

    static float warningLevelTextSize(float f) {
        return Math.max(12.0f, f * 0.5f);
    }

    static float warningLevelTextSize(float f, int i) {
        return (warningLevelTextSize(f) * clampSizePercent(i)) / 100.0f;
    }

    static int adaptiveWarningHeroSize(float f, float f2) {
        if (Float.isNaN(f) || Float.isInfinite(f) || f <= 0.0f) {
            return 1;
        }
        return Math.max(1, (int) (Math.max((int) (0.8f * f), f / 2.0f) * Math.max(0.5f, Math.min(2.0f, f2))));
    }

    static int warningCardWidth(int i) {
        return Math.max(1, Math.round(Math.max(1, i) * WARNING_CARD_WIDTH_RATIO));
    }

    static int warningCardHeight(int i, int i2) {
        return Math.min(Math.max(1, i), Math.max(1, Math.round(Math.max(1, i2) / WARNING_CARD_WIDTH_RATIO)));
    }

    static float warningInfoGap(float f) {
        return Math.max(8.0f, f * 0.16f);
    }

    static float minimumNameGap(float f) {
        return Math.max(4.0f, finitePositive(f, 1.0f) * 0.05f);
    }

    static float minimumLaneGap(float f) {
        return Math.max(4.0f, finitePositive(f, 1.0f) * 0.06f);
    }

    static float warningGoldBaseline(Float f, Float f2, float f3, float f4) {
        return (f == null || f2 == null || f2.floatValue() <= 0.0f) ? f3 + (f4 * 0.6f) : f.floatValue() + (f2.floatValue() * 0.4f);
    }

    static float warningLevelBaseline(Float f, Float f2, float f3, float f4) {
        return (f == null || f2 == null || f2.floatValue() <= 0.0f) ? f3 + (f4 * 0.98f) : f.floatValue() + (f2.floatValue() * 0.78f);
    }

    static float warningHeroLeft(float f, int i, int i2, int i3, int i4) {
        int iMax = Math.max(0, Math.min(Math.max(1, i4) - 1, i3));
        return (f - Math.max(1, i)) - (iMax * (r2 + Math.max(0, i2)));
    }

    static float warningHeroTop(float f, float f2, int i) {
        return f + ((f2 - Math.max(1, i)) / 2.0f);
    }

    static float infoColumnRight(Float f, float f2, float f3) {
        if (f != null) {
            f2 = f.floatValue();
        }
        return infoColumnRight(f2, f3);
    }

    static float adaptiveInfoColumnRight(Float f, float f2, float f3, float f4, float f5, float f6) {
        float fFiniteOr = (f == null || !isFinite(f.floatValue())) ? finiteOr(f2, f4 + 1.0f) : f.floatValue();
        float fFiniteOr2 = finiteOr(f4, 0.0f);
        float fMax = Math.max(1.0f, finiteOr(f5, 1.0f));
        float fMinimumNameGap = fFiniteOr - minimumNameGap(f3);
        float fMinimumNameGap2 = fFiniteOr - minimumNameGap(f3);
        float f7 = fFiniteOr2 + fMax;
        return f7 > fMinimumNameGap2 ? fMinimumNameGap2 : clamp(fMinimumNameGap + finiteOr(f6, 0.0f), f7, fMinimumNameGap2);
    }

    static float adaptiveWarningLaneRight(float f, float f2, float f3, float f4, float f5) {
        float fFiniteOr = finiteOr(f, 1.0f) - Math.max(0.0f, finiteOr(f2, 0.0f));
        float fWarningInfoGap = fFiniteOr - warningInfoGap(f3);
        float fMinimumLaneGap = fFiniteOr - minimumLaneGap(f3);
        float fFiniteOr2 = finiteOr(f4, 0.0f) + 1.0f;
        return fFiniteOr2 > fMinimumLaneGap ? fMinimumLaneGap : clamp(fWarningInfoGap + finiteOr(f5, 0.0f), fFiniteOr2, fMinimumLaneGap);
    }

    static float flushWarningLaneRight(float f, float f2, float f3) {
        return (finiteOr(f, 1.0f) - Math.max(0.0f, finiteOr(f2, 0.0f))) - minimumLaneGap(f3);
    }

    static float automaticWarningLaneRight(float f, float f2, float f3, float f4, float f5, float f6, boolean z) {
        float f7;
        float f8;
        float fAdaptiveWarningLaneRight;
        float f9;
        if (!isFinite(f)) {
            return Float.NaN;
        }
        if (z) {
            float fMax = Math.max(0.0f, finiteOr(f5, 0.0f));
            f7 = f2;
            f8 = f3;
            float fClampLaneRightToVisibleBounds = clampLaneRightToVisibleBounds(adaptiveInfoColumnRight(Float.valueOf(f), f, f7, f8, fMax, f6), fMax, f8, f4);
            f9 = fClampLaneRightToVisibleBounds - fMax;
            fAdaptiveWarningLaneRight = flushWarningLaneRight(fClampLaneRightToVisibleBounds, fMax, f7);
        } else {
            f7 = f2;
            f8 = f3;
            fAdaptiveWarningLaneRight = adaptiveWarningLaneRight(f, 0.0f, f7, f8, 0.0f);
            f9 = f;
        }
        return clampWarningLaneRightBelowInfoColumn(fAdaptiveWarningLaneRight, f9, f7, f8, f4);
    }

    static float clampWarningLaneRightBelowInfoColumn(float f, float f2, float f3, float f4, float f5) {
        float fFiniteOr = finiteOr(f2, 0.0f) - minimumLaneGap(f3);
        float fFiniteOr2 = finiteOr(f4, 0.0f);
        float f6 = 1.0f + fFiniteOr2;
        float fMin = Math.min(Math.max(f6, finiteOr(f5, f6)), fFiniteOr);
        return fMin < fFiniteOr2 ? fMin : clamp(finiteOr(f, fMin), fFiniteOr2, fMin);
    }

    static int fitWarningHeroSize(int i, int i2, int i3, float f, float f2) {
        return fitWarningHeroSize(i, i2, i3, f, f2, 0.0f);
    }

    static int fitWarningHeroSize(int i, int i2, int i3, float f, float f2, float f3) {
        int iMax = Math.max(1, i2);
        int iMax2 = Math.max(0, i3);
        float fFiniteOr = finiteOr(f, 0.0f) + Math.max(0.0f, finiteOr(f3, 0.0f));
        return Math.max(1, Math.min(Math.max(1, i), Math.max(1, (int) Math.floor((Math.max(1.0f, (finiteOr(f2, fFiniteOr + 1.0f) - r9) - fFiniteOr) - (Math.max(0, iMax - 1) * iMax2)) / iMax))));
    }

    static String layoutProfile(int i, int i2, float f, boolean z) {
        if (z) {
            return PROFILE_EMULATOR_DESKTOP;
        }
        int iMax = Math.max(1, i);
        int iMax2 = Math.max(1, i2);
        if (Math.min(iMax, iMax2) / finitePositive(f, 1.0f) >= 600.0f) {
            return PROFILE_TABLET;
        }
        return iMax2 >= iMax ? PROFILE_PHONE_PORTRAIT : PROFILE_PHONE_LANDSCAPE;
    }

    static float effectiveInfoOffsetPx(int i, float f) {
        return clampOffsetDp(i) * finitePositive(f, 1.0f);
    }

    static float effectiveInfoOffsetPx(String str, int i, float f) {
        return effectiveInfoOffsetPx(i, f);
    }

    static int clampOffsetDp(int i) {
        return Math.max(MIN_OFFSET_DP, Math.min(SwapBoardMinimizePolicy.MIN_DOCK_WIDTH_DP, i));
    }

    static float attackReticleSize(Float f, Float f2, int i, float f3, float f4) {
        float fMax = Math.max(0.6f, Math.min(0.9f, f4));
        float fClamp = clamp(finiteOr(f3, 1.0f), 0.5f, 2.0f);
        if (f != null && f2 != null && f.floatValue() > 0.0f && f2.floatValue() > 0.0f) {
            return Math.min(f.floatValue(), f2.floatValue()) * fMax * fClamp;
        }
        return i * 1.1f * fClamp * (fMax / WARNING_CARD_WIDTH_RATIO);
    }

    static float attackReticleOuterRadius(float f) {
        return (Math.max(0.0f, finiteOr(f, 0.0f)) * ATTACK_RETICLE_VISUAL_RADIUS_SCALE) + 1.0f;
    }

    static float attackReticleHalfExtent(float f) {
        return Math.max(0.0f, finiteOr(f, 0.0f)) * 0.42f;
    }

    static float attackReticleCornerLength(float f) {
        return Math.max(0.0f, finiteOr(f, 0.0f)) * 0.18f;
    }

    static float attackReticleHaloRadius(float f) {
        return Math.max(0.0f, finiteOr(f, 0.0f)) * 0.2f;
    }

    static float attackReticleDotRadius(float f) {
        return Math.max(0.0f, finiteOr(f, 0.0f)) * 0.075f;
    }

    static float fitAttackReticleSizeToDisplay(float f, float f2, float f3, float f4) {
        return fitAttackReticleSizeToBounds(f, 0.0f, 0.0f, f2, f3, f4);
    }

    static float fitAttackReticleSizeToBounds(float f, float f2, float f3, float f4, float f5, float f6) {
        float fMax = Math.max(0.0f, finiteOr(f, 0.0f));
        float fFiniteOr = finiteOr(f2, 0.0f);
        float fFiniteOr2 = finiteOr(f3, 0.0f);
        float f7 = fFiniteOr + 1.0f;
        float fMax2 = Math.max(f7, finiteOr(f4, f7));
        float f8 = fFiniteOr2 + 1.0f;
        float fMax3 = Math.max(0.0f, (Math.min(fMax2 - fFiniteOr, Math.max(f8, finiteOr(f5, f8)) - fFiniteOr2) / 2.0f) - Math.max(0.0f, finiteOr(f6, 0.0f)));
        return attackReticleOuterRadius(fMax) <= fMax3 ? fMax : Math.max(0.0f, (fMax3 - 1.0f) / ATTACK_RETICLE_VISUAL_RADIUS_SCALE);
    }

    static float clampAttackReticleCenter(float f, float f2, float f3, float f4) {
        float fFiniteOr = finiteOr(f3, 0.0f);
        float fMax = Math.max(fFiniteOr, finiteOr(f4, fFiniteOr));
        float fAttackReticleOuterRadius = attackReticleOuterRadius(f2);
        float f5 = fFiniteOr + fAttackReticleOuterRadius;
        float f6 = fMax - fAttackReticleOuterRadius;
        return f5 > f6 ? fFiniteOr + ((fMax - fFiniteOr) / 2.0f) : clamp(finiteOr(f, f5), f5, f6);
    }

    static float displayEdgeInLocalCoordinates(float f, float f2) {
        return finiteOr(f, 0.0f) - finiteOr(f2, 0.0f);
    }

    static float localVisibleStart(float f, float f2, float f3) {
        return clamp(Math.max(0.0f, finiteOr(f, 0.0f)) - finiteOr(f2, 0.0f), 0.0f, Math.max(1.0f, finiteOr(f3, 1.0f)));
    }

    static float localVisibleEnd(float f, float f2, float f3, float f4) {
        float fMax = Math.max(1.0f, finiteOr(f4, 1.0f));
        return clamp((Math.max(1.0f, finiteOr(f, fMax)) - Math.max(0.0f, finiteOr(f2, 0.0f))) - finiteOr(f3, 0.0f), 0.0f, fMax);
    }

    static float clampLaneRightToVisibleBounds(float f, float f2, float f3, float f4) {
        float fFiniteOr = finiteOr(f3, 0.0f);
        float f5 = 1.0f + fFiniteOr;
        float fMax = Math.max(f5, finiteOr(f4, f5));
        float fMin = Math.min(fMax, fFiniteOr + Math.max(0.0f, finiteOr(f2, 0.0f)));
        return clamp(finiteOr(f, fMin), fMin, fMax);
    }

    static int clampWindowOffsetToVisibleBounds(int i, int i2, int i3, int i4, int i5, boolean z) {
        int iMax = Math.max(1, i2);
        int iMax2 = Math.max(1, i3);
        int iMax3 = Math.max(0, Math.min(iMax, i4));
        int iMax4 = Math.max(0, Math.min(iMax, i5));
        int i6 = z ? iMax4 : iMax3;
        return Math.max(i6, Math.min(Math.max(i6, (z ? iMax - iMax3 : iMax - iMax4) - iMax2), i));
    }

    static int windowOffsetForPhysicalLeft(int i, int i2, int i3, int i4, int i5, boolean z) {
        long j = z ? (i2 - i) - i3 : i;
        return clampWindowOffsetToVisibleBounds(j < -2147483648L ? Integer.MIN_VALUE : j > 2147483647L ? Integer.MAX_VALUE : (int) j, i2, i3, i4, i5, z);
    }

    static int clampSizePercent(int i) {
        return Math.max(20, Math.min(SwapBoardMinimizePolicy.MIN_DOCK_WIDTH_DP, i));
    }

    static int clampOpacityPercent(int i) {
        return Math.max(20, Math.min(100, i));
    }

    static int clampOpponentIconSizePercent(int i) {
        return Math.max(50, Math.min(TraceCatalog.Feature.AUTO_PICK, i));
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    private static float finitePositive(float f, float f2) {
        return (!isFinite(f) || f <= 0.0f) ? f2 : f;
    }

    private static float finiteOr(float f, float f2) {
        return isFinite(f) ? f : f2;
    }

    private static boolean isFinite(float f) {
        return (Float.isNaN(f) || Float.isInfinite(f)) ? false : true;
    }
}
