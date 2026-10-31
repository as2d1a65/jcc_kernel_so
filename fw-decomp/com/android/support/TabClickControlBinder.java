package com.android.support;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

/* loaded from: classes.dex */
final class TabClickControlBinder {

    interface Host {
        Handler autoRefreshHandler();

        View cachedPageAbout();

        View cachedPageFunction();

        View cachedPageHeroPool();

        View cachedPageHotkey();

        View cachedPageIntro();

        View cachedPagePlayers();

        View cachedPageSettings();

        View cachedPageSpecified();

        View cachedPageVoice();

        int currentFunctionPage();

        void hideAllInPriorityWindow();

        void hideAutoPickHeroWindow();

        void hideChildWindow();

        void hideDropHeroWindow();

        void hideMemoryFunctionWindow();

        void hidePlayersChildWindow();

        void hideSpecifiedHexPickerWindow();

        void loadHeroPoolData();

        void loadPlayersData();

        void refreshLineupList();

        void setFunctionPageVisible(boolean z);

        void setHeroPoolPageVisible(boolean z);

        void setPlayersPageVisible(boolean z);

        void setSpecifiedPageVisible(boolean z);

        void setTabPlayers(LinearLayout linearLayout);

        void showAllInPriorityWindow();

        void showAutoPickHeroWindow();

        void showChildWindow();

        void showDropHeroWindow();

        void showHexFeatureDisabled();

        void showMemoryFunctionWindow();

        void showPlayersChildWindow();

        void showSpecifiedHexPickerWindow();

        void switchTab(LinearLayout linearLayout, LinearLayout[] linearLayoutArr, View view, View[] viewArr);

        void syncAutoRefreshState();

        LinearLayout tabAbout();

        LinearLayout tabFunction();

        LinearLayout tabHeroPool();

        LinearLayout tabHotkey();

        LinearLayout tabIntro();

        LinearLayout tabPlayers();

        LinearLayout tabSettings();

        LinearLayout tabSpecified();

        LinearLayout tabVoice();
    }

    private TabClickControlBinder() {
    }

