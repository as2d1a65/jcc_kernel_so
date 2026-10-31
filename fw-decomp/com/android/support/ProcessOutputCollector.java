package com.android.support;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/* loaded from: classes.dex */
final class ProcessOutputCollector {

    static final class Result {
        final int exitCode;
        final IOException failure;
        final String stderr;
        final String stdout;
        final boolean timedOut;
        final boolean truncated;

        Result(Capture capture, Capture capture2, int i, boolean z) {
            this(capture, capture2, i, z, null);
        }

        Result(Capture capture, Capture capture2, int i, boolean z, IOException iOException) {
            this.stdout = new String(capture.bytes.toByteArray(), StandardCharsets.UTF_8);
            this.stderr = new String(capture2.bytes.toByteArray(), StandardCharsets.UTF_8);
            this.exitCode = i;
            this.timedOut = z;
            this.truncated = capture.truncated || capture2.truncated;
            this.failure = iOException;
        }
    }

    private static final class Capture {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final int limit;
        boolean truncated;

        Capture(int i) {
            this.limit = i;
        }

        void drain(InputStream inputStream, byte[] bArr, int i) throws IOException {
            int iMin = Math.min(inputStream.available(), i);
            while (iMin > 0) {
                boolean z = false;
                int i2 = inputStream.read(bArr, 0, Math.min(bArr.length, iMin));
                if (i2 <= 0) {
                    return;
                }
                int iMin2 = Math.min(i2, this.limit - this.bytes.size());
                this.bytes.write(bArr, 0, iMin2);
                boolean z2 = this.truncated;
                if (iMin2 < i2) {
                    z = true;
                }
                this.truncated = z | z2;
                iMin -= i2;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x00e4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static com.android.support.ProcessOutputCollector.Result read(java.lang.Process r16, long r17, int r19) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 261
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.ProcessOutputCollector.read(java.lang.Process, long, int):com.android.support.ProcessOutputCollector$Result");
    }

    private static void closeQuietly(Closeable closeable) throws IOException {
        try {
            closeable.close();
        } catch (IOException unused) {
        }
    }

    private ProcessOutputCollector() {
    }
}
