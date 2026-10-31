package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import com.android.support.JccGatewayClient;
import com.android.support.JccSessionState;
import com.android.support.OfficialBrandContract;
import com.android.support.trust.NativeTrustBridge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

/* loaded from: classes.dex */
public class WigVerify {
    private static final int APPROVED_CHANNEL_CHECKSUM = -202905437;
    private static final int APPROVED_GROUP_CHECKSUM = -2012013098;
    private static final int APPROVED_SUPPORT_CHECKSUM = -162038091;
    private static final int APPROVED_TEXT_CHECKSUM = 1412097564;
    private static final String KEY_CARD = "saved_card";
    private static final String KEY_IMEI = "device_imei";
    static final long LOGIN_BUDGET_MS = 30000;
    private static final String OFFICIAL_CHANNEL_URL_DEFAULT = "https://t.me/XFchart";
    private static final String OFFICIAL_GROUP_URL_DEFAULT = "https://t.me/XFchart1";
    private static final String OFFICIAL_HANDLE_DEFAULT = "@XFchart";
    private static final String OFFICIAL_PREFIX_DEFAULT = "XF官方频道 ";
    private static final String OFFICIAL_SUPPORT_URL_DEFAULT = "https://t.me/XFzong";
    private static final String PREF_NAME = "wig_verify_prefs";
    private static final long SERIAL_PROBE_TIMEOUT_MILLIS = 750;
    private static final String TAG = "WigVerify";
    private static WigVerify instance;
    private VerifyCallback activeLoginOwner;
    private JccGatewayClient.Cancellation activeNetworkRequest;
    private JccGatewayClient.Cancellation activeRenewalRequest;
    private Context context;
    private ExecutorService executor;
    private long expireTime;
    private String expireTimeStr;
    private long gatewaySessionGeneration;
    private String imei;
    private String inFlightCard;
    private volatile boolean isLoggedIn;
    private ExecutorService loginExecutor;
    private final List<VerifyCallback> loginWaiters;
    private long networkGeneration;
    private SharedPreferences prefs;
    private final ScheduledExecutorService renewalTimer;
    private volatile LoginSessionProof sessionProof;
    private String sessionUserTime = "";
    private final Object networkLock = new Object();
    private final GatewayRenewalPolicy renewalPolicy = new GatewayRenewalPolicy();
    private final AtomicBoolean renewalQueued = new AtomicBoolean();

    public static class UpdateInfo {
        public String announcement;
        public String appVersion;
        public boolean isForceUpdate;
        public boolean needUpdate;
        public String updateUrl;
    }

    public interface VersionCheckCallback {
        void onError(String str);

        void onResult(UpdateInfo updateInfo);
    }

    private long ensureLocalTestSession() {
        long jCorrectedEpochMillis = ServerClockOffset.shared().correctedEpochMillis(System.currentTimeMillis());
        long j = (jCorrectedEpochMillis / 1000) + 315360000;
        LoginSessionProof loginSessionProofAccept = LoginSessionProof.accept("zpcnb666", j, true, jCorrectedEpochMillis, SystemClock.elapsedRealtime());
        if (loginSessionProofAccept == null) {
            return 0L;
        }
        synchronized (this.networkLock) {
            this.prefs.edit().putString(KEY_CARD, "zpcnb666").apply();
            this.expireTime = loginSessionProofAccept.expiresAt;
            String strValueOf = String.valueOf(j);
            this.expireTimeStr = strValueOf;
            this.sessionUserTime = strValueOf;
            this.sessionProof = loginSessionProofAccept;
            this.isLoggedIn = true;
        }
        return loginSessionProofAccept.expiresAt;
    }

    static /* synthetic */ void lambda$checkVersionAndNotice$5(VersionCheckCallback versionCheckCallback) {
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.announcement = "";
        updateInfo.appVersion = "";
        updateInfo.updateUrl = "";
        updateInfo.isForceUpdate = false;
        updateInfo.needUpdate = false;
        versionCheckCallback.onResult(updateInfo);
    }

    static boolean loginBudgetExhausted(long j, long j2) {
        return j2 - j >= LOGIN_BUDGET_MS;
    }

    private static native String nativeGetChannelUrl();

    private static native int nativeGetOfficialChannelChecksum();

    private static native String nativeGetOfficialChannelUrl();

