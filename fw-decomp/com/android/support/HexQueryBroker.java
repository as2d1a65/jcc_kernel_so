package com.android.support;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
final class HexQueryBroker {
    private final Delivery delivery;
    private boolean inFlight;
    private final Scheduler scheduler;
    private final Sender sender;
    private Cancellable timeout;
    private final long timeoutMs;
    private final List<Callback> waiters = new ArrayList();

    interface Callback {
        void onResult(int[] iArr);
    }

    interface Cancellable {
        void cancel();
    }

    interface Delivery {
        void deliver(Runnable runnable);
    }

    interface Scheduler {
        Cancellable schedule(Runnable runnable, long j);
    }

    interface Sender {
        void sendQuery();
    }

    HexQueryBroker(Scheduler scheduler, Sender sender, Delivery delivery, long j) {
        if (scheduler == null || sender == null || delivery == null || j <= 0) {
            throw new IllegalArgumentException("broker dependencies required");
        }
        this.scheduler = scheduler;
        this.sender = sender;
        this.delivery = delivery;
        this.timeoutMs = j;
    }

    void request(Callback callback) {
        boolean z;
        if (callback == null) {
            return;
        }
        synchronized (this) {
            this.waiters.add(callback);
            if (this.inFlight) {
                z = false;
            } else {
                z = true;
                this.inFlight = true;
            }
        }
        if (z) {
            Cancellable cancellableSchedule = this.scheduler.schedule(new Runnable() { // from class: com.android.support.HexQueryBroker$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1204lambda$request$0$comandroidsupportHexQueryBroker();
                }
            }, this.timeoutMs);
            synchronized (this) {
                if (this.inFlight) {
                    this.timeout = cancellableSchedule;
                } else {
                    cancellableSchedule.cancel();
                }
            }
            this.sender.sendQuery();
        }
    }

    /* renamed from: lambda$request$0$com-android-support-HexQueryBroker, reason: not valid java name */
    /* synthetic */ void m1204lambda$request$0$comandroidsupportHexQueryBroker() {
        complete(null);
    }

    void complete(int[] iArr) {
        synchronized (this) {
            if (this.inFlight) {
                this.inFlight = false;
                ArrayList<Callback> arrayList = new ArrayList(this.waiters);
                this.waiters.clear();
                Cancellable cancellable = this.timeout;
                this.timeout = null;
                if (cancellable != null) {
                    cancellable.cancel();
                }
                for (final Callback callback : arrayList) {
                    final int[] iArr2 = iArr == null ? null : (int[]) iArr.clone();
                    this.delivery.deliver(new Runnable() { // from class: com.android.support.HexQueryBroker$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            callback.onResult(iArr2);
                        }
                    });
                }
            }
        }
    }

    void cancelPending() {
        complete(null);
    }

    synchronized boolean isInFlight() {
        return this.inFlight;
    }
}
