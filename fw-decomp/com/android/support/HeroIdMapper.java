package com.android.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public class HeroIdMapper {
    private static final Map<Integer, String> FUX_ID_TO_CN_NAME = new HashMap();
    private static final Map<String, String> FUX_CN_TO_EN_NAME = new HashMap();
    private static final Map<Integer, String> S10_ID_TO_CN_NAME = new HashMap();
    private static final Map<String, String> S10_CN_TO_EN_NAME = new HashMap();
    private static final Map<Integer, String> S16_ID_TO_CN_NAME = new HashMap();
    private static final Map<String, String> S16_CN_TO_EN_NAME = new HashMap();
    private static final Map<Integer, String> S17_ID_TO_CN_NAME = new HashMap();
    private static final Map<String, String> S17_CN_TO_EN_NAME = new HashMap();
    private static final Map<Integer, Integer> S17_FORM_TO_BASE = new HashMap();
    private static final Map<Integer, String> S8_ID_TO_CN_NAME = new HashMap();
    private static final Map<String, String> S8_CN_TO_EN_NAME = new HashMap();
    private static final Map<Integer, Integer> S8_FORM_TO_BASE = new HashMap();
    private static final Map<Integer, String> S18_ID_TO_CN_NAME = new HashMap();
    private static final Map<String, String> S18_CN_TO_EN_NAME = new HashMap();
    private static final Map<Integer, Integer> S18_FORM_TO_BASE = new HashMap();

    static {
        initFUXData();
        initS10Data();
        initS16Data();
        initS17Data();
        initS8Data();
        initS18Data();
    }

    private static void initFUXData() {
        Map<Integer, String> map = FUX_ID_TO_CN_NAME;
        map.put(11112, "黛安娜");
        map.put(11113, "伊莉丝");
        map.put(11114, "菲奥娜");
        map.put(11115, "盖伦");
        map.put(11117, "茂凯");
        map.put(11119, "奈德丽");
        map.put(11120, "塔姆");
        map.put(11121, "崔斯特");
        map.put(11123, "孙悟空");
        map.put(11124, "亚索");
        map.put(11145, "布兰德");
        map.put(11146, "崔丝塔娜");
        map.put(11147, "内瑟斯");
        map.put(12101, "安妮");
        map.put(12103, "赫卡里姆");
        map.put(12104, "迦娜");
        map.put(12105, "嘉文四世");
        map.put(12106, "贾克斯");
        map.put(12107, "璐璐");
        map.put(12108, "派克");
        map.put(12110, "提莫");
        map.put(12112, "蔚");
        map.put(12113, "劫");
        map.put(12135, "洛");
        map.put(12136, "布隆");
        map.put(12137, "诺提勒斯");
        map.put(12139, "弗拉基米尔");
        map.put(13111, "阿卡丽");
        map.put(13112, "伊芙琳");
        map.put(13113, "艾瑞莉娅");
        map.put(13115, "卡莉斯塔");
        map.put(13116, "卡特琳娜");
        map.put(13117, "凯南");
        map.put(13118, "千珏");
        map.put(13120, "努努");
        map.put(13121, "维迦");
        map.put(13122, "赵信");
        map.put(13123, "悠米");
        map.put(13145, "妮蔻");
        map.put(13146, "德莱厄斯");
        map.put(13147, "希瓦娜");
        map.put(13149, "希维尔");
        map.put(14101, "阿狸");
        map.put(14103, "卡西奥佩娅");
        map.put(14104, "烬");
        map.put(14105, "莫甘娜");
        map.put(14107, "瑟庄妮");
        map.put(14108, "慎");
        map.put(14109, "泰隆");
        map.put(14110, "沃里克");
        map.put(14111, "亚托克斯");
        map.put(14112, "锐雯");
        map.put(14131, "霞");
        map.put(14132, "奥瑞利安·索尔");
        map.put(14133, "奥拉夫");
        map.put(14135, "泰达米尔");
        map.put(14136, "凯尔");
        map.put(14138, "科加斯");
        map.put(15091, "阿兹尔");
        map.put(15093, "凯隐");
        map.put(15094, "李青");
        map.put(15096, "瑟提");
        map.put(15097, "永恩");
        map.put(15098, "基兰");
        map.put(15116, "奥恩");
        map.put(15117, "斯维因");
        map.put(15503, "索拉卡");
        map.put(15504, "费德提克");
        map.put(15505, "厄加特");
        map.put(15508, "潘森");
        map.put(15511, "克格莫");
        map.put(15512, "娑娜");
        map.put(15513, "婕拉");
        map.put(15514, "库奇");
        map.put(15516, "锤石");
        map.put(15517, "奇亚娜");
        map.put(15518, "俄洛伊");
        map.put(15528, "德莱文");
        map.put(15529, "凯特琳");
        map.put(15530, "兰博");
        map.put(15531, "阿萝拉");
        map.put(15532, "佛耶戈");
        map.put(15533, "雷克顿");
        map.put(15535, "莫德凯撒");
        map.put(15536, "泽拉斯");
        map.put(15537, "可酷伯");
        map.put(15538, "萨勒芬妮");
        map.put(15539, "阿利斯塔");
        map.put(15540, "格温");
        Map<String, String> map2 = FUX_CN_TO_EN_NAME;
        map2.put("黛安娜", "diana");
        map2.put("伊莉丝", "elise");
        map2.put("菲奥娜", "fiora");
        map2.put("盖伦", "garen");
        map2.put("茂凯", "maokai");
        map2.put("奈德丽", "nidalee");
        map2.put("塔姆", "tahmkench");
        map2.put("崔斯特", "twistedfate");
        map2.put("孙悟空", "wukong");
        map2.put("亚索", "yasuo");
        map2.put("布兰德", "brand");
        map2.put("崔丝塔娜", "tristana");
        map2.put("内瑟斯", "nasus");
        map2.put("安妮", "annie");
        map2.put("迦娜", "janna");
        map2.put("嘉文四世", "jarvan");
        map2.put("贾克斯", "jax");
        map2.put("璐璐", "lulu");
        map2.put("派克", "pyke");
        map2.put("提莫", "teemo");
        map2.put("蔚", "vi");
        map2.put("劫", "zed");
        map2.put("洛", "rakan");
        map2.put("布隆", "braum");
        map2.put("诺提勒斯", "nautilus");
        map2.put("弗拉基米尔", "vladimir");
        map2.put("赫卡里姆", "hecarim");
        map2.put("赵信", "xinzhao");
        map2.put("阿卡丽", "akali");
        map2.put("艾瑞莉娅", "irelia");
        map2.put("卡莉斯塔", "kalista");
        map2.put("卡特琳娜", "katarina");
        map2.put("凯南", "kennen");
        map2.put("千珏", "kindred");
        map2.put("努努", "nunu");
        map2.put("维迦", "veigar");
        map2.put("悠米", "yuumi");
        map2.put("妮蔻", "neeko");
        map2.put("德莱厄斯", "darius");
        map2.put("希瓦娜", "shyvana");
        map2.put("希维尔", "sivir");
        map2.put("烬", "jhin");
        map2.put("莫甘娜", "morgana");
        map2.put("瑟庄妮", "sejuani");
        map2.put("慎", "shen");
        map2.put("泰隆", "talon");
        map2.put("亚托克斯", "aatrox");
        map2.put("锐雯", "riven");
        map2.put("霞", "xayah");
        map2.put("奥瑞利安·索尔", "aurelionsol");
        map2.put("奥拉夫", "olaf");
        map2.put("凯尔", "kayle");
        map2.put("科加斯", "chogath");
        map2.put("萨勒芬妮", "seraphine");
        map2.put("格温", "gwen");
        map2.put("阿兹尔", "azir");
        map2.put("李青", "leesin");
        map2.put("瑟提", "sett");
        map2.put("永恩", "yone");
        map2.put("奥恩", "ornn");
        map2.put("斯维因", "swain");
        map2.put("索拉卡", "soraka");
        map2.put("费德提克", "fiddlesticks");
        map2.put("厄加特", "urgot");
        map2.put("潘森", "pantheon");
        map2.put("娑娜", "sona");
        map2.put("锤石", "thresh_new");
        map2.put("奇亚娜", "qiyana");
        map2.put("俄洛伊", "illaoi");
        map2.put("德莱文", "draven");
        map2.put("凯特琳", "caitlyn");
        map2.put("兰博", "rumble");
        map2.put("阿萝拉", "aurora");
        map2.put("佛耶戈", "viego");
        map2.put("雷克顿", "renekton");
        map2.put("莫德凯撒", "mordekaiser");
        map2.put("泽拉斯", "xerath");
        map2.put("可酷伯", "kobuko");
        map2.put("伊芙琳", "evelynn");
        map2.put("阿狸", "ahri");
        map2.put("卡西奥佩娅", "cassiopeia");
        map2.put("沃里克", "warwick");
        map2.put("泰达米尔", "tryndamere");
        map2.put("凯隐", "kayn");
        map2.put("基兰", "zilean");
        map2.put("克格莫", "kogmaw");
        map2.put("婕拉", "zyra");
        map2.put("库奇", "corki");
        map2.put("阿利斯塔", "alistar");
    }

    private static void initS10Data() {
        Map<Integer, String> map = S10_ID_TO_CN_NAME;
        map.put(11190, "库奇");
        map.put(11191, "莉莉娅");
        map.put(11192, "亚索");
        map.put(11193, "塔姆");
        map.put(11194, "奥拉夫");
        map.put(11195, "奎桑提");
        map.put(11196, "塔里克");
        map.put(11197, "娜美");
        map.put(11198, "凯南");
        map.put(11199, "伊芙琳");
        map.put(11200, "蔚");
        map.put(11201, "金克丝");
        map.put(11202, "安妮");
        map.put(12177, "厄斐琉斯");
        map.put(12178, "萨勒芬妮");
        map.put(12179, "卡莎");
        map.put(12180, "凯尔");
        map.put(12181, "纳尔");
        map.put(12183, "卡特琳娜");
        map.put(12184, "潘森");
        map.put(12185, "古拉加斯");
        map.put(12186, "贾克斯");
        map.put(12187, "赛娜");
        map.put(12188, "图奇");
        map.put(12191, "盖伦");
        map.put(12192, "巴德");
        map.put(13194, "厄加特");
        map.put(13195, "永恩");
        map.put(13196, "阿木木");
        map.put(13197, "璐璐");
        map.put(13198, "莫德拉克斯");
        map.put(13199, "拉克丝");
        map.put(13200, "锐雯");
        map.put(13201, "厄运小姐");
        map.put(13202, "莎弥拉");
        map.put(13203, "瑟提");
        map.put(13206, "妮蔻");
        map.put(13207, "艾克");
        map.put(13208, "薇古丝");
        map.put(14185, "崔斯特");
        map.put(14186, "扎克");
        map.put(14187, "阿狸");
        map.put(14188, "伊泽瑞尔");
        map.put(14189, "卡尔萨斯");
        map.put(14190, "阿卡丽-KDA");
        map.put(14191, "劫");
        map.put(14192, "波比");
        map.put(14194, "凯特琳");
        map.put(14195, "锤石");
        map.put(14196, "阿卡丽-真实伤害");
        map.put(14197, "佛耶戈");
        map.put(14198, "布里茨");
        map.put(15158, "奇亚娜");
        map.put(15160, "吉格斯");
        map.put(15161, "凯隐");
        map.put(15162, "约里克");
        map.put(15163, "卢锡安");
        map.put(15164, "烬");
        map.put(15165, "俄洛伊");
        map.put(15168, "娑娜");
        map.put(16010, "伊莉丝");
        map.put(16011, "阿萝拉");
        map.put(16012, "格温");
        Map<String, String> map2 = S10_CN_TO_EN_NAME;
        map2.put("库奇", "corki");
        map2.put("莉莉娅", "lillia");
        map2.put("亚索", "yasuo");
        map2.put("塔姆", "tahmkench");
        map2.put("奥拉夫", "olaf");
        map2.put("奎桑提", "ksante");
        map2.put("塔里克", "taric");
        map2.put("娜美", "nami");
        map2.put("凯南", "kennen");
        map2.put("伊芙琳", "evelynn");
        map2.put("蔚", "vi");
        map2.put("金克丝", "jinx");
        map2.put("安妮", "annie");
        map2.put("厄斐琉斯", "aphelios");
        map2.put("萨勒芬妮", "seraphine");
        map2.put("卡莎", "kaisa");
        map2.put("凯尔", "kayle");
        map2.put("纳尔", "gnar");
        map2.put("卡特琳娜", "katarina");
        map2.put("潘森", "pantheon");
        map2.put("古拉加斯", "gragas");
        map2.put("贾克斯", "jax");
        map2.put("赛娜", "senna");
        map2.put("图奇", "twitch");
        map2.put("盖伦", "garen");
        map2.put("巴德", "bard");
        map2.put("厄加特", "urgot");
        map2.put("永恩", "yone");
        map2.put("阿木木", "amumu");
        map2.put("璐璐", "lulu");
        map2.put("莫德拉克斯", "mordekaiser");
        map2.put("拉克丝", "lux");
        map2.put("锐雯", "riven");
        map2.put("厄运小姐", "missfortune");
        map2.put("莎弥拉", "samira");
        map2.put("瑟提", "sett");
        map2.put("妮蔻", "neeko");
        map2.put("艾克", "ekko");
        map2.put("薇古丝", "vex");
        map2.put("崔斯特", "twistedfate");
        map2.put("扎克", "zac");
        map2.put("阿狸", "ahri");
        map2.put("伊泽瑞尔", "ezreal");
        map2.put("卡尔萨斯", "karthus");
        map2.put("阿卡丽-KDA", "akali");
        map2.put("劫", "zed");
        map2.put("波比", "poppy");
        map2.put("凯特琳", "caitlyn");
        map2.put("锤石", "thresh");
        map2.put("阿卡丽-真实伤害", "akali_td");
        map2.put("佛耶戈", "viego");
        map2.put("布里茨", "blitzcrank");
        map2.put("奇亚娜", "qiyana");
        map2.put("吉格斯", "ziggs");
        map2.put("凯隐", "kayn");
        map2.put("约里克", "yorick");
        map2.put("卢锡安", "lucian");
        map2.put("烬", "jhin");
        map2.put("俄洛伊", "illaoi");
        map2.put("娑娜", "sona_green");
        map2.put("伊莉丝", "elise");
        map2.put("阿萝拉", "aurora");
        map2.put("格温", "gwen");
    }

    private static void initS16Data() {
        Map<Integer, String> map = S16_ID_TO_CN_NAME;
        map.put(11400, "佛耶戈");
        map.put(11401, "娑娜");
        map.put(11402, "慎");
        map.put(11403, "兰博");
        map.put(11404, "奇亚娜");
        map.put(11405, "璐璐");
        map.put(11406, "克格莫");
        map.put(11407, "烬");
        map.put(11408, "嘉文四世");
        map.put(11409, "俄洛伊");
        map.put(11410, "凯特琳");
        map.put(11411, "贝蕾亚");
        map.put(11412, "布里茨");
        map.put(11413, "艾尼维亚");
        map.put(11415, "俄洛伊");
        map.put(11416, "兰博");
        map.put(12400, "约里克");
        map.put(12401, "亚索");
        map.put(12402, "赵信");
        map.put(12403, "蔚");
        map.put(12404, "崔斯特");
        map.put(12405, "泰达米尔");
        map.put(12406, "崔丝塔娜");
        map.put(12407, "提莫");
        map.put(12408, "赛恩");
        map.put(12409, "雷克塞");
        map.put(12410, "奥莉安娜");
        map.put(12411, "妮蔻");
        map.put(12412, "格雷福斯");
        map.put(12413, "艾克");
        map.put(12414, "科加斯");
        map.put(12415, "巴德");
        map.put(12416, "艾希");
        map.put(12417, "厄斐琉斯");
        map.put(12418, "赵信");
        map.put(12420, "波比");
        map.put(12421, "波比");
        map.put(12422, "布兰德");
        map.put(13320, "佐伊");
        map.put(13321, "薇恩");
        map.put(13322, "瑟庄妮");
        map.put(13324, "诺提勒斯");
        map.put(13325, "米利欧");
        map.put(13326, "玛尔扎哈");
        map.put(13327, "洛里斯");
        map.put(13328, "蕾欧娜");
        map.put(13329, "乐芙兰");
        map.put(13330, "可酷伯与悠米");
        map.put(13331, "凯南");
        map.put(13332, "金克丝");
        map.put(13333, "格温");
        map.put(13334, "普朗克");
        map.put(13335, "德莱文");
        map.put(13336, "蒙多医生");
        map.put(13337, "德莱厄斯");
        map.put(13338, "阿狸");
        map.put(13355, "阿利斯塔");
        map.put(13356, "希维尔");
        map.put(13357, "墨菲特");
        map.put(13475, "维克托");
        map.put(13476, "萨科");
        map.put(14330, "芸阿娜");
        map.put(14331, "永恩");
        map.put(14332, "孙悟空");
        map.put(14333, "沃里克");
        map.put(14334, "维迦");
        map.put(14335, "塔里克");
        map.put(14336, "斯维因");
        map.put(14337, "斯卡纳");
        map.put(14338, "辛吉德");
        map.put(14339, "萨勒芬妮");
        map.put(14340, "峡谷先锋");
        map.put(14341, "雷克顿");
        map.put(14342, "奈德丽");
        map.put(14343, "内瑟斯");
        map.put(14344, "厄运小姐");
        map.put(14345, "拉克丝");
        map.put(14346, "丽桑卓");
        map.put(14347, "卡莉丝塔");
        map.put(14348, "卡莎");
        map.put(14349, "盖伦");
        map.put(14350, "菲兹");
        map.put(14351, "黛安娜");
        map.put(14352, "布隆");
        map.put(14353, "卑尔维斯");
        map.put(14354, "安蓓萨");
        map.put(14360, "卡莎");
        map.put(14364, "库奇");
        map.put(14365, "凯隐");
        map.put(14366, "伊莉丝");
        map.put(14367, "伊莉丝");
        map.put(14368, "凯隐");
        map.put(15270, "基兰");
        map.put(15271, "吉格斯");
        map.put(15272, "泽拉斯");
        map.put(15273, "沃利贝尔");
        map.put(15274, "锤石");
        map.put(15275, "海克斯霸龙");
        map.put(15276, "塔姆");
        map.put(15277, "塞拉斯");
        map.put(15278, "希瓦娜");
        map.put(15279, "瑟提");
        map.put(15280, "瑞兹");
        map.put(15281, "奥恩");
        map.put(15282, "梅尔");
        map.put(15284, "千珏");
        map.put(15285, "加里奥");
        map.put(15287, "费德提克");
        map.put(15288, "岩宝");
        map.put(15289, "纳什男爵");
        map.put(15290, "阿兹尔");
        map.put(15291, "奥瑞利安·索尔");
        map.put(15292, "安妮");
        map.put(15296, "亚托克斯");
        map.put(15297, "卢锡安与赛娜");
        map.put(15301, "希瓦娜");
        map.put(15304, "亚恒");
        map.put(15396, "彗");
        map.put(15397, "卢锡安与赛娜");
        map.put(15398, "卢锡安与赛娜");
        map.put(15399, "亚托克斯");
        map.put(15400, "远古巨龙");
        map.put(15401, "艾瑞莉娅");
        map.put(15402, "杰斯");
        map.put(15403, "莫德凯撒");
        map.put(15409, "杰斯");
        Map<String, String> map2 = S16_CN_TO_EN_NAME;
        map2.put("佛耶戈", "viego");
        map2.put("娑娜", "sona");
        map2.put("慎", "shen");
        map2.put("兰博", "rumble");
        map2.put("奇亚娜", "qiyana");
        map2.put("璐璐", "lulu");
        map2.put("克格莫", "kogmaw");
        map2.put("烬", "jhin");
        map2.put("嘉文四世", "jarvan");
        map2.put("俄洛伊", "illaoi");
        map2.put("凯特琳", "caitlyn");
        map2.put("贝蕾亚", "briar");
        map2.put("布里茨", "blitzcrank");
        map2.put("艾尼维亚", "anivia");
        map2.put("约里克", "yorick");
        map2.put("亚索", "yasuo");
        map2.put("赵信", "xinzhao");
        map2.put("蔚", "vi");
        map2.put("崔斯特", "twistedfate");
        map2.put("泰达米尔", "tryndamere");
        map2.put("崔丝塔娜", "tristana");
        map2.put("提莫", "teemo");
        map2.put("赛恩", "sion");
        map2.put("雷克塞", "reksai");
        map2.put("奥莉安娜", "orianna");
        map2.put("妮蔻", "neeko");
        map2.put("格雷福斯", "graves");
        map2.put("艾克", "ekko");
        map2.put("科加斯", "chogath");
        map2.put("巴德", "bard");
        map2.put("艾希", "ashe");
        map2.put("厄斐琉斯", "aphelios");
        map2.put("波比", "poppy");
        map2.put("布兰德", "brand");
        map2.put("佐伊", "zoe");
        map2.put("薇恩", "vayne");
        map2.put("瑟庄妮", "sejuani");
        map2.put("诺提勒斯", "nautilus");
        map2.put("米利欧", "milio");
        map2.put("玛尔扎哈", "malzahar");
        map2.put("洛里斯", "loris");
        map2.put("蕾欧娜", "leona");
        map2.put("乐芙兰", "leblanc");
        map2.put("可酷伯与悠米", "kobukoyuumi");
        map2.put("凯南", "kennen");
        map2.put("金克丝", "jinx");
        map2.put("格温", "gwen");
        map2.put("普朗克", "gangplank");
        map2.put("德莱文", "draven");
        map2.put("蒙多医生", "drmundo");
        map2.put("德莱厄斯", "darius");
        map2.put("阿狸", "ahri");
        map2.put("阿利斯塔", "alistar");
        map2.put("希维尔", "sivir");
        map2.put("墨菲特", "malphite");
        map2.put("维克托", "viktor");
        map2.put("萨科", "shaco");
        map2.put("芸阿娜", "yunara");
        map2.put("永恩", "yone");
        map2.put("孙悟空", "wukong");
        map2.put("沃里克", "warwick");
        map2.put("维迦", "veigar");
        map2.put("塔里克", "taric");
        map2.put("斯维因", "swain");
        map2.put("斯卡纳", "skarner");
        map2.put("辛吉德", "singed");
        map2.put("萨勒芬妮", "seraphine");
        map2.put("峡谷先锋", "riftherald");
        map2.put("雷克顿", "renekton");
        map2.put("奈德丽", "nidalee");
        map2.put("内瑟斯", "nasus");
        map2.put("厄运小姐", "missfortune");
        map2.put("拉克丝", "lux");
        map2.put("丽桑卓", "lissandra");
        map2.put("卡莉丝塔", "kalista");
        map2.put("盖伦", "garen");
        map2.put("菲兹", "fizz");
        map2.put("黛安娜", "diana");
        map2.put("布隆", "braum");
        map2.put("卑尔维斯", "belveth");
        map2.put("安蓓萨", "ambessa");
        map2.put("卡莎", "kaisa");
        map2.put("库奇", "corki");
        map2.put("凯隐", "kayn");
        map2.put("伊莉丝", "elise");
        map2.put("基兰", "zilean");
        map2.put("吉格斯", "ziggs");
        map2.put("泽拉斯", "xerath");
        map2.put("沃利贝尔", "volibear");
        map2.put("锤石", "thresh");
        map2.put("海克斯霸龙", "thex");
        map2.put("塔姆", "tahmkench");
        map2.put("塞拉斯", "sylas");
        map2.put("希瓦娜", "shyvana");
        map2.put("瑟提", "sett");
        map2.put("瑞兹", "ryze");
        map2.put("奥恩", "ornn");
        map2.put("梅尔", "mel");
        map2.put("千珏", "kindred");
        map2.put("加里奥", "galio");
        map2.put("费德提克", "fiddlesticks");
        map2.put("岩宝", "brock");
        map2.put("纳什男爵", "baronnashor");
        map2.put("阿兹尔", "azir");
        map2.put("奥瑞利安·索尔", "aurelionsol");
        map2.put("安妮", "annie");
        map2.put("亚托克斯", "aatrox");
        map2.put("卢锡安与赛娜", "senna");
        map2.put("亚恒", "zaahen");
        map2.put("彗", "hwei");
        map2.put("远古巨龙", "elderdragon");
        map2.put("艾瑞莉娅", "irelia");
        map2.put("杰斯", "jayce");
        map2.put("莫德凯撒", "mordekaiser");
    }

    private static void initS17Data() {
        putS17(11450, "崔斯特", "twistedfate", new int[0]);
        putS17(11451, "伊泽瑞尔", "ezreal", 11465, 11466);
        putS17(11452, "泰隆", "talon", new int[0]);
        putS17(11453, "内瑟斯", "nasus", 11464);
        putS17(11454, "蕾欧娜", "leona", new int[0]);
        putS17(11455, "雷克塞", "reksai", new int[0]);
        putS17(11456, "凯特琳", "caitlyn", new int[0]);
        putS17(11457, "丽桑卓", "lissandra", new int[0]);
        putS17(11458, "亚托克斯", "aatrox", 11467);
        putS17(11459, "科加斯", "chogath", 11469, 11470);
        putS17(11460, "提莫", "teemo", new int[0]);
        putS17(11461, "维迦", "veigar", new int[0]);
        putS17(11462, "波比", "poppy", 11468);
        putS17(11463, "贝蕾亚", "briar", new int[0]);
        putS17(12450, "纳尔", "gnar", new int[0]);
        putS17(12451, "格温", "gwen", new int[0]);
        putS17(12452, "派克", "pyke", 12464);
        putS17(12453, "佐伊", "zoe", new int[0]);
        putS17(12454, "卑尔维斯", "belveth", new int[0]);
        putS17(12455, "古拉加斯", "gragas", 12463);
        putS17(12456, "米利欧", "milio", new int[0]);
        putS17(12457, "莫德凯撒", "mordekaiser", 12465);
        putS17(12458, "金克丝", "jinx", new int[0]);
        putS17(12459, "潘森", "pantheon", new int[0]);
        putS17(12460, "小木灵", "meepsie", 12466);
        putS17(12461, "阿卡丽", "akali", new int[0]);
        putS17(12462, "贾克斯", "jax", 12467);
        putS17(13450, "卡莎", "kaisa", new int[0]);
        putS17(13451, "厄加特", "urgot", 13464);
        putS17(13452, "维克托", "viktor", new int[0]);
        putS17(13453, "奥恩", "ornn", new int[0]);
        putS17(13454, "璐璐", "lulu", 13468, 13469, 13470, 13471, 13472, 13473, 13474);
        putS17(13455, "菲兹", "fizz", new int[0]);
        putS17(13456, "厄运小姐", "missfortune", 13465, 13466, 13467);
        putS17(13457, "茂凯", "maokai", new int[0]);
        putS17(13458, "黛安娜", "diana", new int[0]);
        putS17(13459, "拉亚斯特", "rhaast", new int[0]);
        putS17(13460, "俄洛伊", "illaoi", new int[0]);
        putS17(13461, "莎弥拉", "samira", new int[0]);
        putS17(13462, "阿萝拉", "aurora", new int[0]);
        putS17(14370, "易", "masteryi", new int[0]);
        putS17(14371, "努努和威朗普", "nunu", new int[0]);
        putS17(14372, "库奇", "corki", new int[0]);
        putS17(14373, "卡尔玛", "karma", new int[0]);
        putS17(14374, "千珏", "kindred", new int[0]);
        putS17(14375, "锐雯", "riven", new int[0]);
        putS17(14376, "拉莫斯", "rammus", new int[0]);
        putS17(14377, "娜美", "nami", new int[0]);
        putS17(14378, "塔姆", "tahmkench", new int[0]);
        putS17(14379, "超级机甲", "galio", 14384);
        putS17(14380, "乐芙兰", "leblanc", 18409);
        putS17(14381, "霞", "xayah", new int[0]);
        putS17(14382, "奥瑞利安·索尔", "aurelionsol", 14383);
        putS17(15420, "巴德", "bard", new int[0]);
        putS17(15421, "布里茨", "blitzcrank", new int[0]);
        putS17(15422, "薇古丝", "vex", new int[0]);
        putS17(15423, "格雷福斯", "graves", 15429);
        putS17(15424, "慎", "shen", 15437);
        putS17(15425, "娑娜", "sona", new int[0]);
        putS17(15426, "菲奥娜", "fiora", new int[0]);
        putS17(15427, "烬", "jhin", 15432);
        putS17(15428, "劫", "zed", 15433);
        putS17(15434, "莫甘娜", "morgana", 15435);
    }

    private static void putS17(int i, String str, String str2, int... iArr) {
        S17_ID_TO_CN_NAME.put(Integer.valueOf(i), str);
        S17_CN_TO_EN_NAME.put(str, str2);
        if (iArr != null) {
            for (int i2 : iArr) {
                S17_FORM_TO_BASE.put(Integer.valueOf(i2), Integer.valueOf(i));
            }
        }
    }

    private static void putS8(int i, String str, String str2, int... iArr) {
        S8_ID_TO_CN_NAME.put(Integer.valueOf(i), str);
        S8_CN_TO_EN_NAME.put(str, str2);
        if (iArr != null) {
            for (int i2 : iArr) {
                S8_FORM_TO_BASE.put(Integer.valueOf(i2), Integer.valueOf(i));
            }
        }
    }

    private static void putS18(int i, String str, String str2, int... iArr) {
        S18_ID_TO_CN_NAME.put(Integer.valueOf(i), str);
        S18_CN_TO_EN_NAME.put(str, str2);
        if (iArr != null) {
            for (int i2 : iArr) {
                S18_FORM_TO_BASE.put(Integer.valueOf(i2), Integer.valueOf(i));
            }
        }
    }

    private static void initS18Data() {
        putS18(11500, "奥恩", "ornn", new int[0]);
        putS18(11501, "洛", "rakan", new int[0]);
        putS18(11502, "蕾欧娜", "leona", new int[0]);
        putS18(11503, "可酷伯", "kobuko", new int[0]);
        putS18(11504, "雷克塞", "reksai", new int[0]);
        putS18(11505, "卡蜜尔", "camille", new int[0]);
        putS18(11506, "阿卡丽", "akali", 11513);
        putS18(11507, "韦鲁斯", "varus", new int[0]);
        putS18(11508, "绯红树怪", "cinderling", new int[0]);
        putS18(11509, "霞", "xayah", new int[0]);
        putS18(11510, "卡尔玛", "karma", new int[0]);
        putS18(11511, "苍蓝哨戒", "sentry", new int[0]);
        putS18(11512, "维迦", "veigar", new int[0]);
        putS18(12505, "约里克", "yorick", new int[0]);
        putS18(12500, "峡谷迅捷蟹", "scuttlecrab", new int[0]);
        putS18(12501, "阿利斯塔", "alistar", new int[0]);
        putS18(12502, "瑟庄妮", "sejuani", new int[0]);
        putS18(12503, "慎", "shen", new int[0]);
        putS18(12504, "伊莉丝", "elise", 12515);
        putS18(12507, "暗影狼", "murkwolf", new int[0]);
        putS18(12508, "沃里克", "warwick", new int[0]);
        putS18(12509, "芸阿娜", "yunara", new int[0]);
        putS18(12510, "凯特琳", "caitlyn", new int[0]);
        putS18(12511, "乐芙兰", "leblanc", new int[0]);
        putS18(12512, "凯尔", "kayle", new int[0]);
        putS18(12513, "魔沼蛙", "gromp", 12516);
        putS18(12514, "提莫", "teemo", new int[0]);
        putS18(13500, "赫卡里姆", "hecarim", new int[0]);
        putS18(13501, "蔚", "vi", new int[0]);
        putS18(13502, "费德提克", "fiddlesticks", new int[0]);
        putS18(13503, "远古石甲虫", "krug", new int[0]);
        putS18(13504, "雷恩加尔", "rengar", new int[0]);
        putS18(13505, "卡兹克", "khazix", new int[0]);
        putS18(13506, "黛安娜", "diana", new int[0]);
        putS18(13507, "易", "masteryi", 13514);
        putS18(13508, "崔丝塔娜", "tristana", new int[0]);
        putS18(13509, "深红锋喙鸟", "raptor", new int[0]);
        putS18(13510, "阿兹尔", "azir", new int[0]);
        putS18(13511, "卡西奥佩娅", "cassiopeia", new int[0]);
        putS18(13512, "克格莫", "kogmaw", 13513);
        putS18(13515, "拉莫斯", "rammus", new int[0]);
        putS18(14500, "苍蓝雕纹魔像", "sentinel", new int[0]);
        putS18(14501, "莫甘娜", "morgana", 14514);
        putS18(14502, "莉莉娅", "lillia", new int[0]);
        putS18(14503, "阿木木", "amumu", new int[0]);
        putS18(14504, "墨菲特", "malphite", new int[0]);
        putS18(14505, "瑟提", "sett", new int[0]);
        putS18(14506, "绯红印记树怪", "brambleback", new int[0]);
        putS18(14507, "奈德丽", "nidalee", 14515);
        putS18(14508, "伊泽瑞尔", "ezreal", new int[0]);
        putS18(14509, "厄斐琉斯", "aphelios", new int[0]);
        putS18(14510, "希维尔", "sivir", new int[0]);
        putS18(14511, "阿狸", "ahri", new int[0]);
        putS18(14512, "婕拉", "zyra", new int[0]);
        putS18(14513, "索拉卡", "soraka", new int[0]);
        putS18(15450, "茂凯", "maokai", new int[0]);
        putS18(15451, "塔里克", "taric", new int[0]);
        putS18(15452, "纳尔", "gnar", 15460);
        putS18(15453, "凯南", "kennen", new int[0]);
        putS18(15454, "艾希", "ashe", new int[0]);
        putS18(15455, "德莱文", "draven", new int[0]);
        putS18(15456, "拉露恩", "alune", new int[0]);
        putS18(15457, "艾翁", "ivern", new int[0]);
        putS18(15458, "远古巨龙", "elderdragon", new int[0]);
        putS18(15459, "拉克丝", "lux_base_pc", 15461, 15462, 15463, 15464, 15465, 15466, 15467, 15468, 15469);
    }

    private static void initS8Data() {
        putS8(11152, "内瑟斯", "nasus", new int[0]);
        putS8(11153, "塞拉斯", "sylas", new int[0]);
        putS8(11154, "艾希", "ashe", new int[0]);
        putS8(11155, "加里奥", "galio", new int[0]);
        putS8(11156, "普朗克", "gangplank", new int[0]);
        putS8(11157, "拉克丝", "lux", new int[0]);
        putS8(11158, "雷克顿", "renekton", new int[0]);
        putS8(11159, "泰隆", "talon", new int[0]);
        putS8(11160, "布里茨", "blitzcrank", new int[0]);
        putS8(11162, "璐璐", "lulu", new int[0]);
        putS8(11163, "孙悟空", "monkeyking", new int[0]);
        putS8(11164, "凯尔", "kayle", new int[0]);
        putS8(11165, "波比", "poppy", 11189);
        putS8(11168, "卢锡安", "lucian", new int[0]);
        putS8(11169, "潘森", "pantheon", new int[0]);
        putS8(12143, "金克丝", "jinx", new int[0]);
        putS8(12144, "安妮", "annie", new int[0]);
        putS8(12145, "卡蜜尔", "camille", new int[0]);
        putS8(12146, "菲奥娜", "fiora", new int[0]);
        putS8(12147, "李青", "leesin", 12200);
        putS8(12148, "芮尔", "rell", new int[0]);
        putS8(12149, "希维尔", "sivir", 12157);
        putS8(12150, "亚索", "yasuo", new int[0]);
        putS8(12151, "悠米", "yuumi", new int[0]);
        putS8(12152, "墨菲特", "malphite", new int[0]);
        putS8(12153, "德莱文", "draven", new int[0]);
        putS8(12154, "蔚", "vi", new int[0]);
        putS8(12155, "伊泽瑞尔", "ezreal", 12158);
        putS8(12156, "派克", "pyke", new int[0]);
        putS8(13158, "锐雯", "riven", new int[0]);
        putS8(13159, "薇恩", "vayne", new int[0]);
        putS8(13160, "阿利斯塔", "alistar", new int[0]);
        putS8(13161, "科加斯", "chogath", new int[0]);
        putS8(13162, "娑娜", "sona", new int[0]);
        putS8(13163, "维克兹", "velkoz", new int[0]);
        putS8(13164, "佐伊", "zoe", new int[0]);
        putS8(13165, "拉莫斯", "rammus", new int[0]);
        putS8(13166, "尼菈", "nilah", new int[0]);
        putS8(13167, "赛娜", "senna", new int[0]);
        putS8(13168, "贾克斯", "jax", new int[0]);
        putS8(13169, "乐芙兰", "leblanc", new int[0]);
        putS8(13170, "卡莎", "kaisa", new int[0]);
        putS8(13171, "纳尔", "gnar", new int[0]);
        putS8(13172, "莫甘娜", "morgana", new int[0]);
        putS8(13173, "薇古丝", "vex", new int[0]);
        putS8(13174, "慎", "shen", new int[0]);
        putS8(14141, "塔莉垭", "taliyah", new int[0]);
        putS8(14142, "佛耶戈", "viego", new int[0]);
        putS8(14143, "奥瑞利安索尔", "aurelionsol", new int[0]);
        putS8(14144, "卑尔维斯", "belveth", new int[0]);
        putS8(14146, "厄运小姐", "missfortune", new int[0]);
        putS8(14147, "莎弥拉", "samira", new int[0]);
        putS8(14148, "瑟提", "sett", new int[0]);
        putS8(14149, "扎克", "zac", 18024, 18032);
        putS8(14150, "劫", "zed", new int[0]);
        putS8(14151, "索拉卡", "soraka", new int[0]);
        putS8(14152, "瑟庄妮", "sejuani", new int[0]);
        putS8(14157, "艾克", "ekko", new int[0]);
        putS8(14159, "烬", "jhin", new int[0]);
        putS8(14160, "盖伦", "garen", new int[0]);
        putS8(14161, "崔斯特", "twistedfate", new int[0]);
        putS8(14162, "沃里克", "warwick", new int[0]);
        putS8(14163, "妮蔻", "neeko", new int[0]);
        putS8(14164, "亚托克斯", "aatrox", 14165);
        putS8(15121, "努努和威朗普", "nunu", new int[0]);
        putS8(15122, "辛德拉", "syndra", new int[0]);
        putS8(15123, "迦娜", "janna", new int[0]);
        putS8(15124, "蕾欧娜", "leona", new int[0]);
        putS8(15125, "莫德凯撒", "mordekaiser", new int[0]);
        putS8(15126, "费德提克", "fiddlesticks", new int[0]);
        putS8(15127, "厄加特", "urgot", new int[0]);
        putS8(15128, "厄斐琉斯", "aphelios", 15129, 15130);
        putS8(15131, "终极伊泽瑞尔", "ezrealfinal", 15132);
    }

    public static String getChineseName(int i, int i2) {
        Map<Integer, String> map;
        Integer num;
        String str;
        String str2;
        String str3;
        if (i2 == 6) {
            return S11HeroCatalog.name(i);
        }
        if (i2 == 0) {
            map = FUX_ID_TO_CN_NAME;
        } else if (i2 == 1) {
            map = S10_ID_TO_CN_NAME;
        } else if (i2 == 2) {
            map = S16_ID_TO_CN_NAME;
        } else if (i2 == 3) {
            map = S17_ID_TO_CN_NAME;
        } else if (i2 == 4) {
            map = S8_ID_TO_CN_NAME;
        } else if (i2 == 5) {
            map = S18_ID_TO_CN_NAME;
        } else {
            map = FUX_ID_TO_CN_NAME;
        }
        String str4 = map.get(Integer.valueOf(i));
        if (str4 != null) {
            return str4;
        }
        if (i2 == 3) {
            Integer num2 = S17_FORM_TO_BASE.get(Integer.valueOf(i));
            return (num2 == null || (str3 = map.get(num2)) == null) ? "未知英雄" : str3;
        }
        if (i2 != 4) {
            return (i2 != 5 || (num = S18_FORM_TO_BASE.get(Integer.valueOf(i))) == null || (str = map.get(num)) == null) ? "未知英雄" : str;
        }
        Integer num3 = S8_FORM_TO_BASE.get(Integer.valueOf(i));
        return (num3 == null || (str2 = map.get(num3)) == null) ? "未知英雄" : str2;
    }

    public static int s17CanonicalBaseId(int i) {
        Integer num;
        return (S17_ID_TO_CN_NAME.containsKey(Integer.valueOf(i)) || (num = S17_FORM_TO_BASE.get(Integer.valueOf(i))) == null) ? i : num.intValue();
    }

    public static List<Integer> getHeroIdsByChineseName(String str) {
        ArrayList arrayList = new ArrayList();
        if (str != null && !str.trim().isEmpty()) {
            collectIdsByName(FUX_ID_TO_CN_NAME, str, arrayList);
            collectIdsByName(S10_ID_TO_CN_NAME, str, arrayList);
            collectIdsByName(S16_ID_TO_CN_NAME, str, arrayList);
            collectIdsByName(S17_ID_TO_CN_NAME, str, arrayList);
            for (Map.Entry<Integer, Integer> entry : S17_FORM_TO_BASE.entrySet()) {
                if (str.equals(S17_ID_TO_CN_NAME.get(entry.getValue()))) {
                    addUnique(arrayList, entry.getKey().intValue());
                }
            }
            collectIdsByName(S8_ID_TO_CN_NAME, str, arrayList);
            for (Map.Entry<Integer, Integer> entry2 : S8_FORM_TO_BASE.entrySet()) {
                if (str.equals(S8_ID_TO_CN_NAME.get(entry2.getValue()))) {
                    addUnique(arrayList, entry2.getKey().intValue());
                }
            }
            collectIdsByName(S18_ID_TO_CN_NAME, str, arrayList);
            collectIdsByName(S11HeroCatalog.NAMES, str, arrayList);
            for (Map.Entry<Integer, Integer> entry3 : S18_FORM_TO_BASE.entrySet()) {
                if (str.equals(S18_ID_TO_CN_NAME.get(entry3.getValue()))) {
                    addUnique(arrayList, entry3.getKey().intValue());
                }
            }
        }
        return arrayList;
    }

    public static int s18CanonicalBaseId(int i) {
        Integer num;
        return (S18_ID_TO_CN_NAME.containsKey(Integer.valueOf(i)) || (num = S18_FORM_TO_BASE.get(Integer.valueOf(i))) == null) ? i : num.intValue();
    }

    public static boolean s18IsKnownHeroId(int i) {
        return S18_ID_TO_CN_NAME.containsKey(Integer.valueOf(i)) || S18_FORM_TO_BASE.containsKey(Integer.valueOf(i));
    }

    private static void collectIdsByName(Map<Integer, String> map, String str, List<Integer> list) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (str.equals(entry.getValue())) {
                addUnique(list, entry.getKey().intValue());
            }
        }
    }

    private static void addUnique(List<Integer> list, int i) {
        if (list.contains(Integer.valueOf(i))) {
            return;
        }
        list.add(Integer.valueOf(i));
    }

    public static boolean s17IsKnownHeroId(int i) {
        return S17_ID_TO_CN_NAME.containsKey(Integer.valueOf(i)) || S17_FORM_TO_BASE.containsKey(Integer.valueOf(i));
    }

    public static String getEnglishName(String str, int i) {
        Map<String, String> map;
        if (i == 6) {
            return S11HeroCatalog.ENGLISH_NAMES.getOrDefault(str, "");
        }
        if (i == 0) {
            map = FUX_CN_TO_EN_NAME;
        } else if (i == 1) {
            map = S10_CN_TO_EN_NAME;
        } else if (i == 2) {
            map = S16_CN_TO_EN_NAME;
        } else if (i == 3) {
            map = S17_CN_TO_EN_NAME;
        } else if (i == 4) {
            map = S8_CN_TO_EN_NAME;
        } else if (i == 5) {
            map = S18_CN_TO_EN_NAME;
        } else {
            map = FUX_CN_TO_EN_NAME;
        }
        return map.getOrDefault(str, "");
    }

    public static String getEnglishNameById(int i, int i2) {
        String chineseName = getChineseName(i, i2);
        if ("未知英雄".equals(chineseName)) {
            return "";
        }
        return getEnglishName(chineseName, i2);
    }

    public static class HeroInfo {
        public String chineseName;
        public int cost;
        public String englishName;
        public int heroId;

        public HeroInfo(int i, String str, String str2) {
            this.heroId = i;
            this.chineseName = str;
            this.englishName = str2;
            while (i >= 100) {
                i /= 10;
            }
            this.cost = i % 10;
        }

        public String toString() {
            return String.format("%s(%s) [ID:%d]", this.chineseName, this.englishName, Integer.valueOf(this.heroId));
        }
    }

    public static HeroInfo getHeroInfo(int i, int i2) {
        String chineseName = getChineseName(i, i2);
        if ("未知英雄".equals(chineseName)) {
            return null;
        }
        return new HeroInfo(i, chineseName, getEnglishName(chineseName, i2));
    }

    public static String getHeroAvatarPath(int i, int i2) {
        String[] heroAvatarPaths = getHeroAvatarPaths(i, i2);
        return heroAvatarPaths.length == 0 ? "" : heroAvatarPaths[0];
    }

    static String canonicalHeroAvatarName(int i, String str) {
        if (str == null) {
            return "";
        }
        String lowerCase = str.trim().toLowerCase(Locale.ROOT);
        if (lowerCase.isEmpty()) {
            return "";
        }
        if (i == 6) {
            if ("frank".equals(lowerCase)) {
                return "kobuko";
            }
            if ("gnarbig".equals(lowerCase)) {
                return "gnar";
            }
        }
        return lowerCase;
    }

    static String[] getHeroAvatarPaths(int i, int i2) {
        String strCanonicalHeroAvatarName = canonicalHeroAvatarName(i2, getEnglishNameById(i, i2));
        if (strCanonicalHeroAvatarName.isEmpty()) {
            return new String[0];
        }
        String str = "FUX";
        switch (i2) {
            case 1:
                str = "S10";
                break;
            case 2:
                str = "S16";
                break;
            case 3:
                str = "S17";
                break;
            case 4:
                str = "S8";
                break;
            case 5:
                str = "S18";
                break;
            case 6:
                str = "S11";
                break;
        }
        if (6 == i2) {
            return new String[]{"heroes/S11/" + strCanonicalHeroAvatarName + ".png"};
        }
        return new String[]{"heroes/" + str + "/" + strCanonicalHeroAvatarName + ".png"};
    }

    public static List<HeroInfo> get5CostHeroes(int i) {
        Map<Integer, String> map;
        switch (i) {
            case 0:
                map = FUX_ID_TO_CN_NAME;
                break;
            case 1:
                map = S10_ID_TO_CN_NAME;
                break;
            case 2:
                map = S16_ID_TO_CN_NAME;
                break;
            case 3:
                map = S17_ID_TO_CN_NAME;
                break;
            case 4:
                map = S8_ID_TO_CN_NAME;
                break;
            case 5:
                map = S18_ID_TO_CN_NAME;
                break;
            case 6:
                map = S11HeroCatalog.NAMES;
                break;
            default:
                map = FUX_ID_TO_CN_NAME;
                break;
        }
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            int iIntValue = entry.getKey().intValue();
            int i2 = iIntValue;
            while (i2 >= 100) {
                i2 /= 10;
            }
            if (i2 == 15) {
                String value = entry.getValue();
                arrayList.add(new HeroInfo(iIntValue, value, getEnglishName(value, i)));
            }
        }
        return arrayList;
    }

    public static List<HeroInfo> getHeroesByCost(int i, int i2) {
        Map<Integer, String> map;
        switch (i2) {
            case 0:
                map = FUX_ID_TO_CN_NAME;
                break;
            case 1:
                map = S10_ID_TO_CN_NAME;
                break;
            case 2:
                map = S16_ID_TO_CN_NAME;
                break;
            case 3:
                map = S17_ID_TO_CN_NAME;
                break;
            case 4:
                map = S8_ID_TO_CN_NAME;
                break;
            case 5:
                map = S18_ID_TO_CN_NAME;
                break;
            case 6:
                map = S11HeroCatalog.NAMES;
                break;
            default:
                map = FUX_ID_TO_CN_NAME;
                break;
        }
        ArrayList arrayList = new ArrayList();
        int i3 = i + 10;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            int iIntValue = entry.getKey().intValue();
            int i4 = iIntValue;
            while (i4 >= 100) {
                i4 /= 10;
            }
            if (i4 == i3) {
                String value = entry.getValue();
                arrayList.add(new HeroInfo(iIntValue, value, getEnglishName(value, i2)));
            }
        }
        return arrayList;
    }
}
