package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.support.JccGatewayClient;
import com.android.support.LatestOwnerListener;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class OnlineStats {
    private static final int HEARTBEAT_SECONDS = 30;
    private static final String KEY_LAST_GENERATION = "last_generation";
    private static final String PREF_NAME = "jcc_presence_prefs";
    private static final long PRESENCE_GENERATION_FLOOR = 9223372035854775807L;
    private static final String TAG = "OnlineStats";
    private static OnlineStats instance;
    private JccGatewayClient.Cancellation activeRequest;
    private final JccGatewayClient gateway;
    private long generation;
    private ScheduledFuture<?> heartbeatTask;
    private final Handler mainHandler;
    private final SharedPreferences presencePrefs;
    private boolean running;
    private long sequence;
    private final ScheduledExecutorService worker;
    private final Object stateLock = new Object();
    private final AtomicInteger onlineCount = new AtomicInteger();
    private final LatestOwnerListener<OnlineCountListener> listeners = new LatestOwnerListener<>();

    public interface OnlineCountListener {
        void onError(String str);

        void onOnlineCountUpdated(int i);
    }

    private OnlineStats(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() { // from class: com.android.support.OnlineStats$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return OnlineStats.lambda$new$0(runnable);
            }
        });
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        this.worker = scheduledThreadPoolExecutor;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.gateway = JccGatewayClient.shared();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        this.presencePrefs = sharedPreferences;
        this.generation = sharedPreferences.getLong(KEY_LAST_GENERATION, PRESENCE_GENERATION_FLOOR);
    }

    static /* synthetic */ Thread lambda$new$0(Runnable runnable) {
        Thread thread = new Thread(runnable, ThreadNaming.WORKER);
        thread.setDaemon(true);
        return thread;
    }

    public static synchronized OnlineStats getInstance(Context context) {
        if (instance == null) {
            instance = new OnlineStats(context.getApplicationContext());
        }
        return instance;
    }

    public LatestOwnerListener.Token registerOnlineCountListener(Object obj, OnlineCountListener onlineCountListener) {
        return this.listeners.register(obj, onlineCountListener);
    }

    public boolean unregisterOnlineCountListener(LatestOwnerListener.Token token) {
        return this.listeners.unregister(token);
    }

    public void start() {
        synchronized (this.stateLock) {
            if (this.running) {
                return;
            }
            this.generation = nextPersistedGenerationLocked();
            this.sequence = 1L;
            this.running = true;
            final PresenceSnapshot presenceSnapshot = new PresenceSnapshot(this.generation, this.sequence, true);
            cancelHeartbeatLocked();
            this.heartbeatTask = this.worker.scheduleAtFixedRate(new OnlineStats$$ExternalSyntheticLambda2(this), 30L, 30L, TimeUnit.SECONDS);
            this.worker.execute(new Runnable() { // from class: com.android.support.OnlineStats$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1381lambda$start$1$comandroidsupportOnlineStats(presenceSnapshot);
                }
            });
        }
    }

    public void restart() {
        final PresenceSnapshot presenceSnapshot;
        synchronized (this.stateLock) {
            this.generation = nextPersistedGenerationLocked();
            this.sequence = 1L;
            this.running = true;
            cancelHeartbeatLocked();
            cancelActiveRequestLocked();
            this.heartbeatTask = this.worker.scheduleAtFixedRate(new OnlineStats$$ExternalSyntheticLambda2(this), 30L, 30L, TimeUnit.SECONDS);
            presenceSnapshot = new PresenceSnapshot(this.generation, this.sequence, true);
        }
        this.worker.execute(new Runnable() { // from class: com.android.support.OnlineStats$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1380lambda$restart$2$comandroidsupportOnlineStats(presenceSnapshot);
            }
        });
    }

    public void suspendForAuthentication() {
        synchronized (this.stateLock) {
            this.running = false;
            this.generation = nextPersistedGenerationLocked();
            this.sequence = 0L;
            cancelHeartbeatLocked();
            cancelActiveRequestLocked();
        }
    }

    public void stop() {
        synchronized (this.stateLock) {
            if (this.running) {
                this.running = false;
                this.sequence = nextSequence(this.sequence);
                final PresenceSnapshot presenceSnapshot = new PresenceSnapshot(this.generation, this.sequence, false);
                cancelHeartbeatLocked();
                cancelActiveRequestLocked();
                this.worker.execute(new Runnable() { // from class: com.android.support.OnlineStats$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1382lambda$stop$3$comandroidsupportOnlineStats(presenceSnapshot);
                    }
                });
            }
        }
    }

    public int sendHeartbeat() {
        synchronized (this.stateLock) {
            if (!this.running) {
                return -1;
            }
            this.sequence = nextSequence(this.sequence);
            PresenceSnapshot presenceSnapshot = new PresenceSnapshot(this.generation, this.sequence, true);
            cancelActiveRequestLocked();
            return m1382lambda$stop$3$comandroidsupportOnlineStats(presenceSnapshot);
        }
    }

    public void fetchOnlineCount() {
        synchronized (this.stateLock) {
            if (this.running) {
                this.worker.execute(new Runnable() { // from class: com.android.support.OnlineStats$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.sendHeartbeat();
                    }
                });
            } else {
                notifyOnlineCount(this.onlineCount.get());
            }
        }
    }

    public void sendOffline() {
        stop();
    }

    public int getCachedOnlineCount() {
        return this.onlineCount.get();
    }

    public boolean isRunning() {
        boolean z;
        synchronized (this.stateLock) {
            z = this.running;
        }
        return z;
    }

    @Deprecated
    public String getDeviceUID() {
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendScheduledHeartbeat() {
        sendHeartbeat();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: sendSnapshot, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public int m1382lambda$stop$3$comandroidsupportOnlineStats(PresenceSnapshot presenceSnapshot) {
        JccGatewayClient.Cancellation cancellation = new JccGatewayClient.Cancellation();
        synchronized (this.stateLock) {
            if (presenceSnapshot.online && (!this.running || presenceSnapshot.generation != this.generation)) {
                return -1;
            }
            this.activeRequest = cancellation;
            if (JccSessionState.shared().bearerTokenNow() == null) {
                suspendForAuthentication();
                return -1;
            }
            try {
                try {
                    JSONObject jSONObjectPresence = this.gateway.presence(presenceSnapshot.generation, presenceSnapshot.sequence, presenceSnapshot.online, cancellation);
                    long jOptLong = jSONObjectPresence.optLong("accepted_seq", -1L);
                    int iOptInt = jSONObjectPresence.optInt("online_count", -1);
                    if (jOptLong == presenceSnapshot.sequence && iOptInt >= 0) {
                        synchronized (this.stateLock) {
                            if (presenceSnapshot.generation == this.generation && presenceSnapshot.sequence == this.sequence) {
                                this.onlineCount.set(iOptInt);
                                notifyOnlineCount(iOptInt);
                                synchronized (this.stateLock) {
                                    if (this.activeRequest == cancellation) {
                                        this.activeRequest = null;
                                    }
                                }
                                return iOptInt;
                            }
                            synchronized (this.stateLock) {
                                if (this.activeRequest == cancellation) {
                                    this.activeRequest = null;
                                }
                            }
                            return -1;
                        }
                    }
                    Log.w(TAG, "presence_response_invalid");
                    synchronized (this.stateLock) {
                        if (this.activeRequest == cancellation) {
                            this.activeRequest = null;
                        }
                    }
                    return -1;
                } catch (JccGatewayClient.JccException e) {
                    if (!"REQUEST_CANCELLED".equals(e.code)) {
                        if (PresenceFailurePolicy.isSessionLoss(e.code)) {
                            Dbg.i(TAG, "presence_suspended code=" + e.code);
                            suspendForAuthentication();
                            synchronized (this.stateLock) {
                                if (this.activeRequest == cancellation) {
                                    this.activeRequest = null;
                                }
                                return -1;
                            }
                        }
                        Log.w(TAG, "presence_failed code=" + e.code);
                        notifyError(e.code);
                    }
                    synchronized (this.stateLock) {
                        if (this.activeRequest == cancellation) {
                            this.activeRequest = null;
                        }
                        return -1;
                    }
                } catch (Exception unused) {
                    Log.w(TAG, "presence_failed");
                    notifyError("PRESENCE_UNAVAILABLE");
                    synchronized (this.stateLock) {
                        if (this.activeRequest == cancellation) {
                            this.activeRequest = null;
                        }
                        return -1;
                    }
                }
            } catch (Throwable th) {
                synchronized (this.stateLock) {
                    if (this.activeRequest == cancellation) {
                        this.activeRequest = null;
                    }
                    throw th;
                }
            }
        }
    }

    private void notifyOnlineCount(final int i) {
        final LatestOwnerListener.Snapshot<OnlineCountListener> snapshot = this.listeners.snapshot();
        if (snapshot == null) {
            return;
        }
        this.mainHandler.post(new Runnable() { // from class: com.android.support.OnlineStats$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1379lambda$notifyOnlineCount$4$comandroidsupportOnlineStats(snapshot, i);
            }
        });
    }

    /* renamed from: lambda$notifyOnlineCount$4$com-android-support-OnlineStats, reason: not valid java name */
    /* synthetic */ void m1379lambda$notifyOnlineCount$4$comandroidsupportOnlineStats(LatestOwnerListener.Snapshot snapshot, int i) {
        if (this.listeners.isCurrent(snapshot)) {
            ((OnlineCountListener) snapshot.listener()).onOnlineCountUpdated(i);
        }
    }

    private void notifyError(final String str) {
        final LatestOwnerListener.Snapshot<OnlineCountListener> snapshot = this.listeners.snapshot();
        if (snapshot == null) {
            return;
        }
        this.mainHandler.post(new Runnable() { // from class: com.android.support.OnlineStats$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1378lambda$notifyError$5$comandroidsupportOnlineStats(snapshot, str);
            }
        });
    }

    /* renamed from: lambda$notifyError$5$com-android-support-OnlineStats, reason: not valid java name */
    /* synthetic */ void m1378lambda$notifyError$5$comandroidsupportOnlineStats(LatestOwnerListener.Snapshot snapshot, String str) {
        if (this.listeners.isCurrent(snapshot)) {
            ((OnlineCountListener) snapshot.listener()).onError(str);
        }
    }

    private static long nextSequence(long j) {
        if (j != Long.MAX_VALUE) {
            return j + 1;
        }
        throw new IllegalStateException("presence sequence exhausted");
    }

    private long nextPersistedGenerationLocked() {
        SharedPreferences sharedPreferences = this.presencePrefs;
        long j = PRESENCE_GENERATION_FLOOR;
        long jMax = Math.max(Math.max(this.generation, sharedPreferences.getLong(KEY_LAST_GENERATION, PRESENCE_GENERATION_FLOOR)), PRESENCE_GENERATION_FLOOR);
        if (jMax < Long.MAX_VALUE) {
            j = jMax;
        }
        long j2 = j + 1;
        this.presencePrefs.edit().putLong(KEY_LAST_GENERATION, j2).apply();
        return j2;
    }

    private void cancelHeartbeatLocked() {
        ScheduledFuture<?> scheduledFuture = this.heartbeatTask;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            this.heartbeatTask = null;
        }
    }

    private void cancelActiveRequestLocked() {
        JccGatewayClient.Cancellation cancellation = this.activeRequest;
        if (cancellation != null) {
            cancellation.cancel();
            this.activeRequest = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class PresenceSnapshot {
        final long generation;
        final boolean online;
        final long sequence;

        PresenceSnapshot(long j, long j2, boolean z) {
            this.generation = j;
            this.sequence = j2;
            this.online = z;
        }
    }
}
