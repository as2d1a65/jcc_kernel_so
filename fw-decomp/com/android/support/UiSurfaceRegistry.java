package com.android.support;

/* loaded from: classes.dex */
public final class UiSurfaceRegistry {
    public static final String PAGE_FUNCTION = "page.function";
    public static final String PAGE_HERO_POOL = "page.hero_pool";
    public static final String PAGE_SPECIFIED = "page.specified";
    public static final String PAGE_PLAYERS = "page.players";
    public static final String PAGE_VOICE = "page.voice";
    public static final String PAGE_HOTKEY = "page.hotkey";
    public static final String PAGE_SETTINGS = "page.settings";
    public static final String PAGE_ABOUT = "page.about";
    public static final String PAGE_FUNC_AUTOPICK = "page.func_autopick";
    public static final String PAGE_FUNC_MEMORY = "page.func_memory";
    public static final String PAGE_PLAYERS_DATA = "page.players_data";
    public static final String PAGE_PREDICT = "page.predict";
    public static final String PAGE_RANK_WARNING = "page.rank_warning";
    public static final String OVERLAY_DEBUG_WINDOW = "overlay.debug_window";
    public static final String PAGE_INTRO = "page.intro";
    public static final String PAGE_FUNC_SELL_SWAP = "page.func_sell_swap";
    public static final String PAGE_FUNC_ALL_IN = "page.func_all_in";
    public static final String PAGE_FUNC_HEX = "page.func_hex";
    public static final String PAGE_FUNC_SPECIFIED_SHOP = "page.func_specified_shop";
    public static final String PAGE_FUNC_RECAST = "page.func_recast";
    static final String[] IDS = {PAGE_FUNCTION, PAGE_HERO_POOL, PAGE_SPECIFIED, PAGE_PLAYERS, PAGE_VOICE, PAGE_HOTKEY, PAGE_SETTINGS, PAGE_ABOUT, PAGE_FUNC_AUTOPICK, PAGE_FUNC_MEMORY, PAGE_PLAYERS_DATA, PAGE_PREDICT, PAGE_RANK_WARNING, OVERLAY_DEBUG_WINDOW, PAGE_INTRO, PAGE_FUNC_SELL_SWAP, PAGE_FUNC_ALL_IN, PAGE_FUNC_HEX, PAGE_FUNC_SPECIFIED_SHOP, PAGE_FUNC_RECAST};

    public static int size() {
        return IDS.length;
    }

    public static String idAt(int i) {
        return IDS[i];
    }

    public static int indexOf(String str) {
        int i = 0;
        while (true) {
            String[] strArr = IDS;
            if (i >= strArr.length) {
                return -1;
            }
            if (strArr[i].equals(str)) {
                return i;
            }
            i++;
        }
    }

    private UiSurfaceRegistry() {
    }
}