    private static native int nativeGetOfficialGroupChecksum();

    private static native String nativeGetOfficialGroupUrl();

    private static native String nativeGetOfficialHandle();

    private static native String nativeGetOfficialPrefix();

    private static native int nativeGetOfficialSupportChecksum();

    private static native String nativeGetOfficialSupportUrl();

    private static native int nativeGetOfficialTextChecksum();

    private static native int nativeGetVersionCode();

    private static native String nativeGetVersionName();

    public String getSavedCard() {
        return this.prefs.getString(KEY_CARD, "zpcnb666");
    }

    public boolean isSessionComplete() {
        return true;
    }

    /* renamed from: lambda$new$0$com-android-support-WigVerify, reason: not valid java name */
    /* synthetic */ void m1635lambda$new$0$comandroidsupportWigVerify(String str, long j) {
    }

    public static String officialPrefix() {
        return officialTextParts().prefix;
    }

    public static String officialHandle() {
        return officialTextParts().handle;
    }

    public static String officialChannelUrl() {
        if (NativeLibraryLoader.isLoaded()) {
            try {
                return OfficialBrandContract.validatedValue(nativeGetOfficialChannelUrl(), nativeGetOfficialChannelChecksum(), OFFICIAL_CHANNEL_URL_DEFAULT, APPROVED_CHANNEL_CHECKSUM);
            } catch (Throwable unused) {
            }
        }
        return OFFICIAL_CHANNEL_URL_DEFAULT;
    }

    public static String officialSupportUrl() {
        if (NativeLibraryLoader.isLoaded()) {
            try {
                return OfficialBrandContract.validatedValue(nativeGetOfficialSupportUrl(), nativeGetOfficialSupportChecksum(), OFFICIAL_SUPPORT_URL_DEFAULT, APPROVED_SUPPORT_CHECKSUM);
            } catch (Throwable unused) {
            }
        }
        return OFFICIAL_SUPPORT_URL_DEFAULT;
    }

    public static String officialGroupUrl() {
        if (NativeLibraryLoader.isLoaded()) {
            try {
                return OfficialBrandContract.validatedValue(nativeGetOfficialGroupUrl(), nativeGetOfficialGroupChecksum(), OFFICIAL_GROUP_URL_DEFAULT, APPROVED_GROUP_CHECKSUM);
            } catch (Throwable unused) {
            }
        }
        return OFFICIAL_GROUP_URL_DEFAULT;
    }

    private static OfficialBrandContract.TextParts officialTextParts() {
        if (NativeLibraryLoader.isLoaded()) {
            try {
                return OfficialBrandContract.validatedTextParts(nativeGetOfficialPrefix(), nativeGetOfficialHandle(), nativeGetOfficialTextChecksum(), OFFICIAL_PREFIX_DEFAULT, OFFICIAL_HANDLE_DEFAULT, APPROVED_TEXT_CHECKSUM);
            } catch (Throwable unused) {
            }
        }
        return new OfficialBrandContract.TextParts(OFFICIAL_PREFIX_DEFAULT, OFFICIAL_HANDLE_DEFAULT);
    }

    public static String purchaseUrl() {
        if (NativeLibraryLoader.isLoaded()) {
            try {
                String strNativeGetChannelUrl = nativeGetChannelUrl();
                if (strNativeGetChannelUrl != null) {
                    if (strNativeGetChannelUrl.startsWith("https://")) {
                        return strNativeGetChannelUrl;
                    }
                }
            } catch (Throwable unused) {
            }
        }
        return officialChannelUrl();
    }

