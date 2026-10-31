package com.android.support;

import android.view.View;
import android.widget.TextView;
import com.android.support.trust.NativeTrustBridge;

/* loaded from: classes.dex */
final class SpecifiedModeControlBinder {
    private static SpecifiedModeControlBinder instance;
    private final View hexCount;
    private final View hexPageBody;
    private final View hexPickerBody;
    private final TextView hexTab;
    private final Listener listener;
    private final View recastCount;
    private boolean recastMode;
    private final View recastPageBody;
    private final View recastPickerBody;
    private final TextView recastTab;
    private final View switchRow;
    private boolean hexAvailable = true;
    private boolean recastAvailable = true;

    interface Listener {
        void onSpecifiedModeChanged(boolean z);
    }

    private SpecifiedModeControlBinder(TextView textView, TextView textView2, View view, View view2, View view3, View view4, View view5, View view6, View view7, Listener listener) {
        this.hexTab = textView;
        this.recastTab = textView2;
        this.hexPageBody = view;
        this.recastPageBody = view2;
        this.hexPickerBody = view3;
        this.recastPickerBody = view4;
        this.hexCount = view5;
        this.recastCount = view6;
        this.switchRow = view7;
        this.listener = listener;
    }

    static SpecifiedModeControlBinder bind(View view, View view2, Listener listener) {
        if (view == null || view2 == null) {
            instance = null;
            return null;
        }
        TextView textView = (TextView) view.findViewById(R.id.btnSpecifiedModeHex);
        TextView textView2 = (TextView) view.findViewById(R.id.btnSpecifiedModeRecast);
        View viewFindViewById = view.findViewById(R.id.pageSpecifiedHexBody);
        View viewFindViewById2 = view.findViewById(R.id.pageSpecifiedRecastBody);
        View viewFindViewById3 = view2.findViewById(R.id.specifiedHexPickerBody);
        View viewFindViewById4 = view2.findViewById(R.id.specifiedRecastPickerBody);
        View viewFindViewById5 = view.findViewById(R.id.tvSpecifiedHexSelectedCount);
        View viewFindViewById6 = view.findViewById(R.id.tvSpecifiedRecastSelectedCount);
        if (textView == null || textView2 == null || viewFindViewById == null || viewFindViewById2 == null || viewFindViewById3 == null || viewFindViewById4 == null || viewFindViewById5 == null || viewFindViewById6 == null) {
            instance = null;
            return null;
        }
        final SpecifiedModeControlBinder specifiedModeControlBinder = new SpecifiedModeControlBinder(textView, textView2, viewFindViewById, viewFindViewById2, viewFindViewById3, viewFindViewById4, viewFindViewById5, viewFindViewById6, view.findViewById(R.id.llSpecifiedModeSwitch), listener);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedModeControlBinder$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                this.f$0.setRecastMode(false);
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedModeControlBinder$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                this.f$0.setRecastMode(true);
            }
        });
        instance = specifiedModeControlBinder;
        specifiedModeControlBinder.applyPolicy(UiSurfaceControl.current());
        return specifiedModeControlBinder;
    }

    static void applyCurrentPolicy(UiSurfacePolicy uiSurfacePolicy) {
        SpecifiedModeControlBinder specifiedModeControlBinder = instance;
        if (specifiedModeControlBinder != null) {
            specifiedModeControlBinder.applyPolicy(uiSurfacePolicy);
        }
    }

    static void release() {
        instance = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRecastMode(boolean z) {
        if (this.recastMode == z) {
            return;
        }
        if (!z || this.recastAvailable) {
            if (z || this.hexAvailable) {
                this.recastMode = z;
                apply(true);
            }
        }
    }

    private void applyPolicy(UiSurfacePolicy uiSurfacePolicy) {
        GameRegion gameRegionCurrent = GameRegionState.current();
        int iSpecifiedCapabilities = NativeTrustBridge.specifiedCapabilities();
        boolean z = (iSpecifiedCapabilities & 8) != 0;
        boolean z2 = (iSpecifiedCapabilities & 16) != 0;
        this.hexAvailable = z && HexFeatureGate.isEnabled();
        boolean z3 = z2 && RecastFeatureGate.isEnabled();
        this.recastAvailable = z3;
        boolean z4 = this.hexAvailable;
        if (!z4 || !z3) {
            this.hexAvailable = z4 && GameRegionFeaturePolicy.operational(gameRegionCurrent, true, uiSurfacePolicy, UiSurfaceRegistry.PAGE_SPECIFIED);
            this.recastAvailable = this.recastAvailable && GameRegionFeaturePolicy.operational(gameRegionCurrent, true, uiSurfacePolicy, UiSurfaceRegistry.PAGE_FUNC_RECAST);
        }
        if (this.hexAvailable && this.recastAvailable) {
            View view = this.switchRow;
            if (view != null) {
                view.setVisibility(0);
            }
            this.hexTab.setVisibility(0);
            this.recastTab.setVisibility(0);
        } else {
            View view2 = this.switchRow;
            if (view2 != null) {
                view2.setVisibility(8);
            }
            this.recastMode = this.recastAvailable && !this.hexAvailable;
        }
        apply(false);
    }

    private void apply(boolean z) {
        Listener listener;
        boolean z2 = this.recastMode && this.recastAvailable;
        boolean z3 = !z2 && this.hexAvailable;
        this.hexTab.setSelected(!z2);
        this.recastTab.setSelected(z2);
        this.hexPageBody.setVisibility(z3 ? 0 : 8);
        this.recastPageBody.setVisibility(z2 ? 0 : 8);
        this.hexPickerBody.setVisibility(z3 ? 0 : 8);
        this.recastPickerBody.setVisibility(z2 ? 0 : 8);
        this.hexCount.setVisibility(z3 ? 0 : 8);
        this.recastCount.setVisibility(z2 ? 0 : 8);
        if (!z || (listener = this.listener) == null) {
            return;
        }
        listener.onSpecifiedModeChanged(z2);
    }
}
