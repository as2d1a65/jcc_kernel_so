package com.android.support;

import java.util.Locale;

/* loaded from: classes.dex */
final class TestNetworkRule {
    static final int DURATION_SECONDS = 30;
    final String address;
    final String chain;
    final int port;
    final String protocol;
    final int targetUid;

    TestNetworkRule(boolean z, int i, int i2, String str, String str2, String str3, String str4) throws NumberFormatException {
        String strTrim;
        if (!z) {
            throw new IllegalArgumentException("仅 TEST 包支持网络实验");
        }
        if (i < 10000 || i2 < 10000 || i2 % 100000 < 10000 || i == i2 || i / 100000 != i2 / 100000) {
            throw new IllegalArgumentException("请选择当前用户的独立游戏应用");
        }
        String strTrim2 = str == null ? "" : str.trim();
        String[] strArrSplit = strTrim2.split("\\.", -1);
        if (strArrSplit.length != 4) {
            throw new IllegalArgumentException("请输入单个 IPv4 地址");
        }
        int i3 = 0;
        for (int i4 = 0; i4 < 4; i4++) {
            if (!strArrSplit[i4].matches("0|[1-9][0-9]{0,2}")) {
                throw new IllegalArgumentException("IPv4 地址格式错误");
            }
            int i5 = Integer.parseInt(strArrSplit[i4]);
            if (i5 > 255) {
                throw new IllegalArgumentException("IPv4 地址格式错误");
            }
            if (i4 == 0) {
                i3 = i5;
            }
        }
        if (i3 == 0 || i3 == 127 || i3 >= 224) {
            throw new IllegalArgumentException("请输入单个远端 IPv4 地址");
        }
        if (str2 == null) {
            strTrim = "";
        } else {
            strTrim = str2.trim();
        }
        if (!strTrim.matches("[1-9][0-9]{0,4}")) {
            throw new IllegalArgumentException("端口须为 1–65535");
        }
        int i6 = Integer.parseInt(strTrim);
        if (i6 > 65535) {
            throw new IllegalArgumentException("端口须为 1–65535");
        }
        String lowerCase = str3 != null ? str3.toLowerCase(Locale.ROOT) : "";
        if (!lowerCase.equals("tcp") && !lowerCase.equals("udp")) {
            throw new IllegalArgumentException("协议须为 TCP 或 UDP");
        }
        if (str4 == null || !str4.matches("[a-f0-9]{12}")) {
            throw new IllegalArgumentException("实验标识无效");
        }
        this.address = strTrim2;
        this.port = i6;
        this.protocol = lowerCase;
        this.targetUid = i2;
        this.chain = "XFNT_" + Integer.toString(i, 36) + "_" + str4;
    }

    String script(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("缺少取消标记路径");
        }
        return "set -e\nipt() { iptables -w 2 \"$@\"; }\ncancel=" + quote(str) + "\n[ ! -e \"$cancel\" ] || exit 0\nipt -N " + this.chain + "\ncleanup() {\n  for attempt in 1 2 3; do\n    ipt -D " + ("OUTPUT -m owner --uid-owner " + this.targetUid + " -j " + this.chain) + " 2>/dev/null || true\n    if ipt -F " + this.chain + " && ipt -X " + this.chain + "; then\n      echo XF_NET_CLEARED; return 0\n    fi\n  done\n  echo XF_NET_CLEANUP_FAILED >&2; return 1\n}\ntrap 'result=$?; trap - EXIT HUP INT TERM; cleanup || result=1; exit \"$result\"' EXIT\ntrap 'exit 130' HUP INT TERM\nipt -A " + this.chain + " -d " + this.address + "/32 -p " + this.protocol + " --dport " + this.port + " -j DROP\n[ ! -e \"$cancel\" ] || exit 0\nipt -I OUTPUT 1 -m owner --uid-owner " + this.targetUid + " -j " + this.chain + "\necho XF_NET_ACTIVE\nremaining=30\nwhile [ \"$remaining\" -gt 0 ] && [ ! -e \"$cancel\" ]; do\n  sleep 1\n  remaining=$((remaining - 1))\ndone\nipt -nvx -L " + this.chain + "\n";
    }

    String cleanupScript() {
        return "iptables -w 2 -S OUTPUT >/dev/null || exit 1\nif ! iptables -w 2 -S " + this.chain + " >/dev/null 2>&1; then echo XF_NET_CLEARED; exit 0; fi\niptables -w 2 -D OUTPUT -m owner --uid-owner " + this.targetUid + " -j " + this.chain + " 2>/dev/null || true\niptables -w 2 -F " + this.chain + " && iptables -w 2 -X " + this.chain + " && echo XF_NET_CLEARED\n";
    }

    private static String quote(String str) {
        return "'" + str.replace("'", "'\\''") + "'";
    }
}
