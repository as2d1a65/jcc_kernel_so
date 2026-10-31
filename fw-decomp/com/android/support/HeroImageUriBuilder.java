package com.android.support;

import com.android.support.lineup.HeroAvatarUrl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
final class HeroImageUriBuilder {
    private static final Map<String, String> FUX_HEAD_OVERRIDE;
    private static final int S11_SEASON = 6;

    private HeroImageUriBuilder() {
    }

    static {
        HashMap map = new HashMap();
        FUX_HEAD_OVERRIDE = map;
        map.put("rumble", "s4_13_head_rumble");
        map.put("aurora", "s4_14_head_aurora");
        map.put("viego", "s4_14_head_viego");
        map.put("renekton", "s4_14_head_renekton");
        map.put("kobuko", "s4_14_head_kobuko");
        map.put("mordekaiser", "s4_8_head_mordekaiser");
        map.put("xerath", "s4_12_head_xerath");
    }

    static String[] build(int i, String str) {
        String str2;
        if (i == 6) {
            return HeroAvatarUrl.buildUriChain("11", str);
        }
        String strCanonicalHeroAvatarName = HeroIdMapper.canonicalHeroAvatarName(i, str);
        if (strCanonicalHeroAvatarName.isEmpty()) {
            return new String[0];
        }
        String[] strArr = new String[0];
        String str3 = "S10";
        String str4 = "S16";
        String str5 = "S17";
        String str6 = "FUX";
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    str2 = "S16";
                    str4 = "S10";
                    str3 = "S17";
                } else if (i == 4) {
                    str6 = "S17";
                    str5 = "FUX";
                    str3 = "S8";
                } else if (i != 5) {
                    str2 = "S16";
                    str4 = "S10";
                    str3 = "FUX";
                    str6 = "S17";
                } else {
                    str3 = "S18";
                    str4 = "FUX";
                    str6 = "S17";
                    str5 = "S8";
                }
                str5 = str2;
            } else {
                str4 = "S10";
                str3 = "S16";
            }
        }
        ArrayList arrayList = new ArrayList(5);
        String strBuildCurrentSeasonCdnUrl = buildCurrentSeasonCdnUrl(i, strCanonicalHeroAvatarName);
        if (strBuildCurrentSeasonCdnUrl != null) {
            arrayList.add(strBuildCurrentSeasonCdnUrl);
        }
        arrayList.add("file:///android_asset/heroes/" + str3 + "/" + strCanonicalHeroAvatarName + ".png");
        arrayList.add("file:///android_asset/heroes/" + str6 + "/" + strCanonicalHeroAvatarName + ".png");
        arrayList.add("file:///android_asset/heroes/" + str5 + "/" + strCanonicalHeroAvatarName + ".png");
        arrayList.add("file:///android_asset/heroes/" + str4 + "/" + strCanonicalHeroAvatarName + ".png");
        return (String[]) arrayList.toArray(new String[0]);
    }

    private static String buildCurrentSeasonCdnUrl(int i, String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        if (i != 0) {
            if (i != 3) {
                return null;
            }
            return "https://game.gtimg.cn/images/lol/act/jkzlk/mode17s18/hero/s17_head_" + str + ".png";
        }
        String str2 = FUX_HEAD_OVERRIDE.get(str);
        if (str2 == null) {
            str2 = "s4_head_" + str;
        }
        return "https://game.gtimg.cn/images/lol/act/jkzlk/mode4s17/hero/" + str2 + ".png";
    }
}
