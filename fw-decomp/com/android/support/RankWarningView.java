package com.android.support;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import androidx.core.view.ViewCompat;
import com.android.support.HeroIdMapper;
import com.android.support.RankWarningPoolIndex;
import com.android.support.RankWarningRenderText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlinx.coroutines.DebugKt;

/* loaded from: classes.dex */
class RankWarningView extends View {
    private static final long RANK_ANCHOR_MIN_FRAME_MS = 33;
    private static final float RANK_ANCHOR_SMOOTH_EPSILON = 1.0E-4f;
    private static final long RANK_ANCHOR_SMOOTH_MAX_DT_MS = 50;
    private static final float RANK_ANCHOR_SMOOTH_TAU_MS = 65.0f;
    private static final String TAG = "FloatingWindowService";
    private static final int WARNING_BITMAP_DECODE_LIMIT_PX = 512;
    private final Map<Integer, RectF> anchorBoxRectCache;
    private final float[] anchorBoxRectTmp;
    private final int[] anchorPixelRectTmp;
    private final Point anchorPixelSizeTmp;
    private final Map<Integer, RectF> anchorRowRectCache;
    private final int[] anchorWindowLocationOnScreen;
    private float anchorWindowOriginX;
    private float anchorWindowOriginY;
    private Path arrowPath;
    private boolean attackIconMode;
    private int attackIconOffsetPx;
    private int attackIconOffsetYPx;
    private float attackIconScale;
    private final Paint attackReticlePaint;
    private boolean autoAnchorMode;
    private Paint bitmapPaint;
    private Paint borderPaint;
    private int boxHeightPx;
    private int boxWidthPx;
    private List<BoardPositionInfo> cachedBoardData;
    private final Point cachedRealDisplaySize;
    private int costFilterBits;
    private Paint countBgPaint;
    private Paint countPaint;
    private Map<Integer, Float> currentYMap;
    private final Set<Integer> deadPlayerIdsScratch;
    private Paint debugBorderPaint;
    private Paint debugInfoPaint;
    private Paint debugLabelPaint;
    private Paint debugMeBorderPaint;
    private Paint debugMeFillPaint;
    private boolean debugMode;
    private Paint debugOpponentBorderPaint;
    private Paint debugOpponentFillPaint;
    private Paint debugTagBgPaint;
    private Paint debugTagPaint;
    private boolean floatingEconomyVisible;
    private final HashMap<Integer, Bitmap> heroBitmapCache;
    private Paint heroBorderPaint;
    private final Map<Integer, Integer> heroCountsScratch;
    private int heroGapPx;
    private Paint heroGlowPaint;
    private Paint heroPlaceholderPaint;
    private Map<Integer, RankWarningPoolIndex.Entry> heroPoolIndex;
    private int heroPoolTimestamp;
    private float heroSizeScale;
    private boolean hideBoxes;
    private final Host host;
    private int infoOffsetPx;
    private ValueAnimator infoSizeAnimator;
    private float infoSizeScale;
    private boolean isAdjustMode;
    private Set<Integer> knownPlayerIds;
    private long lastAnchorDiagnosticAtMs;
    private long lastAnchorPostMs;
    private long lastAnchorRenderFrameMs;
    private long lastAttackReticleDiagnosticAtMs;
    private long lastWarningPlacementDiagnosticAtMs;
    private float legacyOriginX;
    private float legacyOriginY;
    private final HashSet<Integer> loadingHeroIds;
    private boolean manualAnchorOverride;
    private Paint mePaint;
    private Paint opponentArrowPaint;
    private Paint opponentFillPaint;
    private Paint opponentGlowPaint;
    private Paint opponentInfoPaint;
    private Paint opponentPaint;
    private int opponentPlayerId;
    private Paint opponentRankPaint;
    private Paint peerBorderPaint;
    private Paint peerFillPaint;
    private Paint peerGlowPaint;
    private Paint peerInfoPaint;
    private Set<Integer> peerSeats;
    private Paint peerTagBgPaint;
    private Paint peerTagPaint;
    private final Map<PlayerInfo, RankWarningRenderText.PlayerText> playerRenderTextCache;
    private Map<Integer, List<HeroWarningInfo>> playerWarnings;
    private ValueAnimator positionAnimator;
    private final Map<Integer, RankWarningAnchor> rankAnchors;
    private long rankAnchorsReceivedAtMs;
    private List<PlayerInfo> rankedPlayers;
    private final Map<Integer, RenderedAnchor> renderedRankAnchors;
    private float reticleDisplayDensity;
    private int reticleDisplayHeightPx;
    private final DisplayMetrics reticleDisplayMetrics;
    private final Point reticleDisplaySize;
    private int reticleDisplayWidthPx;
    private final RectF reticleLocalVisibleBounds;
    private final int[] reticleWindowLocationOnScreen;
    private boolean showOpponent;
    private int spacingPx;
    private Map<Integer, Float> targetYMap;
    private final Paint.FontMetrics tmpFontMetrics;
    private RectF tmpRect;
    private final float[] warningAnchorGeometryTmp;
    private final Path warningAvatarClipPath;
    private final RectF warningAvatarRect;
    private final int[] warningBitmapCropBounds;
    private final Rect warningBitmapSourceRect;
    private Paint warningCardBgPaint;
    private int warningDiagCount;
    private Paint warningDividerPaint;
    private Paint warningMetaBgPaint;
    private Paint warningMetaRingPaint;
    private Paint warningMetaTextPaint;
    private Paint warningOwnedTextPaint;
    private Paint warningPoolFillPaint;
    private Paint warningPoolTextPaint;
    private Paint warningPoolTrackPaint;
    private Paint warningStatBgPaint;
    private int warningThreshold;

    public interface Host {
        String[] buildHeroImageUris(String str);

        int currentSeason();

        HudAppearance hudAppearance();

        boolean isEmulator();

        SharedPreferences preferences();

        int rankInfoSizePercent();

        int rankWarningX();

        int rankWarningY();

        Display resolveTrustedVisualTargetDisplay();

        String[] seasonNames();

        boolean systemAnimationsEnabled();

        WindowManager windowManager();
    }

    private int getHeroCopies(int i) {
        if (i >= 40000 && i < 50000) {
            return 27;
        }
        if (i < 30000 || i >= 40000) {
            return (i < 20000 || i >= 30000) ? 1 : 3;
        }
        return 9;
    }

    private int getWarningColor(int i) {
        if (i >= 9) {
            return -45211;
        }
        if (i >= 8) {
            return -36547;
        }
        return i >= 7 ? -875971 : -1589928;
    }

    private int warningCostColor(int i) {
        if (i == 2) {
            return -14250151;
        }
        if (i == 3) {
            return -14320714;
        }
        if (i == 4) {
            return -6664779;
        }
        return i >= 5 ? -3966175 : -10919578;
    }

