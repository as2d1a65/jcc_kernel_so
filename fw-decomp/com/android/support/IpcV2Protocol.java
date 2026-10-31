package com.android.support;

import androidx.appcompat.app.AppCompatDelegate;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public final class IpcV2Protocol {
    public static final int MAX_FRAME_BYTES = 1048576;
    public static final int TRANSPORT_ABSTRACT = 0;
    public static final int TRANSPORT_TCP = 1;
    public static final int VERSION = 2;
    private static final byte[] CLIENT_DOMAIN = domain("ipc-v2/cl");
    private static final byte[] SERVER_DOMAIN = domain("ipc-v2/sv");

    private IpcV2Protocol() {
    }

    public static byte[] transcript(long j, long j2, long j3, byte[] bArr, byte[] bArr2, byte[] bArr3, int i) {
        requirePositive(j, "serverGeneration");
        requirePositive(j2, "connectionId");
        requirePositive(j3, "tokenEpoch");
        requireLength(bArr, 16, "challengeId");
        requireLength(bArr2, 32, "serverNonce");
        requireLength(bArr3, 32, "clientNonce");
        if (i != 0 && i != 1) {
            throw new IllegalArgumentException("invalid transport");
        }
        return ByteBuffer.allocate(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY).order(ByteOrder.BIG_ENDIAN).putInt(2).putLong(j).putLong(j2).putLong(j3).put(bArr).put(bArr2).put(bArr3).put((byte) i).array();
    }

    public static byte[] clientProof(byte[] bArr, byte[] bArr2) throws GeneralSecurityException {
        requireLength(bArr, 32, "token");
        return hmac(bArr, concat(CLIENT_DOMAIN, bArr2));
    }

    public static byte[] serverProof(byte[] bArr, byte[] bArr2, byte[] bArr3) throws GeneralSecurityException {
        requireLength(bArr, 32, "token");
        requireLength(bArr3, 16, "sessionId");
        return hmac(bArr, concat(SERVER_DOMAIN, bArr2, bArr3));
    }

    public static boolean proofEquals(byte[] bArr, byte[] bArr2) {
        return (bArr == null || bArr2 == null || !MessageDigest.isEqual(bArr, bArr2)) ? false : true;
    }

    public static String base64Url(byte[] bArr) {
        if (bArr == null) {
            throw new IllegalArgumentException("bytes == null");
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bArr);
    }

    public static byte[] decodeBase64Url(String str, int i) {
        if (str == null || str.indexOf(61) >= 0 || !str.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("invalid base64url");
        }
        try {
            byte[] bArrDecode = Base64.getUrlDecoder().decode(str);
            requireLength(bArrDecode, i, "decoded value");
            if (base64Url(bArrDecode).equals(str)) {
                return bArrDecode;
            }
            throw new IllegalArgumentException("non-canonical base64url");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid base64url", e);
        }
    }

    public static String randomId(SecureRandom secureRandom) {
        byte[] bArr = new byte[16];
        secureRandom.nextBytes(bArr);
        return base64Url(bArr);
    }

    public static long randomPositiveLong(SecureRandom secureRandom) {
        long jNextLong;
        do {
            jNextLong = secureRandom.nextLong() & Long.MAX_VALUE;
        } while (jNextLong == 0);
        return jNextLong;
    }

    private static byte[] hmac(byte[] bArr, byte[] bArr2) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(bArr, "HmacSHA256"));
        return mac.doFinal(bArr2);
    }

    private static byte[] domain(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        byte[] bArr = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, bArr, 0, bytes.length);
        return bArr;
    }

    private static byte[] concat(byte[]... bArr) {
        int length = 0;
        for (byte[] bArr2 : bArr) {
            if (bArr2 == null) {
                throw new IllegalArgumentException("null byte array");
            }
            length += bArr2.length;
        }
        byte[] bArr3 = new byte[length];
        int length2 = 0;
        for (byte[] bArr4 : bArr) {
            System.arraycopy(bArr4, 0, bArr3, length2, bArr4.length);
            length2 += bArr4.length;
        }
        return bArr3;
    }

    private static void requireLength(byte[] bArr, int i, String str) {
        if (bArr == null || bArr.length != i) {
            throw new IllegalArgumentException(str + " must be " + i + " bytes");
        }
    }

    private static void requirePositive(long j, String str) {
        if (j <= 0) {
            throw new IllegalArgumentException(str + " must be positive");
        }
    }
}
