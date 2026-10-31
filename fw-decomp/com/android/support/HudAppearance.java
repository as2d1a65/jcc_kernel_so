package com.android.support;

import android.content.SharedPreferences;
import androidx.core.view.ViewCompat;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class HudAppearance {
    public static final int DEFAULT_FIGHT = -8519692;
    public static final int DEFAULT_GOLD = -670639;
    public static final int DEFAULT_LEVEL = -8519692;
    public static final int DEFAULT_SELF = -8519692;
    public static final int DEFAULT_SHOP = -670639;
    public static final String PREF_FIGHT = "hud_style_fight_argb";
    public static final String PREF_GOLD = "hud_style_gold_argb";
    public static final String PREF_LEVEL = "hud_style_level_argb";
    public static final String PREF_SELF = "hud_style_self_argb";
    public static final String PREF_SHOP = "hud_style_shop_argb";
    private int levelColor = -8519692;
    private int goldColor = -670639;
    private int shopColor = -670639;
    private int selfColor = -8519692;

    public static int rgb24(int i) {
        return i & ViewCompat.MEASURED_SIZE_MASK;
    }

    public static int sanitizeArgb(int i, int i2) {
        return (i & ViewCompat.MEASURED_SIZE_MASK) | ViewCompat.MEASURED_STATE_MASK;
    }

    public static HudAppearance defaults() {
        return new HudAppearance();
    }

    public static HudAppearance load(SharedPreferences sharedPreferences) {
        int i;
        HudAppearance hudAppearance = new HudAppearance();
        if (sharedPreferences == null) {
            return hudAppearance;
        }
        hudAppearance.levelColor = sanitizeArgb(sharedPreferences.getInt(PREF_LEVEL, -8519692), -8519692);
        hudAppearance.goldColor = sanitizeArgb(sharedPreferences.getInt(PREF_GOLD, -670639), -670639);
        hudAppearance.shopColor = sanitizeArgb(sharedPreferences.getInt(PREF_SHOP, -670639), -670639);
        if (sharedPreferences.contains(PREF_SELF)) {
            i = sharedPreferences.getInt(PREF_SELF, -8519692);
        } else {
            i = sharedPreferences.getInt(PREF_FIGHT, -8519692);
        }
        hudAppearance.setCombatColor(i);
        return hudAppearance;
    }

    public void save(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().putInt(PREF_LEVEL, this.levelColor).putInt(PREF_GOLD, this.goldColor).putInt(PREF_SHOP, this.shopColor).putInt(PREF_SELF, this.selfColor).putInt(PREF_FIGHT, this.selfColor).apply();
    }

    public HudAppearance copy() {
        HudAppearance hudAppearance = new HudAppearance();
        hudAppearance.levelColor = this.levelColor;
        hudAppearance.goldColor = this.goldColor;
        hudAppearance.shopColor = this.shopColor;
        hudAppearance.selfColor = this.selfColor;
        return hudAppearance;
    }

    public void resetDefaults() {
        this.levelColor = -8519692;
        this.goldColor = -670639;
        this.shopColor = -670639;
        this.selfColor = -8519692;
    }

    public int getLevelColor() {
        return this.levelColor;
    }

    public int getGoldColor() {
        return this.goldColor;
    }

    public int getShopColor() {
        return this.shopColor;
    }

    public int getSelfColor() {
        return this.selfColor;
    }

    public int getCombatColor() {
        return this.selfColor;
    }

    public int getFightColor() {
        return getCombatColor();
    }

    public void setLevelColor(int i) {
        this.levelColor = sanitizeArgb(i, -8519692);
    }

    public void setGoldColor(int i) {
        this.goldColor = sanitizeArgb(i, -670639);
    }

    public void setShopColor(int i) {
        this.shopColor = sanitizeArgb(i, -670639);
    }

    public void setCombatColor(int i) {
        this.selfColor = sanitizeArgb(i, -8519692);
    }

    public void setSelfColor(int i) {
        setCombatColor(i);
    }

    public void setFightColor(int i) {
        setCombatColor(i);
    }

    public JSONObject toIpcParams() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("levelRgb", rgb24(this.levelColor));
            jSONObject.put("goldRgb", rgb24(this.goldColor));
            jSONObject.put("shopRgb", rgb24(this.shopColor));
            jSONObject.put("selfRgb", rgb24(this.selfColor));
            jSONObject.put("fightRgb", rgb24(this.selfColor));
        } catch (JSONException unused) {
        }
        return jSONObject;
    }

    public static String formatLevelPlain(int i) {
        return Math.max(0, i) + "级";
    }

    public static String formatGoldPlain(int i) {
        return Math.max(0, i) + "金";
    }

    public static String formatNativeWidth(int i, int i2) {
        return formatLevelPlain(i) + " " + formatGoldPlain(i2);
    }

    public static String formatNativeRich(int i, int i2, int i3, int i4) {
        return "<color=#" + hex24(i3) + ">" + formatLevelPlain(i) + "</color> <color=#" + hex24(i4) + ">" + formatGoldPlain(i2) + "</color>";
    }

    public static String formatShopRemain(int i, boolean z) {
        if (i < 0) {
            return "剩?";
        }
        return (z ? "天" : "") + "剩" + i;
    }

    public static String hex24(int i) {
        String upperCase = Integer.toHexString(rgb24(i)).toUpperCase();
        if (upperCase.length() >= 6) {
            return upperCase.substring(upperCase.length() - 6);
        }
        StringBuilder sb = new StringBuilder(6);
        for (int length = upperCase.length(); length < 6; length++) {
            sb.append('0');
        }
        sb.append(upperCase);
        return sb.toString();
    }

    public static boolean isValidHexColor(String str) {
        if (str == null) {
            return false;
        }
        String strTrim = str.trim();
        if (strTrim.startsWith("#")) {
            strTrim = strTrim.substring(1);
        }
        return strTrim.matches("[0-9a-fA-F]{6}");
    }

    public static int parseHexArgb(String str, int i) {
        int iRgb24;
        if (isValidHexColor(str)) {
            String strTrim = str.trim();
            if (strTrim.charAt(0) == '#') {
                strTrim = strTrim.substring(1);
            }
            try {
                return Integer.parseInt(strTrim, 16) | ViewCompat.MEASURED_STATE_MASK;
            } catch (NumberFormatException unused) {
                iRgb24 = rgb24(i);
            }
        } else {
            iRgb24 = rgb24(i);
        }
        return iRgb24 | ViewCompat.MEASURED_STATE_MASK;
    }
}
