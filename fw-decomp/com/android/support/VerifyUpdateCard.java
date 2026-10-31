package com.android.support;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.support.WigVerify;

/* loaded from: classes.dex */
final class VerifyUpdateCard {

    interface Host {
        String currentVersionName();

        int dpVerify(int i);

        void exitAppFromForceUpdate();

        void openUpdateUrl(String str);
    }

    private VerifyUpdateCard() {
    }

    static void showForce(Context context, View view, LinearLayout linearLayout, LinearLayout linearLayout2, Button button, EditText editText, TextView textView, View view2, final WigVerify.UpdateInfo updateInfo, final Host host) {
        button.setVisibility(8);
        editText.setVisibility(8);
        view2.setVisibility(8);
        view.setVisibility(8);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            linearLayout.getChildAt(i).setVisibility(8);
        }
        for (int i2 = 0; i2 < linearLayout2.getChildCount(); i2++) {
            View childAt = linearLayout2.getChildAt(i2);
            if (childAt != textView) {
                childAt.setVisibility(8);
            }
        }
        textView.setVisibility(0);
        textView.setText("版本过旧，必须更新才能使用");
        textView.setTextColor(DsTokens.DANGER);
        textView.setTextSize(13.0f);
        TextView textView2 = new TextView(context);
        textView2.setText("v" + host.currentVersionName() + "  →  v" + updateInfo.appVersion);
        textView2.setTextColor(DsTokens.TEXT_2);
        textView2.setTextSize(11.0f);
        textView2.setTypeface(Typeface.MONOSPACE);
        textView2.setGravity(17);
        textView2.setPadding(0, host.dpVerify(8), 0, host.dpVerify(12));
        linearLayout2.addView(textView2);
        Button button2 = new Button(context);
        button2.setText("立即更新");
        button2.setTextColor(-1);
        button2.setTextSize(16.0f);
        button2.setTypeface(null, 1);
        button2.setAllCaps(false);
        button2.setStateListAnimator(null);
        button2.setBackground(DsTokens.withRipple(context, DsTokens.shape(context, DsTokens.DANGER, 0, 8), DsTokens.RIPPLE_DARK, 8));
        button2.setPadding(0, 0, 0, 0);
        button2.setElevation(host.dpVerify(4));
        DsTokens.pressable(button2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, host.dpVerify(52));
        layoutParams.topMargin = host.dpVerify(8);
        linearLayout2.addView(button2, layoutParams);
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyUpdateCard$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                host.openUpdateUrl(updateInfo.updateUrl);
            }
        });
        TextView textView3 = new TextView(context);
        textView3.setText("退出应用");
        textView3.setTextColor(DsTokens.TEXT_3);
        textView3.setTextSize(13.0f);
        textView3.setGravity(17);
        textView3.setMinHeight(host.dpVerify(44));
        DsTokens.pressable(textView3);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams2.topMargin = host.dpVerify(4);
        linearLayout2.addView(textView3, layoutParams2);
        textView3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyUpdateCard$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                host.exitAppFromForceUpdate();
            }
        });
    }

    static void showOptional(TextView textView, final WigVerify.UpdateInfo updateInfo, final Host host) {
        textView.setText("发现新版本 v" + updateInfo.appVersion + " (当前 v" + host.currentVersionName() + ")");
        textView.setTextColor(-16722689);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyUpdateCard$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.openUpdateUrl(updateInfo.updateUrl);
            }
        });
    }
}
