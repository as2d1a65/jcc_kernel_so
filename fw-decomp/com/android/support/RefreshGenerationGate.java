package com.android.support;

/* loaded from: classes.dex */
final class RefreshGenerationGate {
    static final long NONE = 0;
    private boolean active;
    private long generation;
    private long startedAtMs;

    RefreshGenerationGate() {
    }

    synchronized long tryStart(long j) {
        if (this.active) {
            return 0L;
        }
        long jNextGeneration = nextGeneration(this.generation);
        this.generation = jNextGeneration;
        this.startedAtMs = j;
        this.active = true;
        return jNextGeneration;
    }

    synchronized long expireIfTimedOut(long j, long j2) {
        if (!this.active) {
            return -1L;
        }
        long jMax = Math.max(0L, j - this.startedAtMs);
        if (jMax <= j2) {
            return -1L;
        }
        this.generation = nextGeneration(this.generation);
        this.active = false;
        return jMax;
    }

    synchronized void invalidate() {
        this.generation = nextGeneration(this.generation);
        this.active = false;
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
            if (r0 == 0) goto L13
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.RefreshGenerationGate.isCurrent(long):boolean");
    }

    synchronized boolean finish(long j) {
        if (!isCurrent(j)) {
            return false;
        }
        this.active = false;
        return true;
    }

    private static long nextGeneration(long j) {
        if (j != Long.MAX_VALUE) {
            return j + 1;
        }
        throw new IllegalStateException("refresh generation exhausted");
    }
}
