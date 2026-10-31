package com.android.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class IpcV2OwnerRegistry<T> {
    private long nextId;
    private final IdentityHashMap<Object, Entry<T>> byOwner = new IdentityHashMap<>();
    private final Map<Long, Entry<T>> byId = new HashMap();

    public static final class Token {
        private final long id;

        private Token(long j) {
            this.id = j;
        }
    }

    private static final class Entry<T> {
        final T listener;
        final Object owner;
        final Token token;

        Entry(Object obj, Token token, T t) {
            this.owner = obj;
            this.token = token;
            this.listener = t;
        }
    }

    public synchronized Token register(Object obj, T t) {
        Token token;
        if (obj == null || t == null) {
            throw new IllegalArgumentException("owner/listener required");
        }
        Entry<T> entryRemove = this.byOwner.remove(obj);
        if (entryRemove != null) {
            this.byId.remove(Long.valueOf(entryRemove.token.id));
        }
        if (this.nextId == Long.MAX_VALUE) {
            throw new IllegalStateException("listener token exhausted");
        }
        long j = this.nextId + 1;
        this.nextId = j;
        token = new Token(j);
        Entry<T> entry = new Entry<>(obj, token, t);
        this.byOwner.put(obj, entry);
        this.byId.put(Long.valueOf(token.id), entry);
        return token;
    }

    public synchronized boolean unregister(Token token) {
        if (token == null) {
            return false;
        }
        Entry<T> entryRemove = this.byId.remove(Long.valueOf(token.id));
        if (entryRemove == null) {
            return false;
        }
        this.byOwner.remove(entryRemove.owner);
        return true;
    }

    public synchronized List<T> snapshot() {
        ArrayList arrayList;
        arrayList = new ArrayList(this.byId.size());
        Iterator<Entry<T>> it = this.byId.values().iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().listener);
        }
        return arrayList;
    }

    public synchronized void clear() {
        this.byOwner.clear();
        this.byId.clear();
    }

    public synchronized int size() {
        return this.byId.size();
    }
}
