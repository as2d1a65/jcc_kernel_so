package com.android.support;

import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
final class RecastReminderPolicy {
    private long lastShownAt;
    private final Set<String> shown = new HashSet();

    RecastReminderPolicy() {
    }

    static boolean actionable(String str, String str2) {
        if (!RecastRunStatus.PHASE_BLOCKED.equals(str) || str2 == null) {
            return false;
        }
        str2.hashCode();
        switch (str2) {
            case "no_candidate":
            case "no_recaster":
            case "source_is_target":
            case "wrong_tier":
            case "target_unavailable":
            case "need_replace":
                return true;
            default:
                return false;
        }
    }

    boolean claim(String str, String str2, long j) {
        if (!actionable(str, str2) || this.shown.contains(str2)) {
            return false;
        }
        long j2 = this.lastShownAt;
        if (j2 != 0 && j >= j2 && j - j2 < 2500) {
            return false;
        }
        this.shown.add(str2);
        this.lastShownAt = j;
        return true;
    }

    void reset() {
        this.shown.clear();
        this.lastShownAt = 0L;
    }
}
