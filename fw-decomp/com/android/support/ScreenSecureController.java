package com.android.support;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewTreeObserver;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
final class ScreenSecureController {
    private static final ScreenSecureController SHARED = new ScreenSecureController();
    private static final String TAG = "FloatingWindowService";
    private boolean enabled;
    private boolean inited;
    private Method mGetGenerationId;
    private Method mGetSurfaceControl;
    private Method mGetViewRootImpl;
    private Method mSetSkipScreenshot;
    private boolean ok;
    private boolean preferencesLoaded;
    private final Map<View, Target> targets = new WeakHashMap();

    ScreenSecureController() {
    }

    static ScreenSecureController shared() {
        return SHARED;
    }

    void restorePreference(Context context) {
        if (this.preferencesLoaded) {
            return;
        }
        this.preferencesLoaded = true;
        setEnabled(context.getSharedPreferences(FloatingWindowPrefsKeys.PREF_NAME, 0).getBoolean(FloatingWindowPrefsKeys.KEY_SCREEN_SECURE, false));
    }

    boolean isReady() {
        return this.ok;
    }

    void init() {
        boolean z = true;
        if (Build.VERSION.SDK_INT < 29) {
            this.inited = true;
            this.ok = false;
            return;
        }
        if (this.inited && this.ok) {
            return;
        }
        this.inited = true;
        try {
            Class<?> cls = Class.forName("android.view.ViewRootImpl");
            this.mGetViewRootImpl = findMethod(View.class, "getViewRootImpl", new Class[0]);
            this.mGetSurfaceControl = findMethod(cls, "getSurfaceControl", new Class[0]);
            this.mSetSkipScreenshot = findMethod(SurfaceControl.Transaction.class, "setSkipScreenshot", SurfaceControl.class, Boolean.TYPE);
            try {
                this.mGetGenerationId = findMethod(SurfaceControl.class, "getGenerationId", new Class[0]);
            } catch (Throwable unused) {
                this.mGetGenerationId = null;
            }
            if (this.mGetViewRootImpl == null || this.mGetSurfaceControl == null || this.mSetSkipScreenshot == null) {
                z = false;
            }
            this.ok = z;
            Dbg.i(TAG, "[skipss] 反射初始化 ".concat(this.ok ? "成功" : "失败(方法为null)"));
        } catch (Throwable th) {
            this.ok = false;
            Log.w(TAG, "[skipss] 反射初始化异常: " + th);
        }
    }

    private static Method findMethod(Class<?> cls, String str, Class<?>... clsArr) throws Exception {
        try {
            return cls.getMethod(str, clsArr);
        } catch (ReflectiveOperationException unused) {
            return (Method) Class.class.getDeclaredMethod("getMethod", String.class, Class[].class).invoke(cls, str, clsArr);
        }
    }

    private Integer generationOf(SurfaceControl surfaceControl) {
        Method method = this.mGetGenerationId;
        if (method == null) {
            return null;
        }
        try {
            return (Integer) method.invoke(surfaceControl, new Object[0]);
        } catch (Throwable unused) {
            return null;
        }
    }

    void setEnabled(boolean z) {
        if (this.enabled == z) {
            return;
        }
        this.enabled = z;
        if (z) {
            init();
        }
        applyTargets(new ArrayList(this.targets.values()), true);
    }

    void track(View view) {
        if (view == null) {
            return;
        }
        Target target = this.targets.get(view);
        if (target == null) {
            target = new Target(view);
            this.targets.put(view, target);
            view.addOnAttachStateChangeListener(target);
        }
        target.observe();
    }

    void apply(View view, boolean z) {
        setEnabled(z);
        track(view);
        Target target = this.targets.get(view);
        if (target != null) {
            applyTargets(Collections.singletonList(target), false);
        }
    }

