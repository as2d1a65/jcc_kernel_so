package com.android.support;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcPlayersParser {

    static final class Payload {
        final boolean dualPlay;
        final List<PlayerInfo> players;

        Payload(List<PlayerInfo> list, boolean z) {
            this.players = list;
            this.dualPlay = z;
        }
    }

    private IpcPlayersParser() {
    }

    static Payload parse(JSONObject jSONObject) throws Exception {
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("players");
        if (jSONArrayOptJSONArray == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < jSONArrayOptJSONArray.length()) {
            JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i);
            PlayerInfo playerInfo = new PlayerInfo();
            playerInfo.playerId = jSONObject2.optInt("id", -1);
            playerInfo.hp = jSONObject2.optInt("hp", 0);
            playerInfo.level = jSONObject2.optInt("level", 0);
            playerInfo.gold = jSONObject2.optInt("gold", 0);
            playerInfo.rank = jSONObject2.optInt("rank", 0);
            playerInfo.isMe = jSONObject2.optBoolean("isMe", false);
            i++;
            playerInfo.name = jSONObject2.optString("name", "玩家" + i);
            if (playerInfo.name.isEmpty()) {
                playerInfo.name = "玩家" + i;
            }
            playerInfo.enemyId = jSONObject2.optInt("enemy", -1);
            playerInfo.avatarUrl = SharedMemManager.normalizeAvatarUrl(jSONObject2.optString("avatarUrl", ""));
            playerInfo.avatarId = jSONObject2.optInt("avatarId", -1);
            playerInfo.isBot = jSONObject2.optBoolean("isBot", false);
            playerInfo.botKnown = jSONObject2.optBoolean("botKnown", false);
            arrayList.add(playerInfo);
        }
        return new Payload(arrayList, jSONObject.optBoolean("dualPlay", false));
    }
}
