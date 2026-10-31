package com.android.support;

import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
final class ContinuousLogCapture {
    static final String EXPORT_FILE_NAME = "Xf日志-快照.txt";
    private static final long GAME_PID_REFRESH_MS = 2000;
    static final String LOG_DIR = "/storage/emulated/0/Download";
    static final String LOG_FILE_NAME = "Xf日志.txt";
    private static final String TAG = "FloatingWindowService";
    private volatile boolean running = false;
    private Thread thread = null;
    private Process logcatProc = null;
    private FileWriter writer = null;
    private volatile int lines = 0;
    private String filePath = null;

    interface Host {
        String environmentSnapshot();

        String execShell(String str);

        void updateStatus(String str, boolean z);
    }

    ContinuousLogCapture() {
    }

    boolean isRunning() {
        return this.running;
    }

    String filePath() {
        return this.filePath;
    }

    void start(final Host host) {
        if (this.running) {
            return;
        }
        this.running = true;
        this.lines = 0;
        Thread thread = new Thread(new Runnable() { // from class: com.android.support.ContinuousLogCapture$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.m133lambda$start$0$comandroidsupportContinuousLogCapture(host);
            }
        }, "ContinuousLogThread");
        this.thread = thread;
        thread.start();
    }

    /* renamed from: lambda$start$0$com-android-support-ContinuousLogCapture, reason: not valid java name */
    /* synthetic */ void m133lambda$start$0$comandroidsupportContinuousLogCapture(Host host) throws Throwable {
        boolean z;
        Process process;
        String str;
        String line;
        String strPidToken;
        String str2 = TAG;
        try {
            try {
                try {
                    File file = new File(LOG_DIR);
                    if (!file.isDirectory()) {
                        try {
                            file.mkdirs();
                        } catch (Throwable th) {
                            th = th;
                            z = false;
                            this.running = z;
                            try {
                                FileWriter fileWriter = this.writer;
                                if (fileWriter != null) {
                                    fileWriter.close();
                                    this.writer = null;
                                }
                            } catch (Exception unused) {
                            }
                            try {
                                Process process2 = this.logcatProc;
                                if (process2 == null) {
                                    throw th;
                                }
                                process2.destroy();
                                this.logcatProc = null;
                                throw th;
                            } catch (Exception unused2) {
                                throw th;
                            }
                        }
                    }
                    File file2 = new File(file, LOG_FILE_NAME);
                    this.filePath = file2.getAbsolutePath();
                    int iMyPid = Process.myPid();
                    boolean zDetectRoot = detectRoot(host);
                    int iResolveGamePid = resolveGamePid(host);
                    String strPidToken2 = pidToken(iResolveGamePid);
                    try {
                        if (zDetectRoot) {
                            str = TAG;
                            this.logcatProc = Runtime.getRuntime().exec(new String[]{"su", "-c", "logcat -b main,crash -v threadtime"});
                        } else {
                            str = TAG;
                            this.logcatProc = Runtime.getRuntime().exec(new String[]{"logcat", "-b", "main,crash", "-v", "threadtime"});
                        }
                        FileWriter fileWriter2 = new FileWriter(file2);
                        this.writer = fileWriter2;
                        fileWriter2.write("========== XF 持续运行日志 ==========\n");
                        this.writer.write("开始时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + "\n");
                        this.writer.write("Root权限: " + (zDetectRoot ? "有" : "无") + "\n");
                        this.writer.write("应用PID: " + iMyPid + " (Java IPC 服务端 / 悬浮窗)\n");
                        this.writer.write("游戏PID: " + (iResolveGamePid > 0 ? Integer.valueOf(iResolveGamePid) : "未找到") + " (被注入的 C++ 端)\n");
                        this.writer.write("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")\n");
                        this.writer.write("设备: " + Build.MANUFACTURER + " " + Build.MODEL + "\n");
                        this.writer.write("过滤: 同时保留 APP 进程与游戏进程日志 + 原生崩溃 tombstone\n");
                        this.writer.write("======================================\n\n");
                        try {
                            String strEnvironmentSnapshot = host.environmentSnapshot();
                            if (strEnvironmentSnapshot != null && !strEnvironmentSnapshot.isEmpty()) {
                                this.writer.write(strEnvironmentSnapshot);
                            }
                        } catch (Exception unused3) {
                            this.writer.write("---------- 现场快照采集失败 ----------\n\n");
                        }
                        this.writer.flush();
                        host.updateStatus("记录中... (0行) Xf日志.txt", this.running);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.logcatProc.getInputStream()));
                        String str3 = " " + iMyPid + " ";
                        long jElapsedRealtime = SystemClock.elapsedRealtime();
                        int i = 0;
                        int i2 = iResolveGamePid;
                        while (this.running && (line = bufferedReader.readLine()) != null) {
                            long jElapsedRealtime2 = SystemClock.elapsedRealtime();
                            if (jElapsedRealtime2 - jElapsedRealtime < GAME_PID_REFRESH_MS) {
                                strPidToken = strPidToken2;
                            } else {
                                int iResolveGamePid2 = resolveGamePid(host);
                                if (iResolveGamePid2 <= 0 || iResolveGamePid2 == i2) {
                                    jElapsedRealtime = jElapsedRealtime2;
                                    strPidToken = strPidToken2;
                                } else {
                                    this.writer.write("游戏PID更新: " + (i2 > 0 ? String.valueOf(i2) : "未找到") + " -> " + iResolveGamePid2 + "\n");
                                    this.writer.flush();
                                    i2 = iResolveGamePid2;
                                    strPidToken = pidToken(iResolveGamePid2);
                                    jElapsedRealtime = jElapsedRealtime2;
                                }
                            }
                            if (keepDiagnosticLine(line, strPidToken, str3)) {
                                this.writer.write(line);
                                this.writer.write("\n");
                                this.lines++;
                                i++;
                                if (i >= 20) {
                                    this.writer.flush();
                                    host.updateStatus("记录中... (" + this.lines + "行) " + LOG_FILE_NAME, this.running);
                                    i = 0;
                                }
                            }
                            strPidToken2 = strPidToken;
                        }
                        this.writer.write("\n========== 日志结束 (共 " + this.lines + " 行) ==========\n");
                        this.writer.write("结束时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + "\n");
                        this.writer.flush();
                        this.writer.close();
                        this.writer = null;
                        bufferedReader.close();
                        Process process3 = this.logcatProc;
                        if (process3 != null) {
                            process3.destroy();
                            this.logcatProc = null;
                        }
                        int i3 = this.lines;
                        host.updateStatus("已停止 (" + i3 + "行) " + LOG_FILE_NAME, this.running);
                        str2 = str;
                        Dbg.i(str2, "持续日志已保存: " + file2.getAbsolutePath() + " (" + i3 + "行)");
                        this.running = false;
                        try {
                            FileWriter fileWriter3 = this.writer;
                            if (fileWriter3 != null) {
                                fileWriter3.close();
                                this.writer = null;
                            }
                        } catch (Exception unused4) {
                        }
                        process = this.logcatProc;
                        if (process == null) {
                            return;
                        }
                    } catch (Exception e) {
                        e = e;
                        str2 = str;
                        Log.e(str2, "持续日志异常: " + e.getMessage(), e);
                        host.updateStatus("异常停止: " + e.getMessage(), this.running);
                        this.running = false;
                        try {
                            FileWriter fileWriter4 = this.writer;
                            if (fileWriter4 != null) {
                                fileWriter4.close();
                                this.writer = null;
                            }
                        } catch (Exception unused5) {
                        }
                        process = this.logcatProc;
                        if (process == null) {
                            return;
                        }
                        process.destroy();
                        this.logcatProc = null;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    z = false;
                }
            } catch (Exception e2) {
                e = e2;
            }
            process.destroy();
            this.logcatProc = null;
        } catch (Exception unused6) {
        }
    }

    void stop() {
        this.running = false;
        try {
            Process process = this.logcatProc;
            if (process != null) {
                process.destroy();
            }
        } catch (Exception unused) {
        }
    }

    static boolean hasUid0(String str) {
        return str != null && str.contains("uid=0");
    }

    static int parseGamePid(String str) {
        if (str != null && !str.isEmpty()) {
            String[] strArrSplit = str.trim().split("\\s+");
            for (int i = 0; i < strArrSplit.length; i++) {
                try {
                    return Integer.parseInt(strArrSplit[i].trim());
                } catch (NumberFormatException unused) {
                }
            }
        }
        return -1;
    }

    static String pidToken(int i) {
        if (i > 0) {
            return " " + i + " ";
        }
        return null;
    }

    static boolean keepDiagnosticLine(String str, String str2, String str3) {
        if (str == null) {
            return false;
        }
        if (str2 == null || !str.contains(str2)) {
            return (str3 != null && str.contains(str3)) || str.contains("MXP") || str.contains("JccIsolatedDiag") || str.contains("JccManifestDiag") || str.contains("JccNativeCrash") || str.contains("JccRuntime") || str.contains("LocalSocketIPC") || str.contains("lib10edb57537.so") || str.contains("HexPredict") || str.contains("RecastObserve") || str.contains("ShopPredict") || str.contains(new StringBuilder(">>> ").append(GameRegionState.current().packageName).append("").toString()) || str.contains("tombstone") || str.contains("F DEBUG") || str.contains("F libc");
        }
        return true;
    }

    private static boolean detectRoot(Host host) {
        try {
            return hasUid0(host.execShell("id"));
        } catch (Exception unused) {
            return false;
        }
    }

    private static int resolveGamePid(Host host) {
        try {
            String strExecShell = host.execShell("ps -A -o PID,NAME | grep " + GameRegionState.current().packageName + "");
            if (strExecShell == null || strExecShell.isEmpty()) {
                strExecShell = host.execShell("pidof " + GameRegionState.current().packageName + "");
            }
            return parseGamePid(strExecShell);
        } catch (Exception e) {
            Log.w(TAG, "持续日志-获取游戏PID失败: " + e.getMessage());
            return -1;
        }
    }
}
