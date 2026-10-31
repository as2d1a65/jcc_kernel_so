package com.android.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
final class S11HeroCatalog {
    static final Map<String, String> ENGLISH_NAMES;
    static final Map<Integer, String> NAMES;
    static final int SEASON = 6;

    static {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        put(linkedHashMap, linkedHashMap2, 11205, "盖伦", "garen");
        put(linkedHashMap, linkedHashMap2, 11206, "贾克斯", "jax");
        put(linkedHashMap, linkedHashMap2, 11207, "墨菲特", "malphite");
        put(linkedHashMap, linkedHashMap2, 11208, "科加斯", "chogath");
        put(linkedHashMap, linkedHashMap2, 11209, "卡兹克", "khazix");
        put(linkedHashMap, linkedHashMap2, 11210, "德莱厄斯", "darius");
        put(linkedHashMap, linkedHashMap2, 11211, "雷克塞", "reksai");
        put(linkedHashMap, linkedHashMap2, 11212, "亚索", "yasuo");
        put(linkedHashMap, linkedHashMap2, 11213, "希维尔", "sivir");
        put(linkedHashMap, linkedHashMap2, 11214, "阿狸", "ahri");
        put(linkedHashMap, linkedHashMap2, 11215, "凯特琳", "caitlyn");
        put(linkedHashMap, linkedHashMap2, 11216, "可酷伯", "frank");
        put(linkedHashMap, linkedHashMap2, 11217, "克格莫", "kogmaw");
        put(linkedHashMap, linkedHashMap2, 12195, "纳尔", "gnarbig");
        put(linkedHashMap, linkedHashMap2, 12196, "亚托克斯", "aatrox");
        put(linkedHashMap, linkedHashMap2, 12197, "婕拉", "zyra");
        put(linkedHashMap, linkedHashMap2, 12198, "拉克丝", "lux");
        put(linkedHashMap, linkedHashMap2, 12199, "慎", "shen");
        put(linkedHashMap, linkedHashMap2, 12302, "奇亚娜", "qiyana");
        put(linkedHashMap, linkedHashMap2, 12303, "妮蔻", "neeko");
        put(linkedHashMap, linkedHashMap2, 12305, "千珏", "kindred");
        put(linkedHashMap, linkedHashMap2, 12306, "锐雯", "riven");
        put(linkedHashMap, linkedHashMap2, 12307, "赛娜", "senna");
        put(linkedHashMap, linkedHashMap2, 12308, "约里克", "yorick");
        put(linkedHashMap, linkedHashMap2, 12309, "迦娜", "janna");
        put(linkedHashMap, linkedHashMap2, 12310, "提莫", "teemo");
        put(linkedHashMap, linkedHashMap2, 13211, "永恩", "yone");
        put(linkedHashMap, linkedHashMap2, 13212, "厄斐琉斯", "aphelios");
        put(linkedHashMap, linkedHashMap2, 13213, "巴德", "bard");
        put(linkedHashMap, linkedHashMap2, 13214, "拉露恩", "alune");
        put(linkedHashMap, linkedHashMap2, 13215, "阿木木", "amumu");
        put(linkedHashMap, linkedHashMap2, 13216, "黛安娜", "diana");
        put(linkedHashMap, linkedHashMap2, 13217, "俄洛伊", "illaoi");
        put(linkedHashMap, linkedHashMap2, 13218, "佐伊", "zoe");
        put(linkedHashMap, linkedHashMap2, 13219, "塔姆", "tahmkench");
        put(linkedHashMap, linkedHashMap2, 13220, "崔丝塔娜", "tristana");
        put(linkedHashMap, linkedHashMap2, 13221, "沃利贝尔", "volibear");
        put(linkedHashMap, linkedHashMap2, 13222, "锤石", "thresh");
        put(linkedHashMap, linkedHashMap2, 13223, "索拉卡", "soraka");
        put(linkedHashMap, linkedHashMap2, 14201, "加里奥", "galio");
        put(linkedHashMap, linkedHashMap2, 14202, "莫甘娜", "morgana");
        put(linkedHashMap, linkedHashMap2, 14203, "安妮", "annie");
        put(linkedHashMap, linkedHashMap2, 14205, "奥恩", "ornn");
        put(linkedHashMap, linkedHashMap2, 14206, "莉莉娅", "lillia");
        put(linkedHashMap, linkedHashMap2, 14207, "辛德拉", "syndra");
        put(linkedHashMap, linkedHashMap2, 14209, "卡莎", "kaisa");
        put(linkedHashMap, linkedHashMap2, 14210, "凯隐", "kayn");
        put(linkedHashMap, linkedHashMap2, 14212, "诺提勒斯", "nautilus");
        put(linkedHashMap, linkedHashMap2, 14213, "艾希", "ashe");
        put(linkedHashMap, linkedHashMap2, 14214, "李青", "leesin");
        put(linkedHashMap, linkedHashMap2, 14215, "塞拉斯", "sylas");
        put(linkedHashMap, linkedHashMap2, 15175, "丽桑卓", "lissandra");
        put(linkedHashMap, linkedHashMap2, 15176, "瑟提", "sett");
        put(linkedHashMap, linkedHashMap2, 15177, "霞与洛", "xayah_rakan");
        put(linkedHashMap, linkedHashMap2, 15180, "乌迪尔", "udyr");
        put(linkedHashMap, linkedHashMap2, 15182, "彗", "hwei");
        put(linkedHashMap, linkedHashMap2, 15183, "阿兹尔", "azir");
        put(linkedHashMap, linkedHashMap2, 15184, "艾瑞莉娅", "irelia");
        put(linkedHashMap, linkedHashMap2, 15185, "孙悟空", "wukong");
        NAMES = Collections.unmodifiableMap(linkedHashMap);
        ENGLISH_NAMES = Collections.unmodifiableMap(linkedHashMap2);
    }

    private S11HeroCatalog() {
    }

    private static void put(Map<Integer, String> map, Map<String, String> map2, int i, String str, String str2) {
        map.put(Integer.valueOf(i), str);
        map2.put(str, str2);
    }

    static String name(int i) {
        return (i < 10000 || i > 49999) ? "未知英雄" : NAMES.getOrDefault(Integer.valueOf(i - (((i / 10000) - 1) * 10000)), "未知英雄");
    }
}
