package com.android.support;

/* loaded from: classes.dex */
final class ServiceDestroyOverlayTeardown {

    interface Host {
        void clearFloatingActionRefs();

        void destroyGameVisualOverlayHost();

        void dismissAllQuickSettingsPopupsImmediate();

        void finishAllInRequestPending();

        void hideBoardOverlay();

        void hideHexLevelOverlay();

        void hideRankWarningOverlay();

        void removeAllInPriorityWindow();

        void removeAuthStatusCard();

        void removeAutoPickHeroWindow();

        void removeChildWindow();

        void removeCustomSwapWindowImmediate();

        void removeDropHeroWindow();

        void removeFloatingBall();

        void removeFloatingView();

        void removeMemoryFunctionWindow();

        void removeOpponentCardingWindow();

        void removeOpponentTextOverlay();

        void removeQuickActionOverlayImmediate();

        void removeSelectedHeroesWindow();

        void removeShopSlotOverlays();

        void removeSpecifiedHexPicker();

        void removeSwapBoardWindowImmediate();

        void removeVerifyOverlay();
    }

    private ServiceDestroyOverlayTeardown() {
    }

    static void apply(Host host) {
        host.removeOpponentTextOverlay();
        host.hideHexLevelOverlay();
        host.removeChildWindow();
        host.removeSpecifiedHexPicker();
        host.removeAutoPickHeroWindow();
        host.removeAllInPriorityWindow();
        host.removeOpponentCardingWindow();
        host.removeMemoryFunctionWindow();
        host.removeDropHeroWindow();
        host.removeSelectedHeroesWindow();
        host.hideBoardOverlay();
        host.hideRankWarningOverlay();
        host.destroyGameVisualOverlayHost();
        host.removeShopSlotOverlays();
        host.removeQuickActionOverlayImmediate();
        host.removeSwapBoardWindowImmediate();
        host.removeCustomSwapWindowImmediate();
        host.removeFloatingView();
        host.removeFloatingBall();
        host.removeAuthStatusCard();
        host.removeVerifyOverlay();
        host.dismissAllQuickSettingsPopupsImmediate();
        host.finishAllInRequestPending();
        host.clearFloatingActionRefs();
    }
}
