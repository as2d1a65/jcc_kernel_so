package com.android.support;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

/* loaded from: classes.dex */
public class EmulatorShortcutService extends AccessibilityService {
    public static final String ACTION_TOGGLE_AUTOPICK = "com.android.support.SHORTCUT_TOGGLE_AUTOPICK";
    public static final String ACTION_TOGGLE_CARDPOOL = "com.android.support.SHORTCUT_TOGGLE_CARDPOOL";
    public static final String ACTION_TOGGLE_WINDOW = "com.android.support.SHORTCUT_TOGGLE_WINDOW";
    public static final boolean DEFAULT_ENABLED = false;
    public static final String INTERNAL_SHORTCUT_PERMISSION = "com.android.support.permission.INTERNAL_SHORTCUT";
    public static final String KEY_ENABLED = "shortcut_enabled";
    public static final String KEY_TOGGLE_AUTOPICK = "shortcut_key_toggle_autopick";
    public static final String KEY_TOGGLE_CARDPOOL = "shortcut_key_toggle_cardpool";
    public static final String KEY_TOGGLE_WINDOW = "shortcut_key_toggle_window";
    public static final String PREF_NAME = "EmulatorShortcutConfig";
    private static final String TAG = "EmulatorShortcut";
    private static volatile EmulatorShortcutService sInstance;
    private static volatile boolean sServiceRunning;
    private volatile boolean enabled;
    private volatile int keyToggleAutoPick;
    private volatile int keyToggleCardPool;
    private volatile int keyToggleWindow;
    private SharedPreferences prefs;
    public static final int DEFAULT_KEY_WINDOW = 131;
    public static final int DEFAULT_KEY_AUTOPICK = 132;
    public static final int DEFAULT_KEY_CARDPOOL = 133;
    public static final int[] AVAILABLE_KEYS = {DEFAULT_KEY_WINDOW, DEFAULT_KEY_AUTOPICK, DEFAULT_KEY_CARDPOOL, 134, 135, 136, 137, 138, 139, SwapBoardMinimizePolicy.FALLBACK_FULL_Y_DP, 141, 142, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 68, 61, 124, 112, 92, 93, 116, 121};

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SharedPreferences getSharedPreferences(String str, int i) {
        return super.getSharedPreferences(GameRegionState.preferenceName(str), i);
    }

    public static String getKeyName(int i) {
        if (i >= 131 && i <= 142) {
            return "F" + (i - 130);
        }
        if (i >= 29 && i <= 54) {
            return String.valueOf((char) (i + 36));
        }
        if (i == 61) {
            return "Tab";
        }
        if (i == 68) {
            return "` 波浪";
        }
        if (i == 112) {
            return "Del";
        }
        if (i == 116) {
            return "ScrLk";
        }
        if (i == 121) {
            return "Pause";
        }
        if (i == 124) {
            return "Ins";
        }
        if (i == 92) {
            return "PgUp";
        }
        if (i == 93) {
            return "PgDn";
        }
        return "Key" + i;
    }

    public static int indexOfKey(int i) {
        int i2 = 0;
        while (true) {
            int[] iArr = AVAILABLE_KEYS;
            if (i2 >= iArr.length) {
                return 0;
            }
            if (iArr[i2] == i) {
                return i2;
            }
            i2++;
        }
    }

    public static int getKeyCount() {
        return AVAILABLE_KEYS.length;
    }

    public static boolean isServiceRunning() {
        return sServiceRunning;
    }

    static EmulatorShortcutService getOverlayHost() {
        return sInstance;
    }

    public static void notifyConfigChanged() {
        EmulatorShortcutService emulatorShortcutService = sInstance;
        if (emulatorShortcutService != null) {
            emulatorShortcutService.ensurePrefsAndLoad();
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        ensurePrefsAndLoad();
        sInstance = this;
        sServiceRunning = true;
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onServiceConnected() {
        super.onServiceConnected();
        TrustedVisualOverlayWindowManager.invalidateAll();
        sInstance = this;
        sServiceRunning = true;
        AccessibilityServiceInfo serviceInfo = getServiceInfo();
        if (serviceInfo != null) {
            serviceInfo.flags |= 32;
            setServiceInfo(serviceInfo);
        }
        ensurePrefsAndLoad();
        Dbg.d(TAG, "模拟器快捷键服务已连接");
    }

    @Override // android.app.Service
    public void onDestroy() {
        sServiceRunning = false;
        sInstance = null;
        TrustedVisualOverlayWindowManager.invalidateAll();
        super.onDestroy();
        Dbg.d(TAG, "模拟器快捷键服务已销毁");
    }

    private synchronized void ensurePrefsAndLoad() {
        if (this.prefs == null) {
            this.prefs = getSharedPreferences(PREF_NAME, 0);
        }
        loadConfig();
    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = this.prefs;
        if (sharedPreferences == null) {
            return;
        }
        this.enabled = sharedPreferences.getBoolean(KEY_ENABLED, false);
        this.keyToggleWindow = this.prefs.getInt(KEY_TOGGLE_WINDOW, DEFAULT_KEY_WINDOW);
        this.keyToggleAutoPick = this.prefs.getInt(KEY_TOGGLE_AUTOPICK, DEFAULT_KEY_AUTOPICK);
        this.keyToggleCardPool = this.prefs.getInt(KEY_TOGGLE_CARDPOOL, DEFAULT_KEY_CARDPOOL);
    }

    @Override // android.accessibilityservice.AccessibilityService
    protected boolean onKeyEvent(KeyEvent keyEvent) {
        String str;
        if (this.prefs == null) {
            ensurePrefsAndLoad();
        }
        if (this.enabled && !keyEvent.isCtrlPressed() && !keyEvent.isAltPressed() && !keyEvent.isShiftPressed()) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == this.keyToggleWindow) {
                str = ACTION_TOGGLE_WINDOW;
            } else if (keyCode == this.keyToggleAutoPick) {
                str = ACTION_TOGGLE_AUTOPICK;
            } else if (keyCode == this.keyToggleCardPool) {
                str = ACTION_TOGGLE_CARDPOOL;
            }
            if (keyEvent.getAction() == 1) {
                return true;
            }
            if (keyEvent.getAction() != 0) {
                return false;
            }
            if (keyEvent.getRepeatCount() == 0) {
                dispatchShortcut(str);
            }
            return true;
        }
        return false;
    }

    private void dispatchShortcut(String str) {
        if (FloatingWindowService.dispatchShortcut(str)) {
            return;
        }
        sendBroadcast(new Intent(str).setPackage(getPackageName()).addFlags(268435456), INTERNAL_SHORTCUT_PERMISSION);
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
        Dbg.d(TAG, "模拟器快捷键服务被中断");
    }
}
