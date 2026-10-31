package com.android.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/* loaded from: classes.dex */
final class DisplayProbeDispatcher implements AutoCloseable {
    private boolean closed;
    private boolean inFlight;
    private final Scheduler main;
    private final IntSupplier probe;
    private final Scheduler worker;
    private final Object lock = new Object();
    private final List<IntConsumer> callbacks = new ArrayList();

    interface Scheduler {
        void execute(Runnable runnable);
    }

    DisplayProbeDispatcher(Scheduler scheduler, Scheduler scheduler2, IntSupplier intSupplier) {
        this.worker = (Scheduler) Objects.requireNonNull(scheduler, "worker");
        this.main = (Scheduler) Objects.requireNonNull(scheduler2, "main");
        this.probe = (IntSupplier) Objects.requireNonNull(intSupplier, "probe");
    }

    boolean request(IntConsumer intConsumer) {
        Objects.requireNonNull(intConsumer, "callback");
        synchronized (this.lock) {
            if (this.closed) {
                return false;
            }
            this.callbacks.add(intConsumer);
            if (this.inFlight) {
                return false;
            }
            this.inFlight = true;
            try {
                this.worker.execute(new Runnable() { // from class: com.android.support.DisplayProbeDispatcher$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.runProbe();
                    }
                });
            } catch (RuntimeException unused) {
                postResult(-1);
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runProbe() {
        int asInt;
        try {
            asInt = this.probe.getAsInt();
        } catch (RuntimeException unused) {
            asInt = -1;
        }
        postResult(asInt);
    }

    private void postResult(final int i) {
        try {
            this.main.execute(new Runnable() { // from class: com.android.support.DisplayProbeDispatcher$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m142lambda$postResult$0$comandroidsupportDisplayProbeDispatcher(i);
                }
            });
        } catch (RuntimeException unused) {
            synchronized (this.lock) {
                this.callbacks.clear();
                this.inFlight = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: deliver, reason: merged with bridge method [inline-methods] */
    public void m142lambda$postResult$0$comandroidsupportDisplayProbeDispatcher(int i) {
        synchronized (this.lock) {
            if (this.closed) {
                this.callbacks.clear();
                this.inFlight = false;
                return;
            }
            ArrayList arrayList = new ArrayList(this.callbacks);
            this.callbacks.clear();
            this.inFlight = false;
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                try {
                    ((IntConsumer) it.next()).accept(i);
                } catch (RuntimeException unused) {
                }
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this.lock) {
            this.closed = true;
            this.callbacks.clear();
        }
    }
}
