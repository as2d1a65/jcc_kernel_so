package com.android.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class ConfigProfileCatalog {

    interface ErrorReporter {
        void report(Exception exc);
    }

    private ConfigProfileCatalog() {
    }

    static File[] list(File file, final String str) {
        File[] fileArrListFiles = file.listFiles(new FilenameFilter() { // from class: com.android.support.ConfigProfileCatalog$$ExternalSyntheticLambda0
            @Override // java.io.FilenameFilter
            public final boolean accept(File file2, String str2) {
                return ConfigProfileCatalog.lambda$list$0(str, file2, str2);
            }
        });
        if (fileArrListFiles == null) {
            return new File[0];
        }
        Arrays.sort(fileArrListFiles, new Comparator() { // from class: com.android.support.ConfigProfileCatalog$$ExternalSyntheticLambda1
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return Long.compare(((File) obj2).lastModified(), ((File) obj).lastModified());
            }
        });
        return fileArrListFiles;
    }

    static /* synthetic */ boolean lambda$list$0(String str, File file, String str2) {
        return str2.startsWith(str) && str2.endsWith(".json");
    }

    static String profileName(File file, String str, ErrorReporter errorReporter) {
        try {
            JSONObject json = readJson(file);
            if (json.has("_profile_name")) {
                return json.getString("_profile_name");
            }
        } catch (Exception e) {
            errorReporter.report(e);
        }
        return file.getName().replace(str, "").replace(".json", "");
    }

    static String saveTime(File file, Locale locale) {
        try {
            JSONObject json = readJson(file);
            if (json.has("_save_time_str")) {
                return json.getString("_save_time_str");
            }
        } catch (Exception unused) {
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(new Date(file.lastModified()));
    }

    private static JSONObject readJson(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while (true) {
            try {
                String line = bufferedReader.readLine();
                if (line != null) {
                    sb.append(line);
                } else {
                    bufferedReader.close();
                    return new JSONObject(sb.toString());
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
}
