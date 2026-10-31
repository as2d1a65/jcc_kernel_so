package com.android.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
final class MainChromeControlBinder {
    private static final String TAG = "FloatingWindowService";

    interface Host {
        String aboutVersionName();

        void animateButtonClick(View view);

        View cachedPageAbout();

        View cachedPageFunction();

        View cachedPageHeroPool();

        View cachedPageIntro();

        View cachedPageSettings();

        View cachedPageSpecified();

        Context context();

        void logoutAndShowVerificationOverlay();

        void saveAllConfig();

        void setCachedPageAbout(View view);

        void setCachedPageFunction(View view);

        void setCachedPageHeroPool(View view);

        void setCachedPageHotkey(View view);

        void setCachedPageIntro(View view);

        void setCachedPageSettings(View view);

        void setCachedPageSpecified(View view);

        void setCachedPageVoice(View view);

        void setTabAbout(LinearLayout linearLayout);

        void setTabFunction(LinearLayout linearLayout);

        void setTabHeroPool(LinearLayout linearLayout);

        void setTabHotkey(LinearLayout linearLayout);

        void setTabIntro(LinearLayout linearLayout);

        void setTabSettings(LinearLayout linearLayout);

        void setTabSpecified(LinearLayout linearLayout);

        void setTabVoice(LinearLayout linearLayout);

        void showBallView();

        LinearLayout tabAbout();

        LinearLayout tabFunction();

        LinearLayout tabHeroPool();

        LinearLayout tabHotkey();

        LinearLayout tabIntro();

        LinearLayout tabSettings();

        LinearLayout tabSpecified();

        LinearLayout tabVoice();
    }

    private MainChromeControlBinder() {
    }

    static boolean bind(View view, final Host host) {
        ImageView imageView = (ImageView) view.findViewById(R.id.btnMinimize);
        Dbg.d(TAG, "btnMinimize found: " + (imageView != null));
        if (imageView == null) {
            Log.e(TAG, "ERROR: btnMinimize is null!");
            return false;
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainChromeControlBinder$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MainChromeControlBinder.lambda$bind$0(host, view2);
            }
        });
        ImageView imageView2 = (ImageView) view.findViewById(R.id.btnExitApp);
        if (imageView2 != null) {
            imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainChromeControlBinder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    MainChromeControlBinder.lambda$bind$2(host, view2);
                }
            });
        }
        host.setTabFunction((LinearLayout) view.findViewById(R.id.tabFunction));
        host.setTabSettings((LinearLayout) view.findViewById(R.id.tabSettings));
        host.setTabHeroPool((LinearLayout) view.findViewById(R.id.tabHeroPool));
        host.setTabSpecified((LinearLayout) view.findViewById(R.id.tabSpecified));
        host.setTabVoice((LinearLayout) view.findViewById(R.id.tabVoice));
        host.setTabHotkey((LinearLayout) view.findViewById(R.id.tabHotkey));
        host.setTabAbout((LinearLayout) view.findViewById(R.id.tabAbout));
        host.setTabIntro((LinearLayout) view.findViewById(R.id.tabIntro));
        if (!HexFeatureGate.isEnabled() && !RecastFeatureGate.isEnabled() && host.tabSpecified() != null) {
            host.tabSpecified().setAlpha(0.48f);
            host.tabSpecified().setContentDescription("指定，已禁用");
            TextView textView = (TextView) host.tabSpecified().findViewById(R.id.textSpecified);
            if (textView != null) {
                textView.setText("禁用");
            }
        }
        Dbg.d(TAG, "Tabs found: Function=" + (host.tabFunction() != null) + ", Settings=" + (host.tabSettings() != null) + ", HeroPool=" + (host.tabHeroPool() != null) + ", Specified=" + (host.tabSpecified() != null) + ", Voice=" + (host.tabVoice() != null) + ", Hotkey=" + (host.tabHotkey() != null) + ", About=" + (host.tabAbout() != null) + ", Intro=" + (host.tabIntro() != null));
        if (host.tabFunction() == null || host.tabSettings() == null || host.tabHeroPool() == null || host.tabSpecified() == null || host.tabHotkey() == null || host.tabAbout() == null || host.tabIntro() == null) {
            Log.e(TAG, "ERROR: One or more tabs are null!");
            return false;
        }
        host.setCachedPageFunction(view.findViewById(R.id.pageFunction));
        host.setCachedPageSettings(view.findViewById(R.id.pageSettings));
        host.setCachedPageHeroPool(view.findViewById(R.id.pageHeroPool));
        host.setCachedPageSpecified(view.findViewById(R.id.pageSpecified));
        host.setCachedPageVoice(view.findViewById(R.id.pageVoice));
        host.setCachedPageAbout(view.findViewById(R.id.pageAbout));
        host.setCachedPageHotkey(view.findViewById(R.id.pageHotkey));
        host.setCachedPageIntro(view.findViewById(R.id.pageIntro));
        Dbg.d(TAG, "Pages found: Function=" + (host.cachedPageFunction() != null) + ", Settings=" + (host.cachedPageSettings() != null) + ", HeroPool=" + (host.cachedPageHeroPool() != null) + ", Specified=" + (host.cachedPageSpecified() != null) + ", About=" + (host.cachedPageAbout() != null));
        TextView textView2 = (TextView) view.findViewById(R.id.tvAboutVersion);
        if (textView2 != null) {
            String strAboutVersionName = host.aboutVersionName();
            if (strAboutVersionName == null || strAboutVersionName.trim().isEmpty()) {
                strAboutVersionName = BuildConfig.VERSION_NAME;
            }
            textView2.setText(WigVerify.versionLabel(strAboutVersionName));
        }
        return true;
    }

    static /* synthetic */ void lambda$bind$0(Host host, View view) {
        Dbg.d(TAG, "btnMinimize clicked!");
        host.showBallView();
    }

    static /* synthetic */ void lambda$bind$2(final Host host, View view) {
        host.animateButtonClick(view);
        AlertDialog alertDialogCreate = new AlertDialog.Builder(host.context(), android.R.style.Theme.DeviceDefault.Dialog.Alert).setTitle("退出登录").setMessage("确定要退出当前授权吗？\n将返回悬浮登录窗口。").setPositiveButton("确认退出", new DialogInterface.OnClickListener() { // from class: com.android.support.MainChromeControlBinder$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MainChromeControlBinder.lambda$bind$1(host, dialogInterface, i);
            }
        }).setNegativeButton("取消", (DialogInterface.OnClickListener) null).setCancelable(true).create();
        if (alertDialogCreate.getWindow() != null) {
            alertDialogCreate.getWindow().setType(2038);
        }
        alertDialogCreate.show();
    }

    static /* synthetic */ void lambda$bind$1(Host host, DialogInterface dialogInterface, int i) {
        host.saveAllConfig();
        host.logoutAndShowVerificationOverlay();
    }
}
