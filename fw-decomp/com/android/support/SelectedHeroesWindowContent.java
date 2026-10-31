package com.android.support;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
final class SelectedHeroesWindowContent {
    private static final String TAG = "FloatingWindowService";

    interface Host {
        boolean addTrustedVisualOverlay(View view, WindowManager.LayoutParams layoutParams, String str);

        void applySelectedHeroesHostPosition(View view);

        boolean attachSelectedHeroesToVisualHost();

        int cachedDp1s();

        void constrainWindowBounds(WindowManager.LayoutParams layoutParams, View view);

        Context context();

        FrameLayout createHeroAvatarView(HeroData heroData);

        List<HeroData> currentAllHeroesData();

        void ensureSelectedHeroesDpCache();

        boolean exitSuppressed();

        int heroDisplayColumns();

        int heroDisplayLayout();

        int heroDisplayRows();

        boolean isGameHudSessionReady();

        boolean isSelectedHeroesHosted();

        boolean isSelectedHeroesVisible();

        long lastCountUpdateTime();

        String lastHeroCountsSnapshot();

        String lastHeroStructureSnapshot();

        WindowManager.LayoutParams paramsSelectedHeroes();

        Set<Integer> selectedHeroIds();

        float selectedHeroesScale();

        View selectedHeroesWindow();

        void setLastCountUpdateTime(long j);

        void setLastHeroCountsSnapshot(String str);

        void setLastHeroStructureSnapshot(String str);

        void syncSelectedHeroesVisualVisibility(View view, boolean z);

        void updateHeroCountTexts();

        boolean updateTrustedVisualOverlay(View view, WindowManager.LayoutParams layoutParams, String str);

        WindowManager windowManager();
    }

    private SelectedHeroesWindowContent() {
    }

