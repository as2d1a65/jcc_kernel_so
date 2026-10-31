package com.android.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
final class GameDataLocation {
    static final String CODE_DIRECT = "ok_direct";
    static final String CODE_DIR_NOT_FOUND = "dir_not_found";
    static final String CODE_ESCAPE_REJECTED = "escape_rejected";
    static final String CODE_INIT_NAMESPACE = "ok_init_ns";
    static final String CODE_NAMESPACE_ISOLATED = "namespace_isolated";
    static final String CODE_PROBE_FAILED = "probe_failed";
    static final String CODE_PROCESS_GONE = "process_gone";
    static final String CODE_UID_UNREADABLE = "uid_unreadable";
    static final String DIRECT_VIEW = "direct";
    static final String INIT_MOUNT_NAMESPACE = "/proc/1/ns/mnt";
    static final String INIT_VIEW = "init";
    static final String TARGET_VIEW = "target";
    static final String MARKER = "__JCC_GAME_DATA__";
    private static final Pattern MARKER_LINE = Pattern.compile("(?m)^\\s*" + Pattern.quote(MARKER) + "\\s+([^\\r\\n]+?)\\s*$");
    private static final Pattern EXPANDED_DIR_PATTERN = Pattern.compile("^/mnt/expand/[0-9A-Za-z-]{4,40}/user/(\\d{1,7})/([A-Za-z0-9._]+)$");

    private GameDataLocation() {
    }

    enum Escape {
        NONE(null, "当前"),
        INIT_NAMESPACE(GameDataLocation.INIT_MOUNT_NAMESPACE, "全局");

        private final String description;
        private final String mountNamespaceFile;

        Escape(String str, String str2) {
            this.mountNamespaceFile = str;
            this.description = str2;
        }

        String mountNamespaceFile() {
            return this.mountNamespaceFile;
        }

        String description() {
            return this.description;
        }
    }

    static List<String> candidateDirs(long j, String str) {
        ArrayList arrayList = new ArrayList(3);
        arrayList.add("/data/user/" + j + "/" + str);
        if (j == 0) {
            arrayList.add("/data/data/" + str);
        }
        arrayList.add("/data/user_de/" + j + "/" + str);
        return arrayList;
    }

    static boolean isCandidateDir(String str, long j, String str2) {
        if (str != null && str2 != null) {
            if (candidateDirs(j, str2).contains(str)) {
                return true;
            }
            Matcher matcher = EXPANDED_DIR_PATTERN.matcher(str);
            if (matcher.matches() && matcher.group(1).equals(Long.toString(j)) && matcher.group(2).equals(str2)) {
                return true;
            }
        }
        return false;
    }

    static String buildProbeCommand(int i, String str, String str2, String str3) {
        if (i <= 0) {
            throw new IllegalArgumentException("pid");
        }
        if (str == null || !str.matches("[A-Za-z0-9._]+")) {
            throw new IllegalArgumentException("packageName");
        }
        return "PID=" + i + "; GUID=$(stat -c %u /proc/$PID 2>/dev/null); case \"$GUID\" in ''|*[!0-9]*) GUID=$(awk '/^Uid:/{print $2; exit}' /proc/$PID/status 2>/dev/null);; esac; case \"$GUID\" in ''|*[!0-9]*) GUID=$(grep -m 1 '^Uid:' /proc/$PID/status 2>/dev/null | tr -s ' \\t' ' ' | cut -d ' ' -f 2);; esac; case \"$GUID\" in ''|*[!0-9]*) echo '__JCC_GAME_DATA__ uid=none user=none'; exit 0;; esac; GUSER=$((GUID/100000)); echo \"__JCC_GAME_DATA__ uid=$GUID user=$GUSER\"; CANDS=\"/data/user/$GUSER/" + str + " /data/user_de/$GUSER/" + str + "\"; [ \"$GUSER\" = 0 ] && CANDS=\"/data/user/0/" + str + " /data/data/" + str + " /data/user_de/0/" + str + "\"; for VIEW in direct init target; do case \"$VIEW\" in direct) BASE=''; NSF='';; init) BASE=/proc/1/root; NSF=/proc/1/ns/mnt;; *) BASE=/proc/$PID/root; NSF=/proc/$PID/ns/mnt;; esac; HIT=none; for CAND in $CANDS; do if [ -d \"$BASE$CAND\" ]; then HIT=\"$CAND\"; break; fi; done; if [ \"$HIT\" = none ]; then for EXP in \"$BASE\"/mnt/expand/*/user/$GUSER/" + str + "; do if [ -d \"$EXP\" ]; then HIT=${EXP#\"$BASE\"}; break; fi; done; fi; SELF=0; [ -d \"$BASE\"" + quote(str2) + " ] && SELF=1; LIB=0; [ -d \"$BASE\"" + quote(str3) + " ] && LIB=1; HOP=na; if [ -n \"$NSF\" ]; then HOP=fail; if [ \"$(nsenter --mount=$NSF -- sh -c 'echo hopok' 2>/dev/null)\" = hopok ]; then HOP=ok; fi; fi; echo \"__JCC_GAME_DATA__ view=$VIEW dir=$HIT self=$SELF lib=$LIB hop=$HOP\"; done";
    }

