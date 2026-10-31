package com.android.support;

import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class UiSurfacePolicy {
    public static final UiSurfacePolicy EMPTY = new UiSurfacePolicy(0, new byte[UiSurfaceRegistry.size()], new byte[UiSurfaceRegistry.size()]);
    private static final int MAX_SURFACES = 64;
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_OFF = 2;
    public static final int STATE_ON = 1;
    private final byte[] availability;
    public final int revision;
    private final byte[] visibility;

    private UiSurfacePolicy(int i, byte[] bArr, byte[] bArr2) {
        this.revision = i;
        this.visibility = bArr;
        this.availability = bArr2;
    }

    public static UiSurfacePolicy fromJson(int i, JSONArray jSONArray) throws JSONException {
        if (i < 0 || jSONArray == null || jSONArray.length() > 64) {
            throw new JSONException("ui surface policy bounds");
        }
        byte[] bArr = new byte[UiSurfaceRegistry.size()];
        byte[] bArr2 = new byte[UiSurfaceRegistry.size()];
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i2);
            int iIndexOf = UiSurfaceRegistry.indexOf(jSONObject.getString("id"));
            int iVisibilityState = visibilityState(jSONObject.getString("visibility"));
            int iAvailabilityState = availabilityState(jSONObject.getString("availability"));
            if (iIndexOf >= 0) {
                if (bArr[iIndexOf] != 0 || bArr2[iIndexOf] != 0) {
                    throw new JSONException("duplicate ui surface");
                }
                bArr[iIndexOf] = (byte) iVisibilityState;
                bArr2[iIndexOf] = (byte) iAvailabilityState;
            }
        }
        return new UiSurfacePolicy(i, bArr, bArr2);
    }

    public static UiSurfacePolicy fromMasks(int i, int i2, int i3, int i4, int i5) {
        int size = UiSurfaceRegistry.size();
        byte[] bArr = new byte[size];
        byte[] bArr2 = new byte[UiSurfaceRegistry.size()];
        for (int i6 = 0; i6 < size; i6++) {
            int i7 = 1 << i6;
            if ((i3 & i7) != 0) {
                bArr[i6] = 2;
            } else if ((i2 & i7) != 0) {
                bArr[i6] = 1;
            }
            if ((i5 & i7) != 0) {
                bArr2[i6] = 2;
            } else if ((i7 & i4) != 0) {
                bArr2[i6] = 1;
            }
        }
        return new UiSurfacePolicy(Math.max(0, i), bArr, bArr2);
    }

    public static UiSurfacePolicy withPendingRestrictions(UiSurfacePolicy uiSurfacePolicy, long j, UiSurfacePolicy uiSurfacePolicy2) {
        if (uiSurfacePolicy2 == null) {
            return uiSurfacePolicy;
        }
        int i = uiSurfacePolicy2.revision;
        return ((long) i) <= j ? uiSurfacePolicy : fromMasks(Math.max(uiSurfacePolicy.revision, i), uiSurfacePolicy.forceShowMask(), uiSurfacePolicy.forceHideMask() | uiSurfacePolicy2.forceHideMask(), uiSurfacePolicy.forceEnableMask(), uiSurfacePolicy.forceDisableMask() | uiSurfacePolicy2.forceDisableMask());
    }

    private static int visibilityState(String str) throws JSONException {
        if ("default".equals(str)) {
            return 0;
        }
        if ("show".equals(str)) {
            return 1;
        }
        if ("hide".equals(str)) {
            return 2;
        }
        throw new JSONException("ui surface visibility invalid");
    }

    private static int availabilityState(String str) throws JSONException {
        if ("default".equals(str)) {
            return 0;
        }
        if ("enable".equals(str)) {
            return 1;
        }
        if ("disable".equals(str)) {
            return 2;
        }
        throw new JSONException("ui surface availability invalid");
    }

    public boolean visible(String str, boolean z) {
        return resolve(this.visibility, str, z);
    }

    public boolean enabled(String str, boolean z) {
        return resolve(this.availability, str, z);
    }

    private static boolean resolve(byte[] bArr, String str, boolean z) {
        int iIndexOf = UiSurfaceRegistry.indexOf(str);
        if (iIndexOf >= 0) {
            byte b = bArr[iIndexOf];
            if (b == 1) {
                return true;
            }
            if (b == 2) {
                return false;
            }
        }
        return z;
    }

    public int forceShowMask() {
        return mask(this.visibility, 1);
    }

    public int forceHideMask() {
        return mask(this.visibility, 2);
    }

    public int forceEnableMask() {
        return mask(this.availability, 1);
    }

    public int forceDisableMask() {
        return mask(this.availability, 2);
    }

    private static int mask(byte[] bArr, int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < bArr.length; i3++) {
            if (bArr[i3] == i) {
                i2 |= 1 << i3;
            }
        }
        return i2;
    }

    public boolean overridden(String str) {
        int iIndexOf = UiSurfaceRegistry.indexOf(str);
        if (iIndexOf < 0) {
            return false;
        }
        return (this.visibility[iIndexOf] == 0 && this.availability[iIndexOf] == 0) ? false : true;
    }

    public boolean isEmpty() {
        return forceShowMask() == 0 && forceHideMask() == 0 && forceEnableMask() == 0 && forceDisableMask() == 0;
    }

    public byte[] canonicalBytes() {
        return UiSurfaceCanonicalBytes.encode(this.revision, this.visibility, this.availability);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof UiSurfacePolicy)) {
            return false;
        }
        UiSurfacePolicy uiSurfacePolicy = (UiSurfacePolicy) obj;
        return this.revision == uiSurfacePolicy.revision && Arrays.equals(this.visibility, uiSurfacePolicy.visibility) && Arrays.equals(this.availability, uiSurfacePolicy.availability);
    }

    public int hashCode() {
        return (((this.revision * 31) + Arrays.hashCode(this.visibility)) * 31) + Arrays.hashCode(this.availability);
    }
}
