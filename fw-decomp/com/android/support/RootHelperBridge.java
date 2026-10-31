package com.android.support;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/* loaded from: classes.dex */
final class RootHelperBridge implements AutoCloseable {
    private Thread acceptThread;
    private final RootHelperConnectionLifecycle connectionLifecycle = new RootHelperConnectionLifecycle();
    private final Listener listener;
    private final LocalServerSocket server;

    interface Listener {
        void onGameProbe(RootHelperGameState rootHelperGameState);

        void onReady();
    }

    RootHelperBridge(String str, Listener listener) throws Exception {
        this.server = new LocalServerSocket(str);
        this.listener = listener;
    }

    void start() {
        Thread thread = new Thread(new Runnable() { // from class: com.android.support.RootHelperBridge$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1415lambda$start$0$comandroidsupportRootHelperBridge();
            }
        }, ThreadNaming.WORKER);
        this.acceptThread = thread;
        thread.setDaemon(true);
        this.acceptThread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: acceptLoop, reason: merged with bridge method [inline-methods] */
    public void m1415lambda$start$0$comandroidsupportRootHelperBridge() {
        String line;
        while (!this.connectionLifecycle.isClosed()) {
            LocalSocket localSocketAccept = null;
            try {
                localSocketAccept = this.server.accept();
            } catch (Throwable unused) {
                boolean zIsClosed = this.connectionLifecycle.isClosed();
                RootHelperConnectionLifecycle rootHelperConnectionLifecycle = this.connectionLifecycle;
                if (zIsClosed) {
                    if (!rootHelperConnectionLifecycle.clear(localSocketAccept)) {
                        return;
                    }
                } else if (rootHelperConnectionLifecycle.clear(localSocketAccept)) {
                }
            }
            if (!this.connectionLifecycle.register(localSocketAccept)) {
                if (!this.connectionLifecycle.clear(localSocketAccept)) {
                    return;
                }
                closeQuietly(localSocketAccept);
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(localSocketAccept.getInputStream(), StandardCharsets.UTF_8));
            while (!this.connectionLifecycle.isClosed() && (line = bufferedReader.readLine()) != null) {
                try {
                    dispatch(line);
                } catch (Throwable th) {
                    try {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    } catch (Throwable th3) {
                        if (this.connectionLifecycle.clear(localSocketAccept)) {
                            closeQuietly(localSocketAccept);
                        }
                        throw th3;
                    }
                }
            }
            bufferedReader.close();
            if (this.connectionLifecycle.clear(localSocketAccept)) {
                closeQuietly(localSocketAccept);
            }
        }
    }

    private void dispatch(String str) {
        if (this.listener == null) {
            return;
        }
        if (str.contains(RootHelperProtocol.TYPE_HELPER_READY)) {
            this.listener.onReady();
            return;
        }
        RootHelperGameState gameProbe = RootHelperProtocol.parseGameProbe(str);
        if (gameProbe != null) {
            this.listener.onGameProbe(gameProbe);
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        this.connectionLifecycle.close();
        try {
            this.server.close();
        } catch (Throwable unused) {
        }
        Thread thread = this.acceptThread;
        if (thread != null) {
            thread.interrupt();
        }
    }

    private static void closeQuietly(LocalSocket localSocket) {
        if (localSocket == null) {
            return;
        }
        try {
            localSocket.close();
        } catch (Throwable unused) {
        }
    }
}
