package com.android.support;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class UiSurfacePolicyDispatcher {
    private final Supplier<UiSurfacePolicy> latest;
    private volatile Registration registration;

    /* JADX INFO: Access modifiers changed from: private */
    static final class Registration {
        final Executor executor;
        final Consumer<UiSurfacePolicy> listener;

        Registration(Executor executor, Consumer<UiSurfacePolicy> consumer) {
            this.executor = executor;
            this.listener = consumer;
        }
    }

    UiSurfacePolicyDispatcher(Supplier<UiSurfacePolicy> supplier) {
        this.latest = supplier;
    }

    void subscribe(Executor executor, Consumer<UiSurfacePolicy> consumer) {
        this.registration = consumer == null ? null : new Registration(executor, consumer);
        changed();
    }

    void changed() {
        final Registration registration = this.registration;
        if (registration == null) {
            return;
        }
        registration.executor.execute(new Runnable() { // from class: com.android.support.UiSurfacePolicyDispatcher$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1583lambda$changed$0$comandroidsupportUiSurfacePolicyDispatcher(registration);
            }
        });
    }

    /* renamed from: lambda$changed$0$com-android-support-UiSurfacePolicyDispatcher, reason: not valid java name */
    /* synthetic */ void m1583lambda$changed$0$comandroidsupportUiSurfacePolicyDispatcher(Registration registration) {
        if (this.registration != registration) {
            return;
        }
        registration.listener.accept(this.latest.get());
    }
}
