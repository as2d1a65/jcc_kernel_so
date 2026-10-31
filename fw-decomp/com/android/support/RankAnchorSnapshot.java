package com.android.support;

import com.android.support.diagnostics.DiagnosticUiPublication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
final class RankAnchorSnapshot {
    static final RankAnchorSnapshot EMPTY = new RankAnchorSnapshot(null, 0, 0, 0, null);
    final List<RankWarningAnchor> anchors;
    final long generation;
    final long receivedAtMs;
    final int timestamp;
    final DiagnosticUiPublication uiPublication;

    private RankAnchorSnapshot(List<RankWarningAnchor> list, int i, long j, long j2, DiagnosticUiPublication diagnosticUiPublication) {
        this.anchors = list == null ? null : Collections.unmodifiableList(new ArrayList(list));
        this.timestamp = i;
        this.receivedAtMs = j;
        this.generation = j2;
        this.uiPublication = diagnosticUiPublication;
    }

    RankAnchorSnapshot next(List<RankWarningAnchor> list, long j) {
        return next(list, j, null);
    }

    RankAnchorSnapshot next(List<RankWarningAnchor> list, long j, DiagnosticUiPublication diagnosticUiPublication) {
        return new RankAnchorSnapshot(list, this.timestamp + 1, j, 1 + this.generation, diagnosticUiPublication);
    }

    RankAnchorSnapshot cleared() {
        return new RankAnchorSnapshot(null, this.timestamp + 1, 0L, 1 + this.generation, null);
    }

    boolean isGeneration(long j) {
        return this.generation == j;
    }

    static RankAnchorSnapshot latestApplicable(RankAnchorSnapshot rankAnchorSnapshot, RankAnchorSnapshot rankAnchorSnapshot2) {
        if (rankAnchorSnapshot == null || rankAnchorSnapshot2 == null || rankAnchorSnapshot2.generation < rankAnchorSnapshot.generation || rankAnchorSnapshot2.anchors == null) {
            return null;
        }
        return rankAnchorSnapshot2;
    }
}
