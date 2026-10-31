package com.android.support;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Base64;
import kotlin.UByte;
import kotlin.jvm.internal.ByteCompanionObject;

/* loaded from: classes.dex */
public final class JccVersionPolicy {
    private JccVersionPolicy() {
    }

    public static int compare(String str, String str2) {
        long[] jArr = parse(str);
        long[] jArr2 = parse(str2);
        int iMax = Math.max(jArr.length, jArr2.length);
        int i = 0;
        while (i < iMax) {
            long j = i < jArr.length ? jArr[i] : 0L;
            long j2 = i < jArr2.length ? jArr2[i] : 0L;
            if (j < j2) {
                return -1;
            }
            if (j > j2) {
                return 1;
            }
            i++;
        }
        return 0;
    }

    public static boolean requiresUpdate(int i, String str) {
        if (i > 0) {
            return compare(Integer.toString(i), str) < 0;
        }
        throw new IllegalArgumentException("build code must be positive");
    }

    private static long[] parse(String str) {
        if (str == null || str.isEmpty() || !str.matches("[0-9]+(?:\\.[0-9]+)*")) {
            throw new IllegalArgumentException("version must contain numeric segments only");
        }
        String[] strArrSplit = str.split("\\.", -1);
        long[] jArr = new long[strArrSplit.length];
        for (int i = 0; i < strArrSplit.length; i++) {
            try {
                jArr[i] = Long.parseLong(strArrSplit[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("version segment is too large", e);
            }
        }
        return jArr;
    }

    public static boolean verify(byte[] bArr, String str, PublicKey publicKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (bArr != null && publicKey != null && str != null && str.length() == 86 && str.indexOf(61) < 0) {
            try {
                byte[] bArrDecode = Base64.getUrlDecoder().decode(str);
                if (bArrDecode.length != 64) {
                    return false;
                }
                Signature signature = Signature.getInstance("SHA256withECDSA");
                signature.initVerify(publicKey);
                signature.update(bArr);
                return signature.verify(p1363ToDer(bArrDecode));
            } catch (IllegalArgumentException | Exception unused) {
            }
        }
        return false;
    }

    static byte[] derToP1363(byte[] bArr) {
        if (bArr == null || bArr.length < 8 || (bArr[0] & UByte.MAX_VALUE) != 48) {
            throw new IllegalArgumentException("invalid ECDSA DER signature");
        }
        int[] iArr = {1};
        if (readLength(bArr, iArr) != bArr.length - iArr[0]) {
            throw new IllegalArgumentException("invalid ECDSA DER length");
        }
        byte[] integer = readInteger(bArr, iArr);
        byte[] integer2 = readInteger(bArr, iArr);
        if (iArr[0] != bArr.length) {
            throw new IllegalArgumentException("trailing ECDSA DER data");
        }
        byte[] bArr2 = new byte[64];
        copyUnsigned(integer, bArr2, 0);
        copyUnsigned(integer2, bArr2, 32);
        return bArr2;
    }

    private static byte[] p1363ToDer(byte[] bArr) {
        byte[] bArrUnsignedInteger = unsignedInteger(Arrays.copyOfRange(bArr, 0, 32));
        byte[] bArrUnsignedInteger2 = unsignedInteger(Arrays.copyOfRange(bArr, 32, 64));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(70);
        byteArrayOutputStream.write(2);
        byteArrayOutputStream.write(bArrUnsignedInteger.length);
        byteArrayOutputStream.write(bArrUnsignedInteger, 0, bArrUnsignedInteger.length);
        byteArrayOutputStream.write(2);
        byteArrayOutputStream.write(bArrUnsignedInteger2.length);
        byteArrayOutputStream.write(bArrUnsignedInteger2, 0, bArrUnsignedInteger2.length);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream(72);
        byteArrayOutputStream2.write(48);
        byteArrayOutputStream2.write(byteArray.length);
        byteArrayOutputStream2.write(byteArray, 0, byteArray.length);
        return byteArrayOutputStream2.toByteArray();
    }

    private static byte[] unsignedInteger(byte[] bArr) {
        int i = 0;
        while (i < bArr.length - 1 && bArr[i] == 0) {
            i++;
        }
        int length = bArr.length - i;
        int i2 = (bArr[i] & ByteCompanionObject.MIN_VALUE) != 0 ? 1 : 0;
        byte[] bArr2 = new byte[length + i2];
        System.arraycopy(bArr, i, bArr2, i2, length);
        return bArr2;
    }

    private static int readLength(byte[] bArr, int[] iArr) {
        int i = iArr[0];
        if (i >= bArr.length) {
            throw new IllegalArgumentException("missing ECDSA DER length");
        }
        int i2 = i + 1;
        iArr[0] = i2;
        int i3 = bArr[i];
        int i4 = i3 & 255;
        if (i4 < 128) {
            return i4;
        }
        int i5 = i3 & 127;
        if (i5 == 0 || i5 > 2 || i2 + i5 > bArr.length) {
            throw new IllegalArgumentException("invalid ECDSA DER length");
        }
        int i6 = 0;
        for (int i7 = 0; i7 < i5; i7++) {
            int i8 = iArr[0];
            iArr[0] = i8 + 1;
            i6 = (i6 << 8) | (bArr[i8] & UByte.MAX_VALUE);
        }
        return i6;
    }

    private static byte[] readInteger(byte[] bArr, int[] iArr) {
        int i = iArr[0];
        if (i < bArr.length) {
            iArr[0] = i + 1;
            if ((bArr[i] & UByte.MAX_VALUE) == 2) {
                int length = readLength(bArr, iArr);
                if (length > 0) {
                    int i2 = iArr[0];
                    if (i2 + length <= bArr.length) {
                        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr, i2, i2 + length);
                        iArr[0] = iArr[0] + length;
                        if ((bArrCopyOfRange[0] & ByteCompanionObject.MIN_VALUE) == 0) {
                            return bArrCopyOfRange;
                        }
                        throw new IllegalArgumentException("negative ECDSA DER integer");
                    }
                }
                throw new IllegalArgumentException("invalid ECDSA DER integer length");
            }
        }
        throw new IllegalArgumentException("invalid ECDSA DER integer");
    }

    private static void copyUnsigned(byte[] bArr, byte[] bArr2, int i) {
        int i2 = 0;
        while (i2 < bArr.length - 1 && bArr[i2] == 0) {
            i2++;
        }
        int length = bArr.length - i2;
        if (length > 32) {
            throw new IllegalArgumentException("ECDSA integer exceeds P-256 width");
        }
        System.arraycopy(bArr, i2, bArr2, (i + 32) - length, length);
    }
}