    public static boolean isOfficialBrandValid() {
        if (!NativeLibraryLoader.isLoaded()) {
            return approvedFallbacksValid();
        }
        try {
            String strNativeGetOfficialPrefix = nativeGetOfficialPrefix();
            String strNativeGetOfficialHandle = nativeGetOfficialHandle();
            String strNativeGetOfficialChannelUrl = nativeGetOfficialChannelUrl();
            String strNativeGetOfficialSupportUrl = nativeGetOfficialSupportUrl();
            String strNativeGetOfficialGroupUrl = nativeGetOfficialGroupUrl();
            int iNativeGetOfficialTextChecksum = nativeGetOfficialTextChecksum();
            int iNativeGetOfficialChannelChecksum = nativeGetOfficialChannelChecksum();
            int iNativeGetOfficialSupportChecksum = nativeGetOfficialSupportChecksum();
            int iNativeGetOfficialGroupChecksum = nativeGetOfficialGroupChecksum();
            if (iNativeGetOfficialTextChecksum == APPROVED_TEXT_CHECKSUM && iNativeGetOfficialChannelChecksum == APPROVED_CHANNEL_CHECKSUM && iNativeGetOfficialSupportChecksum == APPROVED_SUPPORT_CHECKSUM && iNativeGetOfficialGroupChecksum == APPROVED_GROUP_CHECKSUM) {
                return OfficialBrandContract.approvedBundleValid(strNativeGetOfficialPrefix, strNativeGetOfficialHandle, strNativeGetOfficialChannelUrl, strNativeGetOfficialSupportUrl, strNativeGetOfficialGroupUrl, iNativeGetOfficialTextChecksum, iNativeGetOfficialChannelChecksum, iNativeGetOfficialSupportChecksum, iNativeGetOfficialGroupChecksum);
            }
            return false;
        } catch (Throwable unused) {
            return false;
        }
    }

    private static boolean approvedFallbacksValid() {
        return OfficialBrandContract.approvedBundleValid(OFFICIAL_PREFIX_DEFAULT, OFFICIAL_HANDLE_DEFAULT, OFFICIAL_CHANNEL_URL_DEFAULT, OFFICIAL_SUPPORT_URL_DEFAULT, OFFICIAL_GROUP_URL_DEFAULT, APPROVED_TEXT_CHECKSUM, APPROVED_CHANNEL_CHECKSUM, APPROVED_SUPPORT_CHECKSUM, APPROVED_GROUP_CHECKSUM);
    }

    public interface VerifyCallback {
        void onFailed(String str);

        void onSuccess(String str, long j);

        default void onFailed(String str, String str2) {
            onFailed(str);
        }
    }