    private float warningInfoLaneWidth() {
        return 0.0f;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public void setWarningHeroOffsetPx(int i) {
    }

    public void setPeerSeats(Set<Integer> set) {
        if (set == null) {
            set = new HashSet<>();
        }
        this.peerSeats = set;
        invalidate();
    }

    public RankWarningView(Context context, Host host) {
        super(context);
        this.boxWidthPx = 40;
        this.boxHeightPx = 30;
        this.spacingPx = 4;
        this.heroGapPx = 4;
        this.warningThreshold = 7;
        this.rankedPlayers = new ArrayList();
        this.playerRenderTextCache = new IdentityHashMap();
        this.currentYMap = new HashMap();
        this.targetYMap = new HashMap();
        this.rankAnchors = new HashMap();
        this.renderedRankAnchors = new HashMap();
        this.anchorRowRectCache = new HashMap();
        this.anchorBoxRectCache = new HashMap();
        this.anchorPixelRectTmp = new int[4];
        this.anchorBoxRectTmp = new float[4];
        this.warningAnchorGeometryTmp = new float[2];
        this.rankAnchorsReceivedAtMs = 0L;
        this.lastAnchorRenderFrameMs = 0L;
        this.lastAnchorPostMs = 0L;
        this.lastAnchorDiagnosticAtMs = 0L;
        this.lastWarningPlacementDiagnosticAtMs = 0L;
        this.autoAnchorMode = false;
        this.manualAnchorOverride = false;
        this.legacyOriginX = 0.0f;
        this.legacyOriginY = 0.0f;
        this.anchorPixelSizeTmp = new Point();
        this.cachedRealDisplaySize = new Point();
        this.anchorWindowLocationOnScreen = new int[2];
        this.knownPlayerIds = new HashSet();
        this.opponentPlayerId = -1;
        this.showOpponent = false;
        this.isAdjustMode = false;
        this.hideBoxes = false;
        this.heroSizeScale = 1.0f;
        this.infoSizeScale = 0.85f;
        this.costFilterBits = 31;
        this.debugMode = false;
        this.floatingEconomyVisible = false;
        this.attackReticlePaint = new Paint(1);
        this.reticleDisplaySize = new Point();
        this.reticleDisplayMetrics = new DisplayMetrics();
        this.reticleWindowLocationOnScreen = new int[2];
        this.reticleDisplayWidthPx = 1;
        this.reticleDisplayHeightPx = 1;
        this.reticleDisplayDensity = 1.0f;
        this.reticleLocalVisibleBounds = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
        this.lastAttackReticleDiagnosticAtMs = 0L;
        this.tmpRect = new RectF();
        this.warningBitmapSourceRect = new Rect();
        this.warningAvatarRect = new RectF();
        this.warningAvatarClipPath = new Path();
        this.warningBitmapCropBounds = new int[4];
        this.tmpFontMetrics = new Paint.FontMetrics();
        this.peerSeats = new HashSet();
        this.playerWarnings = new HashMap();
        this.heroPoolIndex = Collections.emptyMap();
        this.heroPoolTimestamp = Integer.MIN_VALUE;
        this.heroBitmapCache = new HashMap<>();
        this.loadingHeroIds = new HashSet<>();
        this.attackIconMode = false;
        this.attackIconOffsetPx = 0;
        this.attackIconOffsetYPx = 0;
        this.attackIconScale = 1.0f;
        this.infoOffsetPx = 0;
        this.warningDiagCount = 0;
        this.deadPlayerIdsScratch = new HashSet();
        this.heroCountsScratch = new HashMap();
        this.host = host;
        init();
    }

    private void init() {
        Paint paint = new Paint(1);
        this.borderPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(2.0f);
        this.borderPaint.setColor(0);
        Paint paint2 = new Paint(1);
        this.mePaint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.mePaint.setStrokeWidth(3.0f);
        this.mePaint.setColor(0);
        Paint paint3 = new Paint(1);
        this.opponentPaint = paint3;
        paint3.setStyle(Paint.Style.STROKE);
        this.opponentPaint.setStrokeWidth(2.5f);
        this.opponentPaint.setColor(GameHudPalette.ALERT_RED);
        Paint paint4 = new Paint(1);
        this.countPaint = paint4;
        paint4.setColor(-1);
        this.countPaint.setTextAlign(Paint.Align.CENTER);
        this.countPaint.setTextSize(5.0f);
        this.countPaint.setFakeBoldText(true);
        Paint paint5 = new Paint(1);
        this.countBgPaint = paint5;
        paint5.setStyle(Paint.Style.FILL);
        this.countBgPaint.setColor(-435745256);
        this.bitmapPaint = new Paint(3);
        Paint paint6 = new Paint(1);
        this.opponentFillPaint = paint6;
        paint6.setStyle(Paint.Style.FILL);
        this.opponentFillPaint.setColor(671088640);
        Paint paint7 = new Paint(1);
        this.opponentArrowPaint = paint7;
        paint7.setStyle(Paint.Style.FILL);
        this.opponentArrowPaint.setColor(-285258409);
        Paint paint8 = new Paint(1);
        this.opponentGlowPaint = paint8;
        paint8.setStyle(Paint.Style.STROKE);
        this.opponentGlowPaint.setStrokeWidth(3.0f);
        this.opponentGlowPaint.setColor(872369495);
        Paint paint9 = new Paint(1);
        this.opponentRankPaint = paint9;
        paint9.setColor(-1);
        this.opponentRankPaint.setTextAlign(Paint.Align.CENTER);
        this.opponentRankPaint.setTextSize(10.0f);
        this.opponentRankPaint.setFakeBoldText(true);
        this.opponentRankPaint.setShadowLayer(2.0f, 0.0f, 0.0f, ViewCompat.MEASURED_STATE_MASK);
        Paint paint10 = new Paint(1);
        this.opponentInfoPaint = paint10;
        paint10.setColor(-1);
        this.opponentInfoPaint.setTextAlign(Paint.Align.CENTER);
        this.opponentInfoPaint.setTextSize(8.0f);
        this.opponentInfoPaint.setFakeBoldText(true);
        this.opponentInfoPaint.setShadowLayer(2.0f, 0.0f, 0.0f, ViewCompat.MEASURED_STATE_MASK);
        this.arrowPath = new Path();
        Paint paint11 = new Paint(1);
        this.heroBorderPaint = paint11;
        paint11.setStyle(Paint.Style.STROKE);
        this.heroBorderPaint.setStrokeWidth(1.5f);
        this.heroBorderPaint.setColor(-253036292);
        Paint paint12 = new Paint(1);
        this.heroGlowPaint = paint12;
        paint12.setStyle(Paint.Style.STROKE);
        this.heroGlowPaint.setStrokeWidth(3.5f);
        this.heroGlowPaint.setColor(979299327);
        Paint paint13 = new Paint(1);
        this.heroPlaceholderPaint = paint13;
        paint13.setStyle(Paint.Style.FILL);
        this.heroPlaceholderPaint.setColor(-15327185);
        Paint paint14 = new Paint(1);
        this.warningCardBgPaint = paint14;
        paint14.setStyle(Paint.Style.FILL);
        this.warningCardBgPaint.setColor(-234156272);
        Paint paint15 = new Paint(1);
        this.warningStatBgPaint = paint15;
        paint15.setStyle(Paint.Style.FILL);
        this.warningStatBgPaint.setColor(-184219896);
        Paint paint16 = new Paint(1);
        this.warningOwnedTextPaint = paint16;
        paint16.setColor(-9048662);
        this.warningOwnedTextPaint.setTextAlign(Paint.Align.CENTER);
        this.warningOwnedTextPaint.setFakeBoldText(true);
        Paint paint17 = new Paint(1);
        this.warningPoolTextPaint = paint17;
        paint17.setColor(-723209);
        this.warningPoolTextPaint.setTextAlign(Paint.Align.CENTER);
        this.warningPoolTextPaint.setFakeBoldText(true);
        Paint paint18 = new Paint(1);
        this.warningMetaBgPaint = paint18;
        paint18.setStyle(Paint.Style.FILL);
        this.warningMetaBgPaint.setColor(-402192118);
        Paint paint19 = new Paint(1);
        this.warningMetaRingPaint = paint19;
        paint19.setStyle(Paint.Style.STROKE);
        this.warningMetaRingPaint.setStrokeCap(Paint.Cap.ROUND);
        this.warningMetaRingPaint.setColor(-637534209);
        Paint paint20 = new Paint(1);
        this.warningMetaTextPaint = paint20;
        paint20.setColor(-11363);
        this.warningMetaTextPaint.setTextAlign(Paint.Align.CENTER);
        this.warningMetaTextPaint.setFakeBoldText(true);
        Paint paint21 = new Paint(1);
        this.warningPoolTrackPaint = paint21;
        paint21.setStyle(Paint.Style.FILL);
        this.warningPoolTrackPaint.setColor(-1728053248);
        Paint paint22 = new Paint(1);
        this.warningPoolFillPaint = paint22;
        paint22.setStyle(Paint.Style.FILL);
        this.warningPoolFillPaint.setColor(-10886673);
        Paint paint23 = new Paint(1);
        this.warningDividerPaint = paint23;
        paint23.setStyle(Paint.Style.STROKE);
        this.warningDividerPaint.setStrokeWidth(1.0f);
        this.warningDividerPaint.setColor(872415231);
        Paint paint24 = new Paint(1);
        this.peerTagPaint = paint24;
        paint24.setColor(-1);
        this.peerTagPaint.setTextAlign(Paint.Align.CENTER);
        this.peerTagPaint.setTextSize(8.0f);
        this.peerTagPaint.setFakeBoldText(true);
        Paint paint25 = new Paint(1);
        this.peerTagBgPaint = paint25;
        paint25.setStyle(Paint.Style.FILL);
        this.peerTagBgPaint.setColor(-285236660);
        Paint paint26 = new Paint(1);
        this.peerBorderPaint = paint26;
        paint26.setStyle(Paint.Style.STROKE);
        this.peerBorderPaint.setStrokeWidth(1.5f);
        this.peerBorderPaint.setColor(-855662004);
        Paint paint27 = new Paint(1);
        this.peerGlowPaint = paint27;
        paint27.setStyle(Paint.Style.STROKE);
        this.peerGlowPaint.setStrokeWidth(3.0f);
        this.peerGlowPaint.setColor(1157603916);
        Paint paint28 = new Paint(1);
        this.peerFillPaint = paint28;
        paint28.setStyle(Paint.Style.FILL);
        this.peerFillPaint.setColor(687841868);
        Paint paint29 = new Paint(1);
        this.peerInfoPaint = paint29;
        paint29.setColor(-1);
        this.peerInfoPaint.setTextAlign(Paint.Align.CENTER);
        this.peerInfoPaint.setTextSize(8.0f);
        this.peerInfoPaint.setFakeBoldText(true);
        this.peerInfoPaint.setShadowLayer(2.0f, 0.0f, 0.0f, ViewCompat.MEASURED_STATE_MASK);
        Paint paint30 = new Paint(1);
        this.debugBorderPaint = paint30;
        paint30.setStyle(Paint.Style.STROKE);
        this.debugBorderPaint.setStrokeWidth(1.5f);
        this.debugBorderPaint.setColor(-1283479098);
        Paint paint31 = new Paint(1);
        this.debugMeBorderPaint = paint31;
        paint31.setStyle(Paint.Style.STROKE);
        this.debugMeBorderPaint.setStrokeWidth(2.5f);
        this.debugMeBorderPaint.setColor(GameHudPalette.SUCCESS);
        Paint paint32 = new Paint(1);
        this.debugOpponentBorderPaint = paint32;
        paint32.setStyle(Paint.Style.STROKE);
        this.debugOpponentBorderPaint.setStrokeWidth(2.5f);
        this.debugOpponentBorderPaint.setColor(GameHudPalette.ALERT_RED);
        Paint paint33 = new Paint(1);
        this.debugMeFillPaint = paint33;
        paint33.setStyle(Paint.Style.FILL);
        this.debugMeFillPaint.setColor(644411559);
        Paint paint34 = new Paint(1);
        this.debugOpponentFillPaint = paint34;
        paint34.setStyle(Paint.Style.FILL);
        this.debugOpponentFillPaint.setColor(654265687);
        Paint paint35 = new Paint(1);
        this.debugLabelPaint = paint35;
        paint35.setColor(-1);
        this.debugLabelPaint.setTextAlign(Paint.Align.CENTER);
        this.debugLabelPaint.setTextSize(10.0f);
        this.debugLabelPaint.setFakeBoldText(true);
        Paint paint36 = new Paint(1);
        this.debugTagPaint = paint36;
        paint36.setColor(-1);
        this.debugTagPaint.setTextAlign(Paint.Align.CENTER);
        this.debugTagPaint.setTextSize(8.0f);
        this.debugTagPaint.setFakeBoldText(true);
        this.debugTagPaint.setShadowLayer(1.5f, 0.0f, 0.0f, ViewCompat.MEASURED_STATE_MASK);
        Paint paint37 = new Paint(1);
        this.debugTagBgPaint = paint37;
        paint37.setStyle(Paint.Style.FILL);
        this.debugTagBgPaint.setColor(-435745256);
        Paint paint38 = new Paint(1);
        this.debugInfoPaint = paint38;
        paint38.setColor(-1430537285);
        this.debugInfoPaint.setTextAlign(Paint.Align.LEFT);
        this.debugInfoPaint.setTextSize(7.0f);
        this.debugInfoPaint.setFakeBoldText(true);
        this.debugInfoPaint.setShadowLayer(2.0f, 0.0f, 0.0f, ViewCompat.MEASURED_STATE_MASK);
    }

    public void setBoxSize(int i, int i2, int i3) {
        this.boxWidthPx = i;
        this.boxHeightPx = i2;
        this.spacingPx = i3;
        float f = i2;
        this.heroGapPx = Math.max(6, Math.round(0.12f * f));
        this.countPaint.setTextSize(Math.max(8.0f, 0.4f * f));
        this.opponentRankPaint.setTextSize(Math.max(9.0f, 0.55f * f));
        float f2 = 0.32f * f;
        this.opponentInfoPaint.setTextSize(Math.max(7.0f, f2));
        this.debugLabelPaint.setTextSize(Math.max(8.0f, 0.45f * f));
        float f3 = 0.3f * f;
        this.debugTagPaint.setTextSize(Math.max(6.0f, f3));
        this.debugInfoPaint.setTextSize(Math.max(9.0f, f * 0.42f));
        this.peerTagPaint.setTextSize(Math.max(6.0f, f3));
        this.peerInfoPaint.setTextSize(Math.max(7.0f, f2));
        this.heroBitmapCache.clear();
        this.loadingHeroIds.clear();
        if (!this.rankedPlayers.isEmpty()) {
            recalcTargetPositions();
            for (Map.Entry<Integer, Float> entry : this.targetYMap.entrySet()) {
                this.currentYMap.put(entry.getKey(), entry.getValue());
            }
        }
        requestLayout();
        invalidate();
    }

    public void updateAnchors(List<RankWarningAnchor> list, long j) {
        if (list == null) {
            clearAnchors();
            return;
        }
        int i = 0;
        if (list != null) {
            for (RankWarningAnchor rankWarningAnchor : list) {
                if (rankWarningAnchor != null && Float.isFinite(rankWarningAnchor.x) && Float.isFinite(rankWarningAnchor.y) && Float.isFinite(rankWarningAnchor.width) && Float.isFinite(rankWarningAnchor.height) && Float.isFinite(rankWarningAnchor.nameX)) {
                    i++;
                }
            }
        }
        if (!RankWarningAnchorMath.shouldReplaceAnchors(this.rankAnchors.size(), i)) {
            refreshAnchorState();
            return;
        }
        this.rankAnchors.clear();
        if (list != null) {
            for (RankWarningAnchor rankWarningAnchor2 : list) {
                if (rankWarningAnchor2 != null && Float.isFinite(rankWarningAnchor2.x) && Float.isFinite(rankWarningAnchor2.y) && Float.isFinite(rankWarningAnchor2.width) && Float.isFinite(rankWarningAnchor2.height) && Float.isFinite(rankWarningAnchor2.nameX)) {
                    this.rankAnchors.put(Integer.valueOf(rankWarningAnchor2.playerId), rankWarningAnchor2);
                }
            }
        }
        this.anchorRowRectCache.keySet().retainAll(this.rankAnchors.keySet());
        this.anchorBoxRectCache.keySet().retainAll(this.rankAnchors.keySet());
        for (Integer num : this.rankAnchors.keySet()) {
            if (!this.anchorRowRectCache.containsKey(num)) {
                this.anchorRowRectCache.put(num, new RectF());
            }
            if (!this.anchorBoxRectCache.containsKey(num)) {
                this.anchorBoxRectCache.put(num, new RectF());
            }
        }
        this.renderedRankAnchors.keySet().retainAll(this.rankAnchors.keySet());
        for (Map.Entry<Integer, RankWarningAnchor> entry : this.rankAnchors.entrySet()) {
            if (!this.renderedRankAnchors.containsKey(entry.getKey())) {
                this.renderedRankAnchors.put(entry.getKey(), new RenderedAnchor(entry.getValue()));
            }
        }
        this.rankAnchorsReceivedAtMs = j;
        long jUptimeMillis = SystemClock.uptimeMillis();
        if (jUptimeMillis - this.lastAnchorDiagnosticAtMs >= 3000) {
            StringBuilder sb = new StringBuilder("[rank-anchor-data]");
            for (RankWarningAnchor rankWarningAnchor3 : this.rankAnchors.values()) {
                sb.append(" id=").append(rankWarningAnchor3.playerId).append('(').append(rankWarningAnchor3.x).append(',').append(rankWarningAnchor3.y).append(',').append(rankWarningAnchor3.width).append(',').append(rankWarningAnchor3.height).append(" nameX=").append(rankWarningAnchor3.nameX).append(')');
            }
            Dbg.i(TAG, sb.toString());
            this.lastAnchorDiagnosticAtMs = jUptimeMillis;
        }
        refreshAnchorState();
        invalidate();
    }

    public void clearAnchors() {
        this.rankAnchors.clear();
        this.renderedRankAnchors.clear();
        this.anchorRowRectCache.clear();
        this.anchorBoxRectCache.clear();
        this.rankAnchorsReceivedAtMs = 0L;
        this.lastAnchorRenderFrameMs = 0L;
        refreshAnchorState();
        requestLayout();
        invalidate();
    }

    public boolean refreshAnchorState() {
        boolean z = this.autoAnchorMode;
        refreshAnchorMode();
        if (!z && this.autoAnchorMode) {
            captureLegacyOrigin();
        }
        if (z != this.autoAnchorMode) {
            Dbg.i(TAG, "[rank-anchor] mode=" + (this.autoAnchorMode ? DebugKt.DEBUG_PROPERTY_VALUE_AUTO : "legacy") + " anchors=" + this.rankAnchors.size() + " receivedAt=" + this.rankAnchorsReceivedAtMs + " view=" + getWidth() + "x" + getHeight());
            requestLayout();
            invalidate();
        }
        return z != this.autoAnchorMode;
    }

    private void refreshAnchorMode() {
        boolean z = this.autoAnchorMode;
        boolean zShouldUseAutoAnchors = RankWarningAnchorMath.shouldUseAutoAnchors(this.manualAnchorOverride, true, this.rankAnchors.size(), this.rankAnchorsReceivedAtMs);
        this.autoAnchorMode = zShouldUseAutoAnchors;
        if (z != zShouldUseAutoAnchors) {
            this.lastAnchorRenderFrameMs = 0L;
        }
    }

    public boolean isAutoAnchorMode() {
        refreshAnchorMode();
        return this.autoAnchorMode;
    }

    private void captureLegacyOrigin() {
        this.legacyOriginX = this.host.rankWarningX();
        this.legacyOriginY = this.host.rankWarningY();
    }

    private float getLegacyOriginX() {
        if (this.autoAnchorMode) {
            return this.legacyOriginX;
        }
        return 0.0f;
    }

    private float getLegacyOriginY() {
        if (this.autoAnchorMode) {
            return this.legacyOriginY;
        }
        return 0.0f;
    }

    private Float addLegacyOrigin(Float f, float f2) {
        if (f == null) {
            return null;
        }
        return Float.valueOf(f.floatValue() + f2);
    }

    private RankWarningAnchor getFreshAnchor(int i) {
        refreshAnchorMode();
        if (this.autoAnchorMode) {
            return this.rankAnchors.get(Integer.valueOf(i));
        }
        return null;
    }

    private RectF getAnchorBox(int i) {
        RectF rectF;
        RectF anchorRowRect = getAnchorRowRect(i);
        if (anchorRowRect == null || !RankWarningAnchorMath.alignBoxToRowAvatar(anchorRowRect.left, anchorRowRect.top, anchorRowRect.width(), anchorRowRect.height(), this.anchorBoxRectTmp) || (rectF = this.anchorBoxRectCache.get(Integer.valueOf(i))) == null) {
            return null;
        }
        float[] fArr = this.anchorBoxRectTmp;
        float f = fArr[0];
        float f2 = fArr[1];
        rectF.set(f, f2, fArr[2] + f, fArr[3] + f2);
        return rectF;
    }

    private RectF getAnchorRowRect(int i) {
        int width;
        int height;
        RectF rectF;
        RankWarningAnchor freshAnchor = getFreshAnchor(i);
        if (freshAnchor == null) {
            return null;
        }
        RenderedAnchor renderedAnchor = this.renderedRankAnchors.get(Integer.valueOf(i));
        float f = renderedAnchor != null ? renderedAnchor.x : freshAnchor.x;
        float f2 = renderedAnchor != null ? renderedAnchor.y : freshAnchor.y;
        float f3 = renderedAnchor != null ? renderedAnchor.width : freshAnchor.width;
        float f4 = renderedAnchor != null ? renderedAnchor.height : freshAnchor.height;
        if (this.autoAnchorMode) {
            width = this.cachedRealDisplaySize.x;
            height = this.cachedRealDisplaySize.y;
        } else {
            width = 0;
            height = 0;
        }
        if (width <= 0 || height <= 0) {
            width = getWidth();
            height = getHeight();
        }
        int i2 = width;
        int i3 = height;
        if (i2 <= 0 || i3 <= 0 || !RankWarningAnchorMath.mapNormalizedToPixels(f, f2, f3, f4, i2, i3, this.anchorPixelRectTmp) || (rectF = this.anchorRowRectCache.get(Integer.valueOf(i))) == null) {
            return null;
        }
        int[] iArr = this.anchorPixelRectTmp;
        int i4 = iArr[0];
        float f5 = this.anchorWindowOriginX;
        int i5 = iArr[1];
        float f6 = this.anchorWindowOriginY;
        rectF.set(i4 - f5, i5 - f6, (i4 + iArr[2]) - f5, (i5 + iArr[3]) - f6);
        return rectF;
    }

    private float getAnchorNameLeftPx(int i, RectF rectF) {
        RankWarningAnchor freshAnchor = getFreshAnchor(i);
        if (freshAnchor == null) {
            return rectF.left;
        }
        RenderedAnchor renderedAnchor = this.renderedRankAnchors.get(Integer.valueOf(i));
        float f = renderedAnchor != null ? renderedAnchor.nameX : freshAnchor.nameX;
        int width = this.autoAnchorMode ? this.cachedRealDisplaySize.x : 0;
        if (width <= 0) {
            width = getWidth();
        }
        if (width > 0 && Float.isFinite(f)) {
            return Math.round(f * width) - this.anchorWindowOriginX;
        }
        return rectF.left;
    }

    private boolean resolveAutomaticWarningAnchor(float[] fArr) {
        Iterator<Integer> it = this.rankAnchors.keySet().iterator();
        float fPreferredPlayerLaneX = Float.NaN;
        float fMax = 0.0f;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Integer next = it.next();
            RectF anchorRowRect = getAnchorRowRect(next.intValue());
            if (anchorRowRect != null && Float.isFinite(anchorRowRect.left) && Float.isFinite(anchorRowRect.height()) && anchorRowRect.height() > 0.0f && anchorRowRect.width() >= anchorRowRect.height()) {
                RankWarningAnchor freshAnchor = getFreshAnchor(next.intValue());
                fPreferredPlayerLaneX = RankWarningAnchorMath.preferredPlayerLaneX(fPreferredPlayerLaneX, getAnchorNameLeftPx(next.intValue(), anchorRowRect), freshAnchor != null && freshAnchor.hasMeasuredName, anchorRowRect.right - anchorRowRect.height());
                fMax = Math.max(fMax, anchorRowRect.height());
            }
        }
        if (!Float.isFinite(fPreferredPlayerLaneX) || fMax <= 0.0f) {
            return false;
        }
        fArr[0] = fPreferredPlayerLaneX;
        fArr[1] = fMax;
        return true;
    }

