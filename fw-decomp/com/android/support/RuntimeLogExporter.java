package com.android.support;

import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import com.android.support.DiagnosticSnapshot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
final class RuntimeLogExporter {
    private static final String TAG = "FloatingWindowService";

    interface ResultCallback {
        void onResult(String str);
    }

    private RuntimeLogExporter() {
    }

    static void export(final Context context, final ResultCallback resultCallback) {
        new Thread(new Runnable() { // from class: com.android.support.RuntimeLogExporter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() throws InterruptedException, IOException {
                RuntimeLogExporter.lambda$export$0(context, resultCallback);
            }
        }, "LogExportThread").start();
    }

    static /* synthetic */ void lambda$export$0(Context context, ResultCallback resultCallback) throws InterruptedException, IOException {
        ResultCallback resultCallback2;
        String strPidToken;
        String str;
        try {
            File file = new File("/storage/emulated/0/Download");
            if (!file.isDirectory()) {
                file.mkdirs();
            }
            File file2 = new File(file, "Xf日志-快照.txt");
            int iMyPid = Process.myPid();
            boolean zHasUid0 = ContinuousLogCapture.hasUid0(ShellExec.run("id"));
            String strBuild = DiagnosticSnapshot.build(context, new DiagnosticSnapshot.ShellRunner() { // from class: com.android.support.RuntimeLogExporter$$ExternalSyntheticLambda1
                @Override // com.android.support.DiagnosticSnapshot.ShellRunner
                public final String run(String str2) {
                    return ShellExec.run(str2);
                }
            });
            int gamePid = -1;
            try {
                String strRun = ShellExec.run("ps -A -o PID,NAME | grep " + GameRegionState.current().packageName + "");
                if (strRun.isEmpty()) {
                    strRun = ShellExec.run("pidof " + GameRegionState.current().packageName + "");
                }
                gamePid = ContinuousLogCapture.parseGamePid(strRun);
                strPidToken = ContinuousLogCapture.pidToken(gamePid);
            } catch (Exception e) {
                Log.w(TAG, "获取游戏PID失败: " + e.getMessage());
                strPidToken = null;
            }
            Process processExec = zHasUid0 ? Runtime.getRuntime().exec(new String[]{"su", "-c", "logcat -b main,crash -d -v threadtime -t 15000"}) : Runtime.getRuntime().exec(new String[]{"logcat", "-b", "main,crash", "-d", "-v", "threadtime", "-t", "10000"});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(processExec.getInputStream()));
            FileWriter fileWriter = new FileWriter(file2);
            fileWriter.write("========== XF 运行日志导出 ==========\n");
            Process process = processExec;
            fileWriter.write("导出时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + "\n");
            fileWriter.write("Root权限: " + (zHasUid0 ? "有" : "无") + "\n");
            fileWriter.write("应用PID: " + iMyPid + "\n");
            fileWriter.write("游戏PID: " + (gamePid > 0 ? Integer.valueOf(gamePid) : "未找到") + "\n");
            fileWriter.write("包名: " + context.getPackageName() + "\n");
            fileWriter.write("游戏包名: " + GameRegionState.current().packageName + "\n");
            fileWriter.write("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")\n");
            fileWriter.write("设备: " + Build.MANUFACTURER + " " + Build.MODEL + "\n");
            fileWriter.write("======================================\n\n");
            if (strBuild != null && !strBuild.isEmpty()) {
                fileWriter.write(strBuild);
            }
            String str2 = " " + iMyPid + " ";
            int i = 0;
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if (ContinuousLogCapture.keepDiagnosticLine(line, strPidToken, str2)) {
                    fileWriter.write(line);
                    fileWriter.write("\n");
                    i++;
                }
            }
            bufferedReader.close();
            process.waitFor();
            fileWriter.write("\n========== 日志结束 (共 " + i + " 行) ==========\n");
            fileWriter.flush();
            fileWriter.close();
            str = "日志已保存: Download/Xf日志-快照.txt (" + i + "行" + (zHasUid0 ? ", root" : "") + ")";
            Dbg.i(TAG, str + " -> " + file2.getAbsolutePath());
            resultCallback2 = resultCallback;
        } catch (Exception e2) {
            e = e2;
            resultCallback2 = resultCallback;
        }
        try {
            resultCallback2.onResult(str);
        } catch (Exception e3) {
            e = e3;
            Log.e(TAG, "导出日志失败: " + e.getMessage(), e);
            resultCallback2.onResult("导出失败: " + e.getMessage());
        }
    }
}
