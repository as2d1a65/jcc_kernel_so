package com.android.support;

import android.content.Context;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
final class GameVisualOverlayHost {
    static final float ROOT_WINDOW_ALPHA = 0.79f;
    private final Context appContext;
    private final FrameLayout boardHeroLayer;
    private final FrameLayout boardLayer;
    private final FrameLayout cardPoolLayer;
    private boolean destroyed;
    private final Display display;
    private final Context displayContext;
    private final int displayId;
    private final FrameLayout rankWarningLayer;
    private final FrameLayout root;
    private boolean rootAttached;
    private final WindowManager.LayoutParams rootParams;
    private WindowManager rootWindowManager;
    private final FrameLayout transientLayer;
    private int visibleMask;
    private static final Object HOST_LOCK = new Object();
    private static final Map<Integer, GameVisualOverlayHost> HOSTS = new HashMap();

    View rootView() {
        return this.root;
    }

    static GameVisualOverlayHost acquire(Context context, Display display) {
        requireMainThread();
        if (context == null || display == null) {
            throw new IllegalArgumentException("context and display are required");
        }
        synchronized (HOST_LOCK) {
            int displayId = display.getDisplayId();
            Map<Integer, GameVisualOverlayHost> map = HOSTS;
            GameVisualOverlayHost gameVisualOverlayHost = map.get(Integer.valueOf(displayId));
            if (gameVisualOverlayHost != null && !gameVisualOverlayHost.destroyed) {
                return gameVisualOverlayHost;
            }
            GameVisualOverlayHost gameVisualOverlayHost2 = new GameVisualOverlayHost(context, display);
            map.put(Integer.valueOf(displayId), gameVisualOverlayHost2);
            return gameVisualOverlayHost2;
        }
    }

