package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.SecureRandom;

/* loaded from: classes.dex */
final class XfOnlinePresenceId {
    static final int ID_LENGTH = 32;
    private static final String KEY_ID = "presence_id_v1";
    private static final String PREF_NAME = "jcc_xf_online_prefs";

    private XfOnlinePresenceId() {
    }

    static String load(Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, 0);
        String string = sharedPreferences.getString(KEY_ID, null);
        if (isValid(string)) {
            return string;
        }
        String strGenerate = generate(new SecureRandom());
        sharedPreferences.edit().putString(KEY_ID, strGenerate).apply();
        return strGenerate;
    }

    static boolean isValid(String str) {
        if (str == null || str.length() != 32) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if ((cCharAt < '0' || cCharAt > '9') && (cCharAt < 'a' || cCharAt > 'f')) {
                return false;
            }
        }
        return true;
    }

    static String generate(SecureRandom secureRandom) {
        byte[] bArr = new byte[16];
        secureRandom.nextBytes(bArr);
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 16; i++) {
            byte b = bArr[i];
            sb.append(Character.forDigit((b >> 4) & 15, 16));
            sb.append(Character.forDigit(b & 15, 16));
        }
        return sb.toString();
    }
}
