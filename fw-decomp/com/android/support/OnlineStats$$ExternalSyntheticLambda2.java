package com.android.support;

/* compiled from: D8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class OnlineStats$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ OnlineStats f$0;

    public /* synthetic */ OnlineStats$$ExternalSyntheticLambda2(OnlineStats onlineStats) {
        this.f$0 = onlineStats;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.sendScheduledHeartbeat();
    }
}
