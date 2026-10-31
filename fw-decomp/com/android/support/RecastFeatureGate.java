package com.android.support;

/* loaded from: classes.dex */
final class RecastFeatureGate {
    static final String DISABLED_MESSAGE = "指定重铸功能暂时已禁用";

    private RecastFeatureGate() {
    }

    static boolean isEnabled() {
        return isEnabledForPackage(GameRegionState.current().packageName);
    }

    static boolean isEnabledForPackage(String str) {
        if (GameRegion.fromPackage(str) == GameRegion.CN) {
            return false;
        }
        GameRegion gameRegion = GameRegion.SEA;
        return false;
    }
}
