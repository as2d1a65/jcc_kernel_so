package com.android.support;

import com.android.support.diagnostics.DiagnosticUiPublication;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
final class GameSnapshotStore {
    volatile String currentMatchGeneration = "";
    volatile long currentMatchStartedAtMs = 0;
    volatile List<PlayerInfo> cachedPlayers = null;
    volatile int cachedPlayerTimestamp = 0;
    volatile long cachedPlayersReceivedAtMs = 0;
    volatile List<HeroData> cachedHeroPool = null;
    volatile List<HeroData> cachedLockedHeroes = null;
    volatile int cachedHeroPoolTimestamp = 0;
    volatile List<BoardPositionInfo> cachedBoardPositions = null;
    volatile int cachedBoardPosTimestamp = 0;
    volatile long cachedBoardPositionsReceivedAtMs = 0;
    volatile DiagnosticUiPublication cachedBoardUiPublication = null;
    volatile Set<Integer> cachedMyOwnedHeroIds = new HashSet();
    volatile OwnedHeroCopies cachedMyOwnedHeroCopies = OwnedHeroCopies.EMPTY;
    volatile int cachedMyOwnedTimestamp = 0;
    volatile int cachedMyOwnedPieceCount = 0;
    volatile OpponentCardingStatus cachedOpponentCardingStatus = OpponentCardingStatus.disabled();
    volatile int[] cachedHexLevels = null;
    volatile int cachedHexTimestamp = 0;
    volatile int[] cachedHexLivePool = null;
    volatile int[] cachedHexLivePoolWeights = null;
    volatile int cachedHexLivePoolTotalWeight = 0;
    volatile int cachedHexDrawsPerStage = 0;
    volatile RankAnchorSnapshot cachedRankAnchorSnapshot = RankAnchorSnapshot.EMPTY;
    volatile BoardHeroAnchorSnapshot cachedBoardHeroAnchorSnapshot = BoardHeroAnchorSnapshot.EMPTY;
    volatile MatchupSnapshot cachedMatchupSnapshot = MatchupSnapshot.empty(0);
    volatile boolean cachedDualPlayMode = false;
    volatile String lastMatchHash = "";
    volatile int cachedTurnState = -1;
    volatile long cachedTurnStateAtMs = 0;

    GameSnapshotStore() {
    }

    void clearForSessionBoundary(int i) {
        this.cachedPlayers = null;
        this.cachedPlayerTimestamp++;
        this.cachedPlayersReceivedAtMs = 0L;
        this.cachedBoardPositions = null;
        this.cachedBoardPosTimestamp++;
        this.cachedBoardPositionsReceivedAtMs = 0L;
        this.cachedBoardUiPublication = null;
        this.cachedHeroPool = null;
        this.cachedLockedHeroes = null;
        this.cachedHeroPoolTimestamp++;
        this.cachedMyOwnedHeroIds = Collections.emptySet();
        this.cachedMyOwnedHeroCopies = OwnedHeroCopies.EMPTY;
        this.cachedMyOwnedTimestamp++;
        this.cachedMyOwnedPieceCount = 0;
        this.cachedHexLevels = null;
        this.cachedHexTimestamp++;
        this.cachedHexLivePool = null;
        this.cachedHexLivePoolWeights = null;
        this.cachedHexLivePoolTotalWeight = 0;
        this.cachedHexDrawsPerStage = 0;
        this.cachedMatchupSnapshot = MatchupSnapshot.empty(i);
        this.cachedRankAnchorSnapshot = RankAnchorSnapshot.EMPTY;
        this.cachedBoardHeroAnchorSnapshot = BoardHeroAnchorSnapshot.EMPTY;
        this.cachedOpponentCardingStatus = OpponentCardingStatus.disabled();
        this.cachedDualPlayMode = false;
        this.lastMatchHash = "";
        this.cachedTurnState = -1;
        this.cachedTurnStateAtMs = 0L;
    }
}
