package com.android.support;

/* loaded from: classes.dex */
public final class SpecifiedFeaturePolicy {
    public static final int HEX = 8;
    public static final int RECAST = 16;
    public static final int SHOP = 4;

    static boolean canShowPicker(UiSurfacePolicy uiSurfacePolicy, int i, boolean z, boolean z2, boolean z3, boolean z4) {
        return false;
    }

    public static UiSurfacePolicy restrict(UiSurfacePolicy uiSurfacePolicy, int i) {
        int iIndexOf = (1 << UiSurfaceRegistry.indexOf(UiSurfaceRegistry.PAGE_FUNC_SPECIFIED_SHOP)) | (1 << UiSurfaceRegistry.indexOf(UiSurfaceRegistry.PAGE_FUNC_RECAST)) | (1 << UiSurfaceRegistry.indexOf(UiSurfaceRegistry.PAGE_SPECIFIED));
        int i2 = uiSurfacePolicy.revision;
        int iForceShowMask = uiSurfacePolicy.forceShowMask();
        int i3 = ~iIndexOf;
        return UiSurfacePolicy.fromMasks(i2, iForceShowMask & i3, uiSurfacePolicy.forceHideMask() | iIndexOf, i3 & uiSurfacePolicy.forceEnableMask(), uiSurfacePolicy.forceDisableMask() | iIndexOf);
    }

    private SpecifiedFeaturePolicy() {
    }
}
