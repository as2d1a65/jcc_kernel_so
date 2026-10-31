package com.android.support;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public final class TrustedVisualOverlayWindowManager {
    private static final String TAG = "TrustedVisualOverlay";
    private static final Map<Integer, SurfaceOverlayWindowManager> SURFACE_OWNERS = new HashMap();
    private static final Map<Integer, WindowManager> APPLICATION_OWNERS = new HashMap();

    private TrustedVisualOverlayWindowManager() {
    }

    public static WindowManager forApplicationDisplay(Context context, Display display) {
        if (context == null || display == null) {
            return null;
        }
        Context applicationContext = context.getApplicationContext();
        if (applicationContext == null) {
            applicationContext = context;
        }
        Map<Integer, WindowManager> map = APPLICATION_OWNERS;
        synchronized (map) {
            WindowManager windowManager = map.get(Integer.valueOf(display.getDisplayId()));
            if (windowManager != null) {
                Display defaultDisplay = windowManager.getDefaultDisplay();
                if (defaultDisplay != null && defaultDisplay.getDisplayId() == display.getDisplayId()) {
                    return windowManager;
                }
                map.remove(Integer.valueOf(display.getDisplayId()));
            }
            try {
                Context contextCreateDisplayContext = applicationContext.createDisplayContext(display);
                if (Build.VERSION.SDK_INT >= 30) {
                    contextCreateDisplayContext = contextCreateDisplayContext.createWindowContext(2038, null);
                }
                WindowManager windowManagerWrap = (WindowManager) contextCreateDisplayContext.getSystemService("window");
                if (windowManagerWrap != null) {
                    windowManagerWrap = ScreenSecureWindowManager.wrap(context, windowManagerWrap);
                    map.put(Integer.valueOf(display.getDisplayId()), windowManagerWrap);
                }
                return windowManagerWrap;
            } catch (RuntimeException e) {
                Log.w(TAG, "Unable to create application overlay owner for display " + display.getDisplayId(), e);
                return null;
            }
        }
    }

    public static WindowManager forDisplay(Display display) {
        Context contextCreateWindowContext;
        SurfaceOverlayWindowManager surfaceOverlayWindowManager;
        EmulatorShortcutService overlayHost = EmulatorShortcutService.getOverlayHost();
        if (overlayHost == null || display == null) {
            return null;
        }
        Context contextCreateDisplayContext = overlayHost.createDisplayContext(display);
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                contextCreateWindowContext = contextCreateDisplayContext.createWindowContext(2032, null);
            } catch (RuntimeException e) {
                Log.w(TAG, "Unable to create accessibility window context for display " + display.getDisplayId(), e);
                return null;
            }
        } else {
            contextCreateWindowContext = contextCreateDisplayContext;
        }
        if (Build.VERSION.SDK_INT >= 34) {
            Map<Integer, SurfaceOverlayWindowManager> map = SURFACE_OWNERS;
            synchronized (map) {
                surfaceOverlayWindowManager = map.get(Integer.valueOf(display.getDisplayId()));
                if (surfaceOverlayWindowManager == null || surfaceOverlayWindowManager.getDefaultDisplay() == null || surfaceOverlayWindowManager.getDefaultDisplay().getDisplayId() != display.getDisplayId()) {
                    surfaceOverlayWindowManager = new SurfaceOverlayWindowManager(overlayHost, contextCreateDisplayContext, display);
                    map.put(Integer.valueOf(display.getDisplayId()), surfaceOverlayWindowManager);
                }
            }
            return surfaceOverlayWindowManager;
        }
        return ScreenSecureWindowManager.wrap(contextCreateWindowContext, (WindowManager) contextCreateWindowContext.getSystemService("window"));
    }

    public static WindowManager.LayoutParams getLayoutParams(WindowManager windowManager, View view) {
        if (Build.VERSION.SDK_INT >= 34 && (windowManager instanceof SurfaceOverlayWindowManager)) {
            return ((SurfaceOverlayWindowManager) windowManager).getLayoutParams(view);
        }
        ViewGroup.LayoutParams layoutParams = view == null ? null : view.getLayoutParams();
        if (layoutParams instanceof WindowManager.LayoutParams) {
            return (WindowManager.LayoutParams) layoutParams;
        }
        return null;
    }

    public static void syncViewState(WindowManager windowManager, View view) {
        if (Build.VERSION.SDK_INT < 34 || !(windowManager instanceof SurfaceOverlayWindowManager) || view == null) {
            return;
        }
        ((SurfaceOverlayWindowManager) windowManager).syncViewState(view);
    }

    public static boolean isSurfaceOwner(WindowManager windowManager) {
        return Build.VERSION.SDK_INT >= 34 && (windowManager instanceof SurfaceOverlayWindowManager);
    }

    public static int getDisplayId(WindowManager windowManager, View view) {
        if (Build.VERSION.SDK_INT >= 34 && (windowManager instanceof SurfaceOverlayWindowManager)) {
            Display defaultDisplay = windowManager.getDefaultDisplay();
            if (defaultDisplay == null) {
                return -1;
            }
            return defaultDisplay.getDisplayId();
        }
        Display display = view == null ? null : view.getDisplay();
        if (display == null) {
            return -1;
        }
        return display.getDisplayId();
    }

    public static boolean retargetOwnerToDisplay(WindowManager windowManager, Display display) {
        if (Build.VERSION.SDK_INT < 34 || !(windowManager instanceof SurfaceOverlayWindowManager) || display == null) {
            return false;
        }
        SurfaceOverlayWindowManager surfaceOverlayWindowManager = (SurfaceOverlayWindowManager) windowManager;
        Map<Integer, SurfaceOverlayWindowManager> map = SURFACE_OWNERS;
        synchronized (map) {
            Display defaultDisplay = surfaceOverlayWindowManager.getDefaultDisplay();
            int displayId = defaultDisplay == null ? -1 : defaultDisplay.getDisplayId();
            surfaceOverlayWindowManager.retargetDisplay(display);
            if (map.get(Integer.valueOf(displayId)) == surfaceOverlayWindowManager) {
                map.remove(Integer.valueOf(displayId));
            }
            map.put(Integer.valueOf(display.getDisplayId()), surfaceOverlayWindowManager);
        }
        return true;
    }

    public static void invalidateDisplay(int i) {
        Map<Integer, WindowManager> map = APPLICATION_OWNERS;
        synchronized (map) {
            map.remove(Integer.valueOf(i));
        }
        Map<Integer, SurfaceOverlayWindowManager> map2 = SURFACE_OWNERS;
        synchronized (map2) {
            map2.remove(Integer.valueOf(i));
        }
    }

    public static void invalidateAll() {
        Map<Integer, SurfaceOverlayWindowManager> map = SURFACE_OWNERS;
        synchronized (map) {
            map.clear();
        }
    }

    private static final class SurfaceOverlayWindowManager implements WindowManager {
        private static final int BASE_LAYER = 10000;
        private final Context context;
        private volatile Display display;
        private final IdentityHashMap<View, HostedView> hostedViews = new IdentityHashMap<>();
        private int nextLayer = BASE_LAYER;
        private final EmulatorShortcutService service;

        SurfaceOverlayWindowManager(EmulatorShortcutService emulatorShortcutService, Context context, Display display) {
            this.service = emulatorShortcutService;
            this.context = context;
            this.display = display;
        }

        @Override // android.view.ViewManager
        public synchronized void addView(View view, ViewGroup.LayoutParams layoutParams) {
            RuntimeException runtimeException;
            SurfaceControlViewHost surfaceControlViewHost;
            SurfaceControlViewHost.SurfacePackage surfacePackage;
            try {
                if (view == null) {
                    throw new IllegalArgumentException("view == null");
                }
                if (!(layoutParams instanceof WindowManager.LayoutParams)) {
                    throw new IllegalArgumentException("Trusted visual overlay requires WindowManager.LayoutParams");
                }
                if (this.hostedViews.containsKey(view) || view.getParent() != null) {
                    throw new IllegalStateException("View is already attached");
                }
                ScreenSecureController.shared().restorePreference(this.context);
                ScreenSecureController.shared().track(view);
                WindowManager.LayoutParams layoutParamsCopyOf = copyOf((WindowManager.LayoutParams) layoutParams);
                int iResolveWidth = resolveWidth(view, layoutParamsCopyOf);
                int iResolveHeight = resolveHeight(view, layoutParamsCopyOf);
                SurfaceControl surfaceControl = null;
                try {
                    surfaceControlViewHost = new SurfaceControlViewHost(this.context, this.display, new Binder());
                    try {
                        setViewWithAccessibilityLayout(surfaceControlViewHost, view, normalizeHostLayoutParams(layoutParamsCopyOf, iResolveWidth, iResolveHeight));
                        surfacePackage = surfaceControlViewHost.getSurfacePackage();
                        try {
                            if (surfacePackage == null) {
                                throw new IllegalStateException("SurfacePackage unavailable");
                            }
                            SurfaceControl surfaceControl2 = surfacePackage.getSurfaceControl();
                            if (surfaceControl2 != null) {
                                try {
                                    if (surfaceControl2.isValid()) {
                                        this.service.attachAccessibilityOverlayToDisplay(this.display.getDisplayId(), surfaceControl2);
                                        int i = this.nextLayer;
                                        this.nextLayer = i + 1;
                                        HostedView hostedView = new HostedView(view, surfaceControlViewHost, surfacePackage, surfaceControl2, layoutParamsCopyOf, iResolveWidth, iResolveHeight, i);
                                        applyLayout(hostedView);
                                        this.hostedViews.put(view, hostedView);
                                        Dbg.d(TrustedVisualOverlayWindowManager.TAG, "Surface HUD attached display=" + this.display.getDisplayId() + " layer=" + hostedView.layer);
                                    }
                                } catch (RuntimeException e) {
                                    runtimeException = e;
                                    surfaceControl = surfaceControl2;
                                    detachSurface(surfaceControl);
                                    if (surfacePackage != null) {
                                        surfacePackage.release();
                                    }
                                    if (surfaceControlViewHost != null) {
                                        surfaceControlViewHost.release();
                                        throw runtimeException;
                                    }
                                    throw runtimeException;
                                }
                            }
                            throw new IllegalStateException("SurfaceControl unavailable");
                        } catch (RuntimeException e2) {
                            runtimeException = e2;
                        }
                    } catch (RuntimeException e3) {
                        runtimeException = e3;
                        surfacePackage = null;
                    }
                } catch (RuntimeException e4) {
                    runtimeException = e4;
                    surfaceControlViewHost = null;
                    surfacePackage = null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }

        @Override // android.view.ViewManager
        public synchronized void updateViewLayout(View view, ViewGroup.LayoutParams layoutParams) {
            HostedView hostedView = this.hostedViews.get(view);
            if (hostedView == null) {
                throw new IllegalArgumentException("View is not attached");
            }
            if (!(layoutParams instanceof WindowManager.LayoutParams)) {
                throw new IllegalArgumentException("Trusted visual overlay requires WindowManager.LayoutParams");
            }
            hostedView.params = copyOf((WindowManager.LayoutParams) layoutParams);
            int iResolveWidth = resolveWidth(view, hostedView.params);
            int iResolveHeight = resolveHeight(view, hostedView.params);
            if (iResolveWidth != hostedView.width || iResolveHeight != hostedView.height) {
                relayoutWithAccessibilityLayout(hostedView.viewHost, normalizeHostLayoutParams(hostedView.params, iResolveWidth, iResolveHeight));
                hostedView.width = iResolveWidth;
                hostedView.height = iResolveHeight;
            }
            applyLayout(hostedView);
        }

        synchronized void syncViewState(View view) {
            HostedView hostedView = this.hostedViews.get(view);
            if (hostedView == null) {
                return;
            }
            boolean z = true;
            boolean z2 = view.getVisibility() == 0;
            boolean z3 = z2 != hostedView.lastVisible;
            if (!z2 || !view.isLayoutRequested()) {
                z = false;
            }
            if (z3 || z) {
                view.requestLayout();
                int iResolveWidth = resolveWidth(view, hostedView.params);
                int iResolveHeight = resolveHeight(view, hostedView.params);
                if (iResolveWidth != hostedView.width || iResolveHeight != hostedView.height || z3) {
                    relayoutWithAccessibilityLayout(hostedView.viewHost, normalizeHostLayoutParams(hostedView.params, iResolveWidth, iResolveHeight));
                    hostedView.width = iResolveWidth;
                    hostedView.height = iResolveHeight;
                }
                view.invalidate();
            }
            applyLayout(hostedView);
            hostedView.lastVisible = z2;
        }

        synchronized void retargetDisplay(Display display) {
            int i;
            if (display == null) {
                throw new IllegalArgumentException("targetDisplay == null");
            }
            int displayId = display.getDisplayId();
            int displayId2 = this.display == null ? -1 : this.display.getDisplayId();
            ArrayList arrayList = new ArrayList(this.hostedViews.values());
            if (displayId2 != displayId) {
                try {
                    Iterator it = arrayList.iterator();
                    i = 0;
                    while (it.hasNext()) {
                        try {
                            this.service.attachAccessibilityOverlayToDisplay(displayId, ((HostedView) it.next()).surface);
                            i++;
                        } catch (RuntimeException e) {
                            e = e;
                            if (displayId2 != -1) {
                                for (int i2 = 0; i2 < i; i2++) {
                                    try {
                                        this.service.attachAccessibilityOverlayToDisplay(displayId2, ((HostedView) arrayList.get(i2)).surface);
                                    } catch (RuntimeException unused) {
                                    }
                                }
                            }
                            throw e;
                        }
                    }
                } catch (RuntimeException e2) {
                    e = e2;
                    i = 0;
                }
            }
            this.display = display;
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                HostedView hostedView = (HostedView) it2.next();
                int iResolveWidth = resolveWidth(hostedView.view, hostedView.params);
                int iResolveHeight = resolveHeight(hostedView.view, hostedView.params);
                relayoutWithAccessibilityLayout(hostedView.viewHost, normalizeHostLayoutParams(hostedView.params, iResolveWidth, iResolveHeight));
                hostedView.width = iResolveWidth;
                hostedView.height = iResolveHeight;
                applyLayout(hostedView);
            }
            Dbg.d(TrustedVisualOverlayWindowManager.TAG, "Surface HUD owner retargeted " + displayId2 + " -> " + displayId + " views=" + arrayList.size());
        }

        @Override // android.view.ViewManager
        public synchronized void removeView(View view) {
            removeViewImmediate(view);
        }

        @Override // android.view.WindowManager
        public synchronized void removeViewImmediate(View view) {
            HostedView hostedViewRemove = this.hostedViews.remove(view);
            if (hostedViewRemove == null) {
                throw new IllegalArgumentException("View is not attached");
            }
            detachSurface(hostedViewRemove.surface);
            hostedViewRemove.surfacePackage.release();
            hostedViewRemove.viewHost.release();
        }

        @Override // android.view.WindowManager
        public Display getDefaultDisplay() {
            return this.display;
        }

        synchronized WindowManager.LayoutParams getLayoutParams(View view) {
            HostedView hostedView;
            hostedView = this.hostedViews.get(view);
            return hostedView == null ? null : copyOf(hostedView.params);
        }

        private int resolveWidth(View view, WindowManager.LayoutParams layoutParams) {
            return resolveDimension(view, layoutParams.width, true);
        }

        private int resolveHeight(View view, WindowManager.LayoutParams layoutParams) {
            return resolveDimension(view, layoutParams.height, false);
        }

        private int resolveDimension(View view, int i, boolean z) {
            int iHeight;
            if (i > 0) {
                return i;
            }
            if (i == -1) {
                Rect displayFrame = getDisplayFrame();
                if (z) {
                    iHeight = displayFrame.width();
                } else {
                    iHeight = displayFrame.height();
                }
                return Math.max(1, iHeight);
            }
            view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            return Math.max(1, z ? view.getMeasuredWidth() : view.getMeasuredHeight());
        }

        private void applyLayout(HostedView hostedView) {
            Rect displayFrame = getDisplayFrame();
            Rect rect = new Rect();
            Gravity.apply(hostedView.params.gravity, hostedView.width, hostedView.height, displayFrame, hostedView.params.x, hostedView.params.y, rect);
            if (hostedView.params.width == -1) {
                rect.left = displayFrame.left;
                rect.right = displayFrame.right;
            }
            if (hostedView.params.height == -1) {
                rect.top = displayFrame.top;
                rect.bottom = displayFrame.bottom;
            }
            SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
            boolean z = hostedView.view.getVisibility() == 0;
            try {
                transaction.setLayer(hostedView.surface, hostedView.layer).setPosition(hostedView.surface, rect.left, rect.top).setAlpha(hostedView.surface, z ? hostedView.params.alpha : 0.0f).setVisibility(hostedView.surface, z).apply();
            } finally {
                transaction.close();
            }
        }

        private Rect getDisplayFrame() {
            Point point = new Point();
            this.display.getRealSize(point);
            if (point.x <= 0 || point.y <= 0) {
                point.x = this.context.getResources().getDisplayMetrics().widthPixels;
                point.y = this.context.getResources().getDisplayMetrics().heightPixels;
            }
            return new Rect(0, 0, Math.max(1, point.x), Math.max(1, point.y));
        }

        private static WindowManager.LayoutParams normalizeHostLayoutParams(WindowManager.LayoutParams layoutParams, int i, int i2) {
            WindowManager.LayoutParams layoutParamsCopyOf = copyOf(layoutParams);
            layoutParamsCopyOf.width = i;
            layoutParamsCopyOf.height = i2;
            layoutParamsCopyOf.type = 2032;
            layoutParamsCopyOf.flags |= 784;
            if (Build.VERSION.SDK_INT >= 30) {
                layoutParamsCopyOf.layoutInDisplayCutoutMode = 3;
            }
            layoutParamsCopyOf.gravity = 51;
            layoutParamsCopyOf.x = 0;
            layoutParamsCopyOf.y = 0;
            layoutParamsCopyOf.horizontalMargin = 0.0f;
            layoutParamsCopyOf.verticalMargin = 0.0f;
            layoutParamsCopyOf.alpha = 1.0f;
            return layoutParamsCopyOf;
        }

        private static void detachSurface(SurfaceControl surfaceControl) {
            if (surfaceControl == null || !surfaceControl.isValid()) {
                return;
            }
            SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
            try {
                transaction.reparent(surfaceControl, null).apply();
            } finally {
                transaction.close();
            }
        }

        private static void setViewWithAccessibilityLayout(SurfaceControlViewHost surfaceControlViewHost, View view, WindowManager.LayoutParams layoutParams) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
            invokeLayoutParamsMethod(surfaceControlViewHost, "setView", new Class[]{View.class, WindowManager.LayoutParams.class}, new Object[]{view, layoutParams});
        }

        private static void relayoutWithAccessibilityLayout(SurfaceControlViewHost surfaceControlViewHost, WindowManager.LayoutParams layoutParams) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
            invokeLayoutParamsMethod(surfaceControlViewHost, "relayout", new Class[]{WindowManager.LayoutParams.class}, new Object[]{layoutParams});
        }

        private static void invokeLayoutParamsMethod(SurfaceControlViewHost surfaceControlViewHost, String str, Class<?>[] clsArr, Object[] objArr) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
            try {
                Method declaredMethod = SurfaceControlViewHost.class.getDeclaredMethod(str, clsArr);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(surfaceControlViewHost, objArr);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw ((RuntimeException) cause);
                }
                throw new IllegalStateException(str + " failed", cause);
            } catch (ReflectiveOperationException e2) {
                throw new IllegalStateException(str + "(LayoutParams) unavailable", e2);
            }
        }

        private static WindowManager.LayoutParams copyOf(WindowManager.LayoutParams layoutParams) {
            WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
            layoutParams2.copyFrom(layoutParams);
            return layoutParams2;
        }

        private static final class HostedView {
            int height;
            boolean lastVisible;
            final int layer;
            WindowManager.LayoutParams params;
            final SurfaceControl surface;
            final SurfaceControlViewHost.SurfacePackage surfacePackage;
            final View view;
            final SurfaceControlViewHost viewHost;
            int width;

            HostedView(View view, SurfaceControlViewHost surfaceControlViewHost, SurfaceControlViewHost.SurfacePackage surfacePackage, SurfaceControl surfaceControl, WindowManager.LayoutParams layoutParams, int i, int i2, int i3) {
                this.view = view;
                this.viewHost = surfaceControlViewHost;
                this.surfacePackage = surfacePackage;
                this.surface = surfaceControl;
                this.params = layoutParams;
                this.width = i;
                this.height = i2;
                this.layer = i3;
                this.lastVisible = view.getVisibility() == 0;
            }
        }
    }
}
