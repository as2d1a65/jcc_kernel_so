package com.android.support;

import com.android.support.lineup.AutoLineupApplier;
import com.android.support.trust.TrustControlResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class GameRegionPolicy {
    private final EnumMap<GameRegion, Entry> entries;
    public final long issuedAt;
    public final long legacyRevision;
    public final long revision;

    public static final class Entry {
        public final boolean enabled;
        public final String message;
        public final UiSurfacePolicy surfaces;
        public final boolean visible;

        Entry(boolean z, boolean z2, String str, UiSurfacePolicy uiSurfacePolicy) {
            this.visible = z;
            this.enabled = z2;
            this.message = str;
            this.surfaces = uiSurfacePolicy;
        }
    }

    private GameRegionPolicy(long j, long j2, long j3, EnumMap<GameRegion, Entry> enumMap) {
        this.revision = j;
        this.legacyRevision = j2;
        this.issuedAt = j3;
        this.entries = enumMap;
    }

    public Entry forRegion(GameRegion gameRegion) {
        return this.entries.get(gameRegion);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v14 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8 */
    public static GameRegionPolicy parse(String str, int i, TrustControlResponse.PolicyVerifier policyVerifier) throws JSONException, IOException {
        String str2 = "id";
        if (str == null || str.length() > 65536 || policyVerifier == null) {
            throw new IOException("regional policy bounds");
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            byte[] bArrDecode = Base64.getDecoder().decode(jSONObject.getString("payload"));
            if (bArrDecode.length > 32768) {
                throw new IOException("regional payload bounds");
            }
            String string = jSONObject.getString("signature");
            if (!string.matches("[0-9a-f]{128}")) {
                throw new IOException("regional signature format");
            }
            byte[] bArr = new byte[64];
            for (int i2 = 0; i2 < 64; i2++) {
                int i3 = i2 * 2;
                bArr[i2] = (byte) Integer.parseInt(string.substring(i3, i3 + 2), 16);
            }
            byte[] bytes = "JCCREGIONSV1\u0000".getBytes(StandardCharsets.US_ASCII);
            byte[] bArr2 = new byte[bytes.length + bArrDecode.length];
            System.arraycopy(bytes, 0, bArr2, 0, bytes.length);
            System.arraycopy(bArrDecode, 0, bArr2, bytes.length, bArrDecode.length);
            if (!policyVerifier.verify(bArr2, bArr)) {
                throw new IOException("regional signature invalid");
            }
            JSONObject jSONObject2 = new JSONObject(new String(bArrDecode, StandardCharsets.UTF_8));
            ?? r3 = 1;
            if (jSONObject2.getInt("schema") != 1 || jSONObject2.getInt("app_version") != i) {
                throw new IOException("regional policy identity");
            }
            long j = jSONObject2.getLong("revision");
            long j2 = jSONObject2.getLong("legacy_revision");
            long j3 = jSONObject2.getLong("issued_at");
            if (j < 0 || j2 < 0 || j3 < 0) {
                throw new IOException("regional revision bounds");
            }
            JSONArray jSONArray = jSONObject2.getJSONArray("regions");
            if (jSONArray.length() != GameRegion.values().length) {
                throw new IOException("regional catalog incomplete");
            }
            EnumMap enumMap = new EnumMap(GameRegion.class);
            int i4 = 0;
            while (i4 < jSONArray.length()) {
                JSONObject jSONObject3 = jSONArray.getJSONObject(i4);
                GameRegion gameRegionFromId = GameRegion.fromId(jSONObject3.getString(str2));
                if (!gameRegionFromId.packageName.equals(jSONObject3.getString("package")) || !gameRegionFromId.adapterId.equals(jSONObject3.getString("adapter")) || enumMap.containsKey(gameRegionFromId)) {
                    throw new IOException("regional package mismatch");
                }
                String string2 = jSONObject3.getString("message");
                if (string2.getBytes(StandardCharsets.UTF_8).length > 1000) {
                    throw new IOException("regional message bounds");
                }
                JSONArray jSONArray2 = jSONObject3.getJSONArray("surfaces");
                if (jSONArray2.length() >= r3) {
                    boolean z = r3;
                    if (jSONArray2.length() <= 64) {
                        boolean[] zArr = new boolean[UiSurfaceRegistry.size()];
                        JSONArray jSONArray3 = jSONArray;
                        for (int i5 = 0; i5 < jSONArray2.length(); i5++) {
                            int iIndexOf = UiSurfaceRegistry.indexOf(jSONArray2.getJSONObject(i5).getString(str2));
                            if (iIndexOf >= 0) {
                                if (zArr[iIndexOf]) {
                                    throw new IOException("regional surface identity");
                                }
                                zArr[iIndexOf] = z;
                            }
                        }
                        enumMap.put((EnumMap) gameRegionFromId, (GameRegion) new Entry(jSONObject3.getBoolean("visible"), jSONObject3.getBoolean(AutoLineupApplier.PREF_KEY_ENABLED), string2, UiSurfacePolicy.fromJson((int) (j % 2147483647L), jSONArray2)));
                        i4++;
                        jSONArray = jSONArray3;
                        r3 = z;
                        str2 = str2;
                    }
                }
                throw new IOException("regional surfaces incomplete");
            }
            return new GameRegionPolicy(j, j2, j3, enumMap);
        } catch (IllegalArgumentException | JSONException e) {
            throw new IOException("invalid regional policy", e);
        }
    }

    public boolean supersedes(GameRegionPolicy gameRegionPolicy) {
        if (gameRegionPolicy != null) {
            return this.revision >= gameRegionPolicy.revision && this.legacyRevision >= gameRegionPolicy.legacyRevision && this.issuedAt >= gameRegionPolicy.issuedAt;
        }
        return true;
    }
}
