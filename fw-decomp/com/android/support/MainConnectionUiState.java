package com.android.support;

/* loaded from: classes.dex */
final class MainConnectionUiState {
    static final int COLOR_CHECKING = -6725632;
    static final int COLOR_CONNECTING = -13541720;
    static final int COLOR_FAILURE = -4377272;
    static final int COLOR_READY = -16220325;

    enum Action {
        WAIT,
        CONNECT,
        RETURN_TO_GAME,
        REPAIR_OVERLAY,
        CHECK_ENVIRONMENT
    }

    enum Phase {
        CHECKING,
        READY,
        CONNECTING,
        RECOVERING,
        CONNECTED_NEEDS_VERIFY,
        CONNECTED_OVERLAY_BLOCKED,
        CONNECTED,
        CONNECTED_INITIALIZING,
        IN_MATCH,
        RETRY,
        NEEDS_SETUP,
        NEEDS_OVERLAY_PERMISSION
    }

    /* renamed from: com.android.support.MainConnectionUiState$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$support$MainConnectionUiState$Phase;

        static {
            int[] iArr = new int[Phase.values().length];
            $SwitchMap$com$android$support$MainConnectionUiState$Phase = iArr;
            try {
                iArr[Phase.READY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.CONNECTING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.RECOVERING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.CONNECTED_NEEDS_VERIFY.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.CONNECTED_OVERLAY_BLOCKED.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.CONNECTED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.CONNECTED_INITIALIZING.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.IN_MATCH.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.RETRY.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.NEEDS_OVERLAY_PERMISSION.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$support$MainConnectionUiState$Phase[Phase.NEEDS_SETUP.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
        }
    }

    static Presentation forPhase(Phase phase) {
        switch (AnonymousClass1.$SwitchMap$com$android$support$MainConnectionUiState$Phase[phase.ordinal()]) {
            case 1:
                return new Presentation("准备好了", "准备就绪", "游戏尚未连接", "连接游戏", Action.CONNECT, -16220325, 0, 1);
            case 2:
                return new Presentation("连接中", "正在连接游戏", "正在建立连接", "正在连接…", Action.WAIT, -13541720, 60, 1);
            case 3:
                return new Presentation("恢复中", "连接暂时中断", "正在等待游戏响应 · 配置已保留", "检查连接", Action.CONNECT, -6725632, 60, 1);
            case 4:
                return new Presentation("待验证", "游戏已连接", "使用码待验证", "返回游戏验证", Action.RETURN_TO_GAME, -13541720, 100, 2);
            case 5:
                return new Presentation("需要处理", "悬浮窗不可见", "游戏已连接 · 悬浮窗权限未开启", "处理悬浮窗权限", Action.REPAIR_OVERLAY, -4377272, 100, 2);
            case 6:
                return new Presentation("已连接", "等待进入对局", "游戏已连接 · 配置已就绪", "返回游戏", Action.RETURN_TO_GAME, -16220325, 100, 2);
            case 7:
                return new Presentation("初始化中", "游戏功能尚未就绪", "组件已连接 · 等待游戏功能初始化", "返回游戏", Action.RETURN_TO_GAME, -13541720, 90, 2);
            case 8:
                return new Presentation("对局中", "对局进行中", "本局数据正在同步", "返回对局", Action.RETURN_TO_GAME, -16220325, 100, 3);
            case 9:
                return new Presentation("未完成", "连接未完成", "等待重新检查", "重新检查连接", Action.CONNECT, -4377272, 0, 1);
            case 10:
                return new Presentation("需要授权", "悬浮窗权限未开启", "连接准备尚未完成", "开启悬浮窗权限", Action.REPAIR_OVERLAY, -4377272, 0, 1);
            case 11:
                return new Presentation("需要授权", "Root 权限未授予", "连接准备尚未完成", "重新检查权限", Action.CHECK_ENVIRONMENT, -4377272, 0, 0);
            default:
                return new Presentation("检查中", "正在检查环境", "正在确认权限与游戏状态", "正在检查…", Action.WAIT, -6725632, 0, 0);
        }
    }

    static boolean isConnectedPhase(Phase phase) {
        return phase == Phase.CONNECTED || phase == Phase.CONNECTED_INITIALIZING || phase == Phase.IN_MATCH || phase == Phase.CONNECTED_NEEDS_VERIFY || phase == Phase.CONNECTED_OVERLAY_BLOCKED;
    }

    static Phase connectedPhase(boolean z, boolean z2, boolean z3) {
        return connectedPhase(z, z2, z3, true);
    }

    static Phase connectedPhase(boolean z, boolean z2, boolean z3, boolean z4) {
        if (!z2) {
            return Phase.CONNECTED_OVERLAY_BLOCKED;
        }
        if (!z) {
            return Phase.CONNECTED_NEEDS_VERIFY;
        }
        if (z4 || z3) {
            return z3 ? Phase.IN_MATCH : Phase.CONNECTED;
        }
        return Phase.CONNECTED_INITIALIZING;
    }

    static final class Presentation {
        private final Action action;
        private final String actionLabel;
        private final String badge;
        private final int color;
        private final int completedSteps;
        private final String footer;
        private final int progress;
        private final String status;

        Presentation(String str, String str2, String str3, String str4, Action action, int i, int i2, int i3) {
            this.badge = str;
            this.status = str2;
            this.footer = str3;
            this.actionLabel = str4;
            this.action = action;
            this.color = i;
            this.progress = i2;
            this.completedSteps = i3;
        }

        String badge() {
            return this.badge;
        }

        String status() {
            return this.status;
        }

        String footer() {
            return this.footer;
        }

        String actionLabel() {
            return this.actionLabel;
        }

        Action action() {
            return this.action;
        }

        int color() {
            return this.color;
        }

        int progress() {
            return this.progress;
        }

        int completedSteps() {
            return this.completedSteps;
        }

        boolean buttonEnabled() {
            return this.action != Action.WAIT;
        }

        boolean busy() {
            return this.action == Action.WAIT;
        }
    }

    private MainConnectionUiState() {
    }
}
