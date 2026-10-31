package com.android.support;

import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/* loaded from: classes.dex */
final class AtomicJsonFileStore {
    static final Object FILE_LOCK = new Object();

    private AtomicJsonFileStore() {
    }

    static void write(File file, String str) throws IOException {
        synchronized (FILE_LOCK) {
            writeLocked(file, str);
        }
    }

    private static void writeLocked(File file, String str) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs() && !parentFile.isDirectory()) {
            throw new IOException("无法创建备份目录: " + parentFile);
        }
        File file2 = new File(parentFile, file.getName() + ".tmp");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    try {
                        bufferedWriter.write(str);
                        bufferedWriter.flush();
                        fileOutputStream.getFD().sync();
                        bufferedWriter.close();
                        outputStreamWriter.close();
                        fileOutputStream.close();
                        try {
                            Files.move(file2.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                        } catch (AtomicMoveNotSupportedException unused) {
                            Files.move(file2.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } finally {
                    }
                } finally {
                }
            } finally {
            }
        } finally {
            if (file2.exists() && !file2.delete()) {
                Log.w("AtomicJsonFileStore", "无法清理自动备份临时文件: " + file2);
            }
        }
    }
}
