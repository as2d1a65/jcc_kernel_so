package com.android.support;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
final class HeroIdSetCodec {
    static boolean isInS17Range(int i) {
        if (i >= 11450 && i <= 11470) {
            return true;
        }
        if (i >= 12450 && i <= 12467) {
            return true;
        }
        if (i >= 13450 && i <= 13474) {
            return true;
        }
        if (i < 14370 || i > 14384) {
            return (i >= 15420 && i <= 15437) || i == 18409;
        }
        return true;
    }

    private HeroIdSetCodec() {
    }

    static int removeS17RangeIds(Set<Integer> set) {
        if (set == null || set.isEmpty()) {
            return 0;
        }
        int size = set.size();
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            Integer next = it.next();
            if (next != null && isInS17Range(next.intValue())) {
                it.remove();
            }
        }
        return size - set.size();
    }

    static int removeS17RangeKeysFromMap(Map<Integer, Integer> map) {
        if (map == null || map.isEmpty()) {
            return 0;
        }
        int size = map.size();
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            Integer next = it.next();
            if (next != null && isInS17Range(next.intValue())) {
                it.remove();
            }
        }
        return size - map.size();
    }

    static String joinIntsCsv(Set<Integer> set) {
        if (set == null || set.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer num : set) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(num);
        }
        return sb.toString();
    }

    static String joinStarTargetsCsv(Map<Integer, Integer> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(entry.getKey()).append(':').append(entry.getValue());
        }
        return sb.toString();
    }

    static int getCostFromHeroId(int i) {
        if (i <= 0) {
            return 0;
        }
        while (i >= 100) {
            i /= 10;
        }
        int i2 = i % 10;
        if (i2 < 1 || i2 > 5) {
            return 0;
        }
        return i2;
    }

    static String seasonInternalIdToSetId(int i) {
        switch (i) {
            case 0:
                return "4";
            case 1:
                return "10";
            case 2:
                return "16";
            case 3:
                return "17";
            case 4:
                return "8";
            case 5:
                return "18";
            case 6:
                return "11";
            default:
                return "";
        }
    }
}
