package com.android.support;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.view.GravityCompat;

/* loaded from: classes.dex */
final class VerifyOverlayForm {

    interface Host {
        void copyDeviceId();

        int dpVerify(int i);

        void openPurchaseUrl();

        void pasteSavedCard(EditText editText);

        void showKeyboard(EditText editText);

        String unbindAccessibility();

        String unbindSubmitLabel();

        LinearLayout verifyCard();

        LinearLayout verifyFieldLabel(String str, int i);

        StateListDrawable verifyInputBackground();
    }

    static final class Built {
        final Button btnLogin;
        final TextView btnPaste;
        final TextView btnUnbindOld;
        final EditText etCardKey;
        final LinearLayout inputArea;
        final LinearLayout keyCard;
        final ProgressBar progressBar;
        final LinearLayout secondaryRow;
        final TextView tvStatus;

        Built(LinearLayout linearLayout, LinearLayout linearLayout2, EditText editText, TextView textView, TextView textView2, ProgressBar progressBar, Button button, LinearLayout linearLayout3, TextView textView3) {
            this.inputArea = linearLayout;
            this.keyCard = linearLayout2;
            this.etCardKey = editText;
            this.btnPaste = textView;
            this.tvStatus = textView2;
            this.progressBar = progressBar;
            this.btnLogin = button;
            this.secondaryRow = linearLayout3;
            this.btnUnbindOld = textView3;
        }
    }

    private VerifyOverlayForm() {
    }

