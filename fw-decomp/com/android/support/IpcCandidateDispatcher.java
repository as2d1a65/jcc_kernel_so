package com.android.support;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.BooleanSupplier;

/* loaded from: classes.dex */
final class IpcCandidateDispatcher {
    private boolean closed;
    private final int limit;
    private final Set<Candidate> pending = new HashSet();

    IpcCandidateDispatcher(int i) {
        this.limit = i;
    }

    void dispatch(Executor executor, final BooleanSupplier booleanSupplier, final Runnable runnable, Runnable runnable2) {
        if (!booleanSupplier.getAsBoolean()) {
            runnable2.run();
            return;
        }
        final Candidate candidate = new Candidate(runnable2);
        synchronized (this) {
            if (!this.closed && this.pending.size() < this.limit) {
                this.pending.add(candidate);
                try {
                    executor.execute(new Runnable() { // from class: com.android.support.IpcCandidateDispatcher$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1209lambda$dispatch$0$comandroidsupportIpcCandidateDispatcher(booleanSupplier, candidate, runnable);
                        }
                    });
                    return;
                } catch (RejectedExecutionException unused) {
                    synchronized (this) {
                        this.pending.remove(candidate);
                        candidate.cancel();
                        return;
                    }
                }
            }
            runnable2.run();
        }
    }

    /* renamed from: lambda$dispatch$0$com-android-support-IpcCandidateDispatcher, reason: not valid java name */
    /* synthetic */ void m1209lambda$dispatch$0$comandroidsupportIpcCandidateDispatcher(BooleanSupplier booleanSupplier, Candidate candidate, Runnable runnable) {
        try {
            if (!booleanSupplier.getAsBoolean() || candidate.cancelled) {
                candidate.cancel();
            } else {
                runnable.run();
            }
            synchronized (this) {
                this.pending.remove(candidate);
            }
        } catch (Throwable th) {
            synchronized (this) {
                this.pending.remove(candidate);
                throw th;
            }
        }
    }

    synchronized void close() {
        this.closed = true;
        Iterator<Candidate> it = this.pending.iterator();
        while (it.hasNext()) {
            it.next().cancel();
        }
        this.pending.clear();
    }

    synchronized int size() {
        return this.pending.size();
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class Candidate {
        volatile boolean cancelled;
        final Runnable close;

        Candidate(Runnable runnable) {
            this.close = runnable;
        }

        synchronized void cancel() {
            if (this.cancelled) {
                return;
            }
            this.cancelled = true;
            this.close.run();
        }
    }
}
