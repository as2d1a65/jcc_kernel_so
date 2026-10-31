package com.android.support;

import androidx.core.view.PointerIconCompat;
import com.android.support.EquipmentIdMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.internal.ws.WebSocketProtocol;

/* loaded from: classes.dex */
public class EquipmentIdMapper {
    public static final String CAT_BASIC = "基础散件";
    public static final String CAT_COMBINED = "成型装备";
    public static final String CAT_CROWN = "冠冕";
    public static final String CAT_EMBLEM = "纹章";
    public static final String CAT_SUPPORT = "辅助装备";
    public static final String CAT_ARTIFACT = "神器装备";
    public static final String CAT_RADIANT = "光明装备";
    public static final String CAT_DARKIN = "暗裔装备";
    public static final String CAT_FORGE = "锻造器";
    public static final String CAT_CONSUMABLE = "消耗道具";
    public static final String CAT_SPECIAL = "特殊装备";
    public static final String[] CATEGORIES = {CAT_BASIC, CAT_COMBINED, CAT_CROWN, CAT_EMBLEM, CAT_SUPPORT, CAT_ARTIFACT, CAT_RADIANT, CAT_DARKIN, CAT_FORGE, CAT_CONSUMABLE, CAT_SPECIAL};
    private static final Map<Integer, EquipInfo> DATA = new HashMap();

    public static class EquipInfo {
        public final String category;
        public final int id;
        public final String imageName;
        public final String name;

        EquipInfo(int i, String str, String str2, String str3) {
            this.id = i;
            this.name = str;
            this.imageName = str2;
            this.category = str3;
        }
    }

    static {
        initData();
    }

    public static int getCategoryColor(String str) {
        if (CAT_BASIC.equals(str)) {
            return -7631989;
        }
        if (CAT_COMBINED.equals(str)) {
            return -14575885;
        }
        if (CAT_CROWN.equals(str)) {
            return -10496;
        }
        if (CAT_RADIANT.equals(str)) {
            return -22016;
        }
        if (CAT_EMBLEM.equals(str)) {
            return -6543440;
        }
        if (CAT_SUPPORT.equals(str)) {
            return -11751600;
        }
        if (CAT_ARTIFACT.equals(str)) {
            return -43230;
        }
        if (CAT_DARKIN.equals(str)) {
            return -4776932;
        }
        if (CAT_FORGE.equals(str)) {
            return -10453621;
        }
        if (CAT_CONSUMABLE.equals(str)) {
            return -8825528;
        }
        return CAT_SPECIAL.equals(str) ? -26624 : -1;
    }

    private static void add(int i, String str, String str2, String str3) {
        DATA.put(Integer.valueOf(i), new EquipInfo(i, str, str2, str3));
    }

