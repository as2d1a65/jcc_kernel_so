package com.android.support;

import android.content.Context;
import com.bumptech.glide.load.Key;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
final class RecastEquipmentCatalog {
    private static final String ASSET = "recast_equipment_table.txt";
    private static final int MAX_ENTRIES = 600;
    private static final int MIN_ENTRIES = 60;
    private final Map<Integer, Entry> byId;
    private final LinkedHashMap<Integer, List<Entry>> byTier;
    private final String gameVersion;
    private final boolean live;
    private final String matchGeneration;
    private final String seasonTag;

    static final class Entry {
        final int id;
        final String imageName;
        final String name;
        final int poolIndex;
        final String searchKey;
        final int tier;

        Entry(int i, String str, int i2, String str2, int i3) {
            this.id = i;
            this.name = str;
            this.tier = i2;
            this.imageName = str2;
            this.poolIndex = i3;
            this.searchKey = (str + " " + i).toLowerCase(Locale.ROOT);
        }
    }

    private RecastEquipmentCatalog(LinkedHashMap<Integer, List<Entry>> linkedHashMap, Map<Integer, Entry> map, String str, String str2, boolean z, String str3) {
        this.byTier = linkedHashMap;
        this.byId = map;
        this.seasonTag = str;
        this.gameVersion = str2;
        this.live = z;
        this.matchGeneration = str3;
    }

    String seasonTag() {
        return this.seasonTag;
    }

    String gameVersion() {
        return this.gameVersion;
    }

    boolean isLive() {
        return this.live;
    }

    String matchGeneration() {
        return this.matchGeneration;
    }

    static RecastEquipmentCatalog fromLivePool(List<Entry> list, String str) {
        if (list != null && !list.isEmpty()) {
            if (str == null) {
                str = "";
            }
            try {
                return build(list, "", "", true, str);
            } catch (IOException unused) {
            }
        }
        return null;
    }

    static RecastEquipmentCatalog load(Context context) throws IOException {
        if (context == null) {
            throw new IOException("no context");
        }
        ArrayList arrayList = new ArrayList();
        InputStream inputStreamOpen = context.getApplicationContext().getAssets().open(ASSET);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamOpen, Key.STRING_CHARSET_NAME));
            String strTrim = "";
            String strTrim2 = strTrim;
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    try {
                        break;
                    } catch (IOException unused) {
                    }
                } else if (line.startsWith("#season=")) {
                    strTrim = line.substring(8).trim();
                } else if (line.startsWith("#gameVersion=")) {
                    strTrim2 = line.substring(13).trim();
                } else {
                    Entry line2 = parseLine(line);
                    if (line2 != null) {
                        arrayList.add(line2);
                    }
                }
            }
            if (arrayList.size() < 60 || arrayList.size() > MAX_ENTRIES) {
                throw new IOException("recast_equipment_table.txt 条目数异常: " + arrayList.size());
            }
            if (strTrim.isEmpty() || strTrim2.isEmpty()) {
                throw new IOException("重铸目录缺少赛季或游戏版本标记");
            }
            return build(arrayList, strTrim, strTrim2, false, "");
        } finally {
            try {
                inputStreamOpen.close();
            } catch (IOException unused2) {
            }
        }
    }

    private static Entry parseLine(String str) throws NumberFormatException {
        if (str != null && !str.isEmpty() && !str.startsWith("#") && !str.startsWith("ID\t")) {
            String[] strArrSplit = str.split("\t", -1);
            if (strArrSplit.length < 7) {
                return null;
            }
            try {
                int i = Integer.parseInt(strArrSplit[0].trim());
                int i2 = Integer.parseInt(strArrSplit[2].trim());
                int i3 = Integer.parseInt(strArrSplit[6].trim());
                if (i > 0 && i3 >= 0 && RecastTierPolicy.isSelectable(i2)) {
                    String strTrim = strArrSplit[1].trim();
                    if (strTrim.isEmpty()) {
                        return null;
                    }
                    String strTrim2 = strArrSplit[5].trim();
                    return new Entry(i, strTrim, i2, strTrim2.isEmpty() ? null : strTrim2, i3);
                }
            } catch (NumberFormatException unused) {
            }
        }
        return null;
    }

    private static RecastEquipmentCatalog build(List<Entry> list, String str, String str2, boolean z, String str3) throws IOException {
        HashMap map = new HashMap();
        Iterator<Entry> it = list.iterator();
        while (true) {
            int iIntValue = 1;
            if (!it.hasNext()) {
                break;
            }
            Entry next = it.next();
            String str4 = next.tier + "\u0000" + next.name;
            Integer num = (Integer) map.get(str4);
            if (num != null) {
                iIntValue = 1 + num.intValue();
            }
            map.put(str4, Integer.valueOf(iIntValue));
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (int i : RecastTierPolicy.selectableTiers()) {
            linkedHashMap.put(Integer.valueOf(i), new ArrayList());
        }
        HashMap map2 = new HashMap();
        for (Entry entry : list) {
            Integer num2 = (Integer) map.get(entry.tier + "\u0000" + entry.name);
            if (num2 != null && num2.intValue() > 1) {
                entry = new Entry(entry.id, entry.name + " #" + entry.id, entry.tier, entry.imageName, entry.poolIndex);
            }
            List list2 = (List) linkedHashMap.get(Integer.valueOf(entry.tier));
            if (list2 != null) {
                if (entry.poolIndex != list2.size()) {
                    throw new IOException("重铸池顺序不连续: tier=" + entry.tier + " expected=" + list2.size() + " actual=" + entry.poolIndex);
                }
                if (map2.containsKey(Integer.valueOf(entry.id))) {
                    throw new IOException("重铸目录重复 ID: " + entry.id);
                }
                list2.add(entry);
                map2.put(Integer.valueOf(entry.id), entry);
            }
        }
        return new RecastEquipmentCatalog(linkedHashMap, map2, str, str2, z, str3);
    }

    List<Entry> tier(int i) {
        List<Entry> list = this.byTier.get(Integer.valueOf(i));
        return list == null ? Collections.emptyList() : list;
    }

    boolean hasTier(int i) {
        return !tier(i).isEmpty();
    }

    Entry byId(int i) {
        return this.byId.get(Integer.valueOf(i));
    }

    int size() {
        return this.byId.size();
    }
}
