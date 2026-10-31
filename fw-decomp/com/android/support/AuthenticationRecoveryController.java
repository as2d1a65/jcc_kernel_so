package com.android.support;

import com.android.support.ProcessAuthRecoveryGate;

/* loaded from: classes.dex */
final class AuthenticationRecoveryController {
    static final long SILENT_AUTH_RETRY_DELAY_MS = 5000;
    private final Host host;
    private final ProcessAuthRecoveryGate recoveryGate;

    interface Host {
        void checkVersion(VersionCallback versionCallback);

        boolean consumeExplicitUserOpen();

        void logRecoveryFailure(boolean z);

        void logRecoveryStart();

        boolean loggedIn();

        void login(String str, LoginCallback loginCallback);

        boolean nativeTrustAuthorized();

        void post(Runnable runnable);

        void postDelayed(Runnable runnable, long j);

        boolean recoveredFromCrash();

        void restoreVerifiedUi();

        String savedCard();

        boolean serviceDestroyed();

        void showInteractiveVerification();
    }

    interface LoginCallback {
        void onFailure(String str);

        void onSuccess();
    }

    interface VersionCallback {
        void onError(String str);

        void onResult(boolean z);
    }

    AuthenticationRecoveryController(Host host, ProcessAuthRecoveryGate processAuthRecoveryGate) {
        this.host = host;
        this.recoveryGate = processAuthRecoveryGate;
    }

    void beginInitialRecovery() {
        String strSavedCard;
        if (this.host.serviceDestroyed()) {
            return;
        }
        boolean zConsumeExplicitUserOpen = this.host.consumeExplicitUserOpen();
        boolean zFullyAuthorized = fullyAuthorized();
        Host host = this.host;
        if (zFullyAuthorized) {
            host.restoreVerifiedUi();
            return;
        }
        if (host.recoveredFromCrash() && !zConsumeExplicitUserOpen && (strSavedCard = this.host.savedCard()) != null && !strSavedCard.isEmpty()) {
            ProcessAuthRecoveryGate.Decision decisionOnInitialReady = this.recoveryGate.onInitialReady(false, true);
            if (decisionOnInitialReady == ProcessAuthRecoveryGate.Decision.START_RECOVERY) {
                this.host.logRecoveryStart();
                startSilentRecovery(strSavedCard);
                return;
            } else if (decisionOnInitialReady == ProcessAuthRecoveryGate.Decision.USE_CURRENT_SESSION) {
                this.host.restoreVerifiedUi();
                return;
            } else if (decisionOnInitialReady == ProcessAuthRecoveryGate.Decision.WAIT) {
                return;
            }
        }
        this.recoveryGate.suppressAutomaticRecovery();
        showInteractiveVerification();
    }

    /* renamed from: com.android.support.AuthenticationRecoveryController$1, reason: invalid class name */
    class AnonymousClass1 implements VersionCallback {
        final /* synthetic */ String val$savedCard;

        AnonymousClass1(String str) {
            this.val$savedCard = str;
        }

        @Override // com.android.support.AuthenticationRecoveryController.VersionCallback
        public void onResult(final boolean z) {
            Host host = AuthenticationRecoveryController.this.host;
            final String str = this.val$savedCard;
            host.post(new Runnable() { // from class: com.android.support.AuthenticationRecoveryController$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m87x987beadd(z, str);
                }
            });
        }

        /* renamed from: lambda$onResult$0$com-android-support-AuthenticationRecoveryController$1, reason: not valid java name */
        /* synthetic */ void m87x987beadd(boolean z, String str) {
            if (AuthenticationRecoveryController.this.recoveryActive()) {
                AuthenticationRecoveryController authenticationRecoveryController = AuthenticationRecoveryController.this;
                if (z) {
                    authenticationRecoveryController.handleFailure(str, "FORCE_UPDATE");
                } else {
                    authenticationRecoveryController.continueSilentRecovery(str);
                }
            }
        }

