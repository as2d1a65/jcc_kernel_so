package com.android.support;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/* loaded from: classes.dex */
final class OverlayWindowRegistry {

    interface FileLog {
        void flog(String str);
    }

    private OverlayWindowRegistry() {
    }

    static boolean safeAddView(WindowManager windowManager, View view, WindowManager.LayoutParams layoutParams, String str, String str2, FileLog fileLog) {
        if (view == null || layoutParams == null || windowManager == null) {
            Log.w(str2, "[safeAddView] 跳过: view=" + (view != null) + ", params=" + (layoutParams != null) + ", wm=" + (windowManager != null) + ", tag=" + str);
            return false;
        }
        try {
            try {
                if (view.getParent() != null) {
                    try {
                        windowManager.updateViewLayout(view, layoutParams);
                        return true;
                    } catch (IllegalArgumentException unused) {
                        Log.w(str2, "[safeAddView] view 挂载于其它 WindowManager，先移除: " + str);
                        try {
                            windowManager.removeView(view);
                        } catch (Exception unused2) {
                        }
                    }
                }
                windowManager.addView(view, layoutParams);
                return true;
            } catch (Exception e) {
                Log.e(str2, "[safeAddView] 未知异常: " + str, e);
                if (fileLog != null) {
                    fileLog.flog("[safeAddView] 未知异常: " + str + " - " + e.getMessage());
                }
                return false;
            }
        } catch (WindowManager.BadTokenException e2) {
            Log.e(str2, "[safeAddView] BadTokenException（token 过期）: " + str, e2);
            if (fileLog != null) {
                fileLog.flog("[safeAddView] BadTokenException: " + str + " - " + e2.getMessage());
            }
            return false;
        } catch (IllegalStateException e3) {
            Log.e(str2, "[safeAddView] IllegalStateException: " + str, e3);
            if (fileLog != null) {
                fileLog.flog("[safeAddView] IllegalStateException: " + str + " - " + e3.getMessage());
            }
            return false;
        } catch (SecurityException e4) {
            Log.e(str2, "[safeAddView] SecurityException（悬浮窗权限被回收？）: " + str, e4);
            if (fileLog != null) {
                fileLog.flog("[safeAddView] SecurityException: " + str + " - " + e4.getMessage());
            }
            return false;
        }
    }
}
