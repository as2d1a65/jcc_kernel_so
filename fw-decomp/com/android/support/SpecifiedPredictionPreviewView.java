package com.android.support;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.support.SharedMemManager;
import com.android.support.SpecifiedPredictionPreview;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlinx.coroutines.DebugKt;

/* loaded from: classes.dex */
final class SpecifiedPredictionPreviewView extends LinearLayout {
    private static final int ACCENT = -10556417;
    private static final int GREEN = -9706598;
    private static final int GREEN_DEEP = -16045285;
    private static final long HIT_RELEASE_MS = 280;
    private static final int PRIMARY = -985089;
    private static final int SECONDARY = -3613458;
    private static final int TILE_DP = 62;
    private final GridLayout cards;
    private boolean disposed;
    private final int ghostTiles;
    private final List<Card> holders;
    private final boolean iconOnly;
    private final String kind;
    private SpecifiedPredictionPreview latest;
    private final SharedMemManager.SpecifiedPredictionPreviewListener listener;
    private SharedMemManager owner;
    private final Presenter presenter;
    private final Runnable renderLatest;
    private boolean renderQueued;
    private Runnable showPreviewTab;
    private Runnable showSelectedTab;
    private final TextView status;
    private boolean tabPickedByUser;
    private boolean tabShownByForecast;

    interface Presenter {
        void bindIcon(ImageView imageView, SpecifiedPredictionPreview.Item item, int i);

        default void bindSourceIcon(ImageView imageView, SpecifiedPredictionPreview.Item item, int i) {
        }

        boolean canSelect(SpecifiedPredictionPreview.Item item);

        String detail(SpecifiedPredictionPreview.Item item);

        String name(SpecifiedPredictionPreview.Item item);

        void select(SpecifiedPredictionPreview.Item item);
    }

    /* renamed from: lambda$new$0$com-android-support-SpecifiedPredictionPreviewView, reason: not valid java name */
    /* synthetic */ void m1534lambda$new$0$comandroidsupportSpecifiedPredictionPreviewView() {
        this.renderQueued = false;
        if (this.disposed || !isShown()) {
            return;
        }
        render();
    }

    SpecifiedPredictionPreviewView(Context context, String str, Presenter presenter) {
        super(context);
        this.holders = new ArrayList();
        this.listener = new SharedMemManager.SpecifiedPredictionPreviewListener() { // from class: com.android.support.SpecifiedPredictionPreviewView$$ExternalSyntheticLambda0
            @Override // com.android.support.SharedMemManager.SpecifiedPredictionPreviewListener
            public final void onPreview(SpecifiedPredictionPreview specifiedPredictionPreview) {
                this.f$0.update(specifiedPredictionPreview);
            }
        };
        this.renderLatest = new Runnable() { // from class: com.android.support.SpecifiedPredictionPreviewView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1534lambda$new$0$comandroidsupportSpecifiedPredictionPreviewView();
            }
        };
        this.kind = str;
        boolean zEquals = SpecifiedPredictionPreview.KIND_SHOP.equals(str);
        this.iconOnly = !zEquals;
        int i = 3;
        this.ghostTiles = SpecifiedPredictionPreview.KIND_HEX.equals(str) ? 3 : SpecifiedPredictionPreview.KIND_RECAST.equals(str) ? 1 : 0;
        this.presenter = presenter;
        setOrientation(1);
        setPadding(dp(2), dp(6), dp(2), dp(4));
        TextView textViewText = text(11, SECONDARY);
        this.status = textViewText;
        textViewText.setMaxLines(1);
        textViewText.setText("等待预测数据");
        textViewText.setVisibility(!zEquals ? 8 : 0);
        addView(textViewText, new LinearLayout.LayoutParams(-1, -2));
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(false);
        scrollView.setClipToPadding(false);
        scrollView.setOverScrollMode(1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
        layoutParams.topMargin = dp(!zEquals ? 0 : 5);
        addView(scrollView, layoutParams);
        GridLayout gridLayout = new GridLayout(context);
        this.cards = gridLayout;
        if (SpecifiedPredictionPreview.KIND_SHOP.equals(str)) {
            i = 5;
        } else if (!SpecifiedPredictionPreview.KIND_HEX.equals(str)) {
            i = 4;
        }
        gridLayout.setColumnCount(i);
        gridLayout.setClipChildren(false);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(zEquals ? -1 : -2, -2);
        layoutParams2.gravity = 1;
        scrollView.addView(gridLayout, layoutParams2);
    }

