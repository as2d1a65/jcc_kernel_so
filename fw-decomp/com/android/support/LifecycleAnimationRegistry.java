package com.android.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
final class LifecycleAnimationRegistry<T> {
    private boolean active;
    private final List<T> animations = new ArrayList();
    private final Controller<T> controller;
    private boolean destroyed;

    interface Controller<T> {
        void cancel(T t);

        boolean isStarted(T t);

        void start(T t);
    }

    LifecycleAnimationRegistry(Controller<T> controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller == null");
        }
        this.controller = controller;
    }

    void register(T t) {
        if (t == null) {
            return;
        }
        if (this.destroyed) {
            cancelIfStarted(t);
            return;
        }
        this.animations.add(t);
        if (this.active) {
            startIfNeeded(t);
        } else {
            cancelIfStarted(t);
        }
    }

    void startAll() {
        if (this.destroyed || this.active) {
            return;
        }
        this.active = true;
        Iterator<T> it = this.animations.iterator();
        while (it.hasNext()) {
            startIfNeeded(it.next());
        }
    }

    void stopAll() {
        if (this.active) {
            this.active = false;
            Iterator<T> it = this.animations.iterator();
            while (it.hasNext()) {
                cancelIfStarted(it.next());
            }
        }
    }

    void destroy() {
        if (this.destroyed) {
            return;
        }
        stopAll();
        this.destroyed = true;
        this.animations.clear();
    }

    private void startIfNeeded(T t) {
        if (this.controller.isStarted(t)) {
            return;
        }
        this.controller.start(t);
    }

    private void cancelIfStarted(T t) {
        if (this.controller.isStarted(t)) {
            this.controller.cancel(t);
        }
    }
}
