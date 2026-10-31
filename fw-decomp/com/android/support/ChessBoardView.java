package com.android.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.android.support.lineup.BoardLoc;
import com.android.support.lineup.LineupActiveBus;
import com.android.support.lineup.LineupRepo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
class ChessBoardView extends View {
    private static final int BENCH_COLS = 9;
    private static final int COLS = 7;
    private static final int COL_CELL_HL = 1304946943;
    private static final int COL_EMPTY_BOTTOM = -1979051230;
    private static final int COL_EMPTY_STROKE = 1429164400;
    private static final int COL_EMPTY_TOP = -1977733564;
    private static final int COL_PLATE = -670626794;
    private static final int COL_PLATE_BORDER = 734521599;
    private static final int COL_VIGNETTE = 1711276032;
    private static final float HEX_ASPECT = 1.12f;
    private static final float HEX_GAP_RATIO = 0.94f;
    private static final int ROWS = 4;
    private static final float ROW_PITCH_RATIO = 0.78f;
    private int benchBitmask;
    private final int[] benchHeroIds;
    private boolean benchHidden;
    private Shader benchShader;
    private final Paint bitmapPaint;
    private int boardBitmask;
    private final int[] boardHeroIds;
    private String boardSummaryText;
    private float bottomExtra;
    private final Paint carryStarPaint;
    private float cellSpacing;
    private final float density;
    private final RectF dstRect;
    private final Paint emptyFill;
    private final Paint emptyStroke;
    private final Paint fallbackFill;
    private final Paint fallbackText;
    private final HashMap<Integer, Bitmap> heroBitmapCache;
    private final Path hexPath;
    private float hexRx;
    private float hexRy;
    private final Path highlightPath;
    private final Host host;
    private boolean isSelfBoard;
    private final Paint labelPaint;
    private long lastBoardDataFingerprint;
    private String lastDrawSig;
    private final HashSet<Integer> loadingHeroIds;
    private final Paint outerStroke;
    private final Paint plateBorder;
    private final Paint platePaint;
    private final Set<Integer> recommendBaseIds;
    private final Set<Integer> recommendCarryBaseIds;
    private final Paint recommendGlowPaint;
    private LineupActiveBus.Listener recommendListener;
    private final Paint recommendRingPaint;
    private final Paint rimPaint;
    private float rowPitch;
    private final Shader[] rowShaders;
    private final Matrix shaderMatrix;
    private final Rect srcRect;
    private final Paint starFill;
    private final Path starFillPath;
    private final Paint starGlow;
    private final Path starGlowPath;
    private float starPathRadius;
    private final Paint strokePaint;
    private final Paint targetCellFillPaint;
    private final Paint targetCellPaint;
    private final HashMap<Integer, Boolean> targetCellsCarry;
    private final Paint targetMarkerPaint;
    private final RectF tmpRect;
    private float topExtra;
    private float viewScale;
    private final Paint vignettePaint;
    private Shader vignetteShader;

    public interface Host {
        String[] buildHeroImageUris(String str);

        int currentSeason();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getBaseHeroId(int i) {
        return (i < 20000 || i >= 30000) ? (i < 30000 || i >= 40000) ? (i < 40000 || i >= 50000) ? i : i - 30000 : i - AccessibilityNodeInfoCompat.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_MAX_LENGTH : i - 10000;
    }

    private int getStarLevel(int i) {
        if (i >= 40000 && i < 50000) {
            return 4;
        }
        if (i < 30000 || i >= 40000) {
            return (i < 20000 || i >= 30000) ? 1 : 2;
        }
        return 3;
    }

    private long mixBoardFingerprint(long j, int i) {
        for (int i2 = 0; i2 < 32; i2 += 8) {
            j = (j ^ ((i >>> i2) & 255)) * 1099511628211L;
        }
        return j;
    }

    private int starBorderColor(int i) {
        if (i >= 4) {
            return GameHudPalette.CYAN;
        }
        if (i == 3) {
            return -22016;
        }
        return i == 2 ? -4540 : -1;
    }

