package com.android.support;

/* loaded from: classes.dex */
final class AuthUiDisplayPolicy {

    enum Decision {
        SHOW_MAIN,
        SHOW_VERIFICATION,
        WAIT_FOR_TRUST
    }

    enum InitialConnectionDecision {
        SHOW_INTERACTIVE,
        RECOVER_SILENTLY
    }

    private AuthUiDisplayPolicy() {
    }

    static Decision decide(boolean z, boolean z2) {
        if (z2) {
            return z ? Decision.SHOW_MAIN : Decision.WAIT_FOR_TRUST;
        }
        return Decision.SHOW_VERIFICATION;
    }

    static InitialConnectionDecision decideInitialConnection(boolean z) {
        if (z) {
            return InitialConnectionDecision.SHOW_INTERACTIVE;
        }
        return InitialConnectionDecision.RECOVER_SILENTLY;
    }
}
