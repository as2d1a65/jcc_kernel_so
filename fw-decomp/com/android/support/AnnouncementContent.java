package com.android.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class AnnouncementContent {
    final String id;
    final boolean stale;
    final String text;

    AnnouncementContent(String str, String str2, boolean z) {
        this.id = str;
        this.text = str2;
        this.stale = z;
    }

    static AnnouncementContent parse(JSONObject jSONObject) throws IOException {
        Object objOpt = jSONObject.opt("id");
        Object objOpt2 = jSONObject.opt("content");
        Object objOpt3 = jSONObject.opt("stale");
        if ((objOpt instanceof String) && (objOpt2 instanceof String) && (objOpt3 instanceof Boolean)) {
            String str = (String) objOpt2;
            if (str.codePointCount(0, str.length()) <= 8000) {
                String str2 = (String) objOpt;
                if (str2.equals(digest(str))) {
                    return new AnnouncementContent(str2, str, ((Boolean) objOpt3).booleanValue());
                }
            }
        }
        throw new IOException("ANNOUNCEMENT_INVALID");
    }

    static String digest(String str) {
        try {
            byte[] bArrDigest = MessageDigest.getInstance("SHA-256").digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : bArrDigest) {
                sb.append(Character.forDigit((b >>> 4) & 15, 16));
                sb.append(Character.forDigit(b & 15, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
