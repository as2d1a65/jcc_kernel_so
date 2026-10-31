package com.android.support;

/* loaded from: classes.dex */
final class ProcessAuthRecoveryGate {
    private static final int MAX_SILENT_ATTEMPTS = 4;
    private int attemptsStarted;
    private State state = State.IDLE;

    enum Decision {
        USE_CURRENT_SESSION,
        START_RECOVERY,
        WAIT,
        SHOW_INTERACTIVE
    }

    enum FailureAction {
        RETRY_SILENTLY,
        SHOW_INTERACTIVE,
        IGNORE
    }

    private enum State {
        IDLE,
        RUNNING,
        BACKOFF,
        SUCCEEDED,
        FAILED,
        DISPOSED
    }

    ProcessAuthRecoveryGate() {
    }

    synchronized Decision onInitialReady(boolean z, boolean z2) {
        if (this.state != State.DISPOSED && this.state != State.RUNNING && this.state != State.BACKOFF) {
            if (this.state == State.SUCCEEDED) {
                if (z) {
                    return Decision.USE_CURRENT_SESSION;
                }
                this.state = State.FAILED;
                return Decision.SHOW_INTERACTIVE;
            }
            if (this.state == State.FAILED) {
                return Decision.SHOW_INTERACTIVE;
            }
            if (z) {
                this.state = State.SUCCEEDED;
                return Decision.USE_CURRENT_SESSION;
            }
            if (!z2) {
                this.state = State.FAILED;
                return Decision.SHOW_INTERACTIVE;
            }
            this.state = State.RUNNING;
            this.attemptsStarted = 1;
            return Decision.START_RECOVERY;
        }
        return Decision.WAIT;
    }

    synchronized boolean isRunning() {
        return this.state == State.RUNNING;
    }

    synchronized boolean completeSuccess() {
        if (this.state != State.RUNNING) {
            return false;
        }
        this.state = State.SUCCEEDED;
        return true;
    }

    synchronized FailureAction completeFailure(boolean z) {
        if (this.state != State.RUNNING) {
            return FailureAction.IGNORE;
        }
        if (z && this.attemptsStarted < 4) {
            this.state = State.BACKOFF;
            return FailureAction.RETRY_SILENTLY;
        }
        this.state = State.FAILED;
        return FailureAction.SHOW_INTERACTIVE;
    }

    synchronized boolean beginRetry() {
        int i;
        if (this.state == State.BACKOFF && (i = this.attemptsStarted) < 4) {
            this.attemptsStarted = i + 1;
            this.state = State.RUNNING;
            return true;
        }
        return false;
    }

    synchronized void suppressAutomaticRecovery() {
        if (this.state != State.DISPOSED) {
            this.state = State.FAILED;
        }
    }

    synchronized void dispose() {
        this.state = State.DISPOSED;
    }
}
