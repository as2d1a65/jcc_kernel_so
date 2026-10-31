package com.android.support;

import java.util.List;

/* loaded from: classes.dex */
final class CustomSwapPairReset {
    static final int CLEARED_SELECTION = -1;

    interface Surface {
        void clearSelection();

        void refreshStatRow();

        void refreshVisuals();
    }

    private CustomSwapPairReset() {
    }

    static void apply(List<int[]> list, Surface surface) {
        list.clear();
        surface.clearSelection();
        surface.refreshVisuals();
        surface.refreshStatRow();
    }
}
