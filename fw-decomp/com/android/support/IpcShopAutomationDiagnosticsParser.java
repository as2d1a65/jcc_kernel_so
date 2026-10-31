package com.android.support;

import com.android.support.diagnostics.TraceCatalog;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcShopAutomationDiagnosticsParser {
    private IpcShopAutomationDiagnosticsParser() {
    }

    static String formatLine(JSONObject jSONObject) {
        return "[shop-auto-diag] lockKnown=" + jSONObject.optBoolean("lockKnown", false) + " configKnown=" + jSONObject.optBoolean("configKnown", false) + " goldKnown=" + jSONObject.optBoolean("goldKnown", false) + " benchKnown=" + jSONObject.optBoolean("benchKnown", false) + " snapshotComplete=" + jSONObject.optBoolean("snapshotComplete", false) + " candidateCount=" + Math.max(0, Math.min(64, jSONObject.optInt("candidateCount", 0))) + " exactRootCount=" + Math.max(0, Math.min(5, jSONObject.optInt("exactRootCount", 0))) + " autoPickEnabled=" + jSONObject.optBoolean("autoPickEnabled", false) + " gameRecommendActive=" + jSONObject.optBoolean("gameRecommendActive", false) + " autoRefreshEnabled=" + jSONObject.optBoolean("autoRefreshEnabled", false) + " refreshDue=" + jSONObject.optBoolean("refreshDue", false) + " refreshFallbackMs=" + Math.max(100, Math.min(2000, jSONObject.optInt("refreshFallbackMs", TraceCatalog.Feature.BOARD_HUD))) + " pendingBatchSize=" + Math.max(0, Math.min(5, jSONObject.optInt("pendingBatchSize", 0))) + " costMask=" + Math.max(0, Math.min(31, jSONObject.optInt("costMask", 0))) + " source=" + Math.max(0, Math.min(7, jSONObject.optInt("source", 0))) + " cost=" + Math.max(0, Math.min(5, jSONObject.optInt("cost", 0))) + " reject=" + Math.max(0, Math.min(9, jSONObject.optInt("reject", 0))) + " pending=" + jSONObject.optBoolean("pending", false) + " action=" + Math.max(0, Math.min(4, jSONObject.optInt("action", 0))) + " lockReason=" + Math.max(0, Math.min(2, jSONObject.optInt("lockReason", 0)));
    }
}
