package com.android.support;

/* loaded from: classes.dex */
public final class OpponentDisplayPolicy {

    public interface Writer {
        void write(boolean z, boolean z2);
    }

    public static boolean shouldDrawAttackIcon(boolean z, boolean z2) {
        return z && z2;
    }

    public static boolean shouldShowOpponentAnchor(boolean z, boolean z2, boolean z3) {
        if (z) {
            return z2 || z3;
        }
        return false;
    }

    public static boolean shouldShowOpponentText(boolean z, boolean z2, boolean z3) {
        return z && z2 && z3;
    }

    public static final class State {
        public final boolean attackIconEnabled;
        public final boolean highlightEnabled;

        private State(boolean z, boolean z2) {
            this.highlightEnabled = z;
            this.attackIconEnabled = z2;
        }
    }

    private OpponentDisplayPolicy() {
    }

    public static State applyAttackToggle(boolean z, boolean z2, boolean z3, Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer == null");
        }
        State state = new State(z3 || z, z3);
        writer.write(state.highlightEnabled, state.attackIconEnabled);
        return state;
    }
}
