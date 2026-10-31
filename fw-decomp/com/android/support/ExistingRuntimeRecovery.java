package com.android.support;

/* loaded from: classes.dex */
final class ExistingRuntimeRecovery {

    interface Host {
        boolean connected();

        boolean current();

        long nowMs();

        void pause(long j) throws InterruptedException;

        String processIdentity();

        void progress(long j);

        boolean stageBootstrap();
    }

    enum Result {
        READY,
        CANCELLED,
        TARGET_CHANGED,
        BOOTSTRAP_FAILED,
        TIMED_OUT
    }

    static Result run(Host host, long j, long j2) {
        if (j <= 0 || j2 <= 0) {
            throw new IllegalArgumentException("invalid timing");
        }
        if (!host.current()) {
            return Result.CANCELLED;
        }
        String strProcessIdentity = host.processIdentity();
        if (strProcessIdentity == null) {
            return Result.TARGET_CHANGED;
        }
        long jNowMs = host.nowMs();
        boolean z = false;
        while (host.current()) {
            try {
                if (!strProcessIdentity.equals(host.processIdentity())) {
                    return Result.TARGET_CHANGED;
                }
                if (host.connected()) {
                    return host.current() ? Result.READY : Result.CANCELLED;
                }
                long jNowMs2 = host.nowMs() - jNowMs;
                if (jNowMs2 >= j) {
                    return Result.TIMED_OUT;
                }
                if (!z) {
                    if (!host.current()) {
                        return Result.CANCELLED;
                    }
                    if (!host.stageBootstrap()) {
                        return Result.BOOTSTRAP_FAILED;
                    }
                    z = true;
                } else {
                    host.progress(jNowMs2);
                    host.pause(Math.min(j2, j - jNowMs2));
                }
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
        }
        return Result.CANCELLED;
    }

    private ExistingRuntimeRecovery() {
    }
}
