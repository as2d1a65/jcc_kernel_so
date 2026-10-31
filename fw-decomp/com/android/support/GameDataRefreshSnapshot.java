package com.android.support;

import com.android.support.diagnostics.DiagnosticUiPublication;
import java.util.List;

/* loaded from: classes.dex */
final class GameDataRefreshSnapshot {
    final List<BoardPositionInfo> boardPositions;
    final int boardTimestamp;
    final DiagnosticUiPublication boardUiPublication;
    final boolean capturedHudSessionReady;
    final boolean cppConnected;
    final List<HeroData>[] heroData;
    final int heroTimestamp;
    final int[] hexLevels;
    final int hexTimestamp;
    final boolean leftoverReleased;
    final boolean leftoverSkip;
    final int matchupMyId;
    final List<MatchupPairData> matchupPairs;
    final boolean matchupSnapshotRead;
    final DiagnosticUiPublication matchupUiPublication;
    final boolean needBoardOverlay;
    final boolean needHeroPool;
    final boolean needPlayers;
    final boolean needRankWarning;
    final int opponentId;
    final int playerTimestamp;
    final List<PlayerInfo> players;
    final RankAnchorSnapshot rankAnchors;
    final DiagnosticUiPublication rankUiPublication;

    GameDataRefreshSnapshot(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, List<HeroData>[] listArr, int i, List<PlayerInfo> list, int i2, List<BoardPositionInfo> list2, int i3, RankAnchorSnapshot rankAnchorSnapshot, int i4, List<MatchupPairData> list3, int i5, boolean z7, int[] iArr, int i6, boolean z8, boolean z9) {
        this(z, z2, z3, z4, z5, z6, listArr, i, list, i2, list2, i3, null, rankAnchorSnapshot, null, i4, list3, i5, z7, null, iArr, i6, z8, z9);
    }

    GameDataRefreshSnapshot(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, List<HeroData>[] listArr, int i, List<PlayerInfo> list, int i2, List<BoardPositionInfo> list2, int i3, DiagnosticUiPublication diagnosticUiPublication, RankAnchorSnapshot rankAnchorSnapshot, DiagnosticUiPublication diagnosticUiPublication2, int i4, List<MatchupPairData> list3, int i5, boolean z7, DiagnosticUiPublication diagnosticUiPublication3, int[] iArr, int i6, boolean z8, boolean z9) {
        this.capturedHudSessionReady = z;
        this.cppConnected = z2;
        this.needHeroPool = z3;
        this.needPlayers = z4;
        this.needBoardOverlay = z5;
        this.needRankWarning = z6;
        this.heroData = listArr;
        this.heroTimestamp = i;
        this.players = list;
        this.playerTimestamp = i2;
        this.boardPositions = list2;
        this.boardTimestamp = i3;
        this.boardUiPublication = diagnosticUiPublication;
        this.rankAnchors = rankAnchorSnapshot;
        this.rankUiPublication = diagnosticUiPublication2;
        this.opponentId = i4;
        this.matchupPairs = list3;
        this.matchupMyId = i5;
        this.matchupSnapshotRead = z7;
        this.matchupUiPublication = diagnosticUiPublication3;
        this.hexLevels = iArr;
        this.hexTimestamp = i6;
        this.leftoverReleased = z8;
        this.leftoverSkip = z9;
    }
}
