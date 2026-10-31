package com.android.support;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

/* loaded from: classes.dex */
final class UiSurfaceCanonicalBytes {
    private static final String CONTEXT = "JCCUISURFACEV1";

    static byte[] encode(int i, byte[] bArr, byte[] bArr2) throws IOException {
        ArrayList<String> arrayList = new ArrayList();
        for (int i2 = 0; i2 < bArr.length; i2++) {
            if (bArr[i2] != 0 || bArr2[i2] != 0) {
                arrayList.add(UiSurfaceRegistry.idAt(i2));
            }
        }
        Collections.sort(arrayList);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeBytes(CONTEXT);
            dataOutputStream.writeByte(0);
            dataOutputStream.writeInt(i);
            dataOutputStream.writeShort(arrayList.size());
            for (String str : arrayList) {
                int iIndexOf = UiSurfaceRegistry.indexOf(str);
                byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
                dataOutputStream.writeShort(bytes.length);
                dataOutputStream.write(bytes);
                dataOutputStream.writeByte(bArr[iIndexOf]);
                dataOutputStream.writeByte(bArr2[iIndexOf]);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private UiSurfaceCanonicalBytes() {
    }
}
