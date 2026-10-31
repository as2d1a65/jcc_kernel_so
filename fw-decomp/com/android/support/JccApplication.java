package com.android.support;

import android.app.Application;
import android.content.SharedPreferences;

/* loaded from: classes.dex */
public final class JccApplication extends Application {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        try {
            RuntimeDiagnostics.install(this);
        } catch (RuntimeException unused) {
        }
        GameRegionState.initialize(this);
        RuntimeDiagnostics runtimeDiagnostics = RuntimeDiagnostics.get();
        if (runtimeDiagnostics != null) {
            runtimeDiagnostics.record("APP", "NATIVE_LOAD", "BEGIN", "installed runtime");
        }
        try {
            NativeLibraryLoader.ensureLoaded(this);
            if (runtimeDiagnostics != null) {
                runtimeDiagnostics.record("APP", "NATIVE_LOAD", "OK", "installed runtime");
            }
            GameRegionControl.initialize(this);
        } catch (LinkageError | SecurityException e) {
            if (runtimeDiagnostics != null) {
                runtimeDiagnostics.record("APP", "NATIVE_LOAD", "FAIL", RuntimeDiagnostics.throwableSummary(Thread.currentThread(), e));
            }
        }
    }

    SharedPreferences unscopedPreferences(String str, int i) {
        return super.getSharedPreferences(str, i);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SharedPreferences getSharedPreferences(String str, int i) {
        return super.getSharedPreferences(GameRegionState.preferenceName(str), i);
    }
}
