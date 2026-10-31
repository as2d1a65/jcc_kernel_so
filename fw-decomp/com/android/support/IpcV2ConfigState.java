package com.android.support;

import com.android.support.diagnostics.TraceCatalog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class IpcV2ConfigState {
    public static final String AUTO_PICK = "autoPick";
    public static final String AUTO_PICK_5_COST_CHOSEN = "autoPick5CostChosen";
    public static final String AUTO_PICK_ALL_CHOSEN = "autoPickAllChosen";
    public static final String AUTO_PICK_COST_MASK = "autoPickCostMask";
    public static final String AUTO_PLAY = "autoPlay";
    public static final String AUTO_REFRESH_FALLBACK_MS = "autoRefreshFallbackMs";
    public static final String AUTO_REFRESH_RESERVE_GOLD = "autoRefreshReserveGold";
    public static final String AUTO_REFRESH_SHOP = "autoRefreshShop";
    public static final String AUXILIARY_MODE = "auxiliaryMode";
    public static final String BLOCK_DIALOGS = "blockDialogs";
    public static final String BOARD_ENTITY_PREVIEW = "boardEntityPreview";
    public static final String BOARD_HERO_HEAD_OVERLAY = "boardHeroHeadOverlay";
    public static final String CHASE_3_STAR_CHOSEN_THRESHOLD = "chase3StarChosenThreshold";
    public static final String CHASE_3_STAR_THRESHOLD = "chase3StarThreshold";
    public static final String CHESS_OP_INTERVAL_FRAMES = "chessOpIntervalFrames";
    public static final String IOS_FAKE = "iosFake";
    public static final String LINEUP_STATE_KNOWN = "lineupStateKnown";
    public static final String NATIVE_ATTACK_ICON_ENABLED = "nativeAttackIconEnabled";
    public static final String NATIVE_ATTACK_ICON_OFFSET_X = "nativeAttackIconOffsetX";
    public static final String NATIVE_ATTACK_ICON_OFFSET_Y = "nativeAttackIconOffsetY";
    public static final String NATIVE_ATTACK_ICON_SIZE_PERCENT = "nativeAttackIconSizePercent";
    public static final String NATIVE_PLAYER_ECON_NAME = "nativePlayerEconName";
    public static final String OPPONENT_BOARD_PERSPECTIVE = "opponentBoardPerspective";
    public static final String OPPONENT_CARDING_COST_MASK = "opponentCardingCostMask";
    public static final String OPPONENT_CARDING_ENABLED = "opponentCardingEnabled";
    public static final String OPPONENT_CARDING_MAX_OWNED_COPIES = "opponentCardingMaxOwnedCopies";
    public static final String OPPONENT_CARDING_RESERVE_GOLD = "opponentCardingReserveGold";
    public static final String OPPONENT_CARDING_STRATEGY = "opponentCardingStrategy";
    public static final String OPPONENT_CARDING_THRESHOLD = "opponentCardingThreshold";
    public static final String OPPONENT_PREDICT_STYLE = "opponentPredictStyle";
    public static final int PREDICT_STYLE_COLOR = 2;
    public static final int PREDICT_STYLE_IMAGE = 1;
    public static final int PREDICT_STYLE_OFF = 0;
    public static final String SESSION_ACTIVE = "sessionActive";
    public static final String SHOP_NATIVE_LABEL = "shopNativeLabel";
    public static final String SHOW_DEBUG_WINDOW = "showDebugWindow";
    public static final String SPECIFIED_MOVE_COMBAT = "specifiedMoveCombat";
    public static final String SPECIFIED_MOVE_PLANNING = "specifiedMovePlanning";
    public static final String SPECIFIED_SHOP = "specifiedShop";
    public static final String SPECIFIED_SHOP_AFFORD_ONLY = "specifiedShopAffordOnly";
    public static final String SPECIFIED_SHOP_COST_MASK = "specifiedShopCostMask";
    public static final String SPECIFIED_SHOP_REFRESH_ONLY = "specifiedShopRefreshOnly";
    public static final String SPECIFIED_SHOP_RESERVE_GOLD = "specifiedShopReserveGold";
    public static final String SPECIFIED_SHOP_STRICT = "specifiedShopStrict";
    public static final String SPECIFIED_SHOP_USE_LINEUP = "specifiedShopUseLineup";
    public static final String UI_SURFACE_DISABLE_MASK = "uiSurfaceDisableMask";
    public static final String UI_SURFACE_ENABLE_MASK = "uiSurfaceEnableMask";
    public static final String UI_SURFACE_HIDE_MASK = "uiSurfaceHideMask";
    public static final String UI_SURFACE_SHOW_MASK = "uiSurfaceShowMask";
    private long revision;
    private final RevisionPersister revisionPersister;
    private final LinkedHashMap<String, Object> values;
    private static final RevisionPersister NO_OP_REVISION_PERSISTER = new RevisionPersister() { // from class: com.android.support.IpcV2ConfigState$$ExternalSyntheticLambda0
        @Override // com.android.support.IpcV2ConfigState.RevisionPersister
        public final void persist(long j) {
            IpcV2ConfigState.lambda$static$0(j);
        }
    };
    public static final String CUSTOM_AUTO_PICK_HERO_IDS = "customAutoPickHeroIds";
    public static final String EXCLUDED_HERO_IDS = "excludedHeroIds";
    public static final String LINEUP_APPLIED_HERO_IDS = "lineupAppliedHeroIds";
    public static final String LINEUP_APPLIED_STAR_TARGETS = "lineupAppliedStarTargets";
    public static final String AUXILIARY_AUTO_PICK_HERO_IDS = "auxiliaryAutoPickHeroIds";
    public static final String CHOSEN_5_COST_FILTER_IDS = "chosen5CostFilterIds";
    public static final String CHOSEN_ALL_COST_FILTER_IDS = "chosenAllCostFilterIds";
    public static final String STAR_TARGETS = "starTargets";
    public static final String AUXILIARY_STAR_TARGETS = "auxiliaryStarTargets";
    public static final String BATTLE_PANEL_LINEUPS = "battlePanelLineups";
    public static final String[] REQUIRED_COLLECTION_KEYS = {CUSTOM_AUTO_PICK_HERO_IDS, EXCLUDED_HERO_IDS, LINEUP_APPLIED_HERO_IDS, LINEUP_APPLIED_STAR_TARGETS, AUXILIARY_AUTO_PICK_HERO_IDS, CHOSEN_5_COST_FILTER_IDS, CHOSEN_ALL_COST_FILTER_IDS, STAR_TARGETS, AUXILIARY_STAR_TARGETS, BATTLE_PANEL_LINEUPS};

    interface RevisionPersister {
        void persist(long j);
    }

    static /* synthetic */ void lambda$static$0(long j) {
    }

    public static final class Snapshot {
        private final long revision;
        private final Map<String, Object> values;

        private Snapshot(long j, Map<String, Object> map) {
            this.revision = j;
            this.values = Collections.unmodifiableMap(map);
        }

        public long revision() {
            return this.revision;
        }

        public Map<String, Object> values() {
            return this.values;
        }
    }

    public IpcV2ConfigState() {
        this(1L, NO_OP_REVISION_PERSISTER);
    }

    IpcV2ConfigState(long j, RevisionPersister revisionPersister) {
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        this.values = linkedHashMap;
        if (j <= 0 || revisionPersister == null) {
            throw new IllegalArgumentException("invalid config revision state");
        }
        this.revision = j;
        this.revisionPersister = revisionPersister;
        linkedHashMap.put(AUTO_PICK, false);
        linkedHashMap.put(AUTO_PLAY, false);
        linkedHashMap.put(SHOW_DEBUG_WINDOW, false);
        linkedHashMap.put(SESSION_ACTIVE, false);
        linkedHashMap.put(IOS_FAKE, false);
        linkedHashMap.put(BLOCK_DIALOGS, false);
        linkedHashMap.put(AUTO_PICK_5_COST_CHOSEN, false);
        linkedHashMap.put(AUTO_PICK_ALL_CHOSEN, false);
        linkedHashMap.put(UI_SURFACE_SHOW_MASK, 0);
        linkedHashMap.put(UI_SURFACE_HIDE_MASK, 0);
        linkedHashMap.put(UI_SURFACE_ENABLE_MASK, 0);
        linkedHashMap.put(UI_SURFACE_DISABLE_MASK, 0);
        linkedHashMap.put(CHASE_3_STAR_THRESHOLD, 0);
        linkedHashMap.put(CHASE_3_STAR_CHOSEN_THRESHOLD, 0);
        linkedHashMap.put(AUTO_PICK_COST_MASK, 31);
        linkedHashMap.put(SPECIFIED_SHOP, false);
        linkedHashMap.put(SPECIFIED_SHOP_RESERVE_GOLD, 0);
        linkedHashMap.put(SPECIFIED_SHOP_COST_MASK, 31);
        linkedHashMap.put(SPECIFIED_SHOP_USE_LINEUP, false);
        linkedHashMap.put(SPECIFIED_SHOP_AFFORD_ONLY, true);
        linkedHashMap.put(SPECIFIED_SHOP_STRICT, false);
        linkedHashMap.put(SPECIFIED_SHOP_REFRESH_ONLY, false);
        linkedHashMap.put(SPECIFIED_MOVE_PLANNING, true);
        linkedHashMap.put(SPECIFIED_MOVE_COMBAT, true);
        linkedHashMap.put(AUTO_REFRESH_SHOP, false);
        linkedHashMap.put(AUTO_REFRESH_FALLBACK_MS, Integer.valueOf(TraceCatalog.Feature.BOARD_HUD));
        linkedHashMap.put(AUTO_REFRESH_RESERVE_GOLD, 0);
        linkedHashMap.put(CHESS_OP_INTERVAL_FRAMES, 2);
        linkedHashMap.put(AUXILIARY_MODE, false);
        linkedHashMap.put(BOARD_HERO_HEAD_OVERLAY, false);
        linkedHashMap.put(OPPONENT_BOARD_PERSPECTIVE, false);
        linkedHashMap.put(BOARD_ENTITY_PREVIEW, false);
        linkedHashMap.put(NATIVE_PLAYER_ECON_NAME, false);
        linkedHashMap.put(SHOP_NATIVE_LABEL, false);
        linkedHashMap.put(OPPONENT_PREDICT_STYLE, 0);
        linkedHashMap.put(NATIVE_ATTACK_ICON_ENABLED, true);
        linkedHashMap.put(NATIVE_ATTACK_ICON_OFFSET_X, 0);
        linkedHashMap.put(NATIVE_ATTACK_ICON_OFFSET_Y, -20);
        linkedHashMap.put(NATIVE_ATTACK_ICON_SIZE_PERCENT, 100);
        OpponentCardingConfig opponentCardingConfigDefaults = OpponentCardingConfig.defaults();
        linkedHashMap.put(OPPONENT_CARDING_ENABLED, Boolean.valueOf(opponentCardingConfigDefaults.isEnabled()));
        linkedHashMap.put(OPPONENT_CARDING_THRESHOLD, Integer.valueOf(opponentCardingConfigDefaults.getThreshold()));
        linkedHashMap.put(OPPONENT_CARDING_COST_MASK, Integer.valueOf(opponentCardingConfigDefaults.getCostMask()));
        linkedHashMap.put(OPPONENT_CARDING_RESERVE_GOLD, Integer.valueOf(opponentCardingConfigDefaults.getReserveGold()));
        linkedHashMap.put(OPPONENT_CARDING_STRATEGY, Integer.valueOf(opponentCardingConfigDefaults.getStrategy()));
        linkedHashMap.put(OPPONENT_CARDING_MAX_OWNED_COPIES, Integer.valueOf(opponentCardingConfigDefaults.getMaxOwnedCopies()));
        linkedHashMap.put(CUSTOM_AUTO_PICK_HERO_IDS, new int[0]);
        linkedHashMap.put(EXCLUDED_HERO_IDS, new int[0]);
        linkedHashMap.put(LINEUP_STATE_KNOWN, false);
        linkedHashMap.put(LINEUP_APPLIED_HERO_IDS, new int[0]);
        linkedHashMap.put(LINEUP_APPLIED_STAR_TARGETS, Collections.emptyMap());
        linkedHashMap.put(AUXILIARY_AUTO_PICK_HERO_IDS, new int[0]);
        linkedHashMap.put(CHOSEN_5_COST_FILTER_IDS, new int[0]);
        linkedHashMap.put(CHOSEN_ALL_COST_FILTER_IDS, new int[0]);
        linkedHashMap.put(STAR_TARGETS, Collections.emptyMap());
        linkedHashMap.put(AUXILIARY_STAR_TARGETS, Collections.emptyMap());
        linkedHashMap.put(BATTLE_PANEL_LINEUPS, Collections.emptyList());
    }

    public synchronized void setBoolean(String str, boolean z) {
        requireScalarKey(str);
        advanceRevision();
        this.values.put(str, Boolean.valueOf(z));
    }

    public synchronized void setInt(String str, int i) {
        requireScalarKey(str);
        if (OPPONENT_CARDING_STRATEGY.equals(str)) {
            i = 1;
        }
        advanceRevision();
        this.values.put(str, Integer.valueOf(i));
    }

    public synchronized void setNativeAttackIconAppearance(int i, int i2, int i3) {
        advanceRevision();
        this.values.put(NATIVE_ATTACK_ICON_OFFSET_X, Integer.valueOf(i));
        this.values.put(NATIVE_ATTACK_ICON_OFFSET_Y, Integer.valueOf(i2));
        this.values.put(NATIVE_ATTACK_ICON_SIZE_PERCENT, Integer.valueOf(i3));
    }

    public synchronized void setUiSurfaceMasks(int i, int i2, int i3, int i4) {
        advanceRevision();
        this.values.put(UI_SURFACE_SHOW_MASK, Integer.valueOf(i));
        this.values.put(UI_SURFACE_HIDE_MASK, Integer.valueOf(i2));
        this.values.put(UI_SURFACE_ENABLE_MASK, Integer.valueOf(i3));
        this.values.put(UI_SURFACE_DISABLE_MASK, Integer.valueOf(i4));
    }

    public synchronized void setIntArray(String str, int[] iArr) {
        requireCollectionKey(str);
        int[] iArr2 = iArr == null ? new int[0] : (int[]) iArr.clone();
        advanceRevision();
        this.values.put(str, iArr2);
    }

    public synchronized void setCustomAutoPickTargets(int[] iArr, Map<Integer, Integer> map) {
        int[] iArr2 = iArr == null ? new int[0] : (int[]) iArr.clone();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (map != null) {
            linkedHashMap.putAll(map);
        }
        advanceRevision();
        this.values.put(CUSTOM_AUTO_PICK_HERO_IDS, iArr2);
        this.values.put(STAR_TARGETS, Collections.unmodifiableMap(linkedHashMap));
    }

    public synchronized void setLineupAppliedHeroIds(int[] iArr) {
        int[] iArr2 = iArr == null ? new int[0] : (int[]) iArr.clone();
        advanceRevision();
        this.values.put(LINEUP_STATE_KNOWN, true);
        this.values.put(LINEUP_APPLIED_HERO_IDS, iArr2);
    }

    public synchronized void setLineupAppliedTargets(int[] iArr, Map<Integer, Integer> map) {
        int[] iArr2 = iArr == null ? new int[0] : (int[]) iArr.clone();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (map != null) {
            linkedHashMap.putAll(map);
        }
        advanceRevision();
        this.values.put(LINEUP_STATE_KNOWN, true);
        this.values.put(LINEUP_APPLIED_HERO_IDS, iArr2);
        this.values.put(LINEUP_APPLIED_STAR_TARGETS, Collections.unmodifiableMap(linkedHashMap));
    }

    public synchronized void setLineupAndAuxiliaryTargets(int[] iArr, Map<Integer, Integer> map, int[] iArr2, Map<Integer, Integer> map2) {
        int[] iArr3 = iArr == null ? new int[0] : (int[]) iArr.clone();
        int[] iArr4 = iArr2 == null ? new int[0] : (int[]) iArr2.clone();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (map != null) {
            linkedHashMap.putAll(map);
        }
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        if (map2 != null) {
            linkedHashMap2.putAll(map2);
        }
        advanceRevision();
        this.values.put(LINEUP_STATE_KNOWN, true);
        this.values.put(LINEUP_APPLIED_HERO_IDS, iArr3);
        this.values.put(LINEUP_APPLIED_STAR_TARGETS, Collections.unmodifiableMap(linkedHashMap));
        this.values.put(AUXILIARY_AUTO_PICK_HERO_IDS, iArr4);
        this.values.put(AUXILIARY_STAR_TARGETS, Collections.unmodifiableMap(linkedHashMap2));
    }

    public synchronized void setAuxiliaryTargets(int[] iArr, Map<Integer, Integer> map) {
        int[] iArr2 = iArr == null ? new int[0] : (int[]) iArr.clone();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (map != null) {
            linkedHashMap.putAll(map);
        }
        advanceRevision();
        this.values.put(AUXILIARY_AUTO_PICK_HERO_IDS, iArr2);
        this.values.put(AUXILIARY_STAR_TARGETS, Collections.unmodifiableMap(linkedHashMap));
    }

    public synchronized void setStarTargets(String str, Map<Integer, Integer> map) {
        if (!STAR_TARGETS.equals(str) && !AUXILIARY_STAR_TARGETS.equals(str) && !LINEUP_APPLIED_STAR_TARGETS.equals(str)) {
            throw new IllegalArgumentException("not a star-target key: " + str);
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (map != null) {
            linkedHashMap.putAll(map);
        }
        advanceRevision();
        this.values.put(str, Collections.unmodifiableMap(linkedHashMap));
    }

    public synchronized void setStructured(String str, Object obj) {
        if (!BATTLE_PANEL_LINEUPS.equals(str)) {
            throw new IllegalArgumentException("not a structured key: " + str);
        }
        Object objEmptyList = obj == null ? Collections.emptyList() : deepCopy(obj);
        advanceRevision();
        this.values.put(str, objEmptyList);
    }

    public synchronized void setOpponentCardingConfig(OpponentCardingConfig opponentCardingConfig) {
        if (opponentCardingConfig == null) {
            try {
                opponentCardingConfig = OpponentCardingConfig.defaults();
            } catch (Throwable th) {
                throw th;
            }
        }
        advanceRevision();
        this.values.put(OPPONENT_CARDING_ENABLED, Boolean.valueOf(opponentCardingConfig.isEnabled()));
        this.values.put(OPPONENT_CARDING_THRESHOLD, Integer.valueOf(opponentCardingConfig.getThreshold()));
        this.values.put(OPPONENT_CARDING_COST_MASK, Integer.valueOf(opponentCardingConfig.getCostMask()));
        this.values.put(OPPONENT_CARDING_RESERVE_GOLD, Integer.valueOf(opponentCardingConfig.getReserveGold()));
        this.values.put(OPPONENT_CARDING_STRATEGY, 1);
        this.values.put(OPPONENT_CARDING_MAX_OWNED_COPIES, Integer.valueOf(opponentCardingConfig.getMaxOwnedCopies()));
    }

    public synchronized Snapshot snapshot() {
        LinkedHashMap linkedHashMap;
        linkedHashMap = new LinkedHashMap();
        for (Map.Entry<String, Object> entry : this.values.entrySet()) {
            linkedHashMap.put(entry.getKey(), deepCopy(entry.getValue()));
        }
        return new Snapshot(this.revision, linkedHashMap);
    }

    private void advanceRevision() {
        long j = this.revision;
        if (j == Long.MAX_VALUE) {
            throw new IllegalStateException("config revision exhausted");
        }
        long j2 = j + 1;
        this.revisionPersister.persist(j2);
        this.revision = j2;
    }

    private void requireScalarKey(String str) {
        if (!this.values.containsKey(str) || isRequiredCollectionKey(str)) {
            throw new IllegalArgumentException("not a scalar config key: " + str);
        }
    }

    private void requireCollectionKey(String str) {
        if (!isRequiredCollectionKey(str)) {
            throw new IllegalArgumentException("not a collection config key: " + str);
        }
    }

    private boolean isRequiredCollectionKey(String str) {
        for (String str2 : REQUIRED_COLLECTION_KEYS) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static Object deepCopy(Object obj) {
        if (obj instanceof int[]) {
            return ((int[]) obj).clone();
        }
        if (obj instanceof long[]) {
            return ((long[]) obj).clone();
        }
        if (obj instanceof Object[]) {
            Object[] objArr = (Object[]) obj;
            Object[] objArr2 = new Object[objArr.length];
            for (int i = 0; i < objArr.length; i++) {
                objArr2[i] = deepCopy(objArr[i]);
            }
            return objArr2;
        }
        if (obj instanceof Map) {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            for (Map.Entry entry : ((Map) obj).entrySet()) {
                linkedHashMap.put(entry.getKey(), deepCopy(entry.getValue()));
            }
            return Collections.unmodifiableMap(linkedHashMap);
        }
        if (!(obj instanceof List)) {
            return obj;
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = ((List) obj).iterator();
        while (it.hasNext()) {
            arrayList.add(deepCopy(it.next()));
        }
        return Collections.unmodifiableList(arrayList);
    }
}