    private void logWarningPlacement(PlayerInfo playerInfo, RectF rectF, RectF rectF2, float f, int i, int i2) {
        long jUptimeMillis = SystemClock.uptimeMillis();
        if (jUptimeMillis - this.lastWarningPlacementDiagnosticAtMs < 3000) {
            return;
        }
        Dbg.i(TAG, "[rank-warning-layout] id=" + playerInfo.playerId + " rank=" + playerInfo.rank + " auto=" + this.autoAnchorMode + " hideBoxes=" + this.hideBoxes + " row=" + rectF + " avatar=" + rectF2 + " warningRight=" + f + " card=" + i + "x" + i2 + " windowOrigin=" + this.anchorWindowOriginX + "," + this.anchorWindowOriginY);
        this.lastWarningPlacementDiagnosticAtMs = jUptimeMillis;
    }

    private void refreshDisplaySizeCacheForFrame() {
        this.anchorWindowOriginX = 0.0f;
        this.anchorWindowOriginY = 0.0f;
        if (this.autoAnchorMode && isAttachedToWindow() && isLaidOut()) {
            getLocationOnScreen(this.anchorWindowLocationOnScreen);
            int[] iArr = this.anchorWindowLocationOnScreen;
            this.anchorWindowOriginX = iArr[0];
            this.anchorWindowOriginY = iArr[1];
        }
        if (this.autoAnchorMode && this.host.windowManager() != null) {
            try {
                Display displayResolveTrustedVisualTargetDisplay = this.host.resolveTrustedVisualTargetDisplay();
                if (displayResolveTrustedVisualTargetDisplay == null) {
                    displayResolveTrustedVisualTargetDisplay = this.host.windowManager().getDefaultDisplay();
                }
                displayResolveTrustedVisualTargetDisplay.getRealSize(this.cachedRealDisplaySize);
                return;
            } catch (Exception unused) {
            }
        }
        this.cachedRealDisplaySize.set(0, 0);
    }

