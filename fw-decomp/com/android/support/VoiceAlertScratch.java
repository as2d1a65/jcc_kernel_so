package com.android.support;

import com.android.support.VoiceAlertPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
final class VoiceAlertScratch {
    final Map<Integer, PlayerInfo> playersById = new HashMap();
    final List<VoiceAlertPolicy.PlayerBoard> boards = new ArrayList();
    final Map<Integer, HeroData> metadataByCanonicalId = new HashMap();
    final VoiceAlertPolicy.HeroMetadata metadata = new VoiceAlertPolicy.HeroMetadata() { // from class: com.android.support.VoiceAlertScratch.1
        @Override // com.android.support.VoiceAlertPolicy.HeroMetadata
        public int costOf(int i) {
            HeroData heroData = VoiceAlertScratch.this.metadataByCanonicalId.get(Integer.valueOf(i));
            if (heroData == null) {
                return 0;
            }
            return heroData.cost;
        }

        @Override // com.android.support.VoiceAlertPolicy.HeroMetadata
        public String displayNameOf(int i) {
            HeroData heroData = VoiceAlertScratch.this.metadataByCanonicalId.get(Integer.valueOf(i));
            if (heroData == null) {
                return null;
            }
            return heroData.chineseName;
        }
    };

    VoiceAlertScratch() {
    }

    void reset() {
        this.playersById.clear();
        this.boards.clear();
        this.metadataByCanonicalId.clear();
    }
}
