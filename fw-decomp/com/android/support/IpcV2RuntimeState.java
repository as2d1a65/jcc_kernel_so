package com.android.support;

import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public final class IpcV2RuntimeState<T> {
    private Session<T> activeSession;
    private long generation;
    private boolean running;

    public interface Closer<T> {
        void close(T t);
    }

    public static final class Candidate<T> {
        private final AtomicBoolean authenticated;
        private final AtomicBoolean claimed;
        private final AtomicBoolean closed;
        private final long connectionId;
        private final long serverGeneration;
        private final T value;

        private Candidate(long j, long j2, T t) {
            this.authenticated = new AtomicBoolean(false);
            this.claimed = new AtomicBoolean(false);
            this.closed = new AtomicBoolean(false);
            this.serverGeneration = j;
            this.connectionId = j2;
            this.value = t;
        }

        public long serverGeneration() {
            return this.serverGeneration;
        }

        public long connectionId() {
            return this.connectionId;
        }

        public T value() {
            return this.value;
        }

        public void markAuthenticated() {
            this.authenticated.set(true);
        }

        public boolean isAuthenticated() {
            return this.authenticated.get();
        }
    }

    public static final class Session<T> {
        private final AtomicBoolean closed;
        private final Closer<T> closer;
        private final long connectionId;
        private final long serverGeneration;
        private final String sessionId;
        private final T value;

        private Session(long j, long j2, String str, T t, Closer<T> closer) {
            this.closed = new AtomicBoolean(false);
            this.serverGeneration = j;
            this.connectionId = j2;
            this.sessionId = str;
            this.value = t;
            this.closer = closer;
        }

        public long serverGeneration() {
            return this.serverGeneration;
        }

        public long connectionId() {
            return this.connectionId;
        }

        public String sessionId() {
            return this.sessionId;
        }

        public T value() {
            return this.value;
        }

        public boolean isClosed() {
            return this.closed.get();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void closeOnce() {
            if (this.closed.compareAndSet(false, true)) {
                this.closer.close(this.value);
            }
        }
    }

    public synchronized long start() {
        if (!this.running) {
            long j = this.generation;
            if (j == Long.MAX_VALUE) {
                throw new IllegalStateException("generation exhausted");
            }
            this.generation = j + 1;
            this.running = true;
        }
        return this.generation;
    }

    public synchronized long generation() {
        return this.generation;
    }

    public synchronized boolean isRunning() {
        return this.running;
    }

    public synchronized Candidate<T> newCandidate(long j, long j2, T t) {
        if (this.running && j == this.generation) {
            if (j2 <= 0 || t == null) {
                throw new IllegalArgumentException("invalid candidate");
            }
            return new Candidate<>(j, j2, t);
        }
        return null;
    }

    public synchronized Session<T> promoteAuthenticated(Candidate<T> candidate, String str, Closer<T> closer) {
        if (candidate != null && str != null) {
            if (!str.isEmpty() && closer != null) {
                if (this.running && ((Candidate) candidate).serverGeneration == this.generation && ((Candidate) candidate).authenticated.get() && !((Candidate) candidate).closed.get() && ((Candidate) candidate).claimed.compareAndSet(false, true)) {
                    Session<T> session = new Session<>(this.generation, ((Candidate) candidate).connectionId, str, ((Candidate) candidate).value, closer);
                    Session<T> session2 = this.activeSession;
                    this.activeSession = session;
                    if (session2 != null) {
                        session2.closeOnce();
                    }
                    return session;
                }
                return null;
            }
        }
        return null;
    }

    public synchronized Session<T> activeSession() {
        return this.activeSession;
    }

    public synchronized boolean isActive(Session<T> session) {
        return isActiveLocked(session);
    }

    public synchronized boolean runIfActive(Session<T> session, Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("action == null");
        }
        if (!isActiveLocked(session)) {
            return false;
        }
        runnable.run();
        return true;
    }

    public synchronized boolean closeIfActive(Session<T> session, Closer<T> closer) {
        if (session != null) {
            if (this.activeSession == session) {
                this.activeSession = null;
                session.closeOnce();
                return true;
            }
        }
        return false;
    }

    public synchronized boolean closeAndNotifyIfActive(Session<T> session, Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("onClosed == null");
        }
        if (session != null && this.activeSession == session) {
            this.activeSession = null;
            session.closeOnce();
            runnable.run();
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void reject(Candidate<T> candidate, Closer<T> closer) {
        if (candidate == null || closer == 0 || ((Candidate) candidate).claimed.get() || !((Candidate) candidate).closed.compareAndSet(false, true)) {
            return;
        }
        closer.close(((Candidate) candidate).value);
    }

    public synchronized void stop() {
        this.running = false;
        Session<T> session = this.activeSession;
        this.activeSession = null;
        if (session != null) {
            session.closeOnce();
        }
    }

    private boolean isActiveLocked(Session<T> session) {
        return this.running && session != null && this.activeSession == session && !session.isClosed();
    }
}
