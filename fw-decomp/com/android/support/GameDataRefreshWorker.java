package com.android.support;

import android.os.Handler;
import com.android.support.GameDataRefreshApplier;

/* loaded from: classes.dex */
final class GameDataRefreshWorker {

    interface Host {
        GameDataRefreshApplier.Host applyHost();

        Handler autoRefreshHandler();

        void clearHexTimeout();

        boolean debugBoxMode();

        boolean exitHidBecauseStale();

        int exitHidePlayerTimestamp();

        long exitHideRankAtMs();

        boolean exitSuppressed();

        void finishRefresh(long j);

        int hexTimeoutCount();

        void hideInGameOverlaysOnExit();

        void incrementHexTimeout();

        boolean isRefreshGenerationCurrent(long j);

        int lastBattleProbePlayerTimestamp();

        int lastBoardPosTimestamp();

        boolean lastCppConnected();

        int lastHeroPoolTimestamp();

        long lastHexTriggerMs();

        Boolean lastLeftoverPostedCppConnected();

        long lastLivePlayerUpdateAtMs();

        long lastLiveRankAnchorAtMs();

        long leftoverVoiceBoardFingerprint();

        int leftoverVoiceBoardTimestamp();

        boolean leftoverVoiceHold();

        int leftoverVoicePlayerTimestamp();

        long leftoverVoiceRosterFingerprint();

        void logExplicitEmptyMatchup();

        void logHexHide(int i);

        void logHexRestore();

        void logWorkerException(Exception exc);

        Handler mainHandler();

        void noteSharedMemoryReadFailure();

        void onCppReconnected();

        void onSharedMemoryReadFailed();

        boolean opponentHighlightEnabled();

        void resetSharedMemoryReadFailures();

        void restoreInGameOverlays();

        int retainedPlanningOpponentId();

        boolean selectorHasPlayers();

        void setExitHidBecauseStale(boolean z);

        void setExitHidePlayerTimestamp(int i);

        void setExitHideRankAtMs(long j);

        void setExitSuppressed(boolean z);

        void setHexTriggerSent(boolean z);

        void setLastBattleProbePlayerTimestamp(int i);

        void setLastConnectionCheckMs(long j);

        void setLastCppConnected(boolean z);

        void setLastHexTriggerMs(long j);

        void setLastLeftoverPostedCppConnected(Boolean bool);

        void setLastLivePlayerUpdateAtMs(long j);

        void setLastLiveRankAnchorAtMs(long j);

        void setLeftoverVoiceBoardFingerprint(long j);

        void setLeftoverVoiceBoardTimestamp(int i);

        void setLeftoverVoiceHold(boolean z);

        void setLeftoverVoicePlayerTimestamp(int i);

        void setLeftoverVoiceRosterFingerprint(long j);

        void setRetainedPlanningOpponentId(int i);

        SharedMemManager shmManager();
    }

    private GameDataRefreshWorker() {
    }

