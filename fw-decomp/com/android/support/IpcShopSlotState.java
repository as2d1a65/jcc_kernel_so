package com.android.support;

import com.android.support.IpcShopSlotsParser;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcShopSlotState {
    private int diagnosticCount;
    private long fingerprint = Long.MIN_VALUE;
    private volatile List<ShopSlotData> slots;

    IpcShopSlotState() {
    }

    static final class Update {
        final boolean changed;
        final int diagnosticIndex;
        final List<ShopSlotData> slots;

        private Update(boolean z, List<ShopSlotData> list, int i) {
            this.changed = z;
            this.slots = list;
            this.diagnosticIndex = i;
        }

        boolean shouldLogDiagnostics() {
            if (!this.changed) {
                return false;
            }
            int i = this.diagnosticIndex;
            return i < 5 || i % 10 == 0;
        }
    }

    synchronized Update apply(JSONObject jSONObject) throws Exception {
        IpcShopSlotsParser.Payload payload = IpcShopSlotsParser.parse(jSONObject);
        if (payload.fingerprint == this.fingerprint) {
            return new Update(false, this.slots, this.diagnosticCount);
        }
        this.fingerprint = payload.fingerprint;
        this.slots = payload.slots.isEmpty() ? null : payload.slots;
        this.diagnosticCount++;
        return new Update(true, this.slots, this.diagnosticCount);
    }

    List<ShopSlotData> current() {
        return this.slots;
    }

    synchronized void clear() {
        this.slots = null;
        this.fingerprint = Long.MIN_VALUE;
    }
}
