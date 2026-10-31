package com.android.support;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class JccSyncCursor {
    private static final Pattern MATCH_HASH = Pattern.compile("[0-9a-f]{64}");
    private long generation;
    private String matchHash;
    private boolean online;
    private int seat;
    private long sequence;

    public JccSyncCursor() {
        this(seedGeneration());
    }

    JccSyncCursor(long j) {
        this.seat = -1;
        if (j < 0) {
            throw new IllegalArgumentException("initial generation must not be negative");
        }
        this.generation = j;
    }

    public synchronized Snapshot start(String str, int i) {
        validateMatch(str);
        validateSeat(i);
        long j = this.generation;
        this.generation = j == Long.MAX_VALUE ? 1L : j + 1;
        this.sequence = 1L;
        this.matchHash = str;
        this.seat = i;
        this.online = true;
        return snapshot();
    }

    public synchronized Snapshot nextOnline() {
        if (!this.online || this.matchHash == null) {
            throw new IllegalStateException("sync cursor is not running");
        }
        this.sequence = nextSequence(this.sequence);
        return snapshot();
    }

    public synchronized Snapshot stop() {
        if (!this.online || this.matchHash == null) {
            throw new IllegalStateException("sync cursor is not running");
        }
        this.sequence = nextSequence(this.sequence);
        this.online = false;
        return snapshot();
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x000f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean acceptsResponse(long r3, long r5) {
        /*
            r2 = this;
            monitor-enter(r2)
            long r0 = r2.generation     // Catch: java.lang.Throwable -> L12
            int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r3 != 0) goto Lf
            long r3 = r2.sequence     // Catch: java.lang.Throwable -> L12
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r3 != 0) goto Lf
            r3 = 1
            goto L10
        Lf:
            r3 = 0
        L10:
            monitor-exit(r2)
            return r3
        L12:
            r3 = move-exception
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L12
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.JccSyncCursor.acceptsResponse(long, long):boolean");
    }

    public synchronized boolean isRunning() {
        return this.online;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0015  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean currentMatches(java.lang.String r2, int r3) {
        /*
            r1 = this;
            monitor-enter(r1)
            boolean r0 = r1.online     // Catch: java.lang.Throwable -> L18
            if (r0 == 0) goto L15
            java.lang.String r0 = r1.matchHash     // Catch: java.lang.Throwable -> L18
            if (r0 == 0) goto L15
            boolean r2 = r0.equals(r2)     // Catch: java.lang.Throwable -> L18
            if (r2 == 0) goto L15
            int r2 = r1.seat     // Catch: java.lang.Throwable -> L18
            if (r2 != r3) goto L15
            r2 = 1
            goto L16
        L15:
            r2 = 0
        L16:
            monitor-exit(r1)
            return r2
        L18:
            r2 = move-exception
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L18
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.JccSyncCursor.currentMatches(java.lang.String, int):boolean");
    }

    private Snapshot snapshot() {
        return new Snapshot(this.generation, this.sequence, this.matchHash, this.seat, this.online);
    }

    private static long seedGeneration() {
        long jNextLong = new SecureRandom().nextLong() & Long.MAX_VALUE;
        return jNextLong == Long.MAX_VALUE ? jNextLong - 1 : jNextLong;
    }

    private static long nextSequence(long j) {
        if (j != Long.MAX_VALUE) {
            return j + 1;
        }
        throw new IllegalStateException("sequence exhausted; start a new generation");
    }

    private static void validateMatch(String str) {
        if (str == null || !MATCH_HASH.matcher(str).matches()) {
            throw new IllegalArgumentException("match hash must be 64 lowercase hex characters");
        }
    }

    private static void validateSeat(int i) {
        if (i < 0 || i > 7) {
            throw new IllegalArgumentException("seat must be between 0 and 7");
        }
    }

    public static final class Snapshot {
        private final long generation;
        private final String matchHash;
        private final boolean online;
        private final int seat;
        private final long sequence;

        private Snapshot(long j, long j2, String str, int i, boolean z) {
            this.generation = j;
            this.sequence = j2;
            this.matchHash = str;
            this.seat = i;
            this.online = z;
        }

        public long generation() {
            return this.generation;
        }

        public long sequence() {
            return this.sequence;
        }

        public String matchHash() {
            return this.matchHash;
        }

        public int seat() {
            return this.seat;
        }

        public boolean online() {
            return this.online;
        }
    }
}
