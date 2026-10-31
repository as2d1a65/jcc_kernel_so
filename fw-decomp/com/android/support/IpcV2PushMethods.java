package com.android.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class IpcV2PushMethods {
    public static final String RUNTIME_LAUNCH_STATUS = "runtime.launchStatus";
    public static final String RUNTIME_HOOK_STATUS = "runtime.hookStatus";
    public static final String MATCH_STARTED = "match.started";
    public static final String MATCH_ENDED = "match.ended";
    public static final String CONNECTED = "connected";
    public static final String UPDATE_PLAYERS = "updatePlayers";
    public static final String UPDATE_HERO_POOL = "updateHeroPool";
    public static final String UPDATE_BOARD_POSITIONS = "updateBoardPositions";
    public static final String UPDATE_MY_OWNED_HEROES = "updateMyOwnedHeroes";
    public static final String UPDATE_OPPONENT_CARDING_STATUS = "updateOpponentCardingStatus";
    public static final String TOAST = "toast";
    public static final String UPDATE_ALL_IN_STATUS = "updateAllInStatus";
    public static final String UPDATE_BOARD_HERO_ANCHORS = "updateBoardHeroAnchors";
    public static final String UPDATE_HEX_PREDICTION = "updateHexPrediction";
    public static final String HEX_SPECIFIED_STATUS = "hex.specified.status";
    public static final String UPDATE_MATCHUP = "updateMatchup";
    public static final String UPDATE_OPPONENT_DIAGNOSTICS = "updateOpponentDiagnostics";
    public static final String UPDATE_SHOP_AUTOMATION_DIAGNOSTICS = "updateShopAutomationDiagnostics";
    public static final String UPDATE_SPECIFIED_SHOP_STATUS = "updateSpecifiedShopStatus";
    public static final String UPDATE_GAME_RECOMMEND_UI_DIAGNOSTICS = "updateGameRecommendUiDiagnostics";
    public static final String UPDATE_GAME_RECOMMEND_HOOK_DIAGNOSTICS = "updateGameRecommendHookDiagnostics";
    public static final String UPDATE_RANK_ANCHORS = "updateRankAnchors";
    public static final String UPDATE_SHOP_SLOTS = "updateShopSlots";
    public static final String UPDATE_GAME_RECOMMEND_TEAM = "updateGameRecommendTeam";
    public static final String UPDATE_GAME_RECOMMEND_ACTIVE = "updateGameRecommendActive";
    public static final String RECAST_SPECIFIED_STATUS = "recast.specified.status";
    public static final String RECAST_POOL_CATALOG = "recast.pool.catalog";
    public static final String UPDATE_SPECIFIED_SHOP_PREVIEW = "updateSpecifiedShopPreview";
    public static final String UPDATE_SPECIFIED_RECAST_PREVIEW = "updateSpecifiedRecastPreview";
    public static final String UPDATE_SPECIFIED_HEX_PREVIEW = "updateSpecifiedHexPreview";
    private static final Set<String> ALL = Collections.unmodifiableSet(new LinkedHashSet(Arrays.asList(RUNTIME_LAUNCH_STATUS, RUNTIME_HOOK_STATUS, MATCH_STARTED, MATCH_ENDED, CONNECTED, UPDATE_PLAYERS, UPDATE_HERO_POOL, UPDATE_BOARD_POSITIONS, UPDATE_MY_OWNED_HEROES, UPDATE_OPPONENT_CARDING_STATUS, TOAST, UPDATE_ALL_IN_STATUS, UPDATE_BOARD_HERO_ANCHORS, UPDATE_HEX_PREDICTION, HEX_SPECIFIED_STATUS, UPDATE_MATCHUP, UPDATE_OPPONENT_DIAGNOSTICS, UPDATE_SHOP_AUTOMATION_DIAGNOSTICS, UPDATE_SPECIFIED_SHOP_STATUS, UPDATE_GAME_RECOMMEND_UI_DIAGNOSTICS, UPDATE_GAME_RECOMMEND_HOOK_DIAGNOSTICS, UPDATE_RANK_ANCHORS, UPDATE_SHOP_SLOTS, UPDATE_GAME_RECOMMEND_TEAM, UPDATE_GAME_RECOMMEND_ACTIVE, RECAST_SPECIFIED_STATUS, RECAST_POOL_CATALOG, UPDATE_SPECIFIED_SHOP_PREVIEW, UPDATE_SPECIFIED_RECAST_PREVIEW, UPDATE_SPECIFIED_HEX_PREVIEW)));

    private IpcV2PushMethods() {
    }

    public static Set<String> all() {
        return ALL;
    }
}
