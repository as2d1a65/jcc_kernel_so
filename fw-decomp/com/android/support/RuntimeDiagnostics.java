package com.android.support;

import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import androidx.core.os.EnvironmentCompat;
import com.android.support.VerificationTrace;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public final class RuntimeDiagnostics {
    private static final String DIR_NAME = "runtime_diagnostics";
    private static final AtomicBoolean INSTALLED = new AtomicBoolean(false);
    private static volatile RuntimeDiagnostics INSTANCE = null;
    private static final String MARKER_NAME = "last_state.log";
    private static final String TAG = "JccRuntime";
    private final Context context;
    private final Thread.UncaughtExceptionHandler handler;
    private final File markerFile;
    private final Object markerLock = new Object();
    private final Thread.UncaughtExceptionHandler previousHandler;
    private final String previousMarkerSummary;
    private final String sessionId;
    private final long startedAt;

    static /* synthetic */ void lambda$new$0(String str) {
    }

    private RuntimeDiagnostics(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext;
        File file = new File(applicationContext.getFilesDir(), DIR_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.markerFile = new File(file, MARKER_NAME);
        this.previousMarkerSummary = readMarkerSummary();
        this.sessionId = VerificationTrace.start(new VerificationTrace.Sink() { // from class: com.android.support.RuntimeDiagnostics$$ExternalSyntheticLambda0
            @Override // com.android.support.VerificationTrace.Sink
            public final void write(String str) {
                RuntimeDiagnostics.lambda$new$0(str);
            }
        }).id();
        this.startedAt = SystemClock.elapsedRealtime();
        this.previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.handler = new Thread.UncaughtExceptionHandler() { // from class: com.android.support.RuntimeDiagnostics$$ExternalSyntheticLambda1
            @Override // java.lang.Thread.UncaughtExceptionHandler
            public final void uncaughtException(Thread thread, Throwable th) {
                this.f$0.m1417lambda$new$1$comandroidsupportRuntimeDiagnostics(thread, th);
            }
        };
    }

    /* renamed from: lambda$new$1$com-android-support-RuntimeDiagnostics, reason: not valid java name */
    /* synthetic */ void m1417lambda$new$1$comandroidsupportRuntimeDiagnostics(Thread thread, Throwable th) {
        record("JAVA", "UNCAUGHT", "FAIL", throwableSummary(thread, th));
        try {
            FileLogger fileLogger = FileLogger.getInstance();
            if (fileLogger != null && fileLogger.isInitialized()) {
                fileLogger.flushNow();
            }
        } catch (Throwable unused) {
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.previousHandler;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }

    public static RuntimeDiagnostics install(Context context) {
        RuntimeDiagnostics runtimeDiagnostics;
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        RuntimeDiagnostics runtimeDiagnostics2 = INSTANCE;
        if (runtimeDiagnostics2 != null) {
            return runtimeDiagnostics2;
        }
        synchronized (RuntimeDiagnostics.class) {
            runtimeDiagnostics = INSTANCE;
            if (runtimeDiagnostics == null) {
                runtimeDiagnostics = new RuntimeDiagnostics(context);
                INSTANCE = runtimeDiagnostics;
                if (INSTALLED.compareAndSet(false, true)) {
                    Thread.setDefaultUncaughtExceptionHandler(runtimeDiagnostics.handler);
                    runtimeDiagnostics.record("APP", "PROCESS_START", "BEGIN", "abi=" + Build.SUPPORTED_ABIS[0] + " sdk=" + Build.VERSION.SDK_INT + " model=" + Build.MODEL);
                    runtimeDiagnostics.record("APP", "PREVIOUS_MARKER", "INFO", runtimeDiagnostics.previousMarkerSummary);
                }
            }
        }
        return runtimeDiagnostics;
    }

    public static RuntimeDiagnostics get() {
        return INSTANCE;
    }

    public void record(String str, String str2, String str3, String str4) {
        String str5 = RuntimeDiagnosticFormatter.format(this.sessionId, str, str2, str3, SystemClock.elapsedRealtime() - this.startedAt, Process.myPid(), "thread=" + Thread.currentThread().getName() + " " + str4);
        try {
            Dbg.i(TAG, str5);
            FileLogger fileLogger = FileLogger.getInstance();
            if (fileLogger != null && fileLogger.isInitialized()) {
                fileLogger.log("RUNTIME", str5);
            }
            writeMarker(str5);
        } catch (Throwable unused) {
        }
    }

    public String getMarkerFilePath() {
        return this.markerFile.getAbsolutePath();
    }

    private void writeMarker(String str) {
        synchronized (this.markerLock) {
            File file = new File(this.markerFile.getParentFile(), "last_state.log.tmp");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                try {
                    fileOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write(10);
                    fileOutputStream.flush();
                    fileOutputStream.getFD().sync();
                    if (!file.renameTo(this.markerFile)) {
                        FileOutputStream fileOutputStream2 = new FileOutputStream(this.markerFile, false);
                        try {
                            fileOutputStream2.write(str.getBytes(StandardCharsets.UTF_8));
                            fileOutputStream2.write(10);
                            fileOutputStream2.flush();
                            fileOutputStream2.close();
                            file.delete();
                        } finally {
                        }
                    }
                    fileOutputStream.close();
                } catch (Throwable th) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (Exception unused) {
                file.delete();
            }
        }
    }

    private String readMarkerSummary() throws IOException {
        if (!this.markerFile.isFile()) {
            return "none";
        }
        try {
            byte[] allBytes = Files.readAllBytes(this.markerFile.toPath());
            String strTrim = new String(allBytes, StandardCharsets.UTF_8).trim();
            Arrays.fill(allBytes, (byte) 0);
            return strTrim.isEmpty() ? "empty" : strTrim;
        } catch (Exception e) {
            return "unreadable=" + e.getClass().getSimpleName();
        }
    }

    static String throwableSummary(Thread thread, Throwable th) {
        StringWriter stringWriter = new StringWriter(2048);
        if (th != null) {
            th.printStackTrace(new PrintWriter(stringWriter));
        }
        StringBuilder sb = new StringBuilder("thread=");
        String name = EnvironmentCompat.MEDIA_UNKNOWN;
        StringBuilder sbAppend = sb.append(thread == null ? EnvironmentCompat.MEDIA_UNKNOWN : thread.getName()).append(" exception=");
        if (th != null) {
            name = th.getClass().getName();
        }
        return sbAppend.append(name).append(" stack=").append(stringWriter).toString();
    }
}
