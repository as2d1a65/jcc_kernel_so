package com.android.support;

/* loaded from: classes.dex */
final class OpponentTextRefreshPolicy {
    private static final long OFFSET_BASIS = -3750763034362895579L;
    private static final long PRIME = 1099511628211L;
    static final long UNSET = Long.MIN_VALUE;

    private static long mix(long j, int i) {
        return (j ^ (i & 4294967295L)) * PRIME;
    }

    static boolean shouldRender(long j, long j2, boolean z, boolean z2) {
        return (j != Long.MIN_VALUE && j == j2 && z && z2) ? false : true;
    }

    private OpponentTextRefreshPolicy() {
    }

    static long begin(int i, int i2, boolean z, boolean z2, int i3) {
        return mix(mix(mix(mix(mix(OFFSET_BASIS, i), i2), z), z2), i3);
    }

    static long addPlayer(long j, int i, int i2, int i3, int i4, int i5, boolean z, String str, boolean z2, int i6, int i7) {
        return mix(mix(mix(mix(mix(mix(mix(mix(mix(mix(j, i), i2), i3), i4), i5), z), str), z2), i6), i7);
    }

    static long addPair(long j, int i, int i2, int i3) {
        return mix(mix(mix(j, i), i2), i3);
    }

    static long addCollectionSizes(long j, int i, int i2) {
        return mix(mix(j, i), i2);
    }

    static long addSelectorSummary(long j, int i, int i2, int i3) {
        return mix(mix(mix(j, i), i2), i3);
    }

    private static long mix(long j, boolean z) {
        return mix(j, z ? 1 : 0);
    }

    private static long mix(long j, String str) {
        if (str == null) {
            return mix(j, -1);
        }
        long jMix = mix(j, str.length());
        for (int i = 0; i < str.length(); i++) {
            jMix = mix(jMix, str.charAt(i));
        }
        return jMix;
    }
}
