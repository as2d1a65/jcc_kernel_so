package com.android.support;

/* loaded from: classes.dex */
public final class IpcV2ActionCompletion {

    public interface Dispatcher {
        void post(Runnable runnable);
    }

    public interface TerminalCallback {
        void onTerminal(boolean z, String str);
    }

    private IpcV2ActionCompletion() {
    }

    public static boolean deliver(boolean z, String str, Runnable runnable, Dispatcher dispatcher) {
        if (!z || runnable == null || dispatcher == null) {
            return false;
        }
        dispatcher.post(runnable);
        return true;
    }

    public static boolean deliverTerminal(final boolean z, final String str, final TerminalCallback terminalCallback, Dispatcher dispatcher) {
        if (terminalCallback == null || dispatcher == null) {
            return false;
        }
        if (str == null || str.isEmpty()) {
            str = z ? "" : "unknown_error";
        }
        dispatcher.post(new Runnable() { // from class: com.android.support.IpcV2ActionCompletion$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                terminalCallback.onTerminal(z, str);
            }
        });
        return true;
    }
}
