package com.android.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
final class DiagnosticSnapshot {
    private static final int HTTP_CONNECT_TIMEOUT_MS = 5000;
    private static final int HTTP_READ_TIMEOUT_MS = 5000;
    private static final long PROBE_TIMEOUT_MS = 5000;

    interface ShellRunner {
        String run(String str);
    }

    private DiagnosticSnapshot() {
    }

    static String build(Context context, ShellRunner shellRunner) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("---------- 现场快照 ----------\n");
        append(sb, "版本", version(context));
        append(sb, "设备", device());
        append(sb, "权限", permissions(context, shellRunner));
        append(sb, "游戏", game(shellRunner));
        append(sb, "网络", networkType(context));
        append(sb, "解析", dnsProbe());
        append(sb, "服务端", serverProbe());
        append(sb, "最近一步", lastRuntimeStage());
        sb.append("------------------------------\n");
        sb.append("说明: 未知表示未取得证据；映射或IPC连接不代表功能就绪或检测结论。\n\n");
        return sb.toString();
    }

    private static void append(StringBuilder sb, String str, String str2) {
        StringBuilder sbAppend = sb.append('[').append(str).append("] ");
        if (str2 == null || str2.isEmpty()) {
            str2 = "未知";
        }
        sbAppend.append(str2).append('\n');
    }

    private static String version(Context context) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            sb.append(" (code ").append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode).append(')');
            sb.append(" pkg=").append(context.getPackageName());
        } catch (Exception unused) {
            sb.append("读取失败");
        }
        sb.append(" lane=formal 诊断日志=关");
        return sb.toString();
    }

    private static String device() {
        return Build.MANUFACTURER + " " + Build.MODEL + " | Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ") | abi=" + ((Build.SUPPORTED_ABIS == null || Build.SUPPORTED_ABIS.length <= 0) ? "?" : Build.SUPPORTED_ABIS[0]);
    }

    private static String permissions(Context context, ShellRunner shellRunner) {
        StringBuilder sb = new StringBuilder("root=");
        String strSafeShell = safeShell(shellRunner, "id");
        sb.append((strSafeShell == null || !strSafeShell.contains("uid=0")) ? "否" : "是");
        try {
            sb.append(" 悬浮窗=").append(Settings.canDrawOverlays(context) ? "已授权" : "未授权");
        } catch (Exception unused) {
            sb.append(" 悬浮窗=未知");
        }
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService("power");
            sb.append(" 后台运行=").append((powerManager == null || !powerManager.isIgnoringBatteryOptimizations(context.getPackageName())) ? "未允许" : "已允许");
        } catch (Exception unused2) {
            sb.append(" 后台运行=未知");
        }
        return sb.toString();
    }

    private static String game(ShellRunner shellRunner) {
        int iSinglePid = GameDiagnosticState.singlePid(safeShell(shellRunner, "pidof " + GameRegionState.current().packageName));
        return GameDiagnosticState.describe(iSinglePid, iSinglePid > 0 ? safeShell(shellRunner, "cat /proc/" + iSinglePid + "/maps 2>/dev/null") : null, LocalSocketIPC.getInstance().isClientConnected());
    }

    private static String networkType(Context context) {
        String str;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivityManager == null) {
                return "系统未提供网络服务";
            }
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) {
                return "无网络连接";
            }
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (networkCapabilities == null) {
                return "读取受限(以下解析/服务端两行为准)";
            }
            if (networkCapabilities.hasTransport(1)) {
                str = "WiFi";
            } else if (networkCapabilities.hasTransport(0)) {
                str = "移动数据";
            } else {
                str = networkCapabilities.hasTransport(4) ? "VPN" : "其他";
            }
            return str + " 已联网=" + (networkCapabilities.hasCapability(16) ? "是" : "否(可能是登录门户/假连接)");
        } catch (Exception unused) {
            return "读取受限(以下解析/服务端两行为准)";
        }
    }

    private static String dnsProbe() {
        try {
            final String host = new URL(JccNetworkPolicy.VERSION_URL).getHost();
            return bounded(new Callable() { // from class: com.android.support.DiagnosticSnapshot$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return DiagnosticSnapshot.lambda$dnsProbe$0(host);
                }
            }, "解析失败(DNS 被拦截或无网络)");
        } catch (Exception unused) {
            return "未知";
        }
    }

    static /* synthetic */ String lambda$dnsProbe$0(String str) throws Exception {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        InetAddress[] allByName = InetAddress.getAllByName(str);
        long jElapsedRealtime2 = SystemClock.elapsedRealtime() - jElapsedRealtime;
        StringBuilder sbAppend = new StringBuilder(str).append(" -> ");
        for (int i = 0; i < allByName.length && i < 3; i++) {
            if (i > 0) {
                sbAppend.append(", ");
            }
            sbAppend.append(allByName[i].getHostAddress());
        }
        return sbAppend.append(" (").append(jElapsedRealtime2).append("ms)").toString();
    }

    private static String serverProbe() {
        return bounded(new Callable() { // from class: com.android.support.DiagnosticSnapshot$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return DiagnosticSnapshot.lambda$serverProbe$1();
            }
        }, "连不上(TLS/连接失败，多为网络或代理问题)");
    }

    static /* synthetic */ String lambda$serverProbe$1() throws Exception {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(JccNetworkPolicy.VERSION_URL).openConnection();
        try {
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            return "版本接口 HTTP " + responseCode + " (" + (SystemClock.elapsedRealtime() - jElapsedRealtime) + "ms)" + (responseCode == 200 ? "" : " ← 非 200，链路可能被改写或服务端异常");
        } finally {
            httpURLConnection.disconnect();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String lastRuntimeStage() throws java.io.IOException {
        /*
            com.android.support.RuntimeDiagnostics r0 = com.android.support.RuntimeDiagnostics.get()
            java.lang.String r1 = "无记录"
            if (r0 != 0) goto La
            return r1
        La:
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch: java.lang.Exception -> L33
            java.io.FileReader r3 = new java.io.FileReader     // Catch: java.lang.Exception -> L33
            java.lang.String r0 = r0.getMarkerFilePath()     // Catch: java.lang.Exception -> L33
            r3.<init>(r0)     // Catch: java.lang.Exception -> L33
            r2.<init>(r3)     // Catch: java.lang.Exception -> L33
            java.lang.String r0 = r2.readLine()     // Catch: java.lang.Throwable -> L29
            if (r0 == 0) goto L24
            boolean r3 = r0.isEmpty()     // Catch: java.lang.Throwable -> L29
            if (r3 == 0) goto L25
        L24:
            r0 = r1
        L25:
            r2.close()     // Catch: java.lang.Exception -> L33
            return r0
        L29:
            r0 = move-exception
            r2.close()     // Catch: java.lang.Throwable -> L2e
            goto L32
        L2e:
            r2 = move-exception
            r0.addSuppressed(r2)     // Catch: java.lang.Exception -> L33
        L32:
            throw r0     // Catch: java.lang.Exception -> L33
        L33:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.DiagnosticSnapshot.lastRuntimeStage():java.lang.String");
    }

    private static String safeShell(ShellRunner shellRunner, String str) {
        if (shellRunner == null) {
            return null;
        }
        try {
            return shellRunner.run(str);
        } catch (Exception unused) {
            return null;
        }
    }

    private static String bounded(Callable<String> callable, String str) {
        ExecutorService executorServiceNewSingleThreadExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() { // from class: com.android.support.DiagnosticSnapshot$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return DiagnosticSnapshot.lambda$bounded$2(runnable);
            }
        });
        try {
            return (String) executorServiceNewSingleThreadExecutor.submit(callable).get(PROBE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Exception unused) {
            return str;
        } finally {
            executorServiceNewSingleThreadExecutor.shutdownNow();
        }
    }

    static /* synthetic */ Thread lambda$bounded$2(Runnable runnable) {
        Thread thread = new Thread(runnable, ThreadNaming.WORKER);
        thread.setDaemon(true);
        return thread;
    }
}
