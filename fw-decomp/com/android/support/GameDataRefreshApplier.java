package com.android.support;

import com.android.support.PlayersListRefreshPolicy;
import com.android.support.RankAnchorUiApplyPolicy;
import com.android.support.RankWarningDataApplyPolicy;
import com.android.support.diagnostics.DiagnosticUiPublication;
import java.util.List;

/* loaded from: classes.dex */
final class GameDataRefreshApplier {

    interface Host {
        void applyBoardSnapshotUpdate(List<BoardPositionInfo> list, int i, List<PlayerInfo> list2, int i2, boolean z, boolean z2);

        void applyInjectGate(boolean z);

        void applyScreenSecureToAll();

        void cacheMatchup(List<MatchupPairData> list, int i);

        boolean celebrationEnabled();

        void checkThreeStarCelebration(List<BoardPositionInfo> list);

        void checkVoiceAlerts(List<PlayerInfo> list, List<BoardPositionInfo> list2, List<HeroData> list3);

        boolean consumeSecureRefreshTick();

        boolean customSwapAdded();

        boolean debugBoxMode();

        void ensureHexOverlayVisible();

        boolean exitHidBecauseStale();

        boolean exitSuppressed();

        boolean hasPopulationView();

        boolean heroPoolPageVisible();

        boolean hexAlwaysShow();

        boolean hexShownThisRound();

        void hideHexLevelOverlay();

        boolean isGameHudSessionReady();

        int lastCustomSwapBoardTimestamp();

        int lastHexTimestamp();

        int lastPlayerTimestamp();

        int lastRankAnchorTimestamp();

        int lastRankUiBoardTimestamp();

        int lastRankUiPlayerTimestamp();

        int lastTextOpponentId();

        void logCelebrationException(Exception exc);

        void logOpponentTextException(Exception exc);

        void logRefreshUiException(Exception exc);

        void logVoiceException(Exception exc);

        void onPlayersDataUpdated(List<PlayerInfo> list);

        boolean opponentAttackIcon();

        boolean opponentHighlightEnabled();

        boolean opponentShowBox();

        boolean opponentTextWanted();

        RankWarningView rankWarningView();

        void refreshBoardHeroOverlay();

        void refreshBoardOverlayPlaceholder();

        void refreshCustomSwapBoardData();

        void refreshPopulationDisplay(List<PlayerInfo> list);

        void setCachedHexLevels(int[] iArr);

        void setHexShownThisRound(boolean z);

        void setLastCustomSwapBoardTimestamp(int i);

        void setLastHeroPoolTimestamp(int i);

        void setLastHexTimestamp(int i);

        void setLastPlayerTimestamp(int i);

        void setLastRankAnchorTimestamp(int i);

        void setLastRankUiBoardTimestamp(int i);

        void setLastRankUiPlayerTimestamp(int i);

        void setLastTextOpponentId(int i);

        SharedMemManager sharedMemManager();

        boolean shouldEvaluateRankAlertCandidates();

        void showHexLevelOverlay(int[] iArr);

        void updateConnectionIndicator(boolean z);

        void updateHeroPoolUI(List<HeroData> list, List<HeroData> list2, int i);

        void updateOpponentTextContentIfChanged(int i, List<PlayerInfo> list, List<MatchupPairData> list2, int i2);

        void updatePlayersUI(List<PlayerInfo> list);

        void updateRankWarningData(List<PlayerInfo> list, List<BoardPositionInfo> list2);

        void updateRankWarningLayout();

        void updateSelectedHeroesDataOnly(List<HeroData> list, List<HeroData> list2);
    }

    private GameDataRefreshApplier() {
    }

