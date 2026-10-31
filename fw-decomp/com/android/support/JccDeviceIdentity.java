package com.android.support;

import android.security.keystore.KeyGenParameterSpec;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

/* loaded from: classes.dex */
public final class JccDeviceIdentity {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "jcc_gateway_device_p256_v1";
    private static final JccDeviceIdentity SHARED = new JccDeviceIdentity();

    public static JccDeviceIdentity shared() {
        return SHARED;
    }

    public synchronized String publicKeyBase64Url() throws Exception {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(getOrCreateEntry().getCertificate().getPublicKey().getEncoded());
    }

    public synchronized String signBase64Url(byte[] bArr) throws Exception {
        Signature signature;
        if (bArr != null) {
            if (bArr.length != 0 && bArr.length <= 4096) {
                PrivateKey privateKey = getOrCreateEntry().getPrivateKey();
                signature = Signature.getInstance("SHA256withECDSA");
                signature.initSign(privateKey);
                signature.update(bArr);
            }
        }
        throw new IllegalArgumentException("refresh message is invalid");
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signature.sign());
    }

    private KeyStore.PrivateKeyEntry getOrCreateEntry() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(KEY_ALIAS, null);
        if (entry instanceof KeyStore.PrivateKeyEntry) {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
            if ("EC".equals(privateKeyEntry.getPrivateKey().getAlgorithm())) {
                return privateKeyEntry;
            }
            throw new IllegalStateException("gateway device key has the wrong algorithm");
        }
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", ANDROID_KEY_STORE);
        keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(KEY_ALIAS, 4).setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1")).setDigests("SHA-256").setUserAuthenticationRequired(false).build());
        keyPairGenerator.generateKeyPair();
        KeyStore.Entry entry2 = keyStore.getEntry(KEY_ALIAS, null);
        if (!(entry2 instanceof KeyStore.PrivateKeyEntry)) {
            throw new IllegalStateException("gateway device key creation failed");
        }
        return (KeyStore.PrivateKeyEntry) entry2;
    }
}