    private static String quote(String str) {
        if (str == null) {
            return "''";
        }
        return "'" + str.replace("'", "'\\''") + "'";
    }

    static final class View {
        private final String dir;
        private final String hop;
        private final boolean libraryVisible;
        private final String name;
        private final boolean selfVisible;

        private View(String str, String str2, boolean z, boolean z2, String str3) {
            this.name = str;
            this.dir = str2;
            this.selfVisible = z;
            this.libraryVisible = z2;
            this.hop = str3 == null ? "na" : str3;
        }

        String name() {
            return this.name;
        }

        String dir() {
            return this.dir;
        }

        boolean found() {
            return this.dir != null;
        }

        boolean usable() {
            return found();
        }

        boolean hoppable() {
            return "ok".equals(this.hop);
        }

        String summary() {
            StringBuilder sbAppend = new StringBuilder().append(this.name).append("=");
            String str = this.dir;
            if (str == null) {
                str = "none";
            }
            return sbAppend.append(str).append(",self=").append(this.selfVisible ? 1 : 0).append(",lib=").append(this.libraryVisible ? 1 : 0).append(",hop=").append(this.hop).toString();
        }
    }

    static final class Result {
        private final boolean parsed;
        private final long uid;
        private final long userId;
        private final Map<String, View> views;

        private Result(boolean z, long j, long j2, Map<String, View> map) {
            this.parsed = z;
            this.uid = j;
            this.userId = j2;
            this.views = map;
        }

        boolean parsed() {
            return this.parsed;
        }

        long uid() {
            return this.uid;
        }

        long userId() {
            return this.userId;
        }

        boolean hasUid() {
            return this.uid >= 0 && this.userId >= 0;
        }

        View view(String str) {
            return this.views.get(str);
        }

        String summary() {
            StringBuilder sb = new StringBuilder("uid=");
            long j = this.uid;
            StringBuilder sbAppend = sb.append(j < 0 ? "none" : Long.valueOf(j)).append(" user=");
            long j2 = this.userId;
            sbAppend.append(j2 >= 0 ? Long.valueOf(j2) : "none");
            Iterator<View> it = this.views.values().iterator();
            while (it.hasNext()) {
                sb.append(' ').append(it.next().summary());
            }
            return sb.toString();
        }
    }

