package com.android.support;

/* loaded from: classes.dex */
public final class VolumeKeyToggleGate {
    public static final long DEBOUNCE_MS = 400;
    private long lastAcceptedToggleAtMs = Long.MIN_VALUE;
    private int lastMusic;
    private int lastNotification;
    private int lastRing;
    private int lastSystem;
    private boolean seeded;

    public boolean onVolumeSnapshot(boolean z, int i, int i2, int i3, int i4, long j) {
        if (!this.seeded) {
            this.lastMusic = i;
            this.lastRing = i2;
            this.lastNotification = i3;
            this.lastSystem = i4;
            this.seeded = true;
            return false;
        }
        boolean z2 = (i == this.lastMusic && i2 == this.lastRing && i3 == this.lastNotification && i4 == this.lastSystem) ? false : true;
        this.lastMusic = i;
        this.lastRing = i2;
        this.lastNotification = i3;
        this.lastSystem = i4;
        if (!z || !z2) {
            return false;
        }
        long j2 = this.lastAcceptedToggleAtMs;
        if (j2 != Long.MIN_VALUE && j - j2 < 400) {
            return false;
        }
        this.lastAcceptedToggleAtMs = j;
        return true;
    }

    public void reset() {
        this.seeded = false;
        this.lastMusic = 0;
        this.lastRing = 0;
        this.lastNotification = 0;
        this.lastSystem = 0;
        this.lastAcceptedToggleAtMs = Long.MIN_VALUE;
    }
}
