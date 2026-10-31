package com.android.support;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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

/* loaded from: classes.dex */
final class AutoPickHeroWindowContent {
    private static final String TAG = "FloatingWindowService";
    private static boolean loggedNonemptyRoster;

    interface Host {
        Set<Integer> activePickIds();

        Map<Integer, Integer> activeStarTargets();

        View autoPickHeroWindow();

        LinearLayout buildCostGroupCardShell(int i);

        void buildDeckEmptyState(LinearLayout linearLayout);

        Context context();

        int costColor(int i);

        int countSelectedInCost(List<HeroData> list, Set<Integer> set);

        List<HeroData> currentAllHeroesData();

        List<HeroData> currentLockedHeroesData();

        int initialTargetStar(int i);

        void loadHeroAvatar(ImageView imageView, String str);

        void saveActivePickIds();

        void saveUserExcludedHeroIds();

        int starColor(int i);

        void updateStarBadge(TextView textView, int i);

        Set<Integer> userExcludedHeroIds();
    }

    private AutoPickHeroWindowContent() {
    }

    static boolean update(final Host host) {
        String str;
        String str2;
        int i;
        String str3;
        Map<Integer, List<HeroData>> map;
        LinearLayout linearLayout;
        Host host2 = host;
        String str4 = TAG;
        if (host2.autoPickHeroWindow() == null) {
            return false;
        }
        try {
            LinearLayout linearLayoutContent = OverlayScrollDetailList.content(host2.autoPickHeroWindow());
            if (linearLayoutContent == null) {
                Log.e(TAG, "自定义拿牌窗口内容布局未找到");
                return false;
            }
            LinearLayout linearLayoutDetail = OverlayScrollDetailList.detail(linearLayoutContent);
            if (linearLayoutDetail == null) {
                Log.e(TAG, "自定义拿牌窗口详情列表未找到");
                return false;
            }
            linearLayoutDetail.removeAllViews();
            int i2 = 1;
            if (host2.currentAllHeroesData().isEmpty() && host2.currentLockedHeroesData().isEmpty()) {
                host2.buildDeckEmptyState(linearLayoutDetail);
                return true;
            }
            Map<Integer, List<HeroData>> mapGroup = HeroCostGroups.group(HeroPoolMerge.merge(host2.currentAllHeroesData(), host2.currentLockedHeroesData()));
            final int i3 = 1;
            Host host3 = host2;
            LinearLayout linearLayout2 = linearLayoutDetail;
            while (i3 <= 6) {
                try {
                    if (mapGroup.containsKey(Integer.valueOf(i3))) {
                        List<HeroData> list = mapGroup.get(Integer.valueOf(i3));
                        final int iCostColor = host3.costColor(i3);
                        LinearLayout linearLayoutBuildCostGroupCardShell = host3.buildCostGroupCardShell(i3);
                        TextView textView = new TextView(host3.context());
                        textView.setText(DeckCostChrome.icon(i3));
                        textView.setTextSize(14.0f);
                        textView.setTextColor(iCostColor);
                        textView.setShadowLayer(4.0f, 0.0f, 0.0f, iCostColor);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams.rightMargin = ScreenUtils.dp2px(host3.context(), 6.0f);
                        textView.setLayoutParams(layoutParams);
                        final TextView textView2 = new TextView(host3.context());
                        int iCountSelectedInCost = host3.countSelectedInCost(list, host3.activePickIds());
                        textView2.setText(i3 + "费  " + DeckCostChrome.label(i3) + "  (" + list.size() + ")" + (iCountSelectedInCost > 0 ? "  已选" + iCountSelectedInCost : ""));
                        textView2.setTextSize(13.0f);
                        textView2.setTextColor(iCostColor);
                        textView2.setTypeface(null, i2);
                        textView2.setShadowLayer(3.0f, 0.0f, 0.0f, iCostColor);
                        textView2.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
                        Iterator<HeroData> it = list.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                i = i2;
                                break;
                            }
                            if (!host3.activePickIds().contains(Integer.valueOf(it.next().heroId))) {
                                i = 0;
                                break;
                            }
                        }
                        Button button = new Button(host3.context());
                        button.setText(i != 0 ? "✕ 取消" : "✓ 全选");
                        button.setTextSize(10.0f);
                        button.setTextColor(i != 0 ? -39322 : -1);
                        int i4 = i;
                        button.setPadding(ScreenUtils.dp2px(host3.context(), 10.0f), ScreenUtils.dp2px(host3.context(), 2.0f), ScreenUtils.dp2px(host3.context(), 10.0f), ScreenUtils.dp2px(host3.context(), 2.0f));
                        button.setMinHeight(0);
                        button.setMinimumHeight(0);
                        GradientDrawable gradientDrawable = new GradientDrawable();
                        gradientDrawable.setShape(0);
                        gradientDrawable.setCornerRadius(ScreenUtils.dp2px(host3.context(), 12.0f));
                        if (i4 != 0) {
                            gradientDrawable.setColor(872367172);
                            gradientDrawable.setStroke(ScreenUtils.dp2px(host3.context(), 1.0f), 1728005188);
                        } else {
                            int i5 = iCostColor & ViewCompat.MEASURED_SIZE_MASK;
                            gradientDrawable.setColor(1140850688 | i5);
                            gradientDrawable.setStroke(ScreenUtils.dp2px(host3.context(), 1.0f), i5 | DsTokens.RIPPLE_DARK);
                        }
                        button.setBackground(gradientDrawable);
                        linearLayoutBuildCostGroupCardShell.addView(textView);
                        linearLayoutBuildCostGroupCardShell.addView(textView2);
                        linearLayoutBuildCostGroupCardShell.addView(button);
                        linearLayout2.addView(linearLayoutBuildCostGroupCardShell);
                        GridLayout gridLayout = new GridLayout(host3.context());
                        gridLayout.setColumnCount(5);
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams2.bottomMargin = ScreenUtils.dp2px(host3.context(), 2.0f);
                        gridLayout.setLayoutParams(layoutParams2);
                        GradientDrawable gradientDrawable2 = new GradientDrawable();
                        gradientDrawable2.setShape(0);
                        gradientDrawable2.setCornerRadius(ScreenUtils.dp2px(host3.context(), 8.0f));
                        gradientDrawable2.setColor((iCostColor & ViewCompat.MEASURED_SIZE_MASK) | 218103808);
                        gridLayout.setBackground(gradientDrawable2);
                        gridLayout.setPadding(ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 4.0f), ScreenUtils.dp2px(host3.context(), 4.0f));
                        ArrayList arrayList = new ArrayList();
                        final ArrayList arrayList2 = new ArrayList();
                        final ArrayList arrayList3 = new ArrayList();
                        final Host host4 = host3;
                        GridLayout gridLayout2 = gridLayout;
                        LinearLayout linearLayout3 = linearLayout2;
                        for (final HeroData heroData : list) {
                            LinearLayout linearLayout4 = new LinearLayout(host4.context());
                            linearLayout4.setOrientation(1);
                            linearLayout4.setGravity(17);
                            GridLayout.LayoutParams layoutParams3 = new GridLayout.LayoutParams();
                            ArrayList arrayList4 = arrayList2;
                            ArrayList arrayList5 = arrayList3;
                            layoutParams3.width = ScreenUtils.dp2px(host4.context(), 50.0f);
                            layoutParams3.height = -2;
                            final List<HeroData> list2 = list;
                            TextView textView3 = textView2;
                            int i6 = i3;
                            layoutParams3.setMargins(ScreenUtils.dp2px(host4.context(), 2.0f), ScreenUtils.dp2px(host4.context(), 2.0f), ScreenUtils.dp2px(host4.context(), 2.0f), ScreenUtils.dp2px(host4.context(), 2.0f));
                            linearLayout4.setLayoutParams(layoutParams3);
                            FrameLayout frameLayout = new FrameLayout(host4.context());
                            frameLayout.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtils.dp2px(host4.context(), 46.0f), ScreenUtils.dp2px(host4.context(), 46.0f)));
                            frameLayout.setClickable(true);
                            frameLayout.setFocusable(true);
                            final GradientDrawable gradientDrawable3 = new GradientDrawable();
                            gradientDrawable3.setShape(0);
                            gradientDrawable3.setCornerRadius(ScreenUtils.dp2px(host4.context(), 8.0f));
                            gradientDrawable3.setStroke(ScreenUtils.dp2px(host4.context(), 2.0f), iCostColor);
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
                            imageView.setClipToOutline(true);
                            host4.loadHeroAvatar(imageView, heroData.englishName);
                            TextView textView4 = new TextView(host4.context());
                            FrameLayout.LayoutParams layoutParams4 = new FrameLayout.LayoutParams(-2, -2);
                            layoutParams4.gravity = 8388691;
                            layoutParams4.setMargins(ScreenUtils.dp2px(host4.context(), 1.0f), 0, 0, ScreenUtils.dp2px(host4.context(), 1.0f));
                            textView4.setLayoutParams(layoutParams4);
                            textView4.setText(String.valueOf(i6));
                            textView4.setTextSize(8.0f);
                            textView4.setTextColor(-1);
                            textView4.setGravity(17);
                            Map<Integer, List<HeroData>> map2 = mapGroup;
                            textView4.setPadding(ScreenUtils.dp2px(host4.context(), 3.0f), ScreenUtils.dp2px(host4.context(), 1.0f), ScreenUtils.dp2px(host4.context(), 3.0f), ScreenUtils.dp2px(host4.context(), 1.0f));
                            GradientDrawable gradientDrawable5 = new GradientDrawable();
                            gradientDrawable5.setShape(0);
                            gradientDrawable5.setCornerRadius(ScreenUtils.dp2px(host4.context(), 4.0f));
                            gradientDrawable5.setColor(iCostColor);
                            textView4.setBackground(gradientDrawable5);
                            textView4.setElevation(ScreenUtils.dp2px(host4.context(), 2.0f));
                            final TextView textView5 = new TextView(host4.context());
                            FrameLayout.LayoutParams layoutParams5 = new FrameLayout.LayoutParams(ScreenUtils.dp2px(host4.context(), 18.0f), ScreenUtils.dp2px(host4.context(), 18.0f));
                            layoutParams5.gravity = 8388661;
                            layoutParams5.setMargins(0, ScreenUtils.dp2px(host4.context(), -2.0f), ScreenUtils.dp2px(host4.context(), -2.0f), 0);
                            textView5.setLayoutParams(layoutParams5);
                            textView5.setGravity(17);
                            textView5.setTextSize(10.0f);
                            textView5.setTextColor(-1);
                            textView5.setElevation(ScreenUtils.dp2px(host4.context(), 4.0f));
                            boolean zContains = host4.activePickIds().contains(Integer.valueOf(heroData.heroId));
                            int iIntValue = host4.activeStarTargets().getOrDefault(Integer.valueOf(heroData.heroId), 2).intValue();
                            if (zContains) {
                                host4.updateStarBadge(textView5, iIntValue);
                                textView5.setVisibility(0);
                                gradientDrawable3.setStroke(ScreenUtils.dp2px(host4.context(), 3.0f), host4.starColor(iIntValue));
                            } else {
                                textView5.setVisibility(8);
                            }
                            str2 = str4;
                            GridLayout gridLayout3 = gridLayout2;
                            LinearLayout linearLayout5 = linearLayout3;
                            Button button2 = button;
                            textView2 = textView3;
                            i3 = i6;
                            try {
                                frameLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AutoPickHeroWindowContent$$ExternalSyntheticLambda0
                                    @Override // android.view.View.OnClickListener
                                    public final void onClick(View view) {
                                        AutoPickHeroWindowContent.lambda$update$0(host4, heroData, textView5, gradientDrawable3, iCostColor, list2, textView2, i3, view);
                                    }
                                });
                                frameLayout.addView(imageView);
                                frameLayout.addView(textView4);
                                frameLayout.addView(textView5);
                                TextView textView6 = new TextView(host.context());
                                String strLabelOrId = HeroDisplayName.labelOrId(heroData);
                                if (strLabelOrId.length() > 4) {
                                    strLabelOrId = strLabelOrId.substring(0, 3) + "..";
                                }
                                textView6.setText(strLabelOrId);
                                textView6.setTextSize(9.0f);
                                textView6.setTextColor(-3355444);
                                textView6.setGravity(17);
                                textView6.setSingleLine(true);
                                arrayList.add(frameLayout);
                                arrayList4.add(textView5);
                                arrayList5.add(gradientDrawable3);
                                linearLayout4.addView(frameLayout);
                                linearLayout4.addView(textView6);
                                GridLayout gridLayout4 = gridLayout3;
                                gridLayout4.addView(linearLayout4);
                                list = list2;
                                arrayList2 = arrayList4;
                                arrayList3 = arrayList5;
                                button = button2;
                                str4 = str2;
                                linearLayout3 = linearLayout5;
                                mapGroup = map2;
                                host4 = host;
                                gridLayout2 = gridLayout4;
                            } catch (Exception e) {
                                e = e;
                                str = str2;
                                Log.e(str, "更新自定义拿牌窗口内容失败", e);
                                return false;
                            }
                        }
                        final List<HeroData> list3 = list;
                        str3 = str4;
                        map = mapGroup;
                        final Button button3 = button;
                        button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AutoPickHeroWindowContent$$ExternalSyntheticLambda1
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view) {
                                AutoPickHeroWindowContent.lambda$update$1(list3, host, arrayList2, arrayList3, iCostColor, button3, textView2, i3, view);
                            }
                        });
                        linearLayout = linearLayout3;
                        linearLayout.addView(gridLayout2);
                    } else {
                        str3 = str4;
                        linearLayout = linearLayout2;
                        map = mapGroup;
                    }
                    i3++;
                    host3 = host;
                    linearLayout2 = linearLayout;
                    str4 = str3;
                    mapGroup = map;
                    i2 = 1;
                } catch (Exception e2) {
                    e = e2;
                    str2 = str4;
                    str = str2;
                    Log.e(str, "更新自定义拿牌窗口内容失败", e);
                    return false;
                }
            }
            str2 = str4;
            str = str2;
            try {
                Dbg.d(str, "[自定义拿牌] 窗口内容已更新（美化版），已选中: " + host.activePickIds().size() + " 个英雄");
                return true;
            } catch (Exception e3) {
                e = e3;
                Log.e(str, "更新自定义拿牌窗口内容失败", e);
                return false;
            }
        } catch (Exception e4) {
            e = e4;
            str = TAG;
        }
    }

    static /* synthetic */ void lambda$update$0(Host host, HeroData heroData, TextView textView, GradientDrawable gradientDrawable, int i, List list, TextView textView2, int i2, View view) {
        if (!host.activePickIds().contains(Integer.valueOf(heroData.heroId))) {
            int iInitialTargetStar = host.initialTargetStar(heroData.heroId);
            host.activePickIds().add(Integer.valueOf(heroData.heroId));
            host.activeStarTargets().put(Integer.valueOf(heroData.heroId), Integer.valueOf(iInitialTargetStar));
            host.userExcludedHeroIds().remove(Integer.valueOf(heroData.heroId));
            host.updateStarBadge(textView, iInitialTargetStar);
            textView.setVisibility(0);
            gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 3.0f), host.starColor(iInitialTargetStar));
            Dbg.d(TAG, "[自定义拿牌] 选中: " + heroData.chineseName + " 目标" + iInitialTargetStar + "★");
        } else {
            int iIntValue = host.activeStarTargets().getOrDefault(Integer.valueOf(heroData.heroId), 2).intValue();
            if (iIntValue == 2) {
                host.activeStarTargets().put(Integer.valueOf(heroData.heroId), 3);
                host.updateStarBadge(textView, 3);
                gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 3.0f), host.starColor(3));
                Dbg.d(TAG, "[自定义拿牌] " + heroData.chineseName + " → 3★");
            } else if (iIntValue == 3) {
                host.activeStarTargets().put(Integer.valueOf(heroData.heroId), 1);
                host.updateStarBadge(textView, 1);
                gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 3.0f), host.starColor(1));
                Dbg.d(TAG, "[自定义拿牌] " + heroData.chineseName + " → 1★");
            } else {
                host.activePickIds().remove(Integer.valueOf(heroData.heroId));
                host.activeStarTargets().remove(Integer.valueOf(heroData.heroId));
                host.userExcludedHeroIds().remove(Integer.valueOf(heroData.heroId));
                textView.setVisibility(8);
                gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 2.0f), i);
                Dbg.d(TAG, "[自定义拿牌] 取消: " + heroData.chineseName);
            }
        }
        int iCountSelectedInCost = host.countSelectedInCost(list, host.activePickIds());
        textView2.setText(i2 + "费  " + DeckCostChrome.label(i2) + "  (" + list.size() + ")" + (iCountSelectedInCost > 0 ? "  已选" + iCountSelectedInCost : ""));
        host.saveActivePickIds();
        host.saveUserExcludedHeroIds();
    }

    static /* synthetic */ void lambda$update$1(List list, Host host, List list2, List list3, int i, Button button, TextView textView, int i2, View view) {
        Iterator it = list.iterator();
        while (true) {
            if (it.hasNext()) {
                if (!host.activePickIds().contains(Integer.valueOf(((HeroData) it.next()).heroId))) {
                    for (int i3 = 0; i3 < list.size(); i3++) {
                        int i4 = ((HeroData) list.get(i3)).heroId;
                        host.activePickIds().add(Integer.valueOf(i4));
                        host.userExcludedHeroIds().remove(Integer.valueOf(i4));
                        if (!host.activeStarTargets().containsKey(Integer.valueOf(i4))) {
                            host.activeStarTargets().put(Integer.valueOf(i4), Integer.valueOf(host.initialTargetStar(i4)));
                        }
                        int iIntValue = host.activeStarTargets().getOrDefault(Integer.valueOf(i4), 2).intValue();
                        host.updateStarBadge((TextView) list2.get(i3), iIntValue);
                        ((View) list2.get(i3)).setVisibility(0);
                        ((GradientDrawable) list3.get(i3)).setStroke(ScreenUtils.dp2px(host.context(), 3.0f), host.starColor(iIntValue));
                    }
                    button.setText("✕ 取消");
                    button.setTextColor(-39322);
                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(0);
                    gradientDrawable.setCornerRadius(ScreenUtils.dp2px(host.context(), 12.0f));
                    gradientDrawable.setColor(872367172);
                    gradientDrawable.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), 1728005188);
                    button.setBackground(gradientDrawable);
                }
            } else {
                for (int i5 = 0; i5 < list.size(); i5++) {
                    host.activePickIds().remove(Integer.valueOf(((HeroData) list.get(i5)).heroId));
                    host.activeStarTargets().remove(Integer.valueOf(((HeroData) list.get(i5)).heroId));
                    ((View) list2.get(i5)).setVisibility(8);
                    ((GradientDrawable) list3.get(i5)).setStroke(ScreenUtils.dp2px(host.context(), 2.0f), i);
                }
                button.setText("✓ 全选");
                button.setTextColor(-1);
                GradientDrawable gradientDrawable2 = new GradientDrawable();
                gradientDrawable2.setShape(0);
                gradientDrawable2.setCornerRadius(ScreenUtils.dp2px(host.context(), 12.0f));
                int i6 = 16777215 & i;
                gradientDrawable2.setColor(1140850688 | i6);
                gradientDrawable2.setStroke(ScreenUtils.dp2px(host.context(), 1.0f), i6 | DsTokens.RIPPLE_DARK);
                button.setBackground(gradientDrawable2);
            }
        }
        int iCountSelectedInCost = host.countSelectedInCost(list, host.activePickIds());
        textView.setText(i2 + "费  " + DeckCostChrome.label(i2) + "  (" + list.size() + ")" + (iCountSelectedInCost > 0 ? "  已选" + iCountSelectedInCost : ""));
        host.saveActivePickIds();
        host.saveUserExcludedHeroIds();
    }
}
