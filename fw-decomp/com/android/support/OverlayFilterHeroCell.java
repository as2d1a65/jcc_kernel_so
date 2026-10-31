package com.android.support;

import android.content.Context;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.support.HeroIdMapper;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
final class OverlayFilterHeroCell {
    static final int ALL_CHOSEN_SELECTED_BG = -2003032144;
    static final int FIVE_COST_SELECTED_BG = -2013200640;
    static final int UNSELECTED_BG = 872415231;
    static final int FIVE_COST_NAME_COLOR = -26624;
    static final int[] COST_COLORS = {-3355444, -16728155, -14575885, -5552196, FIVE_COST_NAME_COLOR};

    private OverlayFilterHeroCell() {
    }

    static int costColor(int i) {
        return COST_COLORS[Math.min(Math.max(i, 1) - 1, 4)];
    }

    static void paint(LinearLayout linearLayout, boolean z, int i) {
        if (!z) {
            i = UNSELECTED_BG;
        }
        linearLayout.setBackgroundColor(i);
    }

    static boolean flip(Set<Integer> set, int i) {
        if (set.contains(Integer.valueOf(i))) {
            set.remove(Integer.valueOf(i));
            return false;
        }
        set.add(Integer.valueOf(i));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void toggle(Set<Integer> set, int i, LinearLayout linearLayout, int i2) {
        paint(linearLayout, flip(set, i), i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void selectAll(List<HeroIdMapper.HeroInfo> list, Set<Integer> set, List<LinearLayout> list2, int i) {
        Iterator<HeroIdMapper.HeroInfo> it = list.iterator();
        while (it.hasNext()) {
            set.add(Integer.valueOf(it.next().heroId));
        }
        Iterator<LinearLayout> it2 = list2.iterator();
        while (it2.hasNext()) {
            paint(it2.next(), true, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void deselectAll(Set<Integer> set, List<LinearLayout> list) {
        set.clear();
        Iterator<LinearLayout> it = list.iterator();
        while (it.hasNext()) {
            paint(it.next(), false, 0);
        }
    }

    static LinearLayout create(Context context, int i, int i2, int i3) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(17);
        linearLayout.setClickable(true);
        linearLayout.setFocusable(true);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = -2;
        layoutParams.columnSpec = GridLayout.spec(Integer.MIN_VALUE, 1.0f);
        int iDp2px = ScreenUtils.dp2px(context, i);
        layoutParams.setMargins(iDp2px, iDp2px, iDp2px, iDp2px);
        linearLayout.setLayoutParams(layoutParams);
        float f = i2;
        float f2 = i3;
        linearLayout.setPadding(ScreenUtils.dp2px(context, f), ScreenUtils.dp2px(context, f2), ScreenUtils.dp2px(context, f), ScreenUtils.dp2px(context, f2));
        return linearLayout;
    }

    static TextView name(Context context, String str, float f, int i) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextSize(f);
        textView.setTextColor(i);
        textView.setGravity(17);
        textView.setMaxLines(1);
        return textView;
    }

    static TextView costTitle(Context context, int i) {
        TextView textView = new TextView(context);
        textView.setText("── " + i + "费 ──");
        textView.setTextSize(11.0f);
        textView.setTextColor(costColor(i));
        textView.setGravity(17);
        textView.setPadding(0, ScreenUtils.dp2px(context, 4.0f), 0, ScreenUtils.dp2px(context, 2.0f));
        return textView;
    }

    static GridLayout grid(Context context, int i, int i2, int i3) {
        GridLayout gridLayout = new GridLayout(context);
        gridLayout.setColumnCount(i);
        float f = i2;
        gridLayout.setPadding(ScreenUtils.dp2px(context, f), 0, ScreenUtils.dp2px(context, f), ScreenUtils.dp2px(context, i3));
        return gridLayout;
    }
}
