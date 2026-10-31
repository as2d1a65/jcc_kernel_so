package com.android.support;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.support.diagnostics.DiagnosticCoordinator;

/* loaded from: classes.dex */
final class DiagnosticSupportControlBinder {

    interface Host {
        void confirmDeep();

        void copySupportCode(String str);

        void stopDiagnostics();
    }

    static final class Views {
        final TextView code;
        final Button confirm;
        final Button copy;
        final TextView quota;
        final TextView status;
        final Button stop;

        Views(View view) {
            this.code = (TextView) view.findViewById(R.id.tvDiagnosticSupportCode);
            this.status = (TextView) view.findViewById(R.id.tvDiagnosticSupportStatus);
            this.quota = (TextView) view.findViewById(R.id.tvDiagnosticSupportQuota);
            this.copy = (Button) view.findViewById(R.id.btnCopyDiagnosticSupportCode);
            this.confirm = (Button) view.findViewById(R.id.btnConfirmDiagnosticDeep);
            this.stop = (Button) view.findViewById(R.id.btnStopDiagnosticSupport);
        }
    }

    private DiagnosticSupportControlBinder() {
    }

    static Views bind(View view, final Host host) {
        if (view == null || host == null) {
            return null;
        }
        final Views views = new Views(view);
        if (views.copy != null) {
            views.copy.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.DiagnosticSupportControlBinder$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    DiagnosticSupportControlBinder.lambda$bind$0(views, host, view2);
                }
            });
        }
        if (views.confirm != null) {
            views.confirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.DiagnosticSupportControlBinder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.confirmDeep();
                }
            });
        }
        if (views.stop != null) {
            views.stop.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.DiagnosticSupportControlBinder$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    host.stopDiagnostics();
                }
            });
        }
        return views;
    }

    static /* synthetic */ void lambda$bind$0(Views views, Host host, View view) {
        CharSequence text = views.code == null ? "" : views.code.getText();
        host.copySupportCode(text != null ? text.toString() : "");
    }

    static void render(Views views, DiagnosticCoordinator.Snapshot snapshot) {
        if (views == null) {
            return;
        }
        DiagnosticCoordinator.Status status = snapshot == null ? DiagnosticCoordinator.Status.OFF : snapshot.status;
        String str = "";
        String str2 = snapshot == null ? "" : snapshot.supportCode;
        boolean z = snapshot != null && snapshot.deep;
        long j = snapshot == null ? 0L : snapshot.expiresAt;
        int i = snapshot == null ? 0 : snapshot.remainingMatches;
        long j2 = snapshot != null ? snapshot.remainingBytes : 0L;
        if (snapshot != null) {
            str = snapshot.errorCode;
        }
        DiagnosticSupportUiModel diagnosticSupportUiModelCreate = DiagnosticSupportUiModel.create(status, str2, z, j, i, j2, str);
        setTextIfChanged(views.code, diagnosticSupportUiModelCreate.supportCode);
        if (views.status != null) {
            setTextIfChanged(views.status, diagnosticSupportUiModelCreate.status);
            views.status.setTextColor(diagnosticSupportUiModelCreate.statusColor);
        }
        setTextIfChanged(views.quota, diagnosticSupportUiModelCreate.quota);
        if (views.copy != null) {
            views.copy.setEnabled(diagnosticSupportUiModelCreate.copyEnabled);
        }
        if (views.confirm != null) {
            views.confirm.setVisibility(diagnosticSupportUiModelCreate.showConfirm ? 0 : 8);
        }
        if (views.stop != null) {
            views.stop.setVisibility(diagnosticSupportUiModelCreate.showStop ? 0 : 8);
        }
    }

    private static void setTextIfChanged(TextView textView, CharSequence charSequence) {
        if (textView == null) {
            return;
        }
        CharSequence text = textView.getText();
        if (text == null) {
            if (charSequence == null) {
                return;
            }
        } else if (charSequence != null && text.length() == charSequence.length() && text.toString().contentEquals(charSequence)) {
            return;
        }
        textView.setText(charSequence);
    }
}
