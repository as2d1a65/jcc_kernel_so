package com.android.support;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

/* loaded from: classes.dex */
final class PageFlipperControlBinder {
    private static final int[] FUNCTION_DOT_IDS = {R.id.dotFuncPage1, R.id.dotFuncPageSpecifiedShop, R.id.dotFuncPageRefresh, R.id.dotFuncPage2, R.id.dotFuncPage3, R.id.dotFuncPage4, R.id.dotFuncPage5};

    interface Host {
        void setCachedPagePlayers(View view);

        void setFunctionFlipperViews(ViewFlipper viewFlipper, TextView[] textViewArr);

        void setPlayerFlipperViews(ViewFlipper viewFlipper, TextView textView, TextView textView2, TextView textView3);

        void switchFunctionPage(int i);

        void switchPlayerPage(int i);
    }

    private PageFlipperControlBinder() {
    }

    static void bind(View view, Context context, Host host) {
        bindFunctionFlipper(view, context, host);
        bindPlayerFlipper(view, context, host);
    }

    private static void bindFunctionFlipper(View view, Context context, final Host host) {
        ViewFlipper viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipperFunction);
        TextView[] textViewArr = new TextView[7];
        for (int i = 0; i < 7; i++) {
            textViewArr[i] = (TextView) view.findViewById(FUNCTION_DOT_IDS[i]);
        }
        host.setFunctionFlipperViews(viewFlipper, textViewArr);
        if (viewFlipper != null) {
            viewFlipper.setInAnimation(context, R.anim.slide_in_left);
            viewFlipper.setOutAnimation(context, R.anim.slide_out_right);
            for (final int i2 = 0; i2 < 7; i2++) {
                TextView textView = textViewArr[i2];
                if (textView != null) {
                    textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.PageFlipperControlBinder$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            host.switchFunctionPage(i2);
                        }
                    });
                }
            }
            Dbg.d("FloatingWindowService", "Function navigation initialized");
        }
    }

    private static void bindPlayerFlipper(View view, Context context, final Host host) {
        ViewFlipper viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipperPlayers);
        TextView textView = (TextView) view.findViewById(R.id.dotPlayerPage1);
        TextView textView2 = (TextView) view.findViewById(R.id.dotPlayerPageOpp);
        TextView textView3 = (TextView) view.findViewById(R.id.dotPlayerPage2);
        host.setPlayerFlipperViews(viewFlipper, textView, textView2, textView3);
        if (viewFlipper != null) {
            viewFlipper.setInAnimation(context, R.anim.slide_in_left);
            viewFlipper.setOutAnimation(context, R.anim.slide_out_right);
            host.setCachedPagePlayers(view.findViewById(R.id.pagePlayers));
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.PageFlipperControlBinder$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        host.switchPlayerPage(0);
                    }
                });
            }
            if (textView2 != null) {
                textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.PageFlipperControlBinder$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        host.switchPlayerPage(1);
                    }
                });
            }
            if (textView3 != null) {
                textView3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.PageFlipperControlBinder$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        host.switchPlayerPage(2);
                    }
                });
            }
        }
    }
}
