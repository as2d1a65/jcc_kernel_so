package com.android.support;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
final class UiSurfaceViewBinder {
    private static final float DISABLED_ALPHA = 0.35f;
    private static int appliedDisabled;
    private static int appliedHidden;
    private static WeakReference<View> appliedRoot = new WeakReference<>(null);
    private static final WeakHashMap<View, Boolean> enabledBeforePolicy = new WeakHashMap<>();
    private static final WeakHashMap<View, Float> alphaBeforePolicy = new WeakHashMap<>();
    private static final Binding[] TABS = {new Binding(UiSurfaceRegistry.PAGE_FUNCTION, R.id.tabFunction, R.id.pageFunction, R.id.textFunction), new Binding(UiSurfaceRegistry.PAGE_HERO_POOL, R.id.tabHeroPool, R.id.pageHeroPool, R.id.textHeroPool), new Binding(UiSurfaceRegistry.PAGE_SPECIFIED, R.id.tabSpecified, R.id.pageSpecified, R.id.textSpecified), new Binding(UiSurfaceRegistry.PAGE_PLAYERS, R.id.tabPlayers, R.id.pagePlayers, R.id.textPlayers), new Binding(UiSurfaceRegistry.PAGE_VOICE, R.id.tabVoice, R.id.pageVoice, R.id.textVoice), new Binding(UiSurfaceRegistry.PAGE_HOTKEY, R.id.tabHotkey, R.id.pageHotkey, R.id.textHotkey), new Binding(UiSurfaceRegistry.PAGE_SETTINGS, R.id.tabSettings, R.id.pageSettings, R.id.textSettings), new Binding(UiSurfaceRegistry.PAGE_ABOUT, R.id.tabAbout, R.id.pageAbout, R.id.textAbout), new Binding(UiSurfaceRegistry.PAGE_INTRO, R.id.tabIntro, R.id.pageIntro, R.id.textIntro)};
    private static final Binding[] SECTIONS = {new Binding(UiSurfaceRegistry.PAGE_FUNC_AUTOPICK, 0, R.id.pageFuncAutoPick, 0), new Binding(UiSurfaceRegistry.PAGE_FUNC_MEMORY, 0, R.id.pageFuncMemory, 0), new Binding(UiSurfaceRegistry.PAGE_FUNC_SELL_SWAP, 0, R.id.pageFuncSellSwap, 0), new Binding(UiSurfaceRegistry.PAGE_FUNC_ALL_IN, 0, R.id.pageFuncAllIn, 0), new Binding(UiSurfaceRegistry.PAGE_FUNC_HEX, 0, R.id.pageFuncHex, 0), new Binding(UiSurfaceRegistry.PAGE_FUNC_SPECIFIED_SHOP, R.id.dotFuncPageSpecifiedShop, R.id.pageFuncSpecifiedShop, 0), new Binding(UiSurfaceRegistry.PAGE_PLAYERS_DATA, 0, R.id.pagePlayersData, 0), new Binding(UiSurfaceRegistry.PAGE_PREDICT, 0, R.id.pagePredict, 0), new Binding(UiSurfaceRegistry.PAGE_RANK_WARNING, 0, R.id.pageRankWarning, 0)};

    private static final class Binding {
        final int labelId;
        final int pageId;
        final String surfaceId;
        final int tabId;

        Binding(String str, int i, int i2, int i3) {
            this.surfaceId = str;
            this.tabId = i;
            this.pageId = i2;
            this.labelId = i3;
        }
    }

