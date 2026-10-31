package com.android.support;

/* loaded from: classes.dex */
final class GameHudReadinessPolicy {

    enum Action {
        READY_IDLE,
        RESUME,
        KEEP_HIDDEN,
        PAUSE_IMMEDIATE,
        START_DEBOUNCE,
        HOLD_DEBOUNCE,
        PAUSE_AFTER_DEBOUNCE
    }

    static boolean shouldBeReady(boolean z, boolean z2, boolean z3) {
        return z ? z3 : z2;
    }

    private GameHudReadinessPolicy() {
    }

    static Action decide(boolean z, boolean z2, boolean z3, long j, long j2, long j3) {
        if (z) {
            return z2 ? Action.READY_IDLE : Action.RESUME;
        }
        if (!z2) {
            return Action.KEEP_HIDDEN;
        }
        if (!z3) {
            return Action.PAUSE_IMMEDIATE;
        }
        if (j == 0) {
            return Action.START_DEBOUNCE;
        }
        if (j2 - j >= j3) {
            return Action.PAUSE_AFTER_DEBOUNCE;
        }
        return Action.HOLD_DEBOUNCE;
    }
}
