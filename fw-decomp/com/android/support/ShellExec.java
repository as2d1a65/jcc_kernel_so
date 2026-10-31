package com.android.support;

import com.android.support.ProcessOutputCollector;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
final class ShellExec {
    private static volatile String suBinary = "su";

    interface ProcessStarter {
        Process start(String[] strArr) throws IOException;
    }

    private ShellExec() {
    }

    static void setSuBinary(String str) {
        String strTrim;
        if (str == null || str.trim().isEmpty()) {
            strTrim = "su";
        } else {
            strTrim = str.trim();
        }
        suBinary = strTrim;
    }

    static String run(String str) {
        return run(str, 5000L, new ProcessStarter() { // from class: com.android.support.ShellExec$$ExternalSyntheticLambda0
            @Override // com.android.support.ShellExec.ProcessStarter
            public final Process start(String[] strArr) {
                return new ProcessBuilder(strArr).start();
            }
        });
    }

    static String run(String str, long j, ProcessStarter processStarter) {
        String[][] strArr;
        ProcessOutputCollector.Result result;
        long jNanoTime = System.nanoTime();
        if (SuBinaryPathPolicy.isKernelSuDebugBinary(suBinary)) {
            strArr = new String[][]{SuBinaryPathPolicy.pipeCommand(suBinary), new String[]{"sh", "-c", str}};
        } else {
            strArr = new String[][]{new String[]{suBinary, "-c", str}, new String[]{"sh", "-c", str}};
        }
        for (String[] strArr2 : strArr) {
            if (Thread.currentThread().isInterrupted()) {
                return "";
            }
            long millis = j - TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - jNanoTime);
            if (millis <= 0) {
                return "";
            }
            Process processStart = null;
            try {
                try {
                    processStart = processStarter.start(strArr2);
                    if (SuBinaryPathPolicy.isKernelSuDebugBinary(strArr2[0])) {
                        OutputStream outputStream = processStart.getOutputStream();
                        try {
                            outputStream.write((str + "\nexit\n").getBytes(StandardCharsets.US_ASCII));
                            outputStream.flush();
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } finally {
                        }
                    } else {
                        processStart.getOutputStream().close();
                    }
                    result = ProcessOutputCollector.read(processStart, millis, 8388608);
                } catch (IOException unused) {
                    if (0 != 0) {
                        if (0 != 0) {
                            processStart.destroy();
                        }
                        return "";
                    }
                    if (0 != 0) {
                    }
                } catch (InterruptedException unused2) {
                    Thread.currentThread().interrupt();
                    if (0 != 0) {
                        processStart.destroy();
                    }
                    return "";
                }
                if (!result.timedOut && result.failure == null) {
                    if (result.exitCode == 0) {
                        String str2 = result.stdout;
                        if (processStart != null) {
                            processStart.destroy();
                        }
                        return str2;
                    }
                    if (processStart != null) {
                        processStart.destroy();
                    }
                }
                if (processStart != null) {
                    processStart.destroy();
                }
                return "";
            } finally {
            }
        }
        return "";
    }
}
