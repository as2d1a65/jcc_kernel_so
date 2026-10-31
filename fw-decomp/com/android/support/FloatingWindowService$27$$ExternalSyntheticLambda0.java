package com.android.support;

import com.android.support.QuickActionController;
import com.android.support.SharedMemManager;

/* compiled from: D8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class FloatingWindowService$27$$ExternalSyntheticLambda0 implements SharedMemManager.ActionCallback {
    public final /* synthetic */ QuickActionController.ResultCallback f$0;

    public /* synthetic */ FloatingWindowService$27$$ExternalSyntheticLambda0(QuickActionController.ResultCallback resultCallback) {
        this.f$0 = resultCallback;
    }

    @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
    public final void onTerminal(boolean z, String str) {
        this.f$0.onResult(z, str);
    }
}
