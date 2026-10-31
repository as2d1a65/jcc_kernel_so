package com.android.support;

import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class ConfigSnapshotCodec {
    private ConfigSnapshotCodec() {
    }

    static void write(JSONObject jSONObject, SharedPreferences sharedPreferences, String[][] strArr, String[][] strArr2, String[][] strArr3, String[][] strArr4) throws JSONException {
        for (String[] strArr5 : strArr) {
            String str = strArr5[0];
            jSONObject.put(str, sharedPreferences.getBoolean(str, Boolean.parseBoolean(strArr5[1])));
        }
        for (String[] strArr6 : strArr2) {
            String str2 = strArr6[0];
            jSONObject.put(str2, sharedPreferences.getInt(str2, Integer.parseInt(strArr6[1])));
        }
        for (String[] strArr7 : strArr3) {
            jSONObject.put(strArr7[0], sharedPreferences.getFloat(r2, Float.parseFloat(r0[1])));
        }
        for (String[] strArr8 : strArr4) {
            String str3 = strArr8[0];
            jSONObject.put(str3, sharedPreferences.getString(str3, strArr8[1]));
        }
    }

    static void restorePresent(JSONObject jSONObject, SharedPreferences.Editor editor, String[][] strArr, String[][] strArr2, String[][] strArr3, String[][] strArr4) throws JSONException {
        for (String[] strArr5 : strArr) {
            if (jSONObject.has(strArr5[0])) {
                String str = strArr5[0];
                editor.putBoolean(str, jSONObject.getBoolean(str));
            }
        }
        for (String[] strArr6 : strArr2) {
            if (jSONObject.has(strArr6[0])) {
                String str2 = strArr6[0];
                editor.putInt(str2, jSONObject.getInt(str2));
            }
        }
        for (String[] strArr7 : strArr3) {
            if (jSONObject.has(strArr7[0])) {
                String str3 = strArr7[0];
                editor.putFloat(str3, (float) jSONObject.getDouble(str3));
            }
        }
        for (String[] strArr8 : strArr4) {
            if (jSONObject.has(strArr8[0])) {
                String str4 = strArr8[0];
                editor.putString(str4, jSONObject.getString(str4));
            }
        }
    }

    static void writeDefaults(SharedPreferences.Editor editor, String[][] strArr, String[][] strArr2, String[][] strArr3, String[][] strArr4) {
        for (String[] strArr5 : strArr) {
            editor.putBoolean(strArr5[0], Boolean.parseBoolean(strArr5[1]));
        }
        for (String[] strArr6 : strArr2) {
            editor.putInt(strArr6[0], Integer.parseInt(strArr6[1]));
        }
        for (String[] strArr7 : strArr3) {
            editor.putFloat(strArr7[0], Float.parseFloat(strArr7[1]));
        }
        for (String[] strArr8 : strArr4) {
            editor.putString(strArr8[0], strArr8[1]);
        }
    }
}