    private boolean advanceRenderedAnchors(long j) {
        refreshAnchorMode();
        if (!this.autoAnchorMode || this.rankAnchors.isEmpty()) {
            this.lastAnchorRenderFrameMs = 0L;
            return false;
        }
        long j2 = this.lastAnchorRenderFrameMs;
        if (j2 <= 0) {
            this.lastAnchorRenderFrameMs = j;
            return hasUnconvergedRenderedAnchors();
        }
        long jMax = Math.max(0L, j - j2);
        this.lastAnchorRenderFrameMs = j;
        float fSmoothingAlpha = RankWarningAnchorMath.smoothingAlpha(jMax, RANK_ANCHOR_SMOOTH_TAU_MS, RANK_ANCHOR_SMOOTH_MAX_DT_MS);
        boolean zResolveAnchorPixelSize = resolveAnchorPixelSize(this.anchorPixelSizeTmp);
        int i = this.anchorPixelSizeTmp.x;
        int i2 = this.anchorPixelSizeTmp.y;
        for (Map.Entry<Integer, RankWarningAnchor> entry : this.rankAnchors.entrySet()) {
            RenderedAnchor renderedAnchor = this.renderedRankAnchors.get(entry.getKey());
            RankWarningAnchor value = entry.getValue();
            if (renderedAnchor == null) {
                this.renderedRankAnchors.put(entry.getKey(), new RenderedAnchor(value));
            } else {
                renderedAnchor.x = RankWarningAnchorMath.lerp(renderedAnchor.x, value.x, fSmoothingAlpha);
                renderedAnchor.y = RankWarningAnchorMath.lerp(renderedAnchor.y, value.y, fSmoothingAlpha);
                renderedAnchor.width = RankWarningAnchorMath.lerp(renderedAnchor.width, value.width, fSmoothingAlpha);
                renderedAnchor.height = RankWarningAnchorMath.lerp(renderedAnchor.height, value.height, fSmoothingAlpha);
                renderedAnchor.nameX = RankWarningAnchorMath.lerp(renderedAnchor.nameX, value.nameX, fSmoothingAlpha);
                renderedAnchor.snapCloseComponents(value);
                if (zResolveAnchorPixelSize && RankWarningAnchorMath.samePixelRect(renderedAnchor.x, renderedAnchor.y, renderedAnchor.width, renderedAnchor.height, value.x, value.y, value.width, value.height, i, i2)) {
                    renderedAnchor.snapTo(value);
                }
            }
        }
        return hasUnconvergedRenderedAnchors();
    }

