package com.android.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class IpcV2ActionMethods {
    public static final String GAME_MODIFY_GOLD = "game.modifyGold";
    public static final String GAME_MODIFY_HP = "game.modifyHp";
    public static final String GAME_MODIFY_LEVEL = "game.modifyLevel";
    public static final String GAME_MODIFY_POPULATION = "game.modifyPopulation";
    public static final String GAME_EXIT = "game.exit";
    public static final String DROP_GENERATE_HEROES = "drop.generateHeroes";
    public static final String DROP_GENERATE_EQUIPMENT = "drop.generateEquipment";
    public static final String CHESS_SELL = "chess.sell";
    public static final String CHESS_MIRROR = "chess.mirror";
    public static final String CHESS_CUSTOM_SWAP = "chess.customSwap";
    public static final String CHESS_ALL_IN_START = "chess.allIn.start";
    public static final String CHESS_ALL_IN_STOP = "chess.allIn.stop";
    public static final String CANCEL_CHESS_OP = "cancelChessOp";
    public static final String LINEUP_APPLY = "lineup.apply";
    public static final String LINEUP_CLEAR = "lineup.clear";
    public static final String LINEUP_SYNC_TO_SLOT = "lineup.syncToSlot";
    public static final String HEX_QUERY = "hex.query";
    public static final String HEX_SPECIFY = "hex.specify";
    public static final String HEX_CANCEL = "hex.cancel";
    public static final String DIAGNOSTICS_UI_APPLIED = "diagnostics.uiApplied";
    public static final String RECAST_SPECIFY = "recast.specify";
    public static final String RECAST_CANCEL = "recast.cancel";
    private static final Set<String> ALL = Collections.unmodifiableSet(new LinkedHashSet(Arrays.asList(GAME_MODIFY_GOLD, GAME_MODIFY_HP, GAME_MODIFY_LEVEL, GAME_MODIFY_POPULATION, GAME_EXIT, DROP_GENERATE_HEROES, DROP_GENERATE_EQUIPMENT, CHESS_SELL, CHESS_MIRROR, CHESS_CUSTOM_SWAP, CHESS_ALL_IN_START, CHESS_ALL_IN_STOP, CANCEL_CHESS_OP, LINEUP_APPLY, LINEUP_CLEAR, LINEUP_SYNC_TO_SLOT, HEX_QUERY, HEX_SPECIFY, HEX_CANCEL, DIAGNOSTICS_UI_APPLIED, RECAST_SPECIFY, RECAST_CANCEL)));

    private IpcV2ActionMethods() {
    }

    public static Set<String> all() {
        return ALL;
    }
}
