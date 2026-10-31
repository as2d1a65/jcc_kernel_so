package com.android.support;

/* loaded from: classes.dex */
public final class JccSessionState {
    private static final JccSessionState SHARED = new JccSessionState();
    private String accessToken;
    private long expiresAtEpochSeconds;
    private long generation;
    private GameRegion region = GameRegion.CN;

    public static JccSessionState shared() {
        return SHARED;
    }

    public synchronized long install(String str, long j) {
        return install(str, j, GameRegionState.current());
    }

    public synchronized long install(String str, long j, GameRegion gameRegion) {
        long j2;
        if (gameRegion != GameRegionState.current()) {
            throw new IllegalArgumentException("session region changed");
        }
        this.region = gameRegion;
        validateToken(str);
        if (j <= 0) {
            throw new IllegalArgumentException("token expiry must be positive");
        }
        long j3 = this.generation;
        j2 = 1;
        if (j3 != Long.MAX_VALUE) {
            j2 = 1 + j3;
        }
        this.generation = j2;
        this.accessToken = str;
        this.expiresAtEpochSeconds = j;
        return j2;
    }

    public synchronized long installIfCurrent(String str, long j, GameRegion gameRegion, long j2) {
        if (this.generation == j2 && gameRegion == GameRegionState.current()) {
            return install(str, j, gameRegion);
        }
        return 0L;
    }

    public synchronized String bearerToken(long j) {
        String str;
        if (this.region == GameRegionState.current() && (str = this.accessToken) != null) {
            if (j < this.expiresAtEpochSeconds) {
                return str;
            }
        }
        return null;
    }

    public String bearerTokenNow() {
        return bearerToken(ServerClockOffset.shared().correctedEpochSeconds(System.currentTimeMillis() / 1000));
    }

    public synchronized long generation() {
        return this.generation;
    }

    public synchronized Snapshot snapshot() {
        return new Snapshot(this.generation, this.accessToken == null ? GameRegionState.current() : this.region, this.accessToken, this.expiresAtEpochSeconds);
    }

    public synchronized boolean expireIfCurrent(long j) {
        if (this.generation == j && this.accessToken != null) {
            this.expiresAtEpochSeconds = 0L;
            return true;
        }
        return false;
    }

    public static final class Snapshot {
        public final long expiresAt;
        public final long generation;
        public final GameRegion region;
        public final String token;

        Snapshot(long j, GameRegion gameRegion, String str, long j2) {
            this.generation = j;
            this.region = gameRegion;
            this.token = str;
            this.expiresAt = j2;
        }

        public boolean present() {
            return this.token != null && this.region == GameRegionState.current();
        }
    }

    public synchronized boolean clear(long j) {
        long j2 = this.generation;
        if (j != j2) {
            return false;
        }
        this.accessToken = null;
        this.expiresAtEpochSeconds = 0L;
        long j3 = 1;
        if (j2 != Long.MAX_VALUE) {
            j3 = 1 + j2;
        }
        this.generation = j3;
        return true;
    }

    public synchronized void clearAll() {
        this.accessToken = null;
        this.expiresAtEpochSeconds = 0L;
        long j = this.generation;
        long j2 = 1;
        if (j != Long.MAX_VALUE) {
            j2 = 1 + j;
        }
        this.generation = j2;
    }

    private static void validateToken(String str) {
        if (str == null || str.isEmpty() || str.length() > 256) {
            throw new IllegalArgumentException("opaque bearer token is invalid");
        }
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt <= ' ' || cCharAt >= 127) {
                throw new IllegalArgumentException("opaque bearer token is invalid");
            }
        }
    }
}
