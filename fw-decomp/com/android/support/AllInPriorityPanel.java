package com.android.support;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.support.AllInPriorityGrid;
import com.android.support.lineup.LineupRepo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
final class AllInPriorityPanel extends FrameLayout {
    private static final int COLOR_ACCENT = -10556417;
    private static final int COLOR_BORDER = -14991022;
    private static final int COLOR_DANGER = -36205;
    private static final int COLOR_PANEL = -16117988;
    private static final int COLOR_SECTION = -15722455;
    private static final int COLOR_TEXT = -1378052;
    private static final int COLOR_TEXT_DIM = -7034446;
    private static final long OWNED_REFRESH_MS = 1200;
    private final Set<Integer> chaseSet;
    private final float density;
    private ValueAnimator glowAnimator;
    private final List<AllInPriorityGrid> grids;
    private final List<View> highlighted;
    private final Host host;
    private final List<Integer> offsets;
    private String ownedFingerprint;
    private final Runnable ownedRefresh;
    private final List<Boolean> readOnlyGrid;
    private LinearLayout sectionBody;

    interface Host {
        int chaseSlots();

        String currentSetId();

        List<LineupRepo.HeroMeta> fiveCostHeroes();

        void loadAvatar(ImageView imageView, LineupRepo.HeroMeta heroMeta);

        int ownedCopies(int i);

        List<LineupRepo.HeroMeta> ownedHeroes();

        void saveOrder(int[] iArr);

        int[] savedOrder();
    }

    AllInPriorityPanel(Context context, Host host) {
        super(context);
        this.grids = new ArrayList();
        this.offsets = new ArrayList();
        this.readOnlyGrid = new ArrayList();
        this.chaseSet = new LinkedHashSet();
        this.ownedRefresh = new Runnable() { // from class: com.android.support.AllInPriorityPanel.2
            @Override // java.lang.Runnable
            public void run() {
                if (AllInPriorityPanel.this.isAttachedToWindow()) {
                    AllInPriorityPanel.this.refreshOwnedIfChanged();
                    AllInPriorityPanel.this.postDelayed(this, AllInPriorityPanel.OWNED_REFRESH_MS);
                }
            }
        };
        this.ownedFingerprint = "";
        this.highlighted = new ArrayList();
        this.host = host;
        this.density = context.getResources().getDisplayMetrics().density;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(COLOR_PANEL);
        gradientDrawable.setCornerRadius(dp(14));
        gradientDrawable.setStroke(dp(1), COLOR_BORDER);
        setBackground(gradientDrawable);
        ScrollCuePanel scrollCuePanel = new ScrollCuePanel(context);
        scrollCuePanel.setOrientation(1);
        scrollCuePanel.setPadding(dp(10), dp(8), dp(10), dp(10));
        addView(scrollCuePanel, new FrameLayout.LayoutParams(-1, -1));
        scrollCuePanel.addView(buildHeader(context));
        scrollCuePanel.addView(buildHint(context));
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        ScrollEdgeCue.attach(scrollView);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        scrollView.addView(linearLayout, new ViewGroup.LayoutParams(-1, -2));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
        layoutParams.topMargin = dp(8);
        scrollCuePanel.addView(scrollView, layoutParams);
        buildSections(context, linearLayout);
    }

    private int dp(int i) {
        return Math.max(1, (int) (i * this.density));
    }

