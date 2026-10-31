package com.android.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public final class IpcV2PendingRequests<T> {
    private final int capacity;
    private final Map<String, Pending<T>> pending = new HashMap();

    public interface Callback<T> {
        void onTerminal(T t);
    }

    public enum RegisterResult {
        ACCEPTED,
        DUPLICATE_ID,
        BUSY
    }

    private static final class Pending<T> {
        final Callback<T> callback;
        final long deadlineBootMs;
        final String sessionId;
        boolean written;

        Pending(String str, long j, Callback<T> callback) {
            this.sessionId = str;
            this.deadlineBootMs = j;
            this.callback = callback;
        }
    }

    public IpcV2PendingRequests(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = i;
    }

    public synchronized RegisterResult register(String str, String str2, Callback<T> callback) {
        return register(str, str2, Long.MAX_VALUE, callback);
    }

    public synchronized RegisterResult register(String str, String str2, long j, Callback<T> callback) {
        if (str != null) {
            if (!str.isEmpty() && str2 != null && !str2.isEmpty() && callback != null) {
                if (j <= 0) {
                    throw new IllegalArgumentException("deadline must be positive");
                }
                if (this.pending.containsKey(str)) {
                    return RegisterResult.DUPLICATE_ID;
                }
                if (this.pending.size() >= this.capacity) {
                    return RegisterResult.BUSY;
                }
                this.pending.put(str, new Pending<>(str2, j, callback));
                return RegisterResult.ACCEPTED;
            }
        }
        throw new IllegalArgumentException("invalid pending request");
    }

    public boolean complete(String str, T t) {
        Pending<T> pendingRemove;
        synchronized (this) {
            pendingRemove = this.pending.remove(str);
        }
        if (pendingRemove == null) {
            return false;
        }
        deliver(pendingRemove, t);
        return true;
    }

    public boolean complete(String str, String str2, T t) {
        synchronized (this) {
            Pending<T> pending = this.pending.get(str);
            if (pending != null && pending.sessionId.equals(str2)) {
                this.pending.remove(str);
                deliver(pending, t);
                return true;
            }
            return false;
        }
    }

    public synchronized boolean markWritten(String str, String str2) {
        Pending<T> pending = this.pending.get(str);
        if (pending != null && pending.sessionId.equals(str2)) {
            pending.written = true;
            return true;
        }
        return false;
    }

    public synchronized boolean cancel(String str, String str2) {
        Pending<T> pending = this.pending.get(str);
        if (pending != null && pending.sessionId.equals(str2) && !pending.written) {
            this.pending.remove(str);
            return true;
        }
        return false;
    }

    public int expire(long j, T t, T t2) {
        HashMap map = new HashMap();
        synchronized (this) {
            for (Map.Entry<String, Pending<T>> entry : this.pending.entrySet()) {
                if (entry.getValue().deadlineBootMs <= j) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                this.pending.remove((String) it.next());
            }
        }
        for (Pending<T> pending : map.values()) {
            deliver(pending, pending.written ? t2 : t);
        }
        return map.size();
    }

    public void failSession(String str, T t) {
        HashMap map = new HashMap();
        synchronized (this) {
            for (Map.Entry<String, Pending<T>> entry : this.pending.entrySet()) {
                if (entry.getValue().sessionId.equals(str)) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                this.pending.remove((String) it.next());
            }
        }
        Iterator it2 = map.values().iterator();
        while (it2.hasNext()) {
            deliver((Pending) it2.next(), t);
        }
    }

    public void failSession(String str, T t, T t2) {
        HashMap map = new HashMap();
        synchronized (this) {
            for (Map.Entry<String, Pending<T>> entry : this.pending.entrySet()) {
                if (entry.getValue().sessionId.equals(str)) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                this.pending.remove((String) it.next());
            }
        }
        for (Pending<T> pending : map.values()) {
            deliver(pending, pending.written ? t2 : t);
        }
    }

    public synchronized int size() {
        return this.pending.size();
    }

    private void deliver(Pending<T> pending, T t) {
        try {
            pending.callback.onTerminal(t);
        } catch (RuntimeException e) {
            Logger.getLogger(IpcV2PendingRequests.class.getName()).log(Level.WARNING, "IPC terminal callback failed", (Throwable) e);
        }
    }
}
