package com.android.support;

import android.content.Context;
import android.util.Log;
import com.android.support.ConfigPreferenceNamespaces;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import kotlinx.coroutines.DebugKt;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class ConfigNamespaceBackupMirror {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactory() { // from class: com.android.support.ConfigNamespaceBackupMirror$$ExternalSyntheticLambda0
        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            return ConfigNamespaceBackupMirror.lambda$static$0(runnable);
        }
    });
    private static final String TAG = "ConfigNamespaceBackup";

    static /* synthetic */ Thread lambda$static$0(Runnable runnable) {
        Thread thread = new Thread(runnable, ThreadNaming.WORKER);
        thread.setDaemon(true);
        return thread;
    }

    private ConfigNamespaceBackupMirror() {
    }

    static void request(Context context, final ConfigPreferenceNamespaces.Namespace namespace) {
        final Context applicationContext = context.getApplicationContext();
        EXECUTOR.execute(new Runnable() { // from class: com.android.support.ConfigNamespaceBackupMirror$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ConfigNamespaceBackupMirror.update(applicationContext, namespace);
            }
        });
    }

    public static void request(Context context, String str) {
        if (context == null || str == null) {
            return;
        }
        for (ConfigPreferenceNamespaces.Namespace namespace : ConfigPreferenceNamespaces.customerConfigs()) {
            if (str.equals(namespace.preferencesName)) {
                request(context, namespace);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void update(Context context, ConfigPreferenceNamespaces.Namespace namespace) {
        File fileAutoBackupFile = ExternalConfigBackupPaths.autoBackupFile();
        try {
            synchronized (AtomicJsonFileStore.FILE_LOCK) {
                JSONObject existing = readExisting(fileAutoBackupFile);
                mergeNamespace(existing, namespace, SharedPreferencesJsonCodec.dump(context.getSharedPreferences(namespace.preferencesName, 0)), System.currentTimeMillis());
                AtomicJsonFileStore.write(fileAutoBackupFile, existing.toString(2));
            }
        } catch (Exception e) {
            Log.e(TAG, "独立配置即时备份失败: " + namespace.preferencesName, e);
        }
    }

    private static JSONObject readExisting(File file) throws Exception {
        if (!file.isFile() || !file.canRead()) {
            return new JSONObject();
        }
        return new JSONObject(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
    }

    static void mergeNamespace(JSONObject jSONObject, ConfigPreferenceNamespaces.Namespace namespace, JSONObject jSONObject2, long j) throws Exception {
        jSONObject.put(namespace.jsonKey, jSONObject2);
        jSONObject.put("_backup_type", DebugKt.DEBUG_PROPERTY_VALUE_AUTO);
        jSONObject.put("_save_time", j);
        jSONObject.put("_save_time_str", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(j)));
    }
}
