package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class AllInPriorityOrder {
    private static int clamp(int i, int i2, int i3) {
        return i < i2 ? i2 : i > i3 ? i3 : i;
    }

    private AllInPriorityOrder() {
    }

    static <T> void move(List<T> list, int i, int i2) {
        int size;
        int iClamp;
        int iClamp2;
        if (list == null || list.size() < 2 || (iClamp = clamp(i, 0, list.size() - 1)) == (iClamp2 = clamp(i2, 0, size))) {
            return;
        }
        list.add(iClamp2, list.remove(iClamp));
    }

    static int cellIndexAt(int i, int i2, int i3, int i4, int i5, int i6) {
        if (i5 <= 0 || i3 <= 0 || i4 <= 0 || i6 <= 0) {
            return -1;
        }
        return clamp((clamp(i2 / i4, 0, Math.max(0, (((i6 + i5) - 1) / i5) - 1)) * i5) + clamp(i / i3, 0, i5 - 1), 0, i6 - 1);
    }

    static int slotLeft(int i, int i2, int i3) {
        if (i2 <= 0 || i3 <= 0 || i < 0) {
            return 0;
        }
        return (i % i2) * i3;
    }

    static int slotTop(int i, int i2, int i3) {
        if (i2 <= 0 || i3 <= 0 || i < 0) {
            return 0;
        }
        return (i / i2) * i3;
    }
}
