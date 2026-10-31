package com.android.support;

import android.content.Context;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Choreographer;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
final class BoardHeroOverlayController {
    private static final long FRAME_TIME_RESET_NS = 250000000;
    private static final float POSITION_DEADBAND_DP = 1.5f;
    private final BoardHeroAvatarCache avatarCache;
    private final Context context;
    private boolean desiredVisible;
    private boolean enabled;
    private boolean frameCallbackPosted;
    private WindowManager geometryWindowManager;
    private int lastInsetBottom;
    private int lastInsetLeft;
    private int lastInsetRight;
    private int lastInsetTop;
    private int lastScreenHeight;
    private int lastScreenWidth;
    private FrameLayout markerLayer;
    private GameVisualOverlayHost overlayHost;
    private int season;
    private final Map<String, MarkerWindow> markers = new HashMap();
    private final ArrayList<MarkerWindow> frameMarkers = new ArrayList<>();
    private final Choreographer choreographer = Choreographer.getInstance();
    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() { // from class: com.android.support.BoardHeroOverlayController.1
        @Override // android.view.Choreographer.FrameCallback
        public void doFrame(long j) {
            BoardHeroOverlayController.this.frameCallbackPosted = false;
            int size = BoardHeroOverlayController.this.frameMarkers.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                MarkerWindow markerWindow = (MarkerWindow) BoardHeroOverlayController.this.frameMarkers.get(i);
                if (markerWindow.visible && markerWindow.moving) {
                    long j2 = markerWindow.lastFrameNs > 0 ? j - markerWindow.lastFrameNs : 0L;
                    boolean z2 = true;
                    if (j2 <= 0) {
                        markerWindow.lastFrameNs = j;
                        z = true;
                    } else if (j2 > BoardHeroOverlayController.FRAME_TIME_RESET_NS) {
                        BoardHeroOverlayController.snapMarkerToTarget(markerWindow);
                        BoardHeroOverlayController.updateMarkerWindowPosition(markerWindow);
                    } else {
                        markerWindow.lastFrameNs = j;
                        float fSmoothingAlpha = BoardHeroOverlayMath.smoothingAlpha(j2 * 1.0E-9f);
                        markerWindow.currentX = BoardHeroOverlayMath.stepPositionWithAlpha(markerWindow.currentX, markerWindow.targetX, fSmoothingAlpha);
                        markerWindow.currentY = BoardHeroOverlayMath.stepPositionWithAlpha(markerWindow.currentY, markerWindow.targetY, fSmoothingAlpha);
                        if (markerWindow.currentX == markerWindow.targetX && markerWindow.currentY == markerWindow.targetY) {
                            z2 = false;
                        }
                        markerWindow.moving = z2;
                        BoardHeroOverlayController.syncMarkerVisibility(markerWindow);
                        if (!markerWindow.moving) {
                            markerWindow.lastFrameNs = 0L;
                        }
                        z |= markerWindow.moving;
                        BoardHeroOverlayController.updateMarkerWindowPosition(markerWindow);
                    }
                }
            }
            if (z) {
                BoardHeroOverlayController.this.scheduleFrame();
            }
        }
    };
    private BoardHeroAnchorSnapshot snapshot = BoardHeroAnchorSnapshot.EMPTY;
    private Map<Integer, Integer> ownCopies = Collections.emptyMap();
    private Map<Integer, Integer> poolLeft = Collections.emptyMap();

    BoardHeroOverlayController(Context context, WindowManager windowManager, GameVisualOverlayHost gameVisualOverlayHost) {
        this.context = context;
        this.geometryWindowManager = windowManager;
        this.overlayHost = gameVisualOverlayHost;
        this.markerLayer = gameVisualOverlayHost == null ? null : gameVisualOverlayHost.getBoardHeroLayer();
        this.avatarCache = new BoardHeroAvatarCache(context);
    }

    void show() {
        this.desiredVisible = true;
        renderMarkers();
    }

    void hide() {
        this.desiredVisible = false;
        removeAllMarkers();
    }

    void submit(BoardHeroAnchorSnapshot boardHeroAnchorSnapshot, Map<Integer, Integer> map, Map<Integer, Integer> map2, int i, boolean z) {
        if (boardHeroAnchorSnapshot == null) {
            boardHeroAnchorSnapshot = BoardHeroAnchorSnapshot.EMPTY;
        }
        boolean z2 = true;
        boolean z3 = boardHeroAnchorSnapshot.generation != this.snapshot.generation;
        boolean zSameMap = sameMap(this.ownCopies, map);
        boolean zSameMap2 = sameMap(this.poolLeft, map2);
        if (!z3 && zSameMap && zSameMap2 && this.season == i && this.enabled == z) {
            z2 = false;
        }
        this.snapshot = boardHeroAnchorSnapshot;
        if (!zSameMap) {
            this.ownCopies = immutableCopy(map);
        }
        if (!zSameMap2) {
            this.poolLeft = immutableCopy(map2);
        }
        this.season = i;
        this.enabled = z;
        if (z2) {
            renderMarkers();
        } else {
            if (BoardHeroOverlayMath.shouldRender(z, this.snapshot.planning, this.snapshot.receivedAtMs, System.currentTimeMillis())) {
                return;
            }
            removeAllMarkers();
        }
    }

    void rebind(GameVisualOverlayHost gameVisualOverlayHost, WindowManager windowManager) {
        if (gameVisualOverlayHost == null) {
            return;
        }
        if (gameVisualOverlayHost != this.overlayHost) {
            removeAllMarkers();
        }
        this.overlayHost = gameVisualOverlayHost;
        this.markerLayer = gameVisualOverlayHost.getBoardHeroLayer();
        if (windowManager != null) {
            this.geometryWindowManager = windowManager;
        }
        if (this.desiredVisible) {
            renderMarkers();
        }
    }

    void destroy() {
        removeAllMarkers();
        this.desiredVisible = false;
        this.avatarCache.close();
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0269  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x022e  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0248  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x024b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void renderMarkers() {
        /*
            Method dump skipped, instructions count: 734
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.BoardHeroOverlayController.renderMarkers():void");
    }

    private boolean ensureMarkerLayer() {
        GameVisualOverlayHost gameVisualOverlayHost = this.overlayHost;
        if (gameVisualOverlayHost == null) {
            return false;
        }
        this.markerLayer = gameVisualOverlayHost.getBoardHeroLayer();
        this.overlayHost.setLayerEffectiveAlpha(2, 0.79f);
        this.overlayHost.setLayerVisible(2, true);
        return this.overlayHost.isLayerVisible(2);
    }

    private MarkerWindow createMarker(String str, int i, int i2, int i3, int i4) {
        if (this.markerLayer == null) {
            return null;
        }
        BoardHeroOverlayView boardHeroOverlayView = new BoardHeroOverlayView(this.context, this.avatarCache);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(i, i2);
        boardHeroOverlayView.setX(i3);
        boardHeroOverlayView.setY(i4);
        try {
            this.markerLayer.addView(boardHeroOverlayView, layoutParams);
            return new MarkerWindow(boardHeroOverlayView, layoutParams);
        } catch (RuntimeException unused) {
            boardHeroOverlayView.close();
            return null;
        }
    }

    private void removeAllMarkers() {
        ArrayList<MarkerWindow> arrayList;
        stopFrameLoop();
        int size = this.frameMarkers.size();
        while (true) {
            size--;
            arrayList = this.frameMarkers;
            if (size < 0) {
                break;
            }
            MarkerWindow markerWindow = arrayList.get(size);
            markerWindow.visible = false;
            markerWindow.moving = false;
            markerWindow.lastFrameNs = 0L;
            detachMarkerWindow(markerWindow);
        }
        arrayList.clear();
        this.markers.clear();
        GameVisualOverlayHost gameVisualOverlayHost = this.overlayHost;
        if (gameVisualOverlayHost != null) {
            gameVisualOverlayHost.setLayerVisible(2, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleFrame() {
        if (this.frameCallbackPosted || !hasMovingMarkers()) {
            return;
        }
        this.frameCallbackPosted = true;
        this.choreographer.postFrameCallback(this.frameCallback);
    }

    private void stopFrameLoop() {
        if (this.frameCallbackPosted) {
            this.choreographer.removeFrameCallback(this.frameCallback);
            this.frameCallbackPosted = false;
        }
    }

    private boolean hasMovingMarkers() {
        int size = this.frameMarkers.size();
        for (int i = 0; i < size; i++) {
            MarkerWindow markerWindow = this.frameMarkers.get(i);
            if (markerWindow.visible && markerWindow.moving) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void syncMarkerVisibility(MarkerWindow markerWindow) {
        int i = BoardHeroOverlayMath.shouldShowMarker(markerWindow.visible, markerWindow.moving) ? 0 : 4;
        if (markerWindow.view.getVisibility() != i) {
            markerWindow.view.setVisibility(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void snapMarkerToTarget(MarkerWindow markerWindow) {
        markerWindow.currentX = markerWindow.targetX;
        markerWindow.currentY = markerWindow.targetY;
        markerWindow.moving = false;
        markerWindow.lastFrameNs = 0L;
        syncMarkerVisibility(markerWindow);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateMarkerWindowPosition(MarkerWindow markerWindow) {
        int iRound = Math.round(markerWindow.currentX);
        int iRound2 = Math.round(markerWindow.currentY);
        if (markerWindow.view.getWidth() != markerWindow.params.width || markerWindow.view.getHeight() != markerWindow.params.height) {
            markerWindow.view.setLayoutParams(markerWindow.params);
        }
        if (Math.round(markerWindow.view.getX()) != iRound) {
            markerWindow.view.setX(iRound);
        }
        if (Math.round(markerWindow.view.getY()) != iRound2) {
            markerWindow.view.setY(iRound2);
        }
    }

    private void removeMarker(MarkerWindow markerWindow) {
        markerWindow.visible = false;
        markerWindow.moving = false;
        markerWindow.lastFrameNs = 0L;
        this.frameMarkers.remove(markerWindow);
        detachMarkerWindow(markerWindow);
    }

    private void detachMarkerWindow(MarkerWindow markerWindow) {
        if (this.markerLayer != null) {
            ViewParent parent = markerWindow.view.getParent();
            FrameLayout frameLayout = this.markerLayer;
            if (parent == frameLayout) {
                frameLayout.removeView(markerWindow.view);
            }
        }
        markerWindow.view.close();
    }

    private ScreenBounds readScreenBounds() {
        WindowManager windowManager;
        if (Build.VERSION.SDK_INT >= 30 && (windowManager = this.geometryWindowManager) != null) {
            WindowMetrics currentWindowMetrics = windowManager.getCurrentWindowMetrics();
            Rect bounds = currentWindowMetrics.getBounds();
            Insets insetsIgnoringVisibility = currentWindowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
            return new ScreenBounds(Math.max(1, bounds.width()), Math.max(1, bounds.height()), insetsIgnoringVisibility.left, insetsIgnoringVisibility.top, insetsIgnoringVisibility.right, insetsIgnoringVisibility.bottom);
        }
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        return new ScreenBounds(Math.max(1, displayMetrics.widthPixels), Math.max(1, displayMetrics.heightPixels), 0, 0, 0, 0);
    }

    private static int valueOrUnknown(Map<Integer, Integer> map, int i) {
        Integer num = map.get(Integer.valueOf(i));
        if (num == null) {
            return -1;
        }
        return num.intValue();
    }

    private static Map<Integer, Integer> immutableCopy(Map<Integer, Integer> map) {
        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap(map));
    }

    private static boolean sameMap(Map<Integer, Integer> map, Map<Integer, Integer> map2) {
        if (map2 == null || map2.isEmpty()) {
            return map.isEmpty();
        }
        return map.equals(map2);
    }

    private static final class MarkerWindow {
        float currentX;
        float currentY;
        int heroId;
        long lastFrameNs;
        boolean moving;
        final FrameLayout.LayoutParams params;
        boolean positionInitialized;
        float targetX;
        float targetY;
        final BoardHeroOverlayView view;
        boolean visible;

        MarkerWindow(BoardHeroOverlayView boardHeroOverlayView, FrameLayout.LayoutParams layoutParams) {
            this.view = boardHeroOverlayView;
            this.params = layoutParams;
        }
    }

    private static final class ScreenBounds {
        final int height;
        final int insetBottom;
        final int insetLeft;
        final int insetRight;
        final int insetTop;
        final int width;

        ScreenBounds(int i, int i2, int i3, int i4, int i5, int i6) {
            this.width = i;
            this.height = i2;
            this.insetLeft = i3;
            this.insetTop = i4;
            this.insetRight = i5;
            this.insetBottom = i6;
        }
    }
}
