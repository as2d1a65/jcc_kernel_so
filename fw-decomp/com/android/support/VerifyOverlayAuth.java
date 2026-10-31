package com.android.support;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.support.VerifyOverlayAuth;
import com.android.support.WigVerify;

/* loaded from: classes.dex */
final class VerifyOverlayAuth {
    private static final long AUTO_RETRY_BASE_MS = 3000;
    private static final long AUTO_RETRY_MAX_MS = 8000;
    private static final long AUTO_RETRY_STEP_MS = 2000;
    private static final int MAX_AUTO_RETRY_ATTEMPTS = 2;
    private static final String TAG = "FloatingWindowService";
    private static final Object AUTH_STATUS_OWNER = new Object();
    private static final Object UPDATE_STATUS_OWNER = new Object();

    interface Host {
        Context context();

        boolean isAlive();

        Handler mainHandler();

        void markVerifiedAndEnter();

        void showAnnouncement(LinearLayout linearLayout, String str);

        void showForceUpdate(View view, LinearLayout linearLayout, LinearLayout linearLayout2, Button button, EditText editText, TextView textView, View view2, WigVerify.UpdateInfo updateInfo);

        void showInteractiveVerificationAfterSilentRecovery();

        void showKeyboard(EditText editText);

        void showOptionalUpdate(TextView textView, WigVerify.UpdateInfo updateInfo);

        void toast(CharSequence charSequence);

        WigVerify wigVerify();
    }

    static long autoRetryDelayMs(int i, double d) {
        return Math.max(1L, Math.round(Math.min((Math.max(0, i - 1) * AUTO_RETRY_STEP_MS) + AUTO_RETRY_BASE_MS, AUTO_RETRY_MAX_MS) * (((d >= 0.0d ? Math.min(d, 1.0d) : 0.0d) * 0.5d) + 0.75d)));
    }

    private VerifyOverlayAuth() {
    }

    static boolean shouldStartSavedCard(String str, boolean z, boolean z2) {
        return (str == null || str.isEmpty() || z || z2) ? false : true;
    }

    static boolean maybeStartSavedCard(String str, boolean[] zArr, boolean z, Runnable runnable) {
        if (!shouldStartSavedCard(str, zArr[0], z)) {
            return false;
        }
        zArr[0] = true;
        runnable.run();
        return true;
    }

