package com.android.support;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.support.MainConnectionUiState;

/* loaded from: classes.dex */
final class MainScreenInteraction {
    private boolean advancedExpanded;
    private boolean environmentExpanded;
    private final View root;
    private Boolean wideWorkspace;

    MainScreenInteraction(View view, Bundle bundle) throws Resources.NotFoundException {
        this.root = view;
        configureWorkspace();
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.support.MainScreenInteraction$$ExternalSyntheticLambda0
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) throws Resources.NotFoundException {
                this.f$0.m1372lambda$new$0$comandroidsupportMainScreenInteraction(view2, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
        final TextView textView = (TextView) view.findViewById(R.id.tvRootDetail);
        textView.addTextChangedListener(new TextWatcher() { // from class: com.android.support.MainScreenInteraction.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                textView.setVisibility(charSequence.length() == 0 ? 8 : 0);
            }
        });
        boolean z = false;
        textView.setVisibility(textView.length() == 0 ? 8 : 0);
        this.environmentExpanded = bundle != null && bundle.getBoolean("home.environment");
        if (bundle != null && bundle.getBoolean("home.advanced")) {
            z = true;
        }
        this.advancedExpanded = z;
        view.findViewById(R.id.btnEnvironmentDetails).setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainScreenInteraction$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1373lambda$new$1$comandroidsupportMainScreenInteraction(view2);
            }
        });
        view.findViewById(R.id.btnAdvancedOptions).setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainScreenInteraction$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1374lambda$new$2$comandroidsupportMainScreenInteraction(view2);
            }
        });
        renderDisclosures();
    }

    /* renamed from: lambda$new$0$com-android-support-MainScreenInteraction, reason: not valid java name */
    /* synthetic */ void m1372lambda$new$0$comandroidsupportMainScreenInteraction(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) throws Resources.NotFoundException {
        if (i3 - i != i7 - i5) {
            configureWorkspace();
        }
    }

    /* renamed from: lambda$new$1$com-android-support-MainScreenInteraction, reason: not valid java name */
    /* synthetic */ void m1373lambda$new$1$comandroidsupportMainScreenInteraction(View view) {
        this.environmentExpanded = !this.environmentExpanded;
        renderDisclosures();
    }

    /* renamed from: lambda$new$2$com-android-support-MainScreenInteraction, reason: not valid java name */
    /* synthetic */ void m1374lambda$new$2$comandroidsupportMainScreenInteraction(View view) {
        this.advancedExpanded = !this.advancedExpanded;
        renderDisclosures();
    }

    void saveState(Bundle bundle) {
        bundle.putBoolean("home.environment", this.environmentExpanded);
        bundle.putBoolean("home.advanced", this.advancedExpanded);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void configureWorkspace() throws Resources.NotFoundException {
        View childAt = ((ScrollView) this.root.findViewById(R.id.mainScrollContent)).getChildAt(0);
        int dimensionPixelSize = this.root.getResources().getDimensionPixelSize(R.dimen.ds_screen_h);
        childAt.setPaddingRelative(dimensionPixelSize, childAt.getPaddingTop(), dimensionPixelSize, childAt.getPaddingBottom());
        Configuration configuration = this.root.getResources().getConfiguration();
        boolean z = (configuration.screenWidthDp >= 840 || (configuration.screenWidthDp >= 600 && configuration.screenWidthDp > configuration.screenHeightDp)) ? 1 : 0;
        Boolean bool = this.wideWorkspace;
        if (bool == null || bool.booleanValue() != z) {
            this.wideWorkspace = Boolean.valueOf(z);
            ((LinearLayout) this.root.findViewById(R.id.homeWorkspace)).setOrientation(!z);
            View viewFindViewById = this.root.findViewById(R.id.homeConnectionGroup);
            View viewFindViewById2 = this.root.findViewById(R.id.homeOptionsGroup);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(z != 0 ? 0 : -1, -2, z != 0 ? 1.15f : 0.0f);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(z != 0 ? 0 : -1, -2, z != 0 ? 1.0f : 0.0f);
            layoutParams2.setMarginStart(z != 0 ? Math.round(this.root.getResources().getDisplayMetrics().density * 32.0f) : 0);
            viewFindViewById.setLayoutParams(layoutParams);
            viewFindViewById2.setLayoutParams(layoutParams2);
        }
    }

    void openAdvancedOptions() {
        this.advancedExpanded = true;
        renderDisclosures();
    }

    void render(MainConnectionUiState.Phase phase) {
        MainConnectionUiState.Presentation presentationForPhase = MainConnectionUiState.forPhase(phase);
        int[] iArr = {R.id.connectionStepEnvironment, R.id.connectionStepGame, R.id.connectionStepMatch};
        int i = 0;
        while (true) {
            if (i >= 3) {
                break;
            }
            TextView textView = (TextView) this.root.findViewById(iArr[i]);
            boolean z = i < presentationForPhase.completedSteps();
            textView.setSelected(z);
            textView.setTextColor(this.root.getResources().getColor(z ? R.color.ds_brand : R.color.ds_text_2, null));
            textView.setContentDescription(((Object) textView.getText()) + (z ? "，已就绪" : "，等待中"));
            i++;
        }
        if (phase == MainConnectionUiState.Phase.NEEDS_SETUP) {
            this.environmentExpanded = true;
            renderDisclosures();
        }
    }

    private void renderDisclosures() {
        this.root.findViewById(R.id.envStatusCard).setVisibility(this.environmentExpanded ? 0 : 8);
        this.root.findViewById(R.id.advancedOptionsBody).setVisibility(this.advancedExpanded ? 0 : 8);
        setDisclosure(R.id.btnEnvironmentDetails, "运行环境", this.environmentExpanded);
        setDisclosure(R.id.btnAdvancedOptions, "设备与高级设置", this.advancedExpanded);
    }

    private void setDisclosure(int i, String str, boolean z) {
        TextView textView = (TextView) this.root.findViewById(i);
        textView.setText(str);
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, z ? R.drawable.ic_disclosure_up : R.drawable.ic_disclosure_down, 0);
        textView.setSelected(z);
        textView.setContentDescription(str + (z ? "，已展开，点击收起" : "，已收起，点击展开"));
    }
}
