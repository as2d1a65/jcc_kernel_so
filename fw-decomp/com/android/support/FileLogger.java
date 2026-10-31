package com.android.support;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
public class FileLogger {
    private static final String CURRENT_LOG = "current_session.log";
    private static final int FLUSH_COUNT = 32;
    private static final long FLUSH_INTERVAL_MS = 500;
    private static final String HISTORY_PREFIX = "session_";
    private static final String LOG_DIR_NAME = "runtime_logs";
    private static final int MAX_HISTORY_SESSIONS = 5;
    private static final long MAX_LOG_SIZE = 5242880;
    private static final String PREVIOUS_LOG = "previous_session.log";
    private static final String TAG = "FileLogger";
    private static volatile FileLogger sInstance;
    private final File currentLogFile;
    private Handler flushHandler;
    private HandlerThread flushThread;
    private final File logDir;
    private final File previousLogFile;
    private BufferedWriter writer;
    private final Object writeLock = new Object();
    private final ThreadLocal<SimpleDateFormat> timestampFormat = new ThreadLocal<SimpleDateFormat>() { // from class: com.android.support.FileLogger.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
        }
    };
    private volatile boolean initialized = false;
    private long lastFlushMs = 0;
    private int pendingWrites = 0;
    private boolean flushScheduled = false;
    private final Runnable delayedFlush = new Runnable() { // from class: com.android.support.FileLogger$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.flushNow();
        }
    };

    private FileLogger(Context context) {
        File file = new File(context.getExternalFilesDir(null), LOG_DIR_NAME);
        this.logDir = file;
        if (!file.exists()) {
            file.mkdirs();
        }
        this.currentLogFile = new File(file, CURRENT_LOG);
        this.previousLogFile = new File(file, PREVIOUS_LOG);
    }

    public static FileLogger getInstance(Context context) {
        if (sInstance == null) {
            synchronized (FileLogger.class) {
                if (sInstance == null) {
                    sInstance = new FileLogger(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public static FileLogger getInstance() {
        return sInstance;
    }

    public void init() {
        synchronized (this.writeLock) {
            if (this.initialized) {
                return;
            }
            try {
                rotateLog();
                this.writer = new BufferedWriter(new FileWriter(this.currentLogFile, false), 8192);
                this.initialized = true;
                startFlushSchedulerLocked();
                writeSessionHeader();
                Dbg.i(TAG, "FileLogger 初始化完成: " + this.currentLogFile.getAbsolutePath());
            } catch (IOException e) {
                Log.e(TAG, "FileLogger 初始化失败", e);
            }
        }
    }

    private void rotateLog() throws IOException {
        try {
            BufferedWriter bufferedWriter = this.writer;
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (Exception unused) {
                }
                this.writer = null;
            }
            if (this.currentLogFile.exists() && this.currentLogFile.length() > 4096) {
                File file = new File(this.logDir, HISTORY_PREFIX + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(this.currentLogFile.lastModified())) + ".log");
                if (!this.currentLogFile.renameTo(file)) {
                    copyFile(this.currentLogFile, file);
                    this.currentLogFile.delete();
                }
                Dbg.d(TAG, "日志轮转: current → " + file.getName() + " (" + file.length() + " bytes)");
                if (file.length() <= 524288) {
                    if (this.previousLogFile.exists()) {
                        this.previousLogFile.delete();
                    }
                    copyFile(file, this.previousLogFile);
                }
            }
            cleanHistorySessions();
        } catch (Exception e) {
            Log.e(TAG, "日志轮转失败", e);
        }
    }

    private void cleanHistorySessions() {
        try {
            File[] historySessionFiles = getHistorySessionFiles();
            if (historySessionFiles != null && historySessionFiles.length > 5) {
                Arrays.sort(historySessionFiles, new Comparator() { // from class: com.android.support.FileLogger$$ExternalSyntheticLambda1
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        return Long.compare(((File) obj).lastModified(), ((File) obj2).lastModified());
                    }
                });
                int length = historySessionFiles.length - 5;
                for (int i = 0; i < length; i++) {
                    if (historySessionFiles[i].delete()) {
                        Dbg.d(TAG, "清理旧会话: " + historySessionFiles[i].getName());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "清理历史会话失败", e);
        }
    }

    private void copyFile(File file, File file2) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                try {
                    byte[] bArr = new byte[8192];
                    while (true) {
                        int i = fileInputStream.read(bArr);
                        if (i > 0) {
                            fileOutputStream.write(bArr, 0, i);
                        } else {
                            fileOutputStream.close();
                            fileInputStream.close();
                            return;
                        }
                    }
                } finally {
                }
            } finally {
            }
        } catch (IOException e) {
            Log.e(TAG, "复制日志文件失败", e);
        }
    }

    private void writeSessionHeader() {
        try {
            this.writer.write("============================================================\n  会话启动: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + "\n  设备: " + Build.BRAND + " " + Build.MODEL + "\n  Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")\n  PID: " + Process.myPid() + "\n============================================================\n\n");
            flushLocked();
        } catch (IOException e) {
            Log.e(TAG, "写入会话头失败", e);
        }
    }

    public void log(String str, String str2) {
        if (!this.initialized || this.writer == null) {
            return;
        }
        String str3 = this.timestampFormat.get().format(new Date()) + " [" + Thread.currentThread().getName() + "] " + str + ": " + str2 + "\n";
        synchronized (this.writeLock) {
            try {
                if (this.currentLogFile.length() > MAX_LOG_SIZE) {
                    this.writer.write("\n[WARN] 日志文件超过 5MB，之前的日志已截断\n\n");
                    this.writer.flush();
                    this.writer.close();
                    truncateLogFile();
                    this.writer = new BufferedWriter(new FileWriter(this.currentLogFile, true), 8192);
                }
                this.writer.write(str3);
                noteWriteLocked();
            } catch (IOException unused) {
            }
        }
    }

    public void log(String str) {
        log("APP", str);
    }

    public void logSection(String str) {
        if (!this.initialized || this.writer == null) {
            return;
        }
        String str2 = "\n────────────────── " + str + " ──────────────────\n";
        synchronized (this.writeLock) {
            try {
                this.writer.write(str2);
                flushLocked();
            } catch (IOException unused) {
            }
        }
    }

    public void logBlock(String str, String str2) {
        if (!this.initialized || this.writer == null || str2 == null) {
            return;
        }
        String str3 = this.timestampFormat.get().format(new Date());
        synchronized (this.writeLock) {
            try {
                this.writer.write(str3 + " [" + str + "] ↓↓↓\n");
                this.writer.write(str2);
                if (!str2.endsWith("\n")) {
                    this.writer.write("\n");
                }
                this.writer.write(str3 + " [" + str + "] ↑↑↑\n");
                flushLocked();
            } catch (IOException unused) {
            }
        }
    }

    private void truncateLogFile() throws IOException {
        try {
            byte[] allBytes = Files.readAllBytes(this.currentLogFile.toPath());
            int length = allBytes.length / 3;
            FileOutputStream fileOutputStream = new FileOutputStream(this.currentLogFile, false);
            try {
                fileOutputStream.write("[...之前的日志已截断...]\n\n".getBytes());
                fileOutputStream.write(allBytes, length, allBytes.length - length);
                fileOutputStream.close();
            } finally {
            }
        } catch (Exception e) {
            Log.e(TAG, "截断日志文件失败", e);
        }
    }

    public void flushNow() {
        synchronized (this.writeLock) {
            flushLocked();
        }
    }

    private void noteWriteLocked() throws IOException {
        this.pendingWrites++;
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (this.pendingWrites < 32) {
            long j = this.lastFlushMs;
            if (j == 0 || jCurrentTimeMillis - j < FLUSH_INTERVAL_MS) {
                scheduleFlushLocked();
                return;
            }
        }
        flushLocked();
    }

    private void flushLocked() throws IOException {
        BufferedWriter bufferedWriter = this.writer;
        if (bufferedWriter == null) {
            this.pendingWrites = 0;
            cancelScheduledFlushLocked();
        } else {
            try {
                bufferedWriter.flush();
            } catch (IOException unused) {
            }
            this.lastFlushMs = System.currentTimeMillis();
            this.pendingWrites = 0;
            cancelScheduledFlushLocked();
        }
    }

    private void startFlushSchedulerLocked() {
        if (this.flushThread != null) {
            return;
        }
        HandlerThread handlerThread = new HandlerThread("FileLogger-Flush");
        handlerThread.start();
        this.flushThread = handlerThread;
        Looper looper = handlerThread.getLooper();
        this.flushHandler = looper != null ? new Handler(looper) : null;
    }

    private void scheduleFlushLocked() {
        Handler handler;
        if (this.flushScheduled || (handler = this.flushHandler) == null) {
            return;
        }
        this.flushScheduled = true;
        handler.postDelayed(this.delayedFlush, FLUSH_INTERVAL_MS);
    }

    private void cancelScheduledFlushLocked() {
        Handler handler;
        if (this.flushScheduled && (handler = this.flushHandler) != null) {
            handler.removeCallbacks(this.delayedFlush);
        }
        this.flushScheduled = false;
    }

    private void stopFlushSchedulerLocked() {
        cancelScheduledFlushLocked();
        HandlerThread handlerThread = this.flushThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            this.flushThread = null;
            this.flushHandler = null;
        }
    }

    public void close() {
        synchronized (this.writeLock) {
            BufferedWriter bufferedWriter = this.writer;
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.write("\n[SESSION END] " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + "\n");
                    flushLocked();
                    this.writer.close();
                } catch (IOException unused) {
                }
                this.writer = null;
            }
            stopFlushSchedulerLocked();
            this.initialized = false;
        }
    }

    public File getCurrentLogFile() {
        return this.currentLogFile;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    private File[] getHistorySessionFiles() {
        File[] fileArrListFiles = this.logDir.listFiles(new FilenameFilter() { // from class: com.android.support.FileLogger$$ExternalSyntheticLambda2
            @Override // java.io.FilenameFilter
            public final boolean accept(File file, String str) {
                return FileLogger.lambda$getHistorySessionFiles$1(file, str);
            }
        });
        if (fileArrListFiles != null && fileArrListFiles.length > 1) {
            Arrays.sort(fileArrListFiles, new Comparator() { // from class: com.android.support.FileLogger$$ExternalSyntheticLambda3
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return Long.compare(((File) obj2).lastModified(), ((File) obj).lastModified());
                }
            });
        }
        return fileArrListFiles;
    }

    static /* synthetic */ boolean lambda$getHistorySessionFiles$1(File file, String str) {
        return str.startsWith(HISTORY_PREFIX) && str.endsWith(".log");
    }
}