    static Built build(Context context, final Host host, VerifyWindowSpec verifyWindowSpec, LinearLayout linearLayout, LinearLayout linearLayout2, LinearLayout linearLayout3, String str, String str2) {
        LinearLayout linearLayout4 = new LinearLayout(context);
        linearLayout4.setOrientation(1);
        linearLayout4.setPadding(0, ScreenUtils.dp2px(context, 8.0f), 0, 0);
        linearLayout.addView(linearLayout4, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout linearLayoutVerifyCard = host.verifyCard();
        linearLayoutVerifyCard.setBackground(DsTokens.shape(context, -1, DsTokens.LINE, 16));
        int iDpVerify = host.dpVerify(16);
        linearLayoutVerifyCard.setPadding(iDpVerify, iDpVerify, iDpVerify, iDpVerify);
        TextView textView = new TextView(context);
        textView.setText("验证后，即可开始使用");
        textView.setTag("verify_optional_intro");
        textView.setVisibility(verifyWindowSpec.compact ? 8 : 0);
        textView.setTextColor(DsTokens.TEXT_1);
        textView.setTextSize(18.0f);
        textView.setTypeface(null, 1);
        textView.setPadding(0, 0, 0, host.dpVerify(16));
        linearLayoutVerifyCard.addView(textView);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = 0;
        linearLayout2.addView(linearLayoutVerifyCard, layoutParams);
        linearLayoutVerifyCard.addView(host.verifyFieldLabel("使用码", DsTokens.BRAND));
        LinearLayout linearLayout5 = new LinearLayout(context);
        linearLayout5.setOrientation(0);
        linearLayout5.setGravity(16);
        linearLayoutVerifyCard.addView(linearLayout5, new LinearLayout.LayoutParams(-1, -2));
        final EditText editText = new EditText(context);
        editText.setHint("请输入您的使用码");
        editText.setHintTextColor(DsTokens.TEXT_3);
        editText.setTextColor(DsTokens.TEXT_1);
        editText.setTextSize(15.0f);
        editText.setSingleLine(true);
        editText.setInputType(524289);
        editText.setImeOptions(268435462);
        editText.setImportantForAutofill(2);
        editText.setMinHeight(host.dpVerify(48));
        editText.setPadding(host.dpVerify(12), 0, host.dpVerify(12), 0);
        editText.setBackground(host.verifyInputBackground());
        linearLayout5.addView(editText, new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView textView2 = new TextView(context);
        textView2.setText("粘贴");
        textView2.setTextColor(DsTokens.TEXT_1);
        textView2.setTextSize(13.0f);
        textView2.setBackground(DsTokens.secondaryButton(context, 8));
        textView2.setMinWidth(host.dpVerify(58));
        textView2.setMinHeight(host.dpVerify(48));
        textView2.setGravity(17);
        textView2.setPadding(host.dpVerify(12), 0, host.dpVerify(12), 0);
        DsTokens.pressable(textView2);
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyOverlayForm$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.pasteSavedCard(editText);
            }
        });
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams2.setMarginStart(ScreenUtils.dp2px(context, 6.0f));
        linearLayout5.addView(textView2, layoutParams2);
        if (str != null && !str.isEmpty()) {
            editText.setText(str);
            if (!verifyWindowSpec.compact) {
                TextView textView3 = new TextView(context);
                textView3.setText("已保存使用码");
                textView3.setTextColor(DsTokens.OK);
                textView3.setTextSize(11.0f);
                textView3.setPadding(0, host.dpVerify(8), 0, 0);
                linearLayoutVerifyCard.addView(textView3);
            }
        }
        editText.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyOverlayForm$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VerifyOverlayForm.lambda$build$1(editText, host, view);
            }
        });
        LinearLayout linearLayoutVerifyCard2 = host.verifyCard();
        linearLayoutVerifyCard2.setBackground(DsTokens.shape(context, -1, DsTokens.LINE, 12));
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams3.topMargin = ScreenUtils.dp2px(context, 6.0f);
        linearLayout4.addView(linearLayoutVerifyCard2, layoutParams3);
        linearLayoutVerifyCard2.addView(host.verifyFieldLabel("本机编号", DsTokens.ACCENT));
        LinearLayout linearLayout6 = new LinearLayout(context);
        linearLayout6.setOrientation(0);
        linearLayout6.setGravity(16);
        linearLayoutVerifyCard2.addView(linearLayout6);
        TextView textView4 = new TextView(context);
        textView4.setText(str2);
        textView4.setTextColor(DsTokens.TEXT_2);
        textView4.setTextSize(12.0f);
        textView4.setTypeface(Typeface.MONOSPACE);
        textView4.setSingleLine(true);
        textView4.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        textView4.setMinHeight(host.dpVerify(48));
        textView4.setGravity(16);
        textView4.setPadding(host.dpVerify(8), 0, host.dpVerify(8), 0);
        textView4.setBackground(DsTokens.shape(context, DsTokens.SURFACE_3, DsTokens.LINE, 8));
        linearLayout6.addView(textView4, new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView textView5 = new TextView(context);
        textView5.setText("复制");
        textView5.setTextColor(DsTokens.TEXT_1);
        textView5.setTextSize(13.0f);
        textView5.setBackground(DsTokens.secondaryButton(context, 8));
        textView5.setMinWidth(host.dpVerify(58));
        textView5.setMinHeight(host.dpVerify(48));
        textView5.setGravity(17);
        textView5.setPadding(host.dpVerify(12), 0, host.dpVerify(12), 0);
        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams4.setMarginStart(host.dpVerify(8));
        textView5.setLayoutParams(layoutParams4);
        DsTokens.pressable(textView5);
        textView5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyOverlayForm$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.copyDeviceId();
            }
        });
        linearLayout6.addView(textView5);
        TextView textView6 = new TextView(context);
        textView6.setTextColor(DsTokens.OK);
        textView6.setTextSize(13.0f);
        textView6.setGravity(GravityCompat.START);
        textView6.setLineSpacing(0.0f, 1.35f);
        textView6.setAccessibilityLiveRegion(1);
        textView6.setPadding(0, host.dpVerify(verifyWindowSpec.compact ? 8 : 12), 0, host.dpVerify(4));
        linearLayout2.addView(textView6);
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setVisibility(8);
        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(ScreenUtils.dp2px(context, 24.0f), ScreenUtils.dp2px(context, 24.0f));
        layoutParams5.gravity = 17;
        linearLayout2.addView(progressBar, layoutParams5);
        final Button button = new Button(context);
        button.setText("开始验证");
        button.setTextColor(-1);
        button.setTextSize(16.0f);
        button.setTypeface(null, 1);
        button.setLetterSpacing(0.0f);
        button.setBackground(DsTokens.primaryButton(context, 8));
        button.setPadding(0, 0, 0, 0);
        button.setAllCaps(false);
        button.setStateListAnimator(null);
        button.setElevation(0.0f);
        DsTokens.pressable(button);
        LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(-1, -2);
        button.setMinHeight(host.dpVerify(52));
        button.setPadding(host.dpVerify(16), host.dpVerify(10), host.dpVerify(16), host.dpVerify(10));
        layoutParams6.topMargin = 0;
        linearLayout3.addView(button, layoutParams6);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.android.support.VerifyOverlayForm$$ExternalSyntheticLambda3
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView7, int i, KeyEvent keyEvent) {
                return VerifyOverlayForm.lambda$build$3(button, textView7, i, keyEvent);
            }
        });
        TextView textView7 = new TextView(context);
        textView7.setText("获取使用码");
        textView7.setTextColor(DsTokens.BRAND);
        textView7.setTextSize(13.0f);
        textView7.setGravity(17);
        textView7.setMinHeight(host.dpVerify(48));
        textView7.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyOverlayForm$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.openPurchaseUrl();
            }
        });
        DsTokens.pressable(textView7);
        LinearLayout linearLayout7 = new LinearLayout(context);
        linearLayout7.setOrientation(0);
        LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams7.topMargin = host.dpVerify(8);
        linearLayout2.addView(linearLayout7, layoutParams7);
        LinearLayout.LayoutParams layoutParams8 = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams8.setMarginEnd(host.dpVerify(8));
        linearLayout7.addView(textView7, layoutParams8);
        TextView textView8 = new TextView(context);
        textView8.setText(host.unbindSubmitLabel());
        textView8.setTextColor(DsTokens.TEXT_2);
        textView8.setTextSize(13.0f);
        textView8.setTypeface(null, 1);
        textView8.setGravity(17);
        textView8.setMinHeight(host.dpVerify(48));
        textView8.setContentDescription(host.unbindAccessibility());
        textView8.setBackground(DsTokens.withRipple(context, DsTokens.shape(context, 0, 0, 8), DsTokens.DANGER_A16, 8));
        DsTokens.pressable(textView8);
        linearLayout7.addView(textView8, new LinearLayout.LayoutParams(0, -2, 1.0f));
        return new Built(linearLayout4, linearLayoutVerifyCard, editText, textView2, textView6, progressBar, button, linearLayout7, textView8);
    }

    static /* synthetic */ void lambda$build$1(EditText editText, Host host, View view) {
        editText.requestFocus();
        host.showKeyboard(editText);
    }

    static /* synthetic */ boolean lambda$build$3(Button button, TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 || !button.isEnabled()) {
            return false;
        }
        button.performClick();
        return true;
    }
}
