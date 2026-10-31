package com.android.support;

import android.content.Context;
import android.os.SystemClock;
import com.android.support.LocalSocketIPC;
import com.android.support.trust.NativeTrustBridge;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class NativeSpecifiedCompute {
    private static final SpecifiedComputeScheduler SCHEDULER = new SpecifiedComputeScheduler();

    static /* synthetic */ void lambda$retry$3(LocalSocketIPC.TerminalResult terminalResult) {
    }

    static void relay(final Context context, JSONObject jSONObject) {
        final String strOptString = jSONObject.optString("payload", "");
        if (strOptString.isEmpty() || strOptString.length() > 262144) {
            return;
        }
        try {
            final JSONObject jSONObject2 = new JSONObject(strOptString);
            final String strOptString2 = jSONObject2.getJSONObject("query").optString("op");
            if (ComputeOperationPolicy.allows(strOptString2, false)) {
                final long jElapsedRealtime = SystemClock.elapsedRealtime();
                SCHEDULER.execute(strOptString2, new Runnable() { // from class: com.android.support.NativeSpecifiedCompute$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() throws JSONException {
                        NativeSpecifiedCompute.lambda$relay$1(jElapsedRealtime, context, strOptString, strOptString2, jSONObject2);
                    }
                }, new Runnable() { // from class: com.android.support.NativeSpecifiedCompute$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() throws JSONException {
                        NativeSpecifiedCompute.retry(jSONObject2);
                    }
                });
            }
        } catch (JSONException unused) {
        }
    }

    static /* synthetic */ void lambda$relay$1(long j, Context context, String str, String str2, final JSONObject jSONObject) throws JSONException {
        SystemClock.elapsedRealtime();
        try {
            try {
                if (SystemClock.elapsedRealtime() - j > 1000) {
                    return;
                }
                byte[] bArrNativeSpecifiedCompute = NativeTrustBridge.nativeSpecifiedCompute(str.getBytes(StandardCharsets.UTF_8));
                if (bArrNativeSpecifiedCompute == null) {
                    return;
                }
                byte[] bArrNativeVerifySpecifiedCompute = NativeTrustBridge.nativeVerifySpecifiedCompute(bArrNativeSpecifiedCompute, JccGatewayClient.shared().computeSpecified(bArrNativeSpecifiedCompute));
                if (bArrNativeVerifySpecifiedCompute != null && bArrNativeVerifySpecifiedCompute.length <= 786432) {
                    final JSONObject jSONObject2 = new JSONObject(new String(bArrNativeVerifySpecifiedCompute, StandardCharsets.UTF_8));
                    LocalSocketIPC.getInstance().sendRequest(str2.equals("runtime_policy") ? "runtime.policy.result" : "specified.compute.result", jSONObject2, 1000L, new LocalSocketIPC.RequestCallback() { // from class: com.android.support.NativeSpecifiedCompute$$ExternalSyntheticLambda3
                        @Override // com.android.support.LocalSocketIPC.RequestCallback
                        public final void onTerminal(LocalSocketIPC.TerminalResult terminalResult) throws JSONException {
                            NativeSpecifiedCompute.lambda$relay$0(jSONObject2, jSONObject, terminalResult);
                        }
                    });
                }
            } finally {
                retry(jSONObject);
            }
        } catch (Exception | LinkageError unused) {
        }
    }

    static /* synthetic */ void lambda$relay$0(JSONObject jSONObject, JSONObject jSONObject2, LocalSocketIPC.TerminalResult terminalResult) throws JSONException {
        if (terminalResult.isSuccess()) {
            return;
        }
        retry(jSONObject2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void retry(JSONObject jSONObject) throws JSONException {
        try {
            JSONObject jSONObject2 = new JSONObject();
            String[] strArr = {"request_id", "match", "ipc_epoch", "region"};
            for (int i = 0; i < 4; i++) {
                String str = strArr[i];
                jSONObject2.put(str, jSONObject.get(str));
            }
            jSONObject2.put("valid_for_ms", 1500);
            jSONObject2.put("retryable_failure", true);
            LocalSocketIPC.getInstance().sendRequest("runtime_policy".equals(jSONObject.getJSONObject("query").optString("op")) ? "runtime.policy.result" : "specified.compute.result", jSONObject2, 1000L, new LocalSocketIPC.RequestCallback() { // from class: com.android.support.NativeSpecifiedCompute$$ExternalSyntheticLambda2
                @Override // com.android.support.LocalSocketIPC.RequestCallback
                public final void onTerminal(LocalSocketIPC.TerminalResult terminalResult) {
                    NativeSpecifiedCompute.lambda$retry$3(terminalResult);
                }
            });
        } catch (Exception unused) {
        }
    }

    private NativeSpecifiedCompute() {
    }
}
