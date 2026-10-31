package com.android.support;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import com.android.support.ExistingRuntimeRecovery;
import com.android.support.GameDataLocation;
import com.android.support.GameLaunchGuard;
import com.android.support.InjectionTargetReadiness;
import com.android.support.LatestOwnerListener;
import com.android.support.MainConnectionUiState;
import com.android.support.OverlayPermissionDialog;
import com.android.support.OverlayPermissionGuidance;
import com.android.support.ProcessOutputCollector;
import com.android.support.SelfServiceUnbindClient;
import com.android.support.TestNetworkRuleDialog;
import com.android.support.XfOnlineCount;
import com.android.support.databinding.ActivityMainBinding;
import com.android.support.trust.NativeTrustBridge;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity {
    private static final boolean APPLY_SELINUX_PERMISSIVE = false;
    private static final String EARLY_LAUNCH_IDENTITY_KEY = "early_launch_process_identity_v1";
    private static final int EMULATOR_NATIVE_BRIDGE_DELAY_MICROS = 3000000;
    private static final int EXISTING_RUNTIME_RECONNECT_GRACE_MS = 35000;
    private static final int EXISTING_RUNTIME_RECONNECT_POLL_MS = 250;
    private static final int GAME_DATA_PROBE_TIMEOUT_MS = 8000;
    private static final int GAME_READY_POLL_MS = 300;
    private static final int GAME_READY_TIMEOUT_MS = 180000;
    private static final int GAME_READY_TIMEOUT_MS_EMULATOR = 180000;
    private static final int GAME_STABLE_WINDOW_MS = 3000;
    private static final int GAME_STABLE_WINDOW_MS_EMULATOR = 6000;
    private static final long LAUNCH_MODULE_TIMEOUT_MS = 120000;
    private static final String LIB_NAME = "lib10edb57537.so";
    private static final String LIB_NAME_COMPAT = "lib30ae545382.so";
    private static final String LOADER_NAME = "libfd4baf57ce.so";
    private static final String LOADER_NAME_X86 = "lib7ec075c9d5.so";
    private static final long OVERLAY_WATCH_INTERVAL_MS = 1200;
    private static final int REQUEST_CODE_MANAGE_STORAGE = 1003;
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1001;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1002;
    private static final long ROOT_IDENTITY_PROBE_TIMEOUT_MILLIS = 12000;
    private static final String RUNTIME_IDENTITY_KEY = "target_process_identity";
    private static final String RUNTIME_STAGING_DIR = "pkf473104974";
    private static final String RUNTIME_STATE_PREFS = "injection_runtime_state";
    private static final String SHORTCUT_ACCESSIBILITY_COMPONENT = "com.android.support/com.android.support.EmulatorShortcutService";
    private static final int SHORTCUT_BIND_POLL_MS = 100;
    private static final int SHORTCUT_BIND_TIMEOUT_MS = 8000;
    private static final int SHORTCUT_REMOVAL_STABLE_SAMPLES = 3;
    private static final String SUPPORT_LIB_NAME = "lib494bfe9777.so";
    private static final String TAG = "InjectApp";
    private static final int TARGET_INTERACTIVE_POLL_MS = 100;
    private static final int TARGET_INTERACTIVE_STABLE_SAMPLES = 2;
    private static final int TARGET_INTERACTIVE_TIMEOUT_MS = 12000;
    private static final long VERIFY_WINDOW_GRACE_MS = 12000;
    private ActivityMainBinding binding;
    private Runnable blinkStatusRunnable;
    private volatile String bundledKsudPath;
    private DrawerLayout drawerLayout;
    private GameRegionSelector gameRegionSelector;
    private Boolean lastBackgroundAllowed;
    private volatile String lastInjectFailureReason;
    private Boolean lastOverlayGranted;
    private MainAnnouncementController mainAnnouncementController;
    private MainScreenInteraction mainScreenInteraction;
    private LatestOwnerListener.Token onlineCountToken;
    private View onlineIndicator;
    private Runnable onlineIndicatorRunnable;
    private AlertDialog overlayGuidanceDialog;
    private Runnable overlayWatchRunnable;
    private volatile long ownedInjectionAttempt;
    private SharedMemManager shmManager;
    private volatile boolean suPrefixResolved;
    private TestNetworkRuleDialog testNetworkRuleDialog;
    private Runnable timerRunnable;
    private TextView tvOnlineCount;
    private XfOnlineCount xfOnlineCount;
    private static final Pattern MEMFD_NAME_PATTERN = Pattern.compile("memfd_rand\\(\\d+\\)\\s*=\\s*([A-Za-z0-9._-]{5,64})\\.");
    private static final Pattern SAFE_MEMFD_NAME_PATTERN = Pattern.compile("[A-Za-z0-9._-]{5,64}");
    private static final InjectionPropertyCoordinator INJECTION_PROPERTIES = new InjectionPropertyCoordinator();
    private static final InjectionAttemptLifecycle injectionLifecycle = new InjectionAttemptLifecycle();
    private final ExecutorService selfServiceUnbindExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda51
        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            return MainActivity.lambda$new$0(runnable);
        }
    });
    private final AtomicBoolean selfServiceUnbindInFlight = new AtomicBoolean(false);
    private final SelfServiceUnbindClient selfServiceUnbindClient = new SelfServiceUnbindClient();
    private String TARGET_PACKAGE = GameRegionState.current().packageName;
    private String TARGET_ACTIVITY = GameRegionState.current().launchActivity;
    private boolean rootStatus = false;
    private volatile String suBinaryPath = "";
    private final AtomicBoolean suPathCheckInFlight = new AtomicBoolean(false);
    private volatile String rootMountNamespaceFile = null;
    private volatile GameDataLocation.Decision lastGameDataDecision = null;
    private volatile boolean isCompatMode = false;
    private volatile String selectedRuntimeFileName = LIB_NAME;
    private Boolean cachedIsEmulator = null;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Handler handler = new Handler(Looper.getMainLooper());
    private final AtomicBoolean foregroundHandoffInFlight = new AtomicBoolean(false);
    private volatile boolean gameForegroundHandoffCompleted = false;
    private final AtomicBoolean automaticRecoveryStarted = new AtomicBoolean(false);
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private final GradientDrawable statusBlinkDrawable = new GradientDrawable();
    private volatile int mainStatusIndicatorColor = -19372;
    private volatile String lastInjectDiagnosticsText = null;
    private volatile String lastInjectDiagnosticsFilePath = null;
    private MainConnectionUiState.Phase currentMainPhase = MainConnectionUiState.Phase.CHECKING;
    private final AtomicLong mainRenderRevision = new AtomicLong();
    private long verifyWindowExpectedSinceMs = 0;
    private OverlayPermissionGuidance.Blocker overlayGuidanceOnCard = OverlayPermissionGuidance.Blocker.NONE;
    private volatile String[] cachedSuPrefix = null;
    private final Object suPrefixLock = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    interface UnbindCompletion {
        void onComplete(SelfServiceUnbindClient.Result result, String str);
    }

    static /* synthetic */ void lambda$cleanupOldProperties$10() {
    }

    static /* synthetic */ void lambda$handleInjectResult$41() {
    }

    static /* synthetic */ void lambda$runInjectionScript$39() {
    }

    static /* synthetic */ Thread lambda$new$0(Runnable runnable) {
        Thread thread = new Thread(runnable, ThreadNaming.WORKER);
        thread.setDaemon(true);
        return thread;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SharedPreferences getSharedPreferences(String str, int i) {
        return super.getSharedPreferences(GameRegionState.preferenceName(str), i);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!NativeLibraryLoader.isLoaded()) {
            NativeStartupFailureView.show(this);
            return;
        }
        FileLogger.getInstance(this).init();
        RuntimeDiagnostics.install(this);
        runtimeRecord("APP", "ACTIVITY_CREATE", "BEGIN", "abi=" + Build.SUPPORTED_ABIS[0] + " sdk=" + Build.VERSION.SDK_INT);
        ThreadNaming.startSweeper();
        NativeTrustBridge.getInstance().warmPreLoginAsync();
        setupFullScreen();
        ActivityMainBinding activityMainBindingInflate = ActivityMainBinding.inflate(getLayoutInflater());
        this.binding = activityMainBindingInflate;
        setContentView(activityMainBindingInflate.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(this.binding.getRoot(), new OnApplyWindowInsetsListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda3
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return MainActivity.lambda$onCreate$1(view, windowInsetsCompat);
            }
        });
        ViewCompat.requestApplyInsets(this.binding.getRoot());
        this.mainScreenInteraction = new MainScreenInteraction(this.binding.getRoot(), bundle);
        this.gameRegionSelector = new GameRegionSelector(this, new BooleanSupplier() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda4
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return MainActivity.lambda$onCreate$2();
            }
        }, new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1327lambda$onCreate$3$comandroidsupportMainActivity();
            }
        });
        this.binding.cbCompatMode.setChecked(false);
        setupTechUI();
        loadGifIcon();
        initOnlineCount();
        applyMainConnectionState(MainConnectionUiState.Phase.CHECKING);
        startTimeUpdate();
        setupInjectButton();
        setupJoinChannelButton();
        setupMainAnnouncement();
        setupSuBinaryPath();
        setupSelfServiceUnbind();
        setupSidebar();
        requestStoragePermission();
        cleanupOldProperties();
        checkRootAccess();
        this.binding.tvRootDetail.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1328lambda$onCreate$4$comandroidsupportMainActivity(view);
            }
        });
        this.binding.tvRootDetail.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return this.f$0.m1329lambda$onCreate$5$comandroidsupportMainActivity(view);
            }
        });
    }

    static /* synthetic */ WindowInsetsCompat lambda$onCreate$1(View view, WindowInsetsCompat windowInsetsCompat) {
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
        view.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        return windowInsetsCompat;
    }

    static /* synthetic */ boolean lambda$onCreate$2() {
        return !injectionLifecycle.isRunning();
    }

    /* renamed from: lambda$onCreate$3$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1327lambda$onCreate$3$comandroidsupportMainActivity() {
        this.TARGET_PACKAGE = GameRegionState.current().packageName;
        this.TARGET_ACTIVITY = GameRegionState.current().launchActivity;
        initSharedMemory();
    }

    /* renamed from: lambda$onCreate$4$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1328lambda$onCreate$4$comandroidsupportMainActivity(View view) {
        if (this.lastInjectDiagnosticsText == null || this.lastInjectDiagnosticsText.trim().isEmpty()) {
            return;
        }
        copyToClipboard("inject_diagnostics", this.lastInjectDiagnosticsText);
        showToast("诊断已复制，可直接粘贴发送");
    }

    /* renamed from: lambda$onCreate$5$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1329lambda$onCreate$5$comandroidsupportMainActivity(View view) {
        if (this.lastInjectDiagnosticsFilePath == null || this.lastInjectDiagnosticsFilePath.trim().isEmpty()) {
            return false;
        }
        copyToClipboard("inject_diag_path", this.lastInjectDiagnosticsFilePath);
        showToast("诊断路径已复制");
        return true;
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (NativeLibraryLoader.isLoaded()) {
            MainAnnouncementController mainAnnouncementController = this.mainAnnouncementController;
            if (mainAnnouncementController != null) {
                mainAnnouncementController.refresh();
            }
            XfOnlineCount xfOnlineCount = this.xfOnlineCount;
            if (xfOnlineCount != null) {
                xfOnlineCount.start(this);
            }
            startOverlayVisibilityWatch();
            GameRegionSelector gameRegionSelector = this.gameRegionSelector;
            if (gameRegionSelector != null) {
                GameRegionControl.subscribe(gameRegionSelector);
            }
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        setMainDecorativeAnimationsActive(z);
    }

    private void initOnlineCount() {
        this.tvOnlineCount = (TextView) findViewById(R.id.tvOnlineCount);
        this.onlineIndicator = findViewById(R.id.onlineIndicator);
        this.onlineIndicatorRunnable = new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1325lambda$initOnlineCount$6$comandroidsupportMainActivity();
            }
        };
        final XfOnlineCount xfOnlineCountShared = XfOnlineCount.shared();
        this.xfOnlineCount = xfOnlineCountShared;
        this.onlineCountToken = xfOnlineCountShared.registerListener(this, new XfOnlineCount.Listener() { // from class: com.android.support.MainActivity.1
            @Override // com.android.support.XfOnlineCount.Listener
            public void onOnlineCount(int i) {
                MainActivity.this.renderOnlineCount(i, true);
            }

            @Override // com.android.support.XfOnlineCount.Listener
            public void onOnlineCountUnavailable() {
                MainActivity.this.renderOnlineCount(xfOnlineCountShared.cachedCount(), false);
            }
        });
        renderOnlineCount(xfOnlineCountShared.cachedCount(), false);
        xfOnlineCountShared.start(this);
    }

    /* renamed from: lambda$initOnlineCount$6$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1325lambda$initOnlineCount$6$comandroidsupportMainActivity() {
        View view = this.onlineIndicator;
        if (view != null) {
            view.setAlpha(1.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderOnlineCount(int i, boolean z) {
        if (this.tvOnlineCount == null || isFinishing() || isDestroyed()) {
            return;
        }
        int color = ContextCompat.getColor(this, z ? R.color.ds_ok : R.color.ds_text_3);
        this.tvOnlineCount.setText(i >= 0 ? i + " 全服在线" : "-- 全服在线");
        this.tvOnlineCount.setTextColor(color);
        View view = this.onlineIndicator;
        if (view == null) {
            return;
        }
        view.setBackgroundTintList(ColorStateList.valueOf(color));
        this.handler.removeCallbacks(this.onlineIndicatorRunnable);
        View view2 = this.onlineIndicator;
        if (!z) {
            view2.setAlpha(1.0f);
        } else {
            view2.setAlpha(0.3f);
            this.handler.postDelayed(this.onlineIndicatorRunnable, 240L);
        }
    }

    private void initSharedMemory() {
        this.shmManager = SharedMemManager.getInstance(this);
        if (GameRegionState.current().isRuntimeReady()) {
            if (this.shmManager.init()) {
                log("LocalSocket IPC 初始化成功: " + this.shmManager.getSharedFilePath());
            } else {
                log("LocalSocket IPC 初始化失败");
                showToast("LocalSocket IPC 初始化失败");
            }
        }
    }

    private void requestStoragePermission() {
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                if (Environment.isExternalStorageManager()) {
                    log("存储权限已授予 (MANAGE_EXTERNAL_STORAGE)");
                } else {
                    log("存储权限待 Root 自动授予 (MANAGE_EXTERNAL_STORAGE)...");
                }
            } else if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
                log("存储权限已授予");
            } else {
                log("存储权限待 Root 自动授予...");
            }
        } catch (Exception e) {
            log("存储权限检查异常: " + e.getMessage());
        }
        initSharedMemory();
    }

    private boolean hasStoragePermission() {
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                return Environment.isExternalStorageManager();
            }
            return ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0;
        } catch (Exception e) {
            log("hasStoragePermission 异常: " + e.getMessage());
            return false;
        }
    }

    private void requestManageStoragePermission() {
        try {
            if (Build.VERSION.SDK_INT < 30 || Environment.isExternalStorageManager()) {
                return;
            }
            log("跳转系统设置授予 MANAGE_EXTERNAL_STORAGE...");
            Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1003);
        } catch (Exception e) {
            log("跳转 MANAGE_EXTERNAL_STORAGE 设置页失败: " + e.getMessage());
            try {
                startActivityForResult(new Intent("android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION"), 1003);
            } catch (Exception e2) {
                log("跳转通用设置页也失败: " + e2.getMessage());
                FloatingWindowService.safeToast(this, "请手动在设置中授予文件管理权限", 1);
            }
        }
    }

    private void autoGrantPermissionsViaRoot() {
        new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda44
            @Override // java.lang.Runnable
            public final void run() throws InterruptedException {
                this.f$0.m1314x3f84b643();
            }
        }).start();
    }

    /* renamed from: lambda$autoGrantPermissionsViaRoot$9$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1314x3f84b643() throws InterruptedException {
        boolean zIsExternalStorageManager;
        String packageName = getPackageName();
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                log("Android 13+: 跳过 READ/WRITE_EXTERNAL_STORAGE（已废弃）");
            } else if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0 || ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                log("Root 自动授予存储权限...");
                execCommand("pm grant " + packageName + " android.permission.READ_EXTERNAL_STORAGE");
                execCommand("pm grant " + packageName + " android.permission.WRITE_EXTERNAL_STORAGE");
                log("Root 自动授予存储权限命令已执行");
            }
            if (Build.VERSION.SDK_INT >= 30) {
                try {
                    zIsExternalStorageManager = Environment.isExternalStorageManager();
                } catch (Exception e) {
                    log("检查 MANAGE_EXTERNAL_STORAGE 异常: " + e.getMessage());
                    zIsExternalStorageManager = false;
                }
                if (!zIsExternalStorageManager) {
                    log("Root 自动授予 MANAGE_EXTERNAL_STORAGE...");
                    execCommand("appops set --uid " + packageName + " MANAGE_EXTERNAL_STORAGE allow");
                    execCommand("appops set " + packageName + " MANAGE_EXTERNAL_STORAGE allow 2>/dev/null");
                    log("Root 自动授予 MANAGE_EXTERNAL_STORAGE 命令已执行");
                }
            }
            if (!Settings.canDrawOverlays(this)) {
                log("Root 自动授予悬浮窗权限...");
                grantOverlayPermissionViaRoot();
                log("Root 自动授予悬浮窗权限命令已执行");
            }
            if (!isIgnoringBatteryOptimizations()) {
                log("Root 自动加入电池优化白名单（后台运行）...");
                execCommand("dumpsys deviceidle whitelist +" + packageName);
                execCommand("appops set " + packageName + " RUN_IN_BACKGROUND allow 2>/dev/null");
                execCommand("appops set " + packageName + " RUN_ANY_IN_BACKGROUND allow 2>/dev/null");
                execCommand("cmd appops set " + packageName + " RUN_IN_BACKGROUND allow 2>/dev/null");
                execCommand("cmd appops set " + packageName + " RUN_ANY_IN_BACKGROUND allow 2>/dev/null");
                log("Root 自动授予后台运行命令已执行");
            }
            if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != 0) {
                log("Root 自动授予通知权限...");
                execCommand("pm grant " + packageName + " android.permission.POST_NOTIFICATIONS");
            }
            Thread.sleep(500L);
            runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1312xc5452e05();
                }
            });
        } catch (Exception e2) {
            log("Root 自动授权异常: " + e2.getMessage());
            runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1313x264f224();
                }
            });
        }
    }

    /* renamed from: lambda$autoGrantPermissionsViaRoot$7$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1312xc5452e05() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (hasStoragePermission()) {
            log("✓ 存储权限已通过 Root 自动授予");
        } else {
            log("⚠ Root 自动授予存储权限未生效，尝试兜底方案");
            fallbackRequestStoragePermission();
        }
        boolean zCanDrawOverlays = Settings.canDrawOverlays(this);
        if (zCanDrawOverlays) {
            log("✓ 悬浮窗权限已通过 Root 自动授予");
        } else {
            log("⚠ Root 自动授予悬浮窗权限未生效");
        }
        boolean zIsIgnoringBatteryOptimizations = isIgnoringBatteryOptimizations();
        if (zIsIgnoringBatteryOptimizations) {
            log("✓ 后台运行（电池白名单）已通过 Root 自动授予");
        } else {
            log("⚠ Root 自动授予后台运行未生效");
        }
        if (!zCanDrawOverlays) {
            showOverlayGuidance(OverlayPermissionGuidance.Blocker.PERMISSION_MISSING, false);
        } else if (!zIsIgnoringBatteryOptimizations) {
            FloatingWindowService.safeToast(this, "「后台运行」未开启：系统可能随时收回记牌器的后台，窗口容易掉线。请在 设置-电池/省电 里把记牌器设为「不受限制」", 1);
        }
        this.lastOverlayGranted = Boolean.valueOf(zCanDrawOverlays);
        this.lastBackgroundAllowed = Boolean.valueOf(zIsIgnoringBatteryOptimizations);
        refreshEnvBar(this.currentMainPhase);
    }

    /* renamed from: lambda$autoGrantPermissionsViaRoot$8$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1313x264f224() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        fallbackRequestStoragePermission();
    }

    private void fallbackRequestStoragePermission() {
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                requestManageStoragePermission();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 1002);
            }
        } catch (Exception e) {
            log("兜底存储权限请求异常: " + e.getMessage());
            FloatingWindowService.safeToast(this, "请在系统设置中手动授予存储权限", 1);
        }
    }

    private void cleanupOldProperties() {
        final long jBeginLease = INJECTION_PROPERTIES.beginLease();
        new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1317lambda$cleanupOldProperties$11$comandroidsupportMainActivity(jBeginLease);
            }
        }, "Injection-Property-Cleanup").start();
    }

    /* renamed from: lambda$cleanupOldProperties$11$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1317lambda$cleanupOldProperties$11$comandroidsupportMainActivity(long j) {
        try {
            log(INJECTION_PROPERTIES.runIfCurrent(j, new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda41
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.lambda$cleanupOldProperties$10();
                }
            }) ? "已跳过废弃属性清理" : "旧属性清理已被新连接替换");
        } catch (Exception e) {
            log("清理旧属性失败: " + e.getMessage());
        }
    }

    private void setupFullScreen() {
        getWindow().clearFlags(1536);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        insetsController.show(WindowInsetsCompat.Type.systemBars());
        insetsController.setAppearanceLightStatusBars(true);
        insetsController.setAppearanceLightNavigationBars(true);
    }

    private void setupTechUI() {
        startBlinkAnimation(this.binding.statusIndicator);
        playMainEntranceAnimation();
    }

    private void playMainEntranceAnimation() {
        ImageView imageView = this.binding.ivGifIcon;
        if (imageView != null) {
            imageView.setScaleX(0.3f);
            imageView.setScaleY(0.3f);
            imageView.setAlpha(0.0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(imageView, "alpha", 0.0f, 1.0f).setDuration(400L), ObjectAnimator.ofFloat(imageView, "scaleX", 0.3f, 1.0f).setDuration(600L), ObjectAnimator.ofFloat(imageView, "scaleY", 0.3f, 1.0f).setDuration(600L));
            animatorSet.setInterpolator(new OvershootInterpolator(1.3f));
            animatorSet.setStartDelay(100L);
            animatorSet.start();
        }
        TextView textView = this.binding.tvTimestamp;
        if (textView != null) {
            textView.setAlpha(0.0f);
            textView.setTranslationY(15.0f);
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playTogether(ObjectAnimator.ofFloat(textView, "alpha", 0.0f, 1.0f).setDuration(500L), ObjectAnimator.ofFloat(textView, "translationY", 15.0f, 0.0f).setDuration(500L));
            animatorSet2.setInterpolator(new DecelerateInterpolator(1.5f));
            animatorSet2.setStartDelay(300L);
            animatorSet2.start();
        }
        Button button = this.binding.btnInject;
        if (button != null) {
            button.setAlpha(0.0f);
            button.setTranslationY(25.0f);
            AnimatorSet animatorSet3 = new AnimatorSet();
            animatorSet3.playTogether(ObjectAnimator.ofFloat(button, "alpha", 0.0f, 1.0f).setDuration(500L), ObjectAnimator.ofFloat(button, "translationY", 25.0f, 0.0f).setDuration(600L));
            animatorSet3.setInterpolator(new DecelerateInterpolator(1.5f));
            animatorSet3.setStartDelay(600L);
            animatorSet3.start();
        }
        View viewFindViewById = findViewById(R.id.mainFooterDock);
        if (viewFindViewById != null) {
            viewFindViewById.setAlpha(0.0f);
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(viewFindViewById, "alpha", 0.0f, 1.0f);
            objectAnimatorOfFloat.setDuration(500L);
            objectAnimatorOfFloat.setStartDelay(800L);
            objectAnimatorOfFloat.start();
        }
    }

    private void loadGifIcon() {
        ImageView imageView = (ImageView) findViewById(R.id.ivGifIcon);
        if (imageView != null) {
            try {
                Glide.with((FragmentActivity) this).asBitmap().load(Integer.valueOf(R.raw.gift2)).diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView);
                log("GIF 图标加载成功");
            } catch (Exception e) {
                log("GIF 图标加载失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void startBlinkAnimation(View view) {
        if (view == null) {
            return;
        }
        this.statusBlinkDrawable.setShape(1);
        this.statusBlinkDrawable.setColor(this.mainStatusIndicatorColor);
        view.setBackground(this.statusBlinkDrawable);
        Runnable runnable = this.blinkStatusRunnable;
        if (runnable != null) {
            view.removeCallbacks(runnable);
        }
        final WeakReference weakReference = new WeakReference(view);
        Runnable runnable2 = new Runnable() { // from class: com.android.support.MainActivity.2
            int toggle = 0;

            @Override // java.lang.Runnable
            public void run() {
                View view2 = (View) weakReference.get();
                if (view2 == null || MainActivity.this.isFinishing() || MainActivity.this.isDestroyed()) {
                    return;
                }
                MainActivity.this.statusBlinkDrawable.setColor(MainActivity.this.mainStatusIndicatorColor);
                view2.setBackground(MainActivity.this.statusBlinkDrawable);
                view2.setAlpha(this.toggle % 2 == 0 ? 1.0f : 0.45f);
                this.toggle++;
                view2.postDelayed(this, 500L);
            }
        };
        this.blinkStatusRunnable = runnable2;
        view.post(runnable2);
    }

    private void setMainDecorativeAnimationsActive(boolean z) {
        ActivityMainBinding activityMainBinding = this.binding;
        if (activityMainBinding == null) {
            return;
        }
        if (z) {
            startBlinkAnimation(activityMainBinding.statusIndicator);
            startTimeUpdate();
            return;
        }
        Runnable runnable = this.timerRunnable;
        if (runnable != null) {
            this.timerHandler.removeCallbacks(runnable);
        }
        if (this.blinkStatusRunnable != null && this.binding.statusIndicator != null) {
            this.binding.statusIndicator.removeCallbacks(this.blinkStatusRunnable);
            this.binding.statusIndicator.setAlpha(1.0f);
        }
        Runnable runnable2 = this.onlineIndicatorRunnable;
        if (runnable2 != null) {
            this.handler.removeCallbacks(runnable2);
            View view = this.onlineIndicator;
            if (view != null) {
                view.setAlpha(1.0f);
            }
        }
    }

    private void setMainStatusIndicatorColor(int i) {
        this.mainStatusIndicatorColor = i;
        ActivityMainBinding activityMainBinding = this.binding;
        if (activityMainBinding == null || activityMainBinding.statusIndicator == null) {
            return;
        }
        this.statusBlinkDrawable.setColor(i);
        this.binding.statusIndicator.setBackground(this.statusBlinkDrawable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyMainConnectionState(final MainConnectionUiState.Phase phase) {
        final long jIncrementAndGet = this.mainRenderRevision.incrementAndGet();
        Runnable runnable = new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda62
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1307xfeb2def8(jIncrementAndGet, phase);
            }
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            runOnUiThread(runnable);
        }
    }

    /* renamed from: lambda$applyMainConnectionState$12$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1307xfeb2def8(long j, MainConnectionUiState.Phase phase) {
        if (j != this.mainRenderRevision.get() || this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        if (phase == MainConnectionUiState.Phase.CONNECTING && !injectionLifecycle.isRunning() && !isRuntimeConnected()) {
            phase = MainConnectionUiState.Phase.RETRY;
        }
        MainConnectionUiState.Presentation presentationForPhase = MainConnectionUiState.forPhase(phase);
        this.currentMainPhase = phase;
        setMainStatusIndicatorColor(presentationForPhase.color());
        this.binding.tvRootStatus.setText(presentationForPhase.status());
        this.binding.tvRootStatus.setTextColor(getColor(R.color.ds_text_1));
        this.binding.tvFooter.setText(presentationForPhase.footer());
        this.binding.tvFooter.setTextColor(getColor(R.color.ds_text_2));
        this.binding.loadingProgress.setIndeterminate(presentationForPhase.busy());
        this.binding.loadingProgress.setProgress(presentationForPhase.progress());
        this.binding.loadingProgress.setVisibility((presentationForPhase.busy() || phase == MainConnectionUiState.Phase.RECOVERING) ? 0 : 8);
        this.binding.btnInject.setText(presentationForPhase.actionLabel());
        this.binding.btnOpenControlPanel.setEnabled(isRuntimeConnected());
        this.mainScreenInteraction.render(phase);
        this.binding.btnInject.setEnabled(presentationForPhase.buttonEnabled());
        if (this.overlayGuidanceOnCard != OverlayPermissionGuidance.Blocker.NONE && phase != MainConnectionUiState.Phase.CONNECTED_OVERLAY_BLOCKED) {
            this.overlayGuidanceOnCard = OverlayPermissionGuidance.Blocker.NONE;
            this.binding.tvRootDetail.setText("");
            this.binding.tvRootDetail.setTextColor(getColor(R.color.ds_text_2));
        }
        refreshEnvBar(phase);
    }

    private void refreshEnvBar(MainConnectionUiState.Phase phase) {
        View viewFindViewById = findViewById(R.id.envRootLed);
        TextView textView = (TextView) findViewById(R.id.envRootValue);
        View viewFindViewById2 = findViewById(R.id.envFloatLed);
        TextView textView2 = (TextView) findViewById(R.id.envFloatValue);
        View viewFindViewById3 = findViewById(R.id.envBgLed);
        TextView textView3 = (TextView) findViewById(R.id.envBgValue);
        View viewFindViewById4 = findViewById(R.id.envGameLed);
        TextView textView4 = (TextView) findViewById(R.id.envGameValue);
        if (viewFindViewById == null || textView == null || viewFindViewById2 == null || textView2 == null || viewFindViewById3 == null || textView3 == null || viewFindViewById4 == null || textView4 == null) {
            return;
        }
        if (phase == MainConnectionUiState.Phase.CHECKING) {
            paintEnvRow(viewFindViewById, textView, "检查中", 1);
            paintEnvRow(viewFindViewById2, textView2, "检查中", 1);
            paintEnvRow(viewFindViewById3, textView3, "检查中", 1);
            paintEnvRow(viewFindViewById4, textView4, "未检测", 2);
            return;
        }
        boolean z = this.rootStatus;
        paintEnvRow(viewFindViewById, textView, z ? "已开启" : "未开启", z ? 0 : 2);
        boolean zCheckOverlayPermission = checkOverlayPermission();
        paintEnvRow(viewFindViewById2, textView2, zCheckOverlayPermission ? "已授权" : "未授权（必须开启）", zCheckOverlayPermission ? 0 : 3);
        boolean zIsIgnoringBatteryOptimizations = isIgnoringBatteryOptimizations();
        paintEnvRow(viewFindViewById3, textView3, zIsIgnoringBatteryOptimizations ? "已开启" : "未开启", !zIsIgnoringBatteryOptimizations ? 1 : 0);
        if (MainConnectionUiState.isConnectedPhase(phase)) {
            paintEnvRow(viewFindViewById4, textView4, "已连接", 0);
            return;
        }
        if (phase == MainConnectionUiState.Phase.CONNECTING || phase == MainConnectionUiState.Phase.RECOVERING) {
            paintEnvRow(viewFindViewById4, textView4, "连接中", 1);
            return;
        }
        if (phase == MainConnectionUiState.Phase.RETRY) {
            paintEnvRow(viewFindViewById4, textView4, "未连接", 2);
        } else if (phase == MainConnectionUiState.Phase.READY) {
            paintEnvRow(viewFindViewById4, textView4, "等待中", 2);
        } else {
            paintEnvRow(viewFindViewById4, textView4, "未检测", 2);
        }
    }

    private void paintEnvRow(View view, TextView textView, String str, int i) {
        int color;
        textView.setText(str);
        if (i == 0) {
            color = getColor(R.color.ds_ok);
        } else if (i == 1) {
            color = getColor(R.color.ds_warn);
        } else if (i == 3) {
            color = getColor(R.color.ds_danger);
        } else {
            color = getColor(R.color.ds_text_3);
        }
        textView.setTextColor(color);
        view.setBackgroundResource(R.drawable.ds_led);
        view.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void startTimeUpdate() {
        Runnable runnable = this.timerRunnable;
        if (runnable != null) {
            this.timerHandler.removeCallbacks(runnable);
        }
        Runnable runnable2 = new Runnable() { // from class: com.android.support.MainActivity.3
            @Override // java.lang.Runnable
            public void run() {
                if (MainActivity.this.binding == null || MainActivity.this.isFinishing() || MainActivity.this.isDestroyed()) {
                    return;
                }
                MainActivity.this.binding.tvTimestamp.setText(MainActivity.this.timeFormatter.format(new Date()));
                MainActivity.this.timerHandler.postDelayed(this, 1000L);
            }
        };
        this.timerRunnable = runnable2;
        this.timerHandler.post(runnable2);
    }

    private void setupInjectButton() {
        this.binding.btnInject.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda15
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1339lambda$setupInjectButton$13$comandroidsupportMainActivity(view);
            }
        });
        this.binding.btnOpenControlPanel.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda16
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1340lambda$setupInjectButton$14$comandroidsupportMainActivity(view);
            }
        });
        this.binding.btnMainHelp.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda17
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1341lambda$setupInjectButton$15$comandroidsupportMainActivity(view);
            }
        });
        View viewFindViewById = findViewById(R.id.envFloatRow);
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda18
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1342lambda$setupInjectButton$16$comandroidsupportMainActivity(view);
                }
            });
        }
    }

    /* renamed from: com.android.support.MainActivity$15, reason: invalid class name */
    static /* synthetic */ class AnonymousClass15 {
        static final /* synthetic */ int[] $SwitchMap$com$android$support$MainConnectionUiState$Action;

        static {
            int[] iArr = new int[MainConnectionUiState.Action.values().length];
            $SwitchMap$com$android$support$MainConnectionUiState$Action = iArr;
            try {
                iArr[MainConnectionUiState.Action.CHECK_ENVIRONMENT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Action[MainConnectionUiState.Action.REPAIR_OVERLAY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Action[MainConnectionUiState.Action.RETURN_TO_GAME.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Action[MainConnectionUiState.Action.CONNECT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* renamed from: lambda$setupInjectButton$13$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1339lambda$setupInjectButton$13$comandroidsupportMainActivity(View view) {
        int i = AnonymousClass15.$SwitchMap$com$android$support$MainConnectionUiState$Action[MainConnectionUiState.forPhase(this.currentMainPhase).action().ordinal()];
        if (i == 1) {
            applyMainConnectionState(MainConnectionUiState.Phase.CHECKING);
            checkRootAccess();
            return;
        }
        if (i == 2) {
            if (isRuntimeConnected()) {
                showOverlayStatusDetails();
                return;
            } else {
                repairOverlayPermissionThenConnect();
                return;
            }
        }
        if (i == 3) {
            openExistingRuntimeControls(true);
            return;
        }
        if (i != 4) {
            return;
        }
        if (isRuntimeConnected()) {
            refreshMainRuntimeState();
            openExistingRuntimeControls(true);
        } else if (!this.rootStatus) {
            applyMainConnectionState(MainConnectionUiState.Phase.NEEDS_SETUP);
        } else if (checkOverlayPermission()) {
            startInjection();
        } else {
            repairOverlayPermissionThenConnect();
        }
    }

    /* renamed from: lambda$setupInjectButton$14$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1340lambda$setupInjectButton$14$comandroidsupportMainActivity(View view) {
        openExistingRuntimeControls(false);
    }

    /* renamed from: lambda$setupInjectButton$15$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1341lambda$setupInjectButton$15$comandroidsupportMainActivity(View view) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    /* renamed from: lambda$setupInjectButton$16$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1342lambda$setupInjectButton$16$comandroidsupportMainActivity(View view) {
        showOverlayStatusDetails();
    }

    private void openExistingRuntimeControls(boolean z) {
        if (!isRuntimeConnected()) {
            refreshMainRuntimeState();
            showToast("游戏连接已中断，请先检查连接");
        } else {
            if (!checkOverlayPermission()) {
                showOverlayStatusDetails();
                return;
            }
            launchFloatingWindowService();
            if (z) {
                new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda49
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1331xf40c6985();
                    }
                }, "Return-To-Game").start();
            }
        }
    }

    /* renamed from: lambda$openExistingRuntimeControls$18$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1331xf40c6985() {
        if (launchGame()) {
            return;
        }
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1330xb6eca566();
            }
        });
    }

    /* renamed from: lambda$openExistingRuntimeControls$17$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1330xb6eca566() {
        String str;
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (requiresEarlyLaunch()) {
            str = "启动前校验未通过，已阻止打开游戏";
        } else {
            str = "未能切回游戏，请从最近任务中打开游戏";
        }
        showToast(str);
    }

    private void setupJoinChannelButton() {
        Button button = (Button) findViewById(R.id.btnJoinChannel);
        Button button2 = (Button) findViewById(R.id.btnContactSupport);
        Button button3 = (Button) findViewById(R.id.btnJoinGroup);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda42
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1343x15160b13(view);
                }
            });
        }
        if (button2 != null) {
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda43
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1344x55d0e5bd(view);
                }
            });
        }
        if (button3 != null) {
            button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda45
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1345x92f0a9dc(view);
                }
            });
        }
    }

    /* renamed from: lambda$setupJoinChannelButton$19$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1343x15160b13(View view) {
        openOfficialUrl(WigVerify.officialChannelUrl());
    }

    /* renamed from: lambda$setupJoinChannelButton$20$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1344x55d0e5bd(View view) {
        openOfficialUrl(WigVerify.officialSupportUrl());
    }

    /* renamed from: lambda$setupJoinChannelButton$21$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1345x92f0a9dc(View view) {
        openOfficialUrl(WigVerify.officialGroupUrl());
    }

    private void setupMainAnnouncement() {
        MainAnnouncementController mainAnnouncementController = new MainAnnouncementController(this, this.binding.mainAnnouncementBar, new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1346lambda$setupMainAnnouncement$22$comandroidsupportMainActivity();
            }
        });
        this.mainAnnouncementController = mainAnnouncementController;
        mainAnnouncementController.bind();
        FrameLayout root = this.binding.getRoot();
        final MainAnnouncementController mainAnnouncementController2 = this.mainAnnouncementController;
        Objects.requireNonNull(mainAnnouncementController2);
        root.post(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                mainAnnouncementController2.showAutomaticallyIfNeeded();
            }
        });
    }

    /* renamed from: lambda$setupMainAnnouncement$22$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1346lambda$setupMainAnnouncement$22$comandroidsupportMainActivity() {
        openOfficialUrl(WigVerify.officialChannelUrl());
    }

    private void setupSuBinaryPath() {
        this.bundledKsudPath = extractBundledKsud();
        this.suBinaryPath = loadSuBinaryPath();
        ShellExec.setSuBinary(SuBinaryPathPolicy.binary(this.suBinaryPath));
        this.binding.suPathInput.setText(this.suBinaryPath);
        updateSuPathBadge();
        this.binding.suPathApplyButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda25
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1354lambda$setupSuBinaryPath$23$comandroidsupportMainActivity(view);
            }
        });
        this.binding.suPathInput.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda26
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return this.f$0.m1355lambda$setupSuBinaryPath$24$comandroidsupportMainActivity(textView, i, keyEvent);
            }
        });
    }

    /* renamed from: lambda$setupSuBinaryPath$23$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1354lambda$setupSuBinaryPath$23$comandroidsupportMainActivity(View view) {
        applySuBinaryPath();
    }

    /* renamed from: lambda$setupSuBinaryPath$24$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1355lambda$setupSuBinaryPath$24$comandroidsupportMainActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        applySuBinaryPath();
        return true;
    }

    private String extractBundledKsud() throws IOException {
        String str = Build.SUPPORTED_ABIS.length == 0 ? "" : Build.SUPPORTED_ABIS[0];
        String str2 = "x86_64";
        if (!str.startsWith("x86_64")) {
            if (str.startsWith("arm64")) {
                str2 = "arm64-v8a";
            } else {
                log("KernelSU ksud 不支持当前 ABI: " + str);
                return null;
            }
        }
        File file = new File(getFilesDir(), "tools/ksud");
        try {
            InputStream inputStreamOpen = getAssets().open("ksud/" + str2 + "/ksud");
            try {
                File parentFile = file.getParentFile();
                if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs() && !parentFile.exists()) {
                    log("KernelSU ksud 目录创建失败: " + parentFile);
                    if (inputStreamOpen == null) {
                        return null;
                    }
                } else {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    try {
                        byte[] bArr = new byte[8192];
                        while (true) {
                            int i = inputStreamOpen.read(bArr);
                            if (i <= 0) {
                                break;
                            }
                            fileOutputStream.write(bArr, 0, i);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        if (!file.setExecutable(true, false) && !file.canExecute()) {
                            log("KernelSU ksud 无法设置可执行权限: " + file);
                            if (inputStreamOpen != null) {
                            }
                            return null;
                        }
                        log("KernelSU ksud 已解包: " + file + " abi=" + str2);
                        String absolutePath = file.getAbsolutePath();
                        if (inputStreamOpen != null) {
                            inputStreamOpen.close();
                        }
                        return absolutePath;
                    } finally {
                    }
                }
                inputStreamOpen.close();
                return null;
            } finally {
            }
        } catch (Exception e) {
            log("KernelSU ksud 解包失败: " + e.getClass().getSimpleName() + " message=" + e.getMessage());
            return null;
        }
    }

    private String loadSuBinaryPath() {
        String string = getSharedPreferences(ConfigPreferenceNamespaces.APP_SETTINGS.preferencesName, 0).getString("su_binary_path", "");
        String strSanitize = SuBinaryPathPolicy.sanitize(string);
        if (!strSanitize.equals(SuBinaryPathPolicy.normalize(string))) {
            log("已保存的 su 路径不合法，回到自动检测");
        }
        return strSanitize;
    }

    private void applySuBinaryPath() {
        final String strNormalize = SuBinaryPathPolicy.normalize(this.binding.suPathInput.getText() == null ? "" : this.binding.suPathInput.getText().toString());
        String strValidate = SuBinaryPathPolicy.validate(strNormalize);
        if (strValidate != null) {
            showSuPathStatus(strValidate, false);
            this.binding.suPathInput.requestFocus();
            return;
        }
        if (this.suPathCheckInFlight.compareAndSet(false, true)) {
            this.suBinaryPath = strNormalize;
            getSharedPreferences(ConfigPreferenceNamespaces.APP_SETTINGS.preferencesName, 0).edit().putString("su_binary_path", strNormalize).apply();
            ConfigNamespaceBackupMirror.request(this, ConfigPreferenceNamespaces.APP_SETTINGS);
            ShellExec.setSuBinary(SuBinaryPathPolicy.binary(strNormalize));
            invalidateSuPrefix();
            this.binding.suPathInput.setText(strNormalize);
            updateSuPathBadge();
            setSuPathBusy(true);
            showSuPathStatus(getString(R.string.su_path_checking), null);
            new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1309lambda$applySuBinaryPath$26$comandroidsupportMainActivity(strNormalize);
                }
            }, "SuPathApply").start();
        }
    }

    /* renamed from: lambda$applySuBinaryPath$26$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1309lambda$applySuBinaryPath$26$comandroidsupportMainActivity(String str) {
        final String str2;
        final boolean zCheckRootPermission = checkRootPermission();
        String[] suPrefix = getSuPrefix();
        String strJoin = suPrefix == null ? "管道模式" : String.join(" ", suPrefix);
        if (zCheckRootPermission) {
            str2 = "已生效：" + SuBinaryPathPolicy.describe(str) + "（" + strJoin + "）";
        } else {
            str2 = "用 " + SuBinaryPathPolicy.describe(str) + " 拿不到高级权限：请确认路径正确、并在权限管理器里允许记牌器";
        }
        runtimeRecord("APP", "SU_PATH_APPLY", zCheckRootPermission ? "OK" : "FAIL", "custom=" + (!SuBinaryPathPolicy.isAuto(str) ? 1 : 0) + " mode=" + strJoin.replace(' ', '_'));
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda55
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1308lambda$applySuBinaryPath$25$comandroidsupportMainActivity(str2, zCheckRootPermission);
            }
        });
    }

    /* renamed from: lambda$applySuBinaryPath$25$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1308lambda$applySuBinaryPath$25$comandroidsupportMainActivity(String str, boolean z) {
        this.suPathCheckInFlight.set(false);
        if (this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        setSuPathBusy(false);
        showSuPathStatus(str, Boolean.valueOf(z));
        updateRootStatus(z);
    }

    private void invalidateSuPrefix() {
        synchronized (this.suPrefixLock) {
            this.suPrefixResolved = false;
            this.cachedSuPrefix = null;
        }
        this.rootMountNamespaceFile = null;
    }

    private void updateSuPathBadge() {
        int i;
        boolean zIsAuto = SuBinaryPathPolicy.isAuto(this.suBinaryPath);
        TextView textView = this.binding.suPathBadge;
        if (zIsAuto) {
            i = R.string.su_path_badge_auto;
        } else {
            i = R.string.su_path_badge_custom;
        }
        textView.setText(i);
        this.binding.suPathBadge.setTextColor(getColor(zIsAuto ? R.color.ds_text_3 : R.color.ds_brand));
    }

    private void setSuPathBusy(boolean z) {
        this.binding.suPathInput.setEnabled(!z);
        this.binding.suPathApplyButton.setEnabled(!z);
        this.binding.suPathApplyButton.setAlpha(z ? 0.48f : 1.0f);
    }

    private void showSuPathStatus(String str, Boolean bool) {
        int i;
        this.binding.suPathStatus.setVisibility(0);
        if (bool == null) {
            i = R.color.ds_brand;
        } else {
            i = bool.booleanValue() ? R.color.ds_ok : R.color.ds_danger;
        }
        this.binding.suPathStatus.setTextColor(getColor(i));
        this.binding.suPathStatus.setText(str);
    }

    private void setupSelfServiceUnbind() {
        this.binding.selfServiceUnbindButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1347x4b05010a(view);
            }
        });
        this.binding.selfServiceUnbindInput.addTextChangedListener(new TextWatcher() { // from class: com.android.support.MainActivity.4
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                MainActivity.this.updateHomeUnbindSubmitEnabled();
            }
        });
        this.binding.selfServiceUnbindInput.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda11
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return this.f$0.m1348x8824c529(textView, i, keyEvent);
            }
        });
        updateHomeUnbindSubmitEnabled();
    }

    /* renamed from: lambda$setupSelfServiceUnbind$27$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1347x4b05010a(View view) {
        submitHomeUnbind();
    }

    /* renamed from: lambda$setupSelfServiceUnbind$28$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1348x8824c529(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        submitHomeUnbind();
        return true;
    }

    private void submitHomeUnbind() {
        try {
            if (!dispatchUnbind(SelfServiceUnbindClient.normalizeCard(this.binding.selfServiceUnbindInput.getText().toString()), new UnbindCompletion() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda50
                @Override // com.android.support.MainActivity.UnbindCompletion
                public final void onComplete(SelfServiceUnbindClient.Result result, String str) {
                    this.f$0.m1358lambda$submitHomeUnbind$29$comandroidsupportMainActivity(result, str);
                }
            })) {
                showHomeUnbindStatus("更换设备请求正在处理中，请稍候", false);
                return;
            }
            setHomeUnbindLoading(true);
            this.binding.selfServiceUnbindStatus.setVisibility(0);
            this.binding.selfServiceUnbindStatus.setTextColor(getColor(R.color.ds_brand));
            this.binding.selfServiceUnbindStatus.setText(getString(R.string.self_service_unbind_submitting));
        } catch (IllegalArgumentException e) {
            showHomeUnbindStatus(e.getMessage(), false);
            this.binding.selfServiceUnbindInput.requestFocus();
        }
    }

    /* renamed from: lambda$submitHomeUnbind$29$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1358lambda$submitHomeUnbind$29$comandroidsupportMainActivity(SelfServiceUnbindClient.Result result, String str) {
        setHomeUnbindLoading(false);
        if (str != null) {
            showHomeUnbindStatus(str, false);
        } else {
            if (!result.isSuccess()) {
                showHomeUnbindStatus("更换设备未完成，请确认使用码正确；仍失败请联系客服", false);
                return;
            }
            resetLocalAuthenticationAfterSuccessfulUnbind();
            this.binding.selfServiceUnbindInput.setText("");
            showHomeUnbindStatus("旧设备已解除，请返回游戏重新验证", true);
        }
    }

    private void setHomeUnbindLoading(boolean z) {
        int i;
        this.binding.selfServiceUnbindInput.setEnabled(!z);
        Button button = this.binding.selfServiceUnbindButton;
        if (z) {
            i = R.string.self_service_unbind_submitting;
        } else {
            i = R.string.self_service_unbind_submit;
        }
        button.setText(i);
        updateHomeUnbindSubmitEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHomeUnbindSubmitEnabled() {
        boolean z = (this.binding.selfServiceUnbindInput.getText() == null || this.binding.selfServiceUnbindInput.getText().toString().trim().isEmpty() || !this.binding.selfServiceUnbindInput.isEnabled() || this.selfServiceUnbindInFlight.get()) ? false : true;
        this.binding.selfServiceUnbindButton.setEnabled(z);
        this.binding.selfServiceUnbindButton.setAlpha(z ? 1.0f : 0.48f);
    }

    private void showHomeUnbindStatus(String str, boolean z) {
        this.binding.selfServiceUnbindStatus.setVisibility(0);
        this.binding.selfServiceUnbindStatus.setTextColor(getColor(z ? R.color.ds_ok : R.color.ds_danger));
        this.binding.selfServiceUnbindStatus.setText(str);
    }

    private boolean dispatchUnbind(final String str, final UnbindCompletion unbindCompletion) {
        if (!this.selfServiceUnbindInFlight.compareAndSet(false, true)) {
            return false;
        }
        this.selfServiceUnbindExecutor.execute(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1320lambda$dispatchUnbind$31$comandroidsupportMainActivity(str, unbindCompletion);
            }
        });
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v2, types: [com.android.support.MainActivity$$ExternalSyntheticLambda47, java.lang.Runnable] */
    /* renamed from: lambda$dispatchUnbind$31$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1320lambda$dispatchUnbind$31$comandroidsupportMainActivity(String str, final UnbindCompletion unbindCompletion) {
        final String strFailureMessage;
        boolean z = 0;
        boolean z2 = false;
        final SelfServiceUnbindClient.Result result = null;
        try {
            try {
                SelfServiceUnbindClient.Result resultUnbind = this.selfServiceUnbindClient.unbind(str);
                this.selfServiceUnbindInFlight.set(false);
                result = resultUnbind;
                strFailureMessage = null;
            } finally {
                this.selfServiceUnbindInFlight.set(z);
            }
        } catch (IOException | RuntimeException e) {
            strFailureMessage = SelfServiceUnbindClient.failureMessage(e);
        }
        z = new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1319lambda$dispatchUnbind$30$comandroidsupportMainActivity(unbindCompletion, result, strFailureMessage);
            }
        };
        runOnUiThread(z);
    }

    /* renamed from: lambda$dispatchUnbind$30$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1319lambda$dispatchUnbind$30$comandroidsupportMainActivity(UnbindCompletion unbindCompletion, SelfServiceUnbindClient.Result result, String str) {
        if (this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        unbindCompletion.onComplete(result, str);
    }

    private void resetLocalAuthenticationAfterSuccessfulUnbind() {
        try {
            WigVerify wigVerify = WigVerify.getInstance(getApplicationContext());
            wigVerify.forceLogout("self_service_unbind_success");
            wigVerify.clearSavedCard();
            if (FloatingWindowService.isServiceReady()) {
                Intent intent = new Intent(this, (Class<?>) FloatingWindowService.class);
                intent.setAction(FloatingWindowService.ACTION_TRUST_REVOKED);
                startService(intent);
            }
        } catch (Throwable th) {
            log("解绑成功后重置本地验证失败: " + th.getClass().getSimpleName());
        }
    }

    private void openOfficialUrl(String str) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
            FloatingWindowService.safeToast(this, "正在打开官方链接...", 0);
        } catch (Exception e) {
            log("打开官方链接失败: " + e.getMessage());
            FloatingWindowService.safeToast(this, "无法打开链接，请检查浏览器", 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startInjection() {
        MainActivity mainActivity;
        long jBeginLease;
        String strUnavailableReason = GameRegionControl.unavailableReason(GameRegionState.current());
        if (!strUnavailableReason.isEmpty()) {
            showToast(strUnavailableReason);
            return;
        }
        long jBegin = injectionLifecycle.begin();
        if (jBegin == 0) {
            showToast("正在连接游戏，请勿重复操作");
            return;
        }
        this.ownedInjectionAttempt = jBegin;
        try {
            this.isCompatMode = this.binding.cbCompatMode.isChecked();
            this.selectedRuntimeFileName = NativeLibrarySelector.selectRuntimeFileName(Build.SUPPORTED_ABIS, this.isCompatMode);
            jBeginLease = INJECTION_PROPERTIES.beginLease();
            applyMainConnectionState(MainConnectionUiState.Phase.CONNECTING);
            this.binding.tvFooter.setText(this.isCompatMode ? "正在连接游戏（兼容模式）…" : "正在连接游戏…");
            if (!FloatingWindowService.isServiceReady()) {
                startFloatingWindow();
            }
            mainActivity = this;
        } catch (RuntimeException unused) {
            mainActivity = this;
        }
        try {
            new Thread(mainActivity.new AnonymousClass5(jBegin, jBeginLease), "Jcc-Injection").start();
        } catch (RuntimeException unused2) {
            injectionLifecycle.finish(jBegin);
            mainActivity.showErrorAndExit("连接准备失败，请重试");
        }
    }

    /* renamed from: com.android.support.MainActivity$5, reason: invalid class name */
    class AnonymousClass5 implements Runnable {
        final /* synthetic */ long val$loadAttempt;
        final /* synthetic */ long val$propertyLease;

        AnonymousClass5(long j, long j2) {
            this.val$loadAttempt = j;
            this.val$propertyLease = j2;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                try {
                    MainActivity.this.runInjectionScript(this.val$loadAttempt, this.val$propertyLease);
                } catch (RuntimeException e) {
                    MainActivity.this.runtimeRecord("INJECT", "PIPELINE", "EXCEPTION", "exception=" + e.getClass().getName() + " message=" + e.getMessage());
                    MainActivity.this.log("注入流程异常: " + e.getClass().getSimpleName());
                    MainActivity mainActivity = MainActivity.this;
                    final long j = this.val$loadAttempt;
                    mainActivity.runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$5$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1362lambda$run$0$comandroidsupportMainActivity$5(j);
                        }
                    });
                }
            } finally {
                MainActivity.injectionLifecycle.finish(this.val$loadAttempt);
            }
        }

        /* renamed from: lambda$run$0$com-android-support-MainActivity$5, reason: not valid java name */
        /* synthetic */ void m1362lambda$run$0$comandroidsupportMainActivity$5(long j) {
            if (MainActivity.this.isCurrentLoad(j)) {
                MainActivity.this.showErrorAndExit("连接过程出错，已安全停止：请重新打开记牌器再试；多次失败请联系客服并提供诊断日志");
            }
        }
    }

    private void checkRootAccess() {
        if (this.suPathCheckInFlight.compareAndSet(false, true)) {
            setSuPathBusy(true);
            applyMainConnectionState(MainConnectionUiState.Phase.CHECKING);
            final String str = this.suBinaryPath;
            new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1316lambda$checkRootAccess$33$comandroidsupportMainActivity(str);
                }
            }, ThreadNaming.WORKER).start();
        }
    }

    /* renamed from: lambda$checkRootAccess$33$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1316lambda$checkRootAccess$33$comandroidsupportMainActivity(String str) {
        final boolean zRequestRoot = requestRoot(str);
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1315lambda$checkRootAccess$32$comandroidsupportMainActivity(zRequestRoot);
            }
        });
    }

    /* renamed from: lambda$checkRootAccess$32$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1315lambda$checkRootAccess$32$comandroidsupportMainActivity(boolean z) {
        this.suPathCheckInFlight.set(false);
        if (this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        if (z) {
            invalidateSuPrefix();
        }
        setSuPathBusy(false);
        updateRootStatus(z);
    }

    private boolean requestRoot(String str) {
        boolean rootIdentity;
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add(str);
        if (SuBinaryPathPolicy.isAuto(str) && this.bundledKsudPath != null && !this.bundledKsudPath.equals(str)) {
            arrayList.add(this.bundledKsudPath);
        }
        for (String str2 : arrayList) {
            Process processExec = null;
            try {
                try {
                    log("requestRoot start path=" + SuBinaryPathPolicy.describe(str2));
                    processExec = Runtime.getRuntime().exec(SuBinaryPathPolicy.pipeCommand(str2));
                    rootIdentity = BoundedProcessProbe.readRootIdentity(processExec, 12000L);
                    log("requestRoot result=" + rootIdentity + " exit=" + processExec.exitValue() + " path=" + SuBinaryPathPolicy.describe(str2));
                } catch (InterruptedException unused) {
                    Thread.currentThread().interrupt();
                    log("requestRoot interrupted path=" + SuBinaryPathPolicy.describe(str2));
                    if (processExec != null) {
                        processExec.destroy();
                    }
                    return false;
                } catch (Exception e) {
                    log("requestRoot error path=" + SuBinaryPathPolicy.describe(str2) + " type=" + e.getClass().getSimpleName() + " message=" + String.valueOf(e.getMessage()));
                    if (processExec != null) {
                    }
                }
                if (rootIdentity) {
                    adoptWorkingSuPath(str, str2);
                    if (processExec == null) {
                        return true;
                    }
                    processExec.destroy();
                    return true;
                }
                if (processExec != null) {
                    processExec.destroy();
                }
            } catch (Throwable th) {
                if (processExec != null) {
                    processExec.destroy();
                }
                throw th;
            }
        }
        return false;
    }

    private void adoptWorkingSuPath(String str, final String str2) {
        if (!SuBinaryPathPolicy.isAuto(str) || SuBinaryPathPolicy.isAuto(str2)) {
            return;
        }
        this.suBinaryPath = str2;
        ShellExec.setSuBinary(SuBinaryPathPolicy.binary(str2));
        getSharedPreferences(ConfigPreferenceNamespaces.APP_SETTINGS.preferencesName, 0).edit().putString("su_binary_path", str2).apply();
        ConfigNamespaceBackupMirror.request(this, ConfigPreferenceNamespaces.APP_SETTINGS);
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1306lambda$adoptWorkingSuPath$34$comandroidsupportMainActivity(str2);
            }
        });
        log("自动检测已切换 KernelSU ksud: " + str2);
    }

    /* renamed from: lambda$adoptWorkingSuPath$34$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1306lambda$adoptWorkingSuPath$34$comandroidsupportMainActivity(String str) {
        if (this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        this.binding.suPathInput.setText(str);
        updateSuPathBadge();
    }

    private void updateRootStatus(boolean z) {
        MainConnectionUiState.Phase phaseRuntimeConnectedPhase;
        if (this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        this.rootStatus = z;
        if (z) {
            if (isRuntimeConnected()) {
                phaseRuntimeConnectedPhase = runtimeConnectedPhase();
            } else {
                phaseRuntimeConnectedPhase = injectionLifecycle.isRunning() ? MainConnectionUiState.Phase.CONNECTING : MainConnectionUiState.Phase.READY;
            }
            applyMainConnectionState(phaseRuntimeConnectedPhase);
            this.binding.tvRootDetail.setText("");
            this.binding.tvRootDetail.setTextColor(getColor(R.color.ds_ok));
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.binding.btnInject, "alpha", 0.6f, 1.0f);
            objectAnimatorOfFloat.setDuration(300L);
            objectAnimatorOfFloat.start();
            autoGrantPermissionsViaRoot();
            attemptAutomaticRuntimeRecovery();
            return;
        }
        applyMainConnectionState(MainConnectionUiState.Phase.NEEDS_SETUP);
        this.binding.tvRootDetail.setText("未获得 Root 授权（最常见原因）。手机：打开 Magisk/KernelSU 等 Root 管理器允许本应用；模拟器：在设置里开启 Root。授权后点击“重新检查权限”");
        this.binding.tvRootDetail.setTextColor(getColor(R.color.ds_warn));
    }

    private boolean isEmulator() {
        Boolean bool = this.cachedIsEmulator;
        if (bool != null) {
            return bool.booleanValue();
        }
        String str = Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "";
        if (str.contains("x86")) {
            log("模拟器检测: 主 ABI=" + str + " → 模拟器");
            this.cachedIsEmulator = true;
            return true;
        }
        String str2 = Build.HARDWARE;
        if (str2 != null && (str2.contains("ranchu") || str2.contains("goldfish") || str2.contains("vbox") || str2.contains("nox") || str2.contains("nemu") || str2.contains("mumu") || str2.contains("android_x86"))) {
            log("模拟器检测: hardware=" + str2 + " → 模拟器");
            this.cachedIsEmulator = true;
            return true;
        }
        String[] strArr = {"/dev/qemu_pipe", "/dev/goldfish_pipe", "/dev/vboxguest", "/system/lib/libmumuvm.so", "/system/lib64/libmumuvm.so", "/system/lib/libnemu.so", "/system/lib64/libnemu.so", "/system/lib/libldinput.so", "/system/lib64/libldinput.so", "/system/bin/nox", "/system/bin/microvirt"};
        for (int i = 0; i < 11; i++) {
            String str3 = strArr[i];
            if (new File(str3).exists()) {
                log("模拟器检测: 发现特征文件 " + str3 + " → 模拟器");
                this.cachedIsEmulator = true;
                return true;
            }
        }
        String str4 = Build.MANUFACTURER;
        if (str4 != null && (str4.toLowerCase().contains("genymotion") || str4.toLowerCase().contains("netease") || str4.toLowerCase().contains("nemu") || str4.toLowerCase().contains("mumu"))) {
            log("模拟器检测: manufacturer=" + str4 + " → 模拟器");
            this.cachedIsEmulator = true;
            return true;
        }
        log("模拟器检测: 真机 (ABI=" + str + ")");
        this.cachedIsEmulator = false;
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getGamePid() throws NumberFormatException {
        int i;
        try {
            String strTrim = execCommand("pidof " + this.TARGET_PACKAGE + " 2>/dev/null | tr ' ' '\\n' | while read p; do   if cat /proc/$p/cmdline 2>/dev/null | tr '\\0' ' ' | grep -qv ':'; then echo $p; break; fi; done").trim();
            if (strTrim == null || strTrim.isEmpty() || strTrim.startsWith("ERROR")) {
                return -1;
            }
            for (String str : strTrim.split("\n")) {
                try {
                    i = Integer.parseInt(str.trim());
                } catch (NumberFormatException unused) {
                }
                if (i > 0) {
                    return i;
                }
            }
            return -1;
        } catch (Exception e) {
            log("getGamePid error: " + e.getMessage());
            return -1;
        }
    }

    private boolean hasLiveIpcPayloadThreads(int i) {
        if (i <= 0) {
            return false;
        }
        CommandResult commandResultExecCommandDetailed = execCommandDetailed("grep -qx 'pool-2-thread-2' /proc/" + i + "/task/*/comm 2>/dev/null && echo __JCC_IPC_THREADS__", 2000L);
        return !commandResultExecCommandDetailed.timedOut && commandResultExecCommandDetailed.exitCode == 0 && commandResultExecCommandDetailed.getMergedOutput().contains("__JCC_IPC_THREADS__");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasExistingRuntimeReconnectHint(int i) {
        String gameProcessIdentity = readGameProcessIdentity(i);
        if (gameProcessIdentity == null) {
            return false;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(RUNTIME_STATE_PREFS, 0);
        if (sharedPreferences.contains(RUNTIME_IDENTITY_KEY)) {
            return gameProcessIdentity.equals(sharedPreferences.getString(RUNTIME_IDENTITY_KEY, ""));
        }
        return false;
    }

    private void attemptAutomaticRuntimeRecovery() {
        if (this.automaticRecoveryStarted.compareAndSet(false, true)) {
            final long jBegin = injectionLifecycle.begin();
            if (jBegin == 0) {
                return;
            }
            this.ownedInjectionAttempt = jBegin;
            try {
                new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda48
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1311x87142e1f(jBegin);
                    }
                }, "Runtime-Auto-Recover").start();
            } catch (RuntimeException e) {
                injectionLifecycle.finish(jBegin);
                abandonAutomaticRuntimeRecovery(jBegin, "启动恢复线程无法启动: " + e.getClass().getSimpleName());
            }
        }
    }

    /* renamed from: lambda$attemptAutomaticRuntimeRecovery$36$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1311x87142e1f(final long j) {
        try {
            try {
                if (isCurrentLoad(j)) {
                    int gamePid = getGamePid();
                    if (!InjectionReconnectPolicy.shouldAutoRecoverRuntime(gamePid, isGameLibsLoaded(gamePid), hasExistingRuntimeReconnectHint(gamePid))) {
                        log("启动恢复：没有可复用的同进程运行时");
                    } else if (isCurrentLoad(j)) {
                        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda52
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.m1310x49f46a00(j);
                            }
                        });
                        handleReconnect(gamePid, j);
                    }
                }
            } catch (RuntimeException e) {
                abandonAutomaticRuntimeRecovery(j, "启动恢复异常: " + e.getClass().getSimpleName());
            }
        } finally {
            injectionLifecycle.finish(j);
        }
    }

    /* renamed from: lambda$attemptAutomaticRuntimeRecovery$35$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1310x49f46a00(long j) {
        if (this.binding == null || !isCurrentLoad(j)) {
            return;
        }
        applyMainConnectionState(MainConnectionUiState.Phase.CONNECTING);
        this.binding.tvRootStatus.setText("检测到已有游戏连接");
        this.binding.tvRootDetail.setText("正在自动恢复连接与悬浮窗，无需重复操作");
        this.binding.tvFooter.setText("正在恢复上次会话...");
        this.binding.loadingProgress.setProgress(35);
    }

    private void abandonAutomaticRuntimeRecovery(final long j, String str) {
        log(str);
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1305x7603c1e6(j);
            }
        });
    }

    /* renamed from: lambda$abandonAutomaticRuntimeRecovery$37$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1305x7603c1e6(long j) {
        if (this.binding == null || !isCurrentLoad(j)) {
            return;
        }
        applyMainConnectionState(MainConnectionUiState.Phase.READY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean prepareExistingRuntimeBootstrapDelivery(int i) {
        try {
            LocalSocketIPC.getInstance().prepareBootstrapDelivery(i);
            log("启动恢复：当前认证材料已通过内存通道准备就绪");
            return true;
        } catch (IOException e) {
            log("启动恢复：当前认证材料不可用: " + e.getClass().getSimpleName());
            return false;
        }
    }

    private void rememberInjectedRuntime(int i) {
        String gameProcessIdentity = readGameProcessIdentity(i);
        if (gameProcessIdentity == null) {
            return;
        }
        getSharedPreferences(RUNTIME_STATE_PREFS, 0).edit().putString(RUNTIME_IDENTITY_KEY, gameProcessIdentity).apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String readGameProcessIdentity(int i) {
        if (i <= 0) {
            return null;
        }
        String strTrim = execCommand("awk '{print $22}' /proc/" + i + "/stat 2>/dev/null").trim();
        if (strTrim.matches("[0-9]{1,20}")) {
            return i + ":" + strTrim;
        }
        return null;
    }

    private boolean isGameLibsLoaded(int i) {
        if (i <= 0) {
            return false;
        }
        try {
            String strTrim = execCommand("grep -cE 'libil2cpp\\.so' /proc/" + i + "/maps 2>/dev/null").trim();
            if (strTrim != null) {
                if (Integer.parseInt(strTrim.trim()) >= 1) {
                    return true;
                }
            }
        } catch (Exception unused) {
        }
        return false;
    }

    private boolean isProcessAlive(int i) {
        if (i <= 0) {
            return false;
        }
        CommandResult commandResultExecCommandDetailed = execCommandDetailed("test -d /proc/" + i, 2000L);
        return !commandResultExecCommandDetailed.timedOut && commandResultExecCommandDetailed.exitCode == 0;
    }

    private boolean isGameInputReady(int i) {
        if (i > 0 && isProcessAlive(i)) {
            CommandResult commandResultExecCommandDetailed = execCommandDetailed("wchan=$(cat /proc/" + i + "/wchan 2>/dev/null || true); case \"$wchan\" in hrtimer_nanosleep|nanosleep|do_nanosleep) echo __GAME_STARTUP_SLEEP__ ;; '') echo __GAME_WCHAN_UNAVAILABLE__ ;; *) echo __GAME_INPUT_READY__ ;; esac", 2000L);
            if (!commandResultExecCommandDetailed.timedOut && commandResultExecCommandDetailed.exitCode == 0) {
                String mergedOutput = commandResultExecCommandDetailed.getMergedOutput();
                if (mergedOutput.contains("__GAME_STARTUP_SLEEP__")) {
                    log("游戏主线程仍在启动休眠，延后注入: PID=" + i);
                    return false;
                }
                return mergedOutput.contains("__GAME_INPUT_READY__");
            }
        }
        return false;
    }

    private InjectionTargetReadiness.Snapshot readInjectionTargetSnapshot(int i) {
        if (i <= 0) {
            return InjectionTargetReadiness.parse("__JCC_TARGET_STATE__ pid=0 start=0 top=0 freeze=-1 state=? tracer=-1 sigpnd=unknown shdpnd=unknown wchan=unknown cpuset=unknown");
        }
        CommandResult commandResultExecCommandDetailed = execCommandDetailed("PID=" + i + "; if [ ! -r /proc/$PID/status ]; then echo '__JCC_TARGET_STATE__ pid=0 start=0 top=0 freeze=-1 state=? tracer=-1 sigpnd=unknown shdpnd=unknown wchan=unknown cpuset=unknown'; exit 0; fi; START=$(awk '{print $22}' /proc/$PID/stat 2>/dev/null); [ -z \"$START\" ] && START=0; TOP=0; CGROUP=$(sed -n 's/^0:://p' /proc/$PID/cgroup 2>/dev/null | head -n 1); FREEZE=-1; if [ -n \"$CGROUP\" ] && [ -r \"/sys/fs/cgroup$CGROUP/cgroup.freeze\" ]; then FREEZE=$(cat \"/sys/fs/cgroup$CGROUP/cgroup.freeze\" 2>/dev/null); fi; [ -z \"$FREEZE\" ] && FREEZE=-1; STATE=$(awk '/^State:/{print $2; exit}' /proc/$PID/status 2>/dev/null); TRACER=$(awk '/^TracerPid:/{print $2; exit}' /proc/$PID/status 2>/dev/null); SIGPND=$(awk '/^SigPnd:/{print $2; exit}' /proc/$PID/status 2>/dev/null); SHDPND=$(awk '/^ShdPnd:/{print $2; exit}' /proc/$PID/status 2>/dev/null); WCHAN=$(cat /proc/$PID/wchan 2>/dev/null | tr ' ' '_'); CPUSET=$(awk -F: '$2==\"cpuset\"{print $3; exit}' /proc/$PID/cgroup 2>/dev/null | sed 's#^/##' | tr ' /' '__'); [ -z \"$STATE\" ] && STATE=?; [ -z \"$TRACER\" ] && TRACER=-1; [ -z \"$SIGPND\" ] && SIGPND=unknown; [ -z \"$SHDPND\" ] && SHDPND=unknown; [ -z \"$WCHAN\" ] && WCHAN=unknown; [ -z \"$CPUSET\" ] && CPUSET=unknown; echo '__JCC_TARGET_STATE__' pid=$PID start=$START top=$TOP freeze=$FREEZE state=$STATE tracer=$TRACER sigpnd=$SIGPND shdpnd=$SHDPND wchan=$WCHAN cpuset=$CPUSET", 2000L);
        InjectionTargetReadiness.Snapshot snapshot = InjectionTargetReadiness.parse(commandResultExecCommandDetailed.getMergedOutput());
        if (!commandResultExecCommandDetailed.timedOut && commandResultExecCommandDetailed.exitCode == 0) {
            return snapshot;
        }
        runtimeRecord("INJECT", "TARGET_SNAPSHOT", "FAIL", "pid=" + i + " exit=" + commandResultExecCommandDetailed.exitCode + " timeout=" + (commandResultExecCommandDetailed.timedOut ? 1 : 0) + " parsed=" + snapshot.summary());
        return snapshot;
    }

    private InjectionTargetReadiness.Snapshot waitForInteractiveInjectionTarget(int i, int i2) throws InterruptedException {
        InjectionTargetReadiness.Snapshot snapshot;
        InjectionTargetReadiness.Snapshot snapshotAliveFallback;
        String strRejectionReason;
        long j;
        long j2;
        int i3;
        long j3;
        long j4;
        int i4;
        runtimeRecord("INJECT", "TARGET_INTERACTIVE", "WAIT", "pid=" + i);
        log("物理机注入前切回游戏并等待目标解冻: PID=" + i);
        launchGame();
        execCommandDetailed("am unfreeze " + this.TARGET_PACKAGE + " >/dev/null 2>&1 || true", 2000L);
        long jCurrentTimeMillis = System.currentTimeMillis() + Math.max(1000, i2);
        Object obj = null;
        long j5 = 0;
        int i5 = 0;
        InjectionTargetReadiness.Snapshot snapshot2 = null;
        while (System.currentTimeMillis() < jCurrentTimeMillis) {
            snapshotAliveFallback = readInjectionTargetSnapshot(i);
            snapshot = null;
            String strRejectionReason2 = InjectionTargetReadiness.rejectionReason(snapshotAliveFallback, i);
            String str = snapshotAliveFallback.summary() + " reason=" + strRejectionReason2;
            if (!str.equals(obj)) {
                log("注入目标状态: " + str);
                runtimeRecord("INJECT", "TARGET_INTERACTIVE", "WAIT", str);
                obj = str;
            }
            if (InjectionTargetReadiness.isReady(snapshotAliveFallback, i)) {
                if (j5 == snapshotAliveFallback.startTicks) {
                    i4 = i5 + 1;
                    long j6 = j5;
                    j = jCurrentTimeMillis;
                    j4 = j6;
                } else {
                    j = jCurrentTimeMillis;
                    j4 = snapshotAliveFallback.startTicks;
                    i4 = 1;
                }
                if (i4 >= 2) {
                    runtimeRecord("INJECT", "TARGET_INTERACTIVE", "OK", snapshotAliveFallback.summary());
                    return snapshotAliveFallback;
                }
                i3 = i4;
                j3 = j4;
                j2 = 2000;
            } else {
                j = jCurrentTimeMillis;
                if ("frozen".equals(strRejectionReason2)) {
                    j2 = 2000;
                    execCommandDetailed("am unfreeze " + this.TARGET_PACKAGE + " >/dev/null 2>&1 || true", 2000L);
                } else {
                    j2 = 2000;
                }
                i3 = 0;
                j3 = 0;
            }
            try {
                Thread.sleep(100L);
                long j7 = j3;
                i5 = i3;
                jCurrentTimeMillis = j;
                j5 = j7;
                snapshot2 = snapshotAliveFallback;
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
        }
        snapshot = null;
        snapshotAliveFallback = snapshot2;
        if (snapshotAliveFallback == null) {
            strRejectionReason = "snapshot_unavailable";
        } else {
            strRejectionReason = InjectionTargetReadiness.rejectionReason(snapshotAliveFallback, i);
        }
        if (isProcessAlive(i) && !InjectionTargetReadiness.shouldCancelInject(strRejectionReason)) {
            if (snapshotAliveFallback == null || snapshotAliveFallback.pid != i) {
                snapshotAliveFallback = InjectionTargetReadiness.aliveFallback(i);
            }
            runtimeRecord("INJECT", "TARGET_INTERACTIVE", "OK", "fallback=" + strRejectionReason + " " + snapshotAliveFallback.summary());
            log("注入目标诊断未齐，游戏进程仍在，继续注入: " + snapshotAliveFallback.summary());
            return snapshotAliveFallback;
        }
        String strSummary = snapshotAliveFallback == null ? "pid=" + i + " snapshot=none" : snapshotAliveFallback.summary();
        runtimeRecord("INJECT", "TARGET_INTERACTIVE", "FAIL", strSummary);
        log("物理机注入目标未进入可安全附加状态: " + strSummary);
        return snapshot;
    }

    private int[] getGameStatus() {
        int[] iArr = {-1, 0, 0};
        try {
            String strTrim = execCommand("PID=$(pidof " + this.TARGET_PACKAGE + " 2>/dev/null | tr ' ' '\\n' | while read p; do   CMD=$(tr '\\0' '\\n' < /proc/$p/cmdline 2>/dev/null | head -n 1);   if [ \"$CMD\" = \"" + this.TARGET_PACKAGE + "\" ]; then echo $p; break; fi; done); [ -z \"$PID\" ] && PID=0; if [ \"$PID\" -gt 0 ] 2>/dev/null; then   if grep -q 'libil2cpp\\.so' /proc/$PID/maps 2>/dev/null; then LIBS=1; else LIBS=0; fi;   if grep -qx 'pool-2-thread-2' /proc/$PID/task/*/comm 2>/dev/null; then INJ=1; else INJ=0; fi;   echo \"$PID $LIBS $INJ\"; else echo '0 0 0'; fi").trim();
            if (strTrim != null && !strTrim.isEmpty()) {
                String[] strArrSplit = strTrim.split("\\s+");
                if (strArrSplit.length >= 3) {
                    iArr[0] = Integer.parseInt(strArrSplit[0]);
                    iArr[1] = Integer.parseInt(strArrSplit[1]) == 1 ? 1 : 0;
                    iArr[2] = Integer.parseInt(strArrSplit[2]) > 0 ? 1 : 0;
                }
            }
            return iArr;
        } catch (Exception e) {
            log("getGameStatus error: " + e.getMessage());
            return iArr;
        }
    }

    private int waitForGameReady(int i, int i2) throws InterruptedException {
        int i3;
        int i4;
        long jCurrentTimeMillis = System.currentTimeMillis();
        log("等待游戏主进程和核心库稳定（超时=" + i + "ms，稳定窗口=" + i2 + "ms）...");
        int i5 = -1;
        long j = 0;
        while (System.currentTimeMillis() - jCurrentTimeMillis < i) {
            int[] gameStatus = getGameStatus();
            int i6 = gameStatus[0];
            boolean z = gameStatus[1] == 1;
            long jCurrentTimeMillis2 = System.currentTimeMillis();
            if (i6 > 0 && z && isProcessAlive(i6)) {
                if (i6 != i5) {
                    log("检测到已加载核心库的游戏主进程: PID=" + i6 + "，开始稳定计时");
                    i5 = i6;
                    j = jCurrentTimeMillis2;
                }
                i4 = -1;
                long j2 = jCurrentTimeMillis2 - j;
                i3 = i6;
                if (j2 >= i2) {
                    int[] gameStatus2 = getGameStatus();
                    if (gameStatus2[0] == i5 && gameStatus2[1] == 1 && isProcessAlive(i5) && isGameInputReady(i5)) {
                        log("游戏注入条件已稳定: PID=" + i5 + "，稳定=" + j2 + "ms");
                        return i5;
                    }
                    log("最终就绪检查发生变化，重新等待稳定窗口");
                    i5 = i4;
                    j = 0;
                }
            } else {
                i3 = i6;
                i4 = -1;
                if (i5 > 0) {
                    log("游戏进程或核心库状态发生变化，稳定计时清零");
                    i5 = i4;
                    j = 0;
                }
            }
            long jCurrentTimeMillis3 = (System.currentTimeMillis() - jCurrentTimeMillis) / 1000;
            if (i3 <= 0) {
                updateUI("等待游戏启动... " + jCurrentTimeMillis3 + "秒", ((int) (jCurrentTimeMillis3 / 2)) + 20);
            } else if (!z || i5 != i3) {
                updateUI("等待核心库加载... " + jCurrentTimeMillis3 + "秒", ((int) jCurrentTimeMillis3) + 30);
            } else {
                updateUI("等待游戏稳定... " + ((Math.max(0L, i2 - (jCurrentTimeMillis2 - j)) + 999) / 1000) + "秒", 45);
            }
            try {
                Thread.sleep(300L);
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
                log("等待游戏就绪被中断");
                return i4;
            }
        }
        log("等待游戏就绪超时，取消本次注入");
        return -1;
    }

    private boolean isInjectionTargetReady(int i) {
        if (i > 0 && isProcessAlive(i)) {
            int[] gameStatus = getGameStatus();
            if (gameStatus[0] == i && gameStatus[1] == 1) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean requiresEarlyLaunch() {
        return GameRegionState.current() == GameRegion.CN;
    }

    private boolean hasPreparedGameIdentity(int i) {
        String gameProcessIdentity = readGameProcessIdentity(i);
        return gameProcessIdentity != null && gameProcessIdentity.equals(getSharedPreferences(RUNTIME_STATE_PREFS, 0).getString(EARLY_LAUNCH_IDENTITY_KEY, ""));
    }

    private boolean canOpenPreparedGame() throws NumberFormatException {
        int gamePid = getGamePid();
        String gameProcessIdentity = readGameProcessIdentity(gamePid);
        boolean z = false;
        String string = getSharedPreferences(RUNTIME_STATE_PREFS, 0).getString(EARLY_LAUNCH_IDENTITY_KEY, "");
        SharedMemManager sharedMemManager = this.shmManager;
        if (sharedMemManager != null && sharedMemManager.isCppConnected() && LocalSocketIPC.getInstance().isClientConnectedToProcess(gamePid, true)) {
            z = true;
        }
        SharedMemManager sharedMemManager2 = this.shmManager;
        return GameLaunchGuard.allows(gameProcessIdentity, string, z, sharedMemManager2 == null ? null : sharedMemManager2.getRuntimeLaunchStatus(), SystemClock.elapsedRealtime());
    }

    private InjectionTargetReadiness.Snapshot readHeadlessInjectionTarget(int i) {
        execCommandDetailed("am unfreeze " + this.TARGET_PACKAGE + " >/dev/null 2>&1 || true", 2000L);
        InjectionTargetReadiness.Snapshot injectionTargetSnapshot = readInjectionTargetSnapshot(i);
        if (injectionTargetSnapshot.pid != i || injectionTargetSnapshot.startTicks <= 0 || injectionTargetSnapshot.tracerPid != 0 || injectionTargetSnapshot.freezeState == 1 || injectionTargetSnapshot.processState == '?' || injectionTargetSnapshot.processState == 'T' || injectionTargetSnapshot.processState == 't' || injectionTargetSnapshot.processState == 'Z') {
            return null;
        }
        return injectionTargetSnapshot;
    }

    private boolean awaitLaunchModule(int i, long j, long j2) throws InterruptedException {
        SharedMemManager sharedMemManager;
        String gameProcessIdentity = readGameProcessIdentity(i);
        if (gameProcessIdentity == null) {
            return false;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime() + j2;
        while (isCurrentLoad(j) && SystemClock.elapsedRealtime() < jElapsedRealtime && getGamePid() == i && gameProcessIdentity.equals(readGameProcessIdentity(i)) && (sharedMemManager = this.shmManager) != null && sharedMemManager.isCppConnected() && LocalSocketIPC.getInstance().isClientConnectedToProcess(i, true)) {
            GameLaunchGuard.Status runtimeLaunchStatus = this.shmManager.getRuntimeLaunchStatus();
            if (runtimeLaunchStatus != null && runtimeLaunchStatus.fresh(SystemClock.elapsedRealtime())) {
                return true;
            }
            if (runtimeLaunchStatus != null && runtimeLaunchStatus.failed()) {
                runtimeRecord("INJECT", "LAUNCH_MODULE", "FAIL", "reason=" + runtimeLaunchStatus.reason);
                return false;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    private String findHeadlessPreparationAuthority() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(this.TARGET_PACKAGE, 8);
        if (packageInfo.providers == null) {
            return null;
        }
        for (ProviderInfo providerInfo : packageInfo.providers) {
            if (GameLaunchGuard.isPreparationProvider(this.TARGET_PACKAGE, providerInfo.name, providerInfo.processName, providerInfo.authority, providerInfo.enabled) && providerInfo.applicationInfo != null && providerInfo.applicationInfo.enabled) {
                return providerInfo.authority;
            }
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:122:0x035d, code lost:
    
        if (isCurrentLoad(r23) != false) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:134:0x0394, code lost:
    
        if (isCurrentLoad(r23) != false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:144:0x03f9, code lost:
    
        if (isCurrentLoad(r23) != false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x0462, code lost:
    
        if (isCurrentLoad(r23) != false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:167:0x04d1, code lost:
    
        if (isCurrentLoad(r23) != false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:182:0x0546, code lost:
    
        if (isCurrentLoad(r23) != false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:200:0x05c8, code lost:
    
        if (isCurrentLoad(r23) != false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:217:0x0609, code lost:
    
        if (isCurrentLoad(r23) != false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00a7, code lost:
    
        if (isCurrentLoad(r23) != false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00a9, code lost:
    
        r5 = r23;
        r3 = r25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0180, code lost:
    
        if (isCurrentLoad(r23) != false) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0182, code lost:
    
        r5 = r23;
        r3 = r25;
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x01fa, code lost:
    
        if (isCurrentLoad(r23) != false) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x02ac, code lost:
    
        if (isCurrentLoad(r23) != false) goto L54;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:235:0x0659  */
    /* JADX WARN: Removed duplicated region for block: B:237:0x0662 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:240:0x066e  */
    /* JADX WARN: Removed duplicated region for block: B:244:0x06bc  */
    /* JADX WARN: Removed duplicated region for block: B:248:0x06c7  */
    /* JADX WARN: Removed duplicated region for block: B:250:0x06d0 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:253:0x06dc  */
    /* JADX WARN: Removed duplicated region for block: B:257:0x072a  */
    /* JADX WARN: Removed duplicated region for block: B:288:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r0v20, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r22v0, types: [com.android.support.MainActivity] */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v10 */
    /* JADX WARN: Type inference failed for: r2v16 */
    /* JADX WARN: Type inference failed for: r2v17 */
    /* JADX WARN: Type inference failed for: r2v19 */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v20 */
    /* JADX WARN: Type inference failed for: r2v21 */
    /* JADX WARN: Type inference failed for: r2v22, types: [boolean] */
    /* JADX WARN: Type inference failed for: r2v23 */
    /* JADX WARN: Type inference failed for: r2v24 */
    /* JADX WARN: Type inference failed for: r2v25 */
    /* JADX WARN: Type inference failed for: r2v26 */
    /* JADX WARN: Type inference failed for: r2v27 */
    /* JADX WARN: Type inference failed for: r2v28, types: [int] */
    /* JADX WARN: Type inference failed for: r2v29 */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v30 */
    /* JADX WARN: Type inference failed for: r2v4 */
    /* JADX WARN: Type inference failed for: r2v40, types: [android.content.SharedPreferences$Editor] */
    /* JADX WARN: Type inference failed for: r2v42, types: [boolean] */
    /* JADX WARN: Type inference failed for: r2v44 */
    /* JADX WARN: Type inference failed for: r2v45 */
    /* JADX WARN: Type inference failed for: r2v5 */
    /* JADX WARN: Type inference failed for: r2v50, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v51 */
    /* JADX WARN: Type inference failed for: r2v56, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v57 */
    /* JADX WARN: Type inference failed for: r2v6, types: [boolean] */
    /* JADX WARN: Type inference failed for: r2v62, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v63 */
    /* JADX WARN: Type inference failed for: r2v7, types: [boolean] */
    /* JADX WARN: Type inference failed for: r2v70 */
    /* JADX WARN: Type inference failed for: r2v71 */
    /* JADX WARN: Type inference failed for: r2v72 */
    /* JADX WARN: Type inference failed for: r2v73 */
    /* JADX WARN: Type inference failed for: r2v74 */
    /* JADX WARN: Type inference failed for: r2v75 */
    /* JADX WARN: Type inference failed for: r2v76 */
    /* JADX WARN: Type inference failed for: r2v77 */
    /* JADX WARN: Type inference failed for: r2v78 */
    /* JADX WARN: Type inference failed for: r2v79 */
    /* JADX WARN: Type inference failed for: r2v80 */
    /* JADX WARN: Type inference failed for: r2v81 */
    /* JADX WARN: Type inference failed for: r2v82 */
    /* JADX WARN: Type inference failed for: r2v83 */
    /* JADX WARN: Type inference failed for: r2v84 */
    /* JADX WARN: Type inference failed for: r2v85 */
    /* JADX WARN: Type inference failed for: r2v86 */
    /* JADX WARN: Type inference failed for: r2v87 */
    /* JADX WARN: Type inference failed for: r2v88 */
    /* JADX WARN: Type inference failed for: r2v89 */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* JADX WARN: Type inference failed for: r2v90 */
    /* JADX WARN: Type inference failed for: r2v91 */
    /* JADX WARN: Type inference failed for: r2v92 */
    /* JADX WARN: Type inference failed for: r2v93 */
    /* JADX WARN: Type inference failed for: r2v94 */
    /* JADX WARN: Type inference failed for: r3v10, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v11, types: [android.content.SharedPreferences] */
    /* JADX WARN: Type inference failed for: r4v15 */
    /* JADX WARN: Type inference failed for: r4v16 */
    /* JADX WARN: Type inference failed for: r4v17 */
    /* JADX WARN: Type inference failed for: r4v18 */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v20 */
    /* JADX WARN: Type inference failed for: r4v22 */
    /* JADX WARN: Type inference failed for: r4v23 */
    /* JADX WARN: Type inference failed for: r4v24 */
    /* JADX WARN: Type inference failed for: r4v25 */
    /* JADX WARN: Type inference failed for: r4v26 */
    /* JADX WARN: Type inference failed for: r4v27 */
    /* JADX WARN: Type inference failed for: r4v29, types: [boolean] */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v32, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v34 */
    /* JADX WARN: Type inference failed for: r4v35 */
    /* JADX WARN: Type inference failed for: r4v37 */
    /* JADX WARN: Type inference failed for: r4v38 */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v40, types: [android.content.SharedPreferences] */
    /* JADX WARN: Type inference failed for: r4v41 */
    /* JADX WARN: Type inference failed for: r4v42, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v47 */
    /* JADX WARN: Type inference failed for: r4v48 */
    /* JADX WARN: Type inference failed for: r4v49, types: [android.content.SharedPreferences] */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v50 */
    /* JADX WARN: Type inference failed for: r4v51, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v52, types: [android.content.SharedPreferences] */
    /* JADX WARN: Type inference failed for: r4v53 */
    /* JADX WARN: Type inference failed for: r4v54, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v55, types: [android.content.SharedPreferences] */
    /* JADX WARN: Type inference failed for: r4v56 */
    /* JADX WARN: Type inference failed for: r4v57, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v59 */
    /* JADX WARN: Type inference failed for: r4v6, types: [android.content.SharedPreferences] */
    /* JADX WARN: Type inference failed for: r4v60 */
    /* JADX WARN: Type inference failed for: r4v61 */
    /* JADX WARN: Type inference failed for: r4v62 */
    /* JADX WARN: Type inference failed for: r4v63 */
    /* JADX WARN: Type inference failed for: r4v64 */
    /* JADX WARN: Type inference failed for: r4v65 */
    /* JADX WARN: Type inference failed for: r4v66 */
    /* JADX WARN: Type inference failed for: r4v67 */
    /* JADX WARN: Type inference failed for: r4v68 */
    /* JADX WARN: Type inference failed for: r5v12, types: [boolean] */
    /* JADX WARN: Type inference failed for: r5v13, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r6v6, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v10 */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v12 */
    /* JADX WARN: Type inference failed for: r8v13 */
    /* JADX WARN: Type inference failed for: r8v16, types: [int] */
    /* JADX WARN: Type inference failed for: r8v19, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v2 */
    /* JADX WARN: Type inference failed for: r8v22 */
    /* JADX WARN: Type inference failed for: r8v23 */
    /* JADX WARN: Type inference failed for: r8v24 */
    /* JADX WARN: Type inference failed for: r8v25 */
    /* JADX WARN: Type inference failed for: r8v26 */
    /* JADX WARN: Type inference failed for: r8v27 */
    /* JADX WARN: Type inference failed for: r8v28 */
    /* JADX WARN: Type inference failed for: r8v29 */
    /* JADX WARN: Type inference failed for: r8v3 */
    /* JADX WARN: Type inference failed for: r8v34 */
    /* JADX WARN: Type inference failed for: r8v35 */
    /* JADX WARN: Type inference failed for: r8v36 */
    /* JADX WARN: Type inference failed for: r8v37 */
    /* JADX WARN: Type inference failed for: r8v38 */
    /* JADX WARN: Type inference failed for: r8v39 */
    /* JADX WARN: Type inference failed for: r8v4, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v40 */
    /* JADX WARN: Type inference failed for: r8v41 */
    /* JADX WARN: Type inference failed for: r8v42 */
    /* JADX WARN: Type inference failed for: r8v43 */
    /* JADX WARN: Type inference failed for: r8v6, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v8 */
    /* JADX WARN: Type inference failed for: r8v9 */
    /* JADX WARN: Type inference failed for: r9v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v1 */
    /* JADX WARN: Type inference failed for: r9v10 */
    /* JADX WARN: Type inference failed for: r9v11 */
    /* JADX WARN: Type inference failed for: r9v12 */
    /* JADX WARN: Type inference failed for: r9v13 */
    /* JADX WARN: Type inference failed for: r9v14 */
    /* JADX WARN: Type inference failed for: r9v15 */
    /* JADX WARN: Type inference failed for: r9v16 */
    /* JADX WARN: Type inference failed for: r9v17 */
    /* JADX WARN: Type inference failed for: r9v18 */
    /* JADX WARN: Type inference failed for: r9v19, types: [int] */
    /* JADX WARN: Type inference failed for: r9v2 */
    /* JADX WARN: Type inference failed for: r9v21, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r9v26 */
    /* JADX WARN: Type inference failed for: r9v27 */
    /* JADX WARN: Type inference failed for: r9v28 */
    /* JADX WARN: Type inference failed for: r9v29 */
    /* JADX WARN: Type inference failed for: r9v3 */
    /* JADX WARN: Type inference failed for: r9v30 */
    /* JADX WARN: Type inference failed for: r9v31 */
    /* JADX WARN: Type inference failed for: r9v32 */
    /* JADX WARN: Type inference failed for: r9v33 */
    /* JADX WARN: Type inference failed for: r9v34 */
    /* JADX WARN: Type inference failed for: r9v35 */
    /* JADX WARN: Type inference failed for: r9v36 */
    /* JADX WARN: Type inference failed for: r9v37 */
    /* JADX WARN: Type inference failed for: r9v38 */
    /* JADX WARN: Type inference failed for: r9v39 */
    /* JADX WARN: Type inference failed for: r9v4 */
    /* JADX WARN: Type inference failed for: r9v40 */
    /* JADX WARN: Type inference failed for: r9v41 */
    /* JADX WARN: Type inference failed for: r9v5 */
    /* JADX WARN: Type inference failed for: r9v6, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v7, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v8 */
    /* JADX WARN: Type inference failed for: r9v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void runGuardedColdLaunch(final long r23, long r25, java.lang.String r27, boolean r28) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 1842
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.MainActivity.runGuardedColdLaunch(long, long, java.lang.String, boolean):void");
    }

    /* renamed from: lambda$runGuardedColdLaunch$38$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1337lambda$runGuardedColdLaunch$38$comandroidsupportMainActivity(long j) {
        if (!isCurrentLoad(j) || FloatingWindowService.isServiceReady()) {
            return;
        }
        launchFloatingWindowService();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean launchGame() {
        if (!GameRegionState.current().isRuntimeReady()) {
            return false;
        }
        if (requiresEarlyLaunch() && !canOpenPreparedGame()) {
            runtimeRecord("INJECT", "GAME_LAUNCH_GATE", "BLOCKED", "reason=module_not_ready");
            return failInject("防封模块尚未加载成功，已阻止打开游戏；请从金灿灿重新启动");
        }
        if (this.TARGET_ACTIVITY.isEmpty()) {
            Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(this.TARGET_PACKAGE);
            if (launchIntentForPackage == null || launchIntentForPackage.getComponent() == null) {
                return false;
            }
            this.TARGET_ACTIVITY = launchIntentForPackage.getComponent().getClassName();
        }
        if (!this.foregroundHandoffInFlight.compareAndSet(false, true)) {
            log("前台切换已在进行中，跳过重复切换");
            return getGamePid() > 0 && (!requiresEarlyLaunch() || canOpenPreparedGame());
        }
        try {
            int gamePid = getGamePid();
            if (gamePid > 0) {
                if (!requiresEarlyLaunch() || canOpenPreparedGame()) {
                    execCommand("am start -W -n " + this.TARGET_PACKAGE + "/" + this.TARGET_ACTIVITY + " -f 0x20000000 2>/dev/null");
                    try {
                        Thread.sleep(400L);
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                    if (getGamePid() == gamePid && (!requiresEarlyLaunch() || canOpenPreparedGame())) {
                        this.gameForegroundHandoffCompleted = true;
                        log("游戏已切回前台(热切换): am start -W ... -f 0x20000000");
                    }
                    return true;
                }
                return false;
            }
            if (requiresEarlyLaunch()) {
                return failInject("已校验的游戏进程发生变化，已阻止重新启动，请从金灿灿重试");
            }
            String[] strArr = {"monkey -p " + this.TARGET_PACKAGE + " -c android.intent.category.LAUNCHER 1 2>/dev/null", "am start -n " + this.TARGET_PACKAGE + "/" + this.TARGET_ACTIVITY + " 2>/dev/null"};
            for (int i = 0; i < 2; i++) {
                String str = strArr[i];
                execCommand(str);
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException unused2) {
                    Thread.currentThread().interrupt();
                }
                if (getGamePid() > 0) {
                    this.gameForegroundHandoffCompleted = true;
                    log("游戏启动成功(冷启动): " + str);
                    return true;
                }
            }
            return false;
        } finally {
            this.foregroundHandoffInFlight.set(false);
        }
    }

    private boolean runResourceLoader(int i, String str, String str2) {
        return runResourceLoader(i, str, str2, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:108:0x0398  */
    /* JADX WARN: Removed duplicated region for block: B:111:0x03a1  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x03e0  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x03e6 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:127:0x03e8 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:134:0x03f7 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:141:0x0405 A[Catch: all -> 0x03d5, TRY_ENTER, TryCatch #8 {all -> 0x03d5, blocks: (B:118:0x03b8, B:141:0x0405, B:147:0x0411, B:149:0x0431, B:151:0x0437, B:152:0x043f), top: B:234:0x03b8 }] */
    /* JADX WARN: Removed duplicated region for block: B:152:0x043f A[Catch: all -> 0x03d5, TRY_LEAVE, TryCatch #8 {all -> 0x03d5, blocks: (B:118:0x03b8, B:141:0x0405, B:147:0x0411, B:149:0x0431, B:151:0x0437, B:152:0x043f), top: B:234:0x03b8 }] */
    /* JADX WARN: Removed duplicated region for block: B:154:0x0447 A[Catch: all -> 0x058b, TRY_ENTER, TryCatch #2 {all -> 0x058b, blocks: (B:132:0x03f3, B:154:0x0447, B:156:0x046a), top: B:223:0x03f3 }] */
    /* JADX WARN: Removed duplicated region for block: B:173:0x04db  */
    /* JADX WARN: Removed duplicated region for block: B:230:0x035b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0359  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x0361 A[Catch: all -> 0x058f, TRY_LEAVE, TryCatch #6 {all -> 0x058f, blocks: (B:85:0x0361, B:83:0x035b), top: B:230:0x035b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean runResourceLoader(int r32, java.lang.String r33, java.lang.String r34, boolean r35) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 1615
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.MainActivity.runResourceLoader(int, java.lang.String, java.lang.String, boolean):boolean");
    }

    private boolean failInject(String str) {
        this.lastInjectFailureReason = str;
        return false;
    }

    private String extractInjectedMemfdName(String str) {
        String strGroup = null;
        if (str != null && !str.isEmpty()) {
            Matcher matcher = MEMFD_NAME_PATTERN.matcher(str);
            while (matcher.find()) {
                strGroup = matcher.group(1);
            }
        }
        return strGroup;
    }

    private boolean hasExactInjectedMemfdMapping(int i, String str) {
        if (i > 0 && str != null && SAFE_MEMFD_NAME_PATTERN.matcher(str).matches()) {
            CommandResult commandResultExecCommandDetailed = execCommandDetailed("grep -F " + shellQuote("/memfd:" + str) + " /proc/" + i + "/maps >/dev/null 2>&1 && echo __MAP_ENTRY_OK__", 3000L);
            if (!commandResultExecCommandDetailed.timedOut && commandResultExecCommandDetailed.exitCode == 0 && commandResultExecCommandDetailed.getMergedOutput().contains("__MAP_ENTRY_OK__")) {
                return true;
            }
        }
        return false;
    }

    private String resolveGameDataDir(int i) {
        this.rootMountNamespaceFile = null;
        if (i <= 0) {
            this.lastGameDataDecision = GameDataLocation.processGone();
            log("游戏数据目录定位失败: " + this.lastGameDataDecision.detail());
            return null;
        }
        String strBuildProbeCommand = GameDataLocation.buildProbeCommand(i, this.TARGET_PACKAGE, getApplicationInfo().dataDir, getApplicationInfo().nativeLibraryDir);
        GameDataLocation.Decision decisionProbeGameDataLocation = probeGameDataLocation(strBuildProbeCommand, null);
        if (!decisionProbeGameDataLocation.resolved()) {
            decisionProbeGameDataLocation = probeGameDataLocationWithMountMaster(strBuildProbeCommand, decisionProbeGameDataLocation);
        }
        if (decisionProbeGameDataLocation.resolved() && !applyRootMountNamespace(decisionProbeGameDataLocation)) {
            decisionProbeGameDataLocation = GameDataLocation.escapeRejected(decisionProbeGameDataLocation);
        }
        this.lastGameDataDecision = decisionProbeGameDataLocation;
        if (!decisionProbeGameDataLocation.resolved()) {
            log("游戏数据目录定位失败: " + decisionProbeGameDataLocation.detail());
            runtimeRecord("INJECT", "GAME_DATA_DIR", "FAIL", decisionProbeGameDataLocation.detail());
            return null;
        }
        log("目标数据目录: " + decisionProbeGameDataLocation.dataDir() + " (" + decisionProbeGameDataLocation.detail() + ")");
        runtimeRecord("INJECT", "GAME_DATA_DIR", "OK", decisionProbeGameDataLocation.detail());
        return decisionProbeGameDataLocation.dataDir();
    }

    private GameDataLocation.Decision probeGameDataLocation(String str, String[] strArr) {
        GameDataLocation.Decision decision = null;
        int i = 1;
        while (i <= 2) {
            CommandResult commandResultExecCommandWithPrefix = execCommandWithPrefix(strArr == null ? getSuPrefix() : strArr, str, 8000L);
            GameDataLocation.Decision decisionDecide = GameDataLocation.decide(GameDataLocation.parse(commandResultExecCommandWithPrefix.getMergedOutput()), this.TARGET_PACKAGE);
            if (decisionDecide.resolved() || !commandResultExecCommandWithPrefix.timedOut) {
                return decisionDecide;
            }
            log("游戏数据目录探测超时，重试一次");
            i++;
            decision = decisionDecide;
        }
        return decision;
    }

    private GameDataLocation.Decision probeGameDataLocationWithMountMaster(String str, GameDataLocation.Decision decision) {
        if (!"namespace_isolated".equals(decision.code()) && !"dir_not_found".equals(decision.code())) {
            return decision;
        }
        String[] suPrefix = getSuPrefix();
        for (String[] strArr : SuBinaryPathPolicy.mountMasterPrefixes(this.suBinaryPath)) {
            if (!Arrays.equals(strArr, suPrefix)) {
                log("数据目录不可见，改用 su 参数重试: " + String.join(" ", strArr));
                GameDataLocation.Decision decisionProbeGameDataLocation = probeGameDataLocation(str, strArr);
                if (decisionProbeGameDataLocation.resolved()) {
                    adoptSuPrefix(strArr);
                    return decisionProbeGameDataLocation;
                }
                if ("namespace_isolated".equals(decisionProbeGameDataLocation.code())) {
                    decision = decisionProbeGameDataLocation;
                }
            }
        }
        return decision;
    }

    private boolean applyRootMountNamespace(GameDataLocation.Decision decision) {
        String strMountNamespaceFile = decision.escape().mountNamespaceFile();
        if (strMountNamespaceFile == null) {
            this.rootMountNamespaceFile = null;
            return true;
        }
        this.rootMountNamespaceFile = strMountNamespaceFile;
        CommandResult commandResultExecCommandDetailed = execCommandDetailed("[ -d " + shellQuote(decision.dataDir()) + " ] && echo __GAME_DATA_DIR_EXISTS__", 5000L);
        if (commandResultExecCommandDetailed.timedOut || commandResultExecCommandDetailed.exitCode != 0 || !commandResultExecCommandDetailed.getMergedOutput().contains("__GAME_DATA_DIR_EXISTS__")) {
            this.rootMountNamespaceFile = null;
            log("切到" + decision.escape().description() + "挂载命名空间后仍看不到数据目录，已回退");
            return false;
        }
        log("Root 命令已切到" + decision.escape().description() + "挂载命名空间: " + strMountNamespaceFile);
        runtimeRecord("INJECT", "ROOT_NAMESPACE", "OK", "mount_ns=" + strMountNamespaceFile);
        return true;
    }

    private String gameDataFailureMessage() {
        GameDataLocation.Decision decision = this.lastGameDataDecision;
        if (decision == null) {
            return GameDataLocation.processGone().userMessage();
        }
        return decision.userMessage();
    }

    private String resolveResourceSourceDir() {
        return RUNTIME_STAGING_DIR;
    }

    private File runtimeStagingRoot() {
        return new File(getFilesDir(), RUNTIME_STAGING_DIR);
    }

    private boolean isPrivateRuntimePath(String str) throws IOException {
        if (str != null && !str.isEmpty()) {
            try {
                File canonicalFile = runtimeStagingRoot().getCanonicalFile();
                File canonicalFile2 = new File(str).getCanonicalFile();
                File parentFile = canonicalFile2.getParentFile();
                if (parentFile != null && parentFile.equals(canonicalFile)) {
                    if (canonicalFile2.getName().matches("[A-Za-z0-9._-]+")) {
                        return true;
                    }
                }
            } catch (IOException | SecurityException unused) {
            }
        }
        return false;
    }

    private boolean isPrivateRuntimeDirectory(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new File(str).getCanonicalFile().equals(runtimeStagingRoot().getCanonicalFile());
            } catch (IOException | SecurityException unused) {
            }
        }
        return false;
    }

    private String materializeAsset(String str, String str2) throws IOException {
        if (!isPrivateRuntimeDirectory(str2) || str == null || !str.matches("[A-Za-z0-9._-]+")) {
            log("拒绝在非助手私有目录准备资源");
            return null;
        }
        File file = new File(str2, str);
        try {
            InputStream inputStreamOpen = getAssets().open(resolveResourceSourceDir() + "/" + str);
            try {
                RuntimeAssetStager.stage(inputStreamOpen, file);
                if (inputStreamOpen != null) {
                    inputStreamOpen.close();
                }
                return file.getAbsolutePath();
            } finally {
            }
        } catch (IOException unused) {
            log("资源准备失败: " + str);
            return null;
        }
    }

    private void setSelfOnlyMode(String str) {
        execCommandInAppNamespace("chmod 700 " + shellQuote(str) + " 2>/dev/null");
    }

    private void purgeLegacyArtifacts(String str) {
        GameDataLocation.Decision decision = this.lastGameDataDecision;
        if (str == null || decision == null || !decision.resolved() || !str.equals(decision.dataDir())) {
            log("跳过非本轮探测确认的游戏目录清理");
            return;
        }
        String[] strArr = {"libdemo.so", "libdemo_compat.so", "libinject.so", "libinject_x86_64.so", ".native-artifact", ".native-artifact.anchor", ".ipcb_v2_cand", ".ipcb_v2_fml", ".ipcb_v2_test"};
        File file = new File(getFilesDir(), "moved-from-game");
        if (!file.isDirectory() && !file.mkdirs()) {
            log("无法创建迁出目录，游戏目录遗留文件保持不动");
            return;
        }
        String strShellQuote = shellQuote(file.getAbsolutePath());
        StringBuilder sb = new StringBuilder("stamp=$(date +%s); ");
        for (int i = 0; i < 9; i++) {
            String str2 = strArr[i];
            String strShellQuote2 = shellQuote(str + "/" + str2);
            sb.append("if [ -e ").append(strShellQuote2).append(" ]; then mv -f ").append(strShellQuote2).append(' ').append(shellQuote(file.getAbsolutePath() + "/" + str2 + ".")).append("\"$stamp\"; fi; ");
        }
        sb.append("for path in ").append(shellQuote(str)).append("/.ipcb_v2_cand.tmp.* ").append(shellQuote(str)).append("/.ipcb_v2_fml.tmp.* ").append(shellQuote(str)).append("/.ipcb_v2_test.tmp.*; do [ -f \"$path\" ] || continue; name=${path##*/}; printf '%s\\n' \"$name\" | grep -Eq '^\\.ipcb_v2_(cand|fml|test)\\.tmp\\.[0-9]+$' || continue; mv -f \"$path\" ").append(strShellQuote).append("/\"$name.$stamp\"; done; echo __DONE__");
        execCommand(sb.toString());
        log("历史遗留文件已迁出游戏目录");
    }

    private String[] prepareRuntimeResources(int i, String str, String str2) throws IOException {
        String absolutePath = runtimeStagingRoot().getAbsolutePath();
        File file = new File(absolutePath);
        if (!file.isDirectory() && !file.mkdirs()) {
            showErrorAndExit("资源目录准备失败");
            return null;
        }
        if (!isPrivateRuntimeDirectory(absolutePath)) {
            showErrorAndExit("资源目录校验失败，请重新打开助手应用后再试");
            return null;
        }
        setSelfOnlyMode(file.getAbsolutePath());
        String strMaterializeAsset = materializeAsset(str, absolutePath);
        if (strMaterializeAsset == null) {
            showErrorAndExit("资源文件准备失败，请重新打开助手应用后再试");
            return null;
        }
        String strMaterializeAsset2 = materializeAsset(str2, absolutePath);
        if (strMaterializeAsset2 == null) {
            showErrorAndExit("加载组件准备失败，请重新打开助手应用后再试");
            return null;
        }
        if (!isPrivateRuntimePath(strMaterializeAsset) || !isPrivateRuntimePath(strMaterializeAsset2)) {
            showErrorAndExit("资源路径校验失败，请重新打开助手应用后再试");
            return null;
        }
        String strStagedNativeArtifactMode = Android17CompatibilityPolicy.stagedNativeArtifactMode();
        String[] strArr = {strMaterializeAsset, strMaterializeAsset2};
        for (int i2 = 0; i2 < 2; i2++) {
            if (!execCommandInAppNamespace("chmod " + strStagedNativeArtifactMode + " " + shellQuote(strArr[i2]) + " && echo __OK__").trim().contains("__OK__")) {
                showErrorAndExit("资源权限设置失败，请重启设备后再试");
                return null;
            }
        }
        log("资源准备完成（已隔离至应用私有目录）");
        if (i > 0) {
            purgeLegacyArtifacts(resolveGameDataDir(i));
        }
        return new String[]{absolutePath, strMaterializeAsset, strMaterializeAsset2};
    }

    private void cleanupStagedResource(String str) {
        if (!isPrivateRuntimePath(str) || !isPrivateRuntimePath(str + ".anchor")) {
            log("跳过非助手私有目录资源清理");
        } else {
            execCommandInAppNamespace("rm -f " + shellQuote(str) + " " + shellQuote(str + ".anchor") + " 2>/dev/null");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0096  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x009d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void runInjectionScript(final long r19, long r21) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 823
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.MainActivity.runInjectionScript(long, long):void");
    }

    /* renamed from: lambda$runInjectionScript$40$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1338lambda$runInjectionScript$40$comandroidsupportMainActivity(long j) {
        if (isCurrentLoad(j)) {
            try {
                if (FloatingWindowService.isServiceReady()) {
                    return;
                }
                launchFloatingWindowService();
            } catch (Exception e) {
                log("提前启动悬浮窗服务异常: " + e.getMessage());
            }
        }
    }

    private void onInjectionSuccess(final long j) {
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity.6
            @Override // java.lang.Runnable
            public void run() {
                if (MainActivity.this.isCurrentLoad(j)) {
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.applyMainConnectionState(mainActivity.runtimeConnectedPhase());
                    MainActivity.this.verifyWindowExpectedSinceMs = 0L;
                    if (!MainActivity.this.checkOverlayPermission()) {
                        MainActivity.this.log("⚠ 已连接但悬浮窗权限未开启：窗口无法显示，已在状态卡标出");
                        MainActivity.this.reportOverlayBlocked(OverlayPermissionGuidance.Blocker.PERMISSION_MISSING);
                    }
                    boolean zIsServiceReady = FloatingWindowService.isServiceReady();
                    MainActivity mainActivity2 = MainActivity.this;
                    if (!zIsServiceReady) {
                        mainActivity2.log("悬浮窗服务未就绪，尝试启动...");
                        MainActivity.this.startFloatingWindow();
                    } else {
                        mainActivity2.log("悬浮窗服务已就绪（提前启动成功），跳过重复启动");
                    }
                    MainActivity.this.binding.tvFooter.setText("悬浮窗已启动，等待就绪...");
                    MainActivity.this.waitForServiceReadyThenSwitch(j);
                }
            }
        });
    }

    private void handleInjectResult(boolean z, final long j, final long j2) {
        if (isCurrentLoad(j2)) {
            runtimeRecord("INJECT", "PIPELINE", z ? "OK" : "FAIL", "native_inject=" + (z ? 1 : 0));
            new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() throws InterruptedException {
                    this.f$0.m1321lambda$handleInjectResult$42$comandroidsupportMainActivity(j);
                }
            }, "Injection-Property-Expiry").start();
            if (z) {
                log("========== 注入成功 ==========");
                updateUI("组件已连接，等待游戏功能就绪", 90);
                showToast("组件已连接");
                if (isCurrentLoad(j2)) {
                    onInjectionSuccess(j2);
                    return;
                }
                return;
            }
            log("========== 注入失败 ==========");
            final String str = this.lastInjectFailureReason;
            applyMainConnectionState(MainConnectionUiState.Phase.RETRY);
            if (str != null && !str.isEmpty()) {
                runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda28
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1322lambda$handleInjectResult$43$comandroidsupportMainActivity(j2, str);
                    }
                });
                showToast(str);
            } else {
                showToast("连接未完成，请重试");
            }
        }
    }

    /* renamed from: lambda$handleInjectResult$42$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1321lambda$handleInjectResult$42$comandroidsupportMainActivity(long j) throws InterruptedException {
        try {
            Thread.sleep(30000L);
            log(INJECTION_PROPERTIES.runIfCurrent(j, new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda46
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.lambda$handleInjectResult$41();
                }
            }) ? "属性回收已跳过（本版本不写属性）" : "跳过已被替换连接的属性清理");
        } catch (InterruptedException unused) {
            Thread.currentThread().interrupt();
        }
    }

    /* renamed from: lambda$handleInjectResult$43$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1322lambda$handleInjectResult$43$comandroidsupportMainActivity(long j, String str) {
        if (this.binding == null || !isCurrentLoad(j)) {
            return;
        }
        this.binding.tvFooter.setText(str);
    }

    private ExistingRuntimeRecovery.Result waitForTargetRuntime(final int i, final long j, final boolean z, long j2) {
        return ExistingRuntimeRecovery.run(new ExistingRuntimeRecovery.Host() { // from class: com.android.support.MainActivity.7
            private long lastProgressMs;

            @Override // com.android.support.ExistingRuntimeRecovery.Host
            public boolean current() {
                return MainActivity.this.isCurrentLoad(j);
            }

            @Override // com.android.support.ExistingRuntimeRecovery.Host
            public String processIdentity() throws NumberFormatException {
                int gamePid = MainActivity.this.getGamePid();
                int i2 = i;
                if (gamePid == i2) {
                    return MainActivity.this.readGameProcessIdentity(i2);
                }
                return null;
            }

            @Override // com.android.support.ExistingRuntimeRecovery.Host
            public boolean connected() {
                if (MainActivity.this.shmManager == null || !MainActivity.this.shmManager.isCppConnected()) {
                    return false;
                }
                LocalSocketIPC localSocketIPC = LocalSocketIPC.getInstance();
                int i2 = i;
                return localSocketIPC.isClientConnectedToProcess(i2, MainActivity.this.hasExistingRuntimeReconnectHint(i2));
            }

            @Override // com.android.support.ExistingRuntimeRecovery.Host
            public boolean stageBootstrap() {
                return z || MainActivity.this.prepareExistingRuntimeBootstrapDelivery(i);
            }

            @Override // com.android.support.ExistingRuntimeRecovery.Host
            public long nowMs() {
                return SystemClock.elapsedRealtime();
            }

            @Override // com.android.support.ExistingRuntimeRecovery.Host
            public void pause(long j3) throws InterruptedException {
                Thread.sleep(j3);
            }

            @Override // com.android.support.ExistingRuntimeRecovery.Host
            public void progress(long j3) {
                if (j3 - this.lastProgressMs < 5000) {
                    return;
                }
                this.lastProgressMs = j3;
                boolean zRequiresEarlyLaunch = MainActivity.this.requiresEarlyLaunch();
                MainActivity mainActivity = MainActivity.this;
                if (zRequiresEarlyLaunch) {
                    mainActivity.execCommandDetailed("am unfreeze " + MainActivity.this.TARGET_PACKAGE + " >/dev/null 2>&1 || true", 2000L);
                } else {
                    mainActivity.launchGame();
                }
                MainActivity.this.updateUI("等待游戏恢复连接… " + (j3 / 1000) + "秒", 75);
            }
        }, j2, 250L);
    }

    private void handleReconnect(int i, final long j) {
        final String str;
        if (isCurrentLoad(j)) {
            if (requiresEarlyLaunch() && !hasPreparedGameIdentity(i)) {
                failInject("当前进程尚未通过启动前校验，请完全关闭游戏后从金灿灿启动");
                handleInjectResult(false, 0L, j);
                return;
            }
            applyMainConnectionState(MainConnectionUiState.Phase.CONNECTING);
            updateUI("检测到已有连接，正在恢复…", 60);
            if (requiresEarlyLaunch()) {
                execCommandDetailed("am unfreeze " + this.TARGET_PACKAGE + " >/dev/null 2>&1 || true", 2000L);
            } else {
                launchGame();
            }
            initSharedMemory();
            runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda63
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1323lambda$handleReconnect$44$comandroidsupportMainActivity(j);
                }
            });
            ExistingRuntimeRecovery.Result resultWaitForTargetRuntime = waitForTargetRuntime(i, j, false, 35000L);
            if (isCurrentLoad(j)) {
                runtimeRecord("INJECT", "RECONNECT", resultWaitForTargetRuntime.name(), "game_pid=" + i);
                if (resultWaitForTargetRuntime == ExistingRuntimeRecovery.Result.READY) {
                    if (requiresEarlyLaunch() && !awaitLaunchModule(i, j, LAUNCH_MODULE_TIMEOUT_MS)) {
                        failInject("防封模块加载校验未通过，已阻止打开游戏");
                        handleInjectResult(false, 0L, j);
                        return;
                    } else {
                        rememberInjectedRuntime(i);
                        onInjectionSuccess(j);
                        return;
                    }
                }
                if (resultWaitForTargetRuntime == ExistingRuntimeRecovery.Result.CANCELLED) {
                    return;
                }
                if (resultWaitForTargetRuntime == ExistingRuntimeRecovery.Result.TARGET_CHANGED) {
                    str = "游戏进程已变化，请回到游戏后重新检查连接";
                } else if (resultWaitForTargetRuntime == ExistingRuntimeRecovery.Result.BOOTSTRAP_FAILED) {
                    str = "连接材料未能写入，请检查高级权限后重试";
                } else {
                    str = "已有运行时未能恢复连接，已停止重复注入。请完全退出游戏后重新打开，再连接一次";
                }
                log("重连失败: " + resultWaitForTargetRuntime);
                runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda64
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1324lambda$handleReconnect$45$comandroidsupportMainActivity(j, str);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$handleReconnect$44$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1323lambda$handleReconnect$44$comandroidsupportMainActivity(long j) {
        if (isCurrentLoad(j) && checkOverlayPermission() && !FloatingWindowService.isServiceReady()) {
            FloatingWindowService.sReconnectMode = true;
            launchFloatingWindowService();
        }
    }

    /* renamed from: lambda$handleReconnect$45$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1324lambda$handleReconnect$45$comandroidsupportMainActivity(long j, String str) {
        if (this.binding == null || !isCurrentLoad(j)) {
            return;
        }
        applyMainConnectionState(MainConnectionUiState.Phase.RETRY);
        this.binding.tvRootDetail.setText(str);
        this.binding.tvRootDetail.setTextColor(getColor(R.color.ds_warn));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void waitForServiceReadyThenSwitch(final long j) {
        new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() throws InterruptedException {
                this.f$0.m1361xc093ad63(j);
            }
        }).start();
    }

    /* renamed from: lambda$waitForServiceReadyThenSwitch$47$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1361xc093ad63(long j) throws InterruptedException {
        final boolean zLaunchGame;
        int i = 0;
        while (true) {
            if (i >= 5000) {
                break;
            }
            if (!isCurrentLoad(j)) {
                return;
            }
            if (FloatingWindowService.isServiceReady()) {
                log("悬浮窗服务已就绪，" + i + "ms后切换到游戏");
                break;
            } else {
                try {
                    Thread.sleep(100L);
                    i += 100;
                } catch (InterruptedException unused) {
                }
            }
        }
        if (!FloatingWindowService.isServiceReady()) {
            log("警告：等待悬浮窗服务就绪超时(5000ms)，仍然切换");
        }
        if (isCurrentLoad(j)) {
            boolean zIsProcessAlive = isProcessAlive(getGamePid());
            if (this.gameForegroundHandoffCompleted && zIsProcessAlive && (!requiresEarlyLaunch() || canOpenPreparedGame())) {
                log("前台切换已完成且游戏存活，跳过注入成功后的重复切回");
                zLaunchGame = true;
            } else {
                zLaunchGame = launchGame();
            }
            runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1360x8373e944(zLaunchGame);
                }
            });
            if (zLaunchGame) {
                return;
            }
            log("自动切换到游戏失败，保留 XF 界面");
        }
    }

    /* renamed from: lambda$waitForServiceReadyThenSwitch$46$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1360x8373e944(boolean z) {
        String str;
        if (this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        TextView textView = this.binding.tvFooter;
        if (z) {
            str = "控制面板已就绪，已切换到游戏";
        } else {
            str = requiresEarlyLaunch() ? "启动前校验未通过，已阻止打开游戏" : "控制面板已就绪，请手动切换到游戏";
        }
        textView.setText(str);
    }

    private void maybeRestoreShortcutHostInBackground() {
        if (!getSharedPreferences(EmulatorShortcutService.PREF_NAME, 0).getBoolean(EmulatorShortcutService.KEY_ENABLED, false)) {
            runtimeRecord("INJECT", "ACCESSIBILITY_HOST", "SKIP", "shortcut_disabled");
        } else {
            new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() throws InterruptedException {
                    this.f$0.m1326x996da870();
                }
            }, "shortcut-host-restore").start();
        }
    }

    /* renamed from: lambda$maybeRestoreShortcutHostInBackground$48$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1326x996da870() throws InterruptedException {
        boolean zRestoreShortcutAccessibilityIfNeeded = restoreShortcutAccessibilityIfNeeded();
        runtimeRecord("INJECT", "ACCESSIBILITY_HOST", zRestoreShortcutAccessibilityIfNeeded ? "OK" : "SKIP", zRestoreShortcutAccessibilityIfNeeded ? "host=ready" : "host=optional_unavailable");
    }

    private boolean restoreShortcutAccessibilityIfNeeded() throws InterruptedException {
        try {
            int iUserIdFromUid = AccessibilityHostRecoveryPolicy.userIdFromUid(Process.myUid());
            CommandResult commandResultExecCommandDetailed = execCommandDetailed(AccessibilityHostRecoveryPolicy.readEnabledServicesCommand(iUserIdFromUid), 5000L);
            if (commandResultExecCommandDetailed.exitCode == 0 && !commandResultExecCommandDetailed.timedOut) {
                String strTrim = "";
                String strTrim2 = commandResultExecCommandDetailed.stdout == null ? "" : commandResultExecCommandDetailed.stdout.trim();
                boolean zContains = AccessibilityServiceSettings.contains(strTrim2, SHORTCUT_ACCESSIBILITY_COMPONENT);
                boolean zIsServiceRunning = EmulatorShortcutService.isServiceRunning();
                CommandResult commandResultExecCommandDetailed2 = execCommandDetailed(AccessibilityHostRecoveryPolicy.readAccessibilityEnabledCommand(iUserIdFromUid), 5000L);
                if (commandResultExecCommandDetailed2.exitCode == 0 && !commandResultExecCommandDetailed2.timedOut) {
                    if (commandResultExecCommandDetailed2.stdout != null) {
                        strTrim = commandResultExecCommandDetailed2.stdout.trim();
                    }
                    boolean zEquals = "1".equals(strTrim);
                    if (zContains && zEquals && zIsServiceRunning) {
                        log("✓ 无障碍快捷键服务已就绪，无需修复");
                        return true;
                    }
                    String strMerge = AccessibilityServiceSettings.merge(strTrim2, SHORTCUT_ACCESSIBILITY_COMPONENT);
                    if (!AccessibilityServiceSettings.isSafeComponentList(strMerge)) {
                        log("⚠ 无障碍快捷键服务列表包含非法组件格式，拒绝以 Root 写入");
                        return false;
                    }
                    if (!zEquals) {
                        CommandResult commandResultExecCommandDetailed3 = execCommandDetailed(AccessibilityHostRecoveryPolicy.enableAccessibilityCommand(iUserIdFromUid), 5000L);
                        if (commandResultExecCommandDetailed3.exitCode != 0 || commandResultExecCommandDetailed3.timedOut) {
                            log("⚠ 开启无障碍快捷键服务总开关失败，跳过快捷键恢复(不影响注入/HUD)");
                            return false;
                        }
                    }
                    if (zContains && !zIsServiceRunning) {
                        if (!AccessibilityServiceSettings.isSafeComponentList(AccessibilityServiceSettings.remove(strTrim2, SHORTCUT_ACCESSIBILITY_COMPONENT))) {
                            log("⚠ 重绑无障碍快捷键服务时发现非法组件格式，拒绝以 Root 写入");
                            return false;
                        }
                        CommandResult commandResultExecCommandDetailed4 = execCommandDetailed(AccessibilityHostRecoveryPolicy.deleteEnabledServicesCommand(iUserIdFromUid), 5000L);
                        if (commandResultExecCommandDetailed4.exitCode == 0 && !commandResultExecCommandDetailed4.timedOut) {
                            if (!waitForShortcutSettingState(false, iUserIdFromUid)) {
                                log("⚠ 无障碍快捷键服务移除状态未稳定，跳过快捷键恢复(不影响注入/HUD)");
                                return false;
                            }
                        }
                        log("⚠ 重置无障碍快捷键服务失败，跳过快捷键恢复(不影响注入/HUD)");
                        return false;
                    }
                    if (!zContains || !zIsServiceRunning) {
                        CommandResult commandResultExecCommandDetailed5 = execCommandDetailed(AccessibilityHostRecoveryPolicy.writeEnabledServicesCommand(iUserIdFromUid, strMerge), 5000L);
                        if (commandResultExecCommandDetailed5.exitCode == 0) {
                            if (commandResultExecCommandDetailed5.timedOut) {
                            }
                        }
                        log("⚠ 写入无障碍快捷键服务列表失败，跳过快捷键恢复(不影响注入/HUD)");
                        return false;
                    }
                    boolean z = false;
                    boolean zIsServiceRunning2 = false;
                    for (int i = 0; i <= 8000; i += 100) {
                        z = Settings.Secure.getInt(getContentResolver(), "accessibility_enabled", 0) == 1 && AccessibilityServiceSettings.contains(Settings.Secure.getString(getContentResolver(), "enabled_accessibility_services"), SHORTCUT_ACCESSIBILITY_COMPONENT);
                        zIsServiceRunning2 = EmulatorShortcutService.isServiceRunning();
                        if (z && zIsServiceRunning2) {
                            break;
                        }
                        if (i < 8000) {
                            Thread.sleep(100L);
                        }
                    }
                    if (z && zIsServiceRunning2) {
                        log("✓ 无障碍快捷键服务已恢复并绑定");
                        return true;
                    }
                    if (z) {
                        log("⚠ 无障碍快捷键服务已启用，但绑定超时，跳过快捷键恢复(不影响注入/HUD)");
                    } else {
                        log("⚠ 无障碍快捷键服务自动恢复未生效，跳过快捷键恢复(不影响注入/HUD)");
                    }
                    return false;
                }
                log("⚠ 读取无障碍快捷键服务总开关失败，跳过快捷键恢复(不影响注入/HUD)");
                return false;
            }
            log("⚠ 读取无障碍快捷键服务列表失败，跳过快捷键恢复(不影响注入/HUD)");
            return false;
        } catch (Exception e) {
            log("⚠ 无障碍快捷键服务自动恢复异常: " + e.getClass().getSimpleName());
            return false;
        }
    }

    private boolean waitForShortcutSettingState(boolean z, int i) throws InterruptedException {
        int i2 = 0;
        for (int i3 = 0; i3 <= 8000; i3 += 100) {
            CommandResult commandResultExecCommandDetailed = execCommandDetailed(AccessibilityHostRecoveryPolicy.readEnabledServicesCommand(i), 5000L);
            if (commandResultExecCommandDetailed.exitCode != 0 || commandResultExecCommandDetailed.timedOut) {
                break;
            }
            if (AccessibilityServiceSettings.contains(commandResultExecCommandDetailed.stdout == null ? "" : commandResultExecCommandDetailed.stdout.trim(), SHORTCUT_ACCESSIBILITY_COMPONENT) == z) {
                i2++;
                if (i2 >= 3) {
                    return true;
                }
            } else {
                i2 = 0;
            }
            if (i3 < 8000) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException unused) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }

    private boolean checkRootPermission() {
        try {
            String strTrim = execCommand("id").trim();
            if (strTrim.contains("uid=0")) {
                log("Root check OK: " + strTrim);
                return true;
            }
            log("Root check failed: " + strTrim);
            return false;
        } catch (Exception e) {
            log("Root check error: " + e.getMessage());
            return false;
        }
    }

    private String[] getSuPrefix() {
        if (this.suPrefixResolved) {
            return this.cachedSuPrefix;
        }
        synchronized (this.suPrefixLock) {
            if (this.suPrefixResolved) {
                return this.cachedSuPrefix;
            }
            if (SuBinaryPathPolicy.isKernelSuDebugBinary(this.suBinaryPath)) {
                log("su 类型检测: KernelSU ksud debug su 管道模式");
                this.cachedSuPrefix = null;
                this.suPrefixResolved = true;
                return null;
            }
            for (String[] strArr : SuBinaryPathPolicy.mountMasterPrefixes(this.suBinaryPath)) {
                if (suPrefixWorks(strArr)) {
                    log("su 类型检测: " + String.join(" ", strArr) + " ✓");
                    this.cachedSuPrefix = strArr;
                    this.suPrefixResolved = true;
                    return this.cachedSuPrefix;
                }
            }
            String[] strArrPlainPrefix = SuBinaryPathPolicy.plainPrefix(this.suBinaryPath);
            if (suPrefixWorks(strArrPlainPrefix)) {
                log("su 类型检测: " + String.join(" ", strArrPlainPrefix) + " ✓");
                this.cachedSuPrefix = strArrPlainPrefix;
                this.suPrefixResolved = true;
                return this.cachedSuPrefix;
            }
            log("su 类型检测: 使用管道模式");
            this.cachedSuPrefix = null;
            this.suPrefixResolved = true;
            return null;
        }
    }

    private boolean suPrefixWorks(String[] strArr) {
        boolean z = false;
        Process processExec = null;
        try {
            try {
                String[] strArr2 = new String[strArr.length + 1];
                System.arraycopy(strArr, 0, strArr2, 0, strArr.length);
                strArr2[strArr.length] = "echo ok";
                processExec = Runtime.getRuntime().exec(strArr2);
                if ("ok".equals(BoundedProcessProbe.readIdentifier(processExec, 5000L))) {
                    if (processExec.exitValue() == 0) {
                        z = true;
                    }
                }
                if (processExec != null) {
                    processExec.destroy();
                }
                return z;
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
                if (processExec != null) {
                    processExec.destroy();
                }
                return false;
            } catch (Exception unused2) {
                if (processExec != null) {
                    processExec.destroy();
                }
                return false;
            }
        } catch (Throwable th) {
            if (processExec != null) {
                processExec.destroy();
            }
            throw th;
        }
    }

    private void adoptSuPrefix(String[] strArr) {
        synchronized (this.suPrefixLock) {
            this.cachedSuPrefix = strArr;
            this.suPrefixResolved = true;
        }
        log("已改用 su 参数: " + String.join(" ", strArr));
        runtimeRecord("INJECT", "SU_PREFIX", "OK", "prefix=" + String.join("_", strArr));
    }

    private String execCommand(String str) {
        return execCommandDetailed(str).getMergedOutput();
    }

    private String execCommandInAppNamespace(String str) {
        return execCommandDetailedInAppNamespace(str, 35000L).getMergedOutput();
    }

    private static class CommandResult {
        int exitCode;
        String stderr;
        String stdout;
        boolean timedOut;

        private CommandResult() {
            this.stdout = "";
            this.stderr = "";
            this.exitCode = -1;
            this.timedOut = false;
        }

        String getMergedOutput() {
            StringBuilder sb = new StringBuilder();
            String str = this.stdout;
            if (str != null && !str.isEmpty()) {
                sb.append(this.stdout);
                if (!this.stdout.endsWith("\n")) {
                    sb.append('\n');
                }
            }
            String str2 = this.stderr;
            if (str2 != null && !str2.isEmpty()) {
                sb.append(this.stderr);
            }
            return sb.toString();
        }
    }

    private CommandResult execCommandDetailed(String str) {
        return execCommandDetailed(str, 35000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CommandResult execCommandDetailed(String str, long j) {
        return execCommandWithPrefix(getSuPrefix(), str, j);
    }

    private CommandResult execCommandDetailedInAppNamespace(String str, long j) {
        return execCommandWithPrefix(getSuPrefix(), wrapWithAppMountNamespace(str), j, false);
    }

    private String wrapWithRootMountNamespace(String str) {
        String str2 = this.rootMountNamespaceFile;
        return str2 == null ? str : "nsenter --mount=" + shellQuote(str2) + " -- sh -c " + shellQuote(str);
    }

    private String wrapWithAppMountNamespace(String str) {
        String str2 = "/proc/" + Process.myPid() + "/ns/mnt";
        String strShellQuote = shellQuote(str);
        return "if command -v nsenter >/dev/null 2>&1; then exec nsenter --mount=" + shellQuote(str2) + " -- sh -c " + strShellQuote + "; else exec sh -c " + strShellQuote + "; fi";
    }

    private CommandResult execCommandWithPrefix(String[] strArr, String str, long j) {
        return execCommandWithPrefix(strArr, str, j, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v10 */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v3, types: [com.android.support.MainActivity-IA] */
    /* JADX WARN: Type inference failed for: r0v4 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7, types: [java.lang.Process] */
    /* JADX WARN: Type inference failed for: r0v9, types: [java.lang.Process] */
    private CommandResult execCommandWithPrefix(String[] strArr, String str, long j, boolean z) {
        Process process;
        log("Execute: " + str);
        if (z) {
            str = wrapWithRootMountNamespace(str);
        }
        ?? Start = 0;
        Start = 0;
        Start = 0;
        CommandResult commandResult = new CommandResult();
        try {
            try {
                try {
                    if (strArr != null) {
                        String[] strArr2 = new String[strArr.length + 1];
                        System.arraycopy(strArr, 0, strArr2, 0, strArr.length);
                        strArr2[strArr.length] = str;
                        ProcessBuilder processBuilder = new ProcessBuilder(strArr2);
                        processBuilder.redirectErrorStream(true);
                        Start = processBuilder.start();
                    } else {
                        ProcessBuilder processBuilder2 = new ProcessBuilder(SuBinaryPathPolicy.pipeCommand(this.suBinaryPath));
                        processBuilder2.redirectErrorStream(true);
                        Process processStart = processBuilder2.start();
                        DataOutputStream dataOutputStream = new DataOutputStream(processStart.getOutputStream());
                        dataOutputStream.writeBytes(str + "\n");
                        dataOutputStream.writeBytes("exit\n");
                        dataOutputStream.flush();
                        Start = processStart;
                    }
                    Start.getOutputStream().close();
                    if (j <= 0) {
                        j = 35000;
                    }
                    ProcessOutputCollector.Result result = ProcessOutputCollector.read(Start, j, 8388608);
                    commandResult.stdout = result.stdout;
                    commandResult.stderr = result.stderr;
                    if (result.failure != null) {
                        commandResult.stderr += "\nERROR: " + result.failure.getMessage();
                    }
                    commandResult.exitCode = result.exitCode;
                    commandResult.timedOut = result.timedOut;
                    for (String str2 : commandResult.stdout.split("\\r?\\n")) {
                        if (!str2.isEmpty()) {
                            log("  " + str2);
                        }
                    }
                    if (result.truncated) {
                        log("Warning: 命令输出超过记录上限");
                    }
                    log("ExitCode: " + commandResult.exitCode + (commandResult.timedOut ? " (timeout)" : ""));
                    if (Start != 0) {
                        Start.destroy();
                        return commandResult;
                    }
                } catch (InterruptedException unused) {
                    Thread.currentThread().interrupt();
                    commandResult.stderr = "ERROR: command interrupted";
                    commandResult.exitCode = -3;
                    process = Start;
                    if (Start != 0) {
                        process.destroy();
                    }
                }
            } catch (Exception e) {
                log("Error: " + e.getMessage());
                commandResult.stderr = "ERROR: " + e.getMessage();
                commandResult.exitCode = -3;
                if (Start != 0) {
                    process = Start;
                    process.destroy();
                }
            }
            return commandResult;
        } catch (Throwable th) {
            if (Start != 0) {
                Start.destroy();
            }
            throw th;
        }
    }

    private void logInjectFailureDiagnostics(int i, String str, String str2, CommandResult commandResult) throws IOException {
        StringBuilder sb = new StringBuilder(4096);
        diagAppend(sb, "===== 注入失败诊断开始 =====");
        diagAppend(sb, "诊断参数: pid=" + i + ", targetSo=" + str + ", injectTool=" + str2);
        if (commandResult != null) {
            diagAppend(sb, "loader exitCode=" + commandResult.exitCode + ", timedOut=" + commandResult.timedOut);
            diagAppend(sb, "loader输出(截断): " + shortenForLog(commandResult.getMergedOutput(), 8000));
            log("loader exitCode=" + commandResult.exitCode + ", timedOut=" + commandResult.timedOut);
            log("loader原始输出(截断): " + shortenForLog(commandResult.getMergedOutput(), 1600));
        }
        diagAppend(sb, "");
        diagAppend(sb, "[诊断] 基础环境");
        diagAppend(sb, trimAndLimit(execCommand("id; getenforce 2>/dev/null; cat /proc/sys/kernel/yama/ptrace_scope 2>/dev/null"), GAME_STABLE_WINDOW_MS_EMULATOR));
        log("[诊断] 基础环境");
        log(shortenForLog(execCommand("id; getenforce 2>/dev/null; cat /proc/sys/kernel/yama/ptrace_scope 2>/dev/null"), 1200));
        diagAppend(sb, "");
        diagAppend(sb, "[诊断] 目标进程状态");
        diagAppend(sb, trimAndLimit(execCommand("cat /proc/" + i + "/status 2>/dev/null | grep -E 'Name|Pid|PPid|TracerPid|State|Uid|Gid|Seccomp|NoNewPrivs'; cat /proc/" + i + "/attr/current 2>/dev/null || echo 'no attr/current'"), 9000));
        log("[诊断] 目标进程状态");
        log(shortenForLog(execCommand("cat /proc/" + i + "/status 2>/dev/null | grep -E 'Name|Pid|PPid|TracerPid|State|Uid|Gid|Seccomp|NoNewPrivs'; cat /proc/" + i + "/attr/current 2>/dev/null || echo 'no attr/current'"), 1800));
        String strExecCommand = execCommand("echo '[props]'; getprop ro.product.cpu.abi 2>/dev/null; getprop ro.product.cpu.abilist 2>/dev/null; getprop ro.dalvik.vm.native.bridge 2>/dev/null; getprop ro.kernel.qemu 2>/dev/null; echo '[target]'; printf 'cmdline='; tr '\\0' ' ' < /proc/" + i + "/cmdline 2>/dev/null; echo; printf 'exe='; readlink /proc/" + i + "/exe 2>/dev/null || true; echo; echo '[relevant maps]'; grep -E 'libhoudini|libndk_translation|libnativebridge|libil2cpp|libunity|/memfd:' /proc/" + i + "/maps 2>/dev/null || true; echo '[same package pids]'; for p in $(pidof " + this.TARGET_PACKAGE + " 2>/dev/null); do   printf 'pid=%s cmd=' \"$p\"; tr '\\0' ' ' < /proc/$p/cmdline 2>/dev/null; echo;   awk '/^State:|^TracerPid:/{printf \"%s \", $0} END{print \"\"}' /proc/$p/status 2>/dev/null; done");
        diagAppend(sb, "");
        diagAppend(sb, "[诊断] ABI/NativeBridge/Unity 自动快照");
        diagAppend(sb, trimAndLimit(strExecCommand, 18000));
        log("[诊断] ABI/NativeBridge/Unity 自动快照");
        log(shortenForLog(strExecCommand, 2600));
        String strShellQuote = shellQuote(str);
        String strShellQuote2 = shellQuote(str2);
        diagAppend(sb, "");
        diagAppend(sb, "[诊断] SO/注入器权限与SELinux上下文");
        diagAppend(sb, trimAndLimit(execCommandInAppNamespace("ls -l " + strShellQuote + " " + strShellQuote2 + " 2>/dev/null; ls -lZ " + strShellQuote + " " + strShellQuote2 + " 2>/dev/null"), TARGET_INTERACTIVE_TIMEOUT_MS));
        log("[诊断] SO/注入器权限与SELinux上下文");
        log(shortenForLog(execCommandInAppNamespace("ls -l " + strShellQuote + " " + strShellQuote2 + " 2>/dev/null; ls -lZ " + strShellQuote + " " + strShellQuote2 + " 2>/dev/null"), 1800));
        diagAppend(sb, "");
        diagAppend(sb, "[诊断] maps加载情况 (含memfd匿名映射)");
        diagAppend(sb, trimAndLimit(execCommand("cat /proc/" + i + "/maps 2>/dev/null | grep -E '/memfd:' || echo 'maps: no resource/memfd'"), TARGET_INTERACTIVE_TIMEOUT_MS));
        log("[诊断] maps加载情况");
        log(shortenForLog(execCommand("cat /proc/" + i + "/maps 2>/dev/null | grep -E '/memfd:' || echo 'maps: no resource/memfd'"), 1200));
        diagAppend(sb, "");
        diagAppend(sb, "[诊断] 内核拒绝日志(最近20条)");
        diagAppend(sb, trimAndLimit(execCommand("dmesg 2>/dev/null | grep -i -E 'avc:|denied|ptrace|xptrace' | tail -n 20"), TARGET_INTERACTIVE_TIMEOUT_MS));
        log("[诊断] 内核拒绝日志(最近20条)");
        log(shortenForLog(execCommand("dmesg 2>/dev/null | grep -i -E 'avc:|denied|ptrace|xptrace' | tail -n 20"), 2000));
        diagAppend(sb, "===== 注入失败诊断结束 =====");
        String string = sb.toString();
        String strSaveDiagnosticsToFile = saveDiagnosticsToFile(string);
        this.lastInjectDiagnosticsText = string;
        this.lastInjectDiagnosticsFilePath = strSaveDiagnosticsToFile;
        updateDiagnosticsIndicator(strSaveDiagnosticsToFile);
        log("===== 注入失败诊断结束 =====");
    }

    private String shellQuote(String str) {
        if (str == null) {
            return "''";
        }
        return "'" + str.replace("'", "'\\''") + "'";
    }

    private String shortenForLog(String str, int i) {
        if (str == null) {
            return "";
        }
        String strTrim = str.trim();
        return strTrim.length() <= i ? strTrim : strTrim.substring(0, i) + "...(truncated)";
    }

    private String trimAndLimit(String str, int i) {
        if (str == null) {
            return "";
        }
        String strTrim = str.trim();
        return strTrim.length() <= i ? strTrim : strTrim.substring(0, i) + "...(truncated)";
    }

    private void diagAppend(StringBuilder sb, String str) {
        if (sb == null) {
            return;
        }
        if (str == null) {
            str = "";
        }
        sb.append(str).append('\n');
    }

    private void clearLastDiagnosticsIndicator() {
        this.lastInjectDiagnosticsText = null;
        this.lastInjectDiagnosticsFilePath = null;
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1318x26e04a94();
            }
        });
    }

    /* renamed from: lambda$clearLastDiagnosticsIndicator$49$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1318x26e04a94() {
        ActivityMainBinding activityMainBinding = this.binding;
        if (activityMainBinding != null) {
            activityMainBinding.tvRootDetail.setText("");
        }
    }

    private void updateDiagnosticsIndicator(final String str) {
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1359xd32e039e(str);
            }
        });
    }

    /* renamed from: lambda$updateDiagnosticsIndicator$50$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1359xd32e039e(String str) {
        String str2;
        if (this.binding == null) {
            return;
        }
        if (str != null && !str.trim().isEmpty()) {
            str2 = "诊断已保存，点此复制（长按复制路径）";
        } else {
            str2 = "诊断已保存，点此复制";
        }
        this.binding.tvRootDetail.setText(str2);
    }

    private void copyToClipboard(String str, String str2) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService("clipboard");
            if (clipboardManager == null) {
                return;
            }
            clipboardManager.setPrimaryClip(ClipData.newPlainText(str, str2));
        } catch (Exception e) {
            log("copyToClipboard error: " + e.getMessage());
        }
    }

    private String saveDiagnosticsToFile(String str) throws IOException {
        try {
            File externalFilesDir = getExternalFilesDir(null);
            if (externalFilesDir == null) {
                return null;
            }
            File file = new File(externalFilesDir, "diagnostics");
            file.mkdirs();
            File file2 = new File(file, "inject_diag_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".txt");
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            if (str == null) {
                str = "";
            }
            try {
                fileOutputStream.write(str.getBytes(Key.STRING_CHARSET_NAME));
                fileOutputStream.close();
                return file2.getAbsolutePath();
            } finally {
            }
        } catch (Exception e) {
            log("saveDiagnosticsToFile error: " + e.getMessage());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUI(final String str, final int i) {
        final long j = this.ownedInjectionAttempt;
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity.8
            @Override // java.lang.Runnable
            public void run() {
                if (MainActivity.this.binding == null || MainActivity.this.isFinishing() || MainActivity.this.isDestroyed()) {
                    return;
                }
                long j2 = j;
                if (j2 == 0 || MainActivity.this.isCurrentLoad(j2)) {
                    MainActivity.this.binding.tvRootStatus.setText(str);
                    MainActivity.this.binding.loadingProgress.setProgress(i);
                    MainActivity.this.log("UI: " + str + " - " + i + "%");
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showErrorAndExit(final String str) {
        final long j = this.ownedInjectionAttempt;
        log("ERROR: " + str);
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity.9
            @Override // java.lang.Runnable
            public void run() {
                if (MainActivity.this.binding == null || MainActivity.this.isFinishing() || MainActivity.this.isDestroyed()) {
                    return;
                }
                long j2 = j;
                if (j2 == 0 || MainActivity.this.isCurrentLoad(j2)) {
                    FloatingWindowService.safeToast(MainActivity.this, str, 1);
                    MainActivity.this.applyMainConnectionState(MainConnectionUiState.Phase.RETRY);
                    MainActivity.this.binding.tvRootDetail.setText(str);
                }
            }
        });
    }

    private void showToast(final String str) {
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity.10
            @Override // java.lang.Runnable
            public void run() {
                FloatingWindowService.safeToast(MainActivity.this, str, 0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Dbg.d(TAG, str);
        FileLogger fileLogger = FileLogger.getInstance();
        if (fileLogger == null || !fileLogger.isInitialized()) {
            return;
        }
        fileLogger.log(TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runtimeRecord(String str, String str2, String str3, String str4) {
        RuntimeDiagnostics runtimeDiagnostics = RuntimeDiagnostics.get();
        if (runtimeDiagnostics != null) {
            runtimeDiagnostics.record(str, str2, str3, str4);
        }
    }

    private void setupSidebar() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        this.drawerLayout = drawerLayout;
        if (drawerLayout == null) {
            return;
        }
        findViewById(R.id.btnTestNetworkRule);
        final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(false) { // from class: com.android.support.MainActivity.11
            @Override // androidx.activity.OnBackPressedCallback
            public void handleOnBackPressed() {
                MainActivity.this.drawerLayout.closeDrawer(GravityCompat.END);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        this.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() { // from class: com.android.support.MainActivity.12
            @Override // androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener, androidx.drawerlayout.widget.DrawerLayout.DrawerListener
            public void onDrawerSlide(View view, float f) {
                onBackPressedCallback.setEnabled(f > 0.0f);
            }

            @Override // androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener, androidx.drawerlayout.widget.DrawerLayout.DrawerListener
            public void onDrawerOpened(View view) {
                onBackPressedCallback.setEnabled(true);
            }

            @Override // androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener, androidx.drawerlayout.widget.DrawerLayout.DrawerListener
            public void onDrawerClosed(View view) {
                onBackPressedCallback.setEnabled(false);
            }
        });
        View viewFindViewById = findViewById(R.id.btnOpenSidebar);
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda53
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1349lambda$setupSidebar$53$comandroidsupportMainActivity(view);
                }
            });
        }
        View viewFindViewById2 = findViewById(R.id.btnCloseSidebar);
        if (viewFindViewById2 != null) {
            viewFindViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda54
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1350lambda$setupSidebar$54$comandroidsupportMainActivity(view);
                }
            });
        }
        CheckBox checkBox = (CheckBox) findViewById(R.id.cbEnableLogging);
        if (checkBox != null) {
            boolean z = getSharedPreferences("app_settings", 0).getBoolean("logging_enabled", false);
            checkBox.setChecked(z);
            checkBox.setEnabled(true);
            applyLoggingState(z);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda56
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    this.f$0.m1351lambda$setupSidebar$55$comandroidsupportMainActivity(compoundButton, z2);
                }
            });
        }
        Button button = (Button) findViewById(R.id.btnOpenLogDir);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda57
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1352lambda$setupSidebar$56$comandroidsupportMainActivity(view);
                }
            });
        }
        Button button2 = (Button) findViewById(R.id.btnUnbindCard);
        if (button2 != null) {
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda58
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1353lambda$setupSidebar$58$comandroidsupportMainActivity(view);
                }
            });
        }
    }

    private /* synthetic */ TestNetworkRuleDialog.Result lambda$setupSidebar$51(String str, long j) {
        CommandResult commandResultExecCommandDetailedInAppNamespace = execCommandDetailedInAppNamespace(str, j);
        return new TestNetworkRuleDialog.Result(!commandResultExecCommandDetailedInAppNamespace.timedOut && commandResultExecCommandDetailedInAppNamespace.exitCode == 0, commandResultExecCommandDetailedInAppNamespace.getMergedOutput());
    }

    private /* synthetic */ void lambda$setupSidebar$52(View view) {
        this.drawerLayout.closeDrawer(GravityCompat.END);
        this.testNetworkRuleDialog.show(this.TARGET_PACKAGE);
    }

    /* renamed from: lambda$setupSidebar$53$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1349lambda$setupSidebar$53$comandroidsupportMainActivity(View view) {
        this.drawerLayout.openDrawer(GravityCompat.END);
    }

    /* renamed from: lambda$setupSidebar$54$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1350lambda$setupSidebar$54$comandroidsupportMainActivity(View view) {
        this.drawerLayout.closeDrawer(GravityCompat.END);
    }

    /* renamed from: lambda$setupSidebar$55$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1351lambda$setupSidebar$55$comandroidsupportMainActivity(CompoundButton compoundButton, boolean z) {
        getSharedPreferences("app_settings", 0).edit().putBoolean("logging_enabled", z).apply();
        ConfigNamespaceBackupMirror.request(this, ConfigPreferenceNamespaces.APP_SETTINGS);
        applyLoggingState(z);
        showToast(z ? "已开启持续日志记录" : "已关闭日志记录");
    }

    /* renamed from: lambda$setupSidebar$56$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1352lambda$setupSidebar$56$comandroidsupportMainActivity(View view) {
        this.drawerLayout.closeDrawer(GravityCompat.END);
        openLogDirectory();
    }

    /* renamed from: lambda$setupSidebar$58$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1353lambda$setupSidebar$58$comandroidsupportMainActivity(View view) {
        this.drawerLayout.closeDrawer(GravityCompat.END);
        this.mainScreenInteraction.openAdvancedOptions();
        final EditText editText = this.binding.selfServiceUnbindInput;
        editText.requestFocus();
        editText.post(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                View view2 = editText;
                view2.requestRectangleOnScreen(new Rect(0, 0, view2.getWidth(), view2.getHeight()), true);
            }
        });
    }

    private void applyLoggingState(boolean z) {
        FileLogger fileLogger = FileLogger.getInstance();
        if (fileLogger == null) {
            return;
        }
        if (z) {
            if (fileLogger.isInitialized()) {
                return;
            }
            fileLogger.init();
            return;
        }
        fileLogger.close();
    }

    private void openLogDirectory() {
        FileLogger fileLogger = FileLogger.getInstance();
        if (fileLogger == null || !fileLogger.isInitialized()) {
            showToast("请先勾选「持续记录运行日志」");
            return;
        }
        File currentLogFile = fileLogger.getCurrentLogFile();
        if (!currentLogFile.exists() || currentLogFile.length() == 0) {
            showToast("日志文件为空，请等待日志写入");
            return;
        }
        try {
            Uri uriForFile = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", currentLogFile);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(uriForFile, "text/plain");
            intent.addFlags(1);
            showToast("已保存: " + currentLogFile.getName());
            startActivity(Intent.createChooser(intent, "选择打开方式"));
        } catch (Exception unused) {
            showToast("路径: " + currentLogFile.getAbsolutePath());
        }
    }

    private void performUnbindCard(String str) {
        try {
            if (!dispatchUnbind(SelfServiceUnbindClient.normalizeCard(str), new UnbindCompletion() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda59
                @Override // com.android.support.MainActivity.UnbindCompletion
                public final void onComplete(SelfServiceUnbindClient.Result result, String str2) {
                    this.f$0.m1332lambda$performUnbindCard$59$comandroidsupportMainActivity(result, str2);
                }
            })) {
                FloatingWindowService.safeToast(this, "更换设备请求正在处理中，请稍候", 0);
            } else {
                FloatingWindowService.safeToast(this, "正在解除旧设备…", 0);
            }
        } catch (IllegalArgumentException e) {
            FloatingWindowService.safeToast(this, e.getMessage(), 0);
        }
    }

    /* renamed from: lambda$performUnbindCard$59$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1332lambda$performUnbindCard$59$comandroidsupportMainActivity(SelfServiceUnbindClient.Result result, String str) {
        if (str != null) {
            FloatingWindowService.safeToast(this, str, 1);
            return;
        }
        if (result.isSuccess()) {
            resetLocalAuthenticationAfterSuccessfulUnbind();
        }
        FloatingWindowService.safeToast(this, (result.isSuccess() ? "旧设备已解除：" : "处理未完成：") + result.getMessage(), 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestOverlayPermission() {
        try {
            startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())), 1001);
        } catch (RuntimeException e) {
            log("跳转悬浮窗授权页失败，改用应用详情页: " + e.getClass().getSimpleName());
            try {
                startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + getPackageName())));
            } catch (RuntimeException unused) {
                FloatingWindowService.safeToast(this, "无法自动打开设置页，请手动进入 设置-应用-记牌器-权限，开启「显示在其他应用上层」", 1);
            }
        }
    }

    private boolean grantOverlayPermissionViaRoot() throws InterruptedException {
        if (checkOverlayPermission()) {
            return true;
        }
        if (!this.rootStatus) {
            log("无 Root 授权，无法自动开启悬浮窗权限");
            return false;
        }
        try {
            String packageName = getPackageName();
            execCommand("appops set " + packageName + " SYSTEM_ALERT_WINDOW allow");
            execCommand("cmd appops set " + packageName + " SYSTEM_ALERT_WINDOW allow 2>/dev/null");
        } catch (RuntimeException e) {
            log("Root 自动授予悬浮窗权限异常: " + e.getClass().getSimpleName());
        }
        for (int i = 0; i < 12; i++) {
            if (checkOverlayPermission()) {
                return true;
            }
            try {
                Thread.sleep(125L);
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
        }
        return checkOverlayPermission();
    }

    private void repairOverlayPermissionThenConnect() {
        this.binding.btnInject.setEnabled(false);
        this.binding.tvFooter.setText("正在自动开启悬浮窗权限…");
        new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() throws InterruptedException {
                this.f$0.m1336x83ee694d();
            }
        }, "overlay-permission-repair").start();
    }

    /* renamed from: lambda$repairOverlayPermissionThenConnect$61$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1336x83ee694d() throws InterruptedException {
        final boolean zGrantOverlayPermissionViaRoot = grantOverlayPermissionViaRoot();
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1335x46cea52e(zGrantOverlayPermissionViaRoot);
            }
        });
    }

    /* renamed from: lambda$repairOverlayPermissionThenConnect$60$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1335x46cea52e(boolean z) {
        if (this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        this.binding.btnInject.setEnabled(true);
        refreshEnvBar(this.currentMainPhase);
        if (z) {
            log("✓ 连接前已自动补授悬浮窗权限");
            FloatingWindowService.safeToast(this, "悬浮窗权限已自动开启", 0);
            startInjection();
        } else {
            log("⚠ 连接前自动补授悬浮窗权限失败，在 App 内引导用户手动开启");
            this.binding.tvFooter.setText("悬浮窗权限未开启：验证窗口无法显示在游戏画面上");
            this.binding.tvFooter.setTextColor(getColor(R.color.ds_danger));
            showOverlayGuidance(OverlayPermissionGuidance.Blocker.PERMISSION_MISSING, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFloatingWindow() {
        log("startFloatingWindow called");
        if (!checkOverlayPermission()) {
            log("悬浮窗权限未授予，尝试 Root 自动授予...");
            new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda35
                @Override // java.lang.Runnable
                public final void run() throws InterruptedException {
                    this.f$0.m1357lambda$startFloatingWindow$63$comandroidsupportMainActivity();
                }
            }, "overlay-permission-grant").start();
        } else {
            log("悬浮窗权限已授予，启动服务");
            launchFloatingWindowService();
        }
    }

    /* renamed from: lambda$startFloatingWindow$63$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1357lambda$startFloatingWindow$63$comandroidsupportMainActivity() throws InterruptedException {
        final boolean zGrantOverlayPermissionViaRoot = grantOverlayPermissionViaRoot();
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1356lambda$startFloatingWindow$62$comandroidsupportMainActivity(zGrantOverlayPermissionViaRoot);
            }
        });
    }

    /* renamed from: lambda$startFloatingWindow$62$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1356lambda$startFloatingWindow$62$comandroidsupportMainActivity(boolean z) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        refreshEnvBar(this.currentMainPhase);
        if (z) {
            log("✓ 悬浮窗权限已通过 Root 自动授予，启动服务");
            launchFloatingWindowService();
        } else {
            log("⚠ Root 自动授予悬浮窗权限失败，提示用户手动开启");
            FloatingWindowService.safeToast(this, "悬浮窗权限未开启，游戏里不会出现任何窗口。点「运行环境」里的「悬浮窗权限」查看开启步骤", 1);
            markOverlayBlockedOnCard(OverlayPermissionGuidance.Blocker.PERMISSION_MISSING);
        }
    }

    private void startOverlayVisibilityWatch() {
        stopOverlayVisibilityWatch();
        Runnable runnable = new Runnable() { // from class: com.android.support.MainActivity.13
            @Override // java.lang.Runnable
            public void run() {
                if (MainActivity.this.binding == null || MainActivity.this.isFinishing() || MainActivity.this.isDestroyed()) {
                    return;
                }
                MainActivity.this.sampleOverlayEnvironment();
                MainActivity.this.handler.postDelayed(this, MainActivity.OVERLAY_WATCH_INTERVAL_MS);
            }
        };
        this.overlayWatchRunnable = runnable;
        this.handler.post(runnable);
    }

    private void stopOverlayVisibilityWatch() {
        Runnable runnable = this.overlayWatchRunnable;
        if (runnable == null) {
            return;
        }
        this.handler.removeCallbacks(runnable);
        this.overlayWatchRunnable = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sampleOverlayEnvironment() {
        boolean zCheckOverlayPermission = checkOverlayPermission();
        boolean zIsIgnoringBatteryOptimizations = isIgnoringBatteryOptimizations();
        Boolean bool = this.lastOverlayGranted;
        boolean z = false;
        boolean z2 = bool == null || this.lastBackgroundAllowed == null;
        boolean z3 = (!z2 && bool.booleanValue() == zCheckOverlayPermission && this.lastBackgroundAllowed.booleanValue() == zIsIgnoringBatteryOptimizations) ? false : true;
        if (!z2 && !this.lastOverlayGranted.booleanValue() && zCheckOverlayPermission) {
            z = true;
        }
        this.lastOverlayGranted = Boolean.valueOf(zCheckOverlayPermission);
        this.lastBackgroundAllowed = Boolean.valueOf(zIsIgnoringBatteryOptimizations);
        if (z3) {
            log("环境自检：悬浮窗=" + (zCheckOverlayPermission ? "已授权" : "未授权") + " 后台运行=" + (zIsIgnoringBatteryOptimizations ? "已开启" : "未开启"));
            refreshEnvBar(this.currentMainPhase);
        }
        if (z) {
            onOverlayPermissionRestored();
        }
        refreshMainRuntimeState();
        evaluateOverlayVisibility();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MainConnectionUiState.Phase runtimeConnectedPhase() {
        boolean zHasLocalUseSession = hasLocalUseSession();
        boolean zCheckOverlayPermission = checkOverlayPermission();
        SharedMemManager sharedMemManager = this.shmManager;
        boolean z = sharedMemManager != null && sharedMemManager.isMatchActive();
        SharedMemManager sharedMemManager2 = this.shmManager;
        return MainConnectionUiState.connectedPhase(zHasLocalUseSession, zCheckOverlayPermission, z, sharedMemManager2 != null && sharedMemManager2.getRuntimeHookStatus().coreReady);
    }

    private void refreshMainRuntimeState() {
        SharedMemManager sharedMemManager;
        if (this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        MainConnectionUiState.Phase phaseRuntimeConnectedPhase = this.currentMainPhase;
        if (isRuntimeConnected()) {
            phaseRuntimeConnectedPhase = runtimeConnectedPhase();
            if (this.currentMainPhase == MainConnectionUiState.Phase.CONNECTED_OVERLAY_BLOCKED && !hasLocalUseSession() && currentOverlayBlocker() != OverlayPermissionGuidance.Blocker.NONE) {
                phaseRuntimeConnectedPhase = this.currentMainPhase;
            }
        } else if (!injectionLifecycle.isRunning()) {
            if (MainConnectionUiState.isConnectedPhase(this.currentMainPhase)) {
                phaseRuntimeConnectedPhase = MainConnectionUiState.Phase.RECOVERING;
            } else if (this.currentMainPhase == MainConnectionUiState.Phase.CONNECTING) {
                phaseRuntimeConnectedPhase = MainConnectionUiState.Phase.RETRY;
            } else if (this.rootStatus && (this.currentMainPhase == MainConnectionUiState.Phase.READY || this.currentMainPhase == MainConnectionUiState.Phase.NEEDS_OVERLAY_PERMISSION)) {
                phaseRuntimeConnectedPhase = checkOverlayPermission() ? MainConnectionUiState.Phase.READY : MainConnectionUiState.Phase.NEEDS_OVERLAY_PERMISSION;
            }
        }
        if (phaseRuntimeConnectedPhase != this.currentMainPhase) {
            this.binding.tvRootDetail.setText("");
            applyMainConnectionState(phaseRuntimeConnectedPhase);
        }
        if (phaseRuntimeConnectedPhase != MainConnectionUiState.Phase.CONNECTED_INITIALIZING || (sharedMemManager = this.shmManager) == null) {
            return;
        }
        String strUserMessage = sharedMemManager.getRuntimeHookStatus().userMessage();
        if (strUserMessage.contentEquals(this.binding.tvRootDetail.getText())) {
            return;
        }
        this.binding.tvRootDetail.setText(strUserMessage);
    }

    private void onOverlayPermissionRestored() {
        log("✓ 自检到悬浮窗权限已开启");
        dismissOverlayGuidanceDialog();
        FloatingWindowService.safeToast(this, "悬浮窗权限已开启，验证窗口可以正常显示了", 0);
        if (this.currentMainPhase == MainConnectionUiState.Phase.CONNECTED_OVERLAY_BLOCKED || this.currentMainPhase == MainConnectionUiState.Phase.CONNECTED_NEEDS_VERIFY) {
            this.verifyWindowExpectedSinceMs = 0L;
            if (FloatingWindowService.isServiceReady()) {
                return;
            }
            launchFloatingWindowService();
        }
    }

    private void evaluateOverlayVisibility() {
        if (!isConnectedAwaitingVerification()) {
            this.verifyWindowExpectedSinceMs = 0L;
            return;
        }
        OverlayPermissionGuidance.Blocker blockerCurrentOverlayBlocker = currentOverlayBlocker();
        if (blockerCurrentOverlayBlocker == OverlayPermissionGuidance.Blocker.NONE) {
            if (this.currentMainPhase == MainConnectionUiState.Phase.CONNECTED_OVERLAY_BLOCKED) {
                applyMainConnectionState(MainConnectionUiState.Phase.CONNECTED_NEEDS_VERIFY);
                return;
            }
            return;
        }
        reportOverlayBlocked(blockerCurrentOverlayBlocker);
    }

    private OverlayPermissionGuidance.Blocker currentOverlayBlocker() {
        return OverlayPermissionGuidance.diagnose(checkOverlayPermission(), isVerifyWindowOverdue(), FloatingWindowService.hasAnyWindowOnScreen());
    }

    private boolean isVerifyWindowOverdue() {
        if (!FloatingWindowService.isServiceReady() || !isRuntimeConnected()) {
            this.verifyWindowExpectedSinceMs = 0L;
            return false;
        }
        if (this.verifyWindowExpectedSinceMs != 0) {
            return SystemClock.elapsedRealtime() - this.verifyWindowExpectedSinceMs >= 12000;
        }
        this.verifyWindowExpectedSinceMs = SystemClock.elapsedRealtime();
        return false;
    }

    private boolean isRuntimeConnected() {
        SharedMemManager sharedMemManager = this.shmManager;
        return sharedMemManager != null && sharedMemManager.isCppConnected();
    }

    private boolean isConnectedAwaitingVerification() {
        if (this.currentMainPhase != MainConnectionUiState.Phase.CONNECTED_NEEDS_VERIFY && this.currentMainPhase != MainConnectionUiState.Phase.CONNECTED_OVERLAY_BLOCKED) {
            return false;
        }
        if (!hasLocalUseSession()) {
            return true;
        }
        applyMainConnectionState(runtimeConnectedPhase());
        return false;
    }

    private boolean isUseVerified() {
        try {
            return WigVerify.getInstance(getApplicationContext()).isLoggedIn();
        } catch (Throwable th) {
            this.log("读取使用验证状态失败，按未验证处理: " + th.getClass().getSimpleName());
            return false;
        }
    }

    private boolean hasLocalUseSession() {
        try {
            return WigVerify.getInstance(getApplicationContext()).hasLocalSession();
        } catch (Throwable th) {
            this.log("读取本地验证会话失败，按未验证处理: " + th.getClass().getSimpleName());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportOverlayBlocked(OverlayPermissionGuidance.Blocker blocker) {
        if (this.currentMainPhase != MainConnectionUiState.Phase.CONNECTED_OVERLAY_BLOCKED) {
            applyMainConnectionState(MainConnectionUiState.Phase.CONNECTED_OVERLAY_BLOCKED);
        }
        markOverlayBlockedOnCard(blocker);
    }

    private void markOverlayBlockedOnCard(OverlayPermissionGuidance.Blocker blocker) {
        if (this.binding == null || this.overlayGuidanceOnCard == blocker) {
            return;
        }
        this.binding.tvRootDetail.setText(OverlayPermissionGuidance.forBlocker(blocker, this.rootStatus).cardDetail());
        this.binding.tvRootDetail.setTextColor(getColor(R.color.ds_danger));
        this.overlayGuidanceOnCard = blocker;
    }

    private void showOverlayStatusDetails() {
        OverlayPermissionGuidance.Blocker blockerCurrentOverlayBlocker = currentOverlayBlocker();
        if (blockerCurrentOverlayBlocker == OverlayPermissionGuidance.Blocker.NONE) {
            FloatingWindowService.safeToast(this, "悬浮窗权限已开启：验证窗口和游戏内面板都能正常显示", 0);
        } else {
            showOverlayGuidance(blockerCurrentOverlayBlocker, false);
        }
    }

    private void showOverlayGuidance(final OverlayPermissionGuidance.Blocker blocker, final boolean z) {
        if (blocker == OverlayPermissionGuidance.Blocker.NONE || this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        dismissOverlayGuidanceDialog();
        OverlayPermissionGuidance.Guidance guidanceForBlocker = OverlayPermissionGuidance.forBlocker(blocker, this.rootStatus);
        try {
            this.overlayGuidanceDialog = OverlayPermissionDialog.show(this, guidanceForBlocker, z, new OverlayPermissionDialog.Actions() { // from class: com.android.support.MainActivity.14
                @Override // com.android.support.OverlayPermissionDialog.Actions
                public void onOpenSystemSettings() {
                    MainActivity.this.requestOverlayPermission();
                }

                @Override // com.android.support.OverlayPermissionDialog.Actions
                public void onRecheck() {
                    MainActivity.this.recheckOverlayAfterUserAction(blocker, z);
                }

                @Override // com.android.support.OverlayPermissionDialog.Actions
                public void onContinueAnyway() {
                    MainActivity.this.log("用户选择在悬浮窗不可用的情况下继续连接");
                    MainActivity.this.startInjection();
                }
            });
        } catch (RuntimeException e) {
            log("悬浮窗说明弹窗显示失败: " + e.getClass().getSimpleName());
            FloatingWindowService.safeToast(this, guidanceForBlocker.cardStatus(), 1);
        }
    }

    private void dismissOverlayGuidanceDialog() {
        AlertDialog alertDialog = this.overlayGuidanceDialog;
        if (alertDialog == null) {
            return;
        }
        try {
            if (alertDialog.isShowing()) {
                this.overlayGuidanceDialog.dismiss();
            }
        } catch (RuntimeException unused) {
        }
        this.overlayGuidanceDialog = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recheckOverlayAfterUserAction(final OverlayPermissionGuidance.Blocker blocker, final boolean z) {
        FloatingWindowService.safeToast(this, "正在重新检测…", 0);
        new Thread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda60
            @Override // java.lang.Runnable
            public final void run() throws InterruptedException {
                this.f$0.m1334x19587236(z, blocker);
            }
        }, "overlay-permission-recheck").start();
    }

    /* renamed from: lambda$recheckOverlayAfterUserAction$65$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1334x19587236(final boolean z, final OverlayPermissionGuidance.Blocker blocker) throws InterruptedException {
        final boolean zGrantOverlayPermissionViaRoot = grantOverlayPermissionViaRoot();
        runOnUiThread(new Runnable() { // from class: com.android.support.MainActivity$$ExternalSyntheticLambda61
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1333xdc38ae17(zGrantOverlayPermissionViaRoot, z, blocker);
            }
        });
    }

    /* renamed from: lambda$recheckOverlayAfterUserAction$64$com-android-support-MainActivity, reason: not valid java name */
    /* synthetic */ void m1333xdc38ae17(boolean z, boolean z2, OverlayPermissionGuidance.Blocker blocker) {
        if (this.binding == null || isFinishing() || isDestroyed()) {
            return;
        }
        this.lastOverlayGranted = Boolean.valueOf(z);
        refreshEnvBar(this.currentMainPhase);
        if (!z) {
            log("重新检测：悬浮窗权限仍未开启");
            showOverlayGuidance(OverlayPermissionGuidance.Blocker.PERMISSION_MISSING, z2);
            return;
        }
        this.verifyWindowExpectedSinceMs = 0L;
        if (z2) {
            FloatingWindowService.safeToast(this, "悬浮窗权限已开启，开始连接游戏", 0);
            startInjection();
            return;
        }
        if (blocker == OverlayPermissionGuidance.Blocker.WINDOW_NOT_SHOWING) {
            log("重新检测：权限正常，请求悬浮窗重挂一次窗口");
            requestOverlayReattach();
        }
        FloatingWindowService.safeToast(this, "悬浮窗权限正常，正在重新显示验证窗口", 0);
        if (FloatingWindowService.isServiceReady()) {
            return;
        }
        launchFloatingWindowService();
    }

    private void requestOverlayReattach() {
        try {
            Intent intent = new Intent(this, (Class<?>) FloatingWindowService.class);
            intent.setAction(FloatingWindowService.ACTION_REATTACH_OVERLAYS);
            FloatingWindowService.sIntentionalStart = true;
            startForegroundService(intent);
        } catch (RuntimeException e) {
            log("请求重挂悬浮窗失败: " + e.getClass().getSimpleName());
        }
    }

    private void launchFloatingWindowService() {
        try {
            requestIgnoreBatteryOptimizationsIfNeeded();
            Intent intent = new Intent(this, (Class<?>) FloatingWindowService.class);
            intent.putExtra(FloatingWindowService.EXTRA_EXPLICIT_USER_OPEN, true);
            FloatingWindowService.sIntentionalStart = true;
            startForegroundService(intent);
            log("悬浮窗服务已启动（前台模式）");
            FloatingWindowService.safeToast(this, "悬浮窗已启动", 0);
        } catch (Exception e) {
            log("启动悬浮窗服务失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isIgnoringBatteryOptimizations() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService("power");
            if (powerManager != null) {
                if (powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                    return true;
                }
            }
        } catch (Throwable unused) {
        }
        return false;
    }

    private void requestIgnoreBatteryOptimizationsIfNeeded() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService("power");
            if (powerManager == null) {
                return;
            }
            String packageName = getPackageName();
            if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
                return;
            }
            SharedPreferences sharedPreferences = getSharedPreferences(FloatingWindowPrefsKeys.PREF_NAME, 0);
            long j = sharedPreferences.getLong(FloatingWindowPrefsKeys.KEY_LAST_BATTERY_OPT_PROMPT_MS, 0L);
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (j > 0 && jCurrentTimeMillis - j < 86400000) {
                log("电池白名单未授权，但 24h 内问过一次，跳过");
                return;
            }
            sharedPreferences.edit().putLong(FloatingWindowPrefsKeys.KEY_LAST_BATTERY_OPT_PROMPT_MS, jCurrentTimeMillis).apply();
            Intent intent = new Intent("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
            intent.setData(Uri.parse("package:" + packageName));
            intent.addFlags(268435456);
            startActivity(intent);
            log("★ 弹出电池白名单授权页（提升悬浮窗存活率）");
        } catch (Throwable th) {
            log("requestIgnoreBatteryOptimizations 异常: " + th.getMessage());
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        try {
            super.onRequestPermissionsResult(i, strArr, iArr);
        } catch (Exception e) {
            log("onRequestPermissionsResult super 异常: " + e.getMessage());
        }
        if (i == 1002) {
            if (iArr != null) {
                try {
                    if (iArr.length != 0) {
                        boolean z = iArr.length > 0;
                        int length = iArr.length;
                        int i2 = 0;
                        while (true) {
                            if (i2 >= length) {
                                break;
                            }
                            if (iArr[i2] != 0) {
                                z = false;
                                break;
                            }
                            i2++;
                        }
                        if (z) {
                            log("存储权限已授予");
                            FloatingWindowService.safeToast(this, "存储权限已授予", 0);
                            return;
                        } else {
                            log("存储权限被拒绝");
                            if (Build.VERSION.SDK_INT < 30) {
                                FloatingWindowService.safeToast(this, "存储权限被拒绝", 1);
                                return;
                            }
                            return;
                        }
                    }
                } catch (Exception e2) {
                    log("存储权限回调处理异常: " + e2.getMessage());
                    return;
                }
            }
            log("存储权限回调为空");
            FloatingWindowService.safeToast(this, "存储权限请求已取消", 0);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        try {
            super.onActivityResult(i, i2, intent);
        } catch (Exception e) {
            log("onActivityResult super 异常: " + e.getMessage());
        }
        try {
            if (i == 1001) {
                this.lastOverlayGranted = Boolean.valueOf(checkOverlayPermission());
                refreshEnvBar(this.currentMainPhase);
                if (this.lastOverlayGranted.booleanValue()) {
                    dismissOverlayGuidanceDialog();
                    this.verifyWindowExpectedSinceMs = 0L;
                    FloatingWindowService.safeToast(this, "悬浮窗权限已开启，可以点“连接游戏”了", 0);
                    launchFloatingWindowService();
                } else {
                    log("用户从悬浮窗授权页返回，权限仍未开启");
                    showOverlayGuidance(OverlayPermissionGuidance.Blocker.PERMISSION_MISSING, false);
                }
            } else {
                if (i != 1003) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= 30 && Environment.isExternalStorageManager()) {
                    log("✓ MANAGE_EXTERNAL_STORAGE 已通过设置页授予");
                    FloatingWindowService.safeToast(this, "文件管理权限已授予", 0);
                } else {
                    log("⚠ MANAGE_EXTERNAL_STORAGE 未授予（注入仍可通过 Root 进行）");
                }
            }
        } catch (Exception e2) {
            log("onActivityResult 处理异常: " + e2.getMessage());
        }
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        MainScreenInteraction mainScreenInteraction = this.mainScreenInteraction;
        if (mainScreenInteraction != null) {
            mainScreenInteraction.saveState(bundle);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        GameRegionSelector gameRegionSelector = this.gameRegionSelector;
        if (gameRegionSelector != null) {
            GameRegionControl.unsubscribe(gameRegionSelector);
        }
        setMainDecorativeAnimationsActive(false);
        stopOverlayVisibilityWatch();
        XfOnlineCount xfOnlineCount = this.xfOnlineCount;
        if (xfOnlineCount != null) {
            xfOnlineCount.stop();
        }
        super.onStop();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() throws IOException {
        ActivityMainBinding activityMainBinding;
        TestNetworkRuleDialog testNetworkRuleDialog = this.testNetworkRuleDialog;
        if (testNetworkRuleDialog != null) {
            testNetworkRuleDialog.requestStop();
        }
        injectionLifecycle.cancel(this.ownedInjectionAttempt);
        stopOverlayVisibilityWatch();
        dismissOverlayGuidanceDialog();
        MainAnnouncementController mainAnnouncementController = this.mainAnnouncementController;
        if (mainAnnouncementController != null) {
            mainAnnouncementController.release();
            this.mainAnnouncementController = null;
        }
        super.onDestroy();
        this.selfServiceUnbindExecutor.shutdownNow();
        Runnable runnable = this.timerRunnable;
        if (runnable != null) {
            this.timerHandler.removeCallbacks(runnable);
        }
        if (this.blinkStatusRunnable != null && (activityMainBinding = this.binding) != null && activityMainBinding.statusIndicator != null) {
            this.binding.statusIndicator.removeCallbacks(this.blinkStatusRunnable);
            this.blinkStatusRunnable = null;
        }
        Runnable runnable2 = this.onlineIndicatorRunnable;
        if (runnable2 != null) {
            this.handler.removeCallbacks(runnable2);
            this.onlineIndicatorRunnable = null;
        }
        this.handler.removeCallbacksAndMessages(null);
        XfOnlineCount xfOnlineCount = this.xfOnlineCount;
        if (xfOnlineCount != null) {
            xfOnlineCount.stop();
            this.xfOnlineCount.unregisterListener(this.onlineCountToken);
            this.onlineCountToken = null;
            this.xfOnlineCount = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCurrentLoad(long j) {
        return (!injectionLifecycle.isCurrent(j) || isFinishing() || isDestroyed()) ? false : true;
    }
}
