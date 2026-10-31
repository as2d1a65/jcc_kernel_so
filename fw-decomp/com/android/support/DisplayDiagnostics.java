package com.android.support;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
final class DisplayDiagnostics {
    private static final int MAX_LENGTH = 65536;
    private static final String TAG = "DisplayDiag";
    private static final StringBuilder LOG = new StringBuilder();
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    private DisplayDiagnostics() {
    }

    static void log(String str) {
        String str2 = FORMAT.format(new Date()) + "  " + str;
        Dbg.d(TAG, str2);
        StringBuilder sb = LOG;
        synchronized (sb) {
            if (sb.length() > 65536) {
                sb.delete(0, sb.length() / 2);
            }
            sb.append(str2).append('\n');
        }
    }

    static String getLog() {
        String string;
        StringBuilder sb = LOG;
        synchronized (sb) {
            string = sb.toString();
        }
        return string;
    }

    static void clear() {
        StringBuilder sb = LOG;
        synchronized (sb) {
            sb.setLength(0);
        }
    }
}
