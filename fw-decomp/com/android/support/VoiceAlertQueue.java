package com.android.support;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public final class VoiceAlertQueue implements AutoCloseable {
    private int cancellationsInProgress;
    private boolean closed;
    private Attempt current;
    private final Dispatcher dispatcher;
    private final int maxSuccessfulPlays;
    private final Object lock = new Object();
    private final ArrayDeque<VoiceAlertRequest> pending = new ArrayDeque<>();
    private final Set<String> activeKeys = new HashSet();
    private final Map<String, Integer> successfulCounts = new HashMap();
    private final Map<String, Integer> lastSuccessfulCopies = new HashMap();

    public interface Completion {
        void complete(boolean z);
    }

    public interface Dispatcher {
        void cancelCurrent();

        void start(VoiceAlertRequest voiceAlertRequest, Completion completion);
    }

    public VoiceAlertQueue(int i, Dispatcher dispatcher) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxSuccessfulPlays must be positive");
        }
        if (dispatcher == null) {
            throw new IllegalArgumentException("dispatcher must not be null");
        }
        this.maxSuccessfulPlays = i;
        this.dispatcher = dispatcher;
    }

    public boolean offer(VoiceAlertRequest voiceAlertRequest) {
        if (voiceAlertRequest == null) {
            return false;
        }
        synchronized (this.lock) {
            String strKey = voiceAlertRequest.key();
            Integer num = this.lastSuccessfulCopies.get(strKey);
            if (!this.closed && !this.activeKeys.contains(strKey) && successfulCountLocked(strKey) < this.maxSuccessfulPlays && (num == null || voiceAlertRequest.getCopies() > num.intValue())) {
                this.pending.addLast(voiceAlertRequest);
                this.activeKeys.add(strKey);
                drain();
                return true;
            }
            return false;
        }
    }

    public void resetForNewMatch() {
        Attempt attemptClaimCurrentCancellationLocked;
        synchronized (this.lock) {
            attemptClaimCurrentCancellationLocked = claimCurrentCancellationLocked();
            this.pending.clear();
            this.activeKeys.clear();
            this.successfulCounts.clear();
            this.lastSuccessfulCopies.clear();
        }
        if (attemptClaimCurrentCancellationLocked != null) {
            finishCancellation(attemptClaimCurrentCancellationLocked);
        }
    }

    public boolean isBusy() {
        boolean z;
        synchronized (this.lock) {
            z = this.current != null;
        }
        return z;
    }

    public int pendingCount() {
        int size;
        synchronized (this.lock) {
            size = this.pending.size();
        }
        return size;
    }

    int successfulCount(String str) {
        int iSuccessfulCountLocked;
        synchronized (this.lock) {
            iSuccessfulCountLocked = successfulCountLocked(str);
        }
        return iSuccessfulCountLocked;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this.lock) {
            if (this.closed) {
                return;
            }
            this.closed = true;
            Attempt attemptClaimCurrentCancellationLocked = claimCurrentCancellationLocked();
            this.pending.clear();
            this.activeKeys.clear();
            this.successfulCounts.clear();
            this.lastSuccessfulCopies.clear();
            if (attemptClaimCurrentCancellationLocked != null) {
                finishCancellation(attemptClaimCurrentCancellationLocked);
            }
        }
    }

    private void drain() {
        boolean z;
        boolean z2;
        do {
            synchronized (this.lock) {
                if (!this.closed && this.cancellationsInProgress == 0 && this.current == null && !this.pending.isEmpty()) {
                    Attempt attempt = new Attempt(this.pending.removeFirst());
                    z = true;
                    attempt.startInProgress = true;
                    this.current = attempt;
                    try {
                        this.dispatcher.start(attempt.request, attempt);
                        z2 = false;
                    } catch (RuntimeException unused) {
                        z2 = true;
                    }
                    synchronized (this.lock) {
                        attempt.startInProgress = false;
                        if (z2 && this.current == attempt && !attempt.completed) {
                            attempt.completed = true;
                            this.current = null;
                            this.activeKeys.remove(attempt.request.key());
                        }
                        if (!attempt.cancellationClaimed || attempt.cancellationStarted) {
                            attempt = null;
                        } else {
                            attempt.cancellationStarted = true;
                        }
                        if (attempt != null || this.closed || this.cancellationsInProgress != 0 || this.current != null || this.pending.isEmpty()) {
                            z = false;
                        }
                    }
                    if (attempt != null) {
                        finishCancellation(attempt);
                        return;
                    }
                }
                return;
            }
        } while (z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void completeAttempt(Attempt attempt, boolean z) {
        synchronized (this.lock) {
            if (!attempt.completed && this.current == attempt) {
                attempt.completed = true;
                this.current = null;
                String strKey = attempt.request.key();
                this.activeKeys.remove(strKey);
                if (z) {
                    this.successfulCounts.put(strKey, Integer.valueOf(successfulCountLocked(strKey) + 1));
                    Integer num = this.lastSuccessfulCopies.get(strKey);
                    int copies = attempt.request.getCopies();
                    if (num == null || copies > num.intValue()) {
                        this.lastSuccessfulCopies.put(strKey, Integer.valueOf(copies));
                    }
                }
                boolean z2 = attempt.startInProgress;
                if (z2) {
                    return;
                }
                drain();
            }
        }
    }

    private Attempt claimCurrentCancellationLocked() {
        Attempt attempt = this.current;
        if (attempt == null) {
            return null;
        }
        attempt.completed = true;
        this.current = null;
        attempt.cancellationClaimed = true;
        this.cancellationsInProgress++;
        if (attempt.startInProgress) {
            return null;
        }
        attempt.cancellationStarted = true;
        return attempt;
    }

    private int successfulCountLocked(String str) {
        Integer num = this.successfulCounts.get(str);
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    private void cancelSafely() {
        try {
            this.dispatcher.cancelCurrent();
        } catch (RuntimeException unused) {
        }
    }

    private void finishCancellation(Attempt attempt) {
        boolean z = true;
        boolean z2 = false;
        try {
            cancelSafely();
            synchronized (this.lock) {
                if (!attempt.cancellationFinished) {
                    attempt.cancellationFinished = true;
                    int i = this.cancellationsInProgress - 1;
                    this.cancellationsInProgress = i;
                    if (i != 0 || this.closed || this.current != null || this.pending.isEmpty()) {
                        z = false;
                    }
                    z2 = z;
                }
            }
            if (z2) {
                drain();
            }
        } catch (Throwable th) {
            synchronized (this.lock) {
                if (!attempt.cancellationFinished) {
                    attempt.cancellationFinished = true;
                    int i2 = this.cancellationsInProgress - 1;
                    this.cancellationsInProgress = i2;
                    if (i2 != 0 || this.closed || this.current != null || this.pending.isEmpty()) {
                        z = false;
                    }
                    z2 = z;
                }
                if (z2) {
                    drain();
                }
                throw th;
            }
        }
    }

    private final class Attempt implements Completion {
        boolean cancellationClaimed;
        boolean cancellationFinished;
        boolean cancellationStarted;
        boolean completed;
        final VoiceAlertRequest request;
        boolean startInProgress;

        Attempt(VoiceAlertRequest voiceAlertRequest) {
            this.request = voiceAlertRequest;
        }

        @Override // com.android.support.VoiceAlertQueue.Completion
        public void complete(boolean z) {
            VoiceAlertQueue.this.completeAttempt(this, z);
        }
    }
}
