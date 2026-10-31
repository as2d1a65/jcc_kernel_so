package com.android.support;

/* loaded from: classes.dex */
final class SpecifiedHexUiText {
    private static final String[] SLOT_MARKS = {"①", "②", "③"};

    private SpecifiedHexUiText() {
    }

    static String qualityName(int i) {
        if (i == 1) {
            return "银色";
        }
        if (i == 2) {
            return "金色";
        }
        if (i == 3) {
            return "彩色";
        }
        return "当前";
    }

    static String stageQuality(int i, int i2) {
        return "第 " + (i + 1) + " 次海克斯（" + qualityName(i2) + "）";
    }

    static String stageTag(int i, int i2) {
        return (i + 1) + "阶·" + qualityName(i2);
    }

    static String slotMark(int i) {
        if (i < 0) {
            return "?";
        }
        String[] strArr = SLOT_MARKS;
        return i < strArr.length ? strArr[i] : "?";
    }

    static String slotMarks(int i) {
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        while (true) {
            String[] strArr = SLOT_MARKS;
            if (i2 >= strArr.length) {
                return sb.toString();
            }
            if (((1 << i2) & i) != 0) {
                sb.append(strArr[i2]);
            }
            i2++;
        }
    }

    static String noApplicableTargets(int i, int i2, boolean z, boolean z2) {
        if (!z) {
            return "未选目标 · 不会刷新";
        }
        if (!z2) {
            return "请选" + qualityName(i2) + "目标";
        }
        return "已选目标本次不会出现";
    }

    static String ready(int i, int i2, int i3) {
        return (i + 1) + "阶已选" + i3 + "个·等开牌";
    }

    static String monitoring(int i, int i2) {
        return "预测中 · 等目标出现";
    }

    static String preparing(int i, int i2) {
        return "已开牌 · 读取 3 格";
    }

    static String switchingStage(int i, int i2) {
        return "切换阶段 · 请稍候";
    }

    static String refreshingSlot(int i, int i2, int i3, int i4, int i5) {
        return slotMark(i3) + " 命中 · 刷新中";
    }

    static String progress(int i, int i2, int i3, int i4) {
        int iBitCount = Integer.bitCount(i3 & 7);
        int iBitCount2 = Integer.bitCount(i4 & 7);
        int iBitCount3 = Integer.bitCount((i3 | i4) & 7);
        if (iBitCount >= 3) {
            return "①②③ 全部完成";
        }
        if (iBitCount3 >= 3) {
            if (iBitCount == 0) {
                return "刷新次数已用完";
            }
            return "已就位 " + slotMarks(i3) + " · 等待选择";
        }
        if (iBitCount == 0) {
            if (iBitCount2 > 0) {
                return slotMarks(i4) + " 无次数 · 预测中";
            }
            return "预测中 · 等目标出现";
        }
        if (iBitCount2 > 0) {
            return "已完成 " + slotMarks(i3) + " · " + slotMarks(i4) + " 无次数";
        }
        return "已完成 " + slotMarks(i3) + " · 继续预测";
    }
}
