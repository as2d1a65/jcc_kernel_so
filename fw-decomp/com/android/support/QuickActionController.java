package com.android.support;

import com.android.support.AllInAlertText;

/* loaded from: classes.dex */
final class QuickActionController {
    static final long ALL_IN_REQUEST_WATCHDOG_MS = 7500;
    private long allInArmedAtMs;
    private int allInLastReportedReason;
    private boolean allInRequestPending;
    private boolean allInRequestStarting;
    private final Runnable allInRequestTimeout;
    private boolean allInRequested;
    private final Host host;

    interface Host {
        int allInAckTimeoutMs();

        boolean allInAntiMistouch();

        int allInBlockedAction();

        int allInCostMask();

        boolean allInKeepLineup();

        int allInMaxTargets();

        int[] allInPriorityHeroIds();

        int allInReserveGold();

        void animateConfirm(Object obj);

        void announceAllInBlocked(AllInAlertText.Parts parts);

        boolean autoRefreshEnabled();

        int autoRefreshFallbackMs();

        void cancelTimeout(Runnable runnable);

        long elapsedRealtimeMs();

        boolean isMatchActive();

        void persistAllInConfig(int i, int i2, int i3, int i4);

        void persistAutoRefreshEnabled(boolean z);

        void runOnMainThread(Runnable runnable);

        void scheduleTimeout(Runnable runnable, long j);

        void setAutoRefreshEnabled(boolean z);

        void setAutoRefreshFallbackMs(int i);

        void setAutoRefreshShop(boolean z);

        boolean sharedMemoryReady();

        void showMessage(String str);

        void startAllIn(int i, int i2, int i3, boolean z, int i4, int i5, int[] iArr, ResultCallback resultCallback);

        void stopAllIn(ResultCallback resultCallback);

        void updateAllInButton();

        void updateRefreshButton(boolean z);
    }

    interface ResultCallback {
        void onResult(boolean z, String str);
    }

