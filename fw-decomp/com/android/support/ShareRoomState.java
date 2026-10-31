package com.android.support;

/* loaded from: classes.dex */
public final class ShareRoomState {
    private boolean active;
    private long generation;
    private long nextSequence;
    private String room = "";
    private Phase phase = Phase.DISABLED;

    public enum Phase {
        DISABLED,
        STARTING,
        WAITING,
        LIVE,
        RECONNECTING,
        FINISHED,
        ERROR
    }

    public synchronized long begin() {
        long j = this.generation + 1;
        this.generation = j;
        if (j <= 0) {
            this.generation = 1L;
        }
        this.nextSequence = 1L;
        this.active = true;
        this.room = "";
        this.phase = Phase.STARTING;
        return this.generation;
    }

    public synchronized long nextSequence(long j) {
        long j2;
        requireCurrent(j);
        j2 = this.nextSequence;
        if (j2 <= 0 || j2 == Long.MAX_VALUE) {
            throw new IllegalStateException("share sequence exhausted");
        }
        this.nextSequence = 1 + j2;
        return j2;
    }

    public synchronized boolean installRoom(long j, String str) {
        if (!isCurrent(j)) {
            return false;
        }
        if (str == null || !str.matches("XF[0-9]{3}")) {
            throw new IllegalArgumentException("canonical share room is required");
        }
        this.room = str;
        this.phase = Phase.WAITING;
        return true;
    }

    public synchronized long recoverMissingRoom(long j) {
        if (isCurrent(j) && !this.room.isEmpty()) {
            return begin();
        }
        return -1L;
    }

    public synchronized boolean updatePhase(long j, Phase phase) {
        if (!isCurrent(j)) {
            return false;
        }
        if (phase == null || phase == Phase.DISABLED || phase == Phase.FINISHED) {
            throw new IllegalArgumentException("active share phase is required");
        }
        this.phase = phase;
        return true;
    }

    public synchronized boolean finish(long j) {
        if (!isCurrent(j)) {
            return false;
        }
        this.active = false;
        this.phase = Phase.FINISHED;
        return true;
    }

    public synchronized long finishAndAllocateStopSequence(long j) {
        long j2 = -1;
        if (!isCurrent(j)) {
            return -1L;
        }
        if (!this.room.isEmpty()) {
            long j3 = this.nextSequence;
            if (j3 > 0 && j3 < Long.MAX_VALUE) {
                this.nextSequence = 1 + j3;
                j2 = j3;
            }
        }
        this.active = false;
        this.phase = Phase.FINISHED;
        return j2;
    }

    public synchronized boolean disable(long j) {
        if (j != this.generation) {
            return false;
        }
        this.active = false;
        this.room = "";
        this.phase = Phase.DISABLED;
        return true;
    }

    public synchronized Snapshot snapshot() {
        return new Snapshot(this.active, this.generation, this.nextSequence, this.room, this.phase);
    }

    private boolean isCurrent(long j) {
        return this.active && j == this.generation;
    }

    private void requireCurrent(long j) {
        if (!isCurrent(j)) {
            throw new IllegalArgumentException("stale share generation");
        }
    }

    public static final class Snapshot {
        private final boolean active;
        private final long generation;
        private final long nextSequence;
        private final Phase phase;
        private final String room;

        Snapshot(boolean z, long j, long j2, String str, Phase phase) {
            this.active = z;
            this.generation = j;
            this.nextSequence = j2;
            this.room = str;
            this.phase = phase;
        }

        public boolean active() {
            return this.active;
        }

        public long generation() {
            return this.generation;
        }

        public long nextSequence() {
            return this.nextSequence;
        }

        public String room() {
            return this.room;
        }

        public Phase phase() {
            return this.phase;
        }
    }
}