    void update(SpecifiedPredictionPreview specifiedPredictionPreview) {
        if (this.disposed) {
            return;
        }
        this.latest = specifiedPredictionPreview;
        followForecastWithTab((specifiedPredictionPreview == null || !specifiedPredictionPreview.ready || specifiedPredictionPreview.items.isEmpty()) ? false : true);
        if (!isShown() || this.renderQueued) {
            return;
        }
        this.renderQueued = true;
        postDelayed(this.renderLatest, 60L);
    }

    private void followForecastWithTab(boolean z) {
        Runnable runnable;
        if (this.showPreviewTab == null || (runnable = this.showSelectedTab) == null) {
            return;
        }
        if (!z) {
            if (this.tabShownByForecast) {
                runnable.run();
            }
            this.tabShownByForecast = false;
            this.tabPickedByUser = false;
            return;
        }
        if (this.tabPickedByUser || this.tabShownByForecast || getVisibility() == 0) {
            return;
        }
        this.tabShownByForecast = true;
        this.showPreviewTab.run();
    }

    void refresh() {
        if (this.disposed || !isShown()) {
            return;
        }
        render();
    }

    void bind(SharedMemManager sharedMemManager) {
        this.owner = sharedMemManager;
        if (sharedMemManager == null) {
            return;
        }
        sharedMemManager.setSpecifiedPredictionPreviewListener(this.kind, this.listener);
        update(sharedMemManager.getSpecifiedPredictionPreview(this.kind));
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        if (i != 0 || this.status == null || this.cards == null || this.disposed) {
            return;
        }
        refresh();
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (i != 0 || this.status == null || this.cards == null || this.disposed) {
            return;
        }
        refresh();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i2 == i4 || this.status == null || this.cards == null || this.disposed) {
            return;
        }
        refresh();
    }

    private void render() {
        SpecifiedPredictionPreview specifiedPredictionPreview = this.latest;
        boolean z = (specifiedPredictionPreview == null || !specifiedPredictionPreview.ready || specifiedPredictionPreview.items.isEmpty()) ? false : true;
        boolean z2 = z && "landed".equals(specifiedPredictionPreview.state);
        if (!this.iconOnly) {
            this.status.setText(statusText(specifiedPredictionPreview, z));
            this.status.setTextColor(z ? PRIMARY : SECONDARY);
            this.status.setVisibility((!z || getHeight() <= 0 || getHeight() >= dp(96)) ? 0 : 8);
        }
        int size = z ? specifiedPredictionPreview.items.size() : this.ghostTiles;
        while (this.holders.size() < size) {
            Card card = new Card();
            this.holders.add(card);
            this.cards.addView(card.root);
        }
        for (int i = 0; i < this.holders.size(); i++) {
            Card card2 = this.holders.get(i);
            if (i >= size) {
                card2.clear();
                card2.root.setVisibility(8);
            } else {
                card2.root.setVisibility(0);
                if (z) {
                    card2.bind(specifiedPredictionPreview.items.get(i), z2);
                } else {
                    card2.bindGhost();
                }
            }
        }
    }

    private String statusText(SpecifiedPredictionPreview specifiedPredictionPreview, boolean z) {
        if (specifiedPredictionPreview == null || specifiedPredictionPreview.matchGeneration.isEmpty()) {
            return "等待进入对局";
        }
        if (!z) {
            if (DebugKt.DEBUG_PROPERTY_VALUE_OFF.equals(specifiedPredictionPreview.state) || RecastRunStatus.PHASE_IDLE.equals(specifiedPredictionPreview.state)) {
                return SpecifiedPredictionPreview.KIND_SHOP.equals(this.kind) ? "开启指定商店后显示实时预测" : "选择目标后显示实时预测";
            }
            if ("no_targets".equals(specifiedPredictionPreview.state)) {
                return "先选择想要的目标";
            }
            if ("waiting_store".equals(specifiedPredictionPreview.state) || "store_closed".equals(specifiedPredictionPreview.state)) {
                return "等待海克斯选择开启";
            }
            return "等待预测数据";
        }
        Iterator<SpecifiedPredictionPreview.Item> it = specifiedPredictionPreview.items.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().selected) {
                i++;
            }
        }
        if ("landed".equals(specifiedPredictionPreview.state) && i > 0) {
            return SpecifiedPredictionPreview.KIND_SHOP.equals(this.kind) ? "刷到目标 " + i + " 张" : SpecifiedPredictionPreview.KIND_HEX.equals(this.kind) ? "已刷到 " + i + " 个目标，自己选择" : "锤出目标";
        }
        if (!SpecifiedPredictionPreview.KIND_SHOP.equals(this.kind) || i <= 0) {
            return i > 0 ? "下一次刷新命中 " + i + " 个目标" : "下一次刷新";
        }
        if (RecastRunStatus.PHASE_HIT.equals(specifiedPredictionPreview.state)) {
            return "目标已匹配，等待出手";
        }
        return "候选中有目标，继续寻找出手机会";
    }

    static SpecifiedPredictionPreviewView installTabs(final FrameLayout frameLayout, String str, Presenter presenter) {
        LinearLayout linearLayout = (LinearLayout) frameLayout.getParent();
        int iIndexOfChild = linearLayout.indexOfChild(frameLayout);
        ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
        FrameLayout frameLayout2 = new FrameLayout(linearLayout.getContext());
        linearLayout.removeView(frameLayout);
        linearLayout.addView(frameLayout2, iIndexOfChild, layoutParams);
        frameLayout2.addView(frameLayout, new FrameLayout.LayoutParams(-1, -1));
        final SpecifiedPredictionPreviewView specifiedPredictionPreviewView = new SpecifiedPredictionPreviewView(linearLayout.getContext(), str, presenter);
        frameLayout2.addView(specifiedPredictionPreviewView, new FrameLayout.LayoutParams(-1, -1));
        specifiedPredictionPreviewView.setVisibility(8);
        LinearLayout linearLayout2 = new LinearLayout(linearLayout.getContext());
        linearLayout2.setOrientation(0);
        linearLayout2.setPadding(0, specifiedPredictionPreviewView.dp(5), 0, specifiedPredictionPreviewView.dp(2));
        final TextView textViewText = specifiedPredictionPreviewView.text(11, PRIMARY);
        textViewText.setText("已选目标");
        final TextView textViewText2 = specifiedPredictionPreviewView.text(11, SECONDARY);
        textViewText2.setText("预测预览");
        TextView[] textViewArr = {textViewText, textViewText2};
        int i = 0;
        for (int i2 = 2; i < i2; i2 = 2) {
            TextView textView = textViewArr[i];
            textView.setGravity(17);
            textView.setTypeface(Typeface.DEFAULT, 1);
            textView.setPadding(specifiedPredictionPreviewView.dp(8), specifiedPredictionPreviewView.dp(4), specifiedPredictionPreviewView.dp(8), specifiedPredictionPreviewView.dp(4));
            textView.setFocusable(true);
            linearLayout2.addView(textView, new LinearLayout.LayoutParams(0, -2, 1.0f));
            i++;
        }
        linearLayout.addView(linearLayout2, linearLayout.indexOfChild(frameLayout2), new LinearLayout.LayoutParams(-1, -2));
        final Runnable runnable = new Runnable() { // from class: com.android.support.SpecifiedPredictionPreviewView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SpecifiedPredictionPreviewView.lambda$installTabs$1(frameLayout, specifiedPredictionPreviewView, textViewText, textViewText2);
            }
        };
        final Runnable runnable2 = new Runnable() { // from class: com.android.support.SpecifiedPredictionPreviewView$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SpecifiedPredictionPreviewView.lambda$installTabs$2(frameLayout, specifiedPredictionPreviewView, textViewText, textViewText2);
            }
        };
        textViewText.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedPredictionPreviewView$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SpecifiedPredictionPreviewView.lambda$installTabs$3(this.f$0, runnable, view);
            }
        });
        textViewText2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedPredictionPreviewView$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SpecifiedPredictionPreviewView.lambda$installTabs$4(this.f$0, runnable2, view);
            }
        });
        specifiedPredictionPreviewView.showSelectedTab = runnable;
        specifiedPredictionPreviewView.showPreviewTab = runnable2;
        runnable.run();
        return specifiedPredictionPreviewView;
    }

    static /* synthetic */ void lambda$installTabs$1(FrameLayout frameLayout, SpecifiedPredictionPreviewView specifiedPredictionPreviewView, TextView textView, TextView textView2) {
        frameLayout.setVisibility(0);
        specifiedPredictionPreviewView.setVisibility(8);
        specifiedPredictionPreviewView.styleTab(textView, true);
        specifiedPredictionPreviewView.styleTab(textView2, false);
    }

    static /* synthetic */ void lambda$installTabs$2(FrameLayout frameLayout, SpecifiedPredictionPreviewView specifiedPredictionPreviewView, TextView textView, TextView textView2) {
        frameLayout.setVisibility(8);
        specifiedPredictionPreviewView.setVisibility(0);
        specifiedPredictionPreviewView.styleTab(textView, false);
        specifiedPredictionPreviewView.styleTab(textView2, true);
        specifiedPredictionPreviewView.refresh();
        specifiedPredictionPreviewView.animate().cancel();
        specifiedPredictionPreviewView.setAlpha(0.7f);
        specifiedPredictionPreviewView.animate().alpha(1.0f).setDuration(120L).start();
    }

    static /* synthetic */ void lambda$installTabs$3(SpecifiedPredictionPreviewView specifiedPredictionPreviewView, Runnable runnable, View view) {
        specifiedPredictionPreviewView.tabPickedByUser = true;
        specifiedPredictionPreviewView.tabShownByForecast = false;
        runnable.run();
    }

    static /* synthetic */ void lambda$installTabs$4(SpecifiedPredictionPreviewView specifiedPredictionPreviewView, Runnable runnable, View view) {
        specifiedPredictionPreviewView.tabPickedByUser = true;
        specifiedPredictionPreviewView.tabShownByForecast = false;
        runnable.run();
    }

    static SpecifiedPredictionPreviewView installInline(FrameLayout frameLayout, String str, Presenter presenter) {
        LinearLayout linearLayout = (LinearLayout) frameLayout.getParent();
        int iIndexOfChild = linearLayout.indexOfChild(frameLayout);
        SpecifiedPredictionPreviewView specifiedPredictionPreviewView = new SpecifiedPredictionPreviewView(linearLayout.getContext(), str, presenter);
        View view = new View(linearLayout.getContext());
        view.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{6220799, 1717496831, 6220799}));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, specifiedPredictionPreviewView.dp(1));
        layoutParams.topMargin = specifiedPredictionPreviewView.dp(4);
        linearLayout.addView(view, iIndexOfChild + 1, layoutParams);
        linearLayout.addView(specifiedPredictionPreviewView, iIndexOfChild + 2, new LinearLayout.LayoutParams(-1, -2));
        return specifiedPredictionPreviewView;
    }

    private void styleTab(TextView textView, boolean z) {
        textView.setSelected(z);
        textView.setTextColor(z ? -10556417 : SECONDARY);
        textView.setBackground(background(z ? 610200575 : 0, z ? -2141262849 : 0, 8));
        textView.setContentDescription(((Object) textView.getText()) + (z ? "，当前页" : "，点击切换"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean animationsOn() {
        return OverlayUiMotion.animationsEnabled(getContext());
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class HitBadge extends View {
        private ValueAnimator animator;
        private final Path check;
        private final Paint disc;
        private final Path drawn;
        private final DecelerateInterpolator ease;
        private final Paint mark;
        private final PathMeasure measure;
        private final OvershootInterpolator overshoot;
        private float pop;
        private final Paint ring;
        private float ripple;
        private float trim;

        HitBadge(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.disc = paint;
            Paint paint2 = new Paint(1);
            this.mark = paint2;
            Paint paint3 = new Paint(1);
            this.ring = paint3;
            this.check = new Path();
            this.drawn = new Path();
            this.measure = new PathMeasure();
            this.overshoot = new OvershootInterpolator(2.4f);
            this.ease = new DecelerateInterpolator(1.6f);
            this.pop = 1.0f;
            this.trim = 1.0f;
            this.ripple = 1.0f;
            paint.setColor(SpecifiedPredictionPreviewView.GREEN);
            paint2.setColor(SpecifiedPredictionPreviewView.GREEN_DEEP);
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setStrokeCap(Paint.Cap.ROUND);
            paint2.setStrokeJoin(Paint.Join.ROUND);
            paint3.setColor(SpecifiedPredictionPreviewView.GREEN);
            paint3.setStyle(Paint.Style.STROKE);
            setVisibility(8);
        }

        void show(boolean z) {
            cancel();
            setVisibility(0);
            setAlpha(1.0f);
            if (!z) {
                this.ripple = 1.0f;
                this.trim = 1.0f;
                this.pop = 1.0f;
                invalidate();
                return;
            }
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setDuration(520L);
            this.animator.setInterpolator(new LinearInterpolator());
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.support.SpecifiedPredictionPreviewView$HitBadge$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.m1538x9c11a580(valueAnimator);
                }
            });
            this.animator.start();
        }

        /* renamed from: lambda$show$0$com-android-support-SpecifiedPredictionPreviewView$HitBadge, reason: not valid java name */
        /* synthetic */ void m1538x9c11a580(ValueAnimator valueAnimator) {
            float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.pop = this.overshoot.getInterpolation(Math.min(1.0f, fFloatValue / 0.42f));
            this.trim = this.ease.getInterpolation(clamp((fFloatValue - 0.2f) / 0.46f));
            this.ripple = this.ease.getInterpolation(clamp((fFloatValue - 0.08f) / 0.92f));
            invalidate();
        }

        void hide(boolean z) {
            cancel();
            if (!z || getVisibility() != 0) {
                setVisibility(8);
            } else {
                animate().alpha(0.0f).setDuration(140L).withEndAction(new Runnable() { // from class: com.android.support.SpecifiedPredictionPreviewView$HitBadge$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1537x85f96d86();
                    }
                }).start();
            }
        }

        /* renamed from: lambda$hide$1$com-android-support-SpecifiedPredictionPreviewView$HitBadge, reason: not valid java name */
        /* synthetic */ void m1537x85f96d86() {
            setVisibility(8);
        }

        void cancel() {
            animate().cancel();
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.animator = null;
        }

        private float clamp(float f) {
            if (f < 0.0f) {
                return 0.0f;
            }
            return Math.min(1.0f, f);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float width = getWidth() / 2.0f;
            float height = getHeight() / 2.0f;
            float fMin = Math.min(getWidth(), getHeight()) * 0.31f;
            float f = this.ripple;
            if (f < 1.0f) {
                this.ring.setStrokeWidth((0.22f * fMin * (1.0f - f)) + 1.0f);
                this.ring.setAlpha(Math.round((1.0f - this.ripple) * 190.0f));
                canvas.drawCircle(width, height, ((this.ripple * 0.6f) + 1.0f) * fMin, this.ring);
            }
            canvas.save();
            float f2 = this.pop;
            canvas.scale(f2, f2, width, height);
            canvas.drawCircle(width, height, fMin, this.disc);
            this.check.reset();
            this.check.moveTo(width - (0.46f * fMin), (0.02f * fMin) + height);
            this.check.lineTo(width - (0.12f * fMin), (0.36f * fMin) + height);
            this.check.lineTo(width + (0.48f * fMin), height - (0.3f * fMin));
            this.mark.setStrokeWidth(fMin * 0.26f);
            this.measure.setPath(this.check, false);
            this.drawn.reset();
            PathMeasure pathMeasure = this.measure;
            pathMeasure.getSegment(0.0f, pathMeasure.getLength() * this.trim, this.drawn, true);
            canvas.drawPath(this.drawn, this.mark);
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class Card {
        final FrameLayout art;
        final HitBadge badge;
        SpecifiedPredictionPreview.Item bound;
        int boundIconSize;
        String boundName;
        String celebratedKey;
        boolean ghost;
        final View halo;
        boolean hitShown;
        final ImageView icon;
        final TextView name;
        final Runnable releaseHit;
        final FrameLayout root;
        final ImageView source;
        final GradientDrawable surface;

        /* renamed from: lambda$new$0$com-android-support-SpecifiedPredictionPreviewView$Card, reason: not valid java name */
        /* synthetic */ void m1535xcf167509() {
            showHit(false);
        }

        Card() {
            FrameLayout frameLayout = new FrameLayout(SpecifiedPredictionPreviewView.this.getContext());
            this.root = frameLayout;
            GradientDrawable gradientDrawable = new GradientDrawable();
            this.surface = gradientDrawable;
            View view = new View(SpecifiedPredictionPreviewView.this.getContext());
            this.halo = view;
            ImageView imageView = new ImageView(SpecifiedPredictionPreviewView.this.getContext());
            this.icon = imageView;
            ImageView imageView2 = new ImageView(SpecifiedPredictionPreviewView.this.getContext());
            this.source = imageView2;
            TextView textViewText = SpecifiedPredictionPreviewView.this.text(10, SpecifiedPredictionPreviewView.PRIMARY);
            this.name = textViewText;
            HitBadge hitBadge = SpecifiedPredictionPreviewView.this.new HitBadge(SpecifiedPredictionPreviewView.this.getContext());
            this.badge = hitBadge;
            FrameLayout frameLayout2 = new FrameLayout(SpecifiedPredictionPreviewView.this.getContext());
            this.art = frameLayout2;
            this.releaseHit = new Runnable() { // from class: com.android.support.SpecifiedPredictionPreviewView$Card$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1535xcf167509();
                }
            };
            this.boundName = "";
            this.celebratedKey = "";
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            if (SpecifiedPredictionPreviewView.this.iconOnly) {
                layoutParams.width = SpecifiedPredictionPreviewView.this.dp(SpecifiedPredictionPreviewView.TILE_DP);
                layoutParams.height = SpecifiedPredictionPreviewView.this.dp(SpecifiedPredictionPreviewView.TILE_DP);
                layoutParams.setMargins(SpecifiedPredictionPreviewView.this.dp(5), SpecifiedPredictionPreviewView.this.dp(5), SpecifiedPredictionPreviewView.this.dp(5), SpecifiedPredictionPreviewView.this.dp(5));
            } else {
                layoutParams.width = 0;
                layoutParams.height = -2;
                layoutParams.columnSpec = GridLayout.spec(Integer.MIN_VALUE, 1.0f);
                layoutParams.setMargins(SpecifiedPredictionPreviewView.this.dp(3), SpecifiedPredictionPreviewView.this.dp(3), SpecifiedPredictionPreviewView.this.dp(3), SpecifiedPredictionPreviewView.this.dp(3));
            }
            frameLayout.setLayoutParams(layoutParams);
            frameLayout.setClipChildren(false);
            gradientDrawable.setCornerRadius(SpecifiedPredictionPreviewView.this.dp(12));
            frameLayout.setBackground(gradientDrawable);
            paintSurface(false);
            LinearLayout linearLayout = new LinearLayout(SpecifiedPredictionPreviewView.this.getContext());
            linearLayout.setOrientation(1);
            linearLayout.setGravity(1);
            linearLayout.setClipChildren(false);
            if (SpecifiedPredictionPreviewView.this.iconOnly) {
                linearLayout.setGravity(17);
                frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(-1, -1));
            } else {
                linearLayout.setPadding(SpecifiedPredictionPreviewView.this.dp(3), SpecifiedPredictionPreviewView.this.dp(7), SpecifiedPredictionPreviewView.this.dp(3), SpecifiedPredictionPreviewView.this.dp(6));
                frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(-1, -2));
            }
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            gradientDrawable2.setShape(1);
            gradientDrawable2.setGradientType(1);
            gradientDrawable2.setGradientRadius(SpecifiedPredictionPreviewView.this.dp(34));
            gradientDrawable2.setColors(new int[]{-2006195302, 7070618});
            view.setBackground(gradientDrawable2);
            view.setAlpha(0.0f);
            frameLayout2.setClipChildren(false);
            frameLayout2.addView(view, new FrameLayout.LayoutParams(-1, -1));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            frameLayout2.addView(imageView, new FrameLayout.LayoutParams(SpecifiedPredictionPreviewView.this.dp(44), SpecifiedPredictionPreviewView.this.dp(44), 17));
            imageView2.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView2.setBackground(SpecifiedPredictionPreviewView.this.background(-435546590, 1728053247, 6));
            imageView2.setPadding(SpecifiedPredictionPreviewView.this.dp(1), SpecifiedPredictionPreviewView.this.dp(1), SpecifiedPredictionPreviewView.this.dp(1), SpecifiedPredictionPreviewView.this.dp(1));
            imageView2.setVisibility(8);
            frameLayout2.addView(imageView2, new FrameLayout.LayoutParams(SpecifiedPredictionPreviewView.this.dp(20), SpecifiedPredictionPreviewView.this.dp(20), 8388691));
            linearLayout.addView(frameLayout2, SpecifiedPredictionPreviewView.this.iconOnly ? new LinearLayout.LayoutParams(SpecifiedPredictionPreviewView.this.dp(56), SpecifiedPredictionPreviewView.this.dp(56)) : new LinearLayout.LayoutParams(SpecifiedPredictionPreviewView.this.dp(60), SpecifiedPredictionPreviewView.this.dp(52)));
            textViewText.setMaxLines(SpecifiedPredictionPreview.KIND_HEX.equals(SpecifiedPredictionPreviewView.this.kind) ? 2 : 1);
            textViewText.setGravity(17);
            textViewText.setTypeface(Typeface.DEFAULT, 1);
            textViewText.setVisibility(SpecifiedPredictionPreviewView.this.iconOnly ? 8 : 0);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
            layoutParams2.topMargin = SpecifiedPredictionPreviewView.this.dp(3);
            linearLayout.addView(textViewText, layoutParams2);
            FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(SpecifiedPredictionPreviewView.this.dp(34), SpecifiedPredictionPreviewView.this.dp(34), 8388661);
            layoutParams3.setMargins(0, -SpecifiedPredictionPreviewView.this.dp(4), -SpecifiedPredictionPreviewView.this.dp(4), 0);
            frameLayout.addView(hitBadge, layoutParams3);
            frameLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedPredictionPreviewView$Card$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.m1536xf4aa7e0a(view2);
                }
            });
        }

        /* renamed from: lambda$new$1$com-android-support-SpecifiedPredictionPreviewView$Card, reason: not valid java name */
        /* synthetic */ void m1536xf4aa7e0a(View view) {
            if (this.bound == null || !SpecifiedPredictionPreviewView.this.presenter.canSelect(this.bound)) {
                return;
            }
            SpecifiedPredictionPreviewView.this.presenter.select(this.bound);
        }

        private void paintSurface(boolean z) {
            GradientDrawable gradientDrawable = this.surface;
            if (z) {
                gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                this.surface.setColors(new int[]{1198252954, 443278234});
                this.surface.setStroke(SpecifiedPredictionPreviewView.this.dp(2), SpecifiedPredictionPreviewView.GREEN);
            } else {
                gradientDrawable.setColor(this.ghost ? 234881023 : 402653183);
                this.surface.setStroke(SpecifiedPredictionPreviewView.this.dp(1), this.ghost ? 620756991 : 956301311);
            }
        }

        void bindGhost() {
            if (this.ghost && this.bound == null) {
                return;
            }
            clear();
            this.ghost = true;
            paintSurface(false);
            this.name.setText("");
            this.source.setVisibility(8);
            this.root.setClickable(false);
            this.root.setFocusable(false);
            this.root.setContentDescription("等待预测");
        }

        void bind(SpecifiedPredictionPreview.Item item, boolean z) {
            SpecifiedPredictionPreview.Item item2;
            String strName = SpecifiedPredictionPreviewView.this.presenter.name(item);
            String strDetail = SpecifiedPredictionPreviewView.this.presenter.detail(item);
            int iDp = (SpecifiedPredictionPreviewView.this.iconOnly || SpecifiedPredictionPreviewView.this.getHeight() <= 0 || SpecifiedPredictionPreviewView.this.getHeight() >= SpecifiedPredictionPreviewView.this.dp(96)) ? SpecifiedPredictionPreviewView.this.dp(44) : Math.min(SpecifiedPredictionPreviewView.this.dp(36), Math.max(SpecifiedPredictionPreviewView.this.dp(24), SpecifiedPredictionPreviewView.this.getHeight() - SpecifiedPredictionPreviewView.this.dp(30)));
            ViewGroup.LayoutParams layoutParams = this.icon.getLayoutParams();
            if (!SpecifiedPredictionPreviewView.this.iconOnly && (layoutParams.width != iDp || layoutParams.height != iDp)) {
                layoutParams.width = iDp;
                layoutParams.height = iDp;
                this.icon.setLayoutParams(layoutParams);
                ViewGroup.LayoutParams layoutParams2 = this.art.getLayoutParams();
                layoutParams2.width = SpecifiedPredictionPreviewView.this.dp(16) + iDp;
                layoutParams2.height = SpecifiedPredictionPreviewView.this.dp(8) + iDp;
                this.art.setLayoutParams(layoutParams2);
            }
            boolean z2 = this.ghost;
            this.ghost = false;
            boolean z3 = (!z2 && (item2 = this.bound) != null && item2.id == item.id && this.bound.inputId == item.inputId && this.boundName.equals(strName) && this.boundIconSize == iDp) ? false : true;
            this.bound = item;
            if (z3) {
                this.boundName = strName;
                this.boundIconSize = iDp;
                this.name.setText(strName);
                Glide.with(this.icon).clear(this.icon);
                SpecifiedPredictionPreviewView.this.presenter.bindIcon(this.icon, item, iDp);
                if (SpecifiedPredictionPreview.KIND_RECAST.equals(SpecifiedPredictionPreviewView.this.kind)) {
                    this.source.setVisibility(0);
                    Glide.with(this.source).clear(this.source);
                    SpecifiedPredictionPreviewView.this.presenter.bindSourceIcon(this.source, item, SpecifiedPredictionPreviewView.this.dp(18));
                }
                this.icon.animate().cancel();
                boolean zAnimationsOn = SpecifiedPredictionPreviewView.this.animationsOn();
                ImageView imageView = this.icon;
                if (zAnimationsOn) {
                    imageView.setAlpha(0.35f);
                    this.icon.animate().alpha(1.0f).setDuration(110L).start();
                } else {
                    imageView.setAlpha(1.0f);
                }
            }
            this.root.removeCallbacks(this.releaseHit);
            if (item.selected) {
                showHit(true);
            } else if (this.hitShown) {
                if (!SpecifiedPredictionPreviewView.this.iconOnly || z3) {
                    showHit(false);
                } else {
                    this.root.postDelayed(this.releaseHit, SpecifiedPredictionPreviewView.HIT_RELEASE_MS);
                }
            } else if (z2) {
                paintSurface(false);
            }
            String str = "";
            if (z && item.selected) {
                String str2 = item.slot + ":" + item.id;
                if (!str2.equals(this.celebratedKey)) {
                    this.celebratedKey = str2;
                    celebrate();
                }
            } else if (!z) {
                this.celebratedKey = "";
            }
            boolean zCanSelect = SpecifiedPredictionPreviewView.this.presenter.canSelect(item);
            this.root.setClickable(zCanSelect);
            this.root.setFocusable(zCanSelect);
            FrameLayout frameLayout = this.root;
            StringBuilder sbAppend = new StringBuilder("位置 ").append(item.slot + 1).append("，").append(strName).append("，").append(strDetail).append(item.selected ? "，命中已选目标" : "");
            if (zCanSelect) {
                str = "，点击选择目标";
            }
            frameLayout.setContentDescription(sbAppend.append(str).toString());
        }

        private void showHit(boolean z) {
            if (z == this.hitShown) {
                return;
            }
            this.hitShown = z;
            boolean zAnimationsOn = SpecifiedPredictionPreviewView.this.animationsOn();
            paintSurface(z);
            this.halo.animate().cancel();
            this.icon.animate().cancel();
            this.icon.setAlpha(1.0f);
            HitBadge hitBadge = this.badge;
            if (!z) {
                hitBadge.hide(zAnimationsOn);
                View view = this.halo;
                if (zAnimationsOn) {
                    view.animate().alpha(0.0f).setDuration(160L).start();
                } else {
                    view.setAlpha(0.0f);
                }
                this.icon.setScaleX(1.0f);
                this.icon.setScaleY(1.0f);
                return;
            }
            hitBadge.show(zAnimationsOn);
            View view2 = this.halo;
            if (!zAnimationsOn) {
                view2.setAlpha(0.75f);
                return;
            }
            view2.setAlpha(0.0f);
            this.halo.setScaleX(0.6f);
            this.halo.setScaleY(0.6f);
            this.halo.animate().alpha(0.75f).scaleX(1.0f).scaleY(1.0f).setDuration(260L).setInterpolator(new DecelerateInterpolator()).start();
            this.icon.setScaleX(0.86f);
            this.icon.setScaleY(0.86f);
            this.icon.animate().scaleX(1.0f).scaleY(1.0f).setDuration(SpecifiedPredictionPreviewView.HIT_RELEASE_MS).setInterpolator(new OvershootInterpolator(2.2f)).start();
        }

        private void celebrate() {
            showHit(true);
            if (SpecifiedPredictionPreviewView.this.animationsOn()) {
                this.badge.show(true);
                this.root.animate().cancel();
                this.root.setScaleX(0.84f);
                this.root.setScaleY(0.84f);
                this.root.animate().scaleX(1.0f).scaleY(1.0f).setDuration(420L).setInterpolator(new OvershootInterpolator(3.2f)).start();
                this.halo.animate().cancel();
                this.halo.setAlpha(1.0f);
                this.halo.setScaleX(1.35f);
                this.halo.setScaleY(1.35f);
                this.halo.animate().alpha(0.8f).scaleX(1.0f).scaleY(1.0f).setDuration(700L).setInterpolator(new DecelerateInterpolator()).start();
            }
        }

        void clear() {
            this.root.removeCallbacks(this.releaseHit);
            this.root.animate().cancel();
            this.icon.animate().cancel();
            this.halo.animate().cancel();
            this.badge.cancel();
            this.badge.setVisibility(8);
            this.halo.setAlpha(0.0f);
            this.icon.setAlpha(1.0f);
            this.icon.setScaleX(1.0f);
            this.icon.setScaleY(1.0f);
            Glide.with(this.icon).clear(this.icon);
            this.icon.setImageDrawable(null);
            Glide.with(this.source).clear(this.source);
            this.source.setImageDrawable(null);
            this.root.setScaleX(1.0f);
            this.root.setScaleY(1.0f);
            this.bound = null;
            this.boundName = "";
            this.hitShown = false;
            this.ghost = false;
            this.celebratedKey = "";
        }
    }

    void dispose() {
        this.disposed = true;
        SharedMemManager sharedMemManager = this.owner;
        if (sharedMemManager != null) {
            sharedMemManager.clearSpecifiedPredictionPreviewListener(this.kind, this.listener);
        }
        this.owner = null;
        removeCallbacks(this.renderLatest);
        animate().cancel();
        Iterator<Card> it = this.holders.iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
        this.holders.clear();
        this.cards.removeAllViews();
        this.latest = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TextView text(int i, int i2) {
        TextView textView = new TextView(getContext());
        textView.setTextSize(i);
        textView.setTextColor(i2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        return textView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public GradientDrawable background(int i, int i2, int i3) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(i3));
        gradientDrawable.setColor(i);
        gradientDrawable.setStroke(dp(1), i2);
        return gradientDrawable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int dp(int i) {
        return Math.round(i * getResources().getDisplayMetrics().density);
    }
}