    static Runnable bindLogin(final Host host, final EditText editText, final Button button, final ProgressBar progressBar, final TextView textView, final ScrollView scrollView) {
        final Runnable[] runnableArr = new Runnable[1];
        final int[] iArr = new int[1];
        final VerificationAttemptWindow verificationAttemptWindow = new VerificationAttemptWindow();
        final WigVerify.VerifyCallback[] verifyCallbackArr = new WigVerify.VerifyCallback[1];
        runnableArr[0] = new Runnable() { // from class: com.android.support.VerifyOverlayAuth$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VerifyOverlayAuth.lambda$bindLogin$1(host, textView, editText, iArr, verificationAttemptWindow, progressBar, button, verifyCallbackArr, scrollView, runnableArr);
            }
        };
        return runnableArr[0];
    }

    static /* synthetic */ void lambda$bindLogin$1(final Host host, final TextView textView, final EditText editText, final int[] iArr, final VerificationAttemptWindow verificationAttemptWindow, final ProgressBar progressBar, final Button button, final WigVerify.VerifyCallback[] verifyCallbackArr, final ScrollView scrollView, Runnable[] runnableArr) {
        if (!host.isAlive() || textView.getTag() == UPDATE_STATUS_OWNER) {
            return;
        }
        textView.setTag(AUTH_STATUS_OWNER);
        String strTrim = editText.getText().toString().trim();
        if (strTrim.isEmpty()) {
            iArr[0] = 0;
            verificationAttemptWindow.finish();
            progressBar.setVisibility(8);
            button.setEnabled(true);
            editText.setEnabled(true);
            textView.setText("请输入使用码");
            textView.setTextColor(-50384);
            return;
        }
        if (!verificationAttemptWindow.isActive()) {
            iArr[0] = 0;
            verificationAttemptWindow.begin(SystemClock.elapsedRealtime());
            final long jSequence = verificationAttemptWindow.sequence();
            host.mainHandler().postDelayed(new Runnable() { // from class: com.android.support.VerifyOverlayAuth$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    VerifyOverlayAuth.lambda$bindLogin$0(verificationAttemptWindow, jSequence, iArr, host, verifyCallbackArr, textView, progressBar, button, editText, scrollView);
                }
            }, 60000L);
        }
        long jStartAttempt = verificationAttemptWindow.startAttempt();
        if (jStartAttempt == 0) {
            return;
        }
        button.setEnabled(false);
        button.setText("验证中...");
        editText.setEnabled(false);
        progressBar.setVisibility(8);
        textView.setText(iArr[0] == 0 ? "授权服务准备中..." : "授权连接重试中...");
        textView.setTextColor(GameHudPalette.CYAN);
        verifyCallbackArr[0] = new AnonymousClass1(host, textView, verificationAttemptWindow, jStartAttempt, strTrim, iArr, progressBar, button, editText, scrollView, runnableArr);
        host.wigVerify().login(strTrim, verifyCallbackArr[0]);
    }

    static /* synthetic */ void lambda$bindLogin$0(VerificationAttemptWindow verificationAttemptWindow, long j, int[] iArr, Host host, WigVerify.VerifyCallback[] verifyCallbackArr, TextView textView, ProgressBar progressBar, Button button, EditText editText, ScrollView scrollView) {
        if (verificationAttemptWindow.isActive() && verificationAttemptWindow.sequence() == j) {
            verificationAttemptWindow.finish();
            iArr[0] = 0;
            host.wigVerify().cancelPendingLogin(verifyCallbackArr[0]);
            if (host.isAlive() && textView.getTag() == AUTH_STATUS_OWNER) {
                showActionableFailure(progressBar, button, editText, textView, scrollView, VerificationUserMessage.forCode("LOGIN_TIMEOUT", null));
            }
        }
    }

    /* renamed from: com.android.support.VerifyOverlayAuth$1, reason: invalid class name */
    class AnonymousClass1 implements WigVerify.VerifyCallback {
        final /* synthetic */ int[] val$attempt;
        final /* synthetic */ Button val$btnLogin;
        final /* synthetic */ String val$cardKey;
        final /* synthetic */ ScrollView val$contentScroll;
        final /* synthetic */ EditText val$etCardKey;
        final /* synthetic */ Host val$host;
        final /* synthetic */ Runnable[] val$loginTrigger;
        final /* synthetic */ ProgressBar val$progressBar;
        final /* synthetic */ long val$request;
        final /* synthetic */ TextView val$tvStatus;
        final /* synthetic */ VerificationAttemptWindow val$window;

        AnonymousClass1(Host host, TextView textView, VerificationAttemptWindow verificationAttemptWindow, long j, String str, int[] iArr, ProgressBar progressBar, Button button, EditText editText, ScrollView scrollView, Runnable[] runnableArr) {
            this.val$host = host;
            this.val$tvStatus = textView;
            this.val$window = verificationAttemptWindow;
            this.val$request = j;
            this.val$cardKey = str;
            this.val$attempt = iArr;
            this.val$progressBar = progressBar;
            this.val$btnLogin = button;
            this.val$etCardKey = editText;
            this.val$contentScroll = scrollView;
            this.val$loginTrigger = runnableArr;
        }

        @Override // com.android.support.WigVerify.VerifyCallback
        public void onSuccess(final String str, final long j) {
            Handler handlerMainHandler = this.val$host.mainHandler();
            final Host host = this.val$host;
            final TextView textView = this.val$tvStatus;
            final VerificationAttemptWindow verificationAttemptWindow = this.val$window;
            final long j2 = this.val$request;
            final String str2 = this.val$cardKey;
            final int[] iArr = this.val$attempt;
            final ProgressBar progressBar = this.val$progressBar;
            final Button button = this.val$btnLogin;
            final EditText editText = this.val$etCardKey;
            final ScrollView scrollView = this.val$contentScroll;
            handlerMainHandler.post(new Runnable() { // from class: com.android.support.VerifyOverlayAuth$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    VerifyOverlayAuth.AnonymousClass1.lambda$onSuccess$0(host, textView, verificationAttemptWindow, j2, str2, j, iArr, progressBar, button, editText, scrollView, str);
                }
            });
        }

        static /* synthetic */ void lambda$onSuccess$0(Host host, TextView textView, VerificationAttemptWindow verificationAttemptWindow, long j, String str, long j2, int[] iArr, ProgressBar progressBar, Button button, EditText editText, ScrollView scrollView, String str2) {
            if (host.isAlive() && textView.getTag() == VerifyOverlayAuth.AUTH_STATUS_OWNER && verificationAttemptWindow.accept(j)) {
                if (!host.wigVerify().isCurrentLogin(str, j2)) {
                    verificationAttemptWindow.finish();
                    iArr[0] = 0;
                    VerifyOverlayAuth.showActionableFailure(progressBar, button, editText, textView, scrollView, VerificationUserMessage.forCode("GATEWAY_SESSION", null));
                    return;
                }
                progressBar.setVisibility(8);
                textView.setText("✓ 验证成功 " + str2);
                textView.setTextColor(GameHudPalette.SUCCESS);
                host.toast("验证成功!");
                verificationAttemptWindow.finish();
                iArr[0] = 0;
                host.markVerifiedAndEnter();
            }
        }

        @Override // com.android.support.WigVerify.VerifyCallback
        public void onFailed(final String str) {
            Handler handlerMainHandler = this.val$host.mainHandler();
            final Host host = this.val$host;
            final TextView textView = this.val$tvStatus;
            final VerificationAttemptWindow verificationAttemptWindow = this.val$window;
            final long j = this.val$request;
            final int[] iArr = this.val$attempt;
            final ProgressBar progressBar = this.val$progressBar;
            final Button button = this.val$btnLogin;
            final EditText editText = this.val$etCardKey;
            final ScrollView scrollView = this.val$contentScroll;
            handlerMainHandler.post(new Runnable() { // from class: com.android.support.VerifyOverlayAuth$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VerifyOverlayAuth.AnonymousClass1.lambda$onFailed$1(host, textView, verificationAttemptWindow, j, iArr, progressBar, button, editText, scrollView, str);
                }
            });
        }

        static /* synthetic */ void lambda$onFailed$1(Host host, TextView textView, VerificationAttemptWindow verificationAttemptWindow, long j, int[] iArr, ProgressBar progressBar, Button button, EditText editText, ScrollView scrollView, String str) {
            if (host.isAlive() && textView.getTag() == VerifyOverlayAuth.AUTH_STATUS_OWNER && verificationAttemptWindow.accept(j)) {
                verificationAttemptWindow.finish();
                iArr[0] = 0;
                VerifyOverlayAuth.showActionableFailure(progressBar, button, editText, textView, scrollView, str);
            }
        }

        @Override // com.android.support.WigVerify.VerifyCallback
        public void onFailed(final String str, final String str2) {
            Handler handlerMainHandler = this.val$host.mainHandler();
            final Host host = this.val$host;
            final TextView textView = this.val$tvStatus;
            final VerificationAttemptWindow verificationAttemptWindow = this.val$window;
            final long j = this.val$request;
            final int[] iArr = this.val$attempt;
            final Runnable[] runnableArr = this.val$loginTrigger;
            final ProgressBar progressBar = this.val$progressBar;
            final Button button = this.val$btnLogin;
            final EditText editText = this.val$etCardKey;
            final ScrollView scrollView = this.val$contentScroll;
            handlerMainHandler.post(new Runnable() { // from class: com.android.support.VerifyOverlayAuth$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VerifyOverlayAuth.AnonymousClass1.lambda$onFailed$2(host, textView, verificationAttemptWindow, j, str2, iArr, runnableArr, progressBar, button, editText, scrollView, str);
                }
            });
        }

        static /* synthetic */ void lambda$onFailed$2(Host host, TextView textView, VerificationAttemptWindow verificationAttemptWindow, long j, String str, int[] iArr, Runnable[] runnableArr, ProgressBar progressBar, Button button, EditText editText, ScrollView scrollView, String str2) {
            if (host.isAlive() && textView.getTag() == VerifyOverlayAuth.AUTH_STATUS_OWNER && verificationAttemptWindow.accept(j)) {
                if (GatewayFailurePolicy.allowsSilentRetry(str) && iArr[0] < 2) {
                    VerifyOverlayAuth.scheduleAutoRetry(host, runnableArr, iArr, verificationAttemptWindow, j, progressBar, button, editText, textView, scrollView, null, str2);
                    return;
                }
                verificationAttemptWindow.finish();
                iArr[0] = 0;
                VerifyOverlayAuth.showActionableFailure(progressBar, button, editText, textView, scrollView, str2);
            }
        }
    }

    static boolean isSettledVerdict(String str) {
        if (str == null) {
            return false;
        }
        str.hashCode();
        switch (str) {
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void scheduleAutoRetry(final Host host, final Runnable[] runnableArr, final int[] iArr, final VerificationAttemptWindow verificationAttemptWindow, final long j, final ProgressBar progressBar, final Button button, final EditText editText, final TextView textView, final ScrollView scrollView, String str, final String str2) {
        if (verificationAttemptWindow.isCurrent(j) && textView.getTag() == AUTH_STATUS_OWNER) {
            if (iArr[0] >= 2 || verificationAttemptWindow.expired(SystemClock.elapsedRealtime())) {
                verificationAttemptWindow.finish();
                iArr[0] = 0;
                showActionableFailure(progressBar, button, editText, textView, scrollView, str2);
                return;
            }
            iArr[0] = iArr[0] + 1;
            progressBar.setVisibility(8);
            button.setEnabled(false);
            button.setText("验证中...");
            editText.setEnabled(false);
            textView.setText(str != null ? str : "授权连接重试中...");
            textView.setTextColor(GameHudPalette.CYAN);
            host.mainHandler().postDelayed(new Runnable() { // from class: com.android.support.VerifyOverlayAuth$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VerifyOverlayAuth.lambda$scheduleAutoRetry$2(host, textView, verificationAttemptWindow, j, iArr, progressBar, button, editText, scrollView, str2, runnableArr);
                }
            }, autoRetryDelayMs(iArr[0], Math.random()));
        }
    }

    static /* synthetic */ void lambda$scheduleAutoRetry$2(Host host, TextView textView, VerificationAttemptWindow verificationAttemptWindow, long j, int[] iArr, ProgressBar progressBar, Button button, EditText editText, ScrollView scrollView, String str, Runnable[] runnableArr) {
        if (host.isAlive() && textView.getTag() == AUTH_STATUS_OWNER && verificationAttemptWindow.isCurrent(j)) {
            if (verificationAttemptWindow.expired(SystemClock.elapsedRealtime())) {
                verificationAttemptWindow.finish();
                iArr[0] = 0;
                showActionableFailure(progressBar, button, editText, textView, scrollView, str);
                return;
            }
            runnableArr[0].run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showActionableFailure(ProgressBar progressBar, Button button, EditText editText, TextView textView, final ScrollView scrollView, String str) {
        progressBar.setVisibility(8);
        button.setEnabled(true);
        button.setText("开始验证");
        editText.setEnabled(true);
        textView.setText("✗ " + str);
        textView.setTextColor(DsTokens.DANGER);
        scrollView.post(new Runnable() { // from class: com.android.support.VerifyOverlayAuth$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                scrollView.fullScroll(130);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void enterOrStartSavedCard(Host host, String str, boolean[] zArr, Runnable runnable) {
        if (host.isAlive()) {
            if (host.wigVerify().isSessionComplete()) {
                host.markVerifiedAndEnter();
            } else {
                maybeStartSavedCard(str, zArr, false, runnable);
            }
        }
    }

    static void startVersionCheck(Host host, LinearLayout linearLayout, View view, LinearLayout linearLayout2, LinearLayout linearLayout3, Button button, EditText editText, TextView textView, View view2, ProgressBar progressBar, View view3, TextView textView2, String str, boolean[] zArr, Runnable runnable) {
        boolean z = (str == null || str.isEmpty() || zArr[0]) ? false : true;
        textView.setText(z ? "授权服务连接中..." : "可直接开始验证");
        textView.setTextColor(DsTokens.BRAND);
        progressBar.setVisibility(z ? 0 : 8);
        button.setEnabled(true);
        enterOrStartSavedCard(host, str, zArr, runnable);
        host.wigVerify().checkVersionAndNotice(new AnonymousClass2(host, progressBar, view3, textView2, linearLayout, textView, view, linearLayout2, linearLayout3, button, editText, view2, str, zArr, runnable));
    }

    /* renamed from: com.android.support.VerifyOverlayAuth$2, reason: invalid class name */
    class AnonymousClass2 implements WigVerify.VersionCheckCallback {
        final /* synthetic */ LinearLayout val$announcementArea;
        final /* synthetic */ Runnable val$autoVerifySavedCard;
        final /* synthetic */ boolean[] val$autoVerifySavedCardStarted;
        final /* synthetic */ Button val$btnLogin;
        final /* synthetic */ View val$connLed;
        final /* synthetic */ TextView val$connText;
        final /* synthetic */ LinearLayout val$contentLayout;
        final /* synthetic */ EditText val$etCardKey;
        final /* synthetic */ LinearLayout val$footerArea;
        final /* synthetic */ Host val$host;
        final /* synthetic */ View val$keyCard;
        final /* synthetic */ ProgressBar val$progressBar;
        final /* synthetic */ String val$savedCard;
        final /* synthetic */ View val$secondaryRow;
        final /* synthetic */ TextView val$tvStatus;

        AnonymousClass2(Host host, ProgressBar progressBar, View view, TextView textView, LinearLayout linearLayout, TextView textView2, View view2, LinearLayout linearLayout2, LinearLayout linearLayout3, Button button, EditText editText, View view3, String str, boolean[] zArr, Runnable runnable) {
            this.val$host = host;
            this.val$progressBar = progressBar;
            this.val$connLed = view;
            this.val$connText = textView;
            this.val$announcementArea = linearLayout;
            this.val$tvStatus = textView2;
            this.val$keyCard = view2;
            this.val$contentLayout = linearLayout2;
            this.val$footerArea = linearLayout3;
            this.val$btnLogin = button;
            this.val$etCardKey = editText;
            this.val$secondaryRow = view3;
            this.val$savedCard = str;
            this.val$autoVerifySavedCardStarted = zArr;
            this.val$autoVerifySavedCard = runnable;
        }

        @Override // com.android.support.WigVerify.VersionCheckCallback
        public void onResult(final WigVerify.UpdateInfo updateInfo) {
            Handler handlerMainHandler = this.val$host.mainHandler();
            final Host host = this.val$host;
            final ProgressBar progressBar = this.val$progressBar;
            final View view = this.val$connLed;
            final TextView textView = this.val$connText;
            final LinearLayout linearLayout = this.val$announcementArea;
            final TextView textView2 = this.val$tvStatus;
            final View view2 = this.val$keyCard;
            final LinearLayout linearLayout2 = this.val$contentLayout;
            final LinearLayout linearLayout3 = this.val$footerArea;
            final Button button = this.val$btnLogin;
            final EditText editText = this.val$etCardKey;
            final View view3 = this.val$secondaryRow;
            final String str = this.val$savedCard;
            final boolean[] zArr = this.val$autoVerifySavedCardStarted;
            final Runnable runnable = this.val$autoVerifySavedCard;
            handlerMainHandler.post(new Runnable() { // from class: com.android.support.VerifyOverlayAuth$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VerifyOverlayAuth.AnonymousClass2.lambda$onResult$0(host, progressBar, updateInfo, view, textView, linearLayout, textView2, view2, linearLayout2, linearLayout3, button, editText, view3, str, zArr, runnable);
                }
            });
        }

        static /* synthetic */ void lambda$onResult$0(Host host, ProgressBar progressBar, WigVerify.UpdateInfo updateInfo, View view, TextView textView, LinearLayout linearLayout, TextView textView2, View view2, LinearLayout linearLayout2, LinearLayout linearLayout3, Button button, EditText editText, View view3, String str, boolean[] zArr, Runnable runnable) {
            if (host.isAlive()) {
                progressBar.setVisibility(8);
                Dbg.d(VerifyOverlayAuth.TAG, "Verify overlay version check: needUpdate=" + updateInfo.needUpdate + ", forceUpdate=" + updateInfo.isForceUpdate + ", announcement=" + (updateInfo.announcement != null ? updateInfo.announcement.length() + "chars" : "null"));
                view.setBackground(DsTokens.led(host.context(), DsTokens.OK));
                textView.setText("授权服务已连接");
                textView.setTextColor(DsTokens.OK);
                if (updateInfo.announcement != null && !updateInfo.announcement.isEmpty()) {
                    host.showAnnouncement(linearLayout, updateInfo.announcement);
                }
                if (updateInfo.isForceUpdate && updateInfo.needUpdate) {
                    textView2.setTag(VerifyOverlayAuth.UPDATE_STATUS_OWNER);
                    host.wigVerify().cancelPendingLogin();
                    host.showForceUpdate(view2, linearLayout2, linearLayout3, button, editText, textView2, view3, updateInfo);
                } else {
                    if (updateInfo.needUpdate) {
                        if (textView2.getTag() != VerifyOverlayAuth.AUTH_STATUS_OWNER) {
                            host.showOptionalUpdate(textView2, updateInfo);
                            button.setEnabled(true);
                        }
                        VerifyOverlayAuth.enterOrStartSavedCard(host, str, zArr, runnable);
                        return;
                    }
                    if (textView2.getTag() != VerifyOverlayAuth.AUTH_STATUS_OWNER) {
                        textView2.setText("✓ 版本最新");
                        textView2.setTextColor(DsTokens.OK);
                        button.setEnabled(true);
                    }
                    VerifyOverlayAuth.enterOrStartSavedCard(host, str, zArr, runnable);
                }
            }
        }

        @Override // com.android.support.WigVerify.VersionCheckCallback
        public void onError(final String str) {
            Handler handlerMainHandler = this.val$host.mainHandler();
            final Host host = this.val$host;
            final ProgressBar progressBar = this.val$progressBar;
            final View view = this.val$connLed;
            final TextView textView = this.val$connText;
            final TextView textView2 = this.val$tvStatus;
            final Button button = this.val$btnLogin;
            final String str2 = this.val$savedCard;
            final boolean[] zArr = this.val$autoVerifySavedCardStarted;
            final Runnable runnable = this.val$autoVerifySavedCard;
            handlerMainHandler.post(new Runnable() { // from class: com.android.support.VerifyOverlayAuth$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VerifyOverlayAuth.AnonymousClass2.lambda$onError$1(host, progressBar, str, view, textView, textView2, button, str2, zArr, runnable);
                }
            });
        }

        static /* synthetic */ void lambda$onError$1(Host host, ProgressBar progressBar, String str, View view, TextView textView, TextView textView2, Button button, String str2, boolean[] zArr, Runnable runnable) {
            if (host.isAlive()) {
                progressBar.setVisibility(8);
                Log.w(VerifyOverlayAuth.TAG, "Verify overlay version check failed: " + str);
                view.setBackground(DsTokens.led(host.context(), DsTokens.WARN));
                textView.setText("版本信息暂未取得，可继续验证");
                textView.setTextColor(DsTokens.WARN);
                if (textView2.getTag() != VerifyOverlayAuth.AUTH_STATUS_OWNER) {
                    textView2.setText("版本检查未完成，可尝试验证");
                    textView2.setTextColor(DsTokens.WARN);
                    button.setEnabled(true);
                }
                VerifyOverlayAuth.enterOrStartSavedCard(host, str2, zArr, runnable);
            }
        }
    }
}
