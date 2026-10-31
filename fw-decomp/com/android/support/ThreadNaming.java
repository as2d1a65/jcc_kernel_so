package com.android.support;

import com.android.support.lineup.LineupDataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.Dispatcher;

/* loaded from: classes.dex */
public final class ThreadNaming {
    public static final String HTTP = "net-http";
    private static final int MAX_REPORTED = 32;
    public static final String WORKER = "net-worker";
    private static volatile boolean sweepDisabled;
    private static volatile ScheduledExecutorService sweeper;
    private static final String[] FORBIDDEN = {"zhuzhufaka", "jcc", "xfonline", "xf-", "ipc-v2", "prelogin", LineupDataSource.ASSETS_DIR, "cloudtts", "roothelper", "root-identity", "gamesregions", "diagnostic", "peersync", "shareroom", "inject", "soinfo", "il2cpp"};
    private static final Set<String> REPORTED = Collections.newSetFromMap(new ConcurrentHashMap());

    private ThreadNaming() {
    }

    public static ThreadFactory factory(final String str) {
        return new ThreadFactory() { // from class: com.android.support.ThreadNaming.1
            private final AtomicInteger counter = new AtomicInteger();

            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, str + "-" + this.counter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        };
    }

    public static Dispatcher dispatcher() {
        Dispatcher dispatcher = new Dispatcher(Executors.newCachedThreadPool(factory(HTTP)));
        dispatcher.setMaxRequests(64);
        dispatcher.setMaxRequestsPerHost(16);
        return dispatcher;
    }

    public static synchronized void startSweeper() {
        if (sweepDisabled || sweeper != null) {
            return;
        }
        try {
            ScheduledExecutorService scheduledExecutorServiceNewSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(factory(WORKER));
            scheduledExecutorServiceNewSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() { // from class: com.android.support.ThreadNaming$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ThreadNaming.sweep();
                }
            }, 3L, 20L, TimeUnit.SECONDS);
            sweeper = scheduledExecutorServiceNewSingleThreadScheduledExecutor;
        } catch (RuntimeException unused) {
            sweepDisabled = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sweep() {
        String name;
        try {
            for (Thread thread : allThreads()) {
                if (thread != null && thread.isAlive() && (name = thread.getName()) != null && isLeaky(name)) {
                    Set<String> set = REPORTED;
                    if (set.size() < 32) {
                        set.add(name);
                    }
                    try {
                        thread.setName(WORKER);
                    } catch (RuntimeException unused) {
                    }
                }
            }
        } catch (RuntimeException unused2) {
        }
    }

    private static boolean isLeaky(String str) {
        String lowerCase = str.toLowerCase(Locale.ROOT);
        for (String str2 : FORBIDDEN) {
            if (lowerCase.contains(str2)) {
                return true;
            }
        }
        return false;
    }

    private static List<Thread> allThreads() {
        ArrayList arrayList = new ArrayList();
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (threadGroup.getParent() != null) {
            threadGroup = threadGroup.getParent();
        }
        collect(threadGroup, arrayList);
        return arrayList;
    }

    private static void collect(ThreadGroup threadGroup, List<Thread> list) {
        Thread[] threadArr = new Thread[Math.max(16, threadGroup.activeCount() * 2)];
        int iEnumerate = threadGroup.enumerate(threadArr, false);
        for (int i = 0; i < iEnumerate; i++) {
            Thread thread = threadArr[i];
            if (thread != null) {
                list.add(thread);
            }
        }
        ThreadGroup[] threadGroupArr = new ThreadGroup[Math.max(4, threadGroup.activeGroupCount() * 2)];
        int iEnumerate2 = threadGroup.enumerate(threadGroupArr, false);
        for (int i2 = 0; i2 < iEnumerate2; i2++) {
            ThreadGroup threadGroup2 = threadGroupArr[i2];
            if (threadGroup2 != null) {
                collect(threadGroup2, list);
            }
        }
    }
}
