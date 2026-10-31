package com.android.support;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcBoardPositionsParser {
    private IpcBoardPositionsParser() {
    }

    static List<BoardPositionInfo> parse(JSONObject jSONObject) throws Exception {
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("positions");
        if (jSONArrayOptJSONArray == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
            JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i);
            BoardPositionInfo boardPositionInfo = new BoardPositionInfo();
            boardPositionInfo.playerId = jSONObject2.optInt("playerId", 0);
            boardPositionInfo.isMe = jSONObject2.optBoolean("isMe", false);
            boardPositionInfo.boardBitmask = jSONObject2.optInt("boardMask", 0);
            boardPositionInfo.benchBitmask = jSONObject2.optInt("benchMask", 0);
            boardPositionInfo.boardCount = jSONObject2.optInt("boardCount", 0);
            boardPositionInfo.benchCount = jSONObject2.optInt("benchCount", 0);
            JSONArray jSONArrayOptJSONArray2 = jSONObject2.optJSONArray("boardHeroes");
            if (jSONArrayOptJSONArray2 != null) {
                for (int i2 = 0; i2 < Math.min(jSONArrayOptJSONArray2.length(), 28); i2++) {
                    boardPositionInfo.boardHeroIds[i2] = jSONArrayOptJSONArray2.optInt(i2, 0);
                }
            }
            JSONArray jSONArrayOptJSONArray3 = jSONObject2.optJSONArray("benchHeroes");
            if (jSONArrayOptJSONArray3 != null) {
                for (int i3 = 0; i3 < Math.min(jSONArrayOptJSONArray3.length(), 9); i3++) {
                    boardPositionInfo.benchHeroIds[i3] = jSONArrayOptJSONArray3.optInt(i3, 0);
                }
            }
            arrayList.add(boardPositionInfo);
        }
        return arrayList;
    }

    static int twoStarCount(BoardPositionInfo boardPositionInfo) {
        int i = 0;
        for (int i2 : boardPositionInfo.boardHeroIds) {
            if (i2 > 0) {
                while (i2 >= 10) {
                    i2 /= 10;
                }
                if (i2 == 2) {
                    i++;
                }
            }
        }
        for (int i3 : boardPositionInfo.benchHeroIds) {
            if (i3 > 0) {
                while (i3 >= 10) {
                    i3 /= 10;
                }
                if (i3 == 2) {
                    i++;
                }
            }
        }
        return i;
    }

    static String diagnosticLine(List<BoardPositionInfo> list, int i) {
        StringBuilder sb = new StringBuilder();
        for (BoardPositionInfo boardPositionInfo : list) {
            sb.append("p").append(boardPositionInfo.playerId).append("=").append(boardPositionInfo.boardCount).append("+").append(boardPositionInfo.benchCount).append("(2★").append(twoStarCount(boardPositionInfo)).append(") ");
        }
        return "[diag][board] #" + i + " positions=" + list.size() + " " + sb.toString();
    }
}
