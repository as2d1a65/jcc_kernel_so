package com.android.support;

import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcAllInStatus {
    static final IpcAllInStatus INACTIVE = new IpcAllInStatus(false, 0);
    final boolean active;
    final int reason;

    private IpcAllInStatus(boolean z, int i) {
        this.active = z;
        this.reason = i;
    }

    static IpcAllInStatus parse(JSONObject jSONObject) {
        boolean zOptBoolean = jSONObject.optBoolean(OpponentCardingStatus.STATE_ACTIVE, false);
        int iOptInt = jSONObject.optInt("reason", 0);
        if (!zOptBoolean && iOptInt == 0) {
            return INACTIVE;
        }
        return new IpcAllInStatus(zOptBoolean, iOptInt);
    }
}
