package com.android.support;

import androidx.core.os.EnvironmentCompat;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcMatchupEnvelopeParser {

    static final class Payload {
        final int matchId;
        final int myId;
        final int opponentId;
        final List<MatchupPairData> pairs;
        final String source;

        Payload(int i, int i2, int i3, String str, List<MatchupPairData> list) {
            this.myId = i;
            this.opponentId = i2;
            this.matchId = i3;
            this.source = str;
            this.pairs = list;
        }
    }

    private IpcMatchupEnvelopeParser() {
    }

    static Payload parse(JSONObject jSONObject) throws Exception {
        return new Payload(jSONObject.optInt("myId", -1), jSONObject.optInt("opponentId", -1), jSONObject.optInt("matchId", -1), jSONObject.optString("source", EnvironmentCompat.MEDIA_UNKNOWN), IpcMatchupPairsParser.parse(jSONObject));
    }
}
