package com.android.support;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.support.IntroFeatureCatalog;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
final class IntroPageContent {

    interface TabNavigator {
        void navigateTo(int i);
    }

    private IntroPageContent() {
    }

    static void inflate(Context context, LinearLayout linearLayout, TabNavigator tabNavigator) {
        if (context == null || linearLayout == null || linearLayout.getChildCount() > 0) {
            return;
        }
        List<IntroFeatureCatalog.Section> listBuild = IntroFeatureCatalog.build();
        TextView textViewText = text(context, "功能速查", 16, GameHudPalette.TEXT, true);
        textViewText.setPadding(0, dp(context, 4.0f), 0, dp(context, 8.0f));
        linearLayout.addView(textViewText);
        for (IntroFeatureCatalog.Section section : listBuild) {
            TextView textViewText2 = text(context, section.title, 12, GameHudPalette.TEXT_DIM, true);
            textViewText2.setPadding(0, dp(context, 16.0f), 0, dp(context, 8.0f));
            linearLayout.addView(textViewText2);
            Iterator<IntroFeatureCatalog.Feature> it = section.features.iterator();
            while (it.hasNext()) {
                linearLayout.addView(featureRow(context, it.next(), tabNavigator));
            }
        }
    }

    private static View featureRow(Context context, final IntroFeatureCatalog.Feature feature, final TabNavigator tabNavigator) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setMinimumHeight(dp(context, 48.0f));
        linearLayout.setPadding(dp(context, 10.0f), dp(context, 8.0f), dp(context, 10.0f), dp(context, 8.0f));
        linearLayout.setBackgroundResource(R.drawable.bg_xf_btn);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(context, 6.0f);
        linearLayout.setLayoutParams(layoutParams);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(iconForTab(feature.tabId));
        imageView.setColorFilter(GameHudPalette.CYAN);
        imageView.setImportantForAccessibility(2);
        linearLayout.addView(imageView, new LinearLayout.LayoutParams(dp(context, 20.0f), dp(context, 20.0f)));
        TextView textViewText = text(context, feature.name, 13, GameHudPalette.TEXT, false);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams2.setMarginStart(dp(context, 12.0f));
        layoutParams2.setMarginEnd(dp(context, 8.0f));
        linearLayout.addView(textViewText, layoutParams2);
        TextView textViewText2 = text(context, "›", 20, GameHudPalette.TEXT_DIM, false);
        textViewText2.setImportantForAccessibility(2);
        linearLayout.addView(textViewText2);
        linearLayout.setFocusable(true);
        linearLayout.setContentDescription(feature.name + "，打开" + feature.tab);
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.IntroPageContent$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                IntroPageContent.lambda$featureRow$0(tabNavigator, feature, view);
            }
        });
        OverlayUiMotion.installPressScale(linearLayout);
        return linearLayout;
    }

    static /* synthetic */ void lambda$featureRow$0(TabNavigator tabNavigator, IntroFeatureCatalog.Feature feature, View view) {
        if (tabNavigator != null) {
            tabNavigator.navigateTo(feature.tabId);
        }
    }

    private static int iconForTab(int i) {
        return i == R.id.tabHeroPool ? R.drawable.ic_tech_heropool : i == R.id.tabSpecified ? R.drawable.ic_tech_specified : i == R.id.tabPlayers ? R.drawable.ic_tech_players : i == R.id.tabVoice ? R.drawable.ic_tech_voice : i == R.id.tabHotkey ? R.drawable.ic_tech_hotkey : i == R.id.tabSettings ? R.drawable.ic_tech_settings : R.drawable.ic_tech_function;
    }

    private static TextView text(Context context, String str, int i, int i2, boolean z) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextSize(i);
        textView.setTextColor(i2);
        textView.setLetterSpacing(0.0f);
        textView.setIncludeFontPadding(false);
        if (z) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return textView;
    }

    private static int dp(Context context, float f) {
        return ScreenUtils.dp2px(context, f);
    }
}