        @Override // com.android.support.AuthenticationRecoveryController.VersionCallback
        public void onError(String str) {
            Host host = AuthenticationRecoveryController.this.host;
            final String str2 = this.val$savedCard;
            host.post(new Runnable() { // from class: com.android.support.AuthenticationRecoveryController$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m86xdfbac4a9(str2);
                }
            });
        }

        /* renamed from: lambda$onError$1$com-android-support-AuthenticationRecoveryController$1, reason: not valid java name */
        /* synthetic */ void m86xdfbac4a9(String str) {
            if (AuthenticationRecoveryController.this.recoveryActive()) {
                AuthenticationRecoveryController.this.continueSilentRecovery(str);
            }
        }
    }

    void startSilentRecovery(String str) {
        if (recoveryActive()) {
            this.host.checkVersion(new AnonymousClass1(str));
        }
    }

    /* renamed from: com.android.support.AuthenticationRecoveryController$2, reason: invalid class name */
    class AnonymousClass2 implements LoginCallback {
        final /* synthetic */ String val$savedCard;

        AnonymousClass2(String str) {
            this.val$savedCard = str;
        }

        @Override // com.android.support.AuthenticationRecoveryController.LoginCallback
        public void onSuccess() {
            Host host = AuthenticationRecoveryController.this.host;
            final String str = this.val$savedCard;
            host.post(new Runnable() { // from class: com.android.support.AuthenticationRecoveryController$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m88x5b7b8130(str);
                }
            });
        }

        /* renamed from: lambda$onSuccess$0$com-android-support-AuthenticationRecoveryController$2, reason: not valid java name */
        /* synthetic */ void m88x5b7b8130(String str) {
            if (AuthenticationRecoveryController.this.recoveryActive()) {
                if (!AuthenticationRecoveryController.this.fullyAuthorized() || !str.equals(AuthenticationRecoveryController.this.host.savedCard())) {
                    AuthenticationRecoveryController.this.recoveryGate.suppressAutomaticRecovery();
                    AuthenticationRecoveryController.this.showInteractiveVerification();
                } else if (AuthenticationRecoveryController.this.recoveryGate.completeSuccess()) {
                    AuthenticationRecoveryController.this.host.restoreVerifiedUi();
                }
            }
        }

        @Override // com.android.support.AuthenticationRecoveryController.LoginCallback
        public void onFailure(String str) {
            AuthenticationRecoveryController.this.postFailure(this.val$savedCard, str);
        }
    }

    void continueSilentRecovery(String str) {
        if (recoveryActive()) {
            this.host.login(str, new AnonymousClass2(str));
        }
    }

    void postFailure(final String str, final String str2) {
        this.host.post(new Runnable() { // from class: com.android.support.AuthenticationRecoveryController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m85x5bf1437c(str, str2);
            }
        });
    }

    /* renamed from: lambda$postFailure$0$com-android-support-AuthenticationRecoveryController, reason: not valid java name */
    /* synthetic */ void m85x5bf1437c(String str, String str2) {
        if (this.host.serviceDestroyed()) {
            return;
        }
        handleFailure(str, str2);
    }

    void handleFailure(final String str, String str2) {
        boolean zAllowsSilentRetry = GatewayFailurePolicy.allowsSilentRetry(str2);
        ProcessAuthRecoveryGate.FailureAction failureActionCompleteFailure = this.recoveryGate.completeFailure(zAllowsSilentRetry);
        if (failureActionCompleteFailure == ProcessAuthRecoveryGate.FailureAction.RETRY_SILENTLY) {
            this.host.postDelayed(new Runnable() { // from class: com.android.support.AuthenticationRecoveryController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m84xc6518e63(str);
                }
            }, SILENT_AUTH_RETRY_DELAY_MS);
        } else if (failureActionCompleteFailure == ProcessAuthRecoveryGate.FailureAction.SHOW_INTERACTIVE) {
            this.host.logRecoveryFailure(zAllowsSilentRetry);
            showInteractiveVerification();
        }
    }

    /* renamed from: lambda$handleFailure$1$com-android-support-AuthenticationRecoveryController, reason: not valid java name */
    /* synthetic */ void m84xc6518e63(String str) {
        if (this.host.serviceDestroyed() || !this.recoveryGate.beginRetry()) {
            return;
        }
        continueSilentRecovery(str);
    }

    void suppressAutomaticRecovery() {
        this.recoveryGate.suppressAutomaticRecovery();
    }

    void dispose() {
        this.recoveryGate.dispose();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean recoveryActive() {
        return !this.host.serviceDestroyed() && this.recoveryGate.isRunning();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean fullyAuthorized() {
        return LoginSessionProof.validCard(this.host.savedCard()) && this.host.loggedIn() && this.host.nativeTrustAuthorized();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showInteractiveVerification() {
        if (this.host.serviceDestroyed()) {
            return;
        }
        this.host.showInteractiveVerification();
    }
}
