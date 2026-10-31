package com.android.support;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.android.support.JccGatewayClient;
import com.android.support.ShareRoomState;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class ShareRoomController implements AutoCloseable {
    static final int KEEPALIVE_SECONDS = 5;
    private static final int SAMPLE_SECONDS = 1;
    private final ShareSnapshotBuilder builder;
    private volatile boolean closed;
    private volatile JccGatewayClient.Cancellation currentCancellation;
    private final ScheduledExecutorService executor;
    private final JccGatewayClient gateway;
    private long lastPublishedAt;
    private String lastSnapshot;
    private final Listener listener;
    private final Handler mainHandler;
    private ScheduledFuture<?> publishFuture;
    private boolean sawLiveMatch;
    private long startedAt;
    private final ShareRoomState state;

    public interface Listener {
        void onShareState(ViewState viewState);
    }

    static /* synthetic */ Thread lambda$new$0(Runnable runnable) {
        Thread thread = new Thread(runnable, ThreadNaming.WORKER);
        thread.setDaemon(true);
        return thread;
    }

    public ShareRoomController(SharedMemManager sharedMemManager, Listener listener) {
        this(sharedMemManager, JccGatewayClient.shared(), listener);
    }

    ShareRoomController(SharedMemManager sharedMemManager, JccGatewayClient jccGatewayClient, Listener listener) {
        this.executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() { // from class: com.android.support.ShareRoomController$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return ShareRoomController.lambda$new$0(runnable);
            }
        });
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.state = new ShareRoomState();
        this.lastSnapshot = "";
        if (sharedMemManager == null || jccGatewayClient == null || listener == null) {
            throw new IllegalArgumentException("share controller dependencies are required");
        }
        this.builder = new ShareSnapshotBuilder(sharedMemManager);
        this.gateway = jccGatewayClient;
        this.listener = listener;
    }

    public synchronized void start() {
        if (!this.closed && !this.state.snapshot().active()) {
            cancelPublisher();
            this.lastSnapshot = "";
            this.lastPublishedAt = 0L;
            this.sawLiveMatch = false;
            this.startedAt = SystemClock.elapsedRealtime();
            final long jBegin = this.state.begin();
            notifyState("正在创建共享房间");
            this.executor.execute(new Runnable() { // from class: com.android.support.ShareRoomController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1431lambda$start$1$comandroidsupportShareRoomController(jBegin);
                }
            });
        }
    }

    public synchronized void stop() {
        final ShareRoomState.Snapshot snapshot = this.state.snapshot();
        if (snapshot.active()) {
            cancelPublisher();
            cancelCurrentRequest();
            final long jFinishAndAllocateStopSequence = this.state.finishAndAllocateStopSequence(snapshot.generation());
            this.state.disable(snapshot.generation());
            notifyState("共享已关闭");
            if (jFinishAndAllocateStopSequence > 0) {
                this.executor.execute(new Runnable() { // from class: com.android.support.ShareRoomController$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1432lambda$stop$2$comandroidsupportShareRoomController(snapshot, jFinishAndAllocateStopSequence);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$stop$2$com-android-support-ShareRoomController, reason: not valid java name */
    /* synthetic */ void m1432lambda$stop$2$comandroidsupportShareRoomController(ShareRoomState.Snapshot snapshot, long j) {
        stopRemote(snapshot.room(), snapshot.generation(), j);
    }

    public ShareRoomState.Snapshot snapshot() {
        return this.state.snapshot();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v7, types: [com.android.support.JccGatewayClient$Cancellation] */
    /* JADX WARN: Type inference failed for: r11v9, types: [com.android.support.JccGatewayClient$Cancellation] */
    /* renamed from: createRoom, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public void m1431lambda$start$1$comandroidsupportShareRoomController(final long j) {
        if (!isCurrent(j)) {
            return;
        }
        JccGatewayClient.Cancellation cancellation = new JccGatewayClient.Cancellation();
        this.currentCancellation = cancellation;
        try {
            try {
                if (!this.state.installRoom(j, this.gateway.shareStart(j, cancellation).room)) {
                    if (this.currentCancellation == cancellation) {
                        this.currentCancellation = null;
                        return;
                    }
                    return;
                }
                notifyState("房间已建立，等待对局");
                synchronized (this) {
                    if (this.closed || !isCurrent(j)) {
                        ?? r11 = this.currentCancellation;
                        j = r11;
                        if (r11 == cancellation) {
                            this.currentCancellation = null;
                        }
                    } else {
                        this.publishFuture = this.executor.scheduleAtFixedRate(new Runnable() { // from class: com.android.support.ShareRoomController$$ExternalSyntheticLambda4
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.m1427lambda$createRoom$3$comandroidsupportShareRoomController(j);
                            }
                        }, 0L, 1L, TimeUnit.SECONDS);
                        ?? r112 = this.currentCancellation;
                        j = r112;
                        if (r112 == cancellation) {
                            this.currentCancellation = null;
                        }
                    }
                }
            } catch (Exception unused) {
                if (isCurrent(j)) {
                    this.state.updatePhase(j, ShareRoomState.Phase.ERROR);
                    notifyState("创建失败，正在重试");
                    this.executor.schedule(new Runnable() { // from class: com.android.support.ShareRoomController$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1428lambda$createRoom$4$comandroidsupportShareRoomController(j);
                        }
                    }, 5L, TimeUnit.SECONDS);
                    if (this.currentCancellation != cancellation) {
                        return;
                    }
                } else if (this.currentCancellation != cancellation) {
                    return;
                }
                this.currentCancellation = null;
            }
        } finally {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: publishTick, reason: merged with bridge method [inline-methods] */
    public void m1427lambda$createRoom$3$comandroidsupportShareRoomController(long j) {
        ShareRoomState.Snapshot snapshot = this.state.snapshot();
        if (snapshot.active() && snapshot.generation() == j && !snapshot.room().isEmpty()) {
            try {
                int iMin = (int) Math.min(86400L, Math.max(0L, (SystemClock.elapsedRealtime() - this.startedAt) / 1000));
                JSONObject jSONObjectBuild = this.builder.build("live", iMin);
                String strOptString = jSONObjectBuild.optString("state", "waiting");
                boolean z = true;
                if ("live".equals(strOptString)) {
                    this.sawLiveMatch = true;
                }
                if (!this.sawLiveMatch || "live".equals(strOptString)) {
                    z = false;
                }
                boolean z2 = z;
                if (z2) {
                    jSONObjectBuild = this.builder.build("finished", iMin);
                }
                JSONObject jSONObject = jSONObjectBuild;
                JSONObject jSONObject2 = new JSONObject(jSONObject.toString());
                jSONObject2.remove("captured_at_ms");
                JSONObject jSONObjectOptJSONObject = jSONObject2.optJSONObject("match");
                if (jSONObjectOptJSONObject != null) {
                    jSONObjectOptJSONObject.remove("shared_seconds");
                }
                String string = jSONObject2.toString();
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                if (z2 || !string.equals(this.lastSnapshot) || jElapsedRealtime - this.lastPublishedAt >= 5000) {
                    long jNextSequence = this.state.nextSequence(j);
                    JccGatewayClient.Cancellation cancellation = new JccGatewayClient.Cancellation();
                    this.currentCancellation = cancellation;
                    this.gateway.sharePublish(snapshot.room(), j, jNextSequence, jSONObject, cancellation);
                    if (this.currentCancellation == cancellation) {
                        this.currentCancellation = null;
                    }
                    this.lastSnapshot = string;
                    this.lastPublishedAt = jElapsedRealtime;
                    ShareRoomState shareRoomState = this.state;
                    if (z2) {
                        shareRoomState.finish(j);
                        cancelPublisher();
                        notifyState("本局已结束，战况保留十分钟");
                    } else {
                        shareRoomState.updatePhase(j, "live".equals(strOptString) ? ShareRoomState.Phase.LIVE : ShareRoomState.Phase.WAITING);
                        notifyState("live".equals(strOptString) ? "正在实时共享" : "房间已建立，等待对局");
                    }
                }
            } catch (JccGatewayClient.JccException e) {
                if ("ROOM_NOT_FOUND".equals(e.code)) {
                    recoverMissingRoom(j);
                    return;
                }
                if (PresenceFailurePolicy.isSessionLoss(e.code)) {
                    cancelPublisher();
                    this.state.finish(j);
                    this.state.disable(j);
                    notifyState("登录状态已过期，请重新完成使用验证后再开启共享");
                    return;
                }
                markReconnecting(j);
            } catch (Exception unused) {
                markReconnecting(j);
            }
        }
    }

    private void recoverMissingRoom(long j) {
        synchronized (this) {
            if (this.closed) {
                return;
            }
            final long jRecoverMissingRoom = this.state.recoverMissingRoom(j);
            if (jRecoverMissingRoom <= 0) {
                return;
            }
            cancelPublisher();
            this.currentCancellation = null;
            this.lastSnapshot = "";
            this.lastPublishedAt = 0L;
            notifyState("房间已失效，正在创建新房间");
            this.executor.execute(new Runnable() { // from class: com.android.support.ShareRoomController$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1430x770e0695(jRecoverMissingRoom);
                }
            });
        }
    }

    private void markReconnecting(long j) {
        if (isCurrent(j)) {
            this.state.updatePhase(j, ShareRoomState.Phase.RECONNECTING);
            notifyState("网络波动，正在自动重连");
        }
    }

    private void stopRemote(String str, long j, long j2) {
        try {
            this.gateway.shareStop(str, j, j2, new JccGatewayClient.Cancellation());
        } catch (Exception unused) {
        }
    }

    private boolean isCurrent(long j) {
        ShareRoomState.Snapshot snapshot = this.state.snapshot();
        return !this.closed && snapshot.active() && snapshot.generation() == j;
    }

    private void notifyState(String str) {
        ShareRoomState.Snapshot snapshot = this.state.snapshot();
        final ViewState viewState = new ViewState(snapshot.active(), snapshot.phase(), snapshot.room(), str);
        this.mainHandler.post(new Runnable() { // from class: com.android.support.ShareRoomController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1429lambda$notifyState$6$comandroidsupportShareRoomController(viewState);
            }
        });
    }

    /* renamed from: lambda$notifyState$6$com-android-support-ShareRoomController, reason: not valid java name */
    /* synthetic */ void m1429lambda$notifyState$6$comandroidsupportShareRoomController(ViewState viewState) {
        if (this.closed) {
            return;
        }
        this.listener.onShareState(viewState);
    }

    private synchronized void cancelPublisher() {
        ScheduledFuture<?> scheduledFuture = this.publishFuture;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            this.publishFuture = null;
        }
    }

    private void cancelCurrentRequest() {
        JccGatewayClient.Cancellation cancellation = this.currentCancellation;
        if (cancellation != null) {
            cancellation.cancel();
        }
        this.currentCancellation = null;
    }

    @Override // java.lang.AutoCloseable
    public synchronized void close() {
        if (this.closed) {
            return;
        }
        final ShareRoomState.Snapshot snapshot = this.state.snapshot();
        cancelPublisher();
        cancelCurrentRequest();
        final long jFinishAndAllocateStopSequence = snapshot.active() ? this.state.finishAndAllocateStopSequence(snapshot.generation()) : -1L;
        this.closed = true;
        this.mainHandler.removeCallbacksAndMessages(null);
        if (jFinishAndAllocateStopSequence > 0) {
            this.executor.execute(new Runnable() { // from class: com.android.support.ShareRoomController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1426lambda$close$7$comandroidsupportShareRoomController(snapshot, jFinishAndAllocateStopSequence);
                }
            });
        }
        this.executor.shutdown();
    }

    /* renamed from: lambda$close$7$com-android-support-ShareRoomController, reason: not valid java name */
    /* synthetic */ void m1426lambda$close$7$comandroidsupportShareRoomController(ShareRoomState.Snapshot snapshot, long j) {
        stopRemote(snapshot.room(), snapshot.generation(), j);
    }

    public static final class ViewState {
        public final boolean active;
        public final ShareRoomState.Phase phase;
        public final String room;
        public final String status;

        ViewState(boolean z, ShareRoomState.Phase phase, String str, String str2) {
            this.active = z;
            this.phase = phase;
            this.room = str;
            this.status = str2;
        }
    }
}