    private GameVisualOverlayHost(Context context, Display display) {
        Context applicationContext = context.getApplicationContext();
        context = applicationContext != null ? applicationContext : context;
        this.appContext = context;
        Context contextCreateDisplayContext = context.createDisplayContext(display);
        this.displayContext = contextCreateDisplayContext;
        this.display = display;
        this.displayId = display.getDisplayId();
        FrameLayout frameLayout = new FrameLayout(contextCreateDisplayContext);
        this.root = frameLayout;
        frameLayout.setClipChildren(false);
        frameLayout.setClipToPadding(false);
        frameLayout.setImportantForAccessibility(2);
        FrameLayout frameLayout2 = new FrameLayout(contextCreateDisplayContext);
        this.boardLayer = frameLayout2;
        frameLayout2.setClipChildren(false);
        frameLayout2.setClipToPadding(false);
        frameLayout2.setVisibility(8);
        frameLayout.addView(frameLayout2, new FrameLayout.LayoutParams(-1, -1, 8388659));
        FrameLayout frameLayout3 = new FrameLayout(contextCreateDisplayContext);
        this.rankWarningLayer = frameLayout3;
        frameLayout3.setClipChildren(false);
        frameLayout3.setClipToPadding(false);
        frameLayout3.setVisibility(8);
        frameLayout.addView(frameLayout3, new FrameLayout.LayoutParams(-1, -1, 8388659));
        FrameLayout frameLayout4 = new FrameLayout(contextCreateDisplayContext);
        this.cardPoolLayer = frameLayout4;
        frameLayout4.setClipChildren(false);
        frameLayout4.setClipToPadding(false);
        frameLayout4.setVisibility(8);
        frameLayout.addView(frameLayout4, new FrameLayout.LayoutParams(-1, -1, 8388659));
        FrameLayout frameLayout5 = new FrameLayout(contextCreateDisplayContext);
        this.transientLayer = frameLayout5;
        frameLayout5.setClipChildren(false);
        frameLayout5.setClipToPadding(false);
        frameLayout5.setVisibility(8);
        frameLayout.addView(frameLayout5, new FrameLayout.LayoutParams(-1, -1, 8388659));
        FrameLayout frameLayout6 = new FrameLayout(contextCreateDisplayContext);
        this.boardHeroLayer = frameLayout6;
        frameLayout6.setClipChildren(false);
        frameLayout6.setClipToPadding(false);
        frameLayout6.setVisibility(8);
        frameLayout.addView(frameLayout6, new FrameLayout.LayoutParams(-1, -1, 8388659));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2038, 824, -3);
        this.rootParams = layoutParams;
        layoutParams.gravity = 8388659;
        layoutParams.alpha = ROOT_WINDOW_ALPHA;
        layoutParams.setTitle("game-visual-root");
        layoutParams.layoutInDisplayCutoutMode = 1;
    }

    FrameLayout getBoardLayer() {
        return this.boardLayer;
    }

    FrameLayout getBoardHeroLayer() {
        return this.boardHeroLayer;
    }

    FrameLayout getRankWarningLayer() {
        return this.rankWarningLayer;
    }

    FrameLayout getCardPoolLayer() {
        return this.cardPoolLayer;
    }

    FrameLayout getTransientLayer() {
        return this.transientLayer;
    }

    Display getDisplay() {
        return this.display;
    }

    boolean isLayerVisible(int i) {
        return (this.visibleMask & i) != 0;
    }

    boolean isLayerAttached(int i) {
        FrameLayout frameLayoutLayerForBit = layerForBit(i);
        if (this.destroyed || !isLayerVisible(i) || frameLayoutLayerForBit == null || frameLayoutLayerForBit.getVisibility() != 0) {
            return false;
        }
        return this.root.getParent() != null || this.root.isAttachedToWindow();
    }

    void setLayerVisible(int i, boolean z) {
        requireMainThread();
        if (this.destroyed) {
            return;
        }
        int i2 = this.visibleMask;
        int iTransition = GameVisualOverlayHostPolicy.transition(i2, i, z);
        if (i2 == iTransition) {
            if (GameVisualOverlayHostPolicy.shouldAttach(iTransition)) {
                attachRoot();
                return;
            } else {
                if (this.rootAttached || this.root.getParent() != null) {
                    detachRoot();
                    return;
                }
                return;
            }
        }
        this.visibleMask = iTransition;
        syncLayerVisibility();
        if (GameVisualOverlayHostPolicy.shouldAttach(iTransition)) {
            if (attachRoot()) {
                return;
            }
            this.visibleMask = i2;
            syncLayerVisibility();
            return;
        }
        detachRoot();
    }

    void updateBoardBounds(int i, int i2, int i3, int i4, float f) {
        requireMainThread();
        if (this.destroyed) {
            return;
        }
        updateLayerBounds(this.boardLayer, i, i2, i3, i4);
        if (Float.isNaN(f) || Float.isInfinite(f) || f <= 0.0f) {
            f = 1.0f;
        }
        this.boardLayer.setScaleX(f);
        this.boardLayer.setScaleY(f);
    }

    void updateRankWarningBounds(int i, int i2, int i3, int i4) {
        requireMainThread();
        if (this.destroyed) {
            return;
        }
        updateLayerBounds(this.rankWarningLayer, i, i2, i3, i4);
    }

    void setLayerEffectiveAlpha(int i, float f) {
        FrameLayout frameLayoutLayerForBit;
        requireMainThread();
        if (this.destroyed || (frameLayoutLayerForBit = layerForBit(i)) == null) {
            return;
        }
        frameLayoutLayerForBit.setAlpha((float) GameVisualOverlayHostPolicy.normalizedChildAlpha(f, 0.7900000214576721d));
    }

    private static void updateLayerBounds(FrameLayout frameLayout, int i, int i2, int i3, int i4) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
        int iMax = Math.max(1, i3);
        int iMax2 = Math.max(1, i4);
        if (layoutParams.width == iMax && layoutParams.height == iMax2 && layoutParams.leftMargin == i && layoutParams.topMargin == i2 && layoutParams.gravity == 8388659) {
            return;
        }
        layoutParams.width = iMax;
        layoutParams.height = iMax2;
        layoutParams.leftMargin = i;
        layoutParams.topMargin = i2;
        layoutParams.gravity = 8388659;
        frameLayout.setLayoutParams(layoutParams);
    }

    void destroy() {
        requireMainThread();
        if (this.destroyed) {
            return;
        }
        this.visibleMask = 0;
        syncLayerVisibility();
        if (detachRoot()) {
            this.boardLayer.removeAllViews();
            this.boardHeroLayer.removeAllViews();
            this.rankWarningLayer.removeAllViews();
            this.cardPoolLayer.removeAllViews();
            this.transientLayer.removeAllViews();
            this.destroyed = true;
            synchronized (HOST_LOCK) {
                Map<Integer, GameVisualOverlayHost> map = HOSTS;
                if (map.get(Integer.valueOf(this.displayId)) == this) {
                    map.remove(Integer.valueOf(this.displayId));
                }
            }
        }
    }

    private boolean attachRoot() {
        WindowManager windowManagerForApplicationDisplay;
        boolean z = true;
        boolean z2 = this.root.getParent() != null;
        boolean zIsAttachedToWindow = this.root.isAttachedToWindow();
        if (this.rootAttached || z2 || zIsAttachedToWindow) {
            if (!GameVisualOverlayHostPolicy.isDetachConfirmed(z2, zIsAttachedToWindow)) {
                this.rootAttached = true;
                return true;
            }
            this.rootAttached = false;
            this.rootWindowManager = null;
        }
        if (this.root.getParent() != null || (windowManagerForApplicationDisplay = TrustedVisualOverlayWindowManager.forApplicationDisplay(this.appContext, this.display)) == null) {
            return false;
        }
        this.rootWindowManager = windowManagerForApplicationDisplay;
        try {
            windowManagerForApplicationDisplay.addView(this.root, this.rootParams);
            this.rootAttached = true;
            return true;
        } catch (RuntimeException unused) {
            if (this.root.getParent() == null && !this.root.isAttachedToWindow()) {
                z = false;
            }
            this.rootAttached = z;
            detachRoot();
            return false;
        }
    }

    private boolean detachRoot() {
        WindowManager windowManager = this.rootWindowManager;
        if (windowManager != null && (this.rootAttached || this.root.getParent() != null)) {
            try {
                windowManager.removeViewImmediate(this.root);
            } catch (RuntimeException unused) {
            }
        }
        if (!GameVisualOverlayHostPolicy.isDetachConfirmed(this.root.getParent() != null, this.root.isAttachedToWindow())) {
            this.rootAttached = true;
            return false;
        }
        this.rootWindowManager = null;
        this.rootAttached = false;
        return true;
    }

    private void syncLayerVisibility() {
        this.boardLayer.setVisibility((this.visibleMask & 1) != 0 ? 0 : 8);
        this.boardHeroLayer.setVisibility((this.visibleMask & 2) != 0 ? 0 : 8);
        this.rankWarningLayer.setVisibility((this.visibleMask & 4) != 0 ? 0 : 8);
        this.cardPoolLayer.setVisibility((this.visibleMask & 8) != 0 ? 0 : 8);
        this.transientLayer.setVisibility((this.visibleMask & 16) == 0 ? 8 : 0);
    }

    private FrameLayout layerForBit(int i) {
        if (i == 1) {
            return this.boardLayer;
        }
        if (i == 2) {
            return this.boardHeroLayer;
        }
        if (i == 4) {
            return this.rankWarningLayer;
        }
        if (i == 8) {
            return this.cardPoolLayer;
        }
        if (i == 16) {
            return this.transientLayer;
        }
        return null;
    }

    private static void requireMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("GameVisualOverlayHost must run on the main thread");
        }
    }
}
