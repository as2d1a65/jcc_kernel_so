package com.android.support;

/* loaded from: classes.dex */
public final class RootHelperGameState {
    public final long cpuTicks;
    public final String il2cppBase;
    public final boolean injected;
    public final int pid;
    public final long rssKb;

    public RootHelperGameState(int i, long j, long j2, String str, boolean z) {
        this.pid = i;
        this.cpuTicks = j;
        this.rssKb = j2;
        this.il2cppBase = str;
        this.injected = z;
    }

    public String toString() {
        return "RootHelperGameState{pid=" + this.pid + ", cpuTicks=" + this.cpuTicks + ", rssKb=" + this.rssKb + ", il2cppBase=" + this.il2cppBase + ", injected=" + this.injected + "}";
    }
}
