package com.android.support;

import com.android.support.JccSessionState;

/* loaded from: classes.dex */
final class GatewayRenewalPolicy {
    static final long LEAD_SECONDS = 180;
    private int failures;
    private long owner = -1;
    private long retryAt;

    GatewayRenewalPolicy() {
    }

    synchronized boolean due(JccSessionState.Snapshot snapshot, long j, long j2) {
        boolean z;
        z = false;
        if (this.owner != snapshot.generation) {
            this.owner = snapshot.generation;
            this.failures = 0;
            this.retryAt = 0L;
        }
        if (snapshot.present() && snapshot.expiresAt - j <= LEAD_SECONDS) {
            if (j2 >= this.retryAt) {
                z = true;
            }
        }
        return z;
    }

    synchronized void failed(long j, long j2) {
        if (this.owner != j) {
            return;
        }
        int i = this.failures;
        this.failures = i + 1;
        this.retryAt = j2 + Math.min(30000L, 2000 << Math.min(i, 4));
    }

    synchronized void succeeded() {
        this.owner = -1L;
        this.failures = 0;
        this.retryAt = 0L;
    }

    synchronized void incompleteRecovery(long j, long j2) {
        this.owner = j;
        failed(j, j2);
    }
}
