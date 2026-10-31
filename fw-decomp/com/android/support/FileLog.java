package com.android.support;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
final class FileLog {
    private static final long FLOG_MAX_BYTES = 262144;
    private static long sFlogBytes;
    private static PrintWriter sFlogWriter;

    private FileLog() {
    }

    static void write(Context context, String str) {
        try {
            PrintWriter printWriter = sFlogWriter;
            if (printWriter == null || sFlogBytes >= FLOG_MAX_BYTES) {
                if (printWriter != null) {
                    try {
                        printWriter.close();
                    } catch (Throwable unused) {
                    }
                }
                File externalFilesDir = context.getExternalFilesDir(null);
                if (externalFilesDir == null) {
                    return;
                }
                File file = new File(externalFilesDir, "xf-debug.log");
                if (sFlogBytes >= FLOG_MAX_BYTES) {
                    File file2 = new File(externalFilesDir, "xf-debug.log.1");
                    try {
                        if (file2.exists()) {
                            file2.delete();
                        }
                    } catch (Throwable unused2) {
                    }
                    try {
                        file.renameTo(file2);
                    } catch (Throwable unused3) {
                    }
                    sFlogBytes = 0L;
                }
                sFlogWriter = new PrintWriter((Writer) new FileWriter(file, true), false);
            }
            sFlogWriter.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US).format(new Date()) + " " + str);
            sFlogWriter.flush();
            sFlogBytes += r6.length() + 1;
        } catch (Throwable unused4) {
        }
    }
}