    private int starTierColor(int i) {
        if (i >= 3) {
            return -15770;
        }
        if (i == 2) {
            return GameHudPalette.CYAN;
        }
        return -1511685;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public ChessBoardView(Context context, Host host) {
        super(context);
        this.boardBitmask = 0;
        this.benchBitmask = 0;
        this.boardSummaryText = "";
        this.boardHeroIds = new int[28];
        this.benchHeroIds = new int[9];
        this.benchHidden = false;
        this.lastBoardDataFingerprint = Long.MIN_VALUE;
        this.lastDrawSig = "";
        this.heroBitmapCache = new HashMap<>();
        this.loadingHeroIds = new HashSet<>();
        this.emptyFill = new Paint(1);
        this.emptyStroke = new Paint(1);
        this.bitmapPaint = new Paint(3);
        this.fallbackFill = new Paint(1);
        this.fallbackText = new Paint(1);
        this.outerStroke = new Paint(1);
        this.strokePaint = new Paint(1);
        this.rimPaint = new Paint(1);
        this.vignettePaint = new Paint(1);
        this.targetCellFillPaint = new Paint(1);
        this.targetCellPaint = new Paint(1);
        this.targetMarkerPaint = new Paint(1);
        this.recommendGlowPaint = new Paint(1);
        this.recommendRingPaint = new Paint(1);
        this.carryStarPaint = new Paint(1);
        this.starFill = new Paint(1);
        this.starGlow = new Paint(1);
        this.platePaint = new Paint(1);
        this.plateBorder = new Paint(1);
        this.labelPaint = new Paint(1);
        this.hexPath = new Path();
        this.highlightPath = new Path();
        this.starGlowPath = new Path();
        this.starFillPath = new Path();
        this.starPathRadius = -1.0f;
        this.srcRect = new Rect();
        this.dstRect = new RectF();
        this.tmpRect = new RectF();
        this.shaderMatrix = new Matrix();
        this.rowShaders = new Shader[4];
        this.recommendBaseIds = Collections.synchronizedSet(new HashSet());
        this.recommendCarryBaseIds = Collections.synchronizedSet(new HashSet());
        this.targetCellsCarry = new HashMap<>();
        this.isSelfBoard = true;
        this.viewScale = 1.0f;
        this.host = host;
        this.density = getResources().getDisplayMetrics().density;
        init();
    }

    public void setIsSelfBoard(boolean z) {
        if (this.isSelfBoard == z) {
            return;
        }
        this.isSelfBoard = z;
        postInvalidate();
    }

    public void setViewScale(float f) {
        if (Float.compare(this.viewScale, f) == 0) {
            return;
        }
        this.viewScale = f;
        this.cellSpacing = 0.0f;
        requestLayout();
        invalidate();
    }

    public void setBenchHidden(boolean z) {
        if (this.benchHidden == z) {
            return;
        }
        this.benchHidden = z;
        this.cellSpacing = 0.0f;
        requestLayout();
        invalidate();
    }

    private void init() {
        this.emptyStroke.setStyle(Paint.Style.STROKE);
        this.emptyStroke.setColor(COL_EMPTY_STROKE);
        this.emptyStroke.setStrokeWidth(dp(1.0f));
        this.fallbackFill.setStyle(Paint.Style.FILL);
        this.fallbackText.setColor(-1511685);
        this.fallbackText.setFakeBoldText(true);
        this.fallbackText.setTextAlign(Paint.Align.CENTER);
        this.outerStroke.setStyle(Paint.Style.STROKE);
        this.outerStroke.setStrokeCap(Paint.Cap.ROUND);
        this.outerStroke.setStrokeJoin(Paint.Join.ROUND);
        this.outerStroke.setColor(-1291515374);
        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setStrokeCap(Paint.Cap.ROUND);
        this.strokePaint.setStrokeJoin(Paint.Join.ROUND);
        this.rimPaint.setStyle(Paint.Style.STROKE);
        this.rimPaint.setStrokeCap(Paint.Cap.ROUND);
        this.rimPaint.setStrokeJoin(Paint.Join.ROUND);
        this.rimPaint.setColor(COL_CELL_HL);
        this.targetCellPaint.setStyle(Paint.Style.STROKE);
        this.targetCellPaint.setStrokeWidth(dp(1.0f));
        this.targetCellPaint.setColor(-15770);
        this.targetCellPaint.setPathEffect(new DashPathEffect(new float[]{dp(3.0f), dp(2.0f)}, 0.0f));
        this.targetCellFillPaint.setStyle(Paint.Style.FILL);
        this.targetCellFillPaint.setColor(872403530);
        this.targetMarkerPaint.setColor(-15770);
        this.targetMarkerPaint.setTextSize(dp(9.0f));
        this.targetMarkerPaint.setTextAlign(Paint.Align.CENTER);
        this.targetMarkerPaint.setFakeBoldText(true);
        this.recommendGlowPaint.setStyle(Paint.Style.STROKE);
        this.recommendGlowPaint.setStrokeWidth(dp(2.0f) * 2.0f);
        this.recommendGlowPaint.setColor(-872349816);
        this.recommendRingPaint.setStyle(Paint.Style.STROKE);
        this.recommendRingPaint.setStrokeWidth(dp(1.5f));
        this.recommendRingPaint.setColor(GameHudPalette.SUCCESS);
        this.carryStarPaint.setColor(-15770);
        this.carryStarPaint.setTextSize(dp(10.0f));
        this.carryStarPaint.setTextAlign(Paint.Align.CENTER);
        this.carryStarPaint.setFakeBoldText(true);
        this.starFill.setStyle(Paint.Style.FILL);
        this.starGlow.setStyle(Paint.Style.FILL);
        this.platePaint.setColor(COL_PLATE);
        this.plateBorder.setStyle(Paint.Style.STROKE);
        this.plateBorder.setColor(COL_PLATE_BORDER);
        this.plateBorder.setStrokeWidth(Math.max(1.0f, dp(0.75f)));
        this.labelPaint.setColor(GameHudPalette.TEXT);
        this.labelPaint.setTextSize(dp(10.0f));
        this.labelPaint.setFakeBoldText(true);
        RadialGradient radialGradient = new RadialGradient(0.0f, 0.0f, 100.0f, new int[]{0, 0, 1711276032}, new float[]{0.0f, 0.7f, 1.0f}, Shader.TileMode.CLAMP);
        this.vignetteShader = radialGradient;
        this.vignettePaint.setShader(radialGradient);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.recommendListener == null) {
            this.recommendListener = new LineupActiveBus.Listener() { // from class: com.android.support.ChessBoardView.1
                @Override // com.android.support.lineup.LineupActiveBus.Listener
                public void onLineupApplied(LineupRepo.Lineup lineup, Set<String> set, Set<String> set2) {
                    int[] locRowCol;
                    HashSet hashSet = new HashSet();
                    HashSet hashSet2 = new HashSet();
                    LineupRepo lineupRepo = LineupRepo.get();
                    if (set != null) {
                        Iterator<String> it = set.iterator();
                        while (it.hasNext()) {
                            for (Integer num : lineupRepo.getAllIdsByPaint(it.next())) {
                                if (num != null) {
                                    hashSet.add(Integer.valueOf(ChessBoardView.this.getBaseHeroId(num.intValue())));
                                }
                            }
                        }
                    }
                    if (set2 != null) {
                        Iterator<String> it2 = set2.iterator();
                        while (it2.hasNext()) {
                            for (Integer num2 : lineupRepo.getAllIdsByPaint(it2.next())) {
                                if (num2 != null) {
                                    hashSet2.add(Integer.valueOf(ChessBoardView.this.getBaseHeroId(num2.intValue())));
                                }
                            }
                        }
                    }
                    synchronized (ChessBoardView.this.recommendBaseIds) {
                        ChessBoardView.this.recommendBaseIds.clear();
                        ChessBoardView.this.recommendBaseIds.addAll(hashSet);
                        ChessBoardView.this.recommendCarryBaseIds.clear();
                        ChessBoardView.this.recommendCarryBaseIds.addAll(hashSet2);
                    }
                    HashMap map = new HashMap();
                    if (lineup != null && lineup.finalUnits != null) {
                        for (LineupRepo.LineupUnit lineupUnit : lineup.finalUnits) {
                            if (lineupUnit != null && lineupUnit.isHero() && lineupUnit.location != null && (locRowCol = ChessBoardView.this.parseLocRowCol(lineupUnit.location)) != null) {
                                int i = locRowCol[0];
                                int i2 = locRowCol[1];
                                if (i >= 0 && i < 4 && i2 >= 0 && i2 < 7) {
                                    int i3 = i2 | (i << 8);
                                    map.put(Integer.valueOf(i3), Boolean.valueOf(lineupUnit.carry || ((Boolean) map.getOrDefault(Integer.valueOf(i3), false)).booleanValue()));
                                }
                            }
                        }
                    }
                    synchronized (ChessBoardView.this.targetCellsCarry) {
                        ChessBoardView.this.targetCellsCarry.clear();
                        ChessBoardView.this.targetCellsCarry.putAll(map);
                    }
                    ChessBoardView.this.postInvalidate();
                }
            };
        }
        LineupActiveBus.get().addListener(this.recommendListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int[] parseLocRowCol(String str) {
        return BoardLoc.parse(str);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.recommendListener != null) {
            LineupActiveBus.get().removeListener(this.recommendListener);
        }
    }

    public void setBoardData(int i, int i2, int i3, int i4, String str, int[] iArr, int[] iArr2) {
        long jBoardDataFingerprint = boardDataFingerprint(i, i2, i3, i4, str, iArr, iArr2);
        if (this.lastBoardDataFingerprint == jBoardDataFingerprint) {
            return;
        }
        this.lastBoardDataFingerprint = jBoardDataFingerprint;
        this.boardBitmask = i;
        this.benchBitmask = i2;
        this.boardSummaryText = formatBoardSummary(str, i3, i4);
        Arrays.fill(this.boardHeroIds, 0);
        Arrays.fill(this.benchHeroIds, 0);
        if (iArr != null) {
            int[] iArr3 = this.boardHeroIds;
            System.arraycopy(iArr, 0, iArr3, 0, Math.min(iArr.length, iArr3.length));
        }
        if (iArr2 != null) {
            int[] iArr4 = this.benchHeroIds;
            System.arraycopy(iArr2, 0, iArr4, 0, Math.min(iArr2.length, iArr4.length));
        }
        preloadHeroBitmaps();
        invalidate();
    }

    public void clearData() {
        setBoardData(0, 0, 0, 0, "", null, null);
    }

    static String formatBoardSummary(String str, int i, int i2) {
        if (str == null) {
            str = "";
        }
        if (str.isEmpty()) {
            return "";
        }
        return str + " 棋盘:" + i + " 备战:" + i2;
    }

    private long boardDataFingerprint(int i, int i2, int i3, int i4, String str, int[] iArr, int[] iArr2) {
        long jMixBoardFingerprint = mixBoardFingerprint(mixBoardFingerprint(mixBoardFingerprint(mixBoardFingerprint(-3750763034362895579L, i), i2), i3), i4);
        if (str == null) {
            str = "";
        }
        for (int i5 = 0; i5 < str.length(); i5++) {
            jMixBoardFingerprint = mixBoardFingerprint(jMixBoardFingerprint, str.charAt(i5));
        }
        int i6 = 0;
        while (i6 < 28) {
            jMixBoardFingerprint = mixBoardFingerprint(jMixBoardFingerprint, (iArr == null || i6 >= iArr.length) ? 0 : iArr[i6]);
            i6++;
        }
        int i7 = 0;
        while (i7 < 9) {
            jMixBoardFingerprint = mixBoardFingerprint(jMixBoardFingerprint, (iArr2 == null || i7 >= iArr2.length) ? 0 : iArr2[i7]);
            i7++;
        }
        if (jMixBoardFingerprint == 0) {
            return 1L;
        }
        return jMixBoardFingerprint;
    }

    private void preloadHeroBitmaps() {
        for (int i = 0; i < 28; i++) {
            int i2 = this.boardHeroIds[i];
            if (i2 > 0 && !this.heroBitmapCache.containsKey(Integer.valueOf(i2)) && !this.loadingHeroIds.contains(Integer.valueOf(i2))) {
                loadHeroBitmap(i2);
            }
        }
        for (int i3 = 0; i3 < 9; i3++) {
            int i4 = this.benchHeroIds[i3];
            if (i4 > 0 && !this.heroBitmapCache.containsKey(Integer.valueOf(i4)) && !this.loadingHeroIds.contains(Integer.valueOf(i4))) {
                loadHeroBitmap(i4);
            }
        }
    }

    private void loadHeroBitmap(int i) {
        int baseHeroId = getBaseHeroId(i);
        String englishNameById = HeroIdMapper.getEnglishNameById(i, this.host.currentSeason());
        if ((englishNameById == null || englishNameById.isEmpty()) && baseHeroId != i) {
            englishNameById = HeroIdMapper.getEnglishNameById(baseHeroId, this.host.currentSeason());
        }
        if (englishNameById == null || englishNameById.isEmpty()) {
            return;
        }
        this.loadingHeroIds.add(Integer.valueOf(i));
        String lowerCase = englishNameById.toLowerCase();
        tryLoadBitmapFallback(i, this.host.buildHeroImageUris(lowerCase), 0, Math.max((int) dp(28.0f), (int) (this.hexRy * 2.0f)));
    }

    public void clearBitmapCache() {
        this.heroBitmapCache.clear();
        this.loadingHeroIds.clear();
        postInvalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryLoadBitmapFallback(int i, final String[] strArr, final int i2, final int i3) {
        ChessBoardView chessBoardView;
        final int i4;
        if (i2 >= strArr.length) {
            this.loadingHeroIds.remove(Integer.valueOf(i));
            return;
        }
        try {
            chessBoardView = this;
            i4 = i;
            try {
                Glide.with(getContext().getApplicationContext()).asBitmap().load(strArr[i2]).override(i3, i3).centerCrop().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into((RequestBuilder) new CustomTarget<Bitmap>(i3, i3) { // from class: com.android.support.ChessBoardView.2
                    @Override // com.bumptech.glide.request.target.Target
                    public void onLoadCleared(Drawable drawable) {
                    }

                    @Override // com.bumptech.glide.request.target.Target
                    public /* bridge */ /* synthetic */ void onResourceReady(Object obj, Transition transition) {
                        onResourceReady((Bitmap) obj, (Transition<? super Bitmap>) transition);
                    }

                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        ChessBoardView.this.heroBitmapCache.put(Integer.valueOf(i4), bitmap);
                        ChessBoardView.this.loadingHeroIds.remove(Integer.valueOf(i4));
                        ChessBoardView.this.postInvalidate();
                    }

                    @Override // com.bumptech.glide.request.target.CustomTarget, com.bumptech.glide.request.target.Target
                    public void onLoadFailed(Drawable drawable) {
                        ChessBoardView.this.tryLoadBitmapFallback(i4, strArr, i2 + 1, i3);
                    }
                });
            } catch (Exception unused) {
                chessBoardView.loadingHeroIds.remove(Integer.valueOf(i4));
            }
        } catch (Exception unused2) {
            chessBoardView = this;
            i4 = i;
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        float fDp;
        float f = this.viewScale;
        float fDp2 = dp(14.0f) * f;
        int size = View.MeasureSpec.getSize(i);
        if (size <= 0) {
            size = (int) Math.ceil((fDp2 * 2.0f) + (dp(30.0f) * f * visibleColumnSpan()));
        }
        computeGeometry(size);
        if (this.benchHidden) {
            fDp = 0.0f;
        } else {
            fDp = (dp(14.0f) * f) + (this.hexRy * 2.0f);
        }
        setMeasuredDimension(size, (int) Math.ceil((dp(8.0f) * f) + (dp(18.0f) * f) + fDp + this.topExtra + (this.hexRy * 2.0f) + (this.rowPitch * 3.0f) + this.bottomExtra + (dp(6.0f) * f)));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.cellSpacing <= 0.0f) {
            computeGeometry(getWidth() > 0 ? getWidth() : dp(276.0f));
        }
        float f = this.viewScale;
        float fDp = dp(14.0f) * f;
        float fDp2 = dp(8.0f) * f;
        if (!this.boardSummaryText.isEmpty()) {
            this.labelPaint.setTextSize(dp(10.0f) * f);
            canvas.drawText(this.boardSummaryText, fDp, (dp(11.0f) * f) + fDp2, this.labelPaint);
        }
        if (!this.benchHidden) {
            float fDp3 = fDp2 + (dp(18.0f) * f) + this.topExtra + this.hexRy;
            for (int i = 0; i < 9; i++) {
                int i2 = this.isSelfBoard ? i : 8 - i;
                float f2 = this.cellSpacing;
                drawUnit(canvas, this.benchHeroIds[i], (i2 * f2) + fDp + (f2 / 2.0f), fDp3, ((this.benchBitmask >> i) & 1) != 0, true, -1, i);
            }
        }
        for (int i3 = 0; i3 < 4; i3++) {
            float fBoardCenterY = boardCenterY(i3);
            for (int i4 = 0; i4 < 7; i4++) {
                int i5 = (i3 * 7) + i4;
                drawUnit(canvas, this.boardHeroIds[i5], boardCenterX(i3, this.isSelfBoard ? i4 : 6 - i4), fBoardCenterY, ((this.boardBitmask >> i5) & 1) != 0, false, i3, i4);
            }
        }
    }

    private float labelHeight() {
        return dp(18.0f) * this.viewScale;
    }

    private float boardTopY() {
        float f = this.viewScale;
        return (dp(8.0f) * f) + labelHeight() + (this.benchHidden ? 0.0f : (this.hexRy * 2.0f) + (dp(14.0f) * f));
    }

    private float boardCenterX(int i, int i2) {
        float fDp = (dp(14.0f) * this.viewScale) + (i % 2 == 1 ? this.cellSpacing * 0.5f : 0.0f);
        float f = this.cellSpacing;
        return fDp + (i2 * f) + (f / 2.0f);
    }

    private float boardCenterY(int i) {
        return boardTopY() + this.topExtra + this.hexRy + (i * this.rowPitch);
    }

    private void appendHex(Path path, float f, float f2) {
        path.moveTo(f, f2 - this.hexRy);
        path.lineTo(this.hexRx + f, f2 - (this.hexRy / 2.0f));
        path.lineTo(this.hexRx + f, (this.hexRy / 2.0f) + f2);
        path.lineTo(f, this.hexRy + f2);
        path.lineTo(f - this.hexRx, (this.hexRy / 2.0f) + f2);
        path.lineTo(f - this.hexRx, f2 - (this.hexRy / 2.0f));
        path.close();
    }

    private void buildHex(float f, float f2) {
        this.hexPath.reset();
        appendHex(this.hexPath, f, f2);
    }

    private float visibleColumnSpan() {
        if (this.benchHidden) {
            return 7.5f;
        }
        return Math.max(7.5f, 9.0f);
    }

    private void computeGeometry(float f) {
        float f2 = this.viewScale;
        float fDp = f - ((dp(14.0f) * f2) * 2.0f);
        if (fDp <= 0.0f) {
            fDp = dp(250.0f) * f2;
        }
        float fVisibleColumnSpan = fDp / visibleColumnSpan();
        this.cellSpacing = fVisibleColumnSpan;
        float f3 = fVisibleColumnSpan * HEX_GAP_RATIO;
        this.hexRx = f3 / 2.0f;
        float f4 = (f3 * HEX_ASPECT) / 2.0f;
        this.hexRy = f4;
        this.rowPitch = f4 * 2.0f * ROW_PITCH_RATIO;
        this.topExtra = dp(8.0f) * f2;
        this.bottomExtra = dp(5.0f) * f2;
        float fStarRadius = starRadius();
        this.fallbackText.setTextSize(this.hexRx * 0.9f);
        this.outerStroke.setStrokeWidth(Math.max(dp(2.4f), this.hexRx * 0.2f));
        this.strokePaint.setStrokeWidth(Math.max(dp(1.4f), this.hexRx * 0.11f));
        this.rimPaint.setStrokeWidth(Math.max(dp(1.0f), this.hexRx * 0.08f));
        this.emptyStroke.setStrokeWidth(dp(1.0f) * f2);
        this.targetMarkerPaint.setTextSize(Math.max(dp(8.0f), 1.6f * fStarRadius));
        this.carryStarPaint.setTextSize(Math.max(dp(8.0f), fStarRadius * 1.8f));
        this.labelPaint.setTextSize(dp(10.0f) * f2);
        for (int i = 0; i < 4; i++) {
            float fBoardCenterY = boardCenterY(i);
            Shader[] shaderArr = this.rowShaders;
            float f5 = this.hexRy;
            shaderArr[i] = new LinearGradient(0.0f, fBoardCenterY - f5, 0.0f, fBoardCenterY + f5, COL_EMPTY_TOP, COL_EMPTY_BOTTOM, Shader.TileMode.CLAMP);
        }
        float fDp2 = (dp(8.0f) * f2) + labelHeight() + this.topExtra + this.hexRy;
        float f6 = this.hexRy;
        this.benchShader = new LinearGradient(0.0f, fDp2 - f6, 0.0f, fDp2 + f6, COL_EMPTY_TOP, COL_EMPTY_BOTTOM, Shader.TileMode.CLAMP);
    }

    private float starRadius() {
        return Math.max(dp(2.6f), this.cellSpacing * 0.095f);
    }

    private void drawUnit(Canvas canvas, int i, float f, float f2, boolean z, boolean z2, int i2, int i3) {
        buildHex(f, f2);
        if (z) {
            int starLevel = i > 0 ? getStarLevel(i) : 1;
            int iStarBorderColor = starBorderColor(starLevel);
            if (i > 0) {
                drawRecommendOverlay(canvas, f, f2, i);
            }
            drawAvatar(canvas, i, f, f2, iStarBorderColor);
            drawCellVignette(canvas, f, f2);
            this.outerStroke.setStrokeWidth(Math.max(dp(2.4f), this.hexRx * 0.2f));
            canvas.drawPath(this.hexPath, this.outerStroke);
            this.strokePaint.setStrokeWidth(Math.max(dp(1.4f), this.hexRx * 0.11f));
            this.strokePaint.setColor(iStarBorderColor);
            canvas.drawPath(this.hexPath, this.strokePaint);
            drawTopRim(canvas, f, f2);
            if (i > 0) {
                drawStarRow(canvas, f, f2, starLevel);
                return;
            }
            return;
        }
        this.emptyFill.setShader(z2 ? this.benchShader : this.rowShaders[i2]);
        canvas.drawPath(this.hexPath, this.emptyFill);
        canvas.drawPath(this.hexPath, this.emptyStroke);
        this.emptyFill.setShader(null);
        if (z2) {
            return;
        }
        drawTargetCellOverlay(canvas, f, f2, i2, i3);
    }

    private void drawCellVignette(Canvas canvas, float f, float f2) {
        this.shaderMatrix.setScale(this.hexRx / 100.0f, this.hexRy / 100.0f);
        this.shaderMatrix.postTranslate(f, f2);
        this.vignetteShader.setLocalMatrix(this.shaderMatrix);
        canvas.drawPath(this.hexPath, this.vignettePaint);
    }

    private void drawTopRim(Canvas canvas, float f, float f2) {
        this.highlightPath.reset();
        this.highlightPath.moveTo(f - this.hexRx, f2 - (this.hexRy / 2.0f));
        this.highlightPath.lineTo(f, f2 - this.hexRy);
        this.highlightPath.lineTo(f + this.hexRx, f2 - (this.hexRy / 2.0f));
        this.rimPaint.setStrokeWidth(Math.max(dp(1.0f), this.hexRx * 0.08f));
        canvas.drawPath(this.highlightPath, this.rimPaint);
    }

    private void drawAvatar(Canvas canvas, int i, float f, float f2, int i2) {
        String englishNameById;
        Bitmap bitmap = i > 0 ? this.heroBitmapCache.get(Integer.valueOf(i)) : null;
        if (bitmap == null) {
            if (i > 0) {
                loadHeroBitmap(i);
            }
            this.fallbackFill.setColor((i2 & ViewCompat.MEASURED_SIZE_MASK) | (-1728053248));
            canvas.drawPath(this.hexPath, this.fallbackFill);
            canvas.drawText((i <= 0 || (englishNameById = HeroIdMapper.getEnglishNameById(i, this.host.currentSeason())) == null || englishNameById.isEmpty()) ? "?" : englishNameById.substring(0, 1), f, f2 + (this.fallbackText.getTextSize() * 0.36f), this.fallbackText);
            return;
        }
        int iSave = canvas.save();
        canvas.clipPath(this.hexPath);
        int iMin = Math.min(bitmap.getWidth(), bitmap.getHeight());
        this.srcRect.set((bitmap.getWidth() - iMin) / 2, (bitmap.getHeight() - iMin) / 2, (bitmap.getWidth() + iMin) / 2, (bitmap.getHeight() + iMin) / 2);
        float f3 = this.hexRy;
        this.dstRect.set(f - f3, f2 - f3, f + f3, f2 + f3);
        canvas.drawBitmap(bitmap, this.srcRect, this.dstRect, this.bitmapPaint);
        canvas.restoreToCount(iSave);
    }

    private void drawRecommendOverlay(Canvas canvas, float f, float f2, int i) {
        int baseHeroId = getBaseHeroId(i);
        if (this.recommendBaseIds.contains(Integer.valueOf(baseHeroId))) {
            boolean zContains = this.recommendCarryBaseIds.contains(Integer.valueOf(baseHeroId));
            int i2 = zContains ? -15770 : GameHudPalette.SUCCESS;
            this.recommendGlowPaint.setStrokeWidth(dp(2.0f) * 3.5f);
            int i3 = 16777215 & i2;
            this.recommendGlowPaint.setColor(855638016 | i3);
            canvas.drawPath(this.hexPath, this.recommendGlowPaint);
            this.recommendGlowPaint.setStrokeWidth(dp(2.0f) * 2.0f);
            this.recommendGlowPaint.setColor((-2013265920) | i3);
            canvas.drawPath(this.hexPath, this.recommendGlowPaint);
            this.recommendRingPaint.setColor(i2);
            this.recommendRingPaint.setStrokeWidth(dp(1.5f));
            canvas.drawPath(this.hexPath, this.recommendRingPaint);
            if (zContains) {
                canvas.drawText("★", f + (this.hexRx * 0.55f), f2 - (this.hexRy * 0.55f), this.carryStarPaint);
            }
        }
    }

    private void drawTargetCellOverlay(Canvas canvas, float f, float f2, int i, int i2) {
        Boolean bool;
        if (this.isSelfBoard) {
            int i3 = (i << 8) | i2;
            synchronized (this.targetCellsCarry) {
                bool = this.targetCellsCarry.get(Integer.valueOf(i3));
            }
            if (bool == null) {
                return;
            }
            canvas.drawPath(this.hexPath, this.targetCellFillPaint);
            int i4 = bool.booleanValue() ? -19712 : -15770;
            this.targetCellPaint.setColor(i4);
            this.targetCellPaint.setStrokeWidth(dp(1.0f) * (bool.booleanValue() ? 1.4f : 1.0f));
            canvas.drawPath(this.hexPath, this.targetCellPaint);
            String str = bool.booleanValue() ? "★" : "▼";
            this.targetMarkerPaint.setColor(i4);
            canvas.drawText(str, f, f2 + dp(3.0f), this.targetMarkerPaint);
        }
    }

    private void drawStarRow(Canvas canvas, float f, float f2, int i) {
        int iMax = Math.max(1, Math.min(4, i));
        float fStarRadius = starRadius();
        float f3 = 2.2f * fStarRadius;
        float f4 = (iMax * f3) / 2.0f;
        float f5 = f - f4;
        float fDp = ((f2 - this.hexRy) - fStarRadius) - dp(1.5f);
        this.tmpRect.set(f5 - dp(2.5f), (fDp - fStarRadius) - dp(2.0f), f + f4 + dp(2.5f), fDp + fStarRadius + dp(2.0f));
        float fHeight = this.tmpRect.height() / 2.0f;
        canvas.drawRoundRect(this.tmpRect, fHeight, fHeight, this.platePaint);
        canvas.drawRoundRect(this.tmpRect, fHeight, fHeight, this.plateBorder);
        int iStarTierColor = starTierColor(iMax);
        this.starFill.setColor(iStarTierColor);
        this.starGlow.setColor((iStarTierColor & ViewCompat.MEASURED_SIZE_MASK) | 1291845632);
        ensureStarPaths(fStarRadius);
        int iSave = canvas.save();
        canvas.translate((f3 / 2.0f) + f5, fDp);
        for (int i2 = 0; i2 < iMax; i2++) {
            if (i2 > 0) {
                canvas.translate(f3, 0.0f);
            }
            canvas.drawPath(this.starGlowPath, this.starGlow);
            canvas.drawPath(this.starFillPath, this.starFill);
        }
        canvas.restoreToCount(iSave);
    }

    private void ensureStarPaths(float f) {
        if (this.starPathRadius == f) {
            return;
        }
        this.starPathRadius = f;
        buildStarInto(this.starGlowPath, 1.42f * f);
        buildStarInto(this.starFillPath, f);
    }

    private void buildStarInto(Path path, float f) {
        path.reset();
        float f2 = 0.46f * f;
        for (int i = 0; i < 10; i++) {
            double radians = Math.toRadians((i * 36) - 90);
            float f3 = i % 2 == 0 ? f : f2;
            float fCos = ((float) Math.cos(radians)) * f3;
            float fSin = ((float) Math.sin(radians)) * f3;
            if (i == 0) {
                path.moveTo(fCos, fSin);
            } else {
                path.lineTo(fCos, fSin);
            }
        }
        path.close();
    }

    private float dp(float f) {
        return f * this.density;
    }
}