    void applyAll(List<View> list, boolean z) {
        if (list != null) {
            Iterator<View> it = list.iterator();
            while (it.hasNext()) {
                track(it.next());
            }
        }
        if (this.enabled != z) {
            setEnabled(z);
        } else {
            applyTargets(new ArrayList(this.targets.values()), true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyTargets(List<Target> list, boolean z) {
        SurfaceControl.Transaction transaction = null;
        try {
            ArrayList arrayList = null;
            for (final Target target : list) {
                target.observe();
                View view = target.view.get();
                if (this.ok && view != null && view.isAttachedToWindow() && (this.enabled || target.needsClear)) {
                    try {
                        Object objInvoke = this.mGetViewRootImpl.invoke(view, new Object[0]);
                        if (objInvoke != null) {
                            Object objInvoke2 = this.mGetSurfaceControl.invoke(objInvoke, new Object[0]);
                            if (objInvoke2 instanceof SurfaceControl) {
                                final SurfaceControl surfaceControl = (SurfaceControl) objInvoke2;
                                if (surfaceControl.isValid()) {
                                    final Integer numGenerationOf = generationOf(surfaceControl);
                                    if (z || target.dirty || target.surface.get() != surfaceControl || numGenerationOf == null || !numGenerationOf.equals(target.generation) || target.appliedEnabled != this.enabled) {
                                        if (transaction == null) {
                                            transaction = new SurfaceControl.Transaction();
                                        }
                                        this.mSetSkipScreenshot.invoke(transaction, surfaceControl, Boolean.valueOf(this.enabled));
                                        target.dirty = true;
                                        if (this.enabled) {
                                            target.needsClear = true;
                                        }
                                        if (arrayList == null) {
                                            arrayList = new ArrayList();
                                        }
                                        final boolean z2 = this.enabled;
                                        arrayList.add(new Runnable() { // from class: com.android.support.ScreenSecureController$$ExternalSyntheticLambda0
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                ScreenSecureController.lambda$applyTargets$0(target, surfaceControl, numGenerationOf, z2);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    } catch (Throwable unused) {
                    }
                }
            }
            if (transaction != null) {
                transaction.apply();
                if (arrayList != null) {
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        ((Runnable) it.next()).run();
                    }
                }
            }
            if (transaction == null) {
                return;
            }
        } catch (Throwable unused2) {
            if (transaction == null) {
                return;
            }
        }
        try {
            transaction.close();
        } catch (Throwable unused3) {
        }
    }

    static /* synthetic */ void lambda$applyTargets$0(Target target, SurfaceControl surfaceControl, Integer num, boolean z) {
        target.surface = new WeakReference<>(surfaceControl);
        target.generation = num;
        target.appliedEnabled = z;
        target.needsClear = z;
        target.dirty = false;
        if (z) {
            return;
        }
        target.stopObserving();
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class Target implements View.OnAttachStateChangeListener, ViewTreeObserver.OnPreDrawListener {
        boolean appliedEnabled;
        boolean dirty;
        Integer generation;
        boolean needsClear;
        final WeakReference<View> view;
        final List<Target> self = Collections.singletonList(this);
        WeakReference<ViewTreeObserver> observer = new WeakReference<>(null);
        WeakReference<SurfaceControl> surface = new WeakReference<>(null);

        Target(View view) {
            this.view = new WeakReference<>(view);
        }

        void observe() {
            ViewTreeObserver viewTreeObserver;
            View view = this.view.get();
            if (!ScreenSecureController.this.enabled && !this.needsClear) {
                stopObserving();
                return;
            }
            if (view == null || !view.isAttachedToWindow() || (viewTreeObserver = view.getViewTreeObserver()) == this.observer.get() || !viewTreeObserver.isAlive()) {
                return;
            }
            stopObserving();
            viewTreeObserver.addOnPreDrawListener(this);
            this.observer = new WeakReference<>(viewTreeObserver);
        }

        void stopObserving() {
            ViewTreeObserver viewTreeObserver = this.observer.get();
            if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
                viewTreeObserver.removeOnPreDrawListener(this);
            }
            this.observer.clear();
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
            observe();
            ScreenSecureController.this.applyTargets(this.self, false);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            stopObserving();
            this.surface.clear();
            this.generation = null;
            this.appliedEnabled = false;
            this.needsClear = false;
            this.dirty = false;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            View view = this.view.get();
            if (view != null) {
                NativeLicenseWatermark.sync(view);
            }
            ScreenSecureController.this.applyTargets(this.self, false);
            return true;
        }
    }
}
