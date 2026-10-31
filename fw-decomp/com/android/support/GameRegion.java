package com.android.support;

/* loaded from: classes.dex */
public enum GameRegion {
    CN("cn", "国服", "金铲铲之战", "com.tencent.jkchess", "com.tencent.jkchess.ApolloZGame", "jkchess", true),
    SEA("sea", "国际服", "金铲铲国际服", "com.riot.FFGSSea", "com.riot.FFGSSea.ApolloZGame", "tft_sea", true);

    public final String adapterId;
    public final String gameName;
    public final String id;
    public final String label;
    public final String launchActivity;
    public final String packageName;
    public final boolean runtimeReady;

    GameRegion(String str, String str2, String str3, String str4, String str5, String str6, boolean z) {
        this.id = str;
        this.label = str2;
        this.gameName = str3;
        this.packageName = str4;
        this.launchActivity = str5;
        this.adapterId = str6;
        this.runtimeReady = z;
    }

    public static GameRegion fromId(String str) {
        for (GameRegion gameRegion : values()) {
            if (gameRegion.id.equals(str)) {
                return gameRegion;
            }
        }
        throw new IllegalArgumentException("unknown game region");
    }

    public boolean supportsSurface(String str) {
        return (isRuntimeReady() && compiledSurface(str)) || UiSurfaceRegistry.PAGE_SETTINGS.equals(str) || UiSurfaceRegistry.PAGE_ABOUT.equals(str) || UiSurfaceRegistry.PAGE_INTRO.equals(str);
    }

    public boolean isRuntimeReady() {
        return this.runtimeReady;
    }

    public String namespace(String str) {
        return this == CN ? str : str + "_" + this.id;
    }

    public boolean compiledSurface(String str) {
        if (UiSurfaceRegistry.PAGE_SPECIFIED.equals(str)) {
            return HexFeatureGate.isEnabledForPackage(this.packageName) || RecastFeatureGate.isEnabledForPackage(this.packageName);
        }
        if (UiSurfaceRegistry.PAGE_FUNC_HEX.equals(str)) {
            return HexFeatureGate.isEnabledForPackage(this.packageName);
        }
        if (UiSurfaceRegistry.PAGE_FUNC_RECAST.equals(str)) {
            return RecastFeatureGate.isEnabledForPackage(this.packageName);
        }
        return true;
    }

    public static GameRegion fromPackage(String str) {
        for (GameRegion gameRegion : values()) {
            if (gameRegion.packageName.equals(str)) {
                return gameRegion;
            }
        }
        return null;
    }

    public String assetPath(String str) {
        return this == CN ? str : "regions/" + this.id + "/" + str;
    }
}
