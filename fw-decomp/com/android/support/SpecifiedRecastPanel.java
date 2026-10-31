package com.android.support;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import com.android.support.CompanionPairLayout;
import com.android.support.EquipmentIdMapper;
import com.android.support.OverlayActionButtonStyle;
import com.android.support.RecastEquipmentCatalog;
import com.android.support.SharedMemManager;
import com.android.support.SpecifiedPredictionPreview;
import com.android.support.SpecifiedPredictionPreviewView;
import com.android.support.lineup.EquipmentImageResolver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
final class SpecifiedRecastPanel {
    private static final int ACCENT = -10556417;
    private static final int ACCENT_FILL = 1029630975;
    private static final int ACCENT_ON = -16506581;
    private static final int ACCENT_STROKE = -1939936257;
    private static final int CELL_NORMAL = 0;
    private static final int CELL_SELECTED = 1;
    private static final String[] CLASS_DIGITS = {"一", "二", "三", "四", "五"};
    private static final boolean ENGINE_AVAILABLE = true;
    private static final String KEY_TARGET = "target_id";
    private static final String KEY_TIER = "tier_";
    private static final String PREFS = "jcc_specified_recast";
    private static final int STATUS_BUSY = -11930;
    private static final int STATUS_FAIL = -36205;
    private static final int STATUS_IDLE = -3613458;
    private static final int STATUS_OK = -9706598;
    private static final int SURFACE_FILL = 452984831;
    private static final int SURFACE_STROKE = 1040187391;
    private static final String TAG = "RecastPredict";
    private static final int TEXT_PRIMARY = -985089;
    private static final int TEXT_SECONDARY = -3613458;
    private static final int TEXT_TERTIARY = -6310196;
    private RecastAdapter adapter;
    private float cachedDensity;
    private RecastEquipmentCatalog catalog;
    private long catalogLoadRevision;
    private int cellHeightPx;
    private int cellIconPx;
    private int cellNamePx;
    private TextView clearButton;
    private final Context context;
    private HexCueSound cueSound;
    private volatile boolean disposed;
    private TextView emptyText;
    private GridView grid;
    private TextView howToText;
    private RequestOptions iconOptions;
    private int justToggledId;
    private RecastRunStatus lastRunStatus;
    private final SharedMemManager manager;
    private TextView pickCountText;
    private View pickerRoot;
    private SpecifiedPredictionPreviewView predictionPreview;
    private final SharedPreferences prefs;
    private ObjectAnimator pulse;
    private RecastLivePoolCatalog requestedLivePool;
    private TextView resultCountText;
    private TextView ruleText;
    private boolean running;
    private EditText search;
    private boolean seasonRecognized;
    private TextView selectedCountText;
    private TextView selectedDescription;
    private TextView selectedText;
    private Button startButton;
    private View statusDot;
    private TextView statusText;
    private Button stopButton;
    private Switch strictSwitch;
    private LinearLayout targetsContainer;
    private LinearLayout tierChips;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService loader = Executors.newSingleThreadExecutor();
    private final List<RecastEquipmentCatalog.Entry> filtered = new ArrayList();
    private final LinkedHashMap<Integer, LinkedHashMap<Integer, RecastEquipmentCatalog.Entry>> selectedByTier = new LinkedHashMap<>();
    private final Map<Integer, EquipmentImageResolver.Resolution> iconCache = new HashMap();
    private final List<TextView> tierChipViews = new ArrayList();
    private final List<Integer> tierChipValues = new ArrayList();
    private int editingGroup = RecastTierPolicy.displayGroupOf(3);
    private int appliedColumnWidthPx = -1;
    private int quantizedGridWidthPx = -1;
    private int quantizedGridHeightPx = -1;
    private final RecastReminderPolicy reminderPolicy = new RecastReminderPolicy();
    private int currentSeason = -1;
    private final LinkedHashMap<Integer, Integer> emblemClassOrdinal = new LinkedHashMap<>();
    private int emblemSubTier = 0;
    private int lastLoggedButtonState = Integer.MIN_VALUE;

