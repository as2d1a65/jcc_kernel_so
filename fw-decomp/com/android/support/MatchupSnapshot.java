package com.android.support;

import androidx.core.os.EnvironmentCompat;
import com.android.support.diagnostics.DiagnosticUiPublication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class MatchupSnapshot {
    public final int matchId;
    public final int myId;
    public final int opponentId;
    public final List<MatchupPairData> pairs;
    public final long receivedAtMs;
    public final int revision;
    public final String source;
    public final DiagnosticUiPublication uiPublication;

    private MatchupSnapshot(int i, int i2, List<MatchupPairData> list, int i3, long j, int i4, String str, DiagnosticUiPublication diagnosticUiPublication) {
        this.myId = i;
        this.opponentId = i2;
        this.pairs = immutablePairs(list);
        this.revision = i3;
        this.receivedAtMs = j;
        this.matchId = i4;
        this.source = str == null ? EnvironmentCompat.MEDIA_UNKNOWN : str;
        this.uiPublication = diagnosticUiPublication;
    }

    static MatchupSnapshot create(int i, int i2, List<MatchupPairData> list, int i3, long j, int i4, String str) {
        return new MatchupSnapshot(i, i2, list, i3, j, i4, str, null);
    }

    static MatchupSnapshot create(int i, int i2, List<MatchupPairData> list, int i3, long j, int i4, String str, DiagnosticUiPublication diagnosticUiPublication) {
        return new MatchupSnapshot(i, i2, list, i3, j, i4, str, diagnosticUiPublication);
    }

    static MatchupSnapshot empty(int i) {
        return create(-1, -1, null, i, 0L, -1, "clear");
    }

    public boolean isFresh(long j, long j2) {
        long j3 = this.receivedAtMs;
        return j3 > 0 && j2 > 0 && j >= j3 && j - j3 <= j2;
    }

    public boolean isExplicitEmpty() {
        return this.myId < 0 && this.opponentId < 0 && this.pairs.isEmpty() && this.source.endsWith("empty");
    }

    static boolean shouldAccept(MatchupSnapshot matchupSnapshot, int i, String str, long j, long j2) {
        int i2;
        if (matchupSnapshot == null || !matchupSnapshot.isFresh(j, j2)) {
            return true;
        }
        if (i < 0 || (i2 = matchupSnapshot.matchId) < 0) {
            if (i >= 0) {
                return true;
            }
        } else {
            if (i > i2) {
                return true;
            }
            if (i < i2) {
                return false;
            }
        }
        return sourcePriority(str) >= sourcePriority(matchupSnapshot.source);
    }

    private static int sourcePriority(String str) {
        if (str == null) {
            return 0;
        }
        if ("preMatchData".equals(str) || "preMatchData-empty".equals(str)) {
            return 3;
        }
        if ("preMatch".equals(str)) {
            return 2;
        }
        return "selectHighScore".equals(str) ? 1 : 0;
    }

    private static List<MatchupPairData> immutablePairs(List<MatchupPairData> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(list.size());
        for (MatchupPairData matchupPairData : list) {
            if (matchupPairData != null) {
                arrayList.add(new MatchupPairData(matchupPairData.player1Id, matchupPairData.player2Id, matchupPairData.ghostSourcePlayer));
            }
        }
        return Collections.unmodifiableList(arrayList);
    }
}
