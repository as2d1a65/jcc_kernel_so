package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public final class OpponentCardingStatus {
    public static final String STATE_ACTIVE = "active";
    public static final String STATE_BLOCKED_GOLD = "blockedGold";
    public static final String STATE_BLOCKED_LIMIT = "blockedLimit";
    public static final String STATE_DATA_UNAVAILABLE = "dataUnavailable";
    public static final String STATE_DISABLED = "disabled";
    public static final String STATE_TOO_LATE = "tooLate";
    public static final String STATE_WAITING = "waiting";
    private static final Set<String> VALID_STATES;
    private final String state;
    private final String summary;
    private final List<Target> targets;

    static {
        HashSet hashSet = new HashSet();
        hashSet.add(STATE_DISABLED);
        hashSet.add("waiting");
        hashSet.add(STATE_ACTIVE);
        hashSet.add(STATE_BLOCKED_GOLD);
        hashSet.add(STATE_BLOCKED_LIMIT);
        hashSet.add(STATE_TOO_LATE);
        hashSet.add(STATE_DATA_UNAVAILABLE);
        VALID_STATES = Collections.unmodifiableSet(hashSet);
    }

    private OpponentCardingStatus(String str, String str2, List<Target> list) {
        this.state = VALID_STATES.contains(str) ? str : "waiting";
        this.summary = boundText(str2, 80);
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            int iMin = Math.min(list.size(), 32);
            for (int i = 0; i < iMin; i++) {
                Target target = list.get(i);
                if (target != null) {
                    arrayList.add(target);
                }
            }
        }
        this.targets = Collections.unmodifiableList(arrayList);
    }

    public static OpponentCardingStatus create(String str, String str2, List<Target> list) {
        return new OpponentCardingStatus(str, str2, list);
    }

    public static OpponentCardingStatus disabled() {
        return create(STATE_DISABLED, "对手卡牌已关闭", Collections.emptyList());
    }

    public String getState() {
        return this.state;
    }

    public String getSummary() {
        return this.summary;
    }

    public List<Target> getTargets() {
        return this.targets;
    }

    public static final class Target {
        private final int copies;
        private final int heroId;
        private final int maxOwnedCopies;
        private final int neededCopies;
        private final int ownedCopies;
        private final int playerId;
        private final String playerName;
        private final boolean purchaseEligible;
        private final String reason;
        private final int threshold;

        private Target(int i, String str, int i2, int i3, int i4, boolean z, String str2, int i5, int i6, int i7) {
            this.playerId = (i < 0 || i >= 8) ? -1 : i;
            this.playerName = OpponentCardingStatus.boundText(str, 32);
            this.heroId = Math.max(0, i2);
            this.copies = OpponentCardingStatus.clamp(i3, 0, 9);
            this.threshold = OpponentCardingStatus.clamp(i4, 3, 9);
            this.purchaseEligible = z;
            this.reason = OpponentCardingStatus.boundText(str2, 40);
            this.ownedCopies = OpponentCardingStatus.clamp(i5, 0, 9);
            int iClamp = OpponentCardingStatus.clamp(i6, 1, 9);
            this.maxOwnedCopies = iClamp;
            this.neededCopies = OpponentCardingStatus.clamp(i7, 0, iClamp);
        }

        public static Target create(int i, String str, int i2, int i3, int i4) {
            return new Target(i, str, i2, i3, i4, true, "可卡牌", 0, 3, 3);
        }

        public static Target create(int i, String str, int i2, int i3, int i4, boolean z, String str2) {
            return new Target(i, str, i2, i3, i4, z, str2, 0, 3, 3);
        }

        public static Target create(int i, String str, int i2, int i3, int i4, boolean z, String str2, int i5, int i6, int i7) {
            return new Target(i, str, i2, i3, i4, z, str2, i5, i6, i7);
        }

        public int getPlayerId() {
            return this.playerId;
        }

        public String getPlayerName() {
            return this.playerName;
        }

        public int getHeroId() {
            return this.heroId;
        }

        public int getCopies() {
            return this.copies;
        }

        public int getThreshold() {
            return this.threshold;
        }

        public boolean isPurchaseEligible() {
            return this.purchaseEligible;
        }

        public String getReason() {
            return this.reason;
        }

        public int getOwnedCopies() {
            return this.ownedCopies;
        }

        public int getMaxOwnedCopies() {
            return this.maxOwnedCopies;
        }

        public int getNeededCopies() {
            return this.neededCopies;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String boundText(String str, int i) {
        if (str == null) {
            return "";
        }
        return str.length() <= i ? str : str.substring(0, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int clamp(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i, i3));
    }
}
