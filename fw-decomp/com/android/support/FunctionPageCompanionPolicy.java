package com.android.support;

/* loaded from: classes.dex */
final class FunctionPageCompanionPolicy {
    static final int PAGE_ALL_IN = 4;
    static final int PAGE_AUTO_PICK = 0;
    static final int PAGE_COUNT = 7;
    static final int PAGE_HEX = 5;
    static final int PAGE_MEMORY = 6;
    static final int PAGE_REFRESH = 2;
    static final int PAGE_SELL_SWAP = 3;
    static final int PAGE_SPECIFIED_SHOP = 1;

    static int clampPage(int i) {
        if (i < 0) {
            return 0;
        }
        if (i >= 7) {
            return 6;
        }
        return i;
    }

    private FunctionPageCompanionPolicy() {
    }

    static int availablePage(int i, UiSurfacePolicy uiSurfacePolicy) {
        if (uiSurfacePolicy.visible(UiSurfaceRegistry.PAGE_FUNCTION, true) && uiSurfacePolicy.enabled(UiSurfaceRegistry.PAGE_FUNCTION, true)) {
            int iClampPage = clampPage(i);
            if (pageOperational(iClampPage, uiSurfacePolicy)) {
                return iClampPage;
            }
            for (int i2 = 0; i2 < 7; i2++) {
                if (pageOperational(i2, uiSurfacePolicy)) {
                    return i2;
                }
            }
        }
        return -1;
    }

    private static boolean pageOperational(int i, UiSurfacePolicy uiSurfacePolicy) {
        String strSurfaceId = surfaceId(i);
        return strSurfaceId == null || (uiSurfacePolicy.visible(strSurfaceId, true) && uiSurfacePolicy.enabled(strSurfaceId, true));
    }

    static boolean showsAutoPickWindow(int i) {
        int iClampPage = clampPage(i);
        return iClampPage == 0 || iClampPage == 1;
    }

    static boolean showsAllInPriorityWindow(int i) {
        return clampPage(i) == 4;
    }

    static boolean showsMemoryWindows(int i) {
        return clampPage(i) == 6;
    }

    static boolean hasCompanionWindow(int i) {
        return showsAutoPickWindow(i) || showsAllInPriorityWindow(i) || showsMemoryWindows(i);
    }

    static String describe(int i) {
        int iClampPage = clampPage(i);
        if (iClampPage == 0) {
            return "自动拿牌";
        }
        if (iClampPage == 1) {
            return "指定商店";
        }
        if (iClampPage == 2) {
            return "自动刷新";
        }
        if (iClampPage == 3) {
            return "卖牌换位";
        }
        if (iClampPage == 4) {
            return "一键梭哈";
        }
        if (iClampPage == 5) {
            return "海克斯";
        }
        return "单机工具";
    }

    static String surfaceId(int i) {
        int iClampPage = clampPage(i);
        if (iClampPage == 0) {
            return UiSurfaceRegistry.PAGE_FUNC_AUTOPICK;
        }
        if (iClampPage == 1) {
            return UiSurfaceRegistry.PAGE_FUNC_SPECIFIED_SHOP;
        }
        if (iClampPage == 3) {
            return UiSurfaceRegistry.PAGE_FUNC_SELL_SWAP;
        }
        if (iClampPage == 4) {
            return UiSurfaceRegistry.PAGE_FUNC_ALL_IN;
        }
        if (iClampPage == 5) {
            return UiSurfaceRegistry.PAGE_FUNC_HEX;
        }
        if (iClampPage != 6) {
            return null;
        }
        return UiSurfaceRegistry.PAGE_FUNC_MEMORY;
    }
}
