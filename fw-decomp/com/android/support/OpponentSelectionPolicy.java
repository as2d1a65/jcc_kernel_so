package com.android.support;

import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
final class OpponentSelectionPolicy {
    private static final int MAX_PLAYER_ID = 7;
    private static final int MIN_PLAYER_ID = 0;

    private static boolean isValidPlayerId(int i) {
        return i >= 0 && i <= 7;
    }

    private OpponentSelectionPolicy() {
    }

    static int resolve(int i, int i2, List<PlayerInfo> list) {
        return resolve(i, i2, null, list);
    }

    static int resolve(int i, int i2, List<MatchupPairData> list, List<PlayerInfo> list2) {
        int iResolveMyPlayerId = resolveMyPlayerId(i2, list2);
        if (isSelectableOpponent(i, iResolveMyPlayerId, list2)) {
            return i;
        }
        int iResolveFromPairs = resolveFromPairs(iResolveMyPlayerId, list);
        if (isSelectableOpponent(iResolveFromPairs, iResolveMyPlayerId, list2)) {
            return iResolveFromPairs;
        }
        PlayerInfo playerInfoFindLocalPlayer = findLocalPlayer(iResolveMyPlayerId, list2);
        if (playerInfoFindLocalPlayer == null || !isSelectableOpponent(playerInfoFindLocalPlayer.enemyId, iResolveMyPlayerId, list2)) {
            return -1;
        }
        return playerInfoFindLocalPlayer.enemyId;
    }

    static int resolveMyPlayerId(int i, List<PlayerInfo> list) {
        if (isValidPlayerId(i)) {
            return i;
        }
        if (list == null || list.isEmpty()) {
            return -1;
        }
        for (PlayerInfo playerInfo : list) {
            if (playerInfo != null && playerInfo.isMe && isValidPlayerId(playerInfo.playerId)) {
                return playerInfo.playerId;
            }
        }
        return -1;
    }

    static int retainDuringActiveMatch(int i, int i2, boolean z) {
        return retainDuringActiveMatch(i, i2, z, null);
    }

    static int retainDuringActiveMatch(int i, int i2, boolean z, List<PlayerInfo> list) {
        if (isValidPlayerId(i)) {
            if (isStillInMatch(i, list)) {
                return i;
            }
            return -1;
        }
        if (z && isValidPlayerId(i2) && isStillInMatch(i2, list)) {
            return i2;
        }
        return -1;
    }

    private static boolean isStillInMatch(int i, List<PlayerInfo> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        Iterator<PlayerInfo> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            PlayerInfo next = it.next();
            if (next != null && next.playerId == i) {
                if (next.hp != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSelectableOpponent(int i, int i2, List<PlayerInfo> list) {
        if (!isValidPlayerId(i) || (isValidPlayerId(i2) && i == i2)) {
            return false;
        }
        if (list != null && !list.isEmpty()) {
            for (PlayerInfo playerInfo : list) {
                if (playerInfo != null && playerInfo.playerId == i) {
                    return playerInfo.hp != 0;
                }
            }
        }
        return true;
    }

    private static PlayerInfo findLocalPlayer(int i, List<PlayerInfo> list) {
        PlayerInfo playerInfo = null;
        if (list != null && !list.isEmpty()) {
            for (PlayerInfo playerInfo2 : list) {
                if (playerInfo2 != null) {
                    if (isValidPlayerId(i) && playerInfo2.playerId == i) {
                        return playerInfo2;
                    }
                    if (playerInfo == null && playerInfo2.isMe) {
                        playerInfo = playerInfo2;
                    }
                }
            }
        }
        return playerInfo;
    }

    private static int resolveFromPairs(int i, List<MatchupPairData> list) {
        if (isValidPlayerId(i) && list != null && !list.isEmpty()) {
            for (MatchupPairData matchupPairData : list) {
                if (matchupPairData != null) {
                    if (matchupPairData.ghostSourcePlayer != -1) {
                        if (isValidPlayerId(matchupPairData.ghostSourcePlayer) && matchupPairData.player2Id == i && matchupPairData.ghostSourcePlayer != i) {
                            return matchupPairData.ghostSourcePlayer;
                        }
                    } else if (isValidPlayerId(matchupPairData.player1Id) && isValidPlayerId(matchupPairData.player2Id)) {
                        if (matchupPairData.player1Id == i && matchupPairData.player2Id != i) {
                            return matchupPairData.player2Id;
                        }
                        if (matchupPairData.player2Id == i && matchupPairData.player1Id != i) {
                            return matchupPairData.player1Id;
                        }
                    }
                }
            }
        }
        return -1;
    }
}
