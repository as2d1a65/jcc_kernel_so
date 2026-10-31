package com.android.support;

/* loaded from: classes.dex */
final class RecastTierPolicy {
    static final int MAX_TARGETS = 1;
    static final int TIER_ARTIFACT = 12;
    static final int TIER_BASE = 1;
    static final int TIER_COMBINED = 3;
    static final int TIER_EMBLEM_CRAFTED = 4;
    static final int TIER_EMBLEM_LEGACY_PAN = 102;
    static final int TIER_EMBLEM_SPECIAL = 9;
    static final int TIER_RADIANT = 7;
    static final int TIER_SUPPORT = 11;
    static final int TIER_EMBLEM_LEGACY_SPATULA = 101;
    static final int TIER_EMBLEM_LEGACY_FIXED = 103;
    private static final int[] SELECTABLE = {1, 3, 7, 12, 11, 4, 9, TIER_EMBLEM_LEGACY_SPATULA, 102, TIER_EMBLEM_LEGACY_FIXED};
    private static final int[][] DISPLAY_GROUPS = {new int[]{1}, new int[]{3}, new int[]{7}, new int[]{12}, new int[]{11}, new int[]{4, 9, TIER_EMBLEM_LEGACY_SPATULA, 102, TIER_EMBLEM_LEGACY_FIXED}};

    static boolean canAddTarget(int i) {
        return i < 1;
    }

    static boolean isEmblem(int i) {
        return i == 4 || i == 9 || i == TIER_EMBLEM_LEGACY_SPATULA || i == 102 || i == TIER_EMBLEM_LEGACY_FIXED;
    }

    static int tierColor(int i) {
        if (i == 1) {
            return -4732967;
        }
        if (i == 7) {
            return -11930;
        }
        if (i == 9) {
            return -6641409;
        }
        if (i == 3) {
            return -9967367;
        }
        if (i == 4) {
            return -685057;
        }
        if (i == 11) {
            return GameHudPalette.SUCCESS;
        }
        if (i == 12) {
            return -30116;
        }
        switch (i) {
            case TIER_EMBLEM_LEGACY_SPATULA /* 101 */:
                return -2065409;
            case 102:
                return -20240;
            case TIER_EMBLEM_LEGACY_FIXED /* 103 */:
                return -7364353;
            default:
                return -1378561;
        }
    }

    private RecastTierPolicy() {
    }

    static int[] selectableTiers() {
        int[] iArr = SELECTABLE;
        int[] iArr2 = new int[iArr.length];
        System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
        return iArr2;
    }

    static String emblemOriginHint(int i) {
        if (i == 4) {
            return "金铲铲能合成的";
        }
        if (i != 9) {
            switch (i) {
            }
            return "金铲铲能合成的";
        }
        return "铲子合不出、只能靠掉落或海克斯拿到的";
    }

    static int displayGroupCount() {
        return DISPLAY_GROUPS.length;
    }

    static int[] displayGroupTiers(int i) {
        if (i >= 0) {
            int[][] iArr = DISPLAY_GROUPS;
            if (i < iArr.length) {
                int[] iArr2 = iArr[i];
                int[] iArr3 = new int[iArr2.length];
                System.arraycopy(iArr2, 0, iArr3, 0, iArr2.length);
                return iArr3;
            }
        }
        return new int[0];
    }

    static String displayGroupName(int i) {
        int[] iArrDisplayGroupTiers = displayGroupTiers(i);
        return iArrDisplayGroupTiers.length == 0 ? "" : iArrDisplayGroupTiers.length == 1 ? tierName(iArrDisplayGroupTiers[0]) : EquipmentIdMapper.CAT_EMBLEM;
    }

    static String displayGroupFullName(int i) {
        int[] iArrDisplayGroupTiers = displayGroupTiers(i);
        return iArrDisplayGroupTiers.length == 0 ? "" : iArrDisplayGroupTiers.length == 1 ? tierFullName(iArrDisplayGroupTiers[0]) : EquipmentIdMapper.CAT_EMBLEM;
    }

    static int displayGroupColor(int i) {
        int[] iArrDisplayGroupTiers = displayGroupTiers(i);
        if (iArrDisplayGroupTiers.length == 0) {
            return -1378561;
        }
        return tierColor(iArrDisplayGroupTiers[0]);
    }

    static int displayGroupOf(int i) {
        int i2 = 0;
        while (true) {
            int[][] iArr = DISPLAY_GROUPS;
            if (i2 >= iArr.length) {
                return -1;
            }
            for (int i3 : iArr[i2]) {
                if (i3 == i) {
                    return i2;
                }
            }
            i2++;
        }
    }

    static boolean isSelectable(int i) {
        for (int i2 : SELECTABLE) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    static String tierName(int i) {
        if (i == 1) {
            return "散件";
        }
        if (i == 7) {
            return "光明";
        }
        if (i == 9) {
            return "特殊纹章";
        }
        if (i == 3) {
            return "成装";
        }
        if (i == 4) {
            return "合成纹章";
        }
        if (i == 11) {
            return "辅助";
        }
        if (i == 12) {
            return "神器";
        }
        switch (i) {
            case TIER_EMBLEM_LEGACY_SPATULA /* 101 */:
                return "铲子纹章";
            case 102:
                return "锅锅纹章";
            case TIER_EMBLEM_LEGACY_FIXED /* 103 */:
                return "固定纹章";
            default:
                return "品类" + i;
        }
    }

    static String tierFullName(int i) {
        if (i == 1) {
            return EquipmentIdMapper.CAT_BASIC;
        }
        if (i == 7) {
            return EquipmentIdMapper.CAT_RADIANT;
        }
        if (i == 9) {
            return "特殊转职纹章";
        }
        if (i == 3) {
            return EquipmentIdMapper.CAT_COMBINED;
        }
        if (i == 4) {
            return "可合成纹章";
        }
        if (i == 11) {
            return EquipmentIdMapper.CAT_SUPPORT;
        }
        if (i == 12) {
            return EquipmentIdMapper.CAT_ARTIFACT;
        }
        switch (i) {
            case TIER_EMBLEM_LEGACY_SPATULA /* 101 */:
                return "金铲铲纹章";
            case 102:
                return "金锅锅纹章";
            case TIER_EMBLEM_LEGACY_FIXED /* 103 */:
                return "不可合成纹章";
            default:
                return tierName(i);
        }
    }

    static boolean sameRecastPool(int i, int i2) {
        return isSelectable(i) && i == i2;
    }
}
