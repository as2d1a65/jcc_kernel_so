package com.android.support;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import com.android.support.LatestOwnerListener;
import com.android.support.LifecycleAnimationRegistry;
import com.android.support.OnlineStats;
import com.android.support.WigVerify;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/* loaded from: classes.dex */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Button btnChannel;
    private Button btnLogin;
    private EditText etCardKey;
    private ImageView ivLogo;
    private boolean loginInProgress;
    private View onlineIndicator;
    private OnlineStats onlineStats;
    private LatestOwnerListener.Token onlineStatsListenerToken;
    private ProgressBar progressBar;
    private TextView tvDeviceId;
    private TextView tvOnlineCount;
    private TextView tvStatus;
    private boolean versionAllowsLogin;
    private WigVerify wigVerify;
    private boolean onlineStatsHandedOff = false;
    private boolean autoVerifySavedCardStarted = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private final LifecycleAnimationRegistry<Animator> continuousAnimations = new LifecycleAnimationRegistry<>(new LifecycleAnimationRegistry.Controller<Animator>() { // from class: com.android.support.LoginActivity.1
        @Override // com.android.support.LifecycleAnimationRegistry.Controller
        public void start(Animator animator) {
            animator.start();
        }

        @Override // com.android.support.LifecycleAnimationRegistry.Controller
        public void cancel(Animator animator) {
            animator.cancel();
        }

        @Override // com.android.support.LifecycleAnimationRegistry.Controller
        public boolean isStarted(Animator animator) {
            return animator.isStarted();
        }
    });

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.wigVerify = WigVerify.getInstance(this);
        setupFullScreen();
        setContentView(R.layout.activity_login);
        ScrollEdgeCue.attachTree(findViewById(android.R.id.content));
        initViews();
        playEntranceAnimation();
    }

    private void setupFullScreen() {
        getWindow().setFlags(512, 512);
        getWindow().getDecorView().setSystemUiVisibility(5894);
    }

    private void initViews() {
        this.etCardKey = (EditText) findViewById(R.id.etCardKey);
        this.btnLogin = (Button) findViewById(R.id.btnLogin);
        this.btnChannel = (Button) findViewById(R.id.btnChannel);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.tvStatus = (TextView) findViewById(R.id.tvStatus);
        this.tvDeviceId = (TextView) findViewById(R.id.tvDeviceId);
        this.ivLogo = (ImageView) findViewById(R.id.ivLogo);
        this.tvOnlineCount = (TextView) findViewById(R.id.tvOnlineCount);
        this.onlineIndicator = findViewById(R.id.onlineIndicator);
        TextView textView = (TextView) findViewById(R.id.tvVersionWatermark);
        if (textView != null) {
            textView.setText(WigVerify.versionLabel(this.wigVerify.getCurrentVersionName()));
        }
        loadGifLogo();
        initOnlineStats();
        this.tvDeviceId.setText("设备：" + this.wigVerify.getDeviceId());
        String savedCard = this.wigVerify.getSavedCard();
        if (savedCard != null && !savedCard.isEmpty()) {
            this.etCardKey.setText(savedCard);
        }
        this.btnLogin.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1258lambda$initViews$0$comandroidsupportLoginActivity(view);
            }
        });
        this.btnChannel.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1259lambda$initViews$1$comandroidsupportLoginActivity(view);
            }
        });
    }

    /* renamed from: lambda$initViews$0$com-android-support-LoginActivity, reason: not valid java name */
    /* synthetic */ void m1258lambda$initViews$0$comandroidsupportLoginActivity(View view) {
        performNativeLogin();
    }

    /* renamed from: lambda$initViews$1$com-android-support-LoginActivity, reason: not valid java name */
    /* synthetic */ void m1259lambda$initViews$1$comandroidsupportLoginActivity(View view) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(WigVerify.purchaseUrl())));
        } catch (Exception unused) {
            showStatus("无法打开授权页面", false);
        }
    }

    private void performNativeLogin() {
        if (isAuthUiAlive() && this.versionAllowsLogin && !this.loginInProgress) {
            String strTrim = this.etCardKey.getText().toString().trim();
            if (strTrim.isEmpty()) {
                showStatus("请输入授权码", false);
                return;
            }
            this.loginInProgress = true;
            this.btnLogin.setEnabled(false);
            this.etCardKey.setEnabled(false);
            this.progressBar.setVisibility(0);
            this.tvStatus.setVisibility(0);
            this.tvStatus.setText("正在检查使用权限...");
            this.tvStatus.setTextColor(DsTokens.BRAND);
            this.wigVerify.login(strTrim, new AnonymousClass2());
        }
    }

    /* renamed from: com.android.support.LoginActivity$2, reason: invalid class name */
    class AnonymousClass2 implements WigVerify.VerifyCallback {
        AnonymousClass2() {
        }

        @Override // com.android.support.WigVerify.VerifyCallback
        public void onSuccess(String str, long j) {
            LoginActivity.this.handler.post(new Runnable() { // from class: com.android.support.LoginActivity$2$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1266lambda$onSuccess$1$comandroidsupportLoginActivity$2();
                }
            });
        }

        /* renamed from: lambda$onSuccess$1$com-android-support-LoginActivity$2, reason: not valid java name */
        /* synthetic */ void m1266lambda$onSuccess$1$comandroidsupportLoginActivity$2() {
            if (LoginActivity.this.isAuthUiAlive()) {
                LoginActivity.this.progressBar.setVisibility(8);
                if (LoginActivity.this.requireCurrentAuthorization()) {
                    LoginActivity.this.showStatus("✓ 验证成功", true);
                    Handler handler = LoginActivity.this.handler;
                    final LoginActivity loginActivity = LoginActivity.this;
                    handler.postDelayed(new Runnable() { // from class: com.android.support.LoginActivity$2$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            loginActivity.playExitAnimationAndGo();
                        }
                    }, 500L);
                }
            }
        }

        @Override // com.android.support.WigVerify.VerifyCallback
        public void onFailed(final String str) {
            LoginActivity.this.handler.post(new Runnable() { // from class: com.android.support.LoginActivity$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1265lambda$onFailed$2$comandroidsupportLoginActivity$2(str);
                }
            });
        }

        /* renamed from: lambda$onFailed$2$com-android-support-LoginActivity$2, reason: not valid java name */
        /* synthetic */ void m1265lambda$onFailed$2$comandroidsupportLoginActivity$2(String str) {
            if (LoginActivity.this.isAuthUiAlive()) {
                LoginActivity.this.loginInProgress = false;
                LoginActivity.this.btnLogin.setEnabled(true);
                LoginActivity.this.etCardKey.setEnabled(true);
                LoginActivity.this.progressBar.setVisibility(8);
                LoginActivity.this.showStatus("验证失败：" + str, false);
            }
        }

        @Override // com.android.support.WigVerify.VerifyCallback
        public void onFailed(String str, String str2) {
            onFailed(str);
        }
    }

    private void playEntranceAnimation() {
        int[] iArr = {R.id.flLogoContainer, R.id.tvMainTitle, R.id.tvSubtitle, R.id.llStatusCard, R.id.tvVersionWatermark};
        for (int i = 0; i < 5; i++) {
            View viewFindViewById = findViewById(iArr[i]);
            viewFindViewById.setAlpha(0.0f);
            viewFindViewById.animate().alpha(1.0f).setDuration(180L).setInterpolator(new DecelerateInterpolator()).start();
        }
        checkForUpdate();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        this.continuousAnimations.startAll();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        this.continuousAnimations.stopAll();
        super.onStop();
    }

    private void initOnlineStats() {
        OnlineStats onlineStats = OnlineStats.getInstance(this);
        this.onlineStats = onlineStats;
        this.onlineStatsListenerToken = onlineStats.registerOnlineCountListener(this, new OnlineStats.OnlineCountListener() { // from class: com.android.support.LoginActivity.3
            @Override // com.android.support.OnlineStats.OnlineCountListener
            public void onError(String str) {
            }

            @Override // com.android.support.OnlineStats.OnlineCountListener
            public void onOnlineCountUpdated(int i) {
                LoginActivity.this.updateOnlineCountUI(i);
            }
        });
        this.onlineStats.start();
        startOnlineIndicatorAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOnlineCountUI(int i) {
        TextView textView = this.tvOnlineCount;
        if (textView != null) {
            textView.setText(i + " 在线");
        }
    }

    private void startOnlineIndicatorAnimation() {
        if (this.onlineIndicator == null) {
            return;
        }
        this.handler.postDelayed(new Runnable() { // from class: com.android.support.LoginActivity.4
            boolean visible = true;

            @Override // java.lang.Runnable
            public void run() {
                if (LoginActivity.this.onlineIndicator != null) {
                    LoginActivity.this.onlineIndicator.setAlpha(this.visible ? 1.0f : 0.3f);
                    this.visible = !this.visible;
                    LoginActivity.this.handler.postDelayed(this, 800L);
                }
            }
        }, 0L);
    }

    private void loadGifLogo() {
        try {
            Glide.with((FragmentActivity) this).asGif().load(Integer.valueOf(R.raw.gift2)).diskCacheStrategy(DiskCacheStrategy.DATA).into(this.ivLogo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showStatus(String str, boolean z) {
        if (this.tvStatus == null) {
            return;
        }
        ProgressBar progressBar = this.progressBar;
        if (progressBar != null) {
            progressBar.setVisibility(8);
        }
        this.tvStatus.setVisibility(0);
        this.tvStatus.setText(str);
        this.tvStatus.setTextColor(z ? DsTokens.OK : DsTokens.DANGER);
        this.tvStatus.setAlpha(0.0f);
        ObjectAnimator.ofFloat(this.tvStatus, "alpha", 0.0f, 1.0f).setDuration(300L).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playExitAnimationAndGo() {
        if (isAuthUiAlive() && requireCurrentAuthorization()) {
            View viewFindViewById = findViewById(android.R.id.content);
            if (viewFindViewById != null) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(ObjectAnimator.ofFloat(viewFindViewById, "alpha", 1.0f, 0.0f).setDuration(600L));
                animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.support.LoginActivity.5
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        LoginActivity.this.goToMain();
                    }
                });
                animatorSet.setStartDelay(300L);
                animatorSet.start();
                return;
            }
            this.handler.postDelayed(new Runnable() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.goToMain();
                }
            }, 800L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goToMain() {
        if (isAuthUiAlive() && requireCurrentAuthorization()) {
            Intent intent = new Intent(this, (Class<?>) MainActivity.class);
            this.onlineStatsHandedOff = true;
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAuthUiAlive() {
        return (isFinishing() || isDestroyed()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean requireCurrentAuthorization() {
        if (this.versionAllowsLogin && this.wigVerify.isSessionComplete()) {
            return true;
        }
        this.loginInProgress = false;
        View viewFindViewById = findViewById(android.R.id.content);
        if (viewFindViewById != null) {
            viewFindViewById.setAlpha(1.0f);
        }
        this.btnLogin.setEnabled(this.versionAllowsLogin);
        this.etCardKey.setEnabled(true);
        this.progressBar.setVisibility(8);
        showStatus("授权状态已变化，请重试验证", false);
        return false;
    }

    private void checkForUpdate() {
        if (isAuthUiAlive()) {
            this.versionAllowsLogin = false;
            this.btnLogin.setEnabled(false);
            final String savedCard = this.wigVerify.getSavedCard();
            this.wigVerify.checkVersionAndNotice(new AnonymousClass6(new Runnable() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1257lambda$checkForUpdate$2$comandroidsupportLoginActivity(savedCard);
                }
            }));
        }
    }

    /* renamed from: lambda$checkForUpdate$2$com-android-support-LoginActivity, reason: not valid java name */
    /* synthetic */ void m1257lambda$checkForUpdate$2$comandroidsupportLoginActivity(String str) {
        if (!isAuthUiAlive() || this.autoVerifySavedCardStarted || this.loginInProgress) {
            return;
        }
        if (this.wigVerify.isSessionComplete()) {
            goToMain();
        } else {
            if (str == null || str.trim().isEmpty()) {
                return;
            }
            this.autoVerifySavedCardStarted = true;
            this.etCardKey.setText(str);
            performNativeLogin();
        }
    }

    /* renamed from: com.android.support.LoginActivity$6, reason: invalid class name */
    class AnonymousClass6 implements WigVerify.VersionCheckCallback {
        final /* synthetic */ Runnable val$autoVerifySavedCard;

        AnonymousClass6(Runnable runnable) {
            this.val$autoVerifySavedCard = runnable;
        }

        @Override // com.android.support.WigVerify.VersionCheckCallback
        public void onResult(final WigVerify.UpdateInfo updateInfo) {
            Handler handler = LoginActivity.this.handler;
            final Runnable runnable = this.val$autoVerifySavedCard;
            handler.post(new Runnable() { // from class: com.android.support.LoginActivity$6$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1268lambda$onResult$0$comandroidsupportLoginActivity$6(updateInfo, runnable);
                }
            });
        }

        /* renamed from: lambda$onResult$0$com-android-support-LoginActivity$6, reason: not valid java name */
        /* synthetic */ void m1268lambda$onResult$0$comandroidsupportLoginActivity$6(WigVerify.UpdateInfo updateInfo, Runnable runnable) {
            if (LoginActivity.this.isAuthUiAlive()) {
                Dbg.d(LoginActivity.TAG, "Version check result: needUpdate=" + updateInfo.needUpdate + ", isForceUpdate=" + updateInfo.isForceUpdate + ", appVersion=" + updateInfo.appVersion + ", announcement=" + (updateInfo.announcement != null ? updateInfo.announcement.length() + " chars" : "null"));
                if (updateInfo.isForceUpdate && updateInfo.needUpdate) {
                    LoginActivity.this.versionAllowsLogin = false;
                    LoginActivity.this.showForceUpdateDialog(updateInfo);
                    return;
                }
                boolean z = updateInfo.needUpdate;
                LoginActivity loginActivity = LoginActivity.this;
                if (z) {
                    loginActivity.versionAllowsLogin = true;
                    LoginActivity.this.showOptionalUpdateDialog(updateInfo);
                    LoginActivity.this.btnLogin.setEnabled(true);
                    runnable.run();
                    return;
                }
                loginActivity.versionAllowsLogin = true;
                if (updateInfo.announcement != null && !updateInfo.announcement.isEmpty()) {
                    LoginActivity.this.showAnnouncement(updateInfo.announcement);
                }
                LoginActivity.this.showStatus("✓ 版本就绪", true);
                LoginActivity.this.btnLogin.setEnabled(true);
                runnable.run();
            }
        }

        @Override // com.android.support.WigVerify.VersionCheckCallback
        public void onError(final String str) {
            Handler handler = LoginActivity.this.handler;
            final Runnable runnable = this.val$autoVerifySavedCard;
            handler.post(new Runnable() { // from class: com.android.support.LoginActivity$6$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1267lambda$onError$1$comandroidsupportLoginActivity$6(str, runnable);
                }
            });
        }

        /* renamed from: lambda$onError$1$com-android-support-LoginActivity$6, reason: not valid java name */
        /* synthetic */ void m1267lambda$onError$1$comandroidsupportLoginActivity$6(String str, Runnable runnable) {
            if (LoginActivity.this.isAuthUiAlive()) {
                Log.w(LoginActivity.TAG, "Version check failed: " + str);
                LoginActivity.this.versionAllowsLogin = true;
                LoginActivity.this.showStatus("版本检查未完成，不影响验证", false);
                LoginActivity.this.btnLogin.setEnabled(true);
                runnable.run();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAnnouncement(final String str) {
        FrameLayout frameLayout = (FrameLayout) findViewById(android.R.id.content);
        if (frameLayout == null) {
            return;
        }
        View viewFindViewWithTag = frameLayout.findViewWithTag("announcement_bar");
        if (viewFindViewWithTag != null) {
            frameLayout.removeView(viewFindViewWithTag);
        }
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setTag("announcement_bar");
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setMinimumHeight(dp(48));
        linearLayout.setPadding(dp(16), dp(10), dp(16), dp(10));
        linearLayout.setBackground(DsTokens.withRipple(this, DsTokens.shape(this, -1, DsTokens.LINE, 8), DsTokens.BRAND_A16, 8));
        DsTokens.pressable(linearLayout);
        View view = new View(this);
        view.setBackgroundResource(R.drawable.ds_led);
        view.setBackgroundTintList(ColorStateList.valueOf(DsTokens.BRAND));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp(6), dp(6));
        layoutParams.rightMargin = dp(10);
        linearLayout.addView(view, layoutParams);
        TextView textView = new TextView(this);
        textView.setText(str);
        textView.setTextColor(DsTokens.TEXT_1);
        textView.setTextSize(2, 13.0f);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSelected(true);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        linearLayout.addView(textView, new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1260lambda$showAnnouncement$3$comandroidsupportLoginActivity(str, view2);
            }
        });
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, -2);
        layoutParams2.gravity = 81;
        layoutParams2.bottomMargin = dp(80);
        layoutParams2.leftMargin = dp(24);
        layoutParams2.rightMargin = dp(24);
        linearLayout.setAlpha(0.0f);
        linearLayout.setTranslationY(dp(20));
        frameLayout.addView(linearLayout, layoutParams2);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(linearLayout, "alpha", 0.0f, 1.0f).setDuration(500L), ObjectAnimator.ofFloat(linearLayout, "translationY", dp(20), 0.0f).setDuration(500L));
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    /* renamed from: lambda$showAnnouncement$3$com-android-support-LoginActivity, reason: not valid java name */
    /* synthetic */ void m1260lambda$showAnnouncement$3$comandroidsupportLoginActivity(String str, View view) {
        showAnnouncementDialog(str);
    }

    private void boundDialogToScreen(AlertDialog alertDialog) {
        final Window window = alertDialog.getWindow();
        if (window == null) {
            return;
        }
        int realScreenWidth = ScreenUtils.getRealScreenWidth(this);
        int realScreenHeight = ScreenUtils.getRealScreenHeight(this);
        final int iMin = Math.min(dp(400), Math.round(realScreenWidth * 0.92f));
        final int iRound = Math.round(realScreenHeight * 0.9f);
        window.setLayout(iMin, -2);
        window.getDecorView().post(new Runnable() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                LoginActivity.lambda$boundDialogToScreen$4(window, iRound, iMin);
            }
        });
    }

    static /* synthetic */ void lambda$boundDialogToScreen$4(Window window, int i, int i2) {
        try {
            if (window.getDecorView().getHeight() > i) {
                window.setLayout(i2, i);
            }
        } catch (RuntimeException unused) {
        }
    }

    private void showAnnouncementDialog(String str) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(28), dp(24), dp(28), dp(24));
        linearLayout.setBackground(DsTokens.verticalGradient(this, -1, -657673, DsTokens.LINE_STRONG, 8));
        TextView textView = new TextView(this);
        textView.setText("系统公告");
        textView.setTextColor(DsTokens.TEXT_1);
        textView.setTextSize(2, 20.0f);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        textView.setPadding(0, 0, 0, dp(16));
        linearLayout.addView(textView);
        View view = new View(this);
        view.setBackgroundColor(DsTokens.LINE);
        linearLayout.addView(view, new LinearLayout.LayoutParams(-1, dp(1)));
        ScrollView scrollView = new ScrollView(this);
        scrollView.setPadding(0, dp(16), 0, dp(16));
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2, 1.0f);
        TextView textView2 = new TextView(this);
        textView2.setText(str);
        textView2.setTextColor(DsTokens.TEXT_2);
        textView2.setTextSize(2, 15.0f);
        textView2.setLineSpacing(0.0f, 1.4f);
        scrollView.addView(textView2);
        ScrollEdgeCue.attach(scrollView);
        linearLayout.addView(scrollView, layoutParams);
        Button button = new Button(this);
        button.setText("了解");
        button.setTextColor(-1);
        button.setTextSize(2, 16.0f);
        button.setTypeface(null, 1);
        button.setAllCaps(false);
        button.setStateListAnimator(null);
        button.setBackground(DsTokens.primaryButton(this, 8));
        button.setPadding(0, 0, 0, 0);
        DsTokens.pressable(button);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, dp(52));
        layoutParams2.topMargin = dp(16);
        linearLayout.addView(button, layoutParams2);
        final AlertDialog alertDialogCreate = new AlertDialog.Builder(this, android.R.style.Theme.DeviceDefault.Dialog).setView(linearLayout).setCancelable(true).create();
        if (alertDialogCreate.getWindow() != null) {
            alertDialogCreate.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                alertDialogCreate.dismiss();
            }
        });
        alertDialogCreate.show();
        boundDialogToScreen(alertDialogCreate);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showForceUpdateDialog(final WigVerify.UpdateInfo updateInfo) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(28), dp(24), dp(28), dp(24));
        linearLayout.setBackground(DsTokens.verticalGradient(this, -1, -657673, DsTokens.DANGER_A40, 8));
        TextView textView = new TextView(this);
        textView.setText("!");
        textView.setTextSize(2, 28.0f);
        textView.setTypeface(null, 1);
        textView.setTextColor(DsTokens.DANGER);
        textView.setGravity(17);
        textView.setPadding(0, 0, 0, dp(8));
        linearLayout.addView(textView);
        TextView textView2 = new TextView(this);
        textView2.setText("版本过旧");
        textView2.setTextColor(DsTokens.TEXT_1);
        textView2.setTextSize(2, 20.0f);
        textView2.setTypeface(null, 1);
        textView2.setGravity(17);
        textView2.setPadding(0, 0, 0, dp(8));
        linearLayout.addView(textView2);
        TextView textView3 = new TextView(this);
        textView3.setText("请更新到最新版本以继续使用");
        textView3.setTextColor(DsTokens.TEXT_2);
        textView3.setTextSize(2, 13.0f);
        textView3.setGravity(17);
        textView3.setPadding(0, 0, 0, dp(16));
        linearLayout.addView(textView3);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(17);
        linearLayout2.setPadding(dp(16), dp(12), dp(16), dp(12));
        linearLayout2.setBackground(DsTokens.shape(this, DsTokens.SURFACE_2, DsTokens.LINE, 8));
        TextView textView4 = new TextView(this);
        textView4.setText("v" + this.wigVerify.getCurrentVersionName());
        textView4.setTextColor(DsTokens.DANGER);
        textView4.setTextSize(2, 16.0f);
        textView4.setTypeface(Typeface.MONOSPACE, 1);
        linearLayout2.addView(textView4);
        TextView textView5 = new TextView(this);
        textView5.setText("  →  ");
        textView5.setTextColor(DsTokens.TEXT_3);
        textView5.setTextSize(2, 16.0f);
        linearLayout2.addView(textView5);
        TextView textView6 = new TextView(this);
        textView6.setText("v" + updateInfo.appVersion);
        textView6.setTextColor(DsTokens.OK);
        textView6.setTextSize(2, 16.0f);
        textView6.setTypeface(Typeface.MONOSPACE, 1);
        linearLayout2.addView(textView6);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(16);
        linearLayout.addView(linearLayout2, layoutParams);
        if (updateInfo.announcement != null && !updateInfo.announcement.isEmpty()) {
            View view = new View(this);
            view.setBackgroundColor(DsTokens.LINE);
            linearLayout.addView(view, new LinearLayout.LayoutParams(-1, dp(1)));
            ScrollView scrollView = new ScrollView(this);
            scrollView.setPadding(0, dp(12), 0, dp(12));
            ViewGroup.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2, 1.0f);
            TextView textView7 = new TextView(this);
            textView7.setText(updateInfo.announcement);
            textView7.setTextColor(DsTokens.TEXT_2);
            textView7.setTextSize(2, 13.0f);
            textView7.setLineSpacing(0.0f, 1.4f);
            scrollView.addView(textView7);
            ScrollEdgeCue.attach(scrollView);
            linearLayout.addView(scrollView, layoutParams2);
        }
        Button button = new Button(this);
        button.setText("立即更新");
        button.setTextColor(-1);
        button.setTextSize(2, 16.0f);
        button.setTypeface(null, 1);
        button.setAllCaps(false);
        button.setStateListAnimator(null);
        button.setBackground(DsTokens.withRipple(this, DsTokens.shape(this, DsTokens.DANGER, 0, 8), DsTokens.RIPPLE_DARK, 8));
        button.setPadding(0, 0, 0, 0);
        DsTokens.pressable(button);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, dp(52));
        layoutParams3.topMargin = dp(12);
        linearLayout.addView(button, layoutParams3);
        Button button2 = new Button(this);
        button2.setText("退出应用");
        button2.setTextColor(DsTokens.TEXT_3);
        button2.setTextSize(2, 13.0f);
        button2.setAllCaps(false);
        button2.setStateListAnimator(null);
        button2.setBackground(DsTokens.withRipple(this, DsTokens.shape(this, 0, 0, 8), DsTokens.BRAND_A16, 8));
        DsTokens.pressable(button2);
        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(-1, dp(48));
        layoutParams4.topMargin = dp(6);
        linearLayout.addView(button2, layoutParams4);
        AlertDialog alertDialogCreate = new AlertDialog.Builder(this, android.R.style.Theme.DeviceDefault.Dialog).setView(linearLayout).setCancelable(false).create();
        if (alertDialogCreate.getWindow() != null) {
            alertDialogCreate.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1261lambda$showForceUpdateDialog$6$comandroidsupportLoginActivity(updateInfo, view2);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1262lambda$showForceUpdateDialog$7$comandroidsupportLoginActivity(view2);
            }
        });
        alertDialogCreate.show();
        boundDialogToScreen(alertDialogCreate);
        showStatus("⚠ 请更新到最新版本", false);
    }

    /* renamed from: lambda$showForceUpdateDialog$6$com-android-support-LoginActivity, reason: not valid java name */
    /* synthetic */ void m1261lambda$showForceUpdateDialog$6$comandroidsupportLoginActivity(WigVerify.UpdateInfo updateInfo, View view) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(updateInfo.updateUrl));
            startActivity(intent);
        } catch (Exception unused) {
            FloatingWindowService.safeToast(this, "无法打开下载链接", 1);
        }
    }

    /* renamed from: lambda$showForceUpdateDialog$7$com-android-support-LoginActivity, reason: not valid java name */
    /* synthetic */ void m1262lambda$showForceUpdateDialog$7$comandroidsupportLoginActivity(View view) {
        finishAffinity();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showOptionalUpdateDialog(final WigVerify.UpdateInfo updateInfo) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(28), dp(24), dp(28), dp(24));
        linearLayout.setBackground(DsTokens.verticalGradient(this, -1, -657673, DsTokens.LINE_STRONG, 8));
        TextView textView = new TextView(this);
        textView.setText("NEW");
        textView.setTextSize(2, 11.0f);
        textView.setTypeface(null, 1);
        textView.setLetterSpacing(0.18f);
        textView.setTextColor(DsTokens.BRAND);
        textView.setGravity(17);
        textView.setPadding(0, 0, 0, dp(8));
        linearLayout.addView(textView);
        TextView textView2 = new TextView(this);
        textView2.setText("发现新版本");
        textView2.setTextColor(DsTokens.TEXT_1);
        textView2.setTextSize(2, 20.0f);
        textView2.setTypeface(null, 1);
        textView2.setGravity(17);
        textView2.setPadding(0, 0, 0, dp(8));
        linearLayout.addView(textView2);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(17);
        linearLayout2.setPadding(dp(16), dp(12), dp(16), dp(12));
        linearLayout2.setBackground(DsTokens.shape(this, DsTokens.SURFACE_2, DsTokens.LINE, 8));
        TextView textView3 = new TextView(this);
        textView3.setText("v" + this.wigVerify.getCurrentVersionName());
        textView3.setTextColor(DsTokens.TEXT_2);
        textView3.setTextSize(2, 15.0f);
        textView3.setTypeface(Typeface.MONOSPACE);
        linearLayout2.addView(textView3);
        TextView textView4 = new TextView(this);
        textView4.setText("  →  ");
        textView4.setTextColor(DsTokens.TEXT_3);
        textView4.setTextSize(2, 15.0f);
        linearLayout2.addView(textView4);
        TextView textView5 = new TextView(this);
        textView5.setText("v" + updateInfo.appVersion);
        textView5.setTextColor(DsTokens.OK);
        textView5.setTextSize(2, 15.0f);
        textView5.setTypeface(Typeface.MONOSPACE, 1);
        linearLayout2.addView(textView5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(16);
        linearLayout.addView(linearLayout2, layoutParams);
        if (updateInfo.announcement != null && !updateInfo.announcement.isEmpty()) {
            ScrollView scrollView = new ScrollView(this);
            scrollView.setVerticalScrollBarEnabled(false);
            scrollView.setOverScrollMode(2);
            scrollView.setPadding(0, 0, 0, dp(16));
            TextView textView6 = new TextView(this);
            textView6.setText(updateInfo.announcement);
            textView6.setTextColor(DsTokens.TEXT_2);
            textView6.setTextSize(2, 13.0f);
            textView6.setGravity(17);
            textView6.setLineSpacing(0.0f, 1.4f);
            scrollView.addView(textView6);
            ScrollEdgeCue.attach(scrollView);
            linearLayout.addView(scrollView, new LinearLayout.LayoutParams(-1, -2, 1.0f));
        }
        Button button = new Button(this);
        button.setText("前往更新");
        button.setTextColor(-1);
        button.setTextSize(2, 16.0f);
        button.setTypeface(null, 1);
        button.setAllCaps(false);
        button.setStateListAnimator(null);
        button.setBackground(DsTokens.primaryButton(this, 8));
        button.setPadding(0, 0, 0, 0);
        DsTokens.pressable(button);
        linearLayout.addView(button, new LinearLayout.LayoutParams(-1, dp(52)));
        Button button2 = new Button(this);
        button2.setText("暂不更新");
        button2.setTextColor(DsTokens.TEXT_3);
        button2.setTextSize(2, 13.0f);
        button2.setAllCaps(false);
        button2.setStateListAnimator(null);
        button2.setBackground(DsTokens.withRipple(this, DsTokens.shape(this, 0, 0, 8), DsTokens.BRAND_A16, 8));
        DsTokens.pressable(button2);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, dp(48));
        layoutParams2.topMargin = dp(6);
        linearLayout.addView(button2, layoutParams2);
        final AlertDialog alertDialogCreate = new AlertDialog.Builder(this, android.R.style.Theme.DeviceDefault.Dialog).setView(linearLayout).setCancelable(true).create();
        if (alertDialogCreate.getWindow() != null) {
            alertDialogCreate.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1263x3728720b(updateInfo, alertDialogCreate, view);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.LoginActivity$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1264x9e0131cc(alertDialogCreate, view);
            }
        });
        alertDialogCreate.show();
        boundDialogToScreen(alertDialogCreate);
    }

    /* renamed from: lambda$showOptionalUpdateDialog$8$com-android-support-LoginActivity, reason: not valid java name */
    /* synthetic */ void m1263x3728720b(WigVerify.UpdateInfo updateInfo, AlertDialog alertDialog, View view) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(updateInfo.updateUrl));
            startActivity(intent);
        } catch (Exception unused) {
            FloatingWindowService.safeToast(this, "无法打开下载链接", 0);
        }
        alertDialog.dismiss();
    }

    /* renamed from: lambda$showOptionalUpdateDialog$9$com-android-support-LoginActivity, reason: not valid java name */
    /* synthetic */ void m1264x9e0131cc(AlertDialog alertDialog, View view) {
        alertDialog.dismiss();
        showStatus("请输入授权码完成登录", true);
    }

    private int dp(int i) {
        return (int) TypedValue.applyDimension(1, i, getResources().getDisplayMetrics());
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.continuousAnimations.destroy();
        OnlineStats onlineStats = this.onlineStats;
        LatestOwnerListener.Token token = this.onlineStatsListenerToken;
        this.onlineStatsListenerToken = null;
        this.onlineStats = null;
        if (onlineStats != null && token != null) {
            onlineStats.unregisterOnlineCountListener(token);
        }
        if (!this.onlineStatsHandedOff && onlineStats != null) {
            onlineStats.stop();
        }
        this.handler.removeCallbacksAndMessages(null);
    }
}