    private boolean hasUnconvergedRenderedAnchors() {
        for (Map.Entry<Integer, RankWarningAnchor> entry : this.rankAnchors.entrySet()) {
            RenderedAnchor renderedAnchor = this.renderedRankAnchors.get(entry.getKey());
            if (renderedAnchor == null || !renderedAnchor.matches(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean resolveAnchorPixelSize(Point point) {
        int width;
        int height;
        if (this.autoAnchorMode) {
            width = this.cachedRealDisplaySize.x;
            height = this.cachedRealDisplaySize.y;
        } else {
            width = 0;
            height = 0;
        }
        if (width <= 0 || height <= 0) {
            width = getWidth();
            height = getHeight();
        }
        point.x = width;
        point.y = height;
        return width > 0 && height > 0;
    }

    private final class RenderedAnchor {
        float height;
        float nameX;
        float width;
        float x;
        float y;

        RenderedAnchor(RankWarningAnchor rankWarningAnchor) {
            snapTo(rankWarningAnchor);
        }

        void snapTo(RankWarningAnchor rankWarningAnchor) {
            this.x = rankWarningAnchor.x;
            this.y = rankWarningAnchor.y;
            this.width = rankWarningAnchor.width;
            this.height = rankWarningAnchor.height;
            this.nameX = rankWarningAnchor.nameX;
        }

        void snapCloseComponents(RankWarningAnchor rankWarningAnchor) {
            if (Math.abs(this.x - rankWarningAnchor.x) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON) {
                this.x = rankWarningAnchor.x;
            }
            if (Math.abs(this.y - rankWarningAnchor.y) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON) {
                this.y = rankWarningAnchor.y;
            }
            if (Math.abs(this.width - rankWarningAnchor.width) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON) {
                this.width = rankWarningAnchor.width;
            }
            if (Math.abs(this.height - rankWarningAnchor.height) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON) {
                this.height = rankWarningAnchor.height;
            }
            if (Math.abs(this.nameX - rankWarningAnchor.nameX) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON) {
                this.nameX = rankWarningAnchor.nameX;
            }
        }

        boolean matches(RankWarningAnchor rankWarningAnchor) {
            return Math.abs(this.x - rankWarningAnchor.x) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON && Math.abs(this.y - rankWarningAnchor.y) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON && Math.abs(this.width - rankWarningAnchor.width) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON && Math.abs(this.height - rankWarningAnchor.height) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON && Math.abs(this.nameX - rankWarningAnchor.nameX) <= RankWarningView.RANK_ANCHOR_SMOOTH_EPSILON;
        }
    }

    public void setOpponentId(int i) {
        if (this.opponentPlayerId == i) {
            return;
        }
        this.opponentPlayerId = i;
        invalidate();
    }

    public int getOpponentId() {
        return this.opponentPlayerId;
    }

    public void setShowOpponent(boolean z) {
        if (this.showOpponent == z) {
            return;
        }
        this.showOpponent = z;
        Dbg.i(TAG, "[attack-overlay] opponent-anchor=" + z + " opponentId=" + this.opponentPlayerId);
        invalidate();
    }

    public void setAdjustMode(boolean z) {
        this.isAdjustMode = z;
        invalidate();
    }

    public void setManualAnchorOverride(boolean z) {
        this.manualAnchorOverride = z;
        refreshAnchorState();
        requestLayout();
        invalidate();
    }

    public void setDebugMode(boolean z) {
        this.debugMode = z;
        refreshAnchorState();
        requestLayout();
        invalidate();
    }

    public void setFloatingEconomyVisible(boolean z) {
        if (this.floatingEconomyVisible == z) {
            return;
        }
        this.floatingEconomyVisible = z;
        invalidate();
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public void setAttackIconMode(boolean z) {
        if (this.attackIconMode == z) {
            return;
        }
        this.attackIconMode = z;
        Dbg.i(TAG, "[attack-overlay] enabled=" + z + " opponentAnchor=" + this.showOpponent);
        invalidate();
    }

    public void setInfoOffsetPx(int i) {
        if (this.infoOffsetPx == i) {
            return;
        }
        this.infoOffsetPx = i;
        invalidate();
    }

    public void setAttackIconOffsetPx(int i) {
        if (this.attackIconOffsetPx == i) {
            return;
        }
        this.attackIconOffsetPx = i;
        invalidate();
    }

    public void setAttackIconOffsetYPx(int i) {
        if (this.attackIconOffsetYPx == i) {
            return;
        }
        this.attackIconOffsetYPx = i;
        invalidate();
    }

    public void setAttackIconScale(float f) {
        if (Math.abs(this.attackIconScale - f) < 0.01f) {
            return;
        }
        this.attackIconScale = f;
        invalidate();
    }

    private float attackReticleScaleForDisplay() {
        return RankWarningPositionMath.attackReticleScale(getResources().getConfiguration().smallestScreenWidthDp, this.host.isEmulator());
    }

    public void setHideBoxes(boolean z) {
        this.hideBoxes = z;
        refreshAnchorState();
        this.heroBitmapCache.clear();
        this.loadingHeroIds.clear();
        if (this.cachedBoardData != null) {
            recalcWarnings();
        }
        requestLayout();
        invalidate();
    }

    public void setHeroSizeScale(float f) {
        this.heroSizeScale = Math.max(0.5f, Math.min(2.0f, f));
        this.heroBitmapCache.clear();
        this.loadingHeroIds.clear();
        if (this.cachedBoardData != null) {
            recalcWarnings();
        }
        requestLayout();
        invalidate();
    }

    public void setThreshold(int i) {
        this.warningThreshold = i;
        if (this.cachedBoardData != null) {
            recalcWarnings();
            requestLayout();
            invalidate();
        }
    }

    public void setCostFilter(int i) {
        this.costFilterBits = i & 31;
        if (this.cachedBoardData != null) {
            recalcWarnings();
        }
        requestLayout();
        invalidate();
    }

    private int getHeroCost(int i) {
        int iAbs = Math.abs(i);
        while (iAbs >= 100) {
            iAbs /= 10;
        }
        int i2 = iAbs % 10;
        if (i2 < 1 || i2 > 5) {
            try {
                HeroIdMapper.HeroInfo heroInfo = HeroIdMapper.getHeroInfo(i, this.host.currentSeason());
                if (heroInfo != null) {
                    return heroInfo.cost;
                }
            } catch (Exception unused) {
            }
        }
        return i2;
    }

    private boolean isCostEnabled(int i) {
        return i >= 1 && i <= 5 && (this.costFilterBits & (1 << (i - 1))) != 0;
    }

    public void syncCostFilterFromPrefs() {
        if (this.host.preferences() != null) {
            this.costFilterBits = this.host.preferences().getInt(FloatingWindowPrefsKeys.KEY_RANK_COST_FILTER, this.costFilterBits) & 31;
        }
    }

    private List<HeroWarningInfo> getDisplayWarnings(int i) {
        if (RankWarningSelfFilter.isSelf(i, this.rankedPlayers, this.cachedBoardData)) {
            return Collections.emptyList();
        }
        List<HeroWarningInfo> list = this.playerWarnings.get(Integer.valueOf(i));
        return list == null ? Collections.emptyList() : list;
    }

    private int maxWarningCount() {
        Iterator<PlayerInfo> it = this.rankedPlayers.iterator();
        int iMax = 0;
        while (it.hasNext()) {
            iMax = Math.max(iMax, getDisplayWarnings(it.next().playerId).size());
        }
        return iMax;
    }

    private int warningSlotCount() {
        return maxWarningCount();
    }

    private boolean isValidOpponentForEconomyEmphasis(PlayerInfo playerInfo) {
        return playerInfo != null && RankWarningPositionMath.isEconomyEmphasisCandidate(playerInfo.isMe, playerInfo.isPartner, playerInfo.hp);
    }

    private int maximumOpponentGold() {
        int iMax = -1;
        for (PlayerInfo playerInfo : this.rankedPlayers) {
            if (isValidOpponentForEconomyEmphasis(playerInfo)) {
                iMax = Math.max(iMax, playerInfo.gold);
            }
        }
        return iMax;
    }

    private int maximumOpponentLevel() {
        int iMax = -1;
        for (PlayerInfo playerInfo : this.rankedPlayers) {
            if (isValidOpponentForEconomyEmphasis(playerInfo)) {
                iMax = Math.max(iMax, playerInfo.level);
            }
        }
        return iMax;
    }

    private boolean shouldEmphasizeEconomy(PlayerInfo playerInfo, int i, int i2) {
        return playerInfo != null && RankWarningPositionMath.isEconomyEmphasisCandidate(playerInfo.isMe, playerInfo.isPartner, playerInfo.hp) && RankWarningPositionMath.shouldEmphasizeEconomy(playerInfo.isMe, playerInfo.isPartner, playerInfo.gold, playerInfo.level, i, i2);
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x00d5  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00f4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void refreshReticleDisplayGeometry(android.graphics.Canvas r13) {
        /*
            Method dump skipped, instructions count: 369
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.RankWarningView.refreshReticleDisplayGeometry(android.graphics.Canvas):void");
    }

    private RankWarningRenderText.PlayerText renderTextFor(PlayerInfo playerInfo) {
        RankWarningRenderText.PlayerText playerText = this.playerRenderTextCache.get(playerInfo);
        if (playerText != null && playerText.matches(playerInfo)) {
            return playerText;
        }
        RankWarningRenderText.PlayerText playerTextForPlayer = RankWarningRenderText.forPlayer(playerInfo);
        this.playerRenderTextCache.put(playerInfo, playerTextForPlayer);
        return playerTextForPlayer;
    }

    private void rebuildPlayerRenderTextCache() {
        this.playerRenderTextCache.clear();
        for (PlayerInfo playerInfo : this.rankedPlayers) {
            this.playerRenderTextCache.put(playerInfo, RankWarningRenderText.forPlayer(playerInfo));
        }
    }

    private float measureRowInfoWidth(PlayerInfo playerInfo, float f, int i, int i2) {
        if (playerInfo == null) {
            return 0.0f;
        }
        RankWarningRenderText.PlayerText playerTextRenderTextFor = renderTextFor(playerInfo);
        float textSize = this.debugInfoPaint.getTextSize();
        float fEconomyEmphasisScale = RankWarningPositionMath.economyEmphasisScale(shouldEmphasizeEconomy(playerInfo, i, i2));
        this.debugInfoPaint.setTextSize(RankWarningPositionMath.warningGoldTextSize(f, this.host.rankInfoSizePercent()) * fEconomyEmphasisScale);
        float fMeasureText = this.debugInfoPaint.measureText(playerTextRenderTextFor.goldPlain);
        this.debugInfoPaint.setTextSize(RankWarningPositionMath.warningLevelTextSize(f, this.host.rankInfoSizePercent()) * fEconomyEmphasisScale);
        float fMax = Math.max(fMeasureText, this.debugInfoPaint.measureText(playerTextRenderTextFor.levelWithDotPrefix));
        this.debugInfoPaint.setTextSize(textSize);
        return fMax;
    }

    private float measureWarningInfoColumnWidth(float f, int i, int i2) {
        float textSize = this.debugInfoPaint.getTextSize();
        float fMax = 0.0f;
        for (PlayerInfo playerInfo : this.rankedPlayers) {
            RankWarningRenderText.PlayerText playerTextRenderTextFor = renderTextFor(playerInfo);
            float fEconomyEmphasisScale = RankWarningPositionMath.economyEmphasisScale(shouldEmphasizeEconomy(playerInfo, i, i2));
            this.debugInfoPaint.setTextSize(RankWarningPositionMath.warningGoldTextSize(f, this.host.rankInfoSizePercent()) * fEconomyEmphasisScale);
            float fMax2 = Math.max(fMax, this.debugInfoPaint.measureText(playerTextRenderTextFor.goldPlain));
            this.debugInfoPaint.setTextSize(RankWarningPositionMath.warningLevelTextSize(f, this.host.rankInfoSizePercent()) * fEconomyEmphasisScale);
            fMax = Math.max(fMax2, this.debugInfoPaint.measureText(playerTextRenderTextFor.levelWithDotPrefix));
        }
        this.debugInfoPaint.setTextSize(textSize);
        return fMax;
    }

    private void recalcTargetPositions() {
        for (int i = 0; i < this.rankedPlayers.size(); i++) {
            PlayerInfo playerInfo = this.rankedPlayers.get(i);
            this.targetYMap.put(Integer.valueOf(playerInfo.playerId), Float.valueOf(RankWarningPositionMath.topForRank(playerInfo.rank, i, this.boxHeightPx, this.spacingPx)));
        }
    }

    public void resetPlayerBindings() {
        this.knownPlayerIds.clear();
        this.currentYMap.clear();
        this.targetYMap.clear();
        this.rankedPlayers.clear();
        this.playerRenderTextCache.clear();
        this.playerWarnings.clear();
        this.cachedBoardData = null;
        this.heroPoolIndex = Collections.emptyMap();
        this.heroPoolTimestamp = Integer.MIN_VALUE;
        this.rankAnchors.clear();
        this.renderedRankAnchors.clear();
        this.anchorRowRectCache.clear();
        this.anchorBoxRectCache.clear();
        this.rankAnchorsReceivedAtMs = 0L;
        this.lastAnchorRenderFrameMs = 0L;
        this.autoAnchorMode = false;
        ValueAnimator valueAnimator = this.positionAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.positionAnimator.cancel();
        }
        requestLayout();
        invalidate();
    }

    public void setInfoSizeScale(float f) {
        ValueAnimator valueAnimator = this.infoSizeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.infoSizeAnimator = null;
        }
        applyInfoSizeScale(f);
    }

    public void animateInfoSizeScale(float f) {
        float fMax = Math.max(0.2f, Math.min(1.2f, f));
        if (!this.host.systemAnimationsEnabled()) {
            setInfoSizeScale(fMax);
            return;
        }
        ValueAnimator valueAnimator = this.infoSizeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.infoSizeScale, fMax);
        this.infoSizeAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(120L);
        this.infoSizeAnimator.setInterpolator(new DecelerateInterpolator(1.8f));
        this.infoSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.support.RankWarningView$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.m1413x8a2fb8c(valueAnimator2);
            }
        });
        this.infoSizeAnimator.start();
    }

    /* renamed from: lambda$animateInfoSizeScale$0$com-android-support-RankWarningView, reason: not valid java name */
    /* synthetic */ void m1413x8a2fb8c(ValueAnimator valueAnimator) {
        this.infoSizeScale = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        postInvalidateOnAnimation();
    }

    private void applyInfoSizeScale(float f) {
        float fMax = Math.max(0.2f, Math.min(1.2f, f));
        if (Math.abs(this.infoSizeScale - fMax) < 0.001f) {
            return;
        }
        this.infoSizeScale = fMax;
        postInvalidateOnAnimation();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        ValueAnimator valueAnimator = this.infoSizeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.infoSizeAnimator = null;
        }
        super.onDetachedFromWindow();
    }

    public void updatePlayers(List<PlayerInfo> list) {
        if (list == null || list.isEmpty()) {
            this.rankedPlayers.clear();
            this.playerRenderTextCache.clear();
            invalidate();
            return;
        }
        ArrayList arrayList = new ArrayList(list);
        Collections.sort(arrayList, new Comparator() { // from class: com.android.support.RankWarningView$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return RankWarningView.lambda$updatePlayers$1((PlayerInfo) obj, (PlayerInfo) obj2);
            }
        });
        HashSet hashSet = new HashSet();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            hashSet.add(Integer.valueOf(((PlayerInfo) it.next()).playerId));
        }
        Iterator it2 = hashSet.iterator();
        boolean z = false;
        int i = 0;
        while (it2.hasNext()) {
            if (!this.knownPlayerIds.contains(Integer.valueOf(((Integer) it2.next()).intValue()))) {
                i++;
            }
        }
        if (!this.knownPlayerIds.isEmpty() && i > this.knownPlayerIds.size() / 2) {
            resetPlayerBindings();
        }
        this.knownPlayerIds.addAll(hashSet);
        HashSet hashSet2 = new HashSet();
        Iterator<Integer> it3 = this.knownPlayerIds.iterator();
        while (it3.hasNext()) {
            int iIntValue = it3.next().intValue();
            if (!hashSet.contains(Integer.valueOf(iIntValue))) {
                hashSet2.add(Integer.valueOf(iIntValue));
            }
        }
        Iterator it4 = hashSet2.iterator();
        while (it4.hasNext()) {
            int iIntValue2 = ((Integer) it4.next()).intValue();
            this.knownPlayerIds.remove(Integer.valueOf(iIntValue2));
            this.currentYMap.remove(Integer.valueOf(iIntValue2));
            this.targetYMap.remove(Integer.valueOf(iIntValue2));
            this.playerWarnings.remove(Integer.valueOf(iIntValue2));
        }
        HashMap map = new HashMap();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            PlayerInfo playerInfo = (PlayerInfo) arrayList.get(i2);
            map.put(Integer.valueOf(playerInfo.playerId), Float.valueOf(RankWarningPositionMath.topForRank(playerInfo.rank, i2, this.boxHeightPx, this.spacingPx)));
        }
        for (Map.Entry entry : map.entrySet()) {
            if (!this.currentYMap.containsKey(entry.getKey())) {
                this.currentYMap.put((Integer) entry.getKey(), (Float) entry.getValue());
            }
        }
        for (Map.Entry entry2 : map.entrySet()) {
            Float f = this.targetYMap.get(entry2.getKey());
            if (f == null || Math.abs(f.floatValue() - ((Float) entry2.getValue()).floatValue()) > 0.5f) {
                z = true;
                break;
            }
        }
        this.targetYMap = map;
        this.rankedPlayers.clear();
        this.rankedPlayers.addAll(arrayList);
        rebuildPlayerRenderTextCache();
        if (z) {
            animateToTargetPositions();
        } else {
            invalidate();
        }
    }

    static /* synthetic */ int lambda$updatePlayers$1(PlayerInfo playerInfo, PlayerInfo playerInfo2) {
        return playerInfo.rank - playerInfo2.rank;
    }

    private void animateToTargetPositions() {
        ValueAnimator valueAnimator = this.positionAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.positionAnimator.cancel();
        }
        final HashMap map = new HashMap(this.currentYMap);
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.positionAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(350L);
        this.positionAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        this.positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.support.RankWarningView$$ExternalSyntheticLambda3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.m1414x5bc2e371(map, valueAnimator2);
            }
        });
        this.positionAnimator.start();
    }

    /* renamed from: lambda$animateToTargetPositions$2$com-android-support-RankWarningView, reason: not valid java name */
    /* synthetic */ void m1414x5bc2e371(Map map, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (Map.Entry<Integer, Float> entry : this.targetYMap.entrySet()) {
            int iIntValue = entry.getKey().intValue();
            float fFloatValue2 = entry.getValue().floatValue();
            Float fValueOf = (Float) map.get(Integer.valueOf(iIntValue));
            if (fValueOf == null) {
                fValueOf = Float.valueOf(fFloatValue2);
            }
            this.currentYMap.put(Integer.valueOf(iIntValue), Float.valueOf(fValueOf.floatValue() + ((fFloatValue2 - fValueOf.floatValue()) * fFloatValue)));
        }
        invalidate();
    }

    public void updateWarningData(List<BoardPositionInfo> list) {
        this.cachedBoardData = list;
        recalcWarnings();
        requestLayout();
        invalidate();
    }

    public void updateHeroPoolData(List<HeroData> list, List<HeroData> list2, int i) {
        Map<Integer, RankWarningPoolIndex.Entry> mapBuild = RankWarningPoolIndex.build(list, list2);
        if (mapBuild.isEmpty()) {
            mapBuild = Collections.emptyMap();
        }
        this.heroPoolIndex = mapBuild;
        this.heroPoolTimestamp = i;
        for (List<HeroWarningInfo> list3 : this.playerWarnings.values()) {
            if (list3 != null) {
                Iterator<HeroWarningInfo> it = list3.iterator();
                while (it.hasNext()) {
                    applyPoolCount(it.next());
                }
                Collections.sort(list3, new RankWarningView$$ExternalSyntheticLambda0());
            }
        }
        invalidate();
    }

    private void recalcWarnings() {
        this.playerWarnings.clear();
        if (this.cachedBoardData == null) {
            return;
        }
        Set<Integer> set = this.deadPlayerIdsScratch;
        set.clear();
        for (PlayerInfo playerInfo : this.rankedPlayers) {
            if (playerInfo != null && playerInfo.hp == 0) {
                set.add(Integer.valueOf(playerInfo.playerId));
            }
        }
        int size = 0;
        for (BoardPositionInfo boardPositionInfo : this.cachedBoardData) {
            if (boardPositionInfo != null && !RankWarningSelfFilter.isSelf(boardPositionInfo.playerId, this.rankedPlayers, this.cachedBoardData) && !set.contains(Integer.valueOf(boardPositionInfo.playerId))) {
                Map<Integer, Integer> map = this.heroCountsScratch;
                map.clear();
                for (int i = 0; i < 28; i++) {
                    int i2 = boardPositionInfo.boardHeroIds[i];
                    if (i2 > 0) {
                        int baseHeroId = getBaseHeroId(i2);
                        int heroCopies = getHeroCopies(i2);
                        Integer num = map.get(Integer.valueOf(baseHeroId));
                        map.put(Integer.valueOf(baseHeroId), Integer.valueOf((num != null ? num.intValue() : 0) + heroCopies));
                    }
                }
                for (int i3 = 0; i3 < 9; i3++) {
                    int i4 = boardPositionInfo.benchHeroIds[i3];
                    if (i4 > 0) {
                        int baseHeroId2 = getBaseHeroId(i4);
                        int heroCopies2 = getHeroCopies(i4);
                        Integer num2 = map.get(Integer.valueOf(baseHeroId2));
                        map.put(Integer.valueOf(baseHeroId2), Integer.valueOf((num2 != null ? num2.intValue() : 0) + heroCopies2));
                    }
                }
                ArrayList arrayList = new ArrayList();
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    if (entry.getValue().intValue() >= this.warningThreshold) {
                        int heroCost = getHeroCost(entry.getKey().intValue());
                        if (isCostEnabled(heroCost)) {
                            HeroWarningInfo heroWarningInfo = new HeroWarningInfo();
                            heroWarningInfo.baseHeroId = entry.getKey().intValue();
                            heroWarningInfo.setHeroCost(heroCost);
                            heroWarningInfo.setTotalCopies(entry.getValue().intValue());
                            applyPoolCount(heroWarningInfo);
                            arrayList.add(heroWarningInfo);
                            if (!this.heroBitmapCache.containsKey(entry.getKey()) && !this.loadingHeroIds.contains(entry.getKey())) {
                                loadHeroBitmap(entry.getKey().intValue());
                            }
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    Collections.sort(arrayList, new RankWarningView$$ExternalSyntheticLambda0());
                    this.playerWarnings.put(Integer.valueOf(boardPositionInfo.playerId), arrayList);
                    size += arrayList.size();
                }
            }
        }
        int i5 = this.warningDiagCount + 1;
        this.warningDiagCount = i5;
        if (size > 0 && (i5 < 5 || i5 % 20 == 0)) {
            Dbg.i(TAG, "[diag][warn] #" + this.warningDiagCount + " season=" + this.host.seasonNames()[this.host.currentSeason()] + " threshold=" + this.warningThreshold + " poolTs=" + this.heroPoolTimestamp + " hits=" + size + " " + describeWarningHits());
        } else if (size == 0 && this.cachedBoardData != null && i5 % 60 == 0) {
            Dbg.i(TAG, "[diag][warn] #" + this.warningDiagCount + " season=" + this.host.seasonNames()[this.host.currentSeason()] + " threshold=" + this.warningThreshold + " boards=" + this.cachedBoardData.size() + " hits=0（未达到阈值 或 无对局数据）");
        }
    }

    private String describeWarningHits() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, List<HeroWarningInfo>> entry : this.playerWarnings.entrySet()) {
            for (HeroWarningInfo heroWarningInfo : entry.getValue()) {
                String englishNameById = HeroIdMapper.getEnglishNameById(heroWarningInfo.baseHeroId, this.host.currentSeason());
                StringBuilder sbAppend = sb.append("p").append(entry.getKey()).append(":").append(heroWarningInfo.baseHeroId).append("x").append(heroWarningInfo.totalCopies).append("(");
                if (englishNameById == null) {
                    englishNameById = "null";
                }
                sbAppend.append(englishNameById).append(") ");
            }
        }
        return sb.toString();
    }

    private void applyPoolCount(HeroWarningInfo heroWarningInfo) {
        if (heroWarningInfo == null) {
            return;
        }
        RankWarningPoolIndex.Entry entry = this.heroPoolIndex.get(Integer.valueOf(heroWarningInfo.baseHeroId));
        if (entry == null) {
            heroWarningInfo.clearPoolCounts();
        } else {
            heroWarningInfo.setPoolCounts(entry.remaining, entry.total);
        }
    }

    private int getBaseHeroId(int i) {
        return RankWarningPoolIndex.baseHeroId(i);
    }

    private void loadHeroBitmap(int i) {
        String englishNameById = HeroIdMapper.getEnglishNameById(i, this.host.currentSeason());
        if (englishNameById == null || englishNameById.isEmpty()) {
            return;
        }
        this.loadingHeroIds.add(Integer.valueOf(i));
        tryLoadWarningBitmap(i, this.host.buildHeroImageUris(englishNameById.toLowerCase()), 0);
    }

    public void clearBitmapCache() {
        this.heroBitmapCache.clear();
        this.loadingHeroIds.clear();
        postInvalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryLoadWarningBitmap(final int i, final String[] strArr, final int i2) {
        if (i2 >= strArr.length) {
            this.loadingHeroIds.remove(Integer.valueOf(i));
            return;
        }
        try {
            Glide.with(getContext().getApplicationContext()).asBitmap().load(strArr[i2]).override(512, 512).downsample(DownsampleStrategy.AT_MOST).format(DecodeFormat.PREFER_ARGB_8888).disallowHardwareConfig().dontTransform().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into((RequestBuilder) new CustomTarget<Bitmap>() { // from class: com.android.support.RankWarningView.1
                @Override // com.bumptech.glide.request.target.Target
                public void onLoadCleared(Drawable drawable) {
                }

                @Override // com.bumptech.glide.request.target.Target
                public /* bridge */ /* synthetic */ void onResourceReady(Object obj, Transition transition) {
                    onResourceReady((Bitmap) obj, (Transition<? super Bitmap>) transition);
                }

                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    RankWarningView.this.heroBitmapCache.put(Integer.valueOf(i), bitmap);
                    RankWarningView.this.loadingHeroIds.remove(Integer.valueOf(i));
                    RankWarningView.this.postInvalidate();
                }

                @Override // com.bumptech.glide.request.target.CustomTarget, com.bumptech.glide.request.target.Target
                public void onLoadFailed(Drawable drawable) {
                    RankWarningView.this.tryLoadWarningBitmap(i, strArr, i2 + 1);
                }
            });
        } catch (Exception unused) {
            this.loadingHeroIds.remove(Integer.valueOf(i));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:54:0x02bc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawWarningCard(android.graphics.Canvas r25, com.android.support.HeroWarningInfo r26, float r27, float r28, float r29, float r30, boolean r31) {
        /*
            Method dump skipped, instructions count: 921
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.RankWarningView.drawWarningCard(android.graphics.Canvas, com.android.support.HeroWarningInfo, float, float, float, float, boolean):void");
    }

    private void fitTextSize(Paint paint, String str, float f, float f2) {
        paint.setTextSize(f);
        float fMeasureText = paint.measureText(str);
        if (fMeasureText <= f2 || fMeasureText <= 0.0f) {
            return;
        }
        paint.setTextSize(Math.max(3.0f, (f * f2) / fMeasureText));
    }

    private void drawCenteredText(Canvas canvas, String str, float f, float f2, Paint paint) {
        paint.getFontMetrics(this.tmpFontMetrics);
        canvas.drawText(str, f, f2 - ((this.tmpFontMetrics.ascent + this.tmpFontMetrics.descent) / 2.0f), paint);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        refreshAnchorMode();
        if (this.autoAnchorMode) {
            Point point = new Point();
            if (this.host.windowManager() != null) {
                try {
                    this.host.windowManager().getDefaultDisplay().getRealSize(point);
                } catch (Exception unused) {
                }
            }
            if (point.x <= 0 || point.y <= 0) {
                point.x = View.MeasureSpec.getSize(i);
                point.y = View.MeasureSpec.getSize(i2);
            }
            if (point.x > 0 && point.y > 0) {
                setMeasuredDimension(point.x, point.y);
                return;
            }
        }
        if (this.debugMode) {
            int i3 = this.boxHeightPx;
            setMeasuredDimension((warningSlotCount() * (RankWarningPositionMath.warningCardWidth((int) (Math.max((int) (i3 * 0.8f), i3 / 2) * this.heroSizeScale)) + this.heroGapPx)) + Math.round(warningInfoLaneWidth()) + this.boxWidthPx + ((int) (this.boxWidthPx * 2.2f)), (i3 * 8) + (7 * this.spacingPx) + ((int) (i3 * 0.8f)));
            return;
        }
        if (this.hideBoxes) {
            Iterator<PlayerInfo> it = this.rankedPlayers.iterator();
            int i4 = 0;
            int iMax = 0;
            while (it.hasNext()) {
                List<HeroWarningInfo> displayWarnings = getDisplayWarnings(it.next().playerId);
                if (displayWarnings != null && !displayWarnings.isEmpty()) {
                    i4++;
                    iMax = Math.max(iMax, displayWarnings.size());
                }
            }
            if (i4 == 0) {
                setMeasuredDimension(0, 0);
                return;
            } else {
                int i5 = (int) (this.boxHeightPx * 1.4f * this.heroSizeScale);
                setMeasuredDimension(iMax * (RankWarningPositionMath.warningCardWidth(i5) + this.heroGapPx), ((i5 + ((int) this.countPaint.getTextSize()) + 8) * i4) + ((i4 - 1) * this.spacingPx));
                return;
            }
        }
        int i6 = (this.boxHeightPx * 8) + (7 * this.spacingPx);
        int iWarningCardWidth = RankWarningPositionMath.warningCardWidth((int) (Math.max((int) (r5 * 0.8f), r5 / 2) * this.heroSizeScale));
        int iWarningSlotCount = warningSlotCount();
        setMeasuredDimension((iWarningSlotCount > 0 ? iWarningSlotCount * (iWarningCardWidth + this.heroGapPx) : 0) + Math.round(warningInfoLaneWidth()) + this.boxWidthPx, i6);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        boolean z;
        boolean zIsEmpty;
        super.onDraw(canvas);
        refreshDisplaySizeCacheForFrame();
        boolean zAdvanceRenderedAnchors = advanceRenderedAnchors(SystemClock.uptimeMillis());
        try {
            if (this.debugMode) {
                drawDebugMode(canvas);
                if (zAdvanceRenderedAnchors) {
                    if (!z) {
                        if (zIsEmpty) {
                            return;
                        }
                    }
                    return;
                }
                return;
            }
            if (this.rankedPlayers.isEmpty()) {
                if (zAdvanceRenderedAnchors) {
                    if (this.debugMode || !this.rankedPlayers.isEmpty()) {
                        scheduleAnchorFrame();
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.hideBoxes) {
                drawIndependentWarnings(canvas);
            } else {
                drawBoxMode(canvas);
            }
            if (zAdvanceRenderedAnchors) {
                if (this.debugMode || !this.rankedPlayers.isEmpty()) {
                    scheduleAnchorFrame();
                }
            }
        } finally {
            if (zAdvanceRenderedAnchors && (this.debugMode || !this.rankedPlayers.isEmpty())) {
                scheduleAnchorFrame();
            }
        }
    }

    private void scheduleAnchorFrame() {
        long jUptimeMillis = SystemClock.uptimeMillis();
        long j = jUptimeMillis - this.lastAnchorPostMs;
        if (j >= RANK_ANCHOR_MIN_FRAME_MS) {
            this.lastAnchorPostMs = jUptimeMillis;
            postInvalidateOnAnimation();
        } else {
            postInvalidateDelayed(RANK_ANCHOR_MIN_FRAME_MS - j);
        }
    }

    private void drawAttackReticle(Canvas canvas, float f, float f2, float f3) {
        int fightColor = this.host.hudAppearance() != null ? this.host.hudAppearance().getFightColor() : GameHudPalette.ALERT_RED;
        float fAttackReticleHalfExtent = RankWarningPositionMath.attackReticleHalfExtent(f3);
        float fAttackReticleCornerLength = RankWarningPositionMath.attackReticleCornerLength(f3);
        float fAttackReticleHaloRadius = RankWarningPositionMath.attackReticleHaloRadius(f3);
        float fAttackReticleDotRadius = RankWarningPositionMath.attackReticleDotRadius(f3);
        float fMax = Math.max(2.0f, 0.09f * f3);
        Paint paint = this.attackReticlePaint;
        float f4 = f - fAttackReticleHalfExtent;
        float f5 = f + fAttackReticleHalfExtent;
        float f6 = f2 - fAttackReticleHalfExtent;
        float f7 = f2 + fAttackReticleHalfExtent;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.clearShadowLayer();
        paint.setStrokeWidth(3.0f + fMax);
        paint.setColor(-871756264);
        drawFocusArrow(canvas, f4, f6, fAttackReticleCornerLength, fAttackReticleCornerLength, paint);
        float f8 = -fAttackReticleCornerLength;
        drawFocusArrow(canvas, f5, f6, f8, fAttackReticleCornerLength, paint);
        drawFocusArrow(canvas, f4, f7, fAttackReticleCornerLength, f8, paint);
        drawFocusArrow(canvas, f5, f7, f8, f8, paint);
        paint.setStrokeWidth(fMax);
        paint.setColor(fightColor);
        int i = fightColor & ViewCompat.MEASURED_SIZE_MASK;
        int i2 = (-872415232) | i;
        paint.setShadowLayer(0.18f * f3, 0.0f, 0.0f, i2);
        drawFocusArrow(canvas, f4, f6, fAttackReticleCornerLength, fAttackReticleCornerLength, paint);
        drawFocusArrow(canvas, f5, f6, f8, fAttackReticleCornerLength, paint);
        drawFocusArrow(canvas, f4, f7, fAttackReticleCornerLength, f8, paint);
        drawFocusArrow(canvas, f5, f7, f8, f8, paint);
        paint.clearShadowLayer();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(1426063360 | i);
        canvas.drawCircle(f, f2, fAttackReticleHaloRadius, paint);
        paint.setColor(fightColor);
        paint.setShadowLayer(0.14f * f3, 0.0f, 0.0f, i2);
        canvas.drawCircle(f, f2, fAttackReticleDotRadius, paint);
        paint.clearShadowLayer();
    }

    private void drawFocusArrow(Canvas canvas, float f, float f2, float f3, float f4, Paint paint) {
        canvas.drawLine(f, f2, f + f3, f2, paint);
        canvas.drawLine(f, f2, f, f2 + f4, paint);
    }

    private void drawAttackReticleForRow(Canvas canvas, PlayerInfo playerInfo, RectF rectF, float f, float f2, float f3, float f4, float f5) {
        float fReticleCenterY;
        float fAttackReticleSize = RankWarningPositionMath.attackReticleSize(rectF != null ? Float.valueOf(rectF.width()) : null, rectF != null ? Float.valueOf(rectF.height()) : null, Math.round(f4), this.attackIconScale, attackReticleScaleForDisplay());
        float fReticleCenterX = RankWarningPositionMath.reticleCenterX(rectF != null ? Float.valueOf(rectF.right) : null, rectF != null ? Float.valueOf(rectF.height()) : null, f, Math.round(f3), fAttackReticleSize, this.attackIconOffsetPx);
        if (rectF != null) {
            fReticleCenterY = rectF.centerY();
        } else {
            fReticleCenterY = RankWarningPositionMath.reticleCenterY(addLegacyOrigin(playerInfo != null ? this.targetYMap.get(Integer.valueOf(playerInfo.playerId)) : null, f5), f2, Math.round(f4));
        }
        float fMax = Math.max(1.0f, Math.round(this.reticleDisplayDensity * 4.0f));
        float f6 = this.reticleLocalVisibleBounds.left;
        float f7 = this.reticleLocalVisibleBounds.top;
        float f8 = this.reticleLocalVisibleBounds.right;
        float f9 = this.reticleLocalVisibleBounds.bottom;
        float fFitAttackReticleSizeToBounds = RankWarningPositionMath.fitAttackReticleSizeToBounds(fAttackReticleSize, f6, f7, f8, f9, fMax);
        float f10 = f6 + fMax;
        float fClampAttackReticleCenter = RankWarningPositionMath.clampAttackReticleCenter(fReticleCenterX, fFitAttackReticleSizeToBounds, f10, Math.max(f10, f8 - fMax));
        float f11 = f7 + fMax;
        float fClampAttackReticleCenter2 = RankWarningPositionMath.clampAttackReticleCenter(fReticleCenterY + this.attackIconOffsetYPx, fFitAttackReticleSizeToBounds, f11, Math.max(f11, f9 - fMax));
        long jUptimeMillis = SystemClock.uptimeMillis();
        if (jUptimeMillis - this.lastAttackReticleDiagnosticAtMs >= 3000) {
            Dbg.i(TAG, "[attack-overlay] draw opponent=" + (playerInfo == null ? this.opponentPlayerId : playerInfo.playerId) + " center=" + fClampAttackReticleCenter + "," + fClampAttackReticleCenter2 + " size=" + fFitAttackReticleSizeToBounds + " color=#" + HudAppearance.hex24(this.host.hudAppearance() != null ? this.host.hudAppearance().getFightColor() : GameHudPalette.ALERT_RED) + " anchored=" + (rectF != null) + " bounds=" + this.reticleLocalVisibleBounds);
            this.lastAttackReticleDiagnosticAtMs = jUptimeMillis;
        }
        drawAttackReticle(canvas, fClampAttackReticleCenter, fClampAttackReticleCenter2, fFitAttackReticleSizeToBounds);
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x0114  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x012b  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0133  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0152  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0301 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x031b  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0329  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x0380  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawBoxMode(android.graphics.Canvas r35) {
        /*
            Method dump skipped, instructions count: 1056
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.RankWarningView.drawBoxMode(android.graphics.Canvas):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0153  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawIndependentWarnings(android.graphics.Canvas r26) {
        /*
            Method dump skipped, instructions count: 413
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.RankWarningView.drawIndependentWarnings(android.graphics.Canvas):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:108:0x02f6  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x03b9  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x03d8  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x03e9  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x04fd  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x01e3  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x01eb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawDebugMode(android.graphics.Canvas r44) {
        /*
            Method dump skipped, instructions count: 1503
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.RankWarningView.drawDebugMode(android.graphics.Canvas):void");
    }
}
