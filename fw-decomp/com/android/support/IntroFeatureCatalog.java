package com.android.support;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
final class IntroFeatureCatalog {

    static final class Section {
        final int accent;
        final List<Feature> features = new ArrayList();
        final String title;

        Section(String str, int i) {
            this.title = str;
            this.accent = i;
        }
    }

    static final class Feature {
        final String effect;
        final String icon;
        final String name;
        final String tab;
        final int tabId;
        final String tip;

        Feature(String str, String str2, String str3, String str4, String str5, int i) {
            this.icon = str;
            this.name = str2;
            this.effect = str3;
            this.tip = str4;
            this.tab = str5;
            this.tabId = i;
        }
    }

    static List<Section> build() {
        ArrayList arrayList = new ArrayList();
        Section section = new Section("自动拿牌", GameHudPalette.CYAN);
        section.features.add(new Feature("⚡", "一键激活", "进对局后点一次，自动拿牌、海克斯预测、牌库等全部就绪", "功能页顶部「点击激活全部功能」", "功能", R.id.tabFunction));
        section.features.add(new Feature("🛒", "自动拿牌", "商店刷出目标英雄自动购买，可按 1~5 费勾选", "功能页开关 + 费用筛选", "功能", R.id.tabFunction));
        section.features.add(new Feature("🧠", "智能卡牌", "盯住对手追的三星，自动买同名卡限制其成型，可设触发张数与保留金币", "功能页「智能卡牌 ›」打开设置", "功能", R.id.tabFunction));
        section.features.add(new Feature("🌟", "购买五费天选", "刷出二星五费天选自动拿下，可指定只拿哪几张五费", "功能页「购买五费天选 ›」", "功能", R.id.tabFunction));
        section.features.add(new Feature("⭐", "追3★", "棋盘上有 2 星英雄时自动拿同名卡，按张数阈值触发", "功能页「追3★」步进器", "功能", R.id.tabFunction));
        section.features.add(new Feature("👑", "天选拿取", "按阈值自动拿天选英雄，可一键全费天选", "功能页「天选拿取阈值」", "功能", R.id.tabFunction));
        section.features.add(new Feature("🔮", "海克斯预测", "提前知道本局三次海克斯的品质（银/金/彩）与具体选项", "功能页「海克斯预测」，可常显示", "功能", R.id.tabFunction));
        section.features.add(new Feature("🔄", "自动刷新商店", "按节奏自动 D 牌，可设保底间隔与保留金币", "功能页「自动刷新」", "功能", R.id.tabFunction));
        section.features.add(new Feature("🗑", "一键卖牌", "一键卖备战席 / 卖场上，快速清仓换经济", "功能页「卖备战席 / 卖场上」", "功能", R.id.tabFunction));
        section.features.add(new Feature("🔀", "一键换位", "左右 / 上下 / 中心对称镜像换位，也可自定义多对棋子互换", "功能页「一键换位」弹小面板", "功能", R.id.tabFunction));
        section.features.add(new Feature("🚀", "一键梭哈", "自动追三星：拿牌、卖多余棋子、刷新一条龙，达成即停", "快捷栏「梭哈」，可设费用与保留金币", "功能", R.id.tabFunction));
        section.features.add(new Feature("🧩", "阵容管理", "保存 / 加载 / 切换多套阵容，A→B 一键换阵", "功能页底部「阵容管理」", "功能", R.id.tabFunction));
        arrayList.add(section);
        Section section2 = new Section("牌库与商店", -8404496);
        section2.features.add(new Feature("📚", "牌库余量", "实时显示每张牌在牌库还剩几张，谁被拿空一目了然", "牌库页，音量键可快速显隐", "牌库", R.id.tabHeroPool));
        section2.features.add(new Feature("🧍", "棋子脚下牌库", "在每个棋子头顶显示头像、我方张数与牌库余量", "牌库页「棋子脚下牌库」", "牌库", R.id.tabHeroPool));
        section2.features.add(new Feature("🏪", "商店剩余显示", "把牌库余量画进商店金币旁，买牌时不用切窗", "牌库页「商店显示」选图片版/内置版", "牌库", R.id.tabHeroPool));
        section2.features.add(new Feature("✅", "自动选中已有英雄", "牌库自动高亮你已拥有的英雄，凑对凑三星更直观", "牌库页开关", "牌库", R.id.tabHeroPool));
        arrayList.add(section2);
        Section section3 = new Section("玩家与预警", GameHudPalette.PEER_ORANGE);
        section3.features.add(new Feature("📊", "玩家数据", "实时列出八家等级、经济、血量与阵容，谁在憋什么一看便知", "玩家页「玩家数据」", "玩家", R.id.tabPlayers));
        section3.features.add(new Feature("⚔", "对手预测", "提前显示你下一轮可能遇到的对手，并给出攻击准星标记", "玩家页「对手预测」选显示模式", "玩家", R.id.tabPlayers));
        section3.features.add(new Feature("🚨", "三星预警", "有对手快三星时弹横幅 + 语音播报，可按费用筛选", "玩家页「三星预警」", "玩家", R.id.tabPlayers));
        section3.features.add(new Feature("🧊", "游戏内显示经济", "把玩家名改成冰块等级 + 琥珀金币，两套颜色一眼分清", "玩家页「游戏内显示经济」", "玩家", R.id.tabPlayers));
        section3.features.add(new Feature("🤝", "同行识别", "同一对局双方都开启时，在玩家列表互标★同行", "设置页「同行识别」", "设置", R.id.tabSettings));
        arrayList.add(section3);
        Section section4 = new Section("语音播报", GameHudPalette.DANGER);
        section4.features.add(new Feature("🔊", "语音播报", "XF 独家云端模型元气女声，关键事件开口提醒", "语音页开关 + 测试播报", "语音", R.id.tabVoice));
        section4.features.add(new Feature("🎚", "本地音效", "语速 / 音量 / 音调本地滑杆即调即听", "语音页三条滑杆", "语音", R.id.tabVoice));
        section4.features.add(new Feature("📣", "三星预警语音", "三星预警开口报名字，可同步文字横幅并调位置", "语音页「三星预警语音」", "语音", R.id.tabVoice));
        arrayList.add(section4);
        Section section5 = new Section("热键与快捷", -15770);
        section5.features.add(new Feature("⌨", "模拟器快捷键", "F1 显隐悬浮窗、F2 自动拿牌、F3 屏幕牌库，支持 F1~F12 与 A~Z 自定义", "热键页开启无障碍服务后绑定", "热键", R.id.tabHotkey));
        section5.features.add(new Feature("🕹", "快捷按钮", "屏幕常驻一排快捷钮：卖牌 / 换位 / 镜像 / 刷新 / 梭哈，位置可拖可锁", "功能页「快捷按钮」", "功能", R.id.tabFunction));
        arrayList.add(section5);
        Section section6 = new Section("单机工具", -4141862);
        section6.features.add(new Feature("💰", "属性修改", "单机模式下修改金币 / 血量 / 等级 / 人口上限", "功能页右滑「单机工具」", "功能", R.id.tabFunction));
        section6.features.add(new Feature("🎁", "英雄装备生成", "生成指定星级英雄与成装，数量可选 ×1~×100", "功能页右滑「单机工具」", "功能", R.id.tabFunction));
        section6.features.add(new Feature("🚫", "拦截弹窗", "屏蔽游戏内弹窗，操作不被打断", "单机工具页开关", "功能", R.id.tabFunction));
        section6.features.add(new Feature("📱", "iOS转区", "模拟 iOS 区服标识，用于特定对局环境", "功能页「iOS转区」", "功能", R.id.tabFunction));
        section6.features.add(new Feature("🚪", "退出对局", "一键退出当前对局，快速开下一把", "功能页「退出对局」", "功能", R.id.tabFunction));
        arrayList.add(section6);
        Section section7 = new Section("设置与共享", -4741633);
        section7.features.add(new Feature("🌐", "网页共享", "生成房间号，电脑与手机浏览器同步查看战况", "设置页「网页共享」", "设置", R.id.tabSettings));
        section7.features.add(new Feature("🛡", "防录屏/防截屏", "开启后截图与录屏里看不到辅助界面", "设置页「防录屏/防截屏」", "设置", R.id.tabSettings));
        section7.features.add(new Feature("🎨", "界面自定义", "覆盖层透明度、HUD 配色、方框位置大小全部可调", "设置页与各功能页", "设置", R.id.tabSettings));
        section7.features.add(new Feature("💾", "配置管理", "保存 / 加载 / 导出 / 导入整套配置，换机不丢", "设置页「配置管理」", "设置", R.id.tabSettings));
        section7.features.add(new Feature("🩺", "远程诊断", "生成识别码给客服，授权后远程定位问题", "设置页「远程诊断」", "设置", R.id.tabSettings));
        arrayList.add(section7);
        return arrayList;
    }

    private IntroFeatureCatalog() {
    }
}
