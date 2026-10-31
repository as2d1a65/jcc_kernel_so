package com.android.support;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import com.android.support.ContinuousLogCapture;
import com.android.support.DiagnosticSnapshot;
import com.android.support.RuntimeLogExporter;
import java.util.Objects;

/* loaded from: classes.dex */
final class DiagnosticLogController {
    private final Capture capture;
    private final Context context;
    private final Exporter exporter;
    private final Host host;
    private final MainThread mainThread;
    private StatusSink statusSink;

    interface Capture {
        void start(ContinuousLogCapture.Host host);

        void stop();
    }

    interface Exporter {
        void export(Context context, RuntimeLogExporter.ResultCallback resultCallback);
    }

    interface Host {
        String execShell(String str);

        void showToast(String str);
    }

    interface MainThread {
        void post(Runnable runnable);
    }

    interface StatusSink {
        void update(String str, int i);
    }

    DiagnosticLogController(Context context, Host host) {
        this(context, host, createMainThread(), createCapture(), new Exporter() { // from class: com.android.support.DiagnosticLogController$$ExternalSyntheticLambda0
            @Override // com.android.support.DiagnosticLogController.Exporter
            public final void export(Context context2, RuntimeLogExporter.ResultCallback resultCallback) {
                RuntimeLogExporter.export(context2, resultCallback);
            }
        });
    }

    DiagnosticLogController(Context context, Host host, MainThread mainThread, Capture capture, Exporter exporter) {
        this.context = context;
        this.host = host;
        this.mainThread = mainThread;
        this.capture = capture;
        this.exporter = exporter;
    }

    void bindStatusView(final TextView textView) {
        bindStatusSink(textView == null ? null : new StatusSink() { // from class: com.android.support.DiagnosticLogController$$ExternalSyntheticLambda2
            @Override // com.android.support.DiagnosticLogController.StatusSink
            public final void update(String str, int i) {
                DiagnosticLogController.lambda$bindStatusView$0(textView, str, i);
            }
        });
    }

    static /* synthetic */ void lambda$bindStatusView$0(TextView textView, String str, int i) {
        textView.setText(str);
        textView.setTextColor(i);
    }

    void bindStatusSink(StatusSink statusSink) {
        this.statusSink = statusSink;
    }

    /* renamed from: com.android.support.DiagnosticLogController$1, reason: invalid class name */
    class AnonymousClass1 implements ContinuousLogCapture.Host {
        AnonymousClass1() {
        }

        @Override // com.android.support.ContinuousLogCapture.Host
        public String execShell(String str) {
            return DiagnosticLogController.this.host.execShell(str);
        }

        @Override // com.android.support.ContinuousLogCapture.Host
        public void updateStatus(final String str, final boolean z) {
            DiagnosticLogController.this.mainThread.post(new Runnable() { // from class: com.android.support.DiagnosticLogController$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m140x3b0ed3de(str, z);
                }
            });
        }

        /* renamed from: lambda$updateStatus$0$com-android-support-DiagnosticLogController$1, reason: not valid java name */
        /* synthetic */ void m140x3b0ed3de(String str, boolean z) {
            StatusSink statusSink = DiagnosticLogController.this.statusSink;
            if (statusSink != null) {
                statusSink.update(str, z ? GameHudPalette.SUCCESS : -7829368);
            }
        }

        @Override // com.android.support.ContinuousLogCapture.Host
        public String environmentSnapshot() {
            if (DiagnosticLogController.this.context == null) {
                return "";
            }
            Context context = DiagnosticLogController.this.context;
            final Host host = DiagnosticLogController.this.host;
            Objects.requireNonNull(host);
            return DiagnosticSnapshot.build(context, new DiagnosticSnapshot.ShellRunner() { // from class: com.android.support.DiagnosticLogController$1$$ExternalSyntheticLambda1
                @Override // com.android.support.DiagnosticSnapshot.ShellRunner
                public final String run(String str) {
                    return host.execShell(str);
                }
            });
        }
    }

    void startContinuousLog() {
        this.capture.start(new AnonymousClass1());
    }

    void stopContinuousLog() {
        this.capture.stop();
    }

    void exportRuntimeLog() {
        this.exporter.export(this.context, new RuntimeLogExporter.ResultCallback() { // from class: com.android.support.DiagnosticLogController$$ExternalSyntheticLambda3
            @Override // com.android.support.RuntimeLogExporter.ResultCallback
            public final void onResult(String str) {
                this.f$0.m139xa36bbe58(str);
            }
        });
    }

    /* renamed from: lambda$exportRuntimeLog$2$com-android-support-DiagnosticLogController, reason: not valid java name */
    /* synthetic */ void m139xa36bbe58(final String str) {
        this.mainThread.post(new Runnable() { // from class: com.android.support.DiagnosticLogController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m138x8cafbd7(str);
            }
        });
    }

    /* renamed from: lambda$exportRuntimeLog$1$com-android-support-DiagnosticLogController, reason: not valid java name */
    /* synthetic */ void m138x8cafbd7(String str) {
        try {
            this.host.showToast(str);
        } catch (Exception unused) {
        }
    }

    private static MainThread createMainThread() {
        final Handler handler = new Handler(Looper.getMainLooper());
        return new MainThread() { // from class: com.android.support.DiagnosticLogController$$ExternalSyntheticLambda4
            @Override // com.android.support.DiagnosticLogController.MainThread
            public final void post(Runnable runnable) {
                handler.post(runnable);
            }
        };
    }

    private static Capture createCapture() {
        final ContinuousLogCapture continuousLogCapture = new ContinuousLogCapture();
        return new Capture() { // from class: com.android.support.DiagnosticLogController.2
            @Override // com.android.support.DiagnosticLogController.Capture
            public void start(ContinuousLogCapture.Host host) {
                continuousLogCapture.start(host);
            }

            @Override // com.android.support.DiagnosticLogController.Capture
            public void stop() {
                continuousLogCapture.stop();
            }
        };
    }
}
