package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class HeroCatalog {
    private static final String LEGACY_S4_FOLDER = "FUX";
    static final String UNKNOWN_HERO_NAME = "未知英雄";
    private final String assetFolder;
    private final Map<String, String> cnToEn;
    private final Map<String, List<Integer>> cnToIds;
    private final Map<Integer, Integer> formToBase;
    private final Map<Integer, String> idToCn;
    private final Map<Integer, Integer> idToCost;
    private final Map<Integer, String> idToEn;
    private final String setId;
    private final String version;

    private HeroCatalog(String str, String str2, String str3, Map<Integer, String> map, Map<Integer, String> map2, Map<Integer, Integer> map3, Map<String, String> map4, Map<Integer, Integer> map5, Map<String, List<Integer>> map6) {
        this.setId = str;
        this.version = str2;
        this.assetFolder = str3;
        this.idToCn = Collections.unmodifiableMap(map);
        this.idToEn = Collections.unmodifiableMap(map2);
        this.idToCost = Collections.unmodifiableMap(map3);
        this.cnToEn = Collections.unmodifiableMap(map4);
        this.formToBase = Collections.unmodifiableMap(map5);
        this.cnToIds = Collections.unmodifiableMap(map6);
    }

    static HeroCatalog empty(String str) {
        if (str == null) {
            str = "";
        }
        return new HeroCatalog(str, "", "", new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap());
    }

    static HeroCatalog parse(String str, String str2) {
        int id;
        if (str2 == null || str2.isEmpty()) {
            return empty(str);
        }
        try {
            JSONObject jSONObject = new JSONObject(str2);
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("heroes");
            if (jSONObjectOptJSONObject == null || jSONObjectOptJSONObject.length() == 0) {
                return empty(str);
            }
            TreeMap treeMap = new TreeMap();
            Iterator<String> itKeys = jSONObjectOptJSONObject.keys();
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                JSONObject jSONObjectOptJSONObject2 = jSONObjectOptJSONObject.optJSONObject(next);
                if (jSONObjectOptJSONObject2 != null && (id = parseId(next, jSONObjectOptJSONObject2)) > 0) {
                    treeMap.put(Integer.valueOf(id), jSONObjectOptJSONObject2);
                }
            }
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            LinkedHashMap linkedHashMap2 = new LinkedHashMap();
            LinkedHashMap linkedHashMap3 = new LinkedHashMap();
            LinkedHashMap linkedHashMap4 = new LinkedHashMap();
            LinkedHashMap linkedHashMap5 = new LinkedHashMap();
            LinkedHashMap linkedHashMap6 = new LinkedHashMap();
            HashMap map = new HashMap();
            HashMap map2 = new HashMap();
            Iterator it = treeMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                int iIntValue = ((Integer) entry.getKey()).intValue();
                JSONObject jSONObject2 = (JSONObject) entry.getValue();
                String strTrim = jSONObject2.optString("name", "").trim();
                if (!strTrim.isEmpty()) {
                    String strTrim2 = jSONObject2.optString("paint", "").trim();
                    String strPaintPrefix = paintPrefix(strTrim2);
                    Iterator it2 = it;
                    if (!strPaintPrefix.isEmpty()) {
                        Integer num = (Integer) map2.get(strPaintPrefix);
                        map2.put(strPaintPrefix, Integer.valueOf(num == null ? 1 : num.intValue() + 1));
                    }
                    linkedHashMap.put(Integer.valueOf(iIntValue), strTrim);
                    String strStripPaintPrefix = stripPaintPrefix(strTrim2);
                    if (!strStripPaintPrefix.isEmpty()) {
                        linkedHashMap2.put(Integer.valueOf(iIntValue), strStripPaintPrefix);
                    }
                    int iOptInt = jSONObject2.optInt("cost", 0);
                    if (iOptInt < 1 || iOptInt > 5) {
                        iOptInt = costFromId(iIntValue);
                    }
                    if (iOptInt >= 1 && iOptInt <= 5) {
                        linkedHashMap3.put(Integer.valueOf(iIntValue), Integer.valueOf(iOptInt));
                    }
                    Integer num2 = (Integer) map.get(strTrim);
                    if (num2 == null) {
                        map.put(strTrim, Integer.valueOf(iIntValue));
                        if (!strStripPaintPrefix.isEmpty()) {
                            linkedHashMap4.put(strTrim, strStripPaintPrefix);
                        }
                    } else {
                        linkedHashMap5.put(Integer.valueOf(iIntValue), num2);
                    }
                    List arrayList = (List) linkedHashMap6.get(strTrim);
                    if (arrayList == null) {
                        arrayList = new ArrayList(2);
                        linkedHashMap6.put(strTrim, arrayList);
                    }
                    arrayList.add(Integer.valueOf(iIntValue));
                    it = it2;
                }
            }
            return new HeroCatalog(str == null ? "" : str, jSONObject.optString("version", ""), folderForPrefix(dominantPrefix(map2)), linkedHashMap, linkedHashMap2, linkedHashMap3, linkedHashMap4, linkedHashMap5, linkedHashMap6);
        } catch (Exception unused) {
            return empty(str);
        }
    }

    String setId() {
        return this.setId;
    }

    String version() {
        return this.version;
    }

    String assetFolder() {
        return this.assetFolder;
    }

    boolean isEmpty() {
        return this.idToCn.isEmpty();
    }

    int heroCount() {
        return this.idToCn.size();
    }

    String chineseName(int i) {
        String str = this.idToCn.get(Integer.valueOf(i));
        return str == null ? UNKNOWN_HERO_NAME : str;
    }

    boolean isKnownHeroId(int i) {
        return this.idToCn.containsKey(Integer.valueOf(i));
    }

    String englishName(int i) {
        String str = this.idToEn.get(Integer.valueOf(i));
        return str == null ? "" : str;
    }

    String englishNameByChineseName(String str) {
        String str2;
        return (str == null || (str2 = this.cnToEn.get(str)) == null) ? "" : str2;
    }

    int cost(int i) {
        Integer num = this.idToCost.get(Integer.valueOf(i));
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    int canonicalBaseId(int i) {
        Integer num = this.formToBase.get(Integer.valueOf(i));
        return num == null ? i : num.intValue();
    }

    List<Integer> idsByChineseName(String str) {
        if (str == null) {
            return Collections.emptyList();
        }
        List<Integer> list = this.cnToIds.get(str);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    List<Integer> baseIdsByCost(int i) {
        ArrayList arrayList = new ArrayList();
        Iterator<Map.Entry<Integer, String>> it = this.idToCn.entrySet().iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().getKey().intValue();
            if (!this.formToBase.containsKey(Integer.valueOf(iIntValue)) && cost(iIntValue) == i) {
                arrayList.add(Integer.valueOf(iIntValue));
            }
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    private static int parseId(String str, JSONObject jSONObject) throws NumberFormatException {
        int positiveInt = parsePositiveInt(jSONObject.optString("id", ""));
        return positiveInt > 0 ? positiveInt : parsePositiveInt(str);
    }

    private static int parsePositiveInt(String str) throws NumberFormatException {
        if (str != null && !str.isEmpty()) {
            try {
                int i = Integer.parseInt(str.trim());
                if (i > 0) {
                    return i;
                }
            } catch (NumberFormatException unused) {
            }
        }
        return 0;
    }

    private static String paintPrefix(String str) {
        int iIndexOf;
        if (str == null || (iIndexOf = str.indexOf(95)) <= 0) {
            return "";
        }
        return str.substring(0, iIndexOf);
    }

    private static String stripPaintPrefix(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        int iIndexOf = str.indexOf(95);
        return iIndexOf <= 0 ? str : str.substring(iIndexOf + 1);
    }

    private static String dominantPrefix(Map<String, Integer> map) {
        String key = "";
        int i = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            int iIntValue = entry.getValue() == null ? 0 : entry.getValue().intValue();
            if (iIntValue > i) {
                key = entry.getKey();
                i = iIntValue;
            }
        }
        return key;
    }

    private static String folderForPrefix(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return "s4".equals(str) ? LEGACY_S4_FOLDER : str.toUpperCase(Locale.ROOT);
    }

    private static int costFromId(int i) {
        while (i >= 100) {
            i /= 10;
        }
        return i % 10;
    }
}
