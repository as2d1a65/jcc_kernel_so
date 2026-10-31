package com.android.support;

import java.nio.charset.StandardCharsets;

/* loaded from: classes.dex */
final class LoginSessionProof {
    final String card;
    private final long deadlineElapsed;
    final long expiresAt;
    private final long startedElapsed;

    private LoginSessionProof(String str, long j, long j2, long j3) {
        this.card = str;
        this.expiresAt = j;
        this.startedElapsed = j3;
        long j4 = j - j2;
        this.deadlineElapsed = j4 <= Long.MAX_VALUE - j3 ? j3 + j4 : Long.MAX_VALUE;
    }

    static boolean validCard(String str) {
        return (str == null || str.trim().isEmpty() || str.getBytes(StandardCharsets.UTF_8).length > 256) ? false : true;
    }

    static LoginSessionProof accept(String str, long j, boolean z, long j2, long j3) {
        if (validCard(str) && z && j > 0 && j <= 9223372036854775L && j2 > 0 && j3 >= 0) {
            long j4 = j * 1000;
            if (j4 > j2) {
                return new LoginSessionProof(str, j4, j2, j3);
            }
        }
        return null;
    }

    boolean current(String str, long j, long j2) {
        return validCard(str) && this.card.equals(str) && j > 0 && j < this.expiresAt && j2 >= this.startedElapsed && j2 < this.deadlineElapsed;
    }
}
