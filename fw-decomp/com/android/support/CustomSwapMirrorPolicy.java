package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/* loaded from: classes.dex */
public final class CustomSwapMirrorPolicy {
    public static final int AXIS_HORIZONTAL = 0;
    public static final int AXIS_SHUFFLE = 2;
    public static final int AXIS_VERTICAL = 1;

    private CustomSwapMirrorPolicy() {
    }

    public static List<int[]> collectMatchedCells(BoardPositionInfo boardPositionInfo, int i) {
        ArrayList arrayList = new ArrayList();
        if (boardPositionInfo != null) {
            for (int i2 = 0; i2 < 4; i2++) {
                for (int i3 = 0; i3 < 7; i3++) {
                    if (boardPositionInfo.isBoardOccupied(i2, i3)) {
                        int boardHeroId = boardPositionInfo.getBoardHeroId(i2, i3);
                        if (i <= 0 || i > 5 || HeroIdSetCodec.getCostFromHeroId(boardHeroId) == i) {
                            arrayList.add(new int[]{i3, i2});
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public static String emptyMatchToast(int i) {
        if (i > 0 && i <= 5) {
            return "场上没有 " + i + " 费英雄";
        }
        return "场上没有英雄";
    }

    public static List<int[]> buildPairs(int i, List<int[]> list) {
        return buildPairs(i, list, null);
    }

    public static List<int[]> buildPairs(int i, List<int[]> list, Random random) {
        ArrayList arrayList = new ArrayList();
        if (list != null && !list.isEmpty()) {
            if (i == 0 || i == 1) {
                HashSet hashSet = new HashSet();
                for (int[] iArr : list) {
                    if (iArr != null && iArr.length >= 2) {
                        int i2 = iArr[0];
                        int i3 = iArr[1];
                        int i4 = i == 0 ? 6 - i2 : i2;
                        int i5 = i == 1 ? 3 - i3 : i3;
                        if (i2 != i4 || i3 != i5) {
                            String str = i2 + "," + i3 + "->" + i4 + "," + i5;
                            String str2 = i4 + "," + i5 + "->" + i2 + "," + i3;
                            if (!hashSet.contains(str) && !hashSet.contains(str2)) {
                                hashSet.add(str);
                                arrayList.add(new int[]{i2, i3, i4, i5});
                            }
                        }
                    }
                }
            } else {
                ArrayList arrayList2 = new ArrayList(list);
                if (random != null) {
                    Collections.shuffle(arrayList2, random);
                } else {
                    Collections.shuffle(arrayList2);
                }
                int i6 = 0;
                while (true) {
                    int i7 = i6 + 1;
                    if (i7 >= arrayList2.size()) {
                        break;
                    }
                    int[] iArr2 = (int[]) arrayList2.get(i6);
                    int[] iArr3 = (int[]) arrayList2.get(i7);
                    if (iArr2 != null && iArr3 != null && iArr2.length >= 2 && iArr3.length >= 2) {
                        arrayList.add(new int[]{iArr2[0], iArr2[1], iArr3[0], iArr3[1]});
                    }
                    i6 += 2;
                }
            }
        }
        return arrayList;
    }

    public static String successFeeLabel(int i) {
        return (i <= 0 || i > 5) ? "全部" : i + "费";
    }

    public static String emptyPairsToast(int i, int i2) {
        if (i == 2) {
            return "可乱换的棋子不足两枚";
        }
        if (i2 > 0 && i2 <= 5) {
            return "符合 " + i2 + " 费的棋子已经在镜像位置上了";
        }
        return "这些棋子已经在镜像位置上了";
    }
}