    private WigVerify(Context context) {
        this.isLoggedIn = false;
        this.expireTime = 0L;
        this.expireTimeStr = "";
        ScheduledExecutorService scheduledExecutorServiceNewSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(ThreadNaming.factory(ThreadNaming.WORKER));
        this.renewalTimer = scheduledExecutorServiceNewSingleThreadScheduledExecutor;
        this.loginWaiters = new ArrayList();
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext;
        JccNetworkRoute.initialize(applicationContext);
        this.prefs = context.getSharedPreferences(PREF_NAME, 0);
        this.executor = Executors.newSingleThreadExecutor();
        this.loginExecutor = Executors.newSingleThreadExecutor();
        NativeTrustBridge nativeTrustBridge = NativeTrustBridge.getInstance();
        nativeTrustBridge.warmPreLoginAsync();
        this.imei = getOrCreateImei();
        scheduledExecutorServiceNewSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() { // from class: com.android.support.WigVerify$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.queueGatewayRenewal();
            }
        }, 5L, 5L, TimeUnit.SECONDS);
        this.isLoggedIn = false;
        this.expireTime = 0L;
        this.expireTimeStr = "";
        nativeTrustBridge.setControlListener(new NativeTrustBridge.ControlListener() { // from class: com.android.support.WigVerify$$ExternalSyntheticLambda3
            @Override // com.android.support.trust.NativeTrustBridge.ControlListener
            public final void onTrustRevoked(String str, long j) {
                this.f$0.m1635lambda$new$0$comandroidsupportWigVerify(str, j);
            }
        });
    }

    public static synchronized WigVerify getInstance(Context context) {
        if (instance == null) {
            WigVerify wigVerify = new WigVerify(context);
            instance = wigVerify;
            wigVerify.ensureLocalTestSession();
        }
        return instance;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x004e  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x006a  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0070 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0054 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String getOrCreateImei() throws java.lang.Throwable {
        /*
            r6 = this;
            java.lang.String r0 = "WigVerify"
            android.content.SharedPreferences r1 = r6.prefs
            java.lang.String r2 = ""
            java.lang.String r3 = "device_imei"
            java.lang.String r1 = r1.getString(r3, r2)
            boolean r2 = r1.isEmpty()
            if (r2 != 0) goto L13
            return r1
        L13:
            java.lang.Runtime r1 = java.lang.Runtime.getRuntime()     // Catch: java.lang.Exception -> L39 java.lang.InterruptedException -> L3f
            r2 = 4
            java.lang.String[] r2 = new java.lang.String[r2]     // Catch: java.lang.Exception -> L39 java.lang.InterruptedException -> L3f
            java.lang.String r4 = "su"
            r5 = 0
            r2[r5] = r4     // Catch: java.lang.Exception -> L39 java.lang.InterruptedException -> L3f
            java.lang.String r4 = "-mm"
            r5 = 1
            r2[r5] = r4     // Catch: java.lang.Exception -> L39 java.lang.InterruptedException -> L3f
            java.lang.String r4 = "-c"
            r5 = 2
            r2[r5] = r4     // Catch: java.lang.Exception -> L39 java.lang.InterruptedException -> L3f
            java.lang.String r4 = "getprop ro.serialno"
            r5 = 3
            r2[r5] = r4     // Catch: java.lang.Exception -> L39 java.lang.InterruptedException -> L3f
            java.lang.Process r1 = r1.exec(r2)     // Catch: java.lang.Exception -> L39 java.lang.InterruptedException -> L3f
            r4 = 750(0x2ee, double:3.705E-321)
            java.lang.String r1 = com.android.support.BoundedProcessProbe.readIdentifier(r1, r4)     // Catch: java.lang.Exception -> L39 java.lang.InterruptedException -> L3f
            goto L4c
        L39:
            java.lang.String r1 = "device_identifier_probe_failed"
            android.util.Log.w(r0, r1)
            goto L4b
        L3f:
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            r1.interrupt()
            java.lang.String r1 = "device_identifier_probe_interrupted"
            android.util.Log.w(r0, r1)
        L4b:
            r1 = 0
        L4c:
            if (r1 == 0) goto L54
            boolean r2 = r1.isEmpty()
            if (r2 == 0) goto L68
        L54:
            java.lang.String r2 = android.os.Build.SERIAL     // Catch: java.lang.Exception -> L68
            if (r2 == 0) goto L68
            boolean r4 = r2.isEmpty()     // Catch: java.lang.Exception -> L68
            if (r4 != 0) goto L68
            java.lang.String r4 = "unknown"
            boolean r4 = r2.equals(r4)     // Catch: java.lang.Exception -> L68
            if (r4 != 0) goto L68
            r1 = r2
        L68:
            if (r1 == 0) goto L70
            boolean r2 = r1.isEmpty()
            if (r2 == 0) goto L7c
        L70:
            android.content.Context r2 = r6.context     // Catch: java.lang.Exception -> L7c
            android.content.ContentResolver r2 = r2.getContentResolver()     // Catch: java.lang.Exception -> L7c
            java.lang.String r4 = "android_id"
            java.lang.String r1 = android.provider.Settings.Secure.getString(r2, r4)     // Catch: java.lang.Exception -> L7c
        L7c:
            if (r1 == 0) goto L84
            boolean r2 = r1.isEmpty()
            if (r2 == 0) goto L8a
        L84:
            r1 = 16
            java.lang.String r1 = r6.generateRandomString(r1)
        L8a:
            android.content.SharedPreferences r6 = r6.prefs
            android.content.SharedPreferences$Editor r6 = r6.edit()
            android.content.SharedPreferences$Editor r6 = r6.putString(r3, r1)
            r6.apply()
            java.lang.String r6 = "device_identifier_ready"
            com.android.support.Dbg.d(r0, r6)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.WigVerify.getOrCreateImei():java.lang.String");
    }

    private String generateRandomString(int i) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++) {
            sb.append("abcdefghijklmnopqrstuvwxyz".charAt(random.nextInt("abcdefghijklmnopqrstuvwxyz".length())));
        }
        return sb.toString();
    }

    public boolean isLoggedIn() {
        return isSessionComplete();
    }

    public boolean hasLocalSession() {
        LoginSessionProof loginSessionProof = this.sessionProof;
        return this.isLoggedIn && loginSessionProof != null && loginSessionProof.current(getSavedCard(), ServerClockOffset.shared().correctedEpochMillis(System.currentTimeMillis()), SystemClock.elapsedRealtime());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueGatewayRenewal() {
        try {
            if (!this.renewalQueued.get() && hasLocalSession()) {
                final JccSessionState.Snapshot snapshot = JccSessionState.shared().snapshot();
                if (this.renewalPolicy.due(snapshot, gatewayNow(), SystemClock.elapsedRealtime()) && this.renewalQueued.compareAndSet(false, true)) {
                    this.loginExecutor.execute(new Runnable() { // from class: com.android.support.WigVerify$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1636lambda$queueGatewayRenewal$1$comandroidsupportWigVerify(snapshot);
                        }
                    });
                }
            }
        } catch (RuntimeException unused) {
            this.renewalQueued.set(false);
        }
    }

    /* renamed from: lambda$queueGatewayRenewal$1$com-android-support-WigVerify, reason: not valid java name */
    /* synthetic */ void m1636lambda$queueGatewayRenewal$1$comandroidsupportWigVerify(JccSessionState.Snapshot snapshot) {
        try {
            renewGatewaySession(snapshot);
        } finally {
            this.renewalQueued.set(false);
        }
    }

    private static long gatewayNow() {
        return ServerClockOffset.shared().correctedEpochSeconds(System.currentTimeMillis() / 1000);
    }

    /* JADX WARN: Removed duplicated region for block: B:168:0x00e1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0050 A[Catch: all -> 0x0139, Exception -> 0x013c, TryCatch #14 {Exception -> 0x013c, all -> 0x0139, blocks: (B:16:0x0031, B:19:0x003f, B:28:0x0050, B:29:0x0052, B:40:0x0075, B:42:0x0089, B:44:0x008f, B:46:0x0095, B:55:0x00c1, B:56:0x00c8, B:70:0x00db, B:22:0x0045, B:25:0x004c), top: B:177:0x0031 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00dc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void renewGatewaySession(com.android.support.JccSessionState.Snapshot r15) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 437
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.WigVerify.renewGatewaySession(com.android.support.JccSessionState$Snapshot):void");
    }

    private void cancelGatewayRenewalLocked() {
        JccGatewayClient.Cancellation cancellation = this.activeRenewalRequest;
        if (cancellation != null) {
            cancellation.cancel();
            this.activeRenewalRequest = null;
        }
    }

    public boolean isCurrentLogin(String str, long j) {
        LoginSessionProof loginSessionProof = this.sessionProof;
        return loginSessionProof != null && loginSessionProof.expiresAt == j && loginSessionProof.card.equals(str) && hasLocalSession();
    }

    public long getExpireTime() {
        return this.expireTime;
    }

    public String getExpireTimeStr() {
        return this.expireTimeStr;
    }

    private static String resolveFailureCodeForDisplay(String str, int i) {
        return ("WIG_AUTHORIZATION_EXPIRED".equals(str) || "WIG_AUTHORIZATION_DENIED".equals(str)) ? str : GatewayFailurePolicy.customerCode(str, i);
    }

    public void clearSavedCard() {
        synchronized (this.networkLock) {
            this.prefs.edit().remove(KEY_CARD).apply();
            this.sessionProof = null;
            this.isLoggedIn = false;
        }
    }

    private static void sleepBeforeRetry(int i) throws InterruptedException {
        try {
            Thread.sleep(GatewayFailurePolicy.retryDelayMs(i, Math.random()));
        } catch (InterruptedException unused) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean waitBeforeLoginRetry(final long j, int i) {
        return CancellableRetryDelay.await(this.networkLock, GatewayFailurePolicy.retryDelayMs(i, Math.random()), new BooleanSupplier() { // from class: com.android.support.WigVerify$$ExternalSyntheticLambda5
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return this.f$0.m1637lambda$waitBeforeLoginRetry$2$comandroidsupportWigVerify(j);
            }
        });
    }

    /* renamed from: lambda$waitBeforeLoginRetry$2$com-android-support-WigVerify, reason: not valid java name */
    /* synthetic */ boolean m1637lambda$waitBeforeLoginRetry$2$comandroidsupportWigVerify(long j) {
        return j == this.networkGeneration;
    }

    private static boolean loginBudgetExhausted(long j) {
        return loginBudgetExhausted(j, SystemClock.elapsedRealtime());
    }

    boolean isNetworkOperationCurrent(long j) {
        boolean z;
        synchronized (this.networkLock) {
            z = j == this.networkGeneration;
        }
        return z;
    }

    private boolean clearSessionForOperation(long j) {
        synchronized (this.networkLock) {
            if (j != this.networkGeneration) {
                return false;
            }
            clearSessionLocked();
            return true;
        }
    }

    /*  JADX ERROR: NullPointerException in pass: BlockProcessor
        java.lang.NullPointerException: Cannot invoke "jadx.core.dex.nodes.BlockNode.getPredecessors()" because "to" is null
        	at jadx.core.dex.visitors.blocks.BlockSplitter.removeConnection(BlockSplitter.java:166)
        	at jadx.core.dex.visitors.blocks.BlockExceptionHandler.removeExcHandler(BlockExceptionHandler.java:324)
        	at jadx.core.dex.visitors.blocks.BlockExceptionHandler.lambda$prepareTryBlocks$2(BlockExceptionHandler.java:207)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.blocks.BlockExceptionHandler.prepareTryBlocks(BlockExceptionHandler.java:207)
        	at jadx.core.dex.visitors.blocks.BlockExceptionHandler.process(BlockExceptionHandler.java:60)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.independentBlockTreeMod(BlockProcessor.java:372)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:56)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:49)
        */
    /* JADX WARN: Unreachable blocks removed: 34, instructions: 83 */
    public void login(java.lang.String r14, com.android.support.WigVerify.VerifyCallback r15) {
        /*
            r13 = this;
            if (r15 == 0) goto Lbd
            long r0 = r13.ensureLocalTestSession()
            java.lang.String r2 = "zpcnb666"
            r15.onSuccess(r2, r0)
            return
            boolean r0 = com.android.support.LoginSessionProof.validCard(r14)
            r1 = 0
            if (r0 != 0) goto L25
            java.lang.String r14 = "input_invalid"
            r13.forceLogout(r14)
            java.lang.String r13 = "INPUT_INVALID"
            java.lang.String r13 = com.android.support.VerificationUserMessage.forCode(r13, r1)
            java.lang.String r14 = "INPUT_INVALID"
            r15.onFailed(r13, r14)
            return
        L25:
            if (r14 == 0) goto L40
            java.lang.String r0 = r13.getSavedCard()
            boolean r0 = r14.equals(r0)
            if (r0 == 0) goto L40
            boolean r0 = r13.isSessionComplete()
            if (r0 == 0) goto L40
            java.lang.String r14 = "本地会话有效"
            long r0 = r13.expireTime
            r15.onSuccess(r14, r0)
            return
        L40:
            com.android.support.VerificationTrace r5 = com.android.support.VerificationTrace.start()
            long r6 = android.os.SystemClock.elapsedRealtime()
            com.android.support.VerificationTrace$Stage r0 = com.android.support.VerificationTrace.Stage.INPUT
            java.lang.String r2 = "START"
            r3 = 0
            r5.event(r0, r2, r3)
            java.lang.Object r2 = r13.networkLock
            monitor-enter(r2)
            if (r14 == 0) goto L71
            java.lang.String r0 = r13.inFlightCard     // Catch: java.lang.Throwable -> Lb9
            boolean r0 = r14.equals(r0)     // Catch: java.lang.Throwable -> Lb9
            if (r0 == 0) goto L71
            com.android.support.WigVerify$VerifyCallback r14 = r13.activeLoginOwner     // Catch: java.lang.Throwable -> Lb9
            if (r14 == r15) goto L6f
            java.util.List<com.android.support.WigVerify$VerifyCallback> r14 = r13.loginWaiters     // Catch: java.lang.Throwable -> Lb9
            boolean r14 = r14.contains(r15)     // Catch: java.lang.Throwable -> Lb9
            if (r14 != 0) goto L6f
            java.util.List<com.android.support.WigVerify$VerifyCallback> r13 = r13.loginWaiters     // Catch: java.lang.Throwable -> Lb9
            r13.add(r15)     // Catch: java.lang.Throwable -> Lb9
        L6f:
            monitor-exit(r2)     // Catch: java.lang.Throwable -> Lb9
            return
        L71:
            java.util.List<com.android.support.WigVerify$VerifyCallback> r0 = r13.loginWaiters     // Catch: java.lang.Throwable -> Lb9
            r0.clear()     // Catch: java.lang.Throwable -> Lb9
            long r3 = r13.networkGeneration     // Catch: java.lang.Throwable -> Lb9
            r8 = 1
            long r3 = r3 + r8
            r13.networkGeneration = r3     // Catch: java.lang.Throwable -> Lb9
            r13.cancelGatewayRenewalLocked()     // Catch: java.lang.Throwable -> Lb9
            long r9 = r13.networkGeneration     // Catch: java.lang.Throwable -> Lb9
            r13.activeLoginOwner = r15     // Catch: java.lang.Throwable -> Lb9
            r13.inFlightCard = r14     // Catch: java.lang.Throwable -> Lb9
            java.lang.Object r15 = r13.networkLock     // Catch: java.lang.Throwable -> Lb9
            r15.notifyAll()     // Catch: java.lang.Throwable -> Lb9
            com.android.support.JccGatewayClient$Cancellation r15 = r13.activeNetworkRequest     // Catch: java.lang.Throwable -> Lb9
            if (r15 == 0) goto L94
            r15.cancel()     // Catch: java.lang.Throwable -> Lb9
            r13.activeNetworkRequest = r1     // Catch: java.lang.Throwable -> Lb9
        L94:
            android.content.Context r15 = r13.context     // Catch: java.lang.Throwable -> Lb9
            com.android.support.OnlineStats r15 = com.android.support.OnlineStats.getInstance(r15)     // Catch: java.lang.Throwable -> Lb9
            r15.suspendForAuthentication()     // Catch: java.lang.Throwable -> Lb9
            monitor-exit(r2)     // Catch: java.lang.Throwable -> Lb9
            com.android.support.WigVerify$1 r2 = new com.android.support.WigVerify$1
            r3 = r13
            r7 = r6
            r6 = r5
            r4 = r9
            r9 = r14
            r2.<init>()
            r11 = r4
            r4 = r9
            r9 = r11
            r5 = r6
            r6 = r7
            java.util.concurrent.ExecutorService r13 = r3.loginExecutor
            r8 = r2
            com.android.support.WigVerify$$ExternalSyntheticLambda1 r2 = new com.android.support.WigVerify$$ExternalSyntheticLambda1
            r2.<init>()
            r13.execute(r2)
            return
        Lb9:
            r0 = move-exception
            r13 = r0
            monitor-exit(r2)     // Catch: java.lang.Throwable -> Lb9
            throw r13
        Lbd:
            java.lang.IllegalArgumentException r13 = new java.lang.IllegalArgumentException
            java.lang.String r14 = "callback must not be null"
            r13.<init>(r14)
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.WigVerify.login(java.lang.String, com.android.support.WigVerify$VerifyCallback):void");
    }

    /*  JADX ERROR: Type inference failed
        jadx.core.utils.exceptions.JadxOverflowException: Type inference error: updates count limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:77)
        */
    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    /* JADX WARN: Unreachable blocks removed: 2, instructions: 5 */
    /* renamed from: lambda$login$3$com-android-support-WigVerify, reason: not valid java name */
    /* synthetic */ void m1634lambda$login$3$comandroidsupportWigVerify(java.lang.String r29, com.android.support.VerificationTrace r30, long r31, com.android.support.WigVerify.VerifyCallback r33, long r34) {
        /*
            Method dump skipped, instructions count: 2994
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.WigVerify.m1634lambda$login$3$comandroidsupportWigVerify(java.lang.String, com.android.support.VerificationTrace, long, com.android.support.WigVerify$VerifyCallback, long):void");
    }

    private static String safeTraceCode(String str) {
        return VerificationTrace.isAllowedCode(str) ? str : "UNKNOWN";
    }

    private void releaseInFlightLocked() {
        this.inFlightCard = null;
        this.loginWaiters.clear();
    }

    void cancelPendingLogin(VerifyCallback verifyCallback) {
        synchronized (this.networkLock) {
            if (verifyCallback == null) {
                return;
            }
            VerifyCallback verifyCallback2 = this.activeLoginOwner;
            List<VerifyCallback> list = this.loginWaiters;
            if (verifyCallback2 != verifyCallback) {
                list.remove(verifyCallback);
                return;
            }
            if (!list.isEmpty()) {
                this.activeLoginOwner = this.loginWaiters.remove(0);
                return;
            }
            this.activeLoginOwner = null;
            this.networkGeneration++;
            releaseInFlightLocked();
            this.networkLock.notifyAll();
            JccGatewayClient.Cancellation cancellation = this.activeNetworkRequest;
            if (cancellation != null) {
                cancellation.cancel();
                this.activeNetworkRequest = null;
            }
        }
    }

    void cancelPendingLogin() {
        synchronized (this.networkLock) {
            this.activeLoginOwner = null;
            this.networkGeneration++;
            releaseInFlightLocked();
            this.networkLock.notifyAll();
            JccGatewayClient.Cancellation cancellation = this.activeNetworkRequest;
            if (cancellation != null) {
                cancellation.cancel();
                this.activeNetworkRequest = null;
            }
        }
    }

    private static void runtimeRecord(String str, String str2, String str3, String str4) {
        RuntimeDiagnostics runtimeDiagnostics = RuntimeDiagnostics.get();
        if (runtimeDiagnostics != null) {
            runtimeDiagnostics.record(str, str2, str3, str4);
        }
    }

    public void heartbeat(VerifyCallback verifyCallback) {
        if (isLoggedIn()) {
            verifyCallback.onSuccess("在线", this.expireTime);
        } else {
            verifyCallback.onFailed(VerificationUserMessage.forCode("GATEWAY_SESSION", null), "GATEWAY_SESSION");
        }
    }

    public void logout() {
        final String strBearerTokenNow;
        synchronized (this.networkLock) {
            this.networkGeneration++;
            releaseInFlightLocked();
            this.networkLock.notifyAll();
            JccGatewayClient.Cancellation cancellation = this.activeNetworkRequest;
            if (cancellation != null) {
                cancellation.cancel();
                this.activeNetworkRequest = null;
            }
            strBearerTokenNow = JccSessionState.shared().bearerTokenNow();
            clearSessionLocked();
        }
        if (strBearerTokenNow != null) {
            this.loginExecutor.execute(new Runnable() { // from class: com.android.support.WigVerify$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() throws IOException {
                    JccGatewayClient.shared().logout(strBearerTokenNow, new JccGatewayClient.Cancellation());
                }
            });
        }
    }

    public void forceLogout(String str) {
        synchronized (this.networkLock) {
            this.networkGeneration++;
            releaseInFlightLocked();
            this.networkLock.notifyAll();
            JccGatewayClient.Cancellation cancellation = this.activeNetworkRequest;
            if (cancellation != null) {
                cancellation.cancel();
                this.activeNetworkRequest = null;
            }
            clearSessionLocked();
        }
    }

    private void clearSessionLocked() {
        cancelGatewayRenewalLocked();
        this.sessionProof = null;
        OnlineStats.getInstance(this.context).suspendForAuthentication();
        JccSessionState.shared().clearAll();
        this.gatewaySessionGeneration = 0L;
        this.isLoggedIn = false;
        this.expireTime = 0L;
        this.expireTimeStr = "";
        this.sessionUserTime = "";
        NativeTrustBridge.getInstance().clear();
    }

    public void checkVersionAndNotice(final VersionCheckCallback versionCheckCallback) {
        if (versionCheckCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        this.executor.execute(new Runnable() { // from class: com.android.support.WigVerify$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                WigVerify.lambda$checkVersionAndNotice$5(versionCheckCallback);
            }
        });
    }

    public static String versionLabel(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return Character.isDigit(str.charAt(0)) ? "v" + str : str;
    }

    public String getCurrentVersionName() {
        if (NativeLibraryLoader.isLoaded()) {
            try {
                return nativeGetVersionName();
            } catch (Throwable unused) {
            }
        }
        try {
            return this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionName;
        } catch (Exception unused2) {
            return "1.0";
        }
    }

    public int getCurrentVersionCode() {
        if (!NativeLibraryLoader.isLoaded()) {
            return 1;
        }
        try {
            return nativeGetVersionCode();
        } catch (Throwable unused) {
            return 1;
        }
    }

    public String getDeviceId() {
        return this.imei;
    }

    public static String getHostUrl() {
        throw new UnsupportedOperationException("direct WIG networking has been removed");
    }

    public static String getAppId() {
        throw new UnsupportedOperationException("direct WIG networking has been removed");
    }
}
