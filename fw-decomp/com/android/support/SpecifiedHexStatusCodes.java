package com.android.support;

/* loaded from: classes.dex */
final class SpecifiedHexStatusCodes {
    private static final String[] HIT = {"target_hit_slot_1", "target_hit_slot_2", "target_hit_slot_3"};
    private static final String[] READY = {"target_ready_slot_1", "target_ready_slot_2", "target_ready_slot_3"};
    private static final String[] UNAVAILABLE = {"refresh_unavailable_slot_1", "refresh_unavailable_slot_2", "refresh_unavailable_slot_3"};

    private SpecifiedHexStatusCodes() {
    }

    static int unavailableSlotBit(String str) {
        if (str == null) {
            return 0;
        }
        int i = 0;
        while (true) {
            String[] strArr = UNAVAILABLE;
            if (i >= strArr.length) {
                return 0;
            }
            if (strArr[i].equals(str)) {
                return 1 << i;
            }
            i++;
        }
    }

    static int completedSlotBit(String str) {
        if (str == null) {
            return 0;
        }
        int i = 0;
        while (true) {
            String[] strArr = HIT;
            if (i >= strArr.length) {
                return 0;
            }
            if (strArr[i].equals(str) || READY[i].equals(str)) {
                break;
            }
            i++;
        }
        return 1 << i;
    }

    static boolean isOwnRefreshHit(String str) {
        if (str == null) {
            return false;
        }
        for (String str2 : HIT) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }
}
