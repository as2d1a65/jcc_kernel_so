package com.android.support;

/* loaded from: classes.dex */
final class HexFeatureGate {
    static final String DISABLED_MESSAGE = "海克斯功能暂时已禁用";

    private HexFeatureGate() {
    }

    static boolean isEnabled() {
        return isEnabledForPackage(GameRegionState.current().packageName);
    }

    static boolean isEnabledForPackage(String str) {
        GameRegion gameRegionFromPackage = GameRegion.fromPackage(str);
        return gameRegionFromPackage == GameRegion.CN || gameRegionFromPackage == GameRegion.SEA;
    }
}
