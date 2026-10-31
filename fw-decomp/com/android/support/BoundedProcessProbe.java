package com.android.support;

import androidx.core.os.EnvironmentCompat;
import com.android.support.ProcessOutputCollector;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/* loaded from: classes.dex */
final class BoundedProcessProbe {
    private BoundedProcessProbe() {
    }

    static boolean readRootIdentity(Process process, long j) throws Throwable {
        OutputStream outputStream = process.getOutputStream();
        try {
            outputStream.write("id -u\nexit\n".getBytes(StandardCharsets.US_ASCII));
            outputStream.flush();
            if (outputStream != null) {
                outputStream.close();
            }
            String identifier = readIdentifier(process, j);
            return ("0".equals(identifier) || identifier.startsWith("uid=0(")) && process.exitValue() == 0;
        } catch (Throwable th) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    static String readIdentifier(Process process, long j) throws Throwable {
        ProcessOutputCollector.Result result = ProcessOutputCollector.read(process, j, 4096);
        if (result.timedOut || result.truncated || result.failure != null) {
            return null;
        }
        int iIndexOf = result.stdout.indexOf(10);
        int iIndexOf2 = result.stdout.indexOf(13);
        if (iIndexOf2 >= 0 && (iIndexOf < 0 || iIndexOf2 < iIndexOf)) {
            iIndexOf = iIndexOf2;
        }
        String strSubstring = result.stdout;
        if (iIndexOf >= 0) {
            strSubstring = strSubstring.substring(0, iIndexOf);
        }
        return normalizeIdentifier(strSubstring);
    }

    static String normalizeIdentifier(String str) {
        if (str == null) {
            return null;
        }
        String strTrim = str.trim();
        if (strTrim.isEmpty() || EnvironmentCompat.MEDIA_UNKNOWN.equals(strTrim)) {
            return null;
        }
        return strTrim;
    }
}
