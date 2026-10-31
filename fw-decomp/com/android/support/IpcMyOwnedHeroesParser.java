package com.android.support;

import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcMyOwnedHeroesParser {

    static final class Payload {
        final OwnedHeroCopies copies;
        final int pieceCount;

        Payload(OwnedHeroCopies ownedHeroCopies, int i) {
            this.copies = ownedHeroCopies;
            this.pieceCount = i;
        }
    }

    private IpcMyOwnedHeroesParser() {
    }

    static Payload parse(JSONObject jSONObject) {
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("heroes");
        if (jSONArrayOptJSONArray == null) {
            return null;
        }
        OwnedHeroCopies ownedHeroCopies = OwnedHeroCopies.parse(jSONArrayOptJSONArray);
        int iOptInt = jSONObject.optInt("pieceCount", -1);
        if (iOptInt < 0) {
            iOptInt = ownedHeroCopies.heroIds().size();
        }
        return new Payload(ownedHeroCopies, iOptInt);
    }
}
