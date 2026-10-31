package com.android.support;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import androidx.core.os.EnvironmentCompat;
import com.android.support.GameHudSessionPolicy;
import com.android.support.GameLaunchGuard;
import com.android.support.HexQueryBroker;
import com.android.support.IpcActionSender;
import com.android.support.IpcBoardHeroAnchorsParser;
import com.android.support.IpcConfigSync;
import com.android.support.IpcGameRecommendParser;
import com.android.support.IpcHeroPoolParser;
import com.android.support.IpcHexLevelsParser;
import com.android.support.IpcMatchLifecycleParser;
import com.android.support.IpcMatchupEnvelopeParser;
import com.android.support.IpcMyOwnedHeroesParser;
import com.android.support.IpcPlayersParser;
import com.android.support.IpcPushDispatcher;
import com.android.support.IpcShopSlotState;
import com.android.support.IpcV2ActionCompletion;
import com.android.support.IpcV2OwnerRegistry;
import com.android.support.LatestOwnerListener;
import com.android.support.LocalSocketIPC;
import com.android.support.diagnostics.DiagnosticUiPublication;
import com.android.support.diagnostics.TraceCatalog;
import com.android.support.lineup.GameLineupBridge;
import com.android.support.trust.NativeTrustBridge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SharedMemManager {
    private static final long AVATAR_DIAG_INTERVAL_MS = 10000;
    private static final long HEX_QUERY_TIMEOUT_MS = 4000;
    static final long MATCHUP_SNAPSHOT_MAX_AGE_MS = 6500;
    private static final byte[] SENTINEL_DATA = new byte[0];
    private static final String TAG = "SharedMemManager";
    private static SharedMemManager instance;
    private final String TARGET_PACKAGE;
    private final IpcActionSender actionSender;
    private volatile AllInStatusListener allInStatusListener;
    private long avatarDiagLastMs;
    private boolean avatarDiagSucceeded;
    private volatile IpcAllInStatus cachedAllInStatus;
    private final IpcConfigSync configSync;
    private Context context;
    private volatile boolean cppConnectedFlag;
    private volatile CppConnectionCallback cppConnectionCallback;
    private final IpcV2OwnerRegistry<CppConnectionCallback> cppConnectionCallbacks;
    private final Object diagnosticUiLock;
    private final DiagnosticUiPublication[] diagnosticUiPublications;
    private volatile HexLevelsListener hexLevelsListener;
    private final HexQueryBroker hexQueryBroker;
    private volatile HudAppearance hudAppearance;
    private final IpcRuntime ipcRuntime;
    private volatile long lastSpecifiedHexSessionGeneration;
    private volatile long lastSpecifiedHexTargetRevision;
    private Handler mainHandler;
    private volatile Runnable myBoardHeroesListener;
    private volatile OpponentCardingStatusListener opponentCardingStatusListener;
    private volatile boolean peerSyncEnabled;
    private final IpcPushDispatcher pushDispatcher;
    private volatile RecastLivePoolCatalog recastLivePool;
    private volatile RecastLivePoolListener recastLivePoolListener;
    private volatile RecastRunStatus recastRunStatus;
    private volatile RecastRunStatusListener recastRunStatusListener;
    private volatile boolean retired;
    private volatile RuntimeHookStatus runtimeHookStatus;
    private volatile GameLaunchGuard.Status runtimeLaunchStatus;
    private final IpcShopSlotState shopSlotState;
    private final LatestOwnerListener<ShopSlotsCallback> shopSlotsCallbacks;
    private final GameSnapshotStore snapshot;
    private LocalSocketIPC socketIPC;
    private volatile SpecifiedHexStatus specifiedHexStatus;
    private volatile SpecifiedHexStatusListener specifiedHexStatusListener;
    private final SpecifiedPredictionPreview[] specifiedPredictionPreviews;
    private final SpecifiedPredictionPreviewListener[] specifiedPreviewListeners;
    private final long[] specifiedPreviewLogMs;
    private final long[] specifiedPreviewRevisions;
    private volatile SpecifiedShopStatus specifiedShopStatus;
    private volatile SpecifiedShopStatusListener specifiedShopStatusListener;
    private volatile int targetMask;
    private volatile int targetSelf;
    private volatile UiNotifier uiNotifier;

    public interface ActionCallback extends IpcV2ActionCompletion.TerminalCallback {
    }

    public interface AllInStatusListener {
        void onAllInStatus(boolean z, int i);
    }

    public interface CppConnectionCallback {
        void onCppConnected();

        void onCppDisconnected();
    }

    public interface HexLevelsCallback {
        void onResult(int[] iArr);
    }

    public interface HexLevelsListener {
        void onHexLevels(int[] iArr);
    }

    public interface OpponentCardingStatusListener {
        void onOpponentCardingStatus(OpponentCardingStatus opponentCardingStatus);
    }

    public interface RecastLivePoolListener {
        void onRecastLivePool(RecastLivePoolCatalog recastLivePoolCatalog);
    }

    public interface RecastRunStatusListener {
        void onRecastRunStatus(RecastRunStatus recastRunStatus);
    }

    public interface ShopSlotsCallback {
        void onShopSlotsUpdated(List<ShopSlotData> list);
    }

    public interface SpecifiedHexStatusListener {
        void onSpecifiedHexStatus(SpecifiedHexStatus specifiedHexStatus);
    }

    public interface SpecifiedPredictionPreviewListener {
        void onPreview(SpecifiedPredictionPreview specifiedPredictionPreview);
    }

    public interface SpecifiedShopStatusListener {
        void onSpecifiedShopStatus(SpecifiedShopStatus specifiedShopStatus);
    }

    private static int diagnosticUiSlot(int i) {
        if (i == 240) {
            return 0;
        }
        if (i == 250) {
            return 1;
        }
        if (i == 300) {
            return 2;
        }
        if (i == 310) {
            return 3;
        }
        return i == 320 ? 4 : -1;
    }

    private static int nextMatchupRevision(int i) {
        if (i == Integer.MAX_VALUE) {
            return 1;
        }
        return i + 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseGameRecommendHookDiagnostics(JSONObject jSONObject) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseGameRecommendUiDiagnostics(JSONObject jSONObject) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseOpponentDiagnostics(JSONObject jSONObject) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseShopAutomationDiagnostics(JSONObject jSONObject) {
    }

    private static long safeAgeMs(long j, long j2) {
        if (j2 <= 0 || j < j2) {
            return -1L;
        }
        return j - j2;
    }

    static boolean shouldResetSpecifiedHexSessionWatermark(boolean z, long j, long j2) {
        return z && j > 0 && j2 != j;
    }

    public void setSpecifiedMoveCombat(boolean z) {
    }

    public void setSpecifiedMovePlanning(boolean z) {
    }

    public void setSpecifiedShop(boolean z) {
    }

    public void setSpecifiedShopAffordOnly(boolean z) {
    }

    public void setSpecifiedShopCostMask(int i) {
    }

    public void setSpecifiedShopRefreshOnly(boolean z) {
    }

    public void setSpecifiedShopReserveGold(int i) {
    }

    public void setSpecifiedShopStrict(boolean z) {
    }

    public void setSpecifiedShopUseLineup(boolean z) {
    }

    public synchronized SpecifiedPredictionPreview getSpecifiedPredictionPreview(String str) {
        return this.specifiedPredictionPreviews[SpecifiedPredictionPreview.indexOf(str)];
    }

    public synchronized void setSpecifiedPredictionPreviewListener(String str, SpecifiedPredictionPreviewListener specifiedPredictionPreviewListener) {
        int iIndexOf = SpecifiedPredictionPreview.indexOf(str);
        this.specifiedPreviewListeners[iIndexOf] = specifiedPredictionPreviewListener;
        notifySpecifiedPredictionPreview(iIndexOf);
    }

    public synchronized boolean clearSpecifiedPredictionPreviewListener(String str, SpecifiedPredictionPreviewListener specifiedPredictionPreviewListener) {
        int iIndexOf = SpecifiedPredictionPreview.indexOf(str);
        SpecifiedPredictionPreviewListener[] specifiedPredictionPreviewListenerArr = this.specifiedPreviewListeners;
        if (specifiedPredictionPreviewListenerArr[iIndexOf] != specifiedPredictionPreviewListener) {
            return false;
        }
        specifiedPredictionPreviewListenerArr[iIndexOf] = null;
        return true;
    }

    private synchronized void notifySpecifiedPredictionPreview(final int i) {
        Handler handler;
        final SpecifiedPredictionPreviewListener specifiedPredictionPreviewListener = this.specifiedPreviewListeners[i];
        final SpecifiedPredictionPreview specifiedPredictionPreview = this.specifiedPredictionPreviews[i];
        if (specifiedPredictionPreviewListener != null && (handler = this.mainHandler) != null) {
            handler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda42
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1461x77e38e(i, specifiedPredictionPreviewListener, specifiedPredictionPreview);
                }
            });
        }
    }

    /* renamed from: lambda$notifySpecifiedPredictionPreview$0$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1461x77e38e(int i, SpecifiedPredictionPreviewListener specifiedPredictionPreviewListener, SpecifiedPredictionPreview specifiedPredictionPreview) {
        synchronized (this) {
            if (!this.retired && this.specifiedPreviewListeners[i] == specifiedPredictionPreviewListener && this.specifiedPredictionPreviews[i] == specifiedPredictionPreview) {
                specifiedPredictionPreviewListener.onPreview(specifiedPredictionPreview);
            }
        }
    }

    private synchronized void clearSpecifiedPredictionPreviews() {
        int i = 0;
        while (true) {
            SpecifiedPredictionPreview[] specifiedPredictionPreviewArr = this.specifiedPredictionPreviews;
            if (i < specifiedPredictionPreviewArr.length) {
                this.specifiedPreviewRevisions[i] = 0;
                specifiedPredictionPreviewArr[i] = SpecifiedPredictionPreview.empty(SpecifiedPredictionPreview.kindAt(i));
                notifySpecifiedPredictionPreview(i);
                i++;
            }
        }
    }

    private synchronized void parseSpecifiedPredictionPreview(String str, JSONObject jSONObject) {
        SpecifiedPredictionPreview specifiedPredictionPreview;
        int iIndexOf = SpecifiedPredictionPreview.indexOf(str);
        if (!jSONObject.has("sessionGeneration") || jSONObject.optLong("sessionGeneration", -1L) == this.lastSpecifiedHexSessionGeneration) {
            try {
                specifiedPredictionPreview = IpcSpecifiedPredictionPreviewParser.parse(str, jSONObject);
            } catch (Exception e) {
                this.specifiedPredictionPreviews[iIndexOf] = SpecifiedPredictionPreview.empty(str, "invalid", "预测数据已失效");
                notifySpecifiedPredictionPreview(iIndexOf);
                Log.e(TAG, "解析指定预测预览失败: " + str, e);
            }
            if (specifiedPredictionPreview.accepts(this.snapshot.currentMatchGeneration, this.lastSpecifiedHexSessionGeneration, this.specifiedPreviewRevisions[iIndexOf])) {
                this.specifiedPreviewRevisions[iIndexOf] = specifiedPredictionPreview.revision;
                this.specifiedPredictionPreviews[iIndexOf] = specifiedPredictionPreview;
                if (specifiedPredictionPreview.ready && !specifiedPredictionPreview.items.isEmpty()) {
                    long jElapsedRealtime = SystemClock.elapsedRealtime();
                    long[] jArr = this.specifiedPreviewLogMs;
                    if (jElapsedRealtime - jArr[iIndexOf] >= 1000) {
                        jArr[iIndexOf] = jElapsedRealtime;
                        Log.i(TAG, "preview kind=" + str + " ready=true items=" + specifiedPredictionPreview.items.size() + " state=" + specifiedPredictionPreview.state + " frame=" + specifiedPredictionPreview.frame);
                    }
                }
                notifySpecifiedPredictionPreview(iIndexOf);
            }
        }
    }

    public RecastRunStatus getRecastRunStatus() {
        return this.recastRunStatus;
    }

    public void setRecastRunStatusListener(final RecastRunStatusListener recastRunStatusListener) {
        this.recastRunStatusListener = recastRunStatusListener;
        final RecastRunStatus recastRunStatus = this.recastRunStatus;
        if (recastRunStatusListener == null || recastRunStatus == null || !recastRunStatus.isRunning()) {
            return;
        }
        this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1468x5262d102(recastRunStatusListener, recastRunStatus);
            }
        });
    }

    /* renamed from: lambda$setRecastRunStatusListener$1$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1468x5262d102(RecastRunStatusListener recastRunStatusListener, RecastRunStatus recastRunStatus) {
        if (this.recastRunStatusListener == recastRunStatusListener && this.recastRunStatus == recastRunStatus) {
            recastRunStatusListener.onRecastRunStatus(recastRunStatus);
        }
    }

    RecastLivePoolCatalog getRecastLivePool() {
        return this.recastLivePool;
    }

    void setRecastLivePoolListener(final RecastLivePoolListener recastLivePoolListener) {
        this.recastLivePoolListener = recastLivePoolListener;
        final RecastLivePoolCatalog recastLivePoolCatalog = this.recastLivePool;
        if (recastLivePoolListener == null || recastLivePoolCatalog == null) {
            return;
        }
        this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1467x5a8d17a8(recastLivePoolListener, recastLivePoolCatalog);
            }
        });
    }

    /* renamed from: lambda$setRecastLivePoolListener$2$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1467x5a8d17a8(RecastLivePoolListener recastLivePoolListener, RecastLivePoolCatalog recastLivePoolCatalog) {
        if (this.recastLivePoolListener == recastLivePoolListener && this.recastLivePool == recastLivePoolCatalog) {
            recastLivePoolListener.onRecastLivePool(recastLivePoolCatalog);
        }
    }

    public LatestOwnerListener.Token registerShopSlotsCallback(Object obj, ShopSlotsCallback shopSlotsCallback) {
        return this.shopSlotsCallbacks.register(obj, shopSlotsCallback);
    }

    public boolean unregisterShopSlotsCallback(LatestOwnerListener.Token token) {
        return this.shopSlotsCallbacks.unregister(token);
    }

    public List<ShopSlotData> getShopSlots() {
        return this.shopSlotState.current();
    }

    public void setUiNotifier(UiNotifier uiNotifier) {
        this.uiNotifier = uiNotifier;
    }

    static void dispatchNativeToast(UiNotifier uiNotifier, Context context, String str) {
        if (str == null) {
            return;
        }
        String strTrim = str.trim();
        if (strTrim.isEmpty() || TersafePatchNotice.isRetiredMessage(strTrim)) {
            return;
        }
        if (uiNotifier != null) {
            uiNotifier.showToast(context, strTrim, 0);
        } else {
            ToastRateLimiter.show(context, strTrim, 0);
        }
    }

    private SharedMemManager(Context context) {
        String str = GameRegionState.current().packageName;
        this.TARGET_PACKAGE = str;
        this.runtimeHookStatus = RuntimeHookStatus.UNKNOWN;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.cppConnectionCallbacks = new IpcV2OwnerRegistry<>();
        this.snapshot = new GameSnapshotStore();
        this.shopSlotState = new IpcShopSlotState();
        this.diagnosticUiLock = new Object();
        this.diagnosticUiPublications = new DiagnosticUiPublication[5];
        this.specifiedPredictionPreviews = new SpecifiedPredictionPreview[]{SpecifiedPredictionPreview.empty(SpecifiedPredictionPreview.KIND_SHOP), SpecifiedPredictionPreview.empty(SpecifiedPredictionPreview.KIND_RECAST), SpecifiedPredictionPreview.empty(SpecifiedPredictionPreview.KIND_HEX)};
        this.specifiedPreviewRevisions = new long[3];
        this.specifiedPreviewLogMs = new long[3];
        this.specifiedPreviewListeners = new SpecifiedPredictionPreviewListener[3];
        this.cachedAllInStatus = IpcAllInStatus.INACTIVE;
        this.targetMask = 0;
        this.targetSelf = 0;
        this.cppConnectedFlag = false;
        this.specifiedHexStatus = SpecifiedHexStatus.IDLE;
        this.recastRunStatus = RecastRunStatus.IDLE;
        this.shopSlotsCallbacks = new LatestOwnerListener<>();
        this.peerSyncEnabled = false;
        this.hudAppearance = HudAppearance.defaults();
        this.context = GameRegionContext.pin(context, GameRegionState.current());
        this.socketIPC = LocalSocketIPC.getInstance();
        this.ipcRuntime = new IpcRuntime(this.socketIPC, this);
        LocalSocketIPC localSocketIPC = this.socketIPC;
        IpcActionSender.MatchGenerationSource matchGenerationSource = new IpcActionSender.MatchGenerationSource() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda28
            @Override // com.android.support.IpcActionSender.MatchGenerationSource
            public final String currentMatchGeneration() {
                return this.f$0.m1455lambda$new$3$comandroidsupportSharedMemManager();
            }
        };
        IpcActionSender.Clock clock = new IpcActionSender.Clock() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda29
            @Override // com.android.support.IpcActionSender.Clock
            public final long elapsedRealtime() {
                return SystemClock.elapsedRealtime();
            }
        };
        final Handler handler = this.mainHandler;
        Objects.requireNonNull(handler);
        this.actionSender = new IpcActionSender(localSocketIPC, matchGenerationSource, clock, new IpcV2ActionCompletion.Dispatcher() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda30
            @Override // com.android.support.IpcV2ActionCompletion.Dispatcher
            public final void post(Runnable runnable) {
                handler.post(runnable);
            }
        }, new IpcActionSender.WarningSink() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda31
            @Override // com.android.support.IpcActionSender.WarningSink
            public final void warn(String str2, Throwable th) {
                SharedMemManager.lambda$new$4(str2, th);
            }
        });
        Context context2 = this.context;
        final LocalSocketIPC localSocketIPC2 = this.socketIPC;
        Objects.requireNonNull(localSocketIPC2);
        this.configSync = new IpcConfigSync(context2, new IpcConfigSync.Transport() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda32
            @Override // com.android.support.IpcConfigSync.Transport
            public final boolean requestConfigSync() {
                return localSocketIPC2.requestConfigSync();
            }
        });
        HexQueryBroker.Scheduler scheduler = new HexQueryBroker.Scheduler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda34
            @Override // com.android.support.HexQueryBroker.Scheduler
            public final HexQueryBroker.Cancellable schedule(Runnable runnable, long j) {
                return this.f$0.m1457lambda$new$6$comandroidsupportSharedMemManager(runnable, j);
            }
        };
        HexQueryBroker.Sender sender = new HexQueryBroker.Sender() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda35
            @Override // com.android.support.HexQueryBroker.Sender
            public final void sendQuery() {
                this.f$0.m1458lambda$new$7$comandroidsupportSharedMemManager();
            }
        };
        final Handler handler2 = this.mainHandler;
        Objects.requireNonNull(handler2);
        this.hexQueryBroker = new HexQueryBroker(scheduler, sender, new HexQueryBroker.Delivery() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda36
            @Override // com.android.support.HexQueryBroker.Delivery
            public final void deliver(Runnable runnable) {
                handler2.post(runnable);
            }
        }, HEX_QUERY_TIMEOUT_MS);
        this.pushDispatcher = createPushDispatcher();
        try {
            try {
                this.socketIPC.configure(this.context.getDir("ipc_v2", 0), this.context.getPackageManager().getApplicationInfo(str, 0).uid);
            } catch (Exception e) {
                Log.e(TAG, "IPC v2 token store initialization failed", e);
            }
        } catch (PackageManager.NameNotFoundException unused) {
            this.socketIPC.configure(this.context.getDir("ipc_v2", 0), -1);
        } catch (Exception e2) {
            Log.e(TAG, "IPC v2 initialization failed", e2);
        }
        LocalSocketIPC localSocketIPC3 = this.socketIPC;
        final IpcConfigSync ipcConfigSync = this.configSync;
        Objects.requireNonNull(ipcConfigSync);
        localSocketIPC3.setConfigSnapshotProvider(new LocalSocketIPC.ConfigSnapshotProvider() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda37
            @Override // com.android.support.LocalSocketIPC.ConfigSnapshotProvider
            public final LocalSocketIPC.ConfigSnapshot latestSnapshot() {
                return ipcConfigSync.buildConfigSnapshot();
            }
        });
        this.socketIPC.setHudStyleSnapshotProvider(new LocalSocketIPC.HudStyleSnapshotProvider() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda38
            @Override // com.android.support.LocalSocketIPC.HudStyleSnapshotProvider
            public final LocalSocketIPC.HudStyleSnapshot latestSnapshot() {
                return this.f$0.buildHudStyleSnapshot();
            }
        });
    }

    /* renamed from: lambda$new$3$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ String m1455lambda$new$3$comandroidsupportSharedMemManager() {
        return this.snapshot.currentMatchGeneration;
    }

    static /* synthetic */ void lambda$new$4(String str, Throwable th) {
        if (th == null) {
            Log.w(TAG, str);
        } else {
            Log.w(TAG, str, th);
        }
    }

    /* renamed from: lambda$new$6$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ HexQueryBroker.Cancellable m1457lambda$new$6$comandroidsupportSharedMemManager(final Runnable runnable, long j) {
        this.mainHandler.postDelayed(runnable, j);
        return new HexQueryBroker.Cancellable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda27
            @Override // com.android.support.HexQueryBroker.Cancellable
            public final void cancel() {
                this.f$0.m1456lambda$new$5$comandroidsupportSharedMemManager(runnable);
            }
        };
    }

    /* renamed from: lambda$new$5$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1456lambda$new$5$comandroidsupportSharedMemManager(Runnable runnable) {
        this.mainHandler.removeCallbacks(runnable);
    }

    /* renamed from: lambda$new$7$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1458lambda$new$7$comandroidsupportSharedMemManager() {
        sendAction(IpcV2ActionMethods.HEX_QUERY, new JSONObject(), null);
    }

    static synchronized void resetForGameRegion() {
        SharedMemManager sharedMemManager = instance;
        if (sharedMemManager != null) {
            sharedMemManager.retired = true;
            instance.release();
        }
        instance = null;
    }

    public static synchronized SharedMemManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedMemManager(context);
        }
        return instance;
    }

    public static synchronized SharedMemManager peekInstance() {
        return instance;
    }

    public synchronized boolean init() {
        if (this.retired || !GameRegionState.current().isRuntimeReady()) {
            return false;
        }
        try {
            Dbg.i(TAG, "初始化 JSON IPC...");
            if (this.ipcRuntime.start(new LocalSocketIPC.ConnectionListener() { // from class: com.android.support.SharedMemManager.1
                @Override // com.android.support.LocalSocketIPC.ConnectionListener
                public void onClientConnected() {
                    SharedMemManager.this.runtimeHookStatus = RuntimeHookStatus.UNKNOWN;
                    SharedMemManager.this.runtimeLaunchStatus = null;
                    Dbg.i(SharedMemManager.TAG, "C++ 端已连接 (JSON IPC)");
                    SharedMemManager.this.cppConnectedFlag = true;
                    SharedMemManager.this.configSync.requestSync();
                    NativeTrustBridge.getInstance().relayCurrentLease();
                    SharedMemManager.this.notifyCppConnectionChanged(true);
                }

                @Override // com.android.support.LocalSocketIPC.ConnectionListener
                public void onClientDisconnected() {
                    SharedMemManager.this.runtimeHookStatus = RuntimeHookStatus.UNKNOWN;
                    SharedMemManager.this.runtimeLaunchStatus = null;
                    Log.w(SharedMemManager.TAG, "C++ 端已断开 (JSON IPC)");
                    SharedMemManager.this.cppConnectedFlag = false;
                    SharedMemManager.this.snapshot.currentMatchGeneration = "";
                    SharedMemManager.this.snapshot.currentMatchStartedAtMs = 0L;
                    SharedMemManager.this.clearGameSnapshotCachesForSessionBoundary();
                    SharedMemManager.this.notifyCppConnectionChanged(false);
                }
            }, new LocalSocketIPC.MessageListener() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda0
                @Override // com.android.support.LocalSocketIPC.MessageListener
                public final void onMessage(String str, JSONObject jSONObject) {
                    this.f$0.handleCppMessage(str, jSONObject);
                }
            })) {
                Dbg.i(TAG, "JSON IPC 服务器已启动: @" + LocalSocketIPC.SOCKET_NAME);
                return true;
            }
            Log.e(TAG, "JSON IPC 服务器启动失败");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "初始化失败", e);
            return false;
        }
    }

    public void setCppConnectionCallback(CppConnectionCallback cppConnectionCallback) {
        this.cppConnectionCallback = cppConnectionCallback;
    }

    public IpcV2OwnerRegistry.Token registerCppConnectionCallback(Object obj, CppConnectionCallback cppConnectionCallback) {
        return this.cppConnectionCallbacks.register(obj, cppConnectionCallback);
    }

    public void unregisterCppConnectionCallback(IpcV2OwnerRegistry.Token token) {
        this.cppConnectionCallbacks.unregister(token);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCppConnectionChanged(final boolean z) {
        final CppConnectionCallback cppConnectionCallback = this.cppConnectionCallback;
        final List<CppConnectionCallback> listSnapshot = this.cppConnectionCallbacks.snapshot();
        this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1459xa36c169c(cppConnectionCallback, z, listSnapshot);
            }
        });
    }

    /* renamed from: lambda$notifyCppConnectionChanged$8$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1459xa36c169c(CppConnectionCallback cppConnectionCallback, boolean z, List list) {
        Set setNewSetFromMap = Collections.newSetFromMap(new IdentityHashMap());
        if (cppConnectionCallback != null) {
            setNewSetFromMap.add(cppConnectionCallback);
            dispatchCppConnectionCallback(cppConnectionCallback, z);
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            CppConnectionCallback cppConnectionCallback2 = (CppConnectionCallback) it.next();
            if (cppConnectionCallback2 != cppConnectionCallback && setNewSetFromMap.add(cppConnectionCallback2)) {
                dispatchCppConnectionCallback(cppConnectionCallback2, z);
            }
        }
    }

    private void dispatchCppConnectionCallback(CppConnectionCallback cppConnectionCallback, boolean z) {
        try {
            if (z) {
                cppConnectionCallback.onCppConnected();
            } else {
                cppConnectionCallback.onCppDisconnected();
            }
        } catch (RuntimeException e) {
            Log.w(TAG, "C++ connection callback failed", e);
        }
    }

    public void resyncToClient() {
        this.configSync.requestSync();
    }

    public String getSharedFilePath() {
        return "JSON-IPC:@" + LocalSocketIPC.SOCKET_NAME;
    }

    public void setAutoPickCard(boolean z) {
        this.configSync.configAutoPick = z;
        this.configSync.sendConfig(IpcV2ConfigState.AUTO_PICK, z);
        Dbg.i(TAG, "设置自动拿牌: " + z);
    }

    public boolean getAutoPickCard() {
        return this.configSync.configAutoPick;
    }

    public int[] appliedLineupHeroIds() {
        Object obj = this.configSync.state.snapshot().values().get(IpcV2ConfigState.LINEUP_APPLIED_HERO_IDS);
        return obj instanceof int[] ? (int[]) ((int[]) obj).clone() : new int[0];
    }

    public void setOpponentCardingConfig(OpponentCardingConfig opponentCardingConfig) {
        if (opponentCardingConfig == null) {
            opponentCardingConfig = OpponentCardingConfig.defaults();
        }
        this.configSync.configOpponentCarding = opponentCardingConfig;
        this.configSync.state.setOpponentCardingConfig(opponentCardingConfig);
        this.configSync.requestSync();
        if (!opponentCardingConfig.isEnabled()) {
            updateOpponentCardingStatus(OpponentCardingStatus.disabled());
        }
        Dbg.i(TAG, "设置智能卡牌: " + opponentCardingConfig.toSummary() + ", enabled=" + opponentCardingConfig.isEnabled());
    }

    public OpponentCardingConfig getOpponentCardingConfig() {
        return this.configSync.configOpponentCarding;
    }

    public void setOpponentCardingStatusListener(final OpponentCardingStatusListener opponentCardingStatusListener) {
        this.opponentCardingStatusListener = opponentCardingStatusListener;
        if (opponentCardingStatusListener != null) {
            final OpponentCardingStatus opponentCardingStatus = this.snapshot.cachedOpponentCardingStatus;
            this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1466xb00f123e(opponentCardingStatusListener, opponentCardingStatus);
                }
            });
        }
    }

    /* renamed from: lambda$setOpponentCardingStatusListener$9$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1466xb00f123e(OpponentCardingStatusListener opponentCardingStatusListener, OpponentCardingStatus opponentCardingStatus) {
        if (this.opponentCardingStatusListener == opponentCardingStatusListener && this.snapshot.cachedOpponentCardingStatus == opponentCardingStatus) {
            opponentCardingStatusListener.onOpponentCardingStatus(opponentCardingStatus);
        }
    }

    public OpponentCardingStatus getOpponentCardingStatus() {
        return this.snapshot.cachedOpponentCardingStatus;
    }

    public void setAllInStatusListener(final AllInStatusListener allInStatusListener) {
        this.allInStatusListener = allInStatusListener;
        if (allInStatusListener != null) {
            final IpcAllInStatus ipcAllInStatus = this.cachedAllInStatus;
            this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1465x6a50ddad(allInStatusListener, ipcAllInStatus);
                }
            });
        }
    }

    /* renamed from: lambda$setAllInStatusListener$10$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1465x6a50ddad(AllInStatusListener allInStatusListener, IpcAllInStatus ipcAllInStatus) {
        if (this.allInStatusListener == allInStatusListener && this.cachedAllInStatus == ipcAllInStatus) {
            allInStatusListener.onAllInStatus(ipcAllInStatus.active, ipcAllInStatus.reason);
        }
    }

    public void setAutoPick5CostChosen(boolean z) {
        this.configSync.configAutoPick5CostChosen = z;
        this.configSync.sendConfig(IpcV2ConfigState.AUTO_PICK_5_COST_CHOSEN, z);
        Dbg.i(TAG, "设置自动拿五费天选: " + z);
    }

    public boolean getAutoPick5CostChosen() {
        return this.configSync.configAutoPick5CostChosen;
    }

    public void setChase3StarThreshold(int i) {
        this.configSync.configChase3StarMode = i;
        this.configSync.sendConfig(IpcV2ConfigState.CHASE_3_STAR_THRESHOLD, i);
        Dbg.i(TAG, "设置追3星拿牌阈值: " + i);
    }

    public void setAutoPickCostMask(int i) {
        this.configSync.configAutoPickCostMask = i & 31;
        IpcConfigSync ipcConfigSync = this.configSync;
        ipcConfigSync.sendConfig(IpcV2ConfigState.AUTO_PICK_COST_MASK, ipcConfigSync.configAutoPickCostMask);
        Dbg.i(TAG, "设置拿牌费用筛选掩码: " + this.configSync.configAutoPickCostMask);
    }

    public int getAutoPickCostMask() {
        return this.configSync.configAutoPickCostMask;
    }

    public int getChase3StarThreshold() {
        return this.configSync.configChase3StarMode;
    }

    public void setChase3StarChosenThreshold(int i) {
        this.configSync.configChase3StarChosenThreshold = i;
        this.configSync.sendConfig(IpcV2ConfigState.CHASE_3_STAR_CHOSEN_THRESHOLD, i);
        Dbg.i(TAG, "设置追3星天选阈值: " + i);
    }

    public int getChase3StarChosenThreshold() {
        return this.configSync.configChase3StarChosenThreshold;
    }

    public void setAutoPickAllChosen(boolean z) {
        this.configSync.configAutoPickAllChosen = z;
        this.configSync.sendConfig(IpcV2ConfigState.AUTO_PICK_ALL_CHOSEN, z);
        Dbg.i(TAG, "设置天选全费用: " + z);
    }

    public boolean getAutoPickAllChosen() {
        return this.configSync.configAutoPickAllChosen;
    }

    public void setAutoPlayCard(boolean z) {
        this.configSync.configAutoPlay = z;
        this.configSync.sendConfig(IpcV2ConfigState.AUTO_PLAY, z);
        Dbg.i(TAG, "设置自动出牌: " + z);
    }

    public boolean getAutoPlayCard() {
        return this.configSync.configAutoPlay;
    }

    public void setGameSpeed(int i) {
        this.configSync.configGameSpeed = i;
        Dbg.i(TAG, "设置游戏速度: " + i + "x");
    }

    public int getGameSpeed() {
        return this.configSync.configGameSpeed;
    }

    public void setShowDebugWindow(boolean z) {
        this.configSync.configShowDebug = z;
        this.configSync.sendConfig(IpcV2ConfigState.SHOW_DEBUG_WINDOW, z);
        Dbg.i(TAG, "设置调试窗口显示: " + z);
    }

    public boolean getShowDebugWindow() {
        return this.configSync.configShowDebug;
    }

    public void sendUiSurfaceMasks(UiSurfacePolicy uiSurfacePolicy) {
        if (uiSurfacePolicy == null) {
            return;
        }
        try {
            this.configSync.state.setUiSurfaceMasks(uiSurfacePolicy.forceShowMask(), uiSurfacePolicy.forceHideMask(), uiSurfacePolicy.forceEnableMask(), uiSurfacePolicy.forceDisableMask());
            this.configSync.requestSync();
        } catch (Exception e) {
            Log.e(TAG, "发送界面覆盖失败", e);
        }
        Dbg.i(TAG, "下发界面覆盖 revision=" + uiSurfacePolicy.revision);
    }

    public void setSessionActive(boolean z) {
        this.configSync.configSessionActive = z;
        this.configSync.sendConfig(IpcV2ConfigState.SESSION_ACTIVE, z);
        Dbg.i(TAG, "设置会话主开关: ".concat(z ? "ON(已激活)" : "OFF(未激活)"));
    }

    public boolean isSessionActive() {
        return this.configSync.configSessionActive;
    }

    public void setBlockDialogs(boolean z) {
        this.configSync.configBlockDialogs = z;
        this.configSync.sendConfig(IpcV2ConfigState.BLOCK_DIALOGS, z);
        Dbg.i(TAG, "设置拦截弹窗: " + z);
    }

    public boolean getBlockDialogs() {
        return this.configSync.configBlockDialogs;
    }

    public void setBoardHeroHeadOverlayEnabled(boolean z) {
        this.configSync.configBoardHeroHeadOverlayEnabled = z;
        this.configSync.sendConfig(IpcV2ConfigState.BOARD_HERO_HEAD_OVERLAY, z);
        if (z) {
            return;
        }
        clearBoardHeroAnchorCache();
    }

    public void setOpponentBoardPerspectiveEnabled(boolean z) {
        this.configSync.configOpponentBoardPerspectiveEnabled = z;
        this.configSync.sendConfig(IpcV2ConfigState.OPPONENT_BOARD_PERSPECTIVE, z);
    }

    public void setBoardEntityPreviewEnabled(boolean z) {
        this.configSync.configBoardEntityPreviewEnabled = z;
        this.configSync.sendConfig(IpcV2ConfigState.BOARD_ENTITY_PREVIEW, z);
    }

    public void setNativePlayerEconNameEnabled(boolean z) {
        this.configSync.configNativePlayerEconName = z;
        this.configSync.sendConfig(IpcV2ConfigState.NATIVE_PLAYER_ECON_NAME, z);
    }

    public void setShopNativeLabelEnabled(boolean z) {
        this.configSync.configShopNativeLabel = z;
        this.configSync.sendConfig(IpcV2ConfigState.SHOP_NATIVE_LABEL, z);
    }

    public synchronized void setHudAppearance(HudAppearance hudAppearance) {
        this.hudAppearance = hudAppearance == null ? HudAppearance.defaults() : hudAppearance.copy();
        long j = this.configSync.hudStyleRevision;
        IpcConfigSync ipcConfigSync = this.configSync;
        if (j == Long.MAX_VALUE) {
            ipcConfigSync.hudStyleRevision = 1L;
        } else {
            ipcConfigSync.hudStyleRevision++;
        }
        this.socketIPC.requestHudStyleSync();
    }

    public void setOpponentPredictStyle(int i) {
        if (i < 0 || i > 2) {
            i = 0;
        }
        this.configSync.configOpponentPredictStyle = i;
        this.configSync.sendConfig(IpcV2ConfigState.OPPONENT_PREDICT_STYLE, i);
    }

    public int getOpponentPredictStyle() {
        return this.configSync.configOpponentPredictStyle;
    }

    public void setNativeAttackIconEnabled(boolean z) {
        this.configSync.configNativeAttackIconEnabled = z;
        this.configSync.sendConfig(IpcV2ConfigState.NATIVE_ATTACK_ICON_ENABLED, z);
    }

    public void setNativeAttackIconAppearance(int i, int i2, int i3) {
        this.configSync.configNativeAttackIconOffsetX = Math.max(-100, Math.min(100, i));
        this.configSync.configNativeAttackIconOffsetY = Math.max(-100, Math.min(100, i2));
        this.configSync.configNativeAttackIconSizePercent = Math.max(50, Math.min(TraceCatalog.Feature.AUTO_PICK, i3));
        try {
            this.configSync.state.setNativeAttackIconAppearance(this.configSync.configNativeAttackIconOffsetX, this.configSync.configNativeAttackIconOffsetY, this.configSync.configNativeAttackIconSizePercent);
            this.configSync.requestSync();
        } catch (Exception e) {
            Log.e(TAG, "发送内置版攻击标识外观失败", e);
        }
    }

    public void setIOSPlatformFake(boolean z) {
        this.configSync.configIosFake = z;
        this.configSync.sendConfig(IpcV2ConfigState.IOS_FAKE, z);
        Dbg.i(TAG, "设置iOS转区: " + z);
    }

    public boolean getIOSPlatformFake() {
        return this.configSync.configIosFake;
    }

    public void writeModifyTargets(Set<Integer> set) {
        int i = 0;
        if (set == null || set.isEmpty() || (set.size() == 1 && set.contains(-1))) {
            this.targetMask = 0;
            this.targetSelf = 0;
            return;
        }
        boolean zContains = set.contains(-1);
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            if (iIntValue >= 0 && iIntValue < 8) {
                i |= 1 << iIntValue;
            }
        }
        this.targetMask = i;
        this.targetSelf = zContains ? 1 : 0;
    }

    public void modifyGold(int i, Runnable runnable) {
        try {
            sendAction(IpcV2ActionMethods.GAME_MODIFY_GOLD, IpcTargetedActionPayloads.scalar("value", i, this.targetMask, this.targetSelf), runnable);
            Dbg.i(TAG, "触发修改金币: " + i);
        } catch (Exception e) {
            Log.e(TAG, "修改金币失败: " + e.getMessage());
        }
    }

    public void modifyHP(int i, Runnable runnable) {
        try {
            sendAction(IpcV2ActionMethods.GAME_MODIFY_HP, IpcTargetedActionPayloads.scalar("value", i, this.targetMask, this.targetSelf), runnable);
            Dbg.i(TAG, "触发修改血量: " + i);
        } catch (Exception e) {
            Log.e(TAG, "修改血量失败: " + e.getMessage());
        }
    }

    public void modifyLevel(int i, Runnable runnable) {
        try {
            sendAction(IpcV2ActionMethods.GAME_MODIFY_LEVEL, IpcTargetedActionPayloads.scalar("value", i, this.targetMask, this.targetSelf), runnable);
            Dbg.i(TAG, "触发修改等级(经验): " + i);
        } catch (Exception e) {
            Log.e(TAG, "修改等级失败: " + e.getMessage());
        }
    }

    public void triggerGenerateDropHero(int[] iArr, int i, Runnable runnable) {
        if (iArr == null || iArr.length == 0) {
            Log.w(TAG, "英雄ID列表为空");
            return;
        }
        try {
            sendAction(IpcV2ActionMethods.DROP_GENERATE_HEROES, IpcTargetedActionPayloads.heroDrop(iArr, i, this.targetMask, this.targetSelf), runnable);
            Dbg.i(TAG, "触发生成英雄掉落: " + iArr.length + "个, 星级:" + i);
        } catch (Exception e) {
            Log.e(TAG, "触发生成英雄掉落失败: " + e.getMessage());
        }
    }

    public void triggerGenerateDropEquipment(int[] iArr, Runnable runnable) {
        if (iArr == null || iArr.length == 0) {
            Log.w(TAG, "装备ID列表为空");
            return;
        }
        try {
            sendAction(IpcV2ActionMethods.DROP_GENERATE_EQUIPMENT, IpcTargetedActionPayloads.equipmentDrop(iArr, this.targetMask, this.targetSelf), runnable);
            Dbg.i(TAG, "触发生成装备掉落: " + iArr.length + "个");
        } catch (Exception e) {
            Log.e(TAG, "触发生成装备掉落失败: " + e.getMessage());
        }
    }

    public void triggerSellHeroes(int i, Runnable runnable) {
        triggerSellHeroes(i, 31, runnable);
    }

    public void triggerSellHeroes(int i, int i2, Runnable runnable) {
        triggerSellHeroes(i, i2, runnable, null);
    }

    public void triggerSellHeroes(int i, int i2, Runnable runnable, ActionCallback actionCallback) {
        triggerSellHeroes(i, i2, null, false, runnable, actionCallback);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r16v0 */
    /* JADX WARN: Type inference failed for: r16v1 */
    /* JADX WARN: Type inference failed for: r16v5 */
    /* JADX WARN: Type inference failed for: r17v0, types: [com.android.support.SharedMemManager] */
    public void triggerSellHeroes(final int i, int i2, int[] iArr, boolean z, final Runnable runnable, final ActionCallback actionCallback) {
        ?? r16;
        final int i3;
        final boolean z2;
        boolean z3;
        boolean z4 = true;
        if (i < 1 || i > 3) {
            Log.w(TAG, "无效的卖英雄模式: " + i);
            dispatchActionCallback(actionCallback, false, "invalid_sell_mode");
            return;
        }
        int i4 = i2 & 31;
        if (i4 == 0) {
            Log.w(TAG, "未选择允许卖出的费用，取消卖英雄");
            dispatchActionCallback(actionCallback, false, "empty_sell_cost_mask");
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("mode", i);
            jSONObject.put("costMask", i4);
            JSONArray jSONArray = new JSONArray();
            if (iArr != null) {
                int length = iArr.length;
                int i5 = 0;
                while (i5 < length) {
                    int i6 = iArr[i5];
                    if (i6 <= 0) {
                        z3 = z4;
                    } else {
                        z3 = z4;
                        if (jSONArray.length() >= 64) {
                            r16 = z3;
                            break;
                        }
                        jSONArray.put(i6);
                    }
                    i5++;
                    z4 = z3;
                }
                r16 = z4;
            } else {
                r16 = z4;
            }
            jSONObject.put("keepHeroIds", jSONArray);
            jSONObject.put("keepCardingTargets", z ? 1 : 0);
            if (jSONArray.length() > 0 || z) {
                i3 = i4;
                z2 = r16;
            } else {
                i3 = i4;
                z2 = false;
            }
            sendActionWithTerminal(IpcV2ActionMethods.CHESS_SELL, jSONObject, new ActionCallback() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda43
                @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
                public final void onTerminal(boolean z5, String str) throws JSONException {
                    this.f$0.m1469lambda$triggerSellHeroes$12$comandroidsupportSharedMemManager(i3, z2, i, runnable, actionCallback, z5, str);
                }
            });
            String[] strArr = new String[4];
            strArr[0] = "";
            strArr[r16] = "备战席";
            strArr[2] = "场上";
            strArr[3] = "全部";
            Dbg.i(TAG, "触发一键卖英雄: " + strArr[i] + " costMask=" + i3);
        } catch (Exception e) {
            Log.e(TAG, "触发卖英雄失败: " + e.getMessage());
            dispatchActionCallback(actionCallback, false, "invalid_action_params");
        }
    }

    /* renamed from: lambda$triggerSellHeroes$12$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1469lambda$triggerSellHeroes$12$comandroidsupportSharedMemManager(int i, boolean z, int i2, final Runnable runnable, final ActionCallback actionCallback, boolean z2, String str) throws JSONException {
        if (!z2 && i == 31 && !z && ("invalid_request".equals(str) || "unknown_method".equals(str))) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("mode", i2);
                Log.w(TAG, "chess.sell payload rejected; retrying legacy full-cost payload");
                sendActionWithTerminal(IpcV2ActionMethods.CHESS_SELL, jSONObject, new ActionCallback() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda44
                    @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
                    public final void onTerminal(boolean z3, String str2) {
                        SharedMemManager.lambda$triggerSellHeroes$11(runnable, actionCallback, z3, str2);
                    }
                });
                return;
            } catch (Exception e) {
                Log.w(TAG, "legacy chess.sell retry could not be encoded", e);
            }
        }
        if (z2 && runnable != null) {
            runnable.run();
        }
        if (actionCallback != null) {
            actionCallback.onTerminal(z2, str);
        }
    }

    static /* synthetic */ void lambda$triggerSellHeroes$11(Runnable runnable, ActionCallback actionCallback, boolean z, String str) {
        if (z && runnable != null) {
            runnable.run();
        }
        if (actionCallback != null) {
            actionCallback.onTerminal(z, str);
        }
    }

    public void triggerMirrorBoard(int i, Runnable runnable) throws JSONException {
        if (i < 0 || i > 2) {
            i = 0;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("axis", i);
            sendAction(IpcV2ActionMethods.CHESS_MIRROR, jSONObject, runnable);
            Dbg.i(TAG, "触发一键镜像换位: " + new String[]{"水平", "垂直", "中心对称"}[i]);
        } catch (Exception e) {
            Log.e(TAG, "触发镜像换位失败: " + e.getMessage());
        }
    }

    public void triggerCustomSwap(JSONArray jSONArray, Runnable runnable) {
        if (jSONArray == null || jSONArray.length() == 0) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("pairs", jSONArray);
            sendAction(IpcV2ActionMethods.CHESS_CUSTOM_SWAP, jSONObject, runnable);
            Dbg.i(TAG, "触发自定义换位: " + jSONArray.length() + " 对");
        } catch (Exception e) {
            Log.e(TAG, "触发自定义换位失败: " + e.getMessage());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void startAllIn(int i, int i2, int i3, boolean z, int i4, int i5, int[] iArr, ActionCallback actionCallback) {
        Object[] objArr = i5 == 16 || i5 == 8 || i5 == 24;
        if (i < 1 || i > 9 || i2 < 0 || i2 > 50 || i3 < 0 || i3 > 1 || i4 < 2000 || i4 > 6000 || objArr == false) {
            Log.w(TAG, "一键梭哈参数无效");
            dispatchActionCallback(actionCallback, false, "invalid_all_in_params");
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("maxTargets", i);
            jSONObject.put("reserveGold", i2);
            jSONObject.put("blockedAction", i3);
            jSONObject.put("keepLineup", z ? 1 : 0);
            jSONObject.put("ackTimeoutMs", i4);
            jSONObject.put("costMask", i5);
            JSONArray jSONArray = new JSONArray();
            if (iArr != null) {
                for (int i6 : iArr) {
                    if (i6 > 0) {
                        if (jSONArray.length() >= 64) {
                            break;
                        } else {
                            jSONArray.put(i6);
                        }
                    }
                }
            }
            jSONObject.put("priorityHeroIds", jSONArray);
            sendActionWithTerminal(IpcV2ActionMethods.CHESS_ALL_IN_START, jSONObject, actionCallback);
        } catch (Exception e) {
            Log.w(TAG, "一键梭哈启动参数编码失败", e);
            dispatchActionCallback(actionCallback, false, "invalid_action_params");
        }
    }

    public void stopAllIn(ActionCallback actionCallback) {
        sendActionWithTerminal(IpcV2ActionMethods.CHESS_ALL_IN_STOP, new JSONObject(), actionCallback);
    }

    public void cancelChessOp() {
        try {
            this.socketIPC.sendCommand(IpcV2ActionMethods.CANCEL_CHESS_OP, new JSONObject());
            Dbg.i(TAG, "取消所有 ChessOp 任务");
        } catch (Exception e) {
            Log.e(TAG, "取消 ChessOp 失败: " + e.getMessage());
        }
    }

    public void setChessOpInterval(int i) {
        try {
            this.configSync.state.setInt(IpcV2ConfigState.CHESS_OP_INTERVAL_FRAMES, i);
            this.configSync.requestSync();
            Dbg.i(TAG, "ChessOp 节奏设为 " + i + " 帧");
        } catch (Exception e) {
            Log.e(TAG, "设置 ChessOp 节奏失败: " + e.getMessage());
        }
    }

    public void modifyPopulation(int i, Runnable runnable) {
        try {
            sendAction(IpcV2ActionMethods.GAME_MODIFY_POPULATION, IpcTargetedActionPayloads.scalar("delta", i, this.targetMask, this.targetSelf), runnable);
            Dbg.i(TAG, "触发修改人口: " + (i > 0 ? "+" : "") + i);
        } catch (Exception e) {
            Log.e(TAG, "触发修改人口失败: " + e.getMessage());
        }
    }

    public void triggerExitGame() {
        sendAction(IpcV2ActionMethods.GAME_EXIT, new JSONObject(), null);
        Dbg.i(TAG, "已发送退出对局请求");
    }

    public void triggerGameEndDialog() {
        Log.w(TAG, "triggerGameEndDialog is not part of IPC v2");
    }

    public void setAutoRefreshShop(boolean z) {
        try {
            this.configSync.state.setBoolean(IpcV2ConfigState.AUTO_REFRESH_SHOP, z);
            this.configSync.requestSync();
            Dbg.i(TAG, "设置自动刷新商店: " + z);
        } catch (Exception e) {
            Log.e(TAG, "设置自动刷新商店失败: " + e.getMessage());
        }
    }

    public void setAutoRefreshFallbackMs(int i) {
        int iNormalizeMs = AutoRefreshFallbackPolicy.normalizeMs(i);
        try {
            this.configSync.state.setInt(IpcV2ConfigState.AUTO_REFRESH_FALLBACK_MS, iNormalizeMs);
            this.configSync.requestSync();
            Dbg.i(TAG, "设置自动刷新保底: " + iNormalizeMs + "ms");
        } catch (Exception e) {
            Log.e(TAG, "设置自动刷新保底失败: " + e.getMessage());
        }
    }

    public void setAutoRefreshReserveGold(int i) {
        int iNormalizeAllInReserveGold = QuickActionOverlayPolicy.normalizeAllInReserveGold(i);
        this.configSync.state.setInt(IpcV2ConfigState.AUTO_REFRESH_RESERVE_GOLD, iNormalizeAllInReserveGold);
        this.configSync.requestSync();
        Dbg.i(TAG, "设置自动刷新保留金币: " + iNormalizeAllInReserveGold);
    }

    public void triggerReinitIL2CPP() {
        this.configSync.requestSync();
        Dbg.i(TAG, "已同步初始化配置");
    }

    public void writeCustomAutoPickTargets(int[] iArr, Map<Integer, Integer> map) {
        this.configSync.state.setCustomAutoPickTargets(iArr, map);
        this.configSync.requestSync();
    }

    public void writeCustomAutoPickHeroIds(int[] iArr) {
        if (iArr == null) {
            iArr = new int[0];
        }
        try {
            this.configSync.state.setIntArray(IpcV2ConfigState.CUSTOM_AUTO_PICK_HERO_IDS, iArr);
            this.configSync.requestSync();
            Dbg.i(TAG, "[自定义拿牌] 已发送: " + iArr.length + " 个英雄ID");
        } catch (Exception e) {
            Log.e(TAG, "[自定义拿牌] 发送失败", e);
        }
    }

    public void writeUserExcludedHeroIds(int[] iArr) {
        if (iArr == null) {
            iArr = new int[0];
        }
        try {
            this.configSync.state.setIntArray(IpcV2ConfigState.EXCLUDED_HERO_IDS, iArr);
            this.configSync.requestSync();
            Dbg.i(TAG, "[用户排除黑名单] 已发送: " + iArr.length + " 个英雄ID");
        } catch (Exception e) {
            Log.e(TAG, "[用户排除黑名单] 发送失败", e);
        }
    }

    public boolean writeLineupAppliedHeroIds(int[] iArr) {
        if (iArr == null) {
            iArr = new int[0];
        }
        try {
            this.configSync.state.setLineupAppliedHeroIds(iArr);
            boolean zRequestSync = this.configSync.requestSync();
            Dbg.i(TAG, "[阵容应用] 已发送: " + iArr.length + " 个英雄ID");
            return zRequestSync;
        } catch (Exception e) {
            Log.e(TAG, "[阵容应用] 发送失败", e);
            return false;
        }
    }

    public boolean writeLineupAppliedTargets(int[] iArr, Map<Integer, Integer> map) {
        if (iArr == null) {
            iArr = new int[0];
        }
        try {
            this.configSync.state.setLineupAppliedTargets(iArr, map);
            boolean zRequestSync = this.configSync.requestSync();
            Dbg.i(TAG, "[阵容应用] 已原子发送: " + iArr.length + " 个英雄, 星级规则=" + (map == null ? 0 : map.size()));
            return zRequestSync;
        } catch (Exception e) {
            Log.e(TAG, "[阵容应用] 原子发送失败", e);
            return false;
        }
    }

    public void writeLineupAndAuxiliaryTargets(int[] iArr, Map<Integer, Integer> map, int[] iArr2, Map<Integer, Integer> map2) {
        if (iArr == null) {
            iArr = new int[0];
        }
        if (iArr2 == null) {
            iArr2 = new int[0];
        }
        try {
            this.configSync.state.setLineupAndAuxiliaryTargets(iArr, map, iArr2, map2);
            this.configSync.requestSync();
            Dbg.i(TAG, "[阵容应用] 已原子发送 lineup=" + iArr.length + " auxiliary=" + iArr2.length);
        } catch (Exception e) {
            Log.e(TAG, "[阵容+辅助] 原子发送失败", e);
        }
    }

    public boolean applyAppLineupToGame(JSONObject jSONObject) {
        if (jSONObject == null) {
            return false;
        }
        try {
            LocalSocketIPC.SendResult sendResultSendActionWithTerminal = sendActionWithTerminal(IpcV2ActionMethods.LINEUP_APPLY, jSONObject, null);
            Dbg.i(TAG, "[反向注入] applyAppLineup 已发送: heroes=" + ((!jSONObject.has("heroes") || jSONObject.optJSONArray("heroes") == null) ? 0 : jSONObject.optJSONArray("heroes").length()) + " title=" + jSONObject.optString("title", ""));
            return sendResultSendActionWithTerminal == LocalSocketIPC.SendResult.ACCEPTED;
        } catch (Exception e) {
            Log.e(TAG, "[反向注入] applyAppLineup 发送失败", e);
            return false;
        }
    }

    public void syncBattlePanelLineups(JSONArray jSONArray) {
        if (jSONArray == null) {
            jSONArray = new JSONArray();
        }
        JSONArray jSONArraySanitizeBattlePanelLineupsForConfig = sanitizeBattlePanelLineupsForConfig(jSONArray);
        try {
            this.configSync.state.setStructured(IpcV2ConfigState.BATTLE_PANEL_LINEUPS, IpcV2JsonTree.toList(jSONArraySanitizeBattlePanelLineupsForConfig));
            this.configSync.requestSync();
            Dbg.i(TAG, "[反向注入·对局阵容] syncBattlePanel 已发送: lineups=" + jSONArraySanitizeBattlePanelLineupsForConfig.length());
        } catch (Exception e) {
            Log.e(TAG, "[反向注入·对局阵容] syncBattlePanel 发送失败", e);
        }
    }

    private static JSONArray sanitizeBattlePanelLineupsForConfig(JSONArray jSONArray) {
        JSONArray jSONArrayOptJSONArray;
        JSONArray jSONArray2 = new JSONArray();
        if (jSONArray != null) {
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
                if (jSONObjectOptJSONObject != null) {
                    try {
                        JSONObject jSONObject = new JSONObject(jSONObjectOptJSONObject.toString());
                        jSONObject.remove("index");
                        JSONArray jSONArrayOptJSONArray2 = jSONObject.optJSONArray("heroes");
                        if (jSONArrayOptJSONArray2 != null) {
                            for (int i2 = 0; i2 < jSONArrayOptJSONArray2.length(); i2++) {
                                JSONObject jSONObjectOptJSONObject2 = jSONArrayOptJSONArray2.optJSONObject(i2);
                                if (jSONObjectOptJSONObject2 != null && (jSONArrayOptJSONArray = jSONObjectOptJSONObject2.optJSONArray("equips")) != null) {
                                    for (int i3 = 0; i3 < jSONArrayOptJSONArray.length(); i3++) {
                                        JSONObject jSONObjectOptJSONObject3 = jSONArrayOptJSONArray.optJSONObject(i3);
                                        if (jSONObjectOptJSONObject3 != null) {
                                            jSONObjectOptJSONObject3.remove("targetID");
                                        }
                                    }
                                }
                            }
                        }
                        jSONArray2.put(jSONObject);
                    } catch (Exception unused) {
                    }
                }
            }
        }
        return jSONArray2;
    }

    public boolean clearAppLineupFromGame() {
        try {
            LocalSocketIPC.SendResult sendResultSendActionWithTerminal = sendActionWithTerminal(IpcV2ActionMethods.LINEUP_CLEAR, new JSONObject(), null);
            this.configSync.state.setStructured(IpcV2ConfigState.BATTLE_PANEL_LINEUPS, Collections.emptyList());
            boolean zRequestSync = this.configSync.requestSync();
            Dbg.i(TAG, "[阵容推荐] 已发送取消应用请求");
            return sendResultSendActionWithTerminal == LocalSocketIPC.SendResult.ACCEPTED && zRequestSync;
        } catch (Exception e) {
            Log.e(TAG, "[阵容推荐] 取消应用请求发送失败", e);
            return false;
        }
    }

    public boolean syncLineupToGameSlot(JSONObject jSONObject) {
        if (jSONObject == null) {
            return false;
        }
        try {
            LocalSocketIPC.SendResult sendResultSendActionWithTerminal = sendActionWithTerminal(IpcV2ActionMethods.LINEUP_SYNC_TO_SLOT, jSONObject, null);
            Dbg.i(TAG, "[反向注入·阵容列表] syncLineupToGameSlot 已发送: title=" + jSONObject.optString("title", "") + " shareCode=" + jSONObject.optString("shareCode", "") + " deckIndex=" + jSONObject.optInt("deckIndex", -1));
            return sendResultSendActionWithTerminal == LocalSocketIPC.SendResult.ACCEPTED;
        } catch (Exception e) {
            Log.e(TAG, "[反向注入·阵容列表] syncLineupToGameSlot 发送失败", e);
            return false;
        }
    }

    public void writeAuxiliaryAutoPickHeroIds(int[] iArr) {
        if (iArr == null) {
            iArr = new int[0];
        }
        try {
            this.configSync.state.setIntArray(IpcV2ConfigState.AUXILIARY_AUTO_PICK_HERO_IDS, iArr);
            this.configSync.requestSync();
            Dbg.i(TAG, "[辅助拿牌] 已发送: " + iArr.length + " 个英雄ID");
        } catch (Exception e) {
            Log.e(TAG, "[辅助拿牌] 发送失败", e);
        }
    }

    public void writeAuxiliaryTargets(int[] iArr, Map<Integer, Integer> map) {
        int size = 0;
        if (iArr == null) {
            iArr = new int[0];
        }
        try {
            this.configSync.state.setAuxiliaryTargets(iArr, map);
            this.configSync.requestSync();
            StringBuilder sbAppend = new StringBuilder("[辅助拿牌] 已原子恢复: ").append(iArr.length).append(" 个英雄, 星级规则=");
            if (map != null) {
                size = map.size();
            }
            Dbg.i(TAG, sbAppend.append(size).toString());
        } catch (Exception e) {
            Log.e(TAG, "[辅助拿牌] 原子恢复失败", e);
        }
    }

    public void write5CostChosenFilter(int[] iArr) {
        if (iArr == null) {
            iArr = new int[0];
        }
        try {
            this.configSync.state.setIntArray(IpcV2ConfigState.CHOSEN_5_COST_FILTER_IDS, iArr);
            this.configSync.requestSync();
            Dbg.i(TAG, "[五费天选筛选] 已发送: " + iArr.length + " 个英雄ID");
        } catch (Exception e) {
            Log.e(TAG, "[五费天选筛选] 发送失败", e);
        }
    }

    public void writeAllChosenFilter(int[] iArr) {
        if (iArr == null) {
            iArr = new int[0];
        }
        try {
            this.configSync.state.setIntArray(IpcV2ConfigState.CHOSEN_ALL_COST_FILTER_IDS, iArr);
            this.configSync.requestSync();
            Dbg.i(TAG, "[天选全费筛选] 已发送: " + iArr.length + " 个英雄ID");
        } catch (Exception e) {
            Log.e(TAG, "[天选全费筛选] 发送失败", e);
        }
    }

    public void writeStarTargets(Map<Integer, Integer> map) {
        if (map == null) {
            try {
                map = Collections.emptyMap();
            } catch (Exception e) {
                Log.e(TAG, "[升星目标] 发送失败", e);
                return;
            }
        }
        this.configSync.state.setStarTargets(IpcV2ConfigState.STAR_TARGETS, map);
        this.configSync.requestSync();
        Dbg.i(TAG, "[升星目标] 已发送: " + map.size() + " 个英雄");
    }

    public void writeAuxiliaryStarTargets(Map<Integer, Integer> map) {
        if (map == null) {
            try {
                map = Collections.emptyMap();
            } catch (Exception e) {
                Log.e(TAG, "[辅助升星目标] 发送失败", e);
                return;
            }
        }
        this.configSync.state.setStarTargets(IpcV2ConfigState.AUXILIARY_STAR_TARGETS, map);
        this.configSync.requestSync();
        Dbg.i(TAG, "[辅助升星目标] 已发送: " + map.size() + " 个英雄");
    }

    public void writeLineupAppliedStarTargets(Map<Integer, Integer> map) {
        if (map == null) {
            try {
                map = Collections.emptyMap();
            } catch (Exception e) {
                Log.e(TAG, "[阵容升星目标] 发送失败", e);
                return;
            }
        }
        this.configSync.state.setStarTargets(IpcV2ConfigState.LINEUP_APPLIED_STAR_TARGETS, map);
        this.configSync.requestSync();
        Dbg.i(TAG, "[阵容升星目标] 已发送: " + map.size() + " 个英雄");
    }

    public void setAuxiliaryMode(boolean z) {
        try {
            this.configSync.state.setBoolean(IpcV2ConfigState.AUXILIARY_MODE, z);
            this.configSync.requestSync();
            Dbg.i(TAG, "[阵容模式] ".concat(z ? "辅助" : "主阵容"));
        } catch (Exception e) {
            Log.e(TAG, "[阵容模式] 发送失败", e);
        }
    }

    public void requestHexLevels(final HexLevelsCallback hexLevelsCallback) {
        if (hexLevelsCallback == null) {
            return;
        }
        if (!HexFeatureGate.isEnabledForPackage(this.TARGET_PACKAGE)) {
            hexLevelsCallback.onResult(null);
            return;
        }
        HexQueryBroker hexQueryBroker = this.hexQueryBroker;
        Objects.requireNonNull(hexLevelsCallback);
        hexQueryBroker.request(new HexQueryBroker.Callback() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda49
            @Override // com.android.support.HexQueryBroker.Callback
            public final void onResult(int[] iArr) {
                hexLevelsCallback.onResult(iArr);
            }
        });
        Dbg.i(TAG, "发送海克斯查询请求");
    }

    public SpecifiedHexStatus getSpecifiedHexStatus() {
        return this.specifiedHexStatus;
    }

    public void setSpecifiedHexStatusListener(SpecifiedHexStatusListener specifiedHexStatusListener) {
        this.specifiedHexStatusListener = specifiedHexStatusListener;
    }

    public void setHexLevelsListener(HexLevelsListener hexLevelsListener) {
        this.hexLevelsListener = hexLevelsListener;
    }

    public void specifyHexes(List<Integer> list, int i, ActionCallback actionCallback) {
        specifyHexes(list, i, false, actionCallback);
    }

    public void specifyHexes(List<Integer> list, int i, boolean z, ActionCallback actionCallback) {
        dispatchActionCallback(actionCallback, false, "feature_disabled");
    }

    public void cancelSpecifiedHex(ActionCallback actionCallback) {
        dispatchActionCallback(actionCallback, false, "feature_disabled");
    }

    public void specifyRecastTarget(int i, int i2, ActionCallback actionCallback) {
        specifyRecastTarget(i, i2, false, actionCallback);
    }

    public void specifyRecastTarget(int i, int i2, boolean z, ActionCallback actionCallback) {
        dispatchActionCallback(actionCallback, false, "feature_disabled");
    }

    public void cancelSpecifiedRecast(ActionCallback actionCallback) {
        dispatchActionCallback(actionCallback, false, "feature_disabled");
    }

    public void requestDebugPointers(LocalSocketIPC.RequestCallback requestCallback) {
        if (requestCallback == null) {
            return;
        }
        this.socketIPC.sendRequest("debug.pointers", new JSONObject(), 5000L, requestCallback);
    }

    public boolean isCppConnected() {
        return this.socketIPC.isClientConnected() && this.cppConnectedFlag;
    }

    RuntimeHookStatus getRuntimeHookStatus() {
        return this.runtimeHookStatus;
    }

    GameLaunchGuard.Status getRuntimeLaunchStatus() {
        return this.runtimeLaunchStatus;
    }

    public void logStatus() {
        try {
            Dbg.d(TAG, "========== IPC 状态 ==========");
            Dbg.d(TAG, "Socket: @" + LocalSocketIPC.SOCKET_NAME);
            Dbg.d(TAG, "连接: " + this.socketIPC.isClientConnected());
            Dbg.d(TAG, "C++就绪: " + this.cppConnectedFlag);
            Dbg.d(TAG, "自动拿牌: " + this.configSync.configAutoPick);
            Dbg.d(TAG, "自动出牌: " + this.configSync.configAutoPlay);
            Dbg.d(TAG, "==============================");
        } catch (Exception e) {
            Log.e(TAG, "读取状态失败", e);
        }
    }

    public String getConnectionDiagnostics() {
        StringBuilder sb = new StringBuilder("=== JSON IPC 连接诊断 ===\n");
        sb.append(this.socketIPC.getDiagnostics());
        sb.append("C++就绪: ").append(this.cppConnectedFlag ? "是" : "否").append("\n游戏功能初始化: ");
        sb.append(this.runtimeHookStatus.reason).append("\n=========================\n");
        return sb.toString();
    }

    public byte[] readSharedMemoryOnce() {
        return SENTINEL_DATA;
    }

    public boolean isCppConnectedFromData(byte[] bArr) {
        return this.cppConnectedFlag;
    }

    public int getHeroPoolTimestamp() {
        return this.snapshot.cachedHeroPoolTimestamp;
    }

    public int getHeroPoolTimestampFromData(byte[] bArr) {
        return this.snapshot.cachedHeroPoolTimestamp;
    }

    public int getPlayerTimestampFromData(byte[] bArr) {
        return this.snapshot.cachedPlayerTimestamp;
    }

    public List<HeroData> getHeroPoolData() {
        return this.snapshot.cachedHeroPool;
    }

    public List<HeroData> getLockedHeroData() {
        return this.snapshot.cachedLockedHeroes;
    }

    public List<HeroData>[] getHeroPoolDataWithLocked() {
        return buildHeroPoolResult();
    }

    public List<HeroData>[] getHeroPoolDataWithLockedFromData(byte[] bArr) {
        return buildHeroPoolResult();
    }

    private List<HeroData>[] buildHeroPoolResult() {
        List<HeroData> arrayList = this.snapshot.cachedHeroPool;
        List<HeroData> arrayList2 = this.snapshot.cachedLockedHeroes;
        if (arrayList == null && arrayList2 == null) {
            return null;
        }
        List<HeroData>[] listArr = new List[2];
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        listArr[0] = arrayList;
        if (arrayList2 == null) {
            arrayList2 = new ArrayList<>();
        }
        listArr[1] = arrayList2;
        return listArr;
    }

    public List<PlayerInfo> readPlayersInfo() {
        return this.snapshot.cachedPlayers;
    }

    public List<PlayerInfo> readPlayersInfoFromData(byte[] bArr) {
        return this.snapshot.cachedPlayers;
    }

    public List<BoardPositionInfo> readBoardPositionsFromData(byte[] bArr) {
        return this.snapshot.cachedBoardPositions;
    }

    public int getBoardPosTimestampFromData(byte[] bArr) {
        return this.snapshot.cachedBoardPosTimestamp;
    }

    public Set<Integer> getMyOwnedHeroIds() {
        return this.snapshot.cachedMyOwnedHeroIds;
    }

    public Map<Integer, Integer> getMyOwnedHeroCopies() {
        return this.snapshot.cachedMyOwnedHeroCopies.asMap();
    }

    public void setMyBoardHeroesListener(Runnable runnable) {
        this.myBoardHeroesListener = runnable;
    }

    public int getMyOwnedTimestamp() {
        return this.snapshot.cachedMyOwnedTimestamp;
    }

    public int getMyOwnedPieceCount() {
        return this.snapshot.cachedMyOwnedPieceCount;
    }

    public int readOpponentIdFromData(byte[] bArr) {
        return this.snapshot.cachedMatchupSnapshot.opponentId;
    }

    public int getMatchupTimestampFromData(byte[] bArr) {
        return this.snapshot.cachedMatchupSnapshot.revision;
    }

    public List<MatchupPairData> getAllMatchupPairs() {
        MatchupSnapshot matchupSnapshot = this.snapshot.cachedMatchupSnapshot;
        if (matchupSnapshot.pairs.isEmpty()) {
            return null;
        }
        return matchupSnapshot.pairs;
    }

    boolean isMatchupSnapshotFresh(long j, long j2) {
        return this.snapshot.cachedMatchupSnapshot.isFresh(j, j2);
    }

    public int getMatchupMyId() {
        return this.snapshot.cachedMatchupSnapshot.myId;
    }

    public MatchupSnapshot getMatchupSnapshot() {
        return this.snapshot.cachedMatchupSnapshot;
    }

    DiagnosticUiPublication getBoardUiPublication() {
        return this.snapshot.cachedBoardUiPublication;
    }

    RankAnchorSnapshot getRankAnchorSnapshot() {
        return this.snapshot.cachedRankAnchorSnapshot;
    }

    BoardHeroAnchorSnapshot getBoardHeroAnchorSnapshot() {
        return this.snapshot.cachedBoardHeroAnchorSnapshot;
    }

    boolean isCurrentRankAnchorGeneration(long j) {
        return this.snapshot.cachedRankAnchorSnapshot.isGeneration(j);
    }

    boolean isMatchActive() {
        return this.snapshot.currentMatchGeneration.length() == 22;
    }

    String getCurrentMatchGeneration() {
        return this.snapshot.currentMatchGeneration;
    }

    boolean hasFreshActiveGameSession(long j, long j2) {
        GameHudSessionPolicy.SnapshotEvidence snapshotEvidenceCollectSnapshotEvidence = GameHudSessionPolicy.collectSnapshotEvidence(this.snapshot.cachedPlayers, this.snapshot.cachedBoardPositions);
        return GameHudSessionPolicy.isReady(this.cppConnectedFlag, isSessionActive(), isMatchActive(), snapshotEvidenceCollectSnapshotEvidence.hasLocalPlayer(), snapshotEvidenceCollectSnapshotEvidence.hasLocalBoard, this.snapshot.currentMatchStartedAtMs, this.snapshot.cachedPlayersReceivedAtMs, this.snapshot.cachedBoardPositionsReceivedAtMs, j, j2);
    }

    boolean hasCurrentActiveGameSession(long j) {
        GameHudSessionPolicy.SnapshotEvidence snapshotEvidenceCollectSnapshotEvidence = GameHudSessionPolicy.collectSnapshotEvidence(this.snapshot.cachedPlayers, this.snapshot.cachedBoardPositions);
        return GameHudSessionPolicy.hasCurrentMatchEvidence(this.cppConnectedFlag, isMatchActive(), snapshotEvidenceCollectSnapshotEvidence.hasLocalPlayer(), snapshotEvidenceCollectSnapshotEvidence.hasLocalBoard, this.snapshot.currentMatchStartedAtMs, this.snapshot.cachedPlayersReceivedAtMs, this.snapshot.cachedBoardPositionsReceivedAtMs, j);
    }

    String describeFreshActiveGameSession(long j, long j2) {
        GameHudSessionPolicy.SnapshotEvidence snapshotEvidenceCollectSnapshotEvidence = GameHudSessionPolicy.collectSnapshotEvidence(this.snapshot.cachedPlayers, this.snapshot.cachedBoardPositions);
        return "ready=" + GameHudSessionPolicy.isReady(this.cppConnectedFlag, isSessionActive(), isMatchActive(), snapshotEvidenceCollectSnapshotEvidence.hasLocalPlayer(), snapshotEvidenceCollectSnapshotEvidence.hasLocalBoard, this.snapshot.currentMatchStartedAtMs, this.snapshot.cachedPlayersReceivedAtMs, this.snapshot.cachedBoardPositionsReceivedAtMs, j, j2) + " transport=" + this.cppConnectedFlag + " session=" + isSessionActive() + " match=" + isMatchActive() + " localPlayer=" + snapshotEvidenceCollectSnapshotEvidence.hasLocalPlayer() + " localBoard=" + snapshotEvidenceCollectSnapshotEvidence.hasLocalBoard + " playersAgeMs=" + safeAgeMs(j, this.snapshot.cachedPlayersReceivedAtMs) + " boardAgeMs=" + safeAgeMs(j, this.snapshot.cachedBoardPositionsReceivedAtMs) + " startedAgeMs=" + safeAgeMs(j, this.snapshot.currentMatchStartedAtMs);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void clearGameSnapshotCachesForSessionBoundary() {
        GameSnapshotStore gameSnapshotStore = this.snapshot;
        gameSnapshotStore.clearForSessionBoundary(nextMatchupRevision(gameSnapshotStore.cachedMatchupSnapshot.revision));
        clearSpecifiedPredictionPreviews();
        PeerSync.getInstance(this.context).stop();
        this.recastRunStatus = RecastRunStatus.IDLE;
        this.recastLivePool = null;
        this.specifiedShopStatus = null;
        final RecastRunStatusListener recastRunStatusListener = this.recastRunStatusListener;
        final RecastLivePoolListener recastLivePoolListener = this.recastLivePoolListener;
        this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1448x935750f1(recastRunStatusListener, recastLivePoolListener);
            }
        });
        this.shopSlotState.clear();
        updateAllInStatus(IpcAllInStatus.INACTIVE);
        updateOpponentCardingStatus(OpponentCardingStatus.disabled());
        this.lastSpecifiedHexTargetRevision = 0L;
        this.lastSpecifiedHexSessionGeneration = 0L;
        this.hexQueryBroker.cancelPending();
        updateSpecifiedHexStatus(SpecifiedHexStatus.IDLE);
        final LatestOwnerListener.Snapshot<ShopSlotsCallback> snapshot = this.shopSlotsCallbacks.snapshot();
        Handler handler = this.mainHandler;
        if (snapshot != null && handler != null) {
            handler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda48
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1449xf4a9ed90(snapshot);
                }
            });
        }
        notifyHexLevelsListener("", null);
    }

    /* renamed from: lambda$clearGameSnapshotCachesForSessionBoundary$13$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1448x935750f1(RecastRunStatusListener recastRunStatusListener, RecastLivePoolListener recastLivePoolListener) {
        if (recastRunStatusListener != null && this.recastRunStatusListener == recastRunStatusListener && this.recastRunStatus == RecastRunStatus.IDLE) {
            recastRunStatusListener.onRecastRunStatus(RecastRunStatus.IDLE);
        }
        if (recastLivePoolListener != null && this.recastLivePoolListener == recastLivePoolListener && this.recastLivePool == null) {
            recastLivePoolListener.onRecastLivePool(null);
        }
    }

    /* renamed from: lambda$clearGameSnapshotCachesForSessionBoundary$14$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1449xf4a9ed90(LatestOwnerListener.Snapshot snapshot) {
        if (this.shopSlotsCallbacks.isCurrent(snapshot)) {
            ((ShopSlotsCallback) snapshot.listener()).onShopSlotsUpdated(null);
        }
    }

    public int[] readHexLevelsFromData(byte[] bArr) {
        return this.snapshot.cachedHexLevels;
    }

    public int[] getCachedHexLevels() {
        int[] iArr = this.snapshot.cachedHexLevels;
        if (iArr == null) {
            return null;
        }
        return (int[]) iArr.clone();
    }

    public int[] getCachedHexLivePool() {
        int[] iArr = this.snapshot.cachedHexLivePool;
        return iArr == null ? new int[0] : (int[]) iArr.clone();
    }

    public int[] getCachedHexLivePoolWeights() {
        int[] iArr = this.snapshot.cachedHexLivePoolWeights;
        return iArr == null ? new int[0] : (int[]) iArr.clone();
    }

    public int getCachedHexLivePoolTotalWeight() {
        return this.snapshot.cachedHexLivePoolTotalWeight;
    }

    public int getCachedHexDrawsPerStage() {
        return this.snapshot.cachedHexDrawsPerStage;
    }

    private void notifyHexLevelsListener(final String str, int[] iArr) {
        final HexLevelsListener hexLevelsListener = this.hexLevelsListener;
        Handler handler = this.mainHandler;
        if (hexLevelsListener == null || handler == null) {
            return;
        }
        if (str == null) {
            str = "";
        }
        final int[] iArr2 = iArr == null ? null : (int[]) iArr.clone();
        handler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1460xbe5f0819(hexLevelsListener, iArr2, str);
            }
        });
    }

    /* renamed from: lambda$notifyHexLevelsListener$15$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1460xbe5f0819(HexLevelsListener hexLevelsListener, int[] iArr, String str) {
        if (this.hexLevelsListener == hexLevelsListener) {
            if (iArr == null || str.equals(this.snapshot.currentMatchGeneration)) {
                hexLevelsListener.onHexLevels(iArr);
            }
        }
    }

    public int getCachedHexRevision() {
        return this.snapshot.cachedHexTimestamp;
    }

    public int getHexTimestampFromData(byte[] bArr) {
        return this.snapshot.cachedHexTimestamp;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleCppMessage(String str, JSONObject jSONObject) {
        try {
        } catch (Exception e) {
            Log.e(TAG, "[MSG] 处理异常: " + str, e);
        }
        if (!MatchStatePolicy.scoped(str) || MatchStatePolicy.accepts(this.snapshot.currentMatchGeneration, jSONObject.optString("matchGeneration", ""))) {
            if (!this.pushDispatcher.dispatch(str, jSONObject)) {
                Dbg.d(TAG, "[MSG] 未知方法: " + str);
            }
        }
    }

    private IpcPushDispatcher createPushDispatcher() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("specified.compute", new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda51
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) throws Exception {
                this.f$0.m1451xec03d3da(jSONObject);
            }
        });
        linkedHashMap.put("runtime.policy", new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda7
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) throws Exception {
                this.f$0.m1452x4d567079(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.MATCH_STARTED, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda14
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) throws Exception {
                this.f$0.handleMatchStarted(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.MATCH_ENDED, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda15
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) throws Exception {
                this.f$0.handleMatchEnded(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.CONNECTED, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda16
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.handleConnected(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.RUNTIME_HOOK_STATUS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda17
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.handleRuntimeHookStatus(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.RUNTIME_LAUNCH_STATUS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda18
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) throws Exception {
                this.f$0.m1453xaea90d18(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_PLAYERS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda19
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parsePlayersData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_HERO_POOL, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda20
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseHeroPoolData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_BOARD_POSITIONS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda21
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseBoardPositionsData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_MY_OWNED_HEROES, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda52
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseMyOwnedHeroesData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_OPPONENT_CARDING_STATUS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda53
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) throws Exception {
                this.f$0.parseOpponentCardingStatus(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.TOAST, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda54
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.handleNativeToast(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_ALL_IN_STATUS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda55
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) throws Exception {
                this.f$0.m1454xffba9b7(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_BOARD_HERO_ANCHORS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda1
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseBoardHeroAnchorsData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_HEX_PREDICTION, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda2
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseHexData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_MATCHUP, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda3
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseMatchupData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_OPPONENT_DIAGNOSTICS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda4
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseOpponentDiagnostics(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_SHOP_AUTOMATION_DIAGNOSTICS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda5
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseShopAutomationDiagnostics(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_GAME_RECOMMEND_UI_DIAGNOSTICS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda6
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseGameRecommendUiDiagnostics(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_GAME_RECOMMEND_HOOK_DIAGNOSTICS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda8
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseGameRecommendHookDiagnostics(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_RANK_ANCHORS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda9
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseRankAnchorsData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_SHOP_SLOTS, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda10
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseShopSlotsData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_GAME_RECOMMEND_TEAM, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda12
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseGameRecommendTeamData(jSONObject);
            }
        });
        linkedHashMap.put(IpcV2PushMethods.UPDATE_GAME_RECOMMEND_ACTIVE, new IpcPushDispatcher.Handler() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda13
            @Override // com.android.support.IpcPushDispatcher.Handler
            public final void handle(JSONObject jSONObject) {
                this.f$0.parseGameRecommendActiveTargets(jSONObject);
            }
        });
        return new IpcPushDispatcher(linkedHashMap);
    }

    /* renamed from: lambda$createPushDispatcher$16$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1451xec03d3da(JSONObject jSONObject) throws Exception {
        NativeSpecifiedCompute.relay(this.context, jSONObject);
    }

    /* renamed from: lambda$createPushDispatcher$17$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1452x4d567079(JSONObject jSONObject) throws Exception {
        NativeSpecifiedCompute.relay(this.context, jSONObject);
    }

    /* renamed from: lambda$createPushDispatcher$18$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1453xaea90d18(JSONObject jSONObject) throws Exception {
        this.runtimeLaunchStatus = GameLaunchGuard.parse(jSONObject, SystemClock.elapsedRealtime());
    }

    /* renamed from: lambda$createPushDispatcher$19$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1454xffba9b7(JSONObject jSONObject) throws Exception {
        updateAllInStatus(IpcAllInStatus.parse(jSONObject));
    }

    private /* synthetic */ void lambda$createPushDispatcher$20(JSONObject jSONObject) throws Exception {
        parseSpecifiedPredictionPreview(SpecifiedPredictionPreview.KIND_SHOP, jSONObject);
    }

    private /* synthetic */ void lambda$createPushDispatcher$21(JSONObject jSONObject) throws Exception {
        parseSpecifiedPredictionPreview(SpecifiedPredictionPreview.KIND_RECAST, jSONObject);
    }

    private /* synthetic */ void lambda$createPushDispatcher$22(JSONObject jSONObject) throws Exception {
        parseSpecifiedPredictionPreview(SpecifiedPredictionPreview.KIND_HEX, jSONObject);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMatchStarted(JSONObject jSONObject) throws Exception {
        IpcMatchLifecycleParser.Started started = IpcMatchLifecycleParser.parseStarted(jSONObject);
        String str = started.matchGeneration;
        long j = started.sessionGeneration;
        boolean zEquals = str.equals(this.snapshot.currentMatchGeneration);
        if (!zEquals) {
            clearGameSnapshotCachesForSessionBoundary();
            this.snapshot.currentMatchGeneration = str;
            this.snapshot.currentMatchStartedAtMs = System.currentTimeMillis();
        }
        synchronized (this) {
            if (shouldResetSpecifiedHexSessionWatermark(zEquals, this.lastSpecifiedHexSessionGeneration, j)) {
                this.lastSpecifiedHexTargetRevision = 0L;
                clearSpecifiedPredictionPreviews();
                updateSpecifiedHexStatus(SpecifiedHexStatus.IDLE);
            }
            this.lastSpecifiedHexSessionGeneration = j;
        }
        Dbg.i(TAG, "[MSG] match started");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMatchEnded(JSONObject jSONObject) throws Exception {
        if (IpcMatchLifecycleParser.parseEnded(jSONObject).equals(this.snapshot.currentMatchGeneration)) {
            this.snapshot.currentMatchGeneration = "";
            this.snapshot.currentMatchStartedAtMs = 0L;
            clearGameSnapshotCachesForSessionBoundary();
        }
        Dbg.i(TAG, "[MSG] match ended");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRuntimeHookStatus(JSONObject jSONObject) {
        RuntimeDiagnostics runtimeDiagnostics;
        RuntimeHookStatus runtimeHookStatus = RuntimeHookStatus.parse(jSONObject);
        if (runtimeHookStatus == null) {
            return;
        }
        RuntimeHookStatus runtimeHookStatus2 = this.runtimeHookStatus;
        this.runtimeHookStatus = runtimeHookStatus;
        if ((runtimeHookStatus2.coreReady == runtimeHookStatus.coreReady && runtimeHookStatus2.reason.equals(runtimeHookStatus.reason)) || (runtimeDiagnostics = RuntimeDiagnostics.get()) == null) {
            return;
        }
        runtimeDiagnostics.record("GAME", "INITIALIZATION", runtimeHookStatus.coreReady ? "OK" : "WAIT", "reason=" + runtimeHookStatus.reason);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConnected(JSONObject jSONObject) {
        this.cppConnectedFlag = jSONObject.optBoolean("value", false);
        if (!this.cppConnectedFlag) {
            this.runtimeHookStatus = RuntimeHookStatus.UNKNOWN;
            this.runtimeLaunchStatus = null;
            clearSpecifiedPredictionPreviews();
            invalidateHexLevels("ipc_disconnected");
        }
        Dbg.i(TAG, "[MSG] C++ connected: " + this.cppConnectedFlag);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNativeToast(JSONObject jSONObject) {
        String strTrim = jSONObject.optString("text", "").trim();
        if (!strTrim.isEmpty()) {
            Dbg.i(TAG, "[MSG] native toast: " + strTrim);
            dispatchNativeToast(this.uiNotifier, this.context, strTrim);
        } else {
            Log.w(TAG, "[MSG] native toast ignored: empty text");
        }
    }

    private void updateAllInStatus(IpcAllInStatus ipcAllInStatus) {
        if (ipcAllInStatus == null) {
            ipcAllInStatus = IpcAllInStatus.INACTIVE;
        }
        this.cachedAllInStatus = ipcAllInStatus;
        final AllInStatusListener allInStatusListener = this.allInStatusListener;
        if (allInStatusListener == null) {
            return;
        }
        final IpcAllInStatus ipcAllInStatus2 = this.cachedAllInStatus;
        this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1470lambda$updateAllInStatus$23$comandroidsupportSharedMemManager(allInStatusListener, ipcAllInStatus2);
            }
        });
    }

    /* renamed from: lambda$updateAllInStatus$23$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1470lambda$updateAllInStatus$23$comandroidsupportSharedMemManager(AllInStatusListener allInStatusListener, IpcAllInStatus ipcAllInStatus) {
        if (this.allInStatusListener == allInStatusListener && this.cachedAllInStatus == ipcAllInStatus) {
            allInStatusListener.onAllInStatus(ipcAllInStatus.active, ipcAllInStatus.reason);
        }
    }

    private void parseRecastRunStatus(JSONObject jSONObject) {
        try {
            final RecastRunStatus recastRunStatusFromJson = RecastRunStatus.fromJson(jSONObject);
            if (MatchStatePolicy.accepts(this.snapshot.currentMatchGeneration, recastRunStatusFromJson.matchGeneration)) {
                this.recastRunStatus = recastRunStatusFromJson;
                final RecastRunStatusListener recastRunStatusListener = this.recastRunStatusListener;
                if (recastRunStatusListener != null) {
                    this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda45
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1463xac15a4b8(recastRunStatusListener, recastRunStatusFromJson);
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "解析指定重铸状态失败", e);
        }
    }

    /* renamed from: lambda$parseRecastRunStatus$24$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1463xac15a4b8(RecastRunStatusListener recastRunStatusListener, RecastRunStatus recastRunStatus) {
        if (this.recastRunStatusListener == recastRunStatusListener && this.recastRunStatus == recastRunStatus && MatchStatePolicy.accepts(this.snapshot.currentMatchGeneration, recastRunStatus.matchGeneration)) {
            recastRunStatusListener.onRecastRunStatus(recastRunStatus);
        }
    }

    private void parseRecastLivePool(JSONObject jSONObject) {
        try {
            final RecastLivePoolCatalog recastLivePoolCatalog = RecastLivePoolCatalog.parse(jSONObject);
            if (recastLivePoolCatalog != null && MatchStatePolicy.accepts(this.snapshot.currentMatchGeneration, recastLivePoolCatalog.matchGeneration)) {
                this.recastLivePool = recastLivePoolCatalog;
                final RecastLivePoolListener recastLivePoolListener = this.recastLivePoolListener;
                if (recastLivePoolListener != null) {
                    this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda26
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1462xda637cb2(recastLivePoolListener, recastLivePoolCatalog);
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "解析重铸候选池失败", e);
        }
    }

    /* renamed from: lambda$parseRecastLivePool$25$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1462xda637cb2(RecastLivePoolListener recastLivePoolListener, RecastLivePoolCatalog recastLivePoolCatalog) {
        if (this.recastLivePoolListener == recastLivePoolListener && this.recastLivePool == recastLivePoolCatalog && MatchStatePolicy.accepts(this.snapshot.currentMatchGeneration, recastLivePoolCatalog.matchGeneration)) {
            recastLivePoolListener.onRecastLivePool(recastLivePoolCatalog);
        }
    }

    private void parseSpecifiedHexStatus(JSONObject jSONObject) {
        try {
            SpecifiedHexStatus specifiedHexStatusFromJson = SpecifiedHexStatus.fromJson(jSONObject);
            synchronized (this) {
                if (shouldAcceptSpecifiedHexStatus(specifiedHexStatusFromJson, this.snapshot.currentMatchGeneration, this.lastSpecifiedHexSessionGeneration, this.lastSpecifiedHexTargetRevision)) {
                    this.lastSpecifiedHexTargetRevision = specifiedHexStatusFromJson.targetRevision;
                    updateSpecifiedHexStatus(specifiedHexStatusFromJson);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "解析指定海克斯状态失败", e);
        }
    }

    static boolean shouldAcceptSpecifiedHexStatus(SpecifiedHexStatus specifiedHexStatus, String str, long j, long j2) {
        return specifiedHexStatus != null && str != null && specifiedHexStatus.matchGeneration.equals(str) && j > 0 && specifiedHexStatus.sessionGeneration == j && specifiedHexStatus.targetRevision > j2;
    }

    private void updateSpecifiedHexStatus(SpecifiedHexStatus specifiedHexStatus) {
        if (specifiedHexStatus == null) {
            specifiedHexStatus = SpecifiedHexStatus.IDLE;
        }
        this.specifiedHexStatus = specifiedHexStatus;
        final SpecifiedHexStatusListener specifiedHexStatusListener = this.specifiedHexStatusListener;
        if (specifiedHexStatusListener != null) {
            final SpecifiedHexStatus specifiedHexStatus2 = this.specifiedHexStatus;
            this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1472xaa8521e4(specifiedHexStatusListener, specifiedHexStatus2);
                }
            });
        }
    }

    /* renamed from: lambda$updateSpecifiedHexStatus$26$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1472xaa8521e4(SpecifiedHexStatusListener specifiedHexStatusListener, SpecifiedHexStatus specifiedHexStatus) {
        if (this.specifiedHexStatusListener == specifiedHexStatusListener) {
            specifiedHexStatusListener.onSpecifiedHexStatus(specifiedHexStatus);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseOpponentCardingStatus(JSONObject jSONObject) throws Exception {
        updateOpponentCardingStatus(IpcOpponentCardingParser.parse(jSONObject));
    }

    private void updateOpponentCardingStatus(OpponentCardingStatus opponentCardingStatus) {
        GameSnapshotStore gameSnapshotStore = this.snapshot;
        if (opponentCardingStatus == null) {
            opponentCardingStatus = OpponentCardingStatus.disabled();
        }
        gameSnapshotStore.cachedOpponentCardingStatus = opponentCardingStatus;
        final OpponentCardingStatusListener opponentCardingStatusListener = this.opponentCardingStatusListener;
        if (opponentCardingStatusListener != null) {
            final OpponentCardingStatus opponentCardingStatus2 = this.snapshot.cachedOpponentCardingStatus;
            this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda50
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1471xd8c660e1(opponentCardingStatusListener, opponentCardingStatus2);
                }
            });
        }
    }

    /* renamed from: lambda$updateOpponentCardingStatus$27$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1471xd8c660e1(OpponentCardingStatusListener opponentCardingStatusListener, OpponentCardingStatus opponentCardingStatus) {
        if (this.opponentCardingStatusListener == opponentCardingStatusListener && this.snapshot.cachedOpponentCardingStatus == opponentCardingStatus) {
            opponentCardingStatusListener.onOpponentCardingStatus(opponentCardingStatus);
        }
    }

    public boolean isDualPlayMode() {
        return this.snapshot.cachedDualPlayMode;
    }

    public int getGameTurnState() {
        return this.snapshot.cachedTurnState;
    }

    private void applyDualPlayMode(List<PlayerInfo> list, boolean z) {
        this.snapshot.cachedDualPlayMode = z;
        DualPlayTeamPolicy.apply(list, z);
    }

    private void logAvatarPipeline(JSONObject jSONObject, PlayerInfo playerInfo) {
        if (this.avatarDiagSucceeded) {
            return;
        }
        boolean z = (playerInfo.avatarUrl == null || playerInfo.avatarUrl.isEmpty()) ? false : true;
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (z || jCurrentTimeMillis - this.avatarDiagLastMs >= AVATAR_DIAG_INTERVAL_MS) {
            this.avatarDiagLastMs = jCurrentTimeMillis;
            if (z) {
                this.avatarDiagSucceeded = true;
                Dbg.i(TAG, "[头像链路] OK url=" + playerInfo.avatarUrl);
            } else {
                Dbg.i(TAG, "[头像链路] 未取到 hasAvatarUrlKey=" + jSONObject.has("avatarUrl") + " urlLen=" + (playerInfo.avatarUrl == null ? -1 : playerInfo.avatarUrl.length()) + " avatarId=" + playerInfo.avatarId + " botKnown=" + playerInfo.botKnown + "  —— hasAvatarUrlKey=false 说明跑的 native 是旧包；true 但 urlLen=0 说明字段名没解析到，看下一行字段自述");
            }
        }
    }

    static String normalizeAvatarUrl(String str) {
        if (str == null) {
            return "";
        }
        String strTrim = str.trim();
        if (strTrim.isEmpty()) {
            return "";
        }
        return strTrim.regionMatches(true, 0, "http://", 0, 7) ? "https://" + strTrim.substring(7) : strTrim;
    }

    private void logProfileProbe(JSONObject jSONObject) {
        String strOptString = jSONObject.optString("profileProbe", "");
        if (strOptString.isEmpty()) {
            return;
        }
        Dbg.i(TAG, "[头像链路/字段自述] " + strOptString);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parsePlayersData(JSONObject jSONObject) {
        JSONArray jSONArrayOptJSONArray;
        logProfileProbe(jSONObject);
        try {
            IpcPlayersParser.Payload payload = IpcPlayersParser.parse(jSONObject);
            if (payload == null) {
                return;
            }
            if (!payload.players.isEmpty() && (jSONArrayOptJSONArray = jSONObject.optJSONArray("players")) != null && jSONArrayOptJSONArray.length() > 0) {
                logAvatarPipeline(jSONArrayOptJSONArray.getJSONObject(0), payload.players.get(0));
            }
            applyDualPlayMode(payload.players, payload.dualPlay);
            this.snapshot.cachedPlayers = payload.players;
            this.snapshot.cachedPlayerTimestamp++;
            this.snapshot.cachedPlayersReceivedAtMs = System.currentTimeMillis();
            this.snapshot.cachedTurnState = jSONObject.optInt("turnState", -1);
            this.snapshot.cachedTurnStateAtMs = SystemClock.elapsedRealtime();
            triggerPeerSync(payload.players);
        } catch (Exception e) {
            Log.e(TAG, "解析玩家数据失败", e);
        }
    }

    public void setPeerSyncEnabled(boolean z) {
        this.peerSyncEnabled = z;
        if (z) {
            return;
        }
        PeerSync.getInstance(this.context).stop();
        this.snapshot.lastMatchHash = "";
    }

    public boolean isPeerSyncEnabled() {
        return this.peerSyncEnabled;
    }

    private void triggerPeerSync(List<PlayerInfo> list) {
        int iMySeat;
        if (PeerMatchFingerprint.shouldStart(this.peerSyncEnabled, list) && (iMySeat = PeerMatchFingerprint.mySeat(list)) >= 0) {
            String strMd5 = PeerMatchFingerprint.md5(PeerMatchFingerprint.namesKey(list));
            if (PeerMatchFingerprint.isNewHash(strMd5, this.snapshot.lastMatchHash)) {
                this.snapshot.lastMatchHash = strMd5;
                PeerSync.getInstance(this.context).start(strMd5, iMySeat);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseHeroPoolData(JSONObject jSONObject) {
        try {
            IpcHeroPoolParser.Payload payload = IpcHeroPoolParser.parse(jSONObject);
            if (payload.pool != null) {
                this.snapshot.cachedHeroPool = payload.pool;
            }
            if (payload.locked != null) {
                this.snapshot.cachedLockedHeroes = payload.locked;
            }
            this.snapshot.cachedHeroPoolTimestamp++;
        } catch (Exception e) {
            Log.e(TAG, "解析牌库数据失败", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseBoardPositionsData(JSONObject jSONObject) {
        DiagnosticUiPublication diagnosticUiPublication = parseDiagnosticUiPublication(jSONObject, TraceCatalog.Feature.BOARD_HUD);
        try {
            List<BoardPositionInfo> list = IpcBoardPositionsParser.parse(jSONObject);
            if (list == null) {
                return;
            }
            this.snapshot.cachedBoardPositions = list;
            this.snapshot.cachedBoardPosTimestamp++;
            this.snapshot.cachedBoardPositionsReceivedAtMs = System.currentTimeMillis();
            this.snapshot.cachedBoardUiPublication = diagnosticUiPublication;
            Runnable runnable = this.myBoardHeroesListener;
            if (runnable != null) {
                runnable.run();
            }
            if (this.snapshot.cachedBoardPosTimestamp >= 4 && this.snapshot.cachedBoardPosTimestamp % 30 != 0) {
                return;
            }
            Dbg.i(TAG, IpcBoardPositionsParser.diagnosticLine(list, this.snapshot.cachedBoardPosTimestamp));
        } catch (Exception e) {
            Log.e(TAG, "解析棋盘站位数据失败", e);
            completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_PARSE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseMyOwnedHeroesData(JSONObject jSONObject) {
        try {
            IpcMyOwnedHeroesParser.Payload payload = IpcMyOwnedHeroesParser.parse(jSONObject);
            if (payload == null) {
                return;
            }
            this.snapshot.cachedMyOwnedHeroCopies = payload.copies;
            this.snapshot.cachedMyOwnedHeroIds = Collections.unmodifiableSet(new HashSet(payload.copies.heroIds()));
            this.snapshot.cachedMyOwnedPieceCount = payload.pieceCount;
            this.snapshot.cachedMyOwnedTimestamp++;
        } catch (Exception e) {
            Log.e(TAG, "解析自己拥有英雄数据失败", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void parseBoardHeroAnchorsData(JSONObject jSONObject) {
        try {
            IpcBoardHeroAnchorsParser.Payload payload = IpcBoardHeroAnchorsParser.parse(jSONObject);
            GameSnapshotStore gameSnapshotStore = this.snapshot;
            gameSnapshotStore.cachedBoardHeroAnchorSnapshot = gameSnapshotStore.cachedBoardHeroAnchorSnapshot.next(payload.anchors, payload.sequence, payload.planning, System.currentTimeMillis());
        } catch (Exception e) {
            Log.e(TAG, "解析棋子头顶锚点数据失败", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void parseHexData(JSONObject jSONObject) {
        IpcHexLevelsParser.Payload payload;
        try {
            payload = IpcHexLevelsParser.parse(jSONObject);
        } catch (Exception e) {
            Log.e(TAG, "解析海克斯数据失败", e);
        }
        if (payload != null && payload.isForMatch(this.snapshot.currentMatchGeneration)) {
            if (!payload.available) {
                invalidateHexLevels(payload.reason);
                return;
            }
            int[] iArr = payload.levels;
            this.snapshot.cachedHexLevels = iArr;
            this.snapshot.cachedHexTimestamp++;
            if (payload.livePool.length > 0) {
                this.snapshot.cachedHexLivePool = payload.livePool;
                this.snapshot.cachedHexLivePoolWeights = payload.livePoolWeights;
                this.snapshot.cachedHexLivePoolTotalWeight = payload.livePoolTotalWeight;
            }
            if (payload.drawsPerStage > 0) {
                this.snapshot.cachedHexDrawsPerStage = payload.drawsPerStage;
            }
            Dbg.i(TAG, IpcHexLevelsParser.diagnosticLine(payload));
            notifyHexLevelsListener(payload.matchGeneration, iArr);
            this.hexQueryBroker.complete(iArr);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseMatchupData(JSONObject jSONObject) {
        DiagnosticUiPublication diagnosticUiPublication = parseDiagnosticUiPublication(jSONObject, TraceCatalog.Feature.OPPONENT_PREDICTION);
        try {
            IpcMatchupEnvelopeParser.Payload payload = IpcMatchupEnvelopeParser.parse(jSONObject);
            long jCurrentTimeMillis = System.currentTimeMillis();
            MatchupSnapshot matchupSnapshot = this.snapshot.cachedMatchupSnapshot;
            if (!MatchupSnapshot.shouldAccept(matchupSnapshot, payload.matchId, payload.source, jCurrentTimeMillis, MATCHUP_SNAPSHOT_MAX_AGE_MS)) {
                Dbg.i(TAG, "[opponent] dropped lower-priority source=" + payload.source + " matchId=" + payload.matchId + " currentSource=" + matchupSnapshot.source + " currentMatchId=" + matchupSnapshot.matchId);
                completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_STALE_REVISION);
            } else {
                MatchupSnapshot matchupSnapshotCreate = MatchupSnapshot.create(payload.myId, payload.opponentId, payload.pairs, nextMatchupRevision(matchupSnapshot.revision), jCurrentTimeMillis, payload.matchId, payload.source, diagnosticUiPublication);
                this.snapshot.cachedMatchupSnapshot = matchupSnapshotCreate;
                Dbg.i(TAG, "[opponent] received source=" + payload.source + " matchId=" + payload.matchId + " count=" + payload.pairs.size() + " myId=" + matchupSnapshotCreate.myId + " opponentId=" + matchupSnapshotCreate.opponentId);
            }
        } catch (Exception e) {
            Log.e(TAG, "解析对手预测数据失败", e);
            completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_PARSE);
        }
    }

    private DiagnosticUiPublication parseDiagnosticUiPublication(JSONObject jSONObject, int i) {
        DiagnosticUiPublication.Status status;
        int iDiagnosticUiSlot;
        DiagnosticUiPublication diagnosticUiPublication;
        if (jSONObject != null && jSONObject.has(DiagnosticUiPublication.METADATA_KEY)) {
            try {
                DiagnosticUiPublication diagnosticUiPublication2 = DiagnosticUiPublication.parse(jSONObject, i, this.snapshot.currentMatchGeneration);
                if (diagnosticUiPublication2 == null || (iDiagnosticUiSlot = diagnosticUiSlot(i)) < 0) {
                    return null;
                }
                synchronized (this.diagnosticUiLock) {
                    DiagnosticUiPublication diagnosticUiPublication3 = this.diagnosticUiPublications[iDiagnosticUiSlot];
                    if (diagnosticUiPublication3 == null || !diagnosticUiPublication3.matchGeneration.equals(diagnosticUiPublication2.matchGeneration) || Long.compareUnsigned(diagnosticUiPublication2.publicationRevision, diagnosticUiPublication3.publicationRevision) > 0) {
                        this.diagnosticUiPublications[iDiagnosticUiSlot] = diagnosticUiPublication2;
                        diagnosticUiPublication = null;
                    } else {
                        diagnosticUiPublication = diagnosticUiPublication2;
                    }
                }
                if (diagnosticUiPublication == null) {
                    return diagnosticUiPublication2;
                }
                sendDiagnosticUiReceipt(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_STALE_REVISION);
                return null;
            } catch (Exception e) {
                Log.w(TAG, "拒绝无效诊断 UI 发布 feature=" + i, e);
                DiagnosticUiPublication diagnosticUiPublicationRejectionIdentity = DiagnosticUiPublication.rejectionIdentity(jSONObject, i, this.snapshot.currentMatchGeneration);
                if (diagnosticUiPublicationRejectionIdentity != null) {
                    if (e instanceof DiagnosticUiPublication.StaleGenerationException) {
                        status = DiagnosticUiPublication.Status.REJECTED_STALE_GENERATION;
                    } else {
                        status = DiagnosticUiPublication.Status.REJECTED_PARSE;
                    }
                    sendDiagnosticUiReceipt(diagnosticUiPublicationRejectionIdentity, status);
                }
            }
        }
        return null;
    }

    public void completeDiagnosticUiPublication(DiagnosticUiPublication diagnosticUiPublication, DiagnosticUiPublication.Status status) {
        int iDiagnosticUiSlot;
        if (diagnosticUiPublication == null || status == null || (iDiagnosticUiSlot = diagnosticUiSlot(diagnosticUiPublication.featureId)) < 0) {
            return;
        }
        synchronized (this.diagnosticUiLock) {
            DiagnosticUiPublication diagnosticUiPublication2 = this.diagnosticUiPublications[iDiagnosticUiSlot];
            if (diagnosticUiPublication2 != null && diagnosticUiPublication2.sameIdentity(diagnosticUiPublication)) {
                this.diagnosticUiPublications[iDiagnosticUiSlot] = null;
                sendDiagnosticUiReceipt(diagnosticUiPublication, status);
            }
        }
    }

    private void sendDiagnosticUiReceipt(DiagnosticUiPublication diagnosticUiPublication, DiagnosticUiPublication.Status status) {
        try {
            sendAction(IpcV2ActionMethods.DIAGNOSTICS_UI_APPLIED, diagnosticUiPublication.receipt(status), null);
        } catch (Exception e) {
            Log.w(TAG, "诊断 UI 回执发送失败", e);
        }
    }

    private void parseSpecifiedShopStatus(JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        SpecifiedShopStatus specifiedShopStatus = new SpecifiedShopStatus(jSONObject.optBoolean("armed", false), jSONObject.optBoolean("ready", false), Math.max(0, jSONObject.optInt("targets", 0)), Math.max(0, jSONObject.optInt("roster", 0)), Math.max(0, jSONObject.optInt("unaffordable", 0)), Math.max(0, jSONObject.optInt("reachable", 0)), Math.max(0L, jSONObject.optLong("predictions", 0L)), Math.max(0, jSONObject.optInt("refreshed", 0)), Math.max(0, jSONObject.optInt("landed", 0)), Math.max(0, Math.min(3, jSONObject.optInt("timingSamples", 0))), jSONObject.optBoolean("timingFailed", false), jSONObject.optInt("delayMin", -1), jSONObject.optInt("delayMax", -1), jSONObject.optInt("windowMax", -1), jSONObject.optInt("windowMin", jSONObject.optInt("delayMin", -1)), Math.max(0, Math.min(3, jSONObject.optInt("timingSubmitted", 0))), jSONObject.optString("timingIssue", ""));
        this.specifiedShopStatus = specifiedShopStatus;
        Log.i(TAG, "specifiedShop armed=" + specifiedShopStatus.armed + " samples=" + specifiedShopStatus.timingSamples + " submitted=" + specifiedShopStatus.timingSubmitted + " issue=" + specifiedShopStatus.timingIssue + " targets=" + specifiedShopStatus.targets + " roster=" + specifiedShopStatus.roster + " custom=" + jSONObject.optInt("custom", -1) + " auxiliary=" + jSONObject.optInt("auxiliary", -1) + " turn=" + jSONObject.optInt("turn", -1) + " moved=" + jSONObject.optInt("moved", 0) + " window=" + jSONObject.optInt("windowMin", -1) + ":" + jSONObject.optInt("windowMax", -1) + " source=" + jSONObject.optInt("sourceFrame", -1) + "/" + jSONObject.optInt("sourceCount", -1) + " ready=" + specifiedShopStatus.ready);
        SpecifiedShopStatusListener specifiedShopStatusListener = this.specifiedShopStatusListener;
        if (specifiedShopStatusListener != null) {
            specifiedShopStatusListener.onSpecifiedShopStatus(specifiedShopStatus);
        }
    }

    public static final class SpecifiedShopStatus {
        public final boolean armed;
        public final int delayMax;
        public final int delayMin;
        public final int landed;
        public final long predictions;
        public final int reachable;
        public final boolean ready;
        public final int refreshed;
        public final int roster;
        public final int targets;
        public final boolean timingFailed;
        public final String timingIssue;
        public final int timingSamples;
        public final int timingSubmitted;
        public final int unaffordable;
        public final int windowMax;
        public final int windowMin;

        SpecifiedShopStatus(boolean z, boolean z2, int i, int i2, int i3, int i4, long j, int i5, int i6, int i7, boolean z3, int i8, int i9, int i10) {
            this(z, z2, i, i2, i3, i4, j, i5, i6, i7, z3, i8, i9, i10, i8, i7, "");
        }

        SpecifiedShopStatus(boolean z, boolean z2, int i, int i2, int i3, int i4, long j, int i5, int i6, int i7, boolean z3, int i8, int i9, int i10, int i11, int i12, String str) {
            this.armed = z;
            this.ready = z2;
            this.targets = i;
            this.roster = i2;
            this.unaffordable = i3;
            this.reachable = i4;
            this.predictions = j;
            this.refreshed = i5;
            this.landed = i6;
            this.timingSamples = i7;
            this.timingFailed = z3;
            this.delayMin = i8;
            this.delayMax = i9;
            this.windowMax = i10;
            this.windowMin = i11;
            this.timingSubmitted = i12;
            this.timingIssue = str;
        }
    }

    public SpecifiedShopStatus getSpecifiedShopStatus() {
        return this.specifiedShopStatus;
    }

    public void setSpecifiedShopStatusListener(SpecifiedShopStatusListener specifiedShopStatusListener) {
        this.specifiedShopStatusListener = specifiedShopStatusListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseGameRecommendActiveTargets(JSONObject jSONObject) {
        DiagnosticUiPublication diagnosticUiPublication = parseDiagnosticUiPublication(jSONObject, TraceCatalog.Feature.GAME_RECOMMENDATION);
        IpcGameRecommendParser.ActiveTargets activeTargets = IpcGameRecommendParser.parseActiveTargets(jSONObject);
        Dbg.i(TAG, "[game-recommend-active] active=" + activeTargets.active + " targets=" + activeTargets.heroIds.length);
        GameLineupBridge gameLineupBridge = GameLineupBridge.get();
        gameLineupBridge.onGameRecommendActiveTargets(activeTargets.active, activeTargets.heroIds);
        completeGameRecommendPublicationAfterUi(gameLineupBridge, diagnosticUiPublication, DiagnosticUiPublication.Status.APPLIED);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseGameRecommendTeamData(JSONObject jSONObject) {
        DiagnosticUiPublication diagnosticUiPublication = parseDiagnosticUiPublication(jSONObject, TraceCatalog.Feature.GAME_RECOMMENDATION);
        GameLineupBridge gameLineupBridge = GameLineupBridge.get();
        GameLineupBridge.PublicationResult publicationResultOnGameRecommendReceived = gameLineupBridge.onGameRecommendReceived(jSONObject);
        if (diagnosticUiPublication == null) {
            return;
        }
        if (publicationResultOnGameRecommendReceived == GameLineupBridge.PublicationResult.REJECTED_PARSE) {
            completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_PARSE);
        } else if (publicationResultOnGameRecommendReceived == GameLineupBridge.PublicationResult.REJECTED_STALE) {
            completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_STALE_REVISION);
        } else {
            completeGameRecommendPublicationAfterUi(gameLineupBridge, diagnosticUiPublication, DiagnosticUiPublication.Status.APPLIED);
        }
    }

    private void completeGameRecommendPublicationAfterUi(GameLineupBridge gameLineupBridge, final DiagnosticUiPublication diagnosticUiPublication, final DiagnosticUiPublication.Status status) {
        if (diagnosticUiPublication == null) {
            return;
        }
        final boolean z = gameLineupBridge != null && gameLineupBridge.hasUiConsumers();
        this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1450x58bcf817(diagnosticUiPublication, z, status);
            }
        });
    }

    /* renamed from: lambda$completeGameRecommendPublicationAfterUi$28$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1450x58bcf817(DiagnosticUiPublication diagnosticUiPublication, boolean z, DiagnosticUiPublication.Status status) {
        if (!z) {
            status = DiagnosticUiPublication.Status.CACHED_NOT_VISIBLE;
        }
        completeDiagnosticUiPublication(diagnosticUiPublication, status);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void parseRankAnchorsData(JSONObject jSONObject) {
        DiagnosticUiPublication diagnosticUiPublication = parseDiagnosticUiPublication(jSONObject, TraceCatalog.Feature.RANK_HUD);
        try {
            List<RankWarningAnchor> list = IpcRankAnchorsParser.parse(jSONObject);
            GameSnapshotStore gameSnapshotStore = this.snapshot;
            gameSnapshotStore.cachedRankAnchorSnapshot = gameSnapshotStore.cachedRankAnchorSnapshot.next(list, System.currentTimeMillis(), diagnosticUiPublication);
        } catch (Exception e) {
            Log.e(TAG, "解析排行锚点数据失败", e);
            completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_PARSE);
        }
    }

    public void clearMatchupCache() {
        MatchupSnapshot matchupSnapshot = this.snapshot.cachedMatchupSnapshot;
        this.snapshot.cachedMatchupSnapshot = MatchupSnapshot.empty(nextMatchupRevision(matchupSnapshot.revision));
    }

    public synchronized void clearRankAnchorCache() {
        GameSnapshotStore gameSnapshotStore = this.snapshot;
        gameSnapshotStore.cachedRankAnchorSnapshot = gameSnapshotStore.cachedRankAnchorSnapshot.cleared();
    }

    public synchronized void clearBoardHeroAnchorCache() {
        GameSnapshotStore gameSnapshotStore = this.snapshot;
        gameSnapshotStore.cachedBoardHeroAnchorSnapshot = gameSnapshotStore.cachedBoardHeroAnchorSnapshot.cleared();
    }

    public synchronized void clearHexCache() {
        invalidateHexLevels("cache_cleared");
    }

    private synchronized void invalidateHexLevels(String str) {
        this.snapshot.cachedHexLevels = null;
        this.snapshot.cachedHexTimestamp++;
        this.snapshot.cachedHexLivePool = null;
        this.snapshot.cachedHexLivePoolWeights = null;
        this.snapshot.cachedHexLivePoolTotalWeight = 0;
        this.snapshot.cachedHexDrawsPerStage = 0;
        notifyHexLevelsListener(this.snapshot.currentMatchGeneration, null);
        this.hexQueryBroker.complete(null);
        StringBuilder sb = new StringBuilder("海克斯数据已失效: ");
        if (str == null) {
            str = EnvironmentCompat.MEDIA_UNKNOWN;
        }
        Dbg.i(TAG, sb.append(str).toString());
    }

    public synchronized void clearShopSlotCache() {
        this.shopSlotState.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseShopSlotsData(JSONObject jSONObject) {
        final DiagnosticUiPublication diagnosticUiPublication = parseDiagnosticUiPublication(jSONObject, TraceCatalog.Feature.SHOP_HUD);
        try {
            IpcShopSlotState.Update updateApply = this.shopSlotState.apply(jSONObject);
            if (!updateApply.changed) {
                completeDiagnosticUiPublication(diagnosticUiPublication, this.shopSlotsCallbacks.snapshot() != null ? DiagnosticUiPublication.Status.APPLIED : DiagnosticUiPublication.Status.CACHED_NOT_VISIBLE);
                return;
            }
            if (updateApply.shouldLogDiagnostics()) {
                Dbg.i(TAG, "[diag][shop] #" + updateApply.diagnosticIndex + " slots=[" + IpcShopSlotsParser.diagnosticHeroIds(updateApply.slots == null ? Collections.emptyList() : updateApply.slots) + "] callback=" + (this.shopSlotsCallbacks.snapshot() != null));
            }
            final LatestOwnerListener.Snapshot<ShopSlotsCallback> snapshot = this.shopSlotsCallbacks.snapshot();
            final List<ShopSlotData> list = updateApply.slots;
            if (snapshot != null) {
                this.mainHandler.post(new Runnable() { // from class: com.android.support.SharedMemManager$$ExternalSyntheticLambda46
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1464x36448ef1(snapshot, list, diagnosticUiPublication);
                    }
                });
            } else {
                completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.CACHED_NOT_VISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "解析商店格子数据失败", e);
            completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_PARSE);
        }
    }

    /* renamed from: lambda$parseShopSlotsData$29$com-android-support-SharedMemManager, reason: not valid java name */
    /* synthetic */ void m1464x36448ef1(LatestOwnerListener.Snapshot snapshot, List list, DiagnosticUiPublication diagnosticUiPublication) {
        if (this.shopSlotsCallbacks.isCurrent(snapshot)) {
            try {
                ((ShopSlotsCallback) snapshot.listener()).onShopSlotsUpdated(list);
                completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.APPLIED);
                return;
            } catch (RuntimeException e) {
                Log.w(TAG, "商店 HUD 应用诊断发布失败", e);
                completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_PARSE);
                return;
            }
        }
        completeDiagnosticUiPublication(diagnosticUiPublication, DiagnosticUiPublication.Status.REJECTED_NO_CONSUMER);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public LocalSocketIPC.HudStyleSnapshot buildHudStyleSnapshot() {
        HudAppearance hudAppearanceDefaults = this.hudAppearance;
        if (hudAppearanceDefaults == null) {
            hudAppearanceDefaults = HudAppearance.defaults();
        }
        return new LocalSocketIPC.HudStyleSnapshot(Math.max(1L, this.configSync.hudStyleRevision), hudAppearanceDefaults.toIpcParams());
    }

    static boolean isStarTargetKey(String str) {
        return IpcConfigSync.isStarTargetKey(str);
    }

    private void sendAction(String str, JSONObject jSONObject, Runnable runnable) {
        this.actionSender.send(str, jSONObject, runnable);
    }

    private LocalSocketIPC.SendResult sendActionWithTerminal(String str, JSONObject jSONObject, ActionCallback actionCallback) {
        return this.actionSender.sendWithTerminal(str, jSONObject, actionCallback);
    }

    private void dispatchActionCallback(ActionCallback actionCallback, boolean z, String str) {
        this.actionSender.dispatch(actionCallback, z, str);
    }

    public synchronized void release() {
        this.runtimeHookStatus = RuntimeHookStatus.UNKNOWN;
        this.runtimeLaunchStatus = null;
        try {
            this.ipcRuntime.stop();
            this.cppConnectionCallback = null;
            this.cppConnectionCallbacks.clear();
            this.opponentCardingStatusListener = null;
            this.allInStatusListener = null;
            this.specifiedHexStatusListener = null;
            this.hexLevelsListener = null;
            int i = 0;
            while (true) {
                SpecifiedPredictionPreviewListener[] specifiedPredictionPreviewListenerArr = this.specifiedPreviewListeners;
                if (i >= specifiedPredictionPreviewListenerArr.length) {
                    break;
                }
                specifiedPredictionPreviewListenerArr[i] = null;
                i++;
            }
            this.shopSlotsCallbacks.clear();
            this.uiNotifier = null;
            this.cppConnectedFlag = false;
            this.snapshot.currentMatchGeneration = "";
            this.snapshot.currentMatchStartedAtMs = 0L;
            clearGameSnapshotCachesForSessionBoundary();
            this.snapshot.cachedOpponentCardingStatus = OpponentCardingStatus.disabled();
            Dbg.i(TAG, "IPC 资源已释放");
        } catch (Exception e) {
            Log.e(TAG, "释放失败", e);
        }
    }
}
