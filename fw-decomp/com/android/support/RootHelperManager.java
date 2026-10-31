package com.android.support;

import android.content.Context;
import android.util.Log;
import com.android.support.RootHelperBridge;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/* loaded from: classes.dex */
public final class RootHelperManager {
    private static final String TAG = "RootHelper";
    private static volatile RootHelperManager instance;
    private volatile RootHelperBridge bridge;
    private final Context context;
    private volatile long cso;
    private volatile Process helperProcess;
    private volatile RootHelperGameState lastState;
    private volatile Listener listener;
    private volatile boolean started;

    public interface Listener {
        void onGameProbe(RootHelperGameState rootHelperGameState);
    }

    public static RootHelperManager get(Context context) {
        if (instance == null) {
            synchronized (RootHelperManager.class) {
                if (instance == null) {
                    instance = new RootHelperManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private RootHelperManager(Context context) {
        this.context = context;
    }

    public synchronized void start(Listener listener) {
        startInternal(listener, 0L);
    }

    public synchronized void restartWithCso(long j) {
        if (this.started && j > 0) {
            Listener listener = this.listener;
            stop();
            startInternal(listener, j);
        }
    }

    private synchronized void startInternal(final Listener listener, long j) {
        String str;
        if (this.started) {
            if (this.helperProcess != null && this.helperProcess.isAlive()) {
                return;
            }
            this.started = false;
            if (this.bridge != null) {
                this.bridge.close();
                this.bridge = null;
            }
        }
        this.started = true;
        this.cso = j;
        this.listener = listener;
        String strResolveRootBinary = resolveRootBinary();
        try {
            startRootCommand(strResolveRootBinary, "pkill -f jcc_helper");
        } catch (Throwable th) {
            Log.e(TAG, "[root-helper] start failed", th);
        }
        File fileExtractHelper = extractHelper();
        if (fileExtractHelper == null) {
            return;
        }
        String strSocketNameFor = RootHelperProtocol.socketNameFor(this.context.getPackageName());
        try {
            this.bridge = new RootHelperBridge(strSocketNameFor, new RootHelperBridge.Listener() { // from class: com.android.support.RootHelperManager.1
                @Override // com.android.support.RootHelperBridge.Listener
                public void onReady() {
                }

                @Override // com.android.support.RootHelperBridge.Listener
                public void onGameProbe(RootHelperGameState rootHelperGameState) {
                    RootHelperManager.this.lastState = rootHelperGameState;
                    Listener listener2 = listener;
                    if (listener2 != null) {
                        listener2.onGameProbe(rootHelperGameState);
                    }
                }
            });
            this.bridge.start();
            str = fileExtractHelper.getAbsolutePath() + " --sock " + strSocketNameFor;
            if (j > 0) {
                str = str + " --cso " + Long.toHexString(j);
            }
        } catch (Throwable unused) {
            stop();
        }
        try {
            this.helperProcess = startRootCommand(strResolveRootBinary, "exec " + str);
            Log.w(TAG, "[root-helper] launched binary=" + strResolveRootBinary + " command=" + str);
        } finally {
        }
    }

    public synchronized void stop() {
        this.started = false;
        if (this.helperProcess != null) {
            this.helperProcess.destroy();
            this.helperProcess = null;
        }
        if (this.bridge != null) {
            this.bridge.close();
            this.bridge = null;
        }
    }

    public long cso() {
        return this.cso;
    }

    public RootHelperGameState lastState() {
        return this.lastState;
    }

    private String resolveRootBinary() {
        return SuBinaryPathPolicy.binary(SuBinaryPathPolicy.sanitize(this.context.getSharedPreferences(GameRegionState.preferenceName(ConfigPreferenceNamespaces.APP_SETTINGS.preferencesName), 0).getString("su_binary_path", "")));
    }

    private Process startRootCommand(String str, String str2) throws Exception {
        if (SuBinaryPathPolicy.isKernelSuDebugBinary(str)) {
            Process processStart = new ProcessBuilder(SuBinaryPathPolicy.pipeCommand(str)).redirectErrorStream(true).start();
            OutputStream outputStream = processStart.getOutputStream();
            try {
                outputStream.write((str2 + "\nexit\n").getBytes(StandardCharsets.US_ASCII));
                outputStream.flush();
                if (outputStream != null) {
                    outputStream.close();
                }
                return processStart;
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
        String[] strArrPlainPrefix = SuBinaryPathPolicy.plainPrefix(str);
        String[] strArr = new String[strArrPlainPrefix.length + 1];
        System.arraycopy(strArrPlainPrefix, 0, strArr, 0, strArrPlainPrefix.length);
        strArr[strArrPlainPrefix.length] = str2;
        return new ProcessBuilder(strArr).redirectErrorStream(true).start();
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x0018 A[PHI: r2
  0x0018: PHI (r2v1 java.lang.String) = (r2v0 java.lang.String), (r2v10 java.lang.String) binds: [B:3:0x000d, B:6:0x0016] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.io.File extractHelper() {
        /*
            r8 = this;
            java.lang.String[] r0 = android.os.Build.SUPPORTED_ABIS
            r1 = 0
            r0 = r0[r1]
            java.lang.String r2 = "x86_64"
            boolean r3 = r0.startsWith(r2)
            r4 = 0
            if (r3 == 0) goto L10
            goto L18
        L10:
            java.lang.String r2 = "arm64"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto La3
        L18:
            java.io.File r0 = new java.io.File
            android.content.Context r3 = r8.context
            java.io.File r3 = r3.getFilesDir()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            java.lang.String r6 = "helper/"
            r5.<init>(r6)
            java.lang.StringBuilder r5 = r5.append(r2)
            java.lang.String r7 = "/jcc_helper"
            java.lang.StringBuilder r5 = r5.append(r7)
            java.lang.String r5 = r5.toString()
            r0.<init>(r3, r5)
            android.content.Context r8 = r8.context     // Catch: java.lang.Throwable -> La3
            android.content.res.AssetManager r8 = r8.getAssets()     // Catch: java.lang.Throwable -> La3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La3
            r3.<init>(r6)     // Catch: java.lang.Throwable -> La3
            java.lang.StringBuilder r2 = r3.append(r2)     // Catch: java.lang.Throwable -> La3
            java.lang.StringBuilder r2 = r2.append(r7)     // Catch: java.lang.Throwable -> La3
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> La3
            java.io.InputStream r8 = r8.open(r2)     // Catch: java.lang.Throwable -> La3
            java.io.File r2 = r0.getParentFile()     // Catch: java.lang.Throwable -> L97
            if (r2 == 0) goto L6a
            java.io.File r2 = r0.getParentFile()     // Catch: java.lang.Throwable -> L97
            boolean r2 = r2.exists()     // Catch: java.lang.Throwable -> L97
            if (r2 != 0) goto L6a
            java.io.File r2 = r0.getParentFile()     // Catch: java.lang.Throwable -> L97
            r2.mkdirs()     // Catch: java.lang.Throwable -> L97
        L6a:
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L97
            r2.<init>(r0)     // Catch: java.lang.Throwable -> L97
            r3 = 8192(0x2000, float:1.14794E-41)
            byte[] r3 = new byte[r3]     // Catch: java.lang.Throwable -> L8d
        L73:
            int r5 = r8.read(r3)     // Catch: java.lang.Throwable -> L8d
            if (r5 <= 0) goto L7d
            r2.write(r3, r1, r5)     // Catch: java.lang.Throwable -> L8d
            goto L73
        L7d:
            r2.flush()     // Catch: java.lang.Throwable -> L8d
            r2.close()     // Catch: java.lang.Throwable -> L97
            r1 = 1
            r0.setExecutable(r1, r1)     // Catch: java.lang.Throwable -> L97
            if (r8 == 0) goto L8c
            r8.close()     // Catch: java.lang.Throwable -> La3
        L8c:
            return r0
        L8d:
            r0 = move-exception
            r2.close()     // Catch: java.lang.Throwable -> L92
            goto L96
        L92:
            r1 = move-exception
            r0.addSuppressed(r1)     // Catch: java.lang.Throwable -> L97
        L96:
            throw r0     // Catch: java.lang.Throwable -> L97
        L97:
            r0 = move-exception
            if (r8 == 0) goto La2
            r8.close()     // Catch: java.lang.Throwable -> L9e
            goto La2
        L9e:
            r8 = move-exception
            r0.addSuppressed(r8)     // Catch: java.lang.Throwable -> La3
        La2:
            throw r0     // Catch: java.lang.Throwable -> La3
        La3:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.RootHelperManager.extractHelper():java.io.File");
    }
}
