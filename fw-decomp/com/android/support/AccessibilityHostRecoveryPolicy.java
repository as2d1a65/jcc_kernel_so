package com.android.support;

/* loaded from: classes.dex */
final class AccessibilityHostRecoveryPolicy {
    private static final int PER_USER_RANGE = 100000;

    private AccessibilityHostRecoveryPolicy() {
    }

    static int userIdFromUid(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid Android UID");
        }
        return i / PER_USER_RANGE;
    }

    static String readEnabledServicesCommand(int i) {
        return prefix(i, "get") + "enabled_accessibility_services";
    }

    static String readAccessibilityEnabledCommand(int i) {
        return prefix(i, "get") + "accessibility_enabled";
    }

    static String enableAccessibilityCommand(int i) {
        return prefix(i, "put") + "accessibility_enabled 1";
    }

    static String deleteEnabledServicesCommand(int i) {
        return prefix(i, "delete") + "enabled_accessibility_services";
    }

    static String writeEnabledServicesCommand(int i, String str) {
        validateUserId(i);
        if (!AccessibilityServiceSettings.isSafeComponentList(str)) {
            throw new IllegalArgumentException("Unsafe accessibility component list");
        }
        return "settings put --user " + i + " secure enabled_accessibility_services '" + str + "'";
    }

    private static String prefix(int i, String str) {
        validateUserId(i);
        return "settings " + str + " --user " + i + " secure ";
    }

    private static void validateUserId(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid Android user ID");
        }
    }
}
