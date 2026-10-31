package com.android.support;

import android.os.Handler;

/* loaded from: classes.dex */
final class GameDataRefreshCoordinator {
    private final Host host;
    private Runnable loop;

    interface Host {
        Handler autoRefreshHandler();

        boolean gameHudSessionReady();

        boolean needsAutoRefresh();

        void refreshAllData();

        void resetRuntimeUiSnapshotState();

        boolean sessionActiveLocal();

        SharedMemManager sharedMemManager();
    }

    GameDataRefreshCoordinator(Host host) {
        this.host = host;
    }

    void start() {
        stop();
        this.host.resetRuntimeUiSnapshotState();
        this.loop = new Runnable() { // from class: com.android.support.GameDataRefreshCoordinator.1
            @Override // java.lang.Runnable
            public void run() {
                boolean zNeedsAutoRefresh = GameDataRefreshCoordinator.this.host.needsAutoRefresh();
                if (zNeedsAutoRefresh && GameDataRefreshCoordinator.this.host.sharedMemManager() != null) {
                    GameDataRefreshCoordinator.this.host.refreshAllData();
                }
                if (zNeedsAutoRefresh) {
                    GameDataRefreshCoordinator.this.host.autoRefreshHandler().postDelayed(this, RuntimeRefreshPolicy.nextDelayMs(GameDataRefreshCoordinator.this.host.gameHudSessionReady(), GameDataRefreshCoordinator.this.host.sessionActiveLocal()));
                }
            }
        };
        this.host.autoRefreshHandler().post(this.loop);
    }

    void stop() {
        if (this.loop != null) {
            this.host.autoRefreshHandler().removeCallbacks(this.loop);
            this.loop = null;
        }
    }

    void sync() {
        if (this.host.needsAutoRefresh()) {
            start();
        } else {
            stop();
        }
    }
}
