package com.android.support;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.support.OverlayPermissionGuidance;

/* loaded from: classes.dex */
final class OverlayPermissionDialog {

    interface Actions {
        void onContinueAnyway();

        void onOpenSystemSettings();

        void onRecheck();
    }

    private OverlayPermissionDialog() {
    }

    static AlertDialog show(Activity activity, OverlayPermissionGuidance.Guidance guidance, final boolean z, final Actions actions) {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(activity, 24), dp(activity, 20), dp(activity, 24), dp(activity, 20));
        linearLayout.setBackground(DsTokens.verticalGradient(activity, -1, -657673, DsTokens.WARN_A40, 8));
        TextView textView = new TextView(activity);
        textView.setText(guidance.title());
        textView.setTextColor(DsTokens.WARN);
        textView.setTextSize(2, 20.0f);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        linearLayout.addView(textView);
        View view = new View(activity);
        view.setBackgroundColor(DsTokens.LINE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, dp(activity, 1));
        layoutParams.topMargin = dp(activity, 16);
        linearLayout.addView(view, layoutParams);
        TextView textView2 = new TextView(activity);
        textView2.setText(guidance.message());
        textView2.setTextColor(DsTokens.TEXT_2);
        textView2.setTextSize(2, 15.0f);
        textView2.setLineSpacing(0.0f, 1.4f);
        textView2.setTextIsSelectable(true);
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setPadding(0, dp(activity, 16), 0, dp(activity, 16));
        scrollView.addView(textView2);
        ScrollEdgeCue.attach(scrollView);
        linearLayout.addView(scrollView, new LinearLayout.LayoutParams(-1, -2, 1.0f));
        Button buttonPrimaryButton = primaryButton(activity, guidance.primaryActionLabel());
        linearLayout.addView(buttonPrimaryButton, buttonParams(activity, 4));
        Button buttonSecondaryButton = secondaryButton(activity, "已开启，重新检测");
        linearLayout.addView(buttonSecondaryButton, buttonParams(activity, 8));
        Button buttonGhostButton = ghostButton(activity, z ? "仍要继续连接" : "知道了");
        linearLayout.addView(buttonGhostButton, buttonParams(activity, 8));
        final AlertDialog alertDialogCreate = new AlertDialog.Builder(activity, android.R.style.Theme.DeviceDefault.Dialog).setView(linearLayout).setCancelable(true).create();
        Window window = alertDialogCreate.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        buttonPrimaryButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OverlayPermissionDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                OverlayPermissionDialog.lambda$show$0(alertDialogCreate, actions, view2);
            }
        });
        buttonSecondaryButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OverlayPermissionDialog$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                OverlayPermissionDialog.lambda$show$1(alertDialogCreate, actions, view2);
            }
        });
        buttonGhostButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.OverlayPermissionDialog$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                OverlayPermissionDialog.lambda$show$2(alertDialogCreate, z, actions, view2);
            }
        });
        alertDialogCreate.show();
        boundToScreen(activity, window);
        return alertDialogCreate;
    }

    static /* synthetic */ void lambda$show$0(AlertDialog alertDialog, Actions actions, View view) {
        alertDialog.dismiss();
        actions.onOpenSystemSettings();
    }

    static /* synthetic */ void lambda$show$1(AlertDialog alertDialog, Actions actions, View view) {
        alertDialog.dismiss();
        actions.onRecheck();
    }

    static /* synthetic */ void lambda$show$2(AlertDialog alertDialog, boolean z, Actions actions, View view) {
        alertDialog.dismiss();
        if (z) {
            actions.onContinueAnyway();
        }
    }

    private static void boundToScreen(Activity activity, final Window window) {
        if (window == null) {
            return;
        }
        final int iMin = Math.min(dp(activity, 400), Math.round(ScreenUtils.getRealScreenWidth(activity) * 0.92f));
        final int iRound = Math.round(ScreenUtils.getRealScreenHeight(activity) * 0.9f);
        window.setLayout(iMin, -2);
        window.getDecorView().post(new Runnable() { // from class: com.android.support.OverlayPermissionDialog$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                OverlayPermissionDialog.lambda$boundToScreen$3(window, iRound, iMin);
            }
        });
    }

    static /* synthetic */ void lambda$boundToScreen$3(Window window, int i, int i2) {
        try {
            if (window.getDecorView().getHeight() > i) {
                window.setLayout(i2, i);
            }
        } catch (RuntimeException unused) {
        }
    }

    private static Button primaryButton(Activity activity, String str) {
        Button buttonBaseButton = baseButton(activity, str);
        buttonBaseButton.setTextColor(-1);
        buttonBaseButton.setBackground(DsTokens.primaryButton(activity, 8));
        return buttonBaseButton;
    }

    private static Button secondaryButton(Activity activity, String str) {
        Button buttonBaseButton = baseButton(activity, str);
        buttonBaseButton.setTextColor(DsTokens.TEXT_1);
        buttonBaseButton.setBackground(DsTokens.secondaryButton(activity, 8));
        return buttonBaseButton;
    }

    private static Button ghostButton(Activity activity, String str) {
        Button buttonBaseButton = baseButton(activity, str);
        buttonBaseButton.setTextColor(DsTokens.TEXT_3);
        buttonBaseButton.setBackground(DsTokens.outlineButton(activity, DsTokens.LINE, DsTokens.BRAND_A16, 8));
        return buttonBaseButton;
    }

    private static Button baseButton(Activity activity, String str) {
        Button button = new Button(activity);
        button.setText(str);
        button.setTextSize(2, 16.0f);
        button.setTypeface(null, 1);
        button.setAllCaps(false);
        button.setStateListAnimator(null);
        button.setPadding(0, 0, 0, 0);
        DsTokens.pressable(button);
        return button;
    }

    private static LinearLayout.LayoutParams buttonParams(Activity activity, int i) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, dp(activity, 48));
        layoutParams.topMargin = dp(activity, i);
        return layoutParams;
    }

    private static int dp(Activity activity, int i) {
        return ScreenUtils.dp2px(activity, i);
    }
}
