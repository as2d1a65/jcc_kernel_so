package com.android.support;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class SpecifiedHexCatalog {
    final List<Entry> entries;
    final List<Mode> modes;

    static final class Mode {
        final String id;
        final String name;
        final String season;

        Mode(String str, String str2, String str3) {
            this.id = str;
            this.name = str2;
            this.season = str3 == null ? "" : str3;
        }
    }

    static final class Entry {
        final int banSeq1;
        final int banSeq2;
        final int banSeq3;
        final String description;
        final String icon;
        final int id;
        final int level;
        final String mode;
        final String modeName;
        final String name;
        final String searchKey;
        final String season;

        Entry(int i, String str, String str2, int i2, String str3, String str4, String str5, int i3, int i4, int i5, String str6) {
            this.id = i;
            this.name = str;
            this.description = str2;
            this.level = i2;
            this.mode = str3;
            this.modeName = str4;
            this.season = str5 == null ? "" : str5;
            this.banSeq1 = i3;
            this.banSeq2 = i4;
            this.banSeq3 = i5;
            this.icon = str6;
            this.searchKey = (str + " " + i).toLowerCase(Locale.ROOT);
        }

        boolean bannedOnStage(int i) {
            return SpecifiedHexOfferPolicy.bannedOnStage(i, this.banSeq1, this.banSeq2, this.banSeq3);
        }
    }

    private SpecifiedHexCatalog(List<Mode> list, List<Entry> list2) {
        this.modes = Collections.unmodifiableList(list);
        this.entries = Collections.unmodifiableList(list2);
    }

    static SpecifiedHexCatalog load(Context context) throws Exception {
        InputStream inputStreamOpen = context.getAssets().open("hex_catalog.json");
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                byte[] bArr = new byte[16384];
                int i = 0;
                while (true) {
                    int i2 = inputStreamOpen.read(bArr);
                    if (i2 < 0) {
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        byteArrayOutputStream.close();
                        if (inputStreamOpen != null) {
                            inputStreamOpen.close();
                        }
                        return parse(byteArray);
                    }
                    i += i2;
                    if (i > 5242880) {
                        throw new IllegalArgumentException("Hex catalog too large");
                    }
                    byteArrayOutputStream.write(bArr, 0, i2);
                }
            } finally {
            }
        } catch (Throwable th) {
            if (inputStreamOpen != null) {
                try {
                    inputStreamOpen.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    static SpecifiedHexCatalog parse(byte[] bArr) throws Exception {
        String str;
        if (bArr == null || bArr.length == 0 || bArr.length > 5242880) {
            throw new IllegalArgumentException("invalid Hex catalog bytes");
        }
        JSONObject jSONObject = new JSONObject(new String(bArr, StandardCharsets.UTF_8));
        if (!"jcc-specified-hex-catalog/v1".equals(jSONObject.optString("schema"))) {
            throw new IllegalArgumentException("unsupported Hex catalog schema");
        }
        JSONArray jSONArray = jSONObject.getJSONArray("modes");
        JSONArray jSONArray2 = jSONObject.getJSONArray("entries");
        if (jSONArray.length() < 1 || jSONArray.length() > 32 || jSONArray2.length() != jSONObject.getInt("count") || jSONArray2.length() < 100 || jSONArray2.length() > 5000) {
            throw new IllegalArgumentException("invalid Hex catalog cardinality");
        }
        ArrayList arrayList = new ArrayList(jSONArray.length());
        HashSet hashSet = new HashSet();
        int i = 0;
        while (true) {
            String str2 = "season";
            String str3 = "name";
            String str4 = "mode";
            String str5 = "";
            if (i < jSONArray.length()) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                String string = jSONObject2.getString("mode");
                String string2 = jSONObject2.getString("name");
                String strTrim = jSONObject2.optString("season", "").trim();
                if (!string.matches("\\d{1,3}") || string2.isEmpty() || string2.length() > 20 || ((strTrim.length() > 0 && !strTrim.matches("\\d{1,6}")) || !hashSet.add(string))) {
                    break;
                }
                arrayList.add(new Mode(string, string2, strTrim));
                i++;
            } else {
                ArrayList arrayList2 = new ArrayList(jSONArray2.length());
                HashSet hashSet2 = new HashSet();
                int i2 = 0;
                while (i2 < jSONArray2.length()) {
                    JSONObject jSONObject3 = jSONArray2.getJSONObject(i2);
                    int i3 = jSONObject3.getInt("id");
                    int i4 = jSONObject3.getInt("level");
                    String strTrim2 = jSONObject3.getString(str3).trim();
                    String strTrim3 = jSONObject3.optString("description", str5).trim();
                    String string3 = jSONObject3.getString(str4);
                    String strTrim4 = jSONObject3.getString("modeName").trim();
                    String strTrim5 = jSONObject3.optString(str2, str5).trim();
                    JSONArray jSONArray3 = jSONArray2;
                    String str6 = str2;
                    int iOptInt = jSONObject3.optInt("banSeq1", 0);
                    String str7 = str3;
                    int iOptInt2 = jSONObject3.optInt("banSeq2", 0);
                    String str8 = str4;
                    int iOptInt3 = jSONObject3.optInt("banSeq3", 0);
                    JSONArray jSONArrayOptJSONArray = jSONObject3.optJSONArray("banSeq");
                    if (jSONArrayOptJSONArray != null) {
                        str = str5;
                        if (jSONArrayOptJSONArray.length() > 0) {
                            iOptInt = jSONArrayOptJSONArray.optInt(0, iOptInt);
                        }
                        int i5 = iOptInt;
                        if (jSONArrayOptJSONArray.length() > 1) {
                            iOptInt2 = jSONArrayOptJSONArray.optInt(1, iOptInt2);
                        }
                        if (jSONArrayOptJSONArray.length() > 2) {
                            iOptInt3 = jSONArrayOptJSONArray.optInt(2, iOptInt3);
                        }
                        iOptInt = i5;
                    } else {
                        str = str5;
                    }
                    String string4 = jSONObject3.getString("icon");
                    String string5 = jSONObject3.getString("imageSha256");
                    ArrayList arrayList3 = arrayList;
                    String str9 = string3 + ':' + i3;
                    if (i3 <= 0 || i3 > 2000000 || i4 < 1 || i4 > 4 || strTrim2.isEmpty() || strTrim2.length() > 80 || strTrim3.length() > 2000 || !hashSet.contains(string3) || strTrim4.isEmpty() || ((strTrim5.length() > 0 && !strTrim5.matches("\\d{1,6}")) || iOptInt < 0 || iOptInt > 1 || iOptInt2 < 0 || iOptInt2 > 1 || iOptInt3 < 0 || iOptInt3 > 1 || !string4.matches("hex_icons/\\d{1,3}_\\d{1,7}\\.png") || !string5.matches("[0-9a-f]{64}") || !hashSet2.add(str9))) {
                        throw new IllegalArgumentException("invalid Hex entry at " + i2);
                    }
                    arrayList2.add(new Entry(i3, strTrim2, strTrim3, i4, string3, strTrim4, strTrim5, iOptInt, iOptInt2, iOptInt3, string4));
                    i2++;
                    str3 = str7;
                    jSONArray2 = jSONArray3;
                    str2 = str6;
                    str4 = str8;
                    str5 = str;
                    arrayList = arrayList3;
                }
                return new SpecifiedHexCatalog(arrayList, arrayList2);
            }
        }
        throw new IllegalArgumentException("invalid Hex mode");
    }

    static String levelName(int i) {
        if (i == 1) {
            return "银色";
        }
        if (i == 2) {
            return "金色";
        }
        if (i == 3) {
            return "棱彩";
        }
        if (i == 4) {
            return "特殊";
        }
        return "全部";
    }
}
