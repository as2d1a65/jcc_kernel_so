package com.android.support;

import java.util.Objects;

/* loaded from: classes.dex */
final class VoiceAlertBannerLifecycle implements AutoCloseable {
    static final long COMPLETION_HOLD_MS = 800;
    static final int FAILURE_FADE_MS = 120;
    static final int NORMAL_FADE_MS = 220;
    private String activeRequestKey;
    private boolean closed;
    private long generation;
    private Cancelable pendingHide;
    private final Scheduler scheduler;
    private final Surface surface;
    private State state = State.HIDDEN;
    private boolean enabled = true;

    interface Cancelable {
        void cancel();
    }

    interface Scheduler {
        Cancelable schedule(long j, Runnable runnable);
    }

    enum State {
        HIDDEN,
        PREVIEW,
        PLAYING,
        HOLDING
    }

    interface Surface {
        void hide(int i);

        void showAlert(VoiceAlertRequest voiceAlertRequest);

        void showPreview();
    }

    VoiceAlertBannerLifecycle(Surface surface, Scheduler scheduler) {
        this.surface = (Surface) Objects.requireNonNull(surface, "surface");
        this.scheduler = (Scheduler) Objects.requireNonNull(scheduler, "scheduler");
    }

    boolean showPreview() {
        if (!this.closed && this.enabled && this.state != State.PLAYING) {
            long j = this.generation + 1;
            this.generation = j;
            Cancelable cancelableDetachPendingHide = detachPendingHide();
            this.activeRequestKey = null;
            this.state = State.HIDDEN;
            safeCancel(cancelableDetachPendingHide);
            if (isOpenGeneration(j) && this.enabled) {
                this.state = State.PREVIEW;
                try {
                    this.surface.showPreview();
                    return isOpenGeneration(j) && this.state == State.PREVIEW;
                } catch (RuntimeException unused) {
                    recoverFromSurfaceFailure(j);
                }
            }
        }
        return false;
    }

    void setEnabled(boolean z) {
        if (this.closed || this.enabled == z) {
            return;
        }
        this.enabled = z;
        if (z) {
            return;
        }
        transitionToHidden(0);
    }

    void onPlaybackStarted(VoiceAlertRequest voiceAlertRequest) {
        if (this.closed || !this.enabled || voiceAlertRequest == null) {
            return;
        }
        long j = this.generation + 1;
        this.generation = j;
        Cancelable cancelableDetachPendingHide = detachPendingHide();
        this.activeRequestKey = null;
        this.state = State.HIDDEN;
        safeCancel(cancelableDetachPendingHide);
        if (isOpenGeneration(j) && this.enabled) {
            this.activeRequestKey = voiceAlertRequest.key();
            this.state = State.PLAYING;
            try {
                this.surface.showAlert(voiceAlertRequest);
                if (isOpenGeneration(j) && this.state == State.PLAYING) {
                    voiceAlertRequest.key().equals(this.activeRequestKey);
                }
            } catch (RuntimeException unused) {
                recoverFromSurfaceFailure(j);
            }
        }
    }

    void onPlaybackCompleted(VoiceAlertRequest voiceAlertRequest) {
        final VoiceAlertBannerLifecycle voiceAlertBannerLifecycle;
        if (!isCurrentPlayingRequest(voiceAlertRequest)) {
            return;
        }
        final long j = this.generation + 1;
        this.generation = j;
        Cancelable cancelableDetachPendingHide = detachPendingHide();
        this.state = State.HOLDING;
        final String str = this.activeRequestKey;
        safeCancel(cancelableDetachPendingHide);
        if (!isHolding(j, str)) {
            return;
        }
        final ScheduleAttempt scheduleAttempt = new ScheduleAttempt();
        try {
            voiceAlertBannerLifecycle = this;
            try {
                Cancelable cancelableSchedule = this.scheduler.schedule(COMPLETION_HOLD_MS, new Runnable() { // from class: com.android.support.VoiceAlertBannerLifecycle$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1589x81e5b08(scheduleAttempt, j, str);
                    }
                });
                if (scheduleAttempt.fired) {
                    return;
                }
                if (!voiceAlertBannerLifecycle.isHolding(j, str)) {
                    voiceAlertBannerLifecycle.safeCancel(cancelableSchedule);
                } else {
                    voiceAlertBannerLifecycle.pendingHide = cancelableSchedule;
                }
            } catch (RuntimeException unused) {
                voiceAlertBannerLifecycle.recoverFromScheduleFailure(j, str);
            }
        } catch (RuntimeException unused2) {
            voiceAlertBannerLifecycle = this;
        }
    }

    /* renamed from: lambda$onPlaybackCompleted$0$com-android-support-VoiceAlertBannerLifecycle, reason: not valid java name */
    /* synthetic */ void m1589x81e5b08(ScheduleAttempt scheduleAttempt, long j, String str) {
        scheduleAttempt.fired = true;
        completeHold(j, str);
    }

    void onPlaybackFailed(VoiceAlertRequest voiceAlertRequest) {
        if (isCurrentPlayingRequest(voiceAlertRequest)) {
            transitionToHidden(120);
        }
    }

    void hideCurrent() {
        if (this.closed) {
            return;
        }
        transitionToHidden(0);
    }

    State state() {
        return this.state;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        long j = this.generation + 1;
        this.generation = j;
        Cancelable cancelableDetachPendingHide = detachPendingHide();
        this.activeRequestKey = null;
        this.state = State.HIDDEN;
        safeCancel(cancelableDetachPendingHide);
        if (this.generation == j) {
            safeHide(0);
        }
    }

    private boolean isCurrentPlayingRequest(VoiceAlertRequest voiceAlertRequest) {
        return !this.closed && this.enabled && this.state == State.PLAYING && voiceAlertRequest != null && voiceAlertRequest.key().equals(this.activeRequestKey);
    }

    private boolean isOpenGeneration(long j) {
        return !this.closed && this.generation == j;
    }

    private boolean isHolding(long j, String str) {
        return isOpenGeneration(j) && this.enabled && this.state == State.HOLDING && str.equals(this.activeRequestKey);
    }

    private void completeHold(long j, String str) {
        if (isHolding(j, str)) {
            this.generation++;
            this.pendingHide = null;
            this.activeRequestKey = null;
            this.state = State.HIDDEN;
            safeHide(220);
        }
    }

    private void recoverFromScheduleFailure(long j, String str) {
        if (isHolding(j, str)) {
            transitionToHidden(220);
        }
    }

    private void recoverFromSurfaceFailure(long j) {
        if (isOpenGeneration(j)) {
            transitionToHidden(0);
        }
    }

    private void transitionToHidden(int i) {
        long j = this.generation + 1;
        this.generation = j;
        Cancelable cancelableDetachPendingHide = detachPendingHide();
        this.activeRequestKey = null;
        this.state = State.HIDDEN;
        safeCancel(cancelableDetachPendingHide);
        if (isOpenGeneration(j)) {
            safeHide(i);
        }
    }

    private Cancelable detachPendingHide() {
        Cancelable cancelable = this.pendingHide;
        this.pendingHide = null;
        return cancelable;
    }

    private void safeCancel(Cancelable cancelable) {
        if (cancelable != null) {
            try {
                cancelable.cancel();
            } catch (RuntimeException unused) {
            }
        }
    }

    private void safeHide(int i) {
        try {
            this.surface.hide(i);
        } catch (RuntimeException unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class ScheduleAttempt {
        boolean fired;

        private ScheduleAttempt() {
        }
    }
}
