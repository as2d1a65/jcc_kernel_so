package com.android.support;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
final class VerifyOfficialRow {

    interface Host {
        int dpVerify(int i);

        void openOfficialUrl(String str);

        TextView verifyLinkButton(String str, View.OnClickListener onClickListener);
    }

    private VerifyOfficialRow() {
    }

    static LinearLayout add(Context context, final Host host, LinearLayout linearLayout) {
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setPadding(0, host.dpVerify(8), 0, host.dpVerify(4));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.setMarginEnd(ScreenUtils.dp2px(context, 6.0f));
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams2.setMarginEnd(ScreenUtils.dp2px(context, 6.0f));
        linearLayout2.addView(host.verifyLinkButton("频道", new View.OnClickListener() { // from class: com.android.support.VerifyOfficialRow$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.openOfficialUrl(WigVerify.officialChannelUrl());
            }
        }), layoutParams);
        linearLayout2.addView(host.verifyLinkButton("客服", new View.OnClickListener() { // from class: com.android.support.VerifyOfficialRow$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.openOfficialUrl(WigVerify.officialSupportUrl());
            }
        }), layoutParams2);
        linearLayout2.addView(host.verifyLinkButton("群组", new View.OnClickListener() { // from class: com.android.support.VerifyOfficialRow$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                host.openOfficialUrl(WigVerify.officialGroupUrl());
            }
        }), new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout.addView(linearLayout2);
        return linearLayout2;
    }
}
