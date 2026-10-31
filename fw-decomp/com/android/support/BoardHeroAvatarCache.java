package com.android.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
final class BoardHeroAvatarCache {
    private static final int MAX_AVATAR_EDGE_PX = 64;
    private volatile boolean closed;
    private final Context context;
    private final LruCache<String, Bitmap> cache = new LruCache<>(256);
    private final BoardHeroAvatarLoadCallbacks loadCallbacks = new BoardHeroAvatarLoadCallbacks();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    interface Callback {
        void onReady();
    }

    BoardHeroAvatarCache(Context context) {
        this.context = context.getApplicationContext();
    }

    Bitmap get(int i, int i2, final Callback callback) {
        Runnable runnable;
        if (this.closed) {
            return null;
        }
        final String[] heroAvatarPaths = HeroIdMapper.getHeroAvatarPaths(i, i2);
        if (heroAvatarPaths.length == 0) {
            return null;
        }
        String str = heroAvatarPaths[0];
        Bitmap bitmap = this.cache.get(str);
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap;
        }
        if (callback == null) {
            runnable = null;
        } else {
            Objects.requireNonNull(callback);
            runnable = new Runnable() { // from class: com.android.support.BoardHeroAvatarCache$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    callback.onReady();
                }
            };
        }
        if (this.loadCallbacks.join(str, runnable)) {
            try {
                this.executor.execute(new Runnable() { // from class: com.android.support.BoardHeroAvatarCache$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() throws IOException {
                        this.f$0.m90lambda$get$0$comandroidsupportBoardHeroAvatarCache(heroAvatarPaths);
                    }
                });
            } catch (RuntimeException unused) {
                this.loadCallbacks.complete(str);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0020, code lost:
    
        r1 = r9[0];
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0028, code lost:
    
        if (r3.getWidth() > 64) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x002e, code lost:
    
        if (r3.getHeight() <= 64) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0030, code lost:
    
        r6 = android.graphics.Bitmap.createScaledBitmap(r3, 64, 64, true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0035, code lost:
    
        if (r6 == r3) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0037, code lost:
    
        r3.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x003a, code lost:
    
        r3 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x003b, code lost:
    
        if (r5 == null) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x003d, code lost:
    
        r5.close();
     */
    /* renamed from: load, reason: merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void m90lambda$get$0$comandroidsupportBoardHeroAvatarCache(java.lang.String[] r9) throws java.io.IOException {
        /*
            r8 = this;
            r0 = 0
            r1 = r9[r0]
            int r2 = r9.length
            r3 = 0
            r4 = r0
        L6:
            if (r4 >= r2) goto L50
            r5 = r9[r4]
            android.content.Context r6 = r8.context     // Catch: java.lang.Throwable -> L4d
            android.content.res.AssetManager r6 = r6.getAssets()     // Catch: java.lang.Throwable -> L4d
            java.io.InputStream r5 = r6.open(r5)     // Catch: java.lang.Throwable -> L4d
            android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeStream(r5)     // Catch: java.lang.Throwable -> L41
            if (r3 != 0) goto L20
            if (r5 == 0) goto L4d
            r5.close()     // Catch: java.lang.Throwable -> L4d java.lang.Throwable -> L4d
            goto L4d
        L20:
            r1 = r9[r0]     // Catch: java.lang.Throwable -> L41
            int r6 = r3.getWidth()     // Catch: java.lang.Throwable -> L41
            r7 = 64
            if (r6 > r7) goto L30
            int r6 = r3.getHeight()     // Catch: java.lang.Throwable -> L41
            if (r6 <= r7) goto L3b
        L30:
            r6 = 1
            android.graphics.Bitmap r6 = android.graphics.Bitmap.createScaledBitmap(r3, r7, r7, r6)     // Catch: java.lang.Throwable -> L41
            if (r6 == r3) goto L3a
            r3.recycle()     // Catch: java.lang.Throwable -> L41
        L3a:
            r3 = r6
        L3b:
            if (r5 == 0) goto L50
            r5.close()     // Catch: java.lang.Throwable -> L4d java.lang.Throwable -> L4d
            goto L50
        L41:
            r6 = move-exception
            if (r5 == 0) goto L4c
            r5.close()     // Catch: java.lang.Throwable -> L48
            goto L4c
        L48:
            r5 = move-exception
            r6.addSuppressed(r5)     // Catch: java.lang.Throwable -> L4d java.lang.Throwable -> L4d
        L4c:
            throw r6     // Catch: java.lang.Throwable -> L4d java.lang.Throwable -> L4d
        L4d:
            int r4 = r4 + 1
            goto L6
        L50:
            boolean r9 = r8.closed
            if (r9 != 0) goto L5c
            if (r3 == 0) goto L5c
            android.util.LruCache<java.lang.String, android.graphics.Bitmap> r9 = r8.cache
            r9.put(r1, r3)
            goto L61
        L5c:
            if (r3 == 0) goto L61
            r3.recycle()
        L61:
            com.android.support.BoardHeroAvatarLoadCallbacks r9 = r8.loadCallbacks
            java.util.List r9 = r9.complete(r1)
            boolean r0 = r8.closed
            if (r0 != 0) goto L7b
            boolean r0 = r9.isEmpty()
            if (r0 != 0) goto L7b
            android.os.Handler r0 = r8.main
            com.android.support.BoardHeroAvatarCache$$ExternalSyntheticLambda2 r1 = new com.android.support.BoardHeroAvatarCache$$ExternalSyntheticLambda2
            r1.<init>()
            r0.post(r1)
        L7b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.BoardHeroAvatarCache.m90lambda$get$0$comandroidsupportBoardHeroAvatarCache(java.lang.String[]):void");
    }

    /* renamed from: lambda$load$1$com-android-support-BoardHeroAvatarCache, reason: not valid java name */
    /* synthetic */ void m91lambda$load$1$comandroidsupportBoardHeroAvatarCache(List list) {
        if (this.closed) {
            return;
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
    }

    void close() {
        this.closed = true;
        this.executor.shutdownNow();
        this.main.removeCallbacksAndMessages(null);
        this.cache.evictAll();
        this.loadCallbacks.clear();
    }
}
