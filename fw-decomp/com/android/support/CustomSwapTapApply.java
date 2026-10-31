package com.android.support;

import com.android.support.CustomSwapPairingPolicy;

/* loaded from: classes.dex */
final class CustomSwapTapApply {
    static final String REJECT_EMPTY_TOAST = "这格没棋子，换个有棋子的点";

    interface Surface {
        void addPair(int[] iArr);

        void clearSelection();

        void refreshStatRow();

        void refreshVisuals();

        void removePair(int i);

        void setSelection(int i, int i2);

        void toastEmpty();
    }

    private CustomSwapTapApply() {
    }

    /* renamed from: com.android.support.CustomSwapTapApply$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$support$CustomSwapPairingPolicy$Action;

        static {
            int[] iArr = new int[CustomSwapPairingPolicy.Action.values().length];
            $SwitchMap$com$android$support$CustomSwapPairingPolicy$Action = iArr;
            try {
                iArr[CustomSwapPairingPolicy.Action.REMOVE_PAIR.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$support$CustomSwapPairingPolicy$Action[CustomSwapPairingPolicy.Action.REJECT_EMPTY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$support$CustomSwapPairingPolicy$Action[CustomSwapPairingPolicy.Action.SELECT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$support$CustomSwapPairingPolicy$Action[CustomSwapPairingPolicy.Action.DESELECT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$support$CustomSwapPairingPolicy$Action[CustomSwapPairingPolicy.Action.ADD_PAIR.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    static void apply(Surface surface, CustomSwapPairingPolicy.Decision decision, int i, int i2) {
        int i3 = AnonymousClass1.$SwitchMap$com$android$support$CustomSwapPairingPolicy$Action[decision.action.ordinal()];
        if (i3 == 1) {
            surface.removePair(decision.pairIndex);
            surface.clearSelection();
            surface.refreshVisuals();
            surface.refreshStatRow();
            return;
        }
        if (i3 == 2) {
            surface.toastEmpty();
            return;
        }
        if (i3 == 3) {
            surface.setSelection(i, i2);
            surface.refreshVisuals();
        } else if (i3 == 4) {
            surface.clearSelection();
            surface.refreshVisuals();
        } else {
            if (i3 != 5) {
                return;
            }
            surface.addPair(decision.newPair);
            surface.clearSelection();
            surface.refreshVisuals();
            surface.refreshStatRow();
        }
    }
}
