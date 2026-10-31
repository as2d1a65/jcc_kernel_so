package com.android.support;

import com.android.support.lineup.GameSeasonBus;
import com.android.support.lineup.HeroAvatarUrl;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class ShareSnapshotBuilder {
    private static final int MAX_BENCH = 9;
    private static final int MAX_BOARD = 28;
    static final int MAX_HEROES = 100;
    private static final int MAX_HEX = 4;
    static final int MAX_PLAYERS = 8;
    private final SharedMemManager manager;

    public ShareSnapshotBuilder(SharedMemManager sharedMemManager) {
        if (sharedMemManager == null) {
            throw new IllegalArgumentException("manager is required");
        }
        this.manager = sharedMemManager;
    }

    public JSONObject build(String str, int i) throws JSONException {
        if (!"waiting".equals(str) && !"live".equals(str) && !"finished".equals(str)) {
            throw new IllegalArgumentException("invalid share state");
        }
        boolean z = "live".equals(str) && this.manager.isMatchActive();
        if (z) {
            str = "live";
        }
        String str2 = (!"live".equals(str) || z) ? str : "waiting";
        byte[] sharedMemoryOnce = this.manager.readSharedMemoryOnce();
        List<PlayerInfo> listCopyPlayers = copyPlayers(this.manager.readPlayersInfo());
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("schema", 1);
        jSONObject.put("state", str2);
        jSONObject.put("captured_at_ms", System.currentTimeMillis());
        jSONObject.put("match", buildMatch(z, i, listCopyPlayers, sharedMemoryOnce));
        jSONObject.put("players", buildPlayers(listCopyPlayers));
        jSONObject.put("pool", buildPool(this.manager.getHeroPoolDataWithLocked()));
        jSONObject.put("boards", buildBoards(this.manager.readBoardPositionsFromData(sharedMemoryOnce)));
        jSONObject.put("hex_levels", buildHex(this.manager.readHexLevelsFromData(sharedMemoryOnce)));
        return jSONObject;
    }

    private JSONObject buildMatch(boolean z, int i, List<PlayerInfo> list, byte[] bArr) throws JSONException {
        long jCurrentTimeMillis = System.currentTimeMillis();
        MatchupSnapshot matchupSnapshot = this.manager.getMatchupSnapshot();
        boolean zIsFresh = matchupSnapshot.isFresh(jCurrentTimeMillis, 6500L);
        int iResolveMyPlayerId = OpponentSelectionPolicy.resolveMyPlayerId(zIsFresh ? matchupSnapshot.myId : -1, list);
        Iterator<PlayerInfo> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            PlayerInfo next = it.next();
            if (next.isMe) {
                if (iResolveMyPlayerId < 0) {
                    iResolveMyPlayerId = bounded(next.playerId, -1, 7);
                }
            }
        }
        if (!zIsFresh || !matchupSnapshot.isExplicitEmpty()) {
            iResolve = OpponentSelectionPolicy.resolve(zIsFresh ? matchupSnapshot.opponentId : -1, zIsFresh ? matchupSnapshot.myId : iResolveMyPlayerId, zIsFresh ? matchupSnapshot.pairs : null, list);
        }
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(OpponentCardingStatus.STATE_ACTIVE, z);
        jSONObject.put("dual_play", this.manager.isDualPlayMode());
        jSONObject.put("shared_seconds", bounded(i, 0, 86400));
        jSONObject.put("my_player_id", iResolveMyPlayerId);
        jSONObject.put("opponent_id", iResolve);
        return jSONObject;
    }

    private JSONArray buildPlayers(List<PlayerInfo> list) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (PlayerInfo playerInfo : list) {
            if (jSONArray.length() >= 8) {
                break;
            }
            int iBounded = bounded(playerInfo.playerId, -1, 7);
            if (iBounded >= 0 && linkedHashSet.add(Integer.valueOf(iBounded))) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("id", iBounded);
                jSONObject.put("name", safeText(playerInfo.name, 64, 256, "玩家" + (iBounded + 1)));
                jSONObject.put("hp", bounded(playerInfo.hp, 0, 1000));
                jSONObject.put("level", bounded(playerInfo.level, 0, 20));
                jSONObject.put("gold", bounded(playerInfo.gold, 0, 10000));
                jSONObject.put("rank", bounded(playerInfo.rank, 0, 8));
                jSONObject.put("is_me", playerInfo.isMe);
                jSONObject.put("is_partner", playerInfo.isPartner);
                jSONObject.put("team_id", bounded(playerInfo.teamId, -1, 3));
                jSONObject.put("enemy_id", bounded(playerInfo.enemyId, -1, 7));
                jSONArray.put(jSONObject);
            }
        }
        return jSONArray;
    }

    private JSONArray buildPool(List<HeroData>[] listArr) throws JSONException {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        int i = 0;
        int i2 = 1;
        if (listArr != null) {
            addHeroes(linkedHashMap, linkedHashSet, listArr.length > 0 ? listArr[0] : null, false);
            addHeroes(linkedHashMap, linkedHashSet, listArr.length > 1 ? listArr[1] : null, true);
        }
        JSONArray jSONArray = new JSONArray();
        String setId = GameSeasonBus.get().getSetId();
        int iInternalSeason = internalSeason(setId);
        Iterator it = linkedHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (jSONArray.length() >= 100) {
                break;
            }
            HeroData heroData = (HeroData) entry.getValue();
            int iBounded = bounded(heroData.cost, i2, 5);
            int iBounded2 = bounded(heroData.totalCount, i, DsTokens.RADIUS_PILL);
            int iBounded3 = bounded(heroData.currentCount, i, iBounded2);
            String str = heroData.chineseName;
            String str2 = heroData.englishName;
            if (iInternalSeason >= 0) {
                String chineseName = HeroIdMapper.getChineseName(heroData.heroId, iInternalSeason);
                String englishNameById = HeroIdMapper.getEnglishNameById(heroData.heroId, iInternalSeason);
                if (!"未知英雄".equals(chineseName) && englishNameById != null && !englishNameById.isEmpty()) {
                    str = chineseName;
                    str2 = englishNameById;
                }
            }
            String strSafeText = safeText(str2, 64, 64, "");
            String strReplaceAll = strSafeText.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "");
            String str3 = strSafeText.isEmpty() ? "英雄" + heroData.heroId : strSafeText;
            String strBuildHeadUrl = HeroAvatarUrl.buildHeadUrl(setId, strSafeText);
            JSONObject jSONObject = new JSONObject();
            Iterator it2 = it;
            String str4 = setId;
            int i3 = iInternalSeason;
            jSONObject.put("hero_id", bounded(heroData.heroId, 1, 9999999));
            jSONObject.put("cost", iBounded);
            jSONObject.put("remaining", iBounded3);
            jSONObject.put("total", iBounded2);
            jSONObject.put("name", safeText(str, 64, 256, str3));
            jSONObject.put("asset", strReplaceAll);
            jSONObject.put("image_url", strBuildHeadUrl != null ? strBuildHeadUrl : "");
            jSONObject.put("locked", linkedHashSet.contains(entry.getKey()) || heroData.isLocked);
            jSONArray.put(jSONObject);
            i2 = 1;
            it = it2;
            setId = str4;
            iInternalSeason = i3;
            i = 0;
        }
        return jSONArray;
    }

    private static int internalSeason(String str) {
        if ("4".equals(str) || "FUX".equals(str) || "fuxing".equals(str)) {
            return 0;
        }
        if ("10".equals(str)) {
            return 1;
        }
        if ("16".equals(str)) {
            return 2;
        }
        if ("17".equals(str)) {
            return 3;
        }
        return "8".equals(str) ? 4 : -1;
    }

    private static void addHeroes(Map<Integer, HeroData> map, Set<Integer> set, List<HeroData> list, boolean z) {
        if (list == null) {
            return;
        }
        Iterator it = new ArrayList(list).iterator();
        while (it.hasNext()) {
            HeroData heroData = (HeroData) it.next();
            if (heroData != null && heroData.heroId > 0 && heroData.cost >= 1 && heroData.cost <= 5) {
                map.put(Integer.valueOf(heroData.heroId), heroData);
                if (z || heroData.isLocked) {
                    set.add(Integer.valueOf(heroData.heroId));
                }
            }
        }
    }

    private JSONArray buildBoards(List<BoardPositionInfo> list) throws JSONException {
        BoardPositionInfo boardPositionInfo;
        JSONArray jSONArray = new JSONArray();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (list != null) {
            Iterator it = new ArrayList(list).iterator();
            while (it.hasNext() && (boardPositionInfo = (BoardPositionInfo) it.next()) != null && jSONArray.length() < 8) {
                int iBounded = bounded(boardPositionInfo.playerId, -1, 7);
                if (iBounded >= 0 && linkedHashSet.add(Integer.valueOf(iBounded))) {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("player_id", iBounded);
                    jSONObject.put("board", boundedIds(boardPositionInfo.boardHeroIds, 28));
                    jSONObject.put("bench", boundedIds(boardPositionInfo.benchHeroIds, 9));
                    jSONArray.put(jSONObject);
                }
            }
        }
        return jSONArray;
    }

    private static JSONArray boundedIds(int[] iArr, int i) {
        JSONArray jSONArray = new JSONArray();
        if (iArr != null) {
            for (int i2 = 0; i2 < Math.min(iArr.length, i); i2++) {
                jSONArray.put(bounded(iArr[i2], 0, 9999999));
            }
        }
        return jSONArray;
    }

    private static JSONArray buildHex(int[] iArr) {
        JSONArray jSONArray = new JSONArray();
        if (iArr != null) {
            for (int i = 0; i < Math.min(iArr.length, 4); i++) {
                jSONArray.put(bounded(iArr[i], -1, 3));
            }
        }
        return jSONArray;
    }

    private static List<PlayerInfo> copyPlayers(List<PlayerInfo> list) {
        return list == null ? new ArrayList() : new ArrayList(list);
    }

    private static int bounded(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i3, i));
    }

    private static String safeText(String str, int i, int i2, String str2) {
        String strTrim = str == null ? "" : str.trim();
        if (!strTrim.isEmpty()) {
            str2 = strTrim;
        }
        if (str2.length() > i) {
            str2 = str2.substring(0, i);
        }
        while (!str2.isEmpty() && str2.getBytes(StandardCharsets.UTF_8).length > i2) {
            str2 = str2.substring(0, str2.offsetByCodePoints(str2.length(), -1));
        }
        return str2;
    }
}