    static void bind(View view, final Host host) {
        host.setTabPlayers((LinearLayout) view.findViewById(R.id.tabPlayers));
        host.tabFunction().setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TabClickControlBinder.lambda$bind$1(host, view2);
            }
        });
        host.tabSettings().setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TabClickControlBinder.lambda$bind$2(host, view2);
            }
        });
        host.tabHeroPool().setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TabClickControlBinder.lambda$bind$3(host, view2);
            }
        });
        host.tabSpecified().setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TabClickControlBinder.lambda$bind$4(host, view2);
            }
        });
        host.tabPlayers().setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TabClickControlBinder.lambda$bind$6(host, view2);
            }
        });
        host.tabHotkey().setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TabClickControlBinder.lambda$bind$7(host, view2);
            }
        });
        host.tabAbout().setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TabClickControlBinder.lambda$bind$8(host, view2);
            }
        });
        host.tabIntro().setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TabClickControlBinder.lambda$bind$9(host, view2);
            }
        });
        if (host.tabVoice() != null) {
            host.tabVoice().setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda10
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    TabClickControlBinder.lambda$bind$10(host, view2);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$1(final Host host, View view) {
        Dbg.d("FloatingWindowService", "Tab Function clicked!");
        host.switchTab(host.tabFunction(), new LinearLayout[]{host.tabSettings(), host.tabHeroPool(), host.tabSpecified(), host.tabPlayers(), host.tabVoice(), host.tabHotkey(), host.tabAbout(), host.tabIntro()}, host.cachedPageFunction(), new View[]{host.cachedPageSettings(), host.cachedPageHeroPool(), host.cachedPageSpecified(), host.cachedPagePlayers(), host.cachedPageVoice(), host.cachedPageHotkey(), host.cachedPageAbout(), host.cachedPageIntro()});
        host.hideChildWindow();
        host.hideSpecifiedHexPickerWindow();
        host.hidePlayersChildWindow();
        int iCurrentFunctionPage = host.currentFunctionPage();
        if (FunctionPageCompanionPolicy.showsAutoPickWindow(iCurrentFunctionPage)) {
            host.showAutoPickHeroWindow();
        } else {
            host.hideAutoPickHeroWindow();
        }
        if (FunctionPageCompanionPolicy.showsAllInPriorityWindow(iCurrentFunctionPage)) {
            host.showAllInPriorityWindow();
        } else {
            host.hideAllInPriorityWindow();
        }
        if (FunctionPageCompanionPolicy.showsMemoryWindows(iCurrentFunctionPage)) {
            host.showMemoryFunctionWindow();
            host.showDropHeroWindow();
        } else {
            host.hideMemoryFunctionWindow();
            host.hideDropHeroWindow();
        }
        host.autoRefreshHandler().postDelayed(new Runnable() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                host.refreshLineupList();
            }
        }, 50L);
        host.setFunctionPageVisible(true);
        host.setHeroPoolPageVisible(false);
        host.setSpecifiedPageVisible(false);
        host.setPlayersPageVisible(false);
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$2(Host host, View view) {
        Dbg.d("FloatingWindowService", "Tab Settings clicked!");
        host.switchTab(host.tabSettings(), new LinearLayout[]{host.tabFunction(), host.tabHeroPool(), host.tabSpecified(), host.tabPlayers(), host.tabVoice(), host.tabHotkey(), host.tabAbout(), host.tabIntro()}, host.cachedPageSettings(), new View[]{host.cachedPageFunction(), host.cachedPageHeroPool(), host.cachedPageSpecified(), host.cachedPagePlayers(), host.cachedPageVoice(), host.cachedPageHotkey(), host.cachedPageAbout(), host.cachedPageIntro()});
        host.hideChildWindow();
        host.hideSpecifiedHexPickerWindow();
        host.hideAutoPickHeroWindow();
        host.hideAllInPriorityWindow();
        host.hideDropHeroWindow();
        host.hideMemoryFunctionWindow();
        host.hidePlayersChildWindow();
        host.setFunctionPageVisible(false);
        host.setHeroPoolPageVisible(false);
        host.setSpecifiedPageVisible(false);
        host.setPlayersPageVisible(false);
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$3(Host host, View view) {
        Dbg.d("FloatingWindowService", "Tab HeroPool clicked!");
        host.switchTab(host.tabHeroPool(), new LinearLayout[]{host.tabFunction(), host.tabSettings(), host.tabSpecified(), host.tabPlayers(), host.tabVoice(), host.tabHotkey(), host.tabAbout(), host.tabIntro()}, host.cachedPageHeroPool(), new View[]{host.cachedPageFunction(), host.cachedPageSettings(), host.cachedPageSpecified(), host.cachedPagePlayers(), host.cachedPageVoice(), host.cachedPageHotkey(), host.cachedPageAbout(), host.cachedPageIntro()});
        host.hideAutoPickHeroWindow();
        host.hideAllInPriorityWindow();
        host.hideDropHeroWindow();
        host.hideMemoryFunctionWindow();
        host.hidePlayersChildWindow();
        host.setFunctionPageVisible(false);
        host.setHeroPoolPageVisible(true);
        host.setSpecifiedPageVisible(false);
        host.setPlayersPageVisible(false);
        host.showChildWindow();
        host.loadHeroPoolData();
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$4(Host host, View view) {
        if (!RecastFeatureGate.isEnabled()) {
            host.hideSpecifiedHexPickerWindow();
            host.setSpecifiedPageVisible(false);
            host.showHexFeatureDisabled();
            return;
        }
        Dbg.d("FloatingWindowService", "Tab Specified clicked!");
        host.switchTab(host.tabSpecified(), new LinearLayout[]{host.tabFunction(), host.tabSettings(), host.tabHeroPool(), host.tabPlayers(), host.tabVoice(), host.tabHotkey(), host.tabAbout(), host.tabIntro()}, host.cachedPageSpecified(), new View[]{host.cachedPageFunction(), host.cachedPageSettings(), host.cachedPageHeroPool(), host.cachedPagePlayers(), host.cachedPageVoice(), host.cachedPageHotkey(), host.cachedPageAbout(), host.cachedPageIntro()});
        host.hideAutoPickHeroWindow();
        host.hideAllInPriorityWindow();
        host.hideDropHeroWindow();
        host.hideMemoryFunctionWindow();
        host.hidePlayersChildWindow();
        host.setFunctionPageVisible(false);
        host.setHeroPoolPageVisible(false);
        host.setSpecifiedPageVisible(true);
        host.setPlayersPageVisible(false);
        host.showSpecifiedHexPickerWindow();
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$6(final Host host, View view) {
        Dbg.d("FloatingWindowService", "Tab Players clicked!");
        host.switchTab(host.tabPlayers(), new LinearLayout[]{host.tabFunction(), host.tabSettings(), host.tabHeroPool(), host.tabSpecified(), host.tabVoice(), host.tabHotkey(), host.tabAbout(), host.tabIntro()}, host.cachedPagePlayers(), new View[]{host.cachedPageFunction(), host.cachedPageSettings(), host.cachedPageHeroPool(), host.cachedPageSpecified(), host.cachedPageVoice(), host.cachedPageHotkey(), host.cachedPageAbout(), host.cachedPageIntro()});
        host.hideChildWindow();
        host.hideSpecifiedHexPickerWindow();
        host.hideAutoPickHeroWindow();
        host.hideAllInPriorityWindow();
        host.hideDropHeroWindow();
        host.hideMemoryFunctionWindow();
        host.setFunctionPageVisible(false);
        host.setHeroPoolPageVisible(false);
        host.setSpecifiedPageVisible(false);
        host.setPlayersPageVisible(true);
        host.showPlayersChildWindow();
        host.autoRefreshHandler().postDelayed(new Runnable() { // from class: com.android.support.TabClickControlBinder$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                host.loadPlayersData();
            }
        }, 100L);
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$7(Host host, View view) {
        Dbg.d("FloatingWindowService", "Tab Hotkey clicked!");
        host.switchTab(host.tabHotkey(), new LinearLayout[]{host.tabFunction(), host.tabSettings(), host.tabHeroPool(), host.tabSpecified(), host.tabPlayers(), host.tabVoice(), host.tabAbout(), host.tabIntro()}, host.cachedPageHotkey(), new View[]{host.cachedPageFunction(), host.cachedPageSettings(), host.cachedPageHeroPool(), host.cachedPageSpecified(), host.cachedPagePlayers(), host.cachedPageVoice(), host.cachedPageAbout(), host.cachedPageIntro()});
        host.hideChildWindow();
        host.hideSpecifiedHexPickerWindow();
        host.hideAutoPickHeroWindow();
        host.hideAllInPriorityWindow();
        host.hideDropHeroWindow();
        host.hideMemoryFunctionWindow();
        host.hidePlayersChildWindow();
        host.setFunctionPageVisible(false);
        host.setHeroPoolPageVisible(false);
        host.setSpecifiedPageVisible(false);
        host.setPlayersPageVisible(false);
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$8(Host host, View view) {
        Dbg.d("FloatingWindowService", "Tab About clicked!");
        host.switchTab(host.tabAbout(), new LinearLayout[]{host.tabFunction(), host.tabSettings(), host.tabHeroPool(), host.tabSpecified(), host.tabPlayers(), host.tabVoice(), host.tabHotkey(), host.tabIntro()}, host.cachedPageAbout(), new View[]{host.cachedPageFunction(), host.cachedPageSettings(), host.cachedPageHeroPool(), host.cachedPageSpecified(), host.cachedPagePlayers(), host.cachedPageVoice(), host.cachedPageHotkey(), host.cachedPageIntro()});
        host.hideChildWindow();
        host.hideSpecifiedHexPickerWindow();
        host.hideAutoPickHeroWindow();
        host.hideAllInPriorityWindow();
        host.hideDropHeroWindow();
        host.hideMemoryFunctionWindow();
        host.hidePlayersChildWindow();
        host.setFunctionPageVisible(false);
        host.setHeroPoolPageVisible(false);
        host.setSpecifiedPageVisible(false);
        host.setPlayersPageVisible(false);
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$9(Host host, View view) {
        Dbg.d("FloatingWindowService", "Tab Intro clicked!");
        host.switchTab(host.tabIntro(), new LinearLayout[]{host.tabFunction(), host.tabSettings(), host.tabHeroPool(), host.tabSpecified(), host.tabPlayers(), host.tabVoice(), host.tabHotkey(), host.tabAbout()}, host.cachedPageIntro(), new View[]{host.cachedPageFunction(), host.cachedPageSettings(), host.cachedPageHeroPool(), host.cachedPageSpecified(), host.cachedPagePlayers(), host.cachedPageVoice(), host.cachedPageHotkey(), host.cachedPageAbout()});
        host.hideChildWindow();
        host.hideSpecifiedHexPickerWindow();
        host.hideAutoPickHeroWindow();
        host.hideAllInPriorityWindow();
        host.hideDropHeroWindow();
        host.hideMemoryFunctionWindow();
        host.hidePlayersChildWindow();
        host.setFunctionPageVisible(false);
        host.setHeroPoolPageVisible(false);
        host.setSpecifiedPageVisible(false);
        host.setPlayersPageVisible(false);
        host.syncAutoRefreshState();
    }

    static /* synthetic */ void lambda$bind$10(Host host, View view) {
        Dbg.d("FloatingWindowService", "Tab Voice clicked!");
        host.switchTab(host.tabVoice(), new LinearLayout[]{host.tabFunction(), host.tabSettings(), host.tabHeroPool(), host.tabSpecified(), host.tabPlayers(), host.tabHotkey(), host.tabAbout(), host.tabIntro()}, host.cachedPageVoice(), new View[]{host.cachedPageFunction(), host.cachedPageSettings(), host.cachedPageHeroPool(), host.cachedPageSpecified(), host.cachedPagePlayers(), host.cachedPageHotkey(), host.cachedPageAbout(), host.cachedPageIntro()});
        host.hideChildWindow();
        host.hideSpecifiedHexPickerWindow();
        host.hideAutoPickHeroWindow();
        host.hideAllInPriorityWindow();
        host.hideDropHeroWindow();
        host.hideMemoryFunctionWindow();
        host.hidePlayersChildWindow();
        host.setFunctionPageVisible(false);
        host.setHeroPoolPageVisible(false);
        host.setSpecifiedPageVisible(false);
        host.setPlayersPageVisible(false);
        host.syncAutoRefreshState();
    }
}
