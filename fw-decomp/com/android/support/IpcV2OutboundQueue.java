package com.android.support;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class IpcV2OutboundQueue<T> {
    private boolean closed;
    private final int controlCapacity;
    private final ArrayDeque<T> controls;
    private final int eventCapacity;
    private final ArrayDeque<T> events;
    private final int requestCapacity;
    private final ArrayDeque<T> requests;
    private final int stateCapacity;
    private final LinkedHashMap<String, T> states;

    public enum OfferResult {
        ACCEPTED,
        COALESCED,
        BUSY,
        CLOSED
    }

    public static final class StateOffer<T> {
        private final T replaced;
        private final OfferResult result;

        private StateOffer(OfferResult offerResult, T t) {
            this.result = offerResult;
            this.replaced = t;
        }

        public OfferResult result() {
            return this.result;
        }

        public T replaced() {
            return this.replaced;
        }
    }

    public IpcV2OutboundQueue(int i, int i2, int i3) {
        this(i, i2, i3, 16);
    }

    public IpcV2OutboundQueue(int i, int i2, int i3, int i4) {
        this.controls = new ArrayDeque<>();
        this.requests = new ArrayDeque<>();
        this.events = new ArrayDeque<>();
        this.states = new LinkedHashMap<>();
        if (i <= 0 || i2 <= 0 || i3 <= 0 || i4 <= 0) {
            throw new IllegalArgumentException("queue capacities must be positive");
        }
        this.controlCapacity = i;
        this.requestCapacity = i2;
        this.eventCapacity = i3;
        this.stateCapacity = i4;
    }

    public synchronized OfferResult offerControl(T t) {
        return offerBounded(this.controls, this.controlCapacity, t);
    }

    public synchronized OfferResult offerRequest(T t) {
        return offerBounded(this.requests, this.requestCapacity, t);
    }

    public synchronized OfferResult offerEvent(T t) {
        return offerBounded(this.events, this.eventCapacity, t);
    }

    public synchronized OfferResult offerState(String str, T t) {
        return offerStateReplacing(str, t).result();
    }

    public synchronized StateOffer<T> offerStateReplacing(String str, T t) {
        requireValue(t);
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("state method is required");
        }
        IpcV2OutboundQueueIA ipcV2OutboundQueueIA = null;
        if (this.closed) {
            return new StateOffer<>(OfferResult.CLOSED, ipcV2OutboundQueueIA);
        }
        boolean zContainsKey = this.states.containsKey(str);
        if (!zContainsKey && this.states.size() >= this.stateCapacity) {
            return new StateOffer<>(OfferResult.BUSY, ipcV2OutboundQueueIA);
        }
        T tPut = this.states.put(str, t);
        notifyAll();
        return new StateOffer<>(zContainsKey ? OfferResult.COALESCED : OfferResult.ACCEPTED, tPut);
    }

    public synchronized T take() throws InterruptedException {
        while (!this.closed && isEmptyLocked()) {
            wait();
        }
        if (this.closed) {
            return null;
        }
        if (!this.controls.isEmpty()) {
            return this.controls.removeFirst();
        }
        if (!this.requests.isEmpty()) {
            return this.requests.removeFirst();
        }
        if (!this.states.isEmpty()) {
            Iterator<Map.Entry<String, T>> it = this.states.entrySet().iterator();
            Map.Entry<String, T> next = it.next();
            it.remove();
            return next.getValue();
        }
        return this.events.removeFirst();
    }

    public synchronized void close() {
        closeAndDrain();
    }

    public synchronized List<T> closeAndDrain() {
        ArrayList arrayList = new ArrayList();
        if (this.closed) {
            return arrayList;
        }
        this.closed = true;
        arrayList.addAll(this.controls);
        arrayList.addAll(this.requests);
        arrayList.addAll(this.states.values());
        arrayList.addAll(this.events);
        this.controls.clear();
        this.requests.clear();
        this.states.clear();
        this.events.clear();
        notifyAll();
        return arrayList;
    }

    public synchronized boolean isClosed() {
        return this.closed;
    }

    public synchronized int requestSize() {
        return this.requests.size();
    }

    public synchronized int eventSize() {
        return this.events.size();
    }

    public synchronized int stateSize() {
        return this.states.size();
    }

    private OfferResult offerBounded(ArrayDeque<T> arrayDeque, int i, T t) {
        requireValue(t);
        if (this.closed) {
            return OfferResult.CLOSED;
        }
        if (arrayDeque.size() >= i) {
            return OfferResult.BUSY;
        }
        arrayDeque.addLast(t);
        notifyAll();
        return OfferResult.ACCEPTED;
    }

    private boolean isEmptyLocked() {
        return this.controls.isEmpty() && this.requests.isEmpty() && this.states.isEmpty() && this.events.isEmpty();
    }

    private static void requireValue(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("queue value is required");
        }
    }
}
