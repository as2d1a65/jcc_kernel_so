package com.android.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import kotlin.UByte;

/* loaded from: classes.dex */
final class OfficialBrandIdentity {
    private static final String ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String FALLBACK_SEED = "XFJCC-OFFICIAL";

    private OfficialBrandIdentity() {
    }

    static String shortId(String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        if (!str.isEmpty()) {
            str2 = str + (char) 31 + str2;
        } else if (str2.isEmpty()) {
            str2 = FALLBACK_SEED;
        }
        try {
            byte[] bArrDigest = MessageDigest.getInstance("SHA-256").digest(str2.getBytes(StandardCharsets.UTF_8));
            return new String(new char[]{ALPHABET.charAt((bArrDigest[0] & UByte.MAX_VALUE) >>> 3), ALPHABET.charAt(((bArrDigest[0] & 7) << 2) | ((bArrDigest[1] & UByte.MAX_VALUE) >>> 6)), ALPHABET.charAt(31 & ((bArrDigest[1] & UByte.MAX_VALUE) >>> 1)), ALPHABET.charAt(((bArrDigest[2] & UByte.MAX_VALUE) >>> 4) | ((bArrDigest[1] & 1) << 4))});
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-256 is unavailable", e);
        }
    }

    static String displayText(String str, String str2, String str3) {
        return str + str2 + " · ID " + str3;
    }
}
