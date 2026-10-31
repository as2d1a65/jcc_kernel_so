package com.android.support;

/* loaded from: classes.dex */
final class OpponentTextApplyPolicy {

    static final class Decision {
        final boolean clearPairs;
        final int nextLastTextOpponentId;
        final int renderOpponentId;
        final boolean update;

        Decision(boolean z, int i, boolean z2, int i2) {
            this.update = z;
            this.renderOpponentId = i;
            this.clearPairs = z2;
            this.nextLastTextOpponentId = i2;
        }
    }

    private OpponentTextApplyPolicy() {
    }

    static Decision decide(boolean z, int i, boolean z2, int i2) {
        return decide(z, i, z2, i2, true);
    }

    static Decision decide(boolean z, int i, boolean z2, int i2, boolean z3) {
        if (!z) {
            return new Decision(false, -1, true, i2);
        }
        if (!z3) {
            return new Decision(false, -1, true, i2);
        }
        if (i >= 0) {
            return new Decision(true, i, false, i);
        }
        if (!z2 || i2 >= 0) {
            return new Decision(true, -1, true, -1);
        }
        return new Decision(true, -1, true, i2);
    }
}