    private static void initData() {
        add(1001, "暴风之剑", "1001", CAT_BASIC);
        add(PointerIconCompat.TYPE_HAND, "反曲之弓", "1002", CAT_BASIC);
        add(PointerIconCompat.TYPE_HELP, "无用大棒", "1003", CAT_BASIC);
        add(PointerIconCompat.TYPE_WAIT, "女神之泪", "1004", CAT_BASIC);
        add(WebSocketProtocol.CLOSE_NO_STATUS_CODE, "锁子甲", "1005", CAT_BASIC);
        add(PointerIconCompat.TYPE_CELL, "负极斗篷", "1006", CAT_BASIC);
        add(PointerIconCompat.TYPE_CROSSHAIR, "巨人腰带", "1007", CAT_BASIC);
        add(PointerIconCompat.TYPE_TEXT, "金铲铲", "1008", CAT_BASIC);
        add(PointerIconCompat.TYPE_VERTICAL_TEXT, "拳套", "1009", CAT_BASIC);
        add(PointerIconCompat.TYPE_ALIAS, "金锅锅", "frying_pan", CAT_BASIC);
        add(2001, "无尽之刃", "2001", CAT_COMBINED);
        add(2003, "海克斯科技枪刃", "2003", CAT_COMBINED);
        add(2004, "朔极之矛", "2004", CAT_COMBINED);
        add(2005, "夜之锋刃", "2005", CAT_COMBINED);
        add(2006, "汲取剑", "2006", CAT_COMBINED);
        add(2007, "斯特拉克的挑战护手", "steraksgage", CAT_COMBINED);
        add(2009, "红霸符", "2009", CAT_COMBINED);
        add(2010, "鬼索的狂暴之刃", "2010", CAT_COMBINED);
        add(2011, "虚空之杖", "voidstaff", CAT_COMBINED);
        add(2012, "泰坦的坚决", "2012", CAT_COMBINED);
        add(2013, "海妖之怒", "krakensfury", CAT_COMBINED);
        add(2014, "纳什之牙", "nashorstooth", CAT_COMBINED);
        add(2016, "班克斯的魔法帽", "2016", CAT_COMBINED);
        add(2017, "大天使之杖", "2017", CAT_COMBINED);
        add(2018, "冕卫", "crownguard", CAT_COMBINED);
        add(2019, "离子火花", "2019", CAT_COMBINED);
        add(2020, "莫雷洛秘典", "2020", CAT_COMBINED);
        add(2022, "蓝霸符", "2022", CAT_COMBINED);
        add(2023, "圣盾使的誓约", "2023", CAT_COMBINED);
        add(2024, "适应性头盔", "adaptivehelm", CAT_COMBINED);
        add(2025, "振奋盔甲", "spiritvisage", CAT_COMBINED);
        add(2027, "棘刺背心", "2027", CAT_COMBINED);
        add(2028, "石像鬼石板甲", "2028", CAT_COMBINED);
        add(2029, "日炎斗篷", "2029", CAT_COMBINED);
        add(2031, "巨龙之爪", "2031", CAT_COMBINED);
        add(2032, "薄暮法袍", "evenshroud", CAT_COMBINED);
        add(2034, "狂徒铠甲", "2034", CAT_COMBINED);
        add(2037, "最后的轻语", "2037", CAT_COMBINED);
        add(2038, "珠光护手", "2038", CAT_COMBINED);
        add(2039, "正义之手", "2039", CAT_COMBINED);
        add(2040, "坚定之心", "2040", CAT_COMBINED);
        add(2041, "水银", "2041", CAT_COMBINED);
        add(2042, "强袭者的链枷", "strikersflail", CAT_COMBINED);
        add(2044, "秘法手套", "2044", CAT_COMBINED);
        add(2045, "锐利之刃", "2045", CAT_COMBINED);
        add(2046, "巨人捕手", "2046", CAT_COMBINED);
        add(2036, "金铲铲冠冕", "2036", CAT_CROWN);
        add(2047, "金锅锅冠冕", "2047", CAT_CROWN);
        add(2048, "金锅铲冠冕", "2048", CAT_CROWN);
        add(2059, "光明版无尽之刃", "2059", CAT_RADIANT);
        add(2060, "光明版海克斯科技枪刃", "2060", CAT_RADIANT);
        add(2061, "光明版朔极之矛", "2061", CAT_RADIANT);
        add(2062, "光明版夜之锋刃", "2062", CAT_RADIANT);
        add(2063, "光明版汲取剑", "2063", CAT_RADIANT);
        add(2064, "光明版斯特拉克的挑战护手", "2064", CAT_RADIANT);
        add(2065, "光明版红霸符", "2065", CAT_RADIANT);
        add(2066, "光明版鬼索的狂暴之刃", "2066", CAT_RADIANT);
        add(2067, "光明版虚空之杖", "2067", CAT_RADIANT);
        add(2068, "光明版泰坦的坚决", "2068", CAT_RADIANT);
        add(2069, "光明版海妖之怒", "2069", CAT_RADIANT);
        add(2070, "光明版纳什之牙", "2070", CAT_RADIANT);
        add(2071, "光明版班克斯的魔法帽", "2071", CAT_RADIANT);
        add(2072, "光明版大天使之杖", "2072", CAT_RADIANT);
        add(2073, "光明版冕卫", "2073", CAT_RADIANT);
        add(2074, "光明版离子火花", "2074", CAT_RADIANT);
        add(2075, "光明版莫雷洛秘典", "2075", CAT_RADIANT);
        add(2076, "光明版蓝霸符", "2076", CAT_RADIANT);
        add(2077, "光明版圣盾使的誓约", "2077", CAT_RADIANT);
        add(2078, "光明版适应性头盔", "2078", CAT_RADIANT);
        add(2079, "光明版振奋盔甲", "2079", CAT_RADIANT);
        add(2080, "光明版棘刺背心", "2080", CAT_RADIANT);
        add(2081, "光明版石像鬼石板甲", "2081", CAT_RADIANT);
        add(2082, "光明版日炎斗篷", "2082", CAT_RADIANT);
        add(2083, "光明版巨龙之爪", "2083", CAT_RADIANT);
        add(2084, "光明版薄暮法袍", "2084", CAT_RADIANT);
        add(2085, "光明版狂徒铠甲", "2085", CAT_RADIANT);
        add(2086, "光明版最后的轻语", "2086", CAT_RADIANT);
        add(2087, "光明版珠光护手", "2087", CAT_RADIANT);
        add(2088, "光明版正义之手", "2088", CAT_RADIANT);
        add(2089, "光明版坚定之心", "2089", CAT_RADIANT);
        add(2090, "光明版水银", "2090", CAT_RADIANT);
        add(2091, "光明版强袭者的链枷", "2091", CAT_RADIANT);
        add(2092, "光明版秘法手套", "2092", CAT_RADIANT);
        add(2093, "光明版锐利之刃", "2093", CAT_RADIANT);
        add(2094, "光明版巨人捕手", "2094", CAT_RADIANT);
        add(4401, "天神纹章", "4401", CAT_EMBLEM);
        add(4402, "决斗大师纹章", "4402", CAT_EMBLEM);
        add(4404, "永恒之森纹章", "4404", CAT_EMBLEM);
        add(4405, "魔法师纹章", "4405", CAT_EMBLEM);
        add(4406, "重装战士纹章", "4406", CAT_EMBLEM);
        add(4407, "三国猛将纹章", "4407", CAT_EMBLEM);
        add(4408, "刺客纹章", "4408", CAT_EMBLEM);
        add(4409, "腥红之月纹章", "4409", CAT_EMBLEM);
        add(4410, "玉剑仙纹章", "4410", CAT_EMBLEM);
        add(4412, "福星纹章", "4412", CAT_EMBLEM);
        add(4415, "灵魂莲华明昼纹章", "4415", CAT_EMBLEM);
        add(4419, "宗师纹章", "4419", CAT_EMBLEM);
        add(4420, "斗士纹章", "4420", CAT_EMBLEM);
        add(4424, "神盾使纹章", "4424", CAT_EMBLEM);
        add(4425, "秘术师纹章", "4425", CAT_EMBLEM);
        add(4428, "龙魂纹章", "4428", CAT_EMBLEM);
        add(4429, "裁决使纹章", "4429", CAT_EMBLEM);
        add(4430, "战神纹章", "4430", CAT_EMBLEM);
        add(4431, "摄魂使纹章", "4431", CAT_EMBLEM);
        add(41801, "永恒之森纹章", "41801", CAT_EMBLEM);
        add(41802, "黑荆棘纹章", "41802", CAT_EMBLEM);
        add(41803, "魔女纹章", "41803", CAT_EMBLEM);
        add(41804, "花仙子纹章", "41804", CAT_EMBLEM);
        add(41805, "约德尔人和朋友纹章", "41805", CAT_EMBLEM);
        add(41806, "地狱火纹章", "41806", CAT_EMBLEM);
        add(41807, "野兽之灵纹章", "41807", CAT_EMBLEM);
        add(41808, "月蚀骑士纹章", "41808", CAT_EMBLEM);
        add(41809, "灵魂莲华纹章", "41809", CAT_EMBLEM);
        add(41810, "绝命花妖纹章", "41810", CAT_EMBLEM);
        add(41811, "斗士纹章", "41811", CAT_EMBLEM);
        add(41812, "护卫纹章", "41812", CAT_EMBLEM);
        add(41813, "重装战士纹章", "41813", CAT_EMBLEM);
        add(41814, "主宰纹章", "41814", CAT_EMBLEM);
        add(41815, "迅捷射手纹章", "41815", CAT_EMBLEM);
        add(41816, "猎人纹章", "41816", CAT_EMBLEM);
        add(41817, "狂战士纹章", "41817", CAT_EMBLEM);
        add(41818, "裁决使纹章", "41818", CAT_EMBLEM);
        add(41819, "法师纹章", "41819", CAT_EMBLEM);
        add(41820, "神谕纹章", "41820", CAT_EMBLEM);
        add(41821, "绝命花妖纹章", "41821", CAT_EMBLEM);
        add(6001, "基克的先驱", "6001", CAT_SUPPORT);
        add(6002, "兰顿之兆", "6002", CAT_SUPPORT);
        add(6003, "能量圣杯", "6003", CAT_SUPPORT);
        add(6004, "静止法衣", "6004", CAT_SUPPORT);
        add(6005, "兹若特传送门", "6005", CAT_SUPPORT);
        add(6006, "钢铁烈阳之匣", "6006", CAT_SUPPORT);
        add(6007, "灵风", "6007", CAT_SUPPORT);
        add(6008, "黑曜石切割者", "6008", CAT_SUPPORT);
        add(6009, "军团圣盾", "aegis", CAT_SUPPORT);
        add(6010, "殉道美德", "virtueofthemartyr", CAT_SUPPORT);
        add(6011, "女妖面纱", "bansheesveil", CAT_SUPPORT);
        add(6012, "无用大宝石", "6012", CAT_SUPPORT);
        add(6013, "辅助手套", "6013", CAT_SUPPORT);
        add(6014, "永恒烈焰", "the_eternal_flame", CAT_SUPPORT);
        add(6015, "骑士之誓", "knights_vow", CAT_SUPPORT);
        add(6016, "月石再生器", "moonstone_renewer", CAT_SUPPORT);
        add(6018, "不稳定的财宝箱", "unstable_treasure_chest", CAT_SUPPORT);
        add(6052, "大亨之铠", "6052", CAT_ARTIFACT);
        add(6054, "死亡之蔑", "6054", CAT_ARTIFACT);
        add(6057, "三相之力", "6057", CAT_ARTIFACT);
        add(6058, "魔蕴", "3604", CAT_ARTIFACT);
        add(6059, "金币收集者", "6059", CAT_ARTIFACT);
        add(6060, "中娅悖论", "6060", CAT_ARTIFACT);
        add(6061, "碎舰者", "6061", CAT_ARTIFACT);
        add(6063, "狙击手的专注", "6063", CAT_ARTIFACT);
        add(6065, "投机者之刃", "6065", CAT_ARTIFACT);
        add(6067, "飞升护符", "talisman_of_ascension", CAT_ARTIFACT);
        add(6069, "鱼骨头", "fishbones", CAT_ARTIFACT);
        add(6070, "连指手套", "mittens", CAT_ARTIFACT);
        add(6071, "卢登的激荡", "6071", CAT_ARTIFACT);
        add(6072, "密银黎明", "silvermere_dawn", CAT_ARTIFACT);
        add(6073, "暗行者之爪", "chonccsprowlersclaw", CAT_ARTIFACT);
        add(6075, "智慧末刃", "wit_s_end", CAT_ARTIFACT);
        add(6076, "禁忌雕像", "forbidden_idol", CAT_ARTIFACT);
        add(6079, "视界专注", "horizon_focus", CAT_ARTIFACT);
        add(6080, "疾射火炮", "rapid_firecannon", CAT_ARTIFACT);
        add(6083, "枯萎珠宝", "blighting_jewel", CAT_ARTIFACT);
        add(6084, "探索者的护臂", "seeker_s_armguard", CAT_ARTIFACT);
        add(6085, "光盾徽章", "lightshield_crest", CAT_ARTIFACT);
        add(6086, "巫妖之祸", "lich_bane", CAT_ARTIFACT);
        add(6087, "纳沃利烁刃", "flickerblade", CAT_ARTIFACT);
        add(6088, "斯塔缇克电刃", "statikkshiv", CAT_ARTIFACT);
        add(6089, "顽强不屈", "theindomitable", CAT_ARTIFACT);
        add(6090, "巨型九头蛇", "titanichydra", CAT_ARTIFACT);
        add(6091, "黎明核心", "dawncore", CAT_ARTIFACT);
        add(6092, "黎明圣盾", "aegisofdawn", CAT_ARTIFACT);
        add(6093, "黄昏圣盾", "aegisofdusk", CAT_ARTIFACT);
        add(6094, "虚空护手", "voidgauntlet", CAT_ARTIFACT);
        add(6095, "帽子饮品", "cappajuice", CAT_ARTIFACT);
        add(6096, "恶火小斧", "hellfirehatchet", CAT_ARTIFACT);
        add(6098, "永恒契约", "eternalpact", CAT_ARTIFACT);
        add(6103, "金猪钱罐", "6103", CAT_ARTIFACT);
        add(3406, "拉亚斯特的拥抱", "darkinstaff", CAT_DARKIN);
        add(3407, "凯隐的解脱", "darkinscythe", CAT_DARKIN);
        add(4501, "压岁钱", null, CAT_DARKIN);
        add(9982, "一个轻食点心", "9982", CAT_DARKIN);
        add(3131, "不完美的灵魂王冠", "3131", CAT_DARKIN);
        add(3132, "灵魂王冠", "3132", CAT_DARKIN);
        add(3126, "次级忠诚符文", null, CAT_FORGE);
        add(3127, "忠诚符文", null, CAT_FORGE);
        add(3130, "命运骰子", null, CAT_FORGE);
        add(3005, "纹章之书", "3005", CAT_FORGE);
        add(3007, "基础装备锻造器", "3007", CAT_FORGE);
        add(3008, "成装锻造器", "3008", CAT_FORGE);
        add(3009, "神器装备锻造器", "3009", CAT_FORGE);
        add(3015, "辅助装锻造器", "3015", CAT_FORGE);
        add(3016, "免费刷新道具", "3016", CAT_FORGE);
        add(3017, "五费刷新道具", "3017", CAT_FORGE);
        add(3018, "推荐装备选择器", null, CAT_FORGE);
        add(3021, "战利品", "3021", CAT_FORGE);
        add(3022, "传承之物", null, CAT_FORGE);
        add(3023, "皮城装备锻造器", null, CAT_FORGE);
        add(3024, "祖安装备锻造器", null, CAT_FORGE);
        add(3025, "古代装备锻造器", null, CAT_FORGE);
        add(3029, "幸运装备宝箱", "3029", CAT_FORGE);
        add(3030, "光明版幸运装备宝箱", null, CAT_FORGE);
        add(3031, "金币神器装备锻造器", "3031", CAT_FORGE);
        add(2058, "目标假人", "2058", CAT_CONSUMABLE);
        add(3001, "英雄复制器", "3001", CAT_CONSUMABLE);
        add(3002, "装备重铸器", "3002", CAT_CONSUMABLE);
        add(3003, "装备拆卸器", "3003", CAT_CONSUMABLE);
        add(3004, "灌铅骰子", "3004", CAT_CONSUMABLE);
        add(3010, "次级英雄复制器", "3010", CAT_CONSUMABLE);
        add(3014, "商店回溯器", "3014", CAT_CONSUMABLE);
        add(3019, "金制拆卸器", "3019", CAT_CONSUMABLE);
        add(3020, "杰作升级", "3020", CAT_CONSUMABLE);
        add(3028, "打捞术", "3028", CAT_CONSUMABLE);
        add(9159, "成长型投资", "9159", CAT_CONSUMABLE);
        add(9160, "植入：体型", null, CAT_CONSUMABLE);
        add(9161, "植入：速度", null, CAT_CONSUMABLE);
        add(9436, "生命值补剂", "healthbooster", CAT_CONSUMABLE);
        add(9437, "护甲和魔抗补剂", "armormrbooster", CAT_CONSUMABLE);
        add(9438, "法术加成补剂", "abilitybooster", CAT_CONSUMABLE);
        add(9439, "法力回复补剂", "manaregenbooster", CAT_CONSUMABLE);
        add(9450, "物理加成补剂", "attackbooster", CAT_CONSUMABLE);
        add(9451, "攻击速度补剂", "speedbooster", CAT_CONSUMABLE);
        add(9045, "巨人腰带", "1007", CAT_SPECIAL);
        add(9080, "次级微光复制器", "3010", CAT_SPECIAL);
        add(9081, "微光复制器", "3001", CAT_SPECIAL);
        add(9103, "被诅咒的鬼索的狂暴之刃", "9103", CAT_SPECIAL);
        add(9104, "被诅咒的海克斯科技枪刃", "9104", CAT_SPECIAL);
        add(9105, "被诅咒的水银", "9105", CAT_SPECIAL);
        add(9885, "召唤蜂拥", null, CAT_SPECIAL);
        add(9167, "德玛西亚王冠", "9167", CAT_SPECIAL);
        add(9232, "红包", null, CAT_SPECIAL);
        add(9233, "红包", null, CAT_SPECIAL);
        add(9234, "红包", null, CAT_SPECIAL);
        add(9781, "备用零件", "9781", CAT_SPECIAL);
        add(9791, "画龙点睛！", null, CAT_SPECIAL);
        add(9801, "微型维修机器人", "9801", CAT_SPECIAL);
        add(9811, "禁魔杵", "9811", CAT_SPECIAL);
        add(9821, "坦克克星", "9821", CAT_SPECIAL);
        add(9831, "喷火器", "9831", CAT_SPECIAL);
        add(9841, "自毁按钮", "9841", CAT_SPECIAL);
        add(9851, "机械重组！", null, CAT_SPECIAL);
        add(3026, "魔像军团", null, CAT_SPECIAL);
        add(3133, "火箭助推铁拳", "3133", CAT_SPECIAL);
        add(3134, "暗行者之爪", "darkinbow", CAT_SPECIAL);
        add(9452, "灵龟赐福", "s18_primal_turtle", CAT_SPECIAL);
        add(9453, "蛮熊赐福", "s18_primal_bear", CAT_SPECIAL);
        add(9454, "猛虎赐福", "s18_primal_tiger", CAT_SPECIAL);
        add(9455, "凤凰赐福", "s18_primal_phoenix", CAT_SPECIAL);
        add(9456, "阿尔法印记", "alphamark", CAT_SPECIAL);
        add(9464, "生命药水", "healthpotion", CAT_SPECIAL);
        add(9465, "光明版生命药水", "healthpotion_r", CAT_SPECIAL);
        add(9466, "爆破药水", "blastpotion", CAT_SPECIAL);
        add(9467, "光明版爆破药水", "blastpotion_r", CAT_SPECIAL);
        add(9468, "法力药水", "manapotion", CAT_SPECIAL);
        add(9469, "光明版法力药水", "manapotion_r", CAT_SPECIAL);
        add(9470, "随机基础装备", "s18_randomequipment", CAT_SPECIAL);
        add(9471, "随机成装", "s18_randomequipment", CAT_SPECIAL);
        add(9472, "随机纹章", "s18_randomequipment", CAT_SPECIAL);
    }

