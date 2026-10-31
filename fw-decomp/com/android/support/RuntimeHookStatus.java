package com.android.support;

/* loaded from: classes.dex */
final class RuntimeHookStatus {
    static final RuntimeHookStatus UNKNOWN = new RuntimeHookStatus(false, "waiting");
    final boolean coreReady;
    final String reason;

    private RuntimeHookStatus(boolean z, String str) {
        this.coreReady = z;
        this.reason = str;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0037  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static com.android.support.RuntimeHookStatus parse(org.json.JSONObject r4) {
        /*
            r0 = 0
            if (r4 == 0) goto L67
            java.lang.String r1 = "coreReady"
            java.lang.Object r2 = r4.opt(r1)
            boolean r2 = r2 instanceof java.lang.Boolean
            if (r2 != 0) goto Le
            goto L67
        Le:
            boolean r1 = r4.optBoolean(r1)
            java.lang.String r2 = "reason"
            java.lang.String r3 = ""
            java.lang.String r4 = r4.optString(r2, r3)
            r2 = 1
            if (r1 == 0) goto L2b
            java.lang.String r1 = "ready"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L2a
            com.android.support.RuntimeHookStatus r0 = new com.android.support.RuntimeHookStatus
            r0.<init>(r2, r4)
        L2a:
            return r0
        L2b:
            r4.hashCode()
            int r0 = r4.hashCode()
            r1 = 0
            r3 = -1
            switch(r0) {
                case -2015885117: goto L4d;
                case -331825020: goto L44;
                case 909576681: goto L39;
                default: goto L37;
            }
        L37:
            r2 = r3
            goto L57
        L39:
            java.lang.String r0 = "native_bridge_inline_hooks_unavailable"
            boolean r0 = r4.equals(r0)
            if (r0 != 0) goto L42
            goto L37
        L42:
            r2 = 2
            goto L57
        L44:
            java.lang.String r0 = "native_bridge_callback_probe"
            boolean r0 = r4.equals(r0)
            if (r0 != 0) goto L57
            goto L37
        L4d:
            java.lang.String r0 = "game_api_unresolved"
            boolean r0 = r4.equals(r0)
            if (r0 != 0) goto L56
            goto L37
        L56:
            r2 = r1
        L57:
            switch(r2) {
                case 0: goto L62;
                case 1: goto L62;
                case 2: goto L62;
                default: goto L5a;
            }
        L5a:
            com.android.support.RuntimeHookStatus r4 = new com.android.support.RuntimeHookStatus
            java.lang.String r0 = "initializing"
            r4.<init>(r1, r0)
            return r4
        L62:
            com.android.support.RuntimeHookStatus r0 = new com.android.support.RuntimeHookStatus
            r0.<init>(r1, r4)
        L67:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.RuntimeHookStatus.parse(org.json.JSONObject):com.android.support.RuntimeHookStatus");
    }

    String userMessage() {
        String str = this.reason;
        str.hashCode();
        switch (str) {
            case "game_api_unresolved":
                return "组件已连接，游戏功能尚未初始化完成。请保持游戏在前台；持续无数据时请导出运行日志。";
            case "native_bridge_callback_probe":
            case "native_bridge_inline_hooks_unavailable":
                return "组件已连接，当前模拟环境的游戏功能尚未就绪。请导出运行日志以核对兼容情况。";
            default:
                return "组件已连接，正在等待游戏功能就绪。";
        }
    }
}