    SpecifiedRecastPanel(Context context, View view, View view2, SharedMemManager sharedMemManager) {
        this.context = context;
        this.manager = sharedMemManager;
        this.prefs = context.getSharedPreferences(PREFS, 0);
        this.cueSound = new HexCueSound(context);
        for (int i : RecastTierPolicy.selectableTiers()) {
            this.selectedByTier.put(Integer.valueOf(i), new LinkedHashMap<>());
        }
        bindViews(view, view2);
        if (sharedMemManager != null) {
            sharedMemManager.setRecastRunStatusListener(new SharedMemManager.RecastRunStatusListener() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda13
                @Override // com.android.support.SharedMemManager.RecastRunStatusListener
                public final void onRecastRunStatus(RecastRunStatus recastRunStatus) {
                    this.f$0.onRunStatus(recastRunStatus);
                }
            });
            sharedMemManager.setRecastLivePoolListener(new SharedMemManager.RecastLivePoolListener() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda14
                @Override // com.android.support.SharedMemManager.RecastLivePoolListener
                public final void onRecastLivePool(RecastLivePoolCatalog recastLivePoolCatalog) {
                    this.f$0.onLivePool(recastLivePoolCatalog);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLivePool(final RecastLivePoolCatalog recastLivePoolCatalog) {
        if (this.disposed) {
            return;
        }
        if (recastLivePoolCatalog == null) {
            this.requestedLivePool = null;
            this.catalog = null;
            this.iconCache.clear();
            loadCatalog();
            return;
        }
        if (recastLivePoolCatalog.catalog == null || this.requestedLivePool == recastLivePoolCatalog) {
            return;
        }
        this.requestedLivePool = recastLivePoolCatalog;
        final long j = this.catalogLoadRevision + 1;
        this.catalogLoadRevision = j;
        final RecastEquipmentCatalog recastEquipmentCatalog = recastLivePoolCatalog.catalog;
        final boolean z = recastLivePoolCatalog.complete;
        this.loader.execute(new Runnable() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1568lambda$onLivePool$1$comandroidsupportSpecifiedRecastPanel(recastEquipmentCatalog, j, recastLivePoolCatalog, z);
            }
        });
    }

    /* renamed from: lambda$onLivePool$1$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1568lambda$onLivePool$1$comandroidsupportSpecifiedRecastPanel(final RecastEquipmentCatalog recastEquipmentCatalog, final long j, final RecastLivePoolCatalog recastLivePoolCatalog, final boolean z) {
        final Map<Integer, EquipmentImageResolver.Resolution> mapResolveIcons = resolveIcons(recastEquipmentCatalog);
        this.mainHandler.post(new Runnable() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1567lambda$onLivePool$0$comandroidsupportSpecifiedRecastPanel(j, recastLivePoolCatalog, recastEquipmentCatalog, mapResolveIcons, z);
            }
        });
    }

    /* renamed from: lambda$onLivePool$0$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1567lambda$onLivePool$0$comandroidsupportSpecifiedRecastPanel(long j, RecastLivePoolCatalog recastLivePoolCatalog, RecastEquipmentCatalog recastEquipmentCatalog, Map map, boolean z) {
        SharedMemManager sharedMemManager;
        if (!this.disposed && j == this.catalogLoadRevision && this.requestedLivePool == recastLivePoolCatalog && (sharedMemManager = this.manager) != null && MatchStatePolicy.accepts(sharedMemManager.getCurrentMatchGeneration(), recastLivePoolCatalog.matchGeneration)) {
            this.catalog = recastEquipmentCatalog;
            this.iconCache.putAll(map);
            Dbg.i(TAG, "[指定重铸] 已换用本局候选池：条目=" + recastEquipmentCatalog.size() + " 完整=" + z);
            dropTargetMissingFromCatalog(z);
            if (!groupHasEntries(this.editingGroup)) {
                this.editingGroup = firstNonEmptyGroup();
            }
            installTierChips();
            applyFilter();
            updateSelectionUi();
            renderStatus();
        }
    }

    private void dropTargetMissingFromCatalog(boolean z) {
        RecastEquipmentCatalog.Entry entryCurrentTarget;
        if (!z || this.catalog == null || (entryCurrentTarget = currentTarget()) == null || this.catalog.byId(entryCurrentTarget.id) != null) {
            return;
        }
        if (this.running) {
            stop();
        }
        Iterator<LinkedHashMap<Integer, RecastEquipmentCatalog.Entry>> it = this.selectedByTier.values().iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
        persistSelection();
        Dbg.i(TAG, "[指定重铸] 目标不在本局候选池，已清除：ID=" + entryCurrentTarget.id + " 名称=" + entryCurrentTarget.name);
        toast("这一局没有「" + entryCurrentTarget.name + "」· 不同赛季的装备不一样，请重新选一件");
    }

    void onSeasonChanged(int i, boolean z) {
        if (this.disposed) {
            return;
        }
        if (this.currentSeason == i && this.seasonRecognized == z) {
            return;
        }
        this.currentSeason = i;
        this.seasonRecognized = z;
        if (this.catalog == null || !z) {
            return;
        }
        Dbg.i(TAG, "[指定重铸] 赛季检测=" + i + " 目录标记=" + this.catalog.seasonTag() + "/" + this.catalog.gameVersion());
    }

    private void bindViews(View view, View view2) {
        LinearLayout linearLayout;
        this.pickerRoot = view2;
        this.search = (EditText) view2.findViewById(R.id.etSpecifiedRecastSearch);
        this.tierChips = (LinearLayout) view2.findViewById(R.id.llSpecifiedRecastTierChips);
        this.grid = (GridView) view2.findViewById(R.id.gridSpecifiedRecast);
        this.ruleText = (TextView) view2.findViewById(R.id.tvSpecifiedRecastRule);
        this.resultCountText = (TextView) view2.findViewById(R.id.tvSpecifiedRecastResultCount);
        this.emptyText = (TextView) view2.findViewById(R.id.tvSpecifiedRecastEmpty);
        this.pickCountText = (TextView) view2.findViewById(R.id.tvSpecifiedRecastPickCount);
        this.clearButton = (TextView) view2.findViewById(R.id.btnSpecifiedRecastClear);
        this.selectedText = (TextView) view.findViewById(R.id.tvSpecifiedRecastSelected);
        this.selectedDescription = (TextView) view.findViewById(R.id.tvSpecifiedRecastSelectedDescription);
        this.howToText = (TextView) view.findViewById(R.id.tvSpecifiedRecastHowTo);
        this.selectedCountText = (TextView) view.findViewById(R.id.tvSpecifiedRecastSelectedCount);
        this.targetsContainer = (LinearLayout) view.findViewById(R.id.llSpecifiedRecastTargets);
        this.statusText = (TextView) view.findViewById(R.id.tvSpecifiedRecastStatus);
        this.statusDot = view.findViewById(R.id.viewSpecifiedRecastStatusDot);
        this.startButton = (Button) view.findViewById(R.id.btnSpecifiedRecastStart);
        this.stopButton = (Button) view.findViewById(R.id.btnSpecifiedRecastStop);
        Switch r3 = (Switch) view.findViewById(R.id.switchSpecifiedRecastStrict);
        this.strictSwitch = r3;
        r3.setChecked(this.prefs.getBoolean("specifiedRecastStrict", false));
        this.strictSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda17
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.m1558lambda$bindViews$2$comandroidsupportSpecifiedRecastPanel(compoundButton, z);
            }
        });
        if (this.search == null || this.tierChips == null || this.grid == null || this.ruleText == null || this.resultCountText == null || this.emptyText == null || this.pickCountText == null || this.clearButton == null || this.selectedText == null || this.selectedDescription == null || this.selectedCountText == null || (linearLayout = this.targetsContainer) == null || this.statusText == null || this.startButton == null || this.stopButton == null) {
            throw new IllegalStateException("specified recast page is incomplete");
        }
        SpecifiedPredictionPreviewView specifiedPredictionPreviewViewInstallInline = SpecifiedPredictionPreviewView.installInline((FrameLayout) linearLayout.getParent().getParent(), SpecifiedPredictionPreview.KIND_RECAST, new SpecifiedPredictionPreviewView.Presenter() { // from class: com.android.support.SpecifiedRecastPanel.1
            private RecastEquipmentCatalog.Entry entry(int i) {
                if (SpecifiedRecastPanel.this.catalog == null) {
                    return null;
                }
                return SpecifiedRecastPanel.this.catalog.byId(i);
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public String name(SpecifiedPredictionPreview.Item item) {
                RecastEquipmentCatalog.Entry entry = entry(item.id);
                return entry == null ? "装备 " + item.id : entry.name;
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public String detail(SpecifiedPredictionPreview.Item item) {
                RecastEquipmentCatalog.Entry entry = entry(item.inputId);
                if (entry == null) {
                    return "第 " + (item.slot + 1) + " 件装备";
                }
                return entry.name + " → " + name(item);
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public void bindIcon(ImageView imageView, SpecifiedPredictionPreview.Item item, int i) {
                bindEquipmentIcon(imageView, entry(item.id), i);
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public void bindSourceIcon(ImageView imageView, SpecifiedPredictionPreview.Item item, int i) {
                bindEquipmentIcon(imageView, entry(item.inputId), i);
            }

            private void bindEquipmentIcon(ImageView imageView, RecastEquipmentCatalog.Entry entry, int i) {
                imageView.setTag(R.id.recast_icon_bound_key, null);
                if (entry == null) {
                    imageView.setImageResource(R.drawable.bg_equipment_placeholder);
                } else {
                    SpecifiedRecastPanel.this.loadIcon(imageView, entry, i);
                }
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public boolean canSelect(SpecifiedPredictionPreview.Item item) {
                return (SpecifiedRecastPanel.this.running || entry(item.id) == null) ? false : true;
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public void select(SpecifiedPredictionPreview.Item item) {
                RecastEquipmentCatalog.Entry entry = entry(item.id);
                if (SpecifiedRecastPanel.this.running || entry == null) {
                    return;
                }
                SpecifiedRecastPanel.this.toggleSelection(entry);
            }
        });
        this.predictionPreview = specifiedPredictionPreviewViewInstallInline;
        specifiedPredictionPreviewViewInstallInline.bind(this.manager);
        RecastAdapter recastAdapter = new RecastAdapter();
        this.adapter = recastAdapter;
        this.grid.setAdapter((ListAdapter) recastAdapter);
        this.grid.setEmptyView(this.emptyText);
        this.search.addTextChangedListener(new TextWatcher() { // from class: com.android.support.SpecifiedRecastPanel.2
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                SpecifiedRecastPanel.this.applyFilter();
            }
        });
        this.clearButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                this.f$0.m1559lambda$bindViews$3$comandroidsupportSpecifiedRecastPanel(view3);
            }
        });
        this.startButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                this.f$0.m1560lambda$bindViews$4$comandroidsupportSpecifiedRecastPanel(view3);
            }
        });
        this.stopButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                this.f$0.m1561lambda$bindViews$5$comandroidsupportSpecifiedRecastPanel(view3);
            }
        });
        stylePillCount(this.pickCountText);
        stylePillCount(this.selectedCountText);
        updateSelectionUi();
        renderStatus();
        applyPickerDensity();
        loadCatalog();
    }

    /* renamed from: lambda$bindViews$2$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1558lambda$bindViews$2$comandroidsupportSpecifiedRecastPanel(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("specifiedRecastStrict", z).apply();
    }

    /* renamed from: lambda$bindViews$3$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1559lambda$bindViews$3$comandroidsupportSpecifiedRecastPanel(View view) {
        clearSelection();
    }

    /* renamed from: lambda$bindViews$4$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1560lambda$bindViews$4$comandroidsupportSpecifiedRecastPanel(View view) {
        start();
    }

    /* renamed from: lambda$bindViews$5$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1561lambda$bindViews$5$comandroidsupportSpecifiedRecastPanel(View view) {
        stop();
    }

    void applyPickerDensity() {
        if (this.pickerRoot == null) {
            return;
        }
        Resources resources = this.context.getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.xf_hexpick_col_w);
        if (dimensionPixelSize != this.appliedColumnWidthPx) {
            this.appliedColumnWidthPx = dimensionPixelSize;
            refreshCellMetricsCache(resources);
            int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.xf_hexpick_pad);
            int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.xf_hexpick_gap);
            this.pickerRoot.setPadding(dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize2);
            EditText editText = this.search;
            if (editText != null) {
                setViewHeight(editText, resources.getDimensionPixelSize(R.dimen.xf_hexpick_search_h));
                setTopMargin(this.search, dimensionPixelSize3);
            }
            setTopMargin(this.pickerRoot.findViewById(R.id.recastPickTierRow), dimensionPixelSize3);
            TextView textView = this.ruleText;
            View view = textView == null ? null : (View) textView.getParent();
            if (view != null) {
                setViewHeight(view, resources.getDimensionPixelSize(R.dimen.xf_hexpick_count_h));
                setTopMargin(view, dimensionPixelSize3);
            }
            invalidateGridQuantization();
        }
        applyGridQuantization();
    }

    private void invalidateGridQuantization() {
        this.quantizedGridWidthPx = -1;
        this.quantizedGridHeightPx = -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyGridQuantization() {
        GridView gridView = this.grid;
        if (gridView == null) {
            return;
        }
        int width = (gridView.getWidth() - this.grid.getPaddingLeft()) - this.grid.getPaddingRight();
        int height = (this.grid.getHeight() - this.grid.getPaddingTop()) - this.grid.getPaddingBottom();
        if (width <= 0 || height <= 0) {
            this.grid.post(new Runnable() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.applyGridQuantization();
                }
            });
            return;
        }
        if (width == this.quantizedGridWidthPx && height == this.quantizedGridHeightPx) {
            return;
        }
        this.quantizedGridWidthPx = width;
        this.quantizedGridHeightPx = height;
        Resources resources = this.context.getResources();
        CompanionPairLayout.Grid gridResolveGrid = CompanionPairLayout.resolveGrid(width, height, resources.getDimensionPixelSize(R.dimen.xf_hexpick_col_w), resources.getDimensionPixelSize(R.dimen.xf_hexpick_cell_h), Math.max(this.grid.getVerticalSpacing(), this.grid.getHorizontalSpacing()));
        this.cellHeightPx = gridResolveGrid.cellHeightPx;
        this.grid.setNumColumns(gridResolveGrid.columns);
        this.grid.setColumnWidth(gridResolveGrid.cellWidthPx);
        RecastAdapter recastAdapter = this.adapter;
        if (recastAdapter != null) {
            recastAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshCellMetricsCache(Resources resources) {
        this.cellHeightPx = resources.getDimensionPixelSize(R.dimen.xf_hexpick_cell_h);
        this.cellIconPx = resources.getDimensionPixelSize(R.dimen.xf_hexpick_icon);
        this.cellNamePx = resources.getDimensionPixelSize(R.dimen.xf_hexpick_name_h);
        RequestOptions requestOptionsError = new RequestOptions().transform(new RoundedCorners(dp(4))).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.bg_equipment_placeholder).error(R.drawable.bg_equipment_placeholder);
        int i = this.cellIconPx;
        this.iconOptions = requestOptionsError.override(i, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setViewHeight(View view, int i) {
        ViewGroup.LayoutParams layoutParams;
        if (view == null || (layoutParams = view.getLayoutParams()) == null || layoutParams.height == i) {
            return;
        }
        layoutParams.height = i;
        view.setLayoutParams(layoutParams);
    }

    private static void setTopMargin(View view, int i) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            if (marginLayoutParams.topMargin != i) {
                marginLayoutParams.topMargin = i;
                view.setLayoutParams(marginLayoutParams);
            }
        }
    }

    private void loadCatalog() {
        final long j = this.catalogLoadRevision + 1;
        this.catalogLoadRevision = j;
        this.resultCountText.setText("正在载入目录");
        this.loader.execute(new Runnable() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1566lambda$loadCatalog$8$comandroidsupportSpecifiedRecastPanel(j);
            }
        });
    }

    /* renamed from: lambda$loadCatalog$8$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1566lambda$loadCatalog$8$comandroidsupportSpecifiedRecastPanel(long j) {
        final SpecifiedRecastPanel specifiedRecastPanel;
        final long j2;
        final RecastEquipmentCatalog recastEquipmentCatalogLoad;
        final Map<Integer, EquipmentImageResolver.Resolution> mapResolveIcons;
        try {
            recastEquipmentCatalogLoad = RecastEquipmentCatalog.load(this.context);
            mapResolveIcons = resolveIcons(recastEquipmentCatalogLoad);
            specifiedRecastPanel = this;
            j2 = j;
        } catch (Exception e) {
            e = e;
            specifiedRecastPanel = this;
            j2 = j;
        }
        try {
            this.mainHandler.post(new Runnable() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1564lambda$loadCatalog$6$comandroidsupportSpecifiedRecastPanel(j2, recastEquipmentCatalogLoad, mapResolveIcons);
                }
            });
        } catch (Exception e2) {
            e = e2;
            Log.e(TAG, "[指定重铸] 装备目录加载失败", e);
            specifiedRecastPanel.mainHandler.post(new Runnable() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1565lambda$loadCatalog$7$comandroidsupportSpecifiedRecastPanel(j2);
                }
            });
        }
    }

    /* renamed from: lambda$loadCatalog$6$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1564lambda$loadCatalog$6$comandroidsupportSpecifiedRecastPanel(long j, RecastEquipmentCatalog recastEquipmentCatalog, Map map) {
        if (this.disposed || j != this.catalogLoadRevision) {
            return;
        }
        this.catalog = recastEquipmentCatalog;
        this.iconCache.putAll(map);
        if (!groupHasEntries(this.editingGroup)) {
            this.editingGroup = firstNonEmptyGroup();
        }
        restoreSelectionFromPrefs();
        installTierChips();
        applyFilter();
        updateSelectionUi();
        renderStatus();
    }

    /* renamed from: lambda$loadCatalog$7$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1565lambda$loadCatalog$7$comandroidsupportSpecifiedRecastPanel(long j) {
        if (this.disposed || j != this.catalogLoadRevision) {
            return;
        }
        setStatus("装备目录校验失败", -36205);
        this.resultCountText.setText("目录不可用");
        this.emptyText.setText("装备目录校验失败");
    }

    private Map<Integer, EquipmentImageResolver.Resolution> resolveIcons(RecastEquipmentCatalog recastEquipmentCatalog) {
        HashMap map = new HashMap();
        for (int i : RecastTierPolicy.selectableTiers()) {
            for (RecastEquipmentCatalog.Entry entry : recastEquipmentCatalog.tier(i)) {
                map.put(Integer.valueOf(entry.id), EquipmentImageResolver.resolve(this.context, new EquipmentIdMapper.EquipInfo(entry.id, entry.name, entry.imageName, RecastTierPolicy.tierFullName(entry.tier)), Integer.toString(entry.id)));
            }
        }
        return map;
    }

    private boolean groupHasEntries(int i) {
        if (this.catalog == null) {
            return false;
        }
        for (int i2 : RecastTierPolicy.displayGroupTiers(i)) {
            if (this.catalog.hasTier(i2)) {
                return true;
            }
        }
        return false;
    }

    private int firstNonEmptyGroup() {
        for (int i = 0; i < RecastTierPolicy.displayGroupCount(); i++) {
            if (groupHasEntries(i)) {
                return i;
            }
        }
        return 0;
    }

    private String emblemClassLabel(int i) {
        Integer num = this.emblemClassOrdinal.get(Integer.valueOf(i));
        if (num == null || num.intValue() < 1) {
            return "";
        }
        int iIntValue = num.intValue();
        String[] strArr = CLASS_DIGITS;
        if (iIntValue > strArr.length) {
            return "";
        }
        return strArr[num.intValue() - 1] + "类";
    }

    private String classifiedLabel(int i) {
        if (i <= 0 || !RecastTierPolicy.isSelectable(i)) {
            return "";
        }
        String strEmblemClassLabel = emblemClassLabel(i);
        return strEmblemClassLabel.isEmpty() ? categoryLabel(i) : EquipmentIdMapper.CAT_EMBLEM + strEmblemClassLabel;
    }

    private boolean tierHasEntries(int i) {
        RecastEquipmentCatalog recastEquipmentCatalog = this.catalog;
        return (recastEquipmentCatalog == null || recastEquipmentCatalog.tier(i).isEmpty()) ? false : true;
    }

    private boolean classHasMatch(int i, String str) {
        RecastEquipmentCatalog recastEquipmentCatalog = this.catalog;
        if (recastEquipmentCatalog == null) {
            return false;
        }
        Iterator<RecastEquipmentCatalog.Entry> it = recastEquipmentCatalog.tier(i).iterator();
        while (it.hasNext()) {
            if (it.next().searchKey.contains(str)) {
                return true;
            }
        }
        return false;
    }

    private void assignEmblemClasses() {
        this.emblemClassOrdinal.clear();
        int i = 0;
        for (int i2 = 0; i2 < RecastTierPolicy.displayGroupCount(); i2++) {
            for (int i3 : RecastTierPolicy.displayGroupTiers(i2)) {
                if (RecastTierPolicy.isEmblem(i3) && tierHasEntries(i3)) {
                    i++;
                    this.emblemClassOrdinal.put(Integer.valueOf(i3), Integer.valueOf(i));
                    if (this.emblemSubTier == 0) {
                        this.emblemSubTier = i3;
                    }
                }
            }
        }
        if (this.emblemClassOrdinal.containsKey(Integer.valueOf(this.emblemSubTier))) {
            return;
        }
        this.emblemSubTier = this.emblemClassOrdinal.isEmpty() ? 0 : this.emblemClassOrdinal.keySet().iterator().next().intValue();
    }

    private void installTierChips() {
        this.tierChips.removeAllViews();
        this.tierChipViews.clear();
        this.tierChipValues.clear();
        if (this.catalog == null) {
            return;
        }
        assignEmblemClasses();
        for (final int i = 0; i < RecastTierPolicy.displayGroupCount(); i++) {
            if (groupHasEntries(i)) {
                TextView textView = new TextView(this.context);
                textView.setText(RecastTierPolicy.displayGroupName(i));
                textView.setTextSize(2, 12.0f);
                textView.setTypeface(Typeface.DEFAULT, 1);
                textView.setGravity(17);
                textView.setPadding(dp(12), dp(5), dp(12), dp(5));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                if (!this.tierChipViews.isEmpty()) {
                    layoutParams.leftMargin = dp(6);
                }
                textView.setLayoutParams(layoutParams);
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda7
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.m1563x3c1aa832(i, view);
                    }
                });
                this.tierChips.addView(textView);
                this.tierChipViews.add(textView);
                this.tierChipValues.add(Integer.valueOf(i));
            }
        }
        installEmblemClassChips();
        refreshTierChipStyles();
    }

    /* renamed from: lambda$installTierChips$9$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1563x3c1aa832(int i, View view) {
        if (this.editingGroup == i) {
            return;
        }
        this.editingGroup = i;
        installTierChips();
        applyFilter();
        updateSelectionUi();
        renderStatus();
        animateGridSwap();
    }

    private boolean isEmblemGroup(int i) {
        int[] iArrDisplayGroupTiers = RecastTierPolicy.displayGroupTiers(i);
        if (iArrDisplayGroupTiers.length == 0) {
            return false;
        }
        for (int i2 : iArrDisplayGroupTiers) {
            if (!RecastTierPolicy.isEmblem(i2)) {
                return false;
            }
        }
        return true;
    }

    private void installEmblemClassChips() {
        if (!isEmblemGroup(this.editingGroup) || this.emblemClassOrdinal.size() < 2) {
            return;
        }
        View view = new View(this.context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp(1), dp(16));
        layoutParams.leftMargin = dp(8);
        layoutParams.rightMargin = dp(2);
        layoutParams.gravity = 16;
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(SURFACE_STROKE);
        this.tierChips.addView(view);
        Iterator<Map.Entry<Integer, Integer>> it = this.emblemClassOrdinal.entrySet().iterator();
        while (it.hasNext()) {
            final int iIntValue = it.next().getKey().intValue();
            int i = iIntValue == this.emblemSubTier ? 1 : 0;
            TextView textView = new TextView(this.context);
            textView.setText(emblemClassLabel(iIntValue));
            textView.setTextSize(2, 11.0f);
            textView.setTypeface(Typeface.DEFAULT, i);
            textView.setGravity(17);
            textView.setPadding(dp(10), dp(4), dp(10), dp(4));
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
            layoutParams2.leftMargin = dp(6);
            textView.setLayoutParams(layoutParams2);
            int iTierColor = RecastTierPolicy.tierColor(iIntValue);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(0);
            gradientDrawable.setCornerRadius(dp(12));
            gradientDrawable.setColor(i != 0 ? (16777215 & iTierColor) | 1023410176 : SURFACE_FILL);
            gradientDrawable.setStroke(dp(1), i != 0 ? iTierColor : SURFACE_STROKE);
            textView.setBackground(gradientDrawable);
            if (i == 0) {
                iTierColor = TEXT_TERTIARY;
            }
            textView.setTextColor(iTierColor);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.m1562xc2d47392(iIntValue, view2);
                }
            });
            this.tierChips.addView(textView);
        }
    }

    /* renamed from: lambda$installEmblemClassChips$10$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1562xc2d47392(int i, View view) {
        if (this.emblemSubTier == i) {
            return;
        }
        this.emblemSubTier = i;
        installTierChips();
        applyFilter();
        updateSelectionUi();
        renderStatus();
        animateGridSwap();
    }

    private void animateGridSwap() {
        this.grid.animate().cancel();
        this.grid.setAlpha(0.35f);
        this.grid.animate().alpha(1.0f).setDuration(160L).start();
    }

    private void refreshTierChipStyles() {
        for (int i = 0; i < this.tierChipViews.size(); i++) {
            int iIntValue = this.tierChipValues.get(i).intValue();
            styleTierChip(this.tierChipViews.get(i), iIntValue, iIntValue == this.editingGroup);
        }
    }

    private void styleTierChip(TextView textView, int i, boolean z) {
        int iDisplayGroupColor = RecastTierPolicy.displayGroupColor(i);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(dp(13));
        if (z) {
            gradientDrawable.setColor(iDisplayGroupColor);
            gradientDrawable.setStroke(dp(1), iDisplayGroupColor);
            textView.setTextColor(ACCENT_ON);
        } else {
            int i2 = 16777215 & iDisplayGroupColor;
            gradientDrawable.setColor(855638016 | i2);
            gradientDrawable.setStroke(dp(1), i2 | (-1728053248));
            textView.setTextColor(iDisplayGroupColor);
        }
        textView.setBackground(gradientDrawable);
    }

    private void stylePillCount(TextView textView) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(dp(11));
        gradientDrawable.setColor(ACCENT_FILL);
        gradientDrawable.setStroke(dp(1), ACCENT_STROKE);
        textView.setBackground(gradientDrawable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyFilter() {
        String str;
        int i;
        int i2;
        this.filtered.clear();
        if (this.catalog == null) {
            this.adapter.notifyDataSetChanged();
            return;
        }
        String lowerCase = this.search.getText() == null ? "" : this.search.getText().toString().trim().toLowerCase(Locale.ROOT);
        if (!lowerCase.isEmpty() && isEmblemGroup(this.editingGroup) && (i2 = this.emblemSubTier) != 0 && !classHasMatch(i2, lowerCase)) {
            Iterator<Integer> it = this.emblemClassOrdinal.keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                int iIntValue = it.next().intValue();
                if (iIntValue != this.emblemSubTier && classHasMatch(iIntValue, lowerCase)) {
                    this.emblemSubTier = iIntValue;
                    installTierChips();
                    break;
                }
            }
        }
        for (int i3 : RecastTierPolicy.displayGroupTiers(this.editingGroup)) {
            if (!RecastTierPolicy.isEmblem(i3) || (i = this.emblemSubTier) == 0 || i3 == i) {
                for (RecastEquipmentCatalog.Entry entry : this.catalog.tier(i3)) {
                    if (lowerCase.isEmpty() || entry.searchKey.contains(lowerCase)) {
                        this.filtered.add(entry);
                    }
                }
            }
        }
        this.adapter.notifyDataSetChanged();
        TextView textView = this.resultCountText;
        StringBuilder sbAppend = new StringBuilder().append(this.filtered.size()).append(" 个");
        RecastEquipmentCatalog recastEquipmentCatalog = this.catalog;
        textView.setText(sbAppend.append((recastEquipmentCatalog == null || !recastEquipmentCatalog.isLive()) ? " · 进对局后更新" : " · 本局").toString());
        String strDisplayGroupFullName = RecastTierPolicy.displayGroupFullName(this.editingGroup);
        if (isEmblemGroup(this.editingGroup) && this.emblemSubTier != 0) {
            this.ruleText.setText(EquipmentIdMapper.CAT_EMBLEM + emblemClassLabel(this.emblemSubTier) + "：" + RecastTierPolicy.emblemOriginHint(this.emblemSubTier) + " · 只能用同一类的纹章锤出来，别的类锤不出");
        } else {
            this.ruleText.setText(strDisplayGroupFullName + "只能锤出同类" + strDisplayGroupFullName);
        }
        TextView textView2 = this.emptyText;
        if (lowerCase.isEmpty()) {
            str = "该品类没有可指定的装备";
        } else {
            str = "没有匹配的装备";
        }
        textView2.setText(str);
        updateButtons();
    }

    private LinkedHashMap<Integer, RecastEquipmentCatalog.Entry> selectionFor(int i) {
        LinkedHashMap<Integer, RecastEquipmentCatalog.Entry> linkedHashMap = this.selectedByTier.get(Integer.valueOf(i));
        if (linkedHashMap != null) {
            return linkedHashMap;
        }
        LinkedHashMap<Integer, RecastEquipmentCatalog.Entry> linkedHashMap2 = new LinkedHashMap<>();
        this.selectedByTier.put(Integer.valueOf(i), linkedHashMap2);
        return linkedHashMap2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String categoryLabel(int i) {
        int iDisplayGroupOf = RecastTierPolicy.displayGroupOf(i);
        if (iDisplayGroupOf >= 0 && RecastTierPolicy.displayGroupTiers(iDisplayGroupOf).length > 1) {
            return RecastTierPolicy.displayGroupFullName(iDisplayGroupOf);
        }
        return RecastTierPolicy.tierFullName(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RecastEquipmentCatalog.Entry currentTarget() {
        Iterator<LinkedHashMap<Integer, RecastEquipmentCatalog.Entry>> it = this.selectedByTier.values().iterator();
        while (it.hasNext()) {
            Iterator<RecastEquipmentCatalog.Entry> it2 = it.next().values().iterator();
            if (it2.hasNext()) {
                return it2.next();
            }
        }
        return null;
    }

    private int totalSelected() {
        Iterator<LinkedHashMap<Integer, RecastEquipmentCatalog.Entry>> it = this.selectedByTier.values().iterator();
        int size = 0;
        while (it.hasNext()) {
            size += it.next().size();
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleSelection(RecastEquipmentCatalog.Entry entry) {
        LinkedHashMap<Integer, RecastEquipmentCatalog.Entry> linkedHashMapSelectionFor = selectionFor(entry.tier);
        if (linkedHashMapSelectionFor.containsKey(Integer.valueOf(entry.id))) {
            linkedHashMapSelectionFor.remove(Integer.valueOf(entry.id));
            Dbg.i(TAG, "[指定重铸] 已取消目标：ID=" + entry.id + " 名称=" + entry.name + " 品类=" + RecastTierPolicy.tierFullName(entry.tier));
        } else {
            RecastEquipmentCatalog.Entry entryCurrentTarget = currentTarget();
            if (entryCurrentTarget != null) {
                Iterator<LinkedHashMap<Integer, RecastEquipmentCatalog.Entry>> it = this.selectedByTier.values().iterator();
                while (it.hasNext()) {
                    it.next().clear();
                }
                Dbg.i(TAG, "[指定重铸] 目标已替换：旧=" + entryCurrentTarget.id + " 新=" + entry.id);
            }
            linkedHashMapSelectionFor.put(Integer.valueOf(entry.id), entry);
            this.justToggledId = entry.id;
            Dbg.i(TAG, "[指定重铸] 已指定目标：ID=" + entry.id + " 名称=" + entry.name + " 品类=" + RecastTierPolicy.tierFullName(entry.tier));
        }
        this.adapter.notifyDataSetChanged();
        updateSelectionUi();
        persistSelection();
        renderStatus();
    }

    private void clearSelection() {
        if (totalSelected() == 0) {
            return;
        }
        Iterator<LinkedHashMap<Integer, RecastEquipmentCatalog.Entry>> it = this.selectedByTier.values().iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
        this.adapter.notifyDataSetChanged();
        updateSelectionUi();
        persistSelection();
        renderStatus();
    }

    private void persistSelection() {
        SharedPreferences sharedPreferences = this.prefs;
        if (sharedPreferences == null) {
            return;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        RecastEquipmentCatalog.Entry entryCurrentTarget = currentTarget();
        if (entryCurrentTarget != null) {
            editorEdit.putInt(KEY_TARGET, entryCurrentTarget.id);
        } else {
            editorEdit.remove(KEY_TARGET);
        }
        for (int i : RecastTierPolicy.selectableTiers()) {
            editorEdit.remove(KEY_TIER + i);
        }
        editorEdit.apply();
    }

    private void restoreSelectionFromPrefs() {
        int i;
        RecastEquipmentCatalog.Entry entryById;
        LinkedHashMap<Integer, RecastEquipmentCatalog.Entry> linkedHashMap;
        SharedPreferences sharedPreferences = this.prefs;
        if (sharedPreferences == null || this.catalog == null || (i = sharedPreferences.getInt(KEY_TARGET, 0)) <= 0 || (entryById = this.catalog.byId(i)) == null || (linkedHashMap = this.selectedByTier.get(Integer.valueOf(entryById.tier))) == null) {
            return;
        }
        linkedHashMap.clear();
        linkedHashMap.put(Integer.valueOf(entryById.id), entryById);
        int iDisplayGroupOf = RecastTierPolicy.displayGroupOf(entryById.tier);
        if (iDisplayGroupOf >= 0) {
            this.editingGroup = iDisplayGroupOf;
        }
        if (RecastTierPolicy.isEmblem(entryById.tier)) {
            this.emblemSubTier = entryById.tier;
        }
    }

    private void updateSelectionUi() {
        SpecifiedPredictionPreviewView specifiedPredictionPreviewView = this.predictionPreview;
        if (specifiedPredictionPreviewView != null) {
            specifiedPredictionPreviewView.refresh();
        }
        RecastEquipmentCatalog.Entry entryCurrentTarget = currentTarget();
        boolean z = entryCurrentTarget != null;
        this.pickCountText.setText(!z ? "未指定" : "已指定 1");
        this.selectedCountText.setText(z ? "已指定 1" : "未指定");
        TextView textView = this.selectedText;
        if (entryCurrentTarget == null) {
            textView.setText("未指定装备");
            this.selectedText.setTextColor(-3613458);
            this.selectedDescription.setText(buildEmptyStateText());
            this.selectedDescription.setVisibility(0);
            TextView textView2 = this.howToText;
            if (textView2 != null) {
                textView2.setVisibility(8);
            }
        } else {
            textView.setText(categoryLabel(entryCurrentTarget.tier) + "：" + entryCurrentTarget.name);
            this.selectedText.setTextColor(TEXT_PRIMARY);
            this.selectedDescription.setVisibility(8);
            TextView textView3 = this.howToText;
            if (textView3 != null) {
                textView3.setVisibility(8);
            }
        }
        rebuildTargetRows(entryCurrentTarget);
        updateButtons();
    }

    private CharSequence buildEmptyStateText() {
        return "还没有指定目标";
    }

    private void rebuildTargetRows(final RecastEquipmentCatalog.Entry entry) {
        this.targetsContainer.removeAllViews();
        if (entry == null) {
            return;
        }
        int iTierColor = RecastTierPolicy.tierColor(entry.tier);
        FrameLayout frameLayout = new FrameLayout(this.context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp(76), dp(76));
        layoutParams.gravity = 1;
        layoutParams.topMargin = dp(8);
        layoutParams.bottomMargin = dp(4);
        frameLayout.setLayoutParams(layoutParams);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(dp(14));
        gradientDrawable.setColor(SURFACE_FILL);
        gradientDrawable.setStroke(dp(2), (iTierColor & ViewCompat.MEASURED_SIZE_MASK) | (-872415232));
        frameLayout.setBackground(gradientDrawable);
        frameLayout.setContentDescription("目标：" + entry.name);
        ImageView imageView = new ImageView(this.context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        frameLayout.addView(imageView, new FrameLayout.LayoutParams(dp(56), dp(56), 17));
        loadIcon(imageView, entry, dp(56));
        TextView textView = new TextView(this.context);
        textView.setText("×");
        textView.setTextColor(-15665);
        textView.setTextSize(2, 13.0f);
        textView.setTypeface(Typeface.DEFAULT, 1);
        textView.setGravity(17);
        textView.setIncludeFontPadding(false);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(1);
        gradientDrawable2.setColor(-432400862);
        gradientDrawable2.setStroke(dp(1), -1711305306);
        textView.setBackground(gradientDrawable2);
        textView.setContentDescription("取消目标 " + entry.name);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1570x2e136972(entry, view);
            }
        });
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(dp(22), dp(22), 8388661);
        layoutParams2.setMargins(0, dp(2), dp(2), 0);
        frameLayout.addView(textView, layoutParams2);
        this.targetsContainer.addView(frameLayout);
    }

    /* renamed from: lambda$rebuildTargetRows$11$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1570x2e136972(RecastEquipmentCatalog.Entry entry, View view) {
        toggleSelection(entry);
    }

    private void start() {
        final RecastEquipmentCatalog.Entry entryCurrentTarget = currentTarget();
        if (entryCurrentTarget == null || this.manager == null) {
            return;
        }
        this.running = true;
        this.lastRunStatus = null;
        this.reminderPolicy.reset();
        renderStatus();
        this.manager.specifyRecastTarget(entryCurrentTarget.id, entryCurrentTarget.tier, this.strictSwitch.isChecked(), new SharedMemManager.ActionCallback() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda9
            @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
            public final void onTerminal(boolean z, String str) {
                this.f$0.m1571lambda$start$12$comandroidsupportSpecifiedRecastPanel(entryCurrentTarget, z, str);
            }
        });
    }

    /* renamed from: lambda$start$12$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1571lambda$start$12$comandroidsupportSpecifiedRecastPanel(RecastEquipmentCatalog.Entry entry, boolean z, String str) {
        if (this.disposed) {
            return;
        }
        if (z) {
            Dbg.i(TAG, "[指定重铸] 已下发目标 ID=" + entry.id + " 品类=" + entry.tier);
            renderStatus();
        } else {
            this.running = false;
            Dbg.i(TAG, "[指定重铸] 下发失败：" + str);
            setStatus("启动失败：" + describeFailure(str), -36205);
            updateButtons();
        }
    }

    private void stop() {
        resetRun();
        renderStatus();
        SharedMemManager sharedMemManager = this.manager;
        if (sharedMemManager == null) {
            return;
        }
        sharedMemManager.cancelSpecifiedRecast(new SharedMemManager.ActionCallback() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda16
            @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
            public final void onTerminal(boolean z, String str) {
                this.f$0.m1572lambda$stop$13$comandroidsupportSpecifiedRecastPanel(z, str);
            }
        });
    }

    /* renamed from: lambda$stop$13$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1572lambda$stop$13$comandroidsupportSpecifiedRecastPanel(boolean z, String str) {
        if (this.disposed || z) {
            return;
        }
        Dbg.i(TAG, "[指定重铸] 取消失败：" + str);
        this.running = true;
        setStatus("没停下来，请再点一次停止", -36205);
        updateButtons();
        toast("停止没送到 · 请再点一次「停止」，否则可能仍会出手");
    }

    private static String describeFailure(String str) {
        if (str == null) {
            return "未知原因";
        }
        str.hashCode();
        switch (str) {
            case "response_timeout":
                return "游戏端没回应，请再点一次";
            case "invalid_action_params":
            case "invalid_request":
                return "请求无效，请重新选一次装备";
            case "not_connected":
                return "未连接游戏";
            case "no_active_match_generation":
                return "需要先进入对局";
            case "invalid_recast_target":
                return "目标无效";
            case "busy":
                return "正忙，请稍后再点";
            case "feature_disabled":
                return "功能未开启";
            case "not_ready":
                return "还没准备好，请稍后再点";
            default:
                return "请再点一次";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRunStatus(RecastRunStatus recastRunStatus) {
        if (this.disposed || recastRunStatus == null) {
            return;
        }
        this.lastRunStatus = recastRunStatus;
        if (RecastRunStatus.PHASE_IDLE.equals(recastRunStatus.phase)) {
            resetRun();
            renderStatus();
            return;
        }
        if (recastRunStatus.isRunning() && !this.running) {
            this.running = true;
            Dbg.i(TAG, "[指定重铸] 跟随注入端进入运行态：目标=" + recastRunStatus.targetId);
        }
        if (recastRunStatus.isStopped()) {
            String strDescribeStopped = describeStopped(recastRunStatus);
            String strDescribeStoppedDetail = describeStoppedDetail(recastRunStatus, currentTarget());
            resetRun();
            setStatus(strDescribeStopped, -36205);
            toast(strDescribeStoppedDetail);
            updateButtons();
            return;
        }
        if (recastRunStatus.isSettled()) {
            resetRun();
            if (recastRunStatus.isHit()) {
                announceSuccess(recastRunStatus.resultId);
            } else if (RecastRunStatus.REASON_FIRE_TIMEOUT.equals(recastRunStatus.reason)) {
                setStatus("没发出去，可再试", -36205);
                toast("这次没发出去 · 重铸器没有消耗，可以直接再点一次开始");
            } else {
                setStatus("没中，重铸器已消耗", -36205);
                toast("这一锤没中 · 重铸器已消耗，再锤需要新的重铸器");
            }
            updateButtons();
            return;
        }
        renderStatus();
    }

    private void resetRun() {
        this.running = false;
        this.lastRunStatus = null;
        this.reminderPolicy.reset();
    }

    private void announceSuccess(int i) {
        RecastEquipmentCatalog.Entry entryById;
        RecastEquipmentCatalog recastEquipmentCatalog = this.catalog;
        String str = (recastEquipmentCatalog == null || (entryById = recastEquipmentCatalog.byId(i)) == null) ? null : entryById.name;
        if (str == null || str.isEmpty()) {
            RecastEquipmentCatalog.Entry entryCurrentTarget = currentTarget();
            str = entryCurrentTarget != null ? entryCurrentTarget.name : "指定装备";
        }
        setStatus("已锤出「" + str + "」", STATUS_OK);
        popStatus();
        if (!FloatingWindowService.celebrateRecastSuccess(str)) {
            playCue();
        }
        toast("重铸成功 · 已锤出「" + str + "」");
    }

    private void playCue() {
        Context context;
        if (this.disposed || (context = this.context) == null || this.cueSound == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(FloatingWindowPrefsKeys.PREF_NAME, 0);
        if (sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_HEX_CUE_ENABLED, true)) {
            this.cueSound.play(HexCueSound.volumeForSlider(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_HEX_CUE_VOLUME, 80)));
        }
    }

    private void toast(String str) {
        if (this.disposed || this.context == null || str == null || str.isEmpty()) {
            return;
        }
        FloatingWindowService.safeToast(this.context, str, 0);
    }

    private String sourceExamples(RecastEquipmentCatalog.Entry entry, int i) {
        if (this.catalog == null || entry == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        for (RecastEquipmentCatalog.Entry entry2 : this.catalog.tier(entry.tier)) {
            if (entry2.id != entry.id) {
                if (i2 > 0) {
                    sb.append(" / ");
                }
                sb.append(entry2.name);
                i2++;
                if (i2 >= i) {
                    break;
                }
            }
        }
        return sb.toString();
    }

    private String observedName(RecastRunStatus recastRunStatus) {
        if (recastRunStatus == null || recastRunStatus.observedId <= 0) {
            return "身上那件装备";
        }
        RecastEquipmentCatalog recastEquipmentCatalog = this.catalog;
        RecastEquipmentCatalog.Entry entryById = recastEquipmentCatalog == null ? null : recastEquipmentCatalog.byId(recastRunStatus.observedId);
        if (entryById == null) {
            return "身上那件装备";
        }
        return "「" + entryById.name + "」";
    }

    private String placeInstead(RecastEquipmentCatalog.Entry entry) {
        String strSourceExamples = sourceExamples(entry, 3);
        return strSourceExamples.isEmpty() ? "同类的" + categoryLabel(entry.tier) : strSourceExamples;
    }

    private String describeStopped(RecastRunStatus recastRunStatus) {
        String strRuntimeIssue = RecastRunStatus.runtimeIssue(recastRunStatus.reason);
        if (strRuntimeIssue != null) {
            return "已停止 · " + strRuntimeIssue;
        }
        if (RecastRunStatus.REASON_NO_RECASTER.equals(recastRunStatus.reason)) {
            return "已停止 · 装备区没有重铸器";
        }
        if (RecastRunStatus.REASON_TARGET_UNAVAILABLE.equals(recastRunStatus.reason)) {
            return "已停止 · 这一局没有这件装备";
        }
        return "已停止 · 备战席没有能锤出它的装备";
    }

    private String describeStoppedDetail(RecastRunStatus recastRunStatus, RecastEquipmentCatalog.Entry entry) {
        String strRuntimeIssue = RecastRunStatus.runtimeIssue(recastRunStatus.reason);
        if (strRuntimeIssue != null) {
            return "已停止 · " + strRuntimeIssue;
        }
        if (RecastRunStatus.REASON_NO_RECASTER.equals(recastRunStatus.reason)) {
            return "已停止 · 当前装备区没有重铸器";
        }
        if (RecastRunStatus.REASON_TARGET_UNAVAILABLE.equals(recastRunStatus.reason)) {
            return "已停止 · 这一局没有" + targetLabel(entry) + " · 不同赛季的装备不一样，请重新选一件";
        }
        if (RecastRunStatus.REASON_WRONG_TIER.equals(recastRunStatus.reason)) {
            String strClassifiedLabel = classifiedLabel(recastRunStatus.observedTier);
            String strClassifiedLabel2 = entry == null ? "" : classifiedLabel(entry.tier);
            return "已停止 · 当前备战席没有能重铸成指定目标的装备 · 你放的是" + (strClassifiedLabel.isEmpty() ? "" : strClassifiedLabel + "的") + observedName(recastRunStatus) + "，锤不出" + (strClassifiedLabel2.isEmpty() ? "" : strClassifiedLabel2 + "的") + targetLabel(entry) + "，请改放：" + placeInstead(entry);
        }
        if (RecastRunStatus.REASON_SOURCE_IS_TARGET.equals(recastRunStatus.reason)) {
            return "已停止 · 当前备战席没有能重铸成指定目标的装备 · 棋子身上放的就是" + targetLabel(entry) + "本身，锤不出它自己，请换成：" + placeInstead(entry);
        }
        return "已停止 · 当前备战席没有能重铸成指定目标的装备 · 请把" + placeInstead(entry) + "放到备战席的棋子身上";
    }

    private String targetLabel(RecastEquipmentCatalog.Entry entry) {
        return entry == null ? "指定的装备" : "「" + entry.name + "」";
    }

    private String describeRunning(RecastEquipmentCatalog.Entry entry) {
        RecastRunStatus recastRunStatus = this.lastRunStatus;
        if (recastRunStatus != null && RecastRunStatus.PHASE_BLOCKED.equals(recastRunStatus.phase)) {
            String strRuntimeIssue = RecastRunStatus.runtimeIssue(recastRunStatus.reason);
            if (strRuntimeIssue != null) {
                return strRuntimeIssue;
            }
            if (RecastRunStatus.REASON_NO_RECASTER.equals(recastRunStatus.reason)) {
                return "等待中：背包里还没有「装备重铸器」";
            }
            if (RecastRunStatus.REASON_TARGET_UNAVAILABLE.equals(recastRunStatus.reason)) {
                return "这一局没有「" + entry.name + "」，请换一件";
            }
            if (RecastRunStatus.REASON_WRONG_TIER.equals(recastRunStatus.reason)) {
                String strClassifiedLabel = classifiedLabel(recastRunStatus.observedTier);
                String strClassifiedLabel2 = classifiedLabel(entry.tier);
                if (strClassifiedLabel.isEmpty()) {
                    return observedName(recastRunStatus) + "锤不出" + strClassifiedLabel2 + "，按上面换一件";
                }
                return "你放的是" + strClassifiedLabel + "，锤不出" + strClassifiedLabel2 + "，按上面换一件";
            }
            if (RecastRunStatus.REASON_SOURCE_IS_TARGET.equals(recastRunStatus.reason)) {
                return "放的就是它本身，按上面换一件";
            }
            if (RecastRunStatus.REASON_NO_CANDIDATE.equals(recastRunStatus.reason)) {
                return "按上面那行放到备战席棋子身上";
            }
            if (RecastRunStatus.REASON_NEED_REPLACE.equals(recastRunStatus.reason)) {
                return "请把装备取下再放回一次，就能开始";
            }
            return "待命中，条件满足后会自动继续";
        }
        if (recastRunStatus != null && RecastRunStatus.PHASE_FIRED.equals(recastRunStatus.phase)) {
            return "已出手，正在确认结果…";
        }
        if (recastRunStatus == null || recastRunStatus.candidates <= 0) {
            return "正在等待锤出「" + entry.name + "」";
        }
        return recastRunStatus.timingRange.isEmpty() ? "等待中" : recastRunStatus.timingRange;
    }

    private void renderStatus() {
        if (this.disposed) {
            return;
        }
        RecastEquipmentCatalog.Entry entryCurrentTarget = currentTarget();
        if (this.catalog == null) {
            setStatus("正在载入装备目录", -3613458);
        } else if (entryCurrentTarget == null) {
            setStatus("先点选 1 件想锤出来的装备", -3613458);
        } else if (this.running) {
            setStatus(describeRunning(entryCurrentTarget), STATUS_BUSY);
            maybeRemindRunning(entryCurrentTarget);
        } else {
            setStatus("已就绪 · 目标是" + classifiedLabel(entryCurrentTarget.tier) + "，点开始", -10556417);
        }
        updateButtons();
    }

    private void maybeRemindRunning(RecastEquipmentCatalog.Entry entry) {
        String strRemindText;
        RecastRunStatus recastRunStatus = this.lastRunStatus;
        if (recastRunStatus == null || !RecastReminderPolicy.actionable(recastRunStatus.phase, recastRunStatus.reason) || (strRemindText = remindText(entry)) == null) {
            return;
        }
        if (this.reminderPolicy.claim(recastRunStatus.phase, recastRunStatus.reason, SystemClock.elapsedRealtime())) {
            toast(strRemindText);
        }
    }

    private String remindText(RecastEquipmentCatalog.Entry entry) {
        RecastRunStatus recastRunStatus = this.lastRunStatus;
        if (recastRunStatus != null && RecastRunStatus.PHASE_BLOCKED.equals(recastRunStatus.phase)) {
            String strRuntimeIssue = RecastRunStatus.runtimeIssue(recastRunStatus.reason);
            if (strRuntimeIssue != null) {
                return strRuntimeIssue;
            }
            if (RecastRunStatus.REASON_NO_RECASTER.equals(recastRunStatus.reason)) {
                return "重铸待命中：背包里还没有重铸器";
            }
            if (RecastRunStatus.REASON_TARGET_UNAVAILABLE.equals(recastRunStatus.reason)) {
                return "这一局没有「" + entry.name + "」· 不同赛季的装备不一样，请重新选一件";
            }
            if (RecastRunStatus.REASON_WRONG_TIER.equals(recastRunStatus.reason)) {
                return "放的这件锤不出「" + entry.name + "」· 请改放：" + placeInstead(entry);
            }
            if (RecastRunStatus.REASON_SOURCE_IS_TARGET.equals(recastRunStatus.reason)) {
                return "放的就是「" + entry.name + "」本身 · 锤不出它自己，请换成：" + placeInstead(entry);
            }
            if (RecastRunStatus.REASON_NO_CANDIDATE.equals(recastRunStatus.reason)) {
                return "重铸待命中：把" + placeInstead(entry) + "放到备战席的棋子身上";
            }
            if (RecastRunStatus.REASON_NEED_REPLACE.equals(recastRunStatus.reason)) {
                return "重铸待命中：把装备取下再放回一次即可开始";
            }
            return "重铸待命中，条件满足后会自动继续";
        }
        if (recastRunStatus == null || !RecastRunStatus.PHASE_FIRED.equals(recastRunStatus.phase)) {
            return "正在为你等待「" + entry.name + "」，命中会自动出手";
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void updateButtons() {
        String str;
        Switch r0 = this.strictSwitch;
        if (r0 != null) {
            r0.setEnabled(!this.running);
        }
        boolean z = currentTarget() != null ? 1 : 0;
        boolean z2 = (z == 0 || this.running) ? false : true;
        this.startButton.setEnabled(z2);
        OverlayActionButtonStyle.apply(this.startButton, OverlayActionButtonStyle.Kind.GO, z2, dp(11), dp(2));
        Button button = this.startButton;
        if (this.running) {
            str = "重铸中…";
        } else {
            str = z != 0 ? "开始重铸" : "请先选装备";
        }
        button.setText(str);
        boolean z3 = this.running;
        this.stopButton.setEnabled(z3);
        OverlayActionButtonStyle.apply(this.stopButton, OverlayActionButtonStyle.Kind.STOP, z3, dp(11), dp(2));
        int i = (z2 ? 4 : 0) | z | (this.running ? 2 : 0);
        if (i != this.lastLoggedButtonState) {
            this.lastLoggedButtonState = i;
            Dbg.i(TAG, "[指定重铸] 按钮：有目标=" + z + " 运行中=" + this.running + " 可点=" + z2);
        }
        TextView textView = this.clearButton;
        if (textView != null) {
            textView.setEnabled(z);
            this.clearButton.setAlpha(z != 0 ? 1.0f : 0.5f);
        }
    }

    private void setStatus(String str, int i) {
        this.statusText.setText(str);
        this.statusText.setTextColor(i);
        if (this.statusDot == null) {
            return;
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(i);
        gradientDrawable.setStroke(dp(2), (i & ViewCompat.MEASURED_SIZE_MASK) | 1073741824);
        this.statusDot.setBackground(gradientDrawable);
        if (this.running) {
            startPulse();
        } else {
            stopPulse();
        }
    }

    private void startPulse() {
        View view = this.statusDot;
        if (view == null || this.pulse != null) {
            return;
        }
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat((Property<?, Float>) View.ALPHA, 1.0f, 0.35f), PropertyValuesHolder.ofFloat((Property<?, Float>) View.SCALE_X, 1.0f, 1.35f), PropertyValuesHolder.ofFloat((Property<?, Float>) View.SCALE_Y, 1.0f, 1.35f));
        this.pulse = objectAnimatorOfPropertyValuesHolder;
        objectAnimatorOfPropertyValuesHolder.setDuration(820L);
        this.pulse.setRepeatCount(-1);
        this.pulse.setRepeatMode(2);
        this.pulse.setInterpolator(new AccelerateDecelerateInterpolator());
        this.pulse.start();
    }

    private void stopPulse() {
        ObjectAnimator objectAnimator = this.pulse;
        if (objectAnimator == null) {
            return;
        }
        objectAnimator.cancel();
        this.pulse = null;
        View view = this.statusDot;
        if (view == null) {
            return;
        }
        view.setAlpha(1.0f);
        this.statusDot.setScaleX(1.0f);
        this.statusDot.setScaleY(1.0f);
    }

    private void popStatus() {
        TextView textView = this.statusText;
        if (textView == null) {
            return;
        }
        textView.animate().cancel();
        this.statusText.setScaleX(1.0f);
        this.statusText.setScaleY(1.0f);
        this.statusText.animate().scaleX(1.08f).scaleY(1.08f).setDuration(140L).withEndAction(new Runnable() { // from class: com.android.support.SpecifiedRecastPanel$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1569lambda$popStatus$14$comandroidsupportSpecifiedRecastPanel();
            }
        }).start();
    }

    /* renamed from: lambda$popStatus$14$com-android-support-SpecifiedRecastPanel, reason: not valid java name */
    /* synthetic */ void m1569lambda$popStatus$14$comandroidsupportSpecifiedRecastPanel() {
        TextView textView;
        if (this.disposed || (textView = this.statusText) == null) {
            return;
        }
        textView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(180L).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadIcon(ImageView imageView, RecastEquipmentCatalog.Entry entry, int i) {
        RequestOptions requestOptionsOverride;
        RequestBuilder<Drawable> requestBuilderApply;
        EquipmentImageResolver.Resolution resolution = this.iconCache.get(Integer.valueOf(entry.id));
        imageView.setContentDescription(entry.name);
        if (resolution == null) {
            imageView.setImageResource(R.drawable.bg_equipment_placeholder);
            imageView.setTag(R.id.recast_icon_bound_key, null);
            return;
        }
        String str = entry.id + "@" + i;
        if (!str.equals(imageView.getTag(R.id.recast_icon_bound_key)) || imageView.getDrawable() == null) {
            imageView.setTag(R.id.recast_icon_bound_key, str);
            RequestOptions requestOptions = this.iconOptions;
            if (requestOptions == null) {
                requestOptionsOverride = new RequestOptions().placeholder(R.drawable.bg_equipment_placeholder).error(R.drawable.bg_equipment_placeholder).override(i, i);
            } else {
                requestOptionsOverride = requestOptions.mo1721clone().override(i, i);
            }
            try {
                if (resolution.localUri != null) {
                    RequestBuilder<Drawable> requestBuilderApply2 = Glide.with(imageView).load(resolution.localUri).apply((BaseRequestOptions<?>) requestOptionsOverride);
                    if (resolution.remoteUrl == null) {
                        requestBuilderApply = Glide.with(imageView).load(Integer.valueOf(R.drawable.bg_equipment_placeholder));
                    } else {
                        requestBuilderApply = Glide.with(imageView).load(resolution.remoteUrl).apply((BaseRequestOptions<?>) requestOptionsOverride);
                    }
                    requestBuilderApply2.error(requestBuilderApply).into(imageView);
                    return;
                }
                if (resolution.remoteUrl != null) {
                    Glide.with(imageView).load(resolution.remoteUrl).apply((BaseRequestOptions<?>) requestOptionsOverride).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.bg_equipment_placeholder);
                }
            } catch (Throwable unused) {
                imageView.setImageResource(R.drawable.bg_equipment_placeholder);
            }
        }
    }

    void dispose() {
        if (this.disposed) {
            return;
        }
        this.disposed = true;
        SharedMemManager sharedMemManager = this.manager;
        if (sharedMemManager != null) {
            sharedMemManager.setRecastRunStatusListener(null);
            this.manager.setRecastLivePoolListener(null);
        }
        SpecifiedPredictionPreviewView specifiedPredictionPreviewView = this.predictionPreview;
        if (specifiedPredictionPreviewView != null) {
            specifiedPredictionPreviewView.dispose();
        }
        stopPulse();
        TextView textView = this.statusText;
        if (textView != null) {
            textView.animate().cancel();
        }
        HexCueSound hexCueSound = this.cueSound;
        if (hexCueSound != null) {
            hexCueSound.close();
            this.cueSound = null;
        }
        this.loader.shutdownNow();
        if (this.grid == null) {
            return;
        }
        int i = 0;
        while (true) {
            int childCount = this.grid.getChildCount();
            GridView gridView = this.grid;
            if (i < childCount) {
                View childAt = gridView.getChildAt(i);
                ImageView imageView = childAt == null ? null : (ImageView) childAt.findViewWithTag("recast_icon");
                if (imageView != null) {
                    Glide.with(imageView).clear(imageView);
                }
                i++;
            } else {
                gridView.setAdapter((ListAdapter) null);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int dp(int i) {
        if (this.cachedDensity <= 0.0f) {
            float f = this.context.getResources().getDisplayMetrics().density;
            this.cachedDensity = f;
            if (f <= 0.0f) {
                this.cachedDensity = 1.0f;
            }
        }
        return Math.round(i * this.cachedDensity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class RecastAdapter extends BaseAdapter {
        private RecastAdapter() {
        }

        private int applyCellMetrics(Holder holder) {
            if (SpecifiedRecastPanel.this.cellIconPx <= 0) {
                SpecifiedRecastPanel specifiedRecastPanel = SpecifiedRecastPanel.this;
                specifiedRecastPanel.refreshCellMetricsCache(specifiedRecastPanel.context.getResources());
            }
            SpecifiedRecastPanel.setViewHeight(holder.card, SpecifiedRecastPanel.this.cellHeightPx);
            SpecifiedRecastPanel.setViewHeight(holder.name, SpecifiedRecastPanel.this.cellNamePx);
            ViewGroup.LayoutParams layoutParams = holder.icon.getLayoutParams();
            if (layoutParams != null && (layoutParams.width != SpecifiedRecastPanel.this.cellIconPx || layoutParams.height != SpecifiedRecastPanel.this.cellIconPx)) {
                layoutParams.width = SpecifiedRecastPanel.this.cellIconPx;
                layoutParams.height = SpecifiedRecastPanel.this.cellIconPx;
                holder.icon.setLayoutParams(layoutParams);
            }
            return SpecifiedRecastPanel.this.cellIconPx;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return SpecifiedRecastPanel.this.filtered.size();
        }

        @Override // android.widget.Adapter
        public RecastEquipmentCatalog.Entry getItem(int i) {
            return (RecastEquipmentCatalog.Entry) SpecifiedRecastPanel.this.filtered.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return getItem(i).id;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) throws Resources.NotFoundException {
            Holder holder;
            if (!(view instanceof FrameLayout) || view.getTag() == null) {
                FrameLayout frameLayout = new FrameLayout(SpecifiedRecastPanel.this.context);
                frameLayout.setLayoutParams(new AbsListView.LayoutParams(-1, SpecifiedRecastPanel.this.context.getResources().getDimensionPixelSize(R.dimen.xf_hexpick_cell_h)));
                LinearLayout linearLayout = new LinearLayout(SpecifiedRecastPanel.this.context);
                linearLayout.setOrientation(1);
                linearLayout.setGravity(1);
                linearLayout.setPadding(SpecifiedRecastPanel.this.dp(4), SpecifiedRecastPanel.this.dp(7), SpecifiedRecastPanel.this.dp(4), SpecifiedRecastPanel.this.dp(5));
                frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(-1, -1));
                ImageView imageView = new ImageView(SpecifiedRecastPanel.this.context);
                imageView.setTag("recast_icon");
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int dimensionPixelSize = SpecifiedRecastPanel.this.context.getResources().getDimensionPixelSize(R.dimen.xf_hexpick_icon);
                linearLayout.addView(imageView, new LinearLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize));
                TextView textView = new TextView(SpecifiedRecastPanel.this.context);
                textView.setGravity(17);
                textView.setTextSize(2, 10.0f);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTypeface(Typeface.DEFAULT, 1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, SpecifiedRecastPanel.this.context.getResources().getDimensionPixelSize(R.dimen.xf_hexpick_name_h));
                layoutParams.topMargin = SpecifiedRecastPanel.this.dp(2);
                linearLayout.addView(textView, layoutParams);
                TextView textView2 = new TextView(SpecifiedRecastPanel.this.context);
                textView2.setTextSize(2, 11.0f);
                textView2.setGravity(17);
                textView2.setTypeface(Typeface.DEFAULT, 1);
                FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(SpecifiedRecastPanel.this.dp(19), SpecifiedRecastPanel.this.dp(19), 8388661);
                layoutParams2.topMargin = SpecifiedRecastPanel.this.dp(4);
                layoutParams2.rightMargin = SpecifiedRecastPanel.this.dp(4);
                frameLayout.addView(textView2, layoutParams2);
                Holder holder2 = new Holder(frameLayout, imageView, textView, textView2, SpecifiedRecastPanel.this.badgeBackground(-10556417));
                frameLayout.setTag(holder2);
                frameLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedRecastPanel$RecastAdapter$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.m1573x50fdbd7d(view2);
                    }
                });
                frameLayout.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.support.SpecifiedRecastPanel$RecastAdapter$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view2) {
                        return this.f$0.m1574x5234105c(view2);
                    }
                });
                holder = holder2;
                view = frameLayout;
            } else {
                holder = (Holder) view.getTag();
            }
            int iApplyCellMetrics = applyCellMetrics(holder);
            RecastEquipmentCatalog.Entry item = getItem(i);
            holder.entry = item;
            RecastEquipmentCatalog.Entry entryCurrentTarget = SpecifiedRecastPanel.this.currentTarget();
            int i2 = (entryCurrentTarget == null || entryCurrentTarget.id != item.id) ? 0 : 1;
            holder.name.setText(item.name);
            holder.name.setTextColor(RecastTierPolicy.tierColor(item.tier));
            holder.check.setText("✓");
            holder.check.setTextColor(SpecifiedRecastPanel.ACCENT_ON);
            holder.check.setBackground(holder.checkBadge);
            holder.check.setVisibility(i2 != 0 ? 0 : 8);
            if (i2 != 0 && item.id == SpecifiedRecastPanel.this.justToggledId) {
                SpecifiedRecastPanel.this.justToggledId = 0;
                holder.check.setScaleX(0.4f);
                holder.check.setScaleY(0.4f);
                holder.check.setAlpha(0.0f);
                holder.check.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(150L).start();
            } else {
                holder.check.animate().cancel();
                holder.check.setScaleX(1.0f);
                holder.check.setScaleY(1.0f);
                holder.check.setAlpha(1.0f);
            }
            SpecifiedRecastPanel.this.loadIcon(holder.icon, item, iApplyCellMetrics);
            if (holder.appliedCellState == i2 && holder.appliedTier == item.tier) {
                return view;
            }
            holder.appliedCellState = i2;
            holder.appliedTier = item.tier;
            holder.card.setBackground(cellBackground(i2, item.tier));
            return view;
        }

        /* renamed from: lambda$getView$0$com-android-support-SpecifiedRecastPanel$RecastAdapter, reason: not valid java name */
        /* synthetic */ void m1573x50fdbd7d(View view) {
            Holder holder = (Holder) view.getTag();
            if (holder == null || holder.entry == null) {
                return;
            }
            SpecifiedRecastPanel.this.toggleSelection(holder.entry);
        }

        /* renamed from: lambda$getView$1$com-android-support-SpecifiedRecastPanel$RecastAdapter, reason: not valid java name */
        /* synthetic */ boolean m1574x5234105c(View view) {
            String str;
            Holder holder = (Holder) view.getTag();
            if (holder != null && holder.entry != null) {
                RecastEquipmentCatalog.Entry entryCurrentTarget = SpecifiedRecastPanel.this.currentTarget();
                boolean z = (entryCurrentTarget == null || entryCurrentTarget.tier == holder.entry.tier || RecastTierPolicy.displayGroupOf(holder.entry.tier) != RecastTierPolicy.displayGroupOf(entryCurrentTarget.tier)) ? false : true;
                Context context = SpecifiedRecastPanel.this.context;
                StringBuilder sbAppend = new StringBuilder().append(holder.entry.name).append("\nID ").append(holder.entry.id).append(" · ").append(SpecifiedRecastPanel.categoryLabel(holder.entry.tier));
                if (z) {
                    str = "\n与「" + entryCurrentTarget.name + "」不是同一类，互相锤不出来";
                } else {
                    str = "";
                }
                FloatingWindowService.safeToast(context, sbAppend.append(str).toString(), 1);
            }
            return true;
        }

        private GradientDrawable cellBackground(int i, int i2) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(0);
            gradientDrawable.setCornerRadius(SpecifiedRecastPanel.this.dp(14));
            if (i == 1) {
                int iTierColor = RecastTierPolicy.tierColor(i2);
                gradientDrawable.setColor((16777215 & iTierColor) | 1291845632);
                gradientDrawable.setStroke(SpecifiedRecastPanel.this.dp(2), iTierColor);
                return gradientDrawable;
            }
            gradientDrawable.setColor(SpecifiedRecastPanel.SURFACE_FILL);
            gradientDrawable.setStroke(SpecifiedRecastPanel.this.dp(1), SpecifiedRecastPanel.SURFACE_STROKE);
            return gradientDrawable;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public GradientDrawable badgeBackground(int i) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(i);
        return gradientDrawable;
    }

    private static final class Holder {
        int appliedCellState = -1;
        int appliedTier = -1;
        final FrameLayout card;
        final TextView check;
        final GradientDrawable checkBadge;
        RecastEquipmentCatalog.Entry entry;
        final ImageView icon;
        final TextView name;

        Holder(FrameLayout frameLayout, ImageView imageView, TextView textView, TextView textView2, GradientDrawable gradientDrawable) {
            this.card = frameLayout;
            this.icon = imageView;
            this.name = textView;
            this.check = textView2;
            this.checkBadge = gradientDrawable;
        }
    }
}
