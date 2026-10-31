package com.android.support;

import com.android.support.SpecifiedHexCatalog;
import com.android.support.SpecifiedHexOfferTable;

/* loaded from: classes.dex */
final class SpecifiedHexOfferPolicy {
    static final String LIVE_DEFAULT_MODE = "18";
    static final String LIVE_OFFICIAL_SEASON = "19";

    static boolean bannedOnStage(int i, int i2, int i3, int i4) {
        return i == 0 ? i2 != 0 : i == 1 ? i3 != 0 : i == 2 && i4 != 0;
    }

    private SpecifiedHexOfferPolicy() {
    }

    static String officialSeason(int i) {
        if (i == 6) {
            return "100030";
        }
        return LIVE_OFFICIAL_SEASON;
    }

    static String preferredMode(int i) {
        if (i == 6) {
            return "11";
        }
        if (i == 0) {
            return "4";
        }
        if (i == 4) {
            return "8";
        }
        if (i == 2) {
            return "16";
        }
        return LIVE_DEFAULT_MODE;
    }

    static int stageLevel(int[] iArr, int i) {
        int i2;
        if (iArr == null || i < 0 || i >= iArr.length || (i2 = iArr[i]) < 1 || i2 > 3) {
            return 0;
        }
        return i2;
    }

    static boolean hasStageLevel(int[] iArr, int i) {
        return stageLevel(iArr, i) >= 1;
    }

    static boolean hasAnyStageLevel(int[] iArr) {
        return hasStageLevel(iArr, 0) || hasStageLevel(iArr, 1) || hasStageLevel(iArr, 2);
    }

    static int resolvedPickerLevel(boolean z, int[] iArr, int i, int i2) {
        int iStageLevel;
        if (i2 < 1 || i2 > 3) {
            i2 = 1;
        }
        return (z && (iStageLevel = stageLevel(iArr, i)) != 0) ? iStageLevel : i2;
    }

    static int[] copyStageLevels(int[] iArr) {
        int[] iArr2 = new int[3];
        iArr2[0] = 0;
        iArr2[1] = 0;
        iArr2[2] = 0;
        if (iArr != null) {
            int iMin = Math.min(3, iArr.length);
            for (int i = 0; i < iMin; i++) {
                int i2 = iArr[i];
                if (i2 >= 1 && i2 <= 3) {
                    iArr2[i] = i2;
                }
            }
        }
        return iArr2;
    }

    static boolean sameStageLevels(int[] iArr, int[] iArr2) {
        for (int i = 0; i < 3; i++) {
            if (stageLevel(iArr, i) != stageLevel(iArr2, i)) {
                return false;
            }
        }
        return true;
    }

    static boolean modeVisibleThisSeason(SpecifiedHexCatalog.Mode mode, String str) {
        if (mode == null) {
            return false;
        }
        if (str == null || str.isEmpty() || mode.season == null || mode.season.isEmpty()) {
            return true;
        }
        return str.equals(mode.season);
    }

    static boolean modeVisibleForMatch(SpecifiedHexCatalog.Mode mode, String str, String str2) {
        if (!modeVisibleThisSeason(mode, str)) {
            return false;
        }
        if (str2 == null || str2.isEmpty()) {
            return true;
        }
        return str2.equals(mode.id);
    }

    static boolean isOffered(SpecifiedHexCatalog.Entry entry, SpecifiedHexOfferTable specifiedHexOfferTable, String str, String str2, int i, int i2) {
        if (entry == null) {
            return false;
        }
        if (str != null && !str.isEmpty() && entry.season != null && !entry.season.isEmpty() && !str.equals(entry.season)) {
            return false;
        }
        if ((str2 != null && !str2.isEmpty() && !str2.equals(entry.mode)) || i2 < 1 || i2 > 3 || !hasAuthoritativeOfferTable(specifiedHexOfferTable, str, entry.mode)) {
            return false;
        }
        int i3 = i + 1;
        SpecifiedHexOfferTable.Row row = specifiedHexOfferTable.get(entry.mode, entry.id);
        return row != null && row.allowsStage(i3) && row.quality == i2;
    }

    static boolean isOfferedForDisplay(SpecifiedHexCatalog.Entry entry, SpecifiedHexOfferTable specifiedHexOfferTable, String str, String str2, int i, int i2) {
        SpecifiedHexOfferTable.Row row;
        if (isRuntimeCatalog(str, str2)) {
            return isCatalogPreference(entry, str, str2) && i >= 0 && i < 3 && (i2 == 0 || entry.level == i2);
        }
        if (i2 < 1 || i2 > 3) {
            return isInLivePoolWithoutRowLookup(entry, str, str2) && hasAuthoritativeOfferTable(specifiedHexOfferTable, str, entry.mode) && (row = specifiedHexOfferTable.get(entry.mode, entry.id)) != null && row.allowsStage(i + 1);
        }
        return isOffered(entry, specifiedHexOfferTable, str, str2, i, i2);
    }

    static boolean isInLivePool(SpecifiedHexCatalog.Entry entry, SpecifiedHexOfferTable specifiedHexOfferTable, String str, String str2) {
        return isInLivePoolWithoutRowLookup(entry, str, str2) && hasAuthoritativeOfferTable(specifiedHexOfferTable, str, entry.mode) && specifiedHexOfferTable.get(entry.mode, entry.id) != null;
    }

    static boolean isRuntimeCatalog(String str, String str2) {
        return "100030".equals(str) && "11".equals(str2);
    }

    static boolean isCatalogPreference(SpecifiedHexCatalog.Entry entry, String str, String str2) {
        return entry != null && isRuntimeCatalog(str, str2) && str.equals(entry.season) && str2.equals(entry.mode) && entry.level >= 1 && entry.level <= 3;
    }

    private static boolean isInLivePoolWithoutRowLookup(SpecifiedHexCatalog.Entry entry, String str, String str2) {
        if (entry == null) {
            return false;
        }
        if (str == null || str.isEmpty() || entry.season == null || entry.season.isEmpty() || str.equals(entry.season)) {
            return str2 == null || str2.isEmpty() || str2.equals(entry.mode);
        }
        return false;
    }

    static boolean isRollableNow(int i, int[] iArr) {
        if (iArr == null || iArr.length == 0) {
            return true;
        }
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    static boolean hasLivePool(int[] iArr) {
        return iArr != null && iArr.length > 0;
    }

    static boolean hasAuthoritativeOfferTable(SpecifiedHexOfferTable specifiedHexOfferTable, String str, String str2) {
        return (specifiedHexOfferTable == null || str == null || str.isEmpty() || !str.equals(specifiedHexOfferTable.liveSeason) || !specifiedHexOfferTable.coversMode(str2)) ? false : true;
    }

    static boolean isOffered(SpecifiedHexCatalog.Entry entry, String str, String str2, int i, int i2) {
        return isOffered(entry, null, str, str2, i, i2);
    }
}
