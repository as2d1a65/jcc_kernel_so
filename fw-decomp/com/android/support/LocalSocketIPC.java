package com.android.support;

import android.net.Credentials;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.android.support.IpcV2FrameCodec;
import com.android.support.IpcV2OutboundQueue;
import com.android.support.IpcV2OwnerRegistry;
import com.android.support.IpcV2PendingRequests;
import com.android.support.IpcV2RuntimeState;
import com.android.support.IpcV2TokenStore;
import com.android.support.JccDistributionProfile;
import com.android.support.LocalSocketIPC;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import okhttp3.HttpUrl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class LocalSocketIPC {
    private static final int ACCEPT_TIMEOUT_MS = 3000;
    private static final long CONFIG_SYNC_TIMEOUT_MS = 30000;
    private static final JccDistributionProfile.Entry DISTRIBUTION;
    private static final int FRAME_TIMEOUT_MS = 5000;
    private static final int HANDSHAKE_TIMEOUT_MS = 3000;
    private static final long HEARTBEAT_INTERVAL_MS = 5000;
    private static final long HEARTBEAT_TIMEOUT_MS = 30000;
    private static final Object LEGACY_OWNER;
    private static final int MAX_CANDIDATES = 8;
    private static final int MAX_CONFIG_SYNC_RETRIES = 5;
    private static final boolean RELEASE_LOGS = false;
    private static final long REQUEST_TIMEOUT_MS = 3000;
    public static final String SOCKET_NAME;
    private static final String TAG = "LocalSocketIPC";
    public static final int TCP_PORT_END;
    public static final int TCP_PORT_START;
    private static volatile LocalSocketIPC instance;
    private volatile ConfigSnapshotProvider configSnapshotProvider;
    private volatile HudStyleSnapshotProvider hudStyleSnapshotProvider;
    private IpcV2OwnerRegistry.Token legacyConnectionToken;
    private IpcV2OwnerRegistry.Token legacyMessageToken;
    private volatile ServerGeneration server;
    private volatile IpcV2TokenStore tokenStore;
    private final SecureRandom random = new SecureRandom();
    private final IpcV2RuntimeState<Connection> runtime = new IpcV2RuntimeState<>();
    private final IpcV2PendingRequests<TerminalResult> pending = new IpcV2PendingRequests<>(128);
    private final IpcV2OwnerRegistry<ConnectionListener> connectionListeners = new IpcV2OwnerRegistry<>();
    private final IpcV2OwnerRegistry<MessageListener> messageListeners = new IpcV2OwnerRegistry<>();
    private final int selfPid = Process.myPid();
    private volatile int expectedPeerUid = -1;
    private volatile int expectedBootstrapPeerPid = -1;

    public interface ConfigSnapshotProvider {
        ConfigSnapshot latestSnapshot();
    }

    public interface ConnectionListener {
        void onClientConnected();

        void onClientDisconnected();
    }

    public interface HudStyleSnapshotProvider {
        HudStyleSnapshot latestSnapshot();
    }

    public interface MessageListener {
        void onMessage(String str, JSONObject jSONObject);
    }

    public interface RequestCallback {
        void onTerminal(TerminalResult terminalResult);
    }

    public enum SendResult {
        ACCEPTED,
        NOT_READY,
        BUSY,
        CLOSED,
        INVALID
    }

    static /* synthetic */ void lambda$sendCommand$8(String str, TerminalResult terminalResult) {
    }

    static {
        JccDistributionProfile.Entry entryCurrent = JccDistributionProfile.current();
        DISTRIBUTION = entryCurrent;
        SOCKET_NAME = entryCurrent.socketName;
        TCP_PORT_START = entryCurrent.tcpPortStart;
        TCP_PORT_END = entryCurrent.tcpPortEnd();
        LEGACY_OWNER = new Object();
    }

    static String socketNameForApplicationId(String str) {
        return JccDistributionProfile.forApplicationId(str).socketName;
    }

    static int tcpPortStartForApplicationId(String str) {
        return JccDistributionProfile.forApplicationId(str).tcpPortStart;
    }

    public static final class TerminalResult {
        private final JSONObject body;
        private final String code;
        private final boolean success;

        private TerminalResult(boolean z, String str, JSONObject jSONObject) {
            this.success = z;
            this.code = str;
            this.body = jSONObject == null ? new JSONObject() : jSONObject;
        }

        public static TerminalResult success(JSONObject jSONObject) {
            return new TerminalResult(true, "", jSONObject);
        }

        public static TerminalResult failure(String str) {
            return new TerminalResult(false, str, new JSONObject());
        }

        public boolean isSuccess() {
            return this.success;
        }

        public String code() {
            return this.code;
        }

        public JSONObject body() {
            return this.body;
        }
    }

    public static final class ConfigSnapshot {
        private final JSONObject params;
        private final long revision;

        public ConfigSnapshot(long j, JSONObject jSONObject) {
            if (j <= 0 || jSONObject == null) {
                throw new IllegalArgumentException("invalid config snapshot");
            }
            this.revision = j;
            this.params = LocalSocketIPC.cloneJson(jSONObject);
        }

        public long revision() {
            return this.revision;
        }

        JSONObject paramsCopy() {
            return LocalSocketIPC.cloneJson(this.params);
        }
    }

    public static final class HudStyleSnapshot {
        private final JSONObject params;
        private final long revision;

        public HudStyleSnapshot(long j, JSONObject jSONObject) {
            if (j <= 0 || jSONObject == null) {
                throw new IllegalArgumentException("invalid hud style snapshot");
            }
            this.revision = j;
            this.params = LocalSocketIPC.cloneJson(jSONObject);
        }

        public long revision() {
            return this.revision;
        }

        JSONObject paramsCopy() {
            return LocalSocketIPC.cloneJson(this.params);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class OutboundFrame {
        final ParcelFileDescriptor attachedFd;
        final JSONObject body;
        final String bodyKey;
        final String id;
        final String method;
        final boolean request;
        final String type;

        OutboundFrame(String str, String str2, String str3, JSONObject jSONObject, String str4, boolean z) {
            this(str, str2, str3, jSONObject, str4, z, null);
        }

        OutboundFrame(String str, String str2, String str3, JSONObject jSONObject, String str4, boolean z, ParcelFileDescriptor parcelFileDescriptor) {
            this.type = str;
            this.id = str2;
            this.method = str3;
            this.body = LocalSocketIPC.cloneJson(jSONObject);
            this.bodyKey = str4;
            this.request = z;
            this.attachedFd = parcelFileDescriptor;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class Connection implements Closeable {
        volatile long configRevisionInFlight;
        final String description;
        volatile long hudStyleRevisionInFlight;
        final InputStream input;
        final OutputStream output;
        final int peerPid;
        final int peerUid;
        final Closeable socket;
        final int transport;
        final IpcV2OutboundQueue<OutboundFrame> outbound = new IpcV2OutboundQueue<>(64, 128, 64, 16);
        final IpcV2Sequence inboundSequence = new IpcV2Sequence();
        final IpcV2FrameCodec.Reader inboundFrames = new IpcV2FrameCodec.Reader();
        final AtomicLong outboundSequence = new AtomicLong();
        final AtomicBoolean closed = new AtomicBoolean();
        final AtomicBoolean ready = new AtomicBoolean();
        final AtomicBoolean connectedNotified = new AtomicBoolean();
        final AtomicLong lastReceivedBootMs = new AtomicLong(SystemClock.elapsedRealtime());
        final AtomicLong lastPingBootMs = new AtomicLong();
        volatile String sessionId = "";
        final AtomicBoolean configRequestActive = new AtomicBoolean(false);
        final AtomicInteger configSyncFailures = new AtomicInteger();
        final AtomicBoolean hudStyleRequestActive = new AtomicBoolean(false);

        Connection(InputStream inputStream, OutputStream outputStream, Closeable closeable, String str, int i, int i2, int i3) {
            this.input = inputStream;
            this.output = outputStream;
            this.socket = closeable;
            this.description = str;
            this.peerPid = i;
            this.peerUid = i2;
            this.transport = i3;
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this.closed.compareAndSet(false, true)) {
                Iterator<OutboundFrame> it = this.outbound.closeAndDrain().iterator();
                while (it.hasNext()) {
                    LocalSocketIPC.safeClose(it.next().attachedFd);
                }
                LocalSocketIPC.safeClose(this.socket);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class ServerGeneration implements Closeable {
        final long generation;
        volatile LocalServerSocket localServer;
        volatile int tcpPort;
        volatile ServerSocket tcpServer;
        final AtomicBoolean running = new AtomicBoolean(true);
        final IpcCandidateDispatcher candidates = new IpcCandidateDispatcher(8);
        final ExecutorService ioExecutor = Executors.newCachedThreadPool(new ThreadFactory() { // from class: com.android.support.LocalSocketIPC$ServerGeneration$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return LocalSocketIPC.daemonThread(runnable, ThreadNaming.WORKER);
            }
        });
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() { // from class: com.android.support.LocalSocketIPC$ServerGeneration$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return LocalSocketIPC.daemonThread(runnable, ThreadNaming.WORKER);
            }
        });

        ServerGeneration(long j) {
            this.generation = j;
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this.running.compareAndSet(true, false)) {
                LocalSocketIPC.safeClose(this.localServer);
                LocalSocketIPC.safeClose(this.tcpServer);
                this.candidates.close();
                this.scheduler.shutdownNow();
                this.ioExecutor.shutdownNow();
            }
        }
    }

    private LocalSocketIPC() {
    }

    public static LocalSocketIPC getInstance() {
        if (instance == null) {
            synchronized (LocalSocketIPC.class) {
                if (instance == null) {
                    instance = new LocalSocketIPC();
                }
            }
        }
        return instance;
    }

    static boolean acceptAbstractPeer(int i, int i2, int i3, int i4) {
        return i4 >= 0 && i2 % 100000 == i4 % 100000 && i != i3;
    }

    static boolean acceptExpectedBootstrapPeer(int i, int i2) {
        return AbstractPeerAdmission.acceptBootstrapPeer(i, i2);
    }

    public synchronized void configure(File file, int i) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("privateDirectory required");
        }
        this.tokenStore = new IpcV2TokenStore(file, this.random);
        this.expectedPeerUid = i;
    }

    public IpcV2TokenStore.TokenRecord beginInjection(int i) throws IOException {
        if (i <= 0) {
            throw new IllegalArgumentException("targetPid required");
        }
        IpcV2TokenStore.TokenRecord tokenRecordBeginInjection = requireTokenStore().beginInjection();
        this.expectedBootstrapPeerPid = i;
        return tokenRecordBeginInjection;
    }

    public void prepareBootstrapDelivery(int i) throws IOException {
        if (i <= 0) {
            throw new IllegalArgumentException("targetPid required");
        }
        if (requireTokenStore().activeForBootstrap() == null) {
            throw new IOException("authentication token is missing");
        }
        this.expectedBootstrapPeerPid = i;
    }

    public boolean hasCurrentAuthenticationToken() {
        return requireTokenStore().current() != null;
    }

    public void abortInjection(long j) throws IOException {
        requireTokenStore().abortPending(j);
        this.expectedBootstrapPeerPid = -1;
    }

    public void setConfigSnapshotProvider(ConfigSnapshotProvider configSnapshotProvider) {
        this.configSnapshotProvider = configSnapshotProvider;
    }

    public void setHudStyleSnapshotProvider(HudStyleSnapshotProvider hudStyleSnapshotProvider) {
        this.hudStyleSnapshotProvider = hudStyleSnapshotProvider;
    }

    public synchronized IpcV2OwnerRegistry.Token registerConnectionListener(Object obj, ConnectionListener connectionListener) {
        return this.connectionListeners.register(obj, connectionListener);
    }

    public synchronized IpcV2OwnerRegistry.Token registerMessageListener(Object obj, MessageListener messageListener) {
        return this.messageListeners.register(obj, messageListener);
    }

    public void unregisterConnectionListener(IpcV2OwnerRegistry.Token token) {
        this.connectionListeners.unregister(token);
    }

    public void unregisterMessageListener(IpcV2OwnerRegistry.Token token) {
        this.messageListeners.unregister(token);
    }

    public synchronized void setConnectionListener(ConnectionListener connectionListener) {
        IpcV2OwnerRegistry.Token token = this.legacyConnectionToken;
        if (token != null) {
            this.connectionListeners.unregister(token);
        }
        this.legacyConnectionToken = connectionListener == null ? null : this.connectionListeners.register(LEGACY_OWNER, connectionListener);
    }

    public synchronized void setMessageListener(MessageListener messageListener) {
        IpcV2OwnerRegistry.Token token = this.legacyMessageToken;
        if (token != null) {
            this.messageListeners.unregister(token);
        }
        this.legacyMessageToken = messageListener == null ? null : this.messageListeners.register(LEGACY_OWNER, messageListener);
    }

    public synchronized boolean startServer() {
        boolean z;
        if (!GameRegionState.current().isRuntimeReady()) {
            return false;
        }
        ServerGeneration serverGeneration = this.server;
        if (serverGeneration != null && serverGeneration.running.get()) {
            return true;
        }
        if (this.tokenStore == null) {
            return false;
        }
        final ServerGeneration serverGeneration2 = new ServerGeneration(this.runtime.start());
        try {
            serverGeneration2.localServer = new LocalServerSocket(SOCKET_NAME);
            z = true;
        } catch (IOException unused) {
            z = false;
        }
        int i = TCP_PORT_START;
        while (true) {
            if (i > TCP_PORT_END) {
                break;
            }
            try {
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.setSoTimeout(3000);
                serverSocket.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), i));
                serverGeneration2.tcpServer = serverSocket;
                serverGeneration2.tcpPort = i;
                z = true;
                break;
            } catch (IOException unused2) {
                i++;
            }
        }
        if (!z) {
            serverGeneration2.close();
            this.runtime.stop();
            return false;
        }
        this.server = serverGeneration2;
        if (serverGeneration2.localServer != null) {
            serverGeneration2.ioExecutor.execute(new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() throws IOException {
                    this.f$0.m1238lambda$startServer$0$comandroidsupportLocalSocketIPC(serverGeneration2);
                }
            });
        }
        if (serverGeneration2.tcpServer != null) {
            serverGeneration2.ioExecutor.execute(new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() throws IOException {
                    this.f$0.m1239lambda$startServer$1$comandroidsupportLocalSocketIPC(serverGeneration2);
                }
            });
        }
        serverGeneration2.scheduler.scheduleAtFixedRate(new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1240lambda$startServer$2$comandroidsupportLocalSocketIPC(serverGeneration2);
            }
        }, 1000L, 1000L, TimeUnit.MILLISECONDS);
        return true;
    }

    public synchronized void stopServer() {
        ServerGeneration serverGeneration = this.server;
        this.server = null;
        this.runtime.stop();
        if (serverGeneration != null) {
            serverGeneration.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: abstractAcceptLoop, reason: merged with bridge method [inline-methods] */
    public void m1238lambda$startServer$0$comandroidsupportLocalSocketIPC(ServerGeneration serverGeneration) throws IOException {
        while (m1230lambda$dispatchCandidate$3$comandroidsupportLocalSocketIPC(serverGeneration)) {
            try {
                LocalSocket localSocketAccept = serverGeneration.localServer.accept();
                Credentials peerCredentials = localSocketAccept.getPeerCredentials();
                int pid = peerCredentials.getPid();
                int uid = peerCredentials.getUid();
                if (!acceptAbstractPeer(pid, uid, this.selfPid, this.expectedPeerUid)) {
                    safeClose(localSocketAccept);
                } else if (!acceptExpectedBootstrapPeer(pid, this.expectedBootstrapPeerPid)) {
                    Log.w(TAG, "Abstract peer pid rejected: pid=" + pid + " uid=" + uid + " expectedPid=" + this.expectedBootstrapPeerPid);
                    safeClose(localSocketAccept);
                } else {
                    dispatchCandidate(serverGeneration, new Connection(new BufferedInputStream(localSocketAccept.getInputStream()), new BufferedOutputStream(localSocketAccept.getOutputStream()), localSocketAccept, "Abstract(pid=" + pid + ",uid=" + uid + ")", pid, uid, 0));
                }
            } catch (IOException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: tcpAcceptLoop, reason: merged with bridge method [inline-methods] */
    public void m1239lambda$startServer$1$comandroidsupportLocalSocketIPC(ServerGeneration serverGeneration) throws IOException {
        while (m1230lambda$dispatchCandidate$3$comandroidsupportLocalSocketIPC(serverGeneration)) {
            try {
                Socket socketAccept = serverGeneration.tcpServer.accept();
                socketAccept.setTcpNoDelay(true);
                socketAccept.setKeepAlive(true);
                dispatchCandidate(serverGeneration, new Connection(new BufferedInputStream(socketAccept.getInputStream()), new BufferedOutputStream(socketAccept.getOutputStream()), socketAccept, "TCP(127.0.0.1:" + serverGeneration.tcpPort + ")", -1, -1, 1));
            } catch (SocketTimeoutException | IOException unused) {
            }
        }
    }

    private void dispatchCandidate(final ServerGeneration serverGeneration, final Connection connection) {
        IpcCandidateDispatcher ipcCandidateDispatcher = serverGeneration.candidates;
        ExecutorService executorService = serverGeneration.ioExecutor;
        BooleanSupplier booleanSupplier = new BooleanSupplier() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda6
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return this.f$0.m1230lambda$dispatchCandidate$3$comandroidsupportLocalSocketIPC(serverGeneration);
            }
        };
        Runnable runnable = new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() throws JSONException, IOException {
                this.f$0.m1231lambda$dispatchCandidate$4$comandroidsupportLocalSocketIPC(serverGeneration, connection);
            }
        };
        Objects.requireNonNull(connection);
        ipcCandidateDispatcher.dispatch(executorService, booleanSupplier, runnable, new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() throws IOException {
                connection.close();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0268  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x026e  */
    /* renamed from: authenticateAndPromote, reason: merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void m1231lambda$dispatchCandidate$4$comandroidsupportLocalSocketIPC(final com.android.support.LocalSocketIPC.ServerGeneration r25, com.android.support.LocalSocketIPC.Connection r26) throws org.json.JSONException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 633
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.LocalSocketIPC.m1231lambda$dispatchCandidate$4$comandroidsupportLocalSocketIPC(com.android.support.LocalSocketIPC$ServerGeneration, com.android.support.LocalSocketIPC$Connection):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: writerLoop, reason: merged with bridge method [inline-methods] */
    public void m1227xfa593efe(ServerGeneration serverGeneration, IpcV2RuntimeState.Session<Connection> session) {
        OutboundFrame outboundFrameTake;
        Connection connectionValue = session.value();
        while (m1230lambda$dispatchCandidate$3$comandroidsupportLocalSocketIPC(serverGeneration) && !connectionValue.closed.get() && (outboundFrameTake = connectionValue.outbound.take()) != null) {
            try {
                LocalSocket localSocket = connectionValue.socket instanceof LocalSocket ? (LocalSocket) connectionValue.socket : null;
                try {
                    long jIncrementAndGet = connectionValue.outboundSequence.incrementAndGet();
                    if (jIncrementAndGet <= 0) {
                        throw new IOException("outbound sequence exhausted");
                    }
                    JSONObject jSONObjectPut = new JSONObject().put("v", 2).put("type", outboundFrameTake.type).put("sessionId", session.sessionId()).put("id", outboundFrameTake.id).put("seq", jIncrementAndGet).put("method", outboundFrameTake.method).put(outboundFrameTake.bodyKey, outboundFrameTake.body);
                    logTxFrame(session, connectionValue, outboundFrameTake, jIncrementAndGet);
                    if (outboundFrameTake.attachedFd != null) {
                        if (connectionValue.transport != 0 || localSocket == null) {
                            throw new IOException("fd transport requires authenticated LocalSocket");
                        }
                        localSocket.setFileDescriptorsForSend(new FileDescriptor[]{outboundFrameTake.attachedFd.getFileDescriptor()});
                    }
                    IpcV2FrameCodec.writeUtf8(connectionValue.output, jSONObjectPut.toString());
                    if (localSocket != null) {
                        try {
                            if (outboundFrameTake.attachedFd != null) {
                                localSocket.setFileDescriptorsForSend(null);
                            }
                        } finally {
                        }
                    }
                    safeClose(outboundFrameTake.attachedFd);
                    if (outboundFrameTake.request) {
                        this.pending.markWritten(outboundFrameTake.id, session.sessionId());
                    }
                } catch (Throwable th) {
                    if (localSocket != null) {
                        try {
                            if (outboundFrameTake.attachedFd != null) {
                                localSocket.setFileDescriptorsForSend(null);
                            }
                        } finally {
                        }
                    }
                    throw th;
                }
            } catch (Exception unused) {
                disconnectIfCurrent(session, "writer failure");
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: readerLoop, reason: merged with bridge method [inline-methods] */
    public void m1228x6e98775d(ServerGeneration serverGeneration, IpcV2RuntimeState.Session<Connection> session) {
        Connection connectionValue = session.value();
        while (m1230lambda$dispatchCandidate$3$comandroidsupportLocalSocketIPC(serverGeneration) && !connectionValue.closed.get()) {
            try {
                try {
                    JSONObject jSONObject = new JSONObject(connectionValue.inboundFrames.readUtf8(connectionValue.input));
                    validateEnvelope(jSONObject, session, connectionValue.inboundSequence);
                    connectionValue.lastReceivedBootMs.set(SystemClock.elapsedRealtime());
                    logRxFrame(session, jSONObject);
                    handleInbound(session, jSONObject);
                } catch (InterruptedIOException unused) {
                    if (SystemClock.elapsedRealtime() - connectionValue.lastReceivedBootMs.get() > 30000) {
                        disconnectIfCurrent(session, "heartbeat timeout");
                        return;
                    }
                }
            } catch (Exception unused2) {
                disconnectIfCurrent(session, "reader failure");
                return;
            }
        }
    }

    private void handleInbound(IpcV2RuntimeState.Session<Connection> session, JSONObject jSONObject) throws Exception {
        if (this.runtime.isActive(session)) {
            final Connection connectionValue = session.value();
            String string = jSONObject.getString("type");
            String string2 = jSONObject.getString("id");
            final String string3 = jSONObject.getString("method");
            if ("response".equals(string)) {
                this.pending.complete(string2, session.sessionId(), TerminalResult.success(requireObject(jSONObject, "result")));
                return;
            }
            if ("error".equals(string)) {
                this.pending.complete(string2, session.sessionId(), TerminalResult.failure(requireObject(jSONObject, "error").optString("code", "invalid_request")));
                return;
            }
            if ("ping".equals(string)) {
                offerControl(connectionValue, new OutboundFrame("pong", string2, "heartbeat.pong", new JSONObject(), "params", false));
                return;
            }
            if ("pong".equals(string)) {
                return;
            }
            if (!connectionValue.ready.get()) {
                throw new IOException("business frame received before ready");
            }
            if (!"state".equals(string) && !NotificationCompat.CATEGORY_EVENT.equals(string)) {
                throw new IOException("unsupported inbound message type");
            }
            final JSONObject jSONObjectRequireObject = requireObject(jSONObject, "params");
            final List<MessageListener> listSnapshot = this.messageListeners.snapshot();
            final OutboundFrame outboundFrame = NotificationCompat.CATEGORY_EVENT.equals(string) ? new OutboundFrame("response", string2, string3, new JSONObject().put(NotificationCompat.CATEGORY_STATUS, "completed"), "result", false) : null;
            this.runtime.runIfActive(session, new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    LocalSocketIPC.lambda$handleInbound$7(listSnapshot, jSONObjectRequireObject, string3, outboundFrame, connectionValue);
                }
            });
        }
    }

    static /* synthetic */ void lambda$handleInbound$7(List list, JSONObject jSONObject, String str, OutboundFrame outboundFrame, Connection connection) {
        int size = list.size();
        int i = 0;
        while (i < size) {
            try {
                ((MessageListener) list.get(i)).onMessage(str, i == size + (-1) ? jSONObject : cloneJson(jSONObject));
            } catch (RuntimeException unused) {
            }
            i++;
        }
        if (outboundFrame != null) {
            offerControl(connection, outboundFrame);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: maintain, reason: merged with bridge method [inline-methods] */
    public void m1240lambda$startServer$2$comandroidsupportLocalSocketIPC(ServerGeneration serverGeneration) {
        IpcV2RuntimeState.Session<Connection> sessionActiveSession;
        if (m1230lambda$dispatchCandidate$3$comandroidsupportLocalSocketIPC(serverGeneration) && (sessionActiveSession = this.runtime.activeSession()) != null && sessionActiveSession.serverGeneration() == serverGeneration.generation) {
            Connection connectionValue = sessionActiveSession.value();
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            this.pending.expire(jElapsedRealtime, TerminalResult.failure("request_timeout_before_write"), TerminalResult.failure("response_timeout"));
            if (jElapsedRealtime - connectionValue.lastReceivedBootMs.get() > 30000) {
                disconnectIfCurrent(sessionActiveSession, "heartbeat timeout");
            } else {
                if (!connectionValue.ready.get() || jElapsedRealtime - connectionValue.lastPingBootMs.get() < HEARTBEAT_INTERVAL_MS) {
                    return;
                }
                connectionValue.lastPingBootMs.set(jElapsedRealtime);
                offerControl(connectionValue, new OutboundFrame("ping", IpcV2Protocol.randomId(this.random), "heartbeat.ping", new JSONObject(), "params", false));
            }
        }
    }

    public SendResult sendCommand(final String str, JSONObject jSONObject) {
        return sendRequest(str, jSONObject, REQUEST_TIMEOUT_MS, new RequestCallback() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda14
            @Override // com.android.support.LocalSocketIPC.RequestCallback
            public final void onTerminal(LocalSocketIPC.TerminalResult terminalResult) {
                LocalSocketIPC.lambda$sendCommand$8(str, terminalResult);
            }
        });
    }

    public SendResult sendCommand(String str) {
        return sendCommand(str, new JSONObject());
    }

    public SendResult sendRequest(String str, JSONObject jSONObject, long j, RequestCallback requestCallback) {
        if (!validMethod(str) || jSONObject == null || j <= 0 || requestCallback == null) {
            return SendResult.INVALID;
        }
        GameRegion gameRegionCurrent = GameRegionState.current();
        if (!GameRegionFeaturePolicy.allowsAction(gameRegionCurrent, GameRegionControl.visible(gameRegionCurrent) && GameRegionControl.enabled(gameRegionCurrent), UiSurfaceControl.current(), str)) {
            requestCallback.onTerminal(TerminalResult.failure("game_region_feature_unavailable"));
            return SendResult.NOT_READY;
        }
        IpcV2RuntimeState.Session<Connection> sessionActiveSession = this.runtime.activeSession();
        if (sessionActiveSession == null || !sessionActiveSession.value().ready.get()) {
            requestCallback.onTerminal(TerminalResult.failure("not_ready"));
            return SendResult.NOT_READY;
        }
        String strRandomId = IpcV2Protocol.randomId(this.random);
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j2 = j < Long.MAX_VALUE - jElapsedRealtime ? jElapsedRealtime + j : Long.MAX_VALUE;
        IpcV2PendingRequests<TerminalResult> ipcV2PendingRequests = this.pending;
        String strSessionId = sessionActiveSession.sessionId();
        Objects.requireNonNull(requestCallback);
        if (ipcV2PendingRequests.register(strRandomId, strSessionId, j2, new LocalSocketIPC$$ExternalSyntheticLambda16(requestCallback)) != IpcV2PendingRequests.RegisterResult.ACCEPTED) {
            requestCallback.onTerminal(TerminalResult.failure("busy"));
            return SendResult.BUSY;
        }
        IpcV2OutboundQueue.OfferResult offerResultOfferRequest = sessionActiveSession.value().outbound.offerRequest(new OutboundFrame("request", strRandomId, str, jSONObject, "params", true));
        if (offerResultOfferRequest != IpcV2OutboundQueue.OfferResult.ACCEPTED) {
            this.pending.complete(strRandomId, sessionActiveSession.sessionId(), TerminalResult.failure(offerResultOfferRequest == IpcV2OutboundQueue.OfferResult.CLOSED ? "not_connected" : "busy"));
            return offerResultOfferRequest == IpcV2OutboundQueue.OfferResult.CLOSED ? SendResult.CLOSED : SendResult.BUSY;
        }
        return SendResult.ACCEPTED;
    }

    public SendResult sendDiagnosticTraceAttach(ParcelFileDescriptor parcelFileDescriptor, JSONObject jSONObject, long j, RequestCallback requestCallback) throws IOException {
        if (parcelFileDescriptor == null || jSONObject == null || j <= 0 || requestCallback == null) {
            safeClose(parcelFileDescriptor);
            return SendResult.INVALID;
        }
        IpcV2RuntimeState.Session<Connection> sessionActiveSession = this.runtime.activeSession();
        if (sessionActiveSession == null || !sessionActiveSession.value().ready.get() || sessionActiveSession.value().transport != 0 || !(sessionActiveSession.value().socket instanceof LocalSocket)) {
            safeClose(parcelFileDescriptor);
            requestCallback.onTerminal(TerminalResult.failure("trace_transport_unavailable"));
            return SendResult.NOT_READY;
        }
        String strRandomId = IpcV2Protocol.randomId(this.random);
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j2 = j < Long.MAX_VALUE - jElapsedRealtime ? jElapsedRealtime + j : Long.MAX_VALUE;
        IpcV2PendingRequests<TerminalResult> ipcV2PendingRequests = this.pending;
        String strSessionId = sessionActiveSession.sessionId();
        Objects.requireNonNull(requestCallback);
        if (ipcV2PendingRequests.register(strRandomId, strSessionId, j2, new LocalSocketIPC$$ExternalSyntheticLambda16(requestCallback)) != IpcV2PendingRequests.RegisterResult.ACCEPTED) {
            safeClose(parcelFileDescriptor);
            requestCallback.onTerminal(TerminalResult.failure("busy"));
            return SendResult.BUSY;
        }
        IpcV2OutboundQueue.OfferResult offerResultOfferRequest = sessionActiveSession.value().outbound.offerRequest(new OutboundFrame("request", strRandomId, "diagnostics.trace.attach", jSONObject, "params", true, parcelFileDescriptor));
        if (offerResultOfferRequest != IpcV2OutboundQueue.OfferResult.ACCEPTED) {
            safeClose(parcelFileDescriptor);
            this.pending.complete(strRandomId, sessionActiveSession.sessionId(), TerminalResult.failure(offerResultOfferRequest == IpcV2OutboundQueue.OfferResult.CLOSED ? "not_connected" : "busy"));
            return offerResultOfferRequest == IpcV2OutboundQueue.OfferResult.CLOSED ? SendResult.CLOSED : SendResult.BUSY;
        }
        return SendResult.ACCEPTED;
    }

    public boolean requestConfigSync() throws JSONException {
        IpcV2RuntimeState.Session<Connection> sessionActiveSession = this.runtime.activeSession();
        if (sessionActiveSession == null || !sessionActiveSession.value().ready.get()) {
            return false;
        }
        enqueueLatestConfig(sessionActiveSession);
        return sessionActiveSession.value().configRequestActive.get();
    }

    public void requestHudStyleSync() throws JSONException {
        IpcV2RuntimeState.Session<Connection> sessionActiveSession = this.runtime.activeSession();
        if (sessionActiveSession == null || !sessionActiveSession.value().ready.get() || sessionActiveSession.value().hudStyleRequestActive.get()) {
            return;
        }
        enqueueLatestHudStyle(sessionActiveSession);
    }

    private void enqueueLatestConfig(final IpcV2RuntimeState.Session<Connection> session) throws JSONException {
        final Connection connectionValue = session.value();
        if (this.runtime.isActive(session) && connectionValue.configRequestActive.compareAndSet(false, true)) {
            ConfigSnapshotProvider configSnapshotProvider = this.configSnapshotProvider;
            if (configSnapshotProvider == null) {
                runtimeVisible("config provider unavailable; scheduling retry");
                connectionValue.configRevisionInFlight = 0L;
                connectionValue.configRequestActive.set(false);
                scheduleConfigRetry(session, connectionValue, "missing config provider");
                return;
            }
            try {
                final ConfigSnapshot configSnapshotLatestSnapshot = configSnapshotProvider.latestSnapshot();
                final String strRandomId = IpcV2Protocol.randomId(this.random);
                final JSONObject jSONObjectParamsCopy = configSnapshotLatestSnapshot.paramsCopy();
                jSONObjectParamsCopy.put("revision", configSnapshotLatestSnapshot.revision());
                if (this.runtime.runIfActive(session, new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1232lambda$enqueueLatestConfig$10$comandroidsupportLocalSocketIPC(connectionValue, configSnapshotLatestSnapshot, strRandomId, session, jSONObjectParamsCopy);
                    }
                })) {
                    return;
                }
                connectionValue.configRevisionInFlight = 0L;
                connectionValue.configRequestActive.set(false);
            } catch (Exception e) {
                runtimeVisible("config snapshot failed: " + safeMessage(e));
                connectionValue.configRevisionInFlight = 0L;
                connectionValue.configRequestActive.set(false);
                scheduleConfigRetry(session, connectionValue, "config snapshot failed");
            }
        }
    }

    /* renamed from: lambda$enqueueLatestConfig$10$com-android-support-LocalSocketIPC, reason: not valid java name */
    /* synthetic */ void m1232lambda$enqueueLatestConfig$10$comandroidsupportLocalSocketIPC(Connection connection, final ConfigSnapshot configSnapshot, String str, final IpcV2RuntimeState.Session session, JSONObject jSONObject) {
        connection.configRevisionInFlight = configSnapshot.revision();
        if (this.pending.register(str, session.sessionId(), SystemClock.elapsedRealtime() + 30000, new IpcV2PendingRequests.Callback() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda9
            @Override // com.android.support.IpcV2PendingRequests.Callback
            public final void onTerminal(Object obj) throws JSONException {
                this.f$0.m1233lambda$enqueueLatestConfig$9$comandroidsupportLocalSocketIPC(session, configSnapshot, (LocalSocketIPC.TerminalResult) obj);
            }
        }) != IpcV2PendingRequests.RegisterResult.ACCEPTED) {
            connection.configRevisionInFlight = 0L;
            connection.configRequestActive.set(false);
            scheduleConfigRetry(session, connection, "config pending queue full");
            return;
        }
        runtimeVisible("enqueue config.replace revision=" + configSnapshot.revision() + " keys=" + jsonKeys(jSONObject));
        IpcV2OutboundQueue.StateOffer<OutboundFrame> stateOfferOfferStateReplacing = connection.outbound.offerStateReplacing("config.replace", new OutboundFrame("state", str, "config.replace", jSONObject, "params", true));
        IpcV2OutboundQueue.OfferResult offerResultResult = stateOfferOfferStateReplacing.result();
        OutboundFrame outboundFrameReplaced = stateOfferOfferStateReplacing.replaced();
        if (outboundFrameReplaced != null) {
            this.pending.cancel(outboundFrameReplaced.id, session.sessionId());
        }
        if (offerResultResult == IpcV2OutboundQueue.OfferResult.ACCEPTED || offerResultResult == IpcV2OutboundQueue.OfferResult.COALESCED) {
            return;
        }
        this.pending.complete(str, session.sessionId(), TerminalResult.failure("busy"));
    }

    /* renamed from: lambda$enqueueLatestConfig$9$com-android-support-LocalSocketIPC, reason: not valid java name */
    /* synthetic */ void m1233lambda$enqueueLatestConfig$9$comandroidsupportLocalSocketIPC(IpcV2RuntimeState.Session session, ConfigSnapshot configSnapshot, TerminalResult terminalResult) throws JSONException {
        handleConfigAck(session, configSnapshot.revision(), terminalResult);
    }

    private void handleConfigAck(final IpcV2RuntimeState.Session<Connection> session, final long j, final TerminalResult terminalResult) throws JSONException {
        ConfigSnapshot configSnapshotLatestSnapshot;
        final Connection connectionValue = session.value();
        final long jOptLong = terminalResult.body().optLong("revision", -1L);
        if (this.runtime.runIfActive(session, new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1236lambda$handleConfigAck$11$comandroidsupportLocalSocketIPC(connectionValue, terminalResult, j, jOptLong, session);
            }
        }) && terminalResult.isSuccess() && jOptLong == j) {
            ConfigSnapshotProvider configSnapshotProvider = this.configSnapshotProvider;
            if (configSnapshotProvider == null) {
                configSnapshotLatestSnapshot = null;
            } else {
                try {
                    configSnapshotLatestSnapshot = configSnapshotProvider.latestSnapshot();
                } catch (RuntimeException unused) {
                    scheduleConfigRetry(session, connectionValue, "latest config snapshot failed");
                    return;
                }
            }
            if (configSnapshotLatestSnapshot != null && configSnapshotLatestSnapshot.revision() > j) {
                enqueueLatestConfig(session);
            } else {
                enqueueLatestHudStyle(session);
            }
        }
    }

    /* renamed from: lambda$handleConfigAck$11$com-android-support-LocalSocketIPC, reason: not valid java name */
    /* synthetic */ void m1236lambda$handleConfigAck$11$comandroidsupportLocalSocketIPC(Connection connection, TerminalResult terminalResult, long j, long j2, IpcV2RuntimeState.Session session) {
        connection.configRevisionInFlight = 0L;
        connection.configRequestActive.set(false);
        runtimeVisible("config ack success=" + terminalResult.isSuccess() + " code=" + terminalResult.code() + " sentRevision=" + j + " ackRevision=" + j2);
        if (!terminalResult.isSuccess() || j2 != j) {
            scheduleConfigRetry(session, connection, !terminalResult.isSuccess() ? "config ack failed" : "config revision mismatch");
            return;
        }
        connection.configSyncFailures.set(0);
        if (connection.ready.compareAndSet(false, true)) {
            notifyConnected(connection);
        }
    }

    private void scheduleConfigRetry(final IpcV2RuntimeState.Session<Connection> session, final Connection connection, String str) {
        final ServerGeneration serverGeneration = this.server;
        if (serverGeneration == null || !m1230lambda$dispatchCandidate$3$comandroidsupportLocalSocketIPC(serverGeneration) || this.runtime.activeSession() != session || session.isClosed()) {
            return;
        }
        int iIncrementAndGet = connection.configSyncFailures.incrementAndGet();
        if (iIncrementAndGet > 5) {
            runtimeVisible("config sync failed after 5 retries reason=" + str);
            disconnectIfCurrent(session, "config sync failed");
            return;
        }
        runtimeVisible("config sync retry #" + iIncrementAndGet + " reason=" + str);
        try {
            serverGeneration.scheduler.schedule(new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() throws JSONException {
                    this.f$0.m1237lambda$scheduleConfigRetry$12$comandroidsupportLocalSocketIPC(serverGeneration, session, connection);
                }
            }, Math.min(8000L, (1 << Math.min(iIncrementAndGet - 1, 4)) * 500), TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException unused) {
            disconnectIfCurrent(session, "server stopped during config retry");
        }
    }

    /* renamed from: lambda$scheduleConfigRetry$12$com-android-support-LocalSocketIPC, reason: not valid java name */
    /* synthetic */ void m1237lambda$scheduleConfigRetry$12$comandroidsupportLocalSocketIPC(ServerGeneration serverGeneration, IpcV2RuntimeState.Session session, Connection connection) throws JSONException {
        if (m1230lambda$dispatchCandidate$3$comandroidsupportLocalSocketIPC(serverGeneration) && this.runtime.activeSession() == session && !session.isClosed() && connection.configRevisionInFlight == 0) {
            enqueueLatestConfig(session);
        }
    }

    private void enqueueLatestHudStyle(final IpcV2RuntimeState.Session<Connection> session) throws JSONException {
        final Connection connectionValue = session.value();
        if (this.runtime.isActive(session) && connectionValue.ready.get() && connectionValue.hudStyleRequestActive.compareAndSet(false, true)) {
            try {
                HudStyleSnapshotProvider hudStyleSnapshotProvider = this.hudStyleSnapshotProvider;
                if (hudStyleSnapshotProvider == null) {
                    connectionValue.hudStyleRequestActive.set(false);
                    return;
                }
                final HudStyleSnapshot hudStyleSnapshotLatestSnapshot = hudStyleSnapshotProvider.latestSnapshot();
                final JSONObject jSONObjectParamsCopy = hudStyleSnapshotLatestSnapshot.paramsCopy();
                jSONObjectParamsCopy.put("revision", hudStyleSnapshotLatestSnapshot.revision());
                final String strRandomId = IpcV2Protocol.randomId(this.random);
                if (this.runtime.runIfActive(session, new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda12
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1235x3a373f6f(connectionValue, hudStyleSnapshotLatestSnapshot, strRandomId, session, jSONObjectParamsCopy);
                    }
                })) {
                    return;
                }
                connectionValue.hudStyleRequestActive.set(false);
            } catch (Exception e) {
                connectionValue.hudStyleRequestActive.set(false);
                runtimeVisible("hud style snapshot failed: " + safeMessage(e));
            }
        }
    }

    /* renamed from: lambda$enqueueLatestHudStyle$14$com-android-support-LocalSocketIPC, reason: not valid java name */
    /* synthetic */ void m1235x3a373f6f(Connection connection, final HudStyleSnapshot hudStyleSnapshot, String str, final IpcV2RuntimeState.Session session, JSONObject jSONObject) {
        connection.hudStyleRevisionInFlight = hudStyleSnapshot.revision();
        if (this.pending.register(str, session.sessionId(), SystemClock.elapsedRealtime() + 30000, new IpcV2PendingRequests.Callback() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda10
            @Override // com.android.support.IpcV2PendingRequests.Callback
            public final void onTerminal(Object obj) throws JSONException {
                this.f$0.m1234xc5f80710(session, hudStyleSnapshot, (LocalSocketIPC.TerminalResult) obj);
            }
        }) != IpcV2PendingRequests.RegisterResult.ACCEPTED) {
            connection.hudStyleRevisionInFlight = 0L;
            connection.hudStyleRequestActive.set(false);
            runtimeVisible("hud style pending queue full");
            return;
        }
        IpcV2OutboundQueue.StateOffer<OutboundFrame> stateOfferOfferStateReplacing = connection.outbound.offerStateReplacing("hudStyle.replace", new OutboundFrame("state", str, "hudStyle.replace", jSONObject, "params", true));
        if (stateOfferOfferStateReplacing.replaced() != null) {
            this.pending.cancel(stateOfferOfferStateReplacing.replaced().id, session.sessionId());
        }
        if (stateOfferOfferStateReplacing.result() == IpcV2OutboundQueue.OfferResult.ACCEPTED || stateOfferOfferStateReplacing.result() == IpcV2OutboundQueue.OfferResult.COALESCED) {
            return;
        }
        this.pending.complete(str, session.sessionId(), TerminalResult.failure("busy"));
    }

    /* renamed from: lambda$enqueueLatestHudStyle$13$com-android-support-LocalSocketIPC, reason: not valid java name */
    /* synthetic */ void m1234xc5f80710(IpcV2RuntimeState.Session session, HudStyleSnapshot hudStyleSnapshot, TerminalResult terminalResult) throws JSONException {
        handleHudStyleAck(session, hudStyleSnapshot.revision(), terminalResult);
    }

    private void handleHudStyleAck(final IpcV2RuntimeState.Session<Connection> session, long j, TerminalResult terminalResult) throws JSONException {
        HudStyleSnapshotProvider hudStyleSnapshotProvider;
        if (this.runtime.runIfActive(session, new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                LocalSocketIPC.lambda$handleHudStyleAck$15(session);
            }
        })) {
            long jOptLong = terminalResult.body().optLong("revision", -1L);
            runtimeVisible("hud style ack success=" + terminalResult.isSuccess() + " code=" + terminalResult.code() + " sentRevision=" + j + " ackRevision=" + jOptLong);
            if (terminalResult.isSuccess() && jOptLong == j && (hudStyleSnapshotProvider = this.hudStyleSnapshotProvider) != null) {
                try {
                    if (hudStyleSnapshotProvider.latestSnapshot().revision() > j) {
                        enqueueLatestHudStyle(session);
                    }
                } catch (RuntimeException e) {
                    runtimeVisible("latest hud style snapshot failed: " + safeMessage(e));
                }
            }
        }
    }

    static /* synthetic */ void lambda$handleHudStyleAck$15(IpcV2RuntimeState.Session session) {
        ((Connection) session.value()).hudStyleRevisionInFlight = 0L;
        ((Connection) session.value()).hudStyleRequestActive.set(false);
    }

    public boolean isClientConnected() {
        IpcV2RuntimeState.Session<Connection> sessionActiveSession = this.runtime.activeSession();
        return (sessionActiveSession == null || !sessionActiveSession.value().ready.get() || sessionActiveSession.value().closed.get()) ? false : true;
    }

    boolean isClientConnectedToProcess(final int i, final boolean z) {
        final IpcV2RuntimeState.Session<Connection> sessionActiveSession;
        if (i <= 0 || (sessionActiveSession = this.runtime.activeSession()) == null) {
            return false;
        }
        final boolean[] zArr = {false};
        this.runtime.runIfActive(sessionActiveSession, new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                LocalSocketIPC.lambda$isClientConnectedToProcess$16(sessionActiveSession, zArr, i, z);
            }
        });
        return zArr[0];
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0023  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static /* synthetic */ void lambda$isClientConnectedToProcess$16(com.android.support.IpcV2RuntimeState.Session r3, boolean[] r4, int r5, boolean r6) {
        /*
            java.lang.Object r3 = r3.value()
            com.android.support.LocalSocketIPC$Connection r3 = (com.android.support.LocalSocketIPC.Connection) r3
            java.util.concurrent.atomic.AtomicBoolean r0 = r3.ready
            boolean r0 = r0.get()
            r1 = 0
            if (r0 == 0) goto L23
            java.util.concurrent.atomic.AtomicBoolean r0 = r3.closed
            boolean r0 = r0.get()
            if (r0 != 0) goto L23
            int r0 = r3.peerPid
            r2 = 1
            if (r0 == r5) goto L24
            int r3 = r3.transport
            if (r3 != r2) goto L23
            if (r6 == 0) goto L23
            goto L24
        L23:
            r2 = r1
        L24:
            r4[r1] = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.LocalSocketIPC.lambda$isClientConnectedToProcess$16(com.android.support.IpcV2RuntimeState$Session, boolean[], int, boolean):void");
    }

    public String getDiagnostics() {
        String str;
        ServerGeneration serverGeneration = this.server;
        IpcV2RuntimeState.Session<Connection> sessionActiveSession = this.runtime.activeSession();
        if (sessionActiveSession == null) {
            str = "LISTENING";
        } else {
            str = sessionActiveSession.value().ready.get() ? "READY" : "SYNCING";
        }
        StringBuilder sbAppend = new StringBuilder("generation=").append(this.runtime.generation()).append(" state=");
        if (!this.runtime.isRunning()) {
            str = "STOPPED";
        }
        return sbAppend.append(str).append(" abstract=").append((serverGeneration == null || serverGeneration.localServer == null) ? false : true).append(" tcpPort=").append(serverGeneration != null ? serverGeneration.tcpPort : 0).append(" peer=").append(sessionActiveSession == null ? "none" : sessionActiveSession.value().description).append("\n").toString();
    }

    public void forceDisconnectStale() {
        forceDisconnectStale(SystemClock.elapsedRealtime());
    }

    void forceDisconnectStale(long j) {
        IpcV2RuntimeState.Session<Connection> sessionActiveSession = this.runtime.activeSession();
        if (sessionActiveSession == null) {
            return;
        }
        long j2 = sessionActiveSession.value().lastReceivedBootMs.get();
        if (j < j2 || j - j2 <= 30000) {
            return;
        }
        disconnectIfCurrent(sessionActiveSession, "stale reconnect");
    }

    private void disconnectIfCurrent(IpcV2RuntimeState.Session<Connection> session, final String str) {
        final Connection connectionValue = session.value();
        this.runtime.closeAndNotifyIfActive(session, new Runnable() { // from class: com.android.support.LocalSocketIPC$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1229lambda$disconnectIfCurrent$17$comandroidsupportLocalSocketIPC(str, connectionValue);
            }
        });
    }

    /* renamed from: lambda$disconnectIfCurrent$17$com-android-support-LocalSocketIPC, reason: not valid java name */
    /* synthetic */ void m1229lambda$disconnectIfCurrent$17$comandroidsupportLocalSocketIPC(String str, Connection connection) {
        runtimeVisible("disconnected reason=" + str + " ready=" + connection.ready.get() + " revisionInFlight=" + connection.configRevisionInFlight);
        if (connection.connectedNotified.get()) {
            notifyDisconnected();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeConnection(Connection connection) throws IOException {
        connection.close();
        this.pending.failSession(connection.sessionId, TerminalResult.failure("session_replaced"), TerminalResult.failure("peer_disconnected_after_write"));
    }

    private void notifyConnected(Connection connection) {
        if (connection.connectedNotified.compareAndSet(false, true)) {
            runtimeVisible("READY session=" + shortId(connection.sessionId) + " transport=" + transportName(connection.transport));
            Iterator<ConnectionListener> it = this.connectionListeners.snapshot().iterator();
            while (it.hasNext()) {
                try {
                    it.next().onClientConnected();
                } catch (RuntimeException unused) {
                }
            }
        }
    }

    private void notifyDisconnected() {
        Iterator<ConnectionListener> it = this.connectionListeners.snapshot().iterator();
        while (it.hasNext()) {
            try {
                it.next().onClientDisconnected();
            } catch (RuntimeException unused) {
            }
        }
    }

    private static void validateEnvelope(JSONObject jSONObject, IpcV2RuntimeState.Session<Connection> session, IpcV2Sequence ipcV2Sequence) throws Exception {
        if (jSONObject.optInt("v", -1) != 2) {
            throw new IOException("bad version");
        }
        if (!session.sessionId().equals(jSONObject.optString("sessionId", ""))) {
            throw new IOException("session mismatch");
        }
        if (!ipcV2Sequence.accept(jSONObject.optLong("seq", -1L))) {
            throw new IOException("sequence violation");
        }
        if (!validMethod(jSONObject.optString("method", ""))) {
            throw new IOException("invalid method");
        }
        IpcV2Protocol.decodeBase64Url(jSONObject.optString("id", null), 16);
        String strOptString = jSONObject.optString("type", "");
        if (!"state".equals(strOptString) && !"response".equals(strOptString) && !NotificationCompat.CATEGORY_EVENT.equals(strOptString) && !"ping".equals(strOptString) && !"pong".equals(strOptString) && !"error".equals(strOptString)) {
            throw new IOException("invalid message type");
        }
    }

    private static void writeHandshake(OutputStream outputStream, String str, String str2, JSONObject jSONObject) throws Exception {
        IpcV2FrameCodec.writeUtf8(outputStream, new JSONObject().put("v", 2).put("type", "request").put("id", str).put("seq", 0).put("method", str2).put("params", jSONObject).toString());
    }

    private static JSONObject readHandshake(InputStream inputStream) throws Exception {
        return new JSONObject(IpcV2FrameCodec.readUtf8(inputStream));
    }

    private static void requireHandshakeResponse(JSONObject jSONObject, String str, String str2) throws Exception {
        if (jSONObject.optInt("v", -1) != 2 || !"response".equals(jSONObject.optString("type", "")) || !str.equals(jSONObject.optString("id", "")) || jSONObject.optLong("seq", -1L) != 0 || !str2.equals(jSONObject.optString("method", ""))) {
            throw new IOException("invalid handshake response");
        }
    }

    private static JSONObject requireObject(JSONObject jSONObject, String str) throws Exception {
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(str);
        if (jSONObjectOptJSONObject != null) {
            return jSONObjectOptJSONObject;
        }
        throw new IOException(str + " must be an object");
    }

    private static boolean validMethod(String str) {
        return str != null && !str.isEmpty() && str.length() <= 96 && str.matches("[A-Za-z0-9_.]+$");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: isCurrent, reason: merged with bridge method [inline-methods] */
    public boolean m1230lambda$dispatchCandidate$3$comandroidsupportLocalSocketIPC(ServerGeneration serverGeneration) {
        return serverGeneration != null && serverGeneration == this.server && serverGeneration.running.get() && this.runtime.isRunning() && this.runtime.generation() == serverGeneration.generation;
    }

    private IpcV2TokenStore requireTokenStore() throws IOException {
        IpcV2TokenStore ipcV2TokenStore = this.tokenStore;
        if (ipcV2TokenStore != null) {
            return ipcV2TokenStore;
        }
        throw new IOException("IPC v2 token store is not configured");
    }

    private byte[] randomBytes(int i) {
        byte[] bArr = new byte[i];
        this.random.nextBytes(bArr);
        return bArr;
    }

    private static IpcV2OutboundQueue.OfferResult offerControl(Connection connection, OutboundFrame outboundFrame) {
        return connection.outbound.offerControl(outboundFrame);
    }

    private static void setReadTimeout(Closeable closeable, int i) throws IOException {
        if (closeable instanceof LocalSocket) {
            ((LocalSocket) closeable).setSoTimeout(i);
        } else if (closeable instanceof Socket) {
            ((Socket) closeable).setSoTimeout(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static JSONObject cloneJson(JSONObject jSONObject) {
        try {
            return jSONObject == null ? new JSONObject() : new JSONObject(jSONObject.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid JSON object", e);
        }
    }

    private static void logTxFrame(IpcV2RuntimeState.Session<Connection> session, Connection connection, OutboundFrame outboundFrame, long j) {
        if ("config.replace".equals(outboundFrame.method) || "response".equals(outboundFrame.type) || "error".equals(outboundFrame.type) || "ping".equals(outboundFrame.type)) {
            return;
        }
        "pong".equals(outboundFrame.type);
    }

    private static void logRxFrame(IpcV2RuntimeState.Session<Connection> session, JSONObject jSONObject) {
        String strOptString = jSONObject.optString("type", "");
        jSONObject.optString("method", "");
        if ("response".equals(strOptString) || "error".equals(strOptString) || "state".equals(strOptString) || NotificationCompat.CATEGORY_EVENT.equals(strOptString)) {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("result");
            if (jSONObjectOptJSONObject == null) {
                jSONObjectOptJSONObject = jSONObject.optJSONObject("error");
            }
            if (jSONObjectOptJSONObject == null) {
                jSONObject.optJSONObject("params");
            }
        }
    }

    private static String jsonKeys(JSONObject jSONObject) {
        if (jSONObject == null) {
            return "null";
        }
        JSONArray jSONArrayNames = jSONObject.names();
        return jSONArrayNames == null ? HttpUrl.PATH_SEGMENT_ENCODE_SET_URI : jSONArrayNames.toString();
    }

    private static String shortId(String str) {
        if (str == null) {
            return "null";
        }
        return str.length() <= 10 ? str : str.substring(0, 10);
    }

    private static String transportName(int i) {
        if (i == 0) {
            return "abstract";
        }
        if (i == 1) {
            return "tcp";
        }
        return "unknown(" + i + ")";
    }

    private static String safeMessage(Exception exc) {
        String message = exc.getMessage();
        return message == null ? exc.getClass().getSimpleName() : message;
    }

    private static void runtimeVisible(String str) {
        Dbg.i("JccRuntime", "ipc_v2 " + str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Thread daemonThread(Runnable runnable, String str) {
        Thread thread = new Thread(runnable, str + "-" + System.nanoTime());
        thread.setDaemon(true);
        return thread;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void safeClose(Closeable closeable) throws IOException {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception unused) {
        }
    }
}
