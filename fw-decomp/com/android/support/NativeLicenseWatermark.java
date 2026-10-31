package com.android.support;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class NativeLicenseWatermark extends Drawable {
    private final WeakReference<View> owner;

    private static native void nativeDraw(View view, Canvas canvas);

    private static native void nativeSync(View view);

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    private NativeLicenseWatermark(View view) {
        this.owner = new WeakReference<>(view);
    }

    static void sync(View view) {
        NativeLibraryLoader.ensureLoaded(view.getContext());
        nativeSync(view);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        View view = this.owner.get();
        if (view != null) {
            nativeDraw(view, canvas);
        }
    }
}