    static Result parse(String str) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        long j = -1;
        long j2 = -1;
        if (str == null) {
            return new Result(false, j, j2, linkedHashMap);
        }
        Matcher matcher = MARKER_LINE.matcher(str);
        boolean z = false;
        while (matcher.find()) {
            z = true;
            Map<String, String> mapFields = fields(matcher.group(1));
            String str2 = mapFields.get("view");
            if (str2 != null) {
                String str3 = mapFields.get("dir");
                if (str3 == null || "none".equals(str3)) {
                    str3 = null;
                }
                linkedHashMap.put(str2, new View(str2, str3, "1".equals(mapFields.get("self")), "1".equals(mapFields.get("lib")), mapFields.get("hop")));
            } else {
                long j3 = parseLong(mapFields.get("uid"));
                if (j3 >= 0) {
                    long j4 = parseLong(mapFields.get("user"));
                    if (j4 < 0) {
                        j4 = j3 / 100000;
                    }
                    j2 = j4;
                    j = j3;
                }
            }
        }
        return new Result(z, j, j2, linkedHashMap);
    }

    private static Map<String, String> fields(String str) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (String str2 : str.trim().split("\\s+")) {
            int iIndexOf = str2.indexOf(61);
            if (iIndexOf > 0 && iIndexOf != str2.length() - 1) {
                linkedHashMap.put(str2.substring(0, iIndexOf), str2.substring(iIndexOf + 1));
            }
        }
        return linkedHashMap;
    }

    private static long parseLong(String str) {
        if (str == null || !str.matches("\\d{1,18}")) {
            return -1L;
        }
        try {
            return Long.parseLong(str);
        } catch (RuntimeException unused) {
            return -1L;
        }
    }

    static final class Decision {
        private final String code;
        private final String dataDir;
        private final String detail;
        private final Escape escape;

        private Decision(String str, Escape escape, String str2, String str3) {
            this.dataDir = str;
            this.escape = escape;
            this.code = str2;
            this.detail = str3;
        }

        String dataDir() {
            return this.dataDir;
        }

        Escape escape() {
            return this.escape;
        }

        String code() {
            return this.code;
        }

        boolean resolved() {
            return this.dataDir != null;
        }

        String detail() {
            return "code=" + this.code + " escape=" + this.escape.name().toLowerCase(Locale.ROOT) + " " + this.detail;
        }

        String userMessage() {
            String str = this.code;
            switch (str.hashCode()) {
                case -1469125148:
                    if (str.equals(GameDataLocation.CODE_DIR_NOT_FOUND)) {
                        return "找不到所选游戏服的数据目录：分身、多开或虚拟机里的游戏连不上，请用手机原生安装的客户端，打开过一次后再点“连接游戏”。[数据目录-缺失]";
                    }
                    return "检查游戏数据目录没有完成（高级权限命令超时）：请再点一次“连接游戏”；仍然失败请重启设备后再试。[数据目录-超时]";
                case -1434991700:
                    str.equals(GameDataLocation.CODE_PROBE_FAILED);
                    return "检查游戏数据目录没有完成（高级权限命令超时）：请再点一次“连接游戏”；仍然失败请重启设备后再试。[数据目录-超时]";
                case 1562273661:
                    if (str.equals(GameDataLocation.CODE_NAMESPACE_ISOLATED)) {
                        return "已拿到高级权限，但权限会话看不到游戏数据目录（挂载空间被隔离）。请在 Magisk/KernelSU/APatch 里给记牌器开启“全局挂载命名空间(Mount Master)”，或把 su 改回默认路径后重试。[数据目录-命名空间]";
                    }
                    return "检查游戏数据目录没有完成（高级权限命令超时）：请再点一次“连接游戏”；仍然失败请重启设备后再试。[数据目录-超时]";
                case 1993001071:
                    if (str.equals(GameDataLocation.CODE_PROCESS_GONE)) {
                        return "读不到游戏进程信息：请完全关闭《金铲铲之战》后重新打开，回到游戏界面再点“连接游戏”。[数据目录-进程]";
                    }
                    return "检查游戏数据目录没有完成（高级权限命令超时）：请再点一次“连接游戏”；仍然失败请重启设备后再试。[数据目录-超时]";
                case 2002110076:
                    if (str.equals(GameDataLocation.CODE_ESCAPE_REJECTED)) {
                        return "已拿到高级权限，但权限会话看不到游戏数据目录（挂载空间被隔离）。请在 Magisk/KernelSU/APatch 里给记牌器开启“全局挂载命名空间(Mount Master)”，或把 su 改回默认路径后重试。[数据目录-命名空间]";
                    }
                    return "检查游戏数据目录没有完成（高级权限命令超时）：请再点一次“连接游戏”；仍然失败请重启设备后再试。[数据目录-超时]";
                case 2007390424:
                    if (str.equals(GameDataLocation.CODE_UID_UNREADABLE)) {
                        return "读不到游戏进程信息：请完全关闭《金铲铲之战》后重新打开，回到游戏界面再点“连接游戏”。[数据目录-进程]";
                    }
                    return "检查游戏数据目录没有完成（高级权限命令超时）：请再点一次“连接游戏”；仍然失败请重启设备后再试。[数据目录-超时]";
                default:
                    return "检查游戏数据目录没有完成（高级权限命令超时）：请再点一次“连接游戏”；仍然失败请重启设备后再试。[数据目录-超时]";
            }
        }
    }

    static Decision decide(Result result, String str) {
        if (result == null || !result.parsed()) {
            return new Decision(null, Escape.NONE, CODE_PROBE_FAILED, "probe=no_output");
        }
        if (!result.hasUid()) {
            return new Decision(null, Escape.NONE, CODE_UID_UNREADABLE, result.summary());
        }
        long jUserId = result.userId();
        View viewValidated = validated(result.view(DIRECT_VIEW), jUserId, str);
        View viewValidated2 = validated(result.view(INIT_VIEW), jUserId, str);
        View viewValidated3 = validated(result.view(TARGET_VIEW), jUserId, str);
        if (viewValidated == null && viewValidated2 == null && viewValidated3 == null) {
            return new Decision(null, Escape.NONE, CODE_PROBE_FAILED, result.summary());
        }
        if (viewValidated != null && viewValidated.found()) {
            return new Decision(viewValidated.dir(), Escape.NONE, CODE_DIRECT, result.summary());
        }
        if (viewValidated2 != null && viewValidated2.usable() && viewValidated2.hoppable()) {
            return new Decision(viewValidated2.dir(), Escape.INIT_NAMESPACE, CODE_INIT_NAMESPACE, result.summary());
        }
        return new Decision(null, Escape.NONE, (viewValidated2 != null && viewValidated2.found()) || (viewValidated3 != null && viewValidated3.found()) ? CODE_NAMESPACE_ISOLATED : CODE_DIR_NOT_FOUND, result.summary());
    }

    private static View validated(View view, long j, String str) {
        if (view == null) {
            return null;
        }
        if (view.dir() == null || isCandidateDir(view.dir(), j, str)) {
            return view;
        }
        return new View(view.name(), null, view.selfVisible, view.libraryVisible, view.hop);
    }

    static Decision processGone() {
        return new Decision(null, Escape.NONE, CODE_PROCESS_GONE, "pid=gone");
    }

    static Decision escapeRejected(Decision decision) {
        return new Decision(null, Escape.NONE, CODE_ESCAPE_REJECTED, decision == null ? "escape=unverified" : decision.detail);
    }
}
