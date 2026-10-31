package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import com.android.support.CompanionPairLayout;
import com.android.support.OverlayActionButtonStyle;
import com.android.support.SharedMemManager;
import com.android.support.SpecifiedHexCatalog;
import com.android.support.SpecifiedPredictionPreview;
import com.android.support.SpecifiedPredictionPreviewView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
final class SpecifiedHexPanel {
    private static final int ACCENT = -10556417;
    private static final int ACCENT_FILL = 643755007;
    private static final int ACCENT_ON = -16506581;
    private static final int ACCENT_STROKE = 1432284159;
    private static final long AUTO_ARM_DEBOUNCE_MS = 400;
    private static final long AUTO_ARM_TICK_MS = 1500;
    private static final String KEY_MODE_STAGE = "mode_stage_";
    private static final String KEY_STAGE = "stage_";
    private static final int MAX_TARGETS = 12;
    private static final String PREFS = "jcc_specified_hex";
    private static final long SEARCH_DEBOUNCE_MS = 160;
    private static final int STAGE_COUNT = 3;
    private static final int STATUS_BUSY = -11930;
    private static final int STATUS_FAIL = -36205;
    private static final int STATUS_IDLE = -7362374;
    private static final int STATUS_OK = -10685786;
    private static final long SUBMIT_RETRY_MS = 2000;
    private static final int SURFACE_FILL = 268435455;
    private static final int SURFACE_STROKE = 536870911;
    private static final String TAG = "HexPredict";
    private static final int TEXT_MUTED = -7628371;
    private static final int TEXT_PRIMARY = -1378561;
    private HexAdapter adapter;
    private int appliedColumnWidthPx;
    private String armedForMatch;
    private final Runnable autoArmTask;
    private final Runnable autoArmTicker;
    private float cachedDensity;
    private Button cancelButton;
    private SpecifiedHexCatalog catalog;
    private int cellHeightPx;
    private int cellIconPx;
    private int cellNamePx;
    private TextView clearButton;
    private final Context context;
    private HexCueSound cueSound;
    private int cuedCompletedMask;
    private int currentModeIndex;
    private volatile boolean disposed;
    private int editingLevel;
    private int editingStage;
    private TextView emptyText;
    private final List<SpecifiedHexCatalog.Entry> filtered;
    private GridView grid;
    private String hexQueryGeneration;
    private boolean hexQueryInFlight;
    private int justToggledId;
    private int lastObservedHexRevision;
    private long lastRestoredSessionGeneration;
    private long lastRestoredTargetRevision;
    private String lastRuntimeMode;
    private long lastStageLevelQueryMs;
    private String lastStatusText;
    private String lastSubmitTrace;
    private String lastSubmittedKey;
    private final List<Integer> levelChipLevels;
    private View levelChipRow;
    private final List<TextView> levelChipViews;
    private LinearLayout levelChips;
    private String levelsQueriedForMatch;
    private boolean listRefreshScheduled;
    private final Runnable listRefreshTask;
    private int liveDrawsPerStage;
    private int[] liveRollablePool;
    private final HashSet<Integer> liveRollablePoolIds;
    private int liveRollablePoolTotalWeight;
    private int[] liveRollablePoolWeights;
    private int[] liveStageLevels;
    private final ExecutorService loader;
    private final Handler mainHandler;
    private final SharedMemManager manager;
    private String manuallyCancelledGeneration;
    private final SpecifiedHexMatchLifecycle matchLifecycle;
    private final List<View> modeTabUnderlines;
    private final List<TextView> modeTabViews;
    private LinearLayout modeTabs;
    private SpecifiedHexOfferTable offerTable;
    private TextView pickCountText;
    private View pickerRoot;
    private boolean poolAppliesToList;
    private SpecifiedPredictionPreviewView predictionPreview;
    private SharedPreferences prefs;
    private int quantizedGridHeightPx;
    private int quantizedGridWidthPx;
    private TextView resultCountText;
    private EditText search;
    private final Runnable searchFilterTask;
    private LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> selected;
    private final LinkedHashMap<String, List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>>> selectedByMode;
    private List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>> selectedByStage;
    private TextView selectedCountText;
    private TextView selectedDescription;
    private TextView selectedText;
    private int specifiedHexCompletedMask;
    private int specifiedHexHitMask;
    private int specifiedHexUnavailableMask;
    private final List<TextView> stageChipViews;
    private LinearLayout stageChips;
    private View statusDot;
    private TextView statusText;
    private Switch strictSwitch;
    private final Runnable submitRetryTask;
    private LinearLayout targetsContainer;
    private final List<SpecifiedHexCatalog.Mode> visibleModes;

    /* JADX INFO: Access modifiers changed from: private */
    public static int levelColor(int i) {
        if (i == 1) {
            return -4732967;
        }
        if (i == 2) {
            return STATUS_BUSY;
        }
        if (i != 3) {
            return i != 4 ? -1 : -9967367;
        }
        return -685057;
    }

