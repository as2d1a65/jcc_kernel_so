package com.android.support;

/* loaded from: classes.dex */
final class VerificationAttemptWindow {
    static final long TOTAL_TIMEOUT_MS = 60000;
    private boolean active;
    private long deadline;
    private boolean inFlight;
    private long request;
    private long sequence;

    VerificationAttemptWindow() {
    }

    boolean isActive() {
        return this.active;
    }

    long sequence() {
        return this.sequence;
    }

    void begin(long j) {
        if (this.active) {
            return;
        }
        this.active = true;
        this.inFlight = false;
        this.sequence++;
        this.deadline = j + TOTAL_TIMEOUT_MS;
    }

    long startAttempt() {
        if (!this.active || this.inFlight) {
            return 0L;
        }
        this.inFlight = true;
        long j = this.request + 1;
        this.request = j;
        return j;
    }

    boolean accept(long j) {
        if (!isCurrent(j) || !this.inFlight) {
            return false;
        }
        this.inFlight = false;
        return true;
    }

    boolean isCurrent(long j) {
        return this.active && this.request == j;
    }

    boolean expired(long j) {
        return this.active && j >= this.deadline;
    }

    long remaining(long j) {
        if (this.active) {
            return Math.max(0L, this.deadline - j);
        }
        return 0L;
    }

    void finish() {
        this.active = false;
        this.inFlight = false;
        this.request++;
    }
}
