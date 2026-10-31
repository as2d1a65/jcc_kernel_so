package com.android.support;

import androidx.core.os.EnvironmentCompat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
final class InjectionTargetReadiness {
    static final String MARKER = "__JCC_TARGET_STATE__";
    private static final Pattern MARKER_LINE = Pattern.compile("(?m)^\\s*" + Pattern.quote(MARKER) + "\\s+([^\\r\\n]+)\\s*$");

    private InjectionTargetReadiness() {
    }

    static final class Snapshot {
        final String cpuset;
        final int freezeState;
        final int pid;
        final char processState;
        final String shdPnd;
        final String sigPnd;
        final long startTicks;
        final boolean topResumed;
        final int tracerPid;
        final String wchan;

        private Snapshot(Map<String, String> map) {
            this.pid = InjectionTargetReadiness.parseInt(map.get("pid"), -1);
            this.startTicks = InjectionTargetReadiness.parseLong(map.get("start"), 0L);
            this.topResumed = "1".equals(map.get("top"));
            this.freezeState = InjectionTargetReadiness.parseInt(map.get("freeze"), -1);
            String strSafe = InjectionTargetReadiness.safe(map.get("state"), "?");
            this.processState = strSafe.isEmpty() ? '?' : strSafe.charAt(0);
            this.tracerPid = InjectionTargetReadiness.parseInt(map.get("tracer"), -1);
            this.sigPnd = InjectionTargetReadiness.safe(map.get("sigpnd"), EnvironmentCompat.MEDIA_UNKNOWN);
            this.shdPnd = InjectionTargetReadiness.safe(map.get("shdpnd"), EnvironmentCompat.MEDIA_UNKNOWN);
            this.wchan = InjectionTargetReadiness.safe(map.get("wchan"), EnvironmentCompat.MEDIA_UNKNOWN);
            this.cpuset = InjectionTargetReadiness.safe(map.get("cpuset"), EnvironmentCompat.MEDIA_UNKNOWN);
        }

        String summary() {
            return "pid=" + this.pid + " start=" + this.startTicks + " top=" + (this.topResumed ? 1 : 0) + " freeze=" + this.freezeState + " state=" + this.processState + " tracer=" + this.tracerPid + " sigpnd=" + this.sigPnd + " shdpnd=" + this.shdPnd + " wchan=" + this.wchan + " cpuset=" + this.cpuset;
        }
    }

    static Snapshot parse(String str) {
        if (str == null) {
            return new Snapshot(Collections.emptyMap());
        }
        Matcher matcher = MARKER_LINE.matcher(str);
        String strGroup = null;
        while (matcher.find()) {
            strGroup = matcher.group(1);
        }
        if (strGroup == null) {
            return new Snapshot(Collections.emptyMap());
        }
        HashMap map = new HashMap();
        for (String str2 : strGroup.trim().split("\\s+")) {
            int iIndexOf = str2.indexOf(61);
            if (iIndexOf > 0 && iIndexOf != str2.length() - 1) {
                map.put(str2.substring(0, iIndexOf), str2.substring(iIndexOf + 1));
            }
        }
        return new Snapshot(map);
    }

    static boolean isReady(Snapshot snapshot, int i) {
        return "ready".equals(rejectionReason(snapshot, i));
    }

    static boolean shouldCancelInject(Snapshot snapshot, int i) {
        return shouldCancelInject(rejectionReason(snapshot, i));
    }

    static boolean shouldCancelInject(String str) {
        return "frozen".equals(str) || "already_traced".equals(str) || RecastRunStatus.PHASE_STOPPED.equals(str) || "pid_changed".equals(str);
    }

    static Snapshot aliveFallback(int i) {
        return parse("__JCC_TARGET_STATE__ pid=" + Math.max(i, 0) + " start=0 top=0 freeze=-1 state=? tracer=-1 sigpnd=unknown shdpnd=unknown wchan=unknown cpuset=unknown");
    }

    static String rejectionReason(Snapshot snapshot, int i) {
        if (snapshot == null || snapshot.pid <= 0) {
            return "snapshot_unavailable";
        }
        if (snapshot.pid != i) {
            return "pid_changed";
        }
        if (snapshot.freezeState == 1) {
            return "frozen";
        }
        if (snapshot.tracerPid > 0) {
            return "already_traced";
        }
        if (snapshot.processState == 'T' || snapshot.processState == 't') {
            return RecastRunStatus.PHASE_STOPPED;
        }
        return isUnityStartupSleep(snapshot.wchan) ? "startup_sleep" : "ready";
    }

    private static boolean isUnityStartupSleep(String str) {
        return "hrtimer_nanosleep".equals(str) || "nanosleep".equals(str) || "do_nanosleep".equals(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int parseInt(String str, int i) {
        try {
            return Integer.parseInt(str);
        } catch (RuntimeException unused) {
            return i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long parseLong(String str, long j) {
        try {
            return Long.parseLong(str);
        } catch (RuntimeException unused) {
            return j;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String safe(String str, String str2) {
        return (str == null || str.isEmpty()) ? str2 : str;
    }
}
