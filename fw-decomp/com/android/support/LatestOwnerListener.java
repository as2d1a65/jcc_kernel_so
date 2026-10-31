package com.android.support;

/* loaded from: classes.dex */
public final class LatestOwnerListener<T> {
    private T listener;
    private long nextId;
    private Object owner;
    private Token token;

    public static final class Token {
        private final long id;

        private Token(long j) {
            this.id = j;
        }
    }

    public static final class Snapshot<T> {
        private final T listener;
        private final Token token;

        private Snapshot(Token token, T t) {
            this.token = token;
            this.listener = t;
        }

        public T listener() {
            return this.listener;
        }
    }

    public synchronized Token register(Object obj, T t) {
        Token token;
        if (obj == null || t == null) {
            throw new IllegalArgumentException("owner/listener required");
        }
        if (this.nextId == Long.MAX_VALUE) {
            throw new IllegalStateException("listener token exhausted");
        }
        this.owner = obj;
        long j = this.nextId + 1;
        this.nextId = j;
        token = new Token(j);
        this.token = token;
        this.listener = t;
        return token;
    }

    public synchronized boolean unregister(Token token) {
        if (token != null) {
            if (token == this.token) {
                clear();
                return true;
            }
        }
        return false;
    }

    public synchronized void clear() {
        this.owner = null;
        this.token = null;
        this.listener = null;
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [com.android.support.LatestOwnerListener$Snapshot<T>] */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7 */
    public synchronized Snapshot<T> snapshot() {
        LatestOwnerListenerIA latestOwnerListenerIA;
        latestOwnerListenerIA = (Snapshot<T>) null;
        return (Snapshot<T>) (this.listener == null ? latestOwnerListenerIA : (Snapshot<T>) new Snapshot(this.token, this.listener));
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x001c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean isCurrent(com.android.support.LatestOwnerListener.Snapshot<T> r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            if (r3 == 0) goto L1c
            com.android.support.LatestOwnerListener$Token r0 = com.android.support.LatestOwnerListener.Snapshot.m1223$$Nest$fgettoken(r3)     // Catch: java.lang.Throwable -> L19
            com.android.support.LatestOwnerListener$Token r1 = r2.token     // Catch: java.lang.Throwable -> L19
            if (r0 != r1) goto L1c
            java.lang.Object r3 = com.android.support.LatestOwnerListener.Snapshot.m1222$$Nest$fgetlistener(r3)     // Catch: java.lang.Throwable -> L19
            T r0 = r2.listener     // Catch: java.lang.Throwable -> L19
            if (r3 != r0) goto L1c
            java.lang.Object r3 = r2.owner     // Catch: java.lang.Throwable -> L19
            if (r3 == 0) goto L1c
            r3 = 1
            goto L1d
        L19:
            r3 = move-exception
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L19
            throw r3
        L1c:
            r3 = 0
        L1d:
            monitor-exit(r2)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.LatestOwnerListener.isCurrent(com.android.support.LatestOwnerListener$Snapshot):boolean");
    }
}
