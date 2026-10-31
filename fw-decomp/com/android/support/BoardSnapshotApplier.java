package com.android.support;

import com.android.support.BoardPlayerAutoSelectPolicy;
import java.util.List;

/* loaded from: classes.dex */
final class BoardSnapshotApplier {

    interface Host {
        boolean boardOverlayEnabled();

        boolean isBoardOverlayVisible();

        boolean isBoardPlayerManuallySelected();

        int lastAutoSelectedOpponentId();

        int lastBoardPosTimestamp();

        int lastBoardUiSelectedPlayerId();

        int lastBoardUiTimestamp();

        boolean lineupHasFinalUnits();

        void logAutoSelectMaxPieces(int i, List<BoardPositionInfo> list);

        void logAutoSelectOpponent(int i);

        void logAutoSelectOpponentCleared();

        int selectedBoardPlayerId();

        void setBoardPlayerManuallySelected(boolean z);

        void setLastAutoSelectedOpponentId(int i);

        void setLastBoardPosTimestamp(int i);

        void setLastBoardPositions(List<BoardPositionInfo> list);

        void setLastBoardUiSelectedPlayerId(int i);

        void setLastBoardUiTimestamp(int i);

        void setSelectedBoardPlayerId(int i);

        void updateBoardPlayerSelector(List<PlayerInfo> list, List<BoardPositionInfo> list2);

        void updateBoardVisualization();
    }

    private BoardSnapshotApplier() {
    }

    static void apply(Host host, List<BoardPositionInfo> list, int i, List<PlayerInfo> list2, int i2, boolean z, boolean z2) {
        if (BoardSnapshotApplyPolicy.shouldCachePositions(z, z2, list != null)) {
            host.setLastBoardPositions(list);
            if (BoardSnapshotApplyPolicy.shouldUpdateSelector(i, host.lastBoardPosTimestamp())) {
                host.setLastBoardPosTimestamp(i);
                host.updateBoardPlayerSelector(list2, list);
            }
        }
        BoardPlayerAutoSelectPolicy.State stateApply = BoardPlayerAutoSelectPolicy.apply(host.boardOverlayEnabled(), list, i2, host.selectedBoardPlayerId(), host.lastAutoSelectedOpponentId(), host.isBoardPlayerManuallySelected(), (!host.boardOverlayEnabled() || list == null || list.isEmpty()) ? false : host.lineupHasFinalUnits());
        host.setSelectedBoardPlayerId(stateApply.selectedBoardPlayerId);
        host.setLastAutoSelectedOpponentId(stateApply.lastAutoSelectedOpponentId);
        host.setBoardPlayerManuallySelected(stateApply.isBoardPlayerManuallySelected);
        if ("opponent".equals(stateApply.debugReason)) {
            host.logAutoSelectOpponent(stateApply.selectedBoardPlayerId);
        } else if ("opponent-cleared".equals(stateApply.debugReason)) {
            host.logAutoSelectOpponentCleared();
        } else if ("max-pieces".equals(stateApply.debugReason)) {
            host.logAutoSelectMaxPieces(stateApply.selectedBoardPlayerId, list);
        }
        if (BoardSnapshotApplyPolicy.shouldPaintBoard(z, z2, list != null, RuntimeRefreshPolicy.boardSnapshotChanged(host.lastBoardUiTimestamp(), host.lastBoardUiSelectedPlayerId(), i, stateApply.selectedBoardPlayerId, false), host.isBoardOverlayVisible())) {
            host.setLastBoardUiTimestamp(i);
            host.setLastBoardUiSelectedPlayerId(stateApply.selectedBoardPlayerId);
            host.updateBoardVisualization();
        } else if (BoardSnapshotApplyPolicy.shouldResetBoardUiTimestamps(z, z2)) {
            host.setLastBoardUiTimestamp(Integer.MIN_VALUE);
            host.setLastBoardUiSelectedPlayerId(Integer.MIN_VALUE);
        }
    }
}
