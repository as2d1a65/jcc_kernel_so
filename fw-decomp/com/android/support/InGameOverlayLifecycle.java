package com.android.support;

import android.view.View;
import java.util.Set;

/* loaded from: classes.dex */
final class InGameOverlayLifecycle {

    interface Host {
        boolean boardOverlayEnabled();

        ChessBoardView chessBoardView();

        void clearStaleMatchPlayerUi();

        boolean debugBoxMode();

        void dismissInteractiveMatchWindows();

        boolean exitHidBecauseStale();

        void hideBoardOverlay();

        void hideHexLevelOverlay();

        void hideOpponentTextOverlay();

        void hideRankWarningOverlay();

        void hideShopSlotOverlays();

        boolean isBoardOverlayVisible();

        boolean isRankWarningVisible();

        void logHidden();

        boolean opponentHighlightEnabled();

        boolean opponentShowBox();

        boolean rankWarningEnabled();

        RankWarningView rankWarningView();

        void refreshBoardHeroOverlay();

        void refreshShopSlotOverlayFromCache();

        void releaseTouchBlockingOverlayStates();

        void resetLiveActivityClocks();

        void resetRuntimeUiSnapshotState();

        void resetVoiceAlertForNewMatch();

        Set<Integer> selectedHeroIds();

        View selectedHeroesWindow();

        void setCachedHexLevels(int[] iArr);

        void setHexShownThisRound(boolean z);

        void setHexTimeoutCount(int i);

        void setHexTriggerSent(boolean z);

        void setLastHexTimestamp(int i);

        void setLastRankAnchorTimestamp(int i);

        SharedMemManager sharedMemManager();

        void showBoardOverlay();

        void showRankWarningOverlay();

        void syncOpponentTextOverlayVisibility();

        void syncQuickActionOverlayVisibility();

        void syncSelectedHeroesVisualVisibility(View view, boolean z);

        void updateSelectedHeroesWindow();
    }

    private InGameOverlayLifecycle() {
    }

    static void hideOnExit(Host host) {
        host.releaseTouchBlockingOverlayStates();
        if (HideSnapshotResetPolicy.shouldResetPaintUiTimestamps(host.exitHidBecauseStale())) {
            host.resetRuntimeUiSnapshotState();
            host.setLastHexTimestamp(0);
        } else {
            host.resetLiveActivityClocks();
        }
        host.setHexShownThisRound(false);
        host.hideHexLevelOverlay();
        host.setCachedHexLevels(null);
        host.setHexTriggerSent(false);
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (sharedMemManager != null) {
            sharedMemManager.clearHexCache();
            sharedMemManager.clearRankAnchorCache();
            sharedMemManager.clearBoardHeroAnchorCache();
        }
        host.setLastRankAnchorTimestamp(-1);
        host.resetVoiceAlertForNewMatch();
        host.hideBoardOverlay();
        ChessBoardView chessBoardView = host.chessBoardView();
        if (chessBoardView != null) {
            chessBoardView.clearData();
        }
        host.hideRankWarningOverlay();
        RankWarningView rankWarningView = host.rankWarningView();
        if (rankWarningView != null) {
            rankWarningView.resetPlayerBindings();
            rankWarningView.setOpponentId(-1);
        }
        host.hideOpponentTextOverlay();
        host.hideShopSlotOverlays();
        if (host.exitHidBecauseStale()) {
            SharedMemManager sharedMemManager2 = host.sharedMemManager();
            if (sharedMemManager2 != null) {
                sharedMemManager2.clearShopSlotCache();
            }
            host.clearStaleMatchPlayerUi();
        }
        host.dismissInteractiveMatchWindows();
        host.syncSelectedHeroesVisualVisibility(host.selectedHeroesWindow(), false);
        host.refreshBoardHeroOverlay();
        host.syncQuickActionOverlayVisibility();
        host.logHidden();
    }

    static void restore(Host host) {
        host.setHexShownThisRound(false);
        host.setHexTimeoutCount(0);
        host.setCachedHexLevels(null);
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (sharedMemManager != null) {
            sharedMemManager.clearHexCache();
            sharedMemManager.clearMatchupCache();
            sharedMemManager.clearRankAnchorCache();
            sharedMemManager.clearBoardHeroAnchorCache();
        }
        host.setLastRankAnchorTimestamp(-1);
        host.resetVoiceAlertForNewMatch();
        if ((host.debugBoxMode() || host.rankWarningEnabled() || (host.opponentHighlightEnabled() && host.opponentShowBox())) && !host.isRankWarningVisible()) {
            host.showRankWarningOverlay();
        }
        if (host.boardOverlayEnabled() && !host.isBoardOverlayVisible()) {
            host.showBoardOverlay();
        }
        if (host.selectedHeroesWindow() != null && !host.selectedHeroIds().isEmpty()) {
            host.updateSelectedHeroesWindow();
        }
        host.refreshBoardHeroOverlay();
        host.syncOpponentTextOverlayVisibility();
        host.syncQuickActionOverlayVisibility();
        host.refreshShopSlotOverlayFromCache();
    }
}
