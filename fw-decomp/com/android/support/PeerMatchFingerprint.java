package com.android.support;

import com.bumptech.glide.load.Key;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.UByte;

/* loaded from: classes.dex */
final class PeerMatchFingerprint {
    private PeerMatchFingerprint() {
    }

    static boolean shouldStart(boolean z, List<PlayerInfo> list) {
        return z && list != null && list.size() >= 2;
    }

    static int mySeat(List<PlayerInfo> list) {
        if (list == null) {
            return -1;
        }
        for (PlayerInfo playerInfo : list) {
            if (playerInfo.isMe) {
                return playerInfo.playerId;
            }
        }
        return -1;
    }

    static String namesKey(List<PlayerInfo> list) {
        if (list == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (PlayerInfo playerInfo : list) {
            arrayList.add(playerInfo.name != null ? playerInfo.name : "");
        }
        Collections.sort(arrayList);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            if (i > 0) {
                sb.append("|");
            }
            sb.append((String) arrayList.get(i));
        }
        return sb.toString();
    }

    static boolean isNewHash(String str, String str2) {
        return (str == null || str.equals(str2)) ? false : true;
    }

    static String md5(String str) {
        try {
            byte[] bArrDigest = MessageDigest.getInstance("MD5").digest(str.getBytes(Key.STRING_CHARSET_NAME));
            StringBuilder sb = new StringBuilder();
            for (byte b : bArrDigest) {
                sb.append(String.format("%02x", Integer.valueOf(b & UByte.MAX_VALUE)));
            }
            return sb.toString();
        } catch (Exception unused) {
            return String.valueOf(str.hashCode());
        }
    }
}
