package com.android.support;

import java.io.File;

/* loaded from: classes.dex */
final class ExternalConfigBackupPaths {
    static final String AUTO_BACKUP_FILE = "auto_backup.json";
    static final String PROFILES_DIRECTORY = "profiles";
    static final String ROOT_DIRECTORY = "/sdcard/.codex_config";

    private ExternalConfigBackupPaths() {
    }

    static File autoBackupFile() {
        return new File(ROOT_DIRECTORY, AUTO_BACKUP_FILE);
    }
}