    static void invalidateLeftoverApplyClocks(Host host) {
        host.setLastHeroPoolTimestamp(Integer.MIN_VALUE);
        host.setLastPlayerTimestamp(Integer.MIN_VALUE);
        host.setLastCustomSwapBoardTimestamp(Integer.MIN_VALUE);
        host.setLastHexTimestamp(Integer.MIN_VALUE);
        host.setLastRankUiPlayerTimestamp(Integer.MIN_VALUE);
        host.setLastRankUiBoardTimestamp(Integer.MIN_VALUE);
        host.setLastRankAnchorTimestamp(-1);
        host.setLastTextOpponentId(-1);
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x0087 A[Catch: Exception -> 0x0115, TryCatch #1 {Exception -> 0x0115, blocks: (B:36:0x0075, B:38:0x0087, B:39:0x008a, B:41:0x009b, B:43:0x00a2, B:45:0x00a8, B:47:0x00ad, B:49:0x00b9, B:51:0x00bd, B:52:0x00c2, B:54:0x00d8, B:56:0x00dc, B:58:0x00e0, B:63:0x00ec, B:65:0x00f0, B:67:0x00f5, B:69:0x0109, B:71:0x010e, B:70:0x010c, B:66:0x00f3), top: B:109:0x0075 }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x009b A[Catch: Exception -> 0x0115, TryCatch #1 {Exception -> 0x0115, blocks: (B:36:0x0075, B:38:0x0087, B:39:0x008a, B:41:0x009b, B:43:0x00a2, B:45:0x00a8, B:47:0x00ad, B:49:0x00b9, B:51:0x00bd, B:52:0x00c2, B:54:0x00d8, B:56:0x00dc, B:58:0x00e0, B:63:0x00ec, B:65:0x00f0, B:67:0x00f5, B:69:0x0109, B:71:0x010e, B:70:0x010c, B:66:0x00f3), top: B:109:0x0075 }] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00a0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void apply(com.android.support.GameDataRefreshApplier.Host r19, com.android.support.GameDataRefreshSnapshot r20) {
        /*
            Method dump skipped, instructions count: 414
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.GameDataRefreshApplier.apply(com.android.support.GameDataRefreshApplier$Host, com.android.support.GameDataRefreshSnapshot):void");
    }

    private static void completeUiPublication(Host host, DiagnosticUiPublication diagnosticUiPublication, DiagnosticUiPublication.Status status) {
        SharedMemManager sharedMemManager;
        if (diagnosticUiPublication == null || (sharedMemManager = host.sharedMemManager()) == null) {
            return;
        }
        sharedMemManager.completeDiagnosticUiPublication(diagnosticUiPublication, status);
    }

    private static void completeInvisibleUiPublications(Host host, GameDataRefreshSnapshot gameDataRefreshSnapshot) {
        completeUiPublication(host, gameDataRefreshSnapshot.boardUiPublication, DiagnosticUiPublication.Status.CACHED_NOT_VISIBLE);
        completeUiPublication(host, gameDataRefreshSnapshot.rankUiPublication, DiagnosticUiPublication.Status.CACHED_NOT_VISIBLE);
        completeUiPublication(host, gameDataRefreshSnapshot.matchupUiPublication, DiagnosticUiPublication.Status.CACHED_NOT_VISIBLE);
    }

    private static void applyHeroPool(Host host, GameDataRefreshSnapshot gameDataRefreshSnapshot) {
        if (HeroPoolApplyPolicy.shouldApply(gameDataRefreshSnapshot.needHeroPool, gameDataRefreshSnapshot.heroData != null)) {
            host.setLastHeroPoolTimestamp(gameDataRefreshSnapshot.heroTimestamp);
            RankWarningView rankWarningView = host.rankWarningView();
            if (rankWarningView != null) {
                rankWarningView.updateHeroPoolData(gameDataRefreshSnapshot.heroData[0], gameDataRefreshSnapshot.heroData[1], gameDataRefreshSnapshot.heroTimestamp);
            }
            if (HeroPoolApplyPolicy.shouldUpdateFullPage(host.heroPoolPageVisible())) {
                host.updateHeroPoolUI(gameDataRefreshSnapshot.heroData[0], gameDataRefreshSnapshot.heroData[1], gameDataRefreshSnapshot.heroTimestamp);
            } else {
                host.updateSelectedHeroesDataOnly(gameDataRefreshSnapshot.heroData[0], gameDataRefreshSnapshot.heroData[1]);
            }
        }
    }

    private static void applyPlayers(Host host, GameDataRefreshSnapshot gameDataRefreshSnapshot) {
        PlayersListRefreshPolicy.ApplyAction applyActionDecide = PlayersListRefreshPolicy.decide(gameDataRefreshSnapshot.playerTimestamp, host.lastPlayerTimestamp(), gameDataRefreshSnapshot.needPlayers);
        if (applyActionDecide == PlayersListRefreshPolicy.ApplyAction.SKIP) {
            return;
        }
        host.setLastPlayerTimestamp(gameDataRefreshSnapshot.playerTimestamp);
        if (applyActionDecide == PlayersListRefreshPolicy.ApplyAction.PAINT_LIST) {
            host.updatePlayersUI(gameDataRefreshSnapshot.players);
        } else {
            host.onPlayersDataUpdated(gameDataRefreshSnapshot.players);
        }
    }

    private static void applyCustomSwap(Host host, GameDataRefreshSnapshot gameDataRefreshSnapshot) {
        if (CustomSwapRefreshPolicy.shouldRefresh(host.customSwapAdded(), gameDataRefreshSnapshot.boardPositions != null, gameDataRefreshSnapshot.boardTimestamp, host.lastCustomSwapBoardTimestamp())) {
            host.setLastCustomSwapBoardTimestamp(gameDataRefreshSnapshot.boardTimestamp);
            host.refreshCustomSwapBoardData();
        }
    }

    private static void applyHex(Host host, GameDataRefreshSnapshot gameDataRefreshSnapshot) {
        if (HexOverlayRefreshPolicy.shouldClear(gameDataRefreshSnapshot.hexLevels, gameDataRefreshSnapshot.hexTimestamp, host.lastHexTimestamp())) {
            host.setLastHexTimestamp(gameDataRefreshSnapshot.hexTimestamp);
            host.setCachedHexLevels(null);
            host.setHexShownThisRound(false);
            host.hideHexLevelOverlay();
            return;
        }
        if (HexOverlayRefreshPolicy.shouldAccept(gameDataRefreshSnapshot.hexLevels, gameDataRefreshSnapshot.hexTimestamp, host.lastHexTimestamp())) {
            host.setLastHexTimestamp(gameDataRefreshSnapshot.hexTimestamp);
            if (!HexOverlayRefreshPolicy.hasValidLevels(gameDataRefreshSnapshot.hexLevels)) {
                host.setCachedHexLevels(null);
                host.setHexShownThisRound(false);
                host.hideHexLevelOverlay();
            } else {
                host.setCachedHexLevels(gameDataRefreshSnapshot.hexLevels);
                if (HexOverlayRefreshPolicy.shouldPopup(host.hexAlwaysShow(), host.hexShownThisRound())) {
                    host.setHexShownThisRound(true);
                    host.showHexLevelOverlay(gameDataRefreshSnapshot.hexLevels);
                }
            }
        }
    }

    private static boolean applyRankAnchors(Host host, GameDataRefreshSnapshot gameDataRefreshSnapshot) {
        RankWarningView rankWarningView = host.rankWarningView();
        if (rankWarningView == null) {
            return false;
        }
        RankAnchorSnapshot rankAnchorSnapshotLatestApplicable = gameDataRefreshSnapshot.needRankWarning ? RankAnchorSnapshot.latestApplicable(gameDataRefreshSnapshot.rankAnchors, host.sharedMemManager().getRankAnchorSnapshot()) : null;
        RankAnchorUiApplyPolicy.Action actionDecide = RankAnchorUiApplyPolicy.decide(gameDataRefreshSnapshot.needRankWarning, rankAnchorSnapshotLatestApplicable, host.lastRankAnchorTimestamp(), rankWarningView.isAutoAnchorMode());
        if (actionDecide == RankAnchorUiApplyPolicy.Action.WRITE) {
            host.setLastRankAnchorTimestamp(rankAnchorSnapshotLatestApplicable.timestamp);
            rankWarningView.updateAnchors(rankAnchorSnapshotLatestApplicable.anchors, rankAnchorSnapshotLatestApplicable.receivedAtMs);
        } else if (actionDecide == RankAnchorUiApplyPolicy.Action.CLEAR) {
            host.setLastRankAnchorTimestamp(-1);
            rankWarningView.clearAnchors();
        } else if (!rankWarningView.refreshAnchorState()) {
            return true;
        }
        host.updateRankWarningLayout();
        return true;
    }

    private static void applyRankWarningData(Host host, GameDataRefreshSnapshot gameDataRefreshSnapshot) {
        if (!gameDataRefreshSnapshot.needRankWarning) {
            host.setLastRankUiPlayerTimestamp(Integer.MIN_VALUE);
            host.setLastRankUiBoardTimestamp(Integer.MIN_VALUE);
            return;
        }
        boolean z = false;
        boolean zRankSnapshotChanged = RuntimeRefreshPolicy.rankSnapshotChanged(host.lastRankUiPlayerTimestamp(), host.lastRankUiBoardTimestamp(), gameDataRefreshSnapshot.playerTimestamp, gameDataRefreshSnapshot.boardTimestamp, false);
        if (gameDataRefreshSnapshot.players != null && !gameDataRefreshSnapshot.players.isEmpty()) {
            z = true;
        }
        RankWarningDataApplyPolicy.Action actionDecide = RankWarningDataApplyPolicy.decide(zRankSnapshotChanged, z);
        if (actionDecide != RankWarningDataApplyPolicy.Action.SKIP) {
            host.setLastRankUiPlayerTimestamp(gameDataRefreshSnapshot.playerTimestamp);
            host.setLastRankUiBoardTimestamp(gameDataRefreshSnapshot.boardTimestamp);
            if (actionDecide == RankWarningDataApplyPolicy.Action.UPDATE) {
                host.updateRankWarningData(gameDataRefreshSnapshot.players, gameDataRefreshSnapshot.boardPositions);
            } else if (host.rankWarningView() != null) {
                host.rankWarningView().resetPlayerBindings();
                host.setLastRankAnchorTimestamp(-1);
                host.updateRankWarningLayout();
            }
        }
        if (host.rankWarningView() == null || !RankOpponentAnchorApplyPolicy.shouldPublish(host.debugBoxMode(), host.opponentHighlightEnabled(), host.opponentShowBox(), host.opponentAttackIcon())) {
            return;
        }
        host.rankWarningView().setOpponentId(gameDataRefreshSnapshot.opponentId);
    }
}
