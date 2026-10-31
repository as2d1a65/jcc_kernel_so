package com.android.support;

import com.android.support.lineup.LineupDataSource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class GameRegionFeaturePolicy {
    public static boolean operational(GameRegion gameRegion, boolean z, UiSurfacePolicy uiSurfacePolicy, String str) {
        if (!gameRegion.isRuntimeReady() || !gameRegion.compiledSurface(str) || !z || !uiSurfacePolicy.visible(str, true) || !uiSurfacePolicy.enabled(str, true)) {
            return false;
        }
        String strParentSurface = parentSurface(str);
        return strParentSurface == null || (uiSurfacePolicy.visible(strParentSurface, true) && uiSurfacePolicy.enabled(strParentSurface, true));
    }

    public static String actionSurface(String str) {
        if (str == null) {
            return null;
        }
        str.hashCode();
        switch (str) {
        }
        return null;
    }

    public static boolean allowsAction(GameRegion gameRegion, boolean z, UiSurfacePolicy uiSurfacePolicy, String str) {
        if (gameRegion == null || IpcV2ActionMethods.HEX_SPECIFY.equals(str) || IpcV2ActionMethods.HEX_CANCEL.equals(str)) {
            return false;
        }
        if (((IpcV2ActionMethods.RECAST_SPECIFY.equals(str) || IpcV2ActionMethods.RECAST_CANCEL.equals(str)) && !RecastFeatureGate.isEnabledForPackage(gameRegion.packageName)) || !gameRegion.isRuntimeReady()) {
            return false;
        }
        if (!"runtime.policy.result".equals(str) && !"specified.compute.result".equals(str)) {
            String strActionSurface = actionSurface(str);
            if ("cleanup".equals(strActionSurface) || "acceptTrustLease".equals(str) || "beginSpecifiedChallenge".equals(str) || "clearTrustLease".equals(str) || "activateWigSession".equals(str) || "acceptFeatureCatalog".equals(str) || "diagnostics.trace.disable".equals(str)) {
                return true;
            }
            if (!"debug.pointers".equals(str)) {
                if ((IpcV2ActionMethods.HEX_QUERY.equals(str) || IpcV2ActionMethods.HEX_SPECIFY.equals(str)) && !HexFeatureGate.isEnabledForPackage(gameRegion.packageName)) {
                    return false;
                }
                return (!IpcV2ActionMethods.RECAST_SPECIFY.equals(str) || RecastFeatureGate.isEnabledForPackage(gameRegion.packageName)) && strActionSurface != null && operational(gameRegion, z, uiSurfacePolicy, strActionSurface);
            }
        }
        return z;
    }

    private static String configSurface(String str) {
        if (str.startsWith("uiSurface") || str.equals(IpcV2ConfigState.SESSION_ACTIVE)) {
            return null;
        }
        if (str.equals(IpcV2ConfigState.SHOW_DEBUG_WINDOW)) {
            return UiSurfaceRegistry.OVERLAY_DEBUG_WINDOW;
        }
        if (str.startsWith(IpcV2ConfigState.SPECIFIED_SHOP)) {
            return UiSurfaceRegistry.PAGE_FUNC_SPECIFIED_SHOP;
        }
        if (str.startsWith(IpcV2ConfigState.AUTO_PICK) || str.startsWith("autoRefresh") || str.startsWith("chase3Star") || str.equals(IpcV2ConfigState.AUXILIARY_MODE)) {
            return UiSurfaceRegistry.PAGE_FUNC_AUTOPICK;
        }
        if (str.equals(IpcV2ConfigState.AUTO_PLAY) || str.startsWith(LineupDataSource.ASSETS_DIR) || str.equals(IpcV2ConfigState.BATTLE_PANEL_LINEUPS)) {
            return UiSurfaceRegistry.PAGE_FUNC_MEMORY;
        }
        if (str.startsWith("opponentCarding")) {
            return UiSurfaceRegistry.PAGE_FUNC_AUTOPICK;
        }
        if (str.startsWith("opponentPredict") || str.startsWith("nativeAttack")) {
            return UiSurfaceRegistry.PAGE_PREDICT;
        }
        if (str.startsWith("shopNative")) {
            return UiSurfaceRegistry.PAGE_HERO_POOL;
        }
        if (str.startsWith("nativePlayer")) {
            return UiSurfaceRegistry.PAGE_PLAYERS_DATA;
        }
        if (str.startsWith("board") || str.startsWith("opponentBoard") || str.startsWith("battlePanel")) {
            return UiSurfaceRegistry.PAGE_PLAYERS;
        }
        return UiSurfaceRegistry.PAGE_FUNCTION;
    }

    private static String parentSurface(String str) {
        if (UiSurfaceRegistry.PAGE_FUNC_RECAST.equals(str)) {
            return UiSurfaceRegistry.PAGE_SPECIFIED;
        }
        if (str.startsWith("page.func_")) {
            return UiSurfaceRegistry.PAGE_FUNCTION;
        }
        if (UiSurfaceRegistry.PAGE_PLAYERS_DATA.equals(str) || UiSurfaceRegistry.PAGE_PREDICT.equals(str) || UiSurfaceRegistry.PAGE_RANK_WARNING.equals(str)) {
            return UiSurfaceRegistry.PAGE_PLAYERS;
        }
        return null;
    }

    public static Map<String, Object> project(Map<String, Object> map, GameRegion gameRegion, boolean z, UiSurfacePolicy uiSurfacePolicy) {
        LinkedHashMap linkedHashMap = new LinkedHashMap(map);
        linkedHashMap.put(IpcV2ConfigState.SPECIFIED_SHOP, false);
        linkedHashMap.put(IpcV2ConfigState.UI_SURFACE_SHOW_MASK, Integer.valueOf(uiSurfacePolicy.forceShowMask()));
        linkedHashMap.put(IpcV2ConfigState.UI_SURFACE_HIDE_MASK, Integer.valueOf(uiSurfacePolicy.forceHideMask()));
        linkedHashMap.put(IpcV2ConfigState.UI_SURFACE_ENABLE_MASK, Integer.valueOf(uiSurfacePolicy.forceEnableMask()));
        linkedHashMap.put(IpcV2ConfigState.UI_SURFACE_DISABLE_MASK, Integer.valueOf(uiSurfacePolicy.forceDisableMask()));
        if ((!gameRegion.isRuntimeReady() || !z) && linkedHashMap.containsKey(IpcV2ConfigState.SESSION_ACTIVE)) {
            linkedHashMap.put(IpcV2ConfigState.SESSION_ACTIVE, false);
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String strConfigSurface = configSurface(key);
            if (strConfigSurface != null && !operational(gameRegion, z, uiSurfacePolicy, strConfigSurface)) {
                if (key.equals(IpcV2ConfigState.LINEUP_STATE_KNOWN)) {
                    linkedHashMap.put(key, true);
                } else if (key.equals(IpcV2ConfigState.LINEUP_APPLIED_HERO_IDS) || key.equals(IpcV2ConfigState.LINEUP_APPLIED_STAR_TARGETS) || key.equals(IpcV2ConfigState.BATTLE_PANEL_LINEUPS)) {
                    linkedHashMap.put(key, Collections.emptyList());
                } else if (entry.getValue() instanceof Boolean) {
                    linkedHashMap.put(key, false);
                } else if (key.equals("gameSpeed")) {
                    linkedHashMap.put(key, 1);
                } else if (key.equals(IpcV2ConfigState.OPPONENT_PREDICT_STYLE) || key.startsWith("chase3Star")) {
                    linkedHashMap.put(key, 0);
                }
            }
        }
        return linkedHashMap;
    }

    private GameRegionFeaturePolicy() {
    }
}
