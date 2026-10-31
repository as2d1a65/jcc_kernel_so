package com.android.support;

/* loaded from: classes.dex */
final class InjectionAttemptLifecycle {
    private boolean active;
    private long generation;
    private long runningAttempt;

    InjectionAttemptLifecycle() {
    }

    synchronized boolean isRunning() {
        return this.runningAttempt != 0;
    }

    synchronized long begin() {
        if (this.runningAttempt != 0) {
            return 0L;
        }
        long j = this.generation + 1;
        this.generation = j;
        this.active = true;
        this.runningAttempt = j;
        return j;
    }

    synchronized void cancel(long j) {
        if (isCurrent(j)) {
            this.generation++;
            this.active = false;
        }
    }

    synchronized void finish(long j) {
        if (j > 0) {
            if (this.runningAttempt == j) {
                this.runningAttempt = 0L;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0013  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    synchronized boolean isCurrent(long r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.active     // Catch: java.lang.Throwable -> L16
            if (r0 == 0) goto L13
            r0 = 0
            int r0 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r0 <= 0) goto L13
            long r0 = r2.generation     // Catch: java.lang.Throwable -> L16
            int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r3 != 0) goto L13
            r3 = 1
            goto L14
        L13:
            r3 = 0
        L14:
            monitor-exit(r2)
            return r3
        L16:
            r3 = move-exception
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L16
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.InjectionAttemptLifecycle.isCurrent(long):boolean");
    }
}
