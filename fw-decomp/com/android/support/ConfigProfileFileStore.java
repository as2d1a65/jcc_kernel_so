package com.android.support;

import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

/* loaded from: classes.dex */
final class ConfigProfileFileStore {
    private static final String TAG = "FloatingWindowService";

    private ConfigProfileFileStore() {
    }

    static Result backup(File[] fileArr, File file) {
        boolean z = true;
        int i = 0;
        for (File file2 : fileArr) {
            try {
                AtomicJsonFileStore.write(new File(file, file2.getName()), readWithLineEndings(file2));
                i++;
            } catch (Exception e) {
                Log.e(TAG, "备份profile失败: " + file2.getName(), e);
                z = false;
            }
        }
        return new Result(z, i, fileArr.length);
    }

    static Result restore(File file, File file2, final String str) {
        File[] fileArrListFiles = file.listFiles(new FilenameFilter() { // from class: com.android.support.ConfigProfileFileStore$$ExternalSyntheticLambda0
            @Override // java.io.FilenameFilter
            public final boolean accept(File file3, String str2) {
                return ConfigProfileFileStore.lambda$restore$0(str, file3, str2);
            }
        });
        boolean z = true;
        if (fileArrListFiles == null || fileArrListFiles.length == 0) {
            return new Result(true, 0, 0);
        }
        int i = 0;
        int i2 = 0;
        for (File file3 : fileArrListFiles) {
            File file4 = new File(file2, file3.getName());
            if (!file4.exists()) {
                i2++;
                try {
                    AtomicJsonFileStore.write(file4, readWithLineEndings(file3));
                    i++;
                } catch (Exception e) {
                    Log.e(TAG, "恢复profile失败: " + file3.getName(), e);
                    z = false;
                }
            }
        }
        return new Result(z, i, i2);
    }

    static /* synthetic */ boolean lambda$restore$0(String str, File file, String str2) {
        return str2.startsWith(str) && str2.endsWith(".json");
    }

    private static String readWithLineEndings(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while (true) {
            try {
                String line = bufferedReader.readLine();
                if (line != null) {
                    sb.append(line).append("\n");
                } else {
                    bufferedReader.close();
                    return sb.toString();
                }
            } catch (Throwable th) {
                try {
                    bufferedReader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
    }

    static final class Result {
        final int copied;
        final int expected;
        final boolean success;

        Result(boolean z, int i, int i2) {
            this.success = z;
            this.copied = i;
            this.expected = i2;
        }
    }
}
