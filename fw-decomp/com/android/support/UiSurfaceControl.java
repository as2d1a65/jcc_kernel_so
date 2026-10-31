package com.android.support;

import android.content.SharedPreferences;
import com.android.support.trust.NativeTrustBridge;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* loaded from: classes.dex */
public final class UiSurfaceControl {
    public static final String PREFERENCES_NAME = "ui_surface_policy";
    private static final String PREF_DISABLE = "ui_surface_disable_mask";
    private static final String PREF_ENABLE = "ui_surface_enable_mask";
    private static final String PREF_HIDE = "ui_surface_hide_mask";
    private static final String PREF_REVISION = "ui_surface_revision";
    private static final String PREF_SHOW = "ui_surface_show_mask";
    private static final Object LOCK = new Object();
    private static volatile UiSurfacePolicy current = UiSurfacePolicy.EMPTY;
    private static UiSurfacePolicy source = UiSurfacePolicy.EMPTY;
    private static int currentCapabilities = -1;
    private static final UiSurfacePolicyDispatcher DISPATCHER = new UiSurfacePolicyDispatcher(new Supplier() { // from class: com.android.support.UiSurfaceControl$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            return UiSurfaceControl.current();
        }
    });

    public interface Listener {
        void onUiSurfacePolicy(UiSurfacePolicy uiSurfacePolicy);
    }

    public static UiSurfacePolicy current() {
        return SpecifiedFeaturePolicy.restrict(current, NativeTrustBridge.specifiedCapabilities());
    }

    public static void publish(UiSurfacePolicy uiSurfacePolicy) {
        if (uiSurfacePolicy == null) {
            return;
        }
        synchronized (LOCK) {
            source = uiSurfacePolicy;
        }
        refreshSpecifiedAccess();
    }

    public static void refreshSpecifiedAccess() {
        synchronized (LOCK) {
            int iSpecifiedCapabilities = NativeTrustBridge.specifiedCapabilities();
            UiSurfacePolicy uiSurfacePolicyRestrict = SpecifiedFeaturePolicy.restrict(source, iSpecifiedCapabilities);
            if (uiSurfacePolicyRestrict.equals(current) && iSpecifiedCapabilities == currentCapabilities) {
                return;
            }
            current = uiSurfacePolicyRestrict;
            currentCapabilities = iSpecifiedCapabilities;
            DISPATCHER.changed();
        }
    }

    static UiSurfacePolicy serverPolicy() {
        UiSurfacePolicy uiSurfacePolicy;
        synchronized (LOCK) {
            uiSurfacePolicy = source;
        }
        return uiSurfacePolicy;
    }

    public static void setListener(Executor executor, final Listener listener) {
        Consumer<UiSurfacePolicy> consumer;
        UiSurfacePolicyDispatcher uiSurfacePolicyDispatcher = DISPATCHER;
        if (listener == null) {
            consumer = null;
        } else {
            Objects.requireNonNull(listener);
            consumer = new Consumer() { // from class: com.android.support.UiSurfaceControl$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    listener.onUiSurfacePolicy((UiSurfacePolicy) obj);
                }
            };
        }
        uiSurfacePolicyDispatcher.subscribe(executor, consumer);
    }

    public static void restore(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            return;
        }
        UiSurfacePolicy uiSurfacePolicyFromMasks = UiSurfacePolicy.fromMasks(sharedPreferences.getInt(PREF_REVISION, 0), sharedPreferences.getInt(PREF_SHOW, 0), sharedPreferences.getInt(PREF_HIDE, 0), sharedPreferences.getInt(PREF_ENABLE, 0), sharedPreferences.getInt(PREF_DISABLE, 0));
        synchronized (LOCK) {
            source = uiSurfacePolicyFromMasks;
            current = SpecifiedFeaturePolicy.restrict(uiSurfacePolicyFromMasks, NativeTrustBridge.specifiedCapabilities());
        }
    }

    public static void persist(SharedPreferences sharedPreferences, UiSurfacePolicy uiSurfacePolicy) {
        UiSurfacePolicy uiSurfacePolicy2;
        if (sharedPreferences == null || uiSurfacePolicy == null) {
            return;
        }
        synchronized (LOCK) {
            uiSurfacePolicy2 = source;
        }
        sharedPreferences.edit().putInt(PREF_REVISION, uiSurfacePolicy2.revision).putInt(PREF_SHOW, uiSurfacePolicy2.forceShowMask()).putInt(PREF_HIDE, uiSurfacePolicy2.forceHideMask()).putInt(PREF_ENABLE, uiSurfacePolicy2.forceEnableMask()).putInt(PREF_DISABLE, uiSurfacePolicy2.forceDisableMask()).apply();
    }

    private UiSurfaceControl() {
    }
}
