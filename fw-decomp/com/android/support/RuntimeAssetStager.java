package com.android.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/* loaded from: classes.dex */
final class RuntimeAssetStager {
    private RuntimeAssetStager() {
    }

    static void stage(InputStream inputStream, File file) throws IOException {
        File fileCreateTempFile = File.createTempFile(".runtime-", ".tmp", file.getParentFile());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFile);
            try {
                byte[] bArr = new byte[65536];
                while (true) {
                    int i = inputStream.read(bArr);
                    if (i != -1) {
                        fileOutputStream.write(bArr, 0, i);
                    } else {
                        try {
                            break;
                        } catch (IOException unused) {
                        }
                    }
                }
                fileOutputStream.getFD().sync();
                fileOutputStream.close();
                try {
                    Files.move(fileCreateTempFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException unused2) {
                    Files.move(fileCreateTempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
            }
        } finally {
            if (fileCreateTempFile.exists()) {
                fileCreateTempFile.delete();
            }
        }
    }
}
