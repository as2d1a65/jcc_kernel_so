package com.android.support;

/* loaded from: classes.dex */
final class MainAnnouncementState {
    private final SeenStore seenStore;
    private boolean showing;

    enum Origin {
        AUTOMATIC,
        MANUAL
    }

    interface SeenStore {
        boolean isSeen();

        void markSeen();
    }

    MainAnnouncementState(SeenStore seenStore) {
        this.seenStore = seenStore;
    }

    boolean canShowAutomatically() {
        return (this.showing || this.seenStore.isSeen()) ? false : true;
    }

    boolean canShowManually() {
        return !this.showing;
    }

    void onShown(Origin origin) {
        this.showing = true;
        if (origin == Origin.AUTOMATIC) {
            this.seenStore.markSeen();
        }
    }

    void onDismissed() {
        this.showing = false;
    }
}