    QuickActionController(final Host host) {
        this.host = host;
        this.allInRequestTimeout = new Runnable() { // from class: com.android.support.QuickActionController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1406lambda$new$0$comandroidsupportQuickActionController(host);
            }
        };
    }

    /* renamed from: lambda$new$0$com-android-support-QuickActionController, reason: not valid java name */
    /* synthetic */ void m1406lambda$new$0$comandroidsupportQuickActionController(Host host) {
        if (this.allInRequestPending) {
            this.allInRequestPending = false;
            this.allInRequested = !this.allInRequestStarting;
            this.allInRequestStarting = false;
            host.updateAllInButton();
            host.showMessage("梭哈请求超时，请检查游戏连接");
        }
    }

    void toggleAutoRefresh(Object obj) {
        boolean zSharedMemoryReady = this.host.sharedMemoryReady();
        Host host = this.host;
        if (!zSharedMemoryReady) {
            host.showMessage("共享内存未初始化");
            return;
        }
        host.animateConfirm(obj);
        boolean zAutoRefreshEnabled = this.host.autoRefreshEnabled();
        boolean z = !zAutoRefreshEnabled;
        this.host.setAutoRefreshEnabled(z);
        if (!zAutoRefreshEnabled) {
            Host host2 = this.host;
            host2.setAutoRefreshFallbackMs(host2.autoRefreshFallbackMs());
        }
        this.host.setAutoRefreshShop(z);
        this.host.persistAutoRefreshEnabled(z);
        this.host.updateRefreshButton(z);
        this.host.showMessage(!zAutoRefreshEnabled ? "✅ 自动刷新已开启" : "⏸ 自动刷新已关闭");
    }

    void toggleAllIn(Object obj) {
        if (!this.host.sharedMemoryReady()) {
            this.host.showMessage("共享内存未初始化");
            return;
        }
        if (this.allInRequestPending) {
            return;
        }
        if (!this.allInRequested && !this.host.isMatchActive()) {
            this.host.animateConfirm(obj);
            this.host.showMessage("请先进入对局再使用梭哈");
            return;
        }
        this.host.animateConfirm(obj);
        if (!this.allInRequested) {
            long jElapsedRealtimeMs = this.host.elapsedRealtimeMs();
            if (AntiMistouchGate.decide(this.host.allInAntiMistouch(), this.allInArmedAtMs, jElapsedRealtimeMs) == 1) {
                this.allInArmedAtMs = jElapsedRealtimeMs;
                this.host.showMessage("防误触已开启，再点一次开始梭哈");
                return;
            }
        }
        this.allInArmedAtMs = 0L;
        this.allInLastReportedReason = 0;
        this.allInRequestStarting = !this.allInRequested;
        this.allInRequestPending = true;
        this.host.cancelTimeout(this.allInRequestTimeout);
        this.host.scheduleTimeout(this.allInRequestTimeout, ALL_IN_REQUEST_WATCHDOG_MS);
        this.host.updateAllInButton();
        if (this.allInRequested) {
            this.host.stopAllIn(new ResultCallback() { // from class: com.android.support.QuickActionController$$ExternalSyntheticLambda0
                @Override // com.android.support.QuickActionController.ResultCallback
                public final void onResult(boolean z, String str) {
                    this.f$0.m1407lambda$toggleAllIn$1$comandroidsupportQuickActionController(z, str);
                }
            });
            return;
        }
        normalizeAllInPreferences();
        int iNormalizeAllInMaxTargets = QuickActionOverlayPolicy.normalizeAllInMaxTargets(this.host.allInMaxTargets());
        int iNormalizeAllInReserveGold = QuickActionOverlayPolicy.normalizeAllInReserveGold(this.host.allInReserveGold());
        int iNormalizeAllInBlockedAction = QuickActionOverlayPolicy.normalizeAllInBlockedAction(this.host.allInBlockedAction());
        int iNormalizeAllInCostMask = QuickActionOverlayPolicy.normalizeAllInCostMask(this.host.allInCostMask());
        int[] iArrAllInPriorityHeroIds = this.host.allInPriorityHeroIds();
        Host host = this.host;
        boolean zAllInKeepLineup = host.allInKeepLineup();
        int iNormalizeAllInAckTimeoutMs = QuickActionOverlayPolicy.normalizeAllInAckTimeoutMs(this.host.allInAckTimeoutMs());
        if (iArrAllInPriorityHeroIds == null) {
            iArrAllInPriorityHeroIds = new int[0];
        }
        host.startAllIn(iNormalizeAllInMaxTargets, iNormalizeAllInReserveGold, iNormalizeAllInBlockedAction, zAllInKeepLineup, iNormalizeAllInAckTimeoutMs, iNormalizeAllInCostMask, iArrAllInPriorityHeroIds, new ResultCallback() { // from class: com.android.support.QuickActionController$$ExternalSyntheticLambda1
            @Override // com.android.support.QuickActionController.ResultCallback
            public final void onResult(boolean z, String str) {
                this.f$0.m1408lambda$toggleAllIn$2$comandroidsupportQuickActionController(z, str);
            }
        });
    }

    /* renamed from: lambda$toggleAllIn$1$com-android-support-QuickActionController, reason: not valid java name */
    /* synthetic */ void m1407lambda$toggleAllIn$1$comandroidsupportQuickActionController(boolean z, String str) {
        handleAllInActionTerminal(z, str, false);
    }

    /* renamed from: lambda$toggleAllIn$2$com-android-support-QuickActionController, reason: not valid java name */
    /* synthetic */ void m1408lambda$toggleAllIn$2$comandroidsupportQuickActionController(boolean z, String str) {
        handleAllInActionTerminal(z, str, true);
    }

    void onAllInStatusReported(boolean z, int i) {
        boolean z2 = this.allInRequested;
        boolean z3 = this.allInRequestPending;
        this.allInArmedAtMs = 0L;
        finishAllInRequestPending();
        this.allInRequested = z;
        this.host.updateAllInButton();
        if (z) {
            this.allInLastReportedReason = 0;
            return;
        }
        if (i == this.allInLastReportedReason) {
            return;
        }
        String strAllInStopMessage = QuickActionOverlayPolicy.allInStopMessage(i);
        AllInAlertText.Parts partsForStopReason = AllInAlertText.forStopReason(i, strAllInStopMessage);
        if (partsForStopReason != null) {
            this.host.announceAllInBlocked(partsForStopReason);
            this.allInLastReportedReason = i;
        } else if ((z2 || z3) && strAllInStopMessage != null) {
            this.host.showMessage(strAllInStopMessage);
            this.allInLastReportedReason = i;
        }
    }

    void finishAllInRequestPending() {
        this.allInRequestPending = false;
        this.allInRequestStarting = false;
        this.host.cancelTimeout(this.allInRequestTimeout);
    }

    void normalizeAllInPreferences() {
        this.host.persistAllInConfig(QuickActionOverlayPolicy.normalizeAllInMaxTargets(this.host.allInMaxTargets()), QuickActionOverlayPolicy.normalizeAllInReserveGold(this.host.allInReserveGold()), QuickActionOverlayPolicy.normalizeAllInBlockedAction(this.host.allInBlockedAction()), QuickActionOverlayPolicy.normalizeAllInCostMask(this.host.allInCostMask()));
    }

    boolean allInRequested() {
        return this.allInRequested;
    }

    boolean allInRequestPending() {
        return this.allInRequestPending;
    }

    boolean allInRequestStarting() {
        return this.allInRequestStarting;
    }

    private void handleAllInActionTerminal(final boolean z, final String str, final boolean z2) {
        this.host.runOnMainThread(new Runnable() { // from class: com.android.support.QuickActionController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1405x28866b95(z, z2, str);
            }
        });
    }

    /* renamed from: lambda$handleAllInActionTerminal$3$com-android-support-QuickActionController, reason: not valid java name */
    /* synthetic */ void m1405x28866b95(boolean z, boolean z2, String str) {
        if (this.allInRequestPending) {
            finishAllInRequestPending();
            if (z) {
                this.allInRequested = z2;
                this.host.updateAllInButton();
                return;
            }
            this.allInRequested = z2 ? false : this.allInRequested;
            this.host.updateAllInButton();
            if (AllInAlertText.shouldAnnounce(this.allInLastReportedReason)) {
                return;
            }
            this.host.showMessage(allInActionFailureMessage(str));
        }
    }

    static String allInActionFailureMessage(String str) {
        if ("unknown_method".equals(str)) {
            return "梭哈不可用：当前游戏核心版本过旧，请重新注入当前候选";
        }
        if ("execution_failed".equals(str)) {
            return "当前没有满足起追条件且牌库可达的目标";
        }
        if ("no_active_match_generation".equals(str)) {
            return "请先进入对局再使用梭哈";
        }
        if ("invalid_all_in_params".equals(str) || "invalid_action_params".equals(str)) {
            return "梭哈设置无效，请重新保存设置";
        }
        if ("not_ready".equals(str) || "not_connected".equals(str) || "session_replaced".equals(str) || "peer_disconnected_after_write".equals(str)) {
            return "梭哈连接正在恢复，请稍后重试";
        }
        if ("request_timeout_before_write".equals(str) || "response_timeout".equals(str)) {
            return "梭哈请求超时，请检查游戏连接";
        }
        StringBuilder sb = new StringBuilder("梭哈启动失败（");
        if (str == null) {
            str = "unknown_error";
        }
        return sb.append(str).append("）").toString();
    }
}
