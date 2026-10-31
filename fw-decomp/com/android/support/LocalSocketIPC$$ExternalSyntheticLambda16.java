package com.android.support;

import com.android.support.IpcV2PendingRequests;
import com.android.support.LocalSocketIPC;

/* compiled from: D8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class LocalSocketIPC$$ExternalSyntheticLambda16 implements IpcV2PendingRequests.Callback {
    public final /* synthetic */ LocalSocketIPC.RequestCallback f$0;

    @Override // com.android.support.IpcV2PendingRequests.Callback
    public final void onTerminal(Object obj) {
        this.f$0.onTerminal((LocalSocketIPC.TerminalResult) obj);
    }
}
