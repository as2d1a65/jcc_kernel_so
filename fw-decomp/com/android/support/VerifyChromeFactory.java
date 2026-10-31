package com.android.support;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
final class VerifyChromeFactory {

    interface Host {
        int dpVerify(int i);
    }

    private VerifyChromeFactory() {
    }

    static LinearLayout step(Context context, Host host, String str, String str2, int i) {
        int i2 = i < 0 ? DsTokens.OK : i == 0 ? DsTokens.BRAND : DsTokens.TEXT_3;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        linearLayout.setPadding(host.dpVerify(4), host.dpVerify(8), host.dpVerify(4), host.dpVerify(8));
        if (i == 0) {
            linearLayout.setBackground(DsTokens.shape(context, DsTokens.BRAND_A16, 0, 8));
        }
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(i2);
        textView.setTextSize(10.0f);
        textView.setTypeface(Typeface.MONOSPACE);
        textView.setLetterSpacing(0.0f);
        textView.setGravity(17);
        TextView textView2 = new TextView(context);
        if (i < 0) {
            str2 = str2 + " ✓";
        }
        textView2.setText(str2);
        textView2.setTextColor(i2);
        textView2.setTextSize(11.0f);
        textView2.setTypeface(null, 1);
        textView2.setGravity(17);
        textView2.setPadding(0, host.dpVerify(4), 0, 0);
        linearLayout.addView(textView);
        linearLayout.addView(textView2);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        return linearLayout;
    }

    static LinearLayout card(Context context, Host host) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int iDpVerify = host.dpVerify(12);
        linearLayout.setPadding(iDpVerify, iDpVerify, iDpVerify, iDpVerify);
        linearLayout.setBackground(DsTokens.shape(context, -1, DsTokens.LINE, 8));
        return linearLayout;
    }

    static StateListDrawable inputBackground(Context context) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, DsTokens.shape(context, DsTokens.SURFACE_2, DsTokens.BRAND, 8));
        stateListDrawable.addState(new int[0], DsTokens.shape(context, DsTokens.SURFACE_3, DsTokens.LINE, 8));
        return stateListDrawable;
    }

    static TextView linkButton(Context context, Host host, String str, View.OnClickListener onClickListener) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(DsTokens.TEXT_2);
        textView.setTextSize(11.0f);
        textView.setGravity(17);
        textView.setMinHeight(host.dpVerify(44));
        textView.setBackground(DsTokens.outlineButton(context, DsTokens.LINE, DsTokens.BRAND_A16, 8));
        textView.setOnClickListener(onClickListener);
        DsTokens.pressable(textView);
        return textView;
    }

    static LinearLayout fieldLabel(Context context, Host host, String str, int i) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(0, 0, 0, host.dpVerify(8));
        View view = new View(context);
        view.setBackground(DsTokens.accentBar(context, i));
        linearLayout.addView(view, new LinearLayout.LayoutParams(host.dpVerify(3), host.dpVerify(12)));
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(i);
        textView.setTextSize(11.0f);
        textView.setTypeface(null, 1);
        textView.setLetterSpacing(0.0f);
        textView.setPadding(host.dpVerify(8), 0, 0, 0);
        linearLayout.addView(textView);
        return linearLayout;
    }
}
