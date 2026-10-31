package com.android.support;

/* compiled from: D8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class FloatingWindowService$$ExternalSyntheticLambda315 implements Runnable {
    public final /* synthetic */ FloatingWindowService f$0;

    public /* synthetic */ FloatingWindowService$$ExternalSyntheticLambda315(FloatingWindowService floatingWindowService) {
        this.f$0 = floatingWindowService;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.restoreAndClampQuickActionPosition();
    }
}
