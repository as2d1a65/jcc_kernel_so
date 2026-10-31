package com.android.support;

import android.content.Context;
import com.android.support.SpecifiedHexOfferTable;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class SpecifiedHexOfferTable {
    static final String SCHEMA = "jcc-hex-offer/v2";
    private final Map<String, ModeIndex> byMode;
    final String liveSeason;
    private final Set<String> modes;

    static final class Row {
        final int id;
        final String mode;
        final int quality;
        final int stagesMask;

        Row(int i, String str, int i2, int i3) {
            this.id = i;
            this.mode = str;
            this.quality = i2;
            this.stagesMask = i3;
        }

        boolean allowsStage(int i) {
            return i >= 1 && i <= 3 && (this.stagesMask & (1 << (i - 1))) != 0;
        }
    }

    private static final class ModeIndex {
        final int[] ids;
        final Row[] rows;

        ModeIndex(int[] iArr, Row[] rowArr) {
            this.ids = iArr;
            this.rows = rowArr;
        }
    }

    private SpecifiedHexOfferTable(String str, Map<String, ModeIndex> map) {
        this.liveSeason = str == null ? "" : str;
        this.byMode = Collections.unmodifiableMap(map);
        this.modes = Collections.unmodifiableSet(new HashSet(map.keySet()));
    }

    static SpecifiedHexOfferTable load(Context context) throws Exception {
        InputStream inputStreamOpen = context.getAssets().open("hex_offer_table.json");
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
                    if (i > 1048576) {
                        throw new IllegalArgumentException("Hex offer table too large");
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

    static SpecifiedHexOfferTable parse(byte[] bArr) throws Exception {
        if (bArr == null || bArr.length == 0 || bArr.length > 1048576) {
            throw new IllegalArgumentException("invalid Hex offer table bytes");
        }
        JSONObject jSONObject = new JSONObject(new String(bArr, StandardCharsets.UTF_8));
        if (!SCHEMA.equals(jSONObject.optString("schema"))) {
            throw new IllegalArgumentException("unsupported Hex offer schema");
        }
        if (!jSONObject.optBoolean("runtimePoolOnly", false)) {
            throw new IllegalArgumentException("Hex offer table is not runtime-pool filtered");
        }
        String strTrim = jSONObject.optString("liveSeason", "").trim();
        JSONArray jSONArray = jSONObject.getJSONArray("entries");
        if (jSONArray.length() != jSONObject.getInt("count") || jSONArray.length() < 100 || jSONArray.length() > 2000) {
            throw new IllegalArgumentException("invalid Hex offer cardinality");
        }
        HashMap map = new HashMap();
        HashSet hashSet = new HashSet();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
            int i2 = jSONObject2.getInt("id");
            String strTrim2 = jSONObject2.getString("mode").trim();
            int i3 = jSONObject2.getInt("quality");
            int iStagesMask = stagesMask(jSONObject2.getJSONArray("stages"));
            if (i2 <= 0 || i2 > 2000000 || i3 < 1 || i3 > 4 || !strTrim2.matches("\\d{1,3}") || iStagesMask == 0 || !hashSet.add(strTrim2 + ':' + i2)) {
                throw new IllegalArgumentException("invalid Hex offer row at " + i);
            }
            List arrayList = (List) map.get(strTrim2);
            if (arrayList == null) {
                arrayList = new ArrayList();
                map.put(strTrim2, arrayList);
            }
            arrayList.add(new Row(i2, strTrim2, i3, iStagesMask));
        }
        HashMap map2 = new HashMap();
        for (Map.Entry entry : map.entrySet()) {
            List list = (List) entry.getValue();
            Collections.sort(list, new Comparator() { // from class: com.android.support.SpecifiedHexOfferTable$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return Integer.compare(((SpecifiedHexOfferTable.Row) obj).id, ((SpecifiedHexOfferTable.Row) obj2).id);
                }
            });
            int[] iArr = new int[list.size()];
            Row[] rowArr = new Row[list.size()];
            for (int i4 = 0; i4 < list.size(); i4++) {
                Row row = (Row) list.get(i4);
                iArr[i4] = row.id;
                rowArr[i4] = row;
            }
            map2.put((String) entry.getKey(), new ModeIndex(iArr, rowArr));
        }
        return new SpecifiedHexOfferTable(strTrim, map2);
    }

    Row get(String str, int i) {
        ModeIndex modeIndex;
        int iBinarySearch;
        if (str == null || (modeIndex = this.byMode.get(str)) == null || (iBinarySearch = Arrays.binarySearch(modeIndex.ids, i)) < 0) {
            return null;
        }
        return modeIndex.rows[iBinarySearch];
    }

    boolean coversMode(String str) {
        return str != null && this.modes.contains(str);
    }

    private static int stagesMask(JSONArray jSONArray) throws Exception {
        int i = 0;
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            int i3 = jSONArray.getInt(i2);
            if (i3 < 1 || i3 > 3) {
                throw new IllegalArgumentException("invalid offer stage");
            }
            i |= 1 << (i3 - 1);
        }
        return i;
    }
}