    static void apply(View view, UiSurfacePolicy uiSurfacePolicy) {
        View viewFindViewById;
        if (view == null || uiSurfacePolicy == null) {
            return;
        }
        if (appliedRoot.get() != view) {
            appliedRoot = new WeakReference<>(view);
            appliedHidden = 0;
            appliedDisabled = 0;
            enabledBeforePolicy.clear();
            alphaBeforePolicy.clear();
        }
        boolean z = false;
        for (Binding binding : TABS) {
            View viewFindViewById2 = view.findViewById(binding.tabId);
            View viewFindViewById3 = view.findViewById(binding.pageId);
            if (viewFindViewById2 != null) {
                int iIndexOf = 1 << UiSurfaceRegistry.indexOf(binding.surfaceId);
                if (!uiSurfacePolicy.overridden(binding.surfaceId)) {
                    if ((appliedHidden & iIndexOf) != 0) {
                        viewFindViewById2.setVisibility(0);
                        appliedHidden &= ~iIndexOf;
                    }
                    if ((appliedDisabled & iIndexOf) != 0) {
                        applyEnabled(viewFindViewById2, true);
                        appliedDisabled &= ~iIndexOf;
                    }
                    markLabel(view, binding, true);
                } else {
                    boolean zVisible = uiSurfacePolicy.visible(binding.surfaceId, true);
                    boolean zEnabled = uiSurfacePolicy.enabled(binding.surfaceId, true);
                    viewFindViewById2.setVisibility(zVisible ? 0 : 8);
                    applyEnabled(viewFindViewById2, zEnabled);
                    markLabel(view, binding, zEnabled && zVisible);
                    int i = appliedHidden;
                    appliedHidden = zVisible ? i & (~iIndexOf) : i | iIndexOf;
                    int i2 = appliedDisabled;
                    appliedDisabled = zEnabled ? i2 & (~iIndexOf) : i2 | iIndexOf;
                    if ((!zVisible || !zEnabled) && viewFindViewById3 != null && viewFindViewById3.getVisibility() == 0) {
                        viewFindViewById3.setVisibility(8);
                        z = true;
                    }
                }
            }
        }
        if (z) {
            selectFirstAvailableTab(view, uiSurfacePolicy);
        }
        for (Binding binding2 : SECTIONS) {
            View viewFindViewById4 = view.findViewById(binding2.pageId);
            if (viewFindViewById4 != null) {
                int iIndexOf2 = 1 << UiSurfaceRegistry.indexOf(binding2.surfaceId);
                if (!uiSurfacePolicy.overridden(binding2.surfaceId)) {
                    boolean z2 = (appliedHidden & iIndexOf2) != 0;
                    boolean z3 = (appliedDisabled & iIndexOf2) != 0;
                    if (z2) {
                        viewFindViewById4.setVisibility(0);
                        appliedHidden &= ~iIndexOf2;
                    }
                    if (z3) {
                        applyEnabled(viewFindViewById4, true);
                        appliedDisabled &= ~iIndexOf2;
                    }
                    viewFindViewById = binding2.tabId != 0 ? view.findViewById(binding2.tabId) : null;
                    if (z2 && viewFindViewById != null) {
                        viewFindViewById.setVisibility(0);
                    }
                    if (z3 && viewFindViewById != null) {
                        applyEnabled(viewFindViewById, true);
                    }
                } else {
                    boolean zVisible2 = uiSurfacePolicy.visible(binding2.surfaceId, true);
                    boolean zEnabled2 = uiSurfacePolicy.enabled(binding2.surfaceId, true);
                    viewFindViewById = binding2.tabId != 0 ? view.findViewById(binding2.tabId) : null;
                    if (!zVisible2) {
                        viewFindViewById4.setVisibility(8);
                        appliedHidden |= iIndexOf2;
                        if (viewFindViewById != null) {
                            viewFindViewById.setVisibility(8);
                        }
                    } else {
                        if ((appliedHidden & iIndexOf2) != 0) {
                            viewFindViewById4.setVisibility(0);
                        }
                        appliedHidden &= ~iIndexOf2;
                        if (viewFindViewById != null) {
                            viewFindViewById.setVisibility(0);
                        }
                    }
                    applyEnabled(viewFindViewById4, zEnabled2);
                    if (viewFindViewById != null) {
                        applyEnabled(viewFindViewById, zEnabled2);
                    }
                    int i3 = appliedDisabled;
                    appliedDisabled = zEnabled2 ? i3 & (~iIndexOf2) : i3 | iIndexOf2;
                }
            }
        }
    }

    private static void selectFirstAvailableTab(View view, UiSurfacePolicy uiSurfacePolicy) {
        for (Binding binding : TABS) {
            if (uiSurfacePolicy.visible(binding.surfaceId, true) && uiSurfacePolicy.enabled(binding.surfaceId, true)) {
                View viewFindViewById = view.findViewById(binding.tabId);
                View viewFindViewById2 = view.findViewById(binding.pageId);
                if (viewFindViewById != null && viewFindViewById2 != null) {
                    for (Binding binding2 : TABS) {
                        View viewFindViewById3 = view.findViewById(binding2.tabId);
                        if (viewFindViewById3 != null) {
                            viewFindViewById3.setSelected(binding2.tabId == binding.tabId);
                        }
                    }
                    viewFindViewById2.setVisibility(0);
                    viewFindViewById.setSelected(true);
                    return;
                }
            }
        }
    }

    private static void markLabel(View view, Binding binding, boolean z) {
        if (binding.labelId == 0) {
            return;
        }
        View viewFindViewById = view.findViewById(binding.labelId);
        if (viewFindViewById instanceof TextView) {
            TextView textView = (TextView) viewFindViewById;
            String string = textView.getText() == null ? "" : textView.getText().toString();
            boolean zEndsWith = string.endsWith("(停用)");
            if (z && zEndsWith) {
                textView.setText(string.substring(0, string.length() - "(停用)".length()));
            } else {
                if (z || zEndsWith) {
                    return;
                }
                textView.setText(string + "(停用)");
            }
        }
    }

    private static void applyEnabled(View view, boolean z) {
        updateEnabled(view, z);
        if (!z) {
            WeakHashMap<View, Float> weakHashMap = alphaBeforePolicy;
            if (!weakHashMap.containsKey(view)) {
                weakHashMap.put(view, Float.valueOf(view.getAlpha()));
            }
            view.setAlpha(DISABLED_ALPHA);
        } else {
            Float fRemove = alphaBeforePolicy.remove(view);
            if (fRemove != null) {
                view.setAlpha(fRemove.floatValue());
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                applyChildEnabled(viewGroup.getChildAt(i), z);
            }
        }
    }

    private static void applyChildEnabled(View view, boolean z) {
        updateEnabled(view, z);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                applyChildEnabled(viewGroup.getChildAt(i), z);
            }
        }
    }

    private static void updateEnabled(View view, boolean z) {
        if (!z) {
            WeakHashMap<View, Boolean> weakHashMap = enabledBeforePolicy;
            if (!weakHashMap.containsKey(view)) {
                weakHashMap.put(view, Boolean.valueOf(view.isEnabled()));
            }
            view.setEnabled(false);
            return;
        }
        Boolean boolRemove = enabledBeforePolicy.remove(view);
        if (boolRemove != null) {
            view.setEnabled(boolRemove.booleanValue());
        }
    }

    private UiSurfaceViewBinder() {
    }
}