    public static List<EquipInfo> getAll() {
        ArrayList arrayList = new ArrayList(DATA.values());
        Collections.sort(arrayList, new Comparator() { // from class: com.android.support.EquipmentIdMapper$$ExternalSyntheticLambda1
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return Integer.compare(((EquipmentIdMapper.EquipInfo) obj).id, ((EquipmentIdMapper.EquipInfo) obj2).id);
            }
        });
        return Collections.unmodifiableList(arrayList);
    }

    public static EquipInfo getById(int i) {
        return DATA.get(Integer.valueOf(i));
    }

    public static String getName(int i) {
        EquipInfo equipInfo = DATA.get(Integer.valueOf(i));
        return equipInfo != null ? equipInfo.name : "未知装备";
    }

    public static String getImageName(int i) {
        EquipInfo equipInfo = DATA.get(Integer.valueOf(i));
        if (equipInfo != null) {
            return equipInfo.imageName;
        }
        return null;
    }

    public static Map<String, List<EquipInfo>> getAllByCategory() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (String str : CATEGORIES) {
            linkedHashMap.put(str, new ArrayList());
        }
        for (EquipInfo equipInfo : DATA.values()) {
            List list = (List) linkedHashMap.get(equipInfo.category);
            if (list != null) {
                list.add(equipInfo);
            }
        }
        Iterator it = linkedHashMap.values().iterator();
        while (it.hasNext()) {
            Collections.sort((List) it.next(), new Comparator() { // from class: com.android.support.EquipmentIdMapper$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return Integer.compare(((EquipmentIdMapper.EquipInfo) obj).id, ((EquipmentIdMapper.EquipInfo) obj2).id);
                }
            });
        }
        Iterator it2 = linkedHashMap.entrySet().iterator();
        while (it2.hasNext()) {
            if (((List) ((Map.Entry) it2.next()).getValue()).isEmpty()) {
                it2.remove();
            }
        }
        return linkedHashMap;
    }

    public static List<EquipInfo> getByCategory(String str) {
        ArrayList arrayList = new ArrayList();
        for (EquipInfo equipInfo : DATA.values()) {
            if (equipInfo.category.equals(str)) {
                arrayList.add(equipInfo);
            }
        }
        Collections.sort(arrayList, new Comparator() { // from class: com.android.support.EquipmentIdMapper$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return Integer.compare(((EquipmentIdMapper.EquipInfo) obj).id, ((EquipmentIdMapper.EquipInfo) obj2).id);
            }
        });
        return arrayList;
    }

    public static int getTotalCount() {
        return DATA.size();
    }
}
