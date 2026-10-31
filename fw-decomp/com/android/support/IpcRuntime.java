package com.android.support;

import com.android.support.IpcV2OwnerRegistry;
import com.android.support.LocalSocketIPC;

/* loaded from: classes.dex */
final class IpcRuntime {
    private IpcV2OwnerRegistry.Token connectionToken;
    private IpcV2OwnerRegistry.Token messageToken;
    private final Object owner;
    private boolean serverStarted;
    private final Transport transport;

    interface Transport {
        IpcV2OwnerRegistry.Token registerConnectionListener(Object obj, LocalSocketIPC.ConnectionListener connectionListener);

        IpcV2OwnerRegistry.Token registerMessageListener(Object obj, LocalSocketIPC.MessageListener messageListener);

        boolean startServer();

        void stopServer();

        void unregisterConnectionListener(IpcV2OwnerRegistry.Token token);

        void unregisterMessageListener(IpcV2OwnerRegistry.Token token);
    }

    private static final class SocketTransport implements Transport {
        private final LocalSocketIPC socket;

        SocketTransport(LocalSocketIPC localSocketIPC) {
            this.socket = localSocketIPC;
        }

        @Override // com.android.support.IpcRuntime.Transport
        public boolean startServer() {
            return this.socket.startServer();
        }

        @Override // com.android.support.IpcRuntime.Transport
        public IpcV2OwnerRegistry.Token registerConnectionListener(Object obj, LocalSocketIPC.ConnectionListener connectionListener) {
            return this.socket.registerConnectionListener(obj, connectionListener);
        }

        @Override // com.android.support.IpcRuntime.Transport
        public IpcV2OwnerRegistry.Token registerMessageListener(Object obj, LocalSocketIPC.MessageListener messageListener) {
            return this.socket.registerMessageListener(obj, messageListener);
        }

        @Override // com.android.support.IpcRuntime.Transport
        public void unregisterConnectionListener(IpcV2OwnerRegistry.Token token) {
            this.socket.unregisterConnectionListener(token);
        }

        @Override // com.android.support.IpcRuntime.Transport
        public void unregisterMessageListener(IpcV2OwnerRegistry.Token token) {
            this.socket.unregisterMessageListener(token);
        }

        @Override // com.android.support.IpcRuntime.Transport
        public void stopServer() {
            this.socket.stopServer();
        }
    }

    IpcRuntime(LocalSocketIPC localSocketIPC, Object obj) {
        this(new SocketTransport(localSocketIPC), obj);
    }

    IpcRuntime(Transport transport, Object obj) {
        this.transport = transport;
        this.owner = obj;
    }

    synchronized boolean start(LocalSocketIPC.ConnectionListener connectionListener, LocalSocketIPC.MessageListener messageListener) {
        boolean zStartServer;
        zStartServer = false;
        try {
            if (this.messageToken == null) {
                this.messageToken = this.transport.registerMessageListener(this.owner, messageListener);
            }
            if (this.connectionToken == null) {
                this.connectionToken = this.transport.registerConnectionListener(this.owner, connectionListener);
            }
            zStartServer = this.transport.startServer();
            this.serverStarted = zStartServer;
        } finally {
            if (!zStartServer) {
                releaseRegistrations();
            }
        }
        return zStartServer;
    }

    synchronized void stop() {
        releaseRegistrations();
        if (this.serverStarted) {
            this.serverStarted = false;
            this.transport.stopServer();
        }
    }

    private void releaseRegistrations() {
        this.transport.unregisterConnectionListener(this.connectionToken);
        this.connectionToken = null;
        this.transport.unregisterMessageListener(this.messageToken);
        this.messageToken = null;
    }
}
