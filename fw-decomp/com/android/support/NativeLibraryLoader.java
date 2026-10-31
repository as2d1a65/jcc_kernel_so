package com.android.support;

import android.content.Context;
import android.os.Build;
import java.io.File;

/* loaded from: classes.dex */
public final class NativeLibraryLoader {
    private static final NativeRuntimeLoadState STATE = new NativeRuntimeLoadState();

    private NativeLibraryLoader() {
    }

    public static void ensureLoaded(Context context) {
        if (context == null) {
            throw new UnsatisfiedLinkError("missing context");
        }
        STATE.load(new Runnable() { // from class: com.android.support.NativeLibraryLoader$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                System.loadLibrary(NativeLibrarySelector.selectRuntimeBaseName(Build.SUPPORTED_ABIS, false));
            }
        });
    }

    public static boolean isLoaded() {
        return STATE.isLoaded();
    }

    static Throwable loadFailure() {
        return STATE.failure();
    }

    static File hostRuntimeFile(File file, String str) {
        return new File(new File(file, "pkf473104974/host"), str);
    }

    public static void ensureLoaded() {
        if (!isLoaded()) {
            throw new UnsatisfiedLinkError("runtime not loaded");
        }
    }
}
