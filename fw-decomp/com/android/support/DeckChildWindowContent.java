package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.jvm.internal.CharCompanionObject;

/* loaded from: classes.dex */
final class DeckChildWindowContent {
    private static final String TAG = "FloatingWindowService";

    interface Host {
        Handler autoRefreshHandler();

        LinearLayout buildCostGroupCardShell(int i);

        void buildDeckEmptyState(LinearLayout linearLayout);

        View childWindow();

        int companionHeroGridColumns();

        Context context();

        int costColor(int i);

        void loadHeroAvatar(ImageView imageView, String str);

        void maybeDetectSeasonOnHeroDataUpdate(int i);

        void rebuildBoardHeroPoolLeftCache(List<HeroData> list);

        void saveSelectedHeroIdsConfig();

        Set<Integer> selectedHeroIds();

        List<HeroData> selectedHeroesData();

        View selectedHeroesWindow();

        void setLastHeroCountsSnapshot(String str);

        boolean showHeroAvatar();

        void storeCurrentHeroCaches(List<HeroData> list, List<HeroData> list2);

        void syncAutoSelectedOwnedHeroes(List<HeroData> list);

        void updateSelectedHeroesWindow();
    }

    private DeckChildWindowContent() {
    }

    static void update(Host host, List<HeroData> list, List<HeroData> list2) {
        String str;
        String str2;
        String str3;
        LinearLayout linearLayout;
        List<HeroData> list3;
        Map<Integer, List<HeroData>> map;
        final int i;
        boolean z;
        int i2;
        char c;
        float f;
        final Host host2 = host;
        String str4 = TAG;
        if (host2.childWindow() == null) {
            return;
        }
        try {
            LinearLayout linearLayoutContent = OverlayScrollDetailList.content(host2.childWindow());
            if (linearLayoutContent == null) {
                Log.e(TAG, "子窗口内容布局未找到");
                return;
            }
            LinearLayout linearLayoutDetail = OverlayScrollDetailList.detail(linearLayoutContent);
            if (linearLayoutDetail == null) {
                Log.e(TAG, "子窗口详情列表未找到");
                return;
            }
            linearLayoutDetail.removeAllViews();
            List<HeroData> listMerge = HeroPoolMerge.merge(list, list2);
            host2.storeCurrentHeroCaches(listMerge, list2);
            host2.rebuildBoardHeroPoolLeftCache(listMerge);
            host2.maybeDetectSeasonOnHeroDataUpdate(listMerge.size());
            host2.syncAutoSelectedOwnedHeroes(listMerge);
            host2.autoRefreshHandler().post(new Runnable() { // from class: com.android.support.DeckChildWindowContent$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    host2.updateSelectedHeroesWindow();
                }
            });
            if (listMerge.isEmpty()) {
                host2.buildDeckEmptyState(linearLayoutDetail);
                return;
            }
            Map<Integer, List<HeroData>> mapGroup = HeroCostGroups.group(listMerge);
            int i3 = 1;
            int i4 = 1;
            final Host host3 = host2;
            LinearLayout linearLayout2 = linearLayoutDetail;
            while (i4 <= 6) {
                try {
                    List<HeroData> list4 = mapGroup.get(Integer.valueOf(i4));
                    if (list4 == null || list4.isEmpty()) {
                        str3 = str4;
                        linearLayout = linearLayout2;
                        list3 = listMerge;
                        map = mapGroup;
                        i = i4;
                    } else {
                        LinearLayout linearLayoutBuildCostGroupCardShell = host3.buildCostGroupCardShell(i4);
                        int iCostColor = host3.costColor(i4);
                        TextView textView = new TextView(host3.context());
                        textView.setText(DeckCostChrome.icon(i4));
                        textView.setTextSize(14.0f);
                        textView.setTextColor(iCostColor);
                        textView.setShadowLayer(4.0f, 0.0f, 0.0f, iCostColor);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams.rightMargin = ScreenUtils.dp2px(host3.context(), 6.0f);
                        textView.setLayoutParams(layoutParams);
                        TextView textView2 = new TextView(host3.context());
                        textView2.setText(i4 + "费卡池  " + DeckCostChrome.label(i4) + "  (" + list4.size() + ")");
                        textView2.setTextSize(13.0f);
                        textView2.setTextColor(iCostColor);
                        textView2.setTypeface(null, i3);
                        textView2.setShadowLayer(3.0f, 0.0f, 0.0f, iCostColor);
                        textView2.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
                        final Button button = new Button(host3.context());
                        Iterator<HeroData> it = list4.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                if (!host3.selectedHeroIds().contains(Integer.valueOf(it.next().heroId))) {
                                    z = false;
                                    break;
                                }
                            } else {
                                z = true;
                                break;
                            }
                        }
                        button.setText(z ? "✕ 取消" : "✓ 全选");
                        button.setTextSize(10.0f);
                        button.setTextColor(z ? -39322 : -1);
                        boolean z2 = z;
                        button.setPadding(ScreenUtils.dp2px(host3.context(), 10.0f), ScreenUtils.dp2px(host3.context(), 2.0f), ScreenUtils.dp2px(host3.context(), 10.0f), ScreenUtils.dp2px(host3.context(), 2.0f));
                        button.setMinHeight(0);
                        button.setMinimumHeight(0);
                        GradientDrawable gradientDrawable = new GradientDrawable();
                        gradientDrawable.setShape(0);
                        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(host3.context(), 12.0f));
                        if (z2) {
                            gradientDrawable.setColor(872367172);
                            gradientDrawable.setStroke(ScreenUtils.dp2px(host3.context(), 1.0f), 1728005188);
                            i2 = 16777215;
                        } else {
                            int i5 = iCostColor & ViewCompat.MEASURED_SIZE_MASK;
                            gradientDrawable.setColor(1140850688 | i5);
                            i2 = 16777215;
                            gradientDrawable.setStroke(ScreenUtils.dp2px(host3.context(), 1.0f), i5 | DsTokens.RIPPLE_DARK);
                        }
                        button.setBackground(gradientDrawable);
                        final ArrayList arrayList = new ArrayList(list4);
                        linearLayoutBuildCostGroupCardShell.addView(textView);
                        linearLayoutBuildCostGroupCardShell.addView(textView2);
                        linearLayoutBuildCostGroupCardShell.addView(button);
                        linearLayout2.addView(linearLayoutBuildCostGroupCardShell);
                        if (host3.showHeroAvatar()) {
                            GridLayout gridLayout = new GridLayout(host3.context());
                            gridLayout.setColumnCount(host3.companionHeroGridColumns());
                            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams2.bottomMargin = ScreenUtils.dp2px(host3.context(), 2.0f);
                            gridLayout.setLayoutParams(layoutParams2);
                            GradientDrawable gradientDrawable2 = new GradientDrawable();
                            gradientDrawable2.setShape(0);
                            gradientDrawable2.setCornerRadius(ScreenUtils.dp2px(host3.context(), 8.0f));
                            gradientDrawable2.setColor((iCostColor & i2) | 218103808);
                            gridLayout.setBackground(gradientDrawable2);
                            gridLayout.setPadding(ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 4.0f));
                            ArrayList arrayList2 = new ArrayList();
                            final ArrayList arrayList3 = new ArrayList();
                            ArrayList arrayList4 = new ArrayList();
                            ArrayList arrayList5 = new ArrayList();
                            Host host4 = host3;
                            LinearLayout linearLayout3 = linearLayout2;
                            for (HeroData heroData : list4) {
                                LinearLayout linearLayout4 = new LinearLayout(host4.context());
                                linearLayout4.setOrientation(1);
                                linearLayout4.setGravity(17);
                                GridLayout.LayoutParams layoutParams3 = new GridLayout.LayoutParams();
                                GridLayout gridLayout2 = gridLayout;
                                ArrayList arrayList6 = arrayList5;
                                layoutParams3.width = ScreenUtils.dp2px(host4.context(), 50.0f);
                                layoutParams3.height = -2;
                                ArrayList arrayList7 = arrayList4;
                                List<HeroData> list5 = listMerge;
                                Map<Integer, List<HeroData>> map2 = mapGroup;
                                layoutParams3.setMargins(ScreenUtils.dp2px(host4.context(), 2.0f), ScreenUtils.dp2px(host4.context(), 2.0f), ScreenUtils.dp2px(host4.context(), 2.0f), ScreenUtils.dp2px(host4.context(), 2.0f));
                                linearLayout4.setLayoutParams(layoutParams3);
                                FrameLayout frameLayout = new FrameLayout(host4.context());
                                frameLayout.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtils.dp2px(host4.context(), 46.0f), ScreenUtils.dp2px(host4.context(), 46.0f)));
                                frameLayout.setClickable(true);
                                frameLayout.setFocusable(true);
                                final GradientDrawable gradientDrawable3 = new GradientDrawable();
                                gradientDrawable3.setShape(0);
                                gradientDrawable3.setCornerRadius(ScreenUtils.dp2px(host4.context(), 8.0f));
                                gradientDrawable3.setStroke(ScreenUtils.dp2px(host4.context(), 2.0f), host4.costColor(i4));
                                gradientDrawable3.setColor(855638016);
                                frameLayout.setBackground(gradientDrawable3);
                                frameLayout.setPadding(ScreenUtils.dp2px(host4.context(), 2.0f), ScreenUtils.dp2px(host4.context(), 2.0f), ScreenUtils.dp2px(host4.context(), 2.0f), ScreenUtils.dp2px(host4.context(), 2.0f));
                                frameLayout.setElevation(ScreenUtils.dp2px(host4.context(), 3.0f));
                                ImageView imageView = new ImageView(host4.context());
                                imageView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                GradientDrawable gradientDrawable4 = new GradientDrawable();
                                gradientDrawable4.setShape(0);
                                gradientDrawable4.setCornerRadius(ScreenUtils.dp2px(host4.context(), 6.0f));
                                gradientDrawable4.setColor(-15066578);
                                imageView.setBackground(gradientDrawable4);
                                imageView.setPadding(0, 0, 0, 0);
                                imageView.setClipToOutline(true);
                                imageView.setAlpha(HeroPoolCountTier.soldOut(heroData.currentCount) ? 0.5f : 1.0f);
                                host4.loadHeroAvatar(imageView, heroData.englishName);
                                TextView textView3 = new TextView(host4.context());
                                FrameLayout.LayoutParams layoutParams4 = new FrameLayout.LayoutParams(-2, -2);
                                layoutParams4.gravity = 8388691;
                                Button button2 = button;
                                layoutParams4.setMargins(ScreenUtils.dp2px(host4.context(), 1.0f), 0, 0, ScreenUtils.dp2px(host4.context(), 1.0f));
                                textView3.setLayoutParams(layoutParams4);
                                textView3.setText(String.valueOf(i4));
                                textView3.setTextSize(8.0f);
                                textView3.setTextColor(-1);
                                textView3.setGravity(17);
                                ArrayList arrayList8 = arrayList;
                                textView3.setPadding(ScreenUtils.dp2px(host4.context(), 3.0f), ScreenUtils.dp2px(host4.context(), 1.0f), ScreenUtils.dp2px(host4.context(), 3.0f), ScreenUtils.dp2px(host4.context(), 1.0f));
                                GradientDrawable gradientDrawable5 = new GradientDrawable();
                                gradientDrawable5.setShape(0);
                                gradientDrawable5.setCornerRadius(ScreenUtils.dp2px(host4.context(), 4.0f));
                                gradientDrawable5.setColor(host4.costColor(i4));
                                textView3.setBackground(gradientDrawable5);
                                textView3.setElevation(ScreenUtils.dp2px(host4.context(), 2.0f));
                                final TextView textView4 = new TextView(host4.context());
                                FrameLayout.LayoutParams layoutParams5 = new FrameLayout.LayoutParams(ScreenUtils.dp2px(host4.context(), 18.0f), ScreenUtils.dp2px(host4.context(), 18.0f));
                                layoutParams5.gravity = 8388661;
                                layoutParams5.setMargins(0, ScreenUtils.dp2px(host4.context(), -2.0f), ScreenUtils.dp2px(host4.context(), -2.0f), 0);
                                textView4.setLayoutParams(layoutParams5);
                                textView4.setGravity(17);
                                textView4.setText("✓");
                                textView4.setTextSize(10.0f);
                                textView4.setTextColor(-1);
                                GradientDrawable gradientDrawable6 = new GradientDrawable();
                                gradientDrawable6.setShape(1);
                                gradientDrawable6.setGradientType(1);
                                gradientDrawable6.setGradientRadius(ScreenUtils.dp2px(host4.context(), 12.0f));
                                gradientDrawable6.setColors(new int[]{GameHudPalette.SUCCESS, -16733628});
                                gradientDrawable6.setStroke(ScreenUtils.dp2px(host4.context(), 1.0f), -1);
                                textView4.setBackground(gradientDrawable6);
                                textView4.setElevation(ScreenUtils.dp2px(host4.context(), 4.0f));
                                boolean zContains = host4.selectedHeroIds().contains(Integer.valueOf(heroData.heroId));
                                textView4.setVisibility(zContains ? 0 : 8);
                                if (zContains) {
                                    Context context = host4.context();
                                    c = CharCompanionObject.MIN_VALUE;
                                    gradientDrawable3.setStroke(ScreenUtils.dp2px(context, 3.0f), GameHudPalette.SUCCESS);
                                } else {
                                    c = CharCompanionObject.MIN_VALUE;
                                }
                                final int i6 = heroData.heroId;
                                final String str5 = heroData.chineseName;
                                final int iCostColor2 = host4.costColor(i4);
                                LinearLayout linearLayout5 = linearLayout3;
                                int i7 = i4;
                                final Host host5 = host4;
                                str2 = str4;
                                try {
                                    frameLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.DeckChildWindowContent$$ExternalSyntheticLambda1
                                        @Override // android.view.View.OnClickListener
                                        public final void onClick(View view) {
                                            DeckChildWindowContent.lambda$update$1(host5, i6, textView4, gradientDrawable3, iCostColor2, str5, view);
                                        }
                                    });
                                    frameLayout.addView(imageView);
                                    frameLayout.addView(textView3);
                                    frameLayout.addView(textView4);
                                    arrayList2.add(frameLayout);
                                    arrayList3.add(textView4);
                                    arrayList7.add(gradientDrawable3);
                                    arrayList6.add(imageView);
                                    TextView textView5 = new TextView(host.context());
                                    textView5.setTextSize(9.0f);
                                    textView5.setTypeface(null, 1);
                                    textView5.setGravity(17);
                                    textView5.setPadding(ScreenUtils.dp2px(host.context(), 6.0f), ScreenUtils.dp2px(host.context(), 1.0f), ScreenUtils.dp2px(host.context(), 6.0f), ScreenUtils.dp2px(host.context(), 1.0f));
                                    LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(-2, -2);
                                    layoutParams6.topMargin = ScreenUtils.dp2px(host.context(), 2.0f);
                                    layoutParams6.gravity = 1;
                                    textView5.setLayoutParams(layoutParams6);
                                    GradientDrawable gradientDrawable7 = new GradientDrawable();
                                    gradientDrawable7.setCornerRadius(ScreenUtils.dp2px(host.context(), 8.0f));
                                    int iOf = HeroPoolCountTier.of(heroData.currentCount, heroData.totalCount);
                                    if (iOf == 0) {
                                        textView5.setText("售罄");
                                        textView5.setTextColor(-39322);
                                        gradientDrawable7.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                                        gradientDrawable7.setColors(new int[]{-859045888, -863502336});
                                        gradientDrawable7.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), -1996536764);
                                    } else if (iOf == 1) {
                                        textView5.setText(heroData.currentCount + "/" + heroData.totalCount);
                                        textView5.setTextColor(-8960);
                                        gradientDrawable7.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                                        gradientDrawable7.setColors(new int[]{-1144232448, -1148693248});
                                        gradientDrawable7.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), 1728031232);
                                    } else {
                                        textView5.setText(heroData.currentCount + "/" + heroData.totalCount);
                                        textView5.setTextColor(-5579265);
                                        gradientDrawable7.setColor(1427186227);
                                        f = 1.0f;
                                        gradientDrawable7.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), 872415231);
                                        textView5.setBackground(gradientDrawable7);
                                        textView5.setShadowLayer(2.0f, 0.0f, f, -1442840576);
                                        linearLayout4.addView(frameLayout);
                                        linearLayout4.addView(textView5);
                                        gridLayout2.addView(linearLayout4);
                                        host4 = host;
                                        gridLayout = gridLayout2;
                                        arrayList5 = arrayList6;
                                        arrayList4 = arrayList7;
                                        i4 = i7;
                                        str4 = str2;
                                        linearLayout3 = linearLayout5;
                                        listMerge = list5;
                                        mapGroup = map2;
                                        button = button2;
                                        arrayList = arrayList8;
                                    }
                                    f = 1.0f;
                                    textView5.setBackground(gradientDrawable7);
                                    textView5.setShadowLayer(2.0f, 0.0f, f, -1442840576);
                                    linearLayout4.addView(frameLayout);
                                    linearLayout4.addView(textView5);
                                    gridLayout2.addView(linearLayout4);
                                    host4 = host;
                                    gridLayout = gridLayout2;
                                    arrayList5 = arrayList6;
                                    arrayList4 = arrayList7;
                                    i4 = i7;
                                    str4 = str2;
                                    linearLayout3 = linearLayout5;
                                    listMerge = list5;
                                    mapGroup = map2;
                                    button = button2;
                                    arrayList = arrayList8;
                                } catch (Exception e) {
                                    e = e;
                                    str = str2;
                                    Log.e(str, "更新子窗口内容失败", e);
                                }
                            }
                            str3 = str4;
                            LinearLayout linearLayout6 = linearLayout3;
                            list3 = listMerge;
                            map = mapGroup;
                            final Button button3 = button;
                            final int i8 = i4;
                            final ArrayList arrayList9 = arrayList;
                            final ArrayList arrayList10 = arrayList4;
                            linearLayout6.addView(gridLayout);
                            final Host host6 = host;
                            i = i8;
                            button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.DeckChildWindowContent$$ExternalSyntheticLambda2
                                @Override // android.view.View.OnClickListener
                                public final void onClick(View view) {
                                    DeckChildWindowContent.lambda$update$2(arrayList9, host6, arrayList3, arrayList10, i8, button3, view);
                                }
                            });
                            host3 = host6;
                            linearLayout = linearLayout6;
                        } else {
                            str3 = str4;
                            LinearLayout linearLayout7 = linearLayout2;
                            list3 = listMerge;
                            map = mapGroup;
                            i = i4;
                            int i9 = -2;
                            int i10 = 17;
                            GridLayout gridLayout3 = new GridLayout(host3.context());
                            gridLayout3.setColumnCount(3);
                            LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams7.bottomMargin = ScreenUtils.dp2px(host3.context(), 2.0f);
                            gridLayout3.setLayoutParams(layoutParams7);
                            GradientDrawable gradientDrawable8 = new GradientDrawable();
                            gradientDrawable8.setShape(0);
                            gradientDrawable8.setCornerRadius(ScreenUtils.dp2px(host3.context(), 8.0f));
                            gradientDrawable8.setColor((iCostColor & i2) | 218103808);
                            gridLayout3.setBackground(gradientDrawable8);
                            gridLayout3.setPadding(ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 4.0f));
                            final ArrayList arrayList11 = new ArrayList();
                            for (final HeroData heroData2 : list4) {
                                final LinearLayout linearLayout8 = new LinearLayout(host3.context());
                                linearLayout8.setOrientation(1);
                                linearLayout8.setGravity(i10);
                                linearLayout8.setClickable(true);
                                linearLayout8.setFocusable(true);
                                GridLayout.LayoutParams layoutParams8 = new GridLayout.LayoutParams();
                                layoutParams8.width = 0;
                                layoutParams8.height = i9;
                                layoutParams8.columnSpec = GridLayout.spec(Integer.MIN_VALUE, 1.0f);
                                layoutParams8.setMargins(ScreenUtils.dp2px(host3.context(), 2.0f), ScreenUtils.dp2px(host3.context(), 2.0f), ScreenUtils.dp2px(host3.context(), 2.0f), ScreenUtils.dp2px(host3.context(), 2.0f));
                                linearLayout8.setLayoutParams(layoutParams8);
                                boolean zContains2 = host3.selectedHeroIds().contains(Integer.valueOf(heroData2.heroId));
                                int iCostColor3 = host3.costColor(i);
                                GradientDrawable gradientDrawable9 = new GradientDrawable();
                                gradientDrawable9.setShape(0);
                                gradientDrawable9.setCornerRadius(ScreenUtils.dp2px(host3.context(), 6.0f));
                                if (zContains2) {
                                    gradientDrawable9.setColor(1140916104);
                                    gradientDrawable9.setStroke(ScreenUtils.dp2px(host3.context(), 1.0f), GameHudPalette.SUCCESS);
                                } else {
                                    gradientDrawable9.setColor(587202559);
                                    gradientDrawable9.setStroke(ScreenUtils.dp2px(host3.context(), 1.0f), (iCostColor3 & i2) | 855638016);
                                }
                                linearLayout8.setBackground(gradientDrawable9);
                                linearLayout8.setPadding(ScreenUtils.dp2px(host3.context(), 6.0f), ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 6.0f), ScreenUtils.dp2px(host3.context(), 4.0f));
                                TextView textView6 = new TextView(host3.context());
                                textView6.setText(HeroDisplayName.labelOrId(heroData2));
                                textView6.setTextSize(11.0f);
                                textView6.setTextColor(-1118482);
                                textView6.setGravity(17);
                                textView6.setTypeface(null, 1);
                                textView6.setShadowLayer(2.0f, 0.0f, 0.0f, iCostColor3);
                                TextView textView7 = new TextView(host3.context());
                                textView7.setTextSize(10.0f);
                                textView7.setGravity(17);
                                textView7.setTypeface(null, 1);
                                int iOf2 = HeroPoolCountTier.of(heroData2.currentCount, heroData2.totalCount);
                                if (iOf2 == 0) {
                                    textView7.setText("✕ 售罄");
                                    textView7.setTextColor(-43691);
                                } else if (iOf2 == 1) {
                                    textView7.setText(String.format("⚠ %d/%d", Integer.valueOf(heroData2.currentCount), Integer.valueOf(heroData2.totalCount)));
                                    textView7.setTextColor(-22016);
                                } else {
                                    textView7.setText(String.format("%d/%d", Integer.valueOf(heroData2.currentCount), Integer.valueOf(heroData2.totalCount)));
                                    textView7.setTextColor(-16720504);
                                }
                                linearLayout8.addView(textView6);
                                linearLayout8.addView(textView7);
                                final int iCostColor4 = host3.costColor(i);
                                linearLayout8.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.DeckChildWindowContent$$ExternalSyntheticLambda3
                                    @Override // android.view.View.OnClickListener
                                    public final void onClick(View view) {
                                        DeckChildWindowContent.lambda$update$3(host3, heroData2, iCostColor4, linearLayout8, view);
                                    }
                                });
                                arrayList11.add(linearLayout8);
                                gridLayout3.addView(linearLayout8);
                                i9 = -2;
                                i10 = 17;
                            }
                            linearLayout7.addView(gridLayout3);
                            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.DeckChildWindowContent$$ExternalSyntheticLambda4
                                @Override // android.view.View.OnClickListener
                                public final void onClick(View view) {
                                    DeckChildWindowContent.lambda$update$4(arrayList, host3, i, arrayList11, button, view);
                                }
                            });
                            host3 = host3;
                            linearLayout = linearLayout7;
                        }
                        View view = new View(host3.context());
                        LinearLayout.LayoutParams layoutParams9 = new LinearLayout.LayoutParams(-1, ScreenUtils.dp2px(host3.context(), 1.0f));
                        layoutParams9.topMargin = ScreenUtils.dp2px(host3.context(), 6.0f);
                        layoutParams9.bottomMargin = ScreenUtils.dp2px(host3.context(), 2.0f);
                        layoutParams9.leftMargin = ScreenUtils.dp2px(host3.context(), 20.0f);
                        layoutParams9.rightMargin = ScreenUtils.dp2px(host3.context(), 20.0f);
                        view.setLayoutParams(layoutParams9);
                        GradientDrawable gradientDrawable10 = new GradientDrawable();
                        gradientDrawable10.setShape(0);
                        gradientDrawable10.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                        int i11 = i2;
                        gradientDrawable10.setColors(new int[]{i11, (host3.costColor(i) & i2) | 1426063360, i11});
                        view.setBackground(gradientDrawable10);
                        linearLayout.addView(view);
                    }
                    i4 = i + 1;
                    linearLayout2 = linearLayout;
                    str4 = str3;
                    listMerge = list3;
                    mapGroup = map;
                    i3 = 1;
                    host3 = host3;
                } catch (Exception e2) {
                    e = e2;
                    str2 = str4;
                    str = str2;
                    Log.e(str, "更新子窗口内容失败", e);
                }
            }
            str2 = str4;
            List<HeroData> list6 = listMerge;
            str = str2;
            try {
                Dbg.d(str, String.format("子窗口内容已更新，显示 %d 个英雄 (锁定: %d)", Integer.valueOf(list6.size()), Integer.valueOf(list2 != null ? list2.size() : 0)));
                host3.selectedHeroesData().clear();
                for (HeroData heroData3 : list6) {
                    if (host3.selectedHeroIds().contains(Integer.valueOf(heroData3.heroId))) {
                        host3.selectedHeroesData().add(heroData3);
                    }
                }
                Dbg.d(str, String.format("选中英雄数据已保存: %d 个", Integer.valueOf(host3.selectedHeroesData().size())));
                host3.autoRefreshHandler().post(new Runnable() { // from class: com.android.support.DeckChildWindowContent$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        host3.updateSelectedHeroesWindow();
                    }
                });
            } catch (Exception e3) {
                e = e3;
                Log.e(str, "更新子窗口内容失败", e);
            }
        } catch (Exception e4) {
            e = e4;
            str = TAG;
        }
    }

    static /* synthetic */ void lambda$update$1(Host host, int i, TextView textView, GradientDrawable gradientDrawable, int i2, String str, View view) {
        if (host.selectedHeroIds().contains(Integer.valueOf(i))) {
            host.selectedHeroIds().remove(Integer.valueOf(i));
            textView.setVisibility(8);
            gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 2.0f), i2);
            Dbg.d(TAG, "取消选中英雄: " + str + " (ID: " + i + ")");
        } else {
            host.selectedHeroIds().add(Integer.valueOf(i));
            textView.setVisibility(0);
            gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 3.0f), GameHudPalette.SUCCESS);
            Dbg.d(TAG, "选中英雄: " + str + " (ID: " + i + ")");
        }
        host.setLastHeroCountsSnapshot("");
        host.saveSelectedHeroIdsConfig();
        if (host.selectedHeroesWindow() == null || host.selectedHeroesWindow().getParent() == null) {
            return;
        }
        host.updateSelectedHeroesWindow();
    }

    static /* synthetic */ void lambda$update$2(List list, Host host, List list2, List list3, int i, Button button, View view) {
        Iterator it = list.iterator();
        while (true) {
            if (it.hasNext()) {
                if (!host.selectedHeroIds().contains(Integer.valueOf(((HeroData) it.next()).heroId))) {
                    for (int i2 = 0; i2 < list.size(); i2++) {
                        host.selectedHeroIds().add(Integer.valueOf(((HeroData) list.get(i2)).heroId));
                        if (i2 < list2.size() && i2 < list3.size()) {
                            ((View) list2.get(i2)).setVisibility(0);
                            ((GradientDrawable) list3.get(i2)).setStroke(ScreenUtils.dp2px(host.context(), 3.0f), GameHudPalette.SUCCESS);
                        }
                    }
                    button.setText("✕ 取消");
                    button.setTextColor(-39322);
                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(0);
                    gradientDrawable.setCornerRadius(ScreenUtils.dp2px(host.context(), 12.0f));
                    gradientDrawable.setColor(872367172);
                    gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), 1728005188);
                    button.setBackground(gradientDrawable);
                    Dbg.d(TAG, String.format("全选 %d费 英雄，共 %d 个", Integer.valueOf(i), Integer.valueOf(list.size())));
                }
            } else {
                for (int i3 = 0; i3 < list.size(); i3++) {
                    host.selectedHeroIds().remove(Integer.valueOf(((HeroData) list.get(i3)).heroId));
                    if (i3 < list2.size() && i3 < list3.size()) {
                        ((View) list2.get(i3)).setVisibility(8);
                        ((GradientDrawable) list3.get(i3)).setStroke(ScreenUtils.dp2px(host.context(), 2.0f), host.costColor(i));
                    }
                }
                button.setText("✓ 全选");
                button.setTextColor(-1);
                GradientDrawable gradientDrawable2 = new GradientDrawable();
                gradientDrawable2.setShape(0);
                gradientDrawable2.setCornerRadius(ScreenUtils.dp2px(host.context(), 12.0f));
                int iCostColor = host.costColor(i) & ViewCompat.MEASURED_SIZE_MASK;
                gradientDrawable2.setColor(1140850688 | iCostColor);
                gradientDrawable2.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), iCostColor | DsTokens.RIPPLE_DARK);
                button.setBackground(gradientDrawable2);
                Dbg.d(TAG, String.format("取消全选 %d费 英雄，共 %d 个", Integer.valueOf(i), Integer.valueOf(list.size())));
            }
        }
        host.setLastHeroCountsSnapshot("");
        host.saveSelectedHeroIdsConfig();
        if (host.selectedHeroesWindow() == null || host.selectedHeroesWindow().getParent() == null) {
            return;
        }
        host.updateSelectedHeroesWindow();
    }

    static /* synthetic */ void lambda$update$3(Host host, HeroData heroData, int i, LinearLayout linearLayout, View view) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(host.context(), 6.0f));
        if (host.selectedHeroIds().contains(Integer.valueOf(heroData.heroId))) {
            host.selectedHeroIds().remove(Integer.valueOf(heroData.heroId));
            gradientDrawable.setColor(587202559);
            gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), (i & ViewCompat.MEASURED_SIZE_MASK) | 855638016);
            linearLayout.setBackground(gradientDrawable);
            Dbg.d(TAG, "取消选中英雄: " + heroData.chineseName + " (ID:" + heroData.heroId + ")");
        } else {
            host.selectedHeroIds().add(Integer.valueOf(heroData.heroId));
            gradientDrawable.setColor(1140916104);
            gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), GameHudPalette.SUCCESS);
            linearLayout.setBackground(gradientDrawable);
            Dbg.d(TAG, "选中英雄: " + heroData.chineseName + " (ID:" + heroData.heroId + ")");
        }
        host.saveSelectedHeroIdsConfig();
        if (host.selectedHeroesWindow() == null || host.selectedHeroesWindow().getParent() == null) {
            return;
        }
        host.updateSelectedHeroesWindow();
    }

    static /* synthetic */ void lambda$update$4(List list, Host host, int i, List list2, Button button, View view) {
        boolean z;
        Iterator it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                z = true;
                break;
            }
            if (!host.selectedHeroIds().contains(Integer.valueOf(((HeroData) it.next()).heroId))) {
                z = false;
                break;
            }
        }
        int iCostColor = host.costColor(i);
        if (z) {
            for (int i2 = 0; i2 < list.size(); i2++) {
                host.selectedHeroIds().remove(Integer.valueOf(((HeroData) list.get(i2)).heroId));
                if (i2 < list2.size()) {
                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(0);
                    gradientDrawable.setCornerRadius(ScreenUtils.dp2px(host.context(), 6.0f));
                    gradientDrawable.setColor(587202559);
                    gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), (16777215 & iCostColor) | 855638016);
                    ((LinearLayout) list2.get(i2)).setBackground(gradientDrawable);
                }
            }
            button.setText("✓ 全选");
            button.setTextColor(-1);
            Dbg.d(TAG, String.format("取消全选 %d费 英雄，共 %d 个", Integer.valueOf(i), Integer.valueOf(list.size())));
        } else {
            for (int i3 = 0; i3 < list.size(); i3++) {
                host.selectedHeroIds().add(Integer.valueOf(((HeroData) list.get(i3)).heroId));
                if (i3 < list2.size()) {
                    GradientDrawable gradientDrawable2 = new GradientDrawable();
                    gradientDrawable2.setShape(0);
                    gradientDrawable2.setCornerRadius(ScreenUtils.dp2px(host.context(), 6.0f));
                    gradientDrawable2.setColor(1140916104);
                    gradientDrawable2.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), GameHudPalette.SUCCESS);
                    ((LinearLayout) list2.get(i3)).setBackground(gradientDrawable2);
                }
            }
            button.setText("✕ 取消");
            button.setTextColor(-39322);
            Dbg.d(TAG, String.format("全选 %d费 英雄，共 %d 个", Integer.valueOf(i), Integer.valueOf(list.size())));
        }
        host.setLastHeroCountsSnapshot("");
        host.saveSelectedHeroIdsConfig();
        if (host.selectedHeroesWindow() == null || host.selectedHeroesWindow().getParent() == null) {
            return;
        }
        host.updateSelectedHeroesWindow();
    }
}
