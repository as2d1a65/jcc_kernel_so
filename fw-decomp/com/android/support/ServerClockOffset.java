package com.android.support;

import android.os.SystemClock;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

/* loaded from: classes.dex */
public final class ServerClockOffset {
    static final long MAX_PLAUSIBLE_OFFSET_SECONDS = 315360000;
    private static final ServerClockOffset SHARED = new ServerClockOffset();
    public static final long TOLERANCE_SECONDS = 40;
    private volatile Anchor anchor;
    private final LongSupplier elapsedMillis;
    private final AtomicLong measurements;
    private final AtomicLong offsetSeconds;

    private static final class Anchor {
        final long elapsedMillis;
        final long epochMillis;

        Anchor(long j, long j2) {
            this.epochMillis = j;
            this.elapsedMillis = j2;
        }
    }

    private ServerClockOffset() {
        this(new LongSupplier() { // from class: com.android.support.ServerClockOffset$$ExternalSyntheticLambda0
            @Override // java.util.function.LongSupplier
            public final long getAsLong() {
                return SystemClock.elapsedRealtime();
            }
        });
    }

    ServerClockOffset(LongSupplier longSupplier) {
        this.offsetSeconds = new AtomicLong(0L);
        this.measurements = new AtomicLong(0L);
        this.elapsedMillis = longSupplier;
    }

    public static ServerClockOffset shared() {
        return SHARED;
    }

    public boolean record(long j, long j2) {
        if (j <= 0 || j2 <= 0 || j > 9223372036854775L) {
            return false;
        }
        long j3 = j - j2;
        if (Math.abs(j3) > MAX_PLAUSIBLE_OFFSET_SECONDS) {
            return false;
        }
        this.offsetSeconds.set(j3);
        this.anchor = new Anchor(j * 1000, this.elapsedMillis.getAsLong());
        this.measurements.incrementAndGet();
        return true;
    }

    public long offsetSeconds() {
        return this.offsetSeconds.get();
    }

    public boolean measured() {
        return this.measurements.get() > 0;
    }

    public boolean outsideTolerance() {
        return measured() && Math.abs(this.offsetSeconds.get()) > 40;
    }

    public long correctedEpochSeconds(long j) {
        return this.anchor == null ? j : correctedEpochMillis(j * 1000) / 1000;
    }

    public long currentOffsetSeconds() {
        long jCurrentTimeMillis = System.currentTimeMillis() / 1000;
        return correctedEpochSeconds(jCurrentTimeMillis) - jCurrentTimeMillis;
    }

    public long correctedEpochMillis(long j) {
        Anchor anchor = this.anchor;
        if (anchor == null) {
            return j;
        }
        return anchor.epochMillis + Math.max(0L, this.elapsedMillis.getAsLong() - anchor.elapsedMillis);
    }

    public String describeDrift() {
        long j = this.offsetSeconds.get();
        long jAbs = Math.abs(j);
        String str = j > 0 ? "慢" : "快";
        if (jAbs >= 3600) {
            return String.format(Locale.US, "%s约 %d 小时", str, Long.valueOf(jAbs / 3600));
        }
        if (jAbs >= 60) {
            return String.format(Locale.US, "%s约 %d 分钟", str, Long.valueOf(jAbs / 60));
        }
        return String.format(Locale.US, "%s约 %d 秒", str, Long.valueOf(jAbs));
    }

    void resetForTest() {
        this.offsetSeconds.set(0L);
        this.measurements.set(0L);
        this.anchor = null;
    }
}
