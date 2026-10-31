package com.android.support;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.support.SelfServiceUnbindClient;
import java.io.IOException;

/* loaded from: classes.dex */
final class VerifyOverlayUnbind {

    interface Host {
        boolean candidateBuild();

        boolean isAlive();

        Handler mainHandler();

        String submitLabel();

        String submittingLabel();

        WigVerify wigVerify();
    }

    private VerifyOverlayUnbind() {
    }

    static void bind(final Host host, final Button button, final EditText editText, final TextView textView, final TextView textView2, final TextView textView3, final ProgressBar progressBar) {
        final boolean[] zArr = {false};
        final Runnable runnable = new Runnable() { // from class: com.android.support.VerifyOverlayUnbind$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VerifyOverlayUnbind.lambda$bind$0(zArr, textView2, host);
            }
        };
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyOverlayUnbind$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VerifyOverlayUnbind.lambda$bind$4(host, button, runnable, textView3, editText, zArr, textView2, textView, progressBar, view);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(boolean[] zArr, TextView textView, Host host) {
        zArr[0] = false;
        textView.setText(host.submitLabel());
    }

    static /* synthetic */ void lambda$bind$4(final Host host, final Button button, final Runnable runnable, final TextView textView, final EditText editText, boolean[] zArr, final TextView textView2, final TextView textView3, final ProgressBar progressBar, View view) {
        if (host.isAlive()) {
            if (!button.isEnabled()) {
                host.mainHandler().removeCallbacks(runnable);
                runnable.run();
                textView.setText("请等当前操作完成后再解除旧设备");
                textView.setTextColor(DsTokens.WARN);
                return;
            }
            if (CandidateBusinessEndpointPolicy.rejectCandidateBuild(host.candidateBuild(), new Runnable() { // from class: com.android.support.VerifyOverlayUnbind$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    VerifyOverlayUnbind.lambda$bind$1(host, runnable, textView);
                }
            })) {
                return;
            }
            try {
                final String strNormalizeCard = SelfServiceUnbindClient.normalizeCard(editText.getText().toString());
                if (!zArr[0]) {
                    zArr[0] = true;
                    textView2.setText("再点一次确认解除");
                    textView.setText("将解除该使用码绑定的旧设备，确认请再点一次");
                    textView.setTextColor(DsTokens.WARN);
                    host.mainHandler().postDelayed(runnable, 4000L);
                    return;
                }
                host.mainHandler().removeCallbacks(runnable);
                runnable.run();
                textView2.setEnabled(false);
                button.setEnabled(false);
                editText.setEnabled(false);
                textView3.setEnabled(false);
                progressBar.setVisibility(0);
                textView.setText(host.submittingLabel());
                textView.setTextColor(DsTokens.BRAND);
                new Thread(new Runnable() { // from class: com.android.support.VerifyOverlayUnbind$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        VerifyOverlayUnbind.lambda$bind$3(strNormalizeCard, host, progressBar, textView2, button, editText, textView3, textView);
                    }
                }, "VerifyOverlayUnbind").start();
            } catch (IllegalArgumentException e) {
                host.mainHandler().removeCallbacks(runnable);
                runnable.run();
                textView.setText(e.getMessage());
                textView.setTextColor(DsTokens.DANGER);
            }
        }
    }

    static /* synthetic */ void lambda$bind$1(Host host, Runnable runnable, TextView textView) {
        host.mainHandler().removeCallbacks(runnable);
        runnable.run();
        textView.setText("候选版已关闭在线更换设备");
        textView.setTextColor(DsTokens.WARN);
    }

    static /* synthetic */ void lambda$bind$3(String str, final Host host, final ProgressBar progressBar, final TextView textView, final Button button, final EditText editText, final TextView textView2, final TextView textView3) {
        String strFailureMessage;
        boolean zIsSuccess;
        try {
            SelfServiceUnbindClient.Result resultUnbind = new SelfServiceUnbindClient().unbind(str);
            zIsSuccess = resultUnbind.isSuccess();
            strFailureMessage = resultUnbind.getMessage();
        } catch (IOException | RuntimeException e) {
            strFailureMessage = SelfServiceUnbindClient.failureMessage(e);
            zIsSuccess = false;
        }
        final String str2 = strFailureMessage;
        final boolean z = zIsSuccess;
        host.mainHandler().post(new Runnable() { // from class: com.android.support.VerifyOverlayUnbind$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                VerifyOverlayUnbind.lambda$bind$2(host, progressBar, textView, button, editText, textView2, textView3, z, str2);
            }
        });
    }

    static /* synthetic */ void lambda$bind$2(Host host, ProgressBar progressBar, TextView textView, Button button, EditText editText, TextView textView2, TextView textView3, boolean z, String str) {
        if (host.isAlive()) {
            progressBar.setVisibility(8);
            textView.setEnabled(true);
            button.setEnabled(true);
            editText.setEnabled(true);
            textView2.setEnabled(true);
            textView3.setText((z ? "✓ " : "✗ ") + str);
            textView3.setTextColor(z ? DsTokens.OK : DsTokens.DANGER);
            WigVerify wigVerify = host.wigVerify();
            if (!z || wigVerify == null) {
                return;
            }
            wigVerify.clearSavedCard();
        }
    }
}
