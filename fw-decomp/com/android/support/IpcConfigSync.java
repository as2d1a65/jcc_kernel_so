package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.support.IpcV2ConfigState;
import com.android.support.LocalSocketIPC;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcConfigSync {
    private static final String IPC_REVISION_HIGH_WATERMARK = "high_watermark";
    private static final String IPC_REVISION_PREFS = "ipc_v2_config_revision";
    private static final String TAG = "SharedMemManager";
    private final SharedPreferences prefs;
    final IpcV2ConfigState state;
    private final Transport transport;
    volatile boolean configAutoPick = false;
    volatile boolean configAutoPlay = false;
    volatile boolean configShowDebug = false;
    volatile boolean configSessionActive = false;
    volatile boolean configIosFake = false;
    volatile boolean configBlockDialogs = false;
    volatile boolean configBoardHeroHeadOverlayEnabled = false;
    volatile boolean configOpponentBoardPerspectiveEnabled = false;
    volatile boolean configBoardEntityPreviewEnabled = false;
    volatile boolean configNativePlayerEconName = false;
    volatile boolean configShopNativeLabel = false;
    volatile int configOpponentPredictStyle = 0;
    volatile boolean configNativeAttackIconEnabled = true;
    volatile int configNativeAttackIconOffsetX = 0;
    volatile int configNativeAttackIconOffsetY = -20;
    volatile int configNativeAttackIconSizePercent = 100;
    volatile boolean configAutoPick5CostChosen = false;
    volatile boolean configAutoPickAllChosen = false;
    volatile int configChase3StarMode = 0;
    volatile int configAutoPickCostMask = 31;
    volatile int configChase3StarChosenThreshold = 0;
    volatile int configGameSpeed = 1;
    volatile OpponentCardingConfig configOpponentCarding = OpponentCardingConfig.defaults();
    volatile long hudStyleRevision = 1;

    interface Transport {
        boolean requestConfigSync();
    }

    static long sanitizePersistedConfigRevision(long j) {
        if (j < 0) {
            return 0L;
        }
        return j;
    }

    IpcConfigSync(Context context, Transport transport) {
        this.transport = transport;
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(IPC_REVISION_PREFS, 0);
        this.prefs = sharedPreferences;
        long jReserveInitial = IpcV2ConfigRevisionPolicy.reserveInitial(sanitizePersistedConfigRevision(sharedPreferences.getLong(IPC_REVISION_HIGH_WATERMARK, 0L)), System.currentTimeMillis());
        persistInitial(jReserveInitial);
        this.state = new IpcV2ConfigState(jReserveInitial, new IpcV2ConfigState.RevisionPersister() { // from class: com.android.support.IpcConfigSync$$ExternalSyntheticLambda0
            @Override // com.android.support.IpcV2ConfigState.RevisionPersister
            public final void persist(long j) {
                this.f$0.persist(j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void persist(long j) {
        if (j <= 0) {
            throw new IllegalStateException("failed to persist IPC config revision");
        }
        this.prefs.edit().putLong(IPC_REVISION_HIGH_WATERMARK, j).apply();
    }

    private void persistInitial(long j) {
        if (j <= 0 || !this.prefs.edit().putLong(IPC_REVISION_HIGH_WATERMARK, j).commit()) {
            throw new IllegalStateException("failed to persist IPC config revision");
        }
    }

    boolean requestSync() {
        return this.transport.requestConfigSync();
    }

    void sendConfig(String str, boolean z) {
        try {
            this.state.setBoolean(str, z);
            requestSync();
        } catch (Exception e) {
            Log.e(TAG, "发送配置失败: " + str, e);
        }
    }

    void sendConfig(String str, int i) {
        try {
            this.state.setInt(str, i);
            requestSync();
        } catch (Exception e) {
            Log.e(TAG, "发送配置失败: " + str, e);
        }
    }

    LocalSocketIPC.ConfigSnapshot buildConfigSnapshot() {
        IpcV2ConfigState.Snapshot snapshot = this.state.snapshot();
        JSONObject jSONObject = new JSONObject();
        try {
            GameRegion gameRegionCurrent = GameRegionState.current();
            for (Map.Entry<String, Object> entry : GameRegionFeaturePolicy.project(snapshot.values(), gameRegionCurrent, GameRegionControl.visible(gameRegionCurrent) && GameRegionControl.enabled(gameRegionCurrent), UiSurfaceControl.current()).entrySet()) {
                putConfigJsonValue(jSONObject, entry.getKey(), entry.getValue());
            }
            return new LocalSocketIPC.ConfigSnapshot(snapshot.revision(), jSONObject);
        } catch (Exception e) {
            throw new IllegalStateException("failed to encode IPC v2 config snapshot", e);
        }
    }

    static boolean isStarTargetKey(String str) {
        return IpcV2ConfigState.STAR_TARGETS.equals(str) || IpcV2ConfigState.AUXILIARY_STAR_TARGETS.equals(str) || IpcV2ConfigState.LINEUP_APPLIED_STAR_TARGETS.equals(str);
    }

    private static void putConfigJsonValue(JSONObject jSONObject, String str, Object obj) throws Exception {
        if (isStarTargetKey(str)) {
            jSONObject.put(str, starTargetsToJson(obj));
        } else {
            jSONObject.put(str, JSONObject.wrap(obj));
        }
    }

    private static JSONArray starTargetsToJson(Object obj) throws Exception {
        JSONArray jSONArray = new JSONArray();
        if (obj != null) {
            if (!(obj instanceof Map)) {
                throw new IllegalArgumentException("star targets must be a map");
            }
            for (Map.Entry entry : ((Map) obj).entrySet()) {
                jSONArray.put(new JSONObject().put("heroId", toBoundedInt(entry.getKey(), 1, Integer.MAX_VALUE, "heroId")).put("star", toBoundedInt(entry.getValue(), 1, 4, "star")));
            }
        }
        return jSONArray;
    }

    private static int toBoundedInt(Object obj, int i, int i2, String str) throws NumberFormatException {
        long jLongValue;
        if (obj instanceof Number) {
            jLongValue = ((Number) obj).longValue();
        } else if (obj instanceof String) {
            jLongValue = Long.parseLong((String) obj);
        } else {
            throw new IllegalArgumentException(str + " is not numeric");
        }
        if (jLongValue < i || jLongValue > i2) {
            throw new IllegalArgumentException(str + " out of range");
        }
        return (int) jLongValue;
    }
}