    /* JADX WARN: Removed duplicated region for block: B:102:0x0272 A[Catch: Exception -> 0x0467, TryCatch #3 {Exception -> 0x0467, blocks: (B:3:0x000c, B:5:0x0012, B:8:0x001e, B:10:0x0024, B:12:0x003a, B:14:0x0040, B:17:0x004c, B:19:0x0064, B:22:0x006b, B:23:0x006e, B:25:0x0072, B:31:0x0086, B:33:0x0094, B:35:0x009b, B:37:0x00a1, B:39:0x00a6, B:41:0x00b4, B:43:0x00bd, B:45:0x00c3, B:47:0x00ca, B:49:0x00ce, B:51:0x00d6, B:53:0x00de, B:55:0x00f0, B:58:0x00fd, B:60:0x0140, B:62:0x0162, B:64:0x0183, B:66:0x018c, B:71:0x01dc, B:73:0x01e2, B:75:0x01ea, B:77:0x020d, B:79:0x0211, B:81:0x0215, B:83:0x0219, B:98:0x0262, B:100:0x026a, B:102:0x0272, B:103:0x0279, B:104:0x0295, B:106:0x0299, B:108:0x02a9, B:110:0x02b4, B:117:0x02c5, B:120:0x02cb, B:122:0x02d9, B:126:0x02ed, B:133:0x0310, B:172:0x042f, B:174:0x0443, B:176:0x0449, B:178:0x0452, B:180:0x0456, B:116:0x02c1, B:85:0x021d, B:87:0x0223, B:89:0x0233, B:91:0x0237, B:94:0x023d, B:65:0x0188, B:67:0x01ac, B:69:0x01b4, B:46:0x00c8, B:30:0x007c, B:186:0x0463), top: B:197:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:106:0x0299 A[Catch: Exception -> 0x0467, TryCatch #3 {Exception -> 0x0467, blocks: (B:3:0x000c, B:5:0x0012, B:8:0x001e, B:10:0x0024, B:12:0x003a, B:14:0x0040, B:17:0x004c, B:19:0x0064, B:22:0x006b, B:23:0x006e, B:25:0x0072, B:31:0x0086, B:33:0x0094, B:35:0x009b, B:37:0x00a1, B:39:0x00a6, B:41:0x00b4, B:43:0x00bd, B:45:0x00c3, B:47:0x00ca, B:49:0x00ce, B:51:0x00d6, B:53:0x00de, B:55:0x00f0, B:58:0x00fd, B:60:0x0140, B:62:0x0162, B:64:0x0183, B:66:0x018c, B:71:0x01dc, B:73:0x01e2, B:75:0x01ea, B:77:0x020d, B:79:0x0211, B:81:0x0215, B:83:0x0219, B:98:0x0262, B:100:0x026a, B:102:0x0272, B:103:0x0279, B:104:0x0295, B:106:0x0299, B:108:0x02a9, B:110:0x02b4, B:117:0x02c5, B:120:0x02cb, B:122:0x02d9, B:126:0x02ed, B:133:0x0310, B:172:0x042f, B:174:0x0443, B:176:0x0449, B:178:0x0452, B:180:0x0456, B:116:0x02c1, B:85:0x021d, B:87:0x0223, B:89:0x0233, B:91:0x0237, B:94:0x023d, B:65:0x0188, B:67:0x01ac, B:69:0x01b4, B:46:0x00c8, B:30:0x007c, B:186:0x0463), top: B:197:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:107:0x02a8  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x02b4 A[Catch: Exception -> 0x0467, TryCatch #3 {Exception -> 0x0467, blocks: (B:3:0x000c, B:5:0x0012, B:8:0x001e, B:10:0x0024, B:12:0x003a, B:14:0x0040, B:17:0x004c, B:19:0x0064, B:22:0x006b, B:23:0x006e, B:25:0x0072, B:31:0x0086, B:33:0x0094, B:35:0x009b, B:37:0x00a1, B:39:0x00a6, B:41:0x00b4, B:43:0x00bd, B:45:0x00c3, B:47:0x00ca, B:49:0x00ce, B:51:0x00d6, B:53:0x00de, B:55:0x00f0, B:58:0x00fd, B:60:0x0140, B:62:0x0162, B:64:0x0183, B:66:0x018c, B:71:0x01dc, B:73:0x01e2, B:75:0x01ea, B:77:0x020d, B:79:0x0211, B:81:0x0215, B:83:0x0219, B:98:0x0262, B:100:0x026a, B:102:0x0272, B:103:0x0279, B:104:0x0295, B:106:0x0299, B:108:0x02a9, B:110:0x02b4, B:117:0x02c5, B:120:0x02cb, B:122:0x02d9, B:126:0x02ed, B:133:0x0310, B:172:0x042f, B:174:0x0443, B:176:0x0449, B:178:0x0452, B:180:0x0456, B:116:0x02c1, B:85:0x021d, B:87:0x0223, B:89:0x0233, B:91:0x0237, B:94:0x023d, B:65:0x0188, B:67:0x01ac, B:69:0x01b4, B:46:0x00c8, B:30:0x007c, B:186:0x0463), top: B:197:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:113:0x02bb  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x02be  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x02c1 A[Catch: Exception -> 0x0467, TryCatch #3 {Exception -> 0x0467, blocks: (B:3:0x000c, B:5:0x0012, B:8:0x001e, B:10:0x0024, B:12:0x003a, B:14:0x0040, B:17:0x004c, B:19:0x0064, B:22:0x006b, B:23:0x006e, B:25:0x0072, B:31:0x0086, B:33:0x0094, B:35:0x009b, B:37:0x00a1, B:39:0x00a6, B:41:0x00b4, B:43:0x00bd, B:45:0x00c3, B:47:0x00ca, B:49:0x00ce, B:51:0x00d6, B:53:0x00de, B:55:0x00f0, B:58:0x00fd, B:60:0x0140, B:62:0x0162, B:64:0x0183, B:66:0x018c, B:71:0x01dc, B:73:0x01e2, B:75:0x01ea, B:77:0x020d, B:79:0x0211, B:81:0x0215, B:83:0x0219, B:98:0x0262, B:100:0x026a, B:102:0x0272, B:103:0x0279, B:104:0x0295, B:106:0x0299, B:108:0x02a9, B:110:0x02b4, B:117:0x02c5, B:120:0x02cb, B:122:0x02d9, B:126:0x02ed, B:133:0x0310, B:172:0x042f, B:174:0x0443, B:176:0x0449, B:178:0x0452, B:180:0x0456, B:116:0x02c1, B:85:0x021d, B:87:0x0223, B:89:0x0233, B:91:0x0237, B:94:0x023d, B:65:0x0188, B:67:0x01ac, B:69:0x01b4, B:46:0x00c8, B:30:0x007c, B:186:0x0463), top: B:197:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:125:0x02e8  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0308  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x030e  */
    /* JADX WARN: Removed duplicated region for block: B:147:0x036d  */
    /* JADX WARN: Removed duplicated region for block: B:162:0x03d2  */
    /* JADX WARN: Removed duplicated region for block: B:167:0x040f A[Catch: Exception -> 0x045c, TryCatch #1 {Exception -> 0x045c, blocks: (B:165:0x0401, B:167:0x040f, B:168:0x0416, B:170:0x0420), top: B:193:0x0401 }] */
    /* JADX WARN: Removed duplicated region for block: B:170:0x0420 A[Catch: Exception -> 0x045c, TRY_LEAVE, TryCatch #1 {Exception -> 0x045c, blocks: (B:165:0x0401, B:167:0x040f, B:168:0x0416, B:170:0x0420), top: B:193:0x0401 }] */
    /* JADX WARN: Removed duplicated region for block: B:180:0x0456 A[Catch: Exception -> 0x0467, TryCatch #3 {Exception -> 0x0467, blocks: (B:3:0x000c, B:5:0x0012, B:8:0x001e, B:10:0x0024, B:12:0x003a, B:14:0x0040, B:17:0x004c, B:19:0x0064, B:22:0x006b, B:23:0x006e, B:25:0x0072, B:31:0x0086, B:33:0x0094, B:35:0x009b, B:37:0x00a1, B:39:0x00a6, B:41:0x00b4, B:43:0x00bd, B:45:0x00c3, B:47:0x00ca, B:49:0x00ce, B:51:0x00d6, B:53:0x00de, B:55:0x00f0, B:58:0x00fd, B:60:0x0140, B:62:0x0162, B:64:0x0183, B:66:0x018c, B:71:0x01dc, B:73:0x01e2, B:75:0x01ea, B:77:0x020d, B:79:0x0211, B:81:0x0215, B:83:0x0219, B:98:0x0262, B:100:0x026a, B:102:0x0272, B:103:0x0279, B:104:0x0295, B:106:0x0299, B:108:0x02a9, B:110:0x02b4, B:117:0x02c5, B:120:0x02cb, B:122:0x02d9, B:126:0x02ed, B:133:0x0310, B:172:0x042f, B:174:0x0443, B:176:0x0449, B:178:0x0452, B:180:0x0456, B:116:0x02c1, B:85:0x021d, B:87:0x0223, B:89:0x0233, B:91:0x0237, B:94:0x023d, B:65:0x0188, B:67:0x01ac, B:69:0x01b4, B:46:0x00c8, B:30:0x007c, B:186:0x0463), top: B:197:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x020b  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0233 A[Catch: Exception -> 0x0467, TryCatch #3 {Exception -> 0x0467, blocks: (B:3:0x000c, B:5:0x0012, B:8:0x001e, B:10:0x0024, B:12:0x003a, B:14:0x0040, B:17:0x004c, B:19:0x0064, B:22:0x006b, B:23:0x006e, B:25:0x0072, B:31:0x0086, B:33:0x0094, B:35:0x009b, B:37:0x00a1, B:39:0x00a6, B:41:0x00b4, B:43:0x00bd, B:45:0x00c3, B:47:0x00ca, B:49:0x00ce, B:51:0x00d6, B:53:0x00de, B:55:0x00f0, B:58:0x00fd, B:60:0x0140, B:62:0x0162, B:64:0x0183, B:66:0x018c, B:71:0x01dc, B:73:0x01e2, B:75:0x01ea, B:77:0x020d, B:79:0x0211, B:81:0x0215, B:83:0x0219, B:98:0x0262, B:100:0x026a, B:102:0x0272, B:103:0x0279, B:104:0x0295, B:106:0x0299, B:108:0x02a9, B:110:0x02b4, B:117:0x02c5, B:120:0x02cb, B:122:0x02d9, B:126:0x02ed, B:133:0x0310, B:172:0x042f, B:174:0x0443, B:176:0x0449, B:178:0x0452, B:180:0x0456, B:116:0x02c1, B:85:0x021d, B:87:0x0223, B:89:0x0233, B:91:0x0237, B:94:0x023d, B:65:0x0188, B:67:0x01ac, B:69:0x01b4, B:46:0x00c8, B:30:0x007c, B:186:0x0463), top: B:197:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:91:0x0237 A[Catch: Exception -> 0x0467, TryCatch #3 {Exception -> 0x0467, blocks: (B:3:0x000c, B:5:0x0012, B:8:0x001e, B:10:0x0024, B:12:0x003a, B:14:0x0040, B:17:0x004c, B:19:0x0064, B:22:0x006b, B:23:0x006e, B:25:0x0072, B:31:0x0086, B:33:0x0094, B:35:0x009b, B:37:0x00a1, B:39:0x00a6, B:41:0x00b4, B:43:0x00bd, B:45:0x00c3, B:47:0x00ca, B:49:0x00ce, B:51:0x00d6, B:53:0x00de, B:55:0x00f0, B:58:0x00fd, B:60:0x0140, B:62:0x0162, B:64:0x0183, B:66:0x018c, B:71:0x01dc, B:73:0x01e2, B:75:0x01ea, B:77:0x020d, B:79:0x0211, B:81:0x0215, B:83:0x0219, B:98:0x0262, B:100:0x026a, B:102:0x0272, B:103:0x0279, B:104:0x0295, B:106:0x0299, B:108:0x02a9, B:110:0x02b4, B:117:0x02c5, B:120:0x02cb, B:122:0x02d9, B:126:0x02ed, B:133:0x0310, B:172:0x042f, B:174:0x0443, B:176:0x0449, B:178:0x0452, B:180:0x0456, B:116:0x02c1, B:85:0x021d, B:87:0x0223, B:89:0x0233, B:91:0x0237, B:94:0x023d, B:65:0x0188, B:67:0x01ac, B:69:0x01b4, B:46:0x00c8, B:30:0x007c, B:186:0x0463), top: B:197:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0262 A[Catch: Exception -> 0x0467, TryCatch #3 {Exception -> 0x0467, blocks: (B:3:0x000c, B:5:0x0012, B:8:0x001e, B:10:0x0024, B:12:0x003a, B:14:0x0040, B:17:0x004c, B:19:0x0064, B:22:0x006b, B:23:0x006e, B:25:0x0072, B:31:0x0086, B:33:0x0094, B:35:0x009b, B:37:0x00a1, B:39:0x00a6, B:41:0x00b4, B:43:0x00bd, B:45:0x00c3, B:47:0x00ca, B:49:0x00ce, B:51:0x00d6, B:53:0x00de, B:55:0x00f0, B:58:0x00fd, B:60:0x0140, B:62:0x0162, B:64:0x0183, B:66:0x018c, B:71:0x01dc, B:73:0x01e2, B:75:0x01ea, B:77:0x020d, B:79:0x0211, B:81:0x0215, B:83:0x0219, B:98:0x0262, B:100:0x026a, B:102:0x0272, B:103:0x0279, B:104:0x0295, B:106:0x0299, B:108:0x02a9, B:110:0x02b4, B:117:0x02c5, B:120:0x02cb, B:122:0x02d9, B:126:0x02ed, B:133:0x0310, B:172:0x042f, B:174:0x0443, B:176:0x0449, B:178:0x0452, B:180:0x0456, B:116:0x02c1, B:85:0x021d, B:87:0x0223, B:89:0x0233, B:91:0x0237, B:94:0x023d, B:65:0x0188, B:67:0x01ac, B:69:0x01b4, B:46:0x00c8, B:30:0x007c, B:186:0x0463), top: B:197:0x000c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void run(final com.android.support.GameDataRefreshWorker.Host r42, final long r43, boolean r45, com.android.support.RuntimeRefreshPolicy.Needs r46) {
        /*
            Method dump skipped, instructions count: 1135
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.GameDataRefreshWorker.run(com.android.support.GameDataRefreshWorker$Host, long, boolean, com.android.support.RuntimeRefreshPolicy$Needs):void");
    }

    static /* synthetic */ void lambda$run$0(Host host, long j, String str, SharedMemManager sharedMemManager) {
        if (host.isRefreshGenerationCurrent(j) && str.equals(sharedMemManager.getCurrentMatchGeneration())) {
            host.hideInGameOverlaysOnExit();
        }
    }

    static /* synthetic */ void lambda$run$1(Host host, long j, String str, SharedMemManager sharedMemManager) {
        if (host.isRefreshGenerationCurrent(j) && str.equals(sharedMemManager.getCurrentMatchGeneration())) {
            host.restoreInGameOverlays();
        }
    }

    static /* synthetic */ void lambda$run$2(Host host, int[] iArr) {
        if (iArr == null) {
            host.incrementHexTimeout();
        } else {
            host.clearHexTimeout();
        }
    }

    static /* synthetic */ void lambda$run$3(Host host, long j, String str, SharedMemManager sharedMemManager, GameDataRefreshSnapshot gameDataRefreshSnapshot) {
        try {
            if (host.isRefreshGenerationCurrent(j) && str.equals(sharedMemManager.getCurrentMatchGeneration())) {
                GameDataRefreshApplier.apply(host.applyHost(), gameDataRefreshSnapshot);
            }
        } finally {
            host.finishRefresh(j);
        }
    }
}
