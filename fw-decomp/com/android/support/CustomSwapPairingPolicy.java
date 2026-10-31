package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class CustomSwapPairingPolicy {

    enum Action {
        REMOVE_PAIR,
        REJECT_EMPTY,
        SELECT,
        DESELECT,
        ADD_PAIR
    }

    static final class Decision {
        final Action action;
        final int[] newPair;
        final int pairIndex;

        Decision(Action action, int i, int[] iArr) {
            this.action = action;
            this.pairIndex = i;
            this.newPair = iArr;
        }
    }

    static final class Badge {
        final int pairIndex;
        final boolean source;

        Badge(int i, boolean z) {
            this.pairIndex = i;
            this.source = z;
        }
    }

    private CustomSwapPairingPolicy() {
    }

    static boolean isValidPair(int[] iArr) {
        return iArr != null && iArr.length >= 4;
    }

    static int findPairIndex(List<int[]> list, int i, int i2) {
        if (list == null) {
            return -1;
        }
        for (int i3 = 0; i3 < list.size(); i3++) {
            int[] iArr = list.get(i3);
            if (isValidPair(iArr) && ((iArr[0] == i && iArr[1] == i2) || (iArr[2] == i && iArr[3] == i2))) {
                return i3;
            }
        }
        return -1;
    }

    static Decision decideTap(List<int[]> list, int i, int i2, int i3, int i4, int i5) {
        int iFindPairIndex = findPairIndex(list, i3, i4);
        if (iFindPairIndex >= 0) {
            return new Decision(Action.REMOVE_PAIR, iFindPairIndex, null);
        }
        if (i < 0) {
            if (i5 <= 0) {
                return new Decision(Action.REJECT_EMPTY, -1, null);
            }
            return new Decision(Action.SELECT, -1, null);
        }
        if (i == i3 && i2 == i4) {
            return new Decision(Action.DESELECT, -1, null);
        }
        return new Decision(Action.ADD_PAIR, -1, new int[]{i, i2, i3, i4});
    }

    static Badge badgeAt(List<int[]> list, int i, int i2) {
        if (list == null) {
            return null;
        }
        for (int i3 = 0; i3 < list.size(); i3++) {
            int[] iArr = list.get(i3);
            if (isValidPair(iArr)) {
                if (iArr[0] == i && iArr[1] == i2) {
                    return new Badge(i3, true);
                }
                if (iArr[2] == i && iArr[3] == i2) {
                    return new Badge(i3, false);
                }
            }
        }
        return null;
    }

    static String badgeText(Badge badge) {
        if (badge == null || badge.pairIndex < 0) {
            return "";
        }
        if (badge.source) {
            return String.valueOf(badge.pairIndex + 1);
        }
        return String.valueOf((char) (badge.pairIndex + 65));
    }

    static String chipLabel(int i, int[] iArr) {
        if (!isValidPair(iArr)) {
            return "";
        }
        return String.format("%d) (%d,%d)→(%d,%d)", Integer.valueOf(i + 1), Integer.valueOf(iArr[0] + 1), Integer.valueOf(iArr[1] + 1), Integer.valueOf(iArr[2] + 1), Integer.valueOf(iArr[3] + 1));
    }
}