    static void update(Host host) {
        int i;
        if (host.selectedHeroesWindow() == null || !(host.selectedHeroesWindow() instanceof LinearLayout)) {
            return;
        }
        host.ensureSelectedHeroesDpCache();
        LinearLayout linearLayout = (LinearLayout) host.selectedHeroesWindow();
        if (linearLayout.getParent() == null && host.paramsSelectedHeroes() != null && host.windowManager() != null) {
            try {
                if (!host.attachSelectedHeroesToVisualHost()) {
                    host.addTrustedVisualOverlay(linearLayout, host.paramsSelectedHeroes(), "selected-heroes");
                }
                Dbg.d(TAG, "选中英雄窗口已创建，初始位置: X=" + host.paramsSelectedHeroes().x + ", Y=" + host.paramsSelectedHeroes().y);
            } catch (Exception e) {
                Log.e(TAG, "创建选中英雄窗口失败", e);
                return;
            }
        }
        HashMap map = new HashMap();
        ArrayList arrayList = new ArrayList();
        if (host.currentAllHeroesData().isEmpty() && !host.selectedHeroIds().isEmpty()) {
            Dbg.d(TAG, "updateSelectedHeroesWindow: 数据未加载但有选中ID，等待数据加载");
            return;
        }
        if (host.currentAllHeroesData().isEmpty() && host.selectedHeroIds().isEmpty()) {
            if (linearLayout.getVisibility() != 8) {
                host.syncSelectedHeroesVisualVisibility(linearLayout, false);
                linearLayout.removeAllViews();
                Dbg.d(TAG, "updateSelectedHeroesWindow: 数据和选中ID都为空，隐藏窗口");
                return;
            }
            return;
        }
        for (HeroData heroData : host.currentAllHeroesData()) {
            if (heroData != null && host.selectedHeroIds().contains(Integer.valueOf(heroData.heroId)) && HeroDisplayName.isKnown(heroData)) {
                arrayList.add(heroData);
                if (!map.containsKey(Integer.valueOf(heroData.cost))) {
                    map.put(Integer.valueOf(heroData.cost), new ArrayList());
                }
                ((List) map.get(Integer.valueOf(heroData.cost))).add(heroData);
            }
        }
        if (host.selectedHeroIds().isEmpty()) {
            if (linearLayout.getVisibility() != 8) {
                host.syncSelectedHeroesVisualVisibility(linearLayout, false);
                Dbg.d(TAG, "updateSelectedHeroesWindow: 无选中ID，隐藏窗口");
            }
            host.setLastHeroCountsSnapshot("");
            arrayList.clear();
            return;
        }
        host.syncSelectedHeroesVisualVisibility(linearLayout, OverlaySessionPaintPolicy.shouldShowSelectedHeroes(host.isSelectedHeroesVisible(), host.isGameHudSessionReady(), host.exitSuppressed(), !arrayList.isEmpty()));
        if (arrayList.isEmpty()) {
            if (linearLayout.getChildCount() > 0) {
                linearLayout.removeAllViews();
                host.setLastHeroCountsSnapshot("");
                Dbg.d(TAG, String.format("updateSelectedHeroesWindow: effectiveSelectedData 为空，已清空视图 (selectedHeroIds=%d个, currentAllHeroesData=%d个)", Integer.valueOf(host.selectedHeroIds().size()), Integer.valueOf(host.currentAllHeroesData().size())));
                return;
            }
            return;
        }
        String strSnapshot = SelectedHeroesLayoutPolicy.snapshot(host.heroDisplayLayout(), host.heroDisplayColumns(), host.heroDisplayRows(), arrayList);
        if (strSnapshot.equals(host.lastHeroCountsSnapshot())) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis - host.lastCountUpdateTime() > 100) {
                host.updateHeroCountTexts();
                host.setLastCountUpdateTime(jCurrentTimeMillis);
                return;
            }
            return;
        }
        String strStructureSnapshot = SelectedHeroesLayoutPolicy.structureSnapshot(host.heroDisplayLayout(), host.heroDisplayColumns(), host.heroDisplayRows(), arrayList);
        if (linearLayout.getChildCount() > 0 && strStructureSnapshot.equals(host.lastHeroStructureSnapshot())) {
            host.setLastHeroCountsSnapshot(strSnapshot);
            host.setLastCountUpdateTime(System.currentTimeMillis());
            host.updateHeroCountTexts();
            return;
        }
        host.setLastHeroCountsSnapshot(strSnapshot);
        host.setLastHeroStructureSnapshot(strStructureSnapshot);
        host.setLastCountUpdateTime(System.currentTimeMillis());
        linearLayout.removeAllViews();
        int iMax = Math.max(1, host.heroDisplayColumns());
        int i2 = -2;
        if (host.heroDisplayLayout() == 0) {
            linearLayout.setOrientation(1);
            i = 0;
            int i3 = 1;
            while (i3 <= 6) {
                List list = (List) map.get(Integer.valueOf(i3));
                if (list != null && !list.isEmpty()) {
                    if (SelectedHeroesLayoutPolicy.rowCapReached(host.heroDisplayRows(), iMax, i)) {
                        break;
                    }
                    int iCappedGroupSize = SelectedHeroesLayoutPolicy.cappedGroupSize(list.size(), host.heroDisplayRows(), iMax, i);
                    int iGroupRowCount = SelectedHeroesLayoutPolicy.groupRowCount(iCappedGroupSize, iMax);
                    int i4 = 0;
                    while (i4 < iGroupRowCount) {
                        LinearLayout linearLayout2 = new LinearLayout(host.context());
                        linearLayout2.setOrientation(0);
                        linearLayout2.setGravity(16);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i2, i2);
                        layoutParams.topMargin = host.cachedDp1s();
                        layoutParams.bottomMargin = host.cachedDp1s();
                        linearLayout2.setLayoutParams(layoutParams);
                        for (int i5 = 0; i5 < iMax; i5++) {
                            int i6 = (i4 * iMax) + i5;
                            if (i6 >= iCappedGroupSize) {
                                break;
                            }
                            linearLayout2.addView(host.createHeroAvatarView((HeroData) list.get(i6)));
                        }
                        linearLayout.addView(linearLayout2);
                        i4++;
                        i2 = -2;
                    }
                    i += iCappedGroupSize;
                }
                i3++;
                i2 = -2;
            }
        } else {
            linearLayout.setOrientation(0);
            i = 0;
            int i7 = 1;
            for (int i8 = 6; i7 <= i8; i8 = 6) {
                List list2 = (List) map.get(Integer.valueOf(i7));
                if (list2 != null && !list2.isEmpty()) {
                    int size = list2.size();
                    int iGroupColCount = SelectedHeroesLayoutPolicy.groupColCount(size, iMax);
                    if (SelectedHeroesLayoutPolicy.rowCapReached(host.heroDisplayRows(), iMax, i)) {
                        break;
                    }
                    if (host.heroDisplayRows() > 0) {
                        int iRemainingVerticalCols = SelectedHeroesLayoutPolicy.remainingVerticalCols(host.heroDisplayRows(), iMax, i);
                        if (iRemainingVerticalCols <= 0) {
                            break;
                        }
                        iGroupColCount = Math.min(iGroupColCount, iRemainingVerticalCols);
                        size = SelectedHeroesLayoutPolicy.cappedVerticalGroupSize(size, iGroupColCount, iMax);
                    }
                    for (int i9 = 0; i9 < iGroupColCount; i9++) {
                        LinearLayout linearLayout3 = new LinearLayout(host.context());
                        linearLayout3.setOrientation(1);
                        linearLayout3.setGravity(1);
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams2.leftMargin = host.cachedDp1s();
                        layoutParams2.rightMargin = host.cachedDp1s();
                        linearLayout3.setLayoutParams(layoutParams2);
                        for (int i10 = 0; i10 < iMax; i10++) {
                            int i11 = (i9 * iMax) + i10;
                            if (i11 >= size) {
                                break;
                            }
                            linearLayout3.addView(host.createHeroAvatarView((HeroData) list2.get(i11)));
                        }
                        linearLayout.addView(linearLayout3);
                    }
                    i += size;
                }
                i7++;
            }
        }
        boolean zShouldShowSelectedHeroes = OverlaySessionPaintPolicy.shouldShowSelectedHeroes(host.isSelectedHeroesVisible(), host.isGameHudSessionReady(), host.exitSuppressed(), i > 0);
        host.syncSelectedHeroesVisualVisibility(linearLayout, zShouldShowSelectedHeroes);
        if (zShouldShowSelectedHeroes && host.paramsSelectedHeroes() != null && linearLayout.getParent() != null) {
            try {
                host.constrainWindowBounds(host.paramsSelectedHeroes(), linearLayout);
                if (host.isSelectedHeroesHosted()) {
                    host.applySelectedHeroesHostPosition(linearLayout);
                } else {
                    host.updateTrustedVisualOverlay(linearLayout, host.paramsSelectedHeroes(), "selected-heroes-content");
                }
            } catch (RuntimeException e2) {
                Log.w(TAG, "选中英雄内容重测量失败", e2);
            }
        }
        Dbg.d(TAG, String.format("Updated selected heroes window: selectedIds=%d, displayed=%d, layout=%s, cols=%d, rows=%s, scale=%.0f%%", Integer.valueOf(host.selectedHeroIds().size()), Integer.valueOf(i), host.heroDisplayLayout() == 0 ? "横排" : "竖排", Integer.valueOf(host.heroDisplayColumns()), host.heroDisplayRows() == 0 ? "自动" : String.valueOf(host.heroDisplayRows()), Float.valueOf(host.selectedHeroesScale() * 100.0f)));
    }
}
