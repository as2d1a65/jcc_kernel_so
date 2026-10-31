package com.android.support;

import com.android.support.RecastEquipmentCatalog;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
final class RecastLivePoolCatalog {
    private static final int MAX_ENTRIES = 400;
    private static final int MAX_ID = 1000000;
    final RecastEquipmentCatalog catalog;
    final boolean complete;
    final String matchGeneration;

    private RecastLivePoolCatalog(String str, boolean z, RecastEquipmentCatalog recastEquipmentCatalog) {
        this.matchGeneration = str;
        this.complete = z;
        this.catalog = recastEquipmentCatalog;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0044 A[PHI: r1
  0x0044: PHI (r1v9 java.lang.String) = (r1v1 java.lang.String), (r1v1 java.lang.String), (r1v1 java.lang.String), (r1v4 java.lang.String) binds: [B:17:0x0042, B:20:0x0053, B:23:0x005c, B:65:0x0044] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static com.android.support.RecastLivePoolCatalog parse(org.json.JSONObject r18) {
        /*
            Method dump skipped, instructions count: 254
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.RecastLivePoolCatalog.parse(org.json.JSONObject):com.android.support.RecastLivePoolCatalog");
    }

    private static int countForTier(List<RecastEquipmentCatalog.Entry> list, int i) {
        Iterator<RecastEquipmentCatalog.Entry> it = list.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            if (it.next().tier == i) {
                i2++;
            }
        }
        return i2;
    }
}
