package com.android.support;

import com.android.support.IpcV2ActionCompletion;
import com.android.support.LocalSocketIPC;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcActionSender {
    static final long ACTION_TTL_MS = 5000;
    static final long REQUEST_TIMEOUT_MS = 6000;
    private final IpcV2ActionCompletion.Dispatcher callbackDispatcher;
    private final Clock clock;
    private final MatchGenerationSource matchGenerationSource;
    private final Transport transport;
    private final WarningSink warningSink;

    interface Clock {
        long elapsedRealtime();
    }

    interface MatchGenerationSource {
        String currentMatchGeneration();
    }

    interface Transport {
        LocalSocketIPC.SendResult sendRequest(String str, JSONObject jSONObject, long j, LocalSocketIPC.RequestCallback requestCallback);
    }

    interface WarningSink {
        void warn(String str, Throwable th);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    IpcActionSender(final LocalSocketIPC localSocketIPC, MatchGenerationSource matchGenerationSource, Clock clock, IpcV2ActionCompletion.Dispatcher dispatcher, WarningSink warningSink) {
        this(new Transport() { // from class: com.android.support.IpcActionSender$$ExternalSyntheticLambda0
            @Override // com.android.support.IpcActionSender.Transport
            public final LocalSocketIPC.SendResult sendRequest(String str, JSONObject jSONObject, long j, LocalSocketIPC.RequestCallback requestCallback) {
                return localSocketIPC.sendRequest(str, jSONObject, j, requestCallback);
            }
        }, matchGenerationSource, clock, dispatcher, warningSink);
        Objects.requireNonNull(localSocketIPC);
    }

    IpcActionSender(Transport transport, MatchGenerationSource matchGenerationSource, Clock clock, IpcV2ActionCompletion.Dispatcher dispatcher, WarningSink warningSink) {
        this.transport = transport;
        this.matchGenerationSource = matchGenerationSource;
        this.clock = clock;
        this.callbackDispatcher = dispatcher;
        this.warningSink = warningSink;
    }

    void send(String str, JSONObject jSONObject, final Runnable runnable) {
        sendWithTerminal(str, jSONObject, runnable == null ? null : new IpcV2ActionCompletion.TerminalCallback() { // from class: com.android.support.IpcActionSender$$ExternalSyntheticLambda2
            @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
            public final void onTerminal(boolean z, String str2) {
                IpcActionSender.lambda$send$0(runnable, z, str2);
            }
        });
    }

    static /* synthetic */ void lambda$send$0(Runnable runnable, boolean z, String str) {
        if (z) {
            runnable.run();
        }
    }

    LocalSocketIPC.SendResult sendWithTerminal(final String str, JSONObject jSONObject, final IpcV2ActionCompletion.TerminalCallback terminalCallback) throws JSONException {
        String strCurrentMatchGeneration = this.matchGenerationSource.currentMatchGeneration();
        if (strCurrentMatchGeneration == null || strCurrentMatchGeneration.length() != 22) {
            this.warningSink.warn(str + " rejected locally: no active match generation", null);
            dispatch(terminalCallback, false, "no_active_match_generation");
            return LocalSocketIPC.SendResult.NOT_READY;
        }
        try {
            jSONObject.put("matchGeneration", strCurrentMatchGeneration);
            jSONObject.put("createdBootMs", this.clock.elapsedRealtime());
            jSONObject.put("ttlMs", ACTION_TTL_MS);
            return this.transport.sendRequest(str, jSONObject, REQUEST_TIMEOUT_MS, new LocalSocketIPC.RequestCallback() { // from class: com.android.support.IpcActionSender$$ExternalSyntheticLambda1
                @Override // com.android.support.LocalSocketIPC.RequestCallback
                public final void onTerminal(LocalSocketIPC.TerminalResult terminalResult) {
                    this.f$0.m1208lambda$sendWithTerminal$1$comandroidsupportIpcActionSender(terminalCallback, str, terminalResult);
                }
            });
        } catch (Exception e) {
            this.warningSink.warn(str + " rejected locally: invalid action parameters", e);
            dispatch(terminalCallback, false, "invalid_action_params");
            return LocalSocketIPC.SendResult.INVALID;
        }
    }

    /* renamed from: lambda$sendWithTerminal$1$com-android-support-IpcActionSender, reason: not valid java name */
    /* synthetic */ void m1208lambda$sendWithTerminal$1$comandroidsupportIpcActionSender(IpcV2ActionCompletion.TerminalCallback terminalCallback, String str, LocalSocketIPC.TerminalResult terminalResult) {
        dispatch(terminalCallback, terminalResult.isSuccess(), terminalResult.code());
        if (terminalResult.isSuccess()) {
            return;
        }
        this.warningSink.warn(str + " ended with " + terminalResult.code(), null);
    }

    void dispatch(IpcV2ActionCompletion.TerminalCallback terminalCallback, boolean z, String str) {
        IpcV2ActionCompletion.deliverTerminal(z, str, terminalCallback, this.callbackDispatcher);
    }
}
