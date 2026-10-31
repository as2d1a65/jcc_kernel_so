package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class BoardHeroAnchor {
    public final int area;
    public final int col;
    public final int heroId;
    public final boolean isEnemy;
    public final int ownedCopies;
    public final int row;
    public final int unitId;
    public final float x;
    public final float y;

    private BoardHeroAnchor(int i, int i2, int i3, int i4, int i5, int i6, boolean z, float f, float f2) {
        this.heroId = i;
        this.unitId = i2;
        this.ownedCopies = i3;
        this.area = i4;
        this.col = i5;
        this.row = i6;
        this.isEnemy = z;
        this.x = f;
        this.y = f2;
    }

    static BoardHeroAnchor create(int i, int i2, int i3, int i4, int i5, int i6, boolean z, float f, float f2) {
        boolean z2 = true;
        if (i4 != 1 ? i4 != 2 || i5 < 0 || i5 >= 9 || i6 != 0 : i5 < 0 || i5 >= 7 || i6 < 0 || i6 >= 8) {
            z2 = false;
        }
        if (i <= 0 || !z2 || !isFinite(f) || !isFinite(f2) || f < 0.0f || f > 1.0f || f2 < 0.0f || f2 > 1.0f) {
            return null;
        }
        return new BoardHeroAnchor(i, Math.max(0, i2), i3 < 0 ? -1 : i3, i4, i5, i6, z, f, f2);
    }

    public String slotKey() {
        return this.area + ":" + this.col + ":" + this.row;
    }

    public String markerKey() {
        if (this.unitId > 0) {
            return "u:" + this.unitId;
        }
        return this.isEnemy ? "e:" + slotKey() : slotKey();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r2v5 */
    public static List<BoardHeroAnchor> parse(JSONArray jSONArray) {
        int i;
        if (jSONArray == null || jSONArray.length() == 0) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        ?? r2 = 0;
        int i2 = 0;
        while (i2 < jSONArray.length()) {
            JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i2);
            if (jSONObjectOptJSONObject == 0) {
                i = i2;
            } else {
                i = i2;
                BoardHeroAnchor boardHeroAnchorCreate = create(jSONObjectOptJSONObject.optInt("heroId", r2), jSONObjectOptJSONObject.optInt("unitId", r2), jSONObjectOptJSONObject.optInt("ownedCopies", -1), jSONObjectOptJSONObject.optInt("area", r2), jSONObjectOptJSONObject.optInt("col", -1), jSONObjectOptJSONObject.optInt("row", -1), jSONObjectOptJSONObject.optBoolean("isEnemy", r2), (float) jSONObjectOptJSONObject.optDouble("x", Double.NaN), (float) jSONObjectOptJSONObject.optDouble("y", Double.NaN));
                if (boardHeroAnchorCreate != null) {
                    arrayList.add(boardHeroAnchorCreate);
                }
            }
            i2 = i + 1;
            r2 = 0;
        }
        return Collections.unmodifiableList(arrayList);
    }

    private static boolean isFinite(float f) {
        return (Float.isNaN(f) || Float.isInfinite(f)) ? false : true;
    }
}
