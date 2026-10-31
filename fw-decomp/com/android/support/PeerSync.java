package com.android.support;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.support.JccGatewayClient;
import com.android.support.JccSyncCursor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class PeerSync {
    private static final int HEARTBEAT_SECONDS = 10;
    private static final String TAG = "PeerSync";
    private static PeerSync instance;
    private JccGatewayClient.Cancellation activeRequest;
    private final JccSyncCursor cursor;
    private final JccGatewayClient gateway;
    private ScheduledFuture<?> heartbeatTask;
    private PeerUpdateListener listener;
    private final Handler mainHandler;
    private final ScheduledExecutorService worker;
    private final Object stateLock = new Object();
    private Set<Integer> peerSeats = Collections.emptySet();

    public interface PeerUpdateListener {
        void onPeerSeatsUpdated(Set<Integer> set);
    }

    private PeerSync(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() { // from class: com.android.support.PeerSync$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return PeerSync.lambda$new$0(runnable);
            }
        });
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        this.worker = scheduledThreadPoolExecutor;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.gateway = JccGatewayClient.shared();
        this.cursor = new JccSyncCursor();
    }

    static /* synthetic */ Thread lambda$new$0(Runnable runnable) {
        Thread thread = new Thread(runnable, ThreadNaming.WORKER);
        thread.setDaemon(true);
        return thread;
    }

    public static synchronized PeerSync getInstance(Context context) {
        if (instance == null) {
            instance = new PeerSync(context.getApplicationContext());
        }
        return instance;
    }

    public void setListener(PeerUpdateListener peerUpdateListener) {
        synchronized (this.stateLock) {
            this.listener = peerUpdateListener;
        }
    }

    public void start(String str, int i) {
        final JccSyncCursor.Snapshot snapshotStop;
        synchronized (this.stateLock) {
            if (!this.cursor.isRunning()) {
                snapshotStop = null;
            } else {
                if (this.cursor.currentMatches(str, i)) {
                    return;
                }
                snapshotStop = this.cursor.stop();
                cancelActiveRequestLocked();
            }
            try {
                final JccSyncCursor.Snapshot snapshotStart = this.cursor.start(str, i);
                this.peerSeats = Collections.emptySet();
                cancelHeartbeatLocked();
                this.heartbeatTask = this.worker.scheduleAtFixedRate(new Runnable() { // from class: com.android.support.PeerSync$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.enqueueHeartbeat();
                    }
                }, 10L, 10L, TimeUnit.SECONDS);
                notifyListener(Collections.emptySet());
                if (snapshotStop != null) {
                    this.worker.execute(new Runnable() { // from class: com.android.support.PeerSync$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1402lambda$start$1$comandroidsupportPeerSync(snapshotStop);
                        }
                    });
                }
                this.worker.execute(new Runnable() { // from class: com.android.support.PeerSync$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1403lambda$start$2$comandroidsupportPeerSync(snapshotStart);
                    }
                });
            } catch (IllegalArgumentException unused) {
                Log.w(TAG, "peer_start_rejected");
            }
        }
    }

    public void stop() {
        Set<Integer> setEmptySet = Collections.emptySet();
        synchronized (this.stateLock) {
            if (this.cursor.isRunning()) {
                final JccSyncCursor.Snapshot snapshotStop = this.cursor.stop();
                cancelHeartbeatLocked();
                cancelActiveRequestLocked();
                this.peerSeats = setEmptySet;
                notifyListener(setEmptySet);
                this.worker.execute(new Runnable() { // from class: com.android.support.PeerSync$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1404lambda$stop$3$comandroidsupportPeerSync(snapshotStop);
                    }
                });
            }
        }
    }

    public Set<Integer> getPeerSeats() {
        Set<Integer> set;
        synchronized (this.stateLock) {
            set = this.peerSeats;
        }
        return set;
    }

    public boolean isRunning() {
        return this.cursor.isRunning();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enqueueHeartbeat() {
        synchronized (this.stateLock) {
            if (this.cursor.isRunning()) {
                JccSyncCursor.Snapshot snapshotNextOnline = this.cursor.nextOnline();
                cancelActiveRequestLocked();
                m1404lambda$stop$3$comandroidsupportPeerSync(snapshotNextOnline);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: sendSnapshot, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public void m1404lambda$stop$3$comandroidsupportPeerSync(JccSyncCursor.Snapshot snapshot) {
        JccGatewayClient.Cancellation cancellation = new JccGatewayClient.Cancellation();
        synchronized (this.stateLock) {
            this.activeRequest = cancellation;
        }
        try {
            try {
                JSONObject jSONObjectPeerSync = this.gateway.peerSync(snapshot, cancellation);
                long jOptLong = jSONObjectPeerSync.optLong("accepted_seq", -1L);
                if (snapshot.online() && this.cursor.acceptsResponse(snapshot.generation(), jOptLong)) {
                    JSONArray jSONArrayOptJSONArray = jSONObjectPeerSync.optJSONArray("peers");
                    if (jSONArrayOptJSONArray != null && jSONArrayOptJSONArray.length() <= 7) {
                        HashSet hashSet = new HashSet();
                        for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                            JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i);
                            if (jSONObjectOptJSONObject != null && jSONObjectOptJSONObject.length() == 4 && jSONObjectOptJSONObject.has("seat") && jSONObjectOptJSONObject.has("generation") && jSONObjectOptJSONObject.has("seq") && jSONObjectOptJSONObject.has("last_seen")) {
                                int iOptInt = jSONObjectOptJSONObject.optInt("seat", -1);
                                if (iOptInt >= 0 && iOptInt <= 7) {
                                    hashSet.add(Integer.valueOf(iOptInt));
                                }
                                Log.w(TAG, "peer_response_invalid");
                                synchronized (this.stateLock) {
                                    if (this.activeRequest == cancellation) {
                                        this.activeRequest = null;
                                    }
                                }
                                return;
                            }
                            Log.w(TAG, "peer_response_invalid");
                            synchronized (this.stateLock) {
                                if (this.activeRequest == cancellation) {
                                    this.activeRequest = null;
                                }
                            }
                            return;
                        }
                        Set<Integer> setUnmodifiableSet = Collections.unmodifiableSet(hashSet);
                        synchronized (this.stateLock) {
                            if (!this.cursor.acceptsResponse(snapshot.generation(), jOptLong)) {
                                synchronized (this.stateLock) {
                                    if (this.activeRequest == cancellation) {
                                        this.activeRequest = null;
                                    }
                                }
                                return;
                            }
                            boolean zEquals = setUnmodifiableSet.equals(this.peerSeats);
                            if (!zEquals) {
                                this.peerSeats = setUnmodifiableSet;
                            }
                            if (!zEquals) {
                                notifyListener(setUnmodifiableSet);
                            }
                            synchronized (this.stateLock) {
                                if (this.activeRequest == cancellation) {
                                    this.activeRequest = null;
                                }
                            }
                            return;
                        }
                    }
                    Log.w(TAG, "peer_response_invalid");
                    synchronized (this.stateLock) {
                        if (this.activeRequest == cancellation) {
                            this.activeRequest = null;
                        }
                    }
                    return;
                }
                synchronized (this.stateLock) {
                    if (this.activeRequest == cancellation) {
                        this.activeRequest = null;
                    }
                }
            } catch (JccGatewayClient.JccException e) {
                if (!"REQUEST_CANCELLED".equals(e.code)) {
                    Log.w(TAG, "peer_sync_failed code=" + e.code);
                }
                synchronized (this.stateLock) {
                    if (this.activeRequest == cancellation) {
                        this.activeRequest = null;
                    }
                }
            } catch (Exception unused) {
                Log.w(TAG, "peer_sync_failed");
                synchronized (this.stateLock) {
                    if (this.activeRequest == cancellation) {
                        this.activeRequest = null;
                    }
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

    private void notifyListener(final Set<Integer> set) {
        final PeerUpdateListener peerUpdateListener;
        synchronized (this.stateLock) {
            peerUpdateListener = this.listener;
        }
        if (peerUpdateListener != null) {
            this.mainHandler.post(new Runnable() { // from class: com.android.support.PeerSync$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1401lambda$notifyListener$4$comandroidsupportPeerSync(peerUpdateListener, set);
                }
            });
        }
    }

    /* renamed from: lambda$notifyListener$4$com-android-support-PeerSync, reason: not valid java name */
    /* synthetic */ void m1401lambda$notifyListener$4$comandroidsupportPeerSync(PeerUpdateListener peerUpdateListener, Set set) {
        synchronized (this.stateLock) {
            if (this.listener == peerUpdateListener && this.peerSeats == set) {
                peerUpdateListener.onPeerSeatsUpdated(set);
            }
        }
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
}
