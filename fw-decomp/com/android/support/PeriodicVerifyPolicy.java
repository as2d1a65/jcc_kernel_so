package com.android.support;

/* loaded from: classes.dex */
final class PeriodicVerifyPolicy {
    static final int CONFIRMATIONS_REQUIRED = 2;
    static final double JITTER_RATIO = 0.25d;
    static final long RECHECK_DELAY_MS = 5000;

    enum Decision {
        CONTINUE,
        RECHECK,
        SHOW_VERIFICATION
    }

    private PeriodicVerifyPolicy() {
    }

    static Decision evaluate(boolean z, boolean z2, int i) {
        return evaluate(z, z2, i, false);
    }

    static Decision evaluate(boolean z, boolean z2, int i, boolean z3) {
        if (z && !z2) {
            return Decision.CONTINUE;
        }
        if (z3) {
            return Decision.RECHECK;
        }
        if (nextFailureStreak(i) >= 2) {
            return Decision.SHOW_VERIFICATION;
        }
        return Decision.RECHECK;
    }

    static int nextFailureStreak(int i) {
        int iMax = Math.max(0, i);
        return iMax >= 2 ? iMax : iMax + 1;
    }

    static long delayForDecision(Decision decision, long j) {
        return delayForDecision(decision, j, Math.random());
    }

    static long delayForDecision(Decision decision, long j, double d) {
        return decision == Decision.RECHECK ? RECHECK_DELAY_MS : jitter(j, d);
    }

    static long jitter(long j, double d) {
        if (j <= 0) {
            return j;
        }
        return Math.max(1L, Math.round(j * (((d >= 0.0d ? Math.min(d, 1.0d) : 0.0d) * 0.5d) + 0.75d)));
    }
}
