package com.android.support;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.support.LatestOwnerListener;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public final class XfOnlineCount {
    private static final int POLL_SECONDS = 15;
    private static final String TAG = "XfOnlineCount";
    public static final int UNKNOWN = -1;
    private static XfOnlineCount instance;
    private final Handler mainHandler;
    private ScheduledFuture<?> pollTask;
    private String presenceId;
    private boolean running;
    private final ScheduledExecutorService worker;
    private final Object stateLock = new Object();
    private final AtomicInteger latestCount = new AtomicInteger(-1);
    private final LatestOwnerListener<Listener> listeners = new LatestOwnerListener<>();
    private final XfOnlineCountClient client = new XfOnlineCountClient();

    public interface Listener {
        void onOnlineCount(int i);

        void onOnlineCountUnavailable();
    }

    private XfOnlineCount() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() { // from class: com.android.support.XfOnlineCount$$ExternalSyntheticLambda3
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return XfOnlineCount.lambda$new$0(runnable);
            }
        });
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        this.worker = scheduledThreadPoolExecutor;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    static /* synthetic */ Thread lambda$new$0(Runnable runnable) {
        Thread thread = new Thread(runnable, ThreadNaming.WORKER);
        thread.setDaemon(true);
        return thread;
    }

    public static synchronized XfOnlineCount shared() {
        if (instance == null) {
            instance = new XfOnlineCount();
        }
        return instance;
    }

    public LatestOwnerListener.Token registerListener(Object obj, Listener listener) {
        return this.listeners.register(obj, listener);
    }

    public boolean unregisterListener(LatestOwnerListener.Token token) {
        return this.listeners.unregister(token);
    }

    public int cachedCount() {
        return this.latestCount.get();
    }

    public void start(Context context) {
        if (context == null) {
            return;
        }
        String strLoad = XfOnlinePresenceId.load(context);
        synchronized (this.stateLock) {
            this.presenceId = strLoad;
            if (this.running) {
                return;
            }
            this.running = true;
            this.pollTask = this.worker.scheduleWithFixedDelay(new Runnable() { // from class: com.android.support.XfOnlineCount$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.poll();
                }
            }, 0L, 15L, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        boolean z;
        final String str;
        synchronized (this.stateLock) {
            z = this.running;
            this.running = false;
            ScheduledFuture<?> scheduledFuture = this.pollTask;
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
                this.pollTask = null;
            }
            str = this.presenceId;
        }
        if (!z || str == null) {
            return;
        }
        this.worker.execute(new Runnable() { // from class: com.android.support.XfOnlineCount$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1644lambda$stop$1$comandroidsupportXfOnlineCount(str);
            }
        });
    }

    /* renamed from: lambda$stop$1$com-android-support-XfOnlineCount, reason: not valid java name */
    /* synthetic */ void m1644lambda$stop$1$comandroidsupportXfOnlineCount(String str) {
        try {
            this.client.reportOffline(str);
        } catch (Exception unused) {
            Log.w(TAG, "online presence release skipped");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void poll() {
        String str;
        synchronized (this.stateLock) {
            str = this.presenceId;
        }
        if (str == null) {
            return;
        }
        try {
            int iReportOnline = this.client.reportOnline(str);
            if (isRunning()) {
                this.latestCount.set(iReportOnline);
                notifyCount(iReportOnline);
            }
        } catch (Exception unused) {
            Log.w(TAG, "online count unavailable");
            if (isRunning()) {
                notifyUnavailable();
            }
        }
    }

    private boolean isRunning() {
        boolean z;
        synchronized (this.stateLock) {
            z = this.running;
        }
        return z;
    }

    private void notifyCount(final int i) {
        final LatestOwnerListener.Snapshot<Listener> snapshot = this.listeners.snapshot();
        if (snapshot == null) {
            return;
        }
        this.mainHandler.post(new Runnable() { // from class: com.android.support.XfOnlineCount$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1642lambda$notifyCount$2$comandroidsupportXfOnlineCount(snapshot, i);
            }
        });
    }

    /* renamed from: lambda$notifyCount$2$com-android-support-XfOnlineCount, reason: not valid java name */
    /* synthetic */ void m1642lambda$notifyCount$2$comandroidsupportXfOnlineCount(LatestOwnerListener.Snapshot snapshot, int i) {
        if (this.listeners.isCurrent(snapshot)) {
            ((Listener) snapshot.listener()).onOnlineCount(i);
        }
    }

    private void notifyUnavailable() {
        final LatestOwnerListener.Snapshot<Listener> snapshot = this.listeners.snapshot();
        if (snapshot == null) {
            return;
        }
        this.mainHandler.post(new Runnable() { // from class: com.android.support.XfOnlineCount$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1643lambda$notifyUnavailable$3$comandroidsupportXfOnlineCount(snapshot);
            }
        });
    }

    /* renamed from: lambda$notifyUnavailable$3$com-android-support-XfOnlineCount, reason: not valid java name */
    /* synthetic */ void m1643lambda$notifyUnavailable$3$comandroidsupportXfOnlineCount(LatestOwnerListener.Snapshot snapshot) {
        if (this.listeners.isCurrent(snapshot)) {
            ((Listener) snapshot.listener()).onOnlineCountUnavailable();
        }
    }
}
