package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import com.android.support.GameRegionPolicy;
import com.android.support.trust.NativeTrustBridge;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class GameRegionControl {
    private static ScheduledFuture<?> accessPolling;
    private static SharedPreferences cache;
    private static volatile GameRegionPolicy policy;
    private static ScheduledFuture<?> polling;
    private static final Set<Runnable> LISTENERS = new CopyOnWriteArraySet();
    private static final ScheduledExecutorService WORKER = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() { // from class: com.android.support.GameRegionControl$$ExternalSyntheticLambda3
        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            return GameRegionControl.lambda$static$0(runnable);
        }
    });
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final ScheduledExecutorService ACCESS_WORKER = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() { // from class: com.android.support.GameRegionControl$$ExternalSyntheticLambda4
        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            return GameRegionControl.lambda$static$1(runnable);
        }
    });
    private static volatile UiSurfacePolicy domesticLegacy = UiSurfacePolicy.EMPTY;

    static /* synthetic */ Thread lambda$static$0(Runnable runnable) {
        return new Thread(runnable, ThreadNaming.WORKER);
    }

    static /* synthetic */ Thread lambda$static$1(Runnable runnable) {
        return new Thread(runnable, ThreadNaming.WORKER);
    }

    public static synchronized void initialize(Context context) {
        if (cache != null) {
            return;
        }
        cache = context.getApplicationContext().getSharedPreferences("game_region_signed_policy", 0);
        WORKER.execute(new Runnable() { // from class: com.android.support.GameRegionControl$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                GameRegionControl.lambda$initialize$2();
            }
        });
    }

    static /* synthetic */ void lambda$initialize$2() {
        try {
            String string = cache.getString("envelope", "");
            if (string.isEmpty()) {
                return;
            }
            accept(NativeTrustBridge.getInstance().parseGameRegionPolicy(string));
        } catch (Exception unused) {
        }
    }

    public static synchronized void subscribe(Runnable runnable) {
        LISTENERS.add(runnable);
        if (polling == null) {
            polling = WORKER.scheduleWithFixedDelay(new Runnable() { // from class: com.android.support.GameRegionControl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GameRegionControl.refresh();
                }
            }, 0L, 30L, TimeUnit.SECONDS);
        }
        if (accessPolling == null) {
            accessPolling = ACCESS_WORKER.scheduleWithFixedDelay(new Runnable() { // from class: com.android.support.GameRegionControl$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    UiSurfaceControl.refreshSpecifiedAccess();
                }
            }, 0L, 1L, TimeUnit.SECONDS);
        }
        MAIN.post(runnable);
    }

    public static synchronized void unsubscribe(Runnable runnable) {
        ScheduledFuture<?> scheduledFuture;
        ScheduledFuture<?> scheduledFuture2;
        Set<Runnable> set = LISTENERS;
        set.remove(runnable);
        if (set.isEmpty() && (scheduledFuture2 = polling) != null) {
            scheduledFuture2.cancel(false);
            polling = null;
        }
        if (set.isEmpty() && (scheduledFuture = accessPolling) != null) {
            scheduledFuture.cancel(false);
            accessPolling = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void refresh() {
        SharedPreferences sharedPreferences;
        try {
            String strFetchGameRegionPolicy = NativeTrustBridge.getInstance().fetchGameRegionPolicy();
            if (!accept(NativeTrustBridge.getInstance().parseGameRegionPolicy(strFetchGameRegionPolicy)) || (sharedPreferences = cache) == null) {
                return;
            }
            sharedPreferences.edit().putString("envelope", strFetchGameRegionPolicy).apply();
        } catch (Exception unused) {
        }
    }

    private static synchronized boolean accept(GameRegionPolicy gameRegionPolicy) {
        if (!gameRegionPolicy.supersedes(policy)) {
            return false;
        }
        policy = gameRegionPolicy;
        applyCurrent();
        Iterator<Runnable> it = LISTENERS.iterator();
        while (it.hasNext()) {
            MAIN.post(it.next());
        }
        return true;
    }

    public static void onRegionChanged() {
        applyCurrent();
        Iterator<Runnable> it = LISTENERS.iterator();
        while (it.hasNext()) {
            MAIN.post(it.next());
        }
    }

    public static GameRegionPolicy.Entry entry(GameRegion gameRegion) {
        GameRegionPolicy gameRegionPolicy = policy;
        if (gameRegionPolicy == null) {
            return null;
        }
        return gameRegionPolicy.forRegion(gameRegion);
    }

    public static boolean visible(GameRegion gameRegion) {
        GameRegionPolicy.Entry entry = entry(gameRegion);
        return entry == null || entry.visible;
    }

    public static boolean enabled(GameRegion gameRegion) {
        GameRegionPolicy.Entry entry = entry(gameRegion);
        return entry == null || entry.enabled;
    }

    public static String unavailableReason(GameRegion gameRegion) {
        GameRegionPolicy.Entry entry = entry(gameRegion);
        return (entry == null || (entry.visible && entry.enabled)) ? !gameRegion.isRuntimeReady() ? "国际服适配准备中，暂未开放游戏连接" : "" : entry.message.isEmpty() ? gameRegion.label + "维护中，请稍后重试" : entry.message;
    }

    public static void publishDomesticLegacy(UiSurfacePolicy uiSurfacePolicy) {
        if (uiSurfacePolicy == null) {
            return;
        }
        synchronized (GameRegionControl.class) {
            if (uiSurfacePolicy.revision < domesticLegacy.revision) {
                return;
            }
            domesticLegacy = uiSurfacePolicy;
            applyCurrent();
        }
    }

    public static void applyCurrent() {
        GameRegion gameRegionCurrent = GameRegionState.current();
        UiSurfacePolicy uiSurfacePolicySurfaces = surfaces(gameRegionCurrent);
        int iForceDisableMask = uiSurfacePolicySurfaces.forceDisableMask();
        for (int i = 0; i < UiSurfaceRegistry.size(); i++) {
            String strIdAt = UiSurfaceRegistry.idAt(i);
            if (!gameRegionCurrent.supportsSurface(strIdAt) || ((!visible(gameRegionCurrent) || !enabled(gameRegionCurrent)) && !UiSurfaceRegistry.PAGE_SETTINGS.equals(strIdAt) && !UiSurfaceRegistry.PAGE_ABOUT.equals(strIdAt) && !UiSurfaceRegistry.PAGE_INTRO.equals(strIdAt))) {
                iForceDisableMask |= 1 << i;
            }
        }
        UiSurfaceControl.publish(UiSurfacePolicy.fromMasks(uiSurfacePolicySurfaces.revision, uiSurfacePolicySurfaces.forceShowMask(), uiSurfacePolicySurfaces.forceHideMask(), uiSurfacePolicySurfaces.forceEnableMask(), iForceDisableMask));
    }

    public static UiSurfacePolicy surfaces(GameRegion gameRegion) {
        GameRegionPolicy gameRegionPolicy = policy;
        GameRegionPolicy.Entry entryForRegion = gameRegionPolicy == null ? null : gameRegionPolicy.forRegion(gameRegion);
        if (entryForRegion == null) {
            return gameRegion == GameRegion.CN ? domesticLegacy : UiSurfacePolicy.EMPTY;
        }
        if (gameRegion == GameRegion.CN) {
            return UiSurfacePolicy.withPendingRestrictions(entryForRegion.surfaces, gameRegionPolicy.legacyRevision, domesticLegacy);
        }
        return entryForRegion.surfaces;
    }

    private GameRegionControl() {
    }
}
