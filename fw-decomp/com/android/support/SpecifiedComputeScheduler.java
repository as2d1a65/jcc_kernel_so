package com.android.support;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
final class SpecifiedComputeScheduler {
    private final ThreadPoolExecutor runtime = worker("runtime", 1, 2);
    private final ThreadPoolExecutor core = worker("core", 2, 4);
    private final ThreadPoolExecutor opponent = worker("opponent", 1, 2);
    private final ThreadPoolExecutor shop = worker(SpecifiedPredictionPreview.KIND_SHOP, 2, 4);
    private final ThreadPoolExecutor hex = worker(SpecifiedPredictionPreview.KIND_HEX, 1, 4);
    private final ThreadPoolExecutor recast = worker(SpecifiedPredictionPreview.KIND_RECAST, 1, 4);

    SpecifiedComputeScheduler() {
    }

    Executor forOperation(String str) {
        str.hashCode();
        switch (str) {
            case "recast":
            case "sea_recast":
                return this.recast;
            case "opponent":
                return this.opponent;
            case "aux":
            case "rng":
                return this.core;
            case "hex":
                return this.hex;
            case "runtime_policy":
                return this.runtime;
            default:
                return this.shop;
        }
    }

    void execute(String str, Runnable runnable, Runnable runnable2) {
        forOperation(str).execute(new PendingTask(runnable, runnable2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class PendingTask implements Runnable {
        final Runnable rejected;
        final Runnable work;

        PendingTask(Runnable runnable, Runnable runnable2) {
            this.work = runnable;
            this.rejected = runnable2;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.work.run();
        }
    }

    private static ThreadPoolExecutor worker(final String str, int i, int i2) {
        return new ThreadPoolExecutor(i, i, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue(i2), new ThreadFactory() { // from class: com.android.support.SpecifiedComputeScheduler$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return SpecifiedComputeScheduler.lambda$worker$0(str, runnable);
            }
        }, new RejectedExecutionHandler() { // from class: com.android.support.SpecifiedComputeScheduler$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.RejectedExecutionHandler
            public final void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                SpecifiedComputeScheduler.lambda$worker$1(runnable, threadPoolExecutor);
            }
        });
    }

    static /* synthetic */ Thread lambda$worker$0(String str, Runnable runnable) {
        Thread thread = new Thread(runnable, "jcc-" + str + "-transport");
        thread.setDaemon(true);
        return thread;
    }

    static /* synthetic */ void lambda$worker$1(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
        if (runnable instanceof PendingTask) {
            ((PendingTask) runnable).rejected.run();
        }
    }
}