    private LinearLayout buildHeader(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        TextView textView = new TextView(context);
        String strCurrentSetId = this.host.currentSetId();
        textView.setText((strCurrentSetId == null || strCurrentSetId.isEmpty()) ? "配置梭哈优先级" : "配置梭哈优先级 · S" + strCurrentSetId);
        textView.setTextColor(-10556417);
        textView.setTextSize(12.0f);
        textView.setTypeface(null, 1);
        textView.setSingleLine(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.setMarginEnd(dp(8));
        linearLayout.addView(textView, layoutParams);
        TextView textViewChrome = chrome(context, "重置", -36205, 587154534);
        textViewChrome.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AllInPriorityPanel$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m69lambda$buildHeader$0$comandroidsupportAllInPriorityPanel(view);
            }
        });
        linearLayout.addView(textViewChrome, new LinearLayout.LayoutParams(-2, dp(40)));
        return linearLayout;
    }

    /* renamed from: lambda$buildHeader$0$com-android-support-AllInPriorityPanel, reason: not valid java name */
    /* synthetic */ void m69lambda$buildHeader$0$comandroidsupportAllInPriorityPanel(View view) {
        this.host.saveOrder(new int[0]);
        rebuild();
    }

    private TextView chrome(Context context, String str, int i, int i2) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(i);
        textView.setTextSize(11.0f);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        textView.setPadding(dp(12), dp(6), dp(12), dp(6));
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setMinHeight(dp(40));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(10));
        gradientDrawable.setColor(i2);
        gradientDrawable.setStroke(dp(1), (16777215 & i) | DsTokens.RIPPLE_DARK);
        textView.setBackground(gradientDrawable);
        return textView;
    }

    private TextView buildHint(Context context) {
        TextView textView = new TextView(context);
        textView.setText("亮青色高光的就是这一局会去追的（个数=「凑几个三星」减去手上已有的三星）。\n默认按张数从多到少自动排（不用管）。想先追哪个，直接把它拖到最前——上面「我的棋子」拖你已上场的，下面拖还没上场的备选。拖过之后一直按你拖的追，压过张数。\n不想手动了点右上角「重置」，恢复默认自动排序。");
        textView.setTextColor(-7034446);
        textView.setTextSize(9.0f);
        textView.setLineSpacing(dp(2), 1.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = dp(6);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private void buildSections(Context context, LinearLayout linearLayout) {
        this.sectionBody = linearLayout;
        rebuild();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rebuild() {
        if (this.sectionBody == null) {
            return;
        }
        this.grids.clear();
        this.offsets.clear();
        this.readOnlyGrid.clear();
        this.highlighted.clear();
        this.sectionBody.removeAllViews();
        int[] iArrSavedOrder = this.host.savedOrder();
        List<LineupRepo.HeroMeta> listOwnedHeroes = this.host.ownedHeroes();
        List<LineupRepo.HeroMeta> listFiveCostHeroes = this.host.fiveCostHeroes();
        resolveChaseSet(listOwnedHeroes, applySavedOrder(listFiveCostHeroes, iArrSavedOrder));
        addSection(this.sectionBody, "① 我的棋子 · 拖动即优先追（压过张数）", listOwnedHeroes, null, 0, true);
        addSection(this.sectionBody, "② 五费备选 · 还没上场的，手动拖", listFiveCostHeroes, iArrSavedOrder, listOwnedHeroes.size(), false);
        this.ownedFingerprint = fingerprintOf(listOwnedHeroes);
        syncChaseGlow();
    }

    private void addSection(LinearLayout linearLayout, String str, final List<LineupRepo.HeroMeta> list, int[] iArr, final int i, final boolean z) {
        Context context = getContext();
        TextView textView = new TextView(context);
        if (list.isEmpty()) {
            str = str + "（暂无）";
        }
        textView.setText(str);
        textView.setTextColor(-1378052);
        textView.setTextSize(11.0f);
        textView.setTypeface(null, 1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = dp(10);
        layoutParams.bottomMargin = dp(6);
        textView.setLayoutParams(layoutParams);
        linearLayout.addView(textView);
        if (list.isEmpty()) {
            return;
        }
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout2.setPadding(dp(8), dp(8), dp(8), dp(8));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(COLOR_SECTION);
        gradientDrawable.setCornerRadius(dp(10));
        linearLayout2.setBackground(gradientDrawable);
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-1, -2));
        List<Integer> listOrderOwnedForDisplay = z ? orderOwnedForDisplay(list) : applySavedOrder(list, iArr);
        AllInPriorityGrid allInPriorityGrid = new AllInPriorityGrid(context, dp(44), dp(6));
        allInPriorityGrid.setReadOnly(false);
        allInPriorityGrid.setEntries(listOrderOwnedForDisplay, new AllInPriorityGrid.CellFactory() { // from class: com.android.support.AllInPriorityPanel$$ExternalSyntheticLambda0
            @Override // com.android.support.AllInPriorityGrid.CellFactory
            public final View create(int i2, int i3) {
                return this.f$0.m67lambda$addSection$1$comandroidsupportAllInPriorityPanel(i, list, z, i2, i3);
            }
        });
        allInPriorityGrid.setOnOrderChangedListener(new AnonymousClass1(allInPriorityGrid, i));
        allInPriorityGrid.setDragDecorator(new AllInPriorityGrid.DragDecorator() { // from class: com.android.support.AllInPriorityPanel$$ExternalSyntheticLambda1
            @Override // com.android.support.AllInPriorityGrid.DragDecorator
            public final void onDragStateChanged(View view, boolean z2) {
                this.f$0.m68lambda$addSection$2$comandroidsupportAllInPriorityPanel(view, z2);
            }
        });
        linearLayout2.addView(allInPriorityGrid, new LinearLayout.LayoutParams(-1, -2));
        this.grids.add(allInPriorityGrid);
        this.offsets.add(Integer.valueOf(i));
        this.readOnlyGrid.add(false);
    }

    /* renamed from: lambda$addSection$1$com-android-support-AllInPriorityPanel, reason: not valid java name */
    /* synthetic */ View m67lambda$addSection$1$comandroidsupportAllInPriorityPanel(int i, List list, boolean z, int i2, int i3) {
        return buildCell(i2, i + i3, list, z);
    }

    /* renamed from: com.android.support.AllInPriorityPanel$1, reason: invalid class name */
    class AnonymousClass1 implements AllInPriorityGrid.OnOrderChangedListener {
        final /* synthetic */ int val$globalOffset;
        final /* synthetic */ AllInPriorityGrid val$grid;

        AnonymousClass1(AllInPriorityGrid allInPriorityGrid, int i) {
            this.val$grid = allInPriorityGrid;
            this.val$globalOffset = i;
        }

        @Override // com.android.support.AllInPriorityGrid.OnOrderChangedListener
        public void onOrderChanged() {
            AllInPriorityGrid allInPriorityGrid = this.val$grid;
            final int i = this.val$globalOffset;
            allInPriorityGrid.rebindPositions(new AllInPriorityGrid.PositionBinder() { // from class: com.android.support.AllInPriorityPanel$1$$ExternalSyntheticLambda0
                @Override // com.android.support.AllInPriorityGrid.PositionBinder
                public final void bind(View view, int i2, int i3) {
                    this.f$0.m73lambda$onOrderChanged$0$comandroidsupportAllInPriorityPanel$1(i, view, i2, i3);
                }
            });
            AllInPriorityPanel.this.syncChaseGlow();
        }

        /* renamed from: lambda$onOrderChanged$0$com-android-support-AllInPriorityPanel$1, reason: not valid java name */
        /* synthetic */ void m73lambda$onOrderChanged$0$comandroidsupportAllInPriorityPanel$1(int i, View view, int i2, int i3) {
            AllInPriorityPanel.this.applyChaseRank(view, i2, i + i3);
        }

        @Override // com.android.support.AllInPriorityGrid.OnOrderChangedListener
        public void onOrderCommitted() {
            AllInPriorityPanel.this.persist();
            AllInPriorityPanel.this.rebuild();
        }
    }

    /* renamed from: lambda$addSection$2$com-android-support-AllInPriorityPanel, reason: not valid java name */
    /* synthetic */ void m68lambda$addSection$2$comandroidsupportAllInPriorityPanel(View view, boolean z) {
        Drawable background = view.getBackground();
        if (background instanceof GradientDrawable) {
            View viewFindViewWithTag = view.findViewWithTag("cost");
            ((GradientDrawable) background).setStroke(dp(z ? 3 : 2), z ? -10556417 : viewFindViewWithTag instanceof TextView ? ((TextView) viewFindViewWithTag).getCurrentTextColor() : -10556417);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void persist() {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        int i = 0;
        for (int i2 = 0; i2 < this.grids.size(); i2++) {
            if (!this.readOnlyGrid.get(i2).booleanValue()) {
                linkedHashSet.addAll(this.grids.get(i2).order());
            }
        }
        int[] iArr = new int[linkedHashSet.size()];
        Iterator it = linkedHashSet.iterator();
        while (it.hasNext()) {
            iArr[i] = ((Integer) it.next()).intValue();
            i++;
        }
        this.host.saveOrder(iArr);
    }

    private List<Integer> orderOwnedForDisplay(List<LineupRepo.HeroMeta> list) {
        final int[] iArrSavedOrder = this.host.savedOrder();
        List<Integer> listHeroIds = heroIds(list);
        Collections.sort(listHeroIds, new Comparator() { // from class: com.android.support.AllInPriorityPanel$$ExternalSyntheticLambda5
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return this.f$0.m71x1cd8e70a(iArrSavedOrder, (Integer) obj, (Integer) obj2);
            }
        });
        return listHeroIds;
    }

    /* renamed from: lambda$orderOwnedForDisplay$3$com-android-support-AllInPriorityPanel, reason: not valid java name */
    /* synthetic */ int m71x1cd8e70a(int[] iArr, Integer num, Integer num2) {
        int iOwnedCopies = this.host.ownedCopies(num.intValue());
        int iOwnedCopies2 = this.host.ownedCopies(num2.intValue());
        boolean z = iOwnedCopies >= 9;
        if (z != (iOwnedCopies2 >= 9)) {
            return z ? 1 : -1;
        }
        int iSavedRank = savedRank(iArr, num.intValue());
        int iSavedRank2 = savedRank(iArr, num2.intValue());
        return iSavedRank != iSavedRank2 ? iSavedRank - iSavedRank2 : iOwnedCopies != iOwnedCopies2 ? iOwnedCopies2 - iOwnedCopies : num.intValue() - num2.intValue();
    }

    private static int savedRank(int[] iArr, int i) {
        if (iArr == null) {
            return Integer.MAX_VALUE;
        }
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (iArr[i2] == i) {
                return i2;
            }
        }
        return Integer.MAX_VALUE;
    }

    private static List<Integer> heroIds(List<LineupRepo.HeroMeta> list) {
        ArrayList arrayList = new ArrayList(list.size());
        Iterator<LineupRepo.HeroMeta> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(Integer.valueOf(it.next().id));
        }
        return arrayList;
    }

    private static List<Integer> applySavedOrder(List<LineupRepo.HeroMeta> list, int[] iArr) {
        ArrayList<Integer> arrayList = new ArrayList();
        Iterator<LineupRepo.HeroMeta> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(Integer.valueOf(it.next().id));
        }
        ArrayList arrayList2 = new ArrayList();
        if (iArr != null) {
            for (int i : iArr) {
                if (arrayList.contains(Integer.valueOf(i)) && !arrayList2.contains(Integer.valueOf(i))) {
                    arrayList2.add(Integer.valueOf(i));
                }
            }
        }
        for (Integer num : arrayList) {
            if (!arrayList2.contains(num)) {
                arrayList2.add(num);
            }
        }
        return arrayList2;
    }

    private View buildCell(int i, int i2, List<LineupRepo.HeroMeta> list, boolean z) {
        LineupRepo.HeroMeta next;
        int iOwnedCopies;
        Context context = getContext();
        Iterator<LineupRepo.HeroMeta> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                next = null;
                break;
            }
            next = it.next();
            if (next.id == i) {
                break;
            }
        }
        int i3 = next == null ? 0 : next.cost;
        int iCostColor = GameHudPalette.costColor(i3);
        FrameLayout frameLayout = new FrameLayout(context);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(8));
        gradientDrawable.setColor(-15327174);
        gradientDrawable.setStroke(dp(2), iCostColor);
        frameLayout.setBackground(gradientDrawable);
        frameLayout.setPadding(dp(2), dp(2), dp(2), dp(2));
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setClipToOutline(true);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setCornerRadius(dp(6));
        gradientDrawable2.setColor(-15066578);
        imageView.setBackground(gradientDrawable2);
        frameLayout.addView(imageView, new FrameLayout.LayoutParams(-1, -1));
        if (next != null) {
            this.host.loadAvatar(imageView, next);
        }
        frameLayout.addView(buildCostTag(context, i3, iCostColor));
        frameLayout.addView(buildOrderBadge(context, i2));
        if (z && (iOwnedCopies = this.host.ownedCopies(i)) > 0) {
            frameLayout.addView(buildCopiesTag(context, iOwnedCopies));
        }
        applyChaseRank(frameLayout, i, i2);
        return frameLayout;
    }

    private TextView buildCopiesTag(Context context, int i) {
        boolean z = i >= 9;
        TextView textView = new TextView(context);
        textView.setText(z ? "★" : String.valueOf(i));
        textView.setTextColor(-16379366);
        textView.setTextSize(9.0f);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        textView.setIncludeFontPadding(false);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(5));
        gradientDrawable.setColor(z ? GameHudPalette.SUCCESS : -13960);
        textView.setBackground(gradientDrawable);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp(15), dp(15));
        layoutParams.gravity = 8388661;
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private void resolveChaseSet(List<LineupRepo.HeroMeta> list, List<Integer> list2) {
        this.chaseSet.clear();
        Iterator<LineupRepo.HeroMeta> it = list.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (this.host.ownedCopies(it.next().id) >= 9) {
                i++;
            }
        }
        int iMax = Math.max(0, normalizedChaseSlots() - i);
        for (Integer num : orderOwnedForDisplay(list)) {
            if (this.chaseSet.size() >= iMax) {
                return;
            }
            if (num != null && this.host.ownedCopies(num.intValue()) < 9) {
                this.chaseSet.add(num);
            }
        }
        for (Integer num2 : list2) {
            if (this.chaseSet.size() >= iMax) {
                return;
            }
            if (num2 != null && this.host.ownedCopies(num2.intValue()) < 9) {
                this.chaseSet.add(num2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyChaseRank(View view, int i, int i2) {
        View viewFindViewWithTag = view.findViewWithTag("badge");
        boolean z = viewFindViewWithTag instanceof TextView;
        if (z) {
            ((TextView) viewFindViewWithTag).setText(String.valueOf(i2 + 1));
        }
        boolean zContains = this.chaseSet.contains(Integer.valueOf(i));
        this.highlighted.remove(view);
        if (zContains) {
            this.highlighted.add(view);
        }
        if (z) {
            TextView textView = (TextView) viewFindViewWithTag;
            Drawable background = textView.getBackground();
            if (background instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) background;
                gradientDrawable.setColor(zContains ? -10556417 : -435152856);
                gradientDrawable.setStroke(dp(1), zContains ? COLOR_PANEL : 1728053247);
            }
            textView.setTextColor(zContains ? -16379366 : -1);
        }
        if (!zContains) {
            Drawable background2 = view.getBackground();
            View viewFindViewWithTag2 = view.findViewWithTag("cost");
            int currentTextColor = viewFindViewWithTag2 instanceof TextView ? ((TextView) viewFindViewWithTag2).getCurrentTextColor() : -10556417;
            if (background2 instanceof GradientDrawable) {
                ((GradientDrawable) background2).setStroke(dp(2), currentTextColor);
            }
            view.setAlpha(0.82f);
            return;
        }
        view.setAlpha(1.0f);
    }

    private int normalizedChaseSlots() {
        return QuickActionOverlayPolicy.normalizeAllInMaxTargets(this.host.chaseSlots());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncChaseGlow() {
        ValueAnimator valueAnimator = this.glowAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.glowAnimator = null;
        }
        if (this.highlighted.isEmpty()) {
            return;
        }
        if (!OverlayUiMotion.animationsEnabled(getContext())) {
            paintChaseGlow(1.0f);
            return;
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.glowAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(1500L);
        this.glowAnimator.setRepeatCount(-1);
        this.glowAnimator.setRepeatMode(2);
        this.glowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.support.AllInPriorityPanel$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.m72lambda$syncChaseGlow$4$comandroidsupportAllInPriorityPanel(valueAnimator2);
            }
        });
        this.glowAnimator.start();
    }

    /* renamed from: lambda$syncChaseGlow$4$com-android-support-AllInPriorityPanel, reason: not valid java name */
    /* synthetic */ void m72lambda$syncChaseGlow$4$comandroidsupportAllInPriorityPanel(ValueAnimator valueAnimator) {
        paintChaseGlow(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void paintChaseGlow(float f) {
        int iDp = dp(2) + Math.round(dp(2) * f);
        int iRound = ((Math.round(f * 119.0f) + 136) << 24) | 6220799;
        Iterator<View> it = this.highlighted.iterator();
        while (it.hasNext()) {
            Drawable background = it.next().getBackground();
            if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setStroke(iDp, iRound);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshOwnedIfChanged() {
        Iterator<AllInPriorityGrid> it = this.grids.iterator();
        while (it.hasNext()) {
            if (it.next().isDragging()) {
                return;
            }
        }
        if (fingerprintOf(this.host.ownedHeroes()).equals(this.ownedFingerprint)) {
            return;
        }
        rebuild();
    }

    private String fingerprintOf(List<LineupRepo.HeroMeta> list) {
        StringBuilder sb = new StringBuilder(list.size() * 8);
        for (LineupRepo.HeroMeta heroMeta : list) {
            sb.append(heroMeta.id).append(':').append(this.host.ownedCopies(heroMeta.id)).append('|');
        }
        return sb.toString();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks(this.ownedRefresh);
        postDelayed(this.ownedRefresh, OWNED_REFRESH_MS);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        removeCallbacks(this.ownedRefresh);
        ValueAnimator valueAnimator = this.glowAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.glowAnimator = null;
        }
        super.onDetachedFromWindow();
    }

    void onChaseSlotsChanged() {
        resolveChaseSet(this.host.ownedHeroes(), applySavedOrder(this.host.fiveCostHeroes(), this.host.savedOrder()));
        for (int i = 0; i < this.grids.size(); i++) {
            final int iIntValue = this.offsets.get(i).intValue();
            this.grids.get(i).rebindPositions(new AllInPriorityGrid.PositionBinder() { // from class: com.android.support.AllInPriorityPanel$$ExternalSyntheticLambda3
                @Override // com.android.support.AllInPriorityGrid.PositionBinder
                public final void bind(View view, int i2, int i3) {
                    this.f$0.m70x5c99bfc4(iIntValue, view, i2, i3);
                }
            });
        }
        syncChaseGlow();
    }

    /* renamed from: lambda$onChaseSlotsChanged$5$com-android-support-AllInPriorityPanel, reason: not valid java name */
    /* synthetic */ void m70x5c99bfc4(int i, View view, int i2, int i3) {
        applyChaseRank(view, i2, i + i3);
    }

    private TextView buildCostTag(Context context, int i, int i2) {
        TextView textView = new TextView(context);
        textView.setTag("cost");
        textView.setText(i > 0 ? String.valueOf(i) : "?");
        textView.setTextColor(i2);
        textView.setTextSize(9.0f);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        textView.setIncludeFontPadding(false);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(5));
        gradientDrawable.setColor(-435745768);
        gradientDrawable.setStroke(dp(1), (16777215 & i2) | (-1728053248));
        textView.setBackground(gradientDrawable);
        textView.setShadowLayer(this.density * 3.0f, 0.0f, 0.0f, i2);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp(15), dp(15));
        layoutParams.gravity = 8388659;
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private TextView buildOrderBadge(Context context, int i) {
        TextView textView = new TextView(context);
        textView.setTag("badge");
        textView.setText(String.valueOf(i + 1));
        textView.setTextColor(-1);
        textView.setTextSize(9.0f);
        textView.setTypeface(null, 1);
        textView.setGravity(17);
        textView.setIncludeFontPadding(false);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(-435152856);
        gradientDrawable.setStroke(dp(1), 1728053247);
        textView.setBackground(gradientDrawable);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp(18), dp(18));
        layoutParams.gravity = 8388693;
        textView.setLayoutParams(layoutParams);
        return textView;
    }
}