    SpecifiedHexPanel(Context context, View view, View view2, SharedMemManager sharedMemManager) {
        Handler handler = new Handler(Looper.getMainLooper());
        this.mainHandler = handler;
        this.loader = Executors.newSingleThreadExecutor();
        this.filtered = new ArrayList();
        this.selectedByMode = new LinkedHashMap<>();
        this.selectedByStage = newStageSelections();
        this.editingStage = 0;
        this.editingLevel = 1;
        this.matchLifecycle = new SpecifiedHexMatchLifecycle();
        this.appliedColumnWidthPx = -1;
        this.quantizedGridWidthPx = -1;
        this.quantizedGridHeightPx = -1;
        this.modeTabViews = new ArrayList();
        this.modeTabUnderlines = new ArrayList();
        this.levelChipViews = new ArrayList();
        this.levelChipLevels = new ArrayList();
        this.stageChipViews = new ArrayList();
        this.currentModeIndex = 0;
        this.visibleModes = new ArrayList();
        this.liveStageLevels = SpecifiedHexOfferPolicy.copyStageLevels(null);
        this.liveRollablePool = new int[0];
        this.liveRollablePoolWeights = new int[0];
        this.liveRollablePoolTotalWeight = 0;
        this.liveDrawsPerStage = 0;
        this.poolAppliesToList = false;
        this.levelsQueriedForMatch = "";
        this.lastStageLevelQueryMs = -1L;
        this.hexQueryGeneration = "";
        this.justToggledId = 0;
        this.lastStatusText = "";
        this.cuedCompletedMask = 0;
        this.specifiedHexHitMask = 0;
        this.armedForMatch = "";
        this.manuallyCancelledGeneration = "";
        this.lastSubmittedKey = "";
        this.lastSubmitTrace = "";
        this.lastRuntimeMode = "";
        this.lastObservedHexRevision = Integer.MIN_VALUE;
        Runnable runnable = new Runnable() { // from class: com.android.support.SpecifiedHexPanel.1
            @Override // java.lang.Runnable
            public void run() {
                if (SpecifiedHexPanel.this.disposed) {
                    return;
                }
                SpecifiedHexPanel.this.syncLiveOfferContext();
                SpecifiedHexPanel.this.maybeAutoArmForMatch();
                SpecifiedHexPanel.this.mainHandler.postDelayed(this, SpecifiedHexPanel.AUTO_ARM_TICK_MS);
            }
        };
        this.autoArmTicker = runnable;
        this.searchFilterTask = new Runnable() { // from class: com.android.support.SpecifiedHexPanel.4
            @Override // java.lang.Runnable
            public void run() {
                if (SpecifiedHexPanel.this.disposed) {
                    return;
                }
                SpecifiedHexPanel.this.applyFilter();
            }
        };
        this.listRefreshTask = new Runnable() { // from class: com.android.support.SpecifiedHexPanel.5
            @Override // java.lang.Runnable
            public void run() {
                SpecifiedHexPanel.this.listRefreshScheduled = false;
                if (SpecifiedHexPanel.this.disposed) {
                    return;
                }
                SpecifiedHexPanel.this.applyFilter();
                SpecifiedHexPanel.this.updateSelectionUi();
            }
        };
        this.liveRollablePoolIds = new HashSet<>();
        this.autoArmTask = new Runnable() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.submitSelection();
            }
        };
        this.submitRetryTask = new Runnable() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.retrySubmitAfterRejection();
            }
        };
        this.context = context;
        this.manager = sharedMemManager;
        this.prefs = context.getSharedPreferences(PREFS, 0);
        this.cueSound = new HexCueSound(context);
        this.selected = this.selectedByStage.get(0);
        bindViews(view, view2);
        handler.postDelayed(runnable, AUTO_ARM_TICK_MS);
    }

    private static List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>> newStageSelections() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 3; i++) {
            arrayList.add(new LinkedHashMap());
        }
        return arrayList;
    }

    private List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>> selectionsForMode(String str) {
        if (str == null) {
            str = "";
        }
        List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>> list = this.selectedByMode.get(str);
        if (list != null) {
            return list;
        }
        List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>> listNewStageSelections = newStageSelections();
        this.selectedByMode.put(str, listNewStageSelections);
        return listNewStageSelections;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String displayedModeId() {
        int i;
        if (this.visibleModes.isEmpty() || (i = this.currentModeIndex) < 0 || i >= this.visibleModes.size()) {
            return "18";
        }
        return this.visibleModes.get(this.currentModeIndex).id;
    }

    private void bindDisplayedModeSelection() {
        List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>> listSelectionsForMode = selectionsForMode(displayedModeId());
        this.selectedByStage = listSelectionsForMode;
        this.selected = listSelectionsForMode.get(this.editingStage);
    }

    private static String selectionKey(String str, int i) {
        return KEY_MODE_STAGE + str + "_" + i;
    }

    private void persistSelection() {
        SharedPreferences sharedPreferences = this.prefs;
        if (sharedPreferences == null) {
            return;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        for (Map.Entry<String, List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>>> entry : this.selectedByMode.entrySet()) {
            for (int i = 0; i < 3; i++) {
                StringBuilder sb = new StringBuilder();
                for (Integer num : entry.getValue().get(i).keySet()) {
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(num);
                }
                editorEdit.putString(selectionKey(entry.getKey(), i), sb.toString());
            }
        }
        editorEdit.apply();
    }

    private void restoreSelectionFromPrefs() {
        SpecifiedHexCatalog specifiedHexCatalog;
        if (this.prefs == null || (specifiedHexCatalog = this.catalog) == null) {
            return;
        }
        boolean zContains = false;
        boolean z = false;
        for (SpecifiedHexCatalog.Mode mode : specifiedHexCatalog.modes) {
            List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>> listSelectionsForMode = selectionsForMode(mode.id);
            for (int i = 0; i < 3; i++) {
                String strSelectionKey = selectionKey(mode.id, i);
                zContains |= this.prefs.contains(strSelectionKey);
                String string = this.prefs.getString(strSelectionKey, "");
                if (!string.isEmpty() && restoreIds(string, mode.id, listSelectionsForMode.get(i))) {
                    z = true;
                }
            }
        }
        if (!zContains) {
            List<LinkedHashMap<Integer, SpecifiedHexCatalog.Entry>> listSelectionsForMode2 = selectionsForMode("18");
            for (int i2 = 0; i2 < 3; i2++) {
                if (restoreIds(this.prefs.getString(KEY_STAGE + i2, ""), "18", listSelectionsForMode2.get(i2))) {
                    z = true;
                }
            }
            if (z) {
                persistSelection();
            }
        }
        bindDisplayedModeSelection();
        if (z) {
            this.adapter.notifyDataSetChanged();
            updateSelectionUi();
            refreshStageChipStyles();
            updateButtons(this.manager.getSpecifiedHexStatus());
            this.armedForMatch = "";
            this.lastSubmittedKey = "";
        }
    }

    private boolean restoreIds(String str, String str2, LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMap) throws NumberFormatException {
        if (str == null || str.isEmpty()) {
            return false;
        }
        boolean z = false;
        for (String str3 : str.split(",")) {
            try {
                int i = Integer.parseInt(str3.trim());
                SpecifiedHexCatalog.Entry entryFindStoredEntry = findStoredEntry(i, str2);
                if (entryFindStoredEntry != null && SpecifiedHexSelectionState.canAddCurrentQualityTarget(countSelectionAtLevel(linkedHashMap, entryFindStoredEntry.level), 12)) {
                    linkedHashMap.put(Integer.valueOf(i), entryFindStoredEntry);
                    z = true;
                }
            } catch (NumberFormatException unused) {
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeAutoArmForMatch() {
        String currentMatchGeneration = this.manager.getCurrentMatchGeneration();
        if (runtimeContextReady(currentMatchGeneration)) {
            int iRuntimeStageIndex = runtimeStageIndex();
            if (SpecifiedHexOfferPolicy.hasStageLevel(this.liveStageLevels, iRuntimeStageIndex)) {
                String strArmContextKey = SpecifiedHexSelectionState.armContextKey(currentMatchGeneration, iRuntimeStageIndex, SpecifiedHexOfferPolicy.stageLevel(this.liveStageLevels, iRuntimeStageIndex));
                if (SpecifiedHexSelectionState.shouldAutoArmContext(currentMatchGeneration, strArmContextKey, this.armedForMatch, this.manuallyCancelledGeneration)) {
                    this.armedForMatch = strArmContextKey;
                    submitSelection();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00b7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void syncLiveOfferContext() {
        /*
            r7 = this;
            com.android.support.SpecifiedHexCatalog r0 = r7.catalog
            if (r0 != 0) goto L6
            goto Lc5
        L6:
            com.android.support.SharedMemManager r0 = r7.manager
            java.lang.String r0 = r0.getCurrentMatchGeneration()
            r1 = 1
            if (r0 == 0) goto L19
            int r2 = r0.length()
            r3 = 22
            if (r2 != r3) goto L19
            r2 = r1
            goto L1a
        L19:
            r2 = 0
        L1a:
            java.lang.String r3 = ""
            if (r2 == 0) goto L20
            r4 = r0
            goto L21
        L20:
            r4 = r3
        L21:
            boolean r4 = r7.observeMatchGeneration(r4)
            boolean r5 = r7.lockVisibleModeIfNeeded()
            if (r5 == 0) goto L2c
            r4 = r1
        L2c:
            if (r2 == 0) goto L33
            java.lang.String r5 = r7.runtimeModeId()
            goto L34
        L33:
            r5 = r3
        L34:
            java.lang.String r6 = r7.lastRuntimeMode
            boolean r6 = r5.equals(r6)
            if (r6 != 0) goto L42
            r7.lastRuntimeMode = r5
            r7.armedForMatch = r3
            r7.lastSubmittedKey = r3
        L42:
            r5 = 0
            if (r2 != 0) goto L68
            r7.hexQueryGeneration = r3
            int[] r0 = r7.liveStageLevels
            boolean r0 = com.android.support.SpecifiedHexOfferPolicy.hasAnyStageLevel(r0)
            if (r0 != 0) goto L57
            java.lang.String r0 = r7.levelsQueriedForMatch
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L60
        L57:
            r7.levelsQueriedForMatch = r3
            boolean r0 = r7.applyLiveStageLevels(r5)
            if (r0 == 0) goto L60
            r4 = r1
        L60:
            boolean r0 = r7.applyLiveRollablePool(r5)
            if (r0 == 0) goto Lbb
            r4 = r1
            goto Lbb
        L68:
            com.android.support.SharedMemManager r2 = r7.manager
            int r2 = r2.getCachedHexRevision()
            int r3 = r7.lastObservedHexRevision
            if (r2 == r3) goto Lb8
            r7.lastObservedHexRevision = r2
            com.android.support.SharedMemManager r2 = r7.manager
            int[] r2 = r2.getCachedHexLevels()
            boolean r3 = com.android.support.SpecifiedHexOfferPolicy.hasAnyStageLevel(r2)
            if (r3 == 0) goto L8a
            boolean r2 = r7.applyLiveStageLevels(r2)
            if (r2 == 0) goto L99
            r7.levelsQueriedForMatch = r0
        L88:
            r4 = r1
            goto L99
        L8a:
            java.lang.String r2 = r7.levelsQueriedForMatch
            boolean r2 = r0.equals(r2)
            if (r2 != 0) goto L99
            boolean r2 = r7.applyLiveStageLevels(r5)
            if (r2 == 0) goto L99
            goto L88
        L99:
            com.android.support.SharedMemManager r2 = r7.manager
            int[] r2 = r2.getCachedHexLivePool()
            com.android.support.SharedMemManager r3 = r7.manager
            int[] r3 = r3.getCachedHexLivePoolWeights()
            com.android.support.SharedMemManager r5 = r7.manager
            int r5 = r5.getCachedHexLivePoolTotalWeight()
            com.android.support.SharedMemManager r6 = r7.manager
            int r6 = r6.getCachedHexDrawsPerStage()
            boolean r2 = r7.applyLiveRollablePool(r2, r3, r5, r6)
            if (r2 == 0) goto Lb8
            r4 = r1
        Lb8:
            r7.queryStageLevelsForMatch(r0)
        Lbb:
            boolean r0 = r7.syncEditingLevelForMatch()
            if (r0 == 0) goto Lc2
            goto Lc3
        Lc2:
            r1 = r4
        Lc3:
            if (r1 != 0) goto Lc6
        Lc5:
            return
        Lc6:
            r7.refreshLevelChips()
            r7.refreshStageChipStyles()
            r7.scheduleListRefresh()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.SpecifiedHexPanel.syncLiveOfferContext():void");
    }

    private boolean observeMatchGeneration(String str) {
        if (!this.matchLifecycle.observeGeneration(str)) {
            return false;
        }
        this.lastObservedHexRevision = Integer.MIN_VALUE;
        this.mainHandler.removeCallbacks(this.autoArmTask);
        this.armedForMatch = "";
        this.manuallyCancelledGeneration = SpecifiedHexSelectionState.retainManualCancellation(this.manuallyCancelledGeneration, str);
        this.lastSubmittedKey = "";
        this.specifiedHexCompletedMask = 0;
        this.specifiedHexHitMask = 0;
        this.specifiedHexUnavailableMask = 0;
        this.levelsQueriedForMatch = "";
        this.hexQueryGeneration = "";
        this.hexQueryInFlight = false;
        this.lastStageLevelQueryMs = -1L;
        this.editingStage = 0;
        bindDisplayedModeSelection();
        applyLiveStageLevels(null);
        return true;
    }

    private void queryStageLevelsForMatch(final String str) {
        if (str == null || str.length() != 22) {
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (HexStageRefreshPolicy.shouldQuery(this.hexQueryInFlight && str.equals(this.hexQueryGeneration), jElapsedRealtime, this.lastStageLevelQueryMs)) {
            this.lastStageLevelQueryMs = jElapsedRealtime;
            this.hexQueryInFlight = true;
            this.hexQueryGeneration = str;
            this.manager.requestHexLevels(new SharedMemManager.HexLevelsCallback() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda7
                @Override // com.android.support.SharedMemManager.HexLevelsCallback
                public final void onResult(int[] iArr) {
                    this.f$0.m1518x9ece4797(str, iArr);
                }
            });
        }
    }

    /* renamed from: lambda$queryStageLevelsForMatch$0$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1518x9ece4797(String str, int[] iArr) {
        if (str.equals(this.hexQueryGeneration)) {
            this.hexQueryInFlight = false;
            this.hexQueryGeneration = "";
        }
        if (!this.disposed && SpecifiedHexSelectionState.isCurrentResponse(str, this.manager.getCurrentMatchGeneration()) && SpecifiedHexOfferPolicy.hasAnyStageLevel(iArr)) {
            this.levelsQueriedForMatch = str;
            boolean zApplyLiveStageLevels = applyLiveStageLevels(iArr);
            if (lockVisibleModeIfNeeded()) {
                zApplyLiveStageLevels = true;
            }
            if (syncEditingLevelForMatch() ? true : zApplyLiveStageLevels) {
                refreshLevelChips();
                refreshStageChipStyles();
                applyFilter();
                updateSelectionUi();
            }
        }
    }

    private void bindViews(View view, View view2) {
        LinearLayout linearLayout;
        this.pickerRoot = view2;
        this.search = (EditText) view2.findViewById(R.id.etSpecifiedHexSearch);
        this.modeTabs = (LinearLayout) view2.findViewById(R.id.llSpecifiedHexModeTabs);
        this.levelChips = (LinearLayout) view2.findViewById(R.id.llSpecifiedHexLevelChips);
        this.levelChipRow = view2.findViewById(R.id.hexPickLevelRow);
        this.stageChips = (LinearLayout) view2.findViewById(R.id.llSpecifiedHexStageChips);
        this.grid = (GridView) view2.findViewById(R.id.gridSpecifiedHex);
        this.resultCountText = (TextView) view2.findViewById(R.id.tvSpecifiedHexResultCount);
        this.emptyText = (TextView) view2.findViewById(R.id.tvSpecifiedHexEmpty);
        this.pickCountText = (TextView) view2.findViewById(R.id.tvSpecifiedHexPickCount);
        this.clearButton = (TextView) view2.findViewById(R.id.btnSpecifiedHexClear);
        this.selectedText = (TextView) view.findViewById(R.id.tvSpecifiedHexSelected);
        this.selectedDescription = (TextView) view.findViewById(R.id.tvSpecifiedHexSelectedDescription);
        this.selectedCountText = (TextView) view.findViewById(R.id.tvSpecifiedHexSelectedCount);
        this.targetsContainer = (LinearLayout) view.findViewById(R.id.llSpecifiedHexTargets);
        this.statusText = (TextView) view.findViewById(R.id.tvSpecifiedHexStatus);
        this.statusDot = view.findViewById(R.id.viewSpecifiedHexStatusDot);
        this.cancelButton = (Button) view.findViewById(R.id.btnSpecifiedHexCancel);
        Switch r3 = (Switch) view.findViewById(R.id.switchSpecifiedHexStrict);
        this.strictSwitch = r3;
        if (r3 != null) {
            r3.setChecked(this.prefs.getBoolean("strict", false));
            this.strictSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda16
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    this.f$0.m1509lambda$bindViews$1$comandroidsupportSpecifiedHexPanel(compoundButton, z);
                }
            });
        }
        if (this.search == null || this.modeTabs == null || this.levelChips == null || this.grid == null || this.resultCountText == null || this.emptyText == null || this.pickCountText == null || this.clearButton == null || this.selectedText == null || this.selectedDescription == null || this.selectedCountText == null || (linearLayout = this.targetsContainer) == null || this.statusText == null || this.cancelButton == null) {
            throw new IllegalStateException("specified Hex page is incomplete");
        }
        SpecifiedPredictionPreviewView specifiedPredictionPreviewViewInstallInline = SpecifiedPredictionPreviewView.installInline((FrameLayout) linearLayout.getParent().getParent(), SpecifiedPredictionPreview.KIND_HEX, new SpecifiedPredictionPreviewView.Presenter() { // from class: com.android.support.SpecifiedHexPanel.2
            private SpecifiedHexCatalog.Entry entry(SpecifiedPredictionPreview.Item item) {
                if (SpecifiedHexPanel.this.catalog == null) {
                    return null;
                }
                return SpecifiedHexPanel.this.findStoredEntry(item.id, SpecifiedHexPanel.this.runtimeModeId());
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public String name(SpecifiedPredictionPreview.Item item) {
                SpecifiedHexCatalog.Entry entry = entry(item);
                return entry == null ? "海克斯 " + item.id : entry.name;
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public String detail(SpecifiedPredictionPreview.Item item) {
                return "第 " + (item.slot + 1) + " 个选项";
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public void bindIcon(ImageView imageView, SpecifiedPredictionPreview.Item item, int i) {
                SpecifiedHexCatalog.Entry entry = entry(item);
                if (entry == null) {
                    imageView.setImageResource(android.R.drawable.ic_menu_help);
                } else {
                    Glide.with(imageView).load("file:///android_asset/" + entry.icon).override(i, i).dontAnimate().into(imageView);
                }
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public boolean canSelect(SpecifiedPredictionPreview.Item item) {
                return entry(item) != null && SpecifiedHexPanel.this.runtimeStageIndex() >= 0 && SpecifiedHexPanel.this.displayedModeId().equals(SpecifiedHexPanel.this.runtimeModeId());
            }

            @Override // com.android.support.SpecifiedPredictionPreviewView.Presenter
            public void select(SpecifiedPredictionPreview.Item item) {
                SpecifiedHexCatalog.Entry entry = entry(item);
                if (entry == null || !canSelect(item)) {
                    return;
                }
                SpecifiedHexPanel specifiedHexPanel = SpecifiedHexPanel.this;
                specifiedHexPanel.editingStage = specifiedHexPanel.runtimeStageIndex();
                SpecifiedHexPanel specifiedHexPanel2 = SpecifiedHexPanel.this;
                specifiedHexPanel2.selected = (LinkedHashMap) specifiedHexPanel2.selectedByStage.get(SpecifiedHexPanel.this.editingStage);
                SpecifiedHexPanel.this.editingLevel = entry.level;
                SpecifiedHexPanel.this.refreshLevelChips();
                SpecifiedHexPanel.this.refreshStageChipStyles();
                SpecifiedHexPanel.this.applyFilter();
                SpecifiedHexPanel.this.toggleSelection(entry);
            }
        });
        this.predictionPreview = specifiedPredictionPreviewViewInstallInline;
        specifiedPredictionPreviewViewInstallInline.bind(this.manager);
        HexAdapter hexAdapter = new HexAdapter();
        this.adapter = hexAdapter;
        this.grid.setAdapter((ListAdapter) hexAdapter);
        this.grid.setEmptyView(this.emptyText);
        this.search.addTextChangedListener(new TextWatcher() { // from class: com.android.support.SpecifiedHexPanel.3
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                SpecifiedHexPanel.this.mainHandler.removeCallbacks(SpecifiedHexPanel.this.searchFilterTask);
                SpecifiedHexPanel.this.mainHandler.postDelayed(SpecifiedHexPanel.this.searchFilterTask, SpecifiedHexPanel.SEARCH_DEBOUNCE_MS);
            }
        });
        this.clearButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda17
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                this.f$0.m1510lambda$bindViews$2$comandroidsupportSpecifiedHexPanel(view3);
            }
        });
        stylePillCount(this.pickCountText);
        stylePillCount(this.selectedCountText);
        this.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda18
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                this.f$0.m1511lambda$bindViews$3$comandroidsupportSpecifiedHexPanel(view3);
            }
        });
        refreshLevelChips();
        installStageChips();
        updateButtons(SpecifiedHexStatus.IDLE);
        updateSelectionUi();
        this.manager.setSpecifiedHexStatusListener(new SharedMemManager.SpecifiedHexStatusListener() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda19
            @Override // com.android.support.SharedMemManager.SpecifiedHexStatusListener
            public final void onSpecifiedHexStatus(SpecifiedHexStatus specifiedHexStatus) {
                this.f$0.renderStatus(specifiedHexStatus);
            }
        });
        this.manager.setHexLevelsListener(new SharedMemManager.HexLevelsListener() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda20
            @Override // com.android.support.SharedMemManager.HexLevelsListener
            public final void onHexLevels(int[] iArr) {
                this.f$0.onLiveHexLevels(iArr);
            }
        });
        renderStatus(this.manager.getSpecifiedHexStatus());
        applyPickerDensity();
        loadCatalog();
    }

    /* renamed from: lambda$bindViews$1$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1509lambda$bindViews$1$comandroidsupportSpecifiedHexPanel(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("strict", z).apply();
        this.lastSubmittedKey = "";
        scheduleAutoArm();
    }

    /* renamed from: lambda$bindViews$2$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1510lambda$bindViews$2$comandroidsupportSpecifiedHexPanel(View view) {
        clearSelection();
    }

    /* renamed from: lambda$bindViews$3$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1511lambda$bindViews$3$comandroidsupportSpecifiedHexPanel(View view) {
        cancelSpecifiedHex();
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
            setTopMargin(this.pickerRoot.findViewById(R.id.hexPickModeRow), dimensionPixelSize3);
            setTopMargin(this.pickerRoot.findViewById(R.id.hexPickLevelRow), dimensionPixelSize3);
            TextView textView = this.resultCountText;
            if (textView != null) {
                setViewHeight(textView, resources.getDimensionPixelSize(R.dimen.xf_hexpick_count_h));
                setTopMargin(this.resultCountText, dimensionPixelSize3);
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
            this.grid.post(new Runnable() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda12
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
        HexAdapter hexAdapter = this.adapter;
        if (hexAdapter != null) {
            hexAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshCellMetricsCache(Resources resources) {
        this.cellHeightPx = resources.getDimensionPixelSize(R.dimen.xf_hexpick_cell_h);
        this.cellIconPx = resources.getDimensionPixelSize(R.dimen.xf_hexpick_icon);
        this.cellNamePx = resources.getDimensionPixelSize(R.dimen.xf_hexpick_name_h);
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

    private void setStatus(String str, int i) {
        if (str == null) {
            str = "";
        }
        boolean zEquals = str.equals(this.lastStatusText);
        this.lastStatusText = str;
        this.statusText.setTextColor(i);
        TextView textView = this.statusText;
        if (!zEquals) {
            textView.animate().cancel();
            this.statusText.setText(str);
            this.statusText.setAlpha(0.0f);
            this.statusText.setTranslationY(dp(5));
            this.statusText.animate().alpha(1.0f).translationY(0.0f).setDuration(200L).setInterpolator(new DecelerateInterpolator()).start();
        } else {
            textView.setText(str);
        }
        if (this.statusDot == null) {
            return;
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(i);
        gradientDrawable.setStroke(dp(2), (i & ViewCompat.MEASURED_SIZE_MASK) | 1073741824);
        this.statusDot.setBackground(gradientDrawable);
        if (zEquals) {
            return;
        }
        this.statusDot.animate().cancel();
        this.statusDot.setScaleX(0.4f);
        this.statusDot.setScaleY(0.4f);
        this.statusDot.animate().scaleX(1.0f).scaleY(1.0f).setDuration(320L).setInterpolator(new OvershootInterpolator(2.2f)).start();
    }

    private void loadCatalog() {
        setStatus("正在载入海克斯目录", STATUS_IDLE);
        this.resultCountText.setText("正在载入目录");
        this.loader.execute(new Runnable() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1517lambda$loadCatalog$6$comandroidsupportSpecifiedHexPanel();
            }
        });
    }

    /* renamed from: lambda$loadCatalog$6$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1517lambda$loadCatalog$6$comandroidsupportSpecifiedHexPanel() {
        try {
            final SpecifiedHexCatalog specifiedHexCatalogLoad = SpecifiedHexCatalog.load(this.context);
            final SpecifiedHexOfferTable specifiedHexOfferTableLoad = SpecifiedHexOfferTable.load(this.context);
            this.mainHandler.post(new Runnable() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1515lambda$loadCatalog$4$comandroidsupportSpecifiedHexPanel(specifiedHexCatalogLoad, specifiedHexOfferTableLoad);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "[指定海克斯] 海克斯目录加载失败", e);
            this.mainHandler.post(new Runnable() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1516lambda$loadCatalog$5$comandroidsupportSpecifiedHexPanel();
                }
            });
        }
    }

    /* renamed from: lambda$loadCatalog$4$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1515lambda$loadCatalog$4$comandroidsupportSpecifiedHexPanel(SpecifiedHexCatalog specifiedHexCatalog, SpecifiedHexOfferTable specifiedHexOfferTable) {
        if (this.disposed) {
            return;
        }
        this.catalog = specifiedHexCatalog;
        this.offerTable = specifiedHexOfferTable;
        applyLiveStageLevels(this.manager.getCachedHexLevels());
        lockVisibleModeIfNeeded();
        syncEditingLevelForMatch();
        restoreSelectionFromPrefs();
        refreshLevelChips();
        applyFilter();
        renderStatus(this.manager.getSpecifiedHexStatus());
        syncLiveOfferContext();
    }

    /* renamed from: lambda$loadCatalog$5$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1516lambda$loadCatalog$5$comandroidsupportSpecifiedHexPanel() {
        if (this.disposed) {
            return;
        }
        setStatus("海克斯目录校验失败", -36205);
        this.resultCountText.setText("目录不可用");
        this.emptyText.setText("海克斯目录校验失败");
    }

    private void installModeTabs() {
        this.modeTabs.removeAllViews();
        this.modeTabViews.clear();
        this.modeTabUnderlines.clear();
        if (this.catalog == null) {
            return;
        }
        if (this.visibleModes.isEmpty()) {
            refreshVisibleModes();
        }
        for (final int i = 0; i < this.visibleModes.size(); i++) {
            LinearLayout linearLayout = new LinearLayout(this.context);
            linearLayout.setOrientation(1);
            linearLayout.setGravity(1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            if (i > 0) {
                layoutParams.leftMargin = dp(16);
            }
            linearLayout.setLayoutParams(layoutParams);
            TextView textView = new TextView(this.context);
            textView.setText(this.visibleModes.get(i).name);
            textView.setTextSize(2, 13.0f);
            textView.setTypeface(Typeface.DEFAULT, 1);
            textView.setGravity(17);
            textView.setPadding(dp(2), dp(3), dp(2), dp(5));
            linearLayout.addView(textView, new LinearLayout.LayoutParams(-2, -2));
            View view = new View(this.context);
            view.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(2)));
            linearLayout.addView(view);
            linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.m1513lambda$installModeTabs$7$comandroidsupportSpecifiedHexPanel(i, view2);
                }
            });
            this.modeTabs.addView(linearLayout);
            this.modeTabViews.add(textView);
            this.modeTabUnderlines.add(view);
        }
        if (this.currentModeIndex >= this.modeTabViews.size()) {
            this.currentModeIndex = 0;
        }
        refreshTabStyles();
    }

    /* renamed from: lambda$installModeTabs$7$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1513lambda$installModeTabs$7$comandroidsupportSpecifiedHexPanel(int i, View view) {
        if (this.currentModeIndex == i) {
            return;
        }
        this.currentModeIndex = i;
        bindDisplayedModeSelection();
        refreshTabStyles();
        refreshStageChipStyles();
        applyFilter();
        updateSelectionUi();
        updateButtons(this.manager.getSpecifiedHexStatus());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshLevelChips() {
        LinearLayout linearLayout = this.levelChips;
        if (linearLayout == null) {
            return;
        }
        linearLayout.removeAllViews();
        this.levelChipViews.clear();
        this.levelChipLevels.clear();
        View view = this.levelChipRow;
        if (view != null) {
            view.setVisibility(0);
        }
        CharSequence[] charSequenceArr = {"银色", "金色", "彩色"};
        int iStageLevel = SpecifiedHexOfferPolicy.stageLevel(this.liveStageLevels, this.editingStage);
        boolean z = this.manager.isMatchActive() && iStageLevel > 0;
        final int i = z ? iStageLevel : 1;
        int i2 = z ? iStageLevel : 3;
        while (i <= i2) {
            TextView textView = new TextView(this.context);
            textView.setText(charSequenceArr[i - 1]);
            textView.setTextSize(2, 12.0f);
            textView.setTypeface(Typeface.DEFAULT, 1);
            textView.setGravity(17);
            textView.setPadding(dp(12), dp(5), dp(12), dp(5));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            if (!this.levelChipViews.isEmpty()) {
                layoutParams.leftMargin = dp(6);
            }
            textView.setLayoutParams(layoutParams);
            if (!z) {
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.m1520lambda$refreshLevelChips$8$comandroidsupportSpecifiedHexPanel(i, view2);
                    }
                });
            }
            styleChip(textView, this.editingLevel == i);
            this.levelChips.addView(textView);
            this.levelChipViews.add(textView);
            this.levelChipLevels.add(Integer.valueOf(i));
            i++;
        }
    }

    /* renamed from: lambda$refreshLevelChips$8$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1520lambda$refreshLevelChips$8$comandroidsupportSpecifiedHexPanel(int i, View view) {
        if (this.editingLevel == i) {
            return;
        }
        this.editingLevel = i;
        refreshLevelChipStyles();
        applyFilter();
        updateSelectionUi();
        updateButtons(this.manager.getSpecifiedHexStatus());
    }

    private void refreshLevelChipStyles() {
        for (int i = 0; i < this.levelChipViews.size(); i++) {
            styleChip(this.levelChipViews.get(i), this.editingLevel == this.levelChipLevels.get(i).intValue());
        }
    }

    private void installStageChips() {
        LinearLayout linearLayout = this.stageChips;
        if (linearLayout == null) {
            return;
        }
        linearLayout.removeAllViews();
        this.stageChipViews.clear();
        for (final int i = 0; i < 3; i++) {
            TextView textView = new TextView(this.context);
            textView.setTextSize(2, 12.0f);
            textView.setTypeface(Typeface.DEFAULT, 1);
            textView.setGravity(17);
            textView.setPadding(dp(12), dp(5), dp(12), dp(5));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            if (i > 0) {
                layoutParams.leftMargin = dp(6);
            }
            textView.setLayoutParams(layoutParams);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda10
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1514lambda$installStageChips$9$comandroidsupportSpecifiedHexPanel(i, view);
                }
            });
            this.stageChips.addView(textView);
            this.stageChipViews.add(textView);
        }
        refreshStageChipStyles();
    }

    /* renamed from: lambda$installStageChips$9$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1514lambda$installStageChips$9$comandroidsupportSpecifiedHexPanel(int i, View view) {
        if (this.editingStage == i) {
            return;
        }
        this.editingStage = i;
        this.selected = this.selectedByStage.get(i);
        syncEditingLevelForMatch();
        refreshLevelChips();
        refreshStageChipStyles();
        applyFilter();
        updateSelectionUi();
        updateButtons(this.manager.getSpecifiedHexStatus());
        setStatus(selectionTextForStage(this.editingStage), -10556417);
        scheduleAutoArm();
    }

    private static String stageName(int i) {
        if (i == 0) {
            return "一阶段";
        }
        if (i == 1) {
            return "二阶段";
        }
        if (i == 2) {
            return "三阶段";
        }
        return "阶段" + (i + 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshStageChipStyles() {
        int i = 0;
        while (i < this.stageChipViews.size()) {
            TextView textView = this.stageChipViews.get(i);
            String strStageName = stageName(i);
            if (displayedModeId().equals(runtimeModeId()) && currentLiveStageIndex() == i) {
                strStageName = strStageName + " •";
            }
            textView.setText(strStageName);
            styleChip(textView, i == this.editingStage);
            i++;
        }
    }

    private void refreshTabStyles() {
        int i = 0;
        while (i < this.modeTabViews.size()) {
            boolean z = i == this.currentModeIndex;
            this.modeTabViews.get(i).setTextColor(z ? TEXT_PRIMARY : TEXT_MUTED);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(dp(2));
            gradientDrawable.setColor(z ? -10556417 : 0);
            this.modeTabUnderlines.get(i).setBackground(gradientDrawable);
            i++;
        }
    }

    private void styleChip(TextView textView, boolean z) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(dp(13));
        if (z) {
            gradientDrawable.setColor(-10556417);
            gradientDrawable.setStroke(dp(1), -10556417);
            textView.setTextColor(ACCENT_ON);
        } else {
            gradientDrawable.setColor(SURFACE_FILL);
            gradientDrawable.setStroke(dp(1), SURFACE_STROKE);
            textView.setTextColor(TEXT_MUTED);
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

    private void scheduleListRefresh() {
        if (this.disposed || this.listRefreshScheduled) {
            return;
        }
        this.listRefreshScheduled = true;
        this.mainHandler.post(this.listRefreshTask);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyFilter() {
        String str;
        SpecifiedHexCatalog specifiedHexCatalog = this.catalog;
        if (specifiedHexCatalog == null || specifiedHexCatalog.modes.isEmpty()) {
            return;
        }
        if (this.visibleModes.isEmpty()) {
            refreshVisibleModes();
        }
        if (this.visibleModes.isEmpty()) {
            this.filtered.clear();
            this.adapter.notifyDataSetChanged();
            this.resultCountText.setText("当前赛季没有可指定的海克斯");
            this.emptyText.setText("当前赛季没有可指定的海克斯");
            return;
        }
        int i = this.currentModeIndex;
        if (i < 0 || i >= this.visibleModes.size()) {
            this.currentModeIndex = 0;
        }
        String str2 = this.visibleModes.get(this.currentModeIndex).id;
        String str3 = this.visibleModes.get(this.currentModeIndex).name;
        String strOfficialSeason = officialSeason();
        String lowerCase = this.search.getText() == null ? "" : this.search.getText().toString().trim().toLowerCase(Locale.ROOT);
        this.filtered.clear();
        for (SpecifiedHexCatalog.Entry entry : this.catalog.entries) {
            if (SpecifiedHexOfferPolicy.isOfferedForDisplay(entry, this.offerTable, strOfficialSeason, str2, this.editingStage, this.editingLevel) && (lowerCase.isEmpty() || entry.searchKey.contains(lowerCase))) {
                this.filtered.add(entry);
            }
        }
        int size = this.filtered.size();
        boolean zLivePoolAppliesToEditingContext = livePoolAppliesToEditingContext();
        this.poolAppliesToList = zLivePoolAppliesToEditingContext;
        if (zLivePoolAppliesToEditingContext) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (SpecifiedHexCatalog.Entry entry2 : this.filtered) {
                if (rollableNow(entry2.id)) {
                    arrayList.add(entry2);
                } else {
                    arrayList2.add(entry2);
                }
            }
            this.filtered.clear();
            this.filtered.addAll(arrayList);
            this.filtered.addAll(arrayList2);
            size = arrayList.size();
        }
        this.adapter.notifyDataSetChanged();
        String str4 = zLivePoolAppliesToEditingContext ? "（本阶段可出 " + size + " 个）" : "";
        if (SpecifiedHexOfferPolicy.isRuntimeCatalog(strOfficialSeason, str2) && !zLivePoolAppliesToEditingContext) {
            str4 = "（赛季预选，以局内可出池为准）";
        }
        this.resultCountText.setText(str3 + " · " + stageName(this.editingStage) + " · " + SpecifiedHexCatalog.levelName(this.editingLevel) + " " + this.filtered.size() + " 个" + str4);
        TextView textView = this.emptyText;
        if (lowerCase.isEmpty()) {
            str = "该赛季、品质和阶段没有可指定的海克斯";
        } else {
            str = "没有匹配的海克斯";
        }
        textView.setText(str);
        updateButtons(this.manager.getSpecifiedHexStatus());
    }

    private int internalSeason() {
        Context context = this.context;
        if (context instanceof FloatingWindowService) {
            return ((FloatingWindowService) context).currentSeasonIndex();
        }
        return 5;
    }

    private boolean runtimeContextReady(String str) {
        if (str != null && str.length() == 22) {
            Context context = this.context;
            if ((context instanceof FloatingWindowService) && !((FloatingWindowService) context).hasDetectedCurrentSeasonFor(str)) {
                return false;
            }
            String strRuntimeModeId = runtimeModeId();
            SpecifiedHexCatalog.Mode modeModeById = modeById(strRuntimeModeId);
            String str2 = modeModeById == null ? "19" : modeModeById.season;
            if ((modeModeById != null && SpecifiedHexOfferPolicy.isRuntimeCatalog(str2, strRuntimeModeId)) || SpecifiedHexOfferPolicy.hasAuthoritativeOfferTable(this.offerTable, str2, strRuntimeModeId)) {
                return true;
            }
        }
        return false;
    }

    private String runtimeContextPendingText(String str) {
        Context context = this.context;
        if ((context instanceof FloatingWindowService) && !((FloatingWindowService) context).hasDetectedCurrentSeasonFor(str)) {
            return "等待识别本局赛季";
        }
        if (this.offerTable == null) {
            return "海克斯阶段表不可用，已暂停自动指定";
        }
        return "当前模式阶段表尚未校验，已暂停自动指定";
    }

    private String officialSeason() {
        SpecifiedHexCatalog.Mode modeModeById = modeById(displayedModeId());
        if (modeModeById == null) {
            return "19";
        }
        return modeModeById.season;
    }

    private void refreshVisibleModes() {
        String strDisplayedModeId = displayedModeId();
        this.visibleModes.clear();
        if (this.catalog == null) {
            return;
        }
        boolean zIsMatchActive = this.manager.isMatchActive();
        String strRuntimeModeId = runtimeModeId();
        if (zIsMatchActive) {
            String strRuntimeSeason = runtimeSeason();
            for (SpecifiedHexCatalog.Mode mode : this.catalog.modes) {
                if (SpecifiedHexOfferPolicy.modeVisibleForMatch(mode, strRuntimeSeason, strRuntimeModeId)) {
                    this.visibleModes.add(mode);
                }
            }
        } else {
            this.visibleModes.addAll(this.catalog.modes);
        }
        if (zIsMatchActive) {
            strDisplayedModeId = strRuntimeModeId;
        }
        if (modeById(strDisplayedModeId) == null) {
            strDisplayedModeId = "18";
        }
        int i = 0;
        this.currentModeIndex = 0;
        while (true) {
            if (i >= this.visibleModes.size()) {
                break;
            }
            if (strDisplayedModeId.equals(this.visibleModes.get(i).id)) {
                this.currentModeIndex = i;
                break;
            }
            i++;
        }
        bindDisplayedModeSelection();
    }

    private boolean lockVisibleModeIfNeeded() {
        if (this.catalog == null || visibleModesMatchCurrentContext()) {
            return false;
        }
        refreshVisibleModes();
        installModeTabs();
        return true;
    }

    private boolean visibleModesMatchCurrentContext() {
        if (!this.manager.isMatchActive()) {
            if (this.visibleModes.size() != this.catalog.modes.size()) {
                return false;
            }
            for (int i = 0; i < this.catalog.modes.size(); i++) {
                if (!this.catalog.modes.get(i).id.equals(this.visibleModes.get(i).id)) {
                    return false;
                }
            }
            return true;
        }
        String strRuntimeModeId = runtimeModeId();
        String strRuntimeSeason = runtimeSeason();
        int i2 = 0;
        for (SpecifiedHexCatalog.Mode mode : this.catalog.modes) {
            if (SpecifiedHexOfferPolicy.modeVisibleForMatch(mode, strRuntimeSeason, strRuntimeModeId)) {
                if (i2 >= this.visibleModes.size() || !this.visibleModes.get(i2).id.equals(mode.id)) {
                    return false;
                }
                i2++;
            }
        }
        return i2 == this.visibleModes.size();
    }

    private SpecifiedHexCatalog.Mode modeById(String str) {
        SpecifiedHexCatalog specifiedHexCatalog = this.catalog;
        if (specifiedHexCatalog != null && str != null) {
            for (SpecifiedHexCatalog.Mode mode : specifiedHexCatalog.modes) {
                if (str.equals(mode.id)) {
                    return mode;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String runtimeModeId() {
        return SpecifiedHexOfferPolicy.preferredMode(internalSeason());
    }

    private String runtimeSeason() {
        SpecifiedHexCatalog.Mode modeModeById = modeById(runtimeModeId());
        if (modeModeById == null) {
            return "19";
        }
        return modeModeById.season;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int runtimeStageIndex() {
        return SpecifiedHexSelectionState.runtimeSubmitStage(currentLiveStageIndex());
    }

    private int currentLiveStageIndex() {
        return this.matchLifecycle.stageIndexFor(this.manager.getCurrentMatchGeneration());
    }

    private boolean syncEditingLevelForMatch() {
        int iResolvedPickerLevel = SpecifiedHexOfferPolicy.resolvedPickerLevel(this.manager.isMatchActive(), this.liveStageLevels, this.editingStage, this.editingLevel);
        if (iResolvedPickerLevel == this.editingLevel) {
            return false;
        }
        this.editingLevel = iResolvedPickerLevel;
        return true;
    }

    private boolean livePoolAppliesToEditingContext() {
        int iRuntimeStageIndex;
        return SpecifiedHexOfferPolicy.hasLivePool(this.liveRollablePool) && this.manager.isMatchActive() && this.editingStage == (iRuntimeStageIndex = runtimeStageIndex()) && SpecifiedHexOfferPolicy.hasStageLevel(this.liveStageLevels, iRuntimeStageIndex) && this.editingLevel == SpecifiedHexOfferPolicy.stageLevel(this.liveStageLevels, iRuntimeStageIndex);
    }

    private boolean applyLiveRollablePool(int[] iArr) {
        return applyLiveRollablePool(iArr, new int[0], 0, 0);
    }

    private boolean applyLiveRollablePool(int[] iArr, int[] iArr2, int i, int i2) {
        if (iArr == null) {
            iArr = new int[0];
        }
        if (iArr2 == null) {
            iArr2 = new int[0];
        }
        boolean z = (iArr.length == this.liveRollablePool.length && iArr2.length == this.liveRollablePoolWeights.length && i == this.liveRollablePoolTotalWeight && i2 == this.liveDrawsPerStage) ? false : true;
        if (!z) {
            for (int i3 = 0; i3 < iArr.length; i3++) {
                if (iArr[i3] != this.liveRollablePool[i3] || iArr2[i3] != this.liveRollablePoolWeights[i3]) {
                    z = true;
                    break;
                }
            }
        }
        if (!z) {
            return false;
        }
        this.liveRollablePool = iArr;
        this.liveRollablePoolWeights = iArr2;
        this.liveRollablePoolTotalWeight = i;
        this.liveDrawsPerStage = i2;
        this.liveRollablePoolIds.clear();
        for (int i4 : iArr) {
            this.liveRollablePoolIds.add(Integer.valueOf(i4));
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean rollableNow(int i) {
        if (this.liveRollablePoolIds.isEmpty()) {
            return true;
        }
        return this.liveRollablePoolIds.contains(Integer.valueOf(i));
    }

    private double roundChanceFor(int i) {
        return SpecifiedHexHitRate.roundChance(i, this.liveRollablePool, this.liveRollablePoolWeights, this.liveRollablePoolTotalWeight, this.liveDrawsPerStage);
    }

    private boolean applyLiveStageLevels(int[] iArr) {
        int[] iArrCopyStageLevels = SpecifiedHexOfferPolicy.copyStageLevels(iArr);
        if (SpecifiedHexOfferPolicy.sameStageLevels(this.liveStageLevels, iArrCopyStageLevels)) {
            return false;
        }
        this.liveStageLevels = iArrCopyStageLevels;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLiveHexLevels(int[] iArr) {
        String currentMatchGeneration;
        if (this.disposed || this.catalog == null || (currentMatchGeneration = this.manager.getCurrentMatchGeneration()) == null || currentMatchGeneration.length() != 22) {
            return;
        }
        boolean zObserveMatchGeneration = observeMatchGeneration(currentMatchGeneration);
        if (applyLiveStageLevels(iArr)) {
            zObserveMatchGeneration = true;
        }
        if (!SpecifiedHexOfferPolicy.hasAnyStageLevel(iArr)) {
            currentMatchGeneration = "";
        }
        this.levelsQueriedForMatch = currentMatchGeneration;
        if (lockVisibleModeIfNeeded()) {
            zObserveMatchGeneration = true;
        }
        if (syncEditingLevelForMatch() ? true : zObserveMatchGeneration) {
            refreshLevelChips();
            refreshStageChipStyles();
            scheduleListRefresh();
        }
    }

    private boolean offeredForStage(SpecifiedHexCatalog.Entry entry, int i) {
        String str;
        if (this.visibleModes.isEmpty()) {
            refreshVisibleModes();
        }
        if (this.visibleModes.isEmpty()) {
            str = "18";
        } else {
            str = this.visibleModes.get(Math.min(this.currentModeIndex, r0.size() - 1)).id;
        }
        return SpecifiedHexOfferPolicy.isOfferedForDisplay(entry, this.offerTable, officialSeason(), str, i, this.editingLevel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SpecifiedHexCatalog.Entry findStoredEntry(int i, String str) {
        if (this.catalog == null) {
            return null;
        }
        SpecifiedHexCatalog.Mode modeModeById = modeById(str);
        String str2 = modeModeById == null ? "" : modeModeById.season;
        for (SpecifiedHexCatalog.Entry entry : this.catalog.entries) {
            if (entry.id == i && (SpecifiedHexOfferPolicy.isCatalogPreference(entry, str2, str) || SpecifiedHexOfferPolicy.isInLivePool(entry, this.offerTable, str2, str))) {
                return entry;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleSelection(SpecifiedHexCatalog.Entry entry) {
        if (this.selected.containsKey(Integer.valueOf(entry.id))) {
            this.selected.remove(Integer.valueOf(entry.id));
            Dbg.i(TAG, "[指定海克斯] 已移除目标：ID=" + entry.id + " 名称=" + entry.name + " 阶段=" + (this.editingStage + 1));
        } else if (!offeredForStage(entry, this.editingStage)) {
            Dbg.i(TAG, "[指定海克斯] 新增目标已拒绝：ID=" + entry.id + " 名称=" + entry.name + " 阶段=" + (this.editingStage + 1) + " 原因=不属于当前阶段池");
            return;
        } else if (!SpecifiedHexSelectionState.canAddCurrentQualityTarget(editingContextSelection().size(), 12)) {
            FloatingWindowService.safeToast(this.context, "当前品质最多可指定 12 个海克斯", 0);
            return;
        } else {
            this.selected.put(Integer.valueOf(entry.id), entry);
            this.justToggledId = entry.id;
            Dbg.i(TAG, "[指定海克斯] 已添加目标：ID=" + entry.id + " 名称=" + entry.name + " 阶段=" + (this.editingStage + 1));
        }
        this.adapter.notifyDataSetChanged();
        updateSelectionUi();
        updateButtons(this.manager.getSpecifiedHexStatus());
        refreshStageChipStyles();
        persistSelection();
        this.armedForMatch = "";
        this.manuallyCancelledGeneration = "";
        this.lastSubmittedKey = "";
        this.specifiedHexCompletedMask = 0;
        this.specifiedHexHitMask = 0;
        this.specifiedHexUnavailableMask = 0;
        scheduleAutoArm();
    }

    private void clearSelection() {
        List<SpecifiedHexCatalog.Entry> listEditingContextSelection = editingContextSelection();
        if (listEditingContextSelection.isEmpty()) {
            return;
        }
        Iterator<SpecifiedHexCatalog.Entry> it = listEditingContextSelection.iterator();
        while (it.hasNext()) {
            this.selected.remove(Integer.valueOf(it.next().id));
        }
        this.adapter.notifyDataSetChanged();
        updateSelectionUi();
        updateButtons(this.manager.getSpecifiedHexStatus());
        persistSelection();
        this.armedForMatch = "";
        this.manuallyCancelledGeneration = "";
        this.lastSubmittedKey = "";
        this.specifiedHexCompletedMask = 0;
        this.specifiedHexHitMask = 0;
        this.specifiedHexUnavailableMask = 0;
        scheduleAutoArm();
    }

    private List<SpecifiedHexCatalog.Entry> editingContextSelection() {
        ArrayList arrayList = new ArrayList();
        for (SpecifiedHexCatalog.Entry entry : this.selected.values()) {
            if (SpecifiedHexSelectionState.isCurrentQuality(entry.level, this.editingLevel)) {
                arrayList.add(entry);
            }
        }
        return arrayList;
    }

    private static int countSelectionAtLevel(LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMap, int i) {
        Iterator<SpecifiedHexCatalog.Entry> it = linkedHashMap.values().iterator();
        int i2 = 0;
        while (it.hasNext()) {
            if (SpecifiedHexSelectionState.isCurrentQuality(it.next().level, i)) {
                i2++;
            }
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSelectionUi() {
        SpecifiedPredictionPreviewView specifiedPredictionPreviewView = this.predictionPreview;
        if (specifiedPredictionPreviewView != null) {
            specifiedPredictionPreviewView.refresh();
        }
        List<SpecifiedHexCatalog.Entry> listEditingContextSelection = editingContextSelection();
        int size = listEditingContextSelection.size();
        this.pickCountText.setText("已选 " + size);
        this.selectedCountText.setText("已选 " + size);
        TextView textView = this.selectedText;
        if (size == 0) {
            textView.setText("未选择海克斯");
            this.selectedText.setTextColor(STATUS_IDLE);
            this.selectedDescription.setText(buildEmptyStateText());
            this.selectedDescription.setVisibility(0);
        } else {
            textView.setText(SpecifiedHexUiText.qualityName(this.editingLevel) + "目标：已选 " + size + " 个");
            this.selectedText.setTextColor(TEXT_PRIMARY);
            boolean zLivePoolAppliesToEditingContext = livePoolAppliesToEditingContext();
            int[] iArr = new int[size];
            for (int i = 0; i < size; i++) {
                iArr[i] = listEditingContextSelection.get(i).id;
            }
            String strSummaryLabel = SpecifiedHexHitRate.summaryLabel(size, SpecifiedHexHitRate.rollableCount(iArr, this.liveRollablePool), SpecifiedHexHitRate.roundChanceForAny(iArr, this.liveRollablePool, this.liveRollablePoolWeights, this.liveRollablePoolTotalWeight, this.liveDrawsPerStage), zLivePoolAppliesToEditingContext, SpecifiedHexHitRate.weightsKnown(this.liveRollablePoolWeights, this.liveRollablePoolTotalWeight));
            this.selectedDescription.setVisibility(8);
            this.targetsContainer.setContentDescription(strSummaryLabel);
        }
        rebuildTargetRows(listEditingContextSelection);
    }

    private CharSequence buildEmptyStateText() {
        return "还没有指定目标";
    }

    private void rebuildTargetRows(List<SpecifiedHexCatalog.Entry> list) {
        this.targetsContainer.removeAllViews();
        if (list.isEmpty()) {
            return;
        }
        boolean zLivePoolAppliesToEditingContext = livePoolAppliesToEditingContext();
        GridLayout gridLayout = new GridLayout(this.context);
        gridLayout.setColumnCount(4);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 1;
        layoutParams.topMargin = dp(6);
        gridLayout.setLayoutParams(layoutParams);
        for (final SpecifiedHexCatalog.Entry entry : list) {
            String strChanceLabel = SpecifiedHexHitRate.chanceLabel(SpecifiedHexHitRate.outlookFor(entry.id, this.liveRollablePool, this.liveRollablePoolWeights, this.liveRollablePoolTotalWeight, zLivePoolAppliesToEditingContext), roundChanceFor(entry.id));
            boolean z = strChanceLabel.startsWith("本阶段不会出") || strChanceLabel.startsWith("本阶段几乎不出");
            FrameLayout frameLayout = new FrameLayout(this.context);
            GridLayout.LayoutParams layoutParams2 = new GridLayout.LayoutParams();
            layoutParams2.width = dp(56);
            layoutParams2.height = dp(56);
            layoutParams2.setMargins(dp(4), dp(4), dp(4), dp(4));
            frameLayout.setLayoutParams(layoutParams2);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(0);
            gradientDrawable.setCornerRadius(dp(12));
            gradientDrawable.setColor(402653183);
            gradientDrawable.setStroke(dp(2), (levelColor(entry.level) & ViewCompat.MEASURED_SIZE_MASK) | (-872415232));
            frameLayout.setBackground(gradientDrawable);
            frameLayout.setAlpha(z ? 0.42f : 1.0f);
            frameLayout.setContentDescription(entry.name + (strChanceLabel.isEmpty() ? "" : "，" + strChanceLabel));
            ImageView imageView = new ImageView(this.context);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            frameLayout.addView(imageView, new FrameLayout.LayoutParams(dp(40), dp(40), 17));
            Glide.with(imageView).load("file:///android_asset/" + entry.icon).override(dp(40), dp(40)).dontAnimate().into(imageView);
            TextView textView = new TextView(this.context);
            textView.setText("×");
            textView.setTextColor(-15665);
            textView.setTextSize(2, 11.0f);
            textView.setTypeface(Typeface.DEFAULT, 1);
            textView.setGravity(17);
            textView.setIncludeFontPadding(false);
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            gradientDrawable2.setShape(1);
            gradientDrawable2.setColor(-432400862);
            gradientDrawable2.setStroke(dp(1), -1711305306);
            textView.setBackground(gradientDrawable2);
            textView.setContentDescription("取消目标 " + entry.name);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda9
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1519x4bd9d028(entry, view);
                }
            });
            FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(dp(18), dp(18), 8388661);
            layoutParams3.setMargins(0, dp(1), dp(1), 0);
            frameLayout.addView(textView, layoutParams3);
            gridLayout.addView(frameLayout);
        }
        this.targetsContainer.addView(gridLayout);
    }

    /* renamed from: lambda$rebuildTargetRows$10$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1519x4bd9d028(SpecifiedHexCatalog.Entry entry, View view) {
        toggleSelection(entry);
    }

    private void scheduleAutoArm() {
        if (this.disposed) {
            return;
        }
        this.mainHandler.removeCallbacks(this.autoArmTask);
        this.mainHandler.postDelayed(this.autoArmTask, 400L);
    }

    private LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> activeStageSelection() {
        int iRuntimeStageIndex = runtimeStageIndex();
        if (iRuntimeStageIndex < 0) {
            return new LinkedHashMap<>();
        }
        return selectionsForMode(runtimeModeId()).get(iRuntimeStageIndex);
    }

    private LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> runtimeSelection(LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMap, int i) {
        LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMap2 = new LinkedHashMap<>();
        for (SpecifiedHexCatalog.Entry entry : linkedHashMap.values()) {
            if (SpecifiedHexSelectionState.isCurrentQuality(entry.level, i)) {
                linkedHashMap2.put(Integer.valueOf(entry.id), entry);
            }
        }
        return linkedHashMap2;
    }

    private boolean hasSelectionAtLevel(LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMap, int i) {
        Iterator<SpecifiedHexCatalog.Entry> it = linkedHashMap.values().iterator();
        while (it.hasNext()) {
            if (it.next().level == i) {
                return true;
            }
        }
        return false;
    }

    private String selectionTextForStage(int i) {
        if (this.catalog == null || this.offerTable == null) {
            return "正在加载海克斯数据";
        }
        if (i < 0 || i >= 3) {
            return "阶段选择无效";
        }
        int iStageLevel = SpecifiedHexOfferPolicy.stageLevel(this.liveStageLevels, i);
        if (iStageLevel == 0) {
            iStageLevel = this.editingLevel;
        }
        LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMap = selectionsForMode(this.manager.isMatchActive() ? runtimeModeId() : displayedModeId()).get(i);
        LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMapRuntimeSelection = runtimeSelection(linkedHashMap, iStageLevel);
        if (linkedHashMapRuntimeSelection.isEmpty()) {
            return SpecifiedHexUiText.noApplicableTargets(i, iStageLevel, !linkedHashMap.isEmpty(), hasSelectionAtLevel(linkedHashMap, iStageLevel));
        }
        return SpecifiedHexUiText.ready(i, iStageLevel, linkedHashMapRuntimeSelection.size());
    }

    private String currentSelectionText() {
        int iRuntimeStageIndex = runtimeStageIndex();
        if (!SpecifiedHexOfferPolicy.hasStageLevel(this.liveStageLevels, iRuntimeStageIndex)) {
            return "正在读取本局海克斯品质";
        }
        return selectionTextForStage(iRuntimeStageIndex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void submitSelection() {
        if (this.disposed) {
            return;
        }
        if (!this.manager.isMatchActive()) {
            this.armedForMatch = "";
            this.lastSubmittedKey = "";
            this.specifiedHexCompletedMask = 0;
            this.specifiedHexHitMask = 0;
            this.specifiedHexUnavailableMask = 0;
            setStatus("选择已保存，进入对局后会按三个阶段自动生效", STATUS_IDLE);
            return;
        }
        final String currentMatchGeneration = this.manager.getCurrentMatchGeneration();
        if (SpecifiedHexSelectionState.isCurrentResponse(this.manuallyCancelledGeneration, currentMatchGeneration)) {
            return;
        }
        if (!runtimeContextReady(currentMatchGeneration)) {
            this.armedForMatch = "";
            this.lastSubmittedKey = "";
            setStatus(runtimeContextPendingText(currentMatchGeneration), -36205);
            return;
        }
        int iRuntimeStageIndex = runtimeStageIndex();
        if (iRuntimeStageIndex < 0) {
            this.lastSubmittedKey = "";
            this.specifiedHexCompletedMask = 0;
            this.specifiedHexHitMask = 0;
            this.specifiedHexUnavailableMask = 0;
            setStatus("正在等待本局海克斯商店", STATUS_IDLE);
            this.manager.cancelSpecifiedHex(new SharedMemManager.ActionCallback() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda2
                @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
                public final void onTerminal(boolean z, String str) {
                    this.f$0.m1522lambda$submitSelection$11$comandroidsupportSpecifiedHexPanel(currentMatchGeneration, z, str);
                }
            });
            return;
        }
        if (!SpecifiedHexOfferPolicy.hasStageLevel(this.liveStageLevels, iRuntimeStageIndex)) {
            this.armedForMatch = "";
            this.lastSubmittedKey = "";
            setStatus("正在读取本局海克斯品质", STATUS_IDLE);
            return;
        }
        int iStageLevel = SpecifiedHexOfferPolicy.stageLevel(this.liveStageLevels, iRuntimeStageIndex);
        LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMapActiveStageSelection = activeStageSelection();
        LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMapRuntimeSelection = runtimeSelection(linkedHashMapActiveStageSelection, iStageLevel);
        String str = "运行模式=" + runtimeModeId() + " 显示模式=" + displayedModeId() + " 阶段=" + iRuntimeStageIndex + " 编辑阶段=" + this.editingStage + " 品质=" + iStageLevel + " 已存=" + linkedHashMapActiveStageSelection.size() + " 本品质=" + linkedHashMapRuntimeSelection.size();
        if (!str.equals(this.lastSubmitTrace)) {
            this.lastSubmitTrace = str;
            Dbg.i(TAG, "[指定海克斯] 下发检查：" + str);
        }
        if (linkedHashMapRuntimeSelection.isEmpty()) {
            this.lastSubmittedKey = "";
            this.specifiedHexCompletedMask = 0;
            this.specifiedHexHitMask = 0;
            this.specifiedHexUnavailableMask = 0;
            final String strNoApplicableTargets = SpecifiedHexUiText.noApplicableTargets(iRuntimeStageIndex, iStageLevel, !linkedHashMapActiveStageSelection.isEmpty(), hasSelectionAtLevel(linkedHashMapActiveStageSelection, iStageLevel));
            setStatus(strNoApplicableTargets, STATUS_IDLE);
            this.manager.cancelSpecifiedHex(new SharedMemManager.ActionCallback() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda3
                @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
                public final void onTerminal(boolean z, String str2) {
                    this.f$0.m1523lambda$submitSelection$12$comandroidsupportSpecifiedHexPanel(currentMatchGeneration, strNoApplicableTargets, z, str2);
                }
            });
            return;
        }
        ArrayList arrayList = new ArrayList(linkedHashMapRuntimeSelection.keySet());
        String str2 = (currentMatchGeneration != null ? currentMatchGeneration : "") + "|" + iRuntimeStageIndex + "|" + iStageLevel + "|" + arrayList + "|" + this.prefs.getBoolean("strict", false);
        if (str2.equals(this.lastSubmittedKey)) {
            return;
        }
        setStatus(SpecifiedHexUiText.ready(iRuntimeStageIndex, iStageLevel, arrayList.size()), -10556417);
        int i = iRuntimeStageIndex + 1;
        Dbg.i(TAG, "[指定海克斯] 下发目标：阶段=" + i + " 品质=" + iStageLevel + " 数量=" + arrayList.size() + " ID列表=" + arrayList);
        this.lastSubmittedKey = str2;
        this.specifiedHexCompletedMask = 0;
        this.specifiedHexHitMask = 0;
        this.specifiedHexUnavailableMask = 0;
        this.manager.specifyHexes(arrayList, i, this.prefs.getBoolean("strict", false), new SharedMemManager.ActionCallback() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda4
            @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
            public final void onTerminal(boolean z, String str3) {
                this.f$0.m1524lambda$submitSelection$13$comandroidsupportSpecifiedHexPanel(currentMatchGeneration, z, str3);
            }
        });
    }

    /* renamed from: lambda$submitSelection$11$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1522lambda$submitSelection$11$comandroidsupportSpecifiedHexPanel(String str, boolean z, String str2) {
        if (this.disposed || !this.matchLifecycle.isCurrent(str)) {
            return;
        }
        if (!z) {
            setStatus(errorText(str2), -36205);
        }
        updateButtons(this.manager.getSpecifiedHexStatus());
    }

    /* renamed from: lambda$submitSelection$12$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1523lambda$submitSelection$12$comandroidsupportSpecifiedHexPanel(String str, String str2, boolean z, String str3) {
        if (this.disposed || !this.matchLifecycle.isCurrent(str)) {
            return;
        }
        if (z) {
            setStatus(str2, STATUS_IDLE);
        } else {
            setStatus(errorText(str3), -36205);
        }
        updateButtons(this.manager.getSpecifiedHexStatus());
    }

    /* renamed from: lambda$submitSelection$13$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1524lambda$submitSelection$13$comandroidsupportSpecifiedHexPanel(String str, boolean z, String str2) {
        if (!this.disposed && this.matchLifecycle.isCurrent(str) && str.equals(this.manager.getCurrentMatchGeneration()) && !z) {
            if (SpecifiedHexSelectionState.shouldUnlockAfterSubmitFailure(str2)) {
                this.armedForMatch = "";
                this.lastSubmittedKey = "";
            } else if ("execution_failed".equals(str2)) {
                this.mainHandler.removeCallbacks(this.submitRetryTask);
                this.mainHandler.postDelayed(this.submitRetryTask, SUBMIT_RETRY_MS);
            }
            setStatus(errorText(str2), -36205);
            updateButtons(this.manager.getSpecifiedHexStatus());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void retrySubmitAfterRejection() {
        if (this.disposed || !this.manager.isMatchActive()) {
            return;
        }
        this.armedForMatch = "";
        this.lastSubmittedKey = "";
        scheduleAutoArm();
    }

    private void cancelSpecifiedHex() {
        this.cancelButton.setEnabled(false);
        this.mainHandler.removeCallbacks(this.autoArmTask);
        this.mainHandler.removeCallbacks(this.submitRetryTask);
        this.lastSubmittedKey = "";
        this.specifiedHexCompletedMask = 0;
        this.specifiedHexHitMask = 0;
        this.specifiedHexUnavailableMask = 0;
        final String currentMatchGeneration = this.manager.getCurrentMatchGeneration();
        this.manuallyCancelledGeneration = currentMatchGeneration != null ? currentMatchGeneration : "";
        setStatus("本局已暂停自动刷新", STATUS_IDLE);
        this.manager.cancelSpecifiedHex(new SharedMemManager.ActionCallback() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda5
            @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
            public final void onTerminal(boolean z, String str) {
                this.f$0.m1512x62a9ed72(currentMatchGeneration, z, str);
            }
        });
    }

    /* renamed from: lambda$cancelSpecifiedHex$14$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1512x62a9ed72(String str, boolean z, String str2) {
        if (this.disposed) {
            return;
        }
        if (!z) {
            setStatus(errorText(str2), -36205);
            updateButtons(this.manager.getSpecifiedHexStatus());
        } else if (this.manuallyCancelledGeneration.equals(str)) {
            setStatus("本局已暂停自动刷新", STATUS_IDLE);
            updateButtons(this.manager.getSpecifiedHexStatus());
        }
    }

    private void syncHexCues() {
        int i = this.specifiedHexHitMask & this.specifiedHexCompletedMask & 7;
        int i2 = this.cuedCompletedMask;
        if (i == i2) {
            return;
        }
        boolean z = ((~i2) & i) != 0;
        this.cuedCompletedMask = i;
        if (z) {
            playHexCue();
        }
    }

    private void playHexCue() {
        Context context;
        if (this.disposed || (context = this.context) == null || this.cueSound == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(FloatingWindowPrefsKeys.PREF_NAME, 0);
        if (sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_HEX_CUE_ENABLED, true)) {
            this.cueSound.play(HexCueSound.volumeForSlider(sharedPreferences.getInt(FloatingWindowPrefsKeys.KEY_HEX_CUE_VOLUME, 80)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderStatus(SpecifiedHexStatus specifiedHexStatus) {
        int iRuntimeStageIndex;
        int iStageLevel;
        String strMonitoring;
        String strCurrentSelectionText;
        if (this.disposed || specifiedHexStatus == null) {
            return;
        }
        syncHexCues();
        boolean zObserveMatchGeneration = observeMatchGeneration(specifiedHexStatus.matchGeneration);
        if (this.matchLifecycle.isCurrent(specifiedHexStatus.matchGeneration)) {
            boolean zIsNewRestore = SpecifiedHexSelectionState.isNewRestore(specifiedHexStatus.code, specifiedHexStatus.sessionGeneration, specifiedHexStatus.targetRevision, this.lastRestoredSessionGeneration, this.lastRestoredTargetRevision);
            if (zIsNewRestore) {
                this.lastRestoredSessionGeneration = specifiedHexStatus.sessionGeneration;
                this.lastRestoredTargetRevision = specifiedHexStatus.targetRevision;
            }
            int i = specifiedHexStatus.stage - 1;
            if (i >= 3) {
                i = 2;
            }
            boolean zUpdateStage = this.matchLifecycle.updateStage(specifiedHexStatus.matchGeneration, i);
            if (zUpdateStage || "stage_changed".equals(specifiedHexStatus.code)) {
                this.armedForMatch = "";
                this.lastSubmittedKey = "";
                this.specifiedHexCompletedMask = 0;
                this.specifiedHexHitMask = 0;
                this.specifiedHexUnavailableMask = 0;
                int iCurrentLiveStageIndex = currentLiveStageIndex();
                if (zUpdateStage) {
                    if (iCurrentLiveStageIndex >= 0) {
                        this.editingStage = iCurrentLiveStageIndex;
                        bindDisplayedModeSelection();
                    }
                    syncEditingLevelForMatch();
                    refreshLevelChips();
                    refreshStageChipStyles();
                    scheduleListRefresh();
                }
                if (iCurrentLiveStageIndex >= 0) {
                    scheduleAutoArm();
                }
            } else if (zObserveMatchGeneration) {
                refreshLevelChips();
                refreshStageChipStyles();
                scheduleListRefresh();
            }
            final String currentMatchGeneration = this.manager.getCurrentMatchGeneration();
            if (!this.manuallyCancelledGeneration.isEmpty() && this.manuallyCancelledGeneration.equals(currentMatchGeneration)) {
                if (zIsNewRestore) {
                    this.manager.cancelSpecifiedHex(new SharedMemManager.ActionCallback() { // from class: com.android.support.SpecifiedHexPanel$$ExternalSyntheticLambda6
                        @Override // com.android.support.IpcV2ActionCompletion.TerminalCallback
                        public final void onTerminal(boolean z, String str) {
                            this.f$0.m1521lambda$renderStatus$15$comandroidsupportSpecifiedHexPanel(currentMatchGeneration, z, str);
                        }
                    });
                }
                setStatus("本局已暂停自动刷新", STATUS_IDLE);
                updateButtons(specifiedHexStatus);
                return;
            }
            if (zIsNewRestore && SpecifiedHexSelectionState.shouldRearmAfterRestore(specifiedHexStatus.code, currentMatchGeneration, this.manuallyCancelledGeneration)) {
                this.armedForMatch = "";
                this.lastSubmittedKey = "";
                scheduleAutoArm();
            }
            if (this.manager.isMatchActive() && this.catalog != null && this.offerTable != null) {
                int iRuntimeStageIndex2 = runtimeStageIndex();
                int i2 = this.editingStage;
                if (i2 != iRuntimeStageIndex2) {
                    setStatus(selectionTextForStage(i2), -10556417);
                    updateButtons(specifiedHexStatus);
                    return;
                } else if (SpecifiedHexOfferPolicy.hasStageLevel(this.liveStageLevels, iRuntimeStageIndex2)) {
                    LinkedHashMap<Integer, SpecifiedHexCatalog.Entry> linkedHashMapActiveStageSelection = activeStageSelection();
                    int iStageLevel2 = SpecifiedHexOfferPolicy.stageLevel(this.liveStageLevels, iRuntimeStageIndex2);
                    if (runtimeSelection(linkedHashMapActiveStageSelection, iStageLevel2).isEmpty()) {
                        setStatus(SpecifiedHexUiText.noApplicableTargets(iRuntimeStageIndex2, iStageLevel2, !linkedHashMapActiveStageSelection.isEmpty(), hasSelectionAtLevel(linkedHashMapActiveStageSelection, iStageLevel2)), STATUS_IDLE);
                        updateButtons(specifiedHexStatus);
                        return;
                    }
                }
            }
            iRuntimeStageIndex = runtimeStageIndex();
            iStageLevel = SpecifiedHexOfferPolicy.stageLevel(this.liveStageLevels, iRuntimeStageIndex);
            String str = specifiedHexStatus.phase;
            str.hashCode();
            switch (str) {
                case "completed":
                    if ("all_slots_refresh_unavailable".equals(specifiedHexStatus.code)) {
                        this.specifiedHexUnavailableMask |= 7 & (~this.specifiedHexCompletedMask);
                    } else {
                        this.specifiedHexCompletedMask = 7;
                    }
                    setStatus(SpecifiedHexUiText.progress(iRuntimeStageIndex, iStageLevel, this.specifiedHexCompletedMask, this.specifiedHexUnavailableMask), STATUS_OK);
                    break;
                case "failed":
                    setStatus(errorText(specifiedHexStatus.code), -36205);
                    break;
                case "armed":
                    if (InputTimingStatus.issue(specifiedHexStatus.code) != null) {
                        setStatus(InputTimingStatus.issue(specifiedHexStatus.code), STATUS_BUSY);
                        break;
                    } else if ("target_update_pending".equals(specifiedHexStatus.code)) {
                        setStatus(SpecifiedHexUiText.switchingStage(iRuntimeStageIndex, iStageLevel), STATUS_BUSY);
                        break;
                    } else if ("store_open".equals(specifiedHexStatus.code) || "awaiting_hit".equals(specifiedHexStatus.code)) {
                        int i3 = this.specifiedHexCompletedMask;
                        int i4 = this.specifiedHexUnavailableMask;
                        if ((i3 | i4) != 0) {
                            setStatus(SpecifiedHexUiText.progress(iRuntimeStageIndex, iStageLevel, i3, i4), STATUS_OK);
                            break;
                        } else if ("store_open".equals(specifiedHexStatus.code)) {
                            setStatus(SpecifiedHexUiText.preparing(iRuntimeStageIndex, iStageLevel), -10556417);
                            break;
                        } else {
                            if (specifiedHexStatus.timingRange.isEmpty()) {
                                strMonitoring = SpecifiedHexUiText.monitoring(iRuntimeStageIndex, iStageLevel);
                            } else {
                                strMonitoring = specifiedHexStatus.timingRange;
                            }
                            setStatus(strMonitoring, -10556417);
                            break;
                        }
                    } else if ("capability_unavailable".equals(specifiedHexStatus.code)) {
                        setStatus("自动刷新暂未授权，仅保持预测", STATUS_BUSY);
                        break;
                    } else if ("refresh_result_timeout_locked".equals(specifiedHexStatus.code)) {
                        setStatus("有位置已手动刷过 · 其余继续", STATUS_BUSY);
                        break;
                    } else if ("auto_refresh_slot_1".equals(specifiedHexStatus.code)) {
                        setStatus(SpecifiedHexUiText.refreshingSlot(iRuntimeStageIndex, iStageLevel, 0, this.specifiedHexCompletedMask, this.specifiedHexUnavailableMask), STATUS_BUSY);
                        break;
                    } else if ("auto_refresh_slot_2".equals(specifiedHexStatus.code)) {
                        setStatus(SpecifiedHexUiText.refreshingSlot(iRuntimeStageIndex, iStageLevel, 1, this.specifiedHexCompletedMask, this.specifiedHexUnavailableMask), STATUS_BUSY);
                        break;
                    } else if ("auto_refresh_slot_3".equals(specifiedHexStatus.code)) {
                        setStatus(SpecifiedHexUiText.refreshingSlot(iRuntimeStageIndex, iStageLevel, 2, this.specifiedHexCompletedMask, this.specifiedHexUnavailableMask), STATUS_BUSY);
                        break;
                    } else if (SpecifiedHexStatusCodes.completedSlotBit(specifiedHexStatus.code) != 0) {
                        int iCompletedSlotBit = SpecifiedHexStatusCodes.completedSlotBit(specifiedHexStatus.code);
                        this.specifiedHexCompletedMask |= iCompletedSlotBit;
                        if (SpecifiedHexStatusCodes.isOwnRefreshHit(specifiedHexStatus.code)) {
                            this.specifiedHexHitMask = iCompletedSlotBit | this.specifiedHexHitMask;
                        }
                        setStatus(SpecifiedHexUiText.progress(iRuntimeStageIndex, iStageLevel, this.specifiedHexCompletedMask, this.specifiedHexUnavailableMask), STATUS_OK);
                        break;
                    } else if (SpecifiedHexStatusCodes.unavailableSlotBit(specifiedHexStatus.code) != 0) {
                        int iUnavailableSlotBit = this.specifiedHexUnavailableMask | SpecifiedHexStatusCodes.unavailableSlotBit(specifiedHexStatus.code);
                        this.specifiedHexUnavailableMask = iUnavailableSlotBit;
                        setStatus(SpecifiedHexUiText.progress(iRuntimeStageIndex, iStageLevel, this.specifiedHexCompletedMask, iUnavailableSlotBit), STATUS_OK);
                        break;
                    } else if ("refresh_card_retry".equals(specifiedHexStatus.code)) {
                        setStatus("本次刷新未完成，正在自动重试", STATUS_BUSY);
                        break;
                    } else {
                        setStatus(currentSelectionText(), -10556417);
                        break;
                    }
                    break;
                default:
                    if (this.manager.isMatchActive()) {
                        strCurrentSelectionText = currentSelectionText();
                    } else {
                        strCurrentSelectionText = "选择会自动保存，进入对局后按三个阶段生效";
                    }
                    setStatus(strCurrentSelectionText, STATUS_IDLE);
                    break;
            }
            syncHexCues();
            updateButtons(specifiedHexStatus);
        }
    }

    /* renamed from: lambda$renderStatus$15$com-android-support-SpecifiedHexPanel, reason: not valid java name */
    /* synthetic */ void m1521lambda$renderStatus$15$comandroidsupportSpecifiedHexPanel(String str, boolean z, String str2) {
        if (this.disposed || !SpecifiedHexSelectionState.isCurrentResponse(str, this.manager.getCurrentMatchGeneration()) || z) {
            return;
        }
        setStatus(errorText(str2), -36205);
    }

    private void updateButtons(SpecifiedHexStatus specifiedHexStatus) {
        boolean z = specifiedHexStatus != null && specifiedHexStatus.isRunning();
        this.cancelButton.setEnabled(z);
        OverlayActionButtonStyle.apply(this.cancelButton, OverlayActionButtonStyle.Kind.STOP, z, dp(11), dp(2));
        boolean zIsEmpty = editingContextSelection().isEmpty();
        this.clearButton.setEnabled(!zIsEmpty);
        this.clearButton.setAlpha(!zIsEmpty ? 1.0f : 0.5f);
    }

    private String errorText(String str) {
        if ("target_not_observed".equals(str)) {
            return "本轮未预测到已选目标，未执行刷新";
        }
        if ("hex_stage_not_reached".equals(str)) {
            return "本局尚未进入海克斯选择，未执行刷新";
        }
        if ("no_active_match_generation".equals(str)) {
            return "进入对局后会自动启用";
        }
        if ("stale_action".equals(str) || "stale_session".equals(str) || "session_replaced".equals(str) || "peer_disconnected_after_write".equals(str)) {
            return "游戏连接已更新，请稍候自动恢复";
        }
        return "execution_failed".equals(str) ? "对局状态同步中，稍后自动重试" : "busy".equals(str) ? "正在等待上一次刷新完成，请稍候" : "暂时无法启用自动刷新，请稍后重试";
    }

    void dispose() {
        if (this.disposed) {
            return;
        }
        this.disposed = true;
        this.mainHandler.removeCallbacks(this.autoArmTicker);
        this.mainHandler.removeCallbacks(this.autoArmTask);
        this.mainHandler.removeCallbacks(this.submitRetryTask);
        this.mainHandler.removeCallbacks(this.listRefreshTask);
        this.mainHandler.removeCallbacks(this.searchFilterTask);
        this.manager.setSpecifiedHexStatusListener(null);
        this.manager.setHexLevelsListener(null);
        SpecifiedPredictionPreviewView specifiedPredictionPreviewView = this.predictionPreview;
        if (specifiedPredictionPreviewView != null) {
            specifiedPredictionPreviewView.dispose();
        }
        this.loader.shutdownNow();
        HexCueSound hexCueSound = this.cueSound;
        if (hexCueSound != null) {
            hexCueSound.close();
            this.cueSound = null;
        }
        if (this.grid == null) {
            return;
        }
        int i = 0;
        while (true) {
            int childCount = this.grid.getChildCount();
            GridView gridView = this.grid;
            if (i < childCount) {
                View childAt = gridView.getChildAt(i);
                ImageView imageView = childAt == null ? null : (ImageView) childAt.findViewWithTag("hex_icon");
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
    final class HexAdapter extends BaseAdapter {
        private HexAdapter() {
        }

        private int applyCellMetrics(Holder holder) {
            if (SpecifiedHexPanel.this.cellIconPx <= 0) {
                SpecifiedHexPanel specifiedHexPanel = SpecifiedHexPanel.this;
                specifiedHexPanel.refreshCellMetricsCache(specifiedHexPanel.context.getResources());
            }
            SpecifiedHexPanel.setViewHeight(holder.card, SpecifiedHexPanel.this.cellHeightPx);
            SpecifiedHexPanel.setViewHeight(holder.name, SpecifiedHexPanel.this.cellNamePx);
            ViewGroup.LayoutParams layoutParams = holder.icon.getLayoutParams();
            if (layoutParams != null && (layoutParams.width != SpecifiedHexPanel.this.cellIconPx || layoutParams.height != SpecifiedHexPanel.this.cellIconPx)) {
                layoutParams.width = SpecifiedHexPanel.this.cellIconPx;
                layoutParams.height = SpecifiedHexPanel.this.cellIconPx;
                holder.icon.setLayoutParams(layoutParams);
            }
            return SpecifiedHexPanel.this.cellIconPx;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return SpecifiedHexPanel.this.filtered.size();
        }

        @Override // android.widget.Adapter
        public SpecifiedHexCatalog.Entry getItem(int i) {
            return (SpecifiedHexCatalog.Entry) SpecifiedHexPanel.this.filtered.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            SpecifiedHexCatalog.Entry item = getItem(i);
            return (item.id & 4294967295L) | (Integer.parseInt(item.mode) << 32);
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) throws Resources.NotFoundException {
            Holder holder;
            if (!(view instanceof FrameLayout) || view.getTag() == null) {
                FrameLayout frameLayout = new FrameLayout(SpecifiedHexPanel.this.context);
                frameLayout.setLayoutParams(new AbsListView.LayoutParams(-1, SpecifiedHexPanel.this.context.getResources().getDimensionPixelSize(R.dimen.xf_hexpick_cell_h)));
                LinearLayout linearLayout = new LinearLayout(SpecifiedHexPanel.this.context);
                linearLayout.setOrientation(1);
                linearLayout.setGravity(1);
                linearLayout.setPadding(SpecifiedHexPanel.this.dp(4), SpecifiedHexPanel.this.dp(7), SpecifiedHexPanel.this.dp(4), SpecifiedHexPanel.this.dp(5));
                frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(-1, -1));
                ImageView imageView = new ImageView(SpecifiedHexPanel.this.context);
                imageView.setTag("hex_icon");
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int dimensionPixelSize = SpecifiedHexPanel.this.context.getResources().getDimensionPixelSize(R.dimen.xf_hexpick_icon);
                linearLayout.addView(imageView, new LinearLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize));
                TextView textView = new TextView(SpecifiedHexPanel.this.context);
                textView.setGravity(17);
                textView.setTextSize(2, 10.0f);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTypeface(Typeface.DEFAULT, 1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, SpecifiedHexPanel.this.context.getResources().getDimensionPixelSize(R.dimen.xf_hexpick_name_h));
                layoutParams.topMargin = SpecifiedHexPanel.this.dp(2);
                linearLayout.addView(textView, layoutParams);
                TextView textView2 = new TextView(SpecifiedHexPanel.this.context);
                textView2.setText("✓");
                textView2.setTextColor(SpecifiedHexPanel.ACCENT_ON);
                textView2.setTextSize(2, 11.0f);
                textView2.setGravity(17);
                textView2.setTypeface(Typeface.DEFAULT, 1);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(1);
                gradientDrawable.setColor(-10556417);
                textView2.setBackground(gradientDrawable);
                FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(SpecifiedHexPanel.this.dp(19), SpecifiedHexPanel.this.dp(19), 8388661);
                layoutParams2.topMargin = SpecifiedHexPanel.this.dp(4);
                layoutParams2.rightMargin = SpecifiedHexPanel.this.dp(4);
                frameLayout.addView(textView2, layoutParams2);
                Holder holder2 = new Holder(frameLayout, imageView, textView, textView2, cellBackground(false), cellBackground(true));
                frameLayout.setTag(holder2);
                frameLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SpecifiedHexPanel$HexAdapter$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.m1525xa61effc1(view2);
                    }
                });
                frameLayout.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.support.SpecifiedHexPanel$HexAdapter$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view2) {
                        return this.f$0.m1526x97c8a5e0(view2);
                    }
                });
                holder = holder2;
                view = frameLayout;
            } else {
                holder = (Holder) view.getTag();
            }
            int iApplyCellMetrics = applyCellMetrics(holder);
            SpecifiedHexCatalog.Entry item = getItem(i);
            holder.entry = item;
            boolean zContainsKey = SpecifiedHexPanel.this.selected.containsKey(Integer.valueOf(item.id));
            holder.name.setText(item.name);
            holder.name.setTextColor(SpecifiedHexPanel.levelColor(item.level));
            holder.icon.setContentDescription(item.name);
            view.setAlpha((!SpecifiedHexPanel.this.poolAppliesToList || SpecifiedHexPanel.this.rollableNow(item.id)) ? 1.0f : 0.35f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            holder.check.setVisibility(zContainsKey ? 0 : 8);
            if (zContainsKey && item.id == SpecifiedHexPanel.this.justToggledId) {
                SpecifiedHexPanel.this.justToggledId = 0;
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
            long j = (item.id << 20) | (iApplyCellMetrics & 1048575);
            if (holder.boundIconKey != j || holder.icon.getDrawable() == null) {
                holder.boundIconKey = j;
                Glide.with(holder.icon).load("file:///android_asset/" + item.icon).override(iApplyCellMetrics, iApplyCellMetrics).dontAnimate().into(holder.icon);
            }
            if (holder.appliedSelected != zContainsKey) {
                holder.appliedSelected = zContainsKey ? 1 : 0;
                holder.card.setBackground(zContainsKey ? holder.selectedBackground : holder.idleBackground);
            }
            return view;
        }

        /* renamed from: lambda$getView$0$com-android-support-SpecifiedHexPanel$HexAdapter, reason: not valid java name */
        /* synthetic */ void m1525xa61effc1(View view) {
            Holder holder = (Holder) view.getTag();
            if (holder == null || holder.entry == null) {
                return;
            }
            view.animate().cancel();
            view.setScaleX(0.92f);
            view.setScaleY(0.92f);
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(220L).setInterpolator(new OvershootInterpolator(2.5f)).start();
            SpecifiedHexPanel.this.toggleSelection(holder.entry);
        }

        /* renamed from: lambda$getView$1$com-android-support-SpecifiedHexPanel$HexAdapter, reason: not valid java name */
        /* synthetic */ boolean m1526x97c8a5e0(View view) {
            Holder holder = (Holder) view.getTag();
            if (holder != null && holder.entry != null) {
                FloatingWindowService.safeToast(SpecifiedHexPanel.this.context, holder.entry.name + "\n" + holder.entry.description, 1);
            }
            return true;
        }

        private GradientDrawable cellBackground(boolean z) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(0);
            gradientDrawable.setCornerRadius(SpecifiedHexPanel.this.dp(14));
            gradientDrawable.setColor(z ? SpecifiedHexPanel.ACCENT_FILL : SpecifiedHexPanel.SURFACE_FILL);
            gradientDrawable.setStroke(SpecifiedHexPanel.this.dp(z ? 2 : 1), z ? -10556417 : SpecifiedHexPanel.SURFACE_STROKE);
            return gradientDrawable;
        }
    }

    private static final class Holder {
        int appliedSelected = -1;
        long boundIconKey = Long.MIN_VALUE;
        final FrameLayout card;
        final TextView check;
        SpecifiedHexCatalog.Entry entry;
        final ImageView icon;
        final GradientDrawable idleBackground;
        final TextView name;
        final GradientDrawable selectedBackground;

        Holder(FrameLayout frameLayout, ImageView imageView, TextView textView, TextView textView2, GradientDrawable gradientDrawable, GradientDrawable gradientDrawable2) {
            this.card = frameLayout;
            this.icon = imageView;
            this.name = textView;
            this.check = textView2;
            this.idleBackground = gradientDrawable;
            this.selectedBackground = gradientDrawable2;
        }
    }
}
