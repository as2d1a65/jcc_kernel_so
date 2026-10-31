package com.android.support;

/* loaded from: classes.dex */
final class OverlayPermissionGuidance {

    enum Blocker {
        NONE,
        PERMISSION_MISSING,
        WINDOW_NOT_SHOWING
    }

    private OverlayPermissionGuidance() {
    }

    static Blocker diagnose(boolean z, boolean z2, boolean z3) {
        if (!z) {
            return Blocker.PERMISSION_MISSING;
        }
        if (z2 && !z3) {
            return Blocker.WINDOW_NOT_SHOWING;
        }
        return Blocker.NONE;
    }

    /* renamed from: com.android.support.OverlayPermissionGuidance$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$support$OverlayPermissionGuidance$Blocker;

        static {
            int[] iArr = new int[Blocker.values().length];
            $SwitchMap$com$android$support$OverlayPermissionGuidance$Blocker = iArr;
            try {
                iArr[Blocker.PERMISSION_MISSING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$support$OverlayPermissionGuidance$Blocker[Blocker.WINDOW_NOT_SHOWING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$support$OverlayPermissionGuidance$Blocker[Blocker.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    static Guidance forBlocker(Blocker blocker, boolean z) {
        int i = AnonymousClass1.$SwitchMap$com$android$support$OverlayPermissionGuidance$Blocker[blocker.ordinal()];
        if (i == 1) {
            return new Guidance("悬浮窗权限没开，验证窗口无法显示", permissionMissingMessage(z), "悬浮窗权限未开启，验证窗口无法显示", "验证窗口是悬浮窗：权限没开时游戏里不会出现任何窗口。点上面「悬浮窗权限」那一行可再看开启步骤", "去开启权限");
        }
        if (i == 2) {
            return new Guidance("验证窗口没能显示出来", windowNotShowingMessage(), "验证窗口未显示在屏幕上", "悬浮窗权限已开，但窗口没能挂上屏幕。点上面「悬浮窗权限」那一行可再看处理步骤", "重新检测");
        }
        return new Guidance("悬浮窗一切正常", "悬浮窗权限已开启，验证窗口可以正常显示在游戏画面上。", "悬浮窗已就绪", "验证窗口可以正常显示", "重新检测");
    }

    private static String permissionMissingMessage(boolean z) {
        String str;
        if (z) {
            str = "记牌器已经用 Root 自动开过一次但没生效，说明这台系统拦掉了自动授权，只能手动开一次。";
        } else {
            str = "当前没有 Root 授权，记牌器无法替你自动开启，只能手动开一次。";
        }
        return "当前情况：使用验证窗口，以及游戏里的所有面板，都是显示在游戏画面上层的悬浮窗。系统还没允许记牌器显示悬浮窗，所以窗口根本没法出现——你切回游戏只会看到屏幕上什么都没有。这不是卡密过期，也不是网络问题。\n\n请这样开启（开一次，之后长期有效）：\n1. 点下面的「去开启权限」，系统会直接打开记牌器的悬浮窗设置页；\n2. 打开「显示在其他应用上层」（有的手机叫「悬浮窗」「允许显示在其他应用上层」）；\n3. 同一页里如果还有「后台弹出界面」，请一起打开，否则窗口会被系统拦掉；\n4. 返回记牌器，本页的「悬浮窗权限」会自动变成「已授权」，不用重启；\n5. 再点「连接游戏」，验证窗口就会出现在游戏画面上。\n\n模拟器（MuMu、雷电等）：在模拟器的「设置 - 应用管理 / 权限管理」里给记牌器开启悬浮窗。\n".concat(str);
    }

    private static String windowNotShowingMessage() {
        return "当前情况：悬浮窗权限已经是开启状态，但记牌器的验证窗口没有真正显示到屏幕上。常见原因是系统把记牌器的后台运行掐断了，或者这台系统/模拟器在权限之外又拦了一层悬浮窗。\n\n请按顺序处理：\n1. 点下面的「重新检测」，记牌器会重新把窗口挂一次，多数情况一次就恢复；\n2. 打开「后台运行」：系统设置 - 电池/省电 里把记牌器设为「不受限制」，并允许自启动；\n3. 回到悬浮窗权限页，确认「后台弹出界面」也是开启状态；\n4. 模拟器上先把游戏和记牌器都完全关掉再重开，避免上一次的窗口占着位置；\n5. 仍然看不到窗口，请联系客服并说明手机型号或模拟器版本。";
    }

    static final class Guidance {
        private final String cardDetail;
        private final String cardStatus;
        private final String message;
        private final String primaryActionLabel;
        private final String title;

        private Guidance(String str, String str2, String str3, String str4, String str5) {
            this.title = str;
            this.message = str2;
            this.cardStatus = str3;
            this.cardDetail = str4;
            this.primaryActionLabel = str5;
        }

        String title() {
            return this.title;
        }

        String message() {
            return this.message;
        }

        String cardStatus() {
            return this.cardStatus;
        }

        String cardDetail() {
            return this.cardDetail;
        }

        String primaryActionLabel() {
            return this.primaryActionLabel;
        }
    }
}
