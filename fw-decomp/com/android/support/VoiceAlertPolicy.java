package com.android.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class VoiceAlertPolicy {

    public interface HeroMetadata {
        int costOf(int i);

        String displayNameOf(int i);
    }

    public static final class PlayerBoard {
        final int[] benchHeroIds;
        final int[] boardHeroIds;
        final int playerId;
        final String playerName;

        public PlayerBoard(int i, String str, int[] iArr, int[] iArr2) {
            this.playerId = i;
            this.playerName = str;
            this.boardHeroIds = copyOrEmpty(iArr);
            this.benchHeroIds = copyOrEmpty(iArr2);
        }

        private static int[] copyOrEmpty(int[] iArr) {
            return iArr == null ? new int[0] : (int[]) iArr.clone();
        }
    }

    public List<VoiceAlertRequest> findCandidates(List<PlayerBoard> list, int i, int i2, HeroMetadata heroMetadata) {
        return findCandidates(list, i, i2, 31, heroMetadata);
    }

    public List<VoiceAlertRequest> findCandidates(List<PlayerBoard> list, int i, int i2, int i3, HeroMetadata heroMetadata) {
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            int iMax = Math.max(1, Math.min(99, i2));
            for (PlayerBoard playerBoard : list) {
                if (playerBoard != null) {
                    if (playerBoard.playerId != i) {
                        LinkedHashMap linkedHashMap = new LinkedHashMap();
                        addCopies(linkedHashMap, playerBoard.boardHeroIds);
                        addCopies(linkedHashMap, playerBoard.benchHeroIds);
                        String strFallback = fallback(playerBoard.playerName, "玩家" + playerBoard.playerId);
                        for (Map.Entry entry : linkedHashMap.entrySet()) {
                            int iIntValue = ((Integer) entry.getKey()).intValue();
                            int iIntValue2 = ((Integer) entry.getValue()).intValue();
                            int iCostOf = heroMetadata.costOf(iIntValue);
                            if (VoiceAlertCostFilter.includes(i3, iCostOf) && iIntValue2 >= iMax) {
                                arrayList.add(new VoiceAlertRequest(playerBoard.playerId, iIntValue, strFallback, fallback(heroMetadata.displayNameOf(iIntValue), "英雄" + iIntValue), iCostOf, iIntValue2));
                            }
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    private static void addCopies(Map<Integer, Integer> map, int[] iArr) {
        int iIntValue;
        for (int i : iArr) {
            if (i > 0) {
                if (i >= 30000 && i <= 39999) {
                    i -= 20000;
                    iIntValue = 9;
                } else if (i < 20000 || i > 29999) {
                    iIntValue = 1;
                } else {
                    i -= 10000;
                    iIntValue = 3;
                }
                int iS17CanonicalBaseId = HeroIdMapper.s17CanonicalBaseId(i);
                Integer num = map.get(Integer.valueOf(iS17CanonicalBaseId));
                Integer numValueOf = Integer.valueOf(iS17CanonicalBaseId);
                if (num != null) {
                    iIntValue += num.intValue();
                }
                map.put(numValueOf, Integer.valueOf(iIntValue));
            }
        }
    }

    private static String fallback(String str, String str2) {
        return (str == null || str.trim().isEmpty()) ? str2 : str;
    }
}
